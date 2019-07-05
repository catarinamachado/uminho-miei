module Ajudaa2 where    
import Data.Char (isDigit)
import Data.Char (intToDigit)
import Data.Char (digitToInt)
import System.Environment

move :: [String] -> Int -> Char -> [String]
move m j c = if c == 'B' then poeBomba m (intToDigit j)             --quando jogador coloca uma bomba ordenadamente (se nao tiver ja uma bomba nessa posicao)
                         else andThen m (whatIn m (intToDigit j) c) (intToDigit j) c  --quando o jogador se move para U,D,L ou R

--1. analisando U D R L
--1.1. Bate contra pedra? Nada acontece
--1.2. Comando invalido? Nada acontece
--1.3. Bate contra ponto de interrogacao? Nada acontece
--1.4. Bate contra espaço vazio? 
---1.4.1. não tinha power up?
 -- jogador muda de posicao (novas coordenadas)
--- 1.4.2. tinha power up?
 -- jogador muda de posicao (novas coordenadas)
 -- elimina linha do mapa relativamente as informacoes daquele power up
 -- acrescenta + ou ! à frente da posicao do jogador  

type Coordenadas = (Int,Int)

-- onde está cada jogador j?
-- Dada a lista do estado atual do jogo, a funcao "coordenadasJ j l" devolve as coordenadas do jogador j
-- | 'coordenadasJ' : função que devolve as /coordenadas do jogador j/

coordenadasJ :: Char -> [String] -> String
coordenadasJ j m = tiraSpace (retiraPUJogador (tirarNJogador (jogadorInicio j m)))

-- | 'tiraSpace' : função que retira um espaço, se este for o último elemento de uma lista

tiraSpace :: String -> String
tiraSpace l = if (last l == ' ') then init l 
                                 else l

-- | 'retiraPUJogador' : função que retira power up do jogador

retiraPUJogador :: String -> String
retiraPUJogador [] = []
retiraPUJogador (h:t) = if (h == '+' || h == '!') then retiraPUJogador t
                                                  else (h:retiraPUJogador t)

-- | 'tirarNJogador' : @função auxiliar@ da função "coordenadasJ"

tirarNJogador :: String -> String
tirarNJogador (j:v:t) = t

-- | 'jogadorInicio' : @função auxiliar@ da função "coordenadasJ"
jogadorInicio :: Char -> [String] -> [Char]
jogadorInicio _ [] = []
jogadorInicio j (h:t) = if head h == j then h
                                       else jogadorInicio j t

--Devolve as coordenadas do jogador j em formato (Int,Int)
-- coordJogador (coordenadasJ j m)
-- | 'coordJogador' : função que devolve as coordenadas do jogador no tipo "coordenadas" definida anteriormente

coordJogador :: String -> (Int,Int)
coordJogador w = (read (coord1 w) :: Int, read (coord2 w) :: Int)

-- | 'coord1' : @função auxiliar@ da função "coordJogador"
coord1 :: String -> String
coord1 (h:t) = if h /= ' ' then h : coord1 t
                           else []

-- | 'coord2' : @função auxiliar@ da função "coordJogador"
coord2 :: String -> String
coord2 l = drop (1 + length (coord1 l)) l


-- O que esta na coordenada para onde o jogador j quer ir:
-- whatInAux m (coordJogador (coordenadasJ j m)) c = whatIn m j c
-- | 'whatIn' : função que __devolve a coordenada para onde um jogador j quer ir__

whatIn :: [String] -> Char -> Char -> Char
whatIn m j c = whatInAux m (coordJogador (coordenadasJ j m)) c

-- | 'whatInAux' : @função que auxilia@ na mostragem das coordenadas para onde um jogador j quer ir

whatInAux :: [String] -> (Int, Int) -> Char -> Char
whatInAux m (x,y) c | c=='R' = m !! y !! (x+1)
                    | c=='L' = m !! y !! (x-1)
                    | c=='U' = m !! (y-1) !! x
                    | c=='D' = m !! (y+1) !! x
                    | otherwise = 'P' -- comando invalido


-- ver o que está na posição em que o jogador quer ir e assim decidir se ele se pode mexer ou não, e o que acontece se puder 
--------'#' ou ' ' ou '?' 

-- o que acontece se na coordenada para onde o jogador j quer ir tiver:
-- andThen m (whatIn m j c) j c

