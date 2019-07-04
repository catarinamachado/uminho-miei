EXEC := alexico

CXX := gcc
F= -std=c11 -g -Ofast

all:
	@flex alexico.l
	$(CXX) -o $(EXEC) lex.yy.c $(FLAGS)

clean:
	rm -rf lex.yy.c $(EXEC)

