% FICHA 6
%
% 6.1 NÃO CONHEÇO A FUNÇÃO
%     Dada a tabela de valores da função f(x)
%
%     xi | 0.0 0.5 1.0 1.5 2.0 2.5 3.0 4.0 5.0
%        -----------------------------------------------------
%     fi | -4271 -2522 -499 1795 4358 7187 10279 13633 17247
%
%     Calcule a melhor aproximação ao integral S 0.0 a 5.0 f(x) dx
%    usando toda a informação da tabela
%
%>      x = [0.0 0.5 1.0 1.5 2.0 2.5 3.0 4.0 5.0]    
%>      f = [-4271 -2522 -499 1795 4358 7187 10279 13633 17247]
%
%>      trapz(x,f) 
%           = 3.4058e+04
%
% 6.2 SEI A FUNÇÃO
%     Calcule uma aproximação ao integral 
%           I  = S 0 a 1 (4/(1+x^2)) dx
%
%>      f = @(x) 4./(1 + x.^2)       
%>      integral(f,0,1,'AbsTol', 1e-20, 'RelTol', 1e-20)
%         = 3.1416
%
%       NÃO ESQUECER DO . antes de cada cena (/ e ^)
%
%
