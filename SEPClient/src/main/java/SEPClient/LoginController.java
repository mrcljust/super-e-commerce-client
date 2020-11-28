package SEPClient;

import java.util.HashMap;
import SEPCommon.ClientRequest;
import SEPCommon.Request;
import SEPCommon.Response;
import SEPCommon.ServerResponse;
import SEPCommon.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class LoginController {
	
	static String preText = null;
	
	
	@FXML
    private Button Login_ReturnButton;

    @FXML
    private TextField Login_txtEmailOrUser;

    @FXML
    private PasswordField Login_txtPassword;

    @FXML
    private Button Login_OKButton;
    
    @FXML
    private CheckBox Login_checkSaveLogin;
    
    public static void setPreText(String _preText)
	{
		preText = _preText;
	}
    
    @FXML
    public void initialize() {
    	if(preText!=null) //preText wird ggf. im Registrierungsprozess gesetzt
    	{
        	Login_txtEmailOrUser.setText(preText);
        	Login_txtPassword.requestFocus();
        	preText=null;
    	}
    	else
    	{
    		Login_txtEmailOrUser.requestFocus();
    		
    		String savedUsername = SEPCommon.Preferences.getPref("Username");		//Value vom Key
    		String savedPassword = SEPCommon.Preferences.getPref("Password");
			if(savedUsername!="" && savedPassword!="")
			{
				Login_txtEmailOrUser.setText(savedUsername);			//in Feld kommt Value
				Login_txtPassword.setText(savedPassword);
				Login_checkSaveLogin.setSelected(true);
			}
    	}
    }
    
    @FXML
    void txtPassword_KeyPressed(KeyEvent event) {
    	//Taste wird gedrückt
    	//Bei Enter: Button OK Klick simulieren
    	if (event.getCode().equals(KeyCode.ENTER))
        {
            Login_OKButton.fire();
        }
    }
    
    @FXML
	void Login_OKClick(ActionEvent event) {
    	String userOrEmail = Login_txtEmailOrUser.getText().trim();
    	String password = SEPCommon.Methods.getMd5Encryption(Login_txtPassword.getText()); //verschlüssle das PW
    			
    	//Ungültige Eingaben abfangen
    	if(userOrEmail=="" || userOrEmail==null || password=="" || password==null)
    	{
    		FXMLHandler.ShowMessageBox("Bitte geben Sie einen Benutzernamen bzw. eine E-Mail-Adresse und ein Passwort ein.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
    		Login_txtPassword.setText("");
    		return;
    	}
    	
    	HashMap<String, Object> requestMap = new HashMap<String, Object>();
    	requestMap.put("UserOrEmail", userOrEmail);
    	requestMap.put("Password", password);

    	ClientRequest req = new ClientRequest(Request.LoginUser, requestMap);
    	Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);
		
		if(queryResponse.getResponseType() == Response.NoDBConnection)
		{
			FXMLHandler.ShowMessageBox("Es konnte keine Verbindung zur Datenbank hergestellt werden.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
    		Login_txtPassword.setText("");
		}
		else if(queryResponse.getResponseType() == Response.Failure)
		{
			FXMLHandler.ShowMessageBox("Das Konto existiert nicht oder der eingegebene Benutzername/die eingegebene E-Mail-Adresse stimmt nicht mit dem Passwort überein.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
    		Login_txtPassword.setText("");
		}
		else if(queryResponse.getResponseType() == Response.Success)
		{
			//Userdaten aus DB holen (bisher ist nur Email/Username und PW bekannt)
			User user;
			requestMap = new HashMap<String, Object>();
			
			if(userOrEmail.contains("@"))
			{
				requestMap.put("Email", userOrEmail);
			}
			else
			{
				requestMap.put("Username", userOrEmail);
			}
			req = new ClientRequest(Request.GetUserData, requestMap);
			queryResponse = client.sendClientRequest(req);
			
			if(queryResponse.getResponseType() == Response.Failure)
			{
				FXMLHandler.ShowMessageBox("Die Benutzerdaten konnten nicht aus der Datenbank ausgelesen werden bzw. es konnte keine Verbindung zur Datenbank hergestellt werden.",
						"Fehler", "Fehler", AlertType.ERROR, true,
						false);
	    		Login_txtPassword.setText("");
			}
			else if(queryResponse.getResponseType() == Response.Success)
			{
				user = (User)queryResponse.getResponseMap().get("User");
				
				
				if(Login_checkSaveLogin.isSelected())
				{
					//Wenn die Checkbox selected ist, Daten speichern, sonst leeren String speichern
					SEPCommon.Preferences.savePref("Username", userOrEmail);
					SEPCommon.Preferences.savePref("Password", Login_txtPassword.getText());		//login_txt eingegebens Passwort im Client
				}
				else
				{
					SEPCommon.Preferences.savePref("Username", "");					//checkbox nicht selected -> nichts speichern
					SEPCommon.Preferences.savePref("Password", "");
				}
				
				//MainScreen öffnen
                MainScreenController.setUser(user);
				FXMLHandler.OpenSceneInStage((Stage) Login_OKButton.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
			}
			
		} 
	}

    @FXML
	void Login_ReturnClick(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) Login_ReturnButton.getScene().getWindow(), "Start", "Super-E-commerce-Platform", false, true);
	}
	
}