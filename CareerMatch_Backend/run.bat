@echo off
set "JAVA_BIN=java"
set "JAVAC_BIN=javac"

where java >nul 2>nul
if %errorlevel% neq 0 (
    for /d %%D in ("C:\Program Files\Microsoft\jdk*") do (
        if exist "%%D\bin\java.exe" set "JAVA_BIN=%%D\bin\java.exe"
        if exist "%%D\bin\javac.exe" set "JAVAC_BIN=%%D\bin\javac.exe"
    )
)

set M2=%USERPROFILE%\.m2\repository
set CP=target\classes;%M2%\org\xerial\sqlite-jdbc\3.46.1.0\sqlite-jdbc-3.46.1.0.jar;%M2%\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar

if not exist target\classes mkdir target\classes

echo Compiling CareerMatch Java source files...
dir /s /b src\main\java\*.java > sources.txt
"%JAVAC_BIN%" -encoding UTF-8 -cp "%CP%" -d target\classes @sources.txt
del sources.txt

echo Launching CareerMatch Desktop Platform...
"%JAVA_BIN%" -cp "%CP%" com.careermatch.Main
pause
