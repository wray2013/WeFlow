#!/bin/sh

SCRDIR=`dirname $0`

. "$SCRDIR"/../../../etc/setenv.sh

cd "$SCRDIR"/..

gradle createNewProject
