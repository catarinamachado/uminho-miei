module Main where

import Data.Char (isDigit)
import System.Environment
import Text.Read
import Data.Maybe
import Tarefa6_li1g172 (bot)

main :: IO ()
main = do
    a <- getArgs
    let player = readMaybe (a !! 0)
    let ticks = readMaybe (a !! 1)
    w <- getContents
    if isJust player && isJust ticks
        then putStr $ show $ bot (lines w) (fromJust player) (fromJust ticks)
        else putStrLn "Parâmetros inválidos"
