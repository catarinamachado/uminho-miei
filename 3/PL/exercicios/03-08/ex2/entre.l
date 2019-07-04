%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
char* eu = "";
char* ele = "";
%}

%%
^(?i:EU)=[^.]*"."	{ yytext[yyleng-1] = '\0'; eu = strdup(yytext + 3); }
^(?i:ELE)=[^.]*"."	{ yytext[yyleng-1] = '\0'; ele = strdup(yytext + 4); }
^(?i:EU)			{ printf("(%s)", eu); }
^(?i:ELE)			{ printf("(%s)", ele); }
%%

int yywrap(){
	return 1;
}

int main(){
	yylex();
	return 0;
}
