@echo off
title Setup BIRT Report Server
color 0E

echo ========================================
echo   Setup BIRT Report Server
echo ========================================
echo.
echo Questo script crea la struttura del progetto
echo.

REM Vai alla directory del progetto
cd /d "%~dp0"

echo Creazione directory...
echo.

REM Crea directory sorgenti
if not exist "src\com\report" mkdir src\com\report
if not exist "src\com\report\model" mkdir src\com\report\model
if not exist "src\com\report\service" mkdir src\com\report\service
if not exist "src\com\report\controller" mkdir src\com\report\controller

REM Crea directory librerie
if not exist "lib" mkdir lib

REM Crea directory bin
if not exist "bin" mkdir bin

REM Crea directory config
if not exist "config" mkdir config

REM Crea directory runtime
if not exist "uploads" mkdir uploads
if not exist "output" mkdir output
if not exist "logs" mkdir logs

echo [OK] src\com\report
echo [OK] src\com\report\model
echo [OK] src\com\report\service
echo [OK] lib
echo [OK] bin
echo [OK] config
echo [OK] uploads
echo [OK] output
echo [OK] logs
echo.

echo ========================================
echo   Struttura creata con successo!
echo ========================================
echo.
echo Prossimi passi:
echo.
echo 1. Copia i file .java in src\com\report\
echo 2. Scarica le librerie BIRT in lib\
echo 3. Esegui: compile.bat
echo 4. Esegui: start-server.bat
echo.

REM Crea file README nella directory lib
echo IMPORTANTE: Inserisci qui tutte le librerie JAR necessarie > lib\README.txt
echo. >> lib\README.txt
echo Librerie BIRT richieste: >> lib\README.txt
echo - org.eclipse.birt.runtime-*.jar >> lib\README.txt
echo - Tutte le dipendenze BIRT >> lib\README.txt
echo. >> lib\README.txt
echo Per usare Spring Boot (opzionale): >> lib\README.txt
echo - spring-boot-*.jar >> lib\README.txt
echo - spring-web-*.jar >> lib\README.txt
echo - Vedi documentazione completa >> lib\README.txt

echo Creato: lib\README.txt
echo.

pause