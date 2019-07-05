#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include "estado.h"

/**
@file estado.c
Funções que convertem estados em strings e vice-versa, e que ajudam a obter
informações relativas ao estado atual do jogo.
*/

/** \brief Tamanho máximo do buffer que contém o estado do jogo, */
#define MAX_BUFFER		10240


/**
\brief Procedimento que escreve num ficheiro a string que representa o estado atual do jogo.
@param string Estado atual do jogo.
@param user Nome do utilizador.
*/
void str2file(char string[], char user[]) {
	FILE * fP;
	char file[60] = "/var/www/html/users/";

	strcat(file, user);

	fP = fopen(file, "w");

	if (fP == NULL) {
		perror("null fp write str2file");
		exit(1);
	}

	fprintf(fP, "%s", string);

	fclose(fP);

	return;
}


/**
\brief Função que converte um estado numa string
@param e Estado do jogo.
@returns A string correspondente ao estado e.
*/
char *estado2str(ESTADO e) {
	static char buffer[MAX_BUFFER];
 	unsigned char *p = (unsigned char *) &e;
  char *b = buffer;
	unsigned int i;

  b[0] = 0;

	/*põe no array b o conteúdo de p em hexadecimal de forma a ter sempre dois
	char (%02x), i. e., ocupar sempre 2 bytes, daí incrementar o b de 2 em 2. */
	assert(sizeof(ESTADO) < MAX_BUFFER);
  for(i = 0; i < sizeof(ESTADO); i++, b += 2) {
    sprintf(b, "%02x", p[i]);
  }

	return buffer; /*retorna a posição inicial do buffer.*/
}


/**
\brief Função que converte uma string num estado.
@param argumentos Uma string contendo os argumentos passados à CGI.
@returns O estado correspondente à string dos argumentos.
*/
ESTADO str2estado(char *argumentos) {
	ESTADO e;
	unsigned char *p = (unsigned char *) &e;
	unsigned int i;

	for(i = 0; i < sizeof(ESTADO); i++, argumentos += 2) {
		unsigned int d;
		sscanf(argumentos, "%2x", &d);
		p[i] = (unsigned char) d;
	}

	return e;
}


/**
\brief Função que lê de um ficheiro a string que representa o estado atual do jogo.
@param user Nome do utilizador.
@returns O estado correspondente.
*/
ESTADO file2string(char user[]) {
	ESTADO e;
	FILE * fP;
	char linha[10240];
	char file[60] = "/var/www/html/users/";
	char * ret;

	strcat(file,user);

	fP = fopen(file,"r");

	if (fP == NULL) {
		perror("file2string null fp");
		exit(1);
	}

	ret = fgets(linha, 10240, fP);
	(void) ret;

	e = str2estado(linha);

	return e;
}

/**
\brief Função que verifica se a posição (x, y) se encontra dentro do tabuleiro.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns int 1 se a posição é válida (está dentro do tabuleiro), 0 caso contrário.
*/
int posicao_valida(int x, int y) {
	return x > 0 && y > 0 && x < TAM-1 && y < TAM-1;
}

/**
\brief Função que verifica se uma dada POSICAO, (a, b), é igual às coordenadas (x, y).
@param p Estrutura que armazena uma posição.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns int 1 se se tratar das mesmas coordenadas, 0 caso contrário.
*/
int posicao_igual(POSICAO p, int x, int y) {
	return p.x == x && p.y == y;
}

/**
\brief Função que verifica se duas posições são iguais.
@param p1 Estrutura que armazena uma posição.
@param p2 Estrutura que armazena uma posição.
@returns int 1 se se tratar das mesmas coordenadas, 0 caso contrário.
*/
int posicoes_iguais(POSICAO p1, POSICAO p2) {
	return p1.x == p2.x && p1.y == p2.y;
}

/**
\brief Verifica se uma determinada posição, (x, y), está ocupada por um jogador.
@param e Estado do jogo.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns int 1 se nessa posição se encontrar o jogador, 0 caso contrário.
*/
int tem_jogador(ESTADO e, int x, int y) {
	return posicao_igual(e.jog.pos, x, y);
}


/**
\brief Verifica se uma determinada posição, (x, y), está ocupada por um determinado inimigo.
@param e Estado do jogo.
@param tipo Indica qual o tipo de inimigo (esqueleto, ogre ou bruxa).
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns int 1 se nessa posição se encontrar um inimigo desse determinado tipo,
0 caso contrário.
*/
int tem_inimigo(ESTADO e, int tipo, int x, int y) {
	int i;
	  for (i = 0 ; i < e.num_inimigos[tipo] ; i++) {
		  if (posicao_igual(e.inimigo[tipo][i].pos, x, y))
			  return 1;
		}

	return 0;
}

