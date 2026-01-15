# 🚀 BIRT Report Server - Versione SENZA Maven

Questa è la versione semplificata che **non richiede Maven**, usa solo Java puro e HttpServer incluso nel JDK.

## 🎯 Vantaggi di questa versione

- ✅ **Nessuna dipendenza da Maven**
- ✅ **Server HTTP leggero** (usa java.net.HttpServer del JDK)
- ✅ **Compilazione manuale con javac**
- ✅ **Più semplice da capire e debuggare**
- ✅ **Ideale per ambienti con restrizioni**

## 📋 Prerequisiti

- **Java 17+** (include HttpServer)
- Librerie BIRT (file JAR)

## 🏗️ Installazione Passo-Passo

### Passo 1: Crea la Struttura

Esegui (doppio click):
```
setup.bat
```

Oppure manualmente:
```cmd
mkdir src\com\report\model
mkdir src\com\report\service
mkdir lib
mkdir bin
mkdir uploads
mkdir output
mkdir logs
```

### Passo 2: Copia i File Sorgenti

Copia questi file nella directory `src\com\report\`:

```
src\
└── com\
    └── report\
        ├── ReportApplication.java
        ├── model\
        │   └── BirtDesignToDocument.java
        └── service\
            └── ReportGenerationService.java
```

### Passo 3: Aggiungi le Librerie BIRT

Devi copiare **tutte** le librerie BIRT nella cartella `lib\`:

**Metodo A - Copia dal tuo progetto esistente:**
Se hai già il progetto BIRT funzionante, copia tutti i JAR dalle dipendenze:
```cmd
xcopy /s "C:\TuoProgettoEsistente\lib\*.jar" "lib\"
```

**Metodo B - Download manuale:**
Scarica BIRT Runtime da:
- https://download.eclipse.org/birt/downloads/
- Estrai e copia tutti i JAR dalla cartella `ReportEngine/lib` a `lib\`

**Metodo C - Usa PowerShell script:**
```cmd
powershell -ExecutionPolicy Bypass -File download-birt-libs.ps1
```

### Passo 4: Compila il Progetto

Doppio click su:
```
compile.bat
```

Oppure manualmente:
```cmd
javac -d bin -cp "lib\*" src\com\report\*.java src\com\report\model\*.java src\com\report\service\*.java
```

### Passo 5: Avvia il Server

Doppio click su:
```
start-server.bat
```

Oppure manualmente:
```cmd
java -cp "bin;lib\*" com.report.ReportApplication
```

## 📂 Struttura Finale del Progetto

```
BirtReportServer\
├── setup.bat                    ← Crea la struttura
├── compile.bat                  ← Compila il progetto
├── start-server.bat             ← Avvia il server
├── download-birt-libs.ps1       ← Scarica librerie (PowerShell)
├── test-client.html             ← Client di test
│
├── src\                         ← Codice sorgente
│   └── com\report\
│       ├── ReportApplication.java
│       ├── model\
│       │   └── BirtDesignToDocument.java
│       └── service\
│           └── ReportGenerationService.java
│
├── lib\                         ← Librerie JAR
│   ├── org.eclipse.birt.runtime-*.jar
│   ├── slf4j-api-*.jar
│   └── ... (altre dipendenze BIRT)
│
├── bin\                         ← File compilati (.class)
│   └── com\report\...
│
├── uploads\                     ← File BIRT temporanei
├── output\                      ← Report generati
└── logs\                        ← Log applicazione
```

## 🌐 Endpoint API

Una volta avviato, il server risponde su `http://localhost:8080`

### 1. Health Check
```
GET http://localhost:8080/api/reports/health
```
Risposta:
```json
{"status":"UP","service":"Report Generation Service"}
```

### 2. Formati Supportati
```
GET http://localhost:8080/api/reports/formats
```
Risposta:
```json
{"formats":["PDF","XLSX","HTML","DOC"]}
```

### 3. Genera Report
```
POST http://localhost:8080/api/reports/generate
Content-Type: multipart/form-data

Parametri:
- birtFile: file .rptdesign
- jsonApiUrl: URL API JSON
- format: PDF|XLSX|HTML|DOC (opzionale, default: PDF)
```

## 🧪 Test

### Metodo 1: Client HTML
Apri `test-client.html` nel browser (doppio click).

