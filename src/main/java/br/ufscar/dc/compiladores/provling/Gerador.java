package br.ufscar.dc.compiladores.provling;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;

public class Gerador  extends ProvLingBaseVisitor<Void> {
    private String nome_prova;
    private String caminho;
    private String caminho_prova;
    StringBuilder saida;
    SymbolTable table;
    Scopes scopes;


    public Gerador(){

        saida = new StringBuilder();
        this.scopes = new Scopes();
        this.table = new SymbolTable();

    }

    public Gerador(String caminho){

        saida = new StringBuilder();
        this.scopes = new Scopes();
        this.table = new SymbolTable();
        this.caminho = caminho;

    }


    @Override
    public Void visitPrograma(ProvLingParser.ProgramaContext ctx) {
        nome_prova = ctx.identificador_prova().IDENT().getText();
        caminho_prova = caminho+"/"+nome_prova; 
        File file = new File(caminho_prova+".csv");
       
        try {
           
            FileWriter outputfile = new FileWriter(file);
    
           
            CSVWriter writer = new CSVWriter(outputfile);
    
            
            String[] header = { "prova", "questao" , "qtde_alternativas", "enunciado", "resposta" };
            writer.writeNext(header);
    
            
            
    
           
            for (int i = 0; i<ctx.questao().size(); i++){
                ProvLingParser.QuestaoContext ctx2 = ctx.questao(i);
                visitQuestao(ctx2);
                String questao_id = ctx2.identificador_questao().IDENT().getText();
                int quantidade = ctx2.alternativas().FRASE().size();
                String qtde_alternativas = Integer.toString(quantidade);
                String enunciado = ctx2.enunciado().FRASE().getText();
                String resposta = ctx2.resposta().LETRA().getText();
                String[] linha = { nome_prova, questao_id , qtde_alternativas, enunciado, resposta };
                writer.writeNext(linha);
                
            }

        writer.close();
           
        }
        catch (IOException e) {
            
            e.printStackTrace();
        }
        
        
        return null;
    }



    @Override
    public Void visitQuestao(ProvLingParser.QuestaoContext ctx){


        return null;
    }




    
}
