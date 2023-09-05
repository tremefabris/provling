package br.ufscar.dc.compiladores.provling;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.InvalidPathException;

/*
 * Exam Generator
 * 
 * Buffers information to construct the exam (in LaTeX) and stores the
 * resulting TeX file in the folder given by ProvLingSemantic during
 * construction.
 * 
 * This class communicates with QuestionManager to retrieve information
 * about questions already stored in memory - all the responsability of
 * reading CSV files and organizing them is deferred to QuestionManager.
 * 
 * Has many error checking and user-warnings:
 * - Errors out if an exam's data file, infered from its ID, doesn't exist;
 * - Warns user if they've asked for more questions per exam than possible;
 * - Warns user if they've asked for more types of exams than possible;
 * - Errors out if user didn't provide TIPOS or QUESTOES configurations.
 * 
 * The final exam has a fixed template that is incremented in the buffer
 * every time specific functions are called. The only truly dynamic things
 * to add are the institution, the teachers, the subject's name, the guidelines
 * and the questions.
 */


public class ProvBuilder {

    /////////////////////////////////////////////////////////////////////////
    //  VARIABLES
    /////////////////////////////////////////////////////////////////////////

    private Path dump_folder;

    private Path data_folder;
    private boolean data_folder_set = false;

    private String prova_id;
    private boolean prova_id_set = false;

    private Path prova_data_path;
 
    // header settings
    private String instituicao;
    private String disciplina;
    private List<String> docentes = null;
    private Map<String, String> diretrizes = null;

    // exam settings
    private Integer tipos_prova = null;
    private Integer questoes_por_prova = null;
    private Integer alunos_total = null;

    // text buffers
    private String prova_buffer;
    private List<String> conjuntos_questoes;
    private List<String> final_provas;

    private boolean built = false;


    /////////////////////////////////////////////////////////////////////////
    //  CONSTRUCTORS AND SETUP-FUNCS
    /////////////////////////////////////////////////////////////////////////


    public ProvBuilder() {
        this.prova_buffer = new String("");
    }
    // TODO: Adapt to data_folder (dump_folder) not same as prova_data_path's folders
    public ProvBuilder(Path data_folder, String prova_id) {
        this.data_folder = data_folder;
        this.prova_id = prova_id;

        this.prova_data_path = data_folder.resolve(prova_id + ".csv");

        this.prova_buffer = new String("");
    }


    public void withProvaId(String prova_id) {
        this.prova_id = prova_id;
        this.prova_id_set = true;
        this._setupDataPath();
    }
    public void withDataFolder(Path data_folder) {
        this.data_folder = data_folder;
        this.data_folder_set = true;
        this._setupDataPath();
    }
    public void withDumpFolder(Path dump_folder) {
        this.dump_folder = dump_folder;
    }
    // TODO: Handle InvalidPathException
    private void _setupDataPath() {
        if (this.data_folder_set && this.prova_id_set) {
            this.prova_data_path = data_folder.resolve(prova_id + ".csv");

            if (!this.prova_data_path.toFile().exists())
                throw new InvalidPathException(
                    "",
                    "prova " + this.prova_id + " não encontrada (o ID está certo?)"
                );
        }
    }

    public void withTypes(Integer num_types) {
        this.tipos_prova = num_types;
    }
    public void withQuestions(Integer num_questions) {
        this.questoes_por_prova = num_questions;
    }
    public void withParticipants(Integer num_participants) {
        this.alunos_total = num_participants;
    }

