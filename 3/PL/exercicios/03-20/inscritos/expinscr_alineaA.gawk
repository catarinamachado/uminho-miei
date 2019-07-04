BEGIN { FS = "\t" }
NR > 6 && NR < 16 { print $1"\t"$2}
END {  }
