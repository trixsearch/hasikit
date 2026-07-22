@echo off
:: clean-rebuild.bat
:: Stops Gradle daemons, clears all locked build outputs, then rebuilds.
:: Run this when R.jar or other intermediates are locked by a stale process.

echo [clean-rebuild] Stopping all Gradle daemons...
call gradlew.bat --stop
timeout /t 3 /nobreak >nul

echo [clean-rebuild] Removing app\build...
if exist "app\build" rmdir /s /q "app\build"

echo [clean-rebuild] Removing root build...
if exist "build" rmdir /s /q "build"

echo [clean-rebuild] Removing configuration cache...
if exist ".gradle\configuration-cache" rmdir /s /q ".gradle\configuration-cache"

echo [clean-rebuild] Removing Kotlin error logs...
if exist ".kotlin\errors" rmdir /s /q ".kotlin\errors"

echo [clean-rebuild] Starting debug build...
call gradlew.bat assembleDebug

echo [clean-rebuild] Done.
