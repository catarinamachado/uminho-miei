#include<string.h>
#include<stdio.h>
#include<stdlib.h>
#include<assert.h>

#include "estado.h"
#include "monstros.h"
#include "movimento.h"
#include "powerup.h"
#include "ataques.h"

/**
@file monstros.c
Funções relativas ao movimento e ataques dos monstros.
*/

/**
\brief Função local a este módulo que retorna uma posição livre contígua à
posição (x,y) passada como parâmetro da função.
@param e Estado atual do jogo.
@param x Coordenada do eixo das abcissas atual.
@param inimigo Coordenada do eixo das ordenadas atual.
@param x_final Retorna a coordenada do eixo das abcissas livre ou, caso não
encontre nenhuma, retorna a mesma.
@param Y_final Retorna a coordenada do eixo das ordenadas livre ou, caso não
encontre nenhuma,retorna a mesma.
*/
static void posicao_livre(ESTADO *e, int x, int y, int * x_final, int * y_final) {
  int direcao_x, direcao_y;
  for(direcao_x = -1; direcao_x < 2; direcao_x++) {
    for(direcao_y = -1; direcao_y < 2; direcao_y++) {
       if (!posicao_ocupada(*e, (x + direcao_x), (y + direcao_y))) {
         assert(direcao_x != 0 || direcao_y != 0);
         *x_final = x + direcao_x;
         *y_final = y + direcao_y;
         return;
       }
     }
   }
  *x_final = x;
  *y_final = y;
}


/**
\brief Função local a este módulo que calcula as posições do tabuleiro para onde
um monstro, especificado pelo tipo e pelo número(inimigo), se pode mover.
@param e Estado atual do jogo.
@param tipo Identifica o tipo de inimigo - ESQUELETO, OGRE ou BRUXA.
@param inimigo Número do inimigo no jogo.
@param x_final Coordenada do eixo das abcissas para onde o monstro se quer dirigir.
@param Y_final Coordenada do eixo das ordenadas onde o monstro se quer dirigir.
*/
static void calcula_movimento(ESTADO *e, int tipo, int inimigo, int x_final, int y_final) {
  int x_monstro, y_monstro;
  LISTA_CAMINHO *lista;
	int futuro_x = 0, futuro_y = 0;

  x_monstro = e->inimigo[tipo][inimigo].pos.x;
  y_monstro = e->inimigo[tipo][inimigo].pos.y;

  determinar_caminhos(e, x_monstro, y_monstro);
  lista = retorna_caminho(x_final, y_final);

  if (lista->prox == NULL) {
    apaga_caminho(lista);
    return;
  }

  futuro_x = lista->prox->nodo.atual.x;
  futuro_y = lista->prox->nodo.atual.y;
  apaga_caminho(lista);

  /*se a posição para onde quer ir está ocupada procura uma livre */
  if(posicao_ocupada(*e, futuro_x, futuro_y)) {
    posicao_livre(e, x_monstro, y_monstro, &futuro_x, &futuro_y);
  }

  e->inimigo[tipo][inimigo].pos.x = futuro_x;
  e->inimigo[tipo][inimigo].pos.y = futuro_y;
}

/**
\brief Função local a este módulo que atualiza a informação sobre os ataques
sofridos pelos jogador, como, por exemplo, quantas vidas lhe são retiradas
e quem foi o autor do ataque.
@param e Estado atual do jogo.
@param tipo Identifica o tipo de inimigo.
@param ataques Número de ataques sofridos.
*/
static void atacou_jogador(ESTADO *e, int tipo, int ataques) {
   if (e->powerup[POWERUP_ESCUDO][NO_INVENTARIO].jog_rest == 0)
     e->jog.n_ataques += ataques;
   if ((e->powerup[POWERUP_GELO][NO_INVENTARIO].jog_rest == 0)
      && (e->powerup[POWERUP_ESCUDO][NO_INVENTARIO].jog_rest == 0)) {
        if (tipo == BRUXA)
          e->jog.bruxa = 1;
        else if (tipo == OGRE)
          e->jog.ogre = 1;
        else if (tipo == ESQUELETO)
          e->jog.esqueleto = 1;
  }
}

