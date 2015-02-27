@echo off

call "%~dp0..\..\..\etc\setenv"

cd /d "%~dp0.."

rmdir /s /q .\.gradle
call gradle clean

pause
