@echo off
rem MazeBank - Deploy WAR to Tomcat and run
rem Usage: double-click or run from terminal

setlocal enabledelayedexpansion
cd /d "%~dp0" || echo Failed to change to script directory & exit /b 1
color 0B

echo =========================================
echo   MazeBank - WAR Deployment to Tomcat
echo =========================================
echo.

REM Detect TOMCAT_HOME (override by setting env var TOMCAT_HOME)
set "TOMCAT_HOME=%TOMCAT_HOME%"
if "%TOMCAT_HOME%"=="" (
  set "TOMCAT_HOME=C:\tomcat\apache-tomcat-10.1.28"
)

if not exist "%TOMCAT_HOME%\webapps" (
  echo [ERROR] Tomcat not found at: %TOMCAT_HOME%
  echo Set TOMCAT_HOME environment variable or edit this script.
  echo Example: set TOMCAT_HOME=C:\tomcat\apache-tomcat-10.1.28
  pause
  exit /b 1
)

REM Verify required libraries
for %%L in (
  "lib\jakarta.servlet-api-5.0.0.jar"
  "lib\mysql-connector-j-8.0.33.jar"
  "lib\jackson-core-2.16.0.jar"
  "lib\jackson-annotations-2.16.0.jar"
  "lib\jackson-databind-2.16.0.jar"
) do (
  if not exist %%~L (
    echo [ERROR] Missing dependency: %%~L
    echo Please ensure all JARs exist in lib/.
    pause
    exit /b 1
  )
)

REM Prepare WAR structure
mkdir war_build 2>nul
mkdir war_build\WEB-INF 2>nul
mkdir war_build\WEB-INF\classes 2>nul
mkdir war_build\WEB-INF\lib 2>nul

copy /y web\WEB-INF\web.xml war_build\WEB-INF\ >nul

echo [STEP 1/3] Compiling sources into WEB-INF/classes...
set CP=lib\jakarta.servlet-api-5.0.0.jar;lib\mysql-connector-j-8.0.33.jar;lib\jackson-core-2.16.0.jar;lib\jackson-annotations-2.16.0.jar;lib\jackson-databind-2.16.0.jar
for /f "delims=" %%f in ('dir /b /s src\*.java') do (
  set "SRC_LIST=!SRC_LIST! \"%%f\""
)

rem Use a temporary response file to avoid command length limits
set "RESP_FILE=%TEMP%\javac_sources.lst"
if exist "%RESP_FILE%" del "%RESP_FILE%" >nul 2>&1
for /f "delims=" %%f in ('dir /b /s src\*.java') do echo %%f>>"%RESP_FILE%"

javac -d war_build\WEB-INF\classes -cp "%CP%" @"%RESP_FILE%"
if %ERRORLEVEL% NEQ 0 (
  echo [ERROR] Compilation failed.
  pause
  exit /b 1
)

echo [STEP 2/3] Copying libraries into WAR...
copy /y lib\mysql-connector-j-8.0.33.jar war_build\WEB-INF\lib\ >nul
copy /y lib\jackson-*.jar war_build\WEB-INF\lib\ >nul

echo [STEP 3/3] Packaging MazeBank.war...
if exist MazeBank.war del /f /q MazeBank.war >nul 2>&1
jar -cvf MazeBank.war -C war_build . >nul
if %ERRORLEVEL% NEQ 0 (
  echo [ERROR] WAR packaging failed.
  pause
  exit /b 1
)

echo [DEPLOY] Copying WAR to Tomcat webapps...
copy /y MazeBank.war "%TOMCAT_HOME%\webapps\" >nul
if %ERRORLEVEL% NEQ 0 (
  echo [ERROR] Failed to copy WAR to Tomcat webapps.
  pause
  exit /b 1
)

echo [START] Launching Tomcat (foreground)...
pushd "%TOMCAT_HOME%"
call bin\catalina.bat run
popd

echo.
echo =========================================
echo   Tomcat stopped. Deployment complete.
echo =========================================
pause
