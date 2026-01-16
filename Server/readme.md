# 🚀 BIRT Report Server

Server REST API per generazione report BIRT multipiattaforma.

## 📋 Specifiche Tecniche

- **Java**: 21+
- **BIRT**: 4.21
- **Python**: 3.14 (compatibile con 3.8+)
- **OS**: Windows, Linux, macOS

---

## 🎯 Quick Start (5 minuti)

### Windows

```cmd
REM 1. Setup struttura
setup.bat

REM 2. Copia librerie BIRT in lib\
xcopy /s "E:\Stage2025\Stage\Server\lib*.jar" "lib\"

REM 3. Installa Python
install.bat

REM 4. Compila Java
compile.bat

REM 5. Avvia server
start-server.bat

REM 6. Apri test-client.html nel browser
```

### Linux

```bash
# 1. Setup struttura
chmod +x setup.sh && ./setup.sh

# 2. Copia librerie BIRT in lib/

# 3. Installa Python
chmod +x install.sh && ./install.sh

# 4. Compila Java
chmod +x compile.sh && ./compile.sh

# 5. Avvia server
chmod +x start-server.sh && ./start-server.sh

# 6. Apri test-client.html nel browser
```

---

## 📂 Struttura Progetto

```
BirtReportServer/
├── src/com/report/model/
│   └── BirtReportWrapper.java      # Wrapper Java BIRT
├── bin/com/report/model/
│   ├── BirtReportWrapper.class     # Compilati
│   └── BirtDesignToDocument.class
├── lib/
│   └── *.jar                       # Librerie BIRT 4.21
├── server.py                       # Server REST Flask
├── requirements.txt                # Dipendenze Python
├── test-client.html                # Client test
├── setup.bat / setup.sh            # Setup iniziale
├── compile.bat / compile.sh        # Compilazione
├── start-server.bat / start-server.sh  # Avvio
└── install.bat / install.sh        # Installa Python deps

Runtime (auto-create):
C:\Users\<user>\reports/
├── uploads/                        # File temporanei
├── output/                         # Report generati
├── birt/                           # BIRT home
└── logs/server.log                 # Log
```

---

## 🔧 Installazione Dettagliata

### 1. Prerequisiti

#### Java 21+
```cmd
REM Scarica da: https://adoptium.net/temurin/releases/?version=21
REM Verifica:
java -version
```

#### Python 3.8+
```cmd
REM Scarica da: https://www.python.org/downloads/
REM Verifica:
python --version
```

#### Librerie BIRT 4.21
Copia tutte le JAR BIRT nella cartella `lib/`

### 2. Setup

```cmd
REM Windows
setup.bat

# Linux
./setup.sh
```

Questo crea:
- Directory `src/`, `bin/`, `lib/`
- File `.gitignore`

### 3. Copia File

**Codice Java:**
- `BirtReportWrapper.java` → `src/com/report/model/`

**Python:**
- `server.py` → root
- `requirements.txt` → root

**Test:**
- `test-client.html` → root

**Librerie BIRT:**
```cmd
xcopy /s "E:\TuoProgettoOriginale\lib\*.jar" "lib\"
```

### 4. Installa Dipendenze Python

```cmd
REM Windows
install.bat

# Linux
./install.sh
```

Installa: Flask, flask-cors, Werkzeug

### 5. Compila Java

```cmd
REM Windows
compile.bat

# Linux
./compile.sh
```

Genera file `.class` in `bin/`

### 6. Avvia Server

```cmd
REM Windows
start-server.bat

# Linux
./start-server.sh
```

Server disponibile su: **http://localhost:5000**

---

## 🌐 API Endpoints

### Health Check
```bash
curl http://localhost:5000/api/reports/health
```

**Risposta:**
```json
{
  "status": "UP",
  "service": "BIRT Report Generation Service",
  "version": "1.0",
  "java": "21+",
  "birt": "4.21",
  "python": "3.14"
}
```

### Formati Supportati
```bash
curl http://localhost:5000/api/reports/formats
```

**Risposta:**
```json
{
  "formats": ["PDF", "XLSX", "HTML", "DOC"]
}
```

### Genera Report
```bash
curl -X POST http://localhost:5000/api/reports/generate \
  -F "birtFile=@report.rptdesign" \
  -F "jsonApiUrl=https://api.example.com/data" \
  -F "format=PDF" \
  --output report.pdf
```

**Parametri:**
- `birtFile`: File .rptdesign (obbligatorio)
- `jsonApiUrl`: URL API JSON (obbligatorio)
- `format`: PDF|XLSX|HTML|DOC (opzionale, default: PDF)

### Pulizia File Vecchi
```bash
curl -X POST http://localhost:5000/api/reports/cleanup \
  -H "Content-Type: application/json" \
  -d '{"days": 7}'
```

---

## 🧪 Test

### 1. Client HTML
Apri `test-client.html` nel browser.

### 2. cURL (Windows)
```cmd
curl -X POST http://localhost:5000/api/reports/generate ^
  -F "birtFile=@C:\path\to\report.rptdesign" ^
  -F "jsonApiUrl=https://jsonplaceholder.typicode.com/users" ^
  -F "format=PDF" ^
  --output report.pdf
```

### 3. Python Script
```python
import requests

files = {'birtFile': open('report.rptdesign', 'rb')}
data = {'jsonApiUrl': 'https://api.example.com/data', 'format': 'PDF'}

response = requests.post('http://localhost:5000/api/reports/generate', 
                        files=files, data=data)

if response.status_code == 200:
    with open('report.pdf', 'wb') as f:
        f.write(response.content)
```