    private void _setupQuestionConfigs(QuestionManager qm) {

        if (this.questoes_por_prova == null) {
            throw new NullPointerException("config QUESTOES deve ser especificada");
        }
        else if (this.tipos_prova == null) {
            throw new NullPointerException("config TIPOS deve ser especificada");
        }
        if (this.alunos_total == null) {
            Logger.add(
                null,
                "config ALUNOS não usada",
                Logger.Type.WARNING
            );
        }

        Integer max_questions = qm.totalQuestions();
        Integer possible_types = qm.possibleCombinations(this.questoes_por_prova);

        if (possible_types == -1) {
            
            String _error_msg = "config pede " + this.questoes_por_prova +
                                " questões por prova, mas o máximo possivel é " +
                                max_questions;

            Logger.add(
                null,
                _error_msg,
                Logger.Type.WARNING
            );
            Logger.add(
                null,
                "usando " + max_questions + " questões",
                Logger.Type.WARNING
            );

            this.questoes_por_prova = max_questions;
            possible_types = 1;
        }
        if (this.tipos_prova > possible_types) {

            String _error_msg = "config pede " + this.tipos_prova +
                                " tipo(s) diferente(s) de prova, mas só é possível " +
                                "ter " + possible_types + " tipo(s)";

            Logger.add(
                null,
                _error_msg,
                Logger.Type.WARNING
            );
            Logger.add(
                null,
                "gerando " + possible_types + " tipo(s) de prova",
                Logger.Type.WARNING
            );

            this.tipos_prova = possible_types;
        }
    }
    private void _initConjuntoQuestoes() {
        this.conjuntos_questoes = new ArrayList<String>(this.tipos_prova);
        for (Integer i = 0; i < this.tipos_prova; i++)
            this.conjuntos_questoes.add(new String(""));
    }


    /////////////////////////////////////////////////////////////////////////
    //  TEXT-ADDING FUNCTIONS
    /////////////////////////////////////////////////////////////////////////

    private void _add(String _t) {
        this.prova_buffer += _t;
    }
    private ProvBuilder _addr(String _t) {
        this._add(_t);
        return this;
    }
    private void _addl(String _t) {
        this._add(_t + "\n");
    }
    private ProvBuilder _addlr(String _t) {
        this._addl(_t);
        return this;
    }
    private void _endl() {
        this._add("\n");
    }

