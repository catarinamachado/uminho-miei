module Tarefa6_li1g172 where
import Data.Char
import Bomberman (contador, listajogadores, listapu, downCoordenadas, upCoordenadas, leftCoordenadas, rightCoordenadas)

type Coordenadas = (Int,Int)
type Mapa = [String]
type NBot = Int
type TimeLeft = Int
type Raio = Int
type DimensaoMapa = Int

{-|
__Description__ : Módulo Haskell que contém a função que implementa uma estratégia de combate (função 'bot').

Este módulo contém funções recursivas e não recursivas, com o intuito de __devolver o comando__ do bot que joga bomberman automaticamente.
-}

bot :: Mapa -> NBot -> TimeLeft -> Maybe Char  --dado: mapa do estado atual (sem power ups); numero do jogador e instantes que falta ---> Nothing; 'B';'U';'D';'L';'R'
bot m j t = (analisaParede m t j)

-- | A função @coordBot@ dá como resposta as __coordenadas__ do mapa em que o /bot/ se encontra.
coordBot :: Mapa -> NBot -> Coordenadas
coordBot m j = coordBotAux (coordEmString m j)
        where coordBotAux (x:y:vazio) = ((read x) :: Int, (read y) :: Int)

coordEmString :: Mapa -> NBot -> [String]
coordEmString m j = coordEmStringAux (tail (words (linhainfoBot m j))) 
        where coordEmStringAux (x:y:xs) = [x] ++ [y]

-- | A função @linhainfoBot@ dá como resposta a linha da informação do /bot/.
linhainfoBot :: Mapa -> NBot -> String
linhainfoBot (h:t) j = if ((intToDigit j) == head h) then h
                                                     else linhainfoBot t j

--------

-- | A função @coordAdversarios@ dá como resposta as __coordenadas__ dos outros jogadores (__adversários__).
coordAdversarios :: Mapa -> NBot -> [Coordenadas]
coordAdversarios m j = toInt (listajog m j)

toInt :: [[String]] -> [Coordenadas]
toInt [] = []
toInt (h:t) = toIntAux h : toInt t
    where toIntAux (x:y:vazio) = ((read x) :: Int, (read y) :: Int)

listajog :: Mapa -> NBot -> [[String]]
listajog m j = listajog1 (tail1 m j)

listajog1 :: [[String]] -> [[String]]
listajog1 [] = []
listajog1 (h:t) = listajog1Aux h : listajog1 t
    where listajog1Aux (x:y:zs) = [x] ++ [y]

tail1 :: Mapa -> NBot -> [[String]]
tail1 m j = tail1Aux (words1 m j)
    where tail1Aux [] = []
          tail1Aux (h:t) = tail h : tail1Aux t

words1 :: Mapa -> NBot -> [[String]]
words1 m j = words1Aux (linhasinfoAdversarios m j)
    where words1Aux [] = []
          words1Aux (h:t) = (words h) : words1 t j

-- | A função @linhainfoAdversarios@ dá como resposta as __linhas das informações dos adversários__.
linhasinfoAdversarios :: Mapa -> NBot -> [String]
linhasinfoAdversarios m j = linhasinfoAdvAux (eliminainfoBot m j)

-- | A função @linhasinfoAdvAux@ é a principal função auxiliar da função 'linhasinfoAdversarios'.
linhasinfoAdvAux :: [String]-> [String]
linhasinfoAdvAux [] = []
linhasinfoAdvAux (h:t) = if (head h == '0' || head h == '1' || head h == '2' || head h == '3') 
                         then h : linhasinfoAdvAux t 
                         else linhasinfoAdvAux t 

-- | A função @eliminainfoBot@ elimina do mapa a linha respetiva à info do /bot/.
eliminainfoBot :: Mapa -> NBot -> Mapa 
eliminainfoBot [] _ = []
eliminainfoBot (h:t) j = if (head h == intToDigit j) then t
                                                     else h: eliminainfoBot t j
-----------

-- | A função @dimensaomapa@ dá como resposta a __dimensão do mapa__ do estado de jogo em questão.
dimensaomapa :: Mapa -> Int
dimensaomapa (h:t) = length h

-----------

-- | A função @listabomb@ diz as __coordenadas das bombas__ que estão plantadas.
listabomb :: Mapa -> [Coordenadas]
listabomb m = toInt (listabomb1 m)

listabomb1 :: Mapa -> [[String]]
listabomb1 m = listajog1 (tail2 m)

tail2 :: Mapa -> [[String]]
tail2 m = tail2Aux (words2 m)
    where tail2Aux [] = []
          tail2Aux (h:t) = tail h : tail2Aux t


words2 :: Mapa -> [[String]]
words2 m = words2Aux (infoBombas m)
    where words2Aux [] = []
          words2Aux (h:t) = (words h) : words2Aux t

-- | A função @infoBombas@ pega nas linhas relativas às /informações das bombas/.
infoBombas :: Mapa -> [String]
infoBombas [] = []
infoBombas (h:t) = if (head h == '*') then h : infoBombas t
                                      else infoBombas t

-- | A função @raioBomba@ dá como resposta os __raios__ das bombas plantadas.
raioBomba :: Mapa -> [Raio]
raioBomba m = raioBombaAux (raioBomba1 m)
    where raioBombaAux [] = []
          raioBombaAux (h:t) = (read h :: Int) : raioBombaAux t

-- | A função @raioBomba1@ dá como resposta os __raios__ das bombas plantadas em /[String]/.
raioBomba1 :: Mapa -> [String]
raioBomba1 m = raioBomba1Aux (words2 m)
    where raioBomba1Aux [] = []
          raioBomba1Aux (h:t) = (h !! 4) : raioBomba1Aux t

-- | A função @instantesBomba@ diz os __instantes que faltam para as bombas plantadas explodirem__.
instantesBomba :: Mapa -> [TimeLeft]
instantesBomba m = instantesBombaAux (instantesBomba1 m)
    where instantesBombaAux [] = []
          instantesBombaAux (h:t) = (read h :: Int) : instantesBombaAux t

-- | A função @instantesBomba1@ é a principal função auxiliar da função 'instantesBomba'. 
instantesBomba1 :: Mapa -> [String]
instantesBomba1 m = instantesBomba1Aux (words2 m)
    where instantesBomba1Aux [] = []
          instantesBomba1Aux (h:t) = (h !! 5) : instantesBomba1Aux t         

----------

-- | A função @listapudestapados@ dá a __lista dos powerups__ que se encontram destapados (/tijolo que o encobria já foi explodido/).
listapudestapados :: Mapa -> [Coordenadas]
listapudestapados m = listapudestapadosAux m (listapu m)
  where listapudestapadosAux m [] = []
        listapudestapadosAux m ((x,y):zs) = if ((m !! y !! x) == ' ') then ((x,y):(listapudestapadosAux m zs))
                                                                      else listapudestapadosAux m zs


----------

-- | A função @down@ diz o que está /abaixo/ do bot ('#',' ' ou '?').
down :: Mapa -> NBot -> Char --ve o que está abaixo do bot
down m j = downAux (coordBot m j) m
    where downAux (x,y) m = m !! (y+1) !! x

-- | A função @up@ diz o que está /acima/ do bot ('#',' ' ou '?').
up :: Mapa -> NBot -> Char  --ve o que está acima do bot
up m j = upAux (coordBot m j) m
    where upAux (x,y) m = (m !! (y-1) !! x)

-- | A função @left@ diz o que está /à esquerda/ do bot ('#',' ' ou '?').
left :: Mapa -> NBot -> Char  --ve o que está à esquerda do bot
left m j = leftAux (coordBot m j) m
    where leftAux (x,y) m = (m !! y !! (x-1))

-- | A função @right@ diz o que está /à direita/ do bot ('#',' ' ou '?').
right :: Mapa -> NBot -> Char  --ve o que está à direita do bot
right m j = rightAux (coordBot m j) m
    where rightAux (x,y) m = (m !! y !! (x+1))

------------

-- | A função @downc@ diz qual é a __coordenada__ que está imediatamente /abaixo/ da posição do bot.
downc :: Mapa -> NBot  -> Coordenadas
downc m j = downcAux (coordBot m j)

-- | A função @downcAux@ diz qual é a __coordenada__ que está imediatamente /abaixo/ de uma determinada coordenada.
downcAux :: Coordenadas -> Coordenadas
downcAux (x,y)  = (x,(y+1))

-- | A função @upc@ diz qual é a __coordenada__ que está imediatamente /acima/ da posição do bot.
upc :: Mapa -> NBot  -> Coordenadas
upc m j = upcAux (coordBot m j)

-- | A função @upcAux@ diz qual é a __coordenada__ que está imediatamente /acima/ de uma determinada coordenada.
upcAux :: Coordenadas -> Coordenadas
upcAux (x,y) = (x,(y-1))

-- | A função @leftc@ diz qual é a __coordenada__ que está imediatamente /à esquerda/ da posição do bot.
leftc :: Mapa -> NBot  -> Coordenadas
leftc m j = leftcAux (coordBot m j)

-- | A função @leftcAux@ diz qual é a __coordenada__ que está imediatamente /à esquerda/ de uma determinada coordenada.
leftcAux :: Coordenadas -> Coordenadas
leftcAux (x,y) = ((x-1),y)

-- | A função @rightc@ diz qual é a __coordenada__ que está imediatamente /à direita/ da posição do bot.
rightc :: Mapa -> NBot  -> Coordenadas
rightc m j = rightcAux (coordBot m j)

-- | A função @rightcAux@ diz qual é a __coordenada__ que está imediatamente /à direita/ de uma determinada coordenada.
rightcAux :: Coordenadas -> Coordenadas
rightcAux (x,y) = ((x+1),y)

------

-- | A função @oqueestaD@ diz o que está x células __abaixo__ de uma determinada coordenada.
oqueestaD :: Mapa -> Coordenadas -> Raio -> Char 
oqueestaD m (x,y) r = (m !! (y+r) !! x)

-- | A função @oqueestaU@ diz o que está x células __acima__ de uma determinada coordenada.
oqueestaU :: Mapa -> Coordenadas -> Raio -> Char 
oqueestaU m (x,y) r = (m !! (y-r) !! x)

-- | A função @oqueestaL@ diz o que está x células __à esquerda__ de uma determinada coordenada.
oqueestaL :: Mapa -> Coordenadas -> Raio -> Char 
oqueestaL m (x,y) r = (m !! y !! (x-r))

-- | A função @oqueestaR@ diz o que está x células __à direita__ de uma determinada coordenada.
oqueestaR :: Mapa -> Coordenadas -> Raio -> Char 
oqueestaR m (x,y) r = (m !! y !! (x+r))

-------------

{-| A função @downpormenor@ vê com mais pormenor, o que está x coordenadas /abaixo/ de uma determinada coordenada. 

Respostas possíveis:

* Pedra;
* Tijolo;
* Powerup;
* Bomb;
* Jogador;
* Nada.
-}

downpormenor :: (Int,Int) -> Raio -> [String] -> String
downpormenor (x,y) r m | ((oqueestaD m (x,y) r) == '#') = "pedra"
                       | ((oqueestaD m (x,y) r) == '?') = "tijolo"
                       | ((oqueestaD m (x,y) r) == ' ') = espacoVazioD (x,y) r m

-- | A função @espacoVazioD@ é a principal função auxiliar da função 'downpormenor'. 
espacoVazioD :: (Int,Int) -> Raio -> [String] -> String
espacoVazioD (x,y) r m | (elem (downCoordenadas (x,y) r) (listapu m)) = "pu"
                       | (elem (downCoordenadas (x,y) r) (listabomb m)) = "bomb"
                       | (elem (downCoordenadas (x,y) r) (listajogadores m)) = "jogador"
                       | otherwise = "nada"

{-| A função @uppormenor@ vê com mais pormenor, o que está x coordenadas /acima/ de uma determinada coordenada. 

Respostas possíveis:

* Pedra;
* Tijolo;
* Powerup;
* Bomb;
* Jogador;
* Nada.
-}

uppormenor :: (Int,Int) -> Raio -> [String] -> String
uppormenor (x,y) r m | ((oqueestaU m (x,y) r) == '#') = "pedra"
                     | ((oqueestaU m (x,y) r) == '?') = "tijolo"
                     | ((oqueestaU m (x,y) r) == ' ') = espacoVazioU (x,y) r m
 
-- | A função @espacoVazioU@ é a principal função auxiliar da função 'uppormenor'. 
espacoVazioU :: (Int,Int) -> Raio -> [String] -> String
espacoVazioU (x,y) r m | (elem (upCoordenadas (x,y) r) (listapu m)) = "pu"
                       | (elem (upCoordenadas (x,y) r) (listabomb m)) = "bomb"
                       | (elem (upCoordenadas (x,y) r) (listajogadores m)) = "jogador"
                       | otherwise = "nada"

{-| A função @leftpormenor@ vê com mais pormenor, o que está x coordenadas /à esquerda/ de uma determinada coordenada. 

Respostas possíveis:

* Pedra;
* Tijolo;
* Powerup;
* Bomb;
* Jogador;
* Nada.
-}

leftpormenor :: (Int,Int) -> Raio -> [String] -> String
leftpormenor (x,y) r m | ((oqueestaL m (x,y) r) == '#') = "pedra"
                       | ((oqueestaL m (x,y) r) == '?') = "tijolo"
                       | ((oqueestaL m (x,y) r) == ' ') = espacoVazioL (x,y) r m
 
-- | A função @espacoVazioL@ é a principal função auxiliar da função 'leftpormenor'. 
espacoVazioL :: (Int,Int) -> Raio -> [String] -> String
espacoVazioL (x,y) r m | (elem (leftCoordenadas (x,y) r) (listapu m)) = "pu"
                       | (elem (leftCoordenadas (x,y) r) (listabomb m)) = "bomb"
                       | (elem (leftCoordenadas (x,y) r) (listajogadores m)) = "jogador"
                       | otherwise = "nada"

{-| A função @rightpormenor@ vê com mais pormenor, o que está x coordenadas /à direita/ de uma determinada coordenada. 

Respostas possíveis:

* Pedra;
* Tijolo;
* Powerup;
* Bomb;
* Jogador;
* Nada.
-}

rightpormenor :: (Int,Int) -> Raio -> [String] -> String
rightpormenor (x,y) r m | ((oqueestaR m (x,y) r) == '#') = "pedra"
                        | ((oqueestaR m (x,y) r) == '?') = "tijolo"
                        | ((oqueestaR m (x,y) r) == ' ') = espacoVazioR (x,y) r m
 
-- | A função @espacoVazioR@ é a principal função auxiliar da função 'rightpormenor'.
espacoVazioR :: (Int,Int) -> Raio -> [String] -> String
espacoVazioR (x,y) r m | (elem (rightCoordenadas (x,y) r) (listapu m)) = "pu"
                       | (elem (rightCoordenadas (x,y) r) (listabomb m)) = "bomb"
                       | (elem (rightCoordenadas (x,y) r) (listajogadores m)) = "jogador"
                       | otherwise = "nada"


------------

{-| A função @chamasBombas@ dá como resultado a lista das coordenadas que as __chamas das bombas__ atingem. 

Tendo em consideração que as chamas não atravessam:

* As /pedras/;
* Os /tijolos/;
* Os /espaços vazios com power up/.
-}
chamasBombas :: Mapa -> [Coordenadas]
chamasBombas m = eliminaRepetidos $ tiraCoordPedras m (chamasBombasAux m (listabomb m) (listaRaios m))

{-| A função @chamasBombasAux@ é a principal função auxiliar da função 'chamasBombas'.

Esta função concatena o resultado das 4 primeiras (por vezes únicas, depende do raio de destruição da bomba) direções
(cima, baixo, esquerda e direita) que as chamas percorrem; e tem como passo recursivo, percorrer as seguintes 4 direções.
-}
chamasBombasAux :: Mapa -> [Coordenadas] -> [[Int]] -> [Coordenadas]
chamasBombasAux _ [] [] = []
chamasBombasAux m ((x,y):cs) (r:rs) = [(x,y)] ++ (eliminaChamasD m (x,y) r) ++ (eliminaChamasU m (x,y) r) ++ 
                      (eliminaChamasL m (x,y) r) ++ (eliminaChamasR m (x,y) r) ++ (chamasBombasAux m cs rs)


{-| A função @eliminaChamasD@, tem como parâmetros:

1. O mapa;
2. A coordenada de uma bomba que explode;
3. A lista do raio que essa bomba alcança (de 1 até ao raio da bomba).

Esta função @pára de contabilizar as coordenadas das chamas das bombas@ a partir do momento em que encontra uma pedra, 
um tijolo, ou um espaço vazio com power up. 

Dá como resultado as coordenadas que as chamas das bombas atingem, neste caso inferiores (__para baixo__) do local da bomba.  
-}
eliminaChamasD :: Mapa -> Coordenadas -> [Raio] -> [Coordenadas]
eliminaChamasD m (x,y) [] = []
eliminaChamasD m (x,y) (r:rs) = if ((oqueestaD m (x,y) r) == '#' || (oqueestaD m (x,y) r) == '?' || (downpormenor (x,y) r m == "pu"))
                                then [(x,y+r)]
                                else (x,y+r): eliminaChamasD m (x,y) rs

{-| A função @eliminaChamasU@, tem como parâmetros:

1. O mapa;
2. A coordenada de uma bomba que explode;
3. A lista do raio que essa bomba alcança (de 1 até ao raio da bomba).

Esta função @pára de contabilizar as coordenadas das chamas das bombas@ a partir do momento em que encontra uma pedra, 
um tijolo, ou um espaço vazio com power up. 

Dá como resultado as coordenadas que as chamas das bombas atingem, neste caso superiores (__para cima__) do local da bomba.  
-}
eliminaChamasU :: Mapa -> Coordenadas -> [Raio] -> [Coordenadas]
eliminaChamasU m (x,y) [] = []
eliminaChamasU m (x,y) (r:rs) = if ((oqueestaU m (x,y) r) == '#' || (oqueestaU m (x,y) r) == '?' || (uppormenor (x,y) r m == "pu"))
                                then [(x,y-r)]
                                else (x,y-r): eliminaChamasU m (x,y) rs

