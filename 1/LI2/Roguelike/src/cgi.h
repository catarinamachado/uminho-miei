#ifndef ___CGI_H___
#define ___CGI_H___

/**
@file cgi.h
Macros úteis para gerar CGIs
*/

#include <stdio.h>

/**
\brief Escala das imagens do jogo.
*/
#define ESCALA		  30


/**
\brief Caminho para as imagens
*/
#define IMAGE_PATH							"http://localhost/images/"

/**
\brief Macro para a imagem do coração
*/
#define CORACAO                 printf("<image width=20 height=20 src=\"http://localhost/images/vida.png\" />\n");

/**
\brief Macro para a imagem da bruxa
*/
#define BRUXA_INFO              printf("<image width=45 height=50 src=http://localhost/images/bruxa.png>\n");

/**
\brief Macro para a imagem do esqueleto
*/
#define ESQUELETO_INFO          printf("<image width=45 height=50 src=http://localhost/images/esqueleto.png>\n");

/**
\brief Macro para a imagem do ogre
*/
#define OGRE_INFO               printf("<image width=45 height=50 src=http://localhost/images/ogre.png>\n");

/**
\brief Macro para a imagem do bot.
*/
#define BOT_IMAGE               printf("<image width=45 height=50 src=http://localhost/images/rei.png>\n");

/**
\brief Macro para começar o html
*/
#define ABRIR_HEAD_HTML         printf("Content-Type: text/html\n\n"); \
                                printf("<html><head>\n"); \
                                printf("     <title>Game: Ultra Power Mega Roguelike</title>\n"); \
                                printf("\n"); \
                                printf("     <style type=\"text/css\">\n"); \
                                printf("          body {\n"); \
                                printf("               background-image: url(\"http://localhost/images/fundo.png\");\n"); \
                                printf("               background-repeat:no-repeat;\n"); \
                                printf("               background-size:cover;\n"); \
                                printf("\n"); \
                                printf("          }\n");  \
                                printf("          div.absoluteNivel {\n"); \
                                printf("              position: absolute;\n"); \
                                printf("              top:    2px;\n"); \
                                printf("              right: 200px;\n"); \
                                printf("          }\n"); \
                                printf("          div.absoluteInfo {\n"); \
                                printf("              position: absolute;\n"); \
                                printf("              top:   97px;\n"); \
                                printf("              right: 200px;\n"); \
                                printf("          }\n"); \
                                printf("          div.absoluteNome_Inventario {\n"); \
                                printf("              position: absolute;\n"); \
                                printf("              top:    415px;\n"); \
                                printf("              right: 205px;\n"); \
                                printf("          }\n"); \
                                printf("          div.absoluteInventario {\n"); \
                                printf("              position: absolute;\n"); \
                                printf("              top:   477px;\n"); \
                                printf("              right: 270px;\n"); \
                                printf("          }\n"); \
                                printf("          div.absoluteArmas {\n"); \
                                printf("              position: absolute;\n"); \
                                printf("              top:   539px;\n"); \
                                printf("              right: 292px;\n"); \
                                printf("          }\n"); \
                                printf("     </style>\n");
/**
\brief Macro para fechar o html
*/
#define FECHAR_HEAD_HTML        printf("</head>\n");

/**
\brief Macro para criar os
diferentes níveis no jogo
@param nivel Nível do jogo.
*/
#define NIVEL(nivel)            printf("<div class=\"absoluteNivel\">\n"); \
                                printf("     <table border=0 style=position:relative; margin: 0 auto; left: 20px; bottom: 300px;>\n"); \
                                printf("          <tr>\n"); \
                                printf("               <th width=298 height=100><font size=6> N&iacute;vel %d</font></th>\n", nivel); \
                                printf("          </tr>\n"); \
                                printf("     </table>\n"); \
                                printf("</div>\n")

/**
\brief Macro para abrir uma tabela
*/
#define TABELA_INICIO            printf("<div class=\"absoluteInfo\">\n"); \
                                 printf("     <table border=1>\n");

/**
\brief Macro para abrir a informação
sobre as vidas do jogador.
*/
#define INFO_VIDAS              printf("          <tr width=350 height=30 align=middle>\n"); \
                                printf("               <td width=150 height=30>Vidas:</td>\n"); \
                                printf("               <td width=150 height=30>");