-- | 'andThen' : função que __vê o que acontece na coordenada consoante para onde o jogador vai__

andThen :: [String] -> Char -> Char -> Char -> [String]  --funcao move praticamente
andThen m w j c | w=='#' = m
                | w=='?' = m
                | w==' ' = seraQTemPU m j c
                | w=='P' = m

-----funcao que vê se o ' ' tem power up ou não   
-- | 'seraQTemPU' : função que vê se o espaço tem power up ou não

seraQTemPU :: [String] -> Char -> Char -> [String]
seraQTemPU m j c = interrogation (novaCoord (coordJogador (coordenadasJ j m)) c) (coordenadasPU (coordenadassPU m)) m c j

-- | 'interrogation' : função auxiliar de "seraQTemPu"
interrogation :: (Int,Int) -> [(Int,Int)] -> [String] -> Char -> Char -> [String]
interrogation cooJ cooPU m c j = if elem cooJ cooPU then espacoVazioCPU m j c
                                                    else espacoVazio m c j

-------------- o que acontece quando o jogador quer ir para um ESPAÇO VAZIO SEM PU
--espacoVazio mapa coordenadas jogador  
-- | 'espacoVazio' : função que mostra o que acontece quando o jogador quer ir para um espaço vazio sem power ups

espacoVazio :: [String] -> Char -> Char -> [String]
espacoVazio [] _ _ = []
espacoVazio (h:t) c j = if head h == j then (linhaNova (h:t) c j) : t  
                                       else h : espacoVazio t c j

--linhaNova com as informacoes atuais do jogador
--linhaNova m j c
-- | 'linhaNova' : função que mostra as informações atuais do jogador

linhaNova :: [String] -> Char -> Char -> String
linhaNova m c j = [j] ++ " " ++ (coordString (novaCoord (coordJogador (coordenadasJ j m)) c)) ++ powerupS (howManyPU (tirarNJogador (jogadorInicio j m)))
                                      
-- coordenadas em que o jogador fica
-- novaCoord (coordJogador (coordenadasJ j m)) c)
-- | 'novaCoord' = função que mostra as coordenadas em que o jogador fica

novaCoord :: (Int,Int) -> Char -> (Int,Int)
novaCoord (x,y) c | c=='R' = (x+1,y)
                  | c=='L' = (x-1,y)
                  | c=='U' = (x,y-1)
                  | c=='D' = (x,y+1)
                  
-- passa coordenadas atuais do jogador para String
-- coordString (novaCoord (coordJogador (coordenadasJ j m)) c)
-- | 'coordString' : função que muda o tipo de coordenadas do jogador

coordString :: (Int, Int) -> String
coordString (x,y) = (show x ++ " " ++ show y)

-- quais powerups o jogador tinha
-- howManyPU (tirarNJogador (jogadorInicio j m))
-- powerupS (howManyPU (tirarNJogador (jogadorInicio j m)))
-- | 'howManyPU' : função que dá informação de quais power ups o jogador tinha

howManyPU :: String -> String
howManyPU [] = []
howManyPU (h:t) = if (h == '+' || h =='!') then h : howManyPU t
                                           else howManyPU t

-- | 'powerupS' : função que coloca um espaço à frente se a lista não for vazia

powerupS :: String -> String
powerupS l = if l == [] then []
                        else " " ++ l
---------------------------------------------------------

------ o que acontece quando o jogador quer ir para um ESPACO VAZIO COM POWER UP
--Diferencas em relacao ao Espaco Vazio sem Power up:
--1. elimina linha que dava a info desse PU
--2. acrescenta + ou ! a frente do jogador
-- | 'espacoVazioCPU' : função que acrescenta informação de power up à frente do jogador

espacoVazioCPU :: [String] -> Char -> Char -> [String]
espacoVazioCPU m j c = eliminalinhaPU m j c

--1. elimina linha que dava a info PU 
-- | 'eliminalinhaPU' : função que elimina linha que dava a informação do power up

eliminalinhaPU :: [String] -> Char -> Char -> [String]
eliminalinhaPU m j c = comparalinha (somaPU m j c) m j c

--compara com todas as linhas do mapa (com a linha da info do pup) e quando aparecer essa, apaga-a
-- | 'comparalinha' : função que apaga as linhas com informação de power ups

