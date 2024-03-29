package SEPClient;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import SEPCommon.Address;
import SEPCommon.ClientRequest;
import SEPCommon.Customer;
import SEPCommon.Request;
import SEPCommon.Response;
import SEPCommon.Seller;
import SEPCommon.ServerResponse;
import SEPCommon.User;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.coobird.thumbnailator.Thumbnails;

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
    private Button Register_ButtonDeleteImage;

    @FXML
    private ImageView Register_imgPicture;
    
    @FXML
    private Button Register_ButtonOK;
    
    @FXML
    private Label Register_LblBusinessname;
    
    
    @FXML
    public void initialize() {
    	// Vorauswahl Checkbox
    	Register_txtCountry.getItems().addAll("Deutschland", "�sterreich", "Schweiz");
    	Register_txtCountry.getSelectionModel().select("Deutschland");
    	Register_txtUsername.requestFocus();
    	
    	// Auswahl ganz oben entweder Privat oder Gewerbekunde
    	ToggleGroup radioGroup = new ToggleGroup();
    	Register_radioCustomer.setToggleGroup(radioGroup);
    	Register_radioSeller.setToggleGroup(radioGroup);

    	//Standardbild setzen
    	Image defaultImage = new Image(getClass().getResource("/SEPClient/UI/no-image.jpg").toString());
    	Register_imgPicture.setImage(defaultImage);
    }
    
    @FXML
	void Register_OKClick(ActionEvent event) throws IOException {
    	// Felder bekommen Eingaben
    	String username = Register_txtUsername.getText().trim();
    	String email = Register_txtEmail.getText().trim();
    	String password = Register_txtPassword.getText();
    	String passwordRepeat = Register_txtPasswordRepeat.getText();
    	boolean isSeller = Register_radioSeller.isSelected();
    	String fullname = Register_txtFullName.getText().trim();
    	String street = Register_txtStreet.getText().trim();
    	String number = Register_txtNumber.getText().trim();
    	String postalcode = Register_txtPostalcode.getText().trim();
    	String city = Register_txtCity.getText().trim();
    	String country = Register_txtCountry.getValue().trim();
    	String businessname = Register_txtBusinessname.getText().trim();
    	
    	//Bild zu byte Array umwandeln
    	//Codeteil mit Hilfe der folgenden Quelle geschrieben: https://stackoverflow.com/questions/9417356/bufferedimage-resize
    	//(Antwort von coobird, Feb 23 '12 at 17:23)
    	Image image = Register_imgPicture.getImage();
    	BufferedImage imageBuffered = SwingFXUtils.fromFXImage(image, null);
    	
    	//Skalieren unter Beibehaltung des Seitenverh�ltnisses. Die Library "Thumbnailator" wird zum skalieren benutzt.
    	//Anschlie�end wird das Bild in ein Byte-Array umgewandelt, weil dies der ben�tigte Datentyp f�r ein Userobjekt (und die
    	//Datenbank) ist
    	BufferedImage imageResized = Thumbnails.of(imageBuffered).size(512, 512).asBufferedImage();
    	ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
    	ImageIO.write(imageResized, "png", byteOutput);
    	byte[] imageByteArray = byteOutput.toByteArray();
    	byteOutput.close(); 
    	
    	
    	//Ung�ltige Eingaben abfangen (leere Felder...)
    	if(username=="" || username==null || email=="" || email==null || password=="" || password==null || passwordRepeat=="" || passwordRepeat==null || fullname=="" || fullname==null || street=="" || street==null || number=="" || number==null || postalcode=="" || postalcode==null || city=="" || city==null || country=="" || country==null || (isSeller && businessname==null) || (isSeller && businessname==""))
    	{
    		FXMLHandler.ShowMessageBox("Bitte f�llen Sie alle mit einem Stern (*) markierten Felder aus.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
    		Register_txtPassword.setText("");
    		Register_txtPasswordRepeat.setText("");
    		return;
    	}
    	
    	// Passw�rter m�ssen gleich sein
    	if(!password.equals(passwordRepeat))
    	{
    		FXMLHandler.ShowMessageBox("Die Passw�rter stimmen nicht �berein.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
    		Register_txtPassword.setText("");
    		Register_txtPasswordRepeat.setText("");
    		return;
    	}
    	
    	//Passwort verschl�sseln
    	password = SEPCommon.Methods.getMd5Encryption(Register_txtPassword.getText());
    	passwordRepeat = SEPCommon.Methods.getMd5Encryption(Register_txtPasswordRepeat.getText());
    	
    	// Email muss ein @ enthalten, um es sp�ter vom Username unterscheiden zu k�nnen
    	if(!email.contains("@") || !email.contains("."))
    	{
    		FXMLHandler.ShowMessageBox("Bitte geben Sie eine g�ltige E-Mail-Adresse ein.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
    		Register_txtPassword.setText("");
    		Register_txtPasswordRepeat.setText("");
    		return;
    	}
    	
    	// Dementsprechend darf der Username kein @ enthalten
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
    		// Versuchen Plz. in ein Int zu setzen, da diese nur Nummern enthalten darf
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
    	
    	// User(Privat- oder Gewerbekunde)- und Adress-Objekt
    	// Wallet anfangs 0$
    	User user;
    	Address address = new Address(fullname, country, postalint, city, street, number);
    	// Gewerbekunde
    	if(isSeller)
    	{
    		user = new Seller(username, email, password, imageByteArray, 0, address, businessname);
    	}
    	// Privatkunde
    	else
    	{
    		user = new Customer(username, email, password, imageByteArray, 0, address);
    	}
    	HashMap<String, Object> requestMap = new HashMap<String, Object>();
    	requestMap.put("User", user);
    	
    	// >RegisterUser Request mit Daten
    	ClientRequest req = new ClientRequest(Request.RegisterUser, requestMap);
    	Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);
		
		//wenn User erfolgreich registriert wurde Response.Success returnen
		//wenn Email vergeben: Response.Emailtaken returnen
		//wenn User vergeben: Response.UsernameTaken returnen
		//wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		
		// F�lle bei denen Fehlermeldungen kommen
		if(queryResponse.getResponseType() == Response.NoDBConnection)
		{
			FXMLHandler.ShowMessageBox("Es konnte keine Verbindung zur Datenbank hergestellt werden.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
    		Register_txtPassword.setText("");
    		Register_txtPasswordRepeat.setText("");
		}
		
		else if(queryResponse.getResponseType() == Response.ImageTooBig)
		{
			FXMLHandler.ShowMessageBox("Die Dateigr��e des ausgew�hlten Profilbildes ist zu gro� (max. 16MB). Bitte w�hlen Sie ein anderes Bild aus.",
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
		
		// Erfolgsmeldung
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
    	// Benutzer geht zur�ck zum Startscreen
    	FXMLHandler.OpenSceneInStage((Stage) Register_ButtonCancel.getScene().getWindow(), "Start", "Super-E-commerce-Platform", false, true);
	}
    
    @FXML
    // Nur Gewerbekunde darf Gewerbenamen eintragen
	void Register_CustomerSelected(ActionEvent event) {
    	if(Register_radioCustomer.isSelected())
    	{
    		Register_txtBusinessname.setDisable(true);
    		Register_LblBusinessname.setText("Gewerbename");
    		

    		Register_txtBusinessname.setText("");
    	}
    	else
    	{
    		Register_txtBusinessname.setDisable(false);
    		Register_LblBusinessname.setText("Gewerbename*");
    	}
	}
    
    @FXML
	void Register_SellerSelected(ActionEvent event) {
    	if(Register_radioSeller.isSelected())
    	{
    		Register_txtBusinessname.setDisable(false);
    		Register_LblBusinessname.setText("Gewerbename*");
    	}
    	else
    	{
    		Register_txtBusinessname.setDisable(true);
    		Register_LblBusinessname.setText("Gewerbename");
    		Register_txtBusinessname.setText("");
    	}
	}
    
    @FXML
    // Profilbild ausw�hlen
	void Register_OpenPictureClick(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Profilbild ausw�hlen");
    	File file = fileChooser.showOpenDialog(FXMLHandler.getStage());
    	if(file!=null)
    	{
    		// Erlaubte Formate
			if(!file.toURI().toString().toLowerCase().contains(".png") && !file.toURI().toString().toLowerCase().contains(".jpg") && !file.toURI().toString().toLowerCase().contains(".jpeg"))
    	    {
    			//Bild weder .jpg, .jpeg noch .png
    	    	FXMLHandler.ShowMessageBox("Bitte w�hlen Sie eine .jpg-, .jpeg- oder .png-Datei aus.",
    					"Fehler", "Fehler", AlertType.ERROR, true,
    					false);
    	    	return;
    	    }
			// Neues Profilbild ausw�hlen
    	    Image selectedImage = new Image(file.toURI().toString());
    	    Register_imgPicture.setImage(selectedImage);
	    }
	}
    
    @FXML
    void Register_DeletePictureClick(ActionEvent event) {
    	//Standardbild setzen
    	Image defaultImage = new Image(getClass().getResource("/SEPClient/UI/no-image.jpg").toString());
    	Register_imgPicture.setImage(defaultImage);
    }
}
