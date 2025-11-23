#!/bin/bash

# MazeBank - Online Banking System
# Run script for Linux/Mac

echo "========================================="
echo "  MazeBank - Online Banking System"
echo "========================================="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if MySQL JDBC driver exists
JDBC_JAR=$(find lib -name "mysql-connector*.jar" | head -n 1)

if [ -z "$JDBC_JAR" ]; then
    echo -e "${RED}[ERROR] MySQL JDBC Driver not found!${NC}"
    echo ""
    echo "Please download MySQL Connector/J and place it in the lib folder."
    echo "See lib/DOWNLOAD_JDBC_DRIVER.md for instructions."
    echo ""
    exit 1
fi

echo -e "${GREEN}[INFO] Using JDBC Driver: $JDBC_JAR${NC}"
echo ""

# Create bin directory if it doesn't exist
mkdir -p bin

echo "[STEP 1/3] Compiling Java files..."
echo ""

javac -d bin -cp "$JDBC_JAR" \
    src/com/mazebank/Main.java \
    src/com/mazebank/model/*.java \
    src/com/mazebank/dao/*.java \
    src/com/mazebank/ui/*.java \
    src/com/mazebank/util/*.java

if [ $? -ne 0 ]; then
    echo ""
    echo -e "${RED}[ERROR] Compilation failed!${NC}"
    echo "Please check for errors above."
    echo ""
    exit 1
fi

echo ""
echo -e "${GREEN}[SUCCESS] Compilation completed!${NC}"
echo ""

echo "[STEP 2/3] Checking prerequisites..."
echo ""
echo "Please ensure:"
echo "  [*] MySQL Server is running"
echo "  [*] Database 'mazebank_db' is created"
echo "  [*] Database credentials are correct in DatabaseConnection.java"
echo ""

echo "[STEP 3/3] Starting MazeBank Application..."
echo ""
echo "========================================="
echo ""

java -cp "bin:$JDBC_JAR" com.mazebank.Main

if [ $? -ne 0 ]; then
    echo ""
    echo -e "${RED}[ERROR] Application failed to start!${NC}"
    echo ""
    echo "Common issues:"
    echo "  1. MySQL server is not running"
    echo "  2. Database 'mazebank_db' does not exist"
    echo "  3. Wrong database credentials"
    echo "  4. JDBC driver not found"
    echo ""
    echo "Check README.md for troubleshooting."
fi

echo ""
echo "========================================="
echo "  Application Closed"
echo "========================================="