comparalinha :: [String] -> [String] -> Char -> Char -> [String]
comparalinha (h:t) m j c = if (h == acrescentouF m j c || h == acrescentouB m j c) 
                           then t  
                           else h : comparalinha t m j c 

---comparalinha ["###","###","! 2 4"] '1' 'U' 

--2.acrescenta + ou ! a frente da informacao jogador
-- | 'somaPU' : função que acrescenta "+" ou "!" à frente do jogador

somaPU :: [String] -> Char -> Char -> [String]
somaPU m j c = if eBombOrFlame m j c then substituiBomb m j c
                                     else substituiFlame m j c

-- ve se ele apanhou + ou !
-- | 'eBombOrFlame' : função que testa se o jogador apanhou "+" ou "!"

eBombOrFlame :: [String] -> Char -> Char -> Bool
eBombOrFlame m j c = if elem (acrescentouB m j c) (powerUp m) then True --apanhou bomb
                                                              else False  --apanhou flame

-- acrescenta '+' à frente da nova coordenada do jogador
-- acrescentaB (coordString (novaCoord (coordJogador (coordenadasJ j m)) c)) = acrescentouB m j c
-- | 'acrescentaB' : função que acrescenta "+" à frente da nova coordenada do jogador

acrescentaB :: String -> String
acrescentaB nc = '+':' ': nc

-- | 'acrescentouB' : função que acrescenta ou não power ups
acrescentouB :: [String] -> Char -> Char -> String
acrescentouB m j c = acrescentaB (coordString (novaCoord (coordJogador (coordenadasJ j m)) c))

-- acrescenta '!' à frente da nova coordenada do jogador
-- acrescentaF (coordString (novaCoord (coordJogador (coordenadasJ j m)) c)) = acrescentouF m j c
-- | 'acrescentaF' : função que acrescenta "!" à frente da nova coordenada do jogador
acrescentaF :: String -> String
acrescentaF nc = '!':' ': nc

-- | 'acrescentouF' : função que acrescenta ou não power ups

acrescentouF :: [String] -> Char -> Char -> String
acrescentouF m j c = acrescentaF (coordString (novaCoord (coordJogador (coordenadasJ j m)) c))

--------
--- troca a nova linha com + pela "desatualizada"
---substituiBomb m j c
-- | 'substituiBomb' : função que atualiza informação
substituiBomb :: [String] -> Char -> Char -> [String]
substituiBomb (h:t) j c = if (h == (jogadorInicio j (h:t))) then somaBomb (h:t) c j : t
                                                            else h : substituiBomb t j c

--soma + na linha sobre info do jogador
-- | 'somaBomb' :função que adiciona "+" na informação do jogador
somaBomb :: [String] -> Char -> Char -> String
somaBomb m c j | somaBombAux (novaInfoJ m c j) = somaBombAux2 (novaInfoJ m c j) -- se tiver !, adiciona + antes
               | somaBombAux4 (novaInfoJ m c j) = somaBombAux3 m c j -- se nao tiver ! mas tiver +, adiciona + no fim
               | otherwise = somaBombAux5 m c j --se nao tiver ! nem +, adiciona " " ++ "+"


--ir a "novaInfo do Jogador" e quando encontrar ! por antes +
-- | 'somaBombAux' : @função auxiliar@ de "somaBomb"
somaBombAux :: String -> Bool
somaBombAux ln = elem '!' ln

-- | 'somaBombAux2' : @função auxiliar@ de "somaBombAux"
somaBombAux2 :: String -> String
somaBombAux2 (a:b) = if (a == '!') then '+' : a : b
                                   else a : somaBombAux2 b

-- | 'somaBombAux3' : @função auxiliar@ de "somaBombAux2"
somaBombAux3 :: [String] -> Char -> Char -> String
somaBombAux3 m c j = (novaInfoJ m c j) ++ "+"

--ver se tem + para, se tiver adicionar + no fim, se nao , adicionar " " ++ "+"
-- | 'somaBombAux4' : @função auxiliar@ de "somaBombAux3"

somaBombAux4 :: String -> Bool
somaBombAux4 ln = elem '+' ln

-- | 'somaBombAux5' : @função auxiliar@ de "somaBombAux4"

somaBombAux5 :: [String] -> Char -> Char -> String
somaBombAux5 m c j = (novaInfoJ m c j) ++ " " ++ "+"
--------

