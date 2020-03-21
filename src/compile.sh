#!/bin/bash

cd $(dirname $0)

#TOMCAT_PATH=/Users/henriquedn/Lab/Apache/tomcat
TOMCAT_PATH=/opt/projects/tomcat
WEBAPP_NAME=aua

#export PATH=/Library/Java/JavaVirtualMachines/jdk1.7.0_71.jdk/Contents/Home/bin/
export PATH=/usr/lib/jvm/java-1.8.0-openjdk-amd64/bin/
export CLASSPATH=$TOMCAT_PATH/lib/servlet-api.jar
export CLASSPATH=$CLASSPATH:$TOMCAT_PATH/webapps/$WEBAPP_NAME/WEB-INF/lib/baraza.jar
export CLASSPATH=$CLASSPATH:$TOMCAT_PATH/webapps/$WEBAPP_NAME/WEB-INF/lib/commons-codec-1.9.jar
export CLASSPATH=$CLASSPATH:$TOMCAT_PATH/webapps/$WEBAPP_NAME/WEB-INF/lib/commons-logging-1.1.1.jar
export CLASSPATH=$CLASSPATH:$TOMCAT_PATH/webapps/$WEBAPP_NAME/WEB-INF/lib/json-simple-2.3.0.jar
export CLASSPATH=$CLASSPATH:$TOMCAT_PATH/webapps/$WEBAPP_NAME/WEB-INF/lib/okhttp-3.14.0.jar
export CLASSPATH=$CLASSPATH:$TOMCAT_PATH/webapps/$WEBAPP_NAME/WEB-INF/lib/okio-1.17.2.jar
export CLASSPATH=$CLASSPATH:$TOMCAT_PATH/webapps/$WEBAPP_NAME/WEB-INF/lib/json-20171018.jar
export CLASSPATH=$CLASSPATH:$TOMCAT_PATH/webapps/$WEBAPP_NAME/WEB-INF/lib/javax.json-1.0.4.jar
export CLASSPATH=$CLASSPATH:$TOMCAT_PATH/webapps/$WEBAPP_NAME/WEB-INF/lib/postgresql-42.2.2.jar
export CLASSPATH=$CLASSPATH:$TOMCAT_PATH/webapps/$WEBAPP_NAME/WEB-INF/lib/jasperreports-6.3.0.jar
export CLASSPATH=$CLASSPATH:$TOMCAT_PATH/webapps/$WEBAPP_NAME/WEB-INF/lib/slf4j-api-1.7.7.jar
export CLASSPATH=$CLASSPATH:$TOMCAT_PATH/webapps/$WEBAPP_NAME/WEB-INF/classes/


echo 'Compiling.......'
javac com/dewcis/UmisRegistrationData.java
# java com/dewcis/BEcoBank


echo 'Compile Done'
