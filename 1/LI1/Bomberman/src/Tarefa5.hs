{-|
Module         : @Main@

Descrição      : Mapa do jogo Bomberman

Autores        :

* Catarian Araújo Machado, a81047@alunos.uminho.pt

* Joana Albuquerque Matos, a81765@alunos.uminho.pt

Este módulo contém uma maneira de criar uma interface gráfica do jogo.
-}

--["#############","#    ? ?    #","# # #?#?# # #","#   ???    ?#","# #?# #?# #?#","#  ?   ?????#","#?#?# #?# # #","#  ? ? ???? #","# #?# #?# # #","#?  ? ? ??? #","# # # #?#?# #","#    ?   ?  #","#############","+ 5 3","+ 11 3","! 10 7","1 2 3"]


module Main where
import Data.Char
import System.Random
import Graphics.Gloss         
import Graphics.Gloss.Data.Picture  
import Graphics.Gloss.Interface.Pure.Game
import Graphics.Gloss.Data.Bitmap
import Ajudaa1
import Ajudaa2
import Ajudaa4

-- | Uma representação do estado do jogo.
type EstadoV = ([String]
               ,[InfoPlayer]
               ,[Powers]
               ,[InfoBombs])

-- | Uma representação do estado do jogo com a informação das imagens em formato 'bitmap' e o tempo.
type Estado = ([String]
               ,[InfoPlayer]
               ,[Powers]
               ,[InfoBombs]
               ,[Picture]   
               ,Float) --tempo do jogo

-- | Exemplo de uma função que junta as figuras e o tempo ao estado do jogo.
tudo :: EstadoV -> [Picture] -> Float -> Estado
tudo (m,p,c,b) i t = (m,p,c,b,i,t)

-- | Retira do estado atual do mapa, as linhas referentes às informações: do mapa, power ups, bombas e jogadores, respetivamente.
analisaEsboco :: [String] -> [String]
analisaEsboco [] = []
analisaEsboco (h:t) = if (head h == '#') then h: analisaEsboco t
                                         else analisaEsboco t

-- | 'analisaPower' : função que devolve apenas [String] com informações acerca dos power up's.
analisaPower :: [String] -> [String]
analisaPower [] = []
analisaPower (h:t) = if (head h == '+' || head h == '!') then h: analisaPower t
                                                         else analisaPower t

-- | 'analisaBomb' : função que devolve apenas [String] com informações acerca das bombas.
analisaBomb :: [String] -> [String]
analisaBomb [] = []
analisaBomb (h:t) = if (head h == '*') then h: analisaBomb t
                                       else analisaBomb t

-- | 'analisaPlayer' : função que devolve apenas [String] com informações acerca dos jogadores.
analisaPlayer :: [String] -> [String]
analisaPlayer [] = []
analisaPlayer (h:t) = if (head h == '0' || head h == '1' || head h == '2' || head h == '3') then h: analisaPlayer t
                                                                                            else analisaPlayer t

--funcao que dá as coordenadas dos jogadores
playerCoord :: [String] -> [(Int,Int)]
playerCoord (h:t) = (((jogador h) !! 1) , ((jogador h) !! 2)) : playerCoord t

jogador :: String -> [Int]
jogador l = funcB $ funC l

funcB :: [String] -> [Int]
funcB (h:t) = ((read h)::Int) : (funcB t)

funC :: String -> [String]
funC l = words l

--qual é o raio das bombas a partir de analisaBomb
raioBomb :: [String] -> [Int]
raioBomb [] = []
raioBomb (h:t) = ((bomb h) !! 4) : raioBomb t

bomb :: [Char] -> [Int]
bomb l = listaInt $ tirab $ listaBomb l

listaBomb :: [Char] -> [String]
listaBomb l = words l

tirab :: [String] -> [String]
tirab [] = []
tirab ((h:hs):t) | h == '*' = hs:tirab t
                 | otherwise = (h:hs) : tirab t

