#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "powerup.h"
#include "ataques.h"
#include "apresentacao.h"
#include "cgi.h"
#include "estado.h"
#include "monstros.h"


/**
@file powerup.c
Funções que manipulam os power ups.
*/

/**
\brief Procedimento que elimina do estado o vetor relativo às informações do
power up no mapa, para assim o power up desaparecer do mesmo.
@param e Estado do jogo.
@param tipo_powerup Indica qual o tipo de power up.
*/
void elimina_pup_mapa(ESTADO *e, int tipo_powerup){
    e->powerup[tipo_powerup][NO_MAPA] = e->powerup[tipo_powerup][NO_INVENTARIO];
}


/**
\brief Procedimento que elimina do estado o vetor relativo às informações do
power up do inventário, para assim este desaparecer do mesmo.
@param e Estado do jogo.
@param tipo_powerup Indica qual o tipo de power up.
*/
void elimina_pup_inventario(ESTADO *e, int tipo_powerup){
    e->powerup[tipo_powerup][NO_INVENTARIO].janela = 0;
}


/**
\brief Função que indica qual dos power up está numa determinada posição no mapa.
@param e Estado do jogo.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordenadas.
@returns int Tipo do power up que está em (x, y).
*/
int tipo_powerup(ESTADO *e, int x, int y){
    int t;

    for (t = 0; t < MAX_POWERUPS; t++){
        if (posicao_igual(e->powerup[t][NO_MAPA].pos, x, y))
            return t;
    }

    return 0;
}


/**
\brief Função que averigua se um determinado power up está no mapa.
@param e Estado do jogo.
@param tipo_powerup Indica qual o tipo de power up.
@returns int 1 se o power up estiver no mapa, 0 caso contrário.
*/
int esta_mapa(ESTADO *e, int tipo_powerup){
    return (!(posicao_igual(e->powerup[tipo_powerup][NO_MAPA].pos, 0, 0)));
}


/**
\brief Função averigua se todos os power ups estão no mapa,
sem contar com o power up raio.
@param e Estado do jogo.
@returns int 1 caso afirmativo, 0 caso contrario.
*/
int todos_mapa_exceto_raio(ESTADO *e){
    int i;

    for (i = 1; i < MAX_POWERUPS; i++){
        if ((!(esta_mapa(e, i))) && (i != POWERUP_RAIO))
          return 0;
    }

    return 1;
}


/**
\brief Função que averigua se um determinado power up está no inventário.
@param e Estado do jogo.
@param tipo_powerup Indica qual o tipo de power up.
@returns int 1 se o power up estiver no inventário, 0 caso contrário.
*/
int esta_inventario(ESTADO *e, int tipo_powerup){
    return e->powerup[tipo_powerup][NO_INVENTARIO].janela != 0;
}


/**
\brief Função que indica qual dos power up está na determinada janela
do inventário.
@param e Estado do jogo.
@param janela Indica qual é a janela do inventário.
@returns int Número do power up que está na janela.
*/
int qual_powerup(ESTADO *e, int janela){
    int i;

    for (i = 0; i < MAX_POWERUPS; i++){
        if (e->powerup[i][NO_INVENTARIO].janela == janela) return i;
    }

    return 0;
}


/**
\brief Procedimento que põe o power up encontrado pelo jogador no inventário,
e elimina-o do mapa.
@param e Estado do jogo.
@param tipo_pup Indica qual o tipo de power up.
*/
void encontrou_powerup(ESTADO *e, int tipo_pup) {
    pup_no_inventario(e, tipo_pup);
    elimina_pup_mapa(e, tipo_pup);
}


/**
\brief Procedimento que organiza o inventário (seguindo a metodo FIFO,
first in first out).
@param e Estado do jogo.
*/
void organiza_inventario(ESTADO *e){
    int janela1_vazia, janela2_vazia, janela3_vazia, p2, p3;

    p2 = qual_powerup(e, 2);
    p3 = qual_powerup(e, 3);

    janela1_vazia = (qual_powerup(e, 1) == 0);
    janela2_vazia = (p2 == 0);
    janela3_vazia = (p3 == 0);


    if (janela1_vazia && !janela2_vazia && !janela3_vazia){
        e->powerup[p2][NO_INVENTARIO].janela = 1;
        e->powerup[p3][NO_INVENTARIO].janela = 2;
    }

    else if (janela1_vazia && !janela2_vazia && janela3_vazia)
            e->powerup[p2][NO_INVENTARIO].janela = 1;

    else if (!janela1_vazia && janela2_vazia && !janela3_vazia)
            e->powerup[p3][NO_INVENTARIO].janela = 2;
}


