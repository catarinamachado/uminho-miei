%{
#include <stdio.h>
%}

%%
"("                 { return 3; }
")"                 { return 4; }
"{"                 { return 8; }
"}"                 { return 18; }
";"                 { return ';'; }
"="                 { return '='; }
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
