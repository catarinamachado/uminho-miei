#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <assert.h>

#include "apresentacao.h"
#include "estado.h"
#include "monstros.h"
#include "cgi.h"
#include "topScores.h"
#include "help.h"
#include "powerup.h"
#include "ataques.h"
#include "bot.h"

/**
@file apresentacao.c
Impressão do HTML do jogo.
*/

/**
\brief Tamanho máximo do buffer que contém o estado do jogo.
*/
#define MAX_BUFFER  10240

/**
\brief Variável global que contém um apontador para um array de imagens.
*/
const char *imagem[] = {"ogre.png", "bruxa.png", "esqueleto.png"};

/**
\brief Abre um link.
@param x Coordenada do eixo das abcissas do jogador.
@param y Coordenada do eixo das ordenadas do jogador.
@param user Nome do jogador.
*/
void imprime_abrir_link(int x, int y, char *user) {
  char link[MAX_BUFFER];
  sprintf(link, "http://localhost/cgi-bin/game?%s&%d,%d-%s", "jogar", x, y, user);
  ABRIR_LINK(link);
}

/**
\brief Fecha um link.
*/
void imprime_fechar_link() {
    FECHAR_LINK;
}

/**
\brief Verifica se o jogador se pode mover para determinada posição.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@param movimentos Retorna as posições para onde o jogador se pode mover.
@param n_pos Número de posições para onde o jogador se pode mover.
@returns int - 1 se pode mover para aquela posição 0 caso contrário.
*/
int pode_mover(int x, int y, POSICAO *movimentos, int n_pos) {
  int i;
  for (i = 0; i < n_pos; i++) {
    if(movimentos[i].x == x && movimentos[i].y == y) {
      return 1;
    }
  }
  return 0;
}

/**
\brief Procedimento que calcula todos os potenciais movimentos do jogador.
@param e Estado atual do jogo.
@param movimentos Retorna as posições para onde o jogador se pode mover.
@param n_pos Retorna o número de posições para onde o jogador se pode mover.
*/
void calcula_movimentos(ESTADO *e, POSICAO * movimentos, int *n_pos) {
  int x, y, sim_dx, sim_dy;
  int dx = e->intencao + 1; /*e.intencao é zero para se mover normalmente e é 1 para usar powerup movimento 2 casas*/
  int dy = e->intencao + 1;
  assert (*n_pos == 0);

  for (sim_dx = -dx; sim_dx <= dx; sim_dx++) {
    for (sim_dy = -dy ; sim_dy <= dy ; sim_dy++){
      x = e->jog.pos.x + sim_dx;
      y = e->jog.pos.y + sim_dy;
      if(posicao_valida(x, y) && !tem_obstaculo(*e, x, y) && !tem_jogador(*e, x, y)) {
        movimentos[*n_pos].x = x;
        movimentos[*n_pos].y = y;
        (*n_pos)++;
      }
    }
  }
}

/**
\brief Procedimento que imprime todas as casas do tabuleiro através da
utilização da macro "QUADRADO", definida em cgi.h.
@param e Estado atual do jogo.
@param movimentos Posições para onde o jogador se pode mover.
@param n_pos Número de posições para onde o jogador se pode mover.
*/
void imprime_casa(ESTADO *e, POSICAO *movimentos, int n_pos) {
  int x, y, jogada, idx;

  for(y = 0 ; y < TAM ; y++) {
    for(x = 0 ; x < TAM ; x++) {
      idx = (x + y) % 2;
      jogada = pode_mover(x, y, movimentos, n_pos);
      if (jogada) {
        imprime_abrir_link(x, y, e->user);
      }

      QUADRADO(x, y, ESCALA, e->cor[idx]);

      if (jogada) {
        imprime_fechar_link();
      }
    }
  }
}

/**
\brief Procedimento que imprime a imagem da porta através da macro IMAGEM,
definida em cgi.h.
@param e Estado atual do jogo.
@param movimentos Posições para onde o jogador se pode mover.
@param n_pos Número de posições para onde o jogador se pode mover.
*/
void imprime_porta(ESTADO *e, POSICAO *movimentos, int n_pos) {
  int jogada;
  int x = e->porta.x;
  int y = e->porta.y;

  jogada = pode_mover(x, y, movimentos, n_pos);
  if (jogada) {
    imprime_abrir_link(x, y, e->user);
  }

  IMAGEM(x, y, ESCALA, "coroa.png");

  if (jogada) {
    imprime_fechar_link();
  }
}

/**
\brief Procedimento que imprime a imagem do dano do inimigo,
quando ele possui toda a vida.
@param x Coordenada do eixo das abcissas do inimigo.
@param y Coordenada do eixo das ordenadas do inimigo.
*/
void imprime_semdano(int x, int y) {
  IMAGEM(x, y, ESCALA, "semdano.png");
}

