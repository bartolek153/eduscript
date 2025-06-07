grammar EduScript;

// ========================================
// PARSER RULES (Grammar Specifications)
// ========================================

program
    : PROGRAM ID SEMICOLON (globalDeclaration)* mainBlock;

globalDeclaration
    : variableDeclaration
    | constantDeclaration
    | functionDeclaration
    | importDeclaration;

importDeclaration 
    : IMPORT STRING SEMICOLON;

variableDeclaration
    : VAR ID COLON type SEMICOLON;

constantDeclaration
    : CONST ID COLON type ASSIGN expression SEMICOLON;

functionDeclaration
    : FUNCTION ID LPAREN parameters? RPAREN COLON type block;

parameters
    : parameter (COMMA parameter)*;

parameter
    : (REF? ID) COLON type;

type
    : INTEGER
    | REAL_TYPE
    | BOOLEAN
    | CHARACTER
    | STRING_TYPE
    | arrayType
    | recordType;

arrayType
    : ARRAY LBRACK range (COMMA range)? RBRACK FROM_OF type;

range
    : INT; // INT '..' INT;

recordType
    : RECORD LBRACE (variableDeclaration)+ RBRACE;

mainBlock
    : BEGIN statementList END_PROGRAM;

block
    : BEGIN statementList END;

statementList
    : (statement SEMICOLON)*;

statement
    : assignment
    | procedureCall
    | readStatement
    | writeStatement
    | conditional
    | whileLoop
    | forLoop
    | returnStatement
    | variableDeclaration;

assignment
    : ID (LBRACK expression (COMMA expression)? RBRACK)? ASSIGN expression;

procedureCall
    : ID LPAREN arguments? RPAREN;

readStatement
    : READ LPAREN idList RPAREN;

writeStatement
    : WRITE LPAREN expressionList RPAREN;

idList
    : ID (COMMA ID)*;

expressionList
    : expression (COMMA expression)*;

conditional
    : IF expression THEN statementList (ELSE statementList)? END_IF;

whileLoop
    : WHILE expression DO statementList END_WHILE;

forLoop
    : FOR ID FROM_OF expression TO expression (STEP expression)? DO statementList END_FOR;

returnStatement
    : RETURN expression;

arguments
    : expression (COMMA expression)*;

expression
    : expression op=(PLUS|MINUS) expression
    | expression op=(MULT|DIV) expression
    | expression op=(LT|GT|LE|GE|EQ|NE) expression
    | expression op=(AND|OR) expression
    | NOT expression
    | LPAREN expression RPAREN
    | functionCall
    | constant
    | ID
    | ID LBRACK expression (COMMA expression)? RBRACK;

functionCall
    : ID LPAREN arguments? RPAREN;

constant
    : INT
    | REAL
    | STRING
    | TRUE
    | FALSE
    | CHAR;

// ========================================
// LEXER RULES (Token Specifications)
// ========================================

// Keywords (Portuguese strings, English token names)
PROGRAM     : 'programa';
IMPORT      : 'importar';
VAR         : 'var';
CONST       : 'const';
FUNCTION    : 'funcao';
REF         : 'ref';
INTEGER     : 'inteiro';
REAL_TYPE   : 'real';
BOOLEAN     : 'logico';
CHARACTER   : 'caractere';
STRING_TYPE : 'cadeia';
ARRAY       : 'array';
FROM_OF     : 'de';
RECORD      : 'registro';
BEGIN       : 'inicio';
END         : 'fim';
END_PROGRAM : 'fimprograma';
READ        : 'ler';
WRITE       : 'escrever';
IF          : 'se';
THEN        : 'entao';
ELSE        : 'senao';
END_IF      : 'fimse';
WHILE       : 'enquanto';
DO          : 'faca';
END_WHILE   : 'fimenquanto';
FOR         : 'para';
TO          : 'ate';
STEP        : 'passo';
END_FOR     : 'fimpara';
RETURN      : 'retornar';
TRUE        : 'verdadeiro';
FALSE       : 'falso';
AND         : 'e';
OR          : 'ou';
NOT         : 'nao';

// Operators
PLUS        : '+';
MINUS       : '-';
MULT        : '*';
DIV         : '/';
LT          : '<';
GT          : '>';
LE          : '<=';
GE          : '>=';
EQ          : '==';
NE          : '!=';
ASSIGN      : '=';

// Delimiters
LPAREN      : '(';
RPAREN      : ')';
LBRACK      : '[';
RBRACK      : ']';
LBRACE      : '{';
RBRACE      : '}';
SEMICOLON   : ';';
COMMA       : ',';
COLON       : ':';

// Literals
ID          : [a-zA-Z_][a-zA-Z_0-9]*;
INT         : [0-9]+;
REAL        : [0-9]+ '.' [0-9]+;
CHAR        : '\'' . '\'';
STRING      : '"' (~["\r\n])* '"';

// Whitespace and comments
WS          : [ \t\r\n]+ -> skip;
COMMENT     : '//' ~[\r\n]* -> skip;
