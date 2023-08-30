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

    private boolean _areOrderedCorrectly(String first_num, String sec_num) {

        Double first = Double.parseDouble(first_num);
        Double second = Double.parseDouble(sec_num);

        if (second - first <= 1.0) {
            if (second - first <= 0.1) {
                return true;
            }
            else if (second - first == 1.0) {
                return true;
            }
        }

        return false;
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
    public Void visitDiretrizes(ProvLingParser.DiretrizesContext ctx) {

        if (ctx.diretriz() != null) {

            String curr_num;
            String last_num = null;
            for (ProvLingParser.DiretrizContext dctx : ctx.diretriz()) {

                curr_num = dctx.NUM_INT() != null ?
                                dctx.NUM_INT().getText() :
                                dctx.NUM_REAL().getText();
                
                if (last_num != null)
                    if (!this._areOrderedCorrectly(last_num, curr_num))
                        SemanticErrorHandler.addWarning(
                            dctx.getStart(),
                            "diretriz " + curr_num + " está desordenada"    
                        );

                last_num = curr_num;
            }
        }

        return super.visitDiretrizes(ctx);
    }

    @Override
    public Void visitDiretriz(ProvLingParser.DiretrizContext ctx) {

        String num = ctx.NUM_INT() != null ?
                        ctx.NUM_INT().getText() :
                        ctx.NUM_REAL().getText();
        String diretriz_raw = ctx.FRASE().getText();

        pb.defineDiretriz(num, this._strip(diretriz_raw));

        return super.visitDiretriz(ctx);
    }

    @Override
    public Void visitConfig_prova(ProvLingParser.Config_provaContext ctx) {

        for (ProvLingParser.ConfigsContext cctx : ctx.configs()) {
            super.visitConfigs(cctx);
        }

        pb.addQuestions();
        pb.addEnding();

        try {
            pb.generateTexFile();
        }
        catch (Exception e) {

            System.out.println(" LOG :: Couldn't generate TeX file");
            e.printStackTrace();
            SemanticErrorHandler.addError(
                null,
                "TeX file was unable to be generated"
            );

        }

        return null;
    }

    @Override
    public Void visitConfigs(ProvLingParser.ConfigsContext ctx) {

        Integer config_value = Integer.parseInt(ctx.NUM_INT().getText());

        if (ctx.getText().startsWith("TIPOS")) {

            pb.withTypes(config_value);

        }
        if (ctx.getText().startsWith("QUESTOES")) {

            pb.withQuestions(config_value);

        }
        if (ctx.getText().startsWith("ALUNOS")) {

            pb.withParticipants(config_value);

        }

        return super.visitConfigs(ctx);
    }

    /////////////////////////////////////////////////////////////////////////
    //  DEBUGGINGS, RETURNS
    /////////////////////////////////////////////////////////////////////////

    public String getProva() {
        return this.pb.__debugGetProva();
    }
}