### Metodo 2: cURL
```cmd
curl -X POST http://localhost:8080/api/reports/generate ^
  -F "birtFile=@C:\path\to\report.rptdesign" ^
  -F "jsonApiUrl=https://api.example.com/data" ^
  -F "format=PDF" ^
  --output report.pdf
```

### Metodo 3: PowerShell
```powershell
$form = @{
    birtFile = Get-Item "C:\path\to\report.rptdesign"
    jsonApiUrl = "https://api.example.com/data"
    format = "PDF"
}

Invoke-WebRequest -Uri "http://localhost:8080/api/reports/generate" `
    -Method Post -Form $form -OutFile "report.pdf"
```

## 🔧 Configurazione

### Cambiare la Porta

Modifica `ReportApplication.java`:
```java
private static final int PORT = 9090; // Era 8080
```

Poi ricompila:
```cmd
compile.bat
```

### Cambiare le Directory

Modifica `ReportApplication.java`:
```java
private static final String OUTPUT_DIR = "C:/MyReports/output";
```

## 📊 Differenze con la Versione Maven

| Caratteristica | Con Maven | Senza Maven |
|----------------|-----------|-------------|
| Build Tool | Maven | javac manuale |
| Server HTTP | Spring Boot (Tomcat) | java.net.HttpServer |
| Dipendenze | Automatiche | Manuali |
| Packaging | JAR eseguibile | Esecuzione diretta |
| Dimensione | ~50MB+ | ~5-10MB |
| Complessità | Media | Bassa |
| Flessibilità | Alta | Media |

## ⚠️ Limitazioni

1. **HttpServer è basico**: Non ha tutte le funzionalità di Tomcat/Spring Boot
2. **Multipart parsing manuale**: Meno robusto per file molto grandi
3. **No auto-reload**: Devi ricompilare e riavviare manualmente
4. **No dependency management**: Devi gestire le librerie manualmente

## 🐛 Risoluzione Problemi

### Errore: "java non riconosciuto"
Installa Java 17+ e aggiungilo al PATH.

### Errore: "Classe non trovata"
Verifica che `compile.bat` sia stato eseguito con successo.

### Errore: "Porta già in uso"
Un'altra applicazione usa la porta 8080. Cambia porta o chiudi l'altra app.

### Errore compilazione: "package org.eclipse.birt does not exist"
Mancano le librerie BIRT nella cartella `lib\`. Copiale dal tuo progetto esistente.

### Server parte ma non risponde
Verifica il firewall Windows (porta 8080).

## 🔥 Performance

Questa versione è più leggera di Spring Boot:
- **Avvio**: ~1-2 secondi (vs ~5-10 con Spring Boot)
- **Memoria**: ~100-200MB (vs ~300-500MB con Spring Boot)
- **CPU**: Minimo overhead

Per **poche richieste al minuto** (come indicato), è perfettamente adeguata.

## 📈 Prossimi Passi (Opzionali)

Se vuoi migliorare questa versione:

1. **Logging su file**: Aggiungi SLF4J e Logback
2. **Thread pool**: Configura `server.setExecutor(Executors.newFixedThreadPool(10))`
3. **HTTPS**: Usa SSLContext con certificati
4. **Autenticazione**: Aggiungi controllo header Authorization
5. **Rate limiting**: Limita richieste per IP

## 💡 Quando Usare Questa Versione

✅ **Usa questa versione SE:**
- Non puoi/vuoi usare Maven
- Ambiente con restrizioni
- Progetto semplice con poche dipendenze
- Hai già tutte le librerie BIRT
- Vuoi massima semplicità

❌ **Usa la versione Maven SE:**
- Progetto complesso con molte dipendenze
- Vuoi funzionalità avanzate di Spring Boot
- Deployment in container (Docker)
- Team di sviluppo grande
- CI/CD automatizzato

## 📞 Supporto

Per problemi:
1. Controlla i log nella console
2. Verifica che tutte le librerie BIRT siano in `lib\`
3. Testa con `test-client.html`

---

## 🎯 Quick Start (TL;DR)

```cmd
REM 1. Crea struttura
setup.bat

REM 2. Copia i file .java in src\com\report\
REM 3. Copia le librerie BIRT in lib\

REM 4. Compila
compile.bat

REM 5. Avvia
start-server.bat

REM 6. Test
REM Apri test-client.html
```

Done! 🚀