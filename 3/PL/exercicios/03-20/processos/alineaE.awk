#redirecionar resultado

BEGIN { FS = "::" }
$0 != "" { split($2, date, "[/.-]"); contaDatas[date[1]]++ }
END { for(ano in contaDatas) {print ano": "contaDatas[ano] > "processos.out"} }


#temos um ficheiro processos.out com o resultado


#desafio para casa:
#ano.html e dentro dele ter um <li>545-xico</li>
#                              <li>546-ze</li>

