#ifndef ___POWERUP_H___
#define ___POWERUP_H___

/**
@file powerup.h
Funções que manipulam os power ups.
*/

#include "estado.h"


void elimina_pup_mapa(ESTADO *e, int tipo_powerup);

void elimina_pup_inventario(ESTADO *e, int tipo_powerup);

int tipo_powerup(ESTADO *e, int x, int y);

int esta_mapa(ESTADO *e, int tipo_powerup);

int todos_mapa_exceto_raio(ESTADO *e);

int esta_inventario(ESTADO *e, int tipo_powerup);

int qual_powerup(ESTADO *e, int janela);

void encontrou_powerup(ESTADO *e, int tipo_powerup);

void organiza_inventario(ESTADO *e);

void pup_no_inventario(ESTADO *e, int tipo_powerup);

void imprime_tronco(char user[], int x, int y);

void tronco(ESTADO *e, int dx, int dy);

void powerup_tronco(ESTADO *e, int dif_x, int dif_y);

void imprime_martelo(char user[], int x, int y);

void powerup_martelo(ESTADO *e, int x, int y);

void powerup_bomba(ESTADO *e);

void powerup_escudo(ESTADO *e);

void powerup_gelo(ESTADO *e);

void powerup_raio(ESTADO *e);

void powerup_move(ESTADO *e);

void powerup_vida(ESTADO *e);

#endif