--recebe [String já com o words
listaInt :: [String] -> [Int]
listaInt [] = []
listaInt (h:t) = ((read h)::Int) : listaInt t

jogadorBomb :: [String] -> [Int]
jogadorBomb [] = [] 
jogadorBomb (h:t) = ((bomb h) !! 3) : jogadorBomb t

tempoBomb :: [String] -> [Int]
tempoBomb [] = []
tempoBomb (h:t) = ((bomb h) !! 5) : tempoBomb t

--quais sao as coordenadas das bombas (posição 3 e 5 da lista) a partir de analisaBomb
coordBomb :: [String] -> [(Int,Int)]
coordBomb [] = []
coordBomb (h:t) = (((bomb h) !! 1) , ((bomb h) !! 2)) : coordBomb t 

--quais sao as coordenadas dos power ups a partir de analisaPower

coordPU :: [String] -> [(Int,Int)]
coordPU [] = []
coordPU (h:t) = (((powersc h) !! 1) , ((powersc h) !! 2)) : coordPU t

powersc :: [Char] -> [Int]
powersc l = listaIt (listap l)

listap :: [Char] -> [String]
listap l = words l

--recebe [String] já com o words
listaIt :: [String] -> [Int]
listaIt [] = []
listaIt (h:t) = ((read h)::Int) : listaIt t


--ver se o jogador tem power ups a partir de analisaPlayer
jogadorPu :: String -> [Maybe Char] 
jogadorPu [] = [Nothing]
jogadorPu (h:t) | h == '+' = (Just h) : jogadorPu t 
                | h == '!' = (Just h) : jogadorPu t
                | otherwise = jogadorPu t

type Powers =  ( Char
               ,(Int,Int))

type InfoBombs = ( Char        
                  ,(Int,Int)   --coordenadas bomba
                  ,Int         --jogador que a colocou
                  ,Int         --raio
                  ,Int)        --tempo

type InfoPlayer = ( Char         --jogador
                   ,(Int,Int)
                   ,[Maybe Char])

-- | Uma maneira de colocar a informação do estado do jogo no tipo [String] para o tipo EstadoV-
separateMap :: [String] -> EstadoV
separateMap l = (take 13 l , infoplay $ drop 13 l, pu $ drop 13 l , bombs $ drop 13 l)

pu :: [String] -> [Powers]
pu [] = []
pu ((h:hs):t) = if h == '!' || h == '+' 
           then (h , (((powersc (h:hs)) !! 1) , ((powersc (h:hs)) !! 2))) : pu t
           else pu t       

bombs :: [String] -> [InfoBombs]
bombs [] = []
bombs ((h:hs):t) = if h == '*' 
                   then (('*'), ((bomb (h:hs)) !! 0 , (bomb (h:hs)) !! 1) , ((bomb (h:hs)) !! 2) , ((bomb (h:hs)) !! 3) , ((bomb (h:hs)) !! 4)) : bombs t
                   else bombs t

infoplay :: [String] -> [InfoPlayer]
infoplay [] = []
infoplay ((h:hs):t) | h == '0' = (('0') , ((jogador (h:hs)) !! 1 , (jogador (h:hs)) !! 2) , jogadorPu (h:hs)) : infoplay t
                    | h == '1' = (('1') , ((jogador (h:hs)) !! 1 , (jogador (h:hs)) !! 2) , jogadorPu (h:hs)) : infoplay t
                    | h == '2' = (('2') , ((jogador (h:hs)) !! 1 , (jogador (h:hs)) !! 2) , jogadorPu (h:hs)) : infoplay t
                    | h == '3' = (('3') , ((jogador (h:hs)) !! 1 , (jogador (h:hs)) !! 2) , jogadorPu (h:hs)) : infoplay t
                    | otherwise = infoplay t

-- | Uma maneira de colocar a informação do estado do jogo do tipo EsatdoV para [String].
putMapTogether :: EstadoV -> [String]
putMapTogether (m,j,p,b) = m ++ iniPlayer j ++ iniPower p ++ iniBomb b

iniPlayer :: [InfoPlayer] -> [String]
iniPlayer [] = []
iniPlayer ((h,(a,b),m):r) = ((h : ' ' : coordToString (a,b)) ++ puToString m) : iniPlayer r

puToString :: [Maybe Char] -> String
puToString [] = []
puToString (h:t) | (h == Nothing) = puToString t
                 | (h == Just 'a') = ('a') : puToString t

iniPower :: [Powers] -> [String]
iniPower [] = []
iniPower ((h,(a,b)):t) = ((h : ' ' : coordToString (a,b))) : iniPower t

coordToString :: (Int,Int) -> String
coordToString (a,b) = (intToDigit a) : ' ' : [(intToDigit b)]

iniBomb :: [InfoBombs] -> [String]
iniBomb [] = []
iniBomb ((h,(x,y),a,b,c):r) = ((h : ' ' : coordToString (x,y)) ++ (' ' : (intToDigit a) : ' ' : (intToDigit b) : ' ' : [(intToDigit c)])) : iniBomb r

-- todos os jogadores começam nos cantos do mapa

-- | Mapa com a posição inicial dos jogadores
mapa1 :: Int -> Int -> Int -> [String]
-- mapa com dimensao d, semente s e j jogadores
mapa1 d s j | j == 1 = mapa 13 2 ++ ["0 1 1"]
            | j == 2 = mapa 13 2 ++ ["0 1 1" ,
                                   ( "1" ++ " " ++ (show (d-2)) ++ " " ++ (show(d-2)))]
            | j == 3 = mapa 13 2 ++ ["0 1 1" ,
                                   ( "1" ++ " " ++ (show (d-2)) ++ " " ++ "1"),
                                   ( "2" ++ " " ++ "1" ++ " " ++ (show (d-2)))]
            | j == 4 = mapa 13 2 ++ ["0 1 1" ,
                                   ( "1" ++ " " ++ (show (d-2)) ++ " " ++ "1"),
                                   ( "2" ++ " " ++ "1" ++ " " ++ (show (d-2))),
                                   ( "3" ++ " " ++ (show (d-2)) ++ " " ++ (show (d-2)))]

-- | Um exemplo de tamanho e cor do contorno para o jogo.
contorno :: Picture
contorno = color black (Polygon [  (-220,10)
                                  ,(-10,10)
                                  ,(-10,220)
                                  ,(-220,220)])

--mapa com pedras e jogadores nas pontas
--depende do tamanho, dos pu, quantos jogadores

-- | O estado inicial do jogo.
estadoInicial :: Float -> [Picture] -> [Int] -> Estado
estadoInicial n p [ m, s, j] = tudo (separateMap (mapa1 13 2 j)) p n


-- | Função que desenha o jogo.
desenhaEstado :: Estado -> Picture
desenhaEstado (m,p,c,b,[pe,tij,bom,pubom,fl,j1,j2,j3,j4],t)
       = scale 2.25 2.25 ( Translate 120 (-120) (pictures
                                                        ([contorno]                            --
                                                         ++ (imageJPU p 90 180 pubom fl)    --
                                                         ++ (imagePU c comp pubom fl)       --
                                                         ++ (fundinho m 0 comp 0 pe tij)    --
                                                         ++ (imageB b comp bom)             --
                                                         ++ (imageJ p comp j1 j2 j3 j4)  --imagem j1 j2 j3 j4
                                                         ++ (imageF m b comp fl)            --
                                                         ++ [relogio]      )))
              where comp = length (head m)
                    relogio = Translate 35 200 $ Scale 0.25 0.25 $ Color yellow $ Text $ takeWhile (/='.') (show t)

imagePU :: [Powers] -> Int -> Picture -> Picture -> [Picture]
imagePU [] _ _ _ = []
imagePU ((a,(b,c)):t) i x y | a == '+' = Translate
                                        (trans (-227 + (b*(vezes i)))) (trans (227 - (c*(vezes i)))) (Scale ((0.72/13)*j) ((0.72/13)*j) x)
                                        : (imagePU t i x y)
                            | a == '!' = Translate
                                        (trans (-227 + (b*(vezes i)))) (trans (227 - (c*(vezes i)))) (Scale ((0.72/13)*j) ((0.72/13)*j) y)
                                        : (imagePU t i x y)
                    where j = (read ((takeWhile (/='.') (show i)))) :: Float


imageF :: [String] -> [InfoBombs] -> Int -> Picture -> [Picture]
imageF m p comp fl  = images (mapinha (flamess p) m) 0 comp 0 fl


images :: [String] -> Int -> Int -> Int -> Picture -> [Picture]
images [] _ _ _ _ = []
images ([]:t) n i h y = images t 0 i (h+1) y
images ((x:xs):t) n i h y | x == '$' = c
                          | otherwise = (images ((xs):t) (n+1) i h y)
                where j = (read ((takeWhile (/='.') (show i)))) :: Float
                      c = Translate (trans (-227 + (n*(vezes i)))) (trans (227 - (h*(vezes i)))) (Scale ((0.72/13)*j) ((0.72/13)*j) y) : (images ((xs):t) (n+1) i h y)

mapinha :: [(Int,Int)] -> [String] -> [String]
mapinha [] x = x
mapinha (h:t) x = mapinha t (aux h x)



aux :: (Int,Int) -> [String] -> [String]
aux (a,b) s = do let (x,(y:ys)) = splitAt b s 
                     (g,(h:hs)) = splitAt a y
                     z | h == '#' = h:hs
                       | otherwise = '$':hs
                 x++[g++z]++ys 


flamess :: [InfoBombs] -> [(Int,Int)]
flamess [] = []
flamess ((_,(x,y),_,r,o):t) | (o == 1) = [(x,y)] ++ uU ((x,y),r) ++ dD ((x,y),r) ++ lL ((x,y),r) ++ rR ((x,y),r) ++ flamess t
                            | otherwise = flamess t


uU :: ((Int,Int),Int) -> [(Int,Int)]
uU ((a,b),c) = if (c == 0) then []
               else (a,b+1) : uU ((a,b+1),c-1)

dD :: ((Int,Int),Int) -> [(Int,Int)]
dD ((a,b),c) = if (c == 0) then []
               else (a,b-1) : dD ((a,b-1),c-1)

lL :: ((Int,Int),Int) -> [(Int,Int)]
lL ((a,b),c) = if (c == 0) then []
               else (a-1,b) : lL ((a+1,b),c-1)

rR :: ((Int,Int),Int) -> [(Int,Int)]
rR ((a,b),c) = if (c == 0) then []
               else (a+1,b) : rR ((a+1,b),c-1)


imageJPU :: [InfoPlayer] -> Float -> Float -> Picture -> Picture -> [Picture]
imageJPU [] _ _ _ _ = []
imageJPU ((_,(_,_),[]):t) i n pubom fl = imageJPU t 90 (n-30) pubom fl
imageJPU ((a,(b,c),(Nothing:r)):t) i n pubom fl = imageJPU ((a,(b,c),r):t) i n pubom fl
imageJPU ((a,(b,c),(Just 'd':r)):t) i n pubom fl | 'd' == '+' = Translate i n (Scale 0.1 0.1 pubom) : imageJPU  ((a,(b,c),r):t) (i+20) n pubom fl
                                                 | 'd' == '!' = Translate i n (Scale 0.3 0.3 fl) : imageJPU  ((a,(b,c),r):t) (i+20) n pubom fl


fundinho :: [String] -> Int -> Int -> Int-> Picture -> Picture -> [Picture]
fundinho [] _ _ _ pe tij = []
fundinho ([]:r) n i h pe tij = fundinho r 0 i (h+1) pe tij
fundinho ((x:xs):r) n i h pe tij | x == '#'  = Translate (trans (-227 + (n*(vezes i)))) (trans (227 - (h*(vezes i)))) (Scale ((0.72/13)*j) ((0.72/13)*j) pe)
                                                                 : (fundinho ((xs):r) (n+1) i h pe tij)
                                 | x == '?'  = Translate (trans (-227 + (n*(vezes i)))) (trans (227 - (h*(vezes i)))) (Scale ((0.72/13)*j) ((0.72/13)*j) tij)
                                                                 : (fundinho ((xs):r) (n+1) i h pe tij)
                                 | otherwise = (fundinho ((xs):r) (n+1) i h pe tij)
                where j = (read ((takeWhile (/='.') (show i)))) :: Float


trans :: Int -> Float
trans n = read ((takeWhile (/='.') (show n))) :: Float


imageB :: [InfoBombs] -> Int -> Picture -> [Picture]
imageB [] _ _ = []
imageB ((a,(b,c),d,e,f):t) i bom = Translate
                                        (trans (-227 + (b*(vezes i)))) (trans (227 - (c*(vezes i)))) (Scale ((0.72/13)*j) ((0.72/13)*j) bom)
                                        : (imageB t i bom)
                       where j = (read ((takeWhile (/='.') (show i)))) :: Float


imageJ :: [InfoPlayer] -> Int -> Picture -> Picture -> Picture -> Picture -> [Picture]
imageJ [] _ _ _ _ _= []
imageJ ((a,(b,c),d):t) i j1 j2 j3 j4 | a == '0' = e
                                     | a == '1' = f
                                     | a == '2' = g
                                     | a == '3' = h
                  where j = (read ((takeWhile (/='.') (show i)))) :: Float
                        e = Translate (trans (-227 + (b*(vezes i)))) (trans (227 - (c*(vezes i)))) (Scale ((0.72/13)*j) ((0.72/13)*j) j1) : (imageJ t i j1 j2 j3 j4)
                        f = Translate (trans (-227 + (b*(vezes i)))) (trans (227 - (c*(vezes i)))) (Scale ((0.72/13)*j) ((0.72/13)*j) j2) : (imageJ t i j1 j2 j3 j4)
                        g = Translate (trans (-227 + (b*(vezes i)))) (trans (227 - (c*(vezes i)))) (Scale ((0.72/13)*j) ((0.72/13)*j) j3) : (imageJ t i j1 j2 j3 j4)
                        h = Translate (trans (-227 + (b*(vezes i)))) (trans (227 - (c*(vezes i)))) (Scale ((0.72/13)*j) ((0.72/13)*j) j4) : (imageJ t i j1 j2 j3 j4)

vezes :: Int -> Int
vezes 0 = 0
vezes x = div 240 x
                    

--Exemplo da aula:
--desenhaEstado ((dx,dy),(x,y),p,t) = Pictures [mapa, circulo, contador]
--      where mapa = Translate (-dx/2) (-dy/2) $ Color white $ Polygon [(0,0),(dx,0),(dx,dy),(0,dy)] 
--            circulo =Translate x y $ p
--            contador = Sclate 0.5 0.5 $ Text $ show t

--Bomberman:
-- quando encontrar o '#', e dizer coordenadas etc  
-- é preciso usar translate para desenhar os quadrados todos se nao fica tudo uns em cima dos outros (sempre 10 pixeis direita esquerda cima baixo)

-- | Função que altera o estado do jogo quando acontece um evento.
reageEvento :: Event -> Estado -> Estado
reageEvento (EventKey (SpecialKey KeyUp) Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 0 'U' )) i t
reageEvento (EventKey (SpecialKey KeyDown) Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 0 'D')) i t
reageEvento (EventKey (SpecialKey KeyRight) Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 0 'R')) i t
reageEvento (EventKey (SpecialKey KeyLeft) Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 0 'L')) i t
reageEvento (EventKey (SpecialKey KeySpace) Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 0 'B')) i t
reageEvento (EventKey (Char 'w') Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 1 'U')) i t
reageEvento (EventKey (Char 's') Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 1 'D')) i t
reageEvento (EventKey (Char 'd') Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 1 'R')) i t
reageEvento (EventKey (Char 'a') Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 1 'L')) i t
reageEvento (EventKey (Char 'r') Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 1 'B')) i t
reageEvento (EventKey (Char 'i') Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 2 'U')) i t
reageEvento (EventKey (Char 'k') Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 2 'D')) i t
reageEvento (EventKey (Char 'l') Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 2 'R')) i t
reageEvento (EventKey (Char 'j') Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 2 'L')) i t
reageEvento (EventKey (Char 'u') Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 2 'B')) i t 
reageEvento (EventKey (Char 'h') Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 3 'U')) i t
reageEvento (EventKey (Char 'n') Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 3 'D')) i t
reageEvento (EventKey (Char 'm') Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 3 'R')) i t
reageEvento (EventKey (Char 'b') Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 3 'L')) i t
reageEvento (EventKey (Char 'g') Down _ _) (m,p,c,b,i,t) = tudo (separateMap (move (putMapTogether (m,p,c,b)) 3 'B')) i t
reageEvento _ i = i


