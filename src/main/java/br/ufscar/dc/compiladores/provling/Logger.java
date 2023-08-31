package br.ufscar.dc.compiladores.provling;

import java.util.List;
import java.util.ArrayList;

import org.antlr.v4.runtime.Token;


public class Logger {
    
    public static enum Type {
        INFO, WARNING, ERROR
    }

    public static List<String> logs = new ArrayList<String>();

    private static String ANSI_RST     = "\033[0m";
    private static String ANSI_BLU_BLD = "\033[1;34m";
    // private static String ANSI_GRN_BLD = "\033[1;32m";
    private static String ANSI_YLW_BLD = "\033[1;33m";
    private static String ANSI_RED_BLD = "\033[1;31m";


    public static void add(Token token, String msg, Type type) {

        String token_line = token == null ?
                                "Geral" :
                                "Linha " + token.getLine();

        String type_str = null;
        switch (type) {
            case INFO:
                type_str = "[" + ANSI_BLU_BLD + "INFO" + ANSI_RST + "] ";
                break;

            case WARNING:
                type_str = "[" + ANSI_YLW_BLD + "AVISO" + ANSI_RST + "]";
                break;

            case ERROR:
                type_str = "[" + ANSI_RED_BLD + "ERRO" + ANSI_RST + "] ";
                break;
        }

        logs.add(String.format(
            "%s :: %s: %s", type_str, token_line, msg 
        ));
    }

}
