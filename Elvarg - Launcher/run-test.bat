@echo off
echo Compiling launcher test...

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

REM Compile just the test launcher (no dependencies needed)
javac -d bin src/com/elvarg/launcher/LauncherTest.java

REM Run it
echo Running launcher GUI...
java -cp bin com.elvarg.launcher.LauncherTest

pause
