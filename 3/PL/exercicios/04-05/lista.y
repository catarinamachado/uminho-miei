%token LISTA FIM
%token pal num

%%

Lstm : LISTA Elems FIM
     ;

Elems : Elem
      | Elems ','Elem
      ;

Elem : pal
     | num
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
