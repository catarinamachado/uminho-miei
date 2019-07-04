
#gawk -f 2_1alineaB.awk pltesteER.txt

# Contar todas as linhas que contenham marcas HTML (s´o de abertura e depois de abertura e
fecho), imprimindo a respetiva linha.

BEGIN { FS = " "; RS="\n+"; c=0;
        print "\n>>> Iniciar filtragem <<<\n"
      }

$0 ~/<[a-zA-Z][^>]*>/       {c++;}
$0 ~/<\/[a-zA-Z][^>]*>/     {c++;}

END {
    print c;
    print "\n>>> Terminar filtragem <<<\n"
    }


#isto tá a dar erro nao sei PORQUÊ!
