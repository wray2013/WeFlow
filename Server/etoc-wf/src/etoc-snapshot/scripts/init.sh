#!/bin/sh

SCRDIR=`dirname $0`

. "$SCRDIR"/../../../etc/setenv.sh

cd "$SCRDIR"/..

rm -rf ./.gradle ./.metadata
cp -rf ../../../conf/eclipse/.metadata ./
gradle clean cleanEclipse eclipse

PRGDIR=`pwd`
export PRGDIR
"$ECLIPSE_HOME"/eclipse -nosplash -data "$PRGDIR" -vm "$JAVA_HOME"/bin/java -application org.eclipse.ant.core.antRunner -buildfile "$SCRDIR"/eclipse.xml