/**
\brief Função local a este módulo que calcula as posições do tabuleiro para onde
o esqueleto se move, caso não tenha tirado uma vida ao jogador, pois caso isso
aconteça só se move no instante seguinte.
@param e Estado atual do jogo.
@param tipo Identifica o tipo de inimigo.
@param inimigo Número do inimigo.
*/
static void movimento_esqueleto(ESTADO *e, int tipo, int inimigo) {
  int x_final = e->jog.pos.x;
  int y_final = e->jog.pos.y;
  int x = e->inimigo[tipo][inimigo].pos.x;
  int y = e->inimigo[tipo][inimigo].pos.y;

  if ((x == x_final - 1 && y == y_final) || (x == x_final + 1 && y == y_final) ||
      (y == y_final - 1 && x == x_final) || (y == y_final + 1 && x == x_final)) {
    atacou_jogador(e, ESQUELETO, 1);
  } else {
    calcula_movimento(e, tipo, inimigo, x_final, y_final);
  }
}

/**
\brief Função local a este módulo que determina se a bruxa pode atacar ou não,
visto que ela só pode atacar o jogador de três em três jogadas.
@param e Estado atual do jogo.
@returns int que representa um valor boleano - 1 se for verdadeiro e 0 caso
contrário.
*/
static int ataque_bruxa(ESTADO *e) {
  return ((e->num_jogadas % 3) == 0);
}


/**
\brief Função local a este módulo que calcula as posições do tabuleiro para onde
a bruxa se move, caso não tenha tirado uma vida ao jogador, pois caso isso
aconteça só se move no instante seguinte.
@param e Estado atual do jogo.
@param tipo Identifica o tipo de inimigo.
@param inimigo Número do inimigo.
*/
static void movimento_bruxa(ESTADO *e, int tipo, int inimigo) {
  int x = e->inimigo[tipo][inimigo].pos.x;
  int y = e->inimigo[tipo][inimigo].pos.y;
  int raio, numero_ataques = e->jog.n_ataques;

  if (ataque_bruxa(e)) {
    for (raio = 1; raio < 3; raio++) {
      if (pos_ocupada_excpt_jog(*e, x, y - raio))
        break;
      else if (x == e->jog.pos.x && y - raio == e->jog.pos.y) {
        atacou_jogador(e, BRUXA, 2);
      }
    }
    for (raio = 1; raio < 3; raio++) {
      if (pos_ocupada_excpt_jog(*e, x, y + raio))
        break;
      else if (x == e->jog.pos.x && y + raio == e->jog.pos.y){
        atacou_jogador(e, BRUXA, 2);
      }
    }
    for (raio = 1; raio < 3; raio++) {
      if (pos_ocupada_excpt_jog(*e, x - raio, y))
        break;
      else if (x - raio == e->jog.pos.x && y == e->jog.pos.y){
        atacou_jogador(e, BRUXA, 2);
      }
    }
    for (raio = 1; raio < 3; raio++) {
      if (pos_ocupada_excpt_jog(*e, x + raio, y))
        break;
      else if (x + raio == e->jog.pos.x && y == e->jog.pos.y){
        atacou_jogador(e, BRUXA, 2);
      }
    }
    for (raio = 1; raio < 3; raio++) {
      if (pos_ocupada_excpt_jog(*e, x - raio, y - raio))
        break;
      else if (x - raio == e->jog.pos.x && y - raio == e->jog.pos.y){
        atacou_jogador(e, BRUXA, 2);
      }
    }
    for (raio = 1; raio < 3; raio++) {
      if (pos_ocupada_excpt_jog(*e, x - raio, y + raio))
        break;
      else if (x - raio == e->jog.pos.x && y + raio == e->jog.pos.y){
        atacou_jogador(e, BRUXA, 2);
      }
    }
    for (raio = 1; raio < 3; raio++) {
      if (pos_ocupada_excpt_jog(*e, x + raio, y - raio))
        break;
      else if (x + raio == e->jog.pos.x && y - raio == e->jog.pos.y){
        atacou_jogador(e, BRUXA, 2);
      }
    }
    for (raio = 1; raio < 3; raio++) {
      if (pos_ocupada_excpt_jog(*e, x + raio, y + raio))
        break;
      else if (x + raio == e->jog.pos.x && y + raio == e->jog.pos.y){
        atacou_jogador(e, BRUXA, 2);
      }
    }
  }

  /* se a bruxa não atacou o jogador ela move-se, senão fica na mesma posição.*/
  if (numero_ataques == e->jog.n_ataques)
    calcula_movimento(e, tipo, inimigo, e->porta.x, e->porta.y);
}


