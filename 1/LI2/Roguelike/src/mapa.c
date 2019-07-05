#include <time.h>
#include <stdlib.h>
#include <stdio.h>
#include <assert.h>

#include "estado.h"
#include "mapa.h"
#include "ataques.h"

/**
@file mapa.c
Procedimento que cria os obstáculos que formam o mapa.
*/

/**
\brief Procedimento que nas divisões mais pequenas do mapa, define as zonas jogáveis.
       @param a - estrutura das divisões do mapa
*/
void casas(ABin a) {
  int x,y;

  if ( a == NULL ) {
    perror("casas abin a null");
    exit(1);
  }

  if ( a->esq != NULL && a->dir != NULL ) {
    casas(a->esq);
    casas(a->dir);
  }

/*Das divisões mais pequenas do mapa(folhas da abin) passa apenas a usar
uma porção dessas áreas, criando cavernas*/
  else {
    srand(time(NULL));
    x = (a->fx - a->ix);
    y = (a->fy - a->iy);
    a->fx = (rand() % x) + a->ix +1;
    a->fy = (rand() % y) + a->iy +1;
  }

  return;
}

/**
\brief Procedimento que divide o mapa horizontalmente.
       @param a - estrutura das divisões do mapa inical
       @param b - estrutura das divisões do mapa com o corte esquerdo
       @param c - estrutura das divisões do mapa com o corte direito
       @param corte - define a posição em que se vai executar o corte horizontal
*/
void horizontal(ABin a, ABin b, ABin c, int corte) {

  b->ix = a->ix; b->iy = a->iy;
  b->fx = a->fx; b->fy = corte;

  c->ix = a->ix; c->iy = corte;
  c->fx = a->fx; c->fy = a->fy;

  return;
}

/**
\brief Procedimento que divide o mapa verticalmente.
       @param a - estrutura das divisões do mapa inical
       @param b - estrutura das divisões do mapa com o corte superior
       @param c - estrutura das divisões do mapa com o corte inferior
       @param corte - define a posição em que se vai executar o corte vertical
*/
void vertical(ABin a, ABin b, ABin c, int corte) {

  b->ix = a->ix; b->iy = a->iy;
  b->fx = corte; b->fy = a->fy;

  c->ix = corte; c->iy = a->iy;
  c->fx = a->fx; c->fy = a->fy;

  return;
}

/**
\brief Procedimento que, recursivamente, divide o mapa em partes.
       @param a - estrutura das divisões do mapa
*/
void constroi(ABin a) {
  int corte_x, corte_y;
  int x,y;
  ABin b = NULL, c = NULL;

  if ( a == NULL ) {
    perror("constroi abin a null");
    exit(1);
  }

  if (a->fx - a->ix < SIZE || a->fy - a->iy < SIZE )
    return;

  b = malloc(sizeof(struct abin));
  if ( b == NULL ) {
    perror("stupid malloc constroi b");
    exit(1);
  }
  b->dir = b->esq = NULL;

  c = malloc(sizeof(struct abin));
  if ( c == NULL ) {
    perror("stupid malloc constroi c");
    exit(1);
  }
  c->dir = c->esq = NULL;

  a->esq = b;
  a->dir = c;

  srand(time(NULL));

/* zona de corte em local aleatório dentro das margens do retangulo */
  if (a->fx - a->ix > a->fy - a->iy) { /* se comprimento for maior que a largura */
      if ( (x = ((a->fx-1) - (a->ix+1) -1)) <= 0 ) return;
        corte_x = (rand() % x) + a->ix +1; /* zona de corte vertical */
        vertical(a, b, c, corte_x);
  }
  else {
    if ( (y = ((a->fy-1) - (a->iy+1) -1)) <= 0 ) return;
      corte_y = (rand() % y) + a->iy +1; /* zona de corte horizontal */
      horizontal(a, b, c, corte_y);
  }

  constroi(b);
  constroi(c);

  return;
}