/**
\brief Procedimento que imprime a imagem do dano do inimigo,
quando ele possui metade da vida.
@param x Coordenada do eixo das abcissas do inimigo.
@param y Coordenada do eixo das ordenadas do inimigo.
*/
void imprime_meiodano(int x, int y) {
  IMAGEM(x, y, ESCALA, "meiodano.png");
}

/**
\brief Procedimento que imprime a imagem do power up tronco.
@param e Estado atual do jogo.
@param movimentos Posições para onde o jogador se pode mover.
@param n_pos Número de posições para onde o jogador se pode mover.
*/
void imprime_powerup_tronco(ESTADO *e, POSICAO *movimentos, int n_pos) {
  int jogada;
  int x = e->powerup[POWERUP_TRONCO][NO_MAPA].pos.x;
  int y = e->powerup[POWERUP_TRONCO][NO_MAPA].pos.y;

  jogada = pode_mover(x, y, movimentos, n_pos);
  if (jogada) {
    imprime_abrir_link(x, y, e->user);
  }
  IMAGEM(x, y, ESCALA, "powerup_tronco.png");

  if (jogada) {
    imprime_fechar_link();
  }
}

/**
\brief Procedimento que imprime a imagem do power up martelo.
@param e Estado atual do jogo.
@param movimentos Posições para onde o jogador se pode mover.
@param n_pos Número de posições para onde o jogador se pode mover.
*/
void imprime_powerup_martelo(ESTADO *e, POSICAO *movimentos, int n_pos) {
  int jogada;
  int x = e->powerup[POWERUP_MARTELO][NO_MAPA].pos.x;
  int y = e->powerup[POWERUP_MARTELO][NO_MAPA].pos.y;

  jogada = pode_mover(x, y, movimentos, n_pos);
  if (jogada) {
    imprime_abrir_link(x, y, e->user);
  }
  IMAGEM(x, y, ESCALA, "powerup_martelo.png");

  if (jogada) {
    imprime_fechar_link();
  }
}

/**
\brief Procedimento que imprime a imagem do power up bomba.
@param e Estado atual do jogo.
@param movimentos Posições para onde o jogador se pode mover.
@param n_pos Número de posições para onde o jogador se pode mover.
*/
void imprime_powerup_bomba(ESTADO *e, POSICAO *movimentos, int n_pos) {
  int jogada;
  int x = e->powerup[POWERUP_BOMBA][NO_MAPA].pos.x;
  int y = e->powerup[POWERUP_BOMBA][NO_MAPA].pos.y;

  jogada = pode_mover(x, y, movimentos, n_pos);
  if (jogada) {
    imprime_abrir_link(x, y, e->user);
  }
  IMAGEM(x, y, ESCALA, "powerup_bomba.png");

  if (jogada) {
    imprime_fechar_link();
  }
}

/**
\brief Procedimento que imprime a imagem do power up escudo.
@param e Estado atual do jogo.
@param movimentos Posições para onde o jogador se pode mover.
@param n_pos Número de posições para onde o jogador se pode mover.
*/
void imprime_powerup_escudo(ESTADO *e, POSICAO *movimentos, int n_pos) {
  int jogada;
  int x = e->powerup[POWERUP_ESCUDO][NO_MAPA].pos.x;
  int y = e->powerup[POWERUP_ESCUDO][NO_MAPA].pos.y;

  jogada = pode_mover(x, y, movimentos, n_pos);
  if (jogada) {
    imprime_abrir_link(x, y, e->user);
  }
  IMAGEM(x, y, ESCALA, "powerup_escudo.png");

  if (jogada) {
    imprime_fechar_link();
  }
}

/**
\brief Procedimento que imprime a imagem do power up gelo.
@param e Estado atual do jogo.
@param movimentos posições para onde o jogador se pode mover.
@param n_pos número de posições para onde o jogador se pode mover.
*/
void imprime_powerup_gelo(ESTADO *e, POSICAO *movimentos, int n_pos) {
  int jogada;
  int x = e->powerup[POWERUP_GELO][NO_MAPA].pos.x;
  int y = e->powerup[POWERUP_GELO][NO_MAPA].pos.y;

  jogada = pode_mover(x, y, movimentos, n_pos);
  if (jogada) {
    imprime_abrir_link(x, y, e->user);
  }
  IMAGEM(x, y, ESCALA, "powerup_gelo.png");

  if (jogada) {
    imprime_fechar_link();
  }
}

/**
\brief Procedimento que imprime a imagem do power up raio.
@param e Estado atual do jogo.
@param movimentos Posições para onde o jogador se pode mover.
@param n_pos Número de posições para onde o jogador se pode mover.
*/
void imprime_powerup_raio(ESTADO *e, POSICAO *movimentos, int n_pos) {
  int jogada;
  int x = e->powerup[POWERUP_RAIO][NO_MAPA].pos.x;
  int y = e->powerup[POWERUP_RAIO][NO_MAPA].pos.y;

  jogada = pode_mover(x, y, movimentos, n_pos);
  if (jogada) {
    imprime_abrir_link(x, y, e->user);
  }
  IMAGEM(x, y, ESCALA, "powerup_raio.png");

  if (jogada) {
    imprime_fechar_link();
  }
}

