# 🐍 BIRT Report Server - Python Version

Server REST API multipiattaforma per generazione report BIRT.

**✅ Funziona su: Windows, Linux, macOS**

## 🎯 Architettura

```
┌─────────────┐
│   Client    │ (Browser, cURL, Postman, ecc.)
└──────┬──────┘
       │ HTTP REST
       ▼
┌─────────────┐
│   Python    │ Flask Web Server (server.py)
│   Server    │ - Gestisce HTTP requests
└──────┬──────┘ - Upload file
       │         - CORS
       │ subprocess
       ▼
┌─────────────┐
│    Java     │ BIRT Engine (BirtReportWrapper.java)
│   BIRT      │ - Genera report PDF/XLSX/HTML/DOC
└─────────────┘
```

## 📦 Prerequisiti

### Windows
- **Python 3.8+**: https://www.python.org/downloads/
- **Java 17+**: https://adoptium.net/
- **Librerie BIRT**: Dal tuo progetto esistente

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install python3 python3-pip python3-venv
sudo apt install default-jdk
```

### Linux (CentOS/RHEL)
```bash
sudo yum install python3 python3-pip
sudo yum install java-17-openjdk-devel
```

### macOS
```bash
brew install python3
brew install openjdk@17
```

## 🚀 Installazione

### Windows

**1. Scarica/Estrai il progetto:**
```cmd
cd C:\
mkdir BirtReportServer
cd BirtReportServer
```

**2. Copia i file:**
- `server.py`
- `requirements.txt`
- `install.bat`
- `start-server.bat`
- `test-client.html`

**3. Installa dipendenze Python:**
```cmd
install.bat
```

**4. Compila il wrapper Java:**
```cmd
compile.bat
```
(Questo compila BirtReportWrapper.java)

**5. Avvia il server:**
```cmd
start-server.bat
```

### Linux/macOS

**1. Scarica/Estrai il progetto:**
```bash
cd ~
mkdir BirtReportServer
cd BirtReportServer
```

**2. Copia i file e rendi eseguibili gli script:**
```bash
chmod +x install.sh
chmod +x start-server.sh
```

**3. Installa dipendenze Python:**
```bash
./install.sh
```

**4. Compila il wrapper Java:**
```bash
javac -d bin -cp "lib/*" src/com/report/model/BirtReportWrapper.java
```

**5. Avvia il server:**
```bash
./start-server.sh
```

## 📂 Struttura Progetto

```
BirtReportServer/
├── server.py                    ← Server Python Flask
├── requirements.txt             ← Dipendenze Python
├── install.bat                  ← Installazione Windows
├── install.sh                   ← Installazione Linux/macOS
├── start-server.bat             ← Avvio Windows
├── start-server.sh              ← Avvio Linux/macOS
├── test-client.html             ← Client di test
│
├── src/                         ← Codice Java
│   └── com/report/model/
│       └── BirtReportWrapper.java
│
├── bin/                         ← Java compilati
├── lib/                         ← Librerie BIRT (JAR)
│
└── ~/reports/                   ← Directory runtime (auto-create)
    ├── uploads/                 ← File BIRT temporanei
    ├── output/                  ← Report generati
    ├── birt/                    ← BIRT home
    └── logs/                    ← Log server
```

## 🌐 API Endpoints

Il server risponde su **http://localhost:5000**

### 1. Health Check
```bash
curl http://localhost:5000/api/reports/health
```
Risposta:
```json
{
  "status": "UP",
  "service": "Report Generation Service",
  "timestamp": "2024-01-15T10:30:00.000Z"
}
```

### 2. Formati Supportati
```bash
curl http://localhost:5000/api/reports/formats
```
Risposta:
```json
{
  "formats": ["PDF", "XLSX", "HTML", "DOC"]
}
```

### 3. Genera Report
```bash
curl -X POST http://localhost:5000/api/reports/generate \
  -F "birtFile=@/path/to/report.rptdesign" \
  -F "jsonApiUrl=https://api.example.com/data" \
  -F "format=PDF" \
  --output report.pdf
