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
            if(local_table.exists(ctx.LETRA(i).getText())){
                ProvLingSemanticUtils.addSemanticError(ctx.LETRA(i).getSymbol(), "letra já usada para identificar uma alternativa");
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
             if(!local_table.exists(ctx.LETRA(i).getText())){
                ProvLingSemanticUtils.addSemanticError(ctx.start, "letra explicada não está nas alternativas");
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