/**
\brief Função local a este modulo que determina o movimento do ogre,
caso não tenha tirado uma vida ao jogador, senão só se move no instante seguinte.
@param e Estado atual do jogo.
@param tipo Identifica o tipo de inimigo.
@param inimigo Número do inimigo.
*/
static void movimento_ogre(ESTADO *e, int tipo, int inimigo) {
  int jog_x = e->jog.pos.x;
  int jog_y = e->jog.pos.y;
  int x = e->inimigo[tipo][inimigo].pos.x;
  int y = e->inimigo[tipo][inimigo].pos.y;
  int raio, numero_ataques = e->jog.n_ataques;

  for (raio = 1; raio < 4; raio++) {
    if (pos_ocupada_excpt_jog(*e, x - raio, y - raio))
      break;
    else if ((x - raio) == jog_x && (y - raio) == jog_y){
        atacou_jogador(e, OGRE, 2);
      }
  }
  for (raio = 1; raio < 4; raio++) {
    if (pos_ocupada_excpt_jog(*e, x - raio, y + raio))
      break;
    else if ((x - raio) == jog_x && (y + raio) == jog_y){
        atacou_jogador(e, OGRE, 2);
      }
  }
  for (raio = 1; raio < 4; raio++) {
    if (pos_ocupada_excpt_jog(*e, x + raio, y - raio))
      break;
    else if ((x + raio) == jog_x && (y - raio) == jog_y){
        atacou_jogador(e, OGRE, 2);
      }
  }
  for (raio = 1; raio < 4; raio++) {
    if (pos_ocupada_excpt_jog(*e, x + raio, y + raio))
      break;
    else if ((x + raio) == jog_x && (y + raio) == jog_y){
        atacou_jogador(e, OGRE, 2);
      }
  }
  /* se o ogre não atacou o jogador move-se, senão fica na mesma posição.*/
  if (e->jog.n_ataques == numero_ataques)
    calcula_movimento(e, tipo, inimigo, jog_x, jog_y);
}

/**
\brief Cria um tipo que é um apontador para funções que recebem
os parametros infra descritos.
@param e Estado atual do jogo.
@param tipo Identifica o tipo de inimigo em causa.
@param inimigo Número do inimigo.
*/
typedef void (*funcao_movimento)(ESTADO *e, int tipo, int inimigo);

/**
\brief Cria um vetor das funções que recebem como parametros o Estado e dois
inteiros.
*/
funcao_movimento movimento_monstros[] = {movimento_ogre, movimento_bruxa, movimento_esqueleto};


/**
\brief Função que move todos os monstros do jogo.
@param e Estado atual do jogo.
*/
void move_monstros(ESTADO *e) {
  int num_inim, tipo;

  for (tipo = 0; tipo < TIPO_INIMIGO; tipo++) {
    for (num_inim = 0; num_inim < e->num_inimigos[tipo]; num_inim++) {
      (*movimento_monstros[tipo])(e, tipo, num_inim);
    }
  }
}

/* -------------------------------------------------------------------------- */

/**
\brief Descobre qual é o número que identifica o inimigo.
@param e Estado atual do jogo.
@param tipo Identifica o tipo de inimigo.
@param x coordenada do inimigo no eixo das abcissas.
@param y coordenada do inimigo no eixo das ordenadas.
@returns o número que identifica o inimigo.
*/
int num_inimigo(ESTADO *e, int tipo, int x, int y){
    int i;

    for(i = 0 ; i < e->num_inimigos[tipo] ; i++)
        if (e->inimigo[tipo][i].pos.x == x && e->inimigo[tipo][i].pos.y == y)
            return i;

    return i;
}


/**
\brief Elimina o vetor com as informaçoes do inimigo em questao do ESTADO.
@param e Estado atual do jogo.
@param tipo Identifica o tipo de inimigo.
@param x coordenada do inimigo no eixo das abcissas.
@param y coordenada do inimigo no eixo das ordenadas.
*/
void elimina_inimigo(ESTADO *e, int tipo, int x, int y){
    int i, j;

    i = num_inimigo(e, tipo, x, y);

    e->inimigo[tipo][i].pos.x = -1;
	  e->inimigo[tipo][i].pos.y = -1;

    for (j = i + 1 ; j < e->num_inimigos[tipo] ; j++)
        e->inimigo[tipo][j-1] = e->inimigo[tipo][j];

    e->num_inimigos[tipo]--;
}


