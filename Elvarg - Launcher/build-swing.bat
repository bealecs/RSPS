@echo off
echo ============================================
echo Building Elvarg Launcher (Swing Version)
echo ============================================
echo.

REM Clean previous build
if exist "bin" rmdir /S /Q bin
if exist "Elvarg-Launcher.jar" del Elvarg-Launcher.jar
mkdir bin

REM Compile
echo Compiling...
javac -d bin -cp "lib\*" src\com\elvarg\launcher\util\*.java src\com\elvarg\launcher\ui\SwingLauncher.java

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

REM Create JAR
echo Creating JAR...
cd bin
jar cvfe ..\Elvarg-Launcher.jar com.elvarg.launcher.ui.SwingLauncher com\elvarg\launcher\util\*.class com\elvarg\launcher\ui\*.class
cd ..

echo.
echo ============================================
echo SUCCESS! Run with: java -jar Elvarg-Launcher.jar
echo ============================================
pause