/**
\brief Procedimento que imprime a imagem do power up move.
@param e Estado atual do jogo.
@param movimentos Posições para onde o jogador se pode mover.
@param n_pos Número de posições para onde o jogador se pode mover.
*/
void imprime_powerup_move(ESTADO *e, POSICAO *movimentos, int n_pos) {
  int jogada;
  int x = e->powerup[POWERUP_MOVE][NO_MAPA].pos.x;
  int y = e->powerup[POWERUP_MOVE][NO_MAPA].pos.y;

  jogada = pode_mover(x, y, movimentos, n_pos);
  if (jogada) {
    imprime_abrir_link(x, y, e->user);
  }
  IMAGEM(x, y, ESCALA, "powerup_move.png");

  if (jogada) {
    imprime_fechar_link();
  }
}

/**
\brief Procedimento que imprime a imagem do power up vida.
@param e Estado atual do jogo.
@param movimentos Posições para onde o jogador se pode mover.
@param n_pos Número de posições para onde o jogador se pode mover.
*/
void imprime_powerup_vida(ESTADO *e, POSICAO *movimentos, int n_pos) {
  int jogada;
  int x = e->powerup[POWERUP_VIDA][NO_MAPA].pos.x;
  int y = e->powerup[POWERUP_VIDA][NO_MAPA].pos.y;

  jogada = pode_mover(x, y, movimentos, n_pos);
  if (jogada) {
    imprime_abrir_link(x, y, e->user);
  }
  IMAGEM(x, y, ESCALA, "powerup_vida.png");

  if (jogada) {
    imprime_fechar_link();
  }
}

/**
\brief Procedimento que imprime um determinado inimigo através da macro IMAGEM,
definida em cgi.h.
@param e Estado atual do jogo.
@param tipo identifica o tipo de inimigo.
@param num_inimigo Número do inimigo.
@param acao String que determina a ação a ser executada.
@param movimentos Posições para onde o jogador se pode mover.
@param n_pos Número de posições para onde o jogador se pode mover.
*/
void imprime_inimigo(ESTADO *e, int tipo, int num_inimigo, char * acao, POSICAO *movimentos, int n_pos) {
  char link_ent[MAX_BUFFER];
  char link_saida[MAX_BUFFER];
  int jogada;
  int x = e->inimigo[tipo][num_inimigo].pos.x;
  int y = e->inimigo[tipo][num_inimigo].pos.y;

  jogada = pode_mover(x, y, movimentos, n_pos);
  if (jogada) {
    imprime_abrir_link(x, y, e->user);
  }

  if (acao == NULL || (acao != NULL && strcmp(acao, "m") && strcmp(acao, "g") && strcmp(acao, "marte")
      && strcmp(acao, "mart") && strcmp(acao, "tronco") && strcmp(acao, "troncoe"))) {
    sprintf(link_ent, "http://localhost/cgi-bin/game?%s&%s&%d&%d&%d", "info", estado2str(*e), tipo, x, y);
    sprintf(link_saida, "http://localhost/cgi-bin/game?%s&%d,%d-%s", "nada", e->jog.pos.x, e->jog.pos.y, e->user);
    assert(strlen(link_ent) < MAX_BUFFER);
    assert(strlen(link_saida) < MAX_BUFFER);
    IMAGEM_INIMIGO(x, y, ESCALA, imagem[tipo], link_ent, link_saida);
    if (e->powerup[POWERUP_GELO][NO_INVENTARIO].jog_rest != 0) IMAGEM(x, y, ESCALA, "animacao_gelo.png");
  }

  else{
    IMAGEM(x, y, ESCALA, imagem[tipo]);
    if (e->powerup[POWERUP_GELO][NO_INVENTARIO].jog_rest != 0) IMAGEM(x, y, ESCALA, "animacao_gelo.png");
  }

  if (jogada) {
    imprime_fechar_link();
  }
}

/**
\brief Procedimento que imprime inimigos através da macro IMAGEM, definida em cgi.h.
@param e Estado atual do jogo.
@param acao String que determina a ação a ser executada.
@param movimentos Posições para onde o jogador se pode mover.
@param n_pos Número de posições para onde o jogador se pode mover.
*/
void imprime_inimigos(ESTADO *e, char * acao, POSICAO *movimentos, int n_pos) {
  int num_inimigo, tipo;
  for (tipo = 0 ; tipo < TIPO_INIMIGO ; tipo++) {
    for(num_inimigo = 0 ; num_inimigo < e->num_inimigos[tipo] ; num_inimigo++) {
      int x = e->inimigo[tipo][num_inimigo].pos.x;
      int y = e->inimigo[tipo][num_inimigo].pos.y;

      if (e->inimigo[tipo][num_inimigo].n_ataques == 0)
        imprime_semdano(x, y);
      else
        imprime_meiodano(x, y);

      imprime_inimigo(e, tipo, num_inimigo, acao, movimentos, n_pos);
    }
  }
}

