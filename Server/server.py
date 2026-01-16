#!/usr/bin/env python3
"""
BIRT Report Server - Python Version
Server REST API per generazione report BIRT
Compatibile con Windows, Linux e macOS

Requisiti:
- Java 21+
- BIRT 4.21
- Python 3.14 (o 3.8+)
"""

from flask import Flask, request, jsonify, send_file
from flask_cors import CORS
import os
import subprocess
import time
from pathlib import Path
from datetime import datetime
import logging
from werkzeug.utils import secure_filename

# Configurazione
app = Flask(__name__)
CORS(app)  # Abilita CORS

# Directory configurabili
HOME_DIR = Path.home()
BASE_DIR = HOME_DIR / "reports"
UPLOAD_DIR = BASE_DIR / "uploads"
OUTPUT_DIR = BASE_DIR / "output"
BIRT_HOME = BASE_DIR / "birt"
LOG_DIR = BASE_DIR / "logs"

# Configurazione upload
ALLOWED_EXTENSIONS = {'rptdesign'}
MAX_FILE_SIZE = 50 * 1024 * 1024  # 50MB

# Logger (configurato dopo create_directories)
logger = None


def create_directories():
    """Crea le directory necessarie se non esistono"""
    for directory in [UPLOAD_DIR, OUTPUT_DIR, BIRT_HOME, LOG_DIR]:
        directory.mkdir(parents=True, exist_ok=True)
    print(f"✓ Directory create/verificate in: {BASE_DIR}")


def setup_logging():
    """Configura il logging dopo aver creato le directory"""
    global logger
    
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
        handlers=[
            logging.FileHandler(LOG_DIR / "server.log", encoding='utf-8'),
            logging.StreamHandler()
        ]
    )
    logger = logging.getLogger(__name__)
    logger.info("✓ Logging inizializzato")


def allowed_file(filename):
    """Verifica se il file ha un'estensione consentita"""
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def generate_birt_report(birt_file_path, json_api_url, output_format):
    """
    Genera un report BIRT chiamando il wrapper Java
    
    Args:
        birt_file_path: Path del file .rptdesign
        json_api_url: URL dell'API JSON
        output_format: Formato output (PDF, XLSX, HTML, DOC)
    
    Returns:
        Path del file generato o None in caso di errore
    """
    try:
        # Costruisci il classpath corretto per il sistema operativo
        if os.name == 'nt':  # Windows
            classpath_sep = ';'
            lib_pattern = 'lib\\*'
        else:  # Linux/macOS
            classpath_sep = ':'
            lib_pattern = 'lib/*'
        
        classpath = f"bin{classpath_sep}{lib_pattern}"
        
        # Comando Java con tutti i parametri
        java_cmd = [
            "java",
            "-cp", classpath,
            "com.report.model.BirtReportEngine",  # Classe wrapper con main()
            str(birt_file_path),
            json_api_url,
            str(OUTPUT_DIR),
            str(BIRT_HOME),
            output_format
        ]
        
        logger.info(f"Esecuzione comando Java:")
        logger.info(f"  {' '.join(java_cmd)}")
        logger.info(f"  Working dir: {os.getcwd()}")
        
        # Esegui il comando Java
        result = subprocess.run(
            java_cmd,
            capture_output=True,
            text=True,
            timeout=300,  # 5 minuti timeout
            cwd=os.getcwd(),
            encoding='utf-8',
            errors='replace'
        )
        
        # Analizza output
        if result.returncode == 0:
            # Cerca "SUCCESS:" nell'output
            for line in result.stdout.split('\n'):
                if line.startswith('SUCCESS:'):
                    output_path = line.replace('SUCCESS:', '').strip()
                    output_file = Path(output_path)
                    
                    if output_file.exists():
                        logger.info(f"✓ Report generato: {output_file}")
                        return output_file
            
            # Se non trova SUCCESS, cerca l'ultimo file generato
            output_files = list(OUTPUT_DIR.glob(f"report*.{output_format.lower()}"))
            if output_files:
                latest_file = max(output_files, key=lambda p: p.stat().st_mtime)
                logger.info(f"✓ Report generato (fallback): {latest_file}")
                return latest_file
            
            logger.error("✗ Nessun file di output trovato")
            logger.error(f"STDOUT: {result.stdout}")
            return None
        else:
            logger.error(f"✗ Errore generazione report (exit code {result.returncode})")
            logger.error(f"STDOUT:\n{result.stdout}")
            logger.error(f"STDERR:\n{result.stderr}")
            return None
            
    except subprocess.TimeoutExpired:
        logger.error("✗ Timeout durante la generazione del report (>5 min)")
        return None
    except FileNotFoundError:
        logger.error("✗ Java non trovato! Verifica che Java 21+ sia installato e nel PATH")
        return None
    except Exception as e:
        logger.error(f"✗ Errore imprevisto: {str(e)}", exc_info=True)
        return None


