package SEPClient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    	FXMLHandler.OpenSceneInStage((Stage) Start_LoginButton.getScene().getWindow(), "Login", "Anmeldung", false, true);
	}

    
    // Registrierung wird ausgewählt --> Register Scene wird geöffnet
    @FXML
    void Start_RegisterClick(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) Start_RegisterButton.getScene().getWindow(), "Register", "Registrierung", false, true);
	}
}
