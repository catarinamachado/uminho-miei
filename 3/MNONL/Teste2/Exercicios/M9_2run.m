clear all;
options = optimset('Display', 'iter');
[x,f,e,o] = fminsearch('M9_2', [-10 -10])