package br.ufscar.dc.compiladores.provling;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;

import org.antlr.v4.runtime.tree.TerminalNode;

/*
 * Semantic Tree Visitor
 * 
 * The ProvLingSemantic class is responsible for handling the necessary
 * components to make ProvLing work. It extracts data from the input file,
 * instantiates the necessary objects to deal with said data, and handles
 * errors thrown its way (as well as throwing its fair share).
 */


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

    /*
     * The ProvLingSemantic constructors basically just receive the
     * folder to dump data and initialize the /home/user/.provling/
     * folder to store questions.
     */

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

    /*
     * ProvLingSemantic starts here, but doesn't do much.
     */
    @Override
    public Void visitPrograma(ProvLingParser.ProgramaContext ctx) {

        return super.visitPrograma(ctx);
    }

    /*
     * Retrieves the current prova's ID.
     */
    @Override
    public Void visitIdentificador_prova(ProvLingParser.Identificador_provaContext ctx) {

        current_prova_id = ctx.IDENT().getText();

        return super.visitIdentificador_prova(ctx);
    }

    /*
     * Question-mode visitor
     * 
     * Instantiates a DataCreator, which handles question organization
     * and data storing, and uses its to begin a new question.
     */
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

    /*
     * Question-mode visitor
     * 
     * Retrieves the question's ID.
     * Errors out if ID is duplicate.
     */
    @Override
    public Void visitIdentificador_questao(ProvLingParser.Identificador_questaoContext ctx) {

        try {
            dc.withQuestionId(ctx.IDENT().getText());
        }
        catch (RuntimeException re) {
            throw new RuntimeException(
                "Linha " + ctx.IDENT().getSymbol().getLine() + ": " +
                re.getMessage()
            );
        }
        
        return super.visitIdentificador_questao(ctx);
    }

    /*
     * Question-mode visitor
     * 
     * Retrieves the ENUNCIADO from the question and passes it
     * to DataCreator.
     */
    @Override
    public Void visitEnunciado(ProvLingParser.EnunciadoContext ctx) {

        dc.withEnunciado(this._strip(ctx.FRASE().getText()));

        return super.visitEnunciado(ctx);
    }

    /*
     * Question-mode visitor
     * 
     * Retrieves ALTERNATIVAS from question, checks if they are in order, and
     * passes them to DataCreator.
     * Warns the user if a pair of ALTERNATIVAS aren't in order.
     */
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

    /*
     * Question-mode visitor
     * 
     * Retrieves the RESPOSTA from the question and passes it to DataCreator
     */
    @Override
    public Void visitResposta(ProvLingParser.RespostaContext ctx) {

        dc.withResposta(ctx.LETRA().getText());

        return super.visitResposta(ctx);
    }

    /*
     * Question-mode visitor
     * 
     * Retrieves the EXPLICACOES from the question and passes it to DataCreator.
     * Errors out if the number of EXPLICACOES is different from the number of
     * RESPOSTAS.
     */
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

    /*
     * Question-mode visitor
     * 
     * Finishes the question creation, builds it and stores the data.
     */
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

    /*
     * Exam Generation-mode visitor
     * 
     * Initializes a ProvBuilder (responsible for creating the actual exam)
     * and passes necessary information to it.
     * Errors out if the prova's ID doesn't lead to a proper file.
     */
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

    /*
     * Exam Generation-mode visitor
     * 
     * Uses ProvBuilder to properly create the headers.
     */
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

    /*
     * Exam Generation-mode visitor
     * 
     * Passes the INSTITUICAO information to ProvBuilder.
     */
    @Override
    public Void visitInstituicao(ProvLingParser.InstituicaoContext ctx) {

        String instituicao_raw = ctx.FRASE().getText();
        pb.defineInstituicao(this._strip(instituicao_raw));

        return super.visitInstituicao(ctx);
    }

    /*
     * Exam Generation-mode visitor
     * 
     * Passes the DISCIPLINA information to ProvBuilder.
     */
    @Override
    public Void visitDisciplina(ProvLingParser.DisciplinaContext ctx) {

        String disciplina_raw = ctx.FRASE().getText();
        pb.defineDisciplina(this._strip(disciplina_raw));

        return super.visitDisciplina(ctx);
    }

    /*
     * Exam Generation-mode visitor
     * 
     * Passes the DOCENTES information to ProvBuilder.
     */
    @Override
    public Void visitDocentes(ProvLingParser.DocentesContext ctx) {

        for (TerminalNode docente : ctx.FRASE()) {
            pb.defineDocente(this._strip(docente.getText()));
        }

        return super.visitDocentes(ctx);
    }

    /*
     * Exam Generation-mode visitor
     * 
     * Warns the user if a pair of DIRETRIZES are weirdly ordered.
     */
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

    /*
     * Exam Generation-mode visitor
     * 
     * Passes a DIRETRIZ to ProvBuilder.
     */
    @Override
    public Void visitDiretriz(ProvLingParser.DiretrizContext ctx) {

        String num = ctx.NUM_INT() != null ?
                        ctx.NUM_INT().getText() :
                        ctx.NUM_REAL().getText();
        String diretriz_raw = ctx.FRASE().getText();

        pb.defineDiretriz(num, this._strip(diretriz_raw));

        return super.visitDiretriz(ctx);
    }

    /*
     * Exam Generation-mode visitor
     * 
     * Orders ProvBuilder to add questions to the exam (following a logic defined
     * by the number of types and questions) and generates TeX file.
     * 
     * Errors out if TeX file was unable to be created or if something bad happens
     * while ProvBuilder is adding questions.
     */
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

    /*
     * Exam Generation-mode visitor
     * 
     * Passes the configurations to execute the exam generation, such
     * as amount of exam types and amount of questions in each exam.
     */
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

    public String __debugGetProva() {
        return this.pb.__debugGetProva();
    }
}
