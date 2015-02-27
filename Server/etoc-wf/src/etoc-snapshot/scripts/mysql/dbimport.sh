#!/bin/sh

PRGDIR=`dirname $0`

. "$PRGDIR"/../../../../etc/setenv.sh

"%MYSQL_HOME%/bin/mysql" $MYSQL_LOGON zkbc_bsd < "$PRGDIR"/data/zkbc_bsd.sql