/**
\brief Procedimento que imprime jogador através da macro IMAGEM,
definida em cgi.h, e os seus movimentos possíveis.
@param e Estado atual do jogo.
*/
void imprime_jogador(ESTADO *e) {
  if (e->intencao == 0) IMAGEM(e->jog.pos.x, e->jog.pos.y, ESCALA, "rei.png");
  else IMAGEM(e->jog.pos.x, e->jog.pos.y, ESCALA, "animacao_move.png");

  if (e->powerup[POWERUP_ESCUDO][NO_INVENTARIO].jog_rest != 0) IMAGEM(e->jog.pos.x, e->jog.pos.y, ESCALA, "animacao_escudo.png");

}

/**
\brief Procedimento que imprime os obstáculos através da macro IMAGEM,
definida em cgi.h.
@param e Estado atual do jogo.
*/
void imprime_obstaculos(ESTADO e) {
  int obstaculo;
  for(obstaculo = 0 ; obstaculo < e.num_obstaculos ; obstaculo++)
    IMAGEM(e.obstaculo[obstaculo].x, e.obstaculo[obstaculo].y, ESCALA, "obstaculo.png");
}

/**
\brief Procedimento que imprime a parede em torno do mapa através da
macro IMAGEM definida em cgi.h.
*/
void imprime_parede() {
  int x, y;
  for (x = 0 ; x < TAM ; x++) IMAGEM(x,         0,          ESCALA, "wall.png");
  for (y = 0 ; y < TAM ; y++) IMAGEM(0,         y,          ESCALA, "wall.png");
  for (x = 0 ; x < TAM ; x++) IMAGEM(x,         TAM-ESCALA, ESCALA, "wall.png");
  for (y = 0 ; y < TAM ; y++) IMAGEM(TAM-ESCALA, y,         ESCALA, "wall.png");
}

/**
\brief Procedimento que imprime os power ups numa determinada janela do
inventário.
@param e Estado atual do jogo.
@param janela Determina em que posição do inventário se encontra o
power up.
*/
void imprime_janela(ESTADO *e, int janela){
  if (qual_powerup(e, janela) == POWERUP_BOMBA)
      BOMBA;

  if (qual_powerup(e, janela) == POWERUP_ESCUDO)
      ESCUDO;

  if (qual_powerup(e, janela) == POWERUP_GELO)
      GELO;

  if (qual_powerup(e, janela) == POWERUP_MOVE)
      MOVE;

  if (qual_powerup(e, janela) == POWERUP_MARTELO)
      MARTELO(estado2str(*e));

  if (qual_powerup(e, janela) == POWERUP_TRONCO)
      TRONCO(estado2str(*e));

  if (qual_powerup(e, janela) == POWERUP_RAIO)
      RAIO;
}

