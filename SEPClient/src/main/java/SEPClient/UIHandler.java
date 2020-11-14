package SEPClient;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

public class UIHandler extends Application {
	public Scene scene;
	public Stage stage;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		//Start-Szene öffnen
		stage = new Stage();
		OpenScene("Start", "Super-E-commerce-Platform");
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
	
	private Parent CreateParent(String sceneName) throws IOException
	{
		//Die Methode übergibt einen Parent aus einer fxml-Datei, welcher benötigt wird, um eine Szene zu öffnen
		return FXMLLoader.load(getClass().getResource("/SEPClient/UI/" + sceneName + ".fxml"));
	}
}