```

### 4. Pulizia File Vecchi
```bash
curl -X POST http://localhost:5000/api/reports/cleanup \
  -H "Content-Type: application/json" \
  -d '{"days": 7}'
```

## 🧪 Test

### Metodo 1: Client HTML
Apri `test-client.html` nel browser.
**Cambia l'URL del server a: `http://localhost:5000`**

### Metodo 2: cURL (Windows)
```cmd
curl -X POST http://localhost:5000/api/reports/generate ^
  -F "birtFile=@C:\path\to\report.rptdesign" ^
  -F "jsonApiUrl=https://jsonplaceholder.typicode.com/users" ^
  -F "format=PDF" ^
  --output report.pdf
```

### Metodo 3: cURL (Linux/macOS)
```bash
curl -X POST http://localhost:5000/api/reports/generate \
  -F "birtFile=@/path/to/report.rptdesign" \
  -F "jsonApiUrl=https://jsonplaceholder.typicode.com/users" \
  -F "format=PDF" \
  --output report.pdf
```

### Metodo 4: Python Script
```python
import requests

url = "http://localhost:5000/api/reports/generate"

files = {
    'birtFile': open('report.rptdesign', 'rb')
}

data = {
    'jsonApiUrl': 'https://api.example.com/data',
    'format': 'PDF'
}

response = requests.post(url, files=files, data=data)

if response.status_code == 200:
    with open('report.pdf', 'wb') as f:
        f.write(response.content)
    print("Report generato!")
else:
    print(f"Errore: {response.json()}")
```

## ⚙️ Configurazione

### Cambiare Porta

Modifica `server.py`:
```python
app.run(
    host='0.0.0.0',
    port=8080,  # Era 5000
    debug=False
)
```

### Cambiare Directory

Modifica `server.py`:
```python
BASE_DIR = Path("/custom/path/reports")
```

### Abilitare Debug Mode

Modifica `server.py`:
```python
app.run(
    host='0.0.0.0',
    port=5000,
    debug=True,  # Era False
    threaded=True
)
```

⚠️ **NON usare debug=True in produzione!**

## 🐳 Docker (Opzionale)

### Dockerfile
```dockerfile
FROM python:3.11-slim

# Installa Java
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copia file
COPY requirements.txt .
COPY server.py .
COPY src/ src/
COPY lib/ lib/
COPY bin/ bin/

# Installa dipendenze Python
RUN pip install --no-cache-dir -r requirements.txt

# Crea directory
RUN mkdir -p /app/reports/uploads /app/reports/output /app/reports/birt /app/reports/logs

EXPOSE 5000

CMD ["python", "server.py"]
```

### Build e Run
```bash
docker build -t birt-report-server .
docker run -p 5000:5000 -v ~/reports:/app/reports birt-report-server
```

## 🔧 Servizio Systemd (Linux)

Crea `/etc/systemd/system/birt-report.service`:

```ini
[Unit]
Description=BIRT Report Server Python
After=network.target

[Service]
Type=simple
User=your-user
WorkingDirectory=/home/your-user/BirtReportServer
Environment="PATH=/usr/bin:/usr/local/bin"
ExecStart=/usr/bin/python3 /home/your-user/BirtReportServer/server.py
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Abilita e avvia:
```bash
sudo systemctl enable birt-report.service
sudo systemctl start birt-report.service
sudo systemctl status birt-report.service
```

## 🪟 Servizio Windows (NSSM)

**1. Scarica NSSM:** https://nssm.cc/download

**2. Installa il servizio:**
```cmd
nssm install BirtReportServer "C:\Python311\python.exe" "C:\BirtReportServer\server.py"
nssm set BirtReportServer AppDirectory "C:\BirtReportServer"
nssm set BirtReportServer Description "BIRT Report Generation Server"
nssm set BirtReportServer Start SERVICE_AUTO_START
nssm start BirtReportServer
```

## 📊 Logging

I log sono salvati in:
- **Console**: Output standard
- **File**: `~/reports/logs/server.log`

### Visualizza log in tempo reale

**Windows:**
```cmd
powershell Get-Content %USERPROFILE%\reports\logs\server.log -Wait
```

**Linux/macOS:**
```bash
tail -f ~/reports/logs/server.log
```

## 🔒 Sicurezza (Produzione)

Per produzione, considera:

### 1. Reverse Proxy (Nginx)
```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:5000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 2. HTTPS con Let's Encrypt
```bash
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.com
```

