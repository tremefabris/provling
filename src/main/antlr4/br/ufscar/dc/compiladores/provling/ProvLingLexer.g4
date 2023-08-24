lexer grammar ProvLingLexer;

// programa:
//     identificador_prova questao*;

// identificador_prova:
//     'QUESTÕES'? 'PARA'? 'PROVA'? IDENT;

// questao:
//     identificador_questao enunciado alternativas resposta explicacoes? fim_questao;

// identificador_questao:
//     'QUESTÃO' IDENT;

// enunciado:
//     'ENUNCIADO' FRASE;

// alternativas:
//     'ALTERNATIVAS' (('a'..'z' | 'A'..'Z') ':' FRASE)+;

// resposta:
//     'RESPOSTA' ('a'..'z' | 'A'..'Z');

// explicacoes:
//     'EXPLICACOES' (('a'..'z' | 'A'..'Z') ':' FRASE)+;

// fim_questao:
//     'FIM' 'QUESTAO' IDENT;

// ============ KEYWORDS AND IDENTIFIERS ============

PALAVRA_CHAVE:
    'QUESTÕES' | 'QUESTÃO' | 'PARA' | 'PROVA' | 'ENUNCIADO' | 'ALTERNATIVAS' | 'RESPOSTA' |
    'EXPLICACOES' | 'EXPLICACAO' | 'FIM';

LETRA:
    ('A'..'Z');

IDENT:
    ('a'..'z' | 'A'..'Z' | '0'..'9' | '_')+;

// ============ SENTENCE HANDLING ============

fragment
ESC_SEQ:
    '\\"';

FRASE:
    '"' ( ESC_SEQ | ~( '"' | '\\' ) )* '"';

// is this right?
INCOMPLETA_FRASE:
    '"' ( ~( '"' ) )* ( '\n' | '\r' );

// ============ COMMENT HANDLING ============

COMENTARIO:
    '//' ~('\n' | '\r')* -> skip;

// ============ NUMBER HANDLING HANDLING ============

NUM_INT:
    ('0'..'9')+;

NUM_REAL:
    ('0'..'9')+ '.' ('0'..'9')+;

// ============ OPERATOR HANDLING ============

CONFIG_ATRIB:
    ':';

// ============ IGNORABLE SYMBOLS ============

IGNORAVEL:
    ( ' ' | '\n' | '\t' | '\r' ) -> skip;

// ============ WILD-CARD ============

DESCONHECIDO:
    .;