module Main where
import System.Environment
import Text.Read
import Data.Maybe
import System.Random
import Data.Char

{-|
Module : Main

Description : Módulo Haskell que contém um exemplo de possível criação de um mapa de jogo

Um módulo contendo funções recursivas e não recursivas com o intuito de criar um mapa consoante uma dimensão e semente
-}

-- | 'main' : função de teste para a função __mapa__
main :: IO ()
main = do a <- getArgs
          let s = readMaybe (a !! 0)
          let l = readMaybe (a !! 1)
          if length a == 2 && isJust s && isJust l && fromJust s >= 5 && odd (fromJust s)
             then putStr $ unlines $ mapa (fromJust s) (fromJust l)
             else putStrLn "Parâmetros inválidos"

-- | 'mapa' : função que dada uma dimensão e semente devolve uma lista do mapa
mapa :: Int -> Int -> [String]
mapa d s = agrupa d (substituir (contador d) d (pedras d (contador d)) (celulaVazia d (contador d)) (porResultadoGama (gerador d s))) ++ cbomb d s ++ cflame d s

{-|
==Exemplos de utilização:
>>>mapa 9 0 ["#########","#       #","# #?#?# #","#  ?  ? #","#?# # #?#","# ?  ?  #","# #?#?# #","#  ??   #","#########","+ 5 2","+ 3 3","! 5 5"]

>>>mapa 5 0 ["#####","#   #","# # #","#   #","#####"]
-}

-- | 'cbomb' : função que __devolve as coordenadas de power ups do tipo bomb__
cbomb :: Int -> Int -> [String]
cbomb d s = coordenadaBomb (intToChar (zip (auxHB d s) (coordenadaV d (posicaoBomb d s))))

-- | 'cflame' : função que __dá as coordenadas dos power ups tipo flame__
cflame :: Int -> Int -> [String] 
cflame d s = coordenadaFlame (intToChar (zip (auxHF d s) (coordenadaV d (posicaoFlame d s))))

--coordenadaBomb (intToChar (zip (auxHB) (coordenadaV d (posicaoBomb d s))))
-- | 'coordenadaBomb' : @função auxiliar@ da função "cbomb"
coordenadaBomb :: [String] -> [String]
coordenadaBomb [] = []
coordenadaBomb (x:xs) = (['+'] ++ " " ++ x ): coordenadaBomb xs

--coordenadaFlame
-- | 'coordenadaFlame' : @função auxiliar@ da função "cflame"
coordenadaFlame :: [String] -> [String]
coordenadaFlame [] = []
coordenadaFlame (x:xs) = (['!'] ++ " " ++ x ): coordenadaFlame xs

--converter int para char
-- | 'intToChar' : função que altera o tipo da função
intToChar :: [(Int,Int)] -> [String] 
intToChar [] = []
intToChar (h:t) = (coordenada1 h : intToChar t)

-- | 'coordenada1' : @função auxiliar@ da função "intToChar"
coordenada1 :: (Int, Int) -> String
coordenada1 (x,y) = (show x ++ " " ++ show y)

--auxiliares para "simplificar" coordenadaHB e coordenadaHF
-- | 'auxHB' : @função auxiliar@ para "simplificar" coordenadaHB
auxHB :: Int -> Int -> [Int]
auxHB d s = coordenadaHB (agrupa d (substituir2 (contador d) d (pedras d (contador d)) (celulaVazia d (contador d)) (porResultadoGama2 (gerador d s))))

-- | 'auxHF' : @função auxiliar@ para "simplificar" coordenadaHF
auxHF :: Int -> Int -> [Int]
auxHF d s = coordenadaHF (agrupa d (substituir2 (contador d) d (pedras d (contador d)) (celulaVazia d (contador d)) (porResultadoGama2 (gerador d s))))


--coordenadaH (agrupa d (substituir2 (contador d) d (pedras d (contador d)) (celulaVazia d (contador d)) (porResultadoGama2 (gerador d s))))
coordenadaHB :: [String] -> [Int]
coordenadaHB [] = []
coordenadaHB (h:t) = if elem '+' h then (my_elemIndices2 '+' h ++ coordenadaHB t)
                                   else coordenadaHB t


--coordenadaH (agrupa d (substituir2 (contador d) d (pedras d (contador d)) (celulaVazia d (contador d)) (porResultadoGama2 (gerador d s))) )
coordenadaHF :: [String] -> [Int]
coordenadaHF [] = []
coordenadaHF (h:t) = if elem '!' h then (my_elemIndices2 '!' h ++ coordenadaHF t)
                                   else coordenadaHF t

