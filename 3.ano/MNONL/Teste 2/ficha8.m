% FICHA 8 - fminunc
%
% 8.1 Resolva o problema Aluffi-Pentini,
%        min f(x)≡0.25x1^4 −0.5x1^2 + 0.1x1 + 0.5x2^2,
%   considerando o valor inicial (−1, 0.5),
% 
% a) usando o método quasi-Newton sem fornecer as primeiras derivadas da
%    função objectivo:
% 
%    function f = M8_1(x)
%       f = 0.25*x(1)^4 - 0.5*x(1)^2+0.1*x(1)+0.5*x(2)^2;
%    end
%
%   >[x,f,exitflag,output] = fminunc('M8_1',[-1 0.5])
%     R:  f = -0.3524
%
%
%
%
% 8.2 No planeamento da produção de dois produtos, uma determinada companhia espera obter lucros iguais a P:
%     P(x1,x2)=α1(1−e^(−β1x1))+α2(1−e^(−β2x2))+α3(1−e^(−β3x1x2))−x1 −x2,
%     em que x1 é a quantia gasta para produzir e promover o produto 1, 
%     x2 é a quantia gasta para produzir e promover o produto 2 e os αi e βi são constantes definidas. 
%     P, x1 e x2 estão em unidades de 10^5 euros. Calcule o lucro máximo para as seguintes condições:
%
%           α1 = 3, α2 = 4, α3 = 1, β1 = 1.2, β2 = 1.5, β3 = 1.
%     
% a) Resolva o problema usando o método quasi-Newton sem fornecer as primeiras
%    derivadas da função objectivo. Considere a aproximação inicial (1, 1).
%
%   function f = M8_2 (x)
%       a  = [3 4 1]; b = [1.2 1.5 1];
%       P = a(1)*(1-exp(-b(1)*x(1)))+a(2)*(1-exp(-b(2)*x(2)))+a(3)*(1-exp(-b(3)*x(1)*x(2)))-x(1)-x(2);
%       f = -P;
%    end
%    
%    > [x,f,exitflag,output] = fminunc('M8_2',[1 1])
%    R: f = -4.0189, logo, o lucro máximo é 4.0189l.
%    (firstorderopt é o nome do gradiente no final da última iteração ?? nao sei o que a prof quis dizer com isto)
%
%
%
% 8.3 Suponha que pretendia representar um número positivo A na forma de um produto de 
% quatro factores positivos x1, x2, x3, x4. Para A = 2401, 
% determine esses factores de tal forma que a sua soma seja a menor possível.
% 
% Formule o problema como um problema de optimização sem restrições em função das três variáveis x1, x2 e x3.
%
% A partir da aproximação inicial (x1, x2, x3)(1) = (6, 7, 5), ^
% use o método quasi-Newton (com fórmula DFP), para calcular esses factores. 
% Na paragem do processo iterativo use TolX=TolFun=0.0001.
%  
% 
% Calculos auxiliares:
%
% min x1 + x2 + x3 + x4 = x1 + x2 + x3 + (2401/x1*x2*x3)
% s.a:
%     x1*x2*x3*x4 = 2401 <-> x4 = 2401/x1*x2*x3
%
% --
%
% function [f] = M8_3(x)
%    f=x(1)+x(2)+x(3)+2401/(x(1)*x(2)*x(3));
% end
%
% > op = optimset('TolX',0.0001,'TolFun',0.0001,'HessUpdate','DFP')
% > [x,f,exitflag,output] = fminunc('M8_3',[6 7 5],op)
% R: f = 28
% 
%
% 8.4 Resolva o problema Epistatic Michalewicz
%        min x f(x) = - (somatorio de i = 1 até n: sen(yi)*((sen(i*yi^2)/pi))^2*m) 
%
%             { x(i)*cos(teta) - x(i+1)*sen(teta), i = 1,3,5,... < n
%        yi = { x(i)*sen(teta) + x(i+1)*cos(teta), i = 2,4,6,... < n
%             { x(i)                             , i = n
% 
%     pelo método quasi-Newton (sem fornecer derivadas) para n = 5 e para n = 10.
%
% Considere θ = pi/6, m = 10 e o valor inicial x^(1) = { 2, i = 1,3,5,...,<= n
%                                                      { 1, i = 2,4,6,...,<= n
% Resposta:
%  -> m-file M8_4.m
%  -> m-file M8_4run.m
%
%  R: f = -0.9591
%
%
%
% 8.5 Considere o problema Griewank
%
% min x f(x) = 1 + 1/4000 * (somatorio de i = 1 até n ( xi^2 - (N GRANDE DE I = 1 ATÉ N (cos(xi/raiz de i)))))
%
% Resolva-o pelo método quasi-Newton com fórmula DFP para n = 10 e n = 25.
% Considere o valor inicial x^(1) = (1, 1, . . . , 1)^T .
%
%
% -> m-file M8_5.m
% -> m-file M8_5run.m
%
% R: f = 2.3371e-12 (mínimo)
% os x = ... são os minimizantes! no nosso caso como fizemos para n = 10
% são 10.

