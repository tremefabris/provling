package br.ufscar.dc.compiladores.provling;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;

import org.antlr.v4.runtime.tree.TerminalNode;


public class ProvLingSemantic extends ProvLingBaseVisitor<Void> {
    
    /////////////////////////////////////////////////////////////////////////
    //  VARIABLES
    /////////////////////////////////////////////////////////////////////////


    // CONFIGURATION CONSTANTS
    Path DATA_FOLDER = Paths.get(System.getProperty("user.home"), ".provling").toAbsolutePath();
    Path DUMP_FOLDER;

    // INTERNAL RUNTIME INFORMATION
    String current_prova_id;
    
    // QUESTION BANK GENERATION
    DataCreator dc;
    boolean first_time_generating = true;

    Integer qtde_alt_in_question;

    // PROVA GENERATION
    ProvBuilder pb;


    /////////////////////////////////////////////////////////////////////////
    //  CONSTRUCTORS
    /////////////////////////////////////////////////////////////////////////


    public ProvLingSemantic() {   // dev option; don't use
        this.DUMP_FOLDER = Paths.get("tests/").toAbsolutePath();
        this._createDataFolderIfNeeded();
    }
    public ProvLingSemantic(Path dump_folder_path) {
        this.DUMP_FOLDER = dump_folder_path.toAbsolutePath();
        this._createDataFolderIfNeeded();
    }
    public ProvLingSemantic(String dump_folder_path) {
        // TODO: Handle InvalidPathException
        this.DUMP_FOLDER = Paths.get(dump_folder_path).toAbsolutePath();
        this._createDataFolderIfNeeded();
    }


    /////////////////////////////////////////////////////////////////////////
    //  HELPER FUNCTIONS
    /////////////////////////////////////////////////////////////////////////

    // TODO: Defer responsability to DataCreator
    private void _createDataFolderIfNeeded() {
        if (!this.DATA_FOLDER.toFile().exists()) {

            Logger.add(
                null,
                "criando pasta " + this.DATA_FOLDER + " para armazenar informações necessárias...",
                Logger.Type.WARNING
            );

            boolean created = this.DATA_FOLDER.toFile().mkdir();

            if (!created) {
                throw new RuntimeException(
                    "não foi possível criar pasta " + this.DATA_FOLDER
                );
            }
        }
    }

    private String _strip(String frase) {
        return frase.substring(1, frase.length() - 1);
    }

    private boolean _diretrizesOrderedCorrectly(String first_num, String sec_num) {

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

    private boolean _letrasOrderedCorrectly(String first_letra, String sec_letra) {

        Integer first_ascii = (int) first_letra.charAt(0);
        Integer sec_ascii   = (int) sec_letra.charAt(0);

        return (sec_ascii == first_ascii + 1);
    }

    /////////////////////////////////////////////////////////////////////////
    //  OVERRIDES -- SEMANTIC TREE TRAVERSING
    /////////////////////////////////////////////////////////////////////////

    @Override
    public Void visitPrograma(ProvLingParser.ProgramaContext ctx) {

        return super.visitPrograma(ctx);
    }

    @Override
    public Void visitIdentificador_prova(ProvLingParser.Identificador_provaContext ctx) {

        current_prova_id = ctx.IDENT().getText();

        return super.visitIdentificador_prova(ctx);
    }

    @Override
    public Void visitQuestao(ProvLingParser.QuestaoContext ctx) {

        if (this.first_time_generating) {
            first_time_generating = false;
            dc = new DataCreator(this.DATA_FOLDER, this.current_prova_id);
            dc.beginProva();
        }
        dc.createQuestion();

        return super.visitQuestao(ctx);
    }

    @Override
    public Void visitIdentificador_questao(ProvLingParser.Identificador_questaoContext ctx) {

        dc.withQuestionId(ctx.IDENT().getText());

        return super.visitIdentificador_questao(ctx);
    }

    @Override
    public Void visitEnunciado(ProvLingParser.EnunciadoContext ctx) {

        dc.withEnunciado(this._strip(ctx.FRASE().getText()));

        return super.visitEnunciado(ctx);
    }

    @Override
    public Void visitAlternativas(ProvLingParser.AlternativasContext ctx) {
        this.qtde_alt_in_question = ctx.FRASE().size();

        String curr_letra;
        String last_letra = null;
        for (Integer i = 0; i < this.qtde_alt_in_question; i++) {

            curr_letra = ctx.LETRA(i).getText();

            if (last_letra != null) {
                if (!this._letrasOrderedCorrectly(last_letra, curr_letra)) {

                    Logger.add(
                        ctx.LETRA(i).getSymbol(),
                        "alternativas " + last_letra + " e " + curr_letra +
                        " fora de ordem; consertando...",
                        Logger.Type.WARNING 
                    );
            }}
            last_letra = curr_letra;

            dc.withAlternativa(this._strip(ctx.FRASE(i).getText()));
        }
        
        return super.visitAlternativas(ctx);
    }

    @Override
    public Void visitResposta(ProvLingParser.RespostaContext ctx) {

        dc.withResposta(ctx.LETRA().getText());

        return super.visitResposta(ctx);
    }

    // TODO: Check if LETRAs are ordered correctly
    @Override
    public Void visitExplicacoes(ProvLingParser.ExplicacoesContext ctx) {

        if (this.qtde_alt_in_question != ctx.FRASE().size()) {
            throw new RuntimeException(
                "se houver EXPLICACOES, deve ter para todas as ALTERNATIVAS possíveis"
            );
        }

        for (Integer i = 0; i < ctx.FRASE().size(); i++) {
            dc.withExplicacao(this._strip(ctx.FRASE(i).getText()));
        }

        return super.visitExplicacoes(ctx);
    }

    @Override
    public Void visitFim_questao(ProvLingParser.Fim_questaoContext ctx) {

        try {
            dc.build().store();
        }
        catch (IOException ioe) {
            throw new RuntimeException(
                "um erro de I/0 ocorreu ao salvar informações da prova " + this.current_prova_id
            );
        }

        return super.visitFim_questao(ctx);
    }

    @Override
    public Void visitConfig_geracao(ProvLingParser.Config_geracaoContext ctx) {

        pb = new ProvBuilder();
        pb.withDataFolder(this.DATA_FOLDER);
        pb.withDumpFolder(this.DUMP_FOLDER);
        try {
            pb.withProvaId(current_prova_id);
        }
        catch (InvalidPathException ipe) {
            throw new InvalidPathException(
                ipe.getInput(),
                "Linha " + ctx.getStop().getLine() + ": " + ipe.getReason()
            );
        }
        
        pb.addTemplateInfo();
        pb.addDocumentClass();
        pb.addPackages();
        pb.addItemDefinitions();

        return super.visitConfig_geracao(ctx);
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
                    if (!this._diretrizesOrderedCorrectly(last_num, curr_num))
                        Logger.add(
                            dctx.getStart(),
                            "diretriz " + curr_num + " está desordenada",    
                            Logger.Type.WARNING
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
            this.visitConfigs(cctx);
        }

        try {
            pb.addQuestions();
        }
        catch (NullPointerException npe) {    
            throw new NullPointerException(
                "Linha " + ctx.getStart().getLine() + ": " +
                npe.getMessage()
            );
        }

        try {
            pb.generateTexFile();
        }
        catch (IOException e) {
            throw new RuntimeException(
                "não foi possível gerar arquivo TeX da prova " + this.current_prova_id
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