/**
\brief Verifica se uma determinada posição, (x, y), está ocupada por algum inimigo.
@param e Estado do jogo.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns int 1 se nessa posição se encontrar algum dos inimigos (qualquer um),
0 caso contrário.
*/
int tem_inimigos(ESTADO e, int x, int y) {
	int i, tipo;
	for (tipo = 0; tipo < TIPO_INIMIGO; tipo++) {
	  for (i = 0; i < e.num_inimigos[tipo]; i++) {
		  if (posicao_igual(e.inimigo[tipo][i].pos, x, y))
			  return 1;
		}
	}
	return 0;
}

/**
\brief Verifica se uma determinada posição, (x, y), está ocupada pela bruxa número 0.
@param e Estado do jogo.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns int 1 se nessa posição se encontrar a bruxa número 0,
0 caso contrário.
*/
int tem_bruxa_especial(ESTADO e, int x, int y) {
	return (posicao_igual(e.inimigo[BRUXA][0].pos, x, y));
}


/**
\brief Verifica se uma determinada posição, (x, y), está ocupada por um obstáculo.
@param e Estado do jogo.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns int 1 se nessa posição se encontra um obstáculo, 0 caso contrário.
*/
int tem_obstaculo(ESTADO e, int x, int y) {
	int i;
	for (i = 0 ; i < e.num_obstaculos ; i++)
		if (posicao_igual(e.obstaculo[i], x, y) )
			return 1;
	return 0;
}

/**
\brief Verifica se uma determinada posição, (x, y), está ocupada pela
porta de passagem de nível.
@param e Estado do jogo.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns int 1 se nessa posição se encontra a porta de saída,
0 caso contrário.
*/
int tem_porta (ESTADO e, int x, int y) {
	return posicao_igual(e.porta, x, y);
}


/**
\brief Verifica se uma determinada posição, (x, y), está ocupada por algum power up.
@param e Estado do jogo.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns int 1 se essa posição está ocupada por um power up (qualquer),
0 caso contrário.
*/
int tem_powerups(ESTADO e, int x, int y){
	int i;

	for(i = 1 ; i < MAX_POWERUPS ; i++){
		if (posicao_igual(e.powerup[i][NO_MAPA].pos, x, y))
			return 1;
		}

	return 0;
}


/**
\brief Verifica se uma determinada posição, (x, y), está ocupada por algum power up
(sem contar com o power up vida).
@param e Estado do jogo.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns int 1 se essa posição está ocupada pelo power up escudo, gelo, move, raio,
bomba, tronco ou martelo, 0 caso contrário.
*/
int tem_powerups_excpt_vida(ESTADO e, int x, int y){
	int i;

	for(i = 2 ; i < MAX_POWERUPS ; i++){
		if (posicao_igual(e.powerup[i][NO_MAPA].pos, x, y))
			return 1;
		}

	return 0;
}


/**
\brief Verifica se uma determinada posição, (x, y), está ocupada por um deteriminado power up.
@param e Estado do jogo.
@param tipo_powerup Indica qual o tipo de power up.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns int 1 se esse power up estiver nessa posição, 0 caso contrário.
*/
int tem_powerup(ESTADO e, int tipo_powerup, int x, int y){
	return posicao_igual(e.powerup[tipo_powerup][NO_MAPA].pos, x, y);
}


/**
\brief Função que verifica se uma posição, (x, y), poderá estar ocupada por um inimigo,
obstáculo, jogador, porta de saída ou power up.
@param e Estado do jogo.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns int 1 se essa posição estiver ocupada por um dos elementos do jogo ou
se não for uma posição válida (está fora do mapa), 0 caso contrário.
*/
int posicao_ocupada(ESTADO e, int x, int y) {
	return tem_inimigos(e, x, y) || tem_obstaculo(e, x, y) || tem_jogador(e, x, y)
	     || tem_porta(e, x, y)	 || tem_powerups(e, x, y) ||
	      x <= 0 || y <= 0 || x >= TAM-1 || y >= TAM-1;
}


/**
\brief Função que verifica se uma posição, (x, y), poderá estar ocupada por um inimigo,
obstáculo, porta de saída ou power up.
@param e Estado do jogo.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns int 1 se essa posição estiver ocupada por um inimigo, obstáculo, porta,
power up ou se não é uma posição válida (está fora do mapa), 0 caso contrário.
*/
int pos_ocupada_excpt_jog(ESTADO e, int x, int y) {
	return tem_inimigos(e, x, y) || tem_obstaculo(e, x, y) || tem_porta(e, x, y)
 		  || tem_powerups(e, x, y) || x <= 0 || y <= 0 || x >= TAM-1 || y >= TAM-1;
}


/**
\brief Função que verifica se uma posição, (x, y), poderá estar ocupada por
um obstáculo ou pela porta de saída.
@param e Estado do jogo.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns int 1 se essa posição estiver ocupada por um obstáculo ou porta, ou se
não é uma posição válida (está fora do mapa), 0 caso contrário.
*/
int pos_impede_ataque(ESTADO e, int x, int y) {
	return tem_obstaculo(e, x, y) || tem_porta(e, x, y)
 		     || x <= 0 || y <= 0 || x >= TAM-1 || y >= TAM-1;
}
