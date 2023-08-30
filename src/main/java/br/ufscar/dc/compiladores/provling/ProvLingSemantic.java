package br.ufscar.dc.compiladores.provling;

import br.ufscar.dc.compiladores.provling.SymbolTable;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ProvLingSemantic extends ProvLingBaseVisitor<Void> {

    Stack<String> stack;
    
    SymbolTable local_table;
    
    public int letterToNumber(String letter){
        int a;

        switch (letter) {
            case "A":
                a=0;
                break;
            case "B":
                a=1;
                break;
            case "C":
                a=2;
                break;
            case "D":
                a=3;
                break;
            case "E":
                a=4;
                break;
            case "F":
                a=5;
                break;
            case "G":
                a=6;
                break;
            case "H":
                a=7;
                break;
            case "I":
                a=8;
                break;
            case "J":
                a=9;
                break;
            case "K":
                a=10;
                break;
            case "L":
                a=11;
                break;
            case "M":
                a=12;
                break;
            case "N":
                a=13;
                break;
            case "O":
                a=14;
                break;
            case "P":
                a=15;
                break;
            case "Q":
                a=16;
                break;
            case "R":
                a=17;
                break;
            case "S":
                a=18;
                break;
            case "T":
                a=19;
                break;
            case "U":
                a=20;
                break;
            case "V":
                a=21;
                break;
            case "W":
                a=22;
                break;
            case "X":
                a=23;
                break;
            case "Y":
                a=24;
                break;
            case "Z":
                a=25;
                break;
            default:
                a=26;
                break;
        }
        return a; 
    }
    
    @Override
    public Void visitPrograma(ProvLingParser.ProgramaContext ctx) {
        
        stack = new Stack<String>();
        System.out.println("PROGRAMA COMEÇOU");

        

        return super.visitPrograma(ctx);
    }

    @Override
    public Void visitQuestao(ProvLingParser.QuestaoContext ctx){
        
        stack.push(ctx.identificador_questao().IDENT().getText());
        local_table = new SymbolTable();

        return super.visitQuestao(ctx);

    }

    @Override
    public Void visitAlternativas(ProvLingParser.AlternativasContext ctx){

        for (int i=0; i<ctx.LETRA().size(); i++){
            int b = letterToNumber(ctx.LETRA(i).getText());
            if(!(i== b)){
                ProvLingSemanticUtils.addSemanticError(ctx.LETRA(i).getSymbol(), "Letra não é a letra adequada ");
            }else{ 
                    if(local_table.existsequal(ctx.FRASE(i).getText())){
                        ProvLingSemanticUtils.addSemanticError(ctx.FRASE(i).getSymbol(), "existe uma alternativa exatamente igual");
                    }else{
                        local_table.add(ctx.LETRA(i).getText(), ctx.FRASE(i).getText());
                    }
            }
            
        }

        return super.visitAlternativas(ctx);
    }
    
    @Override
    public Void visitResposta(ProvLingParser.RespostaContext ctx){
        if(!local_table.exists(ctx.LETRA().getText())){
            ProvLingSemanticUtils.addSemanticError(ctx.start, "alternativa dada como resposta não existe");
        }
        return super.visitResposta(ctx);
    }

    @Override
    public Void visitExplicacoes(ProvLingParser.ExplicacoesContext ctx){
        for(int i=0; i<ctx.LETRA().size(); i++){
             if(! (i == letterToNumber(ctx.LETRA(i).getText()))){

             }
        }
       


        return super.visitExplicacoes(ctx);
    }

    @Override
    public Void visitFim_questao(ProvLingParser.Fim_questaoContext ctx){
        String stacktop = stack.peek() ;
        String identi = ctx.IDENT().getText();
        if(!stacktop.equals(identi)){
            ProvLingSemanticUtils.addSemanticError(ctx.start, "fechando questão errada");
        }else{
            stack.pop();
        }
        return super.visitFim_questao(ctx);
    }

}
