turma: turma.fl turma.y
	   flex turma.fl
	   yacc -v turma.y
	   #o -v é importante para gerar o automato
	   gcc -o turma  y.tab.c -ly -lm

test: turma
	  ./turma

clean:
	  rm -f y.tab.c lex.yy.c turma
