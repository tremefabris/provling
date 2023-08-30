package br.ufscar.dc.compiladores.provling;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


public class ProvBuilder {

    /////////////////////////////////////////////////////////////////////////
    //  VARIABLES
    /////////////////////////////////////////////////////////////////////////

    private String main_folder;
    private boolean main_folder_set = false;

    private String prova_id;
    private boolean prova_id_set = false;

    private String prova_folder;
 
    private String prova_content;
    

    // header settings
    private String instituicao;
    private String disciplina;
    private List<String> docentes = null;
    private Map<String, String> diretrizes = null;

    // exam settings
    private Integer tipos_prova;
    // private boolean tipos_prova_set = false;

    private Integer questoes_por_prova;
    // private boolean questoes_por_prova_set = false;

    private Integer alunos_total;
    // private boolean alunos_total_set = false;



    /////////////////////////////////////////////////////////////////////////
    //  CONSTRUCTORS AND SETUP-FUNCS
    /////////////////////////////////////////////////////////////////////////

    public ProvBuilder() {
        this.prova_content = new String("");
    }
    public ProvBuilder(String main_folder, String prova_id) {
        this.main_folder = main_folder;
        this.prova_id = prova_id;

        this.prova_folder = main_folder + prova_id;

        this.prova_content = new String("");
    }


    public void withProvaId(String prova_id) {
        this.prova_id = prova_id;
        this.prova_id_set = true;
        this._setupProvaFolder();
    }
    public void withMainFolder(String main_folder) {
        this.main_folder = main_folder;
        this.main_folder_set = true;
        this._setupProvaFolder();
    }
    private void _setupProvaFolder() {  // TODO: Add folder path treatment
        if (this.main_folder_set && this.prova_id_set)
            this.prova_folder = main_folder + prova_id + ".csv";
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
    

    /////////////////////////////////////////////////////////////////////////
    //  TEXT-ADDING FUNCTIONS
    /////////////////////////////////////////////////////////////////////////

    private void _add(String _t) {
        this.prova_content += _t;
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

        String[] _trailing_text = {", \\docente", "} \\\\[8pt]"};
        for (Integer i = 0; i < this.docentes.size(); i++) {
            String _letra = String.valueOf((char)(65 + i));
            this._addr(_letra)
                ._add( _trailing_text[i]);
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
    // TODO: Follow the configurations established
    // TODO: by the user
    public void addQuestions() {
        QuestionManager qm = QuestionManager.FromCSV(this.prova_folder);

        this._addlr("\\begin{questions}"                                        )
            ._addlr("%=========================================================")
            ._addlr("%-------------------QUESTÕES DA PROVA"                     )
            ._addl( "%=========================================================");
        
        for (Integer i = 0; i < qm.totalQuestions(); i++) {

            this._addlr("\\question[1] " + qm.getEnunciadoFromQuestion(i))
                ._addl( "\\begin{choices}"                            );

            for (String alt : qm.getAlternativasFromQuestion(i)) {
                this._addl("\\choice " + alt);
            }

            this._addlr("\\end{choices}")
                ._endl();

        }

        this._addlr("\\end{questions}")
            ._endl();
    }
    public void addEnding() {
        this._addlr("%=========================================================")
            ._addlr("%-------------------FIM DA PROVA"                          )
            ._addlr("%=========================================================")
            ._addlr("\\end{document}"                                           )
            ._endl();
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
    
    // TODO: Add proper error handling
    // TODO: Add folder path treatment
    // TODO: Check whether path truly exists
    // TODO: Add argument for execution of this command
    public void generateTexFile() throws IOException {  

        System.out.println(" LOG :: Generating TeX file...");

        String file_path = this.prova_folder.substring(
            0,
            this.prova_folder.lastIndexOf("/") + 1
        ) + this.prova_id + ".tex";

        System.out.println(" LOG :: File path: " + file_path);

        File tex_file = new File(file_path);
        tex_file.createNewFile();
        PrintWriter pw = new PrintWriter(tex_file, "UTF-8");

        pw.print(this.prova_content);

        System.out.println(" LOG :: TeX file generated successfully");

        pw.close();
    }

    // TODO: Add argument for execution of this command
    public void generatePdfFromTex() {
    }

    /////////////////////////////////////////////////////////////////////////
    //  DEBUG FUNCTIONS
    /////////////////////////////////////////////////////////////////////////

    public void __debugProvaContent() {
        System.out.print(prova_content);
    }

    public String __debugGetProva() {
        return this.prova_content;
    }
}
