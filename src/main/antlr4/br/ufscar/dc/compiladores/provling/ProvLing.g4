grammar ProvLing;

programa:
    identificador_prova ( questao* | config_geracao );

identificador_prova:
    'QUESTOES'? 'PARA'? 'PROVA' OP_ATRIB IDENT;

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
    'FIM' 'QUESTAO' OP_ATRIB IDENT;

config_geracao:
    header config_prova;

header:
    header_info+;

header_info:
    instituicao |
    disciplina |
    docentes |
    diretrizes;

instituicao:
    'INSTITUICAO' OP_ATRIB FRASE;

disciplina:
    'DISCIPLINA' OP_ATRIB FRASE;

docentes:
    'DOCENTES' OP_ATRIB FRASE+;

diretrizes:
    'DIRETRIZES' OP_ATRIB diretriz*;

diretriz:
    NUM_INT OP_ATRIB FRASE;

config_prova:
    'CONFIG' OP_ATRIB configs+;

configs:
    'TIPOS' OP_ATRIB NUM_INT |
    'QUESTOES' OP_ATRIB NUM_INT |
    'ALUNOS' OP_ATRIB NUM_INT;

// ============ KEYWORDS AND IDENTIFIERS ============

PALAVRA_CHAVE:
    'QUESTÕES' | 'QUESTÃO' | 'PARA' | 'PROVA' | 'ENUNCIADO' | 'ALTERNATIVAS' | 'RESPOSTA' |
    'EXPLICACOES' | 'EXPLICACAO' | 'FIM';

LETRA:
    ('A'..'Z');

IDENT:
    ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '0'..'9' | '_')*;

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