#!/bin/sh

PRGDIR=`dirname $0`

. "$PRGDIR"/../../../../etc/setenv.sh

"$MYSQL_HOME/bin/mysql" $MYSQL_LOGON -f mysql < "$PRGDIR"/data/mysql.sql