{-| A função @eliminaChamasL@, tem como parâmetros:

1. O mapa;
2. A coordenada de uma bomba que explode;
3. A lista do raio que essa bomba alcança (de 1 até ao raio da bomba).

Esta função @pára de contabilizar as coordenadas das chamas das bombas@ a partir do momento em que encontra uma pedra, 
um tijolo, ou um espaço vazio com power up. 

Dá como resultado as coordenadas que as chamas das bombas atingem, neste caso __para a esquerda__ do local da bomba.  
-}
eliminaChamasL :: Mapa -> Coordenadas -> [Raio] -> [Coordenadas]
eliminaChamasL m (x,y) [] = []
eliminaChamasL m (x,y) (r:rs) = if ((oqueestaL m (x,y) r) == '#' || (oqueestaL m (x,y) r) == '?' || (leftpormenor (x,y) r m == "pu"))
                                then [(x-r,y)]
                                else (x-r,y): eliminaChamasL m (x,y) rs

{-| A função @eliminaChamasR@, tem como parâmetros:

1. O mapa;
2. A coordenada de uma bomba que explode;
3. A lista do raio que essa bomba alcança (de 1 até ao raio da bomba).

Esta função @pára de contabilizar as coordenadas das chamas das bombas@ a partir do momento em que encontra uma pedra, 
um tijolo, ou um espaço vazio com power up. 

Dá como resultado as coordenadas que as chamas das bombas atingem, neste caso __para a direita do local da bomba.  
-}
eliminaChamasR :: Mapa -> Coordenadas -> [Raio] -> [Coordenadas]
eliminaChamasR m (x,y) [] = []
eliminaChamasR m (x,y) (r:rs) = if ((oqueestaR m (x,y) r) == '#' || (oqueestaR m (x,y) r) == '?' || (rightpormenor (x,y) r m == "pu"))
                                then [(x+r,y)]
                                else (x+r,y): eliminaChamasR m (x,y) rs

-- | A função @listaRaios@ diz as /listas dos raios/ das __bombas__ plantadas.
listaRaios :: Mapa -> [[Int]]
listaRaios m = listaRaiosAux (raioBomba m)  
    where listaRaiosAux [] = []
          listaRaiosAux (h:t) = [1..h] : listaRaiosAux t

{-| A função @tiraCoordPedras@, tem como parâmetros o mapa e a lista das coordenadas que as chamas das bombas atingem, 
e /retira dessa lista as coordenadas que correspondem a __pedras__/.
-}
tiraCoordPedras :: Mapa -> [Coordenadas] -> [Coordenadas]
tiraCoordPedras m [] = []
tiraCoordPedras m ((x,y):zs) = if ((m !! y !! x) == '#') then tiraCoordPedras m zs
                                                         else [(x,y)] ++ tiraCoordPedras m zs  

{-| Na função @eliminaRepetidos@, dada uma lista de coordenadas, elimina dessa lista as coordenadas que se encontram /repetidas/.
(Isso acontece quando bombas diferentes explodem e atingem as mesmas células).
-}
eliminaRepetidos :: [Coordenadas] -> [Coordenadas]
eliminaRepetidos [] = []
eliminaRepetidos ((x,y):zs) = if elem (x,y) zs then (x,y):eliminaRepetidos (eliminaCoord (x,y) zs)
                                               else ((x,y):eliminaRepetidos zs)
                                               
-- | A função @eliminaCoord@ é a função auxiliar da função 'eliminaRepetidos'.
eliminaCoord :: Coordenadas -> [Coordenadas] -> [Coordenadas]
eliminaCoord _ [] = []
eliminaCoord (x,y) ((a,b):cs) = if ((x,y)==(a,b)) then eliminaCoord (x,y) cs
                                                  else ((a,b):eliminaCoord (x,y) cs)

-------------

-- | A função @foraDePerigoEm@ calcula as /coordenadas/ em que o bot está __fora de perigo__.
foraDePerigoEm :: [String] -> [Coordenadas]
foraDePerigoEm m = apagaCoord (chamasBombas m) (contador (dimensaomapa m))


-- | A função @apagaCoord@ recebe como parâmetros /2 listas de coordenadas/, e elimina da __2ª__, as coordenadas da __1ª__.
apagaCoord :: [Coordenadas] -> [Coordenadas] -> [Coordenadas]
apagaCoord [] l = l
apagaCoord (x:xs) l = if elem x l then apagaCoord xs (retira x l)
                                  else apagaCoord xs l

-- | A função @retira@ é a função auxiliar da função 'apagaCoord'.
retira :: Coordenadas -> [Coordenadas] -> [Coordenadas]
retira x (y:ys) = if (x == y) then ys
                              else (y: retira x ys)     

-------------

--funcao que estuda se o bot está em perigo ou nao, nas diferentes situaçoes que se poderão suceder

-- | A função @botEmPerigo@ averigua se na __coordenada em que o bot se encontra__, ele está em /perigo/ ou não (se alguma chama atinge essa coordenada).
botEmPerigo :: Mapa -> NBot -> Bool              
botEmPerigo m j = botEmPerigoAux (coordBot m j) (chamasBombas m)
  where botEmPerigoAux cb chamas = elem cb chamas   

-- | A função @botEmPerigoD@ averigua se na __coordenada abaixo do bot__, ele ficará em /perigo/ ou não (se alguma chama atinge essa coordenada).
botEmPerigoD :: Mapa -> NBot -> Bool 
botEmPerigoD m j = botEmPerigoDAux (downc m j) (chamasBombas m)
  where botEmPerigoDAux db chamas = elem db chamas   

-- | A função @botEmPerigoT@ averigua se na __coordenada acima do bot__, ele ficará em /perigo/ ou não (se alguma chama atinge essa coordenada).
botEmPerigoU :: Mapa -> NBot -> Bool
botEmPerigoU m j = botEmPerigoUAux (upc m j) (chamasBombas m)
  where botEmPerigoUAux ub chamas = elem ub chamas 

-- | A função @botEmPerigoL@ averigua se na __coordenada à esquerda do bot__, ele ficará em /perigo/ ou não (se alguma chama atinge essa coordenada).
botEmPerigoL :: Mapa -> NBot -> Bool
botEmPerigoL m j = botEmPerigoLAux (leftc m j) (chamasBombas m)
  where botEmPerigoLAux lb chamas = elem lb chamas 

-- | A função @botEmPerigoR@ averigua se na __coordenada à direita do bot__, ele ficará em /perigo/ ou não (se alguma chama atinge essa coordenada).
botEmPerigoR :: Mapa -> NBot -> Bool
botEmPerigoR m j = botEmPerigoRAux (rightc m j) (chamasBombas m) 
  where botEmPerigoRAux rb chamas = elem rb chamas 

--------------------------- INICIO DA ESTRATEGIA --------------------------

--Analisar o caminho à sua volta (as 15 funções, que reduzem logo as opcoes de movimentacao do jogador (exclui as pedras))
-- '#' '?' ' ' -> jogador; powerup; bomb; vazio mesmo

{-| A função @analisaParede@, consoante o __bot esteja rodeado por parede__ (pedra ou tijolo), excluí essas direções, 
e põe o bot a analisar somente as direções para as quais ele pode efetivamente ir. 
-}
analisaParede :: Mapa -> TimeLeft -> NBot -> Maybe Char
analisaParede m t j | ((down m j == '#' || down m j == '?') && (right m j == '#' || right m j == '?') && (left m j == '#' || left m j == '?')) = analisaU m t j
                    | ((down m j == '#' || down m j == '?') && (right m j == '#' || right m j == '?') && (up m j == '#' || up m j == '?')) = analisaL m t j
                    | ((down m j == '#' || down m j == '?') && (up m j == '#' || up m j == '?') && (left m j == '#' || left m j == '?')) = analisaR m t j
                    | ((up m j == '#' || up m j == '?') && (right m j == '#' || right m j == '?') && (left m j == '#' || left m j == '?')) = analisaD m t j
                    | ((down m j == '#' || down m j == '?') && (right m j == '#' || right m j == '?')) = analisaUL m t j
                    | ((down m j == '#' || down m j == '?') && (left m j == '#' || left m j == '?')) = analisaUR m t j
                    | ((down m j == '#' || down m j == '?') && (up m j == '#' || up m j == '?')) = analisaLR m t j
                    | ((right m j == '#' || right m j == '?') && (left m j == '#' || left m j == '?')) = analisaDU m t j
                    | ((right m j == '#' || right m j == '?') && (up m j == '#' || up m j == '?')) = analisaLD m t j
                    | ((left m j == '#' || left m j == '?') && (up m j == '#' || up m j == '?')) = analisaRD m t j
                    | (down m j == '#' || down m j == '?') = analisaULR m t j 
                    | (left m j == '#' || left m j == '?') = analisaDUR m t j 
                    | (right m j == '#' || right m j == '?') = analisaDUL m t j 
                    | (up m j == '#' || up m j == '?') = analisaDLR m t j 
                    | otherwise = analisaDULR m t j --quando nao está cercado por nenhuma pedra

-- | A função @analisaU@ analisa conveniente o que o bot deve fazer quando só tem a direção __"up"__ disponível.
analisaU :: Mapa -> TimeLeft -> NBot ->  Maybe Char 
analisaU m t j | ((botEmPerigo m j) && (botEmPerigoU m j)) = Just 'U'
               | (botEmPerigo m j) = Just 'U'
               | (botEmPerigoU m j) = Nothing
               | otherwise = (tempoATerminarU m t j)

-- | A função @analisaL@ analisa conveniente o que o bot deve fazer quando só tem a direção __"left"__ disponível.
analisaL :: Mapa -> TimeLeft -> NBot -> Maybe Char
analisaL m t j | ((botEmPerigo m j) && (botEmPerigoL m j)) = Just 'L'
               | (botEmPerigo m j) = Just 'L'
               | (botEmPerigoL m j) = Nothing
               | otherwise = (tempoATerminarL m t j)

-- | A função @analisaR@ analisa conveniente o que o bot deve fazer quando só tem a direção __"right"__ disponível.
analisaR :: Mapa -> TimeLeft -> NBot -> Maybe Char
analisaR m t j | ((botEmPerigo m j) && (botEmPerigoR m j)) = Just 'R'
               | (botEmPerigo m j) = Just 'R'
               | (botEmPerigoR m j) = Nothing
               | otherwise = (tempoATerminarR m t j)

-- | A função @analisaD@ analisa conveniente o que o bot deve fazer quando só tem a direção __"down"__ disponível.
analisaD :: Mapa -> TimeLeft -> NBot -> Maybe Char
analisaD m t j | ((botEmPerigo m j) && (botEmPerigoD m j)) = Just 'D'
               | (botEmPerigo m j) = Just 'D'
               | (botEmPerigoD m j) = Nothing
               | otherwise = (tempoATerminarD m t j)

-- | A função @analisaUL@ analisa conveniente o que o bot deve fazer quando só tem as direções __"up"__ e __"left"__ disponíveis.
analisaUL :: Mapa -> TimeLeft -> NBot -> Maybe Char
analisaUL m t j | ((botEmPerigo m j) && (botEmPerigoU m j) && (botEmPerigoL m j)) = (fugirUL m j) --FUGIR!
                | ((botEmPerigo m j) && (botEmPerigoU m j)) = Just 'L'
                | ((botEmPerigo m j) && (botEmPerigoL m j)) = Just 'U'
                | ((botEmPerigoU m j) && (botEmPerigoL m j)) = Nothing
                | (botEmPerigo m j) = Just 'L' -- mas n pode ficar parado; arranjar criterio de decisao.
                | (botEmPerigoU m j) = (tempoATerminarL m t j) -- tem que ir para esquerda ou nada -> criterio? pode por bomba! --colar as outras funcoes aqui
                | (botEmPerigoL m j) = (tempoATerminarU m t j) -- tem que ir para cima ou nada -> criterio? pode por bomba!
                | otherwise = (tempoATerminarUL m t j) 

-- | A função @analisaUR@ analisa conveniente o que o bot deve fazer quando só tem as direções __"up"__ e __"right"__ disponíveis.
analisaUR :: Mapa -> TimeLeft -> NBot -> Maybe Char
analisaUR m t j | ((botEmPerigo m j) && (botEmPerigoU m j) && (botEmPerigoR m j)) = (fugirUR m j) --FUGIR!
                | ((botEmPerigo m j) && (botEmPerigoU m j)) = Just 'R'
                | ((botEmPerigo m j) && (botEmPerigoR m j)) = Just 'U'
                | ((botEmPerigoU m j) && (botEmPerigoR m j)) = Nothing
                | (botEmPerigo m j) =  (procuraPowerUR m j (coordBot m j) 1 2 2)  -- n pode ficar parado; arranjar criterio de decisao.
                | (botEmPerigoU m j) = (tempoATerminarR m t j) -- tem que ir para direita ou nada -> criterio? pode por bomba!
                | (botEmPerigoR m j) = (tempoATerminarU m t j) -- tem que ir para direita ou nada -> criterio? pode por bomba!
                | otherwise = (tempoATerminarUR m t j)

-- | A função @analisaLR@ analisa conveniente o que o bot deve fazer quando só tem as direções __"left"__ e __"right"__ disponíveis.
analisaLR :: Mapa -> TimeLeft -> NBot -> Maybe Char 
analisaLR m t j | ((botEmPerigo m j) && (botEmPerigoL m j) && (botEmPerigoR m j)) = (fugirLR m j) --FUGIR!
                | ((botEmPerigo m j) && (botEmPerigoL m j)) = Just 'R'
                | ((botEmPerigo m j) && (botEmPerigoR m j)) = Just 'L'
                | ((botEmPerigoL m j) && (botEmPerigoR m j)) = Nothing
                | (botEmPerigo m j) =  (procuraPowerLR m j (coordBot m j) 1 2 2)  -- n pode ficar parado; arranjar criterio de decisao.
                | (botEmPerigoL m j) = (tempoATerminarR m t j) -- tem que ir para direita ou nada -> criterio? pode por bomba!
                | (botEmPerigoR m j) = (tempoATerminarL m t j) -- tem que ir para direita ou nada -> criterio? pode por bomba!
                | otherwise = (tempoATerminarLR m t j)

-- | A função @analisaDU@ analisa conveniente o que o bot deve fazer quando só tem as direções __"down"__ e __"up"__ disponíveis.
analisaDU :: Mapa -> TimeLeft -> NBot -> Maybe Char
analisaDU m t j | ((botEmPerigo m j) && (botEmPerigoD m j) && (botEmPerigoU m j)) = (fugirDU m j) --FUGIR!
                | ((botEmPerigo m j) && (botEmPerigoD m j)) = Just 'U'
                | ((botEmPerigo m j) && (botEmPerigoU m j)) = Just 'D'
                | ((botEmPerigoD m j) && (botEmPerigoU m j)) = Nothing
                | (botEmPerigo m j) =  (procuraPowerDU m j (coordBot m j) 1 2 2)  -- mas n pode ficar parado; arranjar criterio de decisao.
                | (botEmPerigoD m j) = (tempoATerminarU m t j) -- tem que ir para esquerda ou nada -> criterio? pode por bomba!
                | (botEmPerigoU m j) = (tempoATerminarD m t j) -- tem que ir para esquerda ou nada -> criterio? pode por bomba!
                | otherwise = (tempoATerminarDU m t j) 

-- | A função @analisaLD@ analisa conveniente o que o bot deve fazer quando só tem as direções __"left"__ e __"down"__ disponíveis.
analisaLD :: Mapa -> TimeLeft -> NBot -> Maybe Char
analisaLD m t j | ((botEmPerigo m j) && (botEmPerigoL m j) && (botEmPerigoD m j)) = (fugirLD m j) --FUGIR!
                | ((botEmPerigo m j) && (botEmPerigoL m j)) = Just 'D'
                | ((botEmPerigo m j) && (botEmPerigoD m j)) = Just 'L'
                | ((botEmPerigoL m j) && (botEmPerigoD m j)) = Nothing
                | (botEmPerigo m j) =  (procuraPowerLD m j (coordBot m j) 1 2 2)  -- mas n pode ficar parado; arranjar criterio de decisao.
                | (botEmPerigoL m j) = (tempoATerminarD m t j) -- tem que ir para esquerda ou nada -> criterio? pode por bomba!
                | (botEmPerigoD m j) = (tempoATerminarL m t j) -- tem que ir para esquerda ou nada -> criterio? pode por bomba!
                | otherwise = (tempoATerminarLD m t j) 

-- | A função @analisaRD@ analisa conveniente o que o bot deve fazer quando só tem as direções __"right"__ e __"down"__ disponíveis.
analisaRD :: Mapa -> TimeLeft -> NBot -> Maybe Char
analisaRD m t j | ((botEmPerigo m j) && (botEmPerigoR m j) && (botEmPerigoD m j)) = (fugirRD m j) --FUGIR!
                | ((botEmPerigo m j) && (botEmPerigoR m j)) = Just 'D'
                | ((botEmPerigo m j) && (botEmPerigoD m j)) = Just 'R'
                | ((botEmPerigoR m j) && (botEmPerigoD m j)) = Nothing
                | (botEmPerigo m j) = (procuraPowerRD m j (coordBot m j) 1 2 2)  -- mas n pode ficar parado; arranjar criterio de decisao.
                | (botEmPerigoR m j) = (tempoATerminarD m t j) -- tem que ir para esquerda ou nada -> criterio? pode por bomba!
                | (botEmPerigoD m j) = (tempoATerminarR m t j) -- tem que ir para esquerda ou nada -> criterio? pode por bomba!
                | otherwise = (tempoATerminarRD m t j) 

