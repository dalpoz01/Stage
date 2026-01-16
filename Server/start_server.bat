@echo off
title BIRT Report Server
color 0A

cd /d "%~dp0"

REM Verifica prerequisiti
if not exist "bin\com\report\model\BirtReportEngine.class" (
    echo ================================================================
    echo   ERRORE: Codice Java non compilato!
    echo ================================================================
    echo.
    echo   Esegui prima: compile.bat
    echo.
    pause
    exit /b 1
)

if not exist "server.py" (
    echo ================================================================
    echo   ERRORE: server.py non trovato!
    echo ================================================================
    pause
    exit /b 1
)

where python >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ================================================================
    echo   ERRORE: Python non trovato!
    echo ================================================================
    echo.
    echo   Installa Python e assicurati sia nel PATH
    pause
    exit /b 1
)

REM Verifica Java nel PATH
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ================================================================
    echo   ERRORE: Java non trovato nel PATH!
    echo ================================================================
    echo.
    echo   Installa Java 21+ e aggiungilo al PATH
    pause
    exit /b 1
)

REM Avvia il server
python server.py

pause