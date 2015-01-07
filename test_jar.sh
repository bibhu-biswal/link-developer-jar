#!/bin/sh

RED="\033[1;31m"
CYAN="\033[1;36m"
RESET="\033[0m"

ver=0.0.6
jar=$PWD/target/livepaper-$ver.jar
echo "${CYAN}Running Basic Test [com.hp.livepaper.LivePaperExample.main()]...${RESET}"
cp="$HOME/.m2/repository/com/sun/jersey/jersey-client/1.18.3/jersey-client-1.18.3.jar"
cp="$cp:$HOME/.m2/repository/com/sun/jersey/jersey-core/1.18.3/jersey-core-1.18.3.jar"
cp="$cp:$HOME/.m2/repository/io/fastjson/boon/0.24/boon-0.24.jar"
java -cp $cp:$jar com.hp.livepaper.LivePaperExample || exit $?

echo
echo "${CYAN}Running Additional Tests [com.hp.livepaper.LivePaperExample.main()]...${RESET}"
cd "../playground/lpapi_exemplar_java/"
if [ $? != 0 ]; then
  echo "ERROR: cannot find directory holding test script!"; exit 1
fi
cp -p $jar jars/
./TestLinkAPI.sh
