#Quero saber os anos em que há registos (pode ter repetidos) ; apanhar o ano e imprimi-lo
#split: 1. argumento: o que eu quero partir;
#       2. argumento: onde vou guardar os pedaços partidos;
#       3. argumento: o cenas que separa
#normalmente as datas separam-se através de um traço ou de um ponto
# print date[1] -> só quero imprimir o ano

#awk -f alineaA.awk processos.txt | less

BEGIN   { FS = "::" }
        { split($2, date, "[/.-]"); print date[1]}
END     { print "#"NR } # para ver o numero de registos que temos


#Agora quero saber quantos registos houve em cada ano
#alineaB.awk
