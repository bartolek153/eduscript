grammar EduScript;

// ============
// PARSER RULES
// ============

pipeline:               PIPELINE value LBRACE pipelineBody RBRACE;
pipelineBody:           (envBlock | trgBlock | stageBlock)*;

envBlock:               ENV LBRACE envEntry* RBRACE;
envEntry:               ID EQ value NEWLINE?;

trgBlock:               EVERY trgType ON trgValue NEWLINE?;
trgType:                PUSH | PULL_REQUEST | TAG;
trgValue:               value;

stageBlock:             STAGE value LBRACE stageBody RBRACE;
stageBody:              (imageBlock | runBlock | needsBlock | configBlock)*;

imageBlock:             IMAGE value NEWLINE?;
runBlock:               RUN (value | multiLineString);
needsBlock:             NEEDS stringList NEWLINE?;
configBlock:            CONFIG LBRACE configEntry* RBRACE;
configEntry:            ID EQ value NEWLINE?;

value:                  TEXT | ID;
multiLineString:        TRIPLE_STRING;
stringList:             value (COM value)*;

// ===========
// LEXER RULES
// ===========

PIPELINE:               'pipeline';
ENV:                    'env';
EVERY:                  'every';
ON:                     'on';
PUSH:                   'push';
PULL_REQUEST:           'pull_request';
TAG:                    'tag';
STAGE:                  'stage';
IMAGE:                  'image';
RUN:                    'run';
NEEDS:                  'needs';
CONFIG:                 'config';

EQ:                     '=';
COM:                    ',';
LBRACE:                 '{';
RBRACE:                 '}';
NEWLINE:                ('\r'? '\n')+;
TEXT:                   '"' ( ~["\\] | '\\' .)* '"';
TRIPLE_STRING:          '"""' .*? '"""';
ID:             [a-zA-Z_][a-zA-Z0-9_\-]*;
WS:                     [ \t]+ -> skip;
COMMENT:                '#' ~[\r\n]* -> skip;
