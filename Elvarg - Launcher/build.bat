@echo off
echo Building Elvarg Launcher...

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

REM Compile with GSON library (you'll need to download gson-2.8.9.jar)
javac -d bin -cp "lib/*" src/com/elvarg/launcher/**/*.java

REM Create JAR
cd bin
jar cvfe Elvarg-Launcher.jar com.elvarg.launcher.ui.LauncherGUI com/elvarg/launcher/**/*.class
cd ..

REM Move JAR to root
move bin\Elvarg-Launcher.jar .

echo Build complete! Launcher is ready: Elvarg-Launcher.jar
pause
