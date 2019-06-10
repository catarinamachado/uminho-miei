clear all;
op=optimset('HessUpdate','DFP');
n=10;
x1=ones(1,n);
[x,f,e,o]=fminunc('M8_5',x1,op)