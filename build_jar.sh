#!/bin/bash

mkdir build

CLASSPATH=lib/commons-text.jar:lib/commons-lang3.jar:lib/javax.xml.bind.jar:src/

# Compile
javac --release 8 -cp $CLASSPATH -d ./build/ src/*.java
javac --release 8 -d ./build/ src/layout/*.java

cd build
jar cfm ../chat.jar ../Manifest.txt *.class layout
cd ..

# Make jar
mkdir jar-build
mkdir jar-build/main
mkdir jar-build/lib
cp ./lib/*.jar jar-build/lib/
mv chat.jar ./jar-build/main

# Extract jar
unzip ./lib/one-jar-appgen-0.97.jar -d ./jar-build/

cd jar-build

touch MANIFEST.MF
echo 'Manifest-Version: 1.0' >> MANIFEST.MF
echo 'Main-class: com.simontuffs.onejar.Boot' >> MANIFEST.MF
echo 'One-Jar-Main-Class: Main' >> MANIFEST.MF

# Actually build jar
jar -cvfm ../jar.jar MANIFEST.MF .

# Cleanup
cd ..
rm -r build
rm -r jar-build
