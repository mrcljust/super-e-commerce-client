package SEPClient;

import java.io.IOException;
import java.util.Optional;

import SEPCommon.Constants;
import javafx.application.Application;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
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
			// Fehler bei Verbindungsherstellung zum Server, zeige Fehlermeldung und beende das Programm danach

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
		// Die Methode �bergibt einen Parent aus einer fxml-Datei aus den Resourcen des Projektes, welcher ben�tigt
		// wird, um eine Szene zu �ffnen
		try {
			return FXMLLoader.load(FXMLHandler.class.getResource("/SEPClient/UI/" + sceneName + ".fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void OpenSceneAndStage(String sceneName, String sceneTitle, boolean resizable, boolean startCentered) {
		// Die Methode �ffnet eine Szene aus dem UI Package und vergibt den Titel
		try {
			scene = new Scene(CreateParent(sceneName));
			if(stage==null)
				stage = new Stage();
			stage.getIcons().add(new Image(FXMLHandler.class.getResourceAsStream(Constants.CLIENT_LOGO_RESOURCE_PATH)));
			stage.setScene(scene);
			stage.setTitle(sceneTitle);
			stage.setResizable(resizable);
			stage.show();
			
			if(startCentered)
			{
				//zentriert starten
				Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
				stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
				stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void OpenSceneInStage(Stage _stage, String sceneName, String sceneTitle, boolean resizable, boolean startCentered) {
		// Die Methode �ffnet eine Szene aus dem UI Package und vergibt den Titel
		try {
			scene = new Scene(CreateParent(sceneName));
			stage=_stage;
			stage.getIcons().add(new Image(FXMLHandler.class.getResourceAsStream(Constants.CLIENT_LOGO_RESOURCE_PATH)));
			stage.setScene(scene);
			stage.setTitle(sceneTitle);
			stage.setResizable(resizable);
			stage.show();
			
			if(startCentered)
			{
				//zentriert starten
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
		//Wenn exitAfter=true ist, wird das Programm nach Wegklicken der Meldung beendet (z.B. bei einem Fehler o.�.)
		//Wenn wait=true ist, wird der folgende Quellcode erst ausgef�hrt, nachdem die Meldung beendet wurde.
		Alert alert = new Alert(type, message, ButtonType.OK);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); //Gr��e anpassen, da bei Yannis z.B. die Meldungen gek�rzt wurden
		
		//Icon hinzuf�gen
		((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(FXMLHandler.class.getResourceAsStream(Constants.CLIENT_LOGO_RESOURCE_PATH)));		
		
		if (wait) {
			alert.showAndWait();
			if (exitAfter) {
				System.exit(0);
			}
		} else {
			alert.show();
		}
	}
	
	public static Optional<ButtonType> ShowYesNoQuestionBox(String message, String title, String header, AlertType type) {
		Alert alert = new Alert(type, message, ButtonType.YES, ButtonType.NO);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); //Gr��e anpassen, da bei Yannis z.B. die Meldungen gek�rzt wurden
		
		//Icon hinzuf�gen
		((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(FXMLHandler.class.getResourceAsStream(Constants.CLIENT_LOGO_RESOURCE_PATH)));		
		
		Optional<ButtonType> result = alert.showAndWait();
		return result;
	}
}