/**
\brief Procedimento que coloca o power up selecionado pelo jogador no inventário.
@param e Estado do jogo.
@param tipo_powerup Indica qual o tipo de power up.
*/
void pup_no_inventario(ESTADO *e, int tipo_powerup){
    int janela1_vazia, janela2_vazia, janela3_vazia, p1;

    p1 = qual_powerup(e, 1);

    janela1_vazia = (p1 == 0);

    janela2_vazia = (qual_powerup(e, 2) == 0);

    janela3_vazia = (qual_powerup(e, 3) == 0);

    if (!esta_inventario(e, tipo_powerup)){

        if (janela1_vazia && janela2_vazia && janela3_vazia)
            e->powerup[tipo_powerup][NO_INVENTARIO].janela = 1;

        else if (janela2_vazia && janela3_vazia)
            e->powerup[tipo_powerup][NO_INVENTARIO].janela = 2;

        else if (janela3_vazia)
            e->powerup[tipo_powerup][NO_INVENTARIO].janela = 3;

        else {
            e->powerup[p1][NO_INVENTARIO].janela = 0;
            organiza_inventario(e);
            e->powerup[tipo_powerup][NO_INVENTARIO].janela = 3;
        }
    }
}


/**
\brief Procedimento que abre os links para o jogador poder escolher uma
posição do mapa em seu redor (porém, somente direção horizontal ou vertical).
@param user Nome do utilizador.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordendadas.
*/
void imprime_tronco(char user[], int x, int y) {
    int t = TAM-1;

    if ((y - 1) > 0 && (y - 1) < t)
        imprime_casa_ataque(x, y - 1, user, "tronco");
    if ((y + 1) > 0 && (y + 1) < t)
        imprime_casa_ataque(x, y + 1, user, "tronco");
    if ((x - 1) > 0 && (x - 1) < t)
        imprime_casa_ataque(x - 1, y, user, "tronco");
    if ((x + 1) > 0 && (x + 1) < t)
        imprime_casa_ataque(x + 1, y, user, "tronco");
}


/**
\brief Procedimento que destrói os inimigos que estiverem no sentido escolhido
pelo jogador, sendo a passagem do tronco bloqueada pelos obstáculos, e pela porta.
@param e Estado do jogo.
@param dx int: 1 se o jogador escolheu a direita, -1 se escolheu a esquerda, 0 se
não escolheu uma direção no sentido horizontal.
@param dy int: 1 se o jogador escolheu baixo, -1 se escolheu cima, 0 se
não escolheu uma direção no sentido vertical.
*/
void tronco(ESTADO *e, int dx, int dy){
    int tipo, t, x, y, soma_x, soma_y;
    x = e->jog.pos.x;
    y = e->jog.pos.y;
    soma_x = x + dx;
    soma_y = y + dy;
    t = TAM - 1;

    while (soma_y < t && soma_y > 0 && soma_x < t && soma_x > 0){
        soma_x = x + dx;
        soma_y = y + dy;

        if (tem_obstaculo(*e, soma_x, soma_y) || tem_porta(*e, soma_x, soma_y))
            break;
        else if (tem_inimigos(*e, soma_x, soma_y)) {
            tipo = tipo_ini(e, soma_x, soma_y);
            mata_inimigo(e, tipo, soma_x, soma_y);
        }

        if (dx > 0) dx++;
        else if (dx < 0) dx--;
        else if (dy > 0) dy++;
        else if (dy < 0) dy--;
    }
}


/**
\brief Procedimento que destrói os inimigos que estiverem no sentido
(somente direita, esquerda, cima ou baixo) escolhido pelo jogador.
@param e Estado do jogo.
@param dx int: 1 se o jogador escolheu a direita, -1 se escolheu a esquerda, 0 se
não escolheu uma direção no sentido horizontal.
@param dy int: 1 se o jogador escolheu baixo, -1 se escolheu cima, 0 se
não escolheu uma direção no sentido vertical.
*/
void powerup_tronco(ESTADO *e, int dx, int dy){
    if ((dx == 0 && dy == 1) || (dx == 0  && dy == -1) ||
        (dx == 1 && dy == 0) || (dx == -1 && dy == 0))
        tronco(e, dx, dy);

    elimina_pup_inventario(e, POWERUP_TRONCO);
    organiza_inventario(e);

    printf("<meta http-equiv=\"refresh\" content=\"0;");
    printf("URL=http://localhost/cgi-bin/game?jogar&%d,%d-%s\">", e->jog.pos.x, e->jog.pos.y, e->user);
    printf("<meta name=\"keywords\" content=\"automatic redirection\">");
}


