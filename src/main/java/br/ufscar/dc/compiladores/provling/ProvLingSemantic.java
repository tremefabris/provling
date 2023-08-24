package br.ufscar.dc.compiladores.provling;

public class ProvLingSemantic extends ProvLingBaseVisitor<Void> {
    
    @Override
    public Void visitPrograma(ProvLingParser.ProgramaContext ctx) {
        System.out.println("PROGRAMA COMEÇOU");

        return super.visitPrograma(ctx);
    }

}
