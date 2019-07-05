#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>

#include "mapa.h"
#include "estado.h"
#include "cgi.h"
#include "apresentacao.h"
#include "monstros.h"
#include "readUser.h"
#include "ataques.h"
#include "powerup.h"
#include "movimento.h"
#include "bot.h"

/**
@file game.c
Ficheiro Principal para Gerar o Jogo
*/

/**
\brief macro que habilita ou desabilita um tipo de mapa.
*/
#define MAPA_CAVERNAS 1

/**
\brief Coloca um inimigo numa posição aleatória do mapa que esteja livre.
@param e Estado do jogo.
@param tipo Identifica o tipo de inimigo.
@returns Estado atualizado do jogo.
*/
ESTADO inicializar_inimigo(ESTADO e, int tipo) {
	int x, y;
  do {
		x = rand() % TAM;
		y = rand() % TAM;
	} while (posicao_ocupada(e, x, y) || (x <= 5 && y <= 5) );

	e.inimigo[tipo][(int)e.num_inimigos[tipo]].pos.x = x;
 	e.inimigo[tipo][(int)e.num_inimigos[tipo]].pos.y = y;
	e.num_inimigos[tipo] += 1;

	return e;
}


/**
\brief Função que coloca todos os inimigos numa posição do mapa.
@param e Estado do jogo.
@param num_max Número máximo de inimigos.
returns O estado do jogo atualizado.
*/
ESTADO inicializar_inimigos (ESTADO e, int num_max) {
	int num_inimigo;
	for ( num_inimigo= 0 ; num_inimigo < num_max ; num_inimigo++)
		e = inicializar_inimigo(e, num_inimigo % TIPO_INIMIGO);
	return e;
}


/**
\brief Coloca um obstáculo numa posição aleatória do mapa que esteja livre..
@param e Estado do jogo.
@returns Estado atualizado do jogo.
*/
ESTADO inicializar_obstaculo(ESTADO e) {
	int x, y;
  do {
		x = rand() % TAM;
		y = rand() % TAM;
} while (posicao_ocupada(e, x, y) || (x <= 2 && y <= 2) );

	e.obstaculo[(int)e.num_obstaculos].x = x;
	e.obstaculo[(int)e.num_obstaculos].y = y;

  e.num_obstaculos++;
	return e;
}


/**
\brief Coloca todos os obstáculos no mapa.
@param e Estado do jogo.
@param num_max Número máximo de obstáculos.
@returns Estado atualizado do jogo.
*/
ESTADO inicializar_obstaculos (ESTADO e, int num_max) {
	int obstaculo;
	for (obstaculo = 0 ; obstaculo < num_max ; obstaculo++)
		e = inicializar_obstaculo(e);
	return e;
}


/**
\brief Coloca a porta de passagem de nível numa posição que esteja livre e onde
o jogador tenha acesso.
@param e Estado do jogo.
@returns Estado atualizado do jogo.
*/
ESTADO inicializar_porta (ESTADO e) {
	int x, y;
  do {
		x = rand() % TAM;
		y = rand() % TAM;
	} while (posicao_ocupada(e, x, y) || !tem_caminho(x, y));

	e.porta.x = x;
	e.porta.y = y;

	return e;
}

/**
\brief Coloca um power up numa posição do mapa aleatória e que esteja livre.
@param e Estado do jogo.
@param tipo_powerup Identifica o tipo de power up a ser colocado no mapa.
@returns Estado atualizado do jogo.
*/
ESTADO inicializar_powerup(ESTADO e, int tipo_powerup) {
	int x, y;
	do {
		x = rand() % TAM;
		y = rand() % TAM;
	} while (posicao_ocupada(e, x, y) || (x <= 3 && y <= 3) );

	e.powerup[tipo_powerup][NO_MAPA].pos.x = x;
	e.powerup[tipo_powerup][NO_MAPA].pos.y = y;

	return e;
}

/**
\brief Gerador para as duas cores que compõem o fundo do tabuleiro do jogo.
@param e Estado do jogo.
@returns Estado atualizado do jogo.
*/
ESTADO inicializar_cor(ESTADO e){
	int r[2], g[2], b[2];
	int num0, num1, pos, posf;

	num0 = rand() % 256;
	num1 = (num0 >200)? (num0 - 50) : (num0 + 50);

	do{
	  pos = rand() % 3;
	  posf = rand() % 3;
  } while (pos == posf);

	if (pos == 0){
		r[0] = num0;
		r[1] = num1;
		g[0] = g[1] = b[0] = b[1] = 0x00;
	} else if(pos == 1){
		r[0] = r[1] = b[0] = b[1] = 0x00;
		g[0] = num0;
		g[1] = num1;
	} else {
		r[0] = r[1] = g[0] = g[1] = 0x00;
		b[0] = num0;
		b[1] = num1;
	}

	if (posf == 0){
		r[0] = 0xf0;
	} else if(posf == 1){
		g[0] = 0xf0;
	} else b[0] = 0xf0;

  sprintf(e.cor[0], "#%02x%02x%02x", r[0], g[0], b[0]);
	sprintf(e.cor[1], "#%02x%02x%02x", r[1], g[1], b[1]);

return e;
}

