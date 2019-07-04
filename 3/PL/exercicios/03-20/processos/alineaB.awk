#Agora quero saber quantos registos houve em cada ano
#o contaDatas tem o truque do indice ser a data,
# daí conseguirmos ter o numero de vezes que apareceu cada data sem esforço praticamente nenhum

# awk -f alineaB.awk processos.txt | less

BEGIN { FS = "::" }
{ split($2, date, "[/.-]"); contaDatas[date[1]]++ }
END { for(ano in contaDatas) {print ano": "contaDatas[ano]} }



#mas isto dá um :10 no inicio que significa que existem 10 linhas em branco, ele tb as está a contar

#vamos melhorar isto
#alineaC.awk
