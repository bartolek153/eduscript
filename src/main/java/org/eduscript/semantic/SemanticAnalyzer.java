package org.eduscript.semantic;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.eduscript.datastructures.ArraySymbol;
import org.eduscript.datastructures.FunctionSymbol;
import org.eduscript.datastructures.Scope;
import org.eduscript.datastructures.Symbol;
import org.eduscript.datastructures.Type;
import org.eduscript.datastructures.VariableSymbol;
import org.eduscript.semantic.exceptions.ArrayAccessException;
import org.eduscript.semantic.exceptions.ArrayAssignmentException;
import org.eduscript.semantic.exceptions.SemanticException;
import org.eduscript.semantic.exceptions.SymbolExistsScopeException;
import org.eduscript.semantic.exceptions.TypeMismatchException;
import org.eduscript.semantic.exceptions.UndeclaredVariableException;

import main.antlr4.EduScriptBaseVisitor;
import main.antlr4.EduScriptParser;
import main.antlr4.EduScriptParser.BlockContext;
import main.antlr4.EduScriptParser.MainBlockContext;

public class SemanticAnalyzer extends EduScriptBaseVisitor<Type> {

    private static final String FUN_SBL_T = "Function";
    private static final String PARAM_SBL_T = "Parameter";
    private static final String VAR_SBL_T = "Variable";
    protected Scope currentScope;
    private SemanticErrorHandler errorHandler;

    public SemanticAnalyzer() {
        this.errorHandler = new SemanticErrorHandler();
    }

    public SemanticAnalyzer(SemanticErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public SemanticErrorHandler getErrorHandler() {
        return errorHandler;
    }

    private int getLine(ParserRuleContext ctx) {
        return ctx.getStart().getLine();
    }

    private int getColumn(ParserRuleContext ctx) {
        return ctx.getStart().getCharPositionInLine() + 1;
    }

    /**
     * Initializes the global scope and processes all global declarations
     * Creates a new root scope and visits each global declaration (such as
     * variables or functions), followed by the program's main block.
     *
     * @param ctx The parser context for the entire program.
     * @return Always returns {@code null}, as this method serves as the entry point
     *         for semantic analysis.
     */
    @Override
    public Type visitProgram(EduScriptParser.ProgramContext ctx) {
        Scope global = new Scope(null);

        currentScope = global; // global scope
        for (var decl : ctx.globalDeclaration()) {
            visit(decl);
        }
        visit(ctx.mainBlock());
        return null;
    }

    @Override
    public Type visitMainBlock(MainBlockContext ctx) {
        currentScope = new Scope(currentScope);
        visit(ctx.statementList());
        return null;
    }

    @Override
    public Type visitBlock(BlockContext ctx) {
        currentScope = new Scope(currentScope);
        visit(ctx.statementList());
        return null;
    }

    /**
     * Processes a variable declaration by adding it to the current scope's symbol
     * table.
     * <p>
     * If the variable has already been declared in the same scope, a
     * {@link SemanticException}
     * is thrown and an error message is printed.
     * 
     * @param ctx The parser context for the variable declaration.
     * @return Always returns {@code null}, as this method does not infer expression
     *         types.
     */
    @Override
    public Type visitVariableDeclaration(EduScriptParser.VariableDeclarationContext ctx) {
        String varName = ctx.ID().getText();
        Type type = resolveType(ctx.type());

        Symbol symbol;

        if (ctx.type().arrayType() != null) {
            // é array
            EduScriptParser.ArrayTypeContext arrayCtx = ctx.type().arrayType();
            int length = resolveArrayLength(arrayCtx);
            symbol = new ArraySymbol(varName, type, length);
        } else {
            // tipo primitivo ou outro composto
            symbol = new VariableSymbol(varName, type);
        }

        try {
            currentScope.define(symbol);
        } catch (SemanticException e) {
            // Convert old exception to new standardized format
            SymbolExistsScopeException newException = new SymbolExistsScopeException(
                    varName, VAR_SBL_T, getLine(ctx), getColumn(ctx));
            errorHandler.reportError(newException);
        }

        return null;
    }

    @Override
    public Type visitFunctionDeclaration(EduScriptParser.FunctionDeclarationContext ctx) {
        String functionName = ctx.ID().getText();
        Type returnType = resolveType(ctx.type());

        // Escopo local da função
        Scope functionScope = new Scope(currentScope);
        List<Symbol> parameters = new ArrayList<>();

        if (ctx.parameters() != null) {
            for (EduScriptParser.ParameterContext paramCtx : ctx.parameters().parameter()) {
                String paramName = paramCtx.ID().getText();
                Type paramType = resolveType(paramCtx.type());

                VariableSymbol paramSymbol = new VariableSymbol(paramName, paramType);
                try {
                    functionScope.define(paramSymbol);
                    parameters.add(paramSymbol);
                } catch (SemanticException ex) {
                    // Report parameter name conflict, not function name
                    SymbolExistsScopeException paramException = new SymbolExistsScopeException(
                            paramName, PARAM_SBL_T, getLine(paramCtx), getColumn(paramCtx));
                    errorHandler.reportError(paramException);
                }
            }
        }

        // Cria símbolo da função
        FunctionSymbol functionSymbol = new FunctionSymbol(functionName, returnType, parameters, functionScope);
        try {
            currentScope.define(functionSymbol);
        } catch (SemanticException e) {
            // Use standardized error handling instead of System.err.println
            SymbolExistsScopeException functionException = new SymbolExistsScopeException(
                    functionName, FUN_SBL_T, getLine(ctx), getColumn(ctx));
            errorHandler.reportError(functionException);
        }

        // Visita o corpo da função
        Scope previous = currentScope;
        currentScope = functionScope;
        visit(ctx.block());
        currentScope = previous;

        return null;
    }

    /*
     * a cada atribuição, verifica se a variável foi declarada antes (linguagem
     * estática),
     * e verifica se o tipo da variável é compatível com o tipo do valor sendo
     * atribuído
     */
    @Override
    public Type visitAssignment(EduScriptParser.AssignmentContext ctx) {
        String id = ctx.ID().getText();
        Symbol sym = currentScope.resolve(id);

        if (sym == null) {
            UndeclaredVariableException exception = new UndeclaredVariableException(
                    id, getLine(ctx), getColumn(ctx));
            errorHandler.reportError(exception);
            return Type.INVALIDO;
        }

        // Verifica se é uma atribuição com índice (array)
        if (ctx.LBRACK() != null) {
            if (!(sym instanceof ArraySymbol)) {
                ArrayAccessException exception = new ArrayAccessException(
                        id, getLine(ctx), getColumn(ctx));
                errorHandler.reportError(exception);
                return Type.INVALIDO;
            }

            ArraySymbol arraySym = (ArraySymbol) sym;

            List<EduScriptParser.ExpressionContext> expressions = ctx.expression();
            if (expressions.size() < 2) {
                ArrayAssignmentException exception = new ArrayAssignmentException(
                        id, getLine(ctx), getColumn(ctx));
                errorHandler.reportError(exception);
                return Type.INVALIDO;
            }

            // Verifica o índice
            Type indexType = visit(expressions.get(0));
            if (indexType != Type.INTEIRO) {
                TypeMismatchException exception = TypeMismatchException.forVariable(
                        String.format("array index for '%s'", id), Type.INTEIRO, indexType,
                        getLine(ctx), getColumn(ctx));
                errorHandler.reportError(exception);
                return Type.INVALIDO;
            }

            // Verifica o valor atribuído (última expressão)
            Type valueType = visit(expressions.get(1));
            if (arraySym.getType() != valueType) {
                TypeMismatchException exception = TypeMismatchException.forVariable(
                        String.format("array '%s'", id), arraySym.getType(), valueType,
                        getLine(ctx), getColumn(ctx));
                errorHandler.reportError(exception);
            }

        } else {
            // Atribuição simples
            Type exprType = visit(ctx.expression(0));
            if (sym.getType() != exprType) {
                TypeMismatchException exception = TypeMismatchException.forVariable(
                        id, sym.getType(), exprType, getLine(ctx), getColumn(ctx));
                errorHandler.reportError(exception);
            }
        }

        return null;
    }

    /**
     * Performs type inference and validation on an EduScript expression node.
     * <p>
     * Supports arithmetic, boolean, and relational operations, ensuring operands
     * are of compatible types. Also handles constants and identifiers by verifying
     * declarations and inferring their types. Returns {@code Type.INVALIDO} if
     * any type error is detected.
     *
     * @param ctx The parser context for the expression.
     * @return The resulting type, or {@code Type.INVALIDO} if invalid.
     */
    @Override
    public Type visitExpression(EduScriptParser.ExpressionContext ctx) {
        if (ctx.op != null) {
            Type left = visit(ctx.expression(0));
            Type right = visit(ctx.expression(1));
            String operator = ctx.op.getText();

            switch (operator) {
                case "+":
                case "-":
                case "*":
                case "/":
                    if ((left == Type.INTEIRO || left == Type.REAL) && left == right)
                        return left;
                    TypeMismatchException arithmeticException = TypeMismatchException.forOperation(
                            operator, left, right, getLine(ctx), getColumn(ctx));
                    errorHandler.reportError(arithmeticException);
                    return Type.INVALIDO;
                case "==":
                case "!=":
                case "<":
                case "<=":
                case ">":
                case ">=":
                    return Type.LOGICO;
                case "e":
                case "ou":
                    if (left == Type.LOGICO && right == Type.LOGICO)
                        return Type.LOGICO;
                    TypeMismatchException logicalException = TypeMismatchException.forOperation(
                            operator, left, right, getLine(ctx), getColumn(ctx));
                    errorHandler.reportError(logicalException);
                    return Type.INVALIDO;
            }
        } else if (ctx.constant() != null) {
            return visit(ctx.constant());
        } else if (ctx.ID() != null) {
            Symbol sym = currentScope.resolve(ctx.ID().getText());
            if (sym == null) {
                UndeclaredVariableException exception = new UndeclaredVariableException(
                        ctx.ID().getText(), getLine(ctx), getColumn(ctx));
                errorHandler.reportError(exception);
                return Type.INVALIDO;
            }
            return sym.getType();
        }
        return Type.INVALIDO;
    }

    @Override
    public Type visitConstant(EduScriptParser.ConstantContext ctx) {
        if (ctx.INT() != null)
            return Type.INTEIRO;
        if (ctx.REAL() != null)
            return Type.REAL;
        if (ctx.STRING() != null)
            return Type.CADEIA;
        if (ctx.CHAR() != null)
            return Type.CARACTERE;
        if (ctx.getText().equals("verdadeiro") || ctx.getText().equals("falso"))
            return Type.LOGICO;
        return Type.INVALIDO;
    }

    private Type resolveType(EduScriptParser.TypeContext ctx) {
        if (ctx.arrayType() != null) {
            return resolveType(ctx.arrayType().type());
        }

        switch (ctx.getText()) {
            case "inteiro":
                return Type.INTEIRO;
            case "real":
                return Type.REAL;
            case "logico":
                return Type.LOGICO;
            case "caractere":
                return Type.CARACTERE;
            case "cadeia":
                return Type.CADEIA;
            default:
                return Type.INVALIDO;
        }
    }

    private int resolveArrayLength(EduScriptParser.ArrayTypeContext ctx) {
        EduScriptParser.RangeContext range = ctx.range(0);
        int start = 0;
        int end = Integer.parseInt(range.INT().getText());
        return end - start; // inclusive
    }
}
