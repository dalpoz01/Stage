@echo off
title BIRT Report Server
color 0A

echo ========================================
echo   BIRT Report Server
echo ========================================
echo.

REM Vai alla directory del progetto
cd /d "%~dp0"

REM Verifica che bin esista
if not exist "bin" (
    echo ERRORE: Directory bin non trovata!
    echo.
    echo Esegui prima: compile.bat
    pause
    exit /b 1
)

REM Verifica Java
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERRORE: Java non trovato!
    pause
    exit /b 1
)

REM Costruisci il classpath
echo Caricamento librerie...
set CLASSPATH=bin
for %%i in (lib\*.jar) do call :addToClasspath %%i
goto :continue

:addToClasspath
set CLASSPATH=%CLASSPATH%;%1
goto :eof

:continue

echo.
echo Avvio server...
echo ========================================
echo.

REM Avvia l'applicazione
java -cp "%CLASSPATH%" com.report.ReportApplication

pause