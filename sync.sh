#!/bin/bash

cd $(dirname $0)

java -Xmx512m -jar build/dbSync.jar 
