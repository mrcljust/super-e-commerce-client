package SEPClient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.util.prefs.*;

public class StartController {
	
	@FXML
    private Button Start_LoginButton;

    @FXML
    private Button Start_RegisterButton;
	
    @FXML
    public void initialize() {
    	Start_LoginButton.requestFocus();
    }
    
    @FXML
    void Start_LoginClick(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) Start_LoginButton.getScene().getWindow(), "Login", "Anmeldung", false, true);
	}

    @FXML
    void Start_RegisterClick(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) Start_RegisterButton.getScene().getWindow(), "Register", "Registrierung", false, true);
	}
}