/**
\brief Procedimento que imprime a tabela do jogo, com todas as informações
necessárias (vidas, número de inimigos destruídos, número de jogadas, nome
do utilizador, pontuação atual do jogador, melhor pontuação, informação
relativa às casas que os monstros atacam, ativação do jogador automático,
inventário e recursos(granadas e missil).
@param e Estado atual do jogo.
@param acao String que determina qual a ação a ser executada.
*/
void imprime_tabela(ESTADO *e, char *acao){
  char link_ent[MAX_BUFFER];
  char link_saida[MAX_BUFFER];
  int i, max_score = 0;


  NIVEL(e->nivel + 1);

  max_score = readScore();
  if (max_score < e->score) {
    max_score = e->score;
    writeScore(e->score);
  }

  TABELA_INICIO;

  INFO_VIDAS;

   for (i = 0; i < VIDAS - e->jog.n_ataques; i++) {
     CORACAO;
   }
   if (e->jog.n_ataques >= VIDAS) {
     topScores(e->score, e->user);
     printf("<meta http-equiv=\"refresh\" content=\"0; URL=http://localhost/topscores.html\"><meta name=\"keywords\" content=\"automatic redirection\">");
  }
  INFO_FIM;

  INFO_NUM(e->inimigos_mortos, e->num_jogadas - 1, e->score, max_score, e->user);

  INFO_AJUDAS;
    sprintf(link_ent, "http://localhost/cgi-bin/game?%s&%s", "infos", estado2str(*e));
    sprintf(link_saida, "http://localhost/cgi-bin/game?%s&%d,%d-%s", "nada", e->jog.pos.x, e->jog.pos.y, e->user);
    assert(strlen(link_ent) < MAX_BUFFER);
    assert(strlen(link_saida) < MAX_BUFFER);
    MONSTROS_INFO_INICIO(link_ent, link_saida);
    ESQUELETO_INFO;
    BRUXA_INFO;
    OGRE_INFO;
    MONSTROS_INFO_FIM;
    BOT;
    sprintf(link_ent, "http://localhost/cgi-bin/game?%s&%s", "bot", estado2str(*e));
    assert(strlen(link_ent) < MAX_BUFFER);
    BOT_INICIO(link_ent);
    BOT_IMAGE;
    BOT_FIM;
  INFO_FIM;

  TABELA_FIM;


  NOME_INVENTARIO;

  INVENTARIO_INICIO;
  INV_JANELA1_INICIO;
    if ((qual_powerup(e, 1) != POWERUP_MARTELO) && (qual_powerup(e, 1) != POWERUP_TRONCO) && (acao == NULL ||
      (acao != NULL && strcmp(acao, "m") && strcmp(acao, "g") && strcmp(acao, "marte")
      && strcmp(acao, "mart") && strcmp(acao, "tronco") && strcmp(acao, "troncoe")))){
    sprintf(link_ent, "http://localhost/cgi-bin/game?%s&%s", "janela1", estado2str(*e));
    sprintf(link_saida, "http://localhost/cgi-bin/game?%s&%d,%d-%s", "nada", e->jog.pos.x, e->jog.pos.y, e->user);
    assert(strlen(link_ent) < MAX_BUFFER);
    assert(strlen(link_saida) < MAX_BUFFER);
    JANELA1_INICIO(link_ent, link_saida);
    }
    imprime_janela(e, 1);

    JANELA1_FIM;
  INV_JANELA1_FIM;

  INV_JANELA2_INICIO;
    if ((qual_powerup(e, 2) != POWERUP_MARTELO) && (qual_powerup(e, 2) != POWERUP_TRONCO) && (acao == NULL ||
      (acao != NULL && strcmp(acao, "m") && strcmp(acao, "g") && strcmp(acao, "marte")
      && strcmp(acao, "mart") && strcmp(acao, "tronco") && strcmp(acao, "troncoe")))){
    sprintf(link_ent, "http://localhost/cgi-bin/game?%s&%s", "janela2", estado2str(*e));
    sprintf(link_saida, "http://localhost/cgi-bin/game?%s&%d,%d-%s", "nada", e->jog.pos.x, e->jog.pos.y, e->user);
    assert(strlen(link_ent) < MAX_BUFFER);
    assert(strlen(link_saida) < MAX_BUFFER);
    JANELA2_INICIO(link_ent, link_saida);
    }

    imprime_janela(e, 2);

    JANELA2_FIM;
  INV_JANELA2_FIM;

    INV_JANELA3_INICIO;
    if ((qual_powerup(e, 3) != POWERUP_MARTELO) && (qual_powerup(e, 3) != POWERUP_TRONCO) && (acao == NULL ||
      (acao != NULL && strcmp(acao, "m") && strcmp(acao, "g") && strcmp(acao, "marte")
      && strcmp(acao, "mart") && strcmp(acao, "tronco") && strcmp(acao, "troncoe")))){
    sprintf(link_ent, "http://localhost/cgi-bin/game?%s&%s", "janela3", estado2str(*e));
    sprintf(link_saida, "http://localhost/cgi-bin/game?%s&%d,%d-%s", "nada", e->jog.pos.x, e->jog.pos.y, e->user);
    assert(strlen(link_ent) < MAX_BUFFER);
    assert(strlen(link_ent) < MAX_BUFFER);
    JANELA3_INICIO(link_ent, link_saida);
    }

    imprime_janela(e, 3);

    JANELA3_FIM;
  INV_JANELA3_FIM;

  INVENTARIO_FIM;

  ARMAS(e->armas.granadas, e->armas.missil, estado2str(*e));
}

