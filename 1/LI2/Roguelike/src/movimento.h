#ifndef ___MOVIMENTO_H___
#define ___MOVIMENTO_H___

#include "estado.h"

/**
@file movimento.h
Estruturas úteis para determinar os movimentos do jogador.
*/


/**
\brief Número de direções em que o jagador se pode mover.
*/
#define DIRECOES   8


/**
\brief Estrutura que representa uma posição.
*/
typedef struct nova_posicao {
  /** \brief Abcissa */
  int x;
  /** \brief Ordenada */
  int y;
} NOVA_POSICAO;


/**
\brief Estrutura que define um tipo NODO, o qual contém a distância a
uma posição inicial, as coordenadas da posição anterior e da posição
atual do caminho.
*/
typedef struct nodo {
    /** \brief Distância da posição inicial */
    int dist;
    /** \brief Coordenadas da posição anterior */
    NOVA_POSICAO ant;
    /** \brief Coordenadas da posição atual */
    NOVA_POSICAO atual;
} NODO;

/**
\brief Estrutura que define a lista que conterá o caminho a percorrer.
*/
typedef struct lista {
  /** \brief Cada nodo da lista */
  NODO nodo;
  /** \brief apontador para a próxima posição da lista */
  struct lista *prox;
} LISTA_CAMINHO;

void determinar_caminhos(ESTADO *e, int x, int y);

int tem_caminho(int x, int y);

LISTA_CAMINHO * retorna_caminho(int x, int y);

void apaga_caminho(LISTA_CAMINHO *l);

#endif
