#!/bin/bash
# Avvio BIRT Report Server
# Linux / macOS

echo "================================================================"
echo "  BIRT REPORT SERVER"
echo "================================================================"
echo ""

# Vai alla directory del progetto
cd "$(dirname "$0")"

# Verifica prerequisiti
if [ ! -f "bin/com/report/model/BirtReportEngine.class" ]; then
    echo "================================================================"
    echo "  ERRORE: Codice Java non compilato!"
    echo "================================================================"
    echo ""
    echo "  Esegui prima: ./compile.sh"
    echo ""
    exit 1
fi

if [ ! -f "server.py" ]; then
    echo "================================================================"
    echo "  ERRORE: server.py non trovato!"
    echo "================================================================"
    exit 1
fi

if ! command -v python3 &> /dev/null; then
    echo "================================================================"
    echo "  ERRORE: Python3 non trovato!"
    echo "================================================================"
    echo ""
    echo "  Installa Python e assicurati sia nel PATH"
    exit 1
fi

# Verifica Java nel PATH
if ! command -v java &> /dev/null; then
    echo "================================================================"
    echo "  ERRORE: Java non trovato nel PATH!"
    echo "================================================================"
    echo ""
    echo "  Installa Java 21+ e aggiungilo al PATH"
    exit 1
fi

# Attiva virtual environment se esiste
if [ -d "venv" ]; then
    echo "Attivazione virtual environment..."
    source venv/bin/activate
fi

# Avvia il server
python3 server.py