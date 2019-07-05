#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include <string.h>

#include "estado.h"
#include "apresentacao.h"
#include "monstros.h"
#include "ataques.h"
#include "powerup.h"
#include "movimento.h"
#include "bot.h"

/**
@file bot.c
Estratégia do bot.
*/

/**
\brief Verifica se pode matar os monstros, isto é, se for um esqueleto
pode matar, se for um ogre pode matar caso o jogador esteja numa posição
perpendicular ao mesmo e, por último, se for uma bruxa pode matá-la se
esta já tiver sofrido anteriormente um ataque. Note-se que a bruxa só
ataca a cada três jogadas, portanto, pode ter sofrido uma agressão
nesse intervalo.
@param e Estado atual do jogo.
@param monstro_x Coordenada do eixo das abcissas do monstro.
@param monstro_y Coordenada do eixo das ordenadas do monstro.
@param jog_x Coordenada do eixo das abcissas do jogador.
@param jog_y Coordenada do eixo das ordenadas do jogador.
@returns int - representa um boleano - 1 se pode matar 0 se não pode.
*/
int pode_matar(ESTADO *e, int monstro_x, int monstro_y, int jog_x, int jog_y) {
	if (tem_inimigo(*e, ESQUELETO, monstro_x, monstro_y)) return 1;
	else if (tem_inimigo(*e, OGRE, monstro_x, monstro_y)) {
	  if (monstro_x == jog_x - 1 && monstro_y == jog_y) return 1;
		else if (monstro_x == jog_x + 1 && monstro_y == jog_y) return 1;
		else if (monstro_x == jog_x && monstro_y == jog_y - 1) return 1;
		else if (monstro_x == jog_x && monstro_y == jog_y + 1) return 1;
		else if (dano_inimigo(e, OGRE, monstro_x, monstro_y)) return 1;
	}
	 else if (tem_inimigo(*e, BRUXA, monstro_x, monstro_y) &&
            dano_inimigo(e, BRUXA, monstro_x, monstro_y)) return 1;
	return 0;
}

/**
\brief Verifica se o bot está a usar um power up escudo ou gelo.
@param e Estado atual do jogo.
@param tipo_powerup Que power up o bot está a utilizar.
@returns int - representa um boleano - 1 se o bot está a usar um power
up escudo ou gelo, caso contrário, retorna 0.
*/
int utilizando_powerup(ESTADO *e, int tipo_powerup) {
  if (e->powerup[tipo_powerup][NO_INVENTARIO].jog_rest != 0) return 1;
  return 0;
}

/**
\brief Verifica se o bot está a usar um power up (que não seja o escudo
ou o gelo).
@param e Estado atual do jogo.
@param tipo_powerup Qual o power up instantâneo que o bot está a utilizar.
@returns int - representa um boleano - 1 se o bot está a usar um power
up diferente do escudo ou do gelo, caso contrário, retorna 0.
*/
int utilizando_powerup_imediato(ESTADO *e, int tipo_powerup) {
  if (e->powerup[tipo_powerup][NO_INVENTARIO].info != 0) return 1;
  return 0;
}

/**
\brief Verifica se a posição onde o missil vai ser lançado provoca
dano no bot.
@param e Estado atual do jogo.
@param x Coordenada do eixo das abcissas do bot.
@param y Coordenada do eixo das ordenadas do bot.
@returns int - representa um boleano - 1 se o local onde vai ser
lançado o missil provoca dano no bot, caso contrário, retorna 0.
*/
int afeta_jogador_missil(ESTADO *e, int x, int y) {
  int dx, dy;
  for (dx = -2; dx < 3; dx++){
    for (dy = -2; dy < 3; dy++) {
      if (e->jog.pos.x + dx == x && e->jog.pos.y + dy == y)
        return 1;
    }
  }
  return 0;
}

