@echo off
echo ============================================
echo Building Elvarg Launcher (Verbose Mode)
echo ============================================
echo.

REM Clean previous build
echo [1/5] Cleaning previous build...
if exist "bin" rmdir /S /Q bin
if exist "Elvarg-Launcher.jar" del Elvarg-Launcher.jar
mkdir bin
echo Done!
echo.

REM Check GSON exists
echo [2/5] Checking for GSON library...
if not exist "lib\*.jar" (
    echo ERROR: No JAR files found in lib folder!
    echo Please add gson.jar to the lib folder.
    pause
    exit /b 1
)
dir lib\*.jar
echo Done!
echo.

REM Compile
echo [3/5] Compiling Java files...
javac -d bin -cp "lib\*" src\com\elvarg\launcher\util\*.java src\com\elvarg\launcher\ui\*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Compilation failed!
    echo Check the error messages above.
    pause
    exit /b 1
)
echo Done!
echo.

REM List compiled classes
echo [4/5] Checking compiled classes...
dir /S /B bin\*.class
echo.

REM Create JAR
echo [5/5] Creating JAR file...
cd bin
jar cvfe ..\Elvarg-Launcher.jar com.elvarg.launcher.ui.LauncherGUI com\elvarg\launcher\util\*.class com\elvarg\launcher\ui\*.class
cd ..

if exist "Elvarg-Launcher.jar" (
    echo.
    echo ============================================
    echo SUCCESS! Launcher built successfully!
    echo ============================================
    echo.
    echo Run with: java -jar Elvarg-Launcher.jar
) else (
    echo.
    echo ERROR: JAR file was not created!
)

pause
