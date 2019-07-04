%{
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#define _GNU_SOURCE //asprintf
#define UNDECLARED -1

int TABID[26];      //26 letras
int varcount = 0;
char address = 0;

int lookup(char var){
     return TABID[var - 'a'];
}

void declare(char var){
     TABID[var - 'a'] = varcount++;
}

void initVarTable(){
     for(int i = 0; i < 26; i++){
          TABID[i] = UNDECLARED;
     }
}

int yyerror(char *s);

%}

%union { int CONSTINT; char VARNAME; char* string; }
/* sections */
%token DECLARATION_SECTION
%token CODE_SECTION
%token TERMINATE_PROGRAM
/* type */
%token INTEGER
/* functions */
%token PRINT_FUNCTION
%token READ_FUNCTION
/* operands */
%token <CONSTINT> NUM
%token <VARNAME> VAR
%type <string> Dec Decs Program Insts Inst Atrib Write Read Fator Termo Exp
%%
Program: DECLARATION_SECTION Decs CODE_SECTION Insts TERMINATE_PROGRAM {
          printf("%sSTART\n%sSTOP\n", $2, $4);
         }
       ;

Decs: Dec           { $$ = strdup($1); }
    | Decs ';' Dec  {
                         asprintf(&$$, "%s%s", $1, $3);
                    }
    ;

Dec: VAR ':' Type   { declare($1); asprintf(&$$, "PUSHI 0\n"); }
   ;

Type: INTEGER
    ;

Insts: Inst              { $$ = $1; }
     | Insts ';' Inst    { asprintf(&$$, "%s%s", $1, $3); }
     ;

Inst: Atrib         { $$ = $1; }
    | Write         { $$ = $1; }
    | Read          { $$ = $1; }
    ;

Atrib: VAR '=' Exp  { if((address = lookup($1)) != UNDECLARED)
                         asprintf(&$$, "%sSTOREG %d\n", $3, address);
                      else
                         yyerror("Variável não declarada");
                    }
     ;

Write: PRINT_FUNCTION '(' Exp ')'  {
                    asprintf(&$$, "%sWRITEI\n", $3);
               }
     ;

Read: READ_FUNCTION '(' VAR ')'   {
                    if((address = lookup($3)) != UNDECLARED)
                         asprintf(&$$, "READ\nATOI\nSTOREG %d\n", address);
                    else
                         yyerror("Variável não declarada");
                    }
    ;

Exp: Termo          {$$ = $1;}
   | Exp '+' Termo  {asprintf(&$$, "%s%sADD\n", $1, $3);}
   | Exp '-' Termo  {asprintf(&$$, "%s%sSUB\n", $1, $3);}
   ;

Termo: Fator             { $$ = $1; }
     | Termo '*' Fator { asprintf(&$$, "%s%sMUL\n", $1, $3); }
     | Termo '/' Fator { asprintf(&$$, "%s%sDIV\n", $1, $3); }
     ;

Fator: NUM   { asprintf(&$$, "PUSHI %d\n", $1); }
     | VAR   {
             if((address = lookup($1)) != UNDECLARED)
               asprintf(&$$, "PUSHG %d\n", address);
             else
               yyerror("Variável não declarada");
             }
     | '(' Exp ')'
     ;

%%

#include "lex.yy.c"
#include <stdio.h>

int yyerror(char *s){ fprintf(stderr,"Erro sintático: %s\n",s); return 2;}
int yylex();

int main(){

     initVarTable();

     yyparse();
     return 0;
}
