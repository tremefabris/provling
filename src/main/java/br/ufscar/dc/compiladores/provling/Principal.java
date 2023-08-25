package br.ufscar.dc.compiladores.provling;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import br.ufscar.dc.compiladores.provling.ProvLingParser.ProgramaContext;


public class Principal {

    static PrintWriter setupOutputFile(String filepath) {
        try {
            File outFile = new File(filepath);
            outFile.createNewFile();
            PrintWriter pw = new PrintWriter(outFile, "UTF-8");
            return pw;
        }
        catch (IOException e) {
            System.out.println("An error occured while opening file");
            e.printStackTrace();
            return null;
        }
    }

    static void setupSyntaxErrorListener(ProvLingParser parser) {
        parser.removeErrorListeners();
        SyntaxErrorListener sel = new SyntaxErrorListener();
        parser.addErrorListener(sel);
    }

    static void __debugLexer(ProvLingLexer lex) {
        Token t = null;

        while( (t = lex.nextToken()).getType() != Token.EOF ) {

            String tokenText = "\'" + t.getText() + "\'";
            String tokenType = ProvLingLexer.VOCABULARY.getDisplayName(t.getType());

            System.out.println(
                "<" + tokenText + ", " + tokenType + ">"
            );

        }

        lex.reset();
    }

    public static void main(String[] args) {

        try {
            
            CharStream cs = CharStreams.fromFileName(args[0]);
            ProvLingLexer lex = new ProvLingLexer(cs);

            // __debugLexer(lex);

            CommonTokenStream tokens = new CommonTokenStream(lex);
            ProvLingParser parser = new ProvLingParser(tokens);

            setupSyntaxErrorListener(parser);

            ProgramaContext tree = parser.programa();
            ProvLingSemantic sem = new ProvLingSemantic();

            PrintWriter outputFile = setupOutputFile(args[1]);
            if (outputFile == null) {
                System.exit(-1);
            }

            sem.visitPrograma(tree);

            // System.out.println(ProvLingSemanticUtils.output);
            outputFile.print(ProvLingSemanticUtils.output);


            outputFile.close();

        }

        /*
         * Syntax error being handled in main function
         */
        catch (ParseCancellationException pce) {

            System.out.println(pce.getMessage());

        }

        catch (IOException e) {
            
            e.printStackTrace();

        }

    }
}
