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

public class BirtDesignToDocument {
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
     * @param format Formato di output: "PDF", "DOC", "XLSX", "HTML"
     * @return path completo del file generato, null se fallisce
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
            config.setLogConfig(birtHome, Level.FINE);
            
            Platform.startup(config);
            IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(
                IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY
            );
            IReportEngine engine = factory.createReportEngine(config);
            IReportRunnable design = engine.openReportDesign(sourceBirt);
            IRunAndRenderTask task = engine.createRunAndRenderTask(design);
            
            // Gestione parametri JSON
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
            config.setLogConfig(birtHome, Level.FINE);
            
            Platform.startup(config);
            IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(
                IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY
            );
            IReportEngine engine = factory.createReportEngine(config);
            IReportRunnable design = engine.openReportDesign(sourceBirt);
            IRunAndRenderTask task = engine.createRunAndRenderTask(design);
            
            // Gestione parametri JSON
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
            config.setLogConfig(birtHome, Level.FINE);
            
            Platform.startup(config);
            IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(
                IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY
            );
            IReportEngine engine = factory.createReportEngine(config);
            IReportRunnable design = engine.openReportDesign(sourceBirt);
            IRunAndRenderTask task = engine.createRunAndRenderTask(design);
            
            // Gestione parametri JSON
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
            config.setLogConfig(birtHome, Level.FINE);
            
            Platform.startup(config);
            IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(
                IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY
            );
            IReportEngine engine = factory.createReportEngine(config);
            IReportRunnable design = engine.openReportDesign(sourceBirt);
            IRunAndRenderTask task = engine.createRunAndRenderTask(design);
            
            // Gestione parametri JSON
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
     * Metodo helper per gestire i parametri JSON comuni a tutti i formati
     */
    private void setJsonParameters(IReportEngine engine, IReportRunnable design, IRunAndRenderTask task) throws Exception {
        if(sourceJson.startsWith("http")) {
            // Documento con parametro json url
            task.setParameterValue("JsonSource", json);
            task.setParameterValue("json", sourceJson);
        } else {
            // Documento senza parametro json url
            json = new String(Files.readAllBytes(Paths.get(sourceJson)), StandardCharsets.UTF_8);
            
            // Leggi automaticamente i parametri dal design del report
            IGetParameterDefinitionTask paramTask = engine.createGetParameterDefinitionTask(design);
            @SuppressWarnings("unchecked")
            Collection<IParameterDefnBase> params = paramTask.getParameterDefns(false);
            
            for (IParameterDefnBase param : params) {
                if (param instanceof IScalarParameterDefn) {
                    IScalarParameterDefn scalarParam = (IScalarParameterDefn) param;
                    String paramName = scalarParam.getName();
                    int dataType = scalarParam.getDataType();
                    
                    // Imposta solo per parametri di tipo stringa
                    if (dataType == IScalarParameterDefn.TYPE_STRING) {
                        task.setParameterValue(paramName, json);
                    }
                }
            }
            paramTask.close();
        }
    }
}