    public void addTemplateInfo() {
        this._addlr("% TEMPLATE UTILIZADO POR"                             )
            ._addlr("% 		Vitor Fabris"                                 )
            ._addlr("%		Guilherme Theodoro"                           )
            ._addlr("% Para o trabalho final de Construção de Compiladores")
            ._addlr("%"                                                    )
            ._addlr("% Esse template foi originalmente criado por"         )
            ._addlr("% 		Hirschorn, Philip"                            )
            ._addlr("% e adaptado por"                                     )
            ._addlr("%		Milena Lima"                                  )
            ._addlr("%		Fernanda D. Gomes"                            )
            ._addlr("%		Sara R. Pacheco"                              )
            ._addlr("%"                                                    )
            ._addlr("% O template pode ser acessado em"                    )
            ._addlr("% https://www.overleaf.com/read/bmbsmqkxwndn"         )
            ._addlr("%"                                                    )
            ._addlr("% Agradecemos imensamente o trabalho de todas as"     )
            ._addlr("% pessoas mencionadas e pelo template incrível"       )
            ._endl();
    }
    public void addDocumentClass() {
        this._addl("\\documentclass[12pt]{exam}");
    }
    public void addPackages() {
        this._addlr("\\usepackage[T1]{fontenc}"                                      )
            ._addlr("\\usepackage[utf8]{inputenc}"                                   )
            ._addlr("\\usepackage[brazil]{babel}"                                    )
            ._addlr("\\usepackage[top=2cm,left=1cm,right=1.5cm,bottom=2cm]{geometry}")
            ._addlr("\\usepackage{amsmath,amssymb}"                                  )
            ._addlr("\\usepackage{multicol}"                                         )
            ._addlr("\\usepackage{multirow}"                                         )
            ._addlr("\\usepackage{array}"                                            )
            ._addlr("\\usepackage{ragged2e}"                                         )
            ._addlr("\\usepackage{graphicx}"                                         )
            ._addlr("\\usepackage{tcolorbox}"                                        )
            ._addlr("\\usepackage{pdflscape}"                                        )
            ._addlr("\\usepackage{epstopdf}"                                         )
            ._addlr("\\usepackage{booktabs}"                                         )
            ._addlr("\\usepackage{pdfpages}"                                         )
            ._addlr("\\usepackage[sort&compress,square,comma, authoryear]{natbib}"   )
            ._addr( "\\usepackage[colorlinks=true,urlcolor=magenta,citecolor=red,"   )
            ._addlr("linkcolor=blue,bookmarks=true]{hyperref}"                       )
            ._addlr("\\usepackage{enumerate}"                                        )
            ._addlr("\\usepackage{enumitem}"                                         )
            ._endl();
    }
    public void addItemDefinitions() {
        this._addlr("%---Definição de Itens e Subitens---"                      )
            ._addlr("\\newlist{partes}{enumerate}{3}"                           )
            ._addlr("\\setlist[partes]{label=(\\alph*)}"                        )
            ._addlr("\\newcommand{\\parte}{\\item}"                             )
            ._addlr("\\newlist{subpartes}{enumerate}{3}"                        )
            ._addlr("\\setlist[subpartes]{label=\\roman*)}"                     )
            ._addlr("\\newcommand{\\subparte}{\\item}"                          )
            ._addlr("\\setlist[enumerate,1]{%("                                 )
            ._addlr("leftmargin=*, itemsep=12pt, label={\\textbf{\\arabic*.)}}}")
            ._addlr("\\newcommand{\\dis}{\\displaystyle}"                       )
            ._addlr("\\renewcommand*\\half{.5}"                                 )
            ._addlr("\\newcommand\\answerbox{%%"                                )
            ._addlr("\\fbox{\\rule{2in}{0pt}\\rule[-0.1ex]{0pt}{4ex}}}"         )
            ._addlr("\\pointpoints{ponto}{pontos}"                              )
            ._addlr("\\hqword{\\textcolor{blue}{Questão:}}"                     )
            ._addlr("\\hpword{Valor:}"                                          )
            ._addlr("\\hsword{Pontuação:}"                                      )
            ._addlr("\\htword{\\textcolor{blue}{Total}}"                        )
            ._endl();
    }
    public void addHeaderDefinitions() {
        String _instituicao_str = "\\newcommand{\\instituicao}{" + this.instituicao + "}";
        String _disciplina_str = "\\newcommand{\\disciplina}{" + this.disciplina + "}";

        this._addlr("%=========================================================")
            ._addlr("%------------DEFINIÇÕES DE HEADER"                         )
            ._addlr("%=========================================================")
            ._addlr(_instituicao_str)
            ._addl( _disciplina_str);

        for (Integer i = 0; i < this.docentes.size(); i++) {
            String _docente_str = "\\newcommand{\\docente" +
                                  (char)(65 + i) +
                                  "}{" + this.docentes.get(i) + "}";
            
            this._addl(_docente_str);
        }

        this._addlr("\\newcommand{\\prova}{Prova de \\disciplina}"              )
            ._addlr("\\newcommand{\\aluno}{\\bf Aluno:}"                        )
            ._endl();
    }
    public void addHeaders() {
        this._addlr("%========================================================="                        )
            ._addlr("%------------CABEÇALHOS"                                                           )
            ._addlr("%========================================================="                        )
            ._addlr("\\pagestyle{headandfoot}"                                                          )
            ._addlr("\\firstpageheader{}{}{}"                                                           )
            ._addlr("\\runningheader{\\prova}{}{}"                                                      )
            ._addlr("\\runningheadrule"                                                                 )
            ._addlr("\\firstpagefooter{}{}{}"                                                           )
            ._addlr("\\runningfooter{}{Boa Prova!}{Pag. \\thepage\\ de \\numpages}"                     )
            ._addlr("\\runningfootrule"                                                                 )
            ._addlr("%========================================================"                         )
            ._addlr("\\begin{document}"                                                                 )
            ._addlr("\\fontsize{14}{14}\\selectfont"                                                    )
            ._addlr("\\begin{tabular*}{\\textwidth}{l @{\\extracolsep{\\fill}}l @{\\extracolsep{6pt}}l}")
            ._addlr("%--------------------Instituição"                                                  )
            ._addlr("\\multicolumn{3}{c}{\\textbf{\\instituicao}} \\\\[8pt]"                            )
            ._addlr("%--------------------Disciplina"                                                   )
            ._addlr("\\multicolumn{3}{c}{\\textbf{\\disciplina}} \\\\[8pt]"                             )
            ._addlr("%-------------------Disciplina e Professor"                                        )
            ._add("\\textbf{Docente(s): \\docente"                                                      );

        for (Integer i = 0; i < this.docentes.size(); i++) {
            String _letra = String.valueOf((char)(65 + i));
            
            this._add(_letra);
            if (i != this.docentes.size() - 1)
                this._add(", \\docente");                
            else
                this._add("} \\\\[8pt]");
        }
        this._endl();

        this._addlr("%-------------------"                                            )
            ._addlr("\\multicolumn{3}{l}{\\Large{\\aluno \\textbf{\\hrulefill}}} \\\\")
            ._addlr("\\end{tabular*}"                                                 )
            ._addlr("%--------------------LINHA CENTRAL"                              )
            ._addlr("\\begin{center}"                                                 )
            ._addlr("\\rule[1ex]{\\textwidth}{1pt}"                                   )
            ._addlr("{\\Large{\\prova}} \\\\[6pt]"                                    )
            ._addlr("Data:\\hspace{1cm}/\\hspace{1cm}/\\hspace{1cm} \\\\[6pt]"        )
            ._addlr("\\rule[2ex]{\\textwidth}{1pt}"                                   )
            ._addlr("\\end{center}"                                                   )
            ._endl();
    }
    // TODO: Permit case where no guideline is provided
    public void addGuidelines() {
        this._addlr("%========================================================="              )
            ._addlr("%-------------------DIRETRIZES"                                          )
            ._addlr("%========================================================="              )
            ._addlr("\\noindent"                                                              )
            ._addlr("Esta prova contém \\numpages\\ página(s), incluindo esta capa, e "       )
            ._addlr("\\numquestions\\ questões, formando um um total de \\numpoints\\ pontos.")
            ._addlr("A aplicação dessa prova será regida pelas seguintes diretrizes, "        )
            ._addlr("definidas pelo(s) docente(s):"                                           )
            ._addlr("\\vspace{0.5cm}"                                                         )
            ._addlr("\\noindent"                                                              )
            ._addlr("\\begin{tcolorbox}[colframe=black, colback=white]"                       )
            ._addl( "\\begin{itemize}"                                                        );

        for (Map.Entry<String, String> dir : this.diretrizes.entrySet()) {
            String _diretriz_str = "\\item [" + dir.getKey() + "] " +
                                                dir.getValue();
            
            this._addl(_diretriz_str);
        }

        this._addlr("\\end{itemize}"                                                          )
            ._addlr("\\end{tcolorbox}"                                                        )
            ._endl();
    }
    public void addGradeTable() {
        this._addlr("%=========================================================")
            ._addlr("%-------------------TABELA DE PONTUAÇÃO"                   )
            ._addlr("%=========================================================")
            ._addlr("\\begin{center}"                                           )
            ._addlr("\\textbf{Tabela (para USO EXCLUSIVO do professor)}\\\\"    )
            ._addlr("\\addpoints"                                               )
            ._addlr("\\gradetable[h][questions]"                                )
            ._addlr("\\end{center}"                                             )
            ._addlr("\\noindent"                                                )
            ._addlr("\\rule[1ex]{\\textwidth}{1pt}"                             )
            ._endl();
    }
    public void addQuestions() {
        QuestionManager qm = QuestionManager.FromCSV(this.prova_data_path);

        this._setupQuestionConfigs(qm);
        this._initConjuntoQuestoes();
        List<List<Integer>> q_combs = qm.generateCombinations(
            this.tipos_prova,
            this.questoes_por_prova
        );

        this._addlr("\\begin{questions}"                                        )
            ._addlr("%=========================================================")
            ._addlr("%-------------------QUESTÕES DA PROVA"                     )
            ._addl( "%=========================================================");

        for (Integer i = 0; i < this.tipos_prova; i++) {
            String q_buffer = new String("");

            for (Integer q_num : q_combs.get(i)) {
                q_buffer += "\\question[1] " + qm.getEnunciadoFromQuestion(q_num) + "\n";
                q_buffer += "\\begin{choices}" + "\n";

                for (String alt : qm.getAlternativasFromQuestion(q_num)) {
                    q_buffer += "\\choice " + alt + "\n";
                }

                q_buffer += "\\end{choices}" + "\n\n";
            }
            q_buffer += "\\end{questions}" + "\n\n";
            
            this.conjuntos_questoes.set(i, q_buffer);
        }
    }
    private String _generateEnding() {
        return "%=========================================================" + "\n" +
               "%-------------------FIM DA PROVA"                           + "\n" +
               "%=========================================================" + "\n" +
               "\\end{document}"                                            + "\n";                                           
    }

