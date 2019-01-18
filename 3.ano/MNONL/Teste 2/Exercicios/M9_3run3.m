op = optimset('TolX',1e-20,'MaxFunEvals',10000);
n=5;i=1:n;x1=i-(n/2 + 0.5);
[x,f,e,o] = fminsearch('M9_3',x1,op)