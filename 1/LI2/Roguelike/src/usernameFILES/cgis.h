#ifndef ___CGIS_H___
#define ___CGIS_H___

#include <stdio.h>

/**
@file cgis.h
Macros úteis para gerar CGIs
*/

/**
\brief Imprime página HTML com o botão para dar início ao jogo.
*/
#define PRINT_START       printf("Content-Type: text/html\n\n"); \
                          printf("<html><head><title>Game: Ultra Power Mega Super Rouguelike</title>"); \
                          printf("<style>body{background-image: url(\"http://localhost/images/fundo.png\");background-repeat:no-repeat;background-size:cover}"); \
                          printf(".animate{transition: all 0.1s;-webkit-transition: all 0.1s;}"); \
                          printf(".action-button{padding: 15px 40px;border-radius: 10px;font-family: 'Pacifico', cursive;font-size: 35px;color: #FFF;text-decoration: none;}"); \
                          printf(".red{background-color: #E74C3C;border-bottom: 5px solid #BD3E31;text-shadow: 0px -2px #BD3E31;}"); \
                          printf(".centered{position:absolute;width:100px;height:100px;left:46%%;top:50%%;margin-left:-50px;margin-top:-50px;}"); \
                          printf("</style></head>"); \
                          printf("<body><div class=\"centered\">"); \
                          printf("<a href=\"http://localhost/cgi-bin/game\" class=\"action-button shadow animate red\">Start!</a>"); \
                          printf("</div></body></html>");

/**
\brief Imprime página HTML com formulário para o jogador introduzir o nome de utilizador.
*/
#define PRINT_GET         printf("Content-Type: text/html\n\n"); \
                          printf("<html><head><title>Game: Ultra Power Mega Super Rouguelike</title><style>"); \
                          printf("body{background-image: url(\"http://localhost/images/fundo.png\");background-repeat:no-repeat;background-size:cover}{font-family: \"Roboto\", Helvetica, Arial, sans-serif;font-weight: 100;font-size: 12px;line-height: 30px;color: #777;background: #4CAF50;}"); \
                          printf(".container{max-width: 400px;width: 100%%;margin: 0 auto;position: relative;}"); \
                          printf("#contact input[type=\"text\"],#contact textarea,#contact button[type=\"submit\"] {font: 400 12px/16px \"Roboto\", Helvetica, Arial, sans-serif;}"); \
                          printf("#contact {background: #F9F9F9;padding: 25px;margin: 150px 0;box-shadow: 0 0 20px 0 rgba(0, 0, 0, 0.2), 0 5px 5px 0 rgba(0, 0, 0, 0.24);}"); \
                          printf("#contact h3 {display: block;font-size: 30px;font-weight: 300;margin-bottom: 10px;}"); \
                          printf("fieldset{border: medium none !important;margin: 0 0 10px;min-width: 100%%;padding: 0;width: 100%%;}"); \
                          printf("#contact input[type=\"text\"],#contact textarea {width: 100%%;border: 1px solid #ccc;background: #FFF;margin: 0 0 5px;padding: 10px;}"); \
                          printf("#contact button[type=\"submit\"] {cursor: pointer;width: 100%%;border: none;background: #4CAF50;color: #FFF;margin: 0 0 5px;padding: 10px;font-size: 15px;}"); \
                          printf("#contact button[type=\"submit\"]:hover {background: #43A047;-webkit-transition: background 0.3s ease-in-out;-moz-transition: background 0.3s ease-in-out;transition: background-color 0.3s ease-in-out;}"); \
                          printf("#contact button[type=\"submit\"]:active {box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.5);}"); \
                          printf("</style></head><body><div class=\"container\"><form id=\"contact\" action=\"http://localhost/cgi-bin/user\" method=\"get\">"); \
                          printf("<h3>Game: Ultra Power Mega Super Rouguelike</h3></br>"); \
                          printf("<fieldset><input placeholder=\"Your Username\" type=\"text\" name=\"first\" tabindex=\"2\" required></fieldset>"); \
                          printf("<fieldset><button name=\"submit\" type=\"submit\" id=\"contact-submit\" data-submit=\"...Sending\">Submit</button></fieldset>"); \
                          printf("</form></div></body></html>");

#endif