-- | A função @analisaULR@ analisa conveniente o que o bot deve fazer quando só tem as direções __"up"__, __"left"__ e __"right"__ disponíveis.
analisaULR :: Mapa -> TimeLeft -> NBot -> Maybe Char
analisaULR m t j | ((botEmPerigo m j) && (botEmPerigoU m j) && (botEmPerigoL m j) && (botEmPerigoR m j)) = fugirULR m j --FUGIR!
                 | ((botEmPerigo m j) && (botEmPerigoU m j) && (botEmPerigoL m j)) = Just 'R' --
                 | ((botEmPerigo m j) && (botEmPerigoU m j) && (botEmPerigoR m j)) = Just 'L' --
                 | ((botEmPerigo m j) && (botEmPerigoL m j) && (botEmPerigoR m j)) = Just 'U' -- 
                 | ((botEmPerigoU m j) && (botEmPerigoL m j) && (botEmPerigoR m j)) = Nothing -- 
                 | ((botEmPerigo m j) && (botEmPerigoU m j)) = (procuraPowerLR m j (coordBot m j) 1 2 2) --
                 | ((botEmPerigo m j) && (botEmPerigoL m j)) = (procuraPowerUR m j (coordBot m j) 1 2 2) --
                 | ((botEmPerigo m j) && (botEmPerigoR m j)) = Just 'L' --
                 | ((botEmPerigoU m j) && (botEmPerigoL m j)) = (tempoATerminarR m t j)  --QUIETO OU RIGHT
                 | ((botEmPerigoL m j) && (botEmPerigoR m j)) = (tempoATerminarU m t j) --
                 | ((botEmPerigoU m j) && (botEmPerigoR m j)) = (tempoATerminarD m t j) --
                 | (botEmPerigo m j) = (procuraPowerULR m j (coordBot m j) 1 2 2) --
                 | (botEmPerigoU m j) = (tempoATerminarLR m t j) -- quieta L R 
                 | (botEmPerigoL m j) = (tempoATerminarUR m t j) --
                 | (botEmPerigoR m j) = (tempoATerminarUL m t j) --
                 | otherwise = (tempoATerminarULR m t j) --

-- | A função @analisaDUR@ analisa conveniente o que o bot deve fazer quando só tem as direções __"down"__, __"up"__ e __"right"__ disponíveis.
analisaDUR :: Mapa -> TimeLeft -> NBot -> Maybe Char
analisaDUR m t j | ((botEmPerigo m j) && (botEmPerigoD m j) && (botEmPerigoU m j) && (botEmPerigoR m j)) = fugirDUR m j --FUGIR!
                 | ((botEmPerigo m j) && (botEmPerigoD m j) && (botEmPerigoU m j)) = Just 'R'
                 | ((botEmPerigo m j) && (botEmPerigoD m j) && (botEmPerigoR m j)) = Just 'U'
                 | ((botEmPerigo m j) && (botEmPerigoU m j) && (botEmPerigoR m j)) = Just 'D'
                 | ((botEmPerigoD m j) && (botEmPerigoU m j) && (botEmPerigoR m j)) = Nothing
                 | ((botEmPerigo m j) && (botEmPerigoD m j)) = (procuraPowerUR m j (coordBot m j) 1 2 2)
                 | ((botEmPerigo m j) && (botEmPerigoU m j)) = (procuraPowerRD m j (coordBot m j) 1 2 2)
                 | ((botEmPerigo m j) && (botEmPerigoR m j)) = (procuraPowerDU m j (coordBot m j) 1 2 2)
                 | ((botEmPerigoD m j) && (botEmPerigoU m j)) = (tempoATerminarR m t j)
                 | ((botEmPerigoD m j) && (botEmPerigoR m j)) = (tempoATerminarU m t j)
                 | ((botEmPerigoU m j) && (botEmPerigoR m j)) = (tempoATerminarD m t j)
                 | (botEmPerigo m j) = (procuraPowerDUR m j (coordBot m j) 1 2 2)
                 | (botEmPerigoD m j) = (tempoATerminarUR m t j) 
                 | (botEmPerigoU m j) = (tempoATerminarRD m t j)
                 | (botEmPerigoR m j) = (tempoATerminarDU m t j)
                 | otherwise = (tempoATerminarDUR m t j) 

-- | A função @analisaDUL@ analisa conveniente o que o bot deve fazer quando só tem as direções __"down"__, __"up"__ e __"left"__ disponíveis.
analisaDUL :: Mapa -> TimeLeft -> NBot -> Maybe Char
analisaDUL m t j | ((botEmPerigo m j) && (botEmPerigoD m j) && (botEmPerigoU m j) && (botEmPerigoL m j)) = fugirDUL m j --FUGIR!
                 | ((botEmPerigo m j) && (botEmPerigoD m j) && (botEmPerigoU m j)) = Just 'L'
                 | ((botEmPerigo m j) && (botEmPerigoD m j) && (botEmPerigoL m j)) = Just 'U'
                 | ((botEmPerigo m j) && (botEmPerigoU m j) && (botEmPerigoL m j)) = Just 'D'
                 | ((botEmPerigoD m j) && (botEmPerigoU m j) && (botEmPerigoL m j)) = Nothing
                 | ((botEmPerigo m j) && (botEmPerigoD m j)) = Just 'L'
                 | ((botEmPerigo m j) && (botEmPerigoU m j)) = (procuraPowerLD m j (coordBot m j) 1 2 2)
                 | ((botEmPerigo m j) && (botEmPerigoL m j)) = (procuraPowerDU m j (coordBot m j) 1 2 2)
                 | ((botEmPerigoD m j) && (botEmPerigoU m j)) = (tempoATerminarL m t j)
                 | ((botEmPerigoD m j) && (botEmPerigoL m j)) = (tempoATerminarU m t j)
                 | ((botEmPerigoU m j) && (botEmPerigoL m j)) = (tempoATerminarR m t j)
                 | (botEmPerigo m j) = (procuraPowerDUL m j (coordBot m j) 1 2 2) 
                 | (botEmPerigoD m j) = (tempoATerminarUL m t j)  
                 | (botEmPerigoU m j) = (tempoATerminarLD m t j) 
                 | (botEmPerigoL m j) = (tempoATerminarDU m t j) 
                 | otherwise = (tempoATerminarDUL m t j)  

-- | A função @analisaDLR@ analisa conveniente o que o bot deve fazer quando só tem as direções __"down"__, __"left"__ e __"right"__ disponíveis.
analisaDLR :: Mapa -> TimeLeft -> NBot -> Maybe Char
analisaDLR m t j | ((botEmPerigo m j) && (botEmPerigoD m j) && (botEmPerigoL m j) && (botEmPerigoR m j)) = fugirDLR m j --FUGIR!
                 | ((botEmPerigo m j) && (botEmPerigoD m j) && (botEmPerigoL m j)) = Just 'R'
                 | ((botEmPerigo m j) && (botEmPerigoD m j) && (botEmPerigoR m j)) = Just 'L'
                 | ((botEmPerigo m j) && (botEmPerigoL m j) && (botEmPerigoR m j)) = Just 'D'
                 | ((botEmPerigoD m j) && (botEmPerigoL m j) && (botEmPerigoR m j)) = Nothing
                 | ((botEmPerigo m j) && (botEmPerigoD m j)) = (procuraPowerLR m j (coordBot m j) 1 2 2)
                 | ((botEmPerigo m j) && (botEmPerigoL m j)) = (procuraPowerRD m j (coordBot m j) 1 2 2)
                 | ((botEmPerigo m j) && (botEmPerigoR m j)) = (procuraPowerLD m j (coordBot m j) 1 2 2)
                 | ((botEmPerigoD m j) && (botEmPerigoL m j)) = (tempoATerminarR m t j)
                 | ((botEmPerigoL m j) && (botEmPerigoR m j)) = (tempoATerminarD m t j)
                 | ((botEmPerigoD m j) && (botEmPerigoR m j)) = (tempoATerminarL m t j)
                 | (botEmPerigo m j) = (procuraPowerDLR m j (coordBot m j) 1 2 2)
                 | (botEmPerigoD m j) = (tempoATerminarLR m t j)  
                 | (botEmPerigoL m j) = (tempoATerminarRD m t j) 
                 | (botEmPerigoR m j) = (tempoATerminarLD m t j) 
                 | otherwise = (tempoATerminarDLR m t j)  

-- | A função @analisaDULR@ analisa conveniente o que o bot deve fazer quando tem __todas__ as direções disponíveis.
analisaDULR :: Mapa -> TimeLeft -> NBot -> Maybe Char
analisaDULR m t j | ((botEmPerigo m j) && (botEmPerigoD m j) && (botEmPerigoU m j) && (botEmPerigoL m j) && (botEmPerigoR m j)) = fugirDULR m j --FUGIR!
                  | ((botEmPerigo m j) && (botEmPerigoD m j) && (botEmPerigoU m j) && (botEmPerigoL m j)) = Just 'R' --             
                  | ((botEmPerigo m j) && (botEmPerigoD m j) && (botEmPerigoU m j) && (botEmPerigoR m j)) = Just 'L' --
                  | ((botEmPerigo m j) && (botEmPerigoD m j) && (botEmPerigoL m j) && (botEmPerigoR m j)) = Just 'U' --
                  | ((botEmPerigo m j) && (botEmPerigoU m j) && (botEmPerigoL m j) && (botEmPerigoR m j)) = Just 'D' --           
                  | ((botEmPerigoD m j) && (botEmPerigoU m j) && (botEmPerigoL m j) && (botEmPerigoR m j)) = Nothing --
                  | ((botEmPerigo m j) && (botEmPerigoD m j) && (botEmPerigoU m j)) = (procuraPowerLR m j (coordBot m j) 1 2 2) -- R ou L
                  | ((botEmPerigo m j) && (botEmPerigoD m j) && (botEmPerigoL m j)) = (procuraPowerUR m j (coordBot m j) 1 2 2) -- U ou R
                  | ((botEmPerigo m j) && (botEmPerigoD m j) && (botEmPerigoR m j)) = Just 'L' -- U ou L
                  | ((botEmPerigo m j) && (botEmPerigoU m j) && (botEmPerigoL m j)) = (procuraPowerRD m j (coordBot m j) 1 2 2) -- R ou D
                  | ((botEmPerigo m j) && (botEmPerigoU m j) && (botEmPerigoR m j)) = (procuraPowerLD m j (coordBot m j) 1 2 2) -- D ou L
                  | ((botEmPerigo m j) && (botEmPerigoL m j) && (botEmPerigoR m j)) = (procuraPowerDU m j (coordBot m j) 1 2 2) -- U ou D
                  | ((botEmPerigoD m j) && (botEmPerigoU m j) && (botEmPerigoL m j)) = (tempoATerminarR m t j)  --quieto ou R
                  | ((botEmPerigoD m j) && (botEmPerigoL m j) && (botEmPerigoR m j)) = (tempoATerminarU m t j)  --
                  | ((botEmPerigoD m j) && (botEmPerigoU m j) && (botEmPerigoR m j)) = (tempoATerminarL m t j)  --
                  | ((botEmPerigoU m j) && (botEmPerigoL m j) && (botEmPerigoR m j)) = (tempoATerminarD m t j)  --
                  | ((botEmPerigo m j) && (botEmPerigoD m j)) = (procuraPowerULR m j (coordBot m j) 1 2 2) -- U L R
                  | ((botEmPerigo m j) && (botEmPerigoU m j)) = (procuraPowerDLR m j (coordBot m j) 1 2 2) -- D L R
                  | ((botEmPerigo m j) && (botEmPerigoL m j)) = (procuraPowerDUR m j (coordBot m j) 1 2 2) -- D U R
                  | ((botEmPerigo m j) && (botEmPerigoR m j)) = (procuraPowerDUL m j (coordBot m j) 1 2 2) -- D U L
                  | ((botEmPerigoD m j) && (botEmPerigoU m j)) = (tempoATerminarLR m t j) -- quieto L R
                  | ((botEmPerigoD m j) && (botEmPerigoL m j)) = (tempoATerminarUR m t j) -- quieto U R
                  | ((botEmPerigoD m j) && (botEmPerigoR m j)) = (tempoATerminarUL m t j) -- quieto U L
                  | ((botEmPerigoU m j) && (botEmPerigoL m j)) = (tempoATerminarRD m t j) -- quieto D R
                  | ((botEmPerigoU m j) && (botEmPerigoR m j)) = (tempoATerminarLD m t j) -- quieto D L
                  | ((botEmPerigoL m j) && (botEmPerigoR m j)) = (tempoATerminarDU m t j) -- quiero D U
                  | (botEmPerigo m j) = (procuraPowerDULR m j (coordBot m j) 1 2 2)
                  | (botEmPerigoD m j) = (tempoATerminarULR m t j)  
                  | (botEmPerigoU m j) = (tempoATerminarDLR m t j) 
                  | (botEmPerigoL m j) = (tempoATerminarDUR m t j) 
                  | (botEmPerigoR m j) = (tempoATerminarDUL m t j) 
                  | otherwise = (tempoATerminarDULR m t j)  

-----------------------------

----- ALERTA 15 INSTANTES DE TEMPO PARA ESPIRAL
--funcao averigua se o tempo estiver quase a terminar (faltam 15 instantes de tempo para a espiral começar),
--o bot foge para a coordenada central do mapa, caso contrário, vai procurar power ups (para apanhar) e tijolos e jogadores (para destruir).
--(se o bot nao estiver em perigo vai passar sempre por esta funcao)


-------QUANDO INDECISO ENTRE 4 DIREÇOES

{-| A função @tempoATerminarDULR@ averigua se faltam /15 ou menos instantes de tempo/ para a espiral começar.

* Caso afirmativo, o bot procura __fugir da espiral__ (ir para o centro do mapa);
* Caso contrário, o bot vai tentar __plantar bombas__ (se vantajoso).
-}
tempoATerminarDULR :: Mapa -> TimeLeft -> NBot -> Maybe Char 
tempoATerminarDULR m t j = if (t<= (((dimensaomapa m)-2)^2) + 15) 
                           then fugirEspiralDULR (coordBot m j) (coordenadaObjetivo m)
                           else plantaBombasDULR m j

-- | A função @fugirEspiralDULR@ __encaminha__ o bot para a coordenada mais próxima da coordenada objetivo.
fugirEspiralDULR :: Coordenadas -> Coordenadas -> Maybe Char
fugirEspiralDULR (x,y) (z,w) | (dist (x,y) (z,w) == 0) = Nothing
                              | (r >= d) && (l >= d) && (u >= d) = Just 'D'
                              | (r >= u) && (l >= u) && (d >= u)= Just 'U'
                              | (d >= l) && (r >= l) && (u >= l)= Just 'L'
                              | (d >= r) && (l >= r) && (u >= r)= Just 'R'
                              | otherwise = Nothing
                                  where d = (dist (x,y+1) (z,w))
                                        u = (dist (x,y-1) (z,w))
                                        l = (dist (x-1,y) (z,w))
                                        r = (dist (x+1,y) (z,w))

--------QUANDO INDECISO ENTRE 3 DIREÇÕES

{-| A função @tempoATerminarDLR@ averigua se faltam /15 ou menos instantes de tempo/ para a espiral começar.

* Caso afirmativo, o bot procura __fugir da espiral__ (ir para o centro do mapa);
* Caso contrário, o bot vai tentar __plantar bombas__ (se vantajoso).
-}
tempoATerminarDLR :: Mapa -> TimeLeft -> NBot -> Maybe Char 
tempoATerminarDLR m t j = if (t<= (((dimensaomapa m)-2)^2) + 15) 
                         then encurraladoDLR m j
                         else plantaBombasDLR m j

-- | A função @encurraladoDLR@ averigua se o bot se encontra __cercado__ por __pedras__ no processo de /fuga da espiral/.
encurraladoDLR :: Mapa -> NBot -> Maybe Char
encurraladoDLR m j  = if ((temTijolosAVolta m j) == True ) then Just 'B'
                      else fugirEspiralDLR (coordBot m j) (coordenadaObjetivo m)



-- | A função @fugirEspiralDLR@ __encaminha__ o bot para a coordenada mais próxima da coordenada objetivo.
fugirEspiralDLR :: Coordenadas -> Coordenadas -> Maybe Char
fugirEspiralDLR  (x,y) (z,w) | (dist (x,y) (z,w) == 0) = Nothing
                             | (d >= l) && (r >= l) = Just 'L'
                             | (r >= d) && (l >= d) = Just 'D'
                             | (d >= r) && (l >= r) = Just 'R'
                             | otherwise = Nothing
                                  where d = (dist (x,y+1) (z,w))
                                        l = (dist (x-1,y) (z,w))
                                        r = (dist (x+1,y) (z,w))

{-| A função @tempoATerminarDUL@ averigua se faltam /15 ou menos instantes de tempo/ para a espiral começar.

* Caso afirmativo, o bot procura __fugir da espiral__ (ir para o centro do mapa);
* Caso contrário, o bot vai tentar __plantar bombas__ (se vantajoso).
-}
tempoATerminarDUL :: Mapa -> TimeLeft -> NBot -> Maybe Char 
tempoATerminarDUL m t j = if (t<= (((dimensaomapa m)-2)^2) + 15) 
                         then encurraladoDUL m j
                         else plantaBombasDUL m j

-- | A função @encurraladoDUL@ averigua se o bot se encontra __cercado__ por __pedras__ no processo de /fuga da espiral/.
encurraladoDUL :: Mapa -> NBot -> Maybe Char
encurraladoDUL m j  = if ((temTijolosAVolta m j) == True ) then Just 'B'
                      else fugirEspiralDUL (coordBot m j) (coordenadaObjetivo m)


-- | A função @fugirEspiralDUL@ __encaminha__ o bot para a coordenada mais próxima da coordenada objetivo.
fugirEspiralDUL :: Coordenadas -> Coordenadas -> Maybe Char
fugirEspiralDUL  (x,y) (z,w) | (dist (x,y) (z,w) == 0) = Nothing
                             | (u >= l) && (d >= l) = Just 'L'
                             | (u >= d) && (l >= d) = Just 'D'
                             | (d >= u) && (l >= u) = Just 'U'
                             | otherwise = Nothing
                                  where d = (dist (x,y+1) (z,w))
                                        u = (dist (x,y-1) (z,w))
                                        l = (dist (x-1,y) (z,w))

{-| A função @tempoATerminarDUR@ averigua se faltam /15 ou menos instantes de tempo/ para a espiral começar.

* Caso afirmativo, o bot procura __fugir da espiral__ (ir para o centro do mapa);
* Caso contrário, o bot vai tentar __plantar bombas__ (se vantajoso).
-}
tempoATerminarDUR :: Mapa -> TimeLeft -> NBot -> Maybe Char 
tempoATerminarDUR m t j = if (t<= (((dimensaomapa m)-2)^2) + 15) 
                         then encurraladoDUR m j
                         else plantaBombasDUR m j

-- | A função @encurraladoDUR@ averigua se o bot se encontra __cercado__ por __pedras__ no processo de /fuga da espiral/.
encurraladoDUR :: Mapa -> NBot -> Maybe Char
encurraladoDUR m j  = if ((temTijolosAVolta m j) == True ) then Just 'B'
                      else fugirEspiralDUR (coordBot m j) (coordenadaObjetivo m)


