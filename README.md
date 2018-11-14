# DD1346-project
oopk17-project

Compile code with 
```
javac -cp .:commons-text.jar:javax.xml.bind.jar *.java
```

Add flag `--release 8` to compile with Java 8 compatible compiler

Generate executable JAR-file with
```
jar cfm <jar name>.jar Manifest.txt *.class layout *.jar
```