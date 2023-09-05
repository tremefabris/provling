# ProvLing

Uma linguagem de programação para facilmente criar provas bonitas em LaTeX.

### Motivação

Desenvolvida para o Trabalho Final da disciplina de Construção de Compiladores, semestre 2023/1, ministrada
pelo professor [Daniel Lucrédio](https://www2.dc.ufscar.br/~daniel/).

### Grupo

Esta linguagem foi desenvolvida por:
- [Guilherme Theodoro](https://github.com/Marx-Coxinha), RA: 726531
- [Vitor Lopes Fabris](https://github.com/tremefabris), RA: 769822

## Vídeo demonstrativo

Para compreender rapidamente como ProvLing funciona, basta acessar o link abaixo e assistir o vídeo demonstrativo (hospedado no YouTube)!

[Vídeo demonstrativo](https://youtu.be/sMcWYLiWPuQ)

## Exemplos de uso

Caso você queira testar a geração de provas do ProvLing, basta seguir as instruções abaixo com os arquivos presentes na pasta `examples/`. Lá, existem duas provas criadas especificamente para se testar ProvLing, dividas em dois arquivos: um de questões e um de geração.

Teste! Mude os arquivos; veja como ProvLing reaje! 

Qualquer problema visto pode ser reportado para nós como uma *issue* aqui mesmo no GitHub.

## Descrição

### O que é ProvLing?

ProvLing é uma linguagem de programação para a criação fácil e rápida de provas bonitas em formato TeX. Especificando um ID de prova e várias questões com enunciados, alternativas e uma resposta, ProvLing as armazena em formato CSV. Depois, especificando informações de cabeçalho como instituição, docentes e diretrizes da aplicação da prova, e informações de configuração como a quantidade de questões por prova e quantos tipos diferentes de prova se quer, ele automaticamente gera a prova em formato TeX!

### Como utilizar?

Para utilizar, é bem simples!

Primeiro, é necessário clonar este repositório na sua máquina local:
```bash
git clone https://github.com/tremefabris/provling.git
```

Depois, ao acessar a pasta do repositório, é necessário compilar o projeto Maven:
```bash
mvn clean install package
```

Uma pasta chamada `target` será criada. Dentro dela, existe o arquivo que se deve executar, `provling-1.0-SNAPSHOT-jar-with-dependencies.jar`. Então, para iniciar o armazenamento de questões, execute:
```bash
java -jar target/provling-1.0-SNAPSHOT-jar-with-dependencies.jar /arquivo/de/questoes.txt
```

Para gerar a prova com suas questões, basta executar de novo, mas com o arquivo de geração:
```bash
java -jar target/provling-1.0-SNAPSHOT-jar-with-dependencies.jar /arquivo/de/geracao.txt
```

E pronto! No mesmo diretório do `/arquivo/de/geração.txt` estará seu(s) arquivo(s) TeX!

#### Compilação LaTeX para PDF

ProvLing não compila automaticamente os arquivos TeX para PDF, ficando sob responsabilidade do usuário fazer isso. Contudo, isso não é difícil: basta baixar o `pdflatex` na sua máquina e executá-lo.

Testamos com a versão 3.14159265-2.6-1.40.18 e funcionou perfeitamente!

### Mas... como usar a linguagem?

Também é extremamente simples! Vejamos os dois possíveis casos.

#### Armazenamento de Questões

Todos os arquivos de questões possuem o seguinte formato:

```
// Comentários são livres rs

PROVA: id_da_prova_aqui      // ID da prova que se está criando

QUESTAO: id_questao_1        // ID da questão 1

ENUNCIADO:
    "Aqui você coloca o enunciado da sua questão (não esqueça as aspas!).
    Ah, e pode pular linha a vontade rs"

ALTERNATIVAS:
    A: "Cada alternativa tem uma letra"
    B: "E todas devem ter uma frase entre aspas, como essa!"
    C: "Mas apenas uma alternativa é a correta"

RESPOSTA:
    C

FIM QUESTAO: id_questao_1    // finalização da questão

// ...
```

É possível colocar quantas questões se queira neste arquivo (embora INT_MAX provavelmente é o limite técnico...)!

#### Geração da Prova

Os arquivos de geração de provas possuem o seguinte formato:

```
PROVA: id_da_prova_aqui      // é preciso saber qual prova

INSTITUICAO:
    "Nome da instituição aplicadora da prova"

DOCENTES:
    "Quem vai aplicar a prova?"
    "Pode ter mais de um!"

DIRETRIZES:
    1: "Você pode colocar diretrizes na sua prova"
    2: "Como duração, o que acontece se for pego colando, etc..."
    3: "Elas devem ser precedidas por um número, por sinal"
    3.1: "Podem ser número quebrados"
    4: "Ou não"

CONFIG:
    QUESTOES: 5              // quantas questões cada prova terá?
    TIPOS: 3                 // quantos tipos de questão você quer?
    ALUNOS: 15               // reservado pra versões futuras :\
```

OBS.: Perceba que, por causa das configurações TIPOS e QUESTOES, provas de tipos diferentes terão questões diferentes, selecionadas (pseudo-)aleatoriamente!

#### Suporte a LaTeX

Por causa da maneira que as entranhas do ProvLing são estruturadas, é possível utilizar símbolos LaTeX com ProvLing (vide `examples/prova_calculo/`). A única diferença é que, onde se usaria apenas um `\`, em ProvLing é preciso usar `\\`. Por exemplo:
```latex
$ \\lim_{x \\to \\infty} \\frac {\\sin x} {x} $
```
gera o código LaTeX
```latex
$ \lim_{x \to \infty} \frac {\sin x} {x} $
```