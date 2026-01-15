package com.report.service;

import com.report.model.BirtDesignToDocument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReportGenerationService {
    
    private final String uploadDir;
    private final String outputDir;
    private final String birtHome;
    
    public ReportGenerationService(String uploadDir, String outputDir, String birtHome) {
        this.uploadDir = uploadDir;
        this.outputDir = outputDir;
        this.birtHome = birtHome;
    }
    
    /**
     * Genera un report
     */
    public String generateReport(String birtFilePath, String jsonApiUrl, String format) throws Exception {
        try {
            BirtDesignToDocument generator = new BirtDesignToDocument(
                jsonApiUrl,
                birtFilePath,
                outputDir,
                birtHome
            );
            
            return generator.generateDocument(format);
            
        } catch (Exception e) {
            System.err.println("Errore generazione report: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Pulisce file vecchi
     */
    public void cleanupOldFiles(int daysOld) throws IOException {
        long cutoffTime = System.currentTimeMillis() - (daysOld * 24L * 60L * 60L * 1000L);
        
        Files.list(Paths.get(outputDir))
            .filter(Files::isRegularFile)
            .filter(path -> {
                try {
                    return Files.getLastModifiedTime(path).toMillis() < cutoffTime;
                } catch (IOException e) {
                    return false;
                }
            })
            .forEach(path -> {
                try {
                    Files.delete(path);
                    System.out.println("Eliminato: " + path.getFileName());
                } catch (IOException e) {
                    System.err.println("Errore eliminazione: " + path.getFileName());
                }
            });
    }
}