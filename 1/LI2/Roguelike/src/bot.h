#ifndef ___BOT_H___
#define ___BOT_H___

#include "estado.h"

/**
@file bot.h
Estratégia do bot.
*/

void processar_bot(ESTADO *e, char *acao, int *mov_x, int *mov_y, int *arma_x, int *arma_y);


int bot_usou_powerup(ESTADO *e);

#endif