--- troca a nova linha com ! pela "desatualizada"
---substituiFlame m j c
-- | 'substituirFlame' : função que atualiza a informação da flame

substituiFlame :: [String] -> Char -> Char -> [String]
substituiFlame (h:t) j c = if (h == (jogadorInicio j (h:t))) then somaFlame (h:t) c j : t
                                                             else h : substituiFlame t j c

-- linha nova : somaFlame m c j
-- linha velha : jogadorInicio j m

--soma ! a frente do jogador 
-- | 'somaFlame' : função que coloca "!" ou não à frente do jogador

somaFlame :: [String] -> Char -> Char -> String
somaFlame m c j = if not (somaBombAux (novaInfoJ m c j) || somaBombAux4 (novaInfoJ m c j))
                  then (novaInfoJ m c j) ++ " " ++ "!"  -- se o jogador nao tiver PU entao, adiciona um espaço e !
                  else (novaInfoJ m c j) ++ "!" -- se o jogador ja tiver algum PU, entao adiciona ! no final

---linha sobre info atualizada do jogador 
-- | 'novaInfoJ' : função que devolve a informação do jogador j atualizada
novaInfoJ :: [String] -> Char -> Char -> String
novaInfoJ m c j = jogadorInicio j (espacoVazio m c j)

-------------------------------------------------------

--Onde estao os powerups formato [(Int,Int)]
--coordenadasPU (cordenadassPU m)
-- | 'coordenadasPU' : função que devolve as coordenadas dos power ups no tipo [(Int,Int)]

coordenadasPU :: [String] -> [(Int,Int)]
coordenadasPU [] = []
coordenadasPU (h:t) = coordJogador h : coordenadasPU t 

--Onde estão os power ups?
--Dada a lista do estado atual do jogo, a funcao "coordenadassPU m" devolve as coordenadas de todas as PU

-- | 'coordenadassPU' : função que dado um estado de jogo, devolve a informação relativa aos __power ups__
coordenadassPU :: [String] -> [String]
coordenadassPU m = tirarPUeCV (powerUp m)

-- | 'tirarPUeCV' : @função auxiliar@ de "coordenadassPU"
tirarPUeCV :: [String] -> [String]
tirarPUeCV [] = []
tirarPUeCV (h:t) = (auxTirar h : tirarPUeCV t) 

-- | 'auxTirar' : @função auxiliar@ de "tirarPUeCV"
auxTirar :: String -> String
auxTirar [] = []
auxTirar (pu:v:t) = t

-- | 'powerUp' : função deixa a informação sobre power ups
powerUp :: [String] -> [String]
powerUp [] = []
powerUp (h:t) = if (head h == '+') || (head h == '!') then (h:powerUp t)
                                                      else powerUp t


------ Caso das bombas
-- não pode ser colocada mais de 1 bomba na mesma posicao
-- | 'poeBomba' : função que vê em que síios podem ser postas as bombas

poeBomba :: [String] -> Char -> [String]
poeBomba m j = if (podeCBomba m j && podeColocarMaisB m j) then ordenaBB m j 
                                                           else m

--pode ser colocada 1 bomba nesta posicao?
-- | 'podeCBomba' : função que /testa se a bomba pode ser colocada nessa posição/

podeCBomba :: [String] -> Char -> Bool
podeCBomba m j = not (elem (coordenadasJ j m) (coordBombas m))

-- coordenadas onde o jogador vai por a Bomba = coordenadasJ j m

--coordenadas das bombas que já estao colocadas 
-- | 'coordBombas' : função que dá informação sobre as bombas colocadas

coordBombas :: [String] -> [String]
coordBombas m = coordBombasAux (procuraInfoBomba m)

-- | 'coordBombasAux' : @função auxiliar@ de "coordBombas"
coordBombasAux :: [String] -> [String]
coordBombasAux [] = []
coordBombasAux (h:t) = linha1B h : coordBombasAux t

--coordenada de uma bomba ja colocada
---so pode ter um espaco , ou seja, so deixa a info ate ao 2o espaco 
-- | 'linha1B' : função que dá as coordenadas de uma bomba já colocada

linha1B :: String -> String
linha1B l = tiraRB (tira10 (tiraBeE l))