/**
\brief Mata o inimigo selecionado pelo jogador.
@param e Estado atual do jogo.
@param tipo Identifica o tipo de inimigo.
@param x Coordenada do eixo das abcissas da posição do inimigo.
@param y Coordenada do eixo das ordenadas da posição do inimigo.
*/
void mata_inimigo(ESTADO *e, int tipo, int x, int y){

  elimina_inimigo(e, tipo, x, y);

  e->inimigos_mortos++;

  if (tipo == ESQUELETO) e->score += 5;
  else if (tipo == OGRE) e->score += 10;
  else if (tipo == BRUXA) e->score += 15;

}

/**
\brief Averigua se o inimigo atingido já tinha sofrido algum ataque.
Retorna 1 em caso afirmativo. 0, se foi o seu primeiro ataque.
@param e Estado atual do jogo.
@param tipo Identifica o tipo de inimigo.
@param x Coordenada do eixo das abcissas da posição do inimigo.
@param y Coordenada do eixo das ordenadas da posição do inimigo.
@returns retorna 1 se o inimigo já tiver sofrido um ataque e 0 caso contrário.
*/
int dano_inimigo(ESTADO *e, int tipo, int x, int y){
  int n;

  n = num_inimigo(e, tipo, x, y);

  if (e->inimigo[tipo][n].n_ataques == 0) return 0;
  else return 1;
}

/**
\brief Aumenta o dano do inimigo atingido.
@param e Estado atual do jogo.
@param tipo Identifica o tipo de inimigo.
@param x Coordenada do eixo das abcissas da posição do inimigo.
@param y Coordenada do eixo das ordenadas da posição do inimigo.
*/
void aumenta_dano(ESTADO *e, int tipo, int x, int y){
    int n;

    n = num_inimigo(e, tipo, x, y);

    e->inimigo[tipo][n].n_ataques = 1;
}

/* -------------------------------------------------------------------------- */

/**
\brief Função que determina o alcance do ataque do monstro esqueleto.
@param e Estado atual do jogo.
@param x Coordenada do eixo das abcissas da posição do esqueleto
@param y Coordenada do eixo das ordenadas da posição do esqueleto.
@param m Retorna as posições sob ataque do esqueleto.
@param n_pos Retorna o número de posições sob ataque do esqueleto.
*/
void calcula_ataque_esqueleto(ESTADO *e, int x, int y, POSICAO *m, int *n_pos) {
   assert(*n_pos == 0);

   if (!pos_impede_ataque(*e, x - 1, y)){
     m[*n_pos].x = x - 1;
     m[*n_pos].y = y;
     (*n_pos)++;
  }
  if (!pos_impede_ataque(*e, x + 1, y)){
    m[*n_pos].x = x + 1;
    m[*n_pos].y = y;
    (*n_pos)++;
  }
  if (!pos_impede_ataque(*e, x, y - 1)){
     m[*n_pos].x = x;
     m[*n_pos].y = y - 1;
     (*n_pos)++;
  }
  if (!pos_impede_ataque(*e, x, y + 1)){
    m[*n_pos].x = x;
    m[*n_pos].y = y + 1;
    (*n_pos)++;
  }
}

