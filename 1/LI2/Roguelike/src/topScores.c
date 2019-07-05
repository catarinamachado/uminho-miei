#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "topScores.h"
#include "apresentacao.h"

/**
@file topScores.c
Funções que atualizam, guardam e imprimem as melhores pontuações alcançadas.
*/

/**
\brief Função que lê de um ficheiro o melhor score alcançado.
       @returns i - inteiro com o valor do melhor score
*/
int readScore () {

  FILE * fPointer;
  int i = 0, ret;

  fPointer = fopen("/var/www/html/users/score.txt", "r");

  if (fPointer != NULL) {
    ret = fscanf (fPointer, "%d", &i);
    fclose(fPointer);
  }
  (void) ret;

  return i;
}

/**
\brief Procedimento que imprime num ficheiro um novo top score.
       @param score - valor da pontuação atual
*/
void writeScore (int score) {

  FILE * fPointer;

  fPointer = fopen("/var/www/html/users/score.txt", "wb");

  if (fPointer == NULL) {
    perror("null fp");
    exit(1);
  }

  fprintf(fPointer, "%d", score);

  fclose(fPointer);

  return;
}



/*--------------------------*/

/**
\brief Procedimento que imprime num ficheiro html, para ser apresentado ao utilizador final,
uma tabela com 10 melhores scores e respetivos usernames.
       @param s - estrutura com as informações a imprimir
*/
void print_HTML (SCORE s) {
  FILE * fP_HTML;
  int i;

  fP_HTML = fopen("/var/www/html/topscores.html", "w");

  if (fP_HTML == NULL) {
    perror("null fp html page");
    exit(1);
  }

  fprintf(fP_HTML, "<!DOCTYPE html>\n\n<html>\n\n");
  fprintf(fP_HTML, "  <head>\n<title>Top Scores</title>\n\n");
  fprintf(fP_HTML, "     <style>\n");
  fprintf(fP_HTML, "          body {\n");
  fprintf(fP_HTML, "               background-image: url(\"http://localhost/images/fundo.png\");\n");
  fprintf(fP_HTML, "               background-repeat:no-repeat;\n");
  fprintf(fP_HTML, "               background-size:cover;\n");
  fprintf(fP_HTML, "          }\n");
  fprintf(fP_HTML, ".action-button{\n    padding: 15px 40px;border-radius: 10px;font-family: 'Pacifico', cursive;font-size: 35px;color: #FFF;text-decoration: none;\n}\n");
  fprintf(fP_HTML, ".red{background-color: #E74C3C;border-bottom: 5px solid #BD3E31;text-shadow: 0px -2px #BD3E31;}\n");
  fprintf(fP_HTML, " </style>\n  </head>\n\n");

  fprintf(fP_HTML, "    <body></br></br></br></br></br></br></br>\n\n      <table width=\"400\" align=center border=1 style=\"background-color:#e6ffff\">\n\n");

  for (i = 0; i < NUM_DATA; i++)
    fprintf(fP_HTML,"      <tr><td align=\"center\">%d</td><td align=\"center\">%s</td><td align=\"center\">%d Pontos</td></tr>\n\n", i+1, s.user[i], s.scores[i]);

  fprintf(fP_HTML, " </table>\n\n");
  fprintf(fP_HTML, " </br></br>\n");
  fprintf(fP_HTML, " <table width=\"400\" align=center>\n\n");
  fprintf(fP_HTML, "    <tr><td align=\"center\"><a href=\"http://localhost/cgi-bin/user\" class=\"action-button red\">Restart!</a></td></tr>\n");
  fprintf(fP_HTML, " </table>\n");
  fprintf(fP_HTML, "</body> </html>");

  fclose(fP_HTML);

  return;
}

/**
\brief Procedimento que imprime para um ficheiros as 10 melhores pontuações e respetivos utilizadores.
       @param s - estrutura com as informações a imprimir
*/
void guarda(SCORE s) {

  FILE * fPointer;
  int i;

  fPointer = fopen("/var/www/html/users/topscores.txt", "w");

  if (fPointer == NULL) {
    perror("null fp guarda topscores txt tmp");
    exit(1);
  }

  for (i = 0; i < NUM_DATA; i++)
    fprintf(fPointer, "%s %d\n", s.user[i], s.scores[i]);

  fclose(fPointer);

  return;
}

/**
\brief Função que insere ordenadamente numa estrutura SCORES a pontuação atual e respetivo utilizador.
       @param current_score - valor da pontuação atual
       @param name - nome de utilizador do jogador atual
       @param s - estrutura a ser atualizada
       @returns estrutura score com os novos dados atualizados
*/
SCORE insert(int current_score, char name[], SCORE s) {
  int i, tmp_score;
  char tmp_string[32];

  for (i = 0; i < NUM_DATA-1; i++)
    if (current_score > s.scores[i]) {

      tmp_score     = s.scores[i];       strcpy(tmp_string, s.user[i]);
      s.scores[i]   = current_score;     strcpy(s.user[i], name);
      current_score = tmp_score;         strcpy(name, tmp_string);

    }

  if (i == NUM_DATA-1 && current_score > s.scores[i]) {
    s.scores[i] = current_score;         strcpy(s.user[i], name);
  }

  return s;
}

/**
\brief Função que lê de um ficheiro os 10 melhores scores e respetivos usernames para uma estrutura.
       @param s - estrutura a ser atualizada
       @returns estrutura score com os novos dados atualizados
*/
SCORE atualiza (SCORE s) {
  int i = 0;
  char line[50];
  char * ret;
  FILE * fP;

  fP = fopen ("/var/www/html/users/topscores.txt", "r");

  if (fP == NULL) {
    perror("topscore txt tmp null atualiza");
    exit(1);
  }

  while(i < NUM_DATA) {
    ret = fgets (line, 50, fP);
    sscanf (line, "%s %d", s.user[i] ,&s.scores[i]);
    i++;
    }

  fclose(fP);


  (void) ret;
  return s;
}

/**
\brief Procedimento que atualiza o ficheiro dos 10 melhores scores.
       @param current_score - valor da pontuação atual
       @param name - nome do utilizador atual
*/
void topScores (int current_score, char name[]) {
  int i;
  SCORE s;
  char * ret;
  FILE * fP;
  char line[30];

  fP = fopen ("/var/www/html/users/username.txt", "r");

  if (fP == NULL) strcpy(name, "unknown");

  else {
    ret = fgets (line, 30, fP);
    sscanf (line, "%s", name);
  }


  for (i = 0; i < NUM_DATA; i++) {
    strcpy(s.user[i], "none");
    s.scores[i] = 0;
  }

  s = atualiza(s);
  s = insert(current_score, name, s);
  guarda(s);
  print_HTML(s);

  remove("/var/www/html/users/username.txt");

  (void) ret;
  return;
}
