#!/bin/sh

RED="\033[1;31m"
CYAN="\033[1;36m"
RESET="\033[0m"

if [ "$HTTP_PROXY" ];then
  proxy_opts="${HTTP_PROXY#http://}"
  proxy_opts="-Dhttps.proxyHost=${proxy_opts%:*} -Dhttps.proxyPort=${proxy_opts##*:}"
  echo "${CYAN}Defining https.proxyHost/Port for java...${RESET}"
fi

echo "${CYAN}Checking version...${RESET}"
ver=$(grep '<version>' pom.xml | head -1 | sed -e 's:[^0-9]*\([0-9]\):\1:' -e 's:<.*::')
jar=$PWD/target/linkdeveloper-$ver.jar
cp="$HOME/.m2/repository/com/sun/jersey/jersey-client/1.18.3/jersey-client-1.18.3.jar"
cp="$cp:$HOME/.m2/repository/com/sun/jersey/jersey-core/1.18.3/jersey-core-1.18.3.jar"
cp="$cp:$HOME/.m2/repository/io/fastjson/boon/0.24/boon-0.24.jar"
jar_version=$(java $proxy_opts -cp $cp:$jar com.hp.linkdeveloper.Version)
if [ "$jar_version" != "$ver" ]; then
  echo "${RED}ERROR: version in pom.xml ($ver) does not match${RESET}"
  echo "${RED}       version in the jar (Version.VERSION: ${jar_version})!${RESET}"; exit 1
  exit 1
fi

echo "${CYAN}Defining Network Timeout options (10000 millisecond)${RESET}"
timeout_opts="-DPROPERTY_READ_TIMEOUT=10000 -DPROPERTY_CONNECT_TIMEOUT=10000"
echo "${CYAN}Running Basic Test [com.hp.linkdeveloper.example.LinkDeveloperExample.main()]...${RESET}"
java $timeout_opts $proxy_opts -cp $cp:$jar com.hp.linkdeveloper.example.LinkDeveloperExample || exit $?
