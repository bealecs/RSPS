@echo off
echo Building Elvarg Client...

REM Clean previous build
if exist "bin" rmdir /S /Q bin
if exist "Elvarg-Client.jar" del Elvarg-Client.jar
mkdir bin

REM Find and compile ALL Java files
echo Compiling all Java files...
dir /s /B src\*.java > sources.txt
javac -d bin @sources.txt
del sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

REM Copy non-Java resources (if any)
if exist "src\*.dat" xcopy /E /Y src\*.dat bin\
if exist "Cache" xcopy /E /Y Cache bin\Cache\

REM Create JAR with manifest
echo Creating JAR...
cd bin

REM Create manifest with proper line ending
echo Main-Class: com.runescape.Client> manifest.txt
echo.>> manifest.txt

REM Create JAR (m for manifest, f for file)
jar cmf manifest.txt Elvarg-Client.jar *

REM Move JAR to parent directory
move Elvarg-Client.jar ..
cd ..

echo.
echo ============================================
echo Client built successfully!
echo JAR file: Elvarg-Client.jar
echo ============================================
pause