/**
\brief Verifica se a posição onde a granada vai ser lançada provoca
dano no bot.
@param e Estado atual do jogo.
@param x Coordenada do eixo das abcissas do bot.
@param y Coordenada do eixo das ordenadas do bot.
@returns int - representa um boleano - 1 se o local onde vai ser
lançada a granada provoca dano no bot, caso contrário, retorna 0.
*/
int afeta_jogador_granada(ESTADO *e, int x, int y) {
  int dx, dy;

  /* Calcula o modulo da distancia vertical e horizontal. */
  dx = abs(x - e->jog.pos.x);
  dy = abs(y - e->jog.pos.y);

  /* Basta que o x ou o y estejam a, pelo menos, duas casas de distância*/
  if ((dx == 2 && dy <= 2) || (dx <= 2 && dy == 2))
    return 0;

  return 1;
}

/**
\brief Verifica se pode usar um power up ou recurso.
@param e Estado atual do jogo.
@param bot_acao Retorna a string que indica a ação a executar.
@param x Coordenada do eixo das abcissas sob ataque de um monstro.
@param y Coordenada do eixo das ordenadas sob ataque de um monstro.
@param arma_x Retorna a coordenada do eixo das abcissas onde o recurso vai ser
lançado, por exemplo o míssil ou granada.
@param arma_y Retorna a coordenada do eixo das ordenadas onde o recurso vai ser
lançado, por exemplo o míssil ou granada.
@returns int - que representa o power up/recurso que o bot pode utilizar, 0 se não
pode utilizar nenhum.
*/
int utiliza_powerup(ESTADO *e, char *bot_acao, int x, int y, int *arma_x, int *arma_y) {
  if (e->armas.missil != 0) {
		coord_monstro_atacante(e, x, y, arma_x, arma_y);
    if (!afeta_jogador_missil(e, *arma_x, *arma_y)) {
      strcpy(bot_acao, "me");
      return RECURSO_MISSIL;
    }
  }

  if (e->armas.granadas != 0) {
    coord_monstro_atacante(e, x, y, arma_x, arma_y);
    if (!afeta_jogador_granada(e, *arma_x, *arma_y)) {
      strcpy(bot_acao, "ge");
      return RECURSO_GRANADA;
    }
  }

  /* verifica se exite um power_up_escudo no inventário e se o jogador não está a utilizar já um power_up_escudo */
  if (esta_inventario(e, POWERUP_ESCUDO) && !utilizando_powerup(e, POWERUP_ESCUDO)) {
    powerup_escudo(e);
    elimina_pup_inventario(e, POWERUP_ESCUDO);
    return POWERUP_ESCUDO;
  }

  if (esta_inventario(e, POWERUP_GELO) && !utilizando_powerup(e, POWERUP_GELO)) {
    powerup_gelo(e);
    elimina_pup_inventario(e, POWERUP_GELO);
    return POWERUP_GELO;
  }

  if (esta_inventario(e, POWERUP_BOMBA) && !utilizando_powerup_imediato(e, POWERUP_BOMBA)) {
    e->powerup[POWERUP_BOMBA][NO_INVENTARIO].info = 1;
    elimina_pup_inventario(e, POWERUP_BOMBA);
    return POWERUP_BOMBA;
  }

  if (esta_inventario(e, POWERUP_RAIO) && !utilizando_powerup_imediato(e, POWERUP_RAIO)) {
    e->powerup[POWERUP_RAIO][NO_INVENTARIO].info = 1;
    elimina_pup_inventario(e, POWERUP_RAIO);
    return POWERUP_RAIO;
  }
  return 0;
}

