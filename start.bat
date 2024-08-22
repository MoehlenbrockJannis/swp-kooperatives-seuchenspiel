@echo off

call mvn clean install

REM Start Backend in a new terminal window
echo Starting backend server...
start cmd /k "%~dp0start-server.bat"

REM Start Frontend in a new terminal window
echo Starting frontend client...
start cmd /k "%~dp0start-client.bat"

echo Both backend and frontend are starting in separate windows...