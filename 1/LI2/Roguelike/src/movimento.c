#include "estado.h"
#include "movimento.h"
#include "cgi.h"

#include <stdio.h>
#include <limits.h>
#include <stdlib.h>
#include <string.h>

/**
@file movimento.c
Funções úteis para determinar os movimentos do jogador.
*/

/**
\brief Determina o tabuleiro do jogo com a informação relativa a cada posição,
i. e.,distância de certa posição à posição inicial, coordenadas da posição anterior
e ainda da atual.
*/
NODO tabuleiro[TAM][TAM];

LISTA_CAMINHO * insere_nodo_fim(LISTA_CAMINHO *l, NODO n);

LISTA_CAMINHO * insere_nodo_inicio(LISTA_CAMINHO *l, NODO n);

LISTA_CAMINHO * remove_nodo(LISTA_CAMINHO *l);

void apaga_nodos(LISTA_CAMINHO *l);

/**
\brief Define o valor INVALIDO de uma posição do tabuleiro, por exemplo, onde está
um obstáculo ou a parede que rodeia o jogo.
*/
#define INVALIDO 99

/**
\brief Inicializa todas as posições matriciais com o valor INVALIDO.
*/
void inicializa_tabuleiro() {
  int coluna, linha;
  for (linha = 0; linha < TAM; linha++) {
    for (coluna = 0; coluna < TAM; coluna++) {
      tabuleiro[linha][coluna].dist = INVALIDO;
      tabuleiro[linha][coluna].ant.x = INVALIDO;
      tabuleiro[linha][coluna].ant.y = INVALIDO;
      tabuleiro[linha][coluna].atual.x = INVALIDO;
      tabuleiro[linha][coluna].atual.y = INVALIDO;
    }
  }
}

/**
\brief Função que determina os potenciais movimentos a partir de uma
determinada posição inicial (x,y). Para cada movimento que se afasta da posição
inicial, um peso maior e as coordenadas matriciais da posição predecessora são
atribuídos à posição matricial correspondente. Por outras palavras, para cada
uma das coordenadas temos a sua distância à posição inicial, bem como as
coordenadas predecessoras.
Note-se que este algoritmo não é tão eficiente, já que pode visitar posições mais
distantes antes de outras mais próximas.
@param e Estado atual do jogo.
@param x Coordenada inicial do eixo das abcissas.
@param y Coordenada inicial do eixo das ordenadas.
*/
void pesquisa_em_altura(ESTADO *e, int x, int y) {
  int dx, dy;
  NODO nodo_cabeca, n;
  LISTA_CAMINHO *lista = NULL;

  tabuleiro[y][x].dist= 0;
  tabuleiro[y][x].atual.x = x;
  tabuleiro[y][x].atual.y = y;

  lista = insere_nodo_inicio(lista, tabuleiro[y][x]);

  while (lista != NULL) {
    nodo_cabeca = lista->nodo;
    lista = remove_nodo(lista);
    x = nodo_cabeca.atual.x;
    y = nodo_cabeca.atual.y;
    for (dy = -1; dy < 2; dy++) {
      for (dx = -1; dx < 2; dx++) {
        if (posicao_valida(x + dx, y + dy) && !tem_obstaculo(*e, x + dx, y + dy)
            && tabuleiro[y + dy][x + dx].dist == INVALIDO) { /* nodo_cabeca.dist < tabuleiro[y + dy][x + dx].dist dá o mesmo que pesquisa em largura */
           n.dist = nodo_cabeca.dist + 1;
           n.ant.x = x;
           n.ant.y = y;
           n.atual.x = x + dx;
           n.atual.y = y + dy;
           tabuleiro[y + dy][x + dx] = n;
           lista = insere_nodo_inicio(lista, n);
        }
      }
    }
  }
}

/**
\brief Função que determina os potenciais movimentos a partir de uma
determinada posição inicial (x,y). Para cada movimento que se afasta da posição
inicial, um peso maior e as coordenadas matriciais da posição predecessora são
atribuídos à posição matricial correspondente. Por outras palavras, para cada
uma das coordenadas temos a sua distância à posição inicial, bem como as
coordenadas predecessoras.
Note-se que este algoritmo é mais eficiente, já que garante sempre a visita de
posições matriciais mais próximas.
@param e Estado atual do jogo.
@param x Coordenada inicial do eixo das abcissas.
@param y Coordenada inicial do eixo das ordenadas.
*/
void pesquisa_em_largura(ESTADO *e, int x, int y) {
  int dx, dy;
  NODO nodo_cabeca, n;
  LISTA_CAMINHO *lista = NULL;

  tabuleiro[y][x].dist= 0;
  tabuleiro[y][x].atual.x = x;
  tabuleiro[y][x].atual.y = y;

  lista = insere_nodo_fim(lista, tabuleiro[y][x]);

  while (lista != NULL) {
    nodo_cabeca = lista->nodo;
    lista = remove_nodo(lista);
    x = nodo_cabeca.atual.x;
    y = nodo_cabeca.atual.y;
    for (dy = -1; dy < 2; dy++) {
      for (dx = -1; dx < 2; dx++) {
        if (posicao_valida(x + dx, y + dy) && !tem_obstaculo(*e, x + dx, y + dy)
            && tabuleiro[y + dy][x + dx].dist == INVALIDO) {
           n.dist = nodo_cabeca.dist + 1;
           n.ant.x = x;
           n.ant.y = y;
           n.atual.x = x + dx;
           n.atual.y = y + dy;
           tabuleiro[y + dy][x + dx] = n;
           lista = insere_nodo_fim(lista, n);
        }
      }
    }
  }
}