/**
\brief Função que determina que se o bot estiver a duas casas de um power up
faz um desvio do seu percurso para o ir apanhar.
@param e Estado atual do jogo.
@param futuro_x Retorna a coordenada do eixo das abcissas para onde o bot
se vai mover.
@param futuro_y Retorna a coordenada do eixo das ordenadas para onde o bot
se vai mover.
*/
void tenta_ir_para_powerup(ESTADO *e, int *futuro_x, int *futuro_y) {
	int dx,dy;
	LISTA_CAMINHO *lista;

  /*se num raio de duas casas tiver um power up faz um desvio até ele*/
  for(dx = -2; dx < 3; dx++) {
	  for(dy = -2; dy < 3; dy++) {
		  if (tem_powerups(*e, e->jog.pos.x + dx, e->jog.pos.y + dy)
			  	&& posicao_valida(e->jog.pos.x + dx, e->jog.pos.y + dy)) {
			  lista = retorna_caminho(e->jog.pos.x + dx, e->jog.pos.y + dy);
			  *futuro_x = lista->prox->nodo.atual.x;
		  	*futuro_y = lista->prox->nodo.atual.y;
			  apaga_caminho(lista);
			  break;
		  }
	  }
  }
}

/**
\brief Função que determina a fuga dos ataques dos inimigos.
@param e Estado atual do jogo.
@param pos_ataques Posições sob ataque de todos os monstros.
@param n_ataques Número de posições sob ataque de todos monstros.
@param futuro_x Retorna a coordenada do eixo das abcissas para onde o bot
se vai mover.
@param futuro_y Retorna a coordenada do eixo das ordenadas para onde o bot
se vai mover.
*/
void bot_foge_ataques(ESTADO *e, POSICAO pos_ataques[], int n_ataques, int *futuro_x, int *futuro_y) {
	int dx, dy, foge_x, foge_y;
  /* bot foge do ataque dos monstros */
  for(dx = -1; dx < 2; dx++) {
	  for(dy = -1; dy < 2; dy++) {
		  foge_x = e->jog.pos.x + dx;
		  foge_y = e->jog.pos.y + dy;
		  if(!posicao_sob_ataque(foge_x, foge_y, pos_ataques, n_ataques) &&
				  !tem_obstaculo(*e, foge_x, foge_y) && posicao_valida(foge_x, foge_y)) {
			   *futuro_x = foge_x;
			   *futuro_y = foge_y;
			   return;
		  }
	  }
  }
}

/**
\brief Função que guarda as últimas seis jogadas do jogador automático.
@param e Estado atual do jogo.
@param x Coodenada do eixo das abcissas da próxima posição do jogador automático.
@param y Coodenada do eixo das ordenadaas da próxima posição do jogador automático.
*/
void guarda_posicoes(ESTADO *e, int x, int y) {
	int pos;

	for(pos = 5; pos > 0; pos--) {
		e->bot.ultimas_posicoes[pos] = e->bot.ultimas_posicoes[pos - 1];
	}

	e->bot.ultimas_posicoes[0].x = x;
	e->bot.ultimas_posicoes[0].y = y;
}

/**
\brief Função que verifica se o jogador automático entrou num loop,
analisando as suas últimas seis jogadas, e tira o jogador automático do loop
escolhendo uma qualquer posição em seu redor.
@param e Estado atual do jogo.
@param futuro_x Retorna a coordenada do eixo das abcissas para onde o jogador
automático se vai mover.
@param futuro_y Retorna a coordenada do eixo das ordenadas para onde o jogador
automático se vai mover.
*/
void tira_do_loop(ESTADO *e, int *futuro_x, int *futuro_y) {
	int pos, dx, dy, foge_x, foge_y;


	 for(pos = 0; pos < 4; pos += 2) {
    if (!posicoes_iguais(e->bot.ultimas_posicoes[pos], e->bot.ultimas_posicoes[pos + 2]))
		   return;
	 }

	 for(pos = 1; pos < 4; pos += 2) {
     if (!posicoes_iguais(e->bot.ultimas_posicoes[pos], e->bot.ultimas_posicoes[pos + 2]))
		   return;
	 }

	 for(dy = -1; dy < 2; dy++) {
 	   for(dx = -1; dx < 2; dx++) {
			 foge_x = e->jog.pos.x + dx;
 		   foge_y = e->jog.pos.y + dy;
 		   if(dx != 0 && dy != 0 && !tem_obstaculo(*e, foge_x, foge_y) &&
			    posicao_valida(foge_x, foge_y)) {
 			   *futuro_x = foge_x;
 			   *futuro_y = foge_y;
				 return;
		  }
	  }
  }
}

