@echo off
title Setup BIRT Report Server
color 0E

echo ================================================================
echo   SETUP BIRT REPORT SERVER
echo   Java 21 + BIRT 4.21 + Python 3.14
echo ================================================================
echo.

cd /d "%~dp0"

echo [1/5] Creazione struttura directory...
echo.

REM Crea directory sorgenti Java
if not exist "src\com\report\model" mkdir src\com\report\model
echo   [OK] src\com\report\model

REM Crea directory binari Java
if not exist "bin" mkdir bin
echo   [OK] bin

REM Crea directory librerie
if not exist "lib" mkdir lib
echo   [OK] lib

REM Crea file README in lib
if not exist "lib\README.txt" (
    echo LIBRERIE BIRT 4.21 > lib\README.txt
    echo. >> lib\README.txt
    echo Copia qui tutte le librerie JAR del tuo progetto BIRT: >> lib\README.txt
    echo. >> lib\README.txt
    echo   - org.eclipse.birt.runtime_4.21.0-*.jar >> lib\README.txt
    echo   - org.eclipse.birt.core-*.jar >> lib\README.txt
    echo   - Tutte le altre dipendenze BIRT >> lib\README.txt
    echo. >> lib\README.txt
    echo Esempio comando per copiare da progetto esistente: >> lib\README.txt
    echo   xcopy /s "E:\Stage2025\Stage\Server\lib*.jar" "lib\" >> lib\README.txt
)

echo.
echo [2/5] Verifica prerequisiti...
echo.

REM Verifica Java
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo   [FAIL] Java non trovato!
    echo.
    echo   Installa Java 21+ da: https://adoptium.net/temurin/releases/?version=21
    echo   Assicurati di selezionare "Add to PATH" durante l'installazione
    echo.
    goto :error
)

echo   [OK] Java trovato
java -version 2>&1 | findstr /C:"version" 

REM Verifica Python
where python >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo   [FAIL] Python non trovato!
    echo.
    echo   Installa Python 3.8+ da: https://www.python.org/downloads/
    echo   Assicurati di selezionare "Add Python to PATH" durante l'installazione
    echo.
    goto :error
)

echo   [OK] Python trovato
python --version

echo.
echo [3/5] Controllo file necessari...
echo.

set MISSING_FILES=0

if not exist "src\com\report\model\BirtReportWrapper.java" (
    echo   [WARN] BirtReportWrapper.java non trovato in src\com\report\model\
    set MISSING_FILES=1
)

if not exist "server.py" (
    echo   [WARN] server.py non trovato nella directory root
    set MISSING_FILES=1
)

if not exist "requirements.txt" (
    echo   [WARN] requirements.txt non trovato
    set MISSING_FILES=1
)

if %MISSING_FILES%==1 (
    echo.
    echo   [WARN] Alcuni file mancano. Copiali nella directory corretta.
    echo.
) else (
    echo   [OK] Tutti i file necessari sono presenti
)

echo.
echo [4/5] Creazione script di utilità...
echo.

REM Crea .gitignore se non esiste
if not exist ".gitignore" (
    (
        echo # Python
        echo __pycache__/
        echo *.py[cod]
        echo venv/
        echo .Python
        echo
        echo # Java
        echo *.class
        echo bin/
        echo
        echo # BIRT
        echo lib/*.jar
        echo
        echo # Output
        echo reports/
        echo *.log
    ) > .gitignore
    echo   [OK] .gitignore creato
)

echo.
echo [5/5] Riepilogo setup...
echo.

echo   Directory create:
echo     - src\com\report\model\  (codice Java)
echo     - bin\                    (file compilati)
echo     - lib\                    (librerie BIRT)
echo.
echo ================================================================
echo   SETUP COMPLETATO!
echo ================================================================
echo.
echo   PROSSIMI PASSI:
echo.
echo   1. Copia i file sorgente:
echo      - BirtReportWrapper.java  in  src\com\report\model\
echo      - server.py               in  root\
echo      - requirements.txt        in  root\
echo      - test-client.html        in  root\
echo.
echo   2. Copia le librerie BIRT in lib\
echo      xcopy /s "E:\Stage2025\Stage\Server\lib*.jar" "lib\"
echo.
echo   3. Installa dipendenze Python:
echo      install.bat
echo.
echo   4. Compila il codice Java:
echo      compile.bat
echo.
echo   5. Avvia il server:
echo      start-server.bat
echo.
echo ================================================================
pause
exit /b 0

:error
echo.
echo ================================================================
echo   SETUP FALLITO!
echo ================================================================
echo.
echo   Risolvi i problemi sopra riportati e riprova.
echo.
pause
exit /b 1