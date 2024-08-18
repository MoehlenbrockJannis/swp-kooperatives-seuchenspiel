@echo off
REM Set the relative path for the backend directory
set BACKEND_DIR=%~dp0server

REM Navigate to the backend directory and build the project
cd /d %BACKEND_DIR%
call mvn clean package

REM Run the server
cd target
java -jar server-1.0.0-jar-with-dependencies.jar

REM Exit the script
exit