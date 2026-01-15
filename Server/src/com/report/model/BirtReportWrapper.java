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
 * Wrapper CLI per BirtDesignToDocument
 * Può essere chiamato da Python tramite subprocess
 * 
 * Usage: java com.report.model.BirtReportWrapper <birtFile> <jsonApiUrl> <outputDir> <birtHome> <format>
 */
public class BirtReportWrapper {
    
    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Usage: java BirtReportWrapper <birtFile> <jsonApiUrl> <outputDir> <birtHome> <format>");
            System.exit(1);
        }
        
        String birtFile = args[0];
        String jsonApiUrl = args[1];
        String outputDir = args[2];
        String birtHome = args[3];
        String format = args[4];
        
        try {
            BirtDesignToDocument generator = new BirtDesignToDocument(
                jsonApiUrl,
                birtFile,
                outputDir,
                birtHome
            );
            
            String outputPath = generator.generateDocument(format);
            
            if (outputPath != null) {
                // Stampa il path del file generato su stdout
                System.out.println(outputPath);
                System.exit(0);
            } else {
                System.err.println("Errore: generazione report fallita");
                System.exit(1);
            }
            
        } catch (Exception e) {
            System.err.println("Errore: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

/**
 * Classe BirtDesignToDocument
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
            
            return fullPath;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
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
            
            return fullPath;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
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
            
            return fullPath;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
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
            
            return fullPath;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    private void setJsonParameters(IReportEngine engine, IReportRunnable design, IRunAndRenderTask task) throws Exception {
        if(sourceJson.startsWith("http")) {
            task.setParameterValue("JsonSource", json);
            task.setParameterValue("json", sourceJson);
        } else {
            json = new String(Files.readAllBytes(Paths.get(sourceJson)), StandardCharsets.UTF_8);
            
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