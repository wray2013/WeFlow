#!/bin/sh

JAVA_HOME=`/usr/libexec/java_home`
GROOVY_HOME=/Users/duanaiguo/Documents/Platforms/Guest/usr/local/groovy/groovy-2.1.9

GRADLE_HOME=/Users/duanaiguo/Documents/Platforms/Guest/usr/local/gradle/gradle-1.12
ECLIPSE_HOME=/Users/duanaiguo/Documents/Platforms/Host/devel/eclipse/eclipse-4.3.1

NEXUS_HOME=/Users/duanaiguo/Documents/Platforms/Guest/usr/local/nexus/nexus-2.8.1-01

MYSQL_HOME=/usr/local/Cellar/mysql/5.6.13
MYSQL_LOGON=-uroot

PATH=$JAVA_HOME/bin:$GRADLE_HOME/bin:$PATH

export JAVA_HOME GRADLE_HOME ECLIPSE_HOME NEXUS_HOME MYSQL_HOME MYSQL_LOGON

$NEXUS_HOME/bin/nexus start
$MYSQL_HOME/bin/mysql.server start
