@echo off

call "%~dp0..\..\..\etc\setenv"

cd "%~dp0.."
set PRGDIR=%cd%
start "ECLIPSE" "%ECLIPSE_HOME%\eclipse.exe" -data "%PRGDIR%" -vm "%JAVA_HOME%\bin\javaw.exe"