-- | 'tiraRB' : @função auxiliar@ de "linha1B"
tiraRB :: String -> String     -- "7 7"
tiraRB (h:t) = if (h == ' ') then h : tiraRB1 t   
                             else h : tiraRB t

-- | 'tiraRB1' : @função auxiliar@ de "tiraRB1"
tiraRB1 :: String -> String
tiraRB1 (h:t) = if (h == ' ') then []
                              else h : tiraRB1 t                                                   

-- | 'tira10' : função que retira o raio da bomba à informação dada sobre a mesma

tira10 :: String -> String    --"7 7 1 1"
tira10 l1 = init (init (init l1))

-- | 'tiraBeE' : função que retira o primeiro e segundo elementos
tiraBeE :: String -> String   -- "7 7 1 1 10"
tiraBeE (x:y:z) = z 

--info das bombas que foram colocadas (linha com coordenadas das bombas colocadas)
--- procuraInfoBomba m
-- | 'procuraInfoBomba' : função que dá informação das bombas que foram colocadas

procuraInfoBomba :: [String] -> [String]
procuraInfoBomba [] = []
procuraInfoBomba (h:t) = if head h == '*' then h : procuraInfoBomba t
                                          else procuraInfoBomba t

-----------
---jogador que só tem x power ups bomb só pode plantar no total x+1 bombas
-- | 'podeColocarMaisB' : função que /apenas deixa o jogador plantar consoante os power ups bomb que tem/

podeColocarMaisB :: [String] -> Char -> Bool
podeColocarMaisB m j = podeColocarMaisBaux (quantasPUB m j) (quantasColocadas m j)

-- | 'podeColocarMaisBaux' : @função auxiliar@ de "podeColocarMaisB"
podeColocarMaisBaux :: Int -> Int -> Bool
podeColocarMaisBaux a b = if a <= b then False 
                                    else True

---quantas bombas pode plantar o jogador j ? (power ups bomba o jogador j tem + 1)
-- | 'quantasPUB' : funçãpo que mostra quantas bombas pode um jogador plantar

quantasPUB :: [String] -> Char -> Int
quantasPUB m j = quantasPUBaux (jogadorInicio j m)

-- | 'quantasPUBaux' : função auxiliar de "quantasPUB"
quantasPUBaux :: String -> Int
quantasPUBaux [] = 1
quantasPUBaux (h:t) = if (h=='+') then 1+quantasPUBaux t 
                                  else quantasPUBaux t

---quantas bombas o jogador j tem colocadas?
-- | 'quantasColocadas' : função que mostra as coordenadas dos jogadores e a quantidade de bombas que tem colocadas

quantasColocadas :: [String] -> Char -> Int
quantasColocadas m j = qtsvezes (digitToInt j) (numerosIntJB m)

-- | 'qtsvezes' : função que dá o número de vezes que o jogador colocou

qtsvezes :: Int -> [Int] -> Int 
qtsvezes x [] = 0
qtsvezes x (h:t) = if (x==h) then 1 + qtsvezes x t 
                             else qtsvezes x t
----------------

-- "ordenar info das bombas consoante o numero do jogador"
-- "ordenar info das bombas consoante AS COORDENADAS"
-- se nao houver mais bombas colocadas poelinhaplanta 
-- | 'ordenaBB' : função que /ordena a informação das bombas consoante as coordenadas/
ordenaBB :: [String] -> Char -> [String]
ordenaBB m j = if (procuraInfoBomba m == []) then poelinhaplanta m j
                                             else colocaLinha111 m j

blibli :: [String] -> Char -> Int
blibli m j = bleble m + (blabla (coordJogador (coordenadasJ j m)) (coordBombasInt m))

--funcao que dá o numero de linhas até ao primeiro *
bleble :: [String] -> Int
bleble [] = 0
bleble (h:t) = if (head h == '*') then 0
                                  else 1 + bleble t

--funcao que dá a posicao que a nova informacao se deve situar entre as informacoes das bombas ja colocadas
blabla :: (Int,Int) -> [(Int,Int)] -> Int
blabla (x,y) [] = 1
blabla (x,y) ((a,b):cs) = if (y < b) || ((y == b) && (x < a)) then 1
                                                              else 1 + blabla (x,y) cs


----coloca a linha info nova bomba no mapa no sítio certo
-- | 'colocaLinha111' : função que /coloca a linha com informação da bomba no sitio certo/
colocaLinha111 :: [String] -> Char -> [String]
colocaLinha111 m j = colocaLinha11 m j (linhaplanta m j) (blibli m j)

