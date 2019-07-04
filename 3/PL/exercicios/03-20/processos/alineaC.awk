#mas isto dá um :10 no inicio que significa que existem 10 linhas em branco,
#ele tb as está a contar
#vamos melhorar isto

#solução, adicionar: RS = "\n+"

BEGIN { FS = "::"; RS = "\n+" }
{ split($2, date, "[/.-]"); contaDatas[date[1]]++ }
END { for(ano in contaDatas) {print ano": "contaDatas[ano]} }

# outra solução alternativa no ficheiro alineaD.awk
