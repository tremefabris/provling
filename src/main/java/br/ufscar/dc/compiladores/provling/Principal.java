package br.ufscar.dc.compiladores.provling;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import br.ufscar.dc.compiladores.provling.ProvLingParser.ProgramaContext;


public class Principal {

    static void validateArguments(String[] args) {

        Integer argc = args.length;

        if (argc != 1) {
            throw new IllegalArgumentException(
                "número de argumentos inválido: esperava 1, recebi " + argc
            );
        }

    }

    static void guaranteeFileExistence(Path path) {
        if (!path.toFile().exists()) {
            throw new InvalidPathException(
                path.toString(),
                "arquivo não encontrado (ele existe mesmo?)"
            );
        }
    }

    static PrintWriter setupOutputWriter(Path output_file) {
        try {
            File out_file = output_file.toFile();
            out_file.createNewFile();
            PrintWriter pw = new PrintWriter(out_file, "UTF-8");
            return pw;
        }
        catch (IOException e) {
            throw new InvalidPathException(
                output_file.toString(),
                "não foi possível criar arquivo de output (o caminho está correto?)"
            );
        }
    }

    static void setupSyntaxErrorListener(ProvLingParser parser) {
        parser.removeErrorListeners();
        SyntaxErrorListener sel = new SyntaxErrorListener();
        parser.addErrorListener(sel);
    }

    static Path getFolderFromFile(Path file) {
        return file.getParent();
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
            validateArguments(args);

            Path input_path = Paths.get(args[0]).toAbsolutePath();
            guaranteeFileExistence(input_path);

            CharStream cs = CharStreams.fromPath(input_path);
            ProvLingLexer lex = new ProvLingLexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(lex);
            ProvLingParser parser = new ProvLingParser(tokens);
            setupSyntaxErrorListener(parser);
            ProgramaContext tree = parser.programa();

            Path dump_folder = getFolderFromFile(input_path);
            ProvLingSemantic sem = new ProvLingSemantic(dump_folder);

            sem.visitPrograma(tree);
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

            System.out.println("Oops, something bad is happening...");

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
