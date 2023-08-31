package br.ufscar.dc.compiladores.provling;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.CommonTokenStream;

import br.ufscar.dc.compiladores.provling.ProvLingParser.ProgramaContext;


public class Principal {

    static String checkForLexicalError(Token token, String tokenType) {
        String errorMessage = null;

        if (tokenType == "UNIDENTIFIED_SYMBOL")
            errorMessage = "Linha " + token.getLine() + ": " +
                            token.getText() + " - simbolo nao identificado\n";

        else if (tokenType == "UNCLOSED_COMMENT")
            errorMessage = "Linha " + token.getLine() + ": comentario nao fechado\n";

        else if (tokenType == "UNCLOSED_CADEIA")
            errorMessage = "Linha " + token.getLine() + ": cadeia literal nao fechada\n";
        
        return errorMessage;
    }

    static void guaranteeExistingDirectory(String filePath) {
        
            File outFile = new File(filePath);
            if (!outFile.exists()){
                //outFile.createNewFile();
                boolean created = outFile.mkdirs();
                if(!created){
                System.out.println("An OTHER different error occurred.");
                System.exit(-1);
            }


            }else{
                if (!outFile.isDirectory()){
                    //outFile.createNewFile();
                    if (!outFile.exists()){
                        System.out.println("não foi criado o arquivo");
                    }
                    boolean created = outFile.mkdirs();
                    if(!created){
                        System.out.println("não foi criado o diretorio");
                        System.exit(-1);
                    }
                    
                }

            }
           

        
    }

    public static void main(String[] args) {
        
        try{
            String errorMessage = null;
            Boolean hasLexicalError = false;


            /* Lexer, token stream, and parser configuration */
            CharStream cs = CharStreams.fromFileName(args[0]);
            ProvLingLexer lex = new ProvLingLexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(lex);
            ProvLingParser parser = new ProvLingParser(tokens);
            ProgramaContext prog = parser.programa();

            /* Creating output file to write results */
            String outputFilename = args[1];
            guaranteeExistingDirectory(outputFilename);
            //PrintWriter outputWriter = new PrintWriter(outputFilename, "UTF-8");

            /* Add our custom SyntaxErrorListener */
            parser.removeErrorListeners();
            SyntaxErrorListener sel = new SyntaxErrorListener();
            parser.addErrorListener(sel);

            /**
             * First, we run through the program once to check for lexical errors
             */
            Token t = null;
            while((t = lex.nextToken()).getType() != Token.EOF) {

                String tokenType = ProvLingLexer.VOCABULARY.getDisplayName(t.getType());

                if ((errorMessage = checkForLexicalError(t, tokenType)) != null) {
                    hasLexicalError = true;
                    System.out.println(errorMessage);
                    //outputWriter.print(errorMessage);
                    break;
                }
            }

            /**
             * If no lexical error has been caught, we check for syntax 
             * and semantic errors
             */
            if (!hasLexicalError) {
                try{
                    lex.reset();

                    ProgramaContext tree = parser.programa();
                    ProvLingSemantic sem = new ProvLingSemantic();

                    sem.visitPrograma(tree);

                    for (String error: ProvLingSemanticUtils.semanticErrors) {
                        
                        //outputWriter.println(error);
                        System.out.println(error);
                    }

                } catch (ParseCancellationException e) {
                    //outputWriter.println(e.getMessage());
                }
            }

            Gerador ger = new Gerador(outputFilename);
            ger.visitPrograma(prog);
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }/*

        

        try {
            
            CharStream cs = CharStreams.fromFileName(args[0]);
            ProvLingLexer lex = new ProvLingLexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(lex);
            ProvLingParser parser = new ProvLingParser(tokens);

            String outputFilename = args[1];
            guaranteeExistingDirectory(outputFilename);
            

            ProgramaContext tree = parser.programa();
            ProvLingSemantic sem = new ProvLingSemantic();

            sem.visitPrograma(tree);

             Token t = null;

             while( (t = lex.nextToken()).getType() != Token.EOF ) {

               String tokenText = "\'" + t.getText() + "\'";
                 String tokenType = ProvLingLexer.VOCABULARY.getDisplayName(t.getType());

                 System.out.println(
                     "<" + tokenText + ", " + tokenType + ">"
                 );

             }


            Gerador ger = new Gerador(outputFilename);





            
        }





        catch (IOException e) {
            
            e.printStackTrace();

        }
  */

  
    }
    
    

}
