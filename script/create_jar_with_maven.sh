#!/usr/bin/env bash

maven_missing() {
  echo "ERROR: maven not found!"
  echo "       (install maven with \"brew install maven\")"
  exit 1
}

type mvn > /dev/null || maven_missing
java_version_maven_sees=$(mvn --version | grep 'Java version' | sed -e 's|, .*||' -e 's|.* ||')
major_minor=$(echo $java_version_maven_sees | sed -e 's|\([0-9][0-9]*\)\.\([0-9][0-9]*\)\..*|\1\2|')

if [ "$major_minor" -lt 18 ]; then
  echo "Maven is not seeing Java version 1.8 or greater!"
  echo "You need to set JAVA_HOME to a Java version of 1.8 or greater"
  echo "(maybe you should run \"source ./set_JAVA_HOME\" first)"
  exit 1
fi

mvn -version | grep 'Java version'

echo + mvn package
       mvn package