/**
\brief Função que determina os movimentos do jogador automático.
@param e Estado atual do jogo.
@param bot_acao Retorna a string que indica a ação a executar.
@param mov_x Retorna a coordenada do eixo das abcissas para onde o bot
se vai mover.
@param mov_y Retorna a coordenada do eixo das ordenadas para onde o bot
se vai mover.
@param arma_x Retorna a coordenada do eixo das abcissas onde o power up vai ser
lançado, por exemplo o míssil.
@param arma_y Retorna a coordenada do eixo das ordenadas onde o power up vai ser
lançado, por exemplo o míssil.
*/
void processar_bot(ESTADO *e, char *bot_acao, int *mov_x, int *mov_y, int *arma_x, int *arma_y) {
  LISTA_CAMINHO *lista;
  POSICAO pos_ataques[MAX_ATAQUE * TIPO_INIMIGO * MAX_INIMIGOS];
	int n_ataques = 0;
	int tipo_powerup = 0;
	int futuro_x, futuro_y;

  *mov_x = e->jog.pos.x;
	*mov_y = e->jog.pos.y;

  /* O bot utilizou um powerup arma e foi automaticamente executado.
	Portanto, não pode jogar agora. Mas se foi um outro powerup pode
	continuar a jogar. */
	e->bot.usou_powerup = 0;
	if (e->bot.usou_powerup_imediato) {
		e->bot.usou_powerup_imediato = 0;
		return;
	}

  /* caminhos possíveis */
	determinar_caminhos(e, e->jog.pos.x, e->jog.pos.y);

  /* determina o caminho mais curto para a porta */
	lista = retorna_caminho(e->porta.x, e->porta.y);

	if (lista->prox == NULL) {
		apaga_caminho(lista);
		return;
	}
	futuro_x = lista->prox->nodo.atual.x;
	futuro_y = lista->prox->nodo.atual.y;
	apaga_caminho(lista);

  /*se num raio de duas casas tiver um power up faz um desvio até ele*/
	tenta_ir_para_powerup(e, &futuro_x, &futuro_y);

	/* verifica quais as posições que são atacadas pelos monstros */
	calcula_ataque_monstros(e, pos_ataques, &n_ataques);

  /* se está encurralado tenta usar um power up, senão tenta fugir */
  if (posicao_sob_ataque(futuro_x, futuro_y, pos_ataques, n_ataques) &&
      !utilizando_powerup(e, POWERUP_ESCUDO) && !utilizando_powerup(e, POWERUP_GELO)
      && !pode_matar(e, futuro_x, futuro_y, e->jog.pos.x, e->jog.pos.y)) {

    /* se utiliza power up verifica o seu tipo */
    if ((tipo_powerup = utiliza_powerup(e, bot_acao, futuro_x, futuro_y, arma_x, arma_y))) {
			e->bot.usou_powerup = 1;
			if (tipo_powerup != POWERUP_ESCUDO && tipo_powerup != POWERUP_GELO) {
			  e->bot.usou_powerup_imediato = 1;
			}
			return;
		}

    /* bot foge do ataque dos monstros */
		bot_foge_ataques(e, pos_ataques, n_ataques, &futuro_x, &futuro_y);
  }

  /*verifica se o bot está em loop*/
	tira_do_loop(e, &futuro_x, &futuro_y);

	guarda_posicoes(e, futuro_x, futuro_y);

  /* Movimento suplente, caso não caiba em nenhum dos ifs,
	   para que o bot se mova sempre */
  *mov_x = futuro_x;
	*mov_y = futuro_y;
}

/**
\brief Verifica se o bot usou um power up.
@param e Estado atual do jogo.
@returns int - representa um boleano - retorna 1 se o bot usou um power
up e 0 caso contrário.
*/
int bot_usou_powerup(ESTADO *e) {
	return e->bot.usou_powerup;
}
