@echo off
setlocal
set "SCRIPT_DIR=%~dp0"
call "%SCRIPT_DIR%transferproxy\gradlew.bat" -p "%SCRIPT_DIR:~0,-1%" %*
