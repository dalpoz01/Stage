package com.report.model;

import org.eclipse.birt.report.engine.api.*;
import org.eclipse.birt.core.framework.Platform;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.logging.Level;

/**
 * Wrapper CLI per generazione report BIRT
 * Compatibile con Java 21 e BIRT 4.21
 * 
 * Usage:
 * java com.report.model.BirtReportEngine <birtFile> <jsonApiUrl> <outputDir> <birtHome> <format>
 * 
 * Esempio:
 * java -cp "bin;lib/*" com.report.model.BirtReportEngine \
 *      "uploads/report.rptdesign" \
 *      "https://api.example.com/data" \
 *      "C:/Users/stage01/reports/output" \
 *      "C:/Users/stage01/reports/birt" \
 *      "PDF"
 */
public class BirtReportEngine {
    
    public static void main(String[] args) {
        // Validazione argomenti
        if (args.length != 5) {
            System.err.println("Errore: Numero argomenti non valido");
            System.err.println();
            System.err.println("Usage: java BirtReportWrapper <birtFile> <jsonApiUrl> <outputDir> <birtHome> <format>");
            System.err.println();
            System.err.println("Esempio:");
            System.err.println("  java BirtReportWrapper report.rptdesign https://api.com/data output/ birt/ PDF");
            System.exit(1);
        }
        
        String birtFile = args[0];
        String jsonApiUrl = args[1];
        String outputDir = args[2];
        String birtHome = args[3];
        String format = args[4];
        
        // Log parametri (per debug)
        System.out.println("=== BIRT Report Wrapper ===");
        System.out.println("File BIRT:   " + birtFile);
        System.out.println("API JSON:    " + jsonApiUrl);
        System.out.println("Output Dir:  " + outputDir);
        System.out.println("BIRT Home:   " + birtHome);
        System.out.println("Formato:     " + format);
        System.out.println("===========================");
        System.out.println();
        
        try {
            // Crea generatore
            BirtDesignToDocument document = new BirtDesignToDocument(
                jsonApiUrl,
                birtFile,
                outputDir,
                birtHome
            );
            
            // Genera documento
            String outputPath = document.generateDocument(format);
            
            if (outputPath != null) {
                // Stampa il path del file generato (importante per Python)
                System.out.println("SUCCESS:" + outputPath);
                System.exit(0);
            } else {
                System.err.println("Errore: generazione report fallita");
                System.exit(1);
            }
            
        } catch (Exception e) {
            System.err.println("Errore durante la generazione: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

/**
 * Classe per generazione documenti BIRT
 * Supporta PDF, XLSX, HTML, DOC
 */
class BirtDesignToDocument {
    private final String sourceJson;
    private final String sourceBirt;
    private final String outputDir;
    private final String birtHome;
    private String json = "";
    
    public BirtDesignToDocument(String sourceJson, String sourceBirt, String outputDir, String birtHome) {
        this.sourceJson = sourceJson;
        this.sourceBirt = sourceBirt;
        this.outputDir = outputDir;
        this.birtHome = birtHome;
    }
    
    /**
     * Genera un documento nel formato specificato
     */
    public String generateDocument(String format) throws Exception {
        switch (format.toUpperCase()) {
            case "PDF":
                return generatePDF();
            case "DOC":
                return generateDOC();
            case "XLSX":
                return generateXLSX();
            case "HTML":
                return generateHTML();
            default:
                System.err.println("Formato non supportato: " + format);
                return null;
        }
    }
    
    /**
     * Genera documento PDF
     */
    private String generatePDF() throws Exception {
        try {
            EngineConfig config = new EngineConfig();
            config.setEngineHome(birtHome);
            config.setLogConfig(birtHome, Level.WARNING); // Solo WARNING per ridurre log
            
            Platform.startup(config);
            IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(
                IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY
            );
            IReportEngine engine = factory.createReportEngine(config);
            IReportRunnable design = engine.openReportDesign(sourceBirt);
            IRunAndRenderTask task = engine.createRunAndRenderTask(design);
            
            setJsonParameters(engine, design, task);
            
            LocalTime now = LocalTime.now();
            String timeString = now.format(DateTimeFormatter.ofPattern("HHmmss"));
            String namefile = "report" + timeString + ".pdf";
            String fullPath = outputDir + "/" + namefile;
            
            PDFRenderOption options = new PDFRenderOption();
            options.setOutputFileName(fullPath);
            options.setOutputFormat("pdf");
            
            task.setRenderOption(options);
            task.run();
            task.close();
            engine.destroy();
            Platform.shutdown();
            
            System.out.println("PDF generato: " + fullPath);
            return fullPath;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Genera documento DOC (Word)
     */
    private String generateDOC() throws Exception {
        try {
            EngineConfig config = new EngineConfig();
            config.setEngineHome(birtHome);
            config.setLogConfig(birtHome, Level.WARNING);
            
            Platform.startup(config);
            IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(
                IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY
            );
            IReportEngine engine = factory.createReportEngine(config);
            IReportRunnable design = engine.openReportDesign(sourceBirt);
            IRunAndRenderTask task = engine.createRunAndRenderTask(design);
            
            setJsonParameters(engine, design, task);
            
            LocalTime now = LocalTime.now();
            String timeString = now.format(DateTimeFormatter.ofPattern("HHmmss"));
            String namefile = "report" + timeString + ".doc";
            String fullPath = outputDir + "/" + namefile;
            
            RenderOption options = new RenderOption();
            options.setOutputFileName(fullPath);
            options.setOutputFormat("doc");
            
            task.setRenderOption(options);
            task.run();
            task.close();
            engine.destroy();
            Platform.shutdown();
            
            System.out.println("DOC generato: " + fullPath);
            return fullPath;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Genera documento XLSX (Excel)
     */
    private String generateXLSX() throws Exception {
        try {
            EngineConfig config = new EngineConfig();
            config.setEngineHome(birtHome);
            config.setLogConfig(birtHome, Level.WARNING);
            
            Platform.startup(config);
            IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(
                IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY
            );
            IReportEngine engine = factory.createReportEngine(config);
            IReportRunnable design = engine.openReportDesign(sourceBirt);
            IRunAndRenderTask task = engine.createRunAndRenderTask(design);
            
            setJsonParameters(engine, design, task);
            
            LocalTime now = LocalTime.now();
            String timeString = now.format(DateTimeFormatter.ofPattern("HHmmss"));
            String namefile = "report" + timeString + ".xlsx";
            String fullPath = outputDir + "/" + namefile;
            
            EXCELRenderOption options = new EXCELRenderOption();
            options.setOutputFileName(fullPath);
            options.setOutputFormat("xlsx");
            
            task.setRenderOption(options);
            task.run();
            task.close();
            engine.destroy();
            Platform.shutdown();
            
            System.out.println("XLSX generato: " + fullPath);
            return fullPath;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Genera documento HTML
     */
    private String generateHTML() throws Exception {
        try {
            EngineConfig config = new EngineConfig();
            config.setEngineHome(birtHome);
            config.setLogConfig(birtHome, Level.WARNING);
            
            Platform.startup(config);
            IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(
                IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY
            );
            IReportEngine engine = factory.createReportEngine(config);
            IReportRunnable design = engine.openReportDesign(sourceBirt);
            IRunAndRenderTask task = engine.createRunAndRenderTask(design);
            
            setJsonParameters(engine, design, task);
            
            LocalTime now = LocalTime.now();
            String timeString = now.format(DateTimeFormatter.ofPattern("HHmmss"));
            String namefile = "report" + timeString + ".html";
            String fullPath = outputDir + "/" + namefile;
            
            HTMLRenderOption options = new HTMLRenderOption();
            options.setOutputFileName(fullPath);
            options.setOutputFormat("html");
            
            task.setRenderOption(options);
            task.run();
            task.close();
            engine.destroy();
            Platform.shutdown();
            
            System.out.println("HTML generato: " + fullPath);
            return fullPath;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Imposta i parametri JSON per il report
     */
    private void setJsonParameters(IReportEngine engine, IReportRunnable design, IRunAndRenderTask task) throws Exception {
        if(sourceJson.startsWith("http")) {
            // API URL - passa direttamente
            task.setParameterValue("JsonSource", json);
            task.setParameterValue("json", sourceJson);
        } else {
            // File locale - leggi contenuto
            json = new String(Files.readAllBytes(Paths.get(sourceJson)), StandardCharsets.UTF_8);
            
            // Imposta parametri dinamicamente
            IGetParameterDefinitionTask paramTask = engine.createGetParameterDefinitionTask(design);
            @SuppressWarnings("unchecked")
            Collection<IParameterDefnBase> params = paramTask.getParameterDefns(false);
            
            for (IParameterDefnBase param : params) {
                if (param instanceof IScalarParameterDefn) {
                    IScalarParameterDefn scalarParam = (IScalarParameterDefn) param;
                    String paramName = scalarParam.getName();
                    int dataType = scalarParam.getDataType();
                    
                    if (dataType == IScalarParameterDefn.TYPE_STRING) {
                        task.setParameterValue(paramName, json);
                    }
                }
            }
            paramTask.close();
        }
    }
}