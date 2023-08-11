grammar provling;

programa:
    identificador_prova questao*;

identificador_prova:
    'QUESTÕES'? 'PARA'? 'PROVA'? IDENT;

questao:
    identificador_questao enunciado alternativas resposta explicacoes? fim_questao;

identificador_questao:
    'QUESTÃO' IDENT;

enunciado:
    'ENUNCIADO' FRASE;

alternativas:
    'ALTERNATIVAS' (('a'..'z' | 'A'..'Z') ':' FRASE)+;

resposta:
    'RESPOSTA' ('a'..'z' | 'A'..'Z');

explicacoes:
    'EXPLICACOES' (('a'..'z' | 'A'..'Z') ':' FRASE)+;

fim_questao:
    'FIM' 'QUESTAO' IDENT;

IDENT:
    ('a'..'z' | 'A'..'Z' | '0'..'9' | '_')+;

FRASE:
    ('a'..'z' | 'A'..'Z' | '0'..'9') ('a'..'z' | 'A'..'Z' | '0'..'9' | ' ' | '_')*;

// talvez eu não devesse ignorar espaços em branco
IGNORABLE:
    ( ' ' | '\n' | '\t' ) -> skip;