package SEPClient;


import java.util.HashMap;

import SEPCommon.ClientRequest;
import SEPCommon.Request;
import SEPCommon.Response;
import SEPCommon.ServerResponse;
import SEPCommon.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class WalletController {

	static User user = null;
	
	public static void setUser(User _user)
	{
		user = _user;
	}
	
    @FXML
    public void initialize() {
    	ToggleGroup radioGroup = new ToggleGroup();
    	Wallet_RadioAdd10.setToggleGroup(radioGroup);
    	Wallet_RadioAdd25.setToggleGroup(radioGroup);
    	Wallet_RadioAdd50.setToggleGroup(radioGroup);
    	Wallet_RadioAdd100.setToggleGroup(radioGroup);
    	Wallet_RadioAddCustom.setToggleGroup(radioGroup);
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
    	if(Wallet_RadioAddCustom.isSelected())
    	{
    		Wallet_txtCustomAmount.setDisable(false);
    	}
    	else
    	{
    		Wallet_txtCustomAmount.setDisable(true);
    	}
    }

    @FXML
    void Wallet_CustomAmount(ActionEvent event) {
    	
    }

    @FXML
    void Wallet_IncreaseClick(ActionEvent event) {
    	double amount = 0;
    	if(Wallet_RadioAdd10.isSelected())
    	{
    		amount = 10.0;
    	} else if (Wallet_RadioAdd25.isSelected()) {
    		amount = 25.0;
    	} else if (Wallet_RadioAdd50.isSelected()) {
    		amount = 50.0;
    	} else if (Wallet_RadioAdd100.isSelected()) {
    		amount = 100.0;
    	} else if(Wallet_RadioAddCustom.isSelected()) {
    		if(Wallet_txtCustomAmount.getText()!=null)
    		{
    			try
    	    	{
    	        	double customAmountInt = Double.parseDouble(Wallet_txtCustomAmount.getText().replace(",", ".").trim());
    	        	amount = customAmountInt;
    	    	}
    			catch (NumberFormatException nfe)
    	    	{
    				//Buchstaben enthalten
    				FXMLHandler.ShowMessageBox("Der Aufladebetrag darf nur Nummern enthalten.",
    						"Fehler", "Fehler", AlertType.ERROR, true,
    						false);
    				Wallet_txtCustomAmount.setText("");
    				return;
    	    	}
    			
    		}
    		else
    		{
    			//Textbox ist leer
				FXMLHandler.ShowMessageBox("Bitte geben Sie einen Aufladebetrag ein.",
						"Fehler", "Fehler", AlertType.ERROR, true,
						false);
				Wallet_txtCustomAmount.setText("");
    			return;
    		}
    	}
    	else
    	{
    		return;
    	}
    	
    	HashMap <String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put("User", user);
		requestMap.put("Amount", amount);
    	
    	ClientRequest req = new ClientRequest(Request.IncreaseWallet, requestMap);
    	Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);
    	
		//keine Verbindung zur DB
		if(queryResponse.getResponseType() == Response.NoDBConnection)
		{
			FXMLHandler.ShowMessageBox("Es konnte keine Verbindung zur Datenbank hergestellt werden.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
		}
		
		else if(queryResponse.getResponseType() == Response.Failure)
		{

			FXMLHandler.ShowMessageBox("Ihr Konto konnte nicht aufgeladen werden.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
			
		}
		else if(queryResponse.getResponseType() == Response.Success)
		{
			System.out.println(user.getWallet() + " --  " + amount);
			user.setWallet(user.getWallet() + amount);
			User newUser = user;
			//messagebox
			FXMLHandler.ShowMessageBox("Ihr Konto wurde erfolgreich aufgeladen.",
					"Änderung abgeschlossen", "Änderung abgeschlossen", AlertType.INFORMATION, true, false);
	    	//FXMLHandler.OpenSceneInStage((Stage) Wallet_ButtonReturn.getScene().getWindow(), "Login", "Super-E-commerce-Platform", true, true);
	    	MainScreenController.setUser(newUser);
			FXMLHandler.OpenSceneInStage((Stage) Wallet_ButtonReturn.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
		
		}
    }

    @FXML
    void Wallet_ReturnClick(ActionEvent event) {
    	MainScreenController.setUser(user);
    	FXMLHandler.OpenSceneInStage((Stage) Wallet_ButtonReturn.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
    }
}