@app.route('/api/reports/health', methods=['GET'])
def health_check():
    """Endpoint per verificare lo stato del server"""
    return jsonify({
        "status": "UP",
        "service": "BIRT Report Generation Service",
        "version": "1.0",
        "java": "21+",
        "birt": "4.21",
        "python": "3.14",
        "timestamp": datetime.now().isoformat()
    })


@app.route('/api/reports/formats', methods=['GET'])
def get_formats():
    """Endpoint per ottenere i formati supportati"""
    return jsonify({
        "formats": ["PDF", "XLSX", "HTML", "DOC"]
    })


@app.route('/api/reports/generate', methods=['POST'])
def generate_report():
    """
    Endpoint per generare un report
    
    Parametri multipart/form-data:
        - birtFile: File .rptdesign (obbligatorio)
        - jsonApiUrl: URL API JSON (obbligatorio)
        - format: Formato output (opzionale, default: PDF)
    """
    try:
        # Verifica presenza file
        if 'birtFile' not in request.files:
            return jsonify({"error": "File BIRT mancante"}), 400
        
        file = request.files['birtFile']
        
        if file.filename == '':
            return jsonify({"error": "Nessun file selezionato"}), 400
        
        if not allowed_file(file.filename):
            return jsonify({"error": "Tipo file non valido. Usa .rptdesign"}), 400
        
        # Verifica parametri
        json_api_url = request.form.get('jsonApiUrl', '').strip()
        if not json_api_url:
            return jsonify({"error": "jsonApiUrl mancante"}), 400
        
        if not json_api_url.startswith('http'):
            return jsonify({"error": "jsonApiUrl deve iniziare con http:// o https://"}), 400
        
        output_format = request.form.get('format', 'PDF').upper()
        if output_format not in ['PDF', 'XLSX', 'HTML', 'DOC']:
            return jsonify({"error": f"Formato '{output_format}' non supportato. Usa: PDF, XLSX, HTML, DOC"}), 400
        
        # Salva il file temporaneamente
        filename = secure_filename(file.filename)
        timestamp = int(time.time() * 1000)
        temp_filename = f"{timestamp}_{filename}"
        temp_path = UPLOAD_DIR / temp_filename
        
        file.save(temp_path)
        logger.info(f"✓ File ricevuto: {temp_filename} ({file.content_length} bytes)")
        
        # Genera il report
        logger.info(f"→ Generazione report {output_format} in corso...")
        output_path = generate_birt_report(temp_path, json_api_url, output_format)
        
        # Elimina il file temporaneo
        try:
            temp_path.unlink()
            logger.info(f"✓ File temporaneo eliminato: {temp_filename}")
        except Exception as e:
            logger.warning(f"⚠ Impossibile eliminare file temporaneo: {e}")
        
        if output_path is None or not output_path.exists():
            return jsonify({"error": "Errore durante la generazione del report"}), 500
        
        # Invia il file generato
        return send_file(
            output_path,
            as_attachment=True,
            download_name=output_path.name,
            mimetype=get_mimetype(output_format)
        )
        
    except Exception as e:
        logger.error(f"✗ Errore nel generate_report: {str(e)}", exc_info=True)
        return jsonify({"error": str(e)}), 500


def get_mimetype(format_type):
    """Restituisce il MIME type per il formato"""
    mimetypes = {
        'PDF': 'application/pdf',
        'XLSX': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'HTML': 'text/html',
        'DOC': 'application/msword'
    }
    return mimetypes.get(format_type, 'application/octet-stream')


