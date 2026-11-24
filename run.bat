@echo off
echo ========================================================
echo   Compiling and Launching PRIZZ Java Console System
echo ========================================================
if not exist "bin" mkdir bin
javac -cp "lib/*" -d bin java-prizz/src/com/prizz/*.java java-prizz/src/com/prizz/*/*.java
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed.
    pause
    exit /b %errorlevel%
)
java -cp "bin;lib/*" com.prizz.ui.Main
pause