/**
\brief Macro que imprime a tabela com a informação relativa ao número de inimigos, número de jogadas,
nome do utilizador, pontuação e pontuação máxima.
@param inimortos Número de inimigos mortos
@param numjog Número de jogadas por nível
@param score Pontuação do jogador
@param maxScore Pontuação que estabelece o record
@param user Nome do jogador/utilizador
*/
#define INFO_NUM(inimortos, numjog, score, maxScore, user)      printf("          <tr width=350 height=30 align=middle>\n"); \
                                                                printf("               <td width=150 height=30>Inimigos destru&iacute;dos:</td>\n"); \
                                                                printf("               <td width=150 height=30>%d</td>", inimortos); \
                                                                printf("          </tr>\n"); \
                                                                printf("          <tr width=350 height=30 align=middle>\n"); \
                                                                printf("               <td width=150 height=30>Jogadas:</td>\n"); \
                                                                printf("               <td width=150 height=30>%d</td>", numjog); \
                                                                printf("          </tr>\n"); \
                                                                printf("          <tr width=350 height=30 align=middle>\n"); \
                                                                printf("               <td width=150 height=30>Utilizador:</td>\n"); \
                                                                printf("               <td width=150 height=30>%s</td>", user); \
                                                                printf("          </tr>\n"); \
                                                                printf("          <tr width=350 height=30 align=middle>\n"); \
                                                                printf("               <td width=150 height=30>Pontua&ccedil;&atilde;o:</td>\n"); \
                                                                printf("               <td width=150 height=30>%d</td>\n", score); \
                                                                printf("          </tr>\n"); \
                                                                printf("          <tr width=350 height=30 align=middle>\n"); \
                                                                printf("               <td width=150 height=30>Melhor Pontua&ccedil;&atilde;o:</td>\n"); \
                                                                printf("               <td width=150 height=30>%d</td>\n", maxScore); \
                                                                printf("          </tr>\n");
/**
\brief Macro para abrir informação.
*/
#define INFO_AJUDAS                  printf("          <tr width=350 height=30 align=middle>\n"); \
                                     printf("               <td width=150 height=30>Informa&ccedil;&atilde;o Inimigos:</td>\n"); \
                                     printf("               <td width=150 height=30>");

/**
\brief Macro para abrir um link nos monstros
@param link_ent Link para iluminar todos as casas
atacadas por todos os monstros.
@param link_saida Link de saída da imagem.
*/
#define MONSTROS_INFO_INICIO(link_ent, link_saida)   printf("<div style=\"cursor:pointer\" onmousemove=\"window.location = '%s'\" " \
                                                            "onmouseleave=\"window.location = '%s'\">", \
                                                            link_ent, link_saida);

/**
\brief Macro para fechar o link nos monstros
*/
#define MONSTROS_INFO_FIM              printf("</div>");

/**
\brief Macro para abrir um link no jogador automático.
*/
#define BOT                          printf("          <tr width=350 height=30 align=middle>\n"); \
                                     printf("               <td width=150 height=30>Jogador autom&aacute;tico:</td>\n"); \
                                     printf("               <td width=150 height=30>");

/**
\brief Macro para que o bot jogue automaticamente.
*/
#define REFRESH_BOT                  printf("<meta http-equiv=\"refresh\" content=\"1\">\n");

/**
\brief Macro para abrir um link para o jogador automático.
@param link Link para executar o bot.
*/
#define BOT_INICIO(link)             printf("<div style=\"cursor:pointer\" onclick=\"window.location = '%s'\">", link);

/**
\brief Macro para fechar o link do bot.
*/
#define BOT_FIM                      printf("</div>");

/**
\brief Macro para fechar informação.
*/
#define INFO_FIM                printf("               </td>\n"); \
                                printf("          </tr>\n");

/**
\brief Macro para fechar uma tabela
*/
#define TABELA_FIM               printf("     </table>\n"); \
                                 printf("</div>\n");

/**
\brief Macro para imprimir uma bomba.
*/
#define BOMBA                   printf("<image width=45 height=50 src=http://localhost/images/powerup_bomba.png>\n");

/**
\brief Macro para imprimir o escudo.
*/
#define ESCUDO                  printf("<image width=45 height=50 src=http://localhost/images/powerup_escudo.png>\n");

/**
\brief Macro para imprimir o raio.
*/
#define RAIO                    printf("<image width=45 height=50 src=http://localhost/images/powerup_raio.png>\n");

/**
\brief Macro para imprimir o gelo.
*/
#define GELO                    printf("<image width=45 height=50 src=http://localhost/images/powerup_gelo.png>\n");