/**
\brief Procedimento que imprime as casas que são atacadas por um ou por todos
os monstros.
@param e Estado atual do jogo.
@param acao String que determina a ação a ser executada.
@param tipo Identifica o tipo de inimigo.
@param coord_x Coordenada do eixo das abcissas que pode estar sob ataque de um inimigo.
@param coord_y Coordenada do eixo das ordenadas que pode estar sob ataque de um inimigo.
@param movimentos posições para onde o jogador se pode mover.
@param n_pos número de posições para onde o jogador se pode mover.
*/
void imprime_ajuda(ESTADO *e, char *acao, int tipo, int coord_x, int coord_y, POSICAO *movimentos, int n_pos) {
  POSICAO pos_ataques[MAX_ATAQUE * TIPO_INIMIGO * MAX_INIMIGOS];
  int inimigo, pos, n_ataques = 0;
  int jogada;
  int x, y;
  char link_saida[MAX_BUFFER];

  if (acao == NULL || (strcmp(acao, "info") && strcmp(acao, "infos")))
    return;

  if ((!strcmp(acao, "info")) && (e->powerup[POWERUP_GELO][NO_INVENTARIO].jog_rest == 0)) {
    ataques_monstro[tipo](e, coord_x, coord_y, pos_ataques, &n_ataques);

    for (inimigo = 0; inimigo < n_ataques; inimigo++){
      x = pos_ataques[inimigo].x;
      y = pos_ataques[inimigo].y;
      jogada = pode_mover(x, y, movimentos, n_pos);
      if (jogada) {
        imprime_abrir_link(x, y, e->user);
      }
      if (coord_x == x && coord_y == y){
        sprintf(link_saida, "http://localhost/cgi-bin/game?%s&%d,%d-%s", "nada", e->jog.pos.x, e->jog.pos.y, e->user);
        IMAGEM_BRILHO(x, y, ESCALA, "brilho.png", link_saida);
      } else IMAGEM(x, y, ESCALA, "brilho.png");
      if (jogada) imprime_fechar_link();
    }
  }
  else if ((!strcmp(acao, "infos")) && (e->powerup[POWERUP_GELO][NO_INVENTARIO].jog_rest == 0)) {
      calcula_ataque_monstros(e, pos_ataques, &n_ataques);
      for (pos = 0; pos < n_ataques; pos++){
        x = pos_ataques[pos].x;
        y = pos_ataques[pos].y;
        jogada = pode_mover(x, y, movimentos, n_pos);
        if (jogada) {
          imprime_abrir_link(x, y, e->user);
        }
        IMAGEM(x, y, ESCALA, "brilho.png");
        if (jogada) imprime_fechar_link();
     }
  }
}

/**
\brief Procedimento que imprime as chamas das granadas, do missil e do power up bomba
através da macro IMAGEM, definida em cgi.h.
@param e Estado atual do jogo.
@param bot Identifica se a jogada é do bot.
@param x Coordenada do eixo das abcissas para gerar as chamas.
@param y Coordenada do eixo das ordenadas para gerar as chamas.
*/
void imprime_chamas(ESTADO *e, int bot, int x, int y){
  int dx, dy;
  int ch1, ch2;
  int t = TAM - 1;
  char *acao = bot ? "bot" : "jogar";

  for (dx = -1 ; dx <= 1 ; dx++)
      for (dy = -1 ; dy <= 1 ; dy++){
        ch1 = x + dx;
        ch2 = y + dy;
        if (((dx != 0 || dy != 0) || (!(e->powerup[POWERUP_BOMBA][NO_INVENTARIO].info == 1)))
             && (ch1 > 0 && ch2 > 0 && ch1 < t && ch2 < t))
          IMAGEM(ch1, ch2, ESCALA, "animacao_chamas.png");
    }

    if (e->powerup[POWERUP_BOMBA][NO_INVENTARIO].info == 1){
      printf("<meta http-equiv=\"refresh\" content=\"0;");
      printf("URL=http://localhost/cgi-bin/game?%s&%d,%d-%s\">", acao, e->jog.pos.x, e->jog.pos.y, e->user);
      printf("<meta name=\"keywords\" content=\"automatic redirection\">");
  }
}

/**
\brief Procedimento que imprime a animação do martelo, nas coordenadas que o
jogador selecionou.
@param mov_x Coordenada do eixo das abcissas.
@param mov_y Coordenada do eixo das ordenadas.
*/
void imprime_ani_martelo(int mov_x, int mov_y){
  IMAGEM(mov_x, mov_y, ESCALA, "animacao_martelo.png");
}

/**
\brief Procedimento que imprime a animação do raio, tanto em cima de cada inimigo,
como em cima de todo o mapa.
@param e Estado atual do jogo.
@param bot Identifica se a jogada é do bot.
*/
void imprime_raios(ESTADO *e, int bot){
  int tipo, i, x, y;
  char *acao = bot ? "bot" : "jogar";

  for(tipo = 0 ; tipo < TIPO_INIMIGO ; tipo++)
    for (i = 0 ; i < e->num_inimigos[tipo] ; i++){
        x = e->inimigo[tipo][i].pos.x;
        y = e->inimigo[tipo][i].pos.y;
        IMAGEM(x, y, ESCALA, "powerup_raio.png");
      }

  IMAGEM(0, 0, ESCALA * TAM, "powerup_raio.png");

  printf("<meta http-equiv=\"refresh\" content=\"2;");
  printf("URL=http://localhost/cgi-bin/game?%s&%d,%d-%s\">", acao, e->jog.pos.x, e->jog.pos.y, e->user);
  printf("<meta name=\"keywords\" content=\"automatic redirection\">");

}

/**
\brief Procedimento que imprime a animação do esqueleto, quando este está a
atacar o jogador.
@param e Estado atual do jogo.
*/
void imprime_ani_esqueleto(ESTADO *e){
  IMAGEM(e->jog.pos.x, e->jog.pos.y, ESCALA, "animacao_esqueleto.png");
}

/**
\brief Procedimento que imprime a animação do ogre, quando este está a
atacar o jogador.
@param e Estado atual do jogo.
*/
void imprime_ani_ogre(ESTADO *e){
  IMAGEM(e->jog.pos.x, e->jog.pos.y, ESCALA, "animacao_ogre.png");
}

