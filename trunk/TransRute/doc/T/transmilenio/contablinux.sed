:loop
N
s/\n/\t/
#s/[ 	]//g
s/[,]//g
$!b loop
p
d
