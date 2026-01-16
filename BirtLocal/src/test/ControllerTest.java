package test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;

import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import controller.Controller;
import model.ReportConfig;
import view.Window;

@ExtendWith(MockitoExtension.class)

class ControllerTest {
	Window view;
	ReportConfig config;
	Controller controller;
	
	@BeforeEach
	void setUp() {
	    view = new Window();
	    config = new ReportConfig();
	    controller = new Controller(view, config);
	}
	
	@Test
	void initController_collegaTuttiIListener() {
	    assertTrue(view.getBtnLoadBirt().getActionListeners().length > 0);
	    assertTrue(view.getBtnLoadJson().getActionListeners().length > 0);
	    assertTrue(view.getBtnGenerateDocument().getActionListeners().length > 0);
	    assertTrue(view.getComboBox_JsonType().getItemListeners().length > 0);
	}
	
	@Test
	void abilitaBtnGeneraDocumento_seJsonApiValidoEBirtValido() {	
		JButton btnJson = view.getBtnLoadJson();
		assertFalse(btnJson.isEnabled()&&btnJson.isVisible(), "Bottone Carica Json deve partire disabilitato e non visibile");
		JButton btnGen  = view.getBtnGenerateDocument();
		assertFalse(btnGen.isEnabled(), "Bottone Genera deve partire disabilitato");
		JTextField textField_JsonFile = view.getTextField_JsonFile();
		JTextField txtBirt = view.getTextField_BirtFile();
		JComboBox<String> comboBox_JsonType= view.getComboBox_JsonType();
		assertEquals("API", comboBox_JsonType.getSelectedItem(), "Default della combo deve essere API");
		
		textField_JsonFile.setText("http://example.com/data.json");
        txtBirt.setText("/path/al/file.rptdesign");
        
        assertTrue(btnGen.isEnabled(), "Bottone Genera deve essere abilitato con input validi");
	}
	@Test
	void abilitaBtnGeneraDocumento_seJsonFileValidoEBirtValido() {		
		JButton btnJson = view.getBtnLoadJson();
		assertFalse(btnJson.isEnabled()&&btnJson.isVisible(), "Bottone Carica Json deve partire disabilitato e non visibile");
		JButton btnGen  = view.getBtnGenerateDocument();
		assertFalse(btnGen.isEnabled(), "Bottone Genera deve partire disabilitato");
		
		JTextField textField_JsonFile = view.getTextField_JsonFile();
		JTextField txtBirt = view.getTextField_BirtFile();
		JComboBox<String> comboBox_JsonType= view.getComboBox_JsonType();
		assertEquals("API", comboBox_JsonType.getSelectedItem(), "Default della combo deve essere API");
		assertTrue(textField_JsonFile.isEditable(), "textField Json deve essere abilitata");
		//Cambio valore combobox
		comboBox_JsonType.setSelectedItem("FILE");
		assertEquals("FILE", comboBox_JsonType.getSelectedItem(), "Valore della combo deve essere FILE");
		assertTrue(btnJson.isEnabled()&&btnJson.isVisible(), "Bottone Load Json deve essere abilitato e visibile");
		assertFalse(textField_JsonFile.isEditable(), "textField Json deve essere disabilitata");
		textField_JsonFile.setText("/path/al/file.json");
		
        txtBirt.setText("/path/al/file.rptdesign");
        
        assertTrue(btnGen.isEnabled(), "Bottone Genera deve essere abilitato con input validi");
	}
	@Test
	void generaDocumentoDisabilitato_seJsonAPINonIniziaConHTTPBirtValido() {		
		JButton btnJson = view.getBtnLoadJson();
		assertFalse(btnJson.isEnabled()&&btnJson.isVisible(), "Bottone Carica Json deve partire disabilitato e non visibile");
		JButton btnGen  = view.getBtnGenerateDocument();
		assertFalse(btnGen.isEnabled(), "Bottone Genera deve partire disabilitato");
		
		JComboBox<String> comboBox_JsonType= view.getComboBox_JsonType();
		assertEquals("API", comboBox_JsonType.getSelectedItem(), "Valore della combo deve essere API");
		
		JTextField textField_JsonFile = view.getTextField_JsonFile();
		JTextField txtBirt = view.getTextField_BirtFile();
		
		textField_JsonFile.setText("qualcosa che non inizia con http");
		txtBirt.setText("/path/al/file.rptdesign");
		
		assertFalse(btnGen.isEnabled(), "Bottone Genera deve rimanere disabilitato");
		
	}
	