-- | A função @fugirEspiralDUR@ __encaminha__ o bot para a coordenada mais próxima da coordenada objetivo.
fugirEspiralDUR :: Coordenadas -> Coordenadas -> Maybe Char
fugirEspiralDUR (x,y) (z,w) | (dist (x,y) (z,w) == 0) = Nothing
                            | (u >= r) && (d >= r) = Just 'R'
                            | (u >= d) && (r >= d) = Just 'D'
                            | (d >= u) && (r >= u) = Just 'U'
                            | otherwise = Nothing
                                  where d = (dist (x,y+1) (z,w))
                                        u = (dist (x,y-1) (z,w))
                                        r = (dist (x+1,y) (z,w))

{-| A função @tempoATerminarULR@ averigua se faltam /15 ou menos instantes de tempo/ para a espiral começar.

* Caso afirmativo, o bot procura __fugir da espiral__ (ir para o centro do mapa);
* Caso contrário, o bot vai tentar __plantar bombas__ (se vantajoso).
-}
tempoATerminarULR :: Mapa -> TimeLeft -> NBot -> Maybe Char 
tempoATerminarULR m t j = if (t<= (((dimensaomapa m)-2)^2) + 15) 
                         then encurraladoULR m j
                         else plantaBombasULR m j

-- | A função @encurraladoULR@ averigua se o bot se encontra __cercado__ por __pedras__ no processo de /fuga da espiral/.
encurraladoULR :: Mapa -> NBot -> Maybe Char
encurraladoULR m j  = if ((temTijolosAVolta m j) == True ) then Just 'B'
                      else fugirEspiralULR (coordBot m j) (coordenadaObjetivo m)


-- | A função @fugirEspiralULR@ __encaminha__ o bot para a coordenada mais próxima da coordenada objetivo.
fugirEspiralULR :: Coordenadas -> Coordenadas -> Maybe Char
fugirEspiralULR (x,y) (z,w) | (dist (x,y) (z,w) == 0) = Nothing
                            | (u >= r) && (l >= r) = Just 'R'
                            | (u >= l) && (r >= l) = Just 'L'
                            | (l >= u) && (r >= u) = Just 'U'
                            | otherwise = Nothing
                                  where u = (dist (x,y-1) (z,w))
                                        l = (dist (x-1,y) (z,w))
                                        r = (dist (x+1,y) (z,w))


--------QUANDO INDECISO ENTRE 1 DIREÇÃO (e pode plantar bombas)

{-| A função @tempoATerminarD@ averigua se faltam /15 ou menos instantes de tempo/ para a espiral começar.

* Caso afirmativo, o bot procura __fugir da espiral__ (ir para o centro do mapa);
* Caso contrário, o bot vai tentar __plantar bombas__ (se vantajoso).
-}
tempoATerminarD :: Mapa -> TimeLeft -> NBot -> Maybe Char 
tempoATerminarD m t j = if (t<= (((dimensaomapa m)-2)^2) + 15) 
                         then encurraladoD m j
                         else plantaBombasD m j

-- | A função @encurraladoD@ averigua se o bot se encontra __cercado__ por __pedras__ no processo de /fuga da espiral/.
encurraladoD :: Mapa -> NBot -> Maybe Char
encurraladoD m j  = if ((temTijolosAVolta m j) == True ) then Just 'B'
                    else fugirEspiralD (coordBot m j) (coordenadaObjetivo m)


-- | A função @fugirEspiralD@ __encaminha__ o bot para a coordenada mais próxima da coordenada objetivo.
fugirEspiralD :: Coordenadas -> Coordenadas -> Maybe Char
fugirEspiralD (x,y) (z,w) | (dist (x,y) (z,w) == 0) = Nothing
                          | (dist (x,y) (z,w)) >= (dist (x,y+1) (z,w)) = Just 'D'
                          | otherwise = Nothing

{-| A função @tempoATerminarU@ averigua se faltam /15 ou menos instantes de tempo/ para a espiral começar.

* Caso afirmativo, o bot procura __fugir da espiral__ (ir para o centro do mapa);
* Caso contrário, o bot vai tentar __plantar bombas__ (se vantajoso).
-}
tempoATerminarU :: Mapa -> TimeLeft -> NBot -> Maybe Char 
tempoATerminarU m t j = if (t<= (((dimensaomapa m)-2)^2) + 15) 
                         then encurraladoU m j
                         else plantaBombasU m j

-- | A função @encurraladoU@ averigua se o bot se encontra __cercado__ por __pedras__ no processo de /fuga da espiral/.
encurraladoU :: Mapa -> NBot -> Maybe Char
encurraladoU m j  = if ((temTijolosAVolta m j) == True ) then Just 'B'
                    else fugirEspiralU (coordBot m j) (coordenadaObjetivo m)


-- | A função @fugirEspiralU@ __encaminha__ o bot para a coordenada mais próxima da coordenada objetivo.
fugirEspiralU :: Coordenadas -> Coordenadas -> Maybe Char
fugirEspiralU (x,y) (z,w) | (dist (x,y) (z,w) == 0) = Nothing
                          | (dist (x,y) (z,w)) >= (dist (x,y-1) (z,w)) = Just 'U'
                          | otherwise = Nothing

{-| A função @tempoATerminarR@ averigua se faltam /15 ou menos instantes de tempo/ para a espiral começar.

* Caso afirmativo, o bot procura __fugir da espiral__ (ir para o centro do mapa);
* Caso contrário, o bot vai tentar __plantar bombas__ (se vantajoso).
-}
tempoATerminarR :: Mapa -> TimeLeft -> NBot -> Maybe Char 
tempoATerminarR m t j = if (t<= (((dimensaomapa m)-2)^2) + 15) 
                         then encurraladoR m j
                         else plantaBombasR m j

-- | A função @encurraladoR@ averigua se o bot se encontra __cercado__ por __pedras__ no processo de /fuga da espiral/.
encurraladoR :: Mapa -> NBot -> Maybe Char
encurraladoR m j  = if ((temTijolosAVolta m j) == True ) then Just 'B'
                    else fugirEspiralR (coordBot m j) (coordenadaObjetivo m)


-- | A função @fugirEspiralR@ __encaminha__ o bot para a coordenada mais próxima da coordenada objetivo.
fugirEspiralR :: Coordenadas -> Coordenadas -> Maybe Char
fugirEspiralR (x,y) (z,w) | (dist (x,y) (z,w) == 0) = Nothing
                          | (dist (x,y) (z,w)) >= (dist (x+1,y) (z,w)) = Just 'R'
                          | otherwise = Nothing

{-| A função @tempoATerminarL@ averigua se faltam /15 ou menos instantes de tempo/ para a espiral começar.

* Caso afirmativo, o bot procura __fugir da espiral__ (ir para o centro do mapa);
* Caso contrário, o bot vai tentar __plantar bombas__ (se vantajoso).
-}
tempoATerminarL :: Mapa -> TimeLeft -> NBot -> Maybe Char 
tempoATerminarL m t j = if (t<= (((dimensaomapa m)-2)^2) + 15) 
                         then encurraladoL m j
                         else plantaBombasL m j

-- | A função @encurraladoL@ averigua se o bot se encontra __cercado__ por __pedras__ no processo de /fuga da espiral/.
encurraladoL :: Mapa -> NBot -> Maybe Char
encurraladoL m j  = if ((temTijolosAVolta m j) == True ) then Just 'B'
                    else fugirEspiralL (coordBot m j) (coordenadaObjetivo m)


-- | A função @fugirEspiralL@ __encaminha__ o bot para a coordenada mais próxima da coordenada objetivo.
fugirEspiralL :: Coordenadas -> Coordenadas -> Maybe Char
fugirEspiralL (x,y) (z,w) | (dist (x,y) (z,w) == 0) = Nothing
                          | (dist (x,y) (z,w)) >= (dist (x-1,y) (z,w)) = Just 'L'
                          | otherwise = Nothing


--------QUANDO INDECISO ENTRE 2 DIREÇOES (para plantar bombas)

{-| A função @tempoATerminarRD@ averigua se faltam /15 ou menos instantes de tempo/ para a espiral começar.

* Caso afirmativo, o bot procura __fugir da espiral__ (ir para o centro do mapa);
* Caso contrário, o bot vai tentar __plantar bombas__ (se vantajoso).
-}
tempoATerminarRD :: Mapa -> TimeLeft -> NBot -> Maybe Char 
tempoATerminarRD m t j = if (t<= (((dimensaomapa m)-2)^2) + 15) 
                         then encurraladoRD m j
                         else plantaBombasRD m j

-- | A função @encurraladoRD@ averigua se o bot se encontra __cercado__ por __pedras__ no processo de /fuga da espiral/.
encurraladoRD :: Mapa -> NBot -> Maybe Char
encurraladoRD m j  = if ((temTijolosAVolta m j) == True ) then Just 'B'
                      else fugirEspiralRD (coordBot m j) (coordenadaObjetivo m)


-- | A função @fugirEspiralRD@ __encaminha__ o bot para a coordenada mais próxima da coordenada objetivo.
fugirEspiralRD :: Coordenadas -> Coordenadas -> Maybe Char
fugirEspiralRD (x,y) (z,w) | (dist (x,y) (z,w) == 0) = Nothing
                           | (dist (x+1,y) (z,w)) >= (dist (x,y+1) (z,w)) = Just 'D'
                           | (dist (x+1,y) (z,w)) < (dist (x,y+1) (z,w)) = Just 'R'
                           | otherwise = Nothing


{-| A função @tempoATerminarLD@ averigua se faltam /15 ou menos instantes de tempo/ para a espiral começar.

* Caso afirmativo, o bot procura __fugir da espiral__ (ir para o centro do mapa);
* Caso contrário, o bot vai tentar __plantar bombas__ (se vantajoso).
-}
tempoATerminarLD :: Mapa -> TimeLeft -> NBot -> Maybe Char 
tempoATerminarLD m t j = if (t<= (((dimensaomapa m)-2)^2) + 15) 
                         then encurraladoLD m j
                         else plantaBombasLD m j

-- | A função @encurraladoLD@ averigua se o bot se encontra __cercado__ por __pedras__ no processo de /fuga da espiral/.
encurraladoLD :: Mapa -> NBot -> Maybe Char
encurraladoLD m j  = if ((temTijolosAVolta m j) == True ) then Just 'B'
                      else fugirEspiralLD (coordBot m j) (coordenadaObjetivo m)


-- | A função @fugirEspiralLD@ __encaminha__ o bot para a coordenada mais próxima da coordenada objetivo.
fugirEspiralLD :: Coordenadas -> Coordenadas -> Maybe Char
fugirEspiralLD (x,y) (z,w) | (dist (x,y) (z,w) == 0) = Nothing
                           | (dist (x-1,y) (z,w)) >= (dist (x,y+1) (z,w)) = Just 'D'
                           | (dist (x-1,y) (z,w)) < (dist (x,y+1) (z,w)) = Just 'L'
                           | otherwise = Nothing

{-| A função @tempoATerminarDU@ averigua se faltam /15 ou menos instantes de tempo/ para a espiral começar.

* Caso afirmativo, o bot procura __fugir da espiral__ (ir para o centro do mapa);
* Caso contrário, o bot vai tentar __plantar bombas__ (se vantajoso).
-}
tempoATerminarDU :: Mapa -> TimeLeft -> NBot -> Maybe Char 
tempoATerminarDU m t j = if (t<= (((dimensaomapa m)-2)^2) + 15) 
                         then encurraladoDU m j
                         else plantaBombasDU m j

-- | A função @encurraladoDU@ averigua se o bot se encontra __cercado__ por __pedras__ no processo de /fuga da espiral/.
encurraladoDU :: Mapa -> NBot -> Maybe Char
encurraladoDU m j  = if ((temTijolosAVolta m j) == True ) then Just 'B'
                      else fugirEspiralDU (coordBot m j) (coordenadaObjetivo m)


-- | A função @fugirEspiralDU@ __encaminha__ o bot para a coordenada mais próxima da coordenada objetivo.
fugirEspiralDU :: Coordenadas -> Coordenadas -> Maybe Char
fugirEspiralDU (x,y) (z,w) | (dist (x,y) (z,w) == 0) = Nothing
                           | (dist (x,y+1) (z,w)) >= (dist (x,y-1) (z,w)) = Just 'U'
                           | (dist (x,y+1) (z,w)) < (dist (x,y-1) (z,w)) = Just 'D'
                           | otherwise = Nothing

{-| A função @tempoATerminarLR@ averigua se faltam /15 ou menos instantes de tempo/ para a espiral começar.

* Caso afirmativo, o bot procura __fugir da espiral__ (ir para o centro do mapa);
* Caso contrário, o bot vai tentar __plantar bombas__ (se vantajoso).
-}
tempoATerminarLR :: Mapa -> TimeLeft -> NBot -> Maybe Char 
tempoATerminarLR m t j = if (t<= (((dimensaomapa m)-2)^2) + 15) 
                         then encurraladoLR m j
                         else plantaBombasLR m j

-- | A função @encurraladoLR@ averigua se o bot se encontra __cercado__ por __pedras__ no processo de /fuga da espiral/.
encurraladoLR :: Mapa -> NBot -> Maybe Char
encurraladoLR m j  = if ((temTijolosAVolta m j) == True ) then Just 'B'
                      else fugirEspiralLR (coordBot m j) (coordenadaObjetivo m)


-- | A função @fugirEspiralLR@ __encaminha__ o bot para a coordenada mais próxima da coordenada objetivo.
fugirEspiralLR :: Coordenadas -> Coordenadas -> Maybe Char
fugirEspiralLR (x,y) (z,w) | (dist (x,y) (z,w) == 0) = Nothing
                           | (dist (x-1,y) (z,w)) >= (dist (x+1,y) (z,w)) = Just 'R'
                           | (dist (x-1,y) (z,w)) < (dist (x+1,y) (z,w)) = Just 'L'
                           | otherwise = Nothing

{-| A função @tempoATerminarUR@ averigua se faltam /15 ou menos instantes de tempo/ para a espiral começar.

* Caso afirmativo, o bot procura __fugir da espiral__ (ir para o centro do mapa);
* Caso contrário, o bot vai tentar __plantar bombas__ (se vantajoso).
-}
tempoATerminarUR :: Mapa -> TimeLeft -> NBot -> Maybe Char 
tempoATerminarUR m t j = if (t<= (((dimensaomapa m)-2)^2) + 15) 
                         then encurraladoUR m j
                         else plantaBombasUR m j

-- | A função @encurraladoUR@ averigua se o bot se encontra __cercado__ por __pedras__ no processo de /fuga da espiral/.
encurraladoUR :: Mapa -> NBot -> Maybe Char
encurraladoUR m j  = if ((temTijolosAVolta m j) == True ) then Just 'B'
                     else fugirEspiralUR (coordBot m j) (coordenadaObjetivo m)


-- | A função @fugirEspiralUR@ __encaminha__ o bot para a coordenada mais próxima da coordenada objetivo.
fugirEspiralUR :: Coordenadas -> Coordenadas -> Maybe Char
fugirEspiralUR (x,y) (z,w) | (dist (x,y) (z,w) == 0) = Nothing
                           | (dist (x,y-1) (z,w)) >= (dist (x+1,y) (z,w)) = Just 'R'
                           | (dist (x,y-1) (z,w)) < (dist (x+1,y) (z,w)) = Just 'U'
                           | otherwise = Nothing

{-| A função @tempoATerminarUL@ averigua se faltam /15 ou menos instantes de tempo/ para a espiral começar.

* Caso afirmativo, o bot procura __fugir da espiral__ (ir para o centro do mapa);
* Caso contrário, o bot vai tentar __plantar bombas__ (se vantajoso).
-}
tempoATerminarUL :: Mapa -> TimeLeft -> NBot -> Maybe Char 
tempoATerminarUL m t j = if (t<= (((dimensaomapa m)-2)^2) + 15) 
                         then encurraladoUL m j
                         else plantaBombasUL m j

-- | A função @encurraladoUL@ averigua se o bot se encontra __cercado__ por __pedras__ no processo de /fuga da espiral/.
encurraladoUL :: Mapa -> NBot -> Maybe Char
encurraladoUL m j  = if ((temTijolosAVolta m j) == True ) then Just 'B'
                      else fugirEspiralUL (coordBot m j) (coordenadaObjetivo m)


-- | A função @fugirEspiralUL@ __encaminha__ o bot para a coordenada mais próxima da coordenada objetivo.
fugirEspiralUL :: Coordenadas -> Coordenadas -> Maybe Char
fugirEspiralUL (x,y) (z,w) | (dist (x,y) (z,w) == 0) = Nothing
                           | (dist (x,y-1) (z,w)) >= (dist (x-1,y) (z,w)) = Just 'L'
                           | (dist (x,y-1) (z,w)) < (dist (x-1,y) (z,w)) = Just 'U'
                           | otherwise = Nothing


{-| A função @coordenadaObjetivo@ averigua /se o mapa em questão tem como coordenada central uma pedra/.

* Caso afirmativo, a coordenada objetivo é a __coordenada ao lado esquerdo da coordenada central__ (neste caso, última célula onde cai pedra);
* Caso contrário, a coordenada objetivo é a __coordenada central__ do mapa.
-}
coordenadaObjetivo :: Mapa -> Coordenadas 
coordenadaObjetivo m = if (elem (dimensaomapa m) (listasDimensoesCoordenadaCentroP 5)) then coordenadaObjetivo1 m
                                                                                       else coordenadaObjetivo2 m

-- | A função @coordenadaObjetivo1@ dá como resposta a __coordenada ao lado esquerdo da coordenada central__ do mapa.
coordenadaObjetivo1 :: Mapa -> Coordenadas 
coordenadaObjetivo1 m = (x-1,y) --coordenada ao lado esquerda da coordenada do meio
  where (x,y) = coordenadaObjetivo2 m

-- | A função @coordenadaObjetivo2@ dá como resposta a __coordenada central__ do mapa.
coordenadaObjetivo2 :: Mapa -> Coordenadas  
coordenadaObjetivo2 m = ((div x 2), (div x 2)) --coordenada do meio
        where x = (dimensaomapa m - 1)

-- | A função @listasDimensoesCoordenadaCentroP@ dá a lista de alguns mapas que possuem uma __pedra na coordenada central do mapa__.
listasDimensoesCoordenadaCentroP :: Int -> [Int]
listasDimensoesCoordenadaCentroP 49 = []
listasDimensoesCoordenadaCentroP x = x: listasDimensoesCoordenadaCentroP (x+4)

