% FICHA 9 - fminsearch
%
%
% 9.1 Resolva o problema
%
%     min f(x1,x2)
%
%     com f(x1,x2) = max{|x1|,|x2 − 1|}. Como aproximação inicial considere o ponto (1, 1).
%
%   function f = M9_1(x)
%       u = [abs(x(1)) abs(x(2)-1)];  -- podemos ter os argumentos que
%       quisermos 
%       f = max(u); -- max de funcao é diferente do max de otimizacao!
%       atencao, se for de max tinhamos que trocar os sinais
%   end
%    
% -> m-file M9_1run.m
% 
% R: f = 3.3720e-05
%
%
%
% 9.2 Considere o seguinte problema não diferenciável
%
% minf(x) ≡ max{x1^2 +x2^4, (2−x1)^2 +(2−x2)^2, 2e^(−x1+x2)}.
%
% A partir da aproximação inicial x = (1, −0.1)T , calcule a solução usando
% o método mais adequado. Repita o processo com a seguinte aproximação inicial x = (2, 2)T . 
% Resolva novamente o problema a partir de x = (−10, −10)T .
% Com qual das aproximações iniciais o processo exigiu menos cálculos da função objectivo?
% 
% -> m-file M9_2run.m
%
% x = (1, −0.1)T :  funcCount = 145
% x = (2, 2)T :     funcCount = 112 <---- esta foi a que exigiu menos calculos
% x = (−10, −10)T : funcCount = 124
% o funcCount avalia a eficiência do método
%
%
% 9.3 Considere o seguinte problema não diferenciável
%     
%   min f(x) ≡ n*max (xi) − (somatorio de i = 1..n (xi))
%
% Para n = 2 e a partir da aproximação inicial xi = i−(n/2 +0.5), i = 1,...,n, calcule
% a solução.
% Repita a resolução considerando agora n = 5 e TolX= 10−20. 
% Resolva ainda acrescentando a opção MaxFunEvals=10000. 
% Acrescente ainda a opção MaxIter=10000. Comente os resultados.
%
% -> m-file: M9_3
% -> m-file: M9_3run1
%  R: f = 6.4289e-12  && x = -0.7164   -0.7164
%
% -> m-file: M9_3run2  --> deu e = 0, logo, nao converge
%    para os parametros definidos, matlav dá mensagem a dizer o porquê:
%    número de calculos de funCount máximo foi atingido.
%    a solução é alterar o op para: 
%    op = optimset('TolX',1e-20,'MaxFunEvals',10000)  --> m-file: M9_3run3
%    MAS agora e = 0 porque o max de iteracoes foi atingido! 
%    Anyway, o resultado é:
%  R: x = 0.1272    0.0083    0.0184    0.0170    0.1272
%
%
%
%
%
%
% 9.4 Considere o seguinte problema não diferenciável
%     
%  min f(x) ≡ N GRANDE (i in 1..n) de xi - (min xi)
% 
% Para n = 2 e a partir da aproximação inicial xi = i−(n/2 +0.5), i = 1,...,n, calcule
% a solução
% Repita a resolução considerando agora n = 5 e MaxFunEvals= 5000.
%
% -> m-file M9_4 e M9_4run
%
%
%
%
%
%
%

% 9.5 Considere o seguinte problema não diferenciável
%
% minf(x)≡ max{x1^2 + x2^2, x1^2 + x2^2 + ω(−4*x1 − x2 + 4), x1^2 + x2^2 +ω(−x1 − 2*x2 + 6)}.
%
% 
% A partir da aproximação inicial x = (−1, 5)T , calcule a solução, 
% usando o método mais adequado e considerando ω = 500. 
% A partir da mesma aproximação inicial, volte a resolver o problema, mas agora fazendo ω = 1000.
% Repita mais uma vez considerando ω = 1500.
% Para que valor de ω, o processo iterativo é mais eficiente?
%
%  -> m-file M9_5 e M9_5run.
% R: o mais eficiente é funCount = 188 e w = 500.
%
%

% 9.6 Considere o seguinte problema não diferenciável
%
%       min f(x) ≡ max |ui(x)|
%
% em que
%       ui(x)= x4 −(x1*ti^2 +x2*ti +x3)^2 − raiz quadrada de ti
%
% para i entre 1 e 21
%
% A partir da aproximação inicial xi = 1, i = 1,...,4, calcule a solução, usando o
% método mais adequado e os seguintes valores ti = 0.25+0.75(i−1)/20, i = 1, . . . , 21.
% Repita o processo mas agora considere os seguintes parâmetros ti = 0.2i, i = 1,...,21.
%
% > [x,f,e,o] = fminsearch('M9_6', ones(1,4)) 
% R: O mínimo é 0.0187 (f). E depois alterar o t...
%    

