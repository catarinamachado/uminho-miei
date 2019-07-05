#ifndef __HELP_H__
#define __HELP_H__

#include <stdio.h>

/**
@file help.h
Procedimentos que originam a página HELP do jogo.
*/

/**
\brief Imprime na janela do jogo o botão para aceder à página das informações.
*/
#define PRINT_HELP        printf("<style>"); \
                          printf(".action-button{padding: 6px 6px;border-radius: 10px;font-family: 'Pacifico', cursive;font-size: 10px;color: #FFF;text-decoration: none;}"); \
                          printf(".red{background-color: #E74C3C;border-bottom: 5px solid #BD3E31;text-shadow: 0px -2px #BD3E31;}"); \
                          printf(".centered{position:absolute;width:10px;height:10px;left:100%%;top:2%%;margin-left:-50px;margin-top:0px;}"); \
                          printf("</style>"); \
                          printf("<body><div class=\"centered\">"); \
                          printf("<a href=\"http://localhost/help.html\" target=\"_blank\" class=\"action-button shadow animate red\">Help</a>"); \
                          printf("</div></body></html>");

void print_help();

#endif
