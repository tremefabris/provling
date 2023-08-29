package br.ufscar.dc.compiladores.provling;

import java.util.List;
import java.util.ArrayList;

import org.antlr.v4.runtime.Token;


public class SemanticErrorHandler {
    
    public static List<String> errors = new ArrayList<String>();
    
    public static void addError(Token token, String msg) {
        Integer line = token.getLine();

        errors.add(String.format(
            "ERRO :: Linha %d: %s", line, msg
        ));
    }

    public static void addWarning(Token token, String msg) {
        Integer line = token.getLine();

        errors.add(String.format(
            "AVISO :: Linha %d: %s", line, msg
        ));
    }
}
