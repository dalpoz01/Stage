package com.report;

import com.report.service.ReportGenerationService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Applicazione server HTTP semplice senza Spring Boot
 * Usa solo Java HttpServer incluso nel JDK
 */
public class ReportApplication {
    
    private static final int PORT = 8080;
    private static final String UPLOAD_DIR = System.getProperty("user.home") + "/reports/uploads";
    private static final String OUTPUT_DIR = System.getProperty("user.home") + "/reports/output";
    private static final String BIRT_HOME = System.getProperty("user.home") + "/reports/birt";
    
    private static ReportGenerationService reportService;

    public static void main(String[] args) {
        try {
            // Crea le directory necessarie
            createDirectories();
            
            // Inizializza il servizio
            reportService = new ReportGenerationService(UPLOAD_DIR, OUTPUT_DIR, BIRT_HOME);
            
            // Crea il server HTTP
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            
            // Registra gli endpoint
            server.createContext("/api/reports/health", new HealthHandler());
            server.createContext("/api/reports/formats", new FormatsHandler());
            server.createContext("/api/reports/generate", new GenerateReportHandler());
            
            // Avvia il server
            server.setExecutor(null); // Usa il default executor
            server.start();
            
            System.out.println("╔════════════════════════════════════════════════╗");
            System.out.println("║   BIRT Report Server                          ║");
            System.out.println("╠════════════════════════════════════════════════╣");
            System.out.println("║   Server avviato con successo!                ║");
            System.out.println("║   URL: http://localhost:" + PORT + "                  ║");
            System.out.println("║                                                ║");
            System.out.println("║   Endpoints disponibili:                      ║");
            System.out.println("║   - GET  /api/reports/health                  ║");
            System.out.println("║   - GET  /api/reports/formats                 ║");
            System.out.println("║   - POST /api/reports/generate                ║");
            System.out.println("║                                                ║");
            System.out.println("║   Directory:                                  ║");
            System.out.println("║   - Uploads: " + UPLOAD_DIR);
            System.out.println("║   - Output:  " + OUTPUT_DIR);
            System.out.println("║                                                ║");
            System.out.println("║   Premi CTRL+C per fermare il server          ║");
            System.out.println("╚════════════════════════════════════════════════╝");
            
        } catch (Exception e) {
            System.err.println("Errore avvio server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createDirectories() {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            Files.createDirectories(Paths.get(OUTPUT_DIR));
            Files.createDirectories(Paths.get(BIRT_HOME));
            System.out.println("Directory create con successo");
        } catch (IOException e) {
            System.err.println("Errore creazione directory: " + e.getMessage());
        }
    }
    
    // Handler per /api/reports/health
    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = "{\"status\":\"UP\",\"service\":\"Report Generation Service\"}";
                sendJsonResponse(exchange, 200, response);
            } else {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }
    
    // Handler per /api/reports/formats
    static class FormatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = "{\"formats\":[\"PDF\",\"XLSX\",\"HTML\",\"DOC\"]}";
                sendJsonResponse(exchange, 200, response);
            } else {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }
    
    // Handler per /api/reports/generate
    static class GenerateReportHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }
            
            try {
                // Parse multipart/form-data
                Map<String, Object> formData = parseMultipartFormData(exchange);
                
                // Estrai parametri
                byte[] birtFileData = (byte[]) formData.get("birtFile");
                String birtFileName = (String) formData.get("birtFileName");
                String jsonApiUrl = (String) formData.get("jsonApiUrl");
                String format = (String) formData.getOrDefault("format", "PDF");
                
                // Validazione
                if (birtFileData == null || jsonApiUrl == null || jsonApiUrl.trim().isEmpty()) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"Parametri mancanti\"}");
                    return;
                }
                
                if (!jsonApiUrl.startsWith("http")) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"jsonApiUrl deve iniziare con http:// o https://\"}");
                    return;
                }
                
                // Salva il file BIRT temporaneo
                String birtFilePath = UPLOAD_DIR + "/" + System.currentTimeMillis() + "_" + birtFileName;
                Files.write(Paths.get(birtFilePath), birtFileData);
                
                // Genera il report
                String outputPath = reportService.generateReport(birtFilePath, jsonApiUrl, format);
                
                // Elimina il file temporaneo
                Files.deleteIfExists(Paths.get(birtFilePath));
                
                if (outputPath == null) {
                    sendJsonResponse(exchange, 500, "{\"error\":\"Errore generazione report\"}");
                    return;
                }
                
                // Invia il file generato
                sendFileResponse(exchange, outputPath, format);
                
            } catch (Exception e) {
                e.printStackTrace();
                sendJsonResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }
    
    // Utility per parsare multipart/form-data
    private static Map<String, Object> parseMultipartFormData(HttpExchange exchange) throws IOException {
        Map<String, Object> result = new HashMap<>();
        
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.startsWith("multipart/form-data")) {
            return result;
        }
        
        String boundary = contentType.split("boundary=")[1];
        byte[] body = readAllBytes(exchange.getRequestBody());
        
        String bodyStr = new String(body, StandardCharsets.UTF_8);
        String[] parts = bodyStr.split("--" + boundary);
        
        for (String part : parts) {
            if (part.contains("Content-Disposition")) {
                String[] lines = part.split("\r\n");
                String disposition = lines[0];
                
                if (disposition.contains("name=\"birtFile\"")) {
                    // Estrai il nome del file
                    String fileNameLine = disposition;
                    String fileName = extractFileName(fileNameLine);
                    result.put("birtFileName", fileName);
                    
                    // Trova l'inizio del contenuto binario
                    int headerEnd = part.indexOf("\r\n\r\n") + 4;
                    int contentEnd = part.lastIndexOf("\r\n");
                    
                    if (headerEnd > 0 && contentEnd > headerEnd) {
                        byte[] fileData = Arrays.copyOfRange(
                            body, 
                            bodyStr.indexOf(part) + headerEnd,
                            bodyStr.indexOf(part) + contentEnd
                        );
                        result.put("birtFile", fileData);
                    }
                } else if (disposition.contains("name=\"jsonApiUrl\"")) {
                    String value = lines[2].trim();
                    result.put("jsonApiUrl", value);
                } else if (disposition.contains("name=\"format\"")) {
                    String value = lines[2].trim();
                    result.put("format", value);
                }
            }
        }
        
        return result;
    }
    
    private static String extractFileName(String disposition) {
        String[] parts = disposition.split(";");
        for (String part : parts) {
            if (part.trim().startsWith("filename=")) {
                return part.split("=")[1].trim().replace("\"", "");
            }
        }
        return "file.rptdesign";
    }
    
    // Utility per abilitare CORS
    private static void enableCORS(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }
    
    // Utility per inviare risposta JSON
    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
    
    // Utility per inviare file
    private static void sendFileResponse(HttpExchange exchange, String filePath, String format) throws IOException {
        Path path = Paths.get(filePath);
        byte[] fileBytes = Files.readAllBytes(path);
        
        String contentType = getContentType(format);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Content-Disposition", 
            "attachment; filename=\"" + path.getFileName() + "\"");
        
        exchange.sendResponseHeaders(200, fileBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(fileBytes);
        os.close();
    }
    
    private static String getContentType(String format) {
        switch (format.toUpperCase()) {
            case "PDF": return "application/pdf";
            case "XLSX": return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "HTML": return "text/html";
            case "DOC": return "application/msword";
            default: return "application/octet-stream";
        }
    }
    
    // Utility per leggere tutti i byte da un InputStream (compatibile Java 8)
    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[8192];
        
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        
        buffer.flush();
        return buffer.toByteArray();
    }
}