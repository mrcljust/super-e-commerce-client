package SEPClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class StartController {

	@FXML
	private Button Start_LoginButton;

	@FXML
	private Button Start_RegisterButton;

	@FXML
	public void initialize() {
		Start_LoginButton.requestFocus();
	}

	// Login wird ausgewählt --> Login Scene wird geöffnet
	@FXML
	void Start_LoginClick(ActionEvent event) {
		FXMLHandler.OpenSceneInStage((Stage) Start_LoginButton.getScene().getWindow(), "Login", "Anmeldung", false,
				true);
	}

	// Registrierung wird ausgewählt --> Register Scene wird geöffnet
	@FXML
	void Start_RegisterClick(ActionEvent event) {
		FXMLHandler.OpenSceneInStage((Stage) Start_RegisterButton.getScene().getWindow(), "Register", "Registrierung",
				false, true);
	}

	@FXML
	void MainScreen_InfoButtonMenuClick(ActionEvent event) {

		FXMLHandler.ShowMessageBox(
				"© 'Super-E-commerce-Platform' wurde entwickelt von Denis Artjuch, Yannis Bromby, Marcel Just und Hannah Kalker. Gruppe B, Modul Software Entwicklung & Programmierung, Universität Duisburg-Essen, 2020/21.",
				"Super-E-commerce-Platform", "Super-E-commerce-Platform", AlertType.INFORMATION, true, false);
	}

	@FXML
	void MainScreen_CreditsButtonMenuClick(ActionEvent event) {
		FXMLHandler.ShowMessageBox(
				"net.coobird.thumbnailator - 0.4.13" + System.lineSeparator()
						+ "com.dlsc.GMapsFX - 11.0.1, https://github.com/dlsc-software-consulting-gmbh/GMapsFX"
						+ System.lineSeparator() + "com.google.maps:google-maps-services - 0.15.0, https://github.com/googlemaps/google-maps-services-java"
						+ System.lineSeparator() + "org.slf4j:slf4j-simple - 1.7.25"
						+ System.lineSeparator() + "JavaFX - 15.0.1" + System.lineSeparator() + "JUnit - 4"
						+ System.lineSeparator() + "mysql.mysql-connector-java - 8.0.22" + System.lineSeparator()
						+ "com.sun.mail.javax.mail - 1.6.2" + System.lineSeparator()
						+ "Maps Icons Collection, https://mapicons.mapsmarker.com" + System.lineSeparator()
						+ "FreeLogoDesign, https://de.freelogodesign.org",
				"Super-E-commerce-Platform", "Super-E-commerce-Platform", AlertType.INFORMATION, true, false);
	}

	@FXML
	void MainScreen_GMapsFXMenuClick(ActionEvent event) throws IOException, URISyntaxException {
		java.awt.Desktop.getDesktop().browse(new URI("https://github.com/dlsc-software-consulting-gmbh/GMapsFX"));
	}

	@FXML
	void MainScreen_MapsIconCollectionMenuClick(ActionEvent event) throws IOException, URISyntaxException {
		java.awt.Desktop.getDesktop().browse(new URI("https://mapicons.mapsmarker.com"));
	}

	@FXML
	void MainScreen_FreeLogoDesignMenuClick(ActionEvent event) throws IOException, URISyntaxException {
		java.awt.Desktop.getDesktop().browse(new URI("https://de.freelogodesign.org"));
	}
	
	@FXML
	void MainScreen_GoogleMapsMenuClick(ActionEvent event) throws IOException, URISyntaxException {
		java.awt.Desktop.getDesktop().browse(new URI("https://github.com/googlemaps/google-maps-services-java"));
	}
}
