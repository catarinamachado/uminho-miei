%{
#include <stdio.h>
int c = 0;
%}
%%
(j|J)aneiro		{ printf(" 1"); }
[fF][eE][vV]ereiro	{ printf(" 2"); }
(?i:março)		{ c++;printf(" 3"); }
%%

int yywrap(){
	return 1;
}

int main(){
	printf("filtragem vai começar\n");
	yylex();
	printf("filtragem terminada com %d vezes nr de março\n",c);
	return 0;
}