/**
\brief Criação de caraterísticas e entidades que compõem o jogo (inimigos,
obstáculos, porta de passagem de nível, power ups, recursos (granadas e missil) e cor).
@returns Estado atualizado.
*/
ESTADO inicializar() {
	ESTADO e;
	memset(&e, 0, sizeof(ESTADO));

	e.jog.pos.x = 1;
	e.jog.pos.y = 1;
	e.num_jogadas = 0;
	e.armas.granadas = 2;
	e.armas.missil = 1;

	ler_user(e.user);

#if MAPA_CAVERNAS
	mapa(&e);
#else
	e = inicializar_obstaculos(e, 20);
	determinar_caminhos(&e, e.jog.pos.x, e.jog.pos.y);
	e = inicializar_porta(e);
#endif

	e = inicializar_inimigos(e, 10);
	e = inicializar_cor(e);

	return e;
}

/**
\brief Função que transforma uma string para o tipo struct ESTADO, definido
em estado.h.
@returns Estado atualizado.
*/
ESTADO ler_estado() {
	char user[32];
	char file[60] = "/var/www/html/users/";

	ler_user(user);
	strcat(file, user);

	if(fopen(file, "r") == NULL)
		return inicializar();

	return file2string(user);
}

/**
\brief Função que processa o comando do jogador, fazendo com que os monstros se
movam, o jogador possa matar os inimigos, usar power ups e passar de nível.
@param e Estado do jogo.
@returns Estado atualizado.
*/
ESTADO processar_comando(ESTADO e){
	int nivel = 0;
	int inimigos_mortos = 0;
	int score = 0;
	int pup, num_aleatorio;
	int na_j1, na_j2, na_j3;

	na_j1 = qual_powerup(&e, 1);
	na_j2 = qual_powerup(&e, 2);
	na_j3 = qual_powerup(&e, 3);

	if (tem_jogador(e, e.porta.x, e.porta.y)){
    nivel = e.nivel;
		inimigos_mortos = e.inimigos_mortos;
		score = e.score;

		e = inicializar();
		e.num_jogadas = 0;
	  e.nivel = nivel + 1;
		e.inimigos_mortos = inimigos_mortos;
		e.score = score + 20;

		if (na_j1 != 0) e.powerup[na_j1][NO_INVENTARIO].janela = 1;
		if (na_j2 != 0) e.powerup[na_j2][NO_INVENTARIO].janela = 2;
		if (na_j3 != 0) e.powerup[na_j3][NO_INVENTARIO].janela = 3;

	}

	else if (tem_inimigo(e, ESQUELETO, e.jog.pos.x, e.jog.pos.y)) {
		mata_inimigo(&e, ESQUELETO, e.jog.pos.x, e.jog.pos.y);
	}

	else if (tem_inimigo(e, OGRE, e.jog.pos.x, e.jog.pos.y) && dano_inimigo(&e, OGRE, e.jog.pos.x, e.jog.pos.y)) {
		mata_inimigo(&e, OGRE, e.jog.pos.x, e.jog.pos.y);
	}

	else if (tem_inimigo(e, BRUXA, e.jog.pos.x, e.jog.pos.y) && dano_inimigo(&e, BRUXA, e.jog.pos.x, e.jog.pos.y)) {
		mata_inimigo(&e, BRUXA, e.jog.pos.x, e.jog.pos.y);
	}

	else if(tem_powerup(e, POWERUP_VIDA, e.jog.pos.x, e.jog.pos.y)){
    	powerup_vida(&e);
    	elimina_pup_mapa(&e, POWERUP_VIDA);
  	}

  else if(tem_powerups_excpt_vida(e, e.jog.pos.x, e.jog.pos.y)){
  		pup = tipo_powerup(&e, e.jog.pos.x, e.jog.pos.y);
    	encontrou_powerup(&e, pup);
  	}

  if (e.powerup[POWERUP_GELO][NO_INVENTARIO].jog_rest == 0)
		move_monstros(&e);
	else e.powerup[POWERUP_GELO][NO_INVENTARIO].jog_rest--;

	if (e.powerup[POWERUP_MOVE][NO_INVENTARIO].jog_rest == 1){
		e.powerup[POWERUP_MOVE][NO_INVENTARIO].jog_rest = 0;
		e.intencao = 0;
	} else if (e.powerup[POWERUP_MOVE][NO_INVENTARIO].jog_rest > 1)
		e.powerup[POWERUP_MOVE][NO_INVENTARIO].jog_rest--;

	if (e.powerup[POWERUP_ESCUDO][NO_INVENTARIO].jog_rest != 0)
		e.powerup[POWERUP_ESCUDO][NO_INVENTARIO].jog_rest--;


	if ((!todos_mapa_exceto_raio(&e)) && (e.num_jogadas % 5 == 0)){
		do {
			num_aleatorio = 1 + rand() % 8;
		} while ((esta_mapa(&e, num_aleatorio)) || num_aleatorio == POWERUP_RAIO);

  		e = inicializar_powerup(e, num_aleatorio);
  	}

	e.num_jogadas++;

	return e;
}

