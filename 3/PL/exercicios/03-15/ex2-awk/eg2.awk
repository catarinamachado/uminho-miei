BEGIN{FS=":"}
NR >= 2 && NR <= 4  {
        for(i=1; i <= NF; i++)
            {print $i};
        print NF}
/jcr/,/prh/ {print $1 " - " $(NF-1)}
END {print "total de linhas = " NR}