-- | A função @dist@ dá como resultado a __distância__ entre 2 coordenadas.
dist :: Coordenadas -> Coordenadas -> Float
dist (x,y) (z,w) = sqrt ((((fromIntegral z)-(fromIntegral x))^2) + (((fromIntegral w)-(fromIntegral y))^2))




-----------------------------------------------------PLANTAR BOMBAS--------------------------------------------------------------------------------------------------------


---------PLANTA BOMBAS QUANDO INDECISO ENTRE 4 DIREÇOES

-----PROCURA POWERUPS, TIJOLOS E JOGADORES quando indecisa entre ...

{-| A função @plantaBombasDULR@ faz o bot __plantar uma bomba__ se tiver se o mesmo estiver
rodeado por algum tijolo ou jogador; caso não esteja, o bot irá __procurar__ algum power up destapado,
tijolo ou jogador (se estiverem em coordenadas próximas). 
-}
plantaBombasDULR :: Mapa -> NBot -> Maybe Char
plantaBombasDULR m j = if (((temTijolosAVolta m j) == True ) || ((temJogadoresAVolta m j) == True)) then Just 'B'
                      else (procuraPowerDULR m j (coordBot m j) 1 2 2)

-- | A função @procuraPowerDULR@ faz com que o bot averigue se tem __algum power up próximo de si__ (num raio de 3 coordenadas à sua volta).
procuraPowerDULR :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Int -> Maybe Char
procuraPowerDULR m j (x,y) a a2 b = if (a==3)
    then procuraTijolosDULR m j (x,y) a2 b ---VAI PROCURAR TIJOLOS
    else procuraPowerDULRAux m j (x,y) a a2 b

-- | A função @procuraPowerDULRAux@ é a principal função auxiliar da função 'procuraPowerDULR'.
procuraPowerDULRAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Int -> Maybe Char 
procuraPowerDULRAux m j (x,y) a a2 b | ((listapudestapados m) == []) = procuraTijolosDULR m j (x,y) a2 b
                                     | ((elem (x,(y+a)) (listapudestapados m)) && ((y+a) < (dimensaomapa m)) ) = Just 'D'
                                     | ((elem (x,(y-a)) (listapudestapados m)) && ((y-a) > 0) ) = Just 'U'  
                                     | ((elem ((x-a),y) (listapudestapados m)) && ((x-a) > 0) ) = Just 'L' 
                                     | ((elem ((x+a),y) (listapudestapados m)) && ((x+a) < (dimensaomapa m)) ) = Just 'R' 
                                     | otherwise = procuraPowerDULR m j (x,y) (a+1) a2 b

-- | A função @procuraTijolosDULR@ procura __tijolos__ no mapa (num raio de 3 coordenadas), e em caso afirmativo o bot vai nessa direção.
procuraTijolosDULR :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char  --a=2
procuraTijolosDULR m j (x,y) a a2 = if (a==3)
    then procuraJogadoresDULR m j (x,y) a2 ---VAI PROCURAR JOGADORES
    else procuraTijolosDULRAux m j (x,y) a a2

-- | A função @procuraTijolosDULRAux@ é a principal função auxiliar da função 'procuraTijolosDULR'.
procuraTijolosDULRAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char 
procuraTijolosDULRAux m j (x,y) a a2 | ((m !! (y+a) !! x) == '?') && ((y+a) < (dimensaomapa m)) = Just 'D'
                                     | ((m !! (y-a) !! x) == '?') && ((y-a) > 0) = Just 'U'
                                     | ((m !! y !! (x-a)) == '?') && ((x-a) > 0) = Just 'L'
                                     | ((m !! y !! (x+a)) == '?') && ((x+a) < (dimensaomapa m)) = Just 'R'
                                     | otherwise = procuraTijolosDULR m j (x,y) (a+1) a2
 
{-| A função @procuraJogadoresDULR@ procura __jogadores__ no mapa, num raio de 3 coordenadas.

Caso não existam, vai simplesmente para baixo.
-}
procuraJogadoresDULR :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char  --a=2
procuraJogadoresDULR m j (x,y) a = if (a==3)
    then Just 'D' --- "Aleatório"
    else procuraJogadoresDULRAux m j (x,y) a

-- | A função @procuraJogadoresDULRAux@ é a principal função auxiliar da função 'procuraJogadoresDULR'.
procuraJogadoresDULRAux :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char 
procuraJogadoresDULRAux m j (x,y) a | (elem (x,(y+a)) (coordAdversarios m j)) && ((y+a) < (dimensaomapa m)) = Just 'D'
                                    | (elem (x,(y-a)) (coordAdversarios m j)) && ((y-a) > 0) = Just 'U'
                                    | (elem ((x-a),y) (coordAdversarios m j)) && ((x-a) > 0) = Just 'L' 
                                    | (elem ((x+a),y) (coordAdversarios m j)) && ((x+a) < (dimensaomapa m)) = Just 'R'
                                    | otherwise = procuraJogadoresDULR m j (x,y) (a+1)



---------PLANTA BOMBAS QUANDO INDECISO ENTRE 3 DIREÇOES

-----PROCURA POWERUPS, TIJOLOS E JOGADORES quando indecisa entre ...
{-| A função @plantaBombasDLR@ faz o bot __plantar uma bomba__ se tiver se o mesmo estiver
rodeado por algum tijolo ou jogador; caso não esteja, o bot irá __procurar__ algum power up destapado,
tijolo ou jogador (se estiverem em coordenadas próximas). 
-}
plantaBombasDLR :: Mapa -> NBot -> Maybe Char
plantaBombasDLR m j = if (((temTijolosAVolta m j) == True ) || ((temJogadoresAVolta m j) == True)) then Just 'B'
                      else (procuraPowerDLR m j (coordBot m j) 1 2 2)

-- | A função @procuraPowerDLR@ faz com que o bot averigue se tem __algum power up próximo de si__ (num raio de 3 coordenadas à sua volta).
procuraPowerDLR :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Int -> Maybe Char
procuraPowerDLR m j (x,y) a a2 b = if (a==3)
    then procuraTijolosDLR m j (x,y) a2 b ---VAI PROCURAR TIJOLOS
    else procuraPowerDLRAux m j (x,y) a a2 b

-- | A função @procuraPowerDLRAux@ é a principal função auxiliar da função 'procuraPowerDLR'.
procuraPowerDLRAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Int -> Maybe Char 
procuraPowerDLRAux m j (x,y) a a2 b | (listapudestapados m) == [] = procuraTijolosDLR m j (x,y) a2 b
                                    | (elem (x,(y+a)) (listapudestapados m)) && ((y+a) < (dimensaomapa m)) = Just 'D'  
                                    | (elem ((x-a),y) (listapudestapados m)) && ((x-a) > 0) = Just 'L' 
                                    | (elem ((x+a),y) (listapudestapados m)) && ((x+a) < (dimensaomapa m)) = Just 'R' 
                                    | otherwise = procuraPowerDLR m j (x,y) (a+1) a2 b

-- | A função @procuraTijolosDLR@ procura __tijolos__ no mapa (num raio de 3 coordenadas), e em caso afirmativo o bot vai nessa direção.
procuraTijolosDLR :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char  --a=2
procuraTijolosDLR m j (x,y) a a2 = if (a==3)
    then procuraJogadoresDLR m j (x,y) a2 ---VAI PROCURAR JOGADORES
    else procuraTijolosDLRAux m j (x,y) a a2

-- | A função @procuraTijolosDLRAux@ é a principal função auxiliar da função 'procuraTijolosDLR'.
procuraTijolosDLRAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char 
procuraTijolosDLRAux m j (x,y) a a2 | ((m !! (y+a) !! x) == '?') && ((y+a) < (dimensaomapa m)) = Just 'D'
                                    | ((m !! y !! (x-a)) == '?') && ((x-a) > 0) = Just 'L'
                                    | ((m !! y !! (x+a)) == '?') && ((x+a) < (dimensaomapa m)) = Just 'R'
                                    | otherwise = procuraTijolosDLR m j (x,y) (a+1) a2
                      
{-| A função @procuraJogadoresDLR@ procura __jogadores__ no mapa, num raio de 3 coordenadas.

Caso não existam, vai simplesmente para baixo.
-}                                
procuraJogadoresDLR :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char  --a=2
procuraJogadoresDLR m j (x,y) a = if (a==3)
    then Just 'D' --- "Aleatório"
    else procuraJogadoresDLRAux m j (x,y) a

-- | A função @procuraJogadoresDLRAux@ é a principal função auxiliar da função 'procuraJogadoresDLR'.
procuraJogadoresDLRAux :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char 
procuraJogadoresDLRAux m j (x,y) a | (elem (x,(y+a)) (coordAdversarios m j)) && ((y+a) < (dimensaomapa m)) = Just 'D'
                                   | (elem ((x-a),y) (coordAdversarios m j)) && ((x-a) > 0) = Just 'L' 
                                   | (elem ((x+a),y) (coordAdversarios m j)) && ((x+a) < (dimensaomapa m)) = Just 'R'
                                   | otherwise = procuraJogadoresDLR m j (x,y) (a+1)

-----PROCURA POWERUPS, TIJOLOS E JOGADORES quando indecisa entre ...
{-| A função @plantaBombasDUL@ faz o bot __plantar uma bomba__ se tiver se o mesmo estiver
rodeado por algum tijolo ou jogador; caso não esteja, o bot irá __procurar__ algum power up destapado,
tijolo ou jogador (se estiverem em coordenadas próximas). 
-}
plantaBombasDUL :: Mapa -> NBot -> Maybe Char
plantaBombasDUL m j = if (((temTijolosAVolta m j) == True ) || ((temJogadoresAVolta m j) == True)) then Just 'B'
                      else (procuraPowerDUL m j (coordBot m j) 1 2 2)

-- | A função @procuraPowerDUL@ faz com que o bot averigue se tem __algum power up próximo de si__ (num raio de 3 coordenadas à sua volta).
procuraPowerDUL :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Int -> Maybe Char
procuraPowerDUL m j (x,y) a a2 b = if (a==3)
    then procuraTijolosDUL m j (x,y) a2 b ---VAI PROCURAR TIJOLOS
    else procuraPowerDULAux m j (x,y) a a2 b

-- | A função @procuraPowerDULAux@ é a principal função auxiliar da função 'procuraPowerDUL'.
procuraPowerDULAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Int -> Maybe Char 
procuraPowerDULAux m j (x,y) a a2 b | (listapudestapados m) == [] = procuraTijolosDUL m j (x,y) a2 b
                                    | (elem (x,(y+a)) (listapudestapados m)) && ((y+a) < (dimensaomapa m )) = Just 'D'  
                                    | (elem (x,(y-a)) (listapudestapados m)) && ((y-a) > 0) = Just 'U' 
                                    | (elem ((x-a),y) (listapudestapados m)) && ((x-a) > 0) = Just 'L' 
                                    | otherwise = procuraPowerDUL m j (x,y) (a+1) a2 b

-- | A função @procuraTijolosDUL@ procura __tijolos__ no mapa (num raio de 3 coordenadas), e em caso afirmativo o bot vai nessa direção.
procuraTijolosDUL :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char  --a=2
procuraTijolosDUL m j (x,y) a a2 = if (a==3)
    then procuraJogadoresDUL m j (x,y) a2 ---VAI PROCURAR JOGADORES
    else procuraTijolosDULAux m j (x,y) a a2

-- | A função @procuraTijolosDULAux@ é a principal função auxiliar da função 'procuraTijolosDUL'.
procuraTijolosDULAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char 
procuraTijolosDULAux m j (x,y) a a2 | ((m !! (y+a) !! x) == '?') && ((y+a) < (dimensaomapa m)) = Just 'D'
                                    | ((m !! (y-a) !! x)== '?') && ((y-a) > 0) = Just 'U'
                                    | ((m !! y !! (x-a)) == '?') && ((x-a) > 0) = Just 'L'
                                    | otherwise = procuraTijolosDUL m j (x,y) (a+1) a2
 
{-| A função @procuraJogadoresDUL@ procura __jogadores__ no mapa, num raio de 3 coordenadas.

Caso não existam, vai simplesmente para baixo.
-}
procuraJogadoresDUL :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char  --a=2
procuraJogadoresDUL m j (x,y) a = if (a==3)
    then Just 'D' --- "Aleatório"
    else procuraJogadoresDULAux m j (x,y) a  

-- | A função @procuraJogadoresDULAux@ é a principal função auxiliar da função 'procuraJogadoresDUL'.
procuraJogadoresDULAux :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char 
procuraJogadoresDULAux m j (x,y) a | (elem (x,(y+a)) (coordAdversarios m j)) && ((y+a) == (dimensaomapa m - 1)) = Just 'D'
                                   | (elem (x,(y-a)) (coordAdversarios m j)) && ((y-a) == 1) = Just 'U' 
                                   | (elem ((x-a),y) (coordAdversarios m j)) && ((x-a) == 1) = Just 'L'
                                   | otherwise = procuraJogadoresDUL m j (x,y) (a+1)

-----PROCURA POWERUPS, TIJOLOS E JOGADORES quando indecisa entre ...
{-| A função @plantaBombasDUR@ faz o bot __plantar uma bomba__ se tiver se o mesmo estiver
rodeado por algum tijolo ou jogador; caso não esteja, o bot irá __procurar__ algum power up destapado,
tijolo ou jogador (se estiverem em coordenadas próximas). 
-}
plantaBombasDUR :: Mapa -> NBot -> Maybe Char
plantaBombasDUR m j = if (((temTijolosAVolta m j) == True ) || ((temJogadoresAVolta m j) == True)) then Just 'B'
                      else (procuraPowerDUR m j (coordBot m j) 1 2 2)

-- | A função @procuraPowerDUR@ faz com que o bot averigue se tem __algum power up próximo de si__ (num raio de 3 coordenadas à sua volta).
procuraPowerDUR :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Int -> Maybe Char
procuraPowerDUR m j (x,y) a a2 b = if (a==3)
    then procuraTijolosDUR m j (x,y) a2 b ---VAI PROCURAR TIJOLOS
    else procuraPowerDURAux m j (x,y) a a2 b

-- | A função @procuraPowerDURAux@ é a principal função auxiliar da função 'procuraPowerDUR'.
procuraPowerDURAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Int -> Maybe Char 
procuraPowerDURAux m j (x,y) a a2 b | (listapudestapados m) == [] = procuraTijolosDUR m j (x,y) a2 b
                                    | (elem (x,(y+a)) (listapudestapados m)) && ((y+a) == (dimensaomapa m - 1)) = Just 'D'  
                                    | (elem (x,(y-a)) (listapudestapados m)) && ((y-a) == 1) = Just 'U' 
                                    | (elem ((x+a),y) (listapudestapados m)) && ((x+a) == (dimensaomapa m - 1)) = Just 'R' 
                                    | otherwise = procuraPowerDUR m j (x,y) (a+1) a2 b

-- | A função @procuraTijolosDUR@ procura __tijolos__ no mapa (num raio de 3 coordenadas), e em caso afirmativo o bot vai nessa direção.
procuraTijolosDUR :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char  --a=2
procuraTijolosDUR m j (x,y) a a2 = if (a==3)
    then procuraJogadoresDUR m j (x,y) a2 ---VAI PROCURAR JOGADORES
    else procuraTijolosDURAux m j (x,y) a a2

-- | A função @procuraTijolosDURAux@ é a principal função auxiliar da função 'procuraTijolosDUR'.
procuraTijolosDURAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char 
procuraTijolosDURAux m j (x,y) a a2 | ((m !! (y+a) !! x) == '?') && ((y+a) == (dimensaomapa m - 1)) = Just 'D'
                                    | ((m !! (y-a) !! x)== '?') && ((y-a) == 1) = Just 'U'
                                    | ((m !! y !! (x+a)) == '?') && ((x+a) == (dimensaomapa m - 1)) = Just 'R'
                                    | otherwise = procuraTijolosDUR m j (x,y) (a+1) a2
 
{-| A função @procuraJogadoresDUR@ procura __jogadores__ no mapa, num raio de 3 coordenadas.

Caso não existam, vai simplesmente para baixo.
-}
procuraJogadoresDUR :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char  --a=2
procuraJogadoresDUR m j (x,y) a = if (a==3)
    then Just 'D' --- "Aleatório"
    else procuraJogadoresDURAux m j (x,y) a

-- | A função @procuraJogadoresDURAux@ é a principal função auxiliar da função 'procuraJogadoresDUR'.
procuraJogadoresDURAux :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char 
procuraJogadoresDURAux m j (x,y) a | (elem (x,(y+a)) (coordAdversarios m j)) && ((y+a) == (dimensaomapa m - 1)) = Just 'D'
                                   | (elem (x,(y-a)) (coordAdversarios m j)) && ((y-a) == 1) = Just 'U' 
                                   | (elem ((x+a),y) (coordAdversarios m j)) && ((x+a) == (dimensaomapa m - 1)) = Just 'R'
                                   | otherwise = procuraJogadoresDUR m j (x,y) (a+1)

-----PROCURA POWERUPS, TIJOLOS E JOGADORES quando indecisa entre ...
{-| A função @plantaBombasULR@ faz o bot __plantar uma bomba__ se tiver se o mesmo estiver
rodeado por algum tijolo ou jogador; caso não esteja, o bot irá __procurar__ algum power up destapado,
tijolo ou jogador (se estiverem em coordenadas próximas). 
-}
plantaBombasULR :: Mapa -> NBot -> Maybe Char
plantaBombasULR m j = if (((temTijolosAVolta m j) == True ) || ((temJogadoresAVolta m j) == True)) then Just 'B'
                      else (procuraPowerULR m j (coordBot m j) 1 2 2)

-- | A função @procuraPowerULR@ faz com que o bot averigue se tem __algum power up próximo de si__ (num raio de 3 coordenadas à sua volta).
procuraPowerULR :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Int -> Maybe Char
procuraPowerULR m j (x,y) a a2 b = if (a==3)
    then procuraTijolosULR m j (x,y) a2 b ---VAI PROCURAR TIJOLOS
    else procuraPowerULRAux m j (x,y) a a2 b

