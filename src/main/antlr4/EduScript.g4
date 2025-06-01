grammar EduScript;

program
    : 'programa' ID ';' (globalDeclaration)* mainBlock;

globalDeclaration
    : variableDeclaration
    | constantDeclaration
    | functionDeclaration
    | importDeclaration;

importDeclaration 
    : 'importar' STRING ';';

variableDeclaration
    : 'var' ID ':' type ';';

constantDeclaration
    : 'const' ID ':' type '=' expression ';';

functionDeclaration
    : 'funcao' ID '(' parameters? ')' ':' type block;

parameters
    : parameter (',' parameter)*;

parameter
    : ('ref'? ID) ':' type;

type
    : 'inteiro'
    | 'real'
    | 'logico'
    | 'caractere'
    | 'cadeia'
    | arrayType
    | recordType;

arrayType
    : 'array' LBRACK range (',' range)? RBRACK 'de' type;

range
    :INT; // INT '..' INT;

recordType
    : 'registro' '{' (variableDeclaration)+ '}';

mainBlock
    : 'inicio' statementList 'fimprograma';

block
    : 'inicio' statementList 'fim';

statementList
    : (statement ';')*;

statement
    : assignment
    | procedureCall
    | read
    | write
    | conditional
    | whileLoop
    | forLoop
    | returnStmt
	| variableDeclaration ;

assignment
    : ID (LBRACK expression (',' expression)? RBRACK)? '=' expression;

procedureCall
    : ID '(' arguments? ')';

read
    : 'ler' '(' idList ')';

write
    : 'escrever' '(' expressionList ')';

idList
    : ID (',' ID)*;

expressionList
    : expression (',' expression)*;

conditional
    : 'se' expression 'entao' statementList ('senao' statementList)? 'fimse';

whileLoop
    : 'enquanto' expression 'faca' statementList 'fimenquanto';

forLoop
    : 'para' ID 'de' expression 'ate' expression ('passo' expression)? 'faca' statementList 'fimpara';

returnStmt
    : 'retornar' expression;

arguments
    : expression (',' expression)*;

expression
    : expression op=('+'|'-') expression
    | expression op=('*'|'/') expression
    | expression op=('<'|'>'|'<='|'>='|'=='|'!=') expression
    | expression op=('e'|'ou') expression
    | 'nao' expression
    | '(' expression ')'
    | functionCall
    | constant
    | ID
	| ID LBRACK expression (',' expression)? RBRACK ;

functionCall
    : ID '(' arguments? ')';

constant
    : INT
    | REAL
    | STRING
    | 'verdadeiro'
    | 'falso'
    | CHAR;

ID      : [a-zA-Z_][a-zA-Z_0-9]*;
INT     : [0-9]+;
REAL    : [0-9]+ '.' [0-9]+;
CHAR    : '\'' . '\'';
STRING  : '"' (~["\r\n])* '"';
LBRACK  : '[';
RBRACK  : ']';

// RELOPS  : '>' | '<' | '>=' | '<=' | '==' | '!=';
// ARITOPS : '+'

WS          : [ \t\r\n]+ -> skip;
COMMENT     : '//' ~[\r\n]* -> skip;
