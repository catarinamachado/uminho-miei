%{
int contaA;
int contaN;
%}

%union { char * string; } /* e aqui temos que dizer qual é o tipo em C do que está entre <> na nossa variável (string neste caso) */
%token TURMA_INICIO
%token INICIO_LISTA FIM_LISTA
%token <string> ID  /* temos que dar um tipo a ID porque o queremos imprimir, mas nao podemos esquecer de dizer no lado do flex também */
%token <string> NOME
%token <string> NOTA

%%

turma: TURMA_INICIO ID Alunos { printf("Existem %d alunos\n", contaA); } /* aqui, no fim, já processamos todos os alunos */
      ;

Alunos: Aluno         { contaA = 1; }
       | Alunos Aluno  { contaA++; }
       ;

Aluno: ID NOME LNotas { printf("O aluno %s [%s] tem %d notas\n", $1, $2, contaN); }    /* $1 porque é o primeiro campo */
     | ID LNotas      { printf("O aluno %d tem %d notas\n", contaA + 1, contaN); }
     ;

LNotas: INICIO_LISTA Notas FIM_LISTA
      ;

Notas: NOTA             { contaN = 1; }
     | Notas ',' NOTA   { contaN++; }
     ;

%%

#include "lex.yy.c"
#include <stdio.h>
int yyerror(char *s){ fprintf(stderr,"Erro sintático: %s\n",s);}
int yylex();

int main(){
   yyparse();
   return 0;
}
