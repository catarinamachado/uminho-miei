module Ajudaa4 where
import Data.Char (isDigit)
import System.Environment
import Text.Read
import Data.Maybe
 


type Coordenadas = (Int,Int)
type Mapa = [String]
type NBot = Int
type TimeLeft = Int
type Raio = Int
type DimensaoMapa = Int


{-| A função @avanca@ devolve um novo estado de jogo consoante a __passagem de um instante de tempo__.

==Exemplo de utilização:
>>>avanca m 44 = ["#########","####### #","# #?#?# #","#  ?  ? #","#?# # #?#","# ?  ?  #","# #?# # #","#  ??   #","#########","* 4 5 0 3 1","* 5 6 1 3 1","0 5 4","1 6 5"]
-}

avanca :: Mapa -> TimeLeft -> Mapa
avanca m t = tempo m t (dimensaomapa m)

{-| A função @tempo@ devolve um novo estado de jogo consoante o tempo que falta para a espiral começar:

1. Se os instantes que faltam para o jogo acabar for /superior/ aos instantes que faltam para a espiral começar, então devolve o próprio mapa;
2. Se os instantes que faltam para o jogo acabar for /igual a 0/ (ou seja, o jogo terminou), então devolve o próprio mapa;
3. Se os instantes que faltam para o jogo acabar for /igual ou inferior/ aos instantes que faltam para a espiral começar, então, uma pedra cai na posição conveniente (com recurso à função 'novomapaComEspiral')

-}
tempo :: Mapa -> TimeLeft -> DimensaoMapa -> Mapa
tempo m t n | (t>((n-2)^2)) = (borala m)
            | (t == 0) = borala m
            | otherwise = borala (novomapaComEspiral m t)


---------------------------------------------EFEITO ESPIRAL--------------------------

 
{-| A função @dimensaomapa@ dá a __dimensão do mapa__ (consoante qualquer estado de jogo apresentado no input).

==Exemplo de utilização:
>>>dimensaomapa m = 9
-}
dimensaomapa :: Mapa -> DimensaoMapa
dimensaomapa (h:t) = length h

{-| A funcao @novomapaComEspiral@ devolve um novo estado de jogo com a continuação (ou início) do __efeito espiral__.

As funções 'veOndeCaiPedra' e 'novomapaComEspiral1' são as principais funções auxiliares desta função.
-}
novomapaComEspiral :: Mapa -> TimeLeft -> Mapa
novomapaComEspiral m t = veOndeCaiPedra (novomapaComEspiral1 m t) (coordenadas t (dimensaomapa m) (dimensaomapa m) 1 2)

{-| A função @veOndeCaiPedra@ averigua se a coordenada onde a pedra (da espiral) caiu em cima de uma bomba, power up ou jogador, e devolve o mapa consoante o pretendido:

* __Se caiu em cima de uma bomba__, essa bomba é removida do mapa (com recurso à função 'novomapaSemBombaExpl');
* __Se caiu em cima de um power up__, esse power up é removido do mapa (com recurso à função 'novomapaSemPu');
* __Se caiu em cima de um (ou vários, se tiverem mais do que um na mesma célula) jogador__, esse(s) jogador(es) perde(m) o jogo (com recurso à função 'novomapaSemJog');
* Caso contrário, isto é, __se simplesmente caiu em cima de um espaço vazio (sem power up), pedra, ou tijolo__, devolve o próprio mapa.
-}

veOndeCaiPedra :: Mapa -> Coordenadas -> Mapa
veOndeCaiPedra m (x,y) | (elem (x,y) (listabomb m)) = veOndeCaiPedra (novomapaSemBombaExpl m (x,y)) (x,y)
                       | (elem (x,y) (listapu m)) = veOndeCaiPedra (novomapaSemPU m (x,y)) (x,y)
                       | (elem (x,y) (listajog m)) = veOndeCaiPedra (novomapaSemJog m (x,y)) (x,y)
                       | otherwise = m

{-| A função @novomapaComEspiral1@ devolve o __mapa com a pedra (da espiral) colocada no sítio correto__.

Esta função recorre a algumas funções auxiliares, entre as quais 'novomapaComEspiral2' e 'novomapaComEspiralAux'.
-}

novomapaComEspiral1 :: Mapa -> TimeLeft -> Mapa
novomapaComEspiral1 m t = novomapaComEspiral2 m (dimensaomapa m) (dimensaomapa m) 1 2 t

novomapaComEspiral2 :: Mapa -> DimensaoMapa -> DimensaoMapa -> Int -> Int -> TimeLeft -> Mapa
novomapaComEspiral2 m d s i1 i2 t = novomapaComEspiralAux m m (coordenadas t d s i1 i2) (coordenadas t d s i1 i2)

novomapaComEspiralAux :: Mapa -> Mapa -> Coordenadas -> Coordenadas -> Mapa
novomapaComEspiralAux (h:t) m (x,y) (a,b) = if (y == 0) then [novaLinhaP m (a,b)] ++ t   --passa argumentos duplicados pq os outros são fixos 
                                                        else [h] ++ novomapaComEspiralAux t m (x,y-1) (a,b) 

