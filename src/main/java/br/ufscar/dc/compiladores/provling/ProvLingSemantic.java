package br.ufscar.dc.compiladores.provling;


public class ProvLingSemantic extends ProvLingBaseVisitor<Void> {
    
    /////////////////////////////////////////////////////////////////////////
    //  VARIABLES
    /////////////////////////////////////////////////////////////////////////

    // CONFIGURATION CONSTANTS
    String MAIN_FOLDER = "tests/";  // TODO: make this the given file's folder (args[1])
    
    // INTERNAL RUNTIME INFORMATION
    String current_prova_id;
    ProvBuilder pb;

    /////////////////////////////////////////////////////////////////////////
    //  HELPER FUNCTIONS
    /////////////////////////////////////////////////////////////////////////

    private String _strip(String frase) {
        return frase.substring(1, frase.length() - 1);
    }

    /////////////////////////////////////////////////////////////////////////
    //  OVERRIDES -- SEMANTIC TREE TRAVERSING
    /////////////////////////////////////////////////////////////////////////

    @Override
    public Void visitPrograma(ProvLingParser.ProgramaContext ctx) {

        pb = new ProvBuilder();
        pb.withMainFolder(MAIN_FOLDER);
        pb.addTemplateInfo();

        return super.visitPrograma(ctx);
    }

    @Override
    public Void visitIdentificador_prova(ProvLingParser.Identificador_provaContext ctx) {

        current_prova_id = ctx.IDENT().getText();

        pb.withProvaId(current_prova_id);

        pb.addDocumentClass();
        pb.addPackages();
        pb.addItemDefinitions();

        return super.visitIdentificador_prova(ctx);
    }

    @Override
    public Void visitInstituicao(ProvLingParser.InstituicaoContext ctx) {

        String instituicao_raw = ctx.FRASE().getText();
        pb.defineInstituicao(this._strip(instituicao_raw));
        pb.addInstituicao();

        return super.visitInstituicao(ctx);
    }

    /////////////////////////////////////////////////////////////////////////
    //  DEBUGGINGS, RETURNS
    /////////////////////////////////////////////////////////////////////////

    public String getProva() {
        return this.pb.__debugGetProva();
    }
}
