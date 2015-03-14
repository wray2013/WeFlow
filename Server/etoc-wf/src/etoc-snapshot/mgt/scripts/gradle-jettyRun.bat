@echo off

call "%~dp0..\..\..\..\etc\setenv"

set GRADLE_OPTS=-Dspring.profiles.active="develop" -Xmx512m -XX:MaxPermSize=512m -Xdebug -Xrunjdwp:transport=dt_socket,address=8001,server=y,suspend=n
set GRADLE_USER_HOME=%UserProfile%\.gradle-etoc-mgt

cd /d "%~dp0.."
gradle jettyRun 2>&1 | "%~dp0..\..\scripts\tee" "%~dp0stdout.log"

pause
