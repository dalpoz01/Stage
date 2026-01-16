#!/bin/bash
# Setup BIRT Report Server
# Java 21 + BIRT 4.21 + Python 3.14

echo "================================================================"
echo "  SETUP BIRT REPORT SERVER"
echo "  Java 21 + BIRT 4.21 + Python 3.14"
echo "================================================================"
echo ""

# Vai alla directory del progetto
cd "$(dirname "$0")"

echo "[1/5] Creazione struttura directory..."
echo ""

# Crea directory sorgenti Java
mkdir -p src/com/report/model
echo "  [OK] src/com/report/model"

# Crea directory binari Java
mkdir -p bin
echo "  [OK] bin"

# Crea directory librerie
mkdir -p lib
echo "  [OK] lib"

# Crea file README in lib
if [ ! -f "lib/README.txt" ]; then
    cat > lib/README.txt << 'EOF'
LIBRERIE BIRT 4.21

Copia qui tutte le librerie JAR del tuo progetto BIRT:

  - org.eclipse.birt.runtime_4.21.0-*.jar
  - org.eclipse.birt.core-*.jar
  - Tutte le altre dipendenze BIRT

Esempio comando per copiare da progetto esistente:
  cp -r /path/to/TuoProgetto/lib/*.jar lib/
EOF
fi

echo ""
echo "[2/5] Verifica prerequisiti..."
echo ""

# Verifica Java
if ! command -v java &> /dev/null; then
    echo "  [FAIL] Java non trovato!"
    echo ""
    echo "  Installa Java 21+ con:"
    echo "    Ubuntu/Debian: sudo apt install openjdk-21-jdk"
    echo "    CentOS/RHEL:   sudo yum install java-21-openjdk-devel"
    echo "    macOS:         brew install openjdk@21"
    echo ""
    exit 1
fi

echo "  [OK] Java trovato"
java -version 2>&1 | head -n 1

# Verifica Python
if ! command -v python3 &> /dev/null; then
    echo "  [FAIL] Python3 non trovato!"
    echo ""
    echo "  Installa Python 3.8+ con:"
    echo "    Ubuntu/Debian: sudo apt install python3 python3-pip"
    echo "    CentOS/RHEL:   sudo yum install python3 python3-pip"
    echo "    macOS:         brew install python3"
    echo ""
    exit 1
fi

echo "  [OK] Python trovato"
python3 --version

echo ""
echo "[3/5] Controllo file necessari..."
echo ""

MISSING_FILES=0

if [ ! -f "src/com/report/model/BirtReportEngine.java" ]; then
    echo "  [WARN] BirtReportEngine.java non trovato in src/com/report/model/"
    MISSING_FILES=1
fi

if [ ! -f "server.py" ]; then
    echo "  [WARN] server.py non trovato nella directory root"
    MISSING_FILES=1
fi

if [ ! -f "requirements.txt" ]; then
    echo "  [WARN] requirements.txt non trovato"
    MISSING_FILES=1
fi

if [ $MISSING_FILES -eq 1 ]; then
    echo ""
    echo "  [WARN] Alcuni file mancano. Copiali nella directory corretta."
    echo ""
else
    echo "  [OK] Tutti i file necessari sono presenti"
fi

echo ""
echo "[4/5] Creazione script di utilità..."
echo ""

# Crea .gitignore se non esiste
if [ ! -f ".gitignore" ]; then
    cat > .gitignore << 'EOF'
# Python
__pycache__/
*.py[cod]
venv/
.Python

# Java
*.class
bin/

# BIRT
lib/*.jar

# Output
reports/
*.log
EOF
    echo "  [OK] .gitignore creato"
fi

echo ""
echo "[5/5] Riepilogo setup..."
echo ""

echo "  Directory create:"
echo "    - src/com/report/model/  (codice Java)"
echo "    - bin/                    (file compilati)"
echo "    - lib/                    (librerie BIRT)"
echo ""
echo "================================================================"
echo "  SETUP COMPLETATO!"
echo "================================================================"
echo ""
echo "  PROSSIMI PASSI:"
echo ""
echo "  1. Copia i file sorgente:"
echo "     - BirtReportEngine.java  in  src/com/report/model/"
echo "     - server.py               in  root/"
echo "     - requirements.txt        in  root/"
echo "     - test-client.html        in  root/"
echo ""
echo "  2. Copia le librerie BIRT in lib/"
echo "     cp -r /path/to/TuoProgetto/lib/*.jar lib/"
echo ""
echo "  3. Installa dipendenze Python:"
echo "     ./install.sh"
echo ""
echo "  4. Compila il codice Java:"
echo "     ./compile.sh"
echo ""
echo "  5. Avvia il server:"
echo "     ./start-server.sh"
echo ""
echo "================================================================"