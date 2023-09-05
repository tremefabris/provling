package br.ufscar.dc.compiladores.provling;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import br.ufscar.dc.compiladores.provling.ProvLingParser.ProgramaContext;


/* 
 * ProvLing's entrypoint
 * 
 * The Principal class is the first to be executed. Inside of it, we have
 * functions to validate command-line arguments, to guarantee existence of
 * files, to setup necessary configurations, path manipulation and more.
 * 
 * Its functionality is rather simple: receive argument (in the form of a
 * file to read from), process it, setup the lexer, parser, etc, and start
 * the visitation of the semantic tree, where all the magic happens.
 */

public class Principal {

    /*
     * Validates command-line arguments
     * 
     * Receives a list of String and checks if there are more
     * than one argument present.
     */
    static void validateArguments(String[] args) {

        Integer argc = args.length;

        if (argc != 1) {
            throw new IllegalArgumentException(
                "número de argumentos inválido: esperava 1, recebi " + argc
            );
        }

    }

    /*
     * Guarantees the existence of a File (from its Path)
     * 
     * Throws an InvalidPathException if File doesn't exist
     */
    static void guaranteeFileExistence(Path path) {
        if (!path.toFile().exists()) {
            throw new InvalidPathException(
                path.toString(),
                "arquivo não encontrado (ele existe mesmo?)"
            );
        }
    }

    /*
     * Configures the custom syntax error listener
     */
    static void setupSyntaxErrorListener(ProvLingParser parser) {
        parser.removeErrorListeners();
        SyntaxErrorListener sel = new SyntaxErrorListener();
        parser.addErrorListener(sel);
    }

    /*
     * Self-documenting name
     * 
     * Does basic Path manipulation to retrieve its parent folder
     */
    static Path getFolderFromFile(Path file) {
        return file.getParent();
    }

    /*
     * Function to debug the lexer
     * 
     * Prints the lexer output to System.out
     */
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

    /*
     * Actual entrypoint of our program
     * 
     * More details inside...
     */
    public static void main(String[] args) {

        /*
         * Try-Catch-Finally statement as a way to internally deal
         * with semantic errors
         */
        try {

            /* Validate input file */
            validateArguments(args);
            Path input_path = Paths.get(args[0]).toAbsolutePath();
            guaranteeFileExistence(input_path);

            /* Configure lexer, parser, and semantic tree */
            CharStream cs = CharStreams.fromPath(input_path);
            ProvLingLexer lex = new ProvLingLexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(lex);
            ProvLingParser parser = new ProvLingParser(tokens);
            setupSyntaxErrorListener(parser);
            ProgramaContext tree = parser.programa();

            /* 
             * Retrieve folder to return the exam and initialize
             * the semantic tree traverser
             */
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

            Logger.add(
                null,
                e.getMessage(),
                Logger.Type.ERROR
            );

        }

        /*
         * Logging
         * 
         * Independently of the Exceptions thrown during the program's execution,
         * the logs are printed out
         */
        finally {

            for (String L : Logger.logs)
                System.out.println(L);
        
        }

    }
}