### 3. Firewall
```bash
# Linux (ufw)
sudo ufw allow 5000/tcp

# Windows Firewall (vedi guida precedente)
```

### 4. Rate Limiting
Aggiungi a `server.py`:
```python
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address

limiter = Limiter(
    app=app,
    key_func=get_remote_address,
    default_limits=["100 per hour"]
)

@limiter.limit("10 per minute")
@app.route('/api/reports/generate', methods=['POST'])
def generate_report():
    # ...
```

Installa:
```bash
pip install Flask-Limiter
```

## 🐛 Risoluzione Problemi

### Python non trovato
**Windows:** Reinstalla Python e seleziona "Add Python to PATH"
**Linux:** `sudo apt install python3`

### Java non trovato
Verifica con `java -version`. Se manca, installa Java 17+.

### Errore "Module 'flask' not found"
Esegui `install.bat` (Windows) o `./install.sh` (Linux)

### Errore "BirtReportWrapper class not found"
Compila il wrapper Java:
```bash
javac -d bin -cp "lib/*" src/com/report/model/BirtReportWrapper.java
```

### Porta già in uso
Un'altra app usa la porta 5000. Cambia porta in `server.py`.

### Timeout generazione report
Aumenta il timeout in `server.py`:
```python
result = subprocess.run(
    java_cmd,
    capture_output=True,
    text=True,
    timeout=600  # 10 minuti
)
```

## ⚡ Performance

### Produzione (Gunicorn)

Installa:
```bash
pip install gunicorn
```

Avvia:
```bash
gunicorn -w 4 -b 0.0.0.0:5000 server:app
```

- `-w 4`: 4 worker processes
- Gestisce più richieste concorrenti

### Produzione (Windows - Waitress)

Installa:
```cmd
pip install waitress
```

Crea `serve.py`:
```python
from waitress import serve
from server import app

serve(app, host='0.0.0.0', port=5000, threads=4)
```

Avvia:
```cmd
python serve.py
```

## 📈 Vantaggi Python vs Java Puro

| Caratteristica | Python + Java | Solo Java |
|----------------|---------------|-----------|
| Multipiattaforma | ✅ Eccellente | ⚠️ Buono |
| Facilità Setup | ✅ Semplice | ⚠️ Media |
| Web Framework | ✅ Flask (robusto) | ⚠️ HttpServer (basico) |
| Parsing Multipart | ✅ Werkzeug | ⚠️ Manuale |
| Manutenzione | ✅ Facile | ⚠️ Media |
| Performance | ✅ Buona | ✅ Ottima |
| Memoria | ~150MB | ~100MB |

## 🎯 Quando Usare Questa Versione

✅ **Usa Python SE:**
- Vuoi massima compatibilità multipiattaforma
- Preferisci Python a Java per il server HTTP
- Vuoi un framework web robusto (Flask)
- Hai già BIRT in Java e vuoi solo un wrapper

❌ **Usa Java Puro SE:**
- Vuoi minimizzare dipendenze
- Performance è critica
- Team solo Java

## 💡 Tips

1. **Virtual Environment**: Usa sempre venv per isolare dipendenze
2. **Log Rotation**: Configura logrotate per gestire log grandi
3. **Monitoring**: Usa `htop` (Linux) o Task Manager (Windows)
4. **Backup**: Script cron/task per backup directory output

---

## 🚀 Quick Start

```bash
# 1. Installa dipendenze
./install.sh        # Linux/macOS
install.bat         # Windows

# 2. Compila Java wrapper
javac -d bin -cp "lib/*" src/com/report/model/BirtReportWrapper.java

# 3. Avvia server
./start-server.sh   # Linux/macOS
start-server.bat    # Windows

# 4. Test
curl http://localhost:5000/api/reports/health
```

Done! 🎉