/**
\brief Função que determina o alcance do ataque do monstro ogre.
@param e Estado atual do jogo.
@param x Coordenada do eixo das abcissas da posição do ogre.
@param y Coordenada do eixo das ordenadas da posição do ogre.
@param m Retorna as posições sob ataque do ogre.
@param n_pos Retorna o número de posições sob ataque do ogre.
*/
void calcula_ataque_ogre(ESTADO *e, int x, int y, POSICAO *m, int *n_pos) {
   int raio;
   assert(*n_pos == 0);

   if (!dano_inimigo(e, OGRE, x, y)) {
     m[*n_pos].x = x;
     m[*n_pos].y = y;
     (*n_pos)++;
   }

   /* Direção Noroeste */
   for (raio = 1; raio < 4; raio++) {
     if(!pos_impede_ataque(*e, x - raio, y - raio)) {
       m[*n_pos].x = x - raio;
       m[*n_pos].y = y - raio;
       (*n_pos)++;

       if(tem_powerups(*e, x - raio, y - raio) || tem_inimigos(*e, x - raio, y - raio))
         break;
     }
     else break;
   }
   /* Direcão Sudoeste */
   for (raio = 1; raio < 4; raio++) {
     if(!pos_impede_ataque(*e, x - raio, y + raio)) {
       m[*n_pos].x = x - raio;
       m[*n_pos].y = y + raio;
       (*n_pos)++;

       if(tem_powerups(*e, x - raio, y + raio) || tem_inimigos(*e, x - raio, y + raio))
         break;
     }
     else break;
   }

   /* Direção Nordeste */
   for (raio = 1; raio < 4; raio++) {
     if(!pos_impede_ataque(*e, x + raio, y - raio)) {
       m[*n_pos].x = x + raio;
       m[*n_pos].y = y - raio;
       (*n_pos)++;

       if(tem_powerups(*e, x + raio, y - raio) || tem_inimigos(*e, x + raio, y - raio))
         break;
     }
     else break;
   }

   /* Direção Sudeste */
   for (raio = 1; raio < 4; raio++) {
     if(!pos_impede_ataque(*e, x + raio, y + raio)) {
       m[*n_pos].x = x + raio;
       m[*n_pos].y = y + raio;
       (*n_pos)++;

       if(tem_powerups(*e, x + raio, y + raio) || tem_inimigos(*e, x + raio, y + raio))
         break;
     }
     else break;
  }
}

/**
\brief Função que determina o alcance do ataque do monstro bruxa.
@param e Estado atual do jogo.
@param x Coordenada do eixo das abcissas da posição da bruxa.
@param y Coordenada do eixo das ordenadas da posição da bruxa.
@param m Retorna as posições sob ataque da bruxa.
@param n_pos Retorna o número de posições sob ataque da bruxa.
*/
void calcula_ataque_bruxa(ESTADO *e, int x, int y, POSICAO *m, int *n_pos) {
   int raio;
   assert(*n_pos == 0);

   if (!ataque_bruxa(e)) return;

   if (!dano_inimigo(e, BRUXA, x, y)) {
     m[*n_pos].x = x;
     m[*n_pos].y = y;
     (*n_pos)++;
   }

   for (raio = 1; raio < 3; raio++) {
     if (!pos_impede_ataque(*e, x, y - raio)) {
       m[*n_pos].x = x;
       m[*n_pos].y = y - raio;
       (*n_pos)++;

       if(tem_powerups(*e, x, y - raio) || tem_inimigos(*e, x, y - raio))
         break;
     }
     else break;
   }
   for (raio = 1; raio < 3; raio++) {
     if (!pos_impede_ataque(*e, x, y + raio)) {
       m[*n_pos].x = x;
       m[*n_pos].y = y + raio;
       (*n_pos)++;

       if(tem_powerups(*e, x, y + raio) || tem_inimigos(*e, x, y + raio))
         break;
     }
     else break;
   }
   for (raio = 1; raio < 3; raio++) {
     if (!pos_impede_ataque(*e, x - raio, y)) {
       m[*n_pos].x = x - raio;
       m[*n_pos].y = y;
       (*n_pos)++;

       if(tem_powerups(*e, x - raio, y) || tem_inimigos(*e, x - raio, y))
         break;
     }
     else break;
   }
   for (raio = 1; raio < 3; raio++) {
     if (!pos_impede_ataque(*e, x + raio, y)) {
       m[*n_pos].x = x + raio;
       m[*n_pos].y = y;
       (*n_pos)++;

       if(tem_powerups(*e, x + raio, y) || tem_inimigos(*e, x + raio, y))
         break;
     }
     else break;
   }
   for (raio = 1; raio < 3; raio++) {
     if (!pos_impede_ataque(*e, x - raio, y - raio)) {
       m[*n_pos].x = x - raio;
       m[*n_pos].y = y - raio;
       (*n_pos)++;

       if(tem_powerups(*e, x - raio, y - raio) || tem_inimigos(*e, x - raio, y - raio))
         break;
     }
     else break;
   }
   for (raio = 1; raio < 3; raio++) {
     if (!pos_impede_ataque(*e, x - raio, y + raio)) {
       m[*n_pos].x = x - raio;
       m[*n_pos].y = y + raio;
       (*n_pos)++;

       if(tem_powerups(*e, x - raio, y + raio) || tem_inimigos(*e, x - raio, y + raio))
         break;
     }
     else break;
   }
   for (raio = 1; raio < 3; raio++) {
     if (!pos_impede_ataque(*e, x + raio, y - raio)) {
       m[*n_pos].x = x + raio;
       m[*n_pos].y = y - raio;
       (*n_pos)++;

       if(tem_powerups(*e, x + raio, y - raio) || tem_inimigos(*e, x + raio, y - raio))
         break;
     }
     else break;
   }
   for (raio = 1; raio < 3; raio++) {
     if (!pos_impede_ataque(*e, x + raio, y + raio)) {
       m[*n_pos].x = x + raio;
       m[*n_pos].y = y + raio;
       (*n_pos)++;

       if(tem_powerups(*e, x + raio, y + raio) || tem_inimigos(*e, x + raio, y + raio))
         break;
     }
     else break;
   }
}

