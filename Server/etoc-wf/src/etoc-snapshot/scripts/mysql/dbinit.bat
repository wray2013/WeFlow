@echo off

call "%~dp0..\..\..\..\etc\setenv"

"%MYSQL_HOME%\bin\mysql" %MYSQL_LOGON% -f mysql < %~dp0data\mysql.sql

pause