/**
\brief Extrai informação do query string.
@param estado Retorna o estado atual do jogo extraído do QUERY_STRING.
@param acao Retorna a ação a ser executada no jogo.
@param tipo Retorna o tipo de inimigo caso a ação esteja associada ao inimigo.
@param x Coodenada do eixo das abcissas onde o jogador se encontra.
@param y Coodenada do eixo das ordenadas onde o jogador se encontra.
*/
void extrair_informacao(char **estado, char **acao, int *tipo, int *x, int *y) {
	char * query = getenv("QUERY_STRING");
	char *tmp;

	if (query == NULL)
	  return;

  *acao = strtok(query, "&");

	*estado = strtok(NULL, "&");

	tmp = strtok(NULL, "&");
	if (tmp == NULL)
	  return;
	sscanf(tmp, "%d", tipo);

	tmp = strtok(NULL, "&");
	if (tmp == NULL)
	  return;
	sscanf(tmp, "%d", x);

	tmp = strtok(NULL, "&");
	if (tmp == NULL)
	  return;
	sscanf(tmp, "%d", y);
}


/**
\brief Procedimento que recolhe as informações relativas às coordenadas que o jogador
escolheu para efetuar os ataques de granada, martelo e tronco. Processa o bot.
Retira também vida ao inimigos quando estes sofrem o seu primeiro ataque.
Coloca power up raio no lugar onde estava a bruxa especial (bruxa numero 0).
@param e Estado atual do jogo (retorna novo estado)
@param acao A ação a ser executada no jogo.
@param bot_acao Informação sobre a ação a ser executada pelo bot.
@param estado Estado atual do jogo extraído do QUERY_STRING.
@param mov_x Coordenada do eixo das abcissas onde o ataque irá atingir.
@param mov_y Coordenada do eixo das ordenadas onde o ataque irá atingir.
@param arma_x Coordenada do eixo das abcissas onde o recurso vai ser
lançado, por exemplo o míssil ou granada.
@param arma_y Coordenada do eixo das ordenadas onde o recurso vai ser
lançado, por exemplo o míssil ou granada.
@param dif_x Informação sobre a direção do power up tronco.
@param dif_y Informação sobre a direção do power up tronco.
*/
void recolhe_info_acoes(ESTADO *e, char *acao, char *estado, char bot_acao[], int *mov_x, int *mov_y, int *arma_x, int *arma_y, int *dif_x, int *dif_y){

	if (acao == NULL || !strcmp(acao, "jogar") || !strcmp(acao, "nada") || !strcmp(acao, "bot")) {
    	*e = ler_estado();

		if (estado != NULL && (acao == NULL || (acao != NULL && strcmp(acao, "bot"))))
		  sscanf(estado, "%d,%d", mov_x, mov_y);

		else if (acao != NULL && !strcmp(acao, "bot"))
		  processar_bot(e, bot_acao, mov_x, mov_y, arma_x, arma_y);

		if (!bot_usou_powerup(e) && tem_bruxa_especial(*e, *mov_x, *mov_y)
		    && dano_inimigo(e, BRUXA, *mov_x, *mov_y)) {
			mata_inimigo(e, BRUXA, *mov_x, *mov_y);
			e->powerup[POWERUP_RAIO][NO_MAPA].pos.x = *mov_x;
			e->powerup[POWERUP_RAIO][NO_MAPA].pos.y = *mov_y;
    	}

		else if (!bot_usou_powerup(e) && tem_inimigo(*e, OGRE, *mov_x, *mov_y)
		         && !dano_inimigo(e, OGRE, *mov_x, *mov_y))
      		aumenta_dano(e, OGRE, *mov_x, *mov_y);

  	else if (!bot_usou_powerup(e) && tem_inimigo(*e, BRUXA, *mov_x, *mov_y)
				&& !dano_inimigo(e, BRUXA, *mov_x, *mov_y))
      		aumenta_dano(e, BRUXA, *mov_x, *mov_y);

  	else {
  	 		e->jog.pos.x = *mov_x;
  	  	e->jog.pos.y = *mov_y;
		}

  		if (!bot_usou_powerup(e) && e->jog.n_ataques < VIDAS &&
		    (acao == NULL || !strcmp(acao, "jogar") || !strcmp(acao, "bot"))) {
	  		*e = processar_comando(*e);
		}
  	}

	else if (acao != NULL && (!strcmp(acao, "ge") || !strcmp(acao, "me"))) {
			*e = ler_estado();
			sscanf(estado, "%d,%d", mov_x, mov_y);
	}

	else if (acao != NULL && !strcmp(acao, "marte")){
			*e = ler_estado();
			sscanf(estado, "%d,%d", mov_x, mov_y);
	}

	else if (acao != NULL && !strcmp(acao, "troncoe")){
			*e = ler_estado();
			sscanf(estado, "%d,%d", mov_x, mov_y);
			*dif_x = (*mov_x - e->jog.pos.x);
			*dif_y = (*mov_y - e->jog.pos.y);
	}

	else *e = str2estado(estado);

	if (strcmp(bot_acao, "")) {
		strcpy(acao, bot_acao);
		*mov_x = *arma_x;
		*mov_y = *arma_y;
	}
}


