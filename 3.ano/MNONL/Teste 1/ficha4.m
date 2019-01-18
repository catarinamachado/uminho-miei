% Ficha 4
%
% Grau 2 -> 3 pontos
%
% 4.1 Dada a tabela de valores de uma função f(x)
% x1 | 5.0     5.1   5.2    5.3    5.4    5.5    5.6    5.7    5.8    5.9    6.0
%    ----------------------------------------------------------------------------
% x2 | 0.0639 0.0800 0.0988 0.1203 0.1442 0.1714 0.2010 0.2330 0.2673 0.3036 0.3414
%
% a) Apresente o polinómio interpolador de grau 2
% 
% No terminal:
%> x = [5.3 5.4 5.5]
%> f = [0.1203 0.1442 0.1714]
%> p2 = polyfit(x, f, 2)
%
% O resultado foi:
%   p2 = 0.1650   -1.5265    3.5759
% Logo, o polinómio é: 0.1650x^2 - 1.5265x + 3.5759
%
% b) Com base no polinómio anterior, estime f(5.44)
%> v = polyval(p2, 5.44)
%
% Para comprovar que o polinómio passa pelos 3 pontos:
%> xx = 5.3:0.001:5.5
%> ff = polyval(p2,xx)
%> plot(x, f, 'or', xx, ff, 'k')
%
% 
%