    /////////////////////////////////////////////////////////////////////////
    //  BUILDING, RETURNING FUNCS
    /////////////////////////////////////////////////////////////////////////

    public void defineInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }
    public void defineDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }
    public void defineDocente(String docente) {
        if (this.docentes == null) {
            this.docentes = new ArrayList<String>();
        }
        this.docentes.add(docente);
    }
    public void defineDiretriz(String num, String diretriz) {
        if (this.diretrizes == null) {
            this.diretrizes = new LinkedHashMap<String, String>();
        }
        this.diretrizes.put(num, diretriz);
    }
    
    public void build() {
        Logger.add(
            null,
            "buildando prova...",
            Logger.Type.INFO
        );

        this.final_provas = new ArrayList<String>(this.tipos_prova);
        for (String questoes : this.conjuntos_questoes) {
            this.final_provas.add(
                new String(
                    this.prova_buffer + 
                    questoes +
                    this._generateEnding()
                )
            );
        }

        Logger.add(
            null,
            "prova buildada com sucesso!",
            Logger.Type.INFO
        );

        this.built = true;
    }

    // TODO: Handle IOException
    // TODO: Defer dumping responsability to upper level
    public void generateTexFile() throws IOException {  

        if (!this.built)
            this.build();

        Logger.add(
            null,
            "gerando arquivo TeX...",
            Logger.Type.INFO
        );

        for (Integer i = 0; i < this.final_provas.size(); i++) {

            Path file_path = this.dump_folder.resolve(
                this.prova_id + "_tipo" + (i + 1) + ".tex"
            );
            
            Logger.add(
                null,
                "caminho do arquivo: " + file_path,
                Logger.Type.INFO
            );
        
            File tex_file = file_path.toFile();
            tex_file.createNewFile();
            PrintWriter pw = new PrintWriter(tex_file, "UTF-8");

            pw.print(this.final_provas.get(i));
            pw.close();

            Logger.add(
                null,
                "arquivo TeX gerado com sucesso!",
                Logger.Type.INFO
            );
        }
    }

    // TODO: Implement
    // TODO: Add argument for execution of this command
    public void generatePdfFromTex() {}

    /////////////////////////////////////////////////////////////////////////
    //  DEBUG FUNCTIONS
    /////////////////////////////////////////////////////////////////////////

    public void __debugProvaContent() {
        System.out.print(prova_buffer);
    }

    public String __debugGetProva() {
        return this.prova_buffer;
    }

    public void __debugConjuntoQuestoes() {
        System.out.println(" :: __debugConjuntoQuestoes :: ");

        for (String c_q : this.conjuntos_questoes)
            System.out.println(c_q);

        System.out.println(" :: __debugConjuntoQuestoes :: ");
    }

    public void __debugBuild() {
        System.out.println(" :: __debugBuild :: ");

        this.build();

        for (String prova : final_provas)
            System.out.println(prova + "\n");

        System.out.println(" :: __debugBuild :: ");
    }
}
