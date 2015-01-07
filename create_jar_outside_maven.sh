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
  mkdir -p tmp_jars
  if [ -f tmp_jars/$jar.jar -a ! -s tmp_jars/$jar.jar ]; then
    rm tmp_jars/$jar.jar # previous download problems can leave an empty file... remove it
  fi
  if [ ! -f tmp_jars/$jar.jar ]; then
    echo "${CYAN}downloading $jar.jar...${RESET}"
    if [ "$bin" != "" ]; then
      echo + curl -s $url/${bin} \| gunzip \| tar -xvf - --include '*'$jar.jar
             curl -s $url/${bin}  | gunzip  | tar -xvf - --include '*'$jar.jar
      mv $jar/$jar.jar tmp_jars/ && rmdir $jar
    else
      echo + curl -s $url/$jar.jar \> tmp_jars/$jar.jar
             curl -s $url/$jar.jar  > tmp_jars/$jar.jar
    fi
  fi
  tar -tvf tmp_jars/$jar.jar | grep MANIFEST > /dev/null
  if [ $? = 1 ];then
  	echo "${RED}ERROR: unable to obtain jar \"$jar\"!${RESET}"
  	exit 1
  fi
  CLASSPATH="$CLASSPATH:tmp_jars/${jar}.jar"
}

export CLASSPATH="$HOME/src/live_paper_jar/livepaper.jar"
usejar "boon-0.24"             "http://central.maven.org/maven2/io/fastjson/boon/0.24"
usejar "jersey-client-1.18.3"  "http://central.maven.org/maven2/com/sun/jersey/jersey-client/1.18.3"
usejar "jersey-core-1.18.3"    "http://central.maven.org/maven2/com/sun/jersey/jersey-core/1.18.3"

echo "${CYAN}Compiling LivePaper.java...${RESET}"
javac src/main/java/com/hp/livepaper/LivePaper.java || exit 1

echo "${CYAN}Creating LivePaper.jar...${RESET}"
ver=$(grep '<version>' pom.xml | head -1 | sed -e 's:[^0-9]*\([0-9]\):\1:' -e 's:<.*::')
jar -cf target/livepaper-$ver.jar $(find src | grep LivePaper.class$) || exit 1
