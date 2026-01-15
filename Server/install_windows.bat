@echo off
title Installazione BIRT Report Server Python
color 0B

echo ========================================
echo   Installazione BIRT Report Server
echo   Python Version
echo ========================================
echo.

REM Verifica Python
where python >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERRORE: Python non trovato!
    echo.
    echo Installa Python 3.8+ da: https://www.python.org/downloads/
    echo Assicurati di selezionare "Add Python to PATH" durante l'installazione
    pause
    exit /b 1
)

echo Versione Python:
python --version
echo.

REM Verifica pip
where pip >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERRORE: pip non trovato!
    pause
    exit /b 1
)

echo Installazione dipendenze Python...
echo.

REM Installa dipendenze
pip install -r requirements.txt

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo   INSTALLAZIONE COMPLETATA!
    echo ========================================
    echo.
    echo Prossimi passi:
    echo 1. Compila il progetto Java (se necessario): compile.bat
    echo 2. Avvia il server: start-server.bat
    echo.
) else (
    echo.
    echo ========================================
    echo   ERRORE INSTALLAZIONE!
    echo ========================================
    echo.
)

pause

pause