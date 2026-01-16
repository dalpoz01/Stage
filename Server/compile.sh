#!/bin/bash
# Compilazione codice Java
# BIRT Report Server

echo "================================================================"
echo "  COMPILAZIONE JAVA"
echo "================================================================"
echo ""

# Vai alla directory del progetto
cd "$(dirname "$0")"

# Verifica Java
if ! command -v javac &> /dev/null; then
    echo "[FAIL] javac non trovato!"
    echo ""
    echo "Verifica che Java JDK 21+ sia installato"
    echo "  Ubuntu/Debian: sudo apt install openjdk-21-jdk"
    echo "  CentOS/RHEL:   sudo yum install java-21-openjdk-devel"
    echo "  macOS:         brew install openjdk@21"
    exit 1
fi

echo "[1/3] Versione Java:"
javac -version
echo ""

# Verifica sorgente
if [ ! -f "src/com/report/model/BirtReportEngine.java" ]; then
    echo "[FAIL] BirtReportEngine.java non trovato!"
    echo "       Percorso atteso: src/com/report/model/BirtReportEngine.java"
    exit 1
fi

echo "[2/3] File sorgente trovato"
echo "       src/com/report/model/BirtReportEngine.java"
echo ""

# Verifica librerie
LIB_COUNT=$(find lib -name "*.jar" 2>/dev/null | wc -l)

if [ "$LIB_COUNT" -eq 0 ]; then
    echo "[WARN] Nessuna libreria JAR trovata in lib/"
    echo ""
    echo "       Copia le librerie BIRT nella cartella lib/"
    echo "       Esempio: cp -r /path/to/TuoProgetto/lib/*.jar lib/"
    echo ""
    echo "       Provo comunque a compilare..."
    echo ""
fi

echo "[3/3] Compilazione in corso..."
echo ""

# Crea directory bin se non esiste
mkdir -p bin

# Compila con classpath
javac -d bin -cp "lib/*" src/com/report/model/BirtReportEngine.java 2> compile_errors.txt

if [ $? -eq 0 ]; then
    echo "================================================================"
    echo "  COMPILAZIONE COMPLETATA CON SUCCESSO!"
    echo "================================================================"
    echo ""
    echo "  File generati:"
    ls -1 bin/com/report/model/*.class 2>/dev/null
    echo ""
    echo "  Prossimo passo: ./start-server.sh"
    echo ""
    
    # Elimina file errori se vuoto
    if [ ! -s compile_errors.txt ]; then
        rm compile_errors.txt
    fi
else
    echo "================================================================"
    echo "  ERRORI DI COMPILAZIONE!"
    echo "================================================================"
    echo ""
    echo "  Dettagli errori:"
    cat compile_errors.txt
    echo ""
    echo "  POSSIBILI CAUSE:"
    echo "  1. Librerie BIRT mancanti in lib/"
    echo "  2. Versione Java incompatibile (serve Java 21+)"
    echo "  3. Versione BIRT incompatibile (serve BIRT 4.21 con Java 21)"
    echo ""
    echo "  SOLUZIONI:"
    echo "  - Verifica Java: java -version"
    echo "  - Copia librerie BIRT 4.21 in lib/"
    echo "  - Se usi BIRT compilato per Java 8, aggiorna a BIRT 4.21+"
    echo ""
    exit 1
fi