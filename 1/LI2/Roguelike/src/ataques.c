#include "powerup.h"
#include "monstros.h"
#include "estado.h"
#include "ataques.h"
#include "cgi.h"

#include <stdio.h>
#include <string.h>

/**
@file ataques.c
Funções que manipulam os recursos.
*/

/**
\brief Função que descobre qual é o tipo do inimigo que está
nas coordenadas (x,y).
@param e Estado do jogo.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns int Tipo do inimigo.
*/
int tipo_ini(ESTADO *e, int x, int y) {
  int t = 0, m;

  for (t = 0; t < TIPO_INIMIGO; t++)
    for (m = 0; m < e->num_inimigos[t]; m++)
      if (e->inimigo[t][m].pos.x == x && e->inimigo[t][m].pos.y == y)
        return t;

  return t;
}


/**
\brief Função que descobre qual é o número que identifica o obstáculo.
@param e Estado do jogo.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns int Número do obstáculo.
*/
int num_obstaculo(ESTADO *e, int x, int y){
    int i;

    for(i = 0 ; i < e->num_obstaculos ; i++)
        if (e->obstaculo[i].x == x && e->obstaculo[i].y == y)
            return i;

    return i;
}


/**
\brief Procedimento que elimina o vetor com as informações do obstáculo
em questão do Estado, eliminando assim o obstáculo do mapa.
@param e Estado do jogo.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
*/
void destroi_obstaculo(ESTADO *e, int x, int y){
    int i, j;

    i = num_obstaculo(e, x, y);

    for (j = i + 1 ; j < e->num_obstaculos ; j++)
        e->obstaculo[j-1] = e->obstaculo[j];

    e->num_obstaculos--;

    return;
}


/**
\brief Procedimento que verifica se nas determinadas coordenadas está
algum inimigo, obstáculo, o jogador, ou um power up, e,
em caso afirmativo, destrói-o.
@param e Estado do jogo.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
*/
void afeta(ESTADO *e, int x, int y) {
  int tipo, tipo_pup;

  if ( tem_inimigos(*e, x, y) ) {
    tipo = tipo_ini(e, x, y);
    mata_inimigo(e, tipo, x, y);
  }

  else if ( tem_obstaculo(*e, x, y) )
    destroi_obstaculo(e, x, y);

  else if ( tem_jogador(*e, x, y) )
    e->jog.n_ataques += 2;

  else if (tem_powerups(*e, x, y)){
    tipo_pup = tipo_powerup(e, x, y);
    elimina_pup_mapa(e, tipo_pup);
  }

  return;
}

/**
\brief Procedimento que elimina do mapa todos os inimigos, obstáculos e
power ups que estão nas 8 posições em torno das coordenadas (x, y).
@param e Estado do jogo.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
*/
void explode(ESTADO *e, int x, int y) {
  int dx, dy;

  for (dx = -1 ; dx <= 1 ; dx++)
    for (dy = -1 ; dy <= 1 ; dy++)
      afeta(e, x + dx, y + dy);

  return;
}


/*-------------------------------------------------*/

/**
\brief Função que lê de um ficheiro os 10 melhores scores e respetivos usernames para uma estrutura.
       @param ep - apontador para o estado atual do jogo
       @param acao - string com a ação a executar
       @param bot_acao - verifica que se a ação foi executada pelo jogador automático
       @param x - coordenada x em que a ação foi executada
       @param y - coordenada y em que a ação foi executada
*/
void granada_missil(ESTADO * ep, char * acao, char * bot_acao, int x, int y) {
  char *prox_acao = (acao != NULL && !strcmp(acao, "bot")) || strcmp(bot_acao, "") ? "bot" : "jogar";

  if ( !strcmp(acao, "ge") && ep->armas.granadas != 0 ) {
    explode(ep, x, y);
    ep->armas.granadas--;
  }

  if ( !strcmp(acao, "me") && ep->armas.missil != 0 ) {
    explode(ep, x, y);
    ep->armas.missil--;
  }

  printf("<meta http-equiv=\"refresh\" content=\"0;");
  printf("URL=http://localhost/cgi-bin/game?%s&%d,%d-%s\">", prox_acao,
         ep->jog.pos.x, ep->jog.pos.y, ep->user);
  printf("<meta name=\"keywords\" content=\"automatic redirection\">");

  return;
}

/*-------------------------------------------------*/

/**
\brief Procedimento que imprime no mapa uma posição possível para atacar
       @param x - coordenada x em que a ação pode ser executada
       @param y - coordenada y em que a ação pode ser executada
       @param user - string com o utilizador que pretende executar a ação
       @param tipo - string com a ação que o jogador pretende executar
*/
void imprime_casa_ataque(int x, int y, char user[], char tipo[]) {
  char link[200];

  if (!strcmp(tipo, "missil"))
    sprintf(link, "http://localhost/cgi-bin/game?me&%d,%d-%s", x, y, user);

  else if (!strcmp(tipo, "granada"))
    sprintf(link, "http://localhost/cgi-bin/game?ge&%d,%d-%s", x, y, user);

  else if (!strcmp(tipo, "martelo"))
    sprintf(link, "http://localhost/cgi-bin/game?marte&%d,%d-%s", x, y, user);

  else if (!strcmp(tipo, "tronco"))
    sprintf(link, "http://localhost/cgi-bin/game?troncoe&%d,%d-%s", x, y, user);

  ABRIR_LINK(link);
  IMAGEM(x, y, ESCALA, "target.png");
  FECHAR_LINK;

  return;
}

/**
\brief Procedimento que a partir da posição (x,y) imprime todas as posições para onde o míssil pode ser lançado.
       @param user - string com o utilizador que pretende executar a ação
       @param x - coordenada x em que a ação foi executada
       @param y - coordenada y em que a ação foi executada
*/
void imprime_missil(char user[], int x, int y) {
  int dx, dy, t;
  t = TAM-1;

  for (dx = 1; dx < t; dx++)
    for (dy = 1; dy < t; dy++)
      if (dx != x || dy != y)
        imprime_casa_ataque(dx, dy, user, "missil");

  return;
}

/**
\brief Procedimento que a partir da posição (x,y) imprime todas as posições para onde a granada pode ser lançada.
       @param user - string com o utilizador que pretende executar a ação
       @param x - coordenada x em que a ação foi executada
       @param y - coordenada y em que a ação foi executada
*/
void imprime_granada(int x, int y, char user[]) {
  int dx, dy;
  int t = TAM -1;

  for (dx = -2 ; dx <= 2 ; dx++)
    for (dy = -2 ; dy <= 2 ; dy++)
      if( (dx < -1 || dx > 1 || dy < -1 || dy > 1) && (x+dx) > 0 && (y+dy) > 0 && (x+dx) < t && (y+dy) < t)
        imprime_casa_ataque((x+dx), (y+dy), user, "granada");

  return;
}
