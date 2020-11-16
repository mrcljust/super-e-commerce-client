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

public class UIHandler extends Application   {
	private Scene scene;
	private Stage stage;
	private Client client;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		//Zeige Start-Szene
		stage = new Stage();
		OpenScene("Start", "Super-E-commerce-Platform");
		
		client = new Client();
		if(client.start())
		{
			//Client hat erfolgreich Verbindung zum Server hergestellt
		}
		else
		{
			//Fehler bei Verbindungsherstellung zum Server, zeige Fehlermeldung
			
			ShowMessageBox("Es konnte keine Verbindung zum Server hergestellt werden. Das Programm wird beendet.", "Fehler bei Verbindung zum Server", "Fehler bei Verbindung zum Server", AlertType.ERROR, true, true);
		}
	}
	
	public void OpenScene(String sceneName, String sceneTitle)
	{
		//Die Methode öffnet eine Szene aus dem UI Package und vergibt den Titel
		try {
			scene = new Scene(CreateParent(sceneName));
			stage.setScene(scene);
			stage.setTitle(sceneTitle);
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void ShowMessageBox(String message, String title, String header, AlertType type, boolean wait, boolean exitAfter)
	{
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(message);
		if(wait)
		{
			alert.showAndWait();
			if(exitAfter)
			{
				System.exit(0);
			}
		}
		else
		{
			alert.show();
		}
	}
	// Idee: Je nach Button -> Klasse Eventhandler die je nach Button etwas macht
	
	private Parent CreateParent(String sceneName) throws IOException
	{
		//Die Methode übergibt einen Parent aus einer fxml-Datei, welcher benötigt wird, um eine Szene zu öffnen
		return FXMLLoader.load(getClass().getResource("/SEPClient/UI/" + sceneName + ".fxml"));
	}
}
