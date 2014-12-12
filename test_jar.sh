#!/bin/sh

cd "../playground/lpapi_exemplar_java/"
if [ $? != 0 ]; then
  echo "ERROR: cannot find directory holding test script!"; exit 1
fi

./TestLinkAPI.sh
