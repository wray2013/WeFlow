@echo off

call "%~dp0..\..\..\etc\setenv"

cd /d "%~dp0.."

call gradle createNewProject

pause