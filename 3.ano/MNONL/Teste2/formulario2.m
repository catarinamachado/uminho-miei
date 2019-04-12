% 7 Mínimos quadrados
% 7.1 polyfit e polyval
% À semelhança do que se fez para aproximar polinómios interpoladores, 
% pode usar-se a função polyfit para estimar modelos polinomiais no sentido dos mínimos quadrados. 
% Neste caso usam-se todos os pontos disponíveis. A função pode devolver, como segundo parâmetro de saída, 
% a norma do resíduo, pelo que para se obter o resíduo este valor terá de ser elevado ao quadrado.
%   [p,r] = polyfit(x,y,n)
% p devolve os coeficientes do polinómio em potências descendentes e r uma estrutura que contém 
% no seu último termo a norma do resíduo (normr). x e y são dois vetores que contêm os dados que se 
% pretendem aproximar e n é o grau do polinómio.
% Para se fazer a estimativa obtida pelo polinómio interpolador calculado num ponto, 
% ou num conjunto de pontos, usa-se a função polyval, tal como foi descrito na Subsecção 4.2.
% 7.2 lsqcurvefit
% A função lsqcurvefit resolve qualquer problema de mínimos quadrados. 
% Como tal, é adequada para problemas não polinomiais. A sua sintaxe é
%   [c,S] = lsqcurvefit(’mq’,c0,x,y).
% c é o vetor que contém os parâmetros do modelo e S é a soma dos quadrados dos resíduos. 
% mq é uma m-file que contém o modelo que se pretende estimar, c0 é o vetor com os valores 
% iniciais dos parâmetros (caso não seja dito nada deve considerar-se um vetor de uns) 
% e x e y são os vetores que contêm a tabela de dados a aproximar.

% 8 Métodos do gradiente
% 8.1 fminunc
% A função fminunc encontra o mínimo de uma função multidimensional usando métodos do gradiente. A sua sintaxe é
%   [xmin,fmin,exitflag,output] = fminunc(’func’,x0,options)
% em que os parâmetros de saída definem o minimizante em xmin, o valor mínimo da função fmin, 
% a exitflag define a forma como parou o algoritmo (a desejável é 1 - significa que o processo 
% convergiu para um ponto estacionário) e na estrutura output encontra-se informação sobre o 
% processo iterativo (número de iterações, número de cálculos da função, algoritmo usado...). 
% Os parâmetros de entrada a fornecer são a função que se pretende minimizar (caso o problema 
% seja de maximização tem de se trocar o sinal da função a priori), que deve ser escrita 
% numa m-file do tipo função. Opcionalmente, esta m-file poderá também conter as primeiras 
% e/ou segundas derivadas da função. x0 é o valor inicial e na estrutura options podem alterar-se 
% alguns dos valores que o MATLAB tem por defeito, nomeadamente definir se é pretendido usar as 
% derivadas fornecidas na m-file. Para se saber que opções podem ser alteradas e quais os 
% valores que se encontram por defeito no MATLAB, basta escrever na janela de comandos fminunc(’defaults’). 
% Para se alterar a estrutura options deve usar-se a sintaxe
%            options = optimset(’param1’,value1,’param2’,value2,...)
% Os valores que são strings devem indicar-se entre plicas (’).

% 9 Método de Nelder-Mead
% 9.1 fminsearch
% A função fminunc encontra o mínimo de uma função multidimensional usando o método de Nelder-Mead. A sua sintaxe é
%        [xmin,fmin,exitflag,output] = fminsearch(’func’,x0,options)
% em que os parâmetros de saída definem o minimizante em xmin, o valor mínimo da função fmin, 
% a exitflag define a forma como parou o algoritmo (a desejável é 1 - significa que o processo 
% convergiu para um ponto estacionário) e na estrutura output encontra-se informação sobre o processo iterativo 
% (número de iterações, número de cálculos da função, algoritmo usado...). Os parâmetros de entrada a fornecer 
% são a função que se pretende minimizar (caso o problema seja de maximização tem de se trocar o sinal 
% da função a priori), que deve ser escrita numa m-file do tipo função. x0 é o valor inicial e na estrutura 
% options podem alterar-se alguns dos valores que o MATLAB tem por defeito. Para se saber que opções podem ser 
% alteradas e quais os valores que se encontram por defeito no MATLAB, basta escrever na janela de comandos 
% fminsearch(’defaults’). Para se alterar a estrutura options deve usar-se a sintaxe
%            options = optimset(’param1’,value1,’param2’,value2,...)
% Os valores que são strings devem indicar-se entre plicas (’).