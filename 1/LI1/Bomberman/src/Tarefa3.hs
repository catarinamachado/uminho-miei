module Main where
import System.Environment
import Data.Char
-- | =Main

{-|
Module : Main

Description : Módulo Haskell que contém um exemplo de possível compressão e descompressão de um mapa de jogo 

Um módulo contendo funções recursivas e não recursivas com o intuito de devolver um mapa mais comprimido ou descomprimido, consoante o desejado.
-}

-- | 'encode' : função que codifica o estado do jogo 

encode :: [String] -> String
encode l = unlines (comprimir l)

-- | 'decode' : função que descodifica o estado do jogo comrpimido anteriormente
decode :: String -> [String]
decode l = lines (descomprimir l)

-- |'main' : função que comprime ou descomprime o mapa consoante o seu estado atual 

main :: IO ()
main = do a <- getArgs
          let p = a !! 0
          w <- getContents
          if length a == 1 && length p == 2 && (p=="-e" || p=="-d")
             then if p=="-e" then putStr $ encode $ lines w
                             else putStr $ unlines $ decode w
             else putStrLn "Parâmetros inválidos"

-- | 'separaMapa' : função que mostra apenas o estado inicial referente ao mapa

--mapa
separaMapa :: [String] -> [String]
separaMapa l = take (length (head l)) l 

-- | 'separaCoordenadas' : função que mostra o estado das coordenadas do jogo

--coordenadas
separaCoordenadas :: [String] -> [String]
separaCoordenadas l = drop (length (head l)) l

-------encode

-- | 'comprimirMapa' : função que decide o estado comprimido do jogo consoante o seu tamanho, distinguindo assim os mapas de dimensão 5 dos de dimensão maior que 5 pela razão de que um mapa de dimensão 5 tem possibilidade de maior compressão pois o seu formato será sempre igual independentemente do valor da semente           

comprimir :: [String] -> [String]
comprimir [] = []
comprimir l = comprimirMapa (separaMapa l) ++ comprimirCoordenadas (separaCoordenadas l)

{-|
==Exemplos de utilização:
>>>comprimir ["#####","#   #","# # #","#   #","#####","0 1 1"]
["v","01 1"]

>>>comprimir ["#########","#       #","# #?# # #","#     ? #","#?# # #?#","# ?  ?  #","# #?#?# #","#  ??   #","#########","! 5 5","* 7 7 1 1 10","0 3 3 ++","1 7 7"]
["7","1#?#1#1","5?1","?#1#1#?","1?2?2","1#?#?#1","2??3","!5 5","*7 7 1 1 10","03 3 ++","17 7"]
-}

---------comprime o mapa
comprimirMapa :: [String] -> [String]
comprimirMapa [] = []
comprimirMapa l = if (length (head l)) == 5 then mapa5 l else mapaMaior5 l

--para d=5

-- | 'mapa5' : função que substitui um mapa de dimensão por um caractér

mapa5 :: [String] -> [String]
mapa5 (h:t) = [['v']]

--para d>5

-- | 'mapaMaior5' : função que comprimirá um mapa de dimensão maior que 5

mapaMaior5 :: [String] -> [String]
mapaMaior5 l = (comprimirPedra (comprimirEspacos l))

{-| 
Uma das características que distingue a função 'mapa5' da função 'mapaMaior5':
*calcula um mapa contraído de dimensão 5
-}

--fazer com que '#' desapareça do mapa
comprimirPedra :: [String] -> [String]
comprimirPedra [] = []
comprimirPedra l = primUltPedras (pedrasLinha l)

--------tirar pedras da primeira e ultima linha

-- | 'pedrasLinha' : função que retira as pedras ('#') da primeira e  última linhas, substituindo-as pelo vazio

pedrasLinha :: [String] -> [String]
pedrasLinha (h:t) = init t

--------tirar muro das outras linhas

-- | 'primUltPedras' : função que retira o muro das outras linhas, ou seja, retira o primeiro e último elementos de cada linha


primUltPedras :: [String] -> [String]
primUltPedras l = primPedra (ultPedra l)

---tirar pedra da primeira coluna

-- | 'primPedra' : função que tira as pedras da primeira coluna

primPedra:: [String] -> [String]
primPedra [] = []
primPedra ((x:xs):ys) = xs:primPedra ys

---tirar pedra da última coluna

-- | 'ultPedra' : função que retira as pedras da última coluna

ultPedra :: [String] -> [String]
ultPedra [] = []
ultPedra (h:t) = (init h):(ultPedra t)

----faz contagem dos espaços

-- | 'comprimirEspacos' : função que substitui os espaços pêlo seu número existencial

comprimirEspacos :: [String] -> [String]
comprimirEspacos [] = []
comprimirEspacos (h:t) = espacosNada h 0 : comprimirEspacos t

-- | 'espacosNada' : função que faz contagem dos espaços

espacosNada :: [Char] -> Int -> [Char]
espacosNada [] _ = []
espacosNada (h:t) 0 = if h == ' ' then espacosNada t 1 else h:espacosNada t 0
espacosNada (h:t) x = if h == ' ' then espacosNada t (x+1) else (intToDigit x) : h : espacosNada t 0

---------comprime as coordenadas
-- | 'comprimirCoordenadas' : função que comprime o espaço das coordenadas

comprimirCoordenadas :: [String] -> [String]
comprimirCoordenadas l = tiraEspaCoord (jogadorParaChar l)

---tira apenas o primeiro espaço
-- | 'jogadorParaChar' : função auxiliar de "comprimirCoordenadas"
jogadorParaChar :: [String] -> [String]
jogadorParaChar ((x:xs):t) = if isNumber x then ((numberPorChar x):xs) : jogadorParaChar t else (x:xs):jogadorParaChar t

