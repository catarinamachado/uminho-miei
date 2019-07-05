CFLAGS=-Wall -Wextra -pedantic -O2 -g
FICHEIROS=Doxyfile cgi.h estado.c estado.h game.c apresentacao.c apresentacao.h monstros.c monstros.h topScores.c topScores.h readUser.c readUser.h help.c help.h movimento.h movimento.c ataques.c ataques.h mapa.c mapa.h bot.c bot.h

install: game
	sudo cp game /usr/lib/cgi-bin
	sudo rm -rf /var/www/html/users
	sudo mkdir /var/www/html/users
	sudo rm -rf /var/www/html/log
	sudo mkdir /var/www/html/log
	sudo rm -rf /var/www/html/images
	sudo mkdir /var/www/html/images
	sudo cp topscores.txt /var/www/html/users/
	sudo cp ../images/* /var/www/html/images/.
	sudo chown -R www-data:www-data /var/www/html/
	touch install
	cd usernameFILES && $(MAKE)


game: game.o estado.o apresentacao.o topScores.o readUser.o help.o monstros.o powerup.o movimento.o ataques.o mapa.o bot.o
	cc -o game game.o estado.o apresentacao.o monstros.o powerup.o topScores.o help.o readUser.o movimento.o ataques.o mapa.o bot.o

game.zip: $(FICHEIROS)
	zip -9 game.zip $(FICHEIROS)

doc:
	doxygen Doxyfile

clean:
	rm -rf *.o game latex html install
	cd usernameFILES && make clean


estado.o: estado.c estado.h
game.o: game.c estado.h apresentacao.h topScores.h help.h readUser.h powerup.h ataques.h mapa.h
monstros.o: monstros.c monstros.h movimento.h ataques.h
apresentacao.o: apresentacao.c apresentacao.h cgi.h estado.h topScores.h help.h readUser.h powerup.h ataques.h
powerup.o: powerup.c powerup.h apresentacao.h cgi.h estado.h
movimento.o: movimento.c movimento.h
ataques.o: ataques.c ataques.h monstros.h apresentacao.h
mapa.o: mapa.c mapa.h
bot.o: bot.c bot.h
