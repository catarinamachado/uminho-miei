function [f] = M8_5(x)
    n=length(x);
    i=1:n;
    f=1+1/4000*sum(x.^2)-prod(cos(x./sqrt(i)));
end