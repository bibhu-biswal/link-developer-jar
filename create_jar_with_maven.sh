#!/usr/bin/env bash

maven_missing() {
  echo "ERROR: maven not found!"
  echo "       (install maven with \"brew install maven\")"
  exit 1
}

type mvn > /dev/null 2>&1 || maven_missing

echo + mvn package
       mvn package
