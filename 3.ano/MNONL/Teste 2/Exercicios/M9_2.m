function f = M9_2(x)
u = [x(1)^2+x(2)^2 (2-x(1))^2+(2-x(2))^2 2*exp(-x(1)+x(2))];
f = max(u);
end