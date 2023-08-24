package br.ufscar.dc.compiladores.provling;

import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;


public class Principal {

    public static void main(String[] args) {

        try {
            
            CharStream cs = CharStreams.fromFileName(args[0]);
            ProvLingLexer lex = new ProvLingLexer(cs);

            Token t = null;

            while( (t = lex.nextToken()).getType() != Token.EOF ) {

                String tokenText = "\'" + t.getText() + "\'";
                String tokenType = ProvLingLexer.VOCABULARY.getDisplayName(t.getType());

                System.out.println(
                    "<" + tokenText + ", " + tokenType + ">"
                );

            }

        }



        catch (IOException e) {
            
            e.printStackTrace();

        }

    }
}
