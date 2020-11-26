package SEPClient;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;

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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.coobird.thumbnailator.Thumbnails;

public class EditAccountController {

	static User user = null;
	
	public static void setUser(User _user)
	{
		user = _user;
	}

	public void initialize() throws IOException {
		//Standardbild setzen
    	Image defaultImage = new Image(getClass().getResource("/SEPClient/UI/no-image.jpg").toString());
    	EditAccount_imgPicture.setImage(defaultImage);
		
		EditAccount_txtUsername.setText(user.getUsername());
		EditAccount_txtEmail.setText(user.getEmail());
		EditAccount_txtFullName.setText(user.getAddress().getFullname());
		EditAccount_txtStreet.setText(user.getAddress().getStreet());
		EditAccount_txtNumber.setText(user.getAddress().getNumber());
		EditAccount_txtPostalcode.setText(String.valueOf(user.getAddress().getZipcode()));
		EditAccount_txtCity.setText(user.getAddress().getCity());
		EditAccount_txtCountry.getItems().addAll("Deutschland", "Österreich", "Schweiz");
		EditAccount_txtCountry.getSelectionModel().select(user.getAddress().getCountry());
		
		InputStream in = new ByteArrayInputStream(user.getPicture());
		Image img = new Image(in);
		EditAccount_imgPicture.setImage(img);
		
		EditAccount_radioCustomer.setSelected(true);
		EditAccount_radioSeller.setSelected(true);
		
		if (user instanceof Seller) {
			//für Gewerbekunden:
			EditAccount_radioSeller.setSelected(true);
			EditAccount_radioCustomer.setSelected(false);
			EditAccount_LblBusinessname.setText("Gewerbename*");
			EditAccount_txtBusinessname.setDisable(false);
			EditAccount_txtBusinessname.setText(((Seller) user).getBusinessname());
		} else {
			EditAccount_radioSeller.setSelected(false);
			EditAccount_radioCustomer.setSelected(true);
			EditAccount_LblBusinessname.setText("Gewerbename");
			EditAccount_txtBusinessname.setDisable(true);
		}
	}
	
	@FXML
	private RadioButton EditAccount_radioCustomer;
	@FXML
	private RadioButton EditAccount_radioSeller;
	@FXML
	private TextField EditAccount_txtUsername;
	@FXML
	private TextField EditAccount_txtEmail;
	@FXML
	private PasswordField EditAccount_txtPassword;
	@FXML
	private PasswordField EditAccount_txtPasswordRepeat;
	@FXML
	private TextField EditAccount_txtFullName;
	@FXML
	private TextField EditAccount_txtStreet;
	@FXML
	private TextField EditAccount_txtNumber;
	@FXML
	private TextField EditAccount_txtPostalcode;
	@FXML
	private TextField EditAccount_txtCity;
	@FXML
	private ChoiceBox<String> EditAccount_txtCountry;
	@FXML
	private Label EditAccount_LblBusinessname;
	@FXML
	private TextField EditAccount_txtBusinessname;
	@FXML
	private Button EditAccount_ButtonChooseImage;
	@FXML
	private ImageView EditAccount_imgPicture;
	@FXML
	private Button EditAccount_ButtonDeleteAccount;
	@FXML
	private Button EditAccount_ButtonDeleteImage;
	@FXML
	private Button EditAccount_ButtonOK;
	@FXML
	private Button EditAccount_ButtonCancel;
	
