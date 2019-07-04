%{
#include <stdio.h>
int sum = 0;
%}
%x ACORDA
dig [0-9]
%%
(?i:agora|\!)           { BEGIN ACORDA; }
<ACORDA>\!              { sum = 0; BEGIN INITIAL; }
<ACORDA>[\-+]?{dig}+    { sum += atoi(yytext); }
<ACORDA>"="             { printf("%d\n", sum); sum = 0; }
<*>.|\n       		{ ; }
%%

int yywrap(){
        return 1;
}

int main(){
        yylex();
        return 0;
}
