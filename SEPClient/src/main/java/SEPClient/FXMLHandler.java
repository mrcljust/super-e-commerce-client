package SEPClient;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.*;
import javafx.geometry.Rectangle2D;

public class FXMLHandler extends Application {
	private static Scene scene;
	private static Stage stage;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		// Zeige Start-Szene in neuem Fenster
		OpenSceneAndStage("Start", "Super-E-commerce-Platform", false, true);

		Client client = new Client();
		client.start();
		if (client.isStarted)
		{
			// Client hat erfolgreich Verbindung zum Server hergestellt
		} else {
			// Fehler bei Verbindungsherstellung zum Server, zeige Fehlermeldung

			ShowMessageBox("Es konnte keine Verbindung zum Server hergestellt werden. Das Programm wird beendet.",
					"Fehler bei Verbindung zum Server", "Fehler bei Verbindung zum Server", AlertType.ERROR, true,
					true);
		}
	}
	
	public static Stage getStage()
	{
		return stage;
	}

	private static Parent CreateParent(String sceneName) {
		// Die Methode übergibt einen Parent aus einer fxml-Datei, welcher benötigt
		// wird, um eine Szene zu öffnen
		try {
			return FXMLLoader.load(FXMLHandler.class.getResource("/SEPClient/UI/" + sceneName + ".fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void OpenSceneAndStage(String sceneName, String sceneTitle, boolean resizable, boolean startCentered) {
		// Die Methode öffnet eine Szene aus dem UI Package und vergibt den Titel
		try {
			scene = new Scene(CreateParent(sceneName));
			if(stage==null)
				stage = new Stage();
			stage.setScene(scene);
			stage.setTitle(sceneTitle);
			stage.setResizable(resizable);
			stage.show();
			
			if(startCentered)
			{
				Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
				stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
				stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void OpenSceneInStage(Stage _stage, String sceneName, String sceneTitle, boolean resizable, boolean startCentered) {
		// Die Methode öffnet eine Szene aus dem UI Package und vergibt den Titel
		try {
			scene = new Scene(CreateParent(sceneName));
			stage=_stage;
			stage.setScene(scene);
			stage.setTitle(sceneTitle);
			stage.setResizable(resizable);
			stage.show();
			
			if(startCentered)
			{
				Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
				stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
				stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
			}
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
}
