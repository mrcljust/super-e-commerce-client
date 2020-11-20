package SEPClient;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

public class FXMLHandler extends Application {
	private Scene scene;
	private Stage stage;
	private Client client;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		// Zeige Start-Szene in neuem Fenster
		OpenSceneAndStage("Start", "Super-E-commerce-Platform");

		client = new Client();
		if (client.start()) {
			// Client hat erfolgreich Verbindung zum Server hergestellt
		} else {
			// Fehler bei Verbindungsherstellung zum Server, zeige Fehlermeldung

			ShowMessageBox("Es konnte keine Verbindung zum Server hergestellt werden. Das Programm wird beendet.",
					"Fehler bei Verbindung zum Server", "Fehler bei Verbindung zum Server", AlertType.ERROR, true,
					true);
		}
	}

	public void OpenSceneAndStage(String sceneName, String sceneTitle) {
		// Die Methode �ffnet eine Szene aus dem UI Package und vergibt den Titel
		try {
			scene = new Scene(CreateParent(sceneName));
			if(stage==null)
				stage = new Stage();
			stage.setScene(scene);
			stage.setTitle(sceneTitle);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void OpenSceneInStage(Stage _stage, String sceneName, String sceneTitle) {
		// Die Methode �ffnet eine Szene aus dem UI Package und vergibt den Titel
		try {
			scene = new Scene(CreateParent(sceneName));
			stage=_stage;
			stage.setScene(scene);
			stage.setTitle(sceneTitle);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void ShowMessageBox(String message, String title, String header, AlertType type, boolean wait,
			boolean exitAfter) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(message);
		if (wait) {
			alert.showAndWait();
			if (exitAfter) {
				System.exit(0);
			}
		} else {
			alert.show();
		}
	}
	// Idee: Je nach Button -> Klasse Eventhandler die je nach Button etwas macht

	private Parent CreateParent(String sceneName) throws IOException {
		// Die Methode �bergibt einen Parent aus einer fxml-Datei, welcher ben�tigt
		// wird, um eine Szene zu �ffnen
		return FXMLLoader.load(getClass().getResource("/SEPClient/UI/" + sceneName + ".fxml"));
	}
	
	//FXML OBJEKTE
	
    @FXML
    private Button Start_LoginButton;

    @FXML
    private Button Start_RegisterButton;
    
    //FXML EVENTS
	
    @FXML
    void Start_LoginClick(ActionEvent event) {
		OpenSceneInStage((Stage) Start_LoginButton.getScene().getWindow(), "Login", "Anmeldung");
	}

    @FXML
    void Start_RegisterClick(ActionEvent event) {
    	OpenSceneInStage((Stage) Start_RegisterButton.getScene().getWindow(), "Register", "Registrierung");
	}

	public void Register_OKClick(ActionEvent event) {

	}

	public void Register_ReturnClick(ActionEvent event) {
		
	}

	public void Login_OKClick(ActionEvent event) {

	}

	public void Login_ReturnClick(ActionEvent event) {
		
	}

	public void Register_OpenPictureClick(ActionEvent event) {

	}

	public void EditUser_OkClick(ActionEvent event) {

	}

	public void EditUser_ReturnClick(ActionEvent event) {

	}

	public void EdiutUser_OopenPicutre(ActionEvent event) {

	}

	public void Wallet_IncreaseClick(ActionEvent event) {

	}

	public void Wallet_ReturnClick(ActionEvent event) {

	}

	public void ReturnClick(ActionEvent event) {

	}

	public void EdituUser_Click(ActionEvent event) {

	}

	public void Searchbar_GoClick(ActionEvent event) {

	}

	public void BuyButton_Click(ActionEvent event) {

	}

	public void SellButton_Click(ActionEvent event) {

	}

	public void Sell_OpenCSV(ActionEvent event) {

	}

	public void SellConfirmButton_Click(ActionEvent event) {

	}

	public void CategoryDropDown_Change(ActionEvent event) {

	}

	public void PriceDropDown_Change(ActionEvent event) {

	}
}
