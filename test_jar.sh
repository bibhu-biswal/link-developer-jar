#!/bin/sh

function help () {
		echo "livepapertest - Tests the LPP client JAR using a stub java class."
		echo ""
		echo "livepapertest test - Tests the sample lpp client (with proxies)"
		echo "livepapertest test_noproxy - Tests the sample lpp client without proxies"
}


if [ $1 ]
then 
  rm -f wm.jpg qrcode.png url
	case "$1" in
  test)
	  javac -cp "./livepaper.jar" Sample.java
	  if [ $? -ne 0 ]   #--check if everything is OK
	  then
	    echo "Error during compilation"
	      exit 1
	  fi 
	  echo "Compilation done!"
	  java -Dhttps.proxyHost=web-proxy.sdd.hp.com -Dhttps.proxyPort=8088 -cp "./livepaper.jar:./" Sample > url
	  open qrcode.png
	  open wm.jpg
	  while read line
	  do			
	  echo $line
	  open $line
	  done < url
	  ;;
	test_noproxy)
	  javac -cp "./livepaper.jar" Sample.java > url
	  if [ $? -ne 0 ]  
	  then
	    echo "Error during compilation"
		  exit 1
	  fi			  
	  echo "Compilation done!"
	  java -cp "./livepaper.jar:./" Sample > url
	  open qrcode.png
	  while read line
	  do			
	  echo $line
	  open $line
	  done < url
	  open wm.jpg
	  ;;
	*)
		help;	
	 ;;
  esac
else
	help;
fi