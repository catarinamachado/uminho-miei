#ifndef __ATAQUES_H__
#define __ATAQUES_H__

#include "estado.h"

/**
@file ataques.h
Funções que manipulam os recursos.
*/

int tipo_ini(ESTADO *e, int x, int y);

int num_obstaculo(ESTADO *e, int x, int y);

void destroi_obstaculo(ESTADO *e, int x, int y);

void afeta(ESTADO *e, int x, int y);

void explode(ESTADO *e, int x, int y);

void granada_missil(ESTADO * ep, char * acao, char * bot_acao, int x, int y);

void imprime_casa_ataque(int x, int y, char user[], char tipo[]);

void imprime_missil(char user[], int x, int y);

void imprime_granada(int x, int y, char user[]);

#endif
