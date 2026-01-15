package com.report.scheduler;

import com.report.service.ReportGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler per la pulizia automatica dei file vecchi
 * Esegue la pulizia ogni giorno a mezzanotte
 */
@Component
public class FileCleanupScheduler {

    @Autowired
    private ReportGenerationService reportService;
    
    @Value("${report.cleanup.days:7}")
    private int daysToKeep;
    
    /**
     * Esegue la pulizia dei file vecchi ogni giorno a mezzanotte
     * Il cron expression: secondo minuto ora giorno mese giornoSettimana
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupOldReports() {
        try {
            System.out.println("Avvio pulizia file vecchi...");
            reportService.cleanupOldFiles(daysToKeep);
            System.out.println("Pulizia completata");
        } catch (Exception e) {
            System.err.println("Errore durante la pulizia: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

// NOTA: Per abilitare lo scheduling, aggiungi @EnableScheduling 
// nella classe ReportApplication:
//
// @SpringBootApplication
// @EnableScheduling
// public class ReportApplication { ... }