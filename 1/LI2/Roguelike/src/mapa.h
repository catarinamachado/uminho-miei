#ifndef __MAPA_H__
#define __MAPA_H__

#include "estado.h"

/**
@file mapa.h
Procedimento que cria os obstáculos que formam o mapa.
*/

/**
\brief Tamanho mínimo para que seja permitida uma divisão do mapa.
*/
#define SIZE ((TAM-1)/4)


/**
\brief Estrutura que contém todas as divisões do mapa.
		A raiz da árvore binária corresponde ao mapa total.
		Cada descendente de um nó são 2 subdivisões desse nó.
		As folhas da árvore, todas no mesmo nível, contém as subdivisões mais pequenas.
*/
typedef struct abin {
   /** \brief Abcissa do início do retângulo de divisão */
   int ix;
   /** \brief Ordenada do início do retângulo de divisão */
   int iy;
   /** \brief Abcissa do fim do retângulo de divisão */
   int fx;
   /** \brief Ordenada do fim do retângulo de divisão */
   int fy;
   /** \brief Árvore Binária com a primeira subdivisão */
   struct abin * esq;
   /** \brief Árvore Binária com a segunda subdivisão */
   struct abin * dir;
} * ABin;


void casas(ABin a);

void horizontal(ABin a, ABin b, ABin c, int corte);

void vertical(ABin a, ABin b, ABin c, int corte);

void constroi(ABin a);

void init_obstaculo(ESTADO * ep, ABin a, int obstaculo[TAM][TAM]);

void open_path(ABin a, int obstaculo[TAM][TAM]);

void free_ab(ABin a);

void obstaculos(ESTADO * ep, int obstaculo[TAM][TAM]);

void mapa(ESTADO * ep);

#endif
