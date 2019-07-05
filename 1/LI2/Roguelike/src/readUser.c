#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#include "readUser.h"

/**
@file readUser.c
Procedimento que lê de um ficheiro o nome de utilizador do jogador atual.
*/

/**
\brief Procedimento que lê de um ficheiro o nome de utilizador do jogador atual.
       @param eUser - string na qual se vai armazenar o nome de utilizador
*/
void ler_user (char eUser[]) {
  FILE * fP;
  char *ret;

  strcpy(eUser, "unknown");

  fP = fopen("/var/www/html/users/username.txt", "r");

  if ( fP != NULL) {
    ret = fgets(eUser, 32, fP);
    fclose(fP);
  }

  eUser = strtok(eUser,"\n");

  (void) ret;

  return;
}
