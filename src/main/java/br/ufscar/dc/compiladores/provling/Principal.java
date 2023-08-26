package br.ufscar.dc.compiladores.provling;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.CommonTokenStream;

import br.ufscar.dc.compiladores.provling.ProvLingParser.ProgramaContext;


public class Principal {

    public static void main(String[] args) {

        try {
            
            CharStream cs = CharStreams.fromFileName(args[0]);
            ProvLingLexer lex = new ProvLingLexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(lex);
            ProvLingParser parser = new ProvLingParser(tokens);

            ProgramaContext tree = parser.programa();
            ProvLingSemantic sem = new ProvLingSemantic();

            sem.visitPrograma(tree);

            // Token t = null;

            // while( (t = lex.nextToken()).getType() != Token.EOF ) {

            //     String tokenText = "\'" + t.getText() + "\'";
            //     String tokenType = ProvLingLexer.VOCABULARY.getDisplayName(t.getType());

            //     System.out.println(
            //         "<" + tokenText + ", " + tokenType + ">"
            //     );

            // }



        }



        catch (IOException e) {
            
            e.printStackTrace();

        }

    }
}
