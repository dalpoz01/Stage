package model;

import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IParameterDefnBase;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.eclipse.birt.core.framework.Platform;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.logging.Level;

public class BirtDesignToDocument {
    private final String sourceJson, sourceBirt;
    private String json="";
    
    public BirtDesignToDocument(String sourceJson, String sourceBirt) {
        this.sourceJson = sourceJson;
        this.sourceBirt = sourceBirt;
    }
    
    /**
     * Genera un documento nel formato specificato
     * Formato di output: "PDF", "DOC", "XLSX", "HTML"
     * @return true se la generazione ha successo, false altrimenti
     */
    public boolean generateDocument(String format) throws Exception {
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
                return false;
        }
    }
    
    /**
     * Genera documento PDF
     */
    public boolean generatePDF() throws Exception{
        try{
            EngineConfig config = new EngineConfig();
            config.setEngineHome(System.getProperty("user.home")+"/Desktop/BirtPDF");
            config.setLogConfig(System.getProperty("user.home")+"/Desktop/BirtPDF", Level.FINE);
            
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
            
            PDFRenderOption options = new PDFRenderOption();
            options.setOutputFileName(System.getProperty("user.home")+"/Desktop/"+namefile);
            options.setOutputFormat("pdf");
            
            task.setRenderOption(options);
            task.run();
            task.close();
            engine.destroy();
            Platform.shutdown();
            
            System.out.println("PDF generato: " + namefile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Genera documento DOC (Word)
     */
    public boolean generateDOC() throws Exception {
        try {
            EngineConfig config = new EngineConfig();
            config.setEngineHome(System.getProperty("user.home")+"/Desktop/BirtPDF");
            config.setLogConfig(System.getProperty("user.home")+"/Desktop/BirtPDF", Level.FINE);
            
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
            
            // Opzioni per generare DOC
            RenderOption options = new RenderOption();
            options.setOutputFileName(System.getProperty("user.home")+"/Desktop/"+namefile);
            options.setOutputFormat("doc");
            
            task.setRenderOption(options);
            task.run();
            task.close();
            engine.destroy();
            Platform.shutdown();
            
            System.out.println("DOC generato: " + namefile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Genera documento XLSX (Excel)
     */
    public boolean generateXLSX() throws Exception {
        try {
            EngineConfig config = new EngineConfig();
            config.setEngineHome(System.getProperty("user.home")+"/Desktop/BirtPDF");
            config.setLogConfig(System.getProperty("user.home")+"/Desktop/BirtPDF", Level.FINE);
            
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
            
            // Opzioni per generare XLSX
            EXCELRenderOption options = new EXCELRenderOption();
            options.setOutputFileName(System.getProperty("user.home")+"/Desktop/"+namefile);
            options.setOutputFormat("xlsx");
            
            task.setRenderOption(options);
            task.run();
            task.close();
            engine.destroy();
            Platform.shutdown();
            
            System.out.println("XLSX generato: " + namefile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Genera documento HTML
     */
    public boolean generateHTML() throws Exception {
        try {
            EngineConfig config = new EngineConfig();
            config.setEngineHome(System.getProperty("user.home")+"/Desktop/BirtPDF");
            config.setLogConfig(System.getProperty("user.home")+"/Desktop/BirtPDF", Level.FINE);
            
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
            
            // Opzioni per generare HTML
            HTMLRenderOption options = new HTMLRenderOption();
            options.setOutputFileName(System.getProperty("user.home")+"/Desktop/"+namefile);
            options.setOutputFormat("html");
            
            task.setRenderOption(options);
            task.run();
            task.close();
            engine.destroy();
            Platform.shutdown();
            
            System.out.println("HTML generato: " + namefile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
