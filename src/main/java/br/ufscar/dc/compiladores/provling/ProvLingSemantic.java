package br.ufscar.dc.compiladores.provling;

import org.antlr.v4.runtime.tree.TerminalNode;

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

        if (ctx.config_geracao() != null) {
            pb = new ProvBuilder();
            pb.withMainFolder(MAIN_FOLDER);
            pb.addTemplateInfo();
        }

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
    public Void visitHeader(ProvLingParser.HeaderContext ctx) {

        for (ProvLingParser.Header_infoContext hctx : ctx.header_info()) {
            super.visitHeader_info(hctx);
        }

        pb.addHeaderDefinitions();
        pb.addHeaders();
        pb.addGuidelines();
        pb.addGradeTable();

        return null;
    }

    @Override
    public Void visitInstituicao(ProvLingParser.InstituicaoContext ctx) {

        String instituicao_raw = ctx.FRASE().getText();
        pb.defineInstituicao(this._strip(instituicao_raw));

        return super.visitInstituicao(ctx);
    }

    @Override
    public Void visitDisciplina(ProvLingParser.DisciplinaContext ctx) {

        String disciplina_raw = ctx.FRASE().getText();
        pb.defineDisciplina(this._strip(disciplina_raw));

        return super.visitDisciplina(ctx);
    }

    @Override
    public Void visitDocentes(ProvLingParser.DocentesContext ctx) {

        for (TerminalNode docente : ctx.FRASE()) {
            pb.defineDocente(this._strip(docente.getText()));
        }

        return super.visitDocentes(ctx);
    }

    @Override
    public Void visitDiretriz(ProvLingParser.DiretrizContext ctx) {

        // TODO: add semantic error if current Diretriz doesn't follow
        // TODO: previous Diretriz order
        //
        // EX:  1: bla bla
        //      2: bla bla
        //      4: bla bla 

        String num = ctx.NUM_INT() != null ?
                        ctx.NUM_INT().getText() :
                        ctx.NUM_REAL().getText();
        String diretriz_raw = ctx.FRASE().getText();

        pb.defineDiretriz(num, this._strip(diretriz_raw));

        return super.visitDiretriz(ctx);
    }

    /////////////////////////////////////////////////////////////////////////
    //  DEBUGGINGS, RETURNS
    /////////////////////////////////////////////////////////////////////////

    public String getProva() {
        return this.pb.__debugGetProva();
    }
}
