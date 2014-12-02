#!/bin/sh

rm dep/com/hp/*
echo "Compiling LivePaper.java"
javac -cp "./dep/" lib/LivePaper.java
if [ $? -ne 0 ]   #--check if everything is OK
then
  echo "Error during compilation"
  exit 1
fi
echo "Compiled LivePaper without errors!"
echo "Creating JAR - existing JAR renamed to livepaper_orig.jar"
mv lib/*.class dep/com/hp
mv livepaper.jar livepaper_orig.jar
cd dep
jar -cf livepaper.jar *
mv livepaper.jar ..
cd ..
echo "Done creating JAR"
echo "Running test_jar.sh script"
./test_jar.sh test_noproxy
