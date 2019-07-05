module Bomberman where

type Coordenadas = (Int,Int)
type Mapa = [String]
type Raio = Int
type DimensaoMapa = Int

--devolve todas as coordenadas que existem no mapa
contador :: Int -> [Coordenadas]
contador d = zip (reverse(contadorXXXX d d)) (contadorYYYY d d)

contadorXXXX :: Int -> Int -> [Int]
contadorXXXX d ds = if ds > 0
                 then replicate d ds ++ contadorXXXX d (ds-1)
                 else []

contadorYYYY :: Int -> Int -> [Int]
contadorYYYY d ds = my_replicate d [1..ds] 

my_replicate :: Int -> [a] -> [a]
my_replicate 0 _ = []
my_replicate n l = l ++ my_replicate (n-1) l

-- funcao que diz onde estão as coordenadas do "fogo" da bomba
downCoordenadas :: (Int,Int) -> Int -> (Int,Int)
downCoordenadas (x,y) n1 = (x,(y+n1))

upCoordenadas :: (Int,Int) -> Int -> (Int,Int)
upCoordenadas (x,y) n1 = (x,(y-n1))

leftCoordenadas :: (Int,Int) -> Int -> (Int,Int)
leftCoordenadas (x,y) n1 = ((x-n1),y)

rightCoordenadas :: (Int,Int) -> Int -> (Int,Int)
rightCoordenadas (x,y) n1 = ((x+n1),y)


--Coordenadas dos power ups no mapa:
listapu :: [String] -> [(Int,Int)]
listapu m =(coordenadasPU1 (coordenadasPU2 m))

coordenadasPU1 :: [String] -> [(Int,Int)]
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
-- função que devolve as coordenadas no tipo (Int,Int)
coord :: String -> (Int,Int)
coord w = (read (coord1 w) :: Int, read (coord2 w) :: Int)

coord1 :: String -> String
coord1 (h:t) = if h /= ' ' then h : coord1 t
                           else []
coord2 :: String -> String
coord2 l = drop (1 + length (coord1 l)) l


--Coordenadas dos jogadores no mapa:
listajogadores :: [String] -> [(Int,Int)]
listajogadores m = toInt1 (listajogadores1 m)

toInt1 :: [[String]] -> [(Int,Int)]
toInt1 [] = []
toInt1 (h:t) = toInt11 h : toInt1 t

toInt11 :: [String] -> (Int,Int)
toInt11 (x:y:vazio) = ((read x) :: Int, (read y) :: Int)

listajogadores1 :: [String] -> [[String]]
listajogadores1 m = listajogadores2 (tail11 m)

listajogadores2 :: [[String]] -> [[String]]
listajogadores2 [] = []
listajogadores2 (h:t) = listajogadores3 h : listajogadores2 t

listajogadores3 :: [String] -> [String]
listajogadores3 (x:y:zs) = [x] ++ [y]

tail11 :: [String] -> [[String]]
tail11 m = tail21 (words31 m)

tail21 :: [[String]] -> [[String]]
tail21 [] = []
tail21 (h:t) = tail h : tail21 t

words31 :: [String] -> [[String]]
words31 m = words21 (linhaplayers m)

words21 :: [String] -> [[String]]
words21 [] = []
words21 (h:t) = (words h) : words21 t

--funcao que dá as linhas alusivas às informaçoes dos jogadores
linhaplayers :: [String] -> [String]
linhaplayers [] = []
linhaplayers (h:t) = if (head h == '0' || head h == '1' || head h == '2' || head h == '3')
                     then h : linhaplayers t
                     else linhaplayers t