	@Test
	void generaDocumentoDisabilitato_seJsonFileVuotoBirtValido() {		
		JButton btnJson = view.getBtnLoadJson();
		JTextField textField_JsonFile = view.getTextField_JsonFile();
		JTextField txtBirt = view.getTextField_BirtFile();
		assertFalse(btnJson.isEnabled()&&btnJson.isVisible(), "Bottone Carica Json deve partire disabilitato e non visibile");
		JButton btnGen  = view.getBtnGenerateDocument();
		assertFalse(btnGen.isEnabled(), "Bottone Genera deve partire disabilitato");
		JComboBox<String> comboBox_JsonType= view.getComboBox_JsonType();
		assertEquals("API", comboBox_JsonType.getSelectedItem(), "Valore della combo deve essere API");
		
		comboBox_JsonType.setSelectedItem("FILE");
		assertEquals("FILE", comboBox_JsonType.getSelectedItem(), "Valore della combo deve essere FILE");
		assertTrue(btnJson.isEnabled()&&btnJson.isVisible(), "Bottone Load Json deve essere abilitato e visibile");
		textField_JsonFile.setText("");
		txtBirt.setText("/path/al/file.rptdesign");
		
		assertFalse(btnGen.isEnabled(), "Bottone Genera deve rimanere disabilitato");		
	}
	
	@Test
	void generaDocumentoDisabilitato_seJsonFileValidoBirtVuoto() {		
		JButton btnJson = view.getBtnLoadJson();
		JTextField textField_JsonFile = view.getTextField_JsonFile();
		JTextField txtBirt = view.getTextField_BirtFile();
		assertFalse(btnJson.isEnabled()&&btnJson.isVisible(), "Bottone Carica Json deve partire disabilitato e non visibile");
		JButton btnGen  = view.getBtnGenerateDocument();
		assertFalse(btnGen.isEnabled(), "Bottone Genera deve partire disabilitato");
		JComboBox<String> comboBox_JsonType= view.getComboBox_JsonType();
		assertEquals("API", comboBox_JsonType.getSelectedItem(), "Valore della combo deve essere API");
		
		comboBox_JsonType.setSelectedItem("FILE");
		assertEquals("FILE", comboBox_JsonType.getSelectedItem(), "Valore della combo deve essere FILE");
		assertTrue(btnJson.isEnabled()&&btnJson.isVisible(), "Bottone Load Json deve essere abilitato e visibile");
		textField_JsonFile.setText("/path/al/file.json");
		txtBirt.setText("");
		
		assertFalse(btnGen.isEnabled(), "Bottone Genera deve rimanere disabilitato");		
	}
	
	@Test
	void generaDocumentoDisabilitato_seJsonApiValidoBirtVuoto() {		
		JButton btnJson = view.getBtnLoadJson();
		JTextField textField_JsonFile = view.getTextField_JsonFile();
		JTextField txtBirt = view.getTextField_BirtFile();
		assertFalse(btnJson.isEnabled()&&btnJson.isVisible(), "Bottone Carica Json deve partire disabilitato e non visibile");
		JButton btnGen  = view.getBtnGenerateDocument();
		assertFalse(btnGen.isEnabled(), "Bottone Genera deve partire disabilitato");
		JComboBox<String> comboBox_JsonType= view.getComboBox_JsonType();
		assertEquals("API", comboBox_JsonType.getSelectedItem(), "Valore della combo deve essere API");
		
		textField_JsonFile.setText("http");
		txtBirt.setText("");
		
		assertFalse(btnGen.isEnabled(), "Bottone Genera deve rimanere disabilitato");		
	}
	
	@Test
	void handleJsonTypeChange() {
		JButton btnJson = view.getBtnLoadJson();
		JTextField textField_JsonFile = view.getTextField_JsonFile();
		JComboBox<String> comboBox_JsonType= view.getComboBox_JsonType();
		assertEquals("API", comboBox_JsonType.getSelectedItem(), "Valore della combo deve essere API");
		assertFalse(btnJson.isEnabled()&&btnJson.isVisible(), "Bottone Carica Json deve partire disabilitato e non visibile");
		assertTrue(textField_JsonFile.isEditable(), "textField Json deve essere abilitata");
		comboBox_JsonType.setSelectedItem("FILE");
		assertEquals("FILE", comboBox_JsonType.getSelectedItem(), "Valore della combo deve essere FILE");
		assertTrue(btnJson.isEnabled()&&btnJson.isVisible(), "Bottone Carica Json deve partire abilitato e visibile");
		assertFalse(textField_JsonFile.isEditable(), "textField Json deve essere disabilitata");
	}
	
