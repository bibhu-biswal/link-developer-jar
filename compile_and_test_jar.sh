#!/bin/sh

rm -f dep/com/hp/*
echo "------------------------------------"
echo "Compiling LivePaper.java..."
javac -cp "./dep/" lib/LivePaper.java || exit 1

echo "------------------------------------"
echo "Creating JAR..."
mv lib/*.class dep/com/hp
cd dep
jar -cf livepaper.jar * || exit 1
mv livepaper.jar ..
cd ..

echo "------------------------------------"
echo "Running tests..."
./test_jar.sh