package org.eduscript.codegen;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eduscript.datastructures.Scope;
import org.eduscript.datastructures.Symbol;
import org.eduscript.datastructures.EduType;
import org.eduscript.semantic.SemanticAnalyzer;

import main.antlr4.EduScriptParser.AssignmentContext;
import main.antlr4.EduScriptParser.BlockContext;
import main.antlr4.EduScriptParser.ConditionalContext;
import main.antlr4.EduScriptParser.ExpressionContext;
import main.antlr4.EduScriptParser.ForLoopContext;
import main.antlr4.EduScriptParser.FunctionCallContext;
import main.antlr4.EduScriptParser.MainBlockContext;
import main.antlr4.EduScriptParser.ProgramContext;
import main.antlr4.EduScriptParser.ReadStatementContext;
import main.antlr4.EduScriptParser.VariableDeclarationContext;
import main.antlr4.EduScriptParser.WhileLoopContext;
import main.antlr4.EduScriptParser.WriteStatementContext;

public class CodeGenerator extends SemanticAnalyzer {
    
    private Stack<BlockCommand> blockStack;
    private ProgramCommand program;
    private int currentIndentLevel = 0;
    
    public CodeGenerator() {
        blockStack = new Stack<>();
    }
    
    public String generateCode() {
        if (program != null) {
            return program.generateCode();
        }
        return "";
    }
    
    @Override
    public EduType visitProgram(ProgramContext ctx) {
        // Create the main program command
        program = new ProgramCommand(ctx.ID().getText());
        
        // Process global declarations
        super.visitProgram(ctx);
        
        return null;
    }
    
    @Override
    public EduType visitMainBlock(MainBlockContext ctx) {
        // Create main block
        BlockCommand mainBlock = new BlockCommand(1);
        blockStack.push(mainBlock);
        currentIndentLevel++;
        
        // Visit statements
        super.visitMainBlock(ctx);
        
        // Pop and set main block
        blockStack.pop();
        currentIndentLevel--;
        program.setMainBlock(mainBlock);
        
        return null;
    }
    
    @Override
    public EduType visitBlock(BlockContext ctx) {
        // Create new block
        BlockCommand block = new BlockCommand(currentIndentLevel);
        blockStack.push(block);
        currentIndentLevel++;
        
        // Visit statements
        super.visitBlock(ctx);
        
        // Pop block
        blockStack.pop();
        currentIndentLevel--;
        
        return null;
    }
    
    @Override
    public EduType visitVariableDeclaration(VariableDeclarationContext ctx) {
        // First do semantic analysis
        EduType type = super.visitVariableDeclaration(ctx);
        
        // Generate declaration code
        String varName = ctx.ID().getText();
        Symbol symbol = getCurrentScope().resolve(varName);
        
        if (symbol != null) {
            // If we're in global scope, add to program
            if (blockStack.isEmpty()) {
                program.addGlobalDeclaration(new SymbolDeclarationCommand(symbol));
            } else {
                // Otherwise add to current block
                blockStack.peek().addCommand(new SymbolDeclarationCommand(symbol));
            }
        }
        
        return type;
    }
    
    @Override
    public EduType visitAssignment(AssignmentContext ctx) {
        // First do semantic analysis
        EduType type = super.visitAssignment(ctx);
        
        // Generate assignment code
        String varName = ctx.ID().getText();
        String expression = generateExpression(ctx.expression(0));
        
        AssignmentCommand cmd;
        if (ctx.LBRACK() != null) {
            // Array assignment
            String index = generateExpression(ctx.expression(0));
            expression = generateExpression(ctx.expression(1));
            cmd = new AssignmentCommand(varName, index, expression);
        } else {
            cmd = new AssignmentCommand(varName, expression);
        }
        
        if (!blockStack.isEmpty()) {
            blockStack.peek().addCommand(cmd);
        }
        
        return type;
    }
    
    @Override
    public EduType visitWriteStatement(WriteStatementContext ctx) {
        List<String> expressions = new ArrayList<>();
        
        for (ExpressionContext expr : ctx.expressionList().expression()) {
            expressions.add(generateExpression(expr));
        }
        
        WriteCommand cmd = new WriteCommand(expressions);
        if (!blockStack.isEmpty()) {
            blockStack.peek().addCommand(cmd);
        }
        
        return null;
    }
    
    @Override
    public EduType visitReadStatement(ReadStatementContext ctx) {
        List<String> variables = new ArrayList<>();
        
        for (var id : ctx.idList().ID()) {
            variables.add(id.getText());
        }
        
        ReadCommand cmd = new ReadCommand(variables);
        if (!blockStack.isEmpty()) {
            blockStack.peek().addCommand(cmd);
        }
        
        return null;
    }
    
