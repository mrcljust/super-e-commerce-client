package SEPClient;


import SEPCommon.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class WalletController {

	static User user = null;
	
	public static void setUser(User _user)
	{
		user = _user;
	}
	
    @FXML
    private RadioButton Wallet_RadioAdd10;

    @FXML
    private RadioButton Wallet_RadioAdd25;

    @FXML
    private RadioButton Wallet_RadioAdd50;

    @FXML
    private RadioButton Wallet_RadioAdd100;

    @FXML
    private RadioButton Wallet_RadioAddCustom;

    @FXML
    private TextField Wallet_txtCustomAmount;

    @FXML
    private Button Wallet_ButtonIncrease;

    @FXML
    private Button Wallet_ButtonReturn;

    @FXML
    void Wallet_Add10(ActionEvent event) {

    }

    @FXML
    void Wallet_Add100(ActionEvent event) {

    }

    @FXML
    void Wallet_Add25(ActionEvent event) {

    }

    @FXML
    void Wallet_Add50(ActionEvent event) {

    }

    @FXML
    void Wallet_AddCustom(ActionEvent event) {

    }

    @FXML
    void Wallet_CustomAmount(ActionEvent event) {

    }

    @FXML
    void Wallet_IncreaseClick(ActionEvent event) {

    }

    @FXML
    void Wallet_ReturnClick(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) Wallet_ButtonReturn.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, false);
    }

}

