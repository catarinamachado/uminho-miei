lista: lista.fl lista.y
	   flex lista.fl
	   yacc lista.y
	   cc -o lista  y.tab.c -ly -lm

test: lista
	./lista

clean:
	rm -f y.tab.c lex.yy.c lista