@app.route('/api/reports/cleanup', methods=['POST'])
def cleanup_old_files():
    """
    Endpoint per pulire file vecchi
    
    Parametri JSON:
        - days: Numero di giorni (default: 7)
    """
    try:
        data = request.get_json() or {}
        days = data.get('days', 7)
        
        cutoff_time = time.time() - (days * 24 * 60 * 60)
        deleted_count = 0
        
        for file_path in OUTPUT_DIR.glob('*'):
            if file_path.is_file() and file_path.stat().st_mtime < cutoff_time:
                try:
                    file_path.unlink()
                    deleted_count += 1
                    logger.info(f"✓ Eliminato: {file_path.name}")
                except Exception as e:
                    logger.error(f"✗ Errore eliminazione {file_path.name}: {e}")
        
        return jsonify({
            "message": f"Pulizia completata",
            "deleted_files": deleted_count,
            "days": days
        })
        
    except Exception as e:
        logger.error(f"✗ Errore cleanup: {str(e)}", exc_info=True)
        return jsonify({"error": str(e)}), 500


@app.errorhandler(413)
def request_entity_too_large(error):
    """Handler per file troppo grandi"""
    return jsonify({"error": "File troppo grande. Massimo 50MB"}), 413


@app.errorhandler(500)
def internal_error(error):
    """Handler per errori interni"""
    logger.error(f"✗ Errore interno del server: {error}")
    return jsonify({"error": "Errore interno del server"}), 500


def print_banner():
    """Stampa il banner di avvio"""
    print("\n" + "="*70)
    print("  ██████╗ ██╗██████╗ ████████╗    ██████╗ ███████╗██████╗  ██████╗ ██████╗ ████████╗")
    print("  ██╔══██╗██║██╔══██╗╚══██╔══╝    ██╔══██╗██╔════╝██╔══██╗██╔═══██╗██╔══██╗╚══██╔══╝")
    print("  ██████╔╝██║██████╔╝   ██║       ██████╔╝█████╗  ██████╔╝██║   ██║██████╔╝   ██║   ")
    print("  ██╔══██╗██║██╔══██╗   ██║       ██╔══██╗██╔══╝  ██╔═══╝ ██║   ██║██╔══██╗   ██║   ")
    print("  ██████╔╝██║██║  ██║   ██║       ██║  ██║███████╗██║     ╚██████╔╝██║  ██║   ██║   ")
    print("  ╚═════╝ ╚═╝╚═╝  ╚═╝   ╚═╝       ╚═╝  ╚═╝╚══════╝╚═╝      ╚═════╝ ╚═╝  ╚═╝   ╚═╝   ")
    print("="*70)
    print(f"  🚀 Server:        http://localhost:5000")
    print(f"  📦 Java:          21+")
    print(f"  📊 BIRT:          4.21")
    print(f"  🐍 Python:        3.14")
    print(f"  📁 Uploads:       {UPLOAD_DIR}")
    print(f"  📄 Output:        {OUTPUT_DIR}")
    print(f"  📝 Logs:          {LOG_DIR / 'server.log'}")
    print("\n  📡 Endpoints disponibili:")
    print("    GET  /api/reports/health    - Verifica stato server")
    print("    GET  /api/reports/formats   - Formati supportati")
    print("    POST /api/reports/generate  - Genera report")
    print("    POST /api/reports/cleanup   - Pulisci file vecchi")
    print("\n  💡 Test con: test-client.html")
    print("  ⛔ Premi CTRL+C per fermare il server")
    print("="*70 + "\n")


if __name__ == '__main__':
    # 1. Crea le directory PRIMA di configurare il logging
    create_directories()
    
    # 2. Configura logging DOPO aver creato le directory
    setup_logging()
    
    # 3. Stampa banner
    print_banner()
    
    # 4. Avvia il server
    try:
        app.run(
            host='0.0.0.0',  # Ascolta su tutte le interfacce
            port=5000,
            debug=False,
            threaded=True
        )
    except KeyboardInterrupt:
        print("\n\n✓ Server fermato dall'utente")
        logger.info("Server fermato dall'utente")
    except Exception as e:
        print(f"\n\n✗ Errore avvio server: {e}")
        logger.error(f"Errore avvio server: {e}", exc_info=True)