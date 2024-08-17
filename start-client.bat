@echo off
REM Set the relative path for the frontend directory
set FRONTEND_DIR=%~dp0client

REM Navigate to the frontend directory and build the project
cd /d %FRONTEND_DIR%
call mvn clean package

REM Run the client
cd target
java -jar client-1.0-SNAPSHOT-jar-with-dependencies.jar

REM Exit the script
exit