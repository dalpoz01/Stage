package main;

import java.awt.EventQueue;

import controller.Controller;
import model.ReportConfig;
import view.Window;

public class Main {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
					ReportConfig reportConfig = new ReportConfig();
					@SuppressWarnings("unused")
					Controller controller = new Controller(window, reportConfig);
				} catch (Exception e) {
					System.out.println("Errore avvio applicazione: " + e.getMessage());
					e.printStackTrace();
				}
			}
		});        
	}
}
/*final String sourceFile="C:/Users/stage01/Desktop/Customers.rptdesign";
final String sourceJson="C:/Users/stage01/Desktop/users.json";
boolean generatePDF=false;
if (sourceFile.endsWith(".rptdesign")) {
	BirtDesignToDocument bdr= new BirtDesignToDocument(sourceJson, sourceFile);
	try {
		generatePDF=bdr.generatePDF();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
if (sourceFile.endsWith(".jrxml")) {
	JxmlToDocument jxml= new JxmlToDocument(sourceJson, sourceFile);
	try {
		generatePDF=jxml.generatePDF();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
if(sourceFile.endsWith(".jasper")){
	JasperToDocument jasperFile = new JasperToDocument(sourceJson, sourceFile);
	try {
		generatePDF=jasperFile.generatePDF();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}    	
}
if((!sourceFile.endsWith(".rptdesign"))&&(!sourceFile.endsWith(".jrxml"))&&(!sourceFile.endsWith(".jasper"))) {
	System.out.println("File di design non supportato");
}else {
	if(generatePDF) {
    	System.out.println("DOCUMENTO GENERATO!");
    }else {
    	System.out.println("ERRORE NELLA GENERAZIONE DEL DOCUMENTO!");
    }
}*/
