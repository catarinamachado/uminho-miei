
#gawk -f output.awk ../03-20/processos/processos.txt

BEGIN { print "Início de Filtragem"; FS = "::"; RS="\n+"; }

    {
        split($2, data, "[-/.#]");
        htable[data[1]]++;
        if(htable[data[1]] == 1)
            print "<h1>Lista de Processos Registados no ano " data[1] "</h1>" > data[1]".html"
        print "<p>" $3 "</p>" >> data[1]".html" }

END {
        print "<ul>" > "index.html";
        for(ano in htable) {
            print "<li><a href=\"" ano ".html\">" ano "</li></a>" >> "index.html";
            print "<h3><p>Número de entradas: " htable[ano] "</p></h3>" >> ano".html";
        }
        print "</ul>" >> "index.html";
        print "Fim de Filtragem";
    }

