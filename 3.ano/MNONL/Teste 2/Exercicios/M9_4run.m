clear all;
n=5;i=1:n;x1=i-(n/2 + 0.5);
op = optimset('MaxFunEvals',5000);
[x,f,e,o] = fminsearch('M9_4', x1, op)