#include "apresentacao.h"
#include "help.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

/**
@file help.c
Procedimentos que originam a página HELP do jogo.
*/

/**
\brief Imprime página HTML com todas as informações relativas ao funcionamento do jogo.
*/
void print_help () {
  FILE * fP_HTML;

  fP_HTML = fopen("/var/www/html/help.html", "w");

  if (fP_HTML == NULL) {
    perror("null fp html page");
    exit(1);
  }

  fprintf(fP_HTML, "<!DOCTYPE html>\n\n<html>\n\n");
  fprintf(fP_HTML, "  <head>\n<title>Help</title>\n\n");
  fprintf(fP_HTML, "     <style>\n");
  fprintf(fP_HTML, "          body {\n");
  fprintf(fP_HTML, "               background-image: url(\"http://localhost/images/fundo.png\");\n");
  fprintf(fP_HTML, "               background-repeat:no-repeat;\n");
  fprintf(fP_HTML, "               background-size:cover;\n");
  fprintf(fP_HTML, "          }\n");
  fprintf(fP_HTML, "    </style>\n  </head>\n\n");

/* Titulo da pagina */
  fprintf(fP_HTML, "<div align=justify><center><font size=4><strong>Ultra Power Mega Super Roguelike</strong></font></center><br>");

/* Breve descriçao do objetivo do jogo */
  fprintf(fP_HTML, "<font size=2>O objetivo do jogo &eacute; conseguir atingir o maior n&iacute;vel poss&iacute;vel,"); \
  fprintf(fP_HTML, "           com a maior pontua&ccedil;&atilde;o.</font><br><br>");

/* Descriçao dos ataques dos inimigos */
  fprintf(fP_HTML, "<font size=2><strong>Existem 3 tipos de inimigos:</strong></font><br>");

  fprintf(fP_HTML, "<table border=4 bgcolor=black>\n");
  fprintf(fP_HTML, "  <tr width=50 height=50 align=justify>\n"); \
  fprintf(fP_HTML, "      <td width=50 height=50 bgcolor=#A9C6F2><img src=http://localhost/images/esqueleto.png height=50 width=50></td>\n"); \
  fprintf(fP_HTML, "      <td width=900 height=50 bgcolor=#A9C6F2><font size=2>O esqueleto ataca o jogador se estiver na posi&ccedil;&atilde;o"); \
  fprintf(fP_HTML, "                            imediatamente acima, abaixo, &agrave; esquerda"); \
  fprintf(fP_HTML, "                            ou &agrave; direita do jogador.<br>"); \
  fprintf(fP_HTML, "                                          O ataque do esqueleto retira 1 vida.</font></td>"); \
  fprintf(fP_HTML, "  </tr>\n"); \
  fprintf(fP_HTML, "  <tr width=50 height=50 align=justify>\n"); \
  fprintf(fP_HTML, "      <td width=50 height=50 bgcolor=#A9C6F2><img src=http://localhost/images/ogre.png height=50 width=50></td>\n"); \
  fprintf(fP_HTML, "      <td width=900 height=50 bgcolor=#A9C6F2><font size=2>O ataque do ogre atinge um raio de 3 casas nas suas diagonais. O seu ataque"); \
  fprintf(fP_HTML, "                           &eacute; bloqueado, isto &eacute;, a sua passagem &eacute; inibida por outros inimigos,"); \
  fprintf(fP_HTML, "                           obst&aacute;culos, power ups e pela porta.<br>"); \
  fprintf(fP_HTML, "                                           O ataque do ogre retira 2 vidas.</font></td>\n"); \
  fprintf(fP_HTML, "  </tr>\n"); \
  fprintf(fP_HTML, "  <tr width=50 height=50 align=justify>\n"); \
  fprintf(fP_HTML, "      <td width=50 height=50 bgcolor=#A9C6F2><img src=http://localhost/images/bruxa.png height=50 width=50></td>\n"); \
  fprintf(fP_HTML, "      <td width=900 height=50 bgcolor=#A9C6F2><font size=2>O ataque da bruxa atinge todas as casas num raio de 2. A passagem"); \
  fprintf(fP_HTML, "                           do ataque &eacute; bloqueada por outros inimigos,"); \
  fprintf(fP_HTML, "                           obst&aacute;culos, power ups e pela porta.<br>"); \
  fprintf(fP_HTML, "                                           O ataque da bruxa retira 2 vidas.</font></td>\n"); \
  fprintf(fP_HTML, "  </tr>\n"); \
  fprintf(fP_HTML, "</table>\n"); \

  /* Descriçao do bot */
  fprintf(fP_HTML, "<br><font size=2><strong>Existe tamb&eacute;m um jogador autom&aacute;tico que ajuda a compreender melhor o objetivo do jogo:</strong></font><br>");

  fprintf(fP_HTML, "<table border=4 bgcolor=black>\n");
  fprintf(fP_HTML, "  <tr width=50 height=50 align=justify>\n"); \
  fprintf(fP_HTML, "      <td width=50 height=50 bgcolor=#FFFFAF><img src=http://localhost/images/rei.png height=50 width=50></td>\n"); \
  fprintf(fP_HTML, "      <td width=900 height=50 bgcolor=#FFFFAF><font size=2>O jogador autom&aacute;tico tenta passar de n&iacute;vel, fugindo "); \
  fprintf(fP_HTML, "                            ao ataque dos inimigos, usando, caso seja necess&aacute;rio, "); \
  fprintf(fP_HTML, "                            os power ups e  as armas de que disp&otilde;e.</font></td>"); \
  fprintf(fP_HTML, "  </tr>\n"); \
  fprintf(fP_HTML, "</table>\n"); \


/* Breve descriçao sobre inventario */
  fprintf(fP_HTML, "<br><font size=2>O jogador possui um invent&aacute;rio com 3 janelas, onde armazena os power ups "); \
  fprintf(fP_HTML, "           que apanha. A organiza&ccedil;&atilde;o do mesmo segue o m&eacute;todo FIFO (first in  <br/>"); \
  fprintf(fP_HTML, "           first out), e, assim, se estiver cheio, o primeiro power up que apanhou &eacute; substitu&iacute;do "); \
  fprintf(fP_HTML, "           pelo que apanhou neste momento. N&atilde;o &eacute; poss&iacute;vel ter mais <br/>"); \
  fprintf(fP_HTML, "           do que 1 power up do mesmo tipo.</font><br><br>");

/* Descriçao dos power ups */
  fprintf(fP_HTML, "<font size=2>De 5 em 5 jogadas aparece aleatoriamente um power up no mapa, &agrave; exce&ccedil;&atilde;o "); \
  fprintf(fP_HTML, "do power up raio. Por ser o power up mais poderoso, a &uacute;nica <br/>"); \
  fprintf(fP_HTML, "forma de o obter &eacute; matando a bruxa que o tem. O ataque &agrave; bruxa tem que ser "); \
  fprintf(fP_HTML, "direto, isto &eacute;, sem a ajuda de qualquer power up ou recurso.</font><br>");

  fprintf(fP_HTML, "<br><font size=2><strong>Os 8 tipos de power ups existentes:</strong></font><br>");

  fprintf(fP_HTML, "<table border=4 bgcolor=black>\n");
  fprintf(fP_HTML, "  <tr width=50 height=50 align=justify>\n"); \
  fprintf(fP_HTML, "      <td width=50 height=50 bgcolor=#C0F5AA><img src=http://localhost/images/powerup_vida.png height=50 width=50></td>\n"); \
  fprintf(fP_HTML, "      <td width=900 height=50 bgcolor=#C0F5AA><font size=2>D&aacute; uma vida extra ao jogador.</font></td>"); \
  fprintf(fP_HTML, "  </tr>\n"); \
  fprintf(fP_HTML, "  <tr width=50 height=50 align=justify>\n"); \
  fprintf(fP_HTML, "      <td width=50 height=50 bgcolor=#C0F5AA><img src=http://localhost/images/powerup_escudo.png height=50 width=50></td>\n"); \
  fprintf(fP_HTML, "      <td width=900 height=50 bgcolor=#C0F5AA><font size=2>Protege o jogador de todos os ataques dos inimigos durante 5 jogadas.</font></td>\n"); \
  fprintf(fP_HTML, "  </tr>\n"); \
  fprintf(fP_HTML, "  <tr width=50 height=50 align=justify>\n"); \
  fprintf(fP_HTML, "      <td width=50 height=50 bgcolor=#C0F5AA><img src=http://localhost/images/powerup_gelo.png height=50 width=50></td>\n"); \
  fprintf(fP_HTML, "      <td width=900 height=50 bgcolor=#C0F5AA><font size=2>Imobiliza todos os inimigos durante 5 jogadas.</font></td>\n"); \
  fprintf(fP_HTML, "  </tr>\n"); \
  fprintf(fP_HTML, "  <tr width=50 height=50 align=justify>\n"); \
  fprintf(fP_HTML, "      <td width=50 height=50 bgcolor=#C0F5AA><img src=http://localhost/images/powerup_move.png height=50 width=50></td>\n"); \
  fprintf(fP_HTML, "      <td width=900 height=50 bgcolor=#C0F5AA><font size=2>Permite que o jogador fa&ccedil;a movimentos de 2 casas, durante 5 jogadas.</font></td>\n"); \
  fprintf(fP_HTML, "  </tr>\n"); \
  fprintf(fP_HTML, "  <tr width=50 height=50 align=justify>\n"); \
  fprintf(fP_HTML, "      <td width=50 height=50 bgcolor=#C0F5AA><img src=http://localhost/images/powerup_raio.png height=50 width=50></td>\n"); \
  fprintf(fP_HTML, "      <td width=900 height=50 bgcolor=#C0F5AA><font size=2>Mata todos os inimigos em jogo.</font></td>\n"); \
  fprintf(fP_HTML, "  </tr>\n"); \
  fprintf(fP_HTML, "  <tr width=50 height=50 align=justify>\n"); \
  fprintf(fP_HTML, "      <td width=50 height=50 bgcolor=#C0F5AA><img src=http://localhost/images/powerup_bomba.png height=50 width=50></td>\n"); \
  fprintf(fP_HTML, "      <td width=900 height=50 bgcolor=#C0F5AA><font size=2>Destr&oacute;i todos os inimigos, obst&aacute;culos e/ou power ups que"); \
  fprintf(fP_HTML, "              estiverem nas 8 casas (cima, baixo, esquerda, direita e diagonais) em torno do jogador.</font></td>\n"); \
  fprintf(fP_HTML, "  </tr>\n"); \
  fprintf(fP_HTML, "  <tr width=50 height=50 align=justify>\n"); \
  fprintf(fP_HTML, "      <td width=50 height=50 bgcolor=#C0F5AA><img src=http://localhost/images/powerup_tronco.png height=50 width=50></td>\n"); \
  fprintf(fP_HTML, "      <td width=900 height=50 bgcolor=#C0F5AA><font size=2>O jogador pode escolher um sentido (cima, baixo, esquerda ou direita) e"); \
  fprintf(fP_HTML, "              todos os inimigos que estiverem nesse sentido s&atilde;o destru&iacute;dos. Este power up &eacute;"); \
  fprintf(fP_HTML, "              bloqueado pelos obst&aacute;culos, pela porta e, em &uacute;ltimo caso, pela parede lateral.</font></td>\n"); \
  fprintf(fP_HTML, "  </tr>\n"); \
  fprintf(fP_HTML, "  <tr width=50 height=50 align=justify>\n"); \
  fprintf(fP_HTML, "      <td width=50 height=50 bgcolor=#C0F5AA><img src=http://localhost/images/powerup_martelo.png height=50 width=50></td>\n"); \
  fprintf(fP_HTML, "      <td width=900 height=50 bgcolor=#C0F5AA><font size=2>O jogador pode escolher uma posi&ccedil;&atilde;o do mapa num raio de 6 casas"); \
  fprintf(fP_HTML, "              em seu redor. Se estiver um inimigo nas coordenadas selecionadas pelo jogador, este &eacute;"); \
  fprintf(fP_HTML, "              destru&iacute;do. Caso seja um obst&aacute;culo, a porta, um power up, ou uma casa vazia, nada acontece.</font></td>\n"); \
  fprintf(fP_HTML, "  </tr>\n"); \
  fprintf(fP_HTML, "</table>\n"); \


/* Descriçao dos recursos */
  fprintf(fP_HTML, "<br><font size=2>A cada novo n&iacute;vel o armaz&eacute;m dos recursos &eacute; reabastecido. "); \
  fprintf(fP_HTML, "Independentemente do n&uacute;mero de recursos que o jogador possu&iacute;a, "); \
  fprintf(fP_HTML, "passa agora a ter <br/>"); \
  fprintf(fP_HTML, "2 granadas e 1 m&iacute;ssil.</font><br>");

  fprintf(fP_HTML, "<br><font size=2><strong>Os 2 tipos de recursos:</strong></font><br>");

  fprintf(fP_HTML, "<table border=4 bgcolor=black>\n");
  fprintf(fP_HTML, "  <tr width=50 height=50 align=justify>\n"); \
  fprintf(fP_HTML, "      <td width=50 height=50 bgcolor=#356024><img src=http://localhost/images/granada.png height=50 width=50></td>\n"); \
  fprintf(fP_HTML, "      <td width=900 height=50 bgcolor=#356024><font size=2>O jogador pode escolher uma posi&ccedil;&atilde;o do mapa (tem que ser"); \
  fprintf(fP_HTML, "              um ponto situado numa coroa exatamente num raio de 2 em torno do jogador) e tudo"); \
  fprintf(fP_HTML, "              o que estiver nessas coordenadas e em redor da mesma &eacute; destru&iacute;do"); \
  fprintf(fP_HTML, "              (sejam inimigos, obst&aacute;culos ou power ups).</font></td>\n"); \
  fprintf(fP_HTML, "  </tr>\n"); \
  fprintf(fP_HTML, "  <tr width=50 height=50 align=justify>\n"); \
  fprintf(fP_HTML, "      <td width=50 height=50 bgcolor=#356024><img src=http://localhost/images/missil.png height=50 width=50></td>\n"); \
  fprintf(fP_HTML, "      <td width=900 height=50 bgcolor=#356024><font size=2>O jogador pode escolher uma posi&ccedil;&atilde;o do mapa (qualquer), e "); \
  fprintf(fP_HTML, "              tudo o que estiver nessas coordenadas e em redor da mesma &eacute; destru&iacute;do"); \
  fprintf(fP_HTML, "              (sejam inimigos, obst&aacute;culos ou power ups).</font></td>\n"); \
  fprintf(fP_HTML, "  </tr>\n"); \
  fprintf(fP_HTML, "</table>\n"); \


  fprintf(fP_HTML, "</div></html>");

  fclose(fP_HTML);

  return;
}
