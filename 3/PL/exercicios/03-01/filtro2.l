%{
#include <stdio.h>
int sum = 0;
float realSum = 0.0;
%}
%%
[-+]?[0-9]+		{ sum += atoi(yytext); }
[-+]?[0-9]+\.[0.9]+	{ realSum += atoi(yytext); }
.|\n			{;}
%%

int yywrap(){
        return 1;
}

int main(){
        yylex();
        printf("Somatório dos inteiros: %d\n", sum);
	printf("Somatório dos reais: %f\n", realSum);
        return 0;
}