---

## ⚙️ Configurazione

### Cambiare Porta
**File:** `server.py` (fine file)
```python
app.run(host='0.0.0.0', port=8080, debug=False)  # Era 5000
```

### Cambiare Directory Output
**File:** `server.py` (inizio file)
```python
BASE_DIR = Path("C:/CustomPath/reports")  # Default: Path.home() / "reports"
```

### Timeout Generazione
**File:** `server.py` (funzione generate_birt_report)
```python
result = subprocess.run(java_cmd, timeout=600)  # 10 min (era 300)
```

---

## 🐛 Troubleshooting

### Errore: "Java non trovato"
```cmd
java -version
REM Se non funziona, aggiungi Java al PATH
```

### Errore: "Class not found"
```cmd
REM Ricompila
compile.bat

REM Verifica
dir bin\com\report\model\*.class
```

### Errore: "BIRT libraries not found"
```cmd
REM Verifica librerie
dir lib\*.jar

REM Devono esserci file .jar BIRT 4.21
```

### Errore: "UnsupportedClassVersionError"
Stai usando Java 8 invece di Java 21+.
```cmd
java -version  # Deve mostrare 21 o superiore
```

### Errore: "Module 'flask' not found"
```cmd
install.bat  # Windows
./install.sh # Linux
```

### Timeout Generazione Report
Aumenta timeout in `server.py` (default: 5 minuti)

---

## 📊 Performance

### Requisiti Hardware
- **RAM**: 2 GB liberi (minimo 1 GB)
- **CPU**: 2+ core
- **Disco**: 500 MB + spazio per report

### Capacità
- **Richieste simultanee**: 5-10
- **File max**: 50 MB
- **Timeout**: 5 minuti (configurabile)
- **Tempo generazione**: 5-30 secondi tipico

---

## 🔒 Sicurezza (Produzione)

### Da Implementare

1. **HTTPS**
   - Certificato SSL
   - Reverse proxy (Nginx)

2. **Autenticazione**
   - API Key
   - JWT Token

3. **Rate Limiting**
   ```python
   from flask_limiter import Limiter
   limiter = Limiter(app, default_limits=["100 per hour"])
   ```

4. **Firewall**
   - Apri solo porta 5000
   - Limita per IP

---

## 📝 Log

### Posizione
```
C:\Users\<user>\reports\logs\server.log
```

### Visualizza in Tempo Reale

**Windows:**
```cmd
powershell Get-Content %USERPROFILE%\reports\logs\server.log -Wait
```

**Linux:**
```bash
tail -f ~/reports/logs/server.log
```

---

## 🚢 Deploy Produzione

### Systemd (Linux)

`/etc/systemd/system/birt-report.service`:
```ini
[Unit]
Description=BIRT Report Server
After=network.target

[Service]
Type=simple
User=your-user
WorkingDirectory=/path/to/BirtReportServer
ExecStart=/usr/bin/python3 server.py
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl enable birt-report
sudo systemctl start birt-report
```

### Windows Service (NSSM)

```cmd
nssm install BirtReportServer "C:\Python\python.exe" "C:\BirtReportServer\server.py"
nssm start BirtReportServer
```

---

## 🐳 Docker (Opzionale)

```dockerfile
FROM python:3.14-slim

RUN apt-get update && apt-get install -y openjdk-21-jdk

WORKDIR /app
COPY . .

RUN pip install -r requirements.txt
RUN javac -d bin -cp "lib/*" src/com/report/model/BirtReportWrapper.java

EXPOSE 5000
CMD ["python", "server.py"]
```

```bash
docker build -t birt-server .
docker run -p 5000:5000 birt-server
```

---

## 📚 Comandi Utili

```cmd
REM Windows - Setup completo
setup.bat && install.bat && compile.bat && start-server.bat

REM Ricompila e riavvia
compile.bat && start-server.bat

REM Test health check
curl http://localhost:5000/api/reports/health

REM Pulisci file vecchi (7 giorni)
curl -X POST http://localhost:5000/api/reports/cleanup ^
  -H "Content-Type: application/json" ^
  -d "{\"days\": 7}"
```

---

## ✅ Checklist Pre-Produzione

- [ ] Java 21+ installato e nel PATH
- [ ] Python 3.8+ installato
- [ ] Librerie BIRT 4.21 in `lib/`
- [ ] Codice compilato in `bin/`
- [ ] Dipendenze Python installate
- [ ] Test con `test-client.html` OK
- [ ] Log funzionanti
- [ ] Firewall configurato
- [ ] HTTPS abilitato
- [ ] Autenticazione implementata
- [ ] Backup configurato
- [ ] Monitoring attivo

---

## 📞 Supporto

### Log
```
C:\Users\<user>\reports\logs\server.log
```

### Test Manuale Java
```cmd
java -cp "bin;lib/*" com.report.model.BirtReportWrapper ^
  test.rptdesign ^
  https://jsonplaceholder.typicode.com/users ^
  C:\Users\stage01\reports\output ^
  C:\Users\stage01\reports\birt ^
  PDF
```

### Verifica Versioni
```cmd
java -version    # Deve essere 21+
python --version # Deve essere 3.8+
```

---

## 📄 Licenza

Personalizza secondo le tue esigenze.

---

**Versione:** 1.0.0  
**Data:** 2025-01-16  
**Autore:** Stefano Dal Poz