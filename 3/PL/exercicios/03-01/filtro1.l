%{
#include <stdio.h>
%}
dig [0-9]
%%
(\+351\-)?2{dig}{8}	   	   {printf("<LI>%s\n",yytext);}
9[1236]{dig}{7}	           {printf("<LI>%s\n",yytext);}
\+351\-9[12346]{dig}{7}    {printf("<LI> pt %s\n",yytext+5);}
.|\n			   {;}
%%

int yywrap(){
	return 1;
}

int main(){
	printf("<UL>\n");
	yylex();
	printf("</UL>\n");
	return 0;
}