/**
\brief Procedimento que imprime a animação da bruxa, quando esta está a atacar o
jogador.
@param e Estado atual do jogo.
*/
void imprime_ani_bruxa(ESTADO *e){
  IMAGEM(e->jog.pos.x, e->jog.pos.y, ESCALA, "animacao_bruxa.png");
}

/**
\brief Procedimento que imprime uma animação para o power up tronco
através da macro IMAGEM, definida em cgi.h, dando a ilusão de movimento.
@param e Estado atual do jogo.
@param dx Coordenada do eixo das abcissas.
@param dy Coordenada do eixo das ordenadas.
*/
void imprime_mov_tronco(ESTADO *e, int dx, int dy){
    int t, x, y, soma_x, soma_y;
    x = e->jog.pos.x;
    y = e->jog.pos.y;
    soma_x = x + dx;
    soma_y = y + dy;
    t = TAM - 2;

    if (dx == 1 && dy == 0){
      while (soma_x < t){
        soma_x = x + dx;

        if (tem_obstaculo(*e, soma_x, soma_y) || tem_porta(*e, soma_x, soma_y))
            break;
        else
            IMAGEM(soma_x, soma_y, ESCALA, "animacao_tronco.png");

        dx++;
      }
    }

    else if (dx == -1 && dy == 0){
      while (soma_x > 1){
        soma_x = x + dx;

        if (tem_obstaculo(*e, soma_x, soma_y) || tem_porta(*e, soma_x, soma_y))
            break;
        else
            IMAGEM(soma_x, soma_y, ESCALA, "animacao_tronco.png");

        dx--;
      }
    }

    else if (dx == 0 && dy == 1){
      while (soma_y < t){
        soma_y = y + dy;

        if (tem_obstaculo(*e, soma_x, soma_y) || tem_porta(*e, soma_x, soma_y))
            break;
        else
            IMAGEM(soma_x, soma_y, ESCALA, "powerup_tronco.png");

        dy++;
      }
    }

    else if (dx == 0 && dy == -1){
      while (soma_y > 1){
        soma_y = y + dy;

        if (tem_obstaculo(*e, soma_x, soma_y) || tem_porta(*e, soma_x, soma_y))
            break;
        else
            IMAGEM(soma_x, soma_y, ESCALA, "powerup_tronco.png");

        dy--;
      }
    }

      printf("<meta http-equiv=\"refresh\" content=\"0;");
      printf("URL=http://localhost/cgi-bin/game?jogar&%d,%d-%s\">", e->jog.pos.x, e->jog.pos.y, e->user);
      printf("<meta name=\"keywords\" content=\"automatic redirection\">");
}

/**
\brief Cria um tipo que é um apontador para funções que recebem
como parâmetro o Estado atual do jogo.
@param e Estado atual do jogo.
*/
typedef void (*funcao_inventario)(ESTADO *e);

/**
\brief Cria um vetor das funções que recebem como parâmetro o Estado.
*/
funcao_inventario powerup_inventario[] =
  {powerup_escudo, powerup_gelo, powerup_move};

/**
\brief Procedimento que, tendo em conta a janela do inventário que o jogador
selecionou, aciona o power up escolhido.
@param e Estado atual do jogo.
@param acao String que determina a acão a ser executada.
*/
void imprime_inventario(ESTADO *e, char *acao) {
  int r;

  if (acao == NULL || (strcmp(acao, "janela1") && strcmp(acao, "janela2") && strcmp(acao, "janela3")))
    return;

  if (!strcmp(acao, "janela1")){
    r = qual_powerup(e, 1);
    if (r == POWERUP_BOMBA)
      e->powerup[POWERUP_BOMBA][NO_INVENTARIO].info = 1;
    else if (r == POWERUP_RAIO)
      e->powerup[POWERUP_RAIO][NO_INVENTARIO].info = 1;
    else if ((r == POWERUP_ESCUDO) || (r == POWERUP_GELO) || (r == POWERUP_MOVE))
      powerup_inventario[r-3](e);
    elimina_pup_inventario(e, r);
    organiza_inventario(e);
  }

  else if (!strcmp(acao, "janela2")) {
    r = qual_powerup(e, 2);
    if (r == POWERUP_BOMBA)
      e->powerup[POWERUP_BOMBA][NO_INVENTARIO].info = 1;
    else if (r == POWERUP_RAIO)
      e->powerup[POWERUP_RAIO][NO_INVENTARIO].info = 1;
    else
      powerup_inventario[r-3](e);
    elimina_pup_inventario(e, r);
    organiza_inventario(e);
  }

  else if (!strcmp(acao, "janela3")){
    r = qual_powerup(e, 3);
    if (r == POWERUP_BOMBA)
      e->powerup[POWERUP_BOMBA][NO_INVENTARIO].info = 1;
    else if (r == POWERUP_RAIO)
      e->powerup[POWERUP_RAIO][NO_INVENTARIO].info = 1;
    else
      powerup_inventario[r-3](e);
    elimina_pup_inventario(e, r);
  }
}

