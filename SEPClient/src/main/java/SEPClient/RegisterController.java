package SEPClient;
import java.io.File;
import java.util.HashMap;

import SEPCommon.Address;
import SEPCommon.ClientRequest;
import SEPCommon.Customer;
import SEPCommon.Request;
import SEPCommon.Response; 
import SEPCommon.Seller;
import SEPCommon.ServerResponse;
import SEPCommon.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class RegisterController {
	
	@FXML
    private RadioButton Register_radioCustomer;

    @FXML
    private RadioButton Register_radioSeller;

    @FXML
    private TextField Register_txtUsername;

    @FXML
    private TextField Register_txtEmail;

    @FXML
    private PasswordField Register_txtPassword;

    @FXML
    private PasswordField Register_txtPasswordRepeat;

    @FXML
    private TextField Register_txtFullName;

    @FXML
    private TextField Register_txtStreet;

    @FXML
    private TextField Register_txtPostalcode;

    @FXML
    private TextField Register_txtNumber;

    @FXML
    private TextField Register_txtCity;

    @FXML
    private Button Register_ButtonChooseImage;

    @FXML
    private TextField Register_txtBusinessname;

    @FXML
    private ChoiceBox<String> Register_txtCountry;

    @FXML
    private Button Register_ButtonCancel;

    @FXML
    private ImageView Register_imgPicture;
    
    @FXML
    private Button Register_ButtonOK;
    
    @FXML
    private Label Register_LblBusinessname;
    
    
    @FXML
    public void initialize() {
    	Register_txtCountry.getItems().addAll("Deutschland", "Österreich", "Schweiz");
    	Register_txtCountry.getSelectionModel().select("Deutschland");
    	Register_txtUsername.requestFocus();
    }
    
    @FXML
	void Register_OKClick(ActionEvent event) {
    	String username = Register_txtUsername.getText();
    	String email = Register_txtEmail.getText();
    	String password = Register_txtPassword.getText();
    	String passwordRepeat = Register_txtPasswordRepeat.getText();
    	boolean isSeller = Register_radioSeller.isSelected();
    	String fullname = Register_txtFullName.getText();
    	String street = Register_txtStreet.getText();
    	String number = Register_txtNumber.getText();
    	String postalcode = Register_txtPostalcode.getText();
    	String city = Register_txtCity.getText();
    	String country = Register_txtCountry.getValue();
    	String businessname = Register_txtBusinessname.getText();
    	
    	//Bild zu byte Array umwandeln
    	Image image = Register_imgPicture.getImage();
    	byte[] bufImg = null;
    	if(image!=null)
    	{
    		int w = (int)image.getWidth();
        	int h = (int)image.getHeight();

        	// Create a new Byte Buffer, but we'll use BGRA (1 byte for each channel) //

        	bufImg = new byte[w * h * 4];

        	/* Since you can get the output in whatever format with a WritablePixelFormat,
        	   we'll use an already created one for ease-of-use. */

        	image.getPixelReader().getPixels(0, 0, w, h, PixelFormat.getByteBgraInstance(), bufImg, 0, w * 4);
    	}
    	
    	
    	//Ungültige Eingaben abfangen
    	if(username=="" || username==null || email=="" || email==null || password=="" || password==null || passwordRepeat=="" || passwordRepeat==null || fullname=="" || fullname==null || street=="" || street==null || number=="" || number==null || postalcode=="" || postalcode==null || city=="" || city==null || country=="" || country==null || (isSeller && businessname==null) || (isSeller && businessname==""))
    	{
    		FXMLHandler.ShowMessageBox("Bitte füllen Sie alle mit einem Stern (*) markierten Felder aus.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
    		Register_txtPassword.setText("");
    		Register_txtPasswordRepeat.setText("");
    		return;
    	}
    	
    	if(!password.equals(passwordRepeat))
    	{
    		FXMLHandler.ShowMessageBox("Die Passwörter stimmen nicht überein.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
    		Register_txtPassword.setText("");
    		Register_txtPasswordRepeat.setText("");
    		return;
    	}
    	
    	if(!email.contains("@"))
    	{
    		FXMLHandler.ShowMessageBox("Bitte geben Sie eine gültige E-Mail-Adresse ein.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
    		Register_txtPassword.setText("");
    		Register_txtPasswordRepeat.setText("");
    		return;
    	}
    	
    	if(username.contains("@"))
    	{
    		FXMLHandler.ShowMessageBox("Der Benutzername darf nicht das Zeichen '@' enthalten.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
    		Register_txtPassword.setText("");
    		Register_txtPasswordRepeat.setText("");
    		Register_txtUsername.setText("");
    		return;
    	}
    	
    	int postalint;
    	try
    	{
        	postalint = Integer.parseInt(postalcode.trim());
    	}
        catch (NumberFormatException nfe)
    	{
    		FXMLHandler.ShowMessageBox("Die Postleitzahl darf nur Nummern enthalten.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
    		Register_txtPassword.setText("");
    		Register_txtPasswordRepeat.setText("");
    		return;
    	}
    	
    	
    	User user;
    	Address address = new Address(fullname, country, postalint, city, street, number);
    	if(isSeller)
    	{
    		user = new Seller(username, email, password, bufImg, 0, address, businessname);
    	}
    	else
    	{
    		user = new Customer(username, email, password, bufImg, 0, address);
    	}
    	HashMap<String, Object> requestMap = new HashMap<String, Object>();
    	requestMap.put("User", user);
    	
    	ClientRequest req = new ClientRequest(Request.RegisterUser, requestMap);
    	Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);
		
		//wenn User erfolgreich registriert wurde Response.Success returnen
		//wenn Email vergeben: Response.Emailtaken returnen
		//wenn User vergeben: Response.UsernameTaken returnen
		//wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		
		if(queryResponse.getResponseType() == Response.NoDBConnection)
		{
			FXMLHandler.ShowMessageBox("Es konnte keine Verbindung zur Datenbank hergestellt werden.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
    		Register_txtPassword.setText("");
    		Register_txtPasswordRepeat.setText("");
		}
		if(queryResponse.getResponseType() == Response.ImageTooBig)
		{
			FXMLHandler.ShowMessageBox("Die Dateigröße des ausgewählten Profilbildes ist zu groß. Bitte wählen Sie ein anderes Bild aus.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
    		Register_txtPassword.setText("");
    		Register_txtPasswordRepeat.setText("");
    		Register_imgPicture.setImage(null);
		}
		else if(queryResponse.getResponseType() == Response.UsernameTaken)
		{
			FXMLHandler.ShowMessageBox("Der Benutzername ist bereits vergeben.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
    		Register_txtPassword.setText("");
    		Register_txtPasswordRepeat.setText("");
		}
		else if(queryResponse.getResponseType() == Response.EmailTaken)
		{
			FXMLHandler.ShowMessageBox("Die E-Mail-Adresse ist bereits vergeben.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
    		Register_txtPassword.setText("");
    		Register_txtPasswordRepeat.setText("");
		}
		else if(queryResponse.getResponseType() == Response.Success)
		{
			//Registrierung erfolgreich
			FXMLHandler.ShowMessageBox("Die Registrierung war erfolgreich. Sie werden nun zur Anmeldung weitergeleitet.",
					"Registrierung abgeschlossen", "Registrierung abgeschlossen", AlertType.INFORMATION, true, false);
			LoginController.setPreText(Register_txtUsername.getText());
			FXMLHandler.OpenSceneInStage((Stage) Register_ButtonCancel.getScene().getWindow(), "Login", "Anmeldung", false, true);
		}
	}

    @FXML
	void Register_ReturnClick(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) Register_ButtonCancel.getScene().getWindow(), "Start", "Super-E-commerce-Platform", false, true);
	}
    
    @FXML
	void Register_CustomerSelected(ActionEvent event) {
    	if(Register_radioCustomer.isSelected())
    	{
    		Register_radioSeller.setSelected(false);
    		Register_txtBusinessname.setDisable(true);
    		Register_LblBusinessname.setText("Gewerbename");
    		

    		Register_txtBusinessname.setText("");
    	}
    	else
    	{
    		Register_radioSeller.setSelected(true);
    		Register_txtBusinessname.setDisable(false);
    		Register_LblBusinessname.setText("Gewerbename*");
    	}
	}
    
    @FXML
	void Register_SellerSelected(ActionEvent event) {
    	if(Register_radioSeller.isSelected())
    	{
    		Register_radioCustomer.setSelected(false);
    		Register_txtBusinessname.setDisable(false);
    		Register_LblBusinessname.setText("Gewerbename*");
    	}
    	else
    	{
    		Register_radioCustomer.setSelected(true);
    		Register_txtBusinessname.setDisable(true);
    		Register_LblBusinessname.setText("Gewerbename");
    		Register_txtBusinessname.setText("");
    	}
	}
    
    @FXML
	void Register_OpenPictureClick(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Profilbild auswählen");
    	File file = fileChooser.showOpenDialog(FXMLHandler.getStage());
    	if(file!=null)
    	{
    	    Image selectedImage = new Image(file.toURI().toString());
    	    Register_imgPicture.setImage(selectedImage);
	    }
	}
}