/**
\brief Procedimento que retira da matriz que representa o mapa os obstáculos das posições definidas como jogáveis.
       @param ep - apontador para o estado atual do jogo
       @param a - estrutura das divisões do mapa
       @param obstaculo - matriz que representa o mapa no que diz respeito aos obstáculos
*/
void init_obstaculo(ESTADO * ep, ABin a, int obstaculo[TAM][TAM]) {
  int x, y;

  if ( a == NULL ) {
    perror("init_obstaculo abin a null");
    exit(1);
  }
  if ( ep == NULL ) {
    perror("init_obstaculo estado * ep null");
    exit(1);
  }

/*Para locais jogáveis representados na árvore binária, retira-se da matriz que
representa os mapa do jogo, os obstáculos dessas posições*/
  if ( a->esq == NULL || a->dir == NULL ) {
    for (x = a->ix; x < a->fx; x++ ) {
      for ( y = a->iy; y < a->fy; y++ ) {
          if (posicao_valida(x,y)) {
            obstaculo[x][y] = 0;
            ep->porta.x = x;
          	ep->porta.y = y;
          }
      }
    }
  }
  else {
    init_obstaculo(ep, a->esq ,obstaculo);
    init_obstaculo(ep, a->dir ,obstaculo);
  }

  return;
}

/*Entre as várias "cavernas" da matriz cria ligações tanto vertical como
horizontalmente desde a 1ª posição*/
/**
\brief Procedimento que, depois de criadas as cavernas, cria ligações entre elas.
       @param a - estrutura das divisões do mapa
       @param obstaculo - matriz que representa o mapa no que diz respeito aos obstáculos
*/
void open_path(ABin a, int obstaculo[TAM][TAM]) {
  int x, y;

  if ( a == NULL || a->esq == NULL || a->dir == NULL ) return;

    for ( y = a->esq->iy; y < a->dir->fy; y++ ) {
      if (posicao_valida(a->esq->ix,y)) {
        obstaculo[a->esq->ix][y] = 0;
      }
    }

    for ( x = a->esq->ix; x < a->dir->fx; x++ ) {
      if (posicao_valida(x, a->esq->iy)) {
        obstaculo[x][a->esq->iy] = 0;
      }
    }


  open_path(a->esq, obstaculo);
  open_path(a->dir, obstaculo);

  return;
}

/**
\brief Procedimento liberta espaços de memória ocupados pela estrutura ABin
       @param a - estrutura das divisões do mapa
*/
void free_ab(ABin a) {
  if (a != NULL) return;

  free_ab(a->esq);
  free_ab(a->dir);
  free(a);

  return;
}

/**
\brief Procedimento que guarda no estado do jogo as posições dos obstáculos.
       @param ep - apontador para o estado atual do jogo
       @param obstaculo - matriz que representa o mapa no que diz respeito aos obstáculos
*/
void obstaculos(ESTADO * ep, int obstaculo[TAM][TAM]) {
  int x,y;

  if ( ep== NULL ) {
    perror("obstaculos estado * ep null");
    exit(1);
  }

/*Nos locais onde a matriz que representa o mapa conter o valor 1, guarda no
estado um obstáculo nessa posição*/
  for (x = 1; x < TAM-1; x++)
    for (y = 1; y < TAM-1; y++)
      if ( obstaculo[x][y] == 1 ) {
        ep->obstaculo[(int)ep->num_obstaculos].x = x;
        ep->obstaculo[(int)ep->num_obstaculos].y = y;
        ep->num_obstaculos++;
      }

}

/**
\brief Procedimento que cria os obstáculos que formam o mapa.
       @param ep - apontador para o estado atual do jogo que será modificado
*/
void mapa(ESTADO * ep) {
  ABin a;
  int x, y;
  int obstaculo[TAM][TAM];

  if ( ep == NULL ) {
    perror("estado * ep null from inicializar");
    exit(1);
  }

/*Inicializa a um a matriz que representa as posições dos obstáculos*/
  for(x = 0; x < TAM-1; x ++)
    for(y = 0; y < TAM-1; y ++)
      obstaculo[x][y] = 1;

  a = malloc(sizeof(struct abin));
  if ( a == NULL ) {
    perror("stupid malloc mapa");
    exit(1);
  }

/*Na árvore binária que será usada para as divisões do mapa inicializa-se
o início em (1,1) e o fim em (TAM-1,TAM-1)*/
  a->ix = a->iy = 1;
  a->fx = a->fy = TAM-1;
  a->esq = a->dir = NULL;

  constroi(a);
  casas(a);

  init_obstaculo(ep, a ,obstaculo);
  open_path(a, obstaculo);
  free_ab(a);
  obstaculos(ep, obstaculo);

  return;
}
