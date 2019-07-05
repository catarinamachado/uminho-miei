#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "cgis.h"

/**
@file user.c
Recebe através de um formulário HTML um nome de utilizador obtido pelo jogador e imprime-o para um ficheiro.
*/

/**
\brief Imprime num ficheiro o nome de utilizador escolhido pelo jogador.
       @param user - nome de utilizador
*/
void guarda(char user[]) {

  FILE * fP;

  fP = fopen("/var/www/html/users/username.txt", "w+");
  fprintf(fP, "%s\n", user);
  fclose(fP);

  return;
}

/**
\brief Retira da string argumento tags criada pelo HTML na QUERY_STRING.
       @param user - nome de utilizador
*/
void retira_lixo(char user[]) {
  int i ;

  while ( user[0] != '=' && user[0] != 0 )
    for (i = 0; user[i] !='&'; i++)
      user[i] = user[i+1];

  for (i = 0; user[i] != '&' && user[i] != 0; i++) {
    user[i] = user[i+1];
  }

  user[i-1] = '\0';

  return;
}

/**
\brief Verifica se foi introduzido um nome de utilizador válido.
       @param args - QUERY_STRING obtida
       @returns 1 se foi um nome de utilizador válido, 0 caso contrário.
*/
int verifica(char * args) {
  int r;

  if ( args == NULL || strlen(args) > sizeof(char) * 30 || strlen(args) < 1 )
  	r = 0;
  else
    r = 1;

  return r;

}

/**
\brief Procedimento que remove ficheiro com o estado do utilizador.
       @param name - nome do utilizador atual
*/
void delete_user(char name[]) {
  int ret;
  char file[60] = "/var/www/html/users/";

	strcat(file,name);

  ret = remove(file);

  (void) ret;

  return;
}

/**
\brief Recebe através de um formulário HTML um nome de utilizador obtido pelo jogador e imprime-o para um ficheiro.
       @returns 0 se o programa for executado com sucesso.
*/
int main() {
  char * str;
  char user[32];

  if ( verifica(getenv("QUERY_STRING")) == 1 ) {
    str = getenv("QUERY_STRING");
    strcpy(user, str);
    retira_lixo(user); /* da QUERY_STRING "first=user&submit=" apenas guarda o "user"*/
    guarda(user);
    PRINT_START;
  }
  else {
    PRINT_GET;
    guarda(user);
  }

  guarda(user);

  delete_user(user);

	return 0;
}