/**
\brief Procedimento que abre os links para o jogador poder escolher uma posição
do mapa num raio de 6 casas em seu redor.
@param user Nome do utilizador.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordendadas.
*/
void imprime_martelo(char user[], int x, int y) {
  int dx, dy, t;
  t = TAM-1;

  for (dx = -6; dx <= 6; dx++)
    for (dy = -6; dy <= 6; dy++)
      if (((x + dx) != x || (y + dy) != y) && ((x + dx) > 0 && (y + dy) > 0 && (x + dx) < t && (y + dy) < t))
        imprime_casa_ataque(x + dx, y + dy, user, "martelo");
}


/**
\brief Procedimento que destrói o inimigo que estiver nas coordenadas
selecionadas pelo jogador. Não destrói eventuais obstáculos ou outros power ups.
@param e Estado do jogo.
@param x Coordenada do eixo das abcissas.
@param y Coordenada do eixo das ordendadas.
*/
void powerup_martelo(ESTADO *e, int x, int y){
    int tipo;

    if (tem_inimigos(*e, x, y)) {
      tipo = tipo_ini(e, x, y);
      mata_inimigo(e, tipo, x, y);
    }

    elimina_pup_inventario(e, POWERUP_MARTELO);
    organiza_inventario(e);

    printf("<meta http-equiv=\"refresh\" content=\"0;");
    printf("URL=http://localhost/cgi-bin/game?jogar&%d,%d-%s\">", e->jog.pos.x, e->jog.pos.y, e->user);
    printf("<meta name=\"keywords\" content=\"automatic redirection\">");
}


/**
\brief Procedimento que destrói tudo o que estiver nas 8 casas (cima, baixo,
esquerda, direita e diagonais) em torno do jogador.
@param e Estado do jogo.
*/
void powerup_bomba(ESTADO *e){
    int x = e->jog.pos.x;
    int y = e->jog.pos.y;
    int dx, dy;

    for (dx = -1 ; dx <= 1 ; dx++)
        for (dy = -1 ; dy <= 1 ; dy++){
            if (dx != 0 || dy != 0){
                afeta(e, x + dx, y + dy);
        }
    }
}


/**
\brief Procedimento que protege o jogador de todos os ataques dos
inimigos durante 5 jogadas.
@param e Estado do jogo.
*/
void powerup_escudo(ESTADO *e){
    e->powerup[POWERUP_ESCUDO][NO_INVENTARIO].jog_rest = 5;
}


/**
\brief Procedimento que imobiliza os monstros durante 5 jogadas.
@param e Estado do jogo.
*/
void powerup_gelo(ESTADO *e){
    e->powerup[POWERUP_GELO][NO_INVENTARIO].jog_rest = 5;
}


/**
\brief Procedimento que mata todos os monstros no jogo.
@param e Estado do jogo.
*/
void powerup_raio(ESTADO *e){
    int r, tipo, i, num_ogre, num_bruxa, num_esqueleto, score_monstros = 0;
    num_ogre = e->num_inimigos[OGRE];
    num_bruxa = e->num_inimigos[BRUXA];
    num_esqueleto = e->num_inimigos[ESQUELETO];

    r = num_ogre + num_bruxa + num_esqueleto;
    e->inimigos_mortos += r;

    score_monstros = (num_ogre * 10) + (num_bruxa * 15)  + (num_esqueleto * 5);
    e->score += score_monstros;


    for(tipo = 0 ; tipo < TIPO_INIMIGO ; tipo++)
        for (i = 0 ; i < e->num_inimigos[tipo] ; i++){
            e->inimigo[tipo][i].pos.x = -1;
            e->inimigo[tipo][i].pos.y = -1;
        }

    e->num_inimigos[OGRE] = 0;
    e->num_inimigos[BRUXA] = 0;
    e->num_inimigos[ESQUELETO] = 0;
}


/**
\brief Procedimento que permite que o jogador faça movimentos de 2 casas,
durante 5 jogadas.
@param e Estado do jogo.
*/
void powerup_move(ESTADO *e){
    e->intencao = 1;
    e->powerup[POWERUP_MOVE][NO_INVENTARIO].jog_rest = 5;
}


/**
\brief Procedimento que dá uma vida extra ao jogador.
@param e Estado do jogo.
*/
void powerup_vida(ESTADO *e){
    if (e->jog.n_ataques < VIDAS && e->jog.n_ataques != 0)
        e->jog.n_ataques--;
}