	@FXML
	void EditAccount_OKClick (ActionEvent event) throws IOException {
		String username = EditAccount_txtUsername.getText().trim();
		String email = EditAccount_txtEmail.getText().trim();
		String password = SEPCommon.Methods.getMd5Encryption(EditAccount_txtPassword.getText());
		String passwordRepeated = SEPCommon.Methods.getMd5Encryption(EditAccount_txtPasswordRepeat.getText());
		String fullname = EditAccount_txtFullName.getText().trim();
		String street = EditAccount_txtStreet.getText().trim();
		String number = EditAccount_txtNumber.getText().trim(); //ich muss nicht typecasten?
		String zipcode = EditAccount_txtPostalcode.getText().trim();	
		String city = EditAccount_txtCity.getText().trim();
		String country = (String) EditAccount_txtCountry.getValue().trim(); //richtig so?
		String businessname = EditAccount_txtBusinessname.getText().trim();
		boolean isSeller = EditAccount_radioSeller.isSelected();
		
    	//Bild zu byte Array umwandeln
    	Image image = EditAccount_imgPicture.getImage();
    	BufferedImage imageBuffered = SwingFXUtils.fromFXImage(image, null);

    	//Skalieren unter Beibehaltung des Seitenverhältnisses
    	BufferedImage imageResized = Thumbnails.of(imageBuffered).size(512, 512).asBufferedImage();
    	ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
    	ImageIO.write(imageResized, "png", byteOutput);
    	byte[] imageByteArray = byteOutput.toByteArray();
    	byteOutput.close(); 
		
		//Ungültige Abfragen abfangen: 
		
		if (username=="" || username==null || email=="" || email==null || password=="" || password==null || passwordRepeated=="" || passwordRepeated==null 
				|| fullname=="" || fullname==null || street=="" || street==null || number=="" || number==null || zipcode=="" || zipcode==null 
				|| city=="" || city==null || country=="" || country==null || (isSeller && businessname==null) || (isSeller && businessname=="")) {
			
			FXMLHandler.ShowMessageBox("Bitte füllen Sie alle mit einem Stern (*) versehenen Felder aus.", "Fehler", "Fehler", AlertType.ERROR, true, false);			
			return; //nochmal versuchen
		}
    	
		if (!passwordRepeated.equals(password)) {
			FXMLHandler.ShowMessageBox("Die Passwörter stimmen nicht überein.", "Fehler", "Fehler", AlertType.ERROR, true, false);
			EditAccount_txtPassword.setText("");
			EditAccount_txtPasswordRepeat.setText("");
			return; //nochmal versuchen
		}
		
		if (!email.contains("@") || !email.contains(".")) {
			FXMLHandler.ShowMessageBox("Die E-Mail Adresse ist nicht gültig.", "Fehler", "Fehler", AlertType.ERROR, true, false);
			EditAccount_txtEmail.setText("");
			return;
		}
		
		if (username.contains("@")) {
			FXMLHandler.ShowMessageBox("Der Benutzername darf kein '@' enthalten.", "Fehler", "Fehler", AlertType.ERROR, true, false);
			EditAccount_txtUsername.setText("");
			return;
		}
		
		int postalcode;
		try {
			postalcode = Integer.parseInt(zipcode.trim());
		} catch (NumberFormatException nfe) {
			FXMLHandler.ShowMessageBox("Die Postleitzahl darf nur Zahlen enthalten.", "Fehler", "Fehler", AlertType.ERROR, true, false);
			EditAccount_txtPostalcode.setText("");
			return;
		}
		
		User newUser;
		Address address = new Address (fullname, country, postalcode, city, street, number);
		if (isSeller) {
			newUser = new Seller(user.getId(), username, email, password, imageByteArray, 0, address, businessname);
		} else {
			newUser = new Customer(user.getId(), username, email, password, imageByteArray, 0, address);
		}
		HashMap <String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put("User", newUser);
    	
    	ClientRequest req = new ClientRequest(Request.EditUser, requestMap);
    	Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);
		