	@Test
	void handleLoadBirt_fileSelezionato_aggiornaModelEView() throws Exception {
	    // --- Config mock (verifichiamo le chiamate) ---
	    ReportConfig config = mock(ReportConfig.class);
		Controller controller = spy(new Controller(view, config));
		String fakeBirtPath = "/path/al/report.rptdesign";
	    doReturn(fakeBirtPath)
	        .when(controller)
	        .chooseBirtFile();

	    // --- Recuperiamo il metodo privato ---
	    Method method = Controller.class.getDeclaredMethod("handleLoadBirt");
	    method.setAccessible(true);

	    // --- Esecuzione ---
	    method.invoke(controller);

	    // --- Verifica: model aggiornato ---
	    verify(config).setBirtFilePath(fakeBirtPath);

	    // --- Verifica: view aggiornata ---
	    assertEquals(
	        fakeBirtPath,
	        view.getTextField_BirtFile().getText(),
	        "La textfield BIRT deve contenere il path selezionato"
	    );
	}
	
	@Test
	void handleLoadBirt_utenteAnnullaSelezione_fileNonCaricato() throws Exception{
		ReportConfig config = mock(ReportConfig.class);

	    // Spy del controller per intercettare chooseBirtFile()
	    Controller controller = spy(new Controller(view, config));

	    // Simuliamo l'annullamento del file chooser
	    doReturn(null).when(controller).chooseBirtFile();

	    JButton btnGenerate = view.getBtnGenerateDocument();
	    JTextField txtBirt = view.getTextField_BirtFile();

	    // Stato iniziale
	    assertFalse(btnGenerate.isEnabled(), "Il bottone Genera deve partire disabilitato");
	    assertTrue(txtBirt.getText().isBlank(), "La textfield BIRT deve partire vuota");

	    // Richiamiamo handleLoadBirt (via reflection se non pubblico)
	    Method method = Controller.class.getDeclaredMethod("handleLoadBirt");
	    method.setAccessible(true);
	    method.invoke(controller);

	    // La textfield NON deve cambiare
	    assertTrue(txtBirt.getText().isBlank(),
	            "La textfield BIRT deve rimanere vuota se l'utente annulla");

	    // Il bottone Genera resta disabilitato
	    assertFalse(btnGenerate.isEnabled(),
	            "Il bottone Genera deve restare disabilitato");

	    // Il model NON deve essere aggiornato
	    verify(config, never()).setBirtFilePath(anyString());
	}
	
	@Test
	void handleLoadJson_fileSelezionato_aggiornaModelEView() throws Exception {
	    // --- Config mock (verifichiamo le chiamate) ---
	    ReportConfig config = mock(ReportConfig.class);
		Controller controller = spy(new Controller(view, config));
		String fakeJsonPath = "/path/al/file.json";
	    doReturn(fakeJsonPath)
	        .when(controller)
	        .chooseJsonFile();

	    // --- Recuperiamo il metodo privato ---
	    Method method = Controller.class.getDeclaredMethod("handleLoadJson");
	    method.setAccessible(true);

	    // --- Esecuzione ---
	    method.invoke(controller);

	    // --- Verifica: model aggiornato ---
	    verify(config).setJsonSource(fakeJsonPath);

	    // --- Verifica: view aggiornata ---
	    assertEquals(
	        fakeJsonPath,
	        view.getTextField_JsonFile().getText(),
	        "La textfield Json deve contenere il path selezionato"
	    );
	}
	
	@Test
	void handleLoadJson_utenteAnnullaSelezione_fileNonCaricato() throws Exception{
		ReportConfig config = mock(ReportConfig.class);

	    // Spy del controller per intercettare chooseJsonFile()
	    Controller controller = spy(new Controller(view, config));

	    // Simuliamo l'annullamento del file chooser
	    doReturn(null).when(controller).chooseJsonFile();

	    JButton btnGenerate = view.getBtnGenerateDocument();
	    JTextField txtJson = view.getTextField_JsonFile();

	    // Stato iniziale
	    assertFalse(btnGenerate.isEnabled(), "Il bottone Genera deve partire disabilitato");
	    assertTrue(txtJson.getText().isBlank(), "La textfield Json deve partire vuota");

	    // Richiamiamo handleLoadJson (via reflection se non pubblico)
	    Method method = Controller.class.getDeclaredMethod("handleLoadJson");
	    method.setAccessible(true);
	    method.invoke(controller);

	    // La textfield NON deve cambiare
	    assertTrue(txtJson.getText().isBlank(),
	            "La textfield Json deve rimanere vuota se l'utente annulla");

	    // Il bottone Genera resta disabilitato
	    assertFalse(btnGenerate.isEnabled(),
	            "Il bottone Genera deve restare disabilitato");

	    // Il model NON deve essere aggiornato
	    verify(config, never()).setJsonSource(anyString());
	}
}
