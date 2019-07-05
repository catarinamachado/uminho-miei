CFLAGS=-Wall -Wextra -pedantic -O2 -g
FICHEIROS=cgi.h user.c Makefile

install: user
	sudo cp user /usr/lib/cgi-bin
	touch install

user: user.o
	cc -o user user.o

user.zip: $(FICHEIROS)
	zip -9 user.zip $(FICHEIROS)

doc:
	doxygen -g
	doxygen

clean:
	rm -rf *.o user Doxyfile latex html install

user.o: user.c