{-| A função @novaLinhaP@, com o auxílio das funções 'substituiPedra' e 'pegaLinha', 
dado a mapa do estado atual e as coordenadas onde cai a pedra, dá como resultado a __nova linha com a pedra (#) na posição desejada__.
-}
novaLinhaP :: Mapa -> Coordenadas -> String
novaLinhaP m (x,y) = substituiPedra (pegaLinha m (x,y)) (x,y) 


{-| Na função @substituiPedra@, dada a linha do mapa onde cai a pedra e a respetiva coordenada, 
devolve a linha com a pedra (#) na posição desejada.
-}
substituiPedra :: String -> Coordenadas -> String
substituiPedra (h:t) (x,y) = if (x == 0) then "#" ++ t
                                         else [h] ++ substituiPedra t ((x-1),y)

-----------------------

{-| A função @coordenadas@, dá as coordenadas em que a próxima pedra vai cair.

Esta função tem como parâmetros:

* __t__: número instantes de tempo que faltam para o jogo terminar;
* __d__ , __s__: dimensão do mapa (estes valores encontram-se duplicados porque para as funções auxiliares é necessário que eles se encontrem desta forma);
* __i1__: número 1 (este 1 é necessário para as funções auxiliares, é uma espécie de "contador");
* __i2__: número 2 (este 2 é necessário para as funções auxiliares, é uma espécie de "contador").

==Exemplos de utilização:
>>>coordenadas 44 9 9 1 2 = (6,1)

>>>coordenadas 5 13 13 1 2 = (7,7)
-}

coordenadas :: TimeLeft -> DimensaoMapa -> DimensaoMapa -> Int -> Int -> Coordenadas
coordenadas t d s i1 i2 = coordenadasAux t (reverse (contadorEspiral d s i1 i2))

-- | A função @coordenadasAux@ auxilia a função 'coordenadas'.
coordenadasAux :: TimeLeft -> [Coordenadas] -> Coordenadas
coordenadasAux t (x:xs) = if (t==1) then x
                                    else coordenadasAux (t-1) xs    

---------------------

{-| A função @contadorEspiral@ dá como resposta as coordenadas do mapa pela ordem em que a espiral cai.

==Exemplos de utilização:
>>>contadorEspiral 9 9 1 2 = [(1,1),(2,1),(3,1),(4,1),(5,1),(6,1),(7,1),(7,2),(7,3),(7,4),(7,5),(7,6),(7,7),(6,7),(5,7),(4,7),(3,7),(2,7),(1,7),(1,6),(1,5),(1,4),(1,3),(1,2),(2,2),(3,2),(4,2),(5,2),(6,2),(6,3),(6,4),(6,5),(6,6),(5,6),(4,6),(3,6),(2,6),(2,5),(2,4),(2,3),(3,3),(4,3),(5,3),(5,4),(5,5),(4,5),(3,5),(3,4),(4,4)]

>>>contadorEspiral 5 5 1 2 = [(1,1),(2,1),(3,1),(3,2),(3,3),(2,3),(1,3),(1,2),(2,2)]
-}

contadorEspiral :: DimensaoMapa -> DimensaoMapa -> Int -> Int -> [Coordenadas]
contadorEspiral d s i1 i2 = zip (contadorX d s i1) (contadorY d s i1 i2)

{-| A função @contadorX@, dá a lista das coordenadas X (ou seja, as abcissas) do mapa espiral pela ordem em que as pedras caiem.

Esta função tem o auxílio das seguintes funções: 
'contadorX1', 'contadorX1A', 'contadorX2','contadorX2A', 'contadorX2AA', 'contadorX2AAA', 
'contadorX3', 'contadorX4', 'contadorX4A', 'contadorX5', 'contadorX5A' e 'contadorX6'.
-}

contadorX :: DimensaoMapa -> DimensaoMapa -> Int -> [Int]
contadorX d s i1 = ordenacao2 (contadorX3 d i1) (contadorX6 d s i1)
---
contadorX6 :: DimensaoMapa -> DimensaoMapa -> Int -> [[Int]]
contadorX6 d s i1 = ordenacao (contadorX4 d s) (contadorX5 d i1)

contadorX5 :: DimensaoMapa -> Int -> [[Int]]   --i=1
contadorX5 d i1 = eliminavazio (contadorX5A d i1)

contadorX5A :: DimensaoMapa -> Int -> [[Int]]
contadorX5A d i1 = if d>0 then (replicate (d-4) i1) : contadorX5A (d-2) (i1+1) 
                         else []

contadorX4 :: DimensaoMapa -> DimensaoMapa -> [[Int]]  
contadorX4 d s = eliminavazio (contadorX4A d s)

contadorX4A :: DimensaoMapa -> DimensaoMapa -> [[Int]]
contadorX4A d s = if d>0 then (replicate (d-3) (s-2)) : contadorX4A (d-2) (s-1) 
                         else []
-----
contadorX3 :: DimensaoMapa -> Int -> [[Int]]  --i=1
contadorX3 d i1 = ordenacao (contadorX1 d i1) (contadorX2 d i1)

contadorX2 :: DimensaoMapa -> Int -> [[Int]]         --i=1
contadorX2 d i1 = contadorX2AAA (contadorX2AA d i1)

contadorX2AAA :: [[Int]] -> [[Int]]
contadorX2AAA [] = []
contadorX2AAA (h:t) = (reverse h) : (contadorX2AAA t)

contadorX2AA :: DimensaoMapa -> Int -> [[Int]]
contadorX2AA d i1 = eliminavazio (contadorX2A d i1)

contadorX2A :: DimensaoMapa -> Int -> [[Int]]                     
contadorX2A d i1 = if (d>0) then [i1..(d-3)] : (contadorX2A (d-1) (i1+1))
                            else []

contadorX1 :: DimensaoMapa -> Int -> [[Int]]       --i=1
contadorX1 d i1 = eliminavazio (contadorX1A d i1)

contadorX1A :: DimensaoMapa -> Int -> [[Int]]
contadorX1A d i1 = if (d>0) then [i1..(d-2)] : (contadorX1A (d-1) (i1+1))
                            else []

-- X: (1,2,3,4,5,6,7) (7,7,7,7,7,7)
--    (6,5,4,3,2,1) (1,1,1,1,1)
--    (2,3,4,5,6) (6,6,6,6)
--    (5,4,3,2) (2,2,2)
--    (3,4,5) (5,5)
--    (4,3) (3)
--    (4)

---------

{-| A função @contadorY@, dá a lista das coordenadas Y (ou seja, as ordenadas) do mapa espiral pela ordem em que as pedras caiem.

Esta função tem o auxílio das seguintes funções: 
'contadorY1', 'contadorY1A', 'contadorY2','contadorY2A', 'contadorY3', 'contadorY4', 
'contadorY4A', 'contadorY5', 'contadorY5A', 'contadorY5AA', 'contadorY5AAA', 'contadorX6'
'ordenacao' e 'ordenacao2'.
-}

contadorY :: DimensaoMapa -> DimensaoMapa -> Int -> Int -> [Int]
contadorY d s i1 i2 = ordenacao2 (contadorY3 d s i1) (contadorY6 d i2)

ordenacao2 :: [[Int]] -> [[Int]] -> [Int]
ordenacao2 (x:xs) (y:ys) = x ++ y ++ ordenacao2 xs ys
ordenacao2 [] (h:t) = h
ordenacao2 (h:t) [] = h

--------
contadorY6 :: DimensaoMapa -> Int -> [[Int]]
contadorY6 d i2 = ordenacao (contadorY4 d i2) (contadorY5 d i2)
 
contadorY5 :: DimensaoMapa -> Int -> [[Int]]         --i=2
contadorY5 d i2 = contadorY5AAA (contadorY5AA d i2)

contadorY5AAA :: [[Int]] -> [[Int]]
contadorY5AAA [] = []
contadorY5AAA (h:t) = (reverse h) : (contadorY5AAA t)

contadorY5AA :: DimensaoMapa -> Int -> [[Int]]
contadorY5AA d i = eliminavazio (contadorY5A d i)

contadorY5A :: DimensaoMapa -> Int -> [[Int]]                     
contadorY5A d i = if (d>0) then [i..(d-3)] : (contadorY5A (d-1) (i+1))
                           else []

contadorY4 :: DimensaoMapa -> Int -> [[Int]]       --i=2
contadorY4 d i = eliminavazio (contadorY4A d i)

contadorY4A :: DimensaoMapa -> Int -> [[Int]]
contadorY4A d i = if (d>0) then [i..(d-2)] : (contadorY4A (d-1) (i+1))
                           else []
----
contadorY3 :: DimensaoMapa -> DimensaoMapa -> Int -> [[Int]]
contadorY3 d s i1 = ordenacao (contadorY1 d i1) (contadorY2 d s)

ordenacao :: [[Int]] -> [[Int]] -> [[Int]]
ordenacao (x:xs) (y:ys) = [x] ++ [y] ++ ordenacao xs ys
ordenacao l [] = l
ordenacao [] l = l

contadorY2 :: DimensaoMapa -> DimensaoMapa -> [[Int]]
contadorY2 d s = eliminavazio (contadorY2A d s)

contadorY2A :: DimensaoMapa -> DimensaoMapa -> [[Int]]
contadorY2A d s = if (d>0) then (replicate (d-3) (s-2)) : (contadorY2A (d-2) (s-1))
                           else []

contadorY1 :: DimensaoMapa -> Int -> [[Int]]   --i=1
contadorY1 d i = eliminavazio (contadorY1A d i)

contadorY1A :: DimensaoMapa -> Int -> [[Int]]
contadorY1A d i = if d>0 then (replicate (d-2) i) : contadorY1A (d-2) (i+1) 
                         else []

-- Y: 7x1(1,1,1,1,1,1,1) (2,3,4,5,6,7) 
--    6x7(7,7,7,7,7,7) (6,5,4,3,2)
--    5x2(2,2,2,2,2) (3,4,5,6)
--    4x6(6,6,6,6) (5,4,3)
--    3x3(3,3,3) (4,5)
--    2x5(5,5) (4)
--    1x4(4)
                           
{-| A função @eliminavazio@ recebe uma lista de listas de inteiros, e retira dessa lista, as listas vazias.


= Início Passagem de Tempo
-}

eliminavazio :: [[Int]] -> [[Int]]
eliminavazio [] = []
eliminavazio (h:t) = if (h == []) then eliminavazio t
                                  else h: eliminavazio t


-------------------------------------------INICIO PASSAGEM DE TEMPO------------------------------------------------------

{-| A função @chamasBombas@ dá como resultado a lista das coordenadas que as __chamas das bombas__ atingem. 

Tendo em consideração que as chamas não atravessam:

* As /pedras/;
* Os /tijolos/;
* Os /espaços vazios com power up/.
-}
chamasBombas :: Mapa -> [Coordenadas]
chamasBombas m = eliminaRepetidos $ tiraCoordPedras m (chamasBombasAux m (bombasExplodem m) (raiosExplodem m))

{-| A função @chamasBombasAux@ é a principal função auxiliar da função 'chamasBombas'.

Esta função concatena o resultado das 4 primeiras (por vezes únicas, depende do raio de destruição da bomba) direções
(cima, baixo, esquerda e direita) que as chamas percorrem; e tem como passo recursivo, percorrer as seguintes 4 direções.
-}

chamasBombasAux :: Mapa -> [Coordenadas] -> [[Raio]] -> [Coordenadas]
chamasBombasAux _ [] [] = []
chamasBombasAux m ((x,y):cs) (r:rs) = [(x,y)] ++ (eliminaChamasD m (x,y) r) ++ (eliminaChamasU m (x,y) r) ++ 
                      (eliminaChamasL m (x,y) r) ++ (eliminaChamasR m (x,y) r) ++ (chamasBombasAux m cs rs)


{-| A função @tiraCoordPedras@, tem como parâmetros o mapa e a lista das coordenadas que as chamas das bombas atingem, 
e /retira dessa lista as coordenadas que correspondem a __pedras__/.
-}
tiraCoordPedras :: Mapa -> [Coordenadas] -> [Coordenadas]
tiraCoordPedras m [] = []
tiraCoordPedras m ((x,y):zs) = if ((m !! y !! x) == '#') then tiraCoordPedras m zs
                                                         else [(x,y)] ++ tiraCoordPedras m zs

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
eliminaChamasD m (x,y) (r:rs) = if ((down (x,y) (r:rs) m) == '#' || (down (x,y) (r:rs) m) == '?' || (downpormenor (x,y) (r:rs) m == "pu"))
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
eliminaChamasU m (x,y) (r:rs) = if ((up (x,y) (r:rs) m) == '#' || (up (x,y) (r:rs) m) == '?' || (uppormenor (x,y) (r:rs) m == "pu"))
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
eliminaChamasL m (x,y) (r:rs) = if ((left (x,y) (r:rs) m) == '#' || (left (x,y) (r:rs) m) == '?' || (leftpormenor (x,y) (r:rs) m == "pu"))
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
eliminaChamasR m (x,y) (r:rs) = if ((right (x,y) (r:rs) m) == '#' || (right (x,y) (r:rs) m) == '?' || (rightpormenor (x,y) (r:rs) m == "pu"))
                                then [(x+r,y)]
                                else (x+r,y): eliminaChamasR m (x,y) rs

{-| Na função @eliminaRepetidos@, dada uma lista de coordenadas, elimina dessa lista as coordenadas que se encontram /repetidas/.
(Isso acontece quando bombas diferentes explodem e atingem as mesmas células).
-}
eliminaRepetidos :: [Coordenadas] -> [Coordenadas]
eliminaRepetidos [] = []
eliminaRepetidos ((x,y):zs) = if elem (x,y) zs then (x,y):eliminaRepetidos (eliminaCoord (x,y) zs)
                                               else ((x,y):eliminaRepetidos zs)

{-| A função @eliminaCoord@ é a função auxiliar da função 'eliminaRepetidos'.
-}
eliminaCoord :: Coordenadas -> [Coordenadas] -> [Coordenadas]
eliminaCoord _ [] = []
eliminaCoord (x,y) ((a,b):cs) = if ((x,y)==(a,b)) then eliminaCoord (x,y) cs
                                                  else ((a,b):eliminaCoord (x,y) cs)


{-| A função @chamasBombasTOTAIS@ dá como resultado a lista das coordenadas que as __chamas das bombas__ atingem,
sem eliminar as coordenadas @repetidas@ (deste modo, com esta função, conseguimos verficar se bombas diferentes 
atingem as mesmas células simultaneamente). 
-}
chamasBombasTOTAIS :: Mapa -> [Coordenadas]
chamasBombasTOTAIS m = tiraCoordPedras m (chamasBombasAux m (bombasExplodem m) (raiosExplodem m))


{-| A função @coincide@ averigua se as chamas das bombas que explodem ao mesmo tempo atingem __coordenadas em comum__.
-}
coincide :: Mapa -> Bool
coincide m = temRepetidos (chamasBombasTOTAIS m)
  where temRepetidos [] = False 
        temRepetidos (h:t) = if elem h t then True 
                                         else temRepetidos t


----------FUNCAO FEITA PARA DAR RESPOSTA AO PROBLEMA EM QUE VÁRIAS BOMBAS ATINGEM CÉLULAS EM COMUM----------

{-| A função @variasBombasExplodem@ dá como resposta o mapa, quando __várias bombas__ explodem e atingem as __mesmas células__.
-}

variasBombasExplodem :: Mapa -> Mapa
variasBombasExplodem m = eliminaBombasExpl y (bombasExplodem y)
    where y = (doIt m (chamasBombas m))

{-| A função @doIt@ tem como parâmetros:

* O mapa;
* A lista das coordenadas que as bombas que explodem atingem no próximo estado de jogo.

Com o especial recurso às seguintes funções: 'novomapaSemPI', 'novomapaSemJog', 'novomapaSemPU', 
'novomapaSemBombaExpl' e 'novomapaComInst1', esta função "faz explodir" o que está nas coordenadas
que são atingidas pelas coordenadas das bombas.
-}
doIt :: Mapa -> [Coordenadas] -> Mapa
doIt m [] = m
doIt m ((x,y):zs) | ((m !! y !! x) == '?') = doIt (novomapaSemPI m m (x,y) (x,y)) zs
                  | (((m !! y !! x) == ' ') && (elem (x,y) (listajog m))) =  doIt (novomapaSemJog m (x,y)) ((x,y):zs) --pode ter mais jogadores na mesma celula
                  | (((m !! y !! x) == ' ') && (elem (x,y) (listapu m))) = doIt (novomapaSemPU m (x,y)) zs
                  | (((m !! y !! x) == ' ') && (elem (x,y) (listabomb m))) = if (instante1 m (x,y)) then doIt (novomapaSemBombaExpl m (x,y)) zs else doIt (novomapaComInst1 m (x,y)) zs
                  | otherwise = doIt m zs

{-| A função @eliminaBombasExpl@, __elimina do mapa as informações das bombas__ que acabaram de explodir.
-}
eliminaBombasExpl :: Mapa -> [Coordenadas] -> Mapa
eliminaBombasExpl m [] = m
eliminaBombasExpl m ((x,y):zs) = eliminaBombasExpl (novomapaSemBombaExpl m (x,y)) zs

{-| A função @instante1@ averigua, a partir de uma coordenada, /se essa bomba vai explodir ou não/ no próximo instante de tempo.
-}
instante1 :: Mapa -> Coordenadas -> Bool
instante1 m (x,y) = elem (x,y) (bombasExplodem m)

-------------------------------------------BOMBAS A EXPLODIREM-------------------------------------------------------

{-| A função @borala@ dado o mapa do estado atual do jogo, devolve o __novo mapa atualizado__ tendo em conta se:

1. Alguma /bomba/ explode no próximo instante de tempo;
2. Nenhum /bomba/ explode no próximo instante de tempo.
-}

borala :: Mapa -> Mapa
borala m = if (vaiExplodir m) then analisa m --quando alguma bomba explode no próximo instante de tempo
                              else novoMapaSE m  --quando nenhuma bomba explode no próximo instante de tempo

{-| A função @analisa@ dado o mapa do estado atual do jogo, devolve o __novo mapa atualizado__, sabendo que
há bombas a explodirem no próximo instante de tempo, e tem em conta se:

1. Há bombas a explodir em que as mesmas células são atingidas por diferentes bombas;
2. Todas as bombas que explodem não atingem células em comum.
-}

analisa :: Mapa -> Mapa
analisa m = if (coincide m) then novoMapaSE (variasBombasExplodem m) 
                            else novoMapaSE (prossegue m)


{-| A função @prossegue@ percorre o mapa, e faz explodir as bombas. Devolve o novo estado de jogo atualizado.
-}
prossegue :: Mapa -> Mapa
prossegue m = prossegue1 m (raiosExplodem m) (bombasExplodem m)

{-| A função @prossegue1@ é a principal função auxiliar da função 'prossegue'.
-}
prossegue1 :: Mapa -> [[Raio]] -> [Coordenadas] -> Mapa
prossegue1 m (r1:rs) (b1:bs) = prossegue1 (verificaCaminho m r1 r1 r1 r1 b1) rs bs
prossegue1 m [] [] = m 

{-| A função @verificaCaminho@ averigua /se existe algum jogador/ na coordenada da bomba.

* Caso afirmativo, __elimina-o__ do jogo;
* Caso contrário, continua a analisar o __caminho das chamas__.
-}
verificaCaminho :: Mapa -> [Raio] -> [Raio] -> [Raio] -> [Raio] -> Coordenadas -> Mapa
verificaCaminho m rD rU rL rR (x,y) = if ((propriopormenor (x,y) m) == "jogador") then oqueaconteceD (novomapaSemJog m (x,y)) rD rU rL rR (x,y)
                                                                                  else oqueaconteceD m rD rU rL rR (x,y)


{-| A função @oqueaconteceD@ tem como parâmetros:

* O estado atual do jogo;
* Lista do raio da bomba (4x) (para poderem ser explorados por funções diferentes, intactos);
* Coordenadas da bomba em causa (que está a explodir).

Esta função vai auxiliar no __processo de explosão de uma bomba__, no que diz respeito às coordenadas para __baixo__ da mesma.
-}
oqueaconteceD :: Mapa -> [Raio] -> [Raio] -> [Raio] -> [Raio] -> Coordenadas -> Mapa 
oqueaconteceD m (n1:ns) rU rL rR (x,y) 
 | ((downpormenor (x,y) (n1:ns) m) == "pedra") = (oqueaconteceU m [] rU rL rR (x,y)) --acabou down, passa para analisar up
 | ((downpormenor (x,y) (n1:ns) m) == "tijolo") = (oqueaconteceU (novomapaSemPI m m (downc (x,y) (n1:ns)) (downc (x,y) (n1:ns))) [] rU rL rR (x,y))    --acabou down e desaparece '?' mapa
 | ((downpormenor (x,y) (n1:ns) m) == "pu") = (oqueaconteceU (novomapaSemPU m (downc (x,y) (n1:ns))) [] rU rL rR (x,y))    -- acabou down e elimina linha pu
 | ((downpormenor (x,y) (n1:ns) m) == "jogador") = (oqueaconteceD (novomapaSemJog m (downc (x,y) (n1:ns))) (n1:ns) rU rL rR (x,y)) --continua a ver down (e na mesma celula porque pode ter la bombas) e jogador(es) perdeu
 | ((downpormenor (x,y) (n1:ns) m) == "bomb") = (oqueaconteceD (novomapaComInst1 m (downc (x,y) (n1:ns))) ns rU rL rR (x,y))  --continua a ver down  e bombas instantes 1
 | ((downpormenor (x,y) (n1:ns) m) == "nada") = (oqueaconteceD m ns rU rL rR (x,y))      --continua a ver down simplesmente
 | otherwise = oqueaconteceD m ns rU rL rR (x,y)
oqueaconteceD m [] rU rL rR (x,y) = oqueaconteceU m [] rU rL rR (x,y)                                 


{-| A função @oqueaconteceU@ tem como parâmetros:

* O estado atual do jogo;
* Lista do raio da bomba (4x) (para poderem ser explodrados por funções diferentes, intactos);
* Coordenadas da bomba em causa (que está a explodir).

Esta função vai auxiliar no __processo de explosão de uma bomba__, no que diz respeito às coordenadas para __cima__ da mesma.
-}
oqueaconteceU :: Mapa -> [Raio] -> [Raio] -> [Raio] -> [Raio] -> Coordenadas -> Mapa 
oqueaconteceU m [] (n1:ns) rL rR (x,y) 
 | ((uppormenor (x,y) (n1:ns) m) == "pedra") = (oqueaconteceL m [] [] rL rR (x,y)) --acabou up , passa para analisar UP
 | ((uppormenor (x,y) (n1:ns) m) == "tijolo") = (oqueaconteceL (novomapaSemPI m m (upc (x,y) (n1:ns)) (upc (x,y) (n1:ns))) [] [] rL rR (x,y))    --acabou up e desaparece '?' mapa
 | ((uppormenor (x,y) (n1:ns) m) == "pu") = (oqueaconteceL (novomapaSemPU m (upc (x,y) (n1:ns))) [] [] rL rR (x,y))    -- acabou up e elimina linha pu
 | ((uppormenor (x,y) (n1:ns) m) == "jogador") = (oqueaconteceU (novomapaSemJog m (upc (x,y) (n1:ns))) [] (n1:ns) rL rR (x,y)) --continua a ver up (e na mesma celula porque pode ter la bombas) e jogador(es) perdeu
 | ((uppormenor (x,y) (n1:ns) m) == "bomb") = (oqueaconteceU (novomapaComInst1 m (upc (x,y) (n1:ns))) [] ns rL rR (x,y))  --continua a ver up e bombas instantes 1
 | ((uppormenor (x,y) (n1:ns) m) == "nada") = (oqueaconteceU m [] ns rL rR (x,y))      --continua a ver up simplesmente
 | otherwise = oqueaconteceU m [] ns rL rR (x,y)
oqueaconteceU m [] [] rL rR (x,y) = oqueaconteceL m [] [] rL rR (x,y)                                 

{-| A função @oqueaconteceL@ tem como parâmetros:

* O estado atual do jogo;
* Lista do raio da bomba (4x) (para poderem ser explodrados por funções diferentes, intactos);
* Coordenadas da bomba em causa (que está a explodir).

Esta função vai auxiliar no __processo de explosão de uma bomba__, no que diz respeito às coordenadas para a __esquerda__ da mesma.
-}
oqueaconteceL :: Mapa -> [Raio] -> [Raio] -> [Raio] -> [Raio] -> Coordenadas -> Mapa
oqueaconteceL m [] [] (n1:ns) rR (x,y) 
 | ((leftpormenor (x,y) (n1:ns) m) == "pedra") = (oqueaconteceR m [] [] [] rR (x,y)) --acabou left , passa para analisar UP
 | ((leftpormenor (x,y) (n1:ns) m) == "tijolo") = (oqueaconteceR (novomapaSemPI m m (leftc (x,y) (n1:ns)) (leftc (x,y) (n1:ns))) [] [] [] rR (x,y))    --acabou left e desaparece '?' mapa
 | ((leftpormenor (x,y) (n1:ns) m) == "pu") = (oqueaconteceR (novomapaSemPU m (leftc (x,y) (n1:ns))) [] [] [] rR (x,y))    -- acabou left e elimina linha pu
 | ((leftpormenor (x,y) (n1:ns) m) == "jogador") = (oqueaconteceL (novomapaSemJog m (leftc (x,y) (n1:ns))) [] [] (n1:ns) rR (x,y)) --continua a ver left (e na mesma celula porque pode ter la bombas) e jogador(es) perdeu
 | ((leftpormenor (x,y) (n1:ns) m) == "bomb") = (oqueaconteceL (novomapaComInst1 m (leftc (x,y) (n1:ns))) [] [] ns rR (x,y))  --continua a ver left e bombas instantes 1
 | ((leftpormenor (x,y) (n1:ns) m) == "nada") = (oqueaconteceL m [] [] ns rR (x,y))      --continua a ver left simplesmente
 | otherwise = oqueaconteceL m [] [] ns rR (x,y)
oqueaconteceL m [] [] [] rR (x,y) = oqueaconteceR m [] [] [] rR (x,y)                                 

{-| A função @oqueaconteceR@ tem como parâmetros:

* O estado atual do jogo;
* Lista do raio da bomba (4x) (para poderem ser explodrados por funções diferentes, intactos);
* Coordenadas da bomba em causa (que está a explodir).

Esta função vai auxiliar no __processo de explosão de uma bomba__, no que diz respeito às coordenadas para a __direita__ da mesma.
-}
oqueaconteceR :: Mapa -> [Raio] -> [Raio] -> [Raio] -> [Raio] -> Coordenadas -> Mapa
oqueaconteceR m [] [] [] (n1:ns) (x,y) 
 | ((rightpormenor (x,y) (n1:ns) m) == "pedra") = novomapaSemBombaExpl m (x,y)
 | ((rightpormenor (x,y) (n1:ns) m) == "tijolo") = novomapaSemBombaExpl (novomapaSemPI m m (rightc (x,y) (n1:ns)) (rightc (x,y) (n1:ns))) (x,y)
 | ((rightpormenor (x,y) (n1:ns) m) == "pu") = novomapaSemBombaExpl (novomapaSemPU m (rightc (x,y) (n1:ns))) (x,y) 
 | ((rightpormenor (x,y) (n1:ns) m) == "jogador") = (oqueaconteceR (novomapaSemJog m (rightc (x,y) (n1:ns))) [] [] [] (n1:ns) (x,y)) --continua a ver righ (e na mesma celula porque pode ter la bombas) e jogador(es) perdeu
 | ((rightpormenor (x,y) (n1:ns) m) == "bomb") = (oqueaconteceR (novomapaComInst1 m (rightc (x,y) (n1:ns))) [] [] [] ns (x,y))  --continua a ver right e bombas instantes 1
 | ((rightpormenor (x,y) (n1:ns) m) == "nada") = (oqueaconteceR m [] [] [] ns (x,y))      --continua a ver right simplesmente
 | otherwise = oqueaconteceR m [] [] [] ns (x,y)
oqueaconteceR m [] [] [] [] (x,y) = (novomapaSemBombaExpl m (x,y))


{-| A função @propriopormenor@ vê com mais pormenor, o que está na própria coordenada. 

Nesta função apenas avaliamos se essa coordenada tem algum jogador ou não.
-}

propriopormenor :: Coordenadas -> Mapa -> String
propriopormenor (x,y) m | ((m !! y !! x) == ' ') = espacoVazioPP (x,y) m
                        | otherwise = "naointeressa"

-- | A função @espacoVazioPP@ é função auxiliar da função 'propriopormenor'.
espacoVazioPP :: Coordenadas -> Mapa -> String
espacoVazioPP (x,y) m | elem (x,y) (listajog m) = "jogador"
                      | otherwise = "naointeressa"

{-| A função @downpormenor@ vê com mais pormenor, o que está /x/ posições /abaixo/ de uma determinada coordenada. 

Respostas possíveis:

* Pedra;
* Tijolo;
* Power up;
* Bomb;
* Jogador;
* Nada.
-}

downpormenor :: Coordenadas -> [Raio] -> Mapa -> String
downpormenor (x,y) rs m | ((down (x,y) rs m) == '#') = "pedra"
                        | ((down (x,y) rs m) == '?') = "tijolo"
                        | ((down (x,y) rs m) == ' ') = espacoVazioD (x,y) rs m

-- | A função @espacoVazioD@ é a principal função auxiliar da função 'downpormenor'.
espacoVazioD :: Coordenadas -> [Raio] -> Mapa -> String
espacoVazioD (x,y) rs m | (elem (downc (x,y) rs) (listapu m)) = "pu"
                        | (elem (downc (x,y) rs) (listabomb m)) = "bomb"
                        | (elem (downc (x,y) rs) (listajog m)) = "jogador"
                        | otherwise = "nada"
 
{-| A função @uppormenor@ vê com mais pormenor, o que está /x/ posições /acima/ de uma determinada coordenada. 

Respostas possíveis:

* Pedra;
* Tijolo;
* Power up;
* Bomb;
* Jogador;
* Nada.
-}

uppormenor :: Coordenadas -> [Raio] -> Mapa -> String
uppormenor (x,y) rs m | ((up (x,y) rs m) == '#') = "pedra"
                      | ((up (x,y) rs m) == '?') = "tijolo"
                      | ((up (x,y) rs m) == ' ') = espacoVazioU (x,y) rs m

-- | A função @espacoVazioU@ é a principal função auxiliar da função 'uppormenor'.
espacoVazioU :: Coordenadas -> [Raio] -> Mapa -> String
espacoVazioU (x,y) rs m | (elem (upc (x,y) rs) (listapu m)) = "pu"
                        | (elem (upc (x,y) rs) (listabomb m)) = "bomb"
                        | (elem (upc (x,y) rs) (listajog m)) = "jogador"
                        | otherwise = "nada"

{-| A função @leftpormenor@ vê com mais pormenor, o que está /x/ posições /à esquerda/  de uma determinada coordenada. 

Respostas possíveis:

* Pedra;
* Tijolo;
* Power up;
* Bomb;
* Jogador;
* Nada.
-}

leftpormenor :: Coordenadas -> [Raio] -> Mapa -> String
leftpormenor (x,y) rs m | ((left (x,y) rs m) == '#') = "pedra"
                        | ((left (x,y) rs m) == '?') = "tijolo"
                        | ((left (x,y) rs m) == ' ') = espacoVazioL (x,y) rs m

-- | A função @espacoVazioL@ é a principal função auxiliar da função 'leftpormenor'.
espacoVazioL :: Coordenadas -> [Raio] -> Mapa -> String
espacoVazioL (x,y) rs m | (elem (leftc (x,y) rs) (listapu m)) = "pu"
                        | (elem (leftc (x,y) rs) (listabomb m)) = "bomb"
                        | (elem (leftc (x,y) rs) (listajog m)) = "jogador"
                        | otherwise = "nada"

{-| A função @rightpormenor@ vê com mais pormenor, o que está /x/ posições / à direita/ de uma determinada coordenada. 

Respostas possíveis:

* Pedra;
* Tijolo;
* Power up;
* Bomb;
* Jogador;
* Nada.
-}

rightpormenor :: Coordenadas -> [Raio] -> Mapa -> String
rightpormenor (x,y) rs m | ((right (x,y) rs m) == '#') = "pedra"
                         | ((right (x,y) rs m) == '?') = "tijolo"
                         | ((right (x,y) rs m) == ' ') = espacoVazioR (x,y) rs m

-- | A função @espacoVazioR@ é a principal função auxiliar da função 'rightpormenor'.
espacoVazioR :: Coordenadas -> [Raio] -> Mapa -> String
espacoVazioR (x,y) rs m | (elem (rightc (x,y) rs) (listapu m)) = "pu"
                        | (elem (rightc (x,y) rs) (listabomb m)) = "bomb"
                        | (elem (rightc (x,y) rs) (listajog m)) = "jogador"
                        | otherwise = "nada"

-----

-- | A função @downc@ diz qual é a primeira coordenada do __"fogo"__ da bomba (para baixo do bomba).
downc :: Coordenadas -> [Raio] -> Coordenadas
downc (x,y) (n1:ns) = (x,(y+n1))

-- | A função @upc@ diz qual é a primeira coordenada do __"fogo"__ da bomba (para cima do bomba).
upc :: Coordenadas -> [Raio] -> Coordenadas
upc (x,y) (n1:ns) = (x,(y-n1))

-- | A função @leftc@ diz qual é a primeira coordenada do __"fogo"__ da bomba (para a esquerda do bomba).
leftc :: Coordenadas -> [Raio] -> Coordenadas
leftc (x,y) (n1:ns) = ((x-n1),y)

-- | A função @rightc@ diz qual é a primeira coordenada do __"fogo"__ da bomba (para a direita do bomba).
rightc :: Coordenadas -> [Raio] -> Coordenadas
rightc (x,y) (n1:ns) = ((x+n1),y)

------

-- | A função @down@ diz o que está /abaixo/ da bomba que vai explodir ('#',' ' ou '?').
down :: Coordenadas -> [Raio] -> Mapa -> Char --ve o que está abaixo da bomba
down (x,y) (n1:ns) m = m !! (y+n1) !! x

-- | A função @up@ diz o que está /acima/ da bomba que vai explodir ('#',' ' ou '?').
up :: Coordenadas -> [Raio] -> Mapa -> Char 
up (x,y) (n1:ns) m = (m !! (y-n1) !! x)

-- | A função @left@ diz o que está /à esquerda/ da bomba que vai explodir ('#',' ' ou '?').
left :: Coordenadas -> [Raio] -> Mapa -> Char 
left (x,y) (n1:ns) m = (m !! y !! (x-n1))

-- | A função @right@ diz o que está /à direita/ da bomba que vai explodir ('#',' ' ou '?').
right :: Coordenadas -> [Raio] -> Mapa -> Char
right (x,y) (n1:ns) m = (m !! y !! (x+n1))

------            

-- | A função @bombasExplodem@ diz as __coordenadas das bombas__ que vão __explodir__.
bombasExplodem :: Mapa -> [Coordenadas]
bombasExplodem m = bombasExplodemAux (listabomb m) (quaisExplodem m)

-- | A função @bombasExplodemAux@ é a principal função auxiliar da função 'bombasExplodem'.
bombasExplodemAux :: [Coordenadas] -> [Int] -> [Coordenadas]
bombasExplodemAux _ [] = []
bombasExplodemAux bombs (p1:ps) = ((!-!) bombs p1) : (bombasExplodemAux bombs ps)


{-| A função @raiosExplodem@ diz as listas dos raios das bombas que vão explodir.

==Exemplo de utilização:
>>>raiosExplodem m = [[1,2],[1,2,3]]

(m= ["#########","#       #","# #?#?# #","#  ?  ? #","#?# # #?#","# ?  ?  #","# #?# # #","#  ??   #","#########","* 4 5 0 2 1","* 5 6 1 3 1","* 7 6 0 1 8","0 5 4","1 6 5"])
-}

raiosExplodem :: Mapa -> [[Raio]]                   
raiosExplodem m = raiosExplodemAux (listasraios m) (quaisExplodem m)

-- | A função @raiosExplodemAux@ é a principal função auxiliar da função 'raiosExplodem'.
raiosExplodemAux :: [[Raio]] -> [Int] -> [[Raio]]  
raiosExplodemAux _ [] = []
raiosExplodemAux raios (p1:ps) = ((!-!) raios p1) : (raiosExplodemAux raios ps)

-- | A função @(!-!)@ é uma adaptação da função __!!__, já existente na Prelude do Haskell.
(!-!) :: [a] -> Int -> a
(!-!) (h:t) 1 = h
(!-!) (h:t) n = (!-!) t (n-1)

-- | A função @quaisExplodem@ dá a posição das bombas que vão explodir (relativamente a info nova das bombas).
quaisExplodem :: Mapa -> [Int]
quaisExplodem m = quaisExplodem1 (novaInfoBombas m)

-- | A função @quaisExplodem1@ é a principal função auxiliar da função 'quaisExplodem'.
quaisExplodem1 :: Mapa -> [Int]
quaisExplodem1 lista = quaisExplodemAux 1 lista where
    quaisExplodemAux _ [] = []
    quaisExplodemAux i (x:xs) = if ((last x) == '0')
                                then i : (quaisExplodemAux (i + 1) xs)
                                else quaisExplodemAux (i + 1) xs

{-| A função @listasraios@ diz as listas dos raios de todas bombas presentes no mapa.

==Exemplo de utilização:
>>>listasraios m = [[1,2],[1,2,3],[1]]

(m= ["#########","#       #","# #?#?# #","#  ?  ? #","#?# # #?#","# ?  ?  #","# #?# # #","#  ??   #","#########","* 4 5 0 2 1","* 5 6 1 3 1","* 7 6 0 1 8","0 5 4","1 6 5"])
-}
listasraios :: Mapa -> [[Raio]]
listasraios m = listasraiosAux (raioBombaInt m)

-- | A função @listasraiosAux@ é a principal função auxiliar da função 'listasraios'.
listasraiosAux :: [Raio] -> [[Raio]]
listasraiosAux [] = []
listasraiosAux (h:t) = ((enumFromTo 1 h) : (listasraiosAux t))

-- | A função @raioBombaInt@ dá o raio de cada bomba plantada em [Int].
raioBombaInt :: Mapa -> [Raio]
raioBombaInt m = string2Int (raioBomba m)

-- | A função @raioBomba@ dá o raio de cada bomba plantada em [String].
raioBomba :: Mapa -> [String]
raioBomba m = raioBombaAux (words1 m)

raioBombaAux :: [[String]] -> [String]
raioBombaAux [] = []
raioBombaAux (h:t) = last (init h) : raioBombaAux t



---------Retira 1 instante de tempo a todas as bombas presentes---------------- (acaba sempre por ser necessaria)

{-| A função @novoMapaSE@ __retira 1 instante de tempo a todas as bombas presentes__ no mapa (após as que tinham que explodir
efetivamente explodirem).
-}
novoMapaSE :: Mapa -> Mapa
novoMapaSE m = colocaLinhas (eliminaLinhas m) (novaInfoBombas m)

-- | A função @colocaLinhas@ coloca as linhas com as /informações novas/ das bombas (nomeadamente os instantes de tempo), no mapa.
colocaLinhas :: Mapa -> [String] -> Mapa
colocaLinhas (h:t) bomb = if ((head h == '0') || (head h == '1') || (head h == '2') || (head h == '3'))
                          then bomb ++ (h:t)
                          else h: colocaLinhas t bomb

colocaLinhas [] bomb = bomb

-- | A função @eliminaLinhas@ elimina do mapa as linhas relativas às informações /(antigas)/ das bombas.
eliminaLinhas :: Mapa -> Mapa
eliminaLinhas [] = []
eliminaLinhas (h:t) = if (head h == '*') then eliminaLinhas t
                                         else h:eliminaLinhas t

-- | A função @novaInfoBombas@ põe a info /(nova)/ das bombas no formato desejado ("unwords").
novaInfoBombas :: Mapa -> [String]
novaInfoBombas m = unwords2 (newTime m)

unwords2 :: [[String]] -> [String]
unwords2 [] = []
unwords2 (h:t) = unwords h : (unwords2 t)

-- | A função @newTime@ substitui __instantes de tempo__ /antigos/ pelos /novos/.
newTime :: Mapa -> [[String]]
newTime m = newTimeAux (words1 m) (novoIString m)

-- | A função @newTimeAux@ é a principal função auxiliar da função 'nemTime'.
newTimeAux :: [[String]] -> [String] -> [[String]]
newTimeAux [] [] = []
newTimeAux (x:xs) (y:ys) = substituiu x y : (newTimeAux xs ys)

substituiu :: [String] -> String -> [String]
substituiu [a,b,c,d,e,f] x = [a,b,c,d,e,x]

-- | A função @words1@ põe a info (antiga) das bombas em formato "words".
words1 :: Mapa -> [[String]]
words1 m = words2 (infoBombas m)

words2 :: [String] -> [[String]]
words2 [] = []
words2 (h:t) = (words h) : words2 t

----------------------------------------------------

-- | A função @vaiExplodir@ averigua /se/ alguma das bombas plantadas vai __explodir__ no __novo instante de tempo__.
vaiExplodir :: Mapa -> Bool
vaiExplodir m = vaiExplodirAux (novoInst m)

-- | A função @vaiExplodirAux@ é a principal função auxiliar da função 'vaiExplodir'.
vaiExplodirAux :: [Int] -> Bool
vaiExplodirAux [] = False
vaiExplodirAux (h:t) = if (h == 0) then True
                                   else vaiExplodirAux t

-- | A função @novoIString@ indica /quantos instantes/ faltam para as bombas explodirem em __formato String__.
novoIString :: Mapa -> [String]
novoIString m = novoIStringAux (novoInst m)

-- | A função @novoIStringAux@ é a principal função auxiliar da função 'novoIString'.
novoIStringAux :: [Int] -> [String]
novoIStringAux [] = []
novoIStringAux (h:t) = show h : novoIStringAux t

-- | A função @novoInst@ indica __quantos instantes__ faltam para as bombas explodirem no /novo/ instante de tempo.
novoInst :: Mapa -> [Int]
novoInst m = novoInstAux (qtsInstantes m)

-- | A função @novoInstAux@ é a principal função auxiliar da função 'novoInst'.
novoInstAux :: [Int] -> [Int]
novoInstAux [] = []
novoInstAux (h:t) = (h-1) : novoInstAux t

-- | A função @qtsInstantes@ indica __quantos instantes__ faltam para as bombas explodirem em formato [Int].
qtsInstantes :: Mapa -> [Int]
qtsInstantes m = string2Int (qtsInstantes1 m)

string2Int :: [String] -> [Int]
string2Int [] = []
string2Int (h:t) = (read h :: Int) : string2Int t

-- | A função @qtsInstantes1@ indica __quantos instantes__ faltam para as bombas explodirem em formato [String].
qtsInstantes1 :: Mapa -> [String]
qtsInstantes1 m = qtsInstantes1Aux (infoBombas m)

qtsInstantes1Aux :: [String] -> [String]
qtsInstantes1Aux [] = []
qtsInstantes1Aux (h:t) = last (words h) : qtsInstantes1Aux t

-- | A função @infoBombas@ __pega__ nas linhas das informações das /bombas/.
infoBombas :: Mapa -> [String]
infoBombas [] = []
infoBombas (h:t) = if (head h == '*') then h : infoBombas t
                                      else infoBombas t

------------

-- | A função @lista pu@ dá como resposta as coordenadas dos power ups presentes no mapa.
listapu :: Mapa -> [Coordenadas]
listapu m =(coordenadasPU1 (coordenadasPU2 m))

coordenadasPU1 :: [String] -> [Coordenadas]
coordenadasPU1 [] = []
coordenadasPU1 (h:t) = coord h : coordenadasPU1 t 

coordenadasPU2 :: [String] -> [String]
coordenadasPU2 m = tirarPUeCV (powerUp m)

tirarPUeCV :: [String] -> [String]
tirarPUeCV [] = []
tirarPUeCV (h:t) = (auxTirar h : tirarPUeCV t) 

auxTirar :: String -> String
auxTirar [] = []
auxTirar (pu:v:t) = t

powerUp :: [String] -> [String]
powerUp [] = []
powerUp (h:t) = if (head h == '+') || (head h == '!') then (h:powerUp t)
                                                      else powerUp t

-- | A função @coord@ devolve as coordenadas no tipo (Int,Int).
coord :: String -> Coordenadas
coord w = (read (coord1 w) :: Int, read (coord2 w) :: Int)

coord1 :: String -> String
coord1 (h:t) = if h /= ' ' then h : coord1 t
                           else []
coord2 :: String -> String
coord2 l = drop (1 + length (coord1 l)) l


-- | A função @listajog@ dá como resultado a __lista das coordenadas dos jogadores no mapa__.
listajog :: Mapa -> [Coordenadas]
listajog m = toInt (listajog1 m)

toInt :: [[String]] -> [Coordenadas]
toInt [] = []
toInt (h:t) = toInt1 h : toInt t

toInt1 :: [String] -> Coordenadas
toInt1 (x:y:vazio) = ((read x) :: Int, (read y) :: Int)

listajog1 :: Mapa -> [[String]]
listajog1 m = listajog2 (tail1 m)

listajog2 :: [[String]] -> [[String]]
listajog2 [] = []
listajog2 (h:t) = listajog3 h : listajog2 t

listajog3 :: [String] -> [String]
listajog3 (x:y:zs) = [x] ++ [y]

tail1 :: Mapa -> [[String]]
tail1 m = tail2 (words3 m)

tail2 :: [[String]] -> [[String]]
tail2 [] = []
tail2 (h:t) = tail h : tail2 t

words3 :: Mapa -> [[String]]
words3 m = words2 (linhaplayers m)

-- | A função @linhaplayers@ dá como resultado as linhas alusivas às /informaçoes dos jogadores/.
linhaplayers :: Mapa -> [String]
linhaplayers [] = []
linhaplayers (h:t) = if (head h == '0' || head h == '1' || head h == '2' || head h == '3')
                     then h : linhaplayers t
                     else linhaplayers t


-- | A função @listabomb@ dá como resultado a __lista das coordenadas das bombas plantadas__ no mapa.
listabomb :: Mapa -> [Coordenadas]
listabomb m = toInt (listabomb1 m)

listabomb1 :: Mapa -> [[String]]
listabomb1 m = listajog2 (tail3 m)

tail3 :: Mapa -> [[String]]
tail3 m = tail2 (words4 m)

words4 :: Mapa -> [[String]]
words4 m = words2 (infoBombas m)


--------------------------------------------MAPAS DE RESPOSTA AO PROBLEMAS-------------
--1.
-- | A função @novomapaSemBombaExpl@ dá como resultado o mapa /sem/ a linha relativa à __bomba__ explodida.
novomapaSemBombaExpl :: Mapa -> Coordenadas -> Mapa
novomapaSemBombaExpl m (x,y) = novomapaSemBombaExplAux m (eliminalinhasbomb m) (x,y)

novomapaSemBombaExplAux :: Mapa -> Mapa -> Coordenadas -> Mapa
novomapaSemBombaExplAux m (h:t) (x,y) = if ((head h == '0') || (head h == '1') || (head h == '2') || (head h == '3'))
                                       then (bombexistentes m (x,y)) ++ [h] ++ t
                                       else h: novomapaSemBombaExplAux m t (x,y)
novomapaSemBombaExplAux m [] (x,y) = (bombexistentes m (x,y))

-- | A função @bombexistentes@ dá como resultado as __info das bombas__ que se mantêm no mapa (ainda não explodiram).
bombexistentes :: Mapa -> Coordenadas -> [String]
bombexistentes m (x,y) = bombexistentes1 (infoBombas m) (qualbomb m (x,y)) 

bombexistentes1 :: [String] -> Int -> [String]
bombexistentes1 (h:t) x = if (x==1) then t
                        else (h: (bombexistentes1 t (x-1)))
bombexistentes1 _ _ = []                        

-- | A função @qualbomb@, relativamente às infos das bombas, diz qual é a posição da info que precisa de ser apagada.
qualbomb :: Mapa -> Coordenadas -> Int    
qualbomb m (x,y) = head ((my_elemIndices (x,y)) (listabomb m))

-- | A função @eliminalinhasbomb@ elimina __todas__ as linhas das info das /bombas/ colocadas.
eliminalinhasbomb :: Mapa -> Mapa
eliminalinhasbomb [] = []
eliminalinhasbomb (h:t) = if (head h == '*') 
                         then eliminalinhasbomb t
                         else h: eliminalinhasbomb t


--2.
--Desaparecer '?' do mapa

-- | A função @novomapaSemPI@ dá como resultado o mapa /sem/ o tijolo que foi atingido pelas chamas.
novomapaSemPI :: Mapa -> Mapa -> Coordenadas -> Coordenadas -> Mapa  -- este (x,y) é novas coord (downc (x,y) raios) é o argumento (ou leftc, etc...)
novomapaSemPI (h:t) m (x,y) (a,b) = if (y == 0) then [novaLinha m (a,b)] ++ t   --passa argumentos duplicados pq os outros são fixos 
                                                else [h] ++ novomapaSemPI t m (x,y-1) (a,b) 

-- | A função @novaLinha@ dá como resultado a nova linha atualizada (__sem o '?'__).
novaLinha :: Mapa -> Coordenadas -> String
novaLinha m (x,y) = tiraPI (pegaLinha m (x,y)) (x,y)

-- | A função @tiraPI@ substitui o __'?'__ por __' '__.
tiraPI :: String -> Coordenadas -> String
tiraPI (h:t) (x,y) = if (x == 0) then " " ++ t
                                 else [h] ++ tiraPI t ((x-1),y)

-- | A função @pegaLinha@ pega na linha onde está o __'?'__ que procuramos.
pegaLinha :: Mapa -> Coordenadas -> String
pegaLinha m (x,y) = m !! y     --este (x,y) é a nova posicao nova da chama (é a posicao onde esta o '?')


--3.
--Linha com info da Bomba atingida fica a 1 instante (-> novo m)

{-| A função @novomapaComInst1@ dá como resultado o mapa com a bomba 
que foi atingida por uma bomba que explodiu, com os __instantes de tempo devidamente alterados__
(neste caso, com instantes = 2, porque a função 'novoMapaSE' vai retirar mais 1 instante de tempo a cada bomba).
-}
novomapaComInst1 :: Mapa -> Coordenadas -> Mapa  
novomapaComInst1 m (x,y) = novomapaComInst1Aux m (eliminalinhasbomb m) (x,y)

novomapaComInst1Aux :: Mapa -> Mapa -> Coordenadas -> Mapa
novomapaComInst1Aux m (h:t) (x,y) = if ((head h == '0') || (head h == '1') || (head h == '2') || (head h == '3'))
                                  then (alterainstb m (x,y)) ++ [h] ++ t
                                  else h: novomapaComInst1Aux m t (x,y)
novomapaComInst1Aux m [] (x,y) = alterainstb m (x,y)                                  

-- | A função @alterainstb@ __altera__ informação da bomba atingida pela chama (instante fica = 2).
alterainstb :: Mapa -> Coordenadas -> [String]
alterainstb m (x,y) = alterainstb1 (infoBombas m) (qualbombating m (x,y))

alterainstb1 :: Mapa -> Int -> [String]
alterainstb1 (h:t) x = if (x==1) then [(alterainstante h)] ++ t
                                 else (h: (alterainstb1 t (x-1)))

alterainstante :: String -> String
alterainstante b = unwords (alterainstante1 b)

alterainstante1 :: String -> [String]
alterainstante1 b = alterainstanteAux b ++ ["2"] -- mais tarde vai ser subtraído 1

alterainstanteAux :: String -> [String]
alterainstanteAux b = init (words b)

-- | A função @qualbombating@, relativamente as infos das bombas, diz qual é a /posição da info/ que precisa de mudar instante para 2.
qualbombating :: Mapa -> Coordenadas -> Int    
qualbombating m (x,y) = head (my_elemIndices (x,y) (listabomb m)) 


--4.

-- | A função @novomapaSemPU@ elimina a linha do power up que foi atingido pela chama de uma bomba.
novomapaSemPU :: Mapa -> Coordenadas -> Mapa  
novomapaSemPU m (x,y) = novomapaSemPUAux m (eliminalinhaspu m) (x,y) -- este (x,y) é novas coord (downc (x,y) raios) é o argumento (ou leftc, etc...)

novomapaSemPUAux :: Mapa -> Mapa -> Coordenadas -> Mapa
novomapaSemPUAux m (h:t) (x,y) = if (head h == '*' || head h == '0' || head h == '1' || head h == '2' || head h == '3') 
                               then (puexistentes m (x,y)) ++ [h] ++ t
                               else h: novomapaSemPUAux m t (x,y)
novomapaSemPUAux m [] (x,y) = puexistentes m (x,y)

-- | A função @alterainstb@ devolve /somente/ a informação das linhas do __power ups__ que se mantêm no mapa.
puexistentes :: Mapa -> Coordenadas -> [String]
puexistentes m (x,y) = puexistentes1 (infoPU m) (qualpu1 m (x,y)) 

puexistentes1 :: [String] -> Int -> [String]
puexistentes1 (h:t) x = if (x==1) then t
                                  else (h: (puexistentes1 t (x-1)))

-- | A função @qualpu1@, relativamente as infos dos power ups, diz qual é a __posicao da info__ que precisa de ser apagada.
qualpu1 :: [String] -> Coordenadas -> Int    
qualpu1 m (x,y) = head (my_elemIndices (x,y) (listapu m))   

-- | A função @infoPU@ devolve as informações dos jogadores que se encontram em jogo.
infoPU :: Mapa -> [String]
infoPU [] = []
infoPU (h:t) = if ((head h == '+') || (head h == '!'))
                  then h : infoPU t
                  else infoPU t

-- | A função @eliminalinhaspu@ elimina do mapa as linhas relativas às informações dos power ups.
eliminalinhaspu :: Mapa -> Mapa
eliminalinhaspu [] = []
eliminalinhaspu (h:t) = if ((head h == '+') || (head h == '!'))
                        then eliminalinhaspu t
                        else h : eliminalinhaspu t

--5.

-- | A função @novomapaSemJog@ elimina do mapa as __linhas relativas aos jogadores que perderam__.
novomapaSemJog :: Mapa -> Coordenadas -> Mapa
novomapaSemJog m (x,y) = (eliminalinhasjog m) ++ (jogvivos m (x,y))   -- este (x,y) é novas coord (downc (x,y) raios) é o argumento (ou leftc, etc...)

-- | A função @jogvivos@ devolve as __linhas dos jogadores__ que estão vivos.
jogvivos :: Mapa -> Coordenadas -> [String]
jogvivos m (x,y) = jogvivos1 (infoJogadores m) (qualjogador1 m (x,y))

jogvivos1 :: Mapa -> [Int] -> [String]
jogvivos1 l [] = l
jogvivos1 (h:t) (x:xs) = if (x==1) then jogvivos1 t (funcaoquesubtrai1 xs)
                                   else h: (jogvivos1 t (funcaoquesubtrai1 (x:xs)))
--jogvivos1 [] [] = []

funcaoquesubtrai1 :: [Int] -> [Int]
funcaoquesubtrai1 [] = []
funcaoquesubtrai1 (h:t) = (h-1) : funcaoquesubtrai1 t

-- | A função @qualjogador1@, relativamente as infos dos jogadores, diz qual é a __posição da info__ que precisa de ser apagada.
qualjogador1 :: Mapa -> Coordenadas -> [Int]   
qualjogador1 m (x,y) =(my_elemIndices (x,y) (listajog m))   

my_elemIndices :: Eq a => a -> [a] -> [Int]
my_elemIndices elem lista = elemIndicesAux 1 elem lista where
    elemIndicesAux _ _ [] = []
    elemIndicesAux i y (x:xs) = if (x == y) then i : (elemIndicesAux (i + 1) y xs) else elemIndicesAux (i + 1) y xs

-- | A função @infoJogadores@ devolve as __informações dos jogadores__ no mapa.
infoJogadores :: Mapa -> [String]
infoJogadores [] = []
infoJogadores (h:t) = if ((head h == '0') || (head h == '1') || (head h == '2') || (head h == '3'))
                  then h : infoJogadores t
                  else infoJogadores t


-- | A função @eliminalinhasjog@ devolve o mapa /sem/ as linhas relativas às __informações dos jogadores__.
eliminalinhasjog :: Mapa -> Mapa
eliminalinhasjog [] = []
eliminalinhasjog (h:t) = if ((head h == '0') || (head h == '1') || (head h == '2') || (head h == '3'))
                  then eliminalinhasjog t
                  else h: eliminalinhasjog t

-----------------------------------------------