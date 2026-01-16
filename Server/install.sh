#!/bin/bash
# Installazione dipendenze Python
# BIRT Report Server

echo "================================================================"
echo "  INSTALLAZIONE DIPENDENZE PYTHON"
echo "  BIRT Report Server"
echo "================================================================"
echo ""

# Verifica Python
if ! command -v python3 &> /dev/null; then
    echo "[FAIL] Python3 non trovato!"
    echo ""
    echo "Installa Python 3.8+ con:"
    echo "  Ubuntu/Debian: sudo apt install python3 python3-pip python3-venv"
    echo "  CentOS/RHEL:   sudo yum install python3 python3-pip"
    echo "  macOS:         brew install python3"
    exit 1
fi

echo "[OK] Python trovato"
python3 --version
echo ""

# Verifica pip
if ! command -v pip3 &> /dev/null; then
    echo "[FAIL] pip3 non trovato!"
    echo ""
    echo "Installa pip con:"
    echo "  Ubuntu/Debian: sudo apt install python3-pip"
    echo "  CentOS/RHEL:   sudo yum install python3-pip"
    exit 1
fi

echo "[OK] pip3 trovato"
echo ""

# Crea virtual environment (opzionale ma consigliato)
if [ ! -d "venv" ]; then
    echo "Creazione virtual environment..."
    python3 -m venv venv
    echo "[OK] Virtual environment creato"
fi

# Attiva virtual environment
echo "Attivazione virtual environment..."
source venv/bin/activate

echo ""
echo "Installazione dipendenze da requirements.txt..."
echo ""

# Installa dipendenze
pip install -r requirements.txt

if [ $? -eq 0 ]; then
    echo ""
    echo "================================================================"
    echo "  INSTALLAZIONE COMPLETATA!"
    echo "================================================================"
    echo ""
    echo "Prossimi passi:"
    echo "  1. Compila il codice Java: ./compile.sh"
    echo "  2. Avvia il server: ./start-server.sh"
    echo ""
    echo "NOTA: Il virtual environment è attivo."
    echo "Per disattivarlo: deactivate"
    echo ""
else
    echo ""
    echo "================================================================"
    echo "  ERRORE INSTALLAZIONE!"
    echo "================================================================"
    echo ""
    echo "Controlla gli errori sopra riportati."
    echo ""
    exit 1
fi