/**
\brief Macro para imprimir o power up move.
*/
#define MOVE                    printf("<image width=45 height=50 src=http://localhost/images/powerup_move.png>\n");

/**
\brief Macro para imprimir o martelo.
@param str Estado do jogo no formato array de caracteres.
*/
#define MARTELO(str)            printf("<a href=\"http://localhost/cgi-bin/game?mart&%s\"><img width=45 height=45 src=\"http://localhost/images/powerup_martelo.png\"</a>\n", str);

/**
\brief Macro para imprimir o martelo.
@param str Estado do jogo no formato array de caracteres.
*/
#define TRONCO(str)             printf("<a href=\"http://localhost/cgi-bin/game?tronco&%s\"><img width=45 height=45 src=\"http://localhost/images/powerup_tronco.png\"</a>\n", str);

/**
\brief Macro para imprimir o nome titulo: Inventário
*/
#define NOME_INVENTARIO         printf("<div class=\"absoluteNome_Inventario\">\n"); \
                                printf("     <table border=0 style=position:relative; margin: 0 auto; left: 20px; bottom: 300px;>\n"); \
                                printf("          <tr>\n"); \
                                printf("               <th width=298 height=100><font size=2>Invent&aacute;rio</font></th>\n"); \
                                printf("          </tr>\n"); \
                                printf("     </table>\n"); \
                                printf("</div>\n")

/**
\brief Macro que inicia o inventário.
*/
#define INVENTARIO_INICIO                                 printf("<div class=\"absoluteInventario\">\n"); \
                                                          printf("     <table border=1>\n"); \
                                                          printf("          <tr width=350 height=50 align=middle>\n"); \

/**
\brief Macro que abre a primeira janela do inventário.
*/
#define INV_JANELA1_INICIO                                printf("               <td width=50 height=55>\n"); \

/**
\brief Macro para abrir um link para a primeira janela do inventário
@param link_ent Estado do jogo no formato array de caracteres.
@param link_saida Link de saída da imagem.
*/
#define JANELA1_INICIO(link_ent, link_saida)              printf("<div style=\"cursor:pointer\" onclick=\"window.location = '%s'\" " \
                                                            "onmouseleave=\"window.location = '%s'\">", \
                                                            link_ent, link_saida);

/**
\brief Macro para fechar o link da primeira janela do inventário
*/
#define JANELA1_FIM                                       printf("</div>");

/**
\brief Macro para fechar a primeira janela do inventário
*/
#define INV_JANELA1_FIM                                   printf("                                       </td>\n")

/**
\brief Macro que abre a segunda janela do inventário.
*/
#define INV_JANELA2_INICIO                                printf("               <td width=50 height=55>\n"); \

/**
\brief Macro para abrir um link para a primeira janela do inventário
@param link_ent Estado do jogo no formato array de caracteres.
@param link_saida Link de saída da imagem.
*/
#define JANELA2_INICIO(link_ent, link_saida)              printf("<div style=\"cursor:pointer\" onclick=\"window.location = '%s'\" " \
                                                            "onmouseleave=\"window.location = '%s'\">", \
                                                            link_ent, link_saida);

/**
\brief Macro para fechar o link da primeira janela do inventário
*/
#define JANELA2_FIM                                       printf("</div>");

/**
\brief Macro para fechar a segunda janela do inventário
*/
#define INV_JANELA2_FIM                                   printf("                                         </td>\n")

/**
\brief Macro que abre a terceira janela do inventário.
*/
#define INV_JANELA3_INICIO                                printf("               <td width=50 height=55>\n"); \

/**
\brief Macro para abrir um link para a primeira janela do inventário
@param link_ent Estado do jogo no formato array de caracteres.
@param link_saida Link de saída da imagem.
*/
#define JANELA3_INICIO(link_ent, link_saida)              printf("<div style=\"cursor:pointer\" onclick=\"window.location = '%s'\" " \
                                                            "onmouseleave=\"window.location = '%s'\">", \
                                                            link_ent, link_saida);

/**
\brief Macro para fechar o link da terceira janela do inventário
*/
#define JANELA3_FIM                                       printf("</div>");

/**
\brief Macro para fechar a terceira janela do inventário
*/
#define INV_JANELA3_FIM                                   printf("                                       </td>\n")

/**
\brief Macro para fechar o inventário
*/
#define INVENTARIO_FIM                                    printf("          </tr>\n"); \
                                                          printf("     </table>\n"); \
                                                          printf("</div>\n");

