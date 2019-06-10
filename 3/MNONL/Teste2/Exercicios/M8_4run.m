clear all;
n=5;
i=1:2:n;
x1(i)=2;
i=2:2:n;
x1(i)=1;
t=pi/6;
m=10;
[x,f,e,o]=fminunc('M8_4',x1,[],t,m)