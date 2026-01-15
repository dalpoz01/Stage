package com.report.controller;

import com.report.service.ReportGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*") // Configura secondo le tue esigenze di sicurezza
public class ReportController {

    @Autowired
    private ReportGenerationService reportService;

    /**
     * Endpoint per generare un report
     * 
     * @param birtFile File .rptdesign caricato dal client
     * @param jsonApiUrl URL dell'API JSON da cui prendere i dati
     * @param format Formato di output (PDF, XLSX, HTML, DOC)
     * @return File del report generato come download
     */
    @PostMapping(value = "/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> generateReport(
            @RequestParam("birtFile") String birtFile,
            @RequestParam("jsonApiUrl") String jsonApiUrl,
            @RequestParam(value = "format", defaultValue = "PDF") String format) {
        
        try {
            // Validazione input
            if (birtFile.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Il file BIRT è obbligatorio"));
            }
            
            if (!birtFile.endsWith(".rptdesign")) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Il file deve essere un .rptdesign"));
            }
            
            if (jsonApiUrl == null || !jsonApiUrl.startsWith("http")) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("L'URL JSON API deve iniziare con http:// o https://"));
            }
            
            // Validazione formato
            if (!isValidFormat(format)) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Formato non supportato. Usa: PDF, XLSX, HTML, DOC"));
            }
            
            // Genera il report
            String generatedFilePath = reportService.generateReport(birtFile, jsonApiUrl, format);
            
            // Prepara il file per il download
            Path filePath = Paths.get(generatedFilePath);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists()) {
                return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Errore nella generazione del file"));
            }
            
            // Determina il content type
            String contentType = getContentType(format);
            
            // Ritorna il file come download
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
                
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body(createErrorResponse("Errore nella generazione del report: " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint per verificare lo stato del servizio
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Report Generation Service");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint per ottenere i formati supportati
     */
    @GetMapping("/formats")
    public ResponseEntity<Map<String, Object>> getSupportedFormats() {
        Map<String, Object> response = new HashMap<>();
        response.put("formats", new String[]{"PDF", "XLSX", "HTML", "DOC"});
        return ResponseEntity.ok(response);
    }
    
    private boolean isValidFormat(String format) {
        return format.equalsIgnoreCase("PDF") || 
               format.equalsIgnoreCase("XLSX") || 
               format.equalsIgnoreCase("HTML") || 
               format.equalsIgnoreCase("DOC");
    }
    
    private String getContentType(String format) {
        switch (format.toUpperCase()) {
            case "PDF":
                return "application/pdf";
            case "XLSX":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "HTML":
                return "text/html";
            case "DOC":
                return "application/msword";
            default:
                return "application/octet-stream";
        }
    }
    
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}