my_elemIndices2 :: Eq a => a -> [a] -> [Int]
my_elemIndices2 elem lista = elemIndicesAux 0 elem lista where
    elemIndicesAux _ _ [] = []
    elemIndicesAux i y (x:xs) = if (x == y) then i : (elemIndicesAux (i + 1) y xs) else elemIndicesAux (i + 1) y xs


--coordenadaV d (posicaoBomb d s) coluna
--coordenadaV d (posicaoFlame d s) coluna

coordenadaV :: Int -> [Int] -> [Int]  
coordenadaV d [] = []
coordenadaV d (h:t) = (calculaV d h : coordenadaV d t)

-- | 'calculaV' : função auxiliar de "coordenadaV"
calculaV :: Int -> Int -> Int
calculaV d h = if (h<=(d+1) || h<=(d+d)) then 1
                                         else 1 + (calculaV d (h-d)) 


--em que posicao do mapa estao os power ups
-- | 'posicaoBomb' : função que /diz em que posição estão os power ups do tipo bomb/
posicaoBomb :: Int -> Int -> [Int]
posicaoBomb d s = my_elemIndices '+' (substituir2 (contador d) d (pedras d (contador d)) (celulaVazia d (contador d)) (porResultadoGama2 (gerador d s)))

-- | 'posicaoFlame' : função que /diz em que posição estão os power ups do tipo flame/
posicaoFlame :: Int -> Int -> [Int]
posicaoFlame d s = my_elemIndices '!' (substituir2 (contador d) d (pedras d (contador d)) (celulaVazia d (contador d)) (porResultadoGama2 (gerador d s)))

-- | 'my_elemIndices' : @função auxiliar@ das funções "posicaoBomb" e "posicaoFlame"
my_elemIndices :: Eq a => a -> [a] -> [Int]
my_elemIndices elem lista = elemIndicesAux 1 elem lista where
    elemIndicesAux _ _ [] = []
    elemIndicesAux i y (x:xs) = if (x == y) then i : (elemIndicesAux (i + 1) y xs) else elemIndicesAux (i + 1) y xs

-- | 'substituir2' : @função auxiliar@ de "posicaoFlame" e de "posicaoBomb"
substituir2 :: [Coordenadas] -> Int -> [Char] -> [Char] -> [Char] -> [Char]      
substituir2 l d l1 l2 l3 = if d>5 then condicao l d l1 l2 l3 else condicao5 l d l1 l2 l3

--altera '?' por '+' e '!'
-- | 'porResultadoGama2' : função que /altera "?" por "+" ou "!"/
porResultadoGama2 :: [Int] -> [Char]
porResultadoGama2 [] = []
porResultadoGama2 (h:t) | (h==0 || h==1) = ('+': porResultadoGama2 t)
                        | (h==2 || h==3) = ('!': porResultadoGama2 t)
                        | otherwise = ('?': porResultadoGama2 t)

-- | 'gerador' : /gera um determinado número de números aleatórios/ (consoante o número de células que inicialmente não podem ser célula vazia nem pedra)
gerador :: Int -> Int -> [Int]
gerador d s | (d==5) = take d1 $ randomRs (0,99) (mkStdGen s) --quando dimensao mapa = 5, o numero de espaços vazios (os 4 cantos devem ter 3 celulas vazias) sera menor (=8)
            | (d>5) = take ds $ randomRs (0,99) (mkStdGen s)
              where d1 = ((d*d)-8-((d*4)-4)-((div (d-2) 2)^2))  
                    ds = ((d*d)-12-((d*4)-4)-((div (d-2) 2)^2))

-----------------

---faz as linhas do mapa (pega na info do "substituir"), mas agrupando em linhas ([String])
-- | 'agrupa' : função que __faz as linhas do mapa__ a partir da informação da função "substituir"
agrupa :: Int -> [Char] -> [String]
agrupa d [] = []
agrupa d l = (conta d l: agrupa d (drop d l))

-- | 'conta' : @função auxiliar@ da função "agrupa"
conta :: Int -> [Char] -> [Char]
conta d [] = []
conta d (h:t) = if d > 0
                then (h: conta (d-1) t)
                else [] 
     

type Coordenadas = (Int,Int)

--constroi o mapa tendo em conta onde deve haver pedras, espacos vazios ou pontos de interrogacao
---mas ainda sem ter definidas as linhas ([String])
-- | 'substituir' : função que constrói o mapa tendo em conta onde deve haver pedras, espaços vazios ou pontos de interrogação

