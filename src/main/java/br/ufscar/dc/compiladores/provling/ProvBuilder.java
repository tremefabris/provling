package br.ufscar.dc.compiladores.provling;

import java.util.List;
import java.util.Map;

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
    private List<String> professores;
    private Map<Integer, String> diretrizes;

    // exam settings
    private Integer tipos_prova;
    private Integer questoes_por_prova;
    private Integer alunos_total;


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


    public ProvBuilder withProvaId(String prova_id) {
        this.prova_id = prova_id;
        this.prova_id_set = true;
        this._setupProvaFolder();
        return this;
    }
    public ProvBuilder withMainFolder(String main_folder) {
        this.main_folder = main_folder;
        this.main_folder_set = true;
        this._setupProvaFolder();
        return this;
    }
    private void _setupProvaFolder() {  // TODO: Add folder path treatment
        if (this.main_folder_set && this.prova_id_set)
            this.prova_folder = main_folder + prova_id;
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
    public void addInstituicao() {
        this._addr("\\newcommand{\\instituicao}{")
            ._addr(this.instituicao)
            ._addr("}")
            ._endl();
    }


    /////////////////////////////////////////////////////////////////////////
    //  BUILDING, RETURNING FUNCS
    /////////////////////////////////////////////////////////////////////////

    public void defineInstituicao(String instituicao) {
        this.instituicao = instituicao;
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
