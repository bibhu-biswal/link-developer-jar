#!/bin/sh

RED="\033[1;31m"
CYAN="\033[1;36m"
RESET="\033[0m"

echo "${CYAN}Building JAR with Maven...${RESET}"
mvn package

echo "${CYAN}Running Basic Test [com.hp.LivePaperExample.main()]...${RESET}"
cp="$HOME/.m2/repository/com/sun/jersey/jersey-client/1.18.3/jersey-client-1.18.3.jar"
cp="$cp:$HOME/.m2/repository/com/sun/jersey/jersey-core/1.18.3/jersey-core-1.18.3.jar"
cp="$cp:$HOME/.m2/repository/io/fastjson/boon/0.24/boon-0.24.jar"
java -cp $cp:./target/livepaper-0.0.5.jar com.hp.LivePaperExample

echo "${CYAN}Running Additional Test...${RESET}"
./test_jar.sh
