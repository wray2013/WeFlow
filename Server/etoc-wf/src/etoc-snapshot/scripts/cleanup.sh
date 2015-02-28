#!/bin/sh

SCRDIR=`dirname $0`

. "$SCRDIR"/../../../etc/setenv.sh

cd "$SCRDIR/.."

rm -rf ./.gradle
gradle clean
