BEGIN { FS = "\t" }
$10 ~ /[Ii][Nn][Dd][Ii][Vv][Ii][Dd][Uu][Aa][Ll]/ && $12 ~ /Valongo/ { print $1":"$10 }
