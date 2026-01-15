#!/usr/bin/env python3
"""
BIRT Report Server - Python Version
Server REST API per generazione report BIRT
Compatibile con Windows, Linux e macOS
"""

from flask import Flask, request, jsonify, send_file
from flask_cors import CORS
import os
import subprocess
import tempfile
import time
from pathlib import Path
from datetime import datetime
import logging
from werkzeug.utils import secure_filename

# Configurazione
app = Flask(__name__)
CORS(app)  # Abilita CORS per tutte le route

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

# Logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler(LOG_DIR / "server.log"),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)


def create_directories():
    """Crea le directory necessarie se non esistono"""
    for directory in [UPLOAD_DIR, OUTPUT_DIR, BIRT_HOME, LOG_DIR]:
        directory.mkdir(parents=True, exist_ok=True)
    logger.info(f"Directory create/verificate in: {BASE_DIR}")


def allowed_file(filename):
    """Verifica se il file ha un'estensione consentita"""
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def generate_birt_report(birt_file_path, json_api_url, output_format):
    """
    Genera un report BIRT chiamando il generatore Java
    
    Args:
        birt_file_path: Path del file .rptdesign
        json_api_url: URL dell'API JSON
        output_format: Formato output (PDF, XLSX, HTML, DOC)
    
    Returns:
        Path del file generato o None in caso di errore
    """
    try:
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        output_file = OUTPUT_DIR / f"report_{timestamp}.{output_format.lower()}"
        
        # Costruisci il comando Java
        # Nota: Devi avere il tuo BirtDesignToDocument compilato e nel classpath
        java_cmd = [
            "java",
            "-cp", f"bin{os.pathsep}lib/*",  # Classpath
            "com.report.model.BirtDesignToDocument",
            str(birt_file_path),
            json_api_url,
            str(OUTPUT_DIR),
            str(BIRT_HOME),
            output_format
        ]
        
        # Esegui il comando
        result = subprocess.run(
            java_cmd,
            capture_output=True,
            text=True,
            timeout=300  # 5 minuti timeout
        )
        
        if result.returncode == 0:
            logger.info(f"Report generato con successo: {output_file}")
            return output_file
        else:
            logger.error(f"Errore generazione report: {result.stderr}")
            return None
            
    except subprocess.TimeoutExpired:
        logger.error("Timeout durante la generazione del report")
        return None
    except Exception as e:
        logger.error(f"Errore imprevisto: {str(e)}")
        return None


@app.route('/api/reports/health', methods=['GET'])
def health_check():
    """Endpoint per verificare lo stato del server"""
    return jsonify({
        "status": "UP",
        "service": "Report Generation Service",
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
            return jsonify({"error": "Formato non supportato"}), 400
        
        # Salva il file temporaneamente
        filename = secure_filename(file.filename)
        timestamp = int(time.time() * 1000)
        temp_filename = f"{timestamp}_{filename}"
        temp_path = UPLOAD_DIR / temp_filename
        
        file.save(temp_path)
        logger.info(f"File salvato: {temp_path}")
        
        # Genera il report
        output_path = generate_birt_report(temp_path, json_api_url, output_format)
        
        # Elimina il file temporaneo
        try:
            temp_path.unlink()
        except Exception as e:
            logger.warning(f"Impossibile eliminare file temporaneo: {e}")
        
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
        logger.error(f"Errore nel generate_report: {str(e)}")
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
                    logger.info(f"Eliminato: {file_path}")
                except Exception as e:
                    logger.error(f"Errore eliminazione {file_path}: {e}")
        
        return jsonify({
            "message": f"Pulizia completata",
            "deleted_files": deleted_count
        })
        
    except Exception as e:
        logger.error(f"Errore cleanup: {str(e)}")
        return jsonify({"error": str(e)}), 500


@app.errorhandler(413)
def request_entity_too_large(error):
    """Handler per file troppo grandi"""
    return jsonify({"error": "File troppo grande. Massimo 50MB"}), 413


@app.errorhandler(500)
def internal_error(error):
    """Handler per errori interni"""
    logger.error(f"Errore interno del server: {error}")
    return jsonify({"error": "Errore interno del server"}), 500


def print_banner():
    """Stampa il banner di avvio"""
    print("\n" + "="*60)
    print("          BIRT REPORT SERVER - PYTHON VERSION")
    print("="*60)
    print(f"  Server:        http://localhost:5000")
    print(f"  Uploads:       {UPLOAD_DIR}")
    print(f"  Output:        {OUTPUT_DIR}")
    print(f"  Logs:          {LOG_DIR}")
    print("\n  Endpoints disponibili:")
    print("    GET  /api/reports/health")
    print("    GET  /api/reports/formats")
    print("    POST /api/reports/generate")
    print("    POST /api/reports/cleanup")
    print("\n  Premi CTRL+C per fermare il server")
    print("="*60 + "\n")


if __name__ == '__main__':
    # Crea le directory
    create_directories()
    
    # Stampa banner
    print_banner()
    
    # Avvia il server
    app.run(
        host='0.0.0.0',  # Ascolta su tutte le interfacce
        port=5000,
        debug=False,
        threaded=True
    )