#!/bin/bash
  	contador=1
 	inicio=--cookies=on --keep-session-cookies --save-cookies=cookie.txt 'http://transmilenio.surumbo.com/index2.php3?domain=transmilenio.surumbo.com&alias=&frames=0&referer='
	carpeta='html'
 	parametros='--cookies=on --load-cookies=cookie.txt --keep-session-cookies'
	ruta=http://transmilenio.surumbo.com/sitios/surumbo/scripts/RUTA%20X%20ST%20-%20reporte%20de%20ruta%20-%20S144.php?id_respuesta=
	echo $inicio
	wget $inicio
	convertir='-f ISO-8859-1 -t UTF-8'
         while [  $contador -lt 106 ]; do
             echo Descargando: $parametros $ruta$contador -O $carpeta/$contador.html
	     wget $parametros $ruta$contador -O $carpeta/$contador.htm
	     iconv $convertir $carpeta/$contador.htm > $carpeta/$contador.html
  	     sed -f script.sed  $carpeta/$contador.html > $carpeta/$contador.txt
	     sed -f contablinux.sed $carpeta/$contador.txt > $carpeta/$contador.tab.txt
	     cat $carpeta/tmp $carpeta/$contador.tab.txt > $carpeta/tmp2
	     cat $carpeta/tmp2 > $carpeta/tmp
       	     rm $carpeta/$contador.htm
	     let contador=contador+1 
         done
	 cat $carpeta/tmp > todo.txt
	 rm $carpeta/tmp $carpeta/tmp2


