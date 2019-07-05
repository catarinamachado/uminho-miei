#ifndef ___ESTADO_H___
#define ___ESTADO_H___

/**
@file estado.h
Definição do estado e das funções que convertem estados em strings e vice-versa
*/

/** \brief O nº máximo de inimigos */
#define MAX_INIMIGOS	  	100

/** \brief O nº máximo de obstáculos */
#define MAX_OBSTACULOS		(TAM - 1) * (TAM - 1)

/** \brief O nº máximo de power ups + 1 */
#define MAX_POWERUPS	  		9

/** \brief Não existe o determinado power up atualmente no jogo */
#define NAO_HA				  		0

/** \brief O power up está no mapa  */
#define NO_MAPA	  					1

/** \brief O power up está no inventário */
#define NO_INVENTARIO			  2

/** \brief Tamanho da string que define a cor */
#define TAM_COR          	  8

/** \brief Número de casas no tabuleiro do jogo. */
#define TAM			   	     		21

/** \brief Número de inimigos diferentes. */
#define TIPO_INIMIGO     	  3

/** \brief Tipo do inimigo Ogre. */
#define OGRE		    	 		  0

/** \brief Tipo do inimigo Bruxa. */
#define BRUXA 			     	  1

/** \brief Tipo do inimigo Esqueleto. */
#define ESQUELETO 				  2

/** \brief Número máximo de posições que um só monstro pode atacar. */
#define MAX_ATAQUE 			    16

/** \brief Número máximo de vidas. */
#define VIDAS 						  5

/** \brief O nº máximo de estados possiveis do power up */
#define ESTADO_POWERUP	  	3

/** \brief Tipo do power up Vida. */
#define POWERUP_VIDA			  1

/** \brief Tipo do power up Bomba. */
#define POWERUP_BOMBA		 	  2

/** \brief Tipo do power up Escudo. */
#define POWERUP_ESCUDO			3

/** \brief Tipo do power up Gelo. */
#define POWERUP_GELO		 	  4

/** \brief Tipo do power up Move. */
#define POWERUP_MOVE			  5

/** \brief Tipo do power up Raio. */
#define POWERUP_RAIO			  6

/** \brief Tipo do power up Tronco. */
#define POWERUP_TRONCO		 	7

/** \brief Tipo do power up Martelo. */
#define POWERUP_MARTELO			8

/** \brief Tipo do recurso Granada. */
#define RECURSO_GRANADA 		9

/** \brief Tipo do recurso Missil. */
#define RECURSO_MISSIL		  10

/**
\brief Estrutura que armazena uma posição
*/
typedef struct posicao {
	/** \brief Abcissa */
	char x;
	/** \brief Ordenada */
	char y;
} POSICAO;

/**
\brief Estrutura que reperesenta o estado do jogador quanto à sua
posição no jogo e ao número de ataques sofridos.
Dá também informaçao relativa à possibilidade de o jogador estar
a sofrer ataques dos inimigos (esqueleto, bruxa e ogre).
*/
typedef struct estado_jogador {
	/** \brief Número de ataques sofridos */
	int n_ataques;
	/** \brief Se o jogador está a ser atacado pelo esqueleto */
	int esqueleto;
	/** \brief Se o jogador está a ser atacado pela bruxa */
	int bruxa;
	/** \brief Se o jogador está a ser atacado pelo ogre */
	int ogre;
	/** \brief Posição do jogador no jogo */
	POSICAO pos;
} ESTADO_JOGADOR;

/**
\brief Estrutura do estado do inimigo (relativamente ao seu dano),
e posicao do mesmo.
*/
typedef struct estado_inimigo{
	/** \brief Número de ataques sofridos */
	int n_ataques;
	/** \brief Posição do inimigo no jogo */
	POSICAO pos;
} ESTADO_INIMIGO;

/**
\brief Estrutura dos powerups, dando as seguintes informaçoes:
Número da janela, no caso do power up se encontrar no inventário;
Número de jogadas restantes, aquando da utilizaçao de power ups que
necessitem deste tipo de referencia;
Uma eventual informacao extra;
Posicao do power up no mapa, se este se encontrar no mesmo.
*/
typedef struct info_powerup{
	/** \brief Posição do power up no inventário */
	int janela;
	/** \brief Número de jogadas com power up */
	int jog_rest;
	/** \brief Informação sobre o power up */
	int info;
	/** \brief Posição no jogo onde se encontra o power up */
	POSICAO pos;
} INFO_POWERUP;

