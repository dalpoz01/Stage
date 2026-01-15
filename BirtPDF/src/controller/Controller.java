package controller;

import java.awt.FileDialog;

import javax.swing.JDialog;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import model.BirtDesignToDocument;
import model.ReportConfig;
import view.Window;

public class Controller {
    private Window view;
    private ReportConfig config;

    public Controller(Window view, ReportConfig repConfig) {
        this.view = view;
        this.config = repConfig;
        initController();
    }

    private void initController() {
        // Collega gli eventi della view ai metodi del controller
        view.getBtnLoadBirt().addActionListener(e -> handleLoadBirt());
        view.getBtnLoadJson().addActionListener(e -> handleLoadJson());
        view.getBtnGenerateDocument().addActionListener(e -> handleGenerateDocument());
        view.getComboBox_JsonType().addItemListener(e -> handleJsonTypeChange());
        
        // Aggiungi listener per validare i campi
        view.getTextField_JsonFile().getDocument().addDocumentListener(
            new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { validateInputs(); }
                public void removeUpdate(DocumentEvent e) { validateInputs(); }
                public void changedUpdate(DocumentEvent e) { validateInputs(); }
            }
        );
        
        view.getTextField_BirtFile().getDocument().addDocumentListener(
            new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { validateInputs(); }
                public void removeUpdate(DocumentEvent e) { validateInputs(); }
                public void changedUpdate(DocumentEvent e) { validateInputs(); }
            }
        );
    }

    private void handleLoadBirt() {
        String filePath = chooseBirtFile();
        if (filePath != null) {
            config.setBirtFilePath(filePath);
            view.getTextField_BirtFile().setText(filePath);
        }
    }


    private void handleLoadJson() {
    	String filePath = chooseJsonFile();
        if (filePath != null) {
            config.setJsonSource(filePath);
            view.getTextField_JsonFile().setText(filePath);
        }
    }

    private void handleJsonTypeChange() {
        String selectedType = view.getComboBox_JsonType().getSelectedItem().toString();
        boolean isAPI = "API".equals(selectedType);
        
        config.setJsonSourceType(selectedType);
        view.getBtnLoadJson().setEnabled(!isAPI);
        view.getBtnLoadJson().setVisible(!isAPI);
        view.getTextField_JsonFile().setEditable(isAPI);
        view.getTextField_JsonFile().setText("");
        if(isAPI) {
        	view.getTextField_JsonFile().setToolTipText("Inserisci l'indirizzo API del JSON");
        }else {
        	view.getTextField_JsonFile().setToolTipText("Carica il file JSON");
        }
    }

    private void validateInputs() {
        String jsonText = view.getTextField_JsonFile().getText();
        String birtText = view.getTextField_BirtFile().getText();
        String jsonType = view.getComboBox_JsonType().getSelectedItem().toString();
        
        boolean jsonValid = false;
        if ("API".equals(jsonType)) {
            jsonValid = jsonText.startsWith("http");
        } else {
            jsonValid = !jsonText.isBlank();
        }
        
        boolean birtValid = !birtText.isBlank();
        
        view.getBtnGenerateDocument().setEnabled(jsonValid && birtValid);
    }

    private void handleGenerateDocument() {
        // Aggiorna la configurazione con i valori correnti
        config.setJsonSource(view.getTextField_JsonFile().getText());
        config.setOutputFormat(view.getComboBox_Formato().getSelectedItem().toString());
        
        // Valida i dati
        if (!config.isValidConfig()) {
            view.showErrorDialog("Controlla che tutti i campi siano corretti!");
            return;
        }        
        String formatoSelezionato = config.getOutputFormat();     
        // Mostra dialog di caricamento e genera il documento in background
        JDialog loadingDialog = view.createLoadingDialog(
            "Creazione report " + formatoSelezionato + " in corso..."
        );
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                BirtDesignToDocument generator = new BirtDesignToDocument(
                    config.getJsonSource(), 
                    config.getBirtFilePath()
                );
                return generator.generateDocument(formatoSelezionato);
            }
            @Override
            protected void done() {
                loadingDialog.dispose();
                try {
                    boolean success = get();
                    if (success) {
                        view.showSuccessDialog("Documento " + formatoSelezionato + " generato con successo!");
                    } else {
                        view.showErrorDialog("Errore nella generazione del documento!");
                    }
                } catch (Exception e) {
                    view.showErrorDialog("Errore: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
        loadingDialog.setVisible(true);
    }
    
    public String chooseBirtFile() {
        FileDialog fileDialog = new FileDialog(
            view.getFrame(),
            "Seleziona file BIRT",
            FileDialog.LOAD
        );
        fileDialog.setFile("*.rptdesign");
        fileDialog.setVisible(true);

        String fileName = fileDialog.getFile();
        String directory = fileDialog.getDirectory();

        if (fileName != null && directory != null) {
            return directory + fileName;
        }
        return null;
    }
    
    public String chooseJsonFile() {
    	FileDialog fileDialog = new FileDialog(
            view.getFrame(), 
            "Seleziona file JSON", 
            FileDialog.LOAD
        );
        fileDialog.setFile("*.json");
        fileDialog.setVisible(true);

        String fileName = fileDialog.getFile();
        String directory = fileDialog.getDirectory();

        if (fileName != null && directory != null) {
            return directory + fileName;
        }
        return null;
    }


}