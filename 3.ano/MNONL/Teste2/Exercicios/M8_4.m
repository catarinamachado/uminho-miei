function [f] = M8_4(x,t,m)
    n = length(x);
    i = 1:2:(n-1);
    y(i) = x(i)*cos(t)-x(i+1)*sin(t);
    i = 2:2:(n-1);
    y(i) = x(i)*sin(t)+x(i+1)*cos(t);
    i=n;
    y(i) = x(i);
    i= 1:n;
    f = -sum((sin(y)).*(sin(i.*y.^2/pi)).^(2*m));
end