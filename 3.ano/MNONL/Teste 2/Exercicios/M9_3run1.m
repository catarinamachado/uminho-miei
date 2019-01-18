clear all;
n=2;i=1:n;
x1=i-(n/2 + 0.5);
[x,f,e,o]=fminsearch('M9_3',x1)