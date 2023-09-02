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

    /////////////////////////////////////////////////////////////////////////
    //  OVERRIDES -- SEMANTIC TREE TRAVERSING
    /////////////////////////////////////////////////////////////////////////

    @Override
    public Void visitPrograma(ProvLingParser.ProgramaContext ctx) {

        if (ctx.config_geracao() != null) {
            pb = new ProvBuilder();
            pb.withDataFolder(this.DATA_FOLDER);
            pb.withDumpFolder(this.DUMP_FOLDER);
            pb.addTemplateInfo();
        }

        return super.visitPrograma(ctx);
    }

    @Override
    public Void visitIdentificador_prova(ProvLingParser.Identificador_provaContext ctx) {

        current_prova_id = ctx.IDENT().getText();

        try {
            pb.withProvaId(current_prova_id);
        }
        catch (InvalidPathException ipe) {
            throw new InvalidPathException(
                ipe.getInput(),
                "Linha " + ctx.getStop().getLine() + ": " + ipe.getReason()
            );
        }

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

            // Couldn't throw IOException, had to do this here
            Logger.add(
                null,
                "não foi possível gerar arquivo TeX",
                Logger.Type.ERROR
            );
            Logger.add(
                null,
                "retorno da JVM: " + e.getMessage(),
                Logger.Type.ERROR
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
