%{
#include <stdio.h>
#define INT 256
#define CHAR 257
#define IF 258
#define WHILE 259
#define ID 260
#define STR 261
#define NUM 262
%}

%%
[()\[\]{}=;,!+\-*x]     { return yytext[0]; }
"int"                   { return INT; }
"char"                  { return CHAR; }
"if"                    { return IF; }
"while"                 { return WHILE; }
[a-zA-Z][a-zA-Z0-9_]*   { return ID; }
\"[^"]*\"               { return STR;}
[0-9]+                  { return NUM; }
.|\n                    {;}
%%


int yywrap(){
    return 1;
}

int main(){
    int s;
    while(s = yylex()){
        printf("%d ", s);
    }

    return 0;
}
