@echo off

call "%~dp0..\..\..\..\etc\setenv"

"%MYSQL_HOME%\bin\mysql" %MYSQL_LOGON% zkbc_bsd < %~dp0data\zkbc_bsd.sql

pause