substituir :: [Coordenadas] -> Int -> [Char] -> [Char] -> [Char] -> String
substituir l d l1 l2 l3 = if d>5 then condicao l d l1 l2 l3 else condicao5 l d l1 l2 l3


porResultadoGama :: [Int] -> [Char]
porResultadoGama [] = []
porResultadoGama (h:t) | (h <=0 || h <= 39) = ('?': porResultadoGama t)
                       | otherwise = (' ': porResultadoGama t)

-- | 'condicao' : função auxiliar de "substituir" para um __mapa de dimensão maior que 5__
condicao :: [Coordenadas] -> Int -> [Char] -> [Char] -> [Char] -> [Char]
condicao [] _ _ _ _ = []
condicao ((x,y):t) d (p:ps) (v:vs) (g:gs) | forPedra (x,y) d = (p: condicao t d ps (v:vs) (g:gs))
                                          | forVazia (x,y) d = (v: condicao t d (p:ps) vs (g:gs))
                                          | otherwise = (g: condicao t d (p:ps) (v:vs) gs)
condicao _ d _ [] _ = replicate (d+1) '#'
condicao t d (p:ps) (v:vs) [] = condicao t d (p:ps) (v:vs) ['1'] -- adicionei o '1' para o programa continuar a correr
-- | 'forPedra' : função auxiliar de "condicao"
forPedra :: Coordenadas -> Int -> Bool
forPedra (x,y) d = (x==1) || ((y==1 || y==d)) || ((mod x 2 == 1 && mod y 2 == 1)) || (x==d) 
-- | 'forVazia' : função auxiliar de "condicao"
forVazia :: Coordenadas -> Int -> Bool
forVazia (x,y) d = (x==2 && y==2) || (x==2 && y==3) || (x==2 && y==(d-2)) || (x==2 && y==(d-1)) || (x==3 && y==2)                     
                                  || (x==3 && y==(d-1)) || (x==(d-2) && y==2) || (x==(d-2) && y==(d-1)) || (x==(d-1) && y==2) 
                                  || (x==(d-1) && y==3) || (x==(d-1) && y==(d-2)) || (x==(d-1) && y==(d-1))

-- | 'condicao5' : função auxiliar de "substituir" para um __mapa de dimensão igual a 5__
condicao5 :: [Coordenadas] -> Int -> [Char] -> [Char] -> [Char] -> [Char]
condicao5 [] _ _ _ _ = []
condicao5 ((x,y):t) d (p:ps) (v:vs) (g:gs) | forPedra5 (x,y) d = (p: condicao5 t d ps (v:vs) (g:gs))
                                           | forVazia5 (x,y) d = (v: condicao5 t d (p:ps) vs (g:gs))
                                           | otherwise = (g:condicao5 t d (p:ps) (v:vs) gs)
condicao5 _ d _ [] _ = replicate (d+1) '#'
condicao5 t d (p:ps) (v:vs) [] = condicao5 t d (p:ps) (v:vs) ['1'] -- adicionei o '1' para o programa continuar a correr

-- | 'forPedra5' : @função auxiliar@ de "condicao5"
forPedra5 :: Coordenadas -> Int -> Bool
forPedra5 (x,y) d = (x==1) || (y==1 || y==d) || (x==3 && y==3) || (x==d) 

-- | 'forVazia5' : @função auxiliar@ de "condicao5"
forVazia5 :: Coordenadas -> Int -> Bool
forVazia5 (x,y) d = (x==2 && y==2) || (x==2 && y==3) || (x==2 && y==(d-1)) || (x==3 && y==2)
                                   || (x==3 && y==(d-1)) || (x==(d-1) && y==2)
                                   || (x==(d-1) && y==3) || (x==(d-1) && y==(d-1)) 

--"quantas pedras tem o mapa" ex: se tem 3 devolve "###"
-- | 'pedras' : função que /devolve as pedras existentes no mapa/
pedras :: Int -> [Coordenadas] -> [Char]
pedras d [] = []
pedras d l = if d>5 then dimensaoMore5 d l else dimensaoEqual5 d l

-- | 'dimensaoMore5' : função auxiliar de "pedras" para um mapa com dimensão maior que 5
dimensaoMore5 :: Int -> [Coordenadas] -> [Char]
dimensaoMore5 d [] = []
dimensaoMore5 d ((x,y):t) | (x==1)  = ('#': dimensaoMore5 d t)
                          | (y==1 || y==d) = ('#': dimensaoMore5 d t)
                          | (mod x 2 == 1 && mod y 2 == 1) = ('#': dimensaoMore5 d t)
                          | (x==d) = ('#': dimensaoMore5 d t)
                          | otherwise = dimensaoMore5 d t 