/**
\brief Testa se existe um caminho até às coordenadas (x,y). O que significa que
a posição matricial das referidas coordenadas não corresponde a um valor inválido.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns int - 1 se existir um caminho 0 caso contrário.
*/
int tem_caminho(int x, int y){
  return (tabuleiro[y][x].dist != INVALIDO);
}

/**
\brief Inicializa a tabuleiro e calcula os movimentos do jogador ou de um monstro
a partir das suas coordenadas.
@param e Estado atual do jogo.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
*/
void determinar_caminhos(ESTADO *e, int x, int y) {
  inicializa_tabuleiro();
  pesquisa_em_largura(e, x, y);
}

/**
\brief Calcula o caminho entre duas coordenadas do tabuleiro do jogo, sendo a
primeira delas determinada pelo algoritmo pesquisa_em_largura ou pelo algoritmo
pesquisa_em_altura, dependendo daquele que é escolhido na função determinar_caminhos.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns LISTA_CAMINHO lista com as diversas informações sobre o caminho a percorrer.
*/
LISTA_CAMINHO * retorna_caminho(int x, int y) {
  NODO n;
  LISTA_CAMINHO *lista = NULL;

  while (x != INVALIDO && y != INVALIDO) {
    n = tabuleiro[y][x];
    lista = insere_nodo_inicio(lista, n);
    x = n.ant.x;
    y = n.ant.y;
  }

  return lista;
}

/**
\brief Liberta memória alocada pela função retorna_caminho.
@param l Lista com um caminho.
*/
void apaga_caminho(LISTA_CAMINHO *l) {
  apaga_nodos(l);
}

/**
\brief Insere um elemento no fim de uma lista.
@param l Lista com um caminho.
@param n contém a distância à posição inicial, a posição anterior e a atual
de certo caminho.
@returns LISTA_CAMINHO lista com as diversas informações sobre o caminho a percorrer.
*/
LISTA_CAMINHO * insere_nodo_fim(LISTA_CAMINHO *l, NODO n) {
  LISTA_CAMINHO *aux, *nova;
  aux = l;
  if (l == NULL) {
    l = (LISTA_CAMINHO *)malloc(sizeof(struct lista));
    l->nodo = n;
    l->prox = NULL;
  }
  else {
    while (aux->prox != NULL) {
      aux = aux->prox;
    }
    nova = (LISTA_CAMINHO *)malloc(sizeof(struct lista));
    nova->nodo = n;
    nova->prox = NULL;
    aux->prox = nova;
  }
  return l;
}

/**
\brief Insere um elemento no início de uma lista.
@param l Lista com um caminho.
@param novo contém a distância à posição inicial, a posição anterior e a actual
de certo caminho.
@returns LISTA_CAMINHO Lista com as diversas informações sobre o caminho a percorrer.
*/
LISTA_CAMINHO * insere_nodo_inicio(LISTA_CAMINHO *l, NODO novo) {
  LISTA_CAMINHO *cabeca_lista = l;
  l = (LISTA_CAMINHO *)malloc(sizeof(struct lista));
  l->nodo = novo;
  l->prox = cabeca_lista;

  return l;
}

/**
\brief Remove um elemento do fim de uma lista.
@param l Lista com um caminho.
@returns LISTA_CAMINHO Lista com as diversas informações sobre o caminho a percorrer.
*/
LISTA_CAMINHO * remove_nodo(LISTA_CAMINHO *l) {
  LISTA_CAMINHO *cabeca_lista = l;
  if (l != NULL) {
    l = l->prox;
  }

  if (cabeca_lista != NULL) {
    free(cabeca_lista);
  }
  return l;
}

/**
\brief Remove todos os elementos de uma lista.
@param l Lista com um caminho.
*/
void apaga_nodos(LISTA_CAMINHO *l) {
   while (l != NULL) {
     l = remove_nodo(l);
   }
}
