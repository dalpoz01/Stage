@echo off
title BIRT Report Server - Python
color 0A

echo ========================================
echo   BIRT Report Server - Python
echo ========================================
echo.

REM Vai alla directory del progetto
cd /d "%~dp0"

REM Verifica Python
where python >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERRORE: Python non trovato!
    echo Esegui prima: install.bat
    pause
    exit /b 1
)

REM Verifica che server.py esista
if not exist "server.py" (
    echo ERRORE: server.py non trovato!
    pause
    exit /b 1
)

echo Avvio server Python...
echo.
echo Il server sarà disponibile su: http://localhost:5000
echo.
echo Premi CTRL+C per fermare il server
echo.
echo ========================================
echo.

REM Avvia il server
python server.py

pause