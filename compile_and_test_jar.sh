#!/bin/sh

RED="\033[1;31m"
CYAN="\033[1;36m"
RESET="\033[0m"

usejar() {
  bin=""
  jar="${1%.jar}"
  url="$2"
  case $jar in
    *-bin.tar.gz)
      bin=$jar
      jar=${jar%-bin.tar.gz}
      ;;
  esac
  mkdir -p jars
  if [ -f jars/$jar.jar -a ! -s jars/$jar.jar ]; then
    rm jars/$jar.jar # previous download problems can leave an empty file... remove it
  fi
  if [ ! -f jars/$jar.jar ]; then
    echo "${CYAN}downloading $jar.jar...${RESET}"
    if [ "$bin" != "" ]; then
     echo + curl -s $url/${bin} \| gunzip \| tar -xvf - --include '*'$jar.jar
             curl -s $url/${bin}  | gunzip  | tar -xvf - --include '*'$jar.jar
      mv $jar/$jar.jar jars/ && rmdir $jar
    else
     echo + curl -s $url/$jar.jar \> jars/$jar.jar
             curl -s $url/$jar.jar  > jars/$jar.jar
    fi
  fi
  tar -tvf jars/$jar.jar | grep MANIFEST > /dev/null
  if [ $? = 1 ];then
  	echo "${RED}ERROR: unable to obtain jar \"$jar\"!${RESET}"
  	exit 1
  fi
  CLASSPATH="$CLASSPATH:jars/${jar}.jar"
}

export CLASSPATH="$HOME/src/lpp_client_java/livepaper.jar"
usejar "boon-0.24"             "http://central.maven.org/maven2/io/fastjson/boon/0.24"
usejar "jersey-client-1.18.3"  "http://central.maven.org/maven2/com/sun/jersey/jersey-client/1.18.3"
usejar "jersey-core-1.18.3"    "http://central.maven.org/maven2/com/sun/jersey/jersey-core/1.18.3"

echo "${CYAN}Compiling LivePaper.java...${RESET}"
javac com/hp/LivePaper.java || exit 1

echo "${CYAN}Creating LivePaper.jar...${RESET}"
jar -cf livepaper.jar com || exit 1

echo "${CYAN}Testing the jar...${RESET}"
./test_jar.sh
