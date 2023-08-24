package br.ufscar.dc.compiladores.provling;

import br.ufscar.dc.compiladores.provling.ProvLingSemanticUtils;

public class ProvLingSemantic extends ProvLingBaseVisitor<Void> {
    


    @Override
    public Void visitPrograma(ProvLingParser.ProgramaContext ctx) {
        
        System.out.println("PROGRAMA COMEÇOU");
        ProvLingSemanticUtils.appendOutput("PROGRAMA COMEÇOU");

        return super.visitPrograma(ctx);
    }

}
