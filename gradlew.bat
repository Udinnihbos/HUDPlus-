@echo off
setlocal

set APP_HOME=%~dp0
set CLASSPATH=%APP_HOME%gradle\wrapper\gradle-wrapper.jar

if not exist "%CLASSPATH%" (
    echo.
    echo ERROR: gradle-wrapper.jar tidak ditemukan!
    echo.
    echo Download dulu dengan PowerShell:
    echo   Invoke-WebRequest -Uri "https://github.com/gradle/gradle/raw/v8.11.0/gradle/wrapper/gradle-wrapper.jar" -OutFile "gradle\wrapper\gradle-wrapper.jar"
    echo.
    pause
    exit /b 1
)

java -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