-- | 'colocaLinha11' : @função que auxilía@ na função "colocaLinha111"
colocaLinha11 :: [String] -> Char -> String -> Int -> [String]
colocaLinha11 (h:t) j lp x = if (x==1) then lp : h : t
                                       else h : colocaLinha11 t j lp (x-1)


--coordenadas do sitio onde o jogador quer por a bomba --> coordJogador (coordenadasJ j m)

--da coordenadas das bombas que estão plantadas em [(Int, Int)] ----> coordBombas m [String]
-- | 'coordBombasInt' : função que dá as coordenadas das bombas que estão plantadas em [String] para [(Int,Int)] 
coordBombasInt :: [String] -> [(Int,Int)]
coordBombasInt m = coordBombasIntaux (coordBombas m)

-- | 'coordBombasIntaux' : @função auxiliar@ na "coordBombasInt"
coordBombasIntaux :: [String] -> [(Int,Int)]
coordBombasIntaux [] = []
coordBombasIntaux (h:t) = coordJogador h : coordBombasIntaux t 

--passa numerosJB m de [String] para [Int]
-- | 'numerosIntJB' : função que passa a função "numerosJB" de [String] para [Int]
numerosIntJB :: [String] -> [Int]
numerosIntJB m = numerosIntJB1 (numerosJB m) 

-- | 'numerosIntJB1' : @função que dá auxílio@ na mudança de tipos da função "numerosJB"
numerosIntJB1 :: [String] -> [Int]
numerosIntJB1 [] = []
numerosIntJB1 [" "] = []
numerosIntJB1 (h:t) = (read h :: Int) : numerosIntJB1 t 

--lista com os numeros dos jogadores que tem bomba colocada
-- | 'numerosJB' : função que coloca a lista com os números do jogadores que colocaram bombas

numerosJB :: [String] -> [String]
numerosJB m = (numerosJBAux (coordBombas m) (procuraInfoBomba m))

-- | 'numerosJBAux' : @função que auxilía@ na funcao "numerosJB"

numerosJBAux :: [String] -> [String] -> [String]
numerosJBAux [] _ = []
numerosJBAux (a:b) (c:d) = [(head (drop ((length a)+1) (tiraBeE c)))] : numerosJBAux b d 

--------

---funcao que poe a linha planta no mapa (simplificada)
-- | 'poelinhaplanta' : função que __coloca a "linhaplanta" no mapa (antes da informacao dos jogadores)__
poelinhaplanta :: [String] -> Char -> [String]
poelinhaplanta m j = poelinhaplantaAux m j (linhaplanta m j) 

--poe a linhaplanta no mapa
-- | 'poelinhaplantaAux' : @função que auxilia@ a funcao "poelinhaplanta"

poelinhaplantaAux :: [String] -> Char -> String -> [String]
poelinhaplantaAux (h:t) j lp = if (head h == '0' || head h == '1' || head h == '2' || head h == '3') 
                               then lp:h:t
                               else h: poelinhaplantaAux t j lp

--linha nova que da a informacao sobre a colocacao da bomba 
-- | 'linhaplanta' : função que __devolve uma linha nova com a informação sobre a colocação de uma bomba__

linhaplanta :: [String] -> Char -> String
linhaplanta m j = "*" ++ " " ++ (coordenadasJ j m) ++ " " ++ [j] ++ " " ++ raioBombaa m j ++ " " ++ "10"

--passa raio (Int) para raio String
-- | 'raioBomb' : função que altera o o tipo do raio de [String] para String

raioBombaa :: [String] -> Char -> String
raioBombaa m j = show (raioB m j) 

---Vê quantos power up flame tem o jogador j
---raioB (linha que dá informacao do jogador j)
-- | 'raioB' : função que fornece a quantidade de power ups do tipo flame
raioB :: [String] -> Char -> Int
raioB m j = raioBAux (jogadorInicio j m)

-- | 'raioBAux' : função auxiliar que fornece o raio que terá a bomba, ou seja, a quantidade de power ups do tipo flame

raioBAux :: String -> Int
raioBAux [] = 1
raioBAux (h:t) = if (h=='!') then 1+raioBAux t 
                             else raioBAux t