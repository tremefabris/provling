package br.ufscar.dc.compiladores.provling;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

/*
 * Question-Data Retriever
 * 
 * This class is responsible for retrieving questions' data that are already stored
 * in memory (which means a previous execution of ProvLing). It reads CSV files,
 * organizes the retrieved contents to properly return them to ProvBuilder, and
 * handles the logic for random question selection. It also has combinatoric functions
 * to warn the user if the amount of questions per exam, or types of exams, are
 * possible or not.
 */


public class QuestionManager {
    
    /////////////////////////////////////////////////////////////////////////
    //  QUESTION CLASS
    /////////////////////////////////////////////////////////////////////////

    private class Question {
        protected String prova_id;
        protected String question_id;
        protected String enunciado;
        protected List<String> alternativas;
        protected String resposta;
        protected List<String> explicacao = null;

        protected Question(String p_id, String q_id, String enun, String resposta, Integer num_alt) {
            this.prova_id = p_id;
            this.question_id = q_id;
            this.enunciado = enun;
            this.resposta = resposta;
            this.alternativas = new ArrayList<String>(num_alt);
        }

        protected void addAlternativa(String alternativa){
            this.alternativas.add(alternativa);
        }
        protected void addExplicacao(String explicacao){
            if (this.explicacao == null)
                this.explicacao = new ArrayList<String>();
            this.explicacao.add(explicacao);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    //  VARIABLES
    /////////////////////////////////////////////////////////////////////////

    private Path data_path;
    private String prova_id;

    private List<Question> questoes;

    /////////////////////////////////////////////////////////////////////////
    //  CONSTRUCTORS
    /////////////////////////////////////////////////////////////////////////

    private QuestionManager(Path csv_path) {
        this.data_path = csv_path;
        this.prova_id = this._getProvaIdFromPath(csv_path);
    }

    public static QuestionManager FromCSV(Path csv_path) {
    
        QuestionManager self = new QuestionManager(csv_path);

        self.questoes = self.readQuestions(true);
        self.buildQuestions(true);

        return self;
    }

    /////////////////////////////////////////////////////////////////////////
    //  CSV-READING FUNCS
    /////////////////////////////////////////////////////////////////////////

    // TODO: Handle exceptions
    private List<Question> readQuestions(boolean has_header) {

        try{

            List<Question> questoes = new ArrayList<Question>();

            CSVReader reader = new CSVReaderBuilder(
                               new FileReader(this.data_path.toString())).build();
            String[] csv_line;

            if (has_header)
                reader.skip(1);

            while ((csv_line = reader.readNext()) != null) {

                String p_id = csv_line[0];
                String q_id = csv_line[1];
                String enun = csv_line[2];
                Integer num_alt = Integer.parseInt(csv_line[3]);
                String resp = csv_line[4];

                questoes.add(
                    this.new Question(p_id, q_id, enun, resp, num_alt)
                );

            }

            return questoes;
        }

        catch (FileNotFoundException fe) {}
        catch (IOException ioe){}
        catch (CsvValidationException cve) {}

        return null;
    }
    // TODO: Handle exceptions
    private void buildQuestions(boolean has_header) {

        try{
            Path question_folder = this._getQuestionFolderFromPath(this.data_path);

            for (Question q : this.questoes) {
                Path question_path = question_folder.resolve(q.question_id + ".csv");
                
                CSVReader reader = new CSVReaderBuilder(
                                   new FileReader(question_path.toString())).build();
                String[] csv_line;

                if (has_header)
                    reader.skip(1);

                while ((csv_line = reader.readNext()) != null) {

                    q.addAlternativa(csv_line[2]);
                    if (csv_line.length > 3)
                        q.addExplicacao(csv_line[3]);

                }
            }
        }

        catch(FileNotFoundException fe) {}
        catch(IOException ioe) {}
        catch(CsvValidationException cve) {}
    }

    /////////////////////////////////////////////////////////////////////////
    //  QUESTION-ACESSING FUNCS
    /////////////////////////////////////////////////////////////////////////

    public Integer totalQuestions() {
        return this.questoes.size();
    }
    public String getEnunciadoFromQuestion(Integer idx) {
        return this.questoes.get(idx).enunciado;
    }
    public List<String> getAlternativasFromQuestion(Integer idx) {
        return this.questoes.get(idx).alternativas;
    }

    /////////////////////////////////////////////////////////////////////////
    //  COMBINATORIC FUNCS
    //  TODO: Refactor all of this in a CombinatoricsEngine class
    /////////////////////////////////////////////////////////////////////////

    public Integer possibleCombinations(Integer questions) {

        Integer total = this.questoes.size();
        
        if (questions > total) {
            return -1;
        }
        
        if (total == questions) {
            return 1;
        }

        Integer prod_total = 1;
        for (Integer i = 1; i <= total; i++)
            prod_total *= i;

        Integer prod_questions = 1;
        for (Integer i = 1; i <= questions; i++)
            prod_questions *= i;

        Integer prod_diff = 1;
        for (Integer i = 1; i <= (total - questions); i++)
            prod_diff *= 1;
        
        return ( prod_total / (prod_questions * prod_diff) );
    }
    // TODO: Treat the return of this.possibleCombinations properly
    // TODO: Add different algorithms for generation (now only has random)
    // TODO: Add possibility of final list to be ordered
    // TODO: Maybe it's better to use Set<Integer> instead of List<Integer>?
    public List<List<Integer>> generateCombinations(Integer num_types, Integer num_questions) {

        List<List<Integer>> combs = this._initListOfCombinations(num_types, num_questions);

        do {
            for (List<Integer> prova_type : combs) {
                prova_type.clear();
                prova_type.addAll(this._generateCombinationRandom(num_questions));
            }
        } while(this._existDuplicates(combs));
        
        return combs;
    }


    private List<List<Integer>> _initListOfCombinations(Integer num_types, Integer num_questions) {

        List<List<Integer>> combs = new ArrayList<List<Integer>>(num_types);
        for (Integer i = 0; i < num_types; i++)
            combs.add(i, new ArrayList<Integer>(num_questions));
        
        return combs;
    }
    private Set<Integer> _generateCombinationRandom(Integer num_questions) {

        Integer max_questions = this.questoes.size();
        Random prng = new Random();
        Set<Integer> _comb = new HashSet<Integer>(num_questions);

        do {
            Integer _n = prng.nextInt(max_questions);
            _comb.add(_n);
        } while(_comb.size() < num_questions);

        return _comb;
    }
    // TODO: Time-inefficient
    // TODO: If Lists are unordered but contain same elements, this won't catch it
    private boolean _existDuplicates(List<List<Integer>> combinations) {
        for (Integer i = 0; i < combinations.size(); i++)
            for (Integer j = i + 1; j < combinations.size(); j++)
                if (combinations.get(i).equals(combinations.get(j)))
                    return true;

        return false;
    }

    /////////////////////////////////////////////////////////////////////////
    //  HELPER FUNCS
    /////////////////////////////////////////////////////////////////////////

    private String _getProvaIdFromPath(Path path) {
        String prova_id = path.getFileName().toString();
        return prova_id.substring(0, prova_id.lastIndexOf("."));
    }

    private Path _getQuestionFolderFromPath(Path path) {
        return path.getParent().resolve("." + this.prova_id);
    }

    /////////////////////////////////////////////////////////////////////////
    //  DEBUGGING
    /////////////////////////////////////////////////////////////////////////

    public void __debugQuestions() {
        System.out.println(" :: __debugQuestions :: \n");

        for (Question q : this.questoes) {

            System.out.println(" :: PROVA " + q.prova_id);
            System.out.println(" :: QUESTAO " + q.question_id);
            System.out.println(" :: ENUNCIADO " + q.enunciado);
            for (String alternativa : q.alternativas)
                System.out.println(" :: ALTERNATIVA " + alternativa);
            System.out.println(" :: RESPOSTA " + q.resposta);
            if (q.explicacao != null)
                for (String expl : q.explicacao)
                    System.out.println(" :: EXPLICACAO " + expl);

        }

        System.out.println("\n :: __debugQuestions :: \n");
    }

    public void __debugCombinationGeneration(Integer num_types, Integer num_questions) {
        System.out.println(" :: __debugCombinationGeneration :: ");
        
        List<List<Integer>> _combs = this.generateCombinations(num_types, num_questions);

        System.out.println(" :: COMBINATIONS :: ");
        
        for (List<Integer> _c : _combs) {
            System.out.println(_c);
        }

        System.out.println(" :: __debugCombinationGeneration :: ");
    }
}