		//keine Verbindung zu DB
		if(queryResponse.getResponseType() == Response.NoDBConnection) {
			FXMLHandler.ShowMessageBox("Es konnte keine Verbindung zur Datenbank hergestellt werden.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
		}
		

		//Bild zu großŸ
		if(queryResponse.getResponseType() == Response.ImageTooBig) {
			FXMLHandler.ShowMessageBox("Die Dateigröße des ausgewählten Profilbildes ist zu groß (max. 16MB). Bitte wählen Sie ein anderes Bild aus.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
			EditAccount_imgPicture.setImage(null);
		}
		
		//Username bereits vergeben
		else if(queryResponse.getResponseType() == Response.UsernameTaken) {
			FXMLHandler.ShowMessageBox("Der Benutzername ist bereits vergeben.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
			EditAccount_txtUsername.setText("");
		}
		
		//Email bereits vergeben
		else if(queryResponse.getResponseType() == Response.EmailTaken) {
			FXMLHandler.ShowMessageBox("Die E-Mail-Adresse ist bereits vergeben.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
			EditAccount_txtEmail.setText("");
		}
		
		//Ä„nderungen erfolgreich
		else if(queryResponse.getResponseType() == Response.Success) {
			FXMLHandler.ShowMessageBox("Die Änderung Ihrer Daten war erfolgreich. Sie müssen sich nun erneut anmelden.",
					"Änderung abgeschlossen", "Änderung abgeschlossen", AlertType.INFORMATION, true, false);
			LoginController.setPreText(EditAccount_txtUsername.getText());
			FXMLHandler.OpenSceneInStage((Stage) EditAccount_ButtonCancel.getScene().getWindow(), "Login", "Anmeldung", false, true);
		}
		
	}

	
	@FXML
	void EditAccount_OpenPictureClick(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Profilbild auswählen...");
		File file = fileChooser.showOpenDialog(FXMLHandler.getStage());
		if (file != null) {
			if(!file.toURI().toString().toLowerCase().contains(".png") && !file.toURI().toString().toLowerCase().contains(".jpg") && !file.toURI().toString().toLowerCase().contains(".jpeg"))
    	    {
    			//Bild weder .jpg, .jpeg noch .png
    	    	FXMLHandler.ShowMessageBox("Bitte wählen Sie eine .jpg-, .jpeg- oder .png-Datei aus.",
    					"Fehler", "Fehler", AlertType.ERROR, true,
    					false);
    	    	return;
    	    }
			Image selectedImage = new Image (file.toURI().toString());
			EditAccount_imgPicture.setImage(selectedImage);
		}
	}
	
	@FXML
	void EditAccount_DeleteAccountClick(ActionEvent event) {
		Optional<ButtonType> dialogResult = FXMLHandler.ShowYesNoQuestionBox("Möchten Sie Ihr Konto wirklich löschen?", "Konto löschen?", "Konto löschen", AlertType.NONE);
		if (dialogResult.get() == ButtonType.YES) {
			HashMap<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("User", user);
			
			ClientRequest req = new ClientRequest(Request.DeleteUser, requestMap);
			Client client = Client.getClient();
			ServerResponse queryResponse = client.sendClientRequest(req);
			
			//falls keine Verbindung zur Datenbank besteht:
			if (queryResponse.getResponseType() == Response.NoDBConnection) {
				FXMLHandler.ShowMessageBox("Es konnte keine Verbindung zur Datenbank hergestellt werden.",
    					"Fehler", "Fehler", AlertType.ERROR, true, false);
			} else if (queryResponse.getResponseType() == Response.Failure) {
				FXMLHandler.ShowMessageBox("Es ist ein unbekannter Fehler beim Löschen Ihres Kontos aufgetreten.",
    					"Fehler", "Fehler", AlertType.ERROR, true,false);
			} else if (queryResponse.getResponseType() == Response.Success) {
				FXMLHandler.ShowMessageBox("Ihr Konto wurde erfolgreich gelöscht.",
    					"Konto gelöscht", "Konto gelöscht", AlertType.CONFIRMATION, true,false);
				FXMLHandler.OpenSceneInStage((Stage) EditAccount_ButtonCancel.getScene().getWindow(), "Start", "Super-E-commerce-Platform", false, true);
			}
			
		}
	}
	
	@FXML
	void EditAccount_ReturnClick (ActionEvent event) {
		FXMLHandler.OpenSceneInStage((Stage) EditAccount_ButtonCancel.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
	}
	
    @FXML
    void EditAccount_DeletePictureClick(ActionEvent event) {
    	//Standardbild setzen
    	Image defaultImage = new Image(getClass().getResource("/SEPClient/UI/no-image.jpg").toString());
    	EditAccount_imgPicture.setImage(defaultImage);
    }
}