/**
\brief Cria um tipo que é um apontador para funções que recebem
como parâmetrosos os infra descritos.
@param e Estado atual do jogo.
@param movimentos Posições para onde o jogador se pode mover.
@param n_pos Número de posições para onde o jogador se pode mover.
*/
typedef void (*imprime_powerup)(ESTADO *e, POSICAO *movimentos, int n_pos);

/**
\brief Cria um vetor das funções que recebem como parâmetros o Estado, as
posições para onde o jogador se pode mover e o número dessas posições.
*/
imprime_powerup powerup_aleatorio[] =
  {imprime_powerup_vida, imprime_powerup_bomba, imprime_powerup_escudo, imprime_powerup_gelo,
   imprime_powerup_move, imprime_powerup_raio,  imprime_powerup_tronco, imprime_powerup_martelo};


/**
\brief Procedimento que imprime ordenamente todos os elementos do jogo.
@param e Estado atual do jogo.
@param acao String que determina a acão a ser executada.
@param tipo Identifica o tipo de inimigo.
@param cx Coodenada do eixo das abcissas onde o jogador se encontra.
@param cy Coodenada do eixo das ordenadas onde o jogador se encontra.
@param mov_x Coodenada do eixo das abcissas para onde o jogador se quer mover.
@param mov_y Coodenada do eixo das ordenadas onde o jogador se quer mover.
@param dif_x Informação sobre a direção da animação do power up tronco.
@param dif_y Informação sobre a direção da animação do power up tronco.
*/
void imprime_jogo(ESTADO *e, char *acao, int tipo, int cx, int cy, int mov_x, int mov_y, int dif_x, int dif_y) {
  POSICAO movimentos[MAX_ATAQUE];
  int n_pos = 0;
  int pup;
  int bot = (acao != NULL && !strcmp(acao, "bot")) || bot_usou_powerup(e);

  ABRIR_HEAD_HTML;

  FECHAR_HEAD_HTML;

  print_help();
  PRINT_HELP;

  imprime_inventario(e, acao);

  calcula_movimentos(e, movimentos, &n_pos);

  imprime_tabela(e, acao);

  ABRIR_SVG(1000, 1000);

  imprime_casa(e, movimentos, n_pos);

  for (pup = 0; pup < MAX_POWERUPS; pup++){
      if (esta_mapa(e, pup))
        powerup_aleatorio[pup - 1](e, movimentos, n_pos);
    }

  imprime_inimigos(e, acao, movimentos, n_pos);
  imprime_porta(e, movimentos, n_pos);
  imprime_jogador(e);
  imprime_parede();
  imprime_obstaculos(*e);

  if ( acao != NULL && strcmp(acao, "m") && strcmp(acao, "g")
      && strcmp(acao, "mart") && strcmp(acao, "tronco"))
    imprime_ajuda(e, acao, tipo, cx, cy, movimentos, n_pos);

  if ( acao != NULL && !strcmp(acao, "g") && e->armas.granadas != 0)
    imprime_granada(e->jog.pos.x , e->jog.pos.y, e->user);

  else if ( acao != NULL && !strcmp(acao, "m") && e->armas.missil != 0) {
    imprime_missil(e->user, e->jog.pos.x, e->jog.pos.y);
  }

  else if ( acao != NULL && !strcmp(acao, "mart") ) {
    imprime_martelo(e->user, e->jog.pos.x, e->jog.pos.y);
  }

  else if ( acao != NULL && !strcmp(acao, "tronco") ) {
    imprime_tronco(e->user, e->jog.pos.x, e->jog.pos.y);
  }

  else if (acao != NULL && (!strcmp(acao, "ge") || !strcmp(acao, "me"))){
    imprime_chamas(e, bot, mov_x, mov_y);
   }

  else if (acao != NULL && !strcmp(acao, "troncoe")){
    imprime_mov_tronco(e, dif_x, dif_y);
  }

  else if (acao != NULL && !strcmp(acao, "marte")){
    imprime_ani_martelo(mov_x, mov_y);
  }

  else if (e->powerup[POWERUP_BOMBA][NO_INVENTARIO].info == 1){
    imprime_chamas(e, bot, e->jog.pos.x, e->jog.pos.y);
  }

  else if (e->powerup[POWERUP_RAIO][NO_INVENTARIO].info == 1){
    imprime_raios(e, bot);
  }

  if (e->jog.esqueleto == 1){
    imprime_ani_esqueleto(e);
    e->jog.esqueleto = 0;
  }

  if (e->jog.ogre == 1){
    imprime_ani_ogre(e);
    e->jog.ogre = 0;
  }

  if (e->jog.bruxa == 1){
    imprime_ani_bruxa(e);
    e->jog.bruxa = 0;
  }

  FECHAR_SVG;
}
