@echo off
rem Ensure all relative paths are resolved from the project root (location of this script)
cd /d "%~dp0" || echo Failed to change to script directory & exit /b 1
title MazeBank - Online Banking System
color 0A

echo =========================================
echo   MazeBank - Online Banking System
echo =========================================
echo.

REM Check if MySQL JDBC driver exists
if not exist "lib\mysql-connector-java-8.0.33.jar" (
    if not exist "lib\mysql-connector-j-8.0.33.jar" (
        echo [ERROR] MySQL JDBC Driver not found!
        echo.
        echo Please download MySQL Connector/J and place it in the lib folder.
        echo See lib\DOWNLOAD_JDBC_DRIVER.md for instructions.
        echo.
        pause
        exit /b 1
    )
)

REM Find the MySQL connector JAR
set JDBC_JAR=
for %%f in (lib\mysql-connector*.jar) do set JDBC_JAR=%%f

if "%JDBC_JAR%"=="" (
    echo [ERROR] No MySQL connector JAR found in lib folder!
    pause
    exit /b 1
)

echo [INFO] Using JDBC Driver: %JDBC_JAR%
echo.

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

echo [STEP 1/3] Compiling Java files...
echo.

javac -d bin -cp "%JDBC_JAR%" ^
    src\com\mazebank\Main.java ^
    src\com\mazebank\model\*.java ^
    src\com\mazebank\dao\*.java ^
    src\com\mazebank\ui\*.java ^
    src\com\mazebank\util\*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed!
    echo Please check for errors above.
    echo.
    pause
    exit /b 1
)

echo.
echo [SUCCESS] Compilation completed!
echo.

echo [STEP 2/3] Checking prerequisites...
echo.
echo Please ensure:
echo   [*] MySQL Server is running
echo   [*] Database 'mazebank_db' is created
echo   [*] Database credentials are correct in DatabaseConnection.java
echo.

echo [STEP 3/3] Starting MazeBank Application...
echo.
echo =========================================
echo.

java -cp "bin;%JDBC_JAR%" com.mazebank.Main

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Application failed to start!
    echo.
    echo Common issues:
    echo   1. MySQL server is not running
    echo   2. Database 'mazebank_db' does not exist
    echo   3. Wrong database credentials
    echo   4. JDBC driver not found
    echo.
    echo Check README.md for troubleshooting.
)

echo.
echo =========================================
echo   Application Closed
echo =========================================
pause
