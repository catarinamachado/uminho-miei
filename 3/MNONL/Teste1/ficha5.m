% Ficha 5
%
% 5.1 Considerando a função f(x) dada pela tabela
%
% xi| 5.0 5.1 5.2 5.3 5.4 5.5 5.6 5.7 5.8 5.9 6.0
%   ------------------------------------------------------
% fi| 0.0639 0.0800 0.0988 0.1203 0.1442 0.1714 0.2010 0.2331 0.2673 0.3036 0.3414
%
% qual o valor aproximado da função no ponto x=5.45, bem como o
% segmento da spline correspondente desse ponto
%
% NAO CONHECEMOS A FUNÇÃO
%
% a) usando uma spline cúbica sem considerar derivadas nos extremos:
%> x = [todos os x da tabela]
%> f = [todos os f da tabela]
%> s = spline(x,f)
%
%> s.coefs (escolher a linha 5 pq corresponde ao 5º segmento o intervalo de 5.4 a 5.5)
% Como 5.45 pertence a 5.4 a 5.5 escolhemos o valor inferior do intervalo
% -> 5.4
% Assim, temos o segmento spline:
% s3^5 = -0.3521*(x-5.4)^3 + 0.2004*(x-5.4)^2 + 0.2555*(x-5.4) + 0.1442
% Valor:
% s3 = spline(x,f,5.45)
%    = 0.1574
% b) usando uma spline cúbica completa:
%
%>   f0 = (0.0639-0.08)/(5-5.1)
%>   fn = (0.3036-0.3414)/(5.9-6.0)
%
%>   x = [todos exceto o 5.1 e o 5.9]!!!
%>   f = [todos exceto o 0.0639 e o 0.3036]!!!
%   
%   Agora, o 5.45 já faz parte do 4º segmento!
%>   s=spline(x,[f0 f fn])
%>   s.coefs
%>   escolhendo então a linha 4:
%    s3^4 = -0.3656(x-5.4)^3 + 0.2034(x-5.4)^2 + 0.2553(x-5.4) + 0.1442
%
%    s3 = spline(x, [f0 f fn], 5.45)
%       = 0.1574
%
%
% 5.2 De uma tabela de logaritmos obteve-se o seguinte quadro de valores:
%      xi | 1 1.5 2 3 3.5
%         ---------------------------------
%   ln(xi)| 0 0.4055 0.6931 1.0986 1.2528
%
%   Usando uma função spline cúbica completa, calcule uma aproximação
%  a ln(2.5). Escreva o segmento da spline para estimar este valor.
%
%   SABEMOS A FUNÇÃO!!!! LOGO, NÃO PRECISAMOS DE TIRAR PONTOS.
%
%>   f0 = ln(1)'   = 1/1   = 1
%>   fn = ln(3.5)' = 1/3.5 = 0.2857
%
%>   x = [1 1.5 2 3 3.5]                 TODOS
%>   f = [0 0.4055 0.6931 1.0986 1.2528] TODOS
%
%>   s=spline(x, [f0 f fn])
%
%>   s.coefs e escolher a linha 3!   
%
%    s3^3 = 0.0225(x-2)^3 - 0.1181(x-2)^2 + 0.5011(x-2) + 0.6931
%    s3 = spline(x, [f0 f fn], 2.5)
%       = 0.9169

