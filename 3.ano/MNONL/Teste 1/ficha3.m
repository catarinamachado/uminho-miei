% Ficha 3 EQUAÇÕES NÃO LINEARES

% 3.1 Calcule um zero da função:
%    f(x) = e^x - x^2 - 2*x - 2
% Usando x^(1) = 2:
%
% No m-file com nome "m3_1.m":
% function f = m3_1(x)
%   f = exp(x) - x^2 - 2*x - 2;
% end
%
% No terminal:
% [x, f, exitflag, output] = fsolve('m3_1', 2)

% 3.2 Resolva a seguinte equação não linear, fornecendo as primeiras
% derivadas:
%   cos(x) - cos(3.1*x) = 0
% Considere:
% x^(1) = -1;
%
% No m-file com nome "m3_2.m":
% function [f,d] = m3_2(x)
%   f = cos(x) - cos(3.1*x);
%   if(nargout > 1)
%       d = -sin(x) + 3.1*sin(3.1*x);
%   end
% end
%
% No terminal:
%> options = optimset('Jacobian', 'on')
%> [x, f, exitflag, output] = fsolve('m3_1', -1, options)

% 3.3 Resolva o seguinte sistema de equações não lineares nas
% variáveis x1 e x2, com x1 = (0,0)^T
% { sin ((x1+x2)/2) = 2*x1
% { cos ((x1+x2)/2) = 2*x2
% 
%> function [f] = m3_3(x)
%>   f = [sin((x(1)+x(2))/2)-2*x(1), 
%>        cos((x(1)+x(2))/2)-2*x(2)]
%> end
%
% No terminal:
%> [x, f, exitflag, output] = fsolve('m3_3', [0 0])

% 3.4 Determine a solução do sistema de equações lineares, fornecendo 
%   a matriz do Jacobiano, com x1=[1 1 -1]
%       { x1 = 0
%       { x2^2 + x2 = 0
%       { e^x2 - 1 = 0
%
% function [f,J] = m3_4(x)
%   f = [x(1) x(2)^2 + x(2) exp(x(3))-1];
%   if(nargout > 1)
%       J = [1 0 0; 0 2*x(2)+1 0; 0 0 exp(x(3))];
%   end
% end
%
% No terminal:
%> options = optimset('Jacobian', 'on')
%> [x, f, exitflag, output] = fsolve('m3_4', [1 1 -1], options)