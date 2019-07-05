#ifndef ___MONSTROS_H___
#define ___MONSTROS_H___

#include "estado.h"

/**
@file monstros.h
Funções relativas ao movimento e ataques dos monstros.
*/

/**
\brief O esqueleto poderá ser representado com o número 2.
*/
#define ESQUELETO  2

void move_monstros(ESTADO *e);

int num_inimigo(ESTADO *e, int tipo, int x, int y);

void elimina_inimigo(ESTADO *e, int tipo, int x, int y);

void mata_inimigo(ESTADO *e, int tipo, int x, int y);

int dano_inimigo(ESTADO *e, int tipo, int x, int y);

void aumenta_dano(ESTADO *e, int tipo, int x, int y);

void calcula_ataque_esqueleto(ESTADO *e, int x, int y, POSICAO *m, int *n_pos);

void calcula_ataque_bruxa(ESTADO *e, int x, int y, POSICAO *m, int *n_pos);

void calcula_ataque_ogre(ESTADO *e, int x, int y, POSICAO *m, int *n_pos);

/**
\brief Cria um tipo que é um apontador para funções que recebem
como parâmetros os infra descritos.
@param e Estado atual do jogo.
@param cx Coordenada do eixo das abcissas.
@param cy Coordenada do eixo das ordenadas.
@param pos Retorna um array com as posições do mapa sob ataque de um inimigo.
@param n_pos  Retorna o número de posições sob ataque de um inimigo.
*/
typedef void (*funcao_ataque_monstro)(ESTADO *e, int cx, int cy, POSICAO *pos, int *n_pos);

/**
\brief Cria um vetor das funções que recebem os seguintes parâmetros: o Estado
do jogo, as coordenadas do monstro, as posições sob ataque e o número de
posições sob ataque de um monstro.
*/
extern funcao_ataque_monstro ataques_monstro[];

void calcula_ataque_monstros(ESTADO *e, POSICAO *pos_ataque, int *n_pos_ataque);

void coord_monstro_atacante(ESTADO *e, int x, int y, int *monstro_x, int *monstro_y);

int posicao_sob_ataque(int x, int y, POSICAO *pos_ataques, int n_ataques);

#endif