-- | Função que altera o estado do jogo quando o tempo avança @n@ segundos.
reageTempo :: Float -> Estado -> Estado
reageTempo n (m,p,c,b,i,t) = (m,p,c,b,i,t-n)

--Exemplo da aula:
--reageTempo f (d,p,b,t) = (d,p,b,t+f)

-- | Frame rate
fr :: Int
fr = 8

-- | Display mode
dm :: Display
dm = InWindow "New Game" (1200, 600) (0, 0)
    
-- | Função principal que invoca o jogo.
main :: IO ()
main = do brick <- (loadBMP "tijolo.bmp")
          rock <- (loadBMP "pedrafixa.bmp")
          bombinha <- (loadBMP "bomba.bmp")
          chama <- (loadBMP "flame.bmp")
          powerbomb <- (loadBMP "bombapower.bmp")
          j1 <- (loadBMP "j1.bmp")
          j2 <- (loadBMP "j2.bmp")
          j3 <- (loadBMP "j3.bmp")
          j4 <- (loadBMP "j4.bmp")
          t <- time 13
          j <- quantosJogadores
          r <- quaisComandosJ j
          play dm                                                   -- display mode
               (greyN 0.5)                                           -- côr do fundo da janela
               fr                                                   -- frame rate
               (estadoInicial t ([rock] ++ [brick] ++ [bombinha] ++ [powerbomb] ++ [chama] ++ [j1] ++ [j2] ++ [j3] ++ [j4]) [13, 2, j] ) -- estado inicial
               desenhaEstado                                        -- desenha o estado do jogo
               reageEvento                                          -- reage a um evento
               reageTempo                                           -- reage ao passar do tempo

-- | Um exemplo de uma função que escolhe automaticamente o tempo do jogo.
time :: Int -> IO Float
time n = do putStrLn ("Playing time")
            putStrLn ("Press any Key to continue")
            x <- randomRIO (1,20)
            return (13*x)

-- | Função que dá a opção de escolher quantos jogadores vão jogar.
quantosJogadores :: IO Int
quantosJogadores = do putStrLn ("Choose how many players will play from 1 to 4:")
                      x <- getLine
                      if ((read x) :: Int)>0 then certo x else quantosJogadores

certo :: String -> IO Int
certo l = if ((read l)::Int)>=1 && ((read l)::Int)<=4 then return ((read l) :: Int) 
                                        else quantosJogadores

-- | Função informativa para os jogadores saberem quais as teclas correspondentes aos comandos utilizados no jogo.
quaisComandosJ :: Int -> IO Int
quaisComandosJ q | q == 1 = do putStrLn ("Player 1 -> Up, Down, Right, Left and Space for bomb")
                               putStrLn ("Press any key to advance")
                               x <- getLine
                               return ((read x) ::Int )
                 | q == 2 = do putStrLn ("Player 1 -> Up, Down, Left, Right and Space for bomb")
                               putStrLn ("Press any key to advance")
                               getLine
                               putStrLn ("Player 2 -> Key 'W' for Up, Key 'S' for Down, Key 'D' for Right, Key 'A' for Left and 'R' for Bomb")
                               putStrLn ("Press any key to advance")
                               x <- getLine
                               return ((read x) ::Int )
                 | q == 3 = do putStrLn ("Player 1 -> Up, Down, Left, Right and Space for bomb")
                               putStrLn ("Press any key to advance")
                               getLine
                               putStrLn ("Player 2 -> Key 'W' for Up, Key 'S' for Down, Key 'D' for Right, Key 'A' for Left and Key 'R' for Bomb")
                               putStrLn ("Press any key to advance")
                               getLine
                               putStrLn ("Player 3 -> Key 'I' for Up, Key 'K' for Down, Key 'L' for Right, Key 'J' for Left and Key 'U' for Bomb")
                               putStrLn ("Press any key to advance")
                               x <- getLine
                               return ((read x) ::Int )
                 | q == 4 = do putStrLn ("Player 1 -> Up, Down, Left, Right and Space for bomb")
                               putStrLn ("Press any key to advance")
                               getLine
                               putStrLn ("Player 2 -> Key 'W' for Up, Key 'S' for Down, Key 'D' for Right, Key 'A' for Left and Key 'R' for Bomb")
                               putStrLn ("Press any key to advance")
                               getLine
                               putStrLn ("Player 3 -> Key 'I' for Up, Key 'K' for Down, Key 'L' for Right, Key 'J' for Left and Key 'U' for Bomb")
                               putStrLn ("Press any key to advance")
                               getLine
                               putStrLn ("Player 4 -> Key 'H' for Up, Key 'N' for Down, Key 'M' for Right, Key 'B' for Left and Key 'G' for Bomb")
                               putStrLn ("Press any key to advance")
                               x <- getLine
                               return ((read x) ::Int )