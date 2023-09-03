package br.ufscar.dc.compiladores.provling;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DataCreator {

    /////////////////////////////////////////////////////////////////////////
    //  VARIABLES
    /////////////////////////////////////////////////////////////////////////

    // RUNTIME CONFIGURATION
    Path data_folder;
    Path questions_folder;

    String prova_id;
    String question_id;

    String enunciado;
    List<String> alternativas = null;
    String resposta;
    List<String> explicacoes = null;

    String prov_buffer;
    String question_buffer;

    // CONSTANT CONFIGURATION
    String[] prov_header = {
        "prova", "questao", "enunciado", "qtde_alternativas", "resposta"
    };
    String[] question_header = {
        "prova", "questao", "alternativa", "explicacao"
    };

    String _sep = ",";


    /////////////////////////////////////////////////////////////////////////
    //  CONSTRUCTORS
    /////////////////////////////////////////////////////////////////////////

    public DataCreator(Path data_folder, String prova_id) {
        this.data_folder = data_folder;
        this.prova_id = prova_id;

        this.prov_buffer = new String("");
        this.question_buffer = new String("");
    }


    /////////////////////////////////////////////////////////////////////////
    //  DATA CREATION FUNCS
    /////////////////////////////////////////////////////////////////////////


    private void _q_add(String _t) {
        this.question_buffer += _t;
    }
    private DataCreator _q_addrs(String _t) {
        this._q_add(_t + this._sep);
        return this;
    }
    private DataCreator _q_addrl(String _t) {
        this._q_add(_t + "\n");
        return this;
    }

    private void _p_add(String _t) {
        this.prov_buffer += _t;
    }
    private DataCreator _p_addrs(String _t) {
        this._p_add(_t + this._sep);
        return this;
    }
    private DataCreator _p_addrl(String _t) {
        this._p_add(_t + "\n");
        return this;
    }


    public void beginProva() {
        this._addProvaHeaders();
        this._createQuestionsFolder();
    }

    public void createQuestion() {
        this._addQuestionHeader();
    }
    public void withQuestionId(String question_id) {
        this.question_id = question_id;
    }
    public void withEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }
    public void withAlternativa(String alternativa) {
        if (this.alternativas == null)
            this.alternativas = new ArrayList<String>();
        this.alternativas.add(alternativa);
    }
    public void withResposta(String resposta) {
        this.resposta = resposta;
    }
    public void withExplicacao(String explicacao) {
        if (this.explicacoes == null)
            this.explicacoes = new ArrayList<String>();
        this.explicacoes.add(explicacao);
    }
    
    public DataCreator build() {
        Integer qtde_alt = this.alternativas.size();

        // building question
        this._initEmptyExplicacaoIfNeeded();

        for (Integer i = 0; i < qtde_alt; i++) {

            this._q_addrs("\"" + this.prova_id + "\"")
                ._q_addrs("\"" + this.question_id + "\"")
                ._q_addrs("\"" + this.alternativas.get(i) + "\"")
                ._q_addrl("\"" + this.explicacoes.get(i) + "\"");

        }

        // building prova
        this._p_addrs("\"" + this.prova_id + "\"")
            ._p_addrs("\"" + this.question_id + "\"")
            ._p_addrs("\"" + this.enunciado + "\"")
            ._p_addrs(String.valueOf(qtde_alt))
            ._p_addrl("\"" + this.resposta + "\"");
        
        return this;
    }
    public void store() throws IOException {

        // storing question
        Path q_path = this.questions_folder.resolve(this.question_id + ".csv");

        Logger.add(
            null,
            "salvando questão " + this.question_id + " no endereço " + q_path,
            Logger.Type.INFO
        );

        File q_file = q_path.toFile();
        q_file.createNewFile();
        PrintWriter q_pw = new PrintWriter(q_file, "UTF-8");

        q_pw.print(this.question_buffer);
        q_pw.close();

        Logger.add(
            null,
            "questão " + this.question_id + " salva com sucesso!",
            Logger.Type.INFO
        );

        // storing prova
        Logger.add(
            null,
            "atualizando prova " + this.prova_id,
            Logger.Type.INFO
        );

        Path p_path = this.data_folder.resolve(this.prova_id + ".csv");

        File p_file = p_path.toFile();
        p_file.createNewFile();
        PrintWriter p_pw = new PrintWriter(p_file, "UTF-8");

        p_pw.print(this.prov_buffer);
        p_pw.close();

        Logger.add(
            null,
            "prova " + this.prova_id + " atualizada com sucesso!",
            Logger.Type.INFO
        );

        this._clearQuestionData();
    }

    /////////////////////////////////////////////////////////////////////////
    //  HELPER FUNCS
    /////////////////////////////////////////////////////////////////////////

    private void _addProvaHeaders() {
        for (String h : this.prov_header) {
            String _separator = (h == this.prov_header[this.prov_header.length - 1] ?
                                    "" : this._sep);

            this.prov_buffer += "\"" +  h + "\"" + _separator;
        }
        this.prov_buffer += "\n";
    }
    private void _addQuestionHeader() {
        for (String h : this.question_header) {
            String _separator = (h == this.question_header[this.question_header.length - 1] ?
                                    "" : this._sep);

            this.question_buffer += "\"" +  h + "\"" + _separator;
        }
        this.question_buffer += "\n";
    }

    private void _createQuestionsFolder() {
        Path questions_folder = this.data_folder.resolve("." + this.prova_id);
        if (!questions_folder.toFile().exists()) {

            Logger.add(
                null,
                "criando pasta " + questions_folder + " para armazenar dados de questão...",
                Logger.Type.WARNING
            );

            boolean created = questions_folder.toFile().mkdir();

            if (!created) {
                throw new RuntimeException(
                    "não foi possível criar pasta " + questions_folder
                );
            }
        }
        this.questions_folder = questions_folder;
        System.out.println(this.questions_folder);
    }

    private void _initEmptyExplicacaoIfNeeded() {
        if (this.explicacoes == null) {
            this.explicacoes = new ArrayList<String>(this.alternativas.size());

            for (Integer i = 0; i < this.alternativas.size(); i++)
                this.explicacoes.add(new String(""));
        }
    }
    
    private void _clearQuestionData() {
        this.enunciado = new String("");
        this.alternativas = null;
        this.resposta = new String("");
        this.explicacoes = null;

        this.question_buffer = new String("");
    }
    
    /////////////////////////////////////////////////////////////////////////
    //  DEBUG FUNCTIONS
    /////////////////////////////////////////////////////////////////////////

    private void __debugQuestions() {
        System.out.println(" :: __debugQuestions :: ");

        System.out.println(" :: QUESTOES :: ");
        System.out.println(this.question_buffer);

        System.out.println(" :: PROVA :: ");
        System.out.println(this.prov_buffer);

        System.out.println(" :: __debugQuestions :: ");
    }
}
