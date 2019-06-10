clear all;
options = optimset('Display', 'iter');
[x,f,e,o] = fminsearch('M9_1',[1 1],options)
