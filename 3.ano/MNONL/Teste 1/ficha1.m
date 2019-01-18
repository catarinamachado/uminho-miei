% Ficha 1

%> u = [1 2 3]
%> v = u'
%> v = 1:10          -> vetor linha com todos os nrs <= 10
%> v = 2:2:12        -> inicio:espacamento:fim
%> A = [2 2 3; 4 5 6; 7 8 9]

%> B = A(2:3, 1:2)   -> linhas 2:3 e colunas 1:2
%> C = A(:, 1:2)     -> todas as linhas, colunas 1:2
%> D = [A; 4 4 4]    -> acrescenta uma linha
%> A([1 2], 1) = 2*A([1 2], 1)
%> A([1 3], 1) = A([3 1], 1)

%> eye(5) ou eye(5,5) -> matriz identidade 5x5
%> rand(3)            -> matriz 3x3 com elem. aleatorios de 0 a 1
%> 2*rand(4,3)-1      -> matriz 4x3 com elem. aleatorios de -1 a 1
%> zeros(2,3)         -> matriz 2x3 nula
%> ones(2)            -> matrix com todos os elementos 1
%> 10+zeros(10)       -> matriz 10x10 elementos todos 10 
%> 10*ones(10)        -> matriz 10x10 elementos todos 10
%> diag(diag(A))      -> matriz com os elementos da diagonal da 
%                     matriz A e os restantes iguais a 0

% format short vs format long

%> Escreva um m-file que lê dois números e escreve a sua soma e 
% o seu produto: (acho q nome fich = nome func)
%   function[s,p] = soma_produto(x,y)
%       s = x+y;
%       p = x*y;
%   end
% no terminal: [soma, produto] = soma_produto(1,2)


%> Escreva um programa que lê uma sequência de n números e 
% escreve a sua soma e o seu produto.
%   function[s,p] = soma_produto(x,y)
%       s = sum(x);
%       p = prod(x);
%   end
%

%> Escreva um programa que lê uma sequência de n números e 
% escreve o maior deles.
%   function[m] = maximo(x)
%       m = max(x);
%   end
%