/**
\brief Macro para imprimir as armas - granada e míssil.
@param num_granadas Número de granadas restantes.
@param num_missil Número de mísseis restantes.
@param str Estado do jogo no formato array de caracteres.
*/
#define ARMAS(num_granadas,num_missil,str)   printf("<div class=\"absoluteArmas\">"); \
						 printf("<table border=1><tr width=350 height=50 align=middle><td>\n"); \
                         printf("<a href=\"http://localhost/cgi-bin/game?g&%s\"><img width=45 height=50 src=\"http://localhost/images/granada.png\" alt=\"DON'T FORGET THIS\">%d</a>\n", str, num_granadas); \
                         printf("</td><td>");\
                         printf("<a href=\"http://localhost/cgi-bin/game?m&%s\"><img width=45 height=50 src=\"http://localhost/images/missil.png\" alt=\"DON'T FORGET THIS\">%d</a>\n", str, num_missil); \
                         printf("</td></tr></table>\n"); \
                         printf("</div>\n");


/**
\brief Macro para abrir um svg
@param tamx O comprimento do svg
@param tamy A altura do svg
*/
#define ABRIR_SVG(tamx, tamy)		  		printf("<body>\n"); \
                                      		printf("     <svg width=%d height=%d>\n\n", tamx, tamy)

/**
\brief Macro para fechar um svg
*/
#define FECHAR_SVG							printf("\n     </svg>\n"); \
                                printf("</body></html>\n")

/**
\brief Macro para criar uma imagem
@param X A coordenada X do canto superior esquerdo
@param Y A coordenada Y do canto superior esquerdo
@param ESCALA A escala da imagem
@param FICHEIRO O caminho para o link do ficheiro
*/
#define IMAGEM(X, Y, ESCALA, FICHEIRO)		printf("<image x=%d y=%d width=%d height=%d xlink:href=%s%s />\n", \
												ESCALA * X, ESCALA* Y, ESCALA, ESCALA, IMAGE_PATH, FICHEIRO)

/**
\brief Macro específica para criar uma imagem do inimigo
@param X A coordenada X do canto superior esquerdo
@param Y A coordenada Y do canto superior esquerdo
@param ESCALA A escala da imagem
@param FICHEIRO O caminho para o link do ficheiro
@param LINK_ENT Link para exibir brilho.
@param LINK_SAIDA Link para retirar o brilho.
@
*/
#define IMAGEM_INIMIGO(X, Y, ESCALA, FICHEIRO, LINK_ENT, LINK_SAIDA)	  \
                       printf("<image x=%d y=%d width=%d height=%d style=\"cursor:pointer\" " \
                       "onmousemove=\"window.location = '%s'\" onmouseleave=\"window.location = '%s'\" xlink:href=%s%s  />\n", \
                       ESCALA * X, ESCALA* Y, ESCALA, ESCALA, LINK_ENT, LINK_SAIDA, IMAGE_PATH, FICHEIRO)

/**
\brief Macro para iluminar as posições que os inimigos atacam.
@param X A coordenada X do canto superior esquerdo
@param Y A coordenada Y do canto superior esquerdo
@param ESCALA A escala da imagem
@param FICHEIRO O caminho para o link do ficheiro
@param LINK link de saída da imagem (para que não conte como jogada)
*/
#define IMAGEM_BRILHO(X, Y, ESCALA, FICHEIRO, LINK)	  \
                       printf("<image x=%d y=%d width=%d height=%d " \
                       "onmouseleave=\"window.location = '%s'\" xlink:href=%s%s  />\n", \
                       ESCALA * X, ESCALA* Y, ESCALA, ESCALA, LINK, IMAGE_PATH, FICHEIRO)
/**
\brief Macro para criar um quadrado
@param X A coordenada X do canto superior esquerdo
@param Y A coordenada Y do canto superior esquerdo
@param ESCALA A escala do quadrado
@param COR A cor de preenchimento do quadrado
*/
#define QUADRADO(X, Y, ESCALA, COR)      printf("  <rect x=%d y=%d width=%d height=%d fill=\"%s\" />\n", \
												                     ESCALA * X, ESCALA* Y, ESCALA, ESCALA, COR)

/**
\brief Macro para abrir um link
@param link O caminho para o link
*/
#define ABRIR_LINK(link)					printf("<a xlink:href=%s>\n", link)

/**
\brief Macro para fechar o link
*/
#define FECHAR_LINK							printf("</a>\n")



#endif
