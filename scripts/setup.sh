#!/usr/bin/env bash
set -e

# 1) Tools
sudo apt-get update -y
sudo apt-get install -y openjdk-17-jdk openjfx curl

# 2) JDBC driver
mkdir -p lib
if [ ! -f lib/mysql-connector-j.jar ]; then
  curl -L -o lib/mysql-connector-j.jar \
    https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.4.0/mysql-connector-j-8.4.0.jar
fi

# 3) Compile sources (recursive)
mkdir -p out
find . -name "*.java" > sources.txt

javac \
  --module-path /usr/share/openjfx/lib \
  --add-modules javafx.controls,javafx.fxml \
  -cp .:lib/mysql-connector-j.jar \
  -d out @sources.txt
