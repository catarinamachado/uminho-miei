%{
#include <stdio.h>
%}

dig             [0-9]
fin             [\t\n,.;:?!]

%%
\#tb                    {printf("também");}
\#mb                    {printf("muito bem");}
\#agr                   {printf("agora");}
\#cqd                   {printf("como queria demonstrar");}
\#mB                    {printf("muito bom");}
[a-zA-Z]+\//{fin}       {yytext[yyleng-1]='\0'; printf("%smente",yytext);}
\~/[a-zA-Z]+            {printf("in%s", yytext + 1);}
%%

int yywrap(){
    return 1;
}

int main(){
    yylex();
    return 0;
}