-- | 'numberPorChar' : função que passa o numero do jogador para char
numberPorChar :: Char -> Char
numberPorChar x | x=='0' = 'a'
                | x=='1' = 'b'
                | x=='2' = 'c'
                | x=='3' = 'd'

-- | 'tiraEspaCoord' : função que retira o primeiro espaço existente nas coordenadas

tiraEspaCoord :: [String] -> [String]
tiraEspaCoord [] = []
tiraEspaCoord (h:t) = if elem ' ' h then tiraEspaCoordAux h:tiraEspaCoord t else h:tiraEspaCoord t  --(tiraEspaCoordAux h):t else h:tiraEspaCoord t

-- | 'tiraEspaCoordAux' : função recursiva que retira o primeiro espaço de uma linha de coordenadas

tiraEspaCoordAux :: String -> String
tiraEspaCoordAux [] = []
tiraEspaCoordAux (h:t) = if h == ' ' then t else h:tiraEspaCoordAux t

-------decode

-- | 'descomprimir' : função geral que descomprime o mapa juntamente com as coordenadas para o seu estado inicial

descomprimir :: [Char] -> [Char]
descomprimir (h:t) = if h=='v' then descomprimir5 h ++ descomprimirCoordenadas (h:t) else descomprimirMapa (h:t) ++ descomprimirCoordenadas (h:t)

--vê se o primeiro elemento é ou não uma letra(se for entao d=5 se não é d>5)

-- | 'descomprimirMapa' : descomprimirá o mapa devolvendo o seu estado atual

descomprimirMapa :: [Char] -> [Char]
descomprimirMapa l = meterPedras (descomprimirEspacos l) --funçao de meter pedras + espaços

--para d=5
-- | 'descomprimir5' : função que descomprimirá apenas o estado do mapa

descomprimir5 :: Char -> [Char]
descomprimir5 x = "#####\n#   #\n# # #\n#   #\n#####\n"

--para d>5

--meter espaços
-- | 'descomprimirEspacos' : função que irá descomprimir os espaços inicialmente existentes no mapa

descomprimirEspacos :: [Char] -> [Char]
descomprimirEspacos [] = []
descomprimirEspacos (h:t) = if isNumber h then (meterEspacos h)++descomprimirEspacos t else h:descomprimirEspacos t

-- | 'meterEspacos' : função que coloca espaços, substituindo-os pelos números previamente lá colocados

meterEspacos :: Char -> [Char]
meterEspacos a = replicate (digitToInt a) ' '


--meter pedras
-----passar mapa [Char] para [String]
-- | 'mapaString' : função que passa o mapa do tipo String para [String]

mapaString :: String -> [String]
mapaString l = lines l

----passar mapa de [String] para Char
-- | 'mapaChar' : função que passa o mapa do tipo [String] para String

mapaChar :: [String] -> String
mapaChar l = unlines l

-- | 'meterPedras' : função geral do tipo String que coloca as pedras de novo no mapa

meterPedras:: String -> String
meterPedras l = mapaChar (meterPedrasLString (mapaString l))

-- mete # no fim e inicio das linhas
-- | 'meterPedrasLString' : função do tipo [String] que coloca as pedras

meterPedrasLString :: [String] -> [String] 
meterPedrasLString l = meterPedrasC (meterPedrasL l)

-- | 'meterPedrasC' : função que coloca as pedras na primeira e última colunas

meterPedrasC :: [String] -> [String]
meterPedrasC [] = []
meterPedrasC (h:t) = meterPedrasCAux h : meterPedrasC t

-- | 'meterPedrasCAux' : função auxiliar de "meterPedrasC"
meterPedrasCAux :: String -> String
meterPedrasCAux [] = []
meterPedrasCAux l = ('#':l) ++ ['#']

--mete a primeira e ultima linhas só #
-- | 'meterPedrasL' : função que coloca uma linha no fim e no início de pedras

meterPedrasL :: [String] -> [String]
meterPedrasL (h:t) = (w:h:t)++[w]
        where w = replicate (length h) '#'

--descomprimir espaços das coordenadas
-- | 'descomprimirCoordenadas' : função que nos fornece o estado inicial das coordenadas
descomprimirCoordenadas :: String -> String
descomprimirCoordenadas l = charN (mapaChar (descomprimirCoordenadasC l))

-- | 'charN' : função auxiliar de "descomprimirCoordenadas"
charN :: String -> String
charN [] = []
charN (h:t) = if h == 'a' || h == 'b' || h == 'c' || h == 'd' then charParaNumber h:charN t else h:charN t

-- | 'charParaNumber' : função que devolve os números dos jogadores ao mapa
charParaNumber :: Char -> Char
charParaNumber x | x=='a' = '0'
                 | x=='b' = '1'
                 | x=='c' = '2'
                 | x=='d' = '3'

-- | 'descomprimirCoordenadasC' : função que nos fornece o estado inicial das coordenadas no tipo [String]
descomprimirCoordenadasC :: String -> [String]
descomprimirCoordenadasC l = descomprimirCoordenadasAux (mapaString l) 

-- | 'descomprimirCoordenadasAux' : função auxiliar de "descomprimirCoordenadasC"
descomprimirCoordenadasAux :: [String] -> [String]
descomprimirCoordenadasAux [] = []
descomprimirCoordenadasAux ((x:xs):y) = if x=='a' || x=='b' || x=='c' || x=='d' || x=='+' || x=='!' || x=='*' then ((x:[' '])++xs):descomprimirCoordenadasAux y
                                                                                                              else (x:xs):descomprimirCoordenadasAux y