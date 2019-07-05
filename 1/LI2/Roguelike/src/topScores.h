#ifndef __TOPSCORES_H__
#define __TOPSCORES_H__


/**
@file topScores.h
Funções que atualizam, guardam e imprimem as melhores pontuações alcançadas.
*/

/**
\brief Número máximo de pontuações armazenadas
*/
#define NUM_DATA  10

/**
\brief Estrutura que armazena as melhores pontuações de sempre e respetivos utilizadores.
*/
typedef struct SCORE {
   /** \brief Array de strings com os utilizadores nas mesmas posições das respetivas pontuações*/
   char user[NUM_DATA][32];
   /** \brief Array com os melhores score ordenados */
   int scores[NUM_DATA];
} SCORE;


int readScore ();


void writeScore (int score);


void print_HTML (SCORE s);


void guarda (SCORE s);


SCORE insert(int current_score, char name[], SCORE s);


SCORE atualiza (SCORE s);


/**
\brief Procedimento que atualiza o ficheiro dos 10 melhores scores.
*/
void topScores (int current_score, char name[]);

#endif
