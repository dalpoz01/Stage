@echo off
title Compilazione BIRT Report Server
color 0B

echo ================================================================
echo   COMPILAZIONE JAVA
echo ================================================================
echo.

cd /d "%~dp0"

REM Verifica Java
where javac >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [FAIL] javac non trovato!
    echo.
    echo Verifica che Java JDK 21+ sia installato e nel PATH
    pause
    exit /b 1
)

echo [1/3] Versione Java:
javac -version
echo.

REM Verifica sorgente
if not exist "src\com\report\model\BirtReportEngine.java" (
    echo [FAIL] BirtReportEngine.java non trovato!
    echo        Percorso atteso: src\com\report\model\BirtReportEngine.java
    pause
    exit /b 1
)

echo [2/3] File sorgente trovato
echo       src\com\report\model\BirtReportEngine.java
echo.

REM Verifica librerie
set LIB_COUNT=0
for %%f in (lib\*.jar) do set /a LIB_COUNT+=1

if %LIB_COUNT%==0 (
    echo [WARN] Nessuna libreria JAR trovata in lib\
    echo.
    echo        Copia le librerie BIRT nella cartella lib\
    echo        Esempio: xcopy /s "E:\Stage2025\Stage\Server\lib*.jar" "lib\"
    echo.
    echo        Provo comunque a compilare...
    echo.
)

echo [3/3] Compilazione in corso...
echo.

REM Crea directory bin se non esiste
if not exist "bin" mkdir bin

REM Compila con classpath
javac -d bin -cp "lib\*" src\com\report\model\BirtReportEngine.java 2>compile_errors.txt

if %ERRORLEVEL% EQU 0 (
    echo ================================================================
    echo   COMPILAZIONE COMPLETATA CON SUCCESSO!
    echo ================================================================
    echo.
    echo   File generati:
    dir /b bin\com\report\model\*.class 2>nul
    echo.
    echo   Prossimo passo: start-server.bat
    echo.
    
    REM Elimina file errori se vuoto
    for %%A in (compile_errors.txt) do if %%~zA==0 del compile_errors.txt
) else (
    echo ================================================================
    echo   ERRORI DI COMPILAZIONE!
    echo ================================================================
    echo.
    echo   Dettagli errori:
    type compile_errors.txt
    echo.
    echo   POSSIBILI CAUSE:
    echo   1. Librerie BIRT mancanti in lib\
    echo   2. Versione Java incompatibile (serve Java 21+)
    echo   3. Versione BIRT incompatibile (serve BIRT 4.21 con Java 21)
    echo.
    echo   SOLUZIONI:
    echo   - Verifica Java: java -version
    echo   - Copia librerie BIRT 4.21 in lib\
    echo   - Se usi BIRT compilato per Java 8, aggiorna a BIRT 4.21+
    echo.
)

pause