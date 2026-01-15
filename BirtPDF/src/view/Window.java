package view;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Toolkit;

public class Window {
    private JFrame frame;
    private JPanel panel;
    private JTextField textField_JsonFile, textField_BirtFile;
    private JLabel lblLocalBirtJson, lblFormato, lblLocalJson;
    private JButton btnLoadBirt, btnLoadJson, btnGenerateDocument;
    private JComboBox<String> comboBox_Formato, comboBox_JsonType;

    public Window() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("C:/Users/stage01/Desktop/icon_nosfondo.png"));
        frame.setResizable(false);
        frame.setFont(new Font("Arial", Font.PLAIN, 12));
        frame.setTitle("BirtToReport");
        frame.setBounds(100, 100, 500, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel();
        panel.setLayout(null);
        frame.getContentPane().add(panel, BorderLayout.CENTER);

        lblLocalJson = new JLabel("Source JSON:");
        lblLocalJson.setBounds(10, 11, 102, 23);
        panel.add(lblLocalJson);

        textField_JsonFile = new JTextField();
        textField_JsonFile.setToolTipText("Inserisci l'indirizzo API del JSON");
        textField_JsonFile.setBounds(111, 12, 178, 23);
        panel.add(textField_JsonFile);
        textField_JsonFile.setColumns(10);

        comboBox_JsonType = new JComboBox<>();
        comboBox_JsonType.setModel(new DefaultComboBoxModel<>(new String[]{"API", "FILE"}));
        comboBox_JsonType.setBounds(299, 11, 72, 23);
        panel.add(comboBox_JsonType);

        btnLoadJson = new JButton("Carica");
        btnLoadJson.setEnabled(false);
        btnLoadJson.setVisible(false);
        btnLoadJson.setBounds(380, 11, 89, 23);
        panel.add(btnLoadJson);

        lblLocalBirtJson = new JLabel("File Birt:");
        lblLocalBirtJson.setBounds(10, 45, 102, 23);
        panel.add(lblLocalBirtJson);

        textField_BirtFile = new JTextField();
        textField_BirtFile.setEditable(false);
        textField_BirtFile.setToolTipText("Carica il file .rptdesign");
        textField_BirtFile.setBounds(111, 46, 259, 23);
        panel.add(textField_BirtFile);
        textField_BirtFile.setColumns(10);

        btnLoadBirt = new JButton("Carica");
        btnLoadBirt.setBounds(380, 45, 89, 23);
        panel.add(btnLoadBirt);

        lblFormato = new JLabel("Formato output");
        lblFormato.setBounds(10, 80, 89, 23);
        panel.add(lblFormato);

        comboBox_Formato = new JComboBox<>();
        comboBox_Formato.setModel(new DefaultComboBoxModel<>(new String[]{"PDF", "XLSX", "HTML", "DOC"}));
        comboBox_Formato.setBounds(111, 80, 110, 23);
        panel.add(comboBox_Formato);

        btnGenerateDocument = new JButton("Genera Documento");
        btnGenerateDocument.setEnabled(false);
        btnGenerateDocument.setBounds(111, 120, 259, 52);
        panel.add(btnGenerateDocument);

        frame.setVisible(true);
    }

    // Metodi per mostrare dialog (parte della View)
    public JDialog createLoadingDialog(String message) {
        JDialog loadingDialog = new JDialog(frame, "Caricamento", true);
        JLabel loadingLabel = new JLabel(message, JLabel.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        loadingDialog.getContentPane().add(loadingLabel);
        loadingDialog.setSize(300, 100);
        loadingDialog.setLocationRelativeTo(frame);
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        return loadingDialog;
    }

    public void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(
            frame,
            message,
            "Operazione completata",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
            frame,
            message,
            "Errore!",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public JFrame getFrame() {
        return frame;
    }

    public JTextField getTextField_JsonFile() {
        return textField_JsonFile;
    }

    public JTextField getTextField_BirtFile() {
        return textField_BirtFile;
    }

    public JButton getBtnLoadBirt() {
        return btnLoadBirt;
    }

    public JButton getBtnLoadJson() {
        return btnLoadJson;
    }

    public JButton getBtnGenerateDocument() {
        return btnGenerateDocument;
    }

    public JComboBox<String> getComboBox_Formato() {
        return comboBox_Formato;
    }

    public JComboBox<String> getComboBox_JsonType() {
        return comboBox_JsonType;
    }
}