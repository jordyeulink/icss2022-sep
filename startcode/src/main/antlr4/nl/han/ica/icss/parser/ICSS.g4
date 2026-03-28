grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---
stylesheet: variable* ruleset* EOF;

variable: CAPITAL_IDENT ASSIGNMENT_OPERATOR value SEMICOLON;

ruleset: selector OPEN_BRACE (declaration | ifClause)* CLOSE_BRACE;

selector
    : LOWER_IDENT
    | ID_IDENT
    | CLASS_IDENT;

declaration: LOWER_IDENT COLON assignmentValue SEMICOLON;

ifClause : IF BOX_BRACKET_OPEN booleanType BOX_BRACKET_CLOSE OPEN_BRACE (declaration | ifClause)* CLOSE_BRACE (elseClause)?;

elseClause : ELSE OPEN_BRACE (declaration | ifClause)* CLOSE_BRACE;

assignmentValue
    : value
    | CAPITAL_IDENT
    | additionExpr;

additionExpr: multiplicationExpr (additionOperator multiplicationExpr)*;

multiplicationExpr: calculationValue ((multiplicationOperator) calculationValue)*;

calculationValue
    : CAPITAL_IDENT
    | PIXELSIZE
    | SCALAR;

multiplicationOperator: MUL;

additionOperator
    : PLUS
    | MIN;

booleanType
    : TRUE
    | FALSE
    | CAPITAL_IDENT;

value
    : COLOR
    | PIXELSIZE
    | TRUE
    | FALSE;