-- | 'dimensaoEqual5' : função auxiliar de "pedras" para um mapa com dimensão 5
dimensaoEqual5 :: Int -> [Coordenadas] -> [Char]
dimensaoEqual5 d [] = []
dimensaoEqual5 d ((x,y):t) | (x==1)  = ('#': dimensaoEqual5 d t)
                           | (y==1 || y==d) = ('#': dimensaoEqual5 d t)
                           | (x==3 && y==3) = ('#': dimensaoEqual5 d t)
                           | (x==d) = ('#': dimensaoEqual5 d t)
                           | otherwise = dimensaoEqual5 d t 


--"quantas celulas vazias tem o mapa"
-- | 'celulaVazia' : função que /testa quantas células vazias tem o mapa/
celulaVazia :: Int -> [Coordenadas] -> [Char]
celulaVazia d [] = []
celulaVazia d l = if d>5 then dimensaoMaiorQue5 d l else dimensaoIgual5 d l        

-- | 'dimensaoMaiorQue5' : função auxiliar de "celulaVazia" para um mapa com dimensão maior que 5

dimensaoMaiorQue5 :: Int -> [Coordenadas] -> [Char]
dimensaoMaiorQue5 d [] = []
dimensaoMaiorQue5 d ((x,y):t) | (x==2 && y==2) = (' ':celulaVazia d t)
                              | (x==2 && y==3) = (' ':celulaVazia d t)
                              | (x==2 && y==(d-2)) = (' ':celulaVazia d t)
                              | (x==2 && y==(d-1)) = (' ':celulaVazia d t)
                              | (x==3 && y==2) = (' ':celulaVazia d t)                       
                              | (x==3 && y==(d-1)) = (' ':celulaVazia d t)
                              | (x==(d-2) && y==2) =(' ':celulaVazia d t)
                              | (x==(d-2) && y==(d-1)) = (' ':celulaVazia d t)
                              | (x==(d-1) && y==2) = (' ':celulaVazia d t)
                              | (x==(d-1) && y==3) = (' ':celulaVazia d t)
                              | (x==(d-1) && y==(d-2)) = (' ':celulaVazia d t)
                              | (x==(d-1) && y==(d-1)) = (' ':celulaVazia d t)
                              | otherwise = celulaVazia d t

-- | 'dimensaoIgual5' : função auxiliar de "celulaVazia" para um mapa de dimensão 5
dimensaoIgual5 :: Int -> [Coordenadas] -> [Char]
dimensaoIgual5 d [] = []
dimensaoIgual5 d ((x,y):t) | (x==2 && y==2) = (' ':celulaVazia d t)
                           | (x==2 && y==3) = (' ':celulaVazia d t)
                           | (x==2 && y==(d-1)) = (' ':celulaVazia d t)
                           | (x==3 && y==2) = (' ':celulaVazia d t)
                           | (x==3 && y==(d-1)) = (' ':celulaVazia d t)
                           | (x==(d-1) && y==2) = (' ':celulaVazia d t)
                           | (x==(d-1) && y==3) = (' ':celulaVazia d t)
                           | (x==(d-1) && y==(d-1)) = (' ':celulaVazia d t)
                           | otherwise = celulaVazia d t

{-
dimensao 9
(2,2) (2,3) (2,7) (2,8)
(3,2) (3,8)
(7,2) (7,8)
(8,2) (8,3) (8,7) (8,8) 

dimensao 7
(2,2) (2,3) (2,5) (2,6)
(3,2) (3,6)
(5,2) (5,6)
(6,2) (6,3) (6,5) (6,6)

dimensao 5
(2,2) (2,3) (2,4)
(3,2) (3,4)
(4,2) (4,3) (4,4)
 -}

--devolve todas as coordenadas que existem no mapa
-- | 'contador' : função que __devolve todas as coordenadas que existem no mapa__
contador :: Int -> [Coordenadas]
contador d = zip (reverse(contadorX d d)) (contadorY d d)

-- | 'contadorX' : @função auxiliar@ de "contador"
contadorX :: Int -> Int -> [Int]
contadorX d ds = if ds > 0
                 then replicate d ds ++ contadorX d (ds-1)
                 else []

-- | 'contadorY' : f@unção auxiliar@ de "contador"
contadorY :: Int -> Int -> [Int]
contadorY d ds = my_replicate d [1..ds] 

-- | 'my_replicate' : @função auxiliar@ da função "contadorY"
my_replicate :: Int -> [a] -> [a]
my_replicate 0 _ = []
my_replicate n l = l ++ my_replicate (n-1) l
