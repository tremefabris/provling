grammar ProvLing;

programa:
    identificador_prova questao*;

identificador_prova:
    'QUESTOES'? 'PARA'? 'PROVA' IDENT;

questao:
    identificador_questao enunciado alternativas resposta explicacoes? fim_questao;

identificador_questao:
    'QUESTAO' OP_ATRIB IDENT;

enunciado:
    'ENUNCIADO' OP_ATRIB FRASE;

alternativas:
    'ALTERNATIVAS' OP_ATRIB (LETRA OP_ATRIB FRASE)+;

resposta:
    'RESPOSTA' OP_ATRIB (LETRA);

explicacoes:
    'EXPLICACOES' OP_ATRIB (LETRA OP_ATRIB FRASE)+;

fim_questao:
    'FIM' 'QUESTAO' IDENT;

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

OP_ATRIB:
    ':';

// ============ IGNORABLE SYMBOLS ============

IGNORAVEL:
    ( ' ' | '\n' | '\t' | '\r' ) -> skip;

// ============ WILD-CARD ============

DESCONHECIDO:
    .;