package org.eduscript.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import main.antlr4.EduScriptBaseVisitor;
import main.antlr4.EduScriptParser;
import main.antlr4.EduScriptParser.BlockContext;
import main.antlr4.EduScriptParser.MainBlockContext;

public class SemanticAnalyzer extends EduScriptBaseVisitor<Type> {

    private Scope currentScope;
    private Stack<Scope> stack = new Stack<>();

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
        stack.push(global);
        
        currentScope = new Scope(null); // global scope
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
            int length = resolveArrayLength(arrayCtx); // método abaixo
            symbol = new ArraySymbol(varName, type, length);
        } else {
            // tipo primitivo ou outro composto
            symbol = new VariableSymbol(varName, type);
        }

        try {
            currentScope.define(symbol);
        } catch (SemanticException e) {
            System.err.println("Erro semântico: " + e.getMessage());
        }

        return null;
    }

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
                System.err.println("error in functionScope.define(paramSymbol);");
            }
        }
    }

    // Cria símbolo da função
    FunctionSymbol functionSymbol = new FunctionSymbol(functionName, returnType, parameters, functionScope);
    try {
        currentScope.define(functionSymbol);
    } catch (SemanticException e) {
        System.err.println("Erro semântico: " + e.getMessage());
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
            System.err.println("Erro: variável não declarada: " + id);
            return Type.INVALIDO;
        }

        // Verifica se é uma atribuição com índice (array)
        if (ctx.LBRACK() != null) {
            if (!(sym instanceof ArraySymbol)) {
                System.err.printf("Erro: variável %s não é um array, mas foi usada como tal%n", id);
                return Type.INVALIDO;
            }

            ArraySymbol arraySym = (ArraySymbol) sym;

            List<EduScriptParser.ExpressionContext> expressions = ctx.expression();
            if (expressions.size() < 2) {
                System.err.printf("Erro: atribuição a array %s exige índice e valor%n", id);
                return Type.INVALIDO;
            }

            // Verifica o índice
            Type indexType = visit(expressions.get(0));
            if (indexType != Type.INTEIRO) {
                System.err.printf("Erro: índice do array %s deve ser inteiro, mas é %s%n", id, indexType);
                return Type.INVALIDO;
            }

            // Verifica o valor atribuído (última expressão)
            Type valueType = visit(expressions.get(1));
            if (arraySym.getType() != valueType) {
                System.err.printf("Erro de tipo: array %s armazena %s, mas tentou atribuir %s%n", id,
                        arraySym.getType(), valueType);
            }

        } else {
            // Atribuição simples
            Type exprType = visit(ctx.expression(0));
            if (sym.getType() != exprType) {
                System.err.printf("Erro de tipo: variável %s é do tipo %s, mas expressão é %s%n", id, sym.getType(),
                        exprType);
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
            switch (ctx.op.getText()) {
                case "+":
                case "-":
                case "*":
                case "/":
                    if ((left == Type.INTEIRO || left == Type.REAL) && left == right)
                        return left;
                    System.err.println("Erro de tipo em operação aritmética.");
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
                    System.err.println("Erro de tipo: esperado tipo lógico.");
                    return Type.INVALIDO;
            }
        } else if (ctx.constant() != null) {
            return visit(ctx.constant());
        } else if (ctx.ID() != null) {
            Symbol sym = currentScope.resolve(ctx.ID().getText());
            if (sym == null) {
                System.err.println("Erro: identificador não declarado " + ctx.ID().getText());
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
