#ifndef __APRESENTACAO_H__
#define __APRESENTACAO_H__

#include "estado.h"

/**
@file apresentacao.h
Impressão do jogo.
*/

void imprime_jogo(ESTADO *e, char *acao, int tipo, int cx, int cy, int mov_x, int mov_y, int dif_x, int dif_y);

#endif
