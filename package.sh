#!/bin/bash

mvn clean
mvn assembly:assembly
cd target/
mv com.stock.crawler-1.0-SNAPSHOT-jar-with-dependencies.jar  stock-crawler.jar
scp stock-crawler.jar dp@192.168.1.26:~/stock