/**
\brief Procedimento que faz que com que as granadas, missil, martelo, tronco, bomba e raio
efetivamente ataquem os inimigos e afetem o estado dos outros elementos do jogo.
@param e Estado atual do jogo (retorna novo estado)
@param acao A ação a ser executada no jogo.
@param bot_acao Informação sobre a ação a ser executada pelo bot.
@param mov_x Coordenada do eixo das abcissas onde o ataque irá atingir.
@param mov_y Coordenada do eixo das ordenadas onde o ataque irá atingir.
@param dif_x Informação sobre a direção do power up tronco.
@param dif_y Informação sobre a direção do power up tronco.
*/
void ataques_explosoes(ESTADO *e, char *acao, char bot_acao[], int mov_x, int mov_y, int dif_x, int dif_y){

	if (acao != NULL && (!strcmp(acao, "ge") || !strcmp(acao, "me")))
		granada_missil(e, acao, bot_acao, mov_x, mov_y);

	else if (acao != NULL && !strcmp(acao, "marte"))
    	powerup_martelo(e, mov_x, mov_y);

	else if (acao != NULL && !strcmp(acao, "troncoe"))
   		powerup_tronco(e, dif_x, dif_y);

  	else if (e->powerup[POWERUP_BOMBA][NO_INVENTARIO].info == 1){
		powerup_bomba(e);
		e->powerup[POWERUP_BOMBA][NO_INVENTARIO].info = 0;
	}

	else if (e->powerup[POWERUP_RAIO][NO_INVENTARIO].info == 1) {
		powerup_raio(e);
		e->powerup[POWERUP_RAIO][NO_INVENTARIO].info = 0;
	}
}


/**
\brief Função que inicializa o programa.
@returns 0 se o programa tiver sucesso.
*/
int main() {
	ESTADO e;
	char bot_acao[200];
	char *acao = NULL;
	char *estado = NULL;
	int tipo;
	int x, y, mov_x = 1, mov_y = 1, dif_x = 0, dif_y = 0, arma_x, arma_y;
	bot_acao[0] = 0;

	srand(time(NULL));

	extrair_informacao(&estado, &acao, &tipo, &x, &y);

	recolhe_info_acoes(&e, acao, estado, bot_acao, &mov_x, &mov_y, &arma_x, &arma_y, &dif_x, &dif_y);

	imprime_jogo(&e, acao, tipo, x, y, mov_x, mov_y, dif_x, dif_y);

	ataques_explosoes(&e, acao, bot_acao, mov_x, mov_y, dif_x, dif_y);

	if ((acao != NULL && !strcmp(acao, "bot")) || bot_usou_powerup(&e)) {
	    if (e.jog.n_ataques < VIDAS){
		    REFRESH_BOT;
		}
		else
	      printf("<meta http-equiv=\"refresh\" content=\"0; URL=http://localhost/topscores.html\"><meta name=\"keywords\" content=\"automatic redirection\">");
  	}

	str2file(estado2str(e), e.user);

	return 0;
}
