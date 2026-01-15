@echo off
title Compilazione BIRT Report Server
color 0B

echo ========================================
echo   Compilazione Progetto
echo ========================================
echo.

REM Vai alla directory del progetto
cd /d "%~dp0"

REM Verifica Java
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERRORE: Java non trovato!
    echo Installa Java 17+ e aggiungilo al PATH
    pause
    exit /b 1
)

echo Versione Java:
java -version
echo.

REM Crea directory bin se non esiste
if not exist "bin" mkdir bin

REM Costruisci il classpath con tutte le librerie
echo Costruzione classpath...
set CLASSPATH=.
for %%i in (lib\*.jar) do call :addToClasspath %%i
goto :continue

:addToClasspath
set CLASSPATH=%CLASSPATH%;%1
goto :eof

:continue

echo.
echo Classpath configurato
echo.
echo Compilazione in corso...
echo.

REM Compila tutti i file Java
javac -d bin -cp "%CLASSPATH%" src\com\report\*.java src\com\report\model\*.java src\com\report\service\*.java 2>compile_errors.txt

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo   COMPILAZIONE COMPLETATA!
    echo ========================================
    echo.
    echo File .class generati in: bin\
    echo.
    echo Per avviare il server: start-server.bat
    echo.
    
    REM Elimina file errori se vuoto
    for %%A in (compile_errors.txt) do if %%~zA==0 del compile_errors.txt
) else (
    echo.
    echo ========================================
    echo   ERRORI DI COMPILAZIONE!
    echo ========================================
    echo.
    echo Controlla il file: compile_errors.txt
    type compile_errors.txt
    echo.
)

pause