-- | A função @procuraPowerULRAux@ é a principal função auxiliar da função 'procuraPowerULR'.
procuraPowerULRAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Int -> Maybe Char 
procuraPowerULRAux m j (x,y) a a2 b | (listapudestapados m) == [] = procuraTijolosULR m j (x,y) a2 b
                                    | (elem (x,(y-a)) (listapudestapados m)) && ((y-a) == 1) = Just 'U'  
                                    | (elem ((x-a),y) (listapudestapados m)) && ((x-a) == 1) = Just 'L' 
                                    | (elem ((x+a),y) (listapudestapados m)) && ((x+a) == (dimensaomapa m - 1)) = Just 'R' 
                                    | otherwise = procuraPowerULR m j (x,y) (a+1) a2 b

-- | A função @procuraTijolosULR@ procura __tijolos__ no mapa (num raio de 3 coordenadas), e em caso afirmativo o bot vai nessa direção.
procuraTijolosULR :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char  --a=2
procuraTijolosULR m j (x,y) a a2 = if (a==3)
    then procuraJogadoresULR m j (x,y) a2 ---VAI PROCURAR JOGADORES
    else procuraTijolosULRAux m j (x,y) a a2

-- | A função @procuraTijolosULRAux@ é a principal função auxiliar da função 'procuraTijolosULR'.
procuraTijolosULRAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char 
procuraTijolosULRAux m j (x,y) a a2 | ((m !! (y-a) !! x) == '?') && ((y-a) == 1) = Just 'U'
                                    | ((m !! y !! (x-a)) == '?') && ((x-a) == 1) = Just 'L'
                                    | ((m !! y !! (x+a)) == '?') && ((x+a) == (dimensaomapa m - 1)) = Just 'R'
                                    | otherwise = procuraTijolosULR m j (x,y) (a+1) a2
 
{-| A função @procuraJogadoresULR@ procura __jogadores__ no mapa, num raio de 3 coordenadas.

Caso não existam, vai simplesmente para a direita.
-}
procuraJogadoresULR :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char  --a=2
procuraJogadoresULR m j (x,y) a = if (a==3)
    then Just 'R' --- "Aleatório"
    else procuraJogadoresULRAux m j (x,y) a

-- | A função @procuraJogadoresULRAux@ é a principal função auxiliar da função 'procuraJogadoresULR'.
procuraJogadoresULRAux :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char 
procuraJogadoresULRAux m j (x,y) a | (elem (x,(y-a)) (coordAdversarios m j)) && ((y-a) == 1) = Just 'U'
                                   | (elem ((x-a),y) (coordAdversarios m j)) && ((x-a) == 1) = Just 'L' 
                                   | (elem ((x+a),y) (coordAdversarios m j)) && ((x+a) == (dimensaomapa m - 1)) = Just 'R'
                                   | otherwise = procuraJogadoresULR m j (x,y) (a+1)




------------PLANTA BOMBAS QUANDO "INDECISO" ENTRE 1 DIREÇÃO

----PLANTA BOMBA OU NAO QUANDO INDECISO ENTRE NOTHING OU DOWN
plantaBombasD :: Mapa -> NBot -> Maybe Char
plantaBombasD m j = if (((temTijolosAVolta m j) == True ) || ((temJogadoresAVolta m j) == True)) then Just 'B'
                    else Just 'D'

----PLANTA BOMBA OU NAO QUANDO INDECISO ENTRE NOTHING OU DOWN
plantaBombasU :: Mapa -> NBot -> Maybe Char
plantaBombasU m j = if (((temTijolosAVolta m j) == True ) || ((temJogadoresAVolta m j) == True)) then Just 'B'
                    else Just 'U'

----PLANTA BOMBA OU NAO QUANDO INDECISO ENTRE NOTHING OU DOWN
plantaBombasL :: Mapa -> NBot -> Maybe Char
plantaBombasL m j = if (((temTijolosAVolta m j) == True ) || ((temJogadoresAVolta m j) == True)) then Just 'B'
                    else Just 'L'

----PLANTA BOMBA OU NAO QUANDO INDECISO ENTRE NOTHING OU DOWN
plantaBombasR :: Mapa -> NBot -> Maybe Char
plantaBombasR m j = if (((temTijolosAVolta m j) == True ) || ((temJogadoresAVolta m j) == True)) then Just 'B'
                    else Just 'R'


-----PROCURA POWERUPS, TIJOLOS E JOGADORES quando indecisa entre right e down
{-| A função @plantaBombasRD@ faz o bot __plantar uma bomba__ se tiver se o mesmo estiver
rodeado por algum tijolo ou jogador; caso não esteja, o bot irá __procurar__ algum power up destapado,
tijolo ou jogador (se estiverem em coordenadas próximas). 
-}
plantaBombasRD :: Mapa -> NBot -> Maybe Char
plantaBombasRD m j = if (((temTijolosAVolta m j) == True ) || ((temJogadoresAVolta m j) == True)) then Just 'B'
                     else (procuraPowerRD m j (coordBot m j) 1 2 2)

-- | A função @procuraPowerRD@ faz com que o bot averigue se tem __algum power up próximo de si__ (num raio de 3 coordenadas à sua volta).
procuraPowerRD :: Mapa -> NBot -> Coordenadas -> Int ->  Int ->  Int -> Maybe Char
procuraPowerRD m j (x,y) a a2 b = if (a==3)
    then procuraTijolosRD m j (x,y) a2 b --VAI PROCURAR TIJOLOS
    else procuraPowerRDAux m j (x,y) a a2 b

-- | A função @procuraPowerRDAux@ é a principal função auxiliar da função 'procuraPowerRD'.
procuraPowerRDAux :: Mapa -> NBot -> Coordenadas -> Int ->  Int -> Int -> Maybe Char 
procuraPowerRDAux m j (x,y) a a2 b | (listapudestapados m) == [] = procuraTijolosRD m j (x,y) a2 b
                                   | (elem ((x+a),y) (listapudestapados m)) && ((x+a) == (dimensaomapa m - 1)) = Just 'R'  
                                   | (elem (x,(y+a)) (listapudestapados m)) && ((y+a) == (dimensaomapa m - 1)) = Just 'D' 
                                   | otherwise = procuraPowerRD m j (x,y) (a+1) a2 b

-- | A função @procuraTijolosRD@ procura __tijolos__ no mapa (num raio de 3 coordenadas), e em caso afirmativo o bot vai nessa direção.
procuraTijolosRD :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char  --a=2
procuraTijolosRD m j (x,y) a a2  = if (a==3)
    then procuraJogadoresRD m j (x,y) a2 ---VAI PROCURAR JOGADORES
    else procuraTijolosRDAux m j (x,y) a a2

-- | A função @procuraTijolosRDAux@ é a principal função auxiliar da função 'procuraTijolosRD'.
procuraTijolosRDAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char 
procuraTijolosRDAux m j (x,y) a a2 | ((m !! y !! (x+a)) == '?') && ((x+a) == (dimensaomapa m - 1)) = Just 'R'
                                   | ((m !! (y+a) !! x) == '?') && ((y+a) == (dimensaomapa m - 1)) = Just 'D'
                                   | otherwise = procuraTijolosRD m j (x,y) (a+1) a2
 
{-| A função @procuraJogadoresRD@ procura __jogadores__ no mapa, num raio de 3 coordenadas.

Caso não existam, vai simplesmente para baixo.
-}
procuraJogadoresRD :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char  --a=2
procuraJogadoresRD m j (x,y) a = if (a==3)
    then Just 'D' 
    else procuraJogadoresRDAux m j (x,y) a

-- | A função @procuraJogadoresRDAux@ é a principal função auxiliar da função 'procuraJogadoresRD'.
procuraJogadoresRDAux :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char 
procuraJogadoresRDAux m j (x,y) a | (elem ((x+a),y) (coordAdversarios m j)) && ((x+a) == (dimensaomapa m - 1)) = Just 'R' 
                                  | (elem (x,(y+a)) (coordAdversarios m j)) && ((y+a) == (dimensaomapa m - 1)) = Just 'D'
                                  | otherwise = procuraJogadoresRD m j (x,y) (a+1)


-----PROCURA POWERUPS, TIJOLOS E JOGADORES quando indecisa entre left e down
{-| A função @plantaBombasLD@ faz o bot __plantar uma bomba__ se tiver se o mesmo estiver
rodeado por algum tijolo ou jogador; caso não esteja, o bot irá __procurar__ algum power up destapado,
tijolo ou jogador (se estiverem em coordenadas próximas). 
-}
plantaBombasLD :: Mapa -> NBot -> Maybe Char
plantaBombasLD m j = if (((temTijolosAVolta m j) == True ) || ((temJogadoresAVolta m j) == True)) then Just 'B'
                     else (procuraPowerLD m j (coordBot m j) 1 2 2)

-- | A função @procuraPowerLD@ faz com que o bot averigue se tem __algum power up próximo de si__ (num raio de 3 coordenadas à sua volta).
procuraPowerLD :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Int -> Maybe Char
procuraPowerLD m j (x,y) a a2 b = if (a==3)
    then procuraTijolosLD m j (x,y) a2 b ---VAI PROCURAR TIJOLOS
    else procuraPowerLDAux m j (x,y) a a2 b

-- | A função @procuraPowerLDAux@ é a principal função auxiliar da função 'procuraPowerLD'.
procuraPowerLDAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Int -> Maybe Char 
procuraPowerLDAux m j (x,y) a a2 b | (listapudestapados m) == [] = procuraTijolosLD m j (x,y) a2 b
                                   | (elem ((x-a),y) (listapudestapados m)) && ((x-a) == 1) = Just 'L'  
                                   | (elem (x,(y+a)) (listapudestapados m)) && ((y+a) == (dimensaomapa m - 1)) = Just 'D' 
                                   | otherwise = procuraPowerLD m j (x,y) (a+1) a2 b

-- | A função @procuraTijolosLD@ procura __tijolos__ no mapa (num raio de 3 coordenadas), e em caso afirmativo o bot vai nessa direção.
procuraTijolosLD :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char  --a=2
procuraTijolosLD m j (x,y) a a2 = if (a==3)
    then procuraJogadoresLD m j (x,y) a2 ---VAI PROCURAR JOGADORES
    else procuraTijolosLDAux m j (x,y) a a2

-- | A função @procuraTijolosLDAux@ é a principal função auxiliar da função 'procuraTijolosLD'.
procuraTijolosLDAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char 
procuraTijolosLDAux m j (x,y) a a2 | ((m !! y !! (x-a)) == '?') && ((x-a) == 1) = Just 'L'
                                   | ((m !! (y+a) !! x) == '?') && ((y+a) == (dimensaomapa m - 1)) = Just 'D'
                                   | otherwise = procuraTijolosLD m j (x,y) (a+1) a2
 
{-| A função @procuraJogadoresLD@ procura __jogadores__ no mapa, num raio de 3 coordenadas.

Caso não existam, vai simplesmente para baixo.
-}
procuraJogadoresLD :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char  --a=2
procuraJogadoresLD m j (x,y) a = if (a==3)
    then Just 'D' --- "Aleatório"
    else procuraJogadoresLDAux m j (x,y) a

-- | A função @procuraJogadoresLDAux@ é a principal função auxiliar da função 'procuraJogadoresLD'.
procuraJogadoresLDAux :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char 
procuraJogadoresLDAux m j (x,y) a | (elem ((x-a),y) (coordAdversarios m j)) && ((x-a) == 1) = Just 'L' 
                                  | (elem (x,(y+a)) (coordAdversarios m j)) && ((y+a) == (dimensaomapa m - 1)) = Just 'D'
                                  | otherwise = procuraJogadoresLD m j (x,y) (a+1)



-----PROCURA POWERUPS, TIJOLOS E JOGADORES quando indecisa entre down e up
{-| A função @plantaBombasDU@ faz o bot __plantar uma bomba__ se tiver se o mesmo estiver
rodeado por algum tijolo ou jogador; caso não esteja, o bot irá __procurar__ algum power up destapado,
tijolo ou jogador (se estiverem em coordenadas próximas). 
-}
plantaBombasDU :: Mapa -> NBot -> Maybe Char
plantaBombasDU m j = if (((temTijolosAVolta m j) == True ) || ((temJogadoresAVolta m j) == True)) then Just 'B'
                     else (procuraPowerDU m j (coordBot m j) 1 2 2)

-- | A função @procuraPowerDU@ faz com que o bot averigue se tem __algum power up próximo de si__ (num raio de 3 coordenadas à sua volta).
procuraPowerDU :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Int -> Maybe Char
procuraPowerDU m j (x,y) a a2 b = if (a==3)
    then procuraTijolosDU m j (x,y) a2 b ----VAI PROCURAR TIJOLOS
    else procuraPowerDUAux m j (x,y) a a2 b

-- | A função @procuraPowerDUAux@ é a principal função auxiliar da função 'procuraPowerDU'.
procuraPowerDUAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Int -> Maybe Char 
procuraPowerDUAux m j (x,y) a a2 b | (listapudestapados m) == [] = procuraTijolosDU m j (x,y) a2 b
                                   | (elem (x,(y+a)) (listapudestapados m)) && ((y+a) == (dimensaomapa m - 1)) = Just 'D'  
                                   | (elem (x,(y-a)) (listapudestapados m)) && ((y-a) == 1) = Just 'U' 
                                   | otherwise = procuraPowerDU m j (x,y) (a+1) a2 b

-- | A função @procuraTijolosDU@ procura __tijolos__ no mapa (num raio de 3 coordenadas), e em caso afirmativo o bot vai nessa direção.
procuraTijolosDU :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char  --a=2
procuraTijolosDU m j (x,y) a a2  = if (a==3)
    then procuraJogadoresDU m j (x,y) a2 ---VAI PROCURAR JOGADORES
    else procuraTijolosDUAux m j (x,y) a a2

-- | A função @procuraTijolosDUAux@ é a principal função auxiliar da função 'procuraTijolosDU'.
procuraTijolosDUAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char 
procuraTijolosDUAux m j (x,y) a a2 | ((m !! (y+a) !! x) == '?') && ((y+a) == (dimensaomapa m - 1)) = Just 'D'
                                   | ((m !! (y-a) !! x) == '?') && ((y-a) == 1) = Just 'U'
                                   | otherwise = procuraTijolosDU m j (x,y) (a+1) a2
 
{-| A função @procuraJogadoresDU@ procura __jogadores__ no mapa, num raio de 3 coordenadas.

Caso não existam, vai simplesmente para baixo.
-}
procuraJogadoresDU :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char  --a=2
procuraJogadoresDU m j (x,y) a = if (a==3)
    then Just 'D'
    else procuraJogadoresDUAux m j (x,y) a

-- | A função @procuraJogadoresDUAux@ é a principal função auxiliar da função 'procuraJogadoresDU'.
procuraJogadoresDUAux :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char 
procuraJogadoresDUAux m j (x,y) a | (elem (x,(y+a)) (coordAdversarios m j)) && ((y+a) == (dimensaomapa m - 1)) = Just 'D' 
                                  | (elem (x,(y-a)) (coordAdversarios m j)) && ((y-a) == 1) = Just 'U'
                                  | otherwise = procuraJogadoresDU m j (x,y) (a+1)



-----PROCURA POWERUPS, TIJOLOS E JOGADORES quando indecisa entre left e right
{-| A função @plantaBombasLR@ faz o bot __plantar uma bomba__ se tiver se o mesmo estiver
rodeado por algum tijolo ou jogador; caso não esteja, o bot irá __procurar__ algum power up destapado,
tijolo ou jogador (se estiverem em coordenadas próximas). 
-}
plantaBombasLR :: Mapa -> NBot -> Maybe Char
plantaBombasLR m j = if (((temTijolosAVolta m j) == True ) || ((temJogadoresAVolta m j) == True)) then Just 'B'
                     else (procuraPowerLR m j (coordBot m j) 1 2 2)

-- | A função @procuraPowerLR@ faz com que o bot averigue se tem __algum power up próximo de si__ (num raio de 3 coordenadas à sua volta).
procuraPowerLR :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Int -> Maybe Char
procuraPowerLR m j (x,y) a a2 b = if (a==3)
    then procuraTijolosLR m j (x,y) a2 b ----VAI PROCURAR TIJOLOS
    else procuraPowerLRAux m j (x,y) a a2 b

-- | A função @procuraPowerLRAux@ é a principal função auxiliar da função 'procuraPowerLR'.
procuraPowerLRAux :: Mapa -> NBot -> Coordenadas -> Int ->  Int ->  Int -> Maybe Char 
procuraPowerLRAux m j (x,y) a a2 b | (listapudestapados m) == [] = procuraTijolosLR m j (x,y) a2 b
                                   | (elem ((x-a),y) (listapudestapados m)) && ((x-a) == 1) = Just 'L'  
                                   | (elem ((x+a),y) (listapudestapados m)) && ((x+a) == (dimensaomapa m - 1)) = Just 'R' 
                                   | otherwise = procuraPowerLR m j (x,y) (a+1) a2 b

-- | A função @procuraTijolosLR@ procura __tijolos__ no mapa (num raio de 3 coordenadas), e em caso afirmativo o bot vai nessa direção.
procuraTijolosLR :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char  --a=2
procuraTijolosLR m j (x,y) a a2 = if (a==3)
    then procuraJogadoresLR m j (x,y) a2 ---VAI PROCURAR JOGADORES
    else procuraTijolosLRAux m j (x,y) a a2

-- | A função @procuraTijolosLRAux@ é a principal função auxiliar da função 'procuraTijolosLR'.
procuraTijolosLRAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char 
procuraTijolosLRAux m j (x,y) a a2 | ((m !! y !! (x-a)) == '?') && ((x-a) == 1) = Just 'L'
                                   | ((m !! y !! (x+a)) == '?') && ((x+a) == (dimensaomapa m - 1)) = Just 'R'
                                   | otherwise = procuraTijolosLR m j (x,y) (a+1) a2
 
{-| A função @procuraJogadoresLR@ procura __jogadores__ no mapa, num raio de 3 coordenadas.

Caso não existam, vai simplesmente para a direita.
-}
procuraJogadoresLR :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char  --a=2
procuraJogadoresLR m j (x,y) a = if (a==3)
    then Just 'R'
    else procuraJogadoresLRAux m j (x,y) a

-- | A função @procuraJogadoresLRAux@ é a principal função auxiliar da função 'procuraJogadoresLR'.
procuraJogadoresLRAux :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char 
procuraJogadoresLRAux m j (x,y) a | (elem ((x-a),y) (coordAdversarios m j)) && ((x-a) == 1) = Just 'L' 
                                  | (elem ((x+a),y) (coordAdversarios m j)) && ((x+a) == (dimensaomapa m - 1)) = Just 'R'
                                  | otherwise = procuraJogadoresLR m j (x,y) (a+1)


-----PROCURA POWERUPS, TIJOLOS E JOGADORES quando indecisa entre up e right
{-| A função @plantaBombasUR@ faz o bot __plantar uma bomba__ se tiver se o mesmo estiver
rodeado por algum tijolo ou jogador; caso não esteja, o bot irá __procurar__ algum power up destapado,
tijolo ou jogador (se estiverem em coordenadas próximas). 
-}
plantaBombasUR :: Mapa -> NBot -> Maybe Char
plantaBombasUR m j = if (((temTijolosAVolta m j) == True ) || ((temJogadoresAVolta m j) == True)) then Just 'B'
                     else (procuraPowerUR m j (coordBot m j) 1 2 2)

-- | A função @procuraPowerUR@ faz com que o bot averigue se tem __algum power up próximo de si__ (num raio de 3 coordenadas à sua volta).
procuraPowerUR :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Int -> Maybe Char
procuraPowerUR m j (x,y) a a2 b = if (a==3)
    then procuraTijolosUR m j (x,y) a2 b
    else procuraPowerURAux m j (x,y) a a2 b

-- | A função @procuraPowerURAux@ é a principal função auxiliar da função 'procuraPowerUR'.
procuraPowerURAux :: Mapa -> NBot -> Coordenadas -> Int ->  Int -> Int -> Maybe Char 
procuraPowerURAux m j (x,y) a a2 b | (listapudestapados m) == [] = procuraTijolosUR m j (x,y) a2 b
                                   | (elem (x,(y-a)) (listapudestapados m)) && ((y-a) == 1) = Just 'U'  
                                   | (elem ((x+a),y) (listapudestapados m)) && ((x+a) == (dimensaomapa m - 1)) = Just 'R' 
                                   | otherwise = procuraPowerUR m j (x,y) (a+1) a2 b

-- | A função @procuraTijolosUR@ procura __tijolos__ no mapa (num raio de 3 coordenadas), e em caso afirmativo o bot vai nessa direção.
procuraTijolosUR :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char  --a=2
procuraTijolosUR m j (x,y) a a2  = if (a==3)
    then procuraJogadoresUR m j (x,y) a2 ---VAI PROCURAR JOGADORES
    else procuraTijolosURAux m j (x,y) a a2

-- | A função @procuraTijolosURAux@ é a principal função auxiliar da função 'procuraTijolosUR'.
procuraTijolosURAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char 
procuraTijolosURAux m j (x,y) a a2 | ((m !! (y-a) !! x) == '?') && ((y-a) == 1) = Just 'U'
                                   | ((m !! y !! (x+a)) == '?') && ((x+a) == (dimensaomapa m - 1)) = Just 'R'
                                   | otherwise = procuraTijolosUR m j (x,y) (a+1) a2

{-| A função @procuraJogadoresUR@ procura __jogadores__ no mapa, num raio de 3 coordenadas.

Caso não existam, vai simplesmente para a direita.
-}
procuraJogadoresUR :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char  --a=2
procuraJogadoresUR m j (x,y) a = if (a==3)
    then Just 'R'  ----VAI PROCURAR POWER UPS
    else procuraJogadoresURAux m j (x,y) a

-- | A função @procuraJogadoresURAux@ é a principal função auxiliar da função 'procuraJogadoresUR'.
procuraJogadoresURAux :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char 
procuraJogadoresURAux m j (x,y) a | (elem (x,(y-a)) (coordAdversarios m j)) && ((y-a) == 1) = Just 'U' 
                                  | (elem ((x+a),y) (coordAdversarios m j)) && ((x+a) == (dimensaomapa m - 1)) = Just 'R'
                                  | otherwise = procuraJogadoresUR m j (x,y) (a+1)



-----PROCURA POWERUPS, TIJOLOS E JOGADORES quando indecisa entre up e left
{-| A função @plantaBombasUL@ faz o bot __plantar uma bomba__ se tiver se o mesmo estiver
rodeado por algum tijolo ou jogador; caso não esteja, o bot irá __procurar__ algum power up destapado,
tijolo ou jogador (se estiverem em coordenadas próximas). 
-}
plantaBombasUL :: Mapa -> NBot -> Maybe Char
plantaBombasUL m j = if (((temTijolosAVolta m j) == True ) || ((temJogadoresAVolta m j) == True)) then Just 'B'
                     else (procuraPowerUL m j (coordBot m j) 1 2 2)

-- | A função @procuraPowerUL@ faz com que o bot averigue se tem __algum power up próximo de si__ (num raio de 3 coordenadas à sua volta).
procuraPowerUL :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Int -> Maybe Char
procuraPowerUL m j (x,y) a a2 b = if (a==3)
    then procuraTijolosUL m j (x,y) a2 b ---VAI PROCURAR TIJOLOS
    else procuraPowerULAux m j (x,y) a a2 b

-- | A função @procuraPowerULAux@ é a principal função auxiliar da função 'procuraPowerUL'.
procuraPowerULAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Int -> Maybe Char 
procuraPowerULAux m j (x,y) a a2 b | (listapudestapados m) == [] = procuraTijolosUL m j (x,y) a2 b
                                   | (elem (x,(y-a)) (listapudestapados m)) && ((y-a) == 1) = Just 'U'  
                                   | (elem ((x-a),y) (listapudestapados m)) && ((x-a) == 1) = Just 'L' 
                                   | otherwise = procuraPowerUL m j (x,y) (a+1) a2 b

-- | A função @procuraTijolosUL@ procura __tijolos__ no mapa (num raio de 3 coordenadas), e em caso afirmativo o bot vai nessa direção.
procuraTijolosUL :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char  --a=2
procuraTijolosUL m j (x,y) a a2  = if (a==3)
    then procuraJogadoresUL m j (x,y) a2 ---VAI PROCURAR JOGADORES
    else procuraTijolosULAux m j (x,y) a a2

-- | A função @procuraTijolosULAux@ é a principal função auxiliar da função 'procuraTijolosUL'.
procuraTijolosULAux :: Mapa -> NBot -> Coordenadas -> Int -> Int -> Maybe Char 
procuraTijolosULAux m j (x,y) a a2 | ((m !! (y-a) !! x) == '?') && ((y-a) == 1) = Just 'U'
                                   | ((m !! y !! (x-a)) == '?') && ((x-a) == 1) = Just 'L'
                                   | otherwise = procuraTijolosUL m j (x,y) (a+1) a2
 
{-| A função @procuraJogadoresUL@ procura __jogadores__ no mapa, num raio de 3 coordenadas.

Caso não existam, o bot não faz anda.
-}
procuraJogadoresUL :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char  --a=2
procuraJogadoresUL m j (x,y) a = if (a==3)
    then Nothing
    else procuraJogadoresULAux m j (x,y) a

-- | A função @procuraJogadoresULAux@ é a principal função auxiliar da função 'procuraJogadoresUL'.
procuraJogadoresULAux :: Mapa -> NBot -> Coordenadas -> Int -> Maybe Char 
procuraJogadoresULAux m j (x,y) a | (elem (x,(y-a)) (coordAdversarios m j)) && ((y-a) == 1) = Just 'U' 
                                  | (elem ((x-a),y) (coordAdversarios m j)) && ((x-a) == 1) = Just 'L'
                                  | otherwise = procuraJogadoresUL m j (x,y) (a+1)


-- | A função @temTijolosAVolta@ vê se o bot está /"cercado"/ por algum __tijolo__.
temTijolosAVolta :: Mapa -> NBot -> Bool
temTijolosAVolta m j = temTijolosAVoltaAux m (coordBot m j)

-- | A função @temTijolosAVoltaAux@ é a principal função auxiliar da função 'temTijolosAVolta'.
temTijolosAVoltaAux :: Mapa -> Coordenadas -> Bool 
temTijolosAVoltaAux m (x,y) | (((m !! (y+1) !! x) == '?') || ((m !! (y-1) !! x) == '?') || ((m !! y !! (x+1)) == '?') || ((m !! y !! (x-1)) == '?')) = True
                            | otherwise = False

-- | A função @temJogadoresAVolta@ vê se o bot tem __jogadores à sua volta__ (ou na própria posição).
temJogadoresAVolta :: Mapa -> NBot -> Bool
temJogadoresAVolta m j = temJogadoresAVoltaAux m j (coordBot m j) (coordAdversarios m j)
  where temJogadoresAVoltaAux m j (x,y) adv | elem (x,y) adv = True
                                            | elem (downc m j) adv = True
                                            | elem (upc m j) adv = True
                                            | elem (leftc m j) adv = True
                                            | elem (rightc m j) adv = True
                                            | otherwise = False


-----------------------FUGIR DAS BOMBAS----------------
-----FUGIR DAS BOMBAS QUANDO INDECIDO ENTRE AS 4 DIREÇOES 

-- | A função @fugirDULR@ ajuda o bot a __fugir das bombas__, quando ele está indeciso entre todas as direções.                                                                         
fugirDULR :: Mapa -> NBot -> Maybe Char
fugirDULR m j = fugirDULRAux m (paraOndePossoIr m (coordBot m j))
                                                                             
fugirDULRAux :: Mapa -> [Coordenadas] -> Maybe Char
fugirDULRAux m [(x1,y1),(x2,y2),(x3,y3),(x4,y4)] | (semPerigo m (x1,y1)) = Just 'D'
                                                 | (semPerigo m (x2,y2)) = Just 'U'
                                                 | (semPerigo m (x3,y3)) = Just 'L'
                                                 | (semPerigo m (x4,y4)) = Just 'R'
                                                 | otherwise = estudaDULR1 m [(x1,y1),(x2,y2),(x3,y3),(x4,y4)]
                                                                                            
estudaDULR1 :: Mapa -> [Coordenadas] -> Maybe Char
estudaDULR1 m [(x1,y1),(x2,y2),(x3,y3),(x4,y4)] = if (semPerigoLista m (paraOndePossoIr m (x1,y1))) then Just 'D'
                                                                                   else estudaDULR2 m [(x1,y1),(x2,y2),(x3,y3),(x4,y4)]
estudaDULR2  :: Mapa -> [Coordenadas] -> Maybe Char
estudaDULR2 m [(x1,y1),(x2,y2),(x3,y3),(x4,y4)] = if (semPerigoLista m (paraOndePossoIr m (x2,y2))) then Just 'U'
                                                                                  else estudaDULR3 m [(x1,y1),(x2,y2),(x3,y3),(x4,y4)]
estudaDULR3  :: Mapa -> [Coordenadas] -> Maybe Char
estudaDULR3 m [(x1,y1),(x2,y2),(x3,y3),(x4,y4)] = if (semPerigoLista m (paraOndePossoIr m (x3,y3))) then Just 'L'
                                                                                  else estudaDULR4 m [(x1,y1),(x2,y2),(x3,y3),(x4,y4)]
estudaDULR4  :: Mapa -> [Coordenadas] -> Maybe Char
estudaDULR4 m [(x1,y1),(x2,y2),(x3,y3),(x4,y4)] = if (semPerigoLista m (paraOndePossoIr m (x4,y4))) then Just 'R'
                                                                                  else estudaDULR11 m [(x1,y1),(x2,y2),(x3,y3),(x4,y4)]

estudaDULR11 :: Mapa -> [Coordenadas] -> Maybe Char
estudaDULR11 m [(x1,y1),(x2,y2),(x3,y3),(x4,y4)] = estudaDULR11Aux m (paraOndePossoIr m (x1,y1))
    where estudaDULR11Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'D'
                                                                                   else estudaDULR22 m [(x1,y1),(x2,y2),(x3,y3),(x4,y4)]
estudaDULR22 :: Mapa -> [Coordenadas] -> Maybe Char
estudaDULR22 m [(x1,y1),(x2,y2),(x3,y3),(x4,y4)] = estudaDULR22Aux m (paraOndePossoIr m (x2,y2))
    where estudaDULR22Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'U'
                                                                                   else estudaDULR33 m [(x1,y1),(x2,y2),(x3,y3),(x4,y4)]
estudaDULR33 :: Mapa -> [Coordenadas] -> Maybe Char
estudaDULR33 m [(x1,y1),(x2,y2),(x3,y3),(x4,y4)] = estudaDULR33Aux m (paraOndePossoIr m (x3,y3))
    where estudaDULR33Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'L'
                                                                                   else estudaDULR44 m (x4,y4) 
estudaDULR44 :: Mapa -> Coordenadas -> Maybe Char
estudaDULR44 m (x4,y4) = estudaDULR44Aux m (paraOndePossoIr m (x4,y4))
    where estudaDULR44Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'R'
                                                                                   else Just 'R' 


-----FUGIR DAS BOMBAS QUANDO INDECISO ENTRE 3 DIREÇÕES

-- | A função @fugirDLR@ ajuda o bot a __fugir das bombas__, quando ele está indeciso entre down, left e right.                     
fugirDLR :: Mapa -> NBot -> Maybe Char
fugirDLR m j = fugirDLRAux m (paraOndePossoIr m (coordBot m j))
                                                                             
fugirDLRAux :: Mapa -> [Coordenadas] -> Maybe Char
fugirDLRAux m [(x1,y1),(x2,y2),(x3,y3)] | (semPerigo m (x1,y1)) = Just 'D'
                                        | (semPerigo m (x2,y2)) = Just 'L'
                                        | (semPerigo m (x3,y3)) = Just 'R'
                                        | otherwise = estudaDLR1 m [(x1,y1),(x2,y2),(x3,y3)]
                                                                                            
estudaDLR1 :: Mapa -> [Coordenadas] -> Maybe Char
estudaDLR1 m [(x1,y1),(x2,y2),(x3,y3)] = if (semPerigoLista m (paraOndePossoIr m (x1,y1))) then Just 'D'
                                                                                   else estudaDLR2 m [(x1,y1),(x2,y2),(x3,y3)]
estudaDLR2  :: Mapa -> [Coordenadas] -> Maybe Char
estudaDLR2 m [(x1,y1),(x2,y2),(x3,y3)] = if (semPerigoLista m (paraOndePossoIr m (x2,y2))) then Just 'L'
                                                                                  else estudaDLR3 m [(x1,y1),(x2,y2),(x3,y3)]  
estudaDLR3  :: Mapa -> [Coordenadas] -> Maybe Char
estudaDLR3 m [(x1,y1),(x2,y2),(x3,y3)] = if (semPerigoLista m (paraOndePossoIr m (x3,y3))) then Just 'R'
                                                                                  else estudaDLR11 m [(x1,y1),(x2,y2),(x3,y3)]  

estudaDLR11 :: Mapa -> [Coordenadas] -> Maybe Char
estudaDLR11 m [(x1,y1),(x2,y2),(x3,y3)] = estudaDLR11Aux m (paraOndePossoIr m (x1,y1))
    where estudaDLR11Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'D'
                                                                                  else estudaDLR22 m [(x1,y1),(x2,y2),(x3,y3)]
estudaDLR22 :: Mapa -> [Coordenadas] -> Maybe Char
estudaDLR22 m [(x1,y1),(x2,y2),(x3,y3)] = estudaDLR22Aux m (paraOndePossoIr m (x2,y2))
    where estudaDLR22Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'L'
                                                                                  else estudaDLR33 m (x3,y3)
estudaDLR33 :: Mapa -> Coordenadas -> Maybe Char
estudaDLR33 m (x3,y3) = estudaDLR33Aux m (paraOndePossoIr m (x3,y3))
    where estudaDLR33Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'R'
                                                                                  else Just 'R' 

-- | A função @fugirDUL@ ajuda o bot a __fugir das bombas__, quando ele está indeciso entre down, up e left.                                                                           
fugirDUL :: Mapa -> NBot -> Maybe Char
fugirDUL m j = fugirDULAux m (paraOndePossoIr m (coordBot m j))
                                                                             
fugirDULAux :: Mapa -> [Coordenadas] -> Maybe Char
fugirDULAux m [(x1,y1),(x2,y2),(x3,y3)] | (semPerigo m (x1,y1)) = Just 'D'
                                        | (semPerigo m (x2,y2)) = Just 'U'
                                        | (semPerigo m (x3,y3)) = Just 'L'
                                        | otherwise = estudaDUL1 m [(x1,y1),(x2,y2),(x3,y3)]
                                                                                            
estudaDUL1 :: Mapa -> [Coordenadas] -> Maybe Char
estudaDUL1 m [(x1,y1),(x2,y2),(x3,y3)] = if (semPerigoLista m (paraOndePossoIr m (x1,y1))) then Just 'D'
                                                                                   else estudaDUL2 m [(x1,y1),(x2,y2),(x3,y3)]
estudaDUL2  :: Mapa -> [Coordenadas] -> Maybe Char
estudaDUL2 m [(x1,y1),(x2,y2),(x3,y3)] = if (semPerigoLista m (paraOndePossoIr m (x2,y2))) then Just 'U'
                                                                                  else estudaDUL3 m [(x1,y1),(x2,y2),(x3,y3)]  
estudaDUL3  :: Mapa -> [Coordenadas] -> Maybe Char
estudaDUL3 m [(x1,y1),(x2,y2),(x3,y3)] = if (semPerigoLista m (paraOndePossoIr m (x3,y3))) then Just 'L'
                                                                                  else estudaDUL11 m [(x1,y1),(x2,y2),(x3,y3)]  

estudaDUL11 :: Mapa -> [Coordenadas] -> Maybe Char
estudaDUL11 m [(x1,y1),(x2,y2),(x3,y3)] = estudaDUL11Aux m (paraOndePossoIr m (x1,y1))
    where estudaDUL11Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'D'
                                                                                  else estudaDUL22 m [(x1,y1),(x2,y2),(x3,y3)]
estudaDUL22 :: Mapa -> [Coordenadas] -> Maybe Char
estudaDUL22 m [(x1,y1),(x2,y2),(x3,y3)] = estudaDUL22Aux m (paraOndePossoIr m (x2,y2))
    where estudaDUL22Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'U'
                                                                                  else estudaDUL33 m (x3,y3)
estudaDUL33 :: Mapa -> Coordenadas -> Maybe Char
estudaDUL33 m (x3,y3) = estudaDUL33Aux m (paraOndePossoIr m (x3,y3))
    where estudaDUL33Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'L'
                                                                                  else Just 'L' 

-- | A função @fugirDUR@ ajuda o bot a __fugir das bombas__, quando ele está indeciso entre down, up e right.                                                                           
fugirDUR :: Mapa -> NBot -> Maybe Char
fugirDUR m j = fugirDURAux m (paraOndePossoIr m (coordBot m j))
                                                                             
fugirDURAux :: Mapa -> [Coordenadas] -> Maybe Char
fugirDURAux m [(x1,y1),(x2,y2),(x3,y3)] | (semPerigo m (x1,y1)) = Just 'D'
                                        | (semPerigo m (x2,y2)) = Just 'U'
                                        | (semPerigo m (x3,y3)) = Just 'R'
                                        | otherwise = estudaDUR1 m [(x1,y1),(x2,y2),(x3,y3)]
                                                                                            
estudaDUR1 :: Mapa -> [Coordenadas] -> Maybe Char
estudaDUR1 m [(x1,y1),(x2,y2),(x3,y3)] = if (semPerigoLista m (paraOndePossoIr m (x1,y1))) then Just 'D'
                                                                                   else estudaDUR2 m [(x1,y1),(x2,y2),(x3,y3)]
estudaDUR2  :: Mapa -> [Coordenadas] -> Maybe Char
estudaDUR2 m [(x1,y1),(x2,y2),(x3,y3)] = if (semPerigoLista m (paraOndePossoIr m (x2,y2))) then Just 'U'
                                                                                  else estudaDUR3 m [(x1,y1),(x2,y2),(x3,y3)]  
estudaDUR3  :: Mapa -> [Coordenadas] -> Maybe Char
estudaDUR3 m [(x1,y1),(x2,y2),(x3,y3)] = if (semPerigoLista m (paraOndePossoIr m (x3,y3))) then Just 'R'
                                                                                  else estudaDUR11 m [(x1,y1),(x2,y2),(x3,y3)]  

estudaDUR11 :: Mapa -> [Coordenadas] -> Maybe Char
estudaDUR11 m [(x1,y1),(x2,y2),(x3,y3)] = estudaDUR11Aux m (paraOndePossoIr m (x1,y1))
    where estudaDUR11Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'D'
                                                                                  else estudaDUR22 m [(x1,y1),(x2,y2),(x3,y3)]
estudaDUR22 :: Mapa -> [Coordenadas] -> Maybe Char
estudaDUR22 m [(x1,y1),(x2,y2),(x3,y3)] = estudaDUR22Aux m (paraOndePossoIr m (x2,y2))
    where estudaDUR22Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'U'
                                                                                  else estudaDUR33 m (x3,y3)
estudaDUR33 :: Mapa -> Coordenadas -> Maybe Char
estudaDUR33 m (x3,y3) = estudaDUR33Aux m (paraOndePossoIr m (x3,y3))
    where estudaDUR33Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'R'
                                                                                  else Just 'R' 

-- | A função @fugirULR@ ajuda o bot a __fugir das bombas__, quando ele está indeciso entre up, left e right.                                                                             
fugirULR :: Mapa -> NBot -> Maybe Char
fugirULR m j = fugirULRAux m (paraOndePossoIr m (coordBot m j))
                                                                             
fugirULRAux :: Mapa -> [Coordenadas] -> Maybe Char
fugirULRAux m [(x1,y1),(x2,y2),(x3,y3)] | (semPerigo m (x1,y1)) = Just 'U'
                                        | (semPerigo m (x2,y2)) = Just 'L'
                                        | (semPerigo m (x3,y3)) = Just 'R'
                                        | otherwise = estudaULR1 m [(x1,y1),(x2,y2),(x3,y3)]
                                                                                            
estudaULR1 :: Mapa -> [Coordenadas] -> Maybe Char
estudaULR1 m [(x1,y1),(x2,y2),(x3,y3)] = if (semPerigoLista m (paraOndePossoIr m (x1,y1))) then Just 'U'
                                                                                   else estudaULR2 m [(x1,y1),(x2,y2),(x3,y3)]
estudaULR2  :: Mapa -> [Coordenadas] -> Maybe Char
estudaULR2 m [(x1,y1),(x2,y2),(x3,y3)] = if (semPerigoLista m (paraOndePossoIr m (x2,y2))) then Just 'L'
                                                                                  else estudaULR3 m [(x1,y1),(x2,y2),(x3,y3)]  
estudaULR3  :: Mapa -> [Coordenadas] -> Maybe Char
estudaULR3 m [(x1,y1),(x2,y2),(x3,y3)] = if (semPerigoLista m (paraOndePossoIr m (x3,y3))) then Just 'R'
                                                                                  else estudaULR11 m [(x1,y1),(x2,y2),(x3,y3)]  

estudaULR11 :: Mapa -> [Coordenadas] -> Maybe Char
estudaULR11 m [(x1,y1),(x2,y2),(x3,y3)] = estudaULR11Aux m (paraOndePossoIr m (x1,y1))
    where estudaULR11Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'U'
                                                                                  else estudaULR22 m [(x1,y1),(x2,y2),(x3,y3)]
estudaULR22 :: Mapa -> [Coordenadas] -> Maybe Char
estudaULR22 m [(x1,y1),(x2,y2),(x3,y3)] = estudaULR22Aux m (paraOndePossoIr m (x2,y2))
    where estudaULR22Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'L'
                                                                                  else estudaULR33 m (x3,y3)
estudaULR33 :: Mapa -> Coordenadas -> Maybe Char
estudaULR33 m (x3,y3) = estudaULR33Aux m (paraOndePossoIr m (x3,y3))
    where estudaULR33Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'R'
                                                                                  else Just 'R' 

------FUGIR DAS BOMBAS QUANDO INDECISO ENTRE 2 DIREÇÕES

-- | A função @fugirRD@ ajuda o bot a __fugir das bombas__, quando ele está indeciso entre right e down.                                                                          
fugirRD :: Mapa -> NBot -> Maybe Char
fugirRD m j = fugirRDAux m (paraOndePossoIr m (coordBot m j))
                                                                             
fugirRDAux :: Mapa -> [Coordenadas] -> Maybe Char
fugirRDAux m [(x1,y1),(x2,y2)] | (semPerigo m (x1,y1)) = Just 'D'
                               | (semPerigo m (x2,y2)) = Just 'R'
                               | otherwise = estudaRD1 m [(x1,y1),(x2,y2)]
                                                                                            
estudaRD1 :: Mapa -> [Coordenadas] -> Maybe Char
estudaRD1 m [(x1,y1),(x2,y2)] = if (semPerigoLista m (paraOndePossoIr m (x1,y1))) then Just 'D'
                                                                                   else estudaRD2 m [(x1,y1),(x2,y2)]
estudaRD2  :: Mapa -> [Coordenadas] -> Maybe Char
estudaRD2 m [(x1,y1),(x2,y2)] = if (semPerigoLista m (paraOndePossoIr m (x2,y2))) then Just 'R'
                                                                                  else estudaRD11 m [(x1,y1),(x2,y2)]            
estudaRD11 :: Mapa -> [Coordenadas] -> Maybe Char
estudaRD11 m [(x1,y1),(x2,y2)] = estudaRD11Aux m (paraOndePossoIr m (x1,y1))
    where estudaRD11Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'D'
                                                                                 else estudaRD22 m (x2,y2)
estudaRD22 :: Mapa -> Coordenadas -> Maybe Char
estudaRD22 m (x2,y2) = estudaRD22Aux m (paraOndePossoIr m (x2,y2))
    where estudaRD22Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'R'
                                                                                 else Just 'R' 

-- | A função @fugirLD@ ajuda o bot a __fugir das bombas__, quando ele está indeciso entre left e down.                                                                             
fugirLD :: Mapa -> NBot -> Maybe Char
fugirLD m j = fugirLDAux m (paraOndePossoIr m (coordBot m j))
                                                                             
fugirLDAux :: Mapa -> [Coordenadas] -> Maybe Char
fugirLDAux m [(x1,y1),(x2,y2)] | (semPerigo m (x1,y1)) = Just 'D'
                               | (semPerigo m (x2,y2)) = Just 'L'
                               | otherwise = estudaLD1 m [(x1,y1),(x2,y2)]
                                                                                            
estudaLD1 :: Mapa -> [Coordenadas] -> Maybe Char
estudaLD1 m [(x1,y1),(x2,y2)] = if (semPerigoLista m (paraOndePossoIr m (x1,y1))) then Just 'D'
                                                                                   else estudaLD2 m [(x1,y1),(x2,y2)]
estudaLD2  :: Mapa -> [Coordenadas] -> Maybe Char
estudaLD2 m [(x1,y1),(x2,y2)] = if (semPerigoLista m (paraOndePossoIr m (x2,y2))) then Just 'L'
                                                                                  else estudaLD11 m [(x1,y1),(x2,y2)]            
estudaLD11 :: Mapa -> [Coordenadas] -> Maybe Char
estudaLD11 m [(x1,y1),(x2,y2)] = estudaLD11Aux m (paraOndePossoIr m (x1,y1))
    where estudaLD11Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'D'
                                                                                 else estudaLD22 m (x2,y2)
estudaLD22 :: Mapa -> Coordenadas -> Maybe Char
estudaLD22 m (x2,y2) = estudaLD22Aux m (paraOndePossoIr m (x2,y2))
    where estudaLD22Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'L'
                                                                                 else Just 'L' 

-- | A função @fugirDU@ ajuda o bot a __fugir das bombas__, quando ele está indeciso entre down e up.                                                                             
fugirDU :: Mapa -> NBot -> Maybe Char
fugirDU m j = fugirDUAux m (paraOndePossoIr m (coordBot m j))
                                                                             
fugirDUAux :: Mapa -> [Coordenadas] -> Maybe Char
fugirDUAux m [(x1,y1),(x2,y2)] | (semPerigo m (x1,y1)) = Just 'D'
                               | (semPerigo m (x2,y2)) = Just 'U'
                               | otherwise = estudaDU1 m [(x1,y1),(x2,y2)]
                                                                                            
estudaDU1 :: Mapa -> [Coordenadas] -> Maybe Char
estudaDU1 m [(x1,y1),(x2,y2)] = if (semPerigoLista m (paraOndePossoIr m (x1,y1))) then Just 'D'
                                                                                   else estudaDU2 m [(x1,y1),(x2,y2)]
estudaDU2  :: Mapa -> [Coordenadas] -> Maybe Char
estudaDU2 m [(x1,y1),(x2,y2)] = if (semPerigoLista m (paraOndePossoIr m (x2,y2))) then Just 'U'
                                                                                  else estudaDU11 m [(x1,y1),(x2,y2)]            
estudaDU11 :: Mapa -> [Coordenadas] -> Maybe Char
estudaDU11 m [(x1,y1),(x2,y2)] = estudaDU11Aux m (paraOndePossoIr m (x1,y1))
    where estudaDU11Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'D'
                                                                                 else estudaDU22 m (x2,y2)
estudaDU22 :: Mapa -> Coordenadas -> Maybe Char
estudaDU22 m (x2,y2) = estudaDU22Aux m (paraOndePossoIr m (x2,y2))
    where estudaDU22Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'U'
                                                                                 else Just 'U' 

-- | A função @fugirLR@ ajuda o bot a __fugir das bombas__, quando ele está indeciso entre left e right.                                                                            
fugirLR :: Mapa -> NBot -> Maybe Char
fugirLR m j = fugirLRAux m (paraOndePossoIr m (coordBot m j))
                                                                             
fugirLRAux :: Mapa -> [Coordenadas] -> Maybe Char
fugirLRAux m [(x1,y1),(x2,y2)] | (semPerigo m (x1,y1)) = Just 'L'
                               | (semPerigo m (x2,y2)) = Just 'R'
                               | otherwise = estudaLR1 m [(x1,y1),(x2,y2)]
                                                                                            
estudaLR1 :: Mapa -> [Coordenadas] -> Maybe Char
estudaLR1 m [(x1,y1),(x2,y2)] = if (semPerigoLista m (paraOndePossoIr m (x1,y1))) then Just 'L'
                                                                                   else estudaLR2 m [(x1,y1),(x2,y2)]
estudaLR2  :: Mapa -> [Coordenadas] -> Maybe Char
estudaLR2 m [(x1,y1),(x2,y2)] = if (semPerigoLista m (paraOndePossoIr m (x2,y2))) then Just 'R'
                                                                                  else estudaLR11 m [(x1,y1),(x2,y2)]            
estudaLR11 :: Mapa -> [Coordenadas] -> Maybe Char
estudaLR11 m [(x1,y1),(x2,y2)] = estudaLR11Aux m (paraOndePossoIr m (x1,y1))
    where estudaLR11Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'L'
                                                                                 else estudaLR22 m (x2,y2)
estudaLR22 :: Mapa -> Coordenadas -> Maybe Char
estudaLR22 m (x2,y2) = estudaLR22Aux m (paraOndePossoIr m (x2,y2))
    where estudaLR22Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'R'
                                                                                 else Just 'R' 

-- | A função @fugirUR@ ajuda o bot a __fugir das bombas__, quando ele está indeciso entre up e right.                                                                             
fugirUR :: Mapa -> NBot -> Maybe Char
fugirUR m j = fugirURAux m (paraOndePossoIr m (coordBot m j))
                                                                             
fugirURAux :: Mapa -> [Coordenadas] -> Maybe Char
fugirURAux m [(x1,y1),(x2,y2)] | (semPerigo m (x1,y1)) = Just 'U'
                               | (semPerigo m (x2,y2)) = Just 'R'
                               | otherwise = estudaUR1 m [(x1,y1),(x2,y2)]
                                                                                            
estudaUR1 :: Mapa -> [Coordenadas] -> Maybe Char
estudaUR1 m [(x1,y1),(x2,y2)] = if (semPerigoLista m (paraOndePossoIr m (x1,y1))) then Just 'U'
                                                                                   else estudaUR2 m [(x1,y1),(x2,y2)]
estudaUR2  :: Mapa -> [Coordenadas] -> Maybe Char
estudaUR2 m [(x1,y1),(x2,y2)] = if (semPerigoLista m (paraOndePossoIr m (x2,y2))) then Just 'R'
                                                                                  else estudaUR11 m [(x1,y1),(x2,y2)]            
estudaUR11 :: Mapa -> [Coordenadas] -> Maybe Char
estudaUR11 m [(x1,y1),(x2,y2)] = estudaUR11Aux m (paraOndePossoIr m (x1,y1))
    where estudaUR11Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'U'
                                                                                 else estudaUR22 m (x2,y2)
estudaUR22 :: Mapa -> Coordenadas -> Maybe Char
estudaUR22 m (x2,y2) = estudaUR22Aux m (paraOndePossoIr m (x2,y2))
    where estudaUR22Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'R'
                                                                                 else Just 'R'  

-- | A função @fugirUL@ ajuda o bot a __fugir das bombas__, quando ele está indeciso entre up e left.                                                                            
fugirUL :: Mapa -> NBot -> Maybe Char
fugirUL m j = fugirULAux m (paraOndePossoIr m (coordBot m j))
                                                                             
fugirULAux :: Mapa -> [Coordenadas] -> Maybe Char
fugirULAux m [(x1,y1),(x2,y2)] | (semPerigo m (x1,y1)) = Just 'U'
                               | (semPerigo m (x2,y2)) = Just 'L'
                               | otherwise = estudaUL1 m [(x1,y1),(x2,y2)]
                                                                                            
estudaUL1 :: Mapa -> [Coordenadas] -> Maybe Char
estudaUL1 m [(x1,y1),(x2,y2)] = if (semPerigoLista m (paraOndePossoIr m (x1,y1))) then Just 'U'
                                                                                   else estudaUL2 m [(x1,y1),(x2,y2)]
estudaUL2  :: Mapa -> [Coordenadas] -> Maybe Char
estudaUL2 m [(x1,y1),(x2,y2)] = if (semPerigoLista m (paraOndePossoIr m (x2,y2))) then Just 'L'
                                                                                  else estudaUL11 m [(x1,y1),(x2,y2)]            
estudaUL11 :: Mapa -> [Coordenadas] -> Maybe Char
estudaUL11 m [(x1,y1),(x2,y2)] = estudaUL11Aux m (paraOndePossoIr m (x1,y1))
    where estudaUL11Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'U'
                                                                                 else estudaUL22 m [(x1,y1),(x2,y2)]
estudaUL22 :: Mapa -> [Coordenadas] -> Maybe Char
estudaUL22 m [(x1,y1),(x2,y2)] = estudaUL22Aux m (paraOndePossoIr m (x2,y2))
    where estudaUL22Aux m cs = if (semPerigoLista m (paraOndePossoIrLista m cs)) then Just 'L'
                                                                                 else Just 'L'                                                                     

----------

-- | A função @semPerigoLista@ vê se uma lista das coordenadas de uma "direcao" para onde o bot pode ir __não pertence__ as chamas das bombas plantadas.
semPerigoLista :: Mapa -> [Coordenadas] -> Bool
semPerigoLista m [] = False
semPerigoLista m (c1:cs) = (semPerigo m c1) || (semPerigoLista m cs)

--lista da funcao paraOndePossoIr
paraOndePossoIrLista :: Mapa -> [Coordenadas] -> [Coordenadas]
paraOndePossoIrLista m [] = []
paraOndePossoIrLista m (c1:cs) = (paraOndePossoIr m c1) ++ (paraOndePossoIrLista m cs)

-- |  A função @semPerigo@ vê se o bot __está em perigo__ numa determinada coordenada.
semPerigo :: Mapa -> Coordenadas -> Bool
semPerigo m (x,y) = not (elem (x,y) (chamasBombas m))   

-- | A função @paraOndePossoIr@ dá as 4 coordenadas (no máx.) que ele pode ir (tendo em atenção as pedras).
paraOndePossoIr :: Mapa -> Coordenadas -> [Coordenadas]
paraOndePossoIr m (x,y) = paraOndePossoIrAux m (paraOndePossoIrTodos m (x,y))

paraOndePossoIrAux :: Mapa -> [Coordenadas] -> [Coordenadas]
paraOndePossoIrAux m [] = []
paraOndePossoIrAux m ((x,y):zs) 
    = if ((m !! y !! x) == '?') || ((m !! y !! x) == '#') then paraOndePossoIrAux m zs
                                                          else ((x,y):paraOndePossoIrAux m zs)


paraOndePossoIrTodos :: Mapa -> Coordenadas -> [Coordenadas]
paraOndePossoIrTodos m (x,y) = [downcAux (x,y)] ++ [upcAux (x,y)] ++ [leftcAux (x,y)]  ++ [rightcAux (x,y)] 

