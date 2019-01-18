% Ficha 7 - Mínimos quadrados

% 7.3 Considerem-se as seguintes funções de aproximação
%
% 1: um polinómio de grau 3 (p3(x))
% 2: M(x) = c1 + c2 cos(x) + c3 sin(x)
% 3: N(x) = c1*e^x + c2*(1/x)
% 4: O(x) = c1 + c2*x + (c3/x)
% 5: Q(x) = c1x + c2*e^x
%
% a) Calcule os coeficientes dos vários modelos (e construa-os) 
% que melhor se ajustam à função f(x) dada pela tabela seguinte, no sentido dos mínimos quadrados.
% xi | -1.00 -0.95 -0.85 -0.80 0.20 0.50 0.90
% -------------------------------------------
% fi | -1.00 -0.05  0.90  1.00 0.90 0.50 -0.3
%
% x = [-1.00 -0.95 -0.85 -0.80 0.20 0.50 0.90]
% f = [-1.00 -0.05  0.90  1.00 0.90 0.50 -0.3]
% 
% 1:  > p3 = polyfit(x,f,3)
%     R: p3(x) = 3.2764x^3 - 2.2107x^2 - 2.9271x + 1.8138
% 
% 2:  function M = MQ1(c,x)
%        M = c(1)+c(2)*cos(x)+c(3)*sin(x);
%     end
%
%     > c1 = lsqcurvefit('MQ1',[1 1 1],x,f)     - qd nao diz nada pôs vetor linha de 1
%     R: M(x) = -2.6842 + 4.0344cos(x) - 0.4432sen(x)
%
% 3: function M = MQ2(c,x)
%       M = c(1)*exp(x)+c(2)*(1./x);
%    end
%
%    > c2 = lsqcurvefit('MQ2',[1 1],x,f) ...
%
% ...
%
% 5: function Q = MQ4(c,x)
%       Q = c(1)*x + c(2)*exp(x);
%    end
% 
%    > c4 = lsqcurvefit('MQ4',[1 1 1],x,f)
%    R: Q(x) = -0.1552x + 0.1753*e^(x)
%
% b) Estime f(0.6) para cada um deles.
%
% 1: > polyval(p3, 0.6)
%    R: -0.0307
%
% 2: > polyval(c1, 0.6)
%    R: 1.0111
%
%
%
%
% 5: > polyval(c4, 0.6)
%    R: 0.0822
%
% c) Indique o resíduo para cada um dos modelos.
%
% 1: > [p3,r] = polyfit(x,f,3)
%    R: normr = 0.5473, logo, o resíduo S = r^2 = 0.5473^2 = 0.2995 
%
% 2: > [c,S] = lsqcurvefit('MQ1',[1 1 1],x,f)
%    R: S = 3.6209
%
%
%
% d) Qual dos modelos é melhor, no sentido dos mínimos quadrados? Justifique.
%    O modelo 1 é melhor que o método 2 uma vez que o valor do resíduo do
%    método 1 é menor que o valor do resíduo do método 2 (0.2995 vs 3.6209).
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%
%


