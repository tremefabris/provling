package br.ufscar.dc.compiladores.provling;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    static String getFolderFromArgument(String path_arg) {
        return path_arg.substring(
            0,
            path_arg.lastIndexOf("/") + 1
        );
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
            
            Path input_file = Paths.get(args[0]).toAbsolutePath();

            CharStream cs = CharStreams.fromPath(input_file);
            ProvLingLexer lex = new ProvLingLexer(cs);

            CommonTokenStream tokens = new CommonTokenStream(lex);
            ProvLingParser parser = new ProvLingParser(tokens);

            setupSyntaxErrorListener(parser);

            ProgramaContext tree = parser.programa();
            // TODO: Fix -- breaks when using different folder
            ProvLingSemantic sem = new ProvLingSemantic();

            PrintWriter outputFile = setupOutputFile(args[1]);
            if (outputFile == null) {
                System.exit(-1);
            }

            sem.visitPrograma(tree);

            // TODO: Remove this
            String prova = sem.getProva();

            // TODO: Do something with this
            // System.out.print(prova);
            outputFile.print(prova);

            outputFile.close();

        }

        /*
         * Syntax error being handled in main function
         */
        catch (ParseCancellationException pce) {

            Logger.add(
                null,
                pce.getMessage(),
                Logger.Type.ERROR
            );

        }
        /*
         * Errors with file opening, CSVReader breaking, etc...
         */
        catch (IOException e) {
            
            Logger.add(
                null,
                "erro de I/O aconteceu",
                Logger.Type.ERROR
            );
            Logger.add(
                null,
                "retorno da JVM: " + e.getMessage(),
                Logger.Type.ERROR
            );

        }
        /*
         * All other exceptions
         * 
         * These are most likely exceptions thrown by us in the semantic visitor
         */
        catch (Exception e) {

            // System.out.println("Hey, remember me?");
            
            Logger.add(
                null,
                e.getMessage(),
                Logger.Type.ERROR
            );

        }

        finally {

            for (String L : Logger.logs)
                System.out.println(L);
        
        }
            
        
    }
}