/**
\brief Estrutura das armas (recursos) do jogador.
Dá informaçao relativa à quantidade de granadas e misseis restantes.
*/
typedef struct armas{
	/** \brief Granadas restantes */
	char granadas;
	/** \brief Misseis restantes */
	char missil;
} ARMAS;

/**
\brief Informações sobre o bot, como por exemplo, após usar um
power up ele não pode jogar.
*/
typedef struct bot_info {
	/** \brief Indica se o bot usou uma arma, o raio ou a bomba */
	char usou_powerup_imediato;
	/** \brief Indica se o bot usou o escudo ou o gelo*/
	char usou_powerup;
	/** \brief Guarda as últimas seis posições do bot*/
	POSICAO ultimas_posicoes[6];
} BOT_INFO;

/**
\brief Estrutura que armazena o estado do jogo
*/
typedef struct estado {
	/** \brief A posição e o número de ataques sofridos pelo jogador */
	ESTADO_JOGADOR jog;
	/** \brief O nº de inimigos 0 */
	char num_inimigos[TIPO_INIMIGO];
  /** \brief O nível atual em que o jogo se encontra. */
	int nivel;
  /** \brief A posição da porta para um novo nível. */
	POSICAO porta;
	/** \brief intençao de açao do jogador */
	char intencao; /* 0 - para se mexer; 1 - move 2 casas; */
	/** \brief Array com a posição e número de ataques dos inimigos */
	ESTADO_INIMIGO inimigo[TIPO_INIMIGO][MAX_INIMIGOS];
  /** \brief Array com a pallet de cores do cenário do jogo */
	char cor[2][TAM_COR];
	/** \brief Score do atual jogador */
	int score;
	/** \brief Inimigos Mortos */
	int inimigos_mortos;
	/** \brief Número de jogadas (movimento) do jogador do nível atual */
	int num_jogadas;
	/** \brief Nome de utilizador atual */
	char user[32];
	/** \brief Estado dos power ups (no mapa, inventario ou nao existem) e a
	respetiva posicao, tanto no inventario como no mapa */
	INFO_POWERUP powerup[MAX_POWERUPS][ESTADO_POWERUP];
	/** \brief Granadas e misseis disponiveis ao jogador */
	ARMAS armas;
	/** \brief Array com a posição dos obstáculos */
	POSICAO obstaculo[MAX_OBSTACULOS];
	/** \brief O nº de obstáculos */
	int num_obstaculos;
  /** \brief Informações sobre o bot, como por exemplo, após usar um
	powerup ele não pode jogar. */
	BOT_INFO bot;
} ESTADO;


/**
\brief Procedimento que escreve num ficheiro a string que representa o estado atual do jogo.
@param string Estado atual do jogo.
@param user Nome do utilizador.
*/
void str2file(char *string, char user[]);


char * estado2str(ESTADO e);


ESTADO str2estado(char *argumentos);


ESTADO file2string(char user[]);


int posicao_valida(int x, int y);


int posicao_igual(POSICAO p, int x, int y);


int posicoes_iguais(POSICAO p1, POSICAO p2);


int tem_jogador(ESTADO e, int x, int y);


int tem_inimigo(ESTADO e, int tipo, int x, int y);


int tem_inimigos(ESTADO e, int x, int y);


int tem_bruxa_especial(ESTADO e, int x, int y);


int tem_obstaculo(ESTADO e, int x, int y);


int tem_porta (ESTADO e, int x, int y);


int tem_powerups(ESTADO e, int x, int y);


int tem_powerups_excpt_vida(ESTADO e, int x, int y);


int tem_powerup(ESTADO e, int tipo_powerup, int x, int y);


int posicao_ocupada(ESTADO e, int x, int y);


int pos_ocupada_excpt_jog(ESTADO e, int x, int y);


int pos_impede_ataque(ESTADO e, int x, int y);

#endif