    @Override
    public EduType visitConditional(ConditionalContext ctx) {
        String condition = generateExpression(ctx.expression());
        
        // Create then block
        BlockCommand thenBlock = new BlockCommand(currentIndentLevel + 1);
        blockStack.push(thenBlock);
        currentIndentLevel++;
        
        // Visit then statements
        visit(ctx.statementList(0));
        
        blockStack.pop();
        currentIndentLevel--;
        
        ConditionalCommand cmd = new ConditionalCommand(condition, thenBlock);
        
        // Handle else block if present
        if (ctx.statementList().size() > 1) {
            BlockCommand elseBlock = new BlockCommand(currentIndentLevel + 1);
            blockStack.push(elseBlock);
            currentIndentLevel++;
            
            visit(ctx.statementList(1));
            
            blockStack.pop();
            currentIndentLevel--;
            
            cmd.setElseBlock(elseBlock);
        }
        
        if (!blockStack.isEmpty()) {
            blockStack.peek().addCommand(cmd);
        }
        
        return null;
    }
    
    @Override
    public EduType visitWhileLoop(WhileLoopContext ctx) {
        String condition = generateExpression(ctx.expression());
        
        // Create loop body
        BlockCommand body = new BlockCommand(currentIndentLevel + 1);
        blockStack.push(body);
        currentIndentLevel++;
        
        visit(ctx.statementList());
        
        blockStack.pop();
        currentIndentLevel--;
        
        WhileCommand cmd = new WhileCommand(condition, body);
        if (!blockStack.isEmpty()) {
            blockStack.peek().addCommand(cmd);
        }
        
        return null;
    }
    
    @Override
    public EduType visitForLoop(ForLoopContext ctx) {
        String variable = ctx.ID().getText();
        String startValue = generateExpression(ctx.expression(0));
        String endValue = generateExpression(ctx.expression(1));
        
        // Create loop body
        BlockCommand body = new BlockCommand(currentIndentLevel + 1);
        blockStack.push(body);
        currentIndentLevel++;
        
        visit(ctx.statementList());
        
        blockStack.pop();
        currentIndentLevel--;
        
        ForCommand cmd = new ForCommand(variable, startValue, endValue, body);
        
        // Handle step if present
        if (ctx.expression().size() > 2) {
            cmd.setStep(generateExpression(ctx.expression(2)));
        }
        
        if (!blockStack.isEmpty()) {
            blockStack.peek().addCommand(cmd);
        }
        
        return null;
    }
    
    private String generateExpression(ExpressionContext ctx) {
        if (ctx.op != null) {
            String left = generateExpression(ctx.expression(0));
            String right = generateExpression(ctx.expression(1));
            String op = ctx.op.getText();
            
            // Convert EduScript operators to C operators
            switch (op) {
                case "e":
                    op = "&&";
                    break;
                case "ou":
                    op = "||";
                    break;
                case "nao":
                    return "!" + left;
            }
            
            return left + " " + op + " " + right;
        } else if (ctx.constant() != null) {
            return ctx.constant().getText();
        } else if (ctx.ID() != null) {
            if (ctx.LBRACK() != null) {
                // Array access
                String index = generateExpression(ctx.expression(0));
                return ctx.ID().getText() + "[" + index + "]";
            }
            return ctx.ID().getText();
        } else if (ctx.functionCall() != null) {
            return generateFunctionCall(ctx.functionCall());
        } else if (ctx.expression().size() == 1) {
            // Parenthesized expression
            return "(" + generateExpression(ctx.expression(0)) + ")";
        }
        
        return "";
    }
    
    private String generateFunctionCall(FunctionCallContext ctx) {
        StringBuilder call = new StringBuilder();
        call.append(ctx.ID().getText()).append("(");
        
        if (ctx.arguments() != null) {
            List<String> args = new ArrayList<>();
            for (ExpressionContext expr : ctx.arguments().expression()) {
                args.add(generateExpression(expr));
            }
            call.append(String.join(", ", args));
        }
        
        call.append(")");
        return call.toString();
    }
    
    private Scope getCurrentScope() {
        return currentScope;
    }

    public ProgramCommand getProgram() {
        return this.program;
    }
    
    // Helper command class for symbol declarations
    private static class SymbolDeclarationCommand extends BaseCommand {
        private Symbol symbol;
        
        public SymbolDeclarationCommand(Symbol symbol) {
            this.symbol = symbol;
        }
        
        @Override
        public String generateCode() {
            String c = symbol.generateDeclaration();
            return c;
        }
    }
}