/**
\brief Cria um vetor de funções.
*/
funcao_ataque_monstro ataques_monstro[] =
   {calcula_ataque_ogre, calcula_ataque_bruxa, calcula_ataque_esqueleto};

/**
\brief Função que determina o alcance do ataque do conjunto de todos os monstros
em jogo.
@param e Estado atual do jogo.
@param pos_ataque Retorna as posições sob ataque de todos os monstros.
@param n_pos_ataque Retorna o número de posições sob ataque de todos monstros.
*/
void calcula_ataque_monstros(ESTADO *e, POSICAO *pos_ataque, int *n_pos_ataque) {
  int inimigo, tipo, coord_x, coord_y;
  POSICAO *ptr_pos_ataque = pos_ataque;
  int ptr_n_pos_ataque;
  assert(*n_pos_ataque == 0);

  for (tipo = 0; tipo < TIPO_INIMIGO; tipo++) {
    for (inimigo = 0; inimigo < e->num_inimigos[tipo]; inimigo++) {
      coord_x = e->inimigo[tipo][inimigo].pos.x;
      coord_y = e->inimigo[tipo][inimigo].pos.y;
      ptr_n_pos_ataque = 0;
      ataques_monstro[tipo](e, coord_x, coord_y, ptr_pos_ataque, &ptr_n_pos_ataque);
      ptr_pos_ataque += ptr_n_pos_ataque;
      *n_pos_ataque += ptr_n_pos_ataque;
    }
  }
}


/**
\brief Função que determina as coordenadas de um monstro que
ataca certa posição.
@param e Estado atual do jogo.
@param x coordenada do eixo das abcissas.
@param y coordenada do eixo das ordenadas.
@param monstro_x Retorna a coordenada do eixo das abcissas da posição do monstro.
@param monstro_y Retorna a coordenada do eixo das ordenadas da posição do monstro.
*/
void coord_monstro_atacante(ESTADO *e, int x, int y, int *monstro_x, int *monstro_y) {
  int inimigo, tipo, coord_x, coord_y;
  POSICAO pos_ataques[16 * TIPO_INIMIGO * MAX_INIMIGOS];
	int n_ataques = 0;

  for (tipo = 0; tipo < TIPO_INIMIGO; tipo++) {
    for (inimigo = 0; inimigo < e->num_inimigos[tipo]; inimigo++) {
      coord_x = e->inimigo[tipo][inimigo].pos.x;
      coord_y = e->inimigo[tipo][inimigo].pos.y;
      n_ataques = 0;
      ataques_monstro[tipo](e, coord_x, coord_y, pos_ataques, &n_ataques);
      if (posicao_sob_ataque(x, y, pos_ataques, n_ataques)) {
        *monstro_x = coord_x;
        *monstro_y = coord_y;
        return;
      }
    }
  }
}

/**
\brief Verifica se a posição (x, y) está sob ataque de um monstro.
@param x Coordenada do eixo das abcissas de determinada posição no jogo.
@param y Coordenada do eixo das ordenadas de determinada posição no jogo.
@param pos_ataques Certa posição do jogo.
@param n_ataques Número total de ataques dos monstros.
@returns int que representa um boleano - se a posição está sob ataque de um
monstro retorna 1, senão retorna 0.
*/
int posicao_sob_ataque(int x, int y, POSICAO *pos_ataques, int n_ataques) {
  int pos;

  for (pos = 0; pos < n_ataques; pos++) {
	  if (x == pos_ataques[pos].x && y == pos_ataques[pos].y) {
		  return 1;
	  }
	}
	return 0;
}
