package SEPClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;

import SEPCommon.ClientRequest;
import SEPCommon.Request;
import SEPCommon.Response;
import SEPCommon.Seller;
import SEPCommon.ServerResponse;
import SEPCommon.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class EditAccountController {

	static User user = null;
	
	public static void setUser(User _user)
	{
		user = _user;
	}

	public void initialize() throws IOException {
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
	private ChoiceBox EditAccount_txtCountry;
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
	private Button EditAccount_ButtonOK;
	@FXML
	private Button EditAccount_ButtonCancel;
	
	@FXML
	void EditAccount_OpenPictureClick(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Profilbild auswählen...");
		File file = fileChooser.showOpenDialog(FXMLHandler.getStage());
		if (file != null) {
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
    					"Konto gelöscht", "Konto gelöscht", AlertType.ERROR, true,false);
				FXMLHandler.OpenSceneInStage((Stage) EditAccount_ButtonCancel.getScene().getWindow(), "Start", "Super-E-commerce-Platform", false, true);
			}
			
		}
	}
	
	@FXML
	void EditAccount_ReturnClick (ActionEvent event) {
		FXMLHandler.OpenSceneInStage((Stage) EditAccount_ButtonCancel.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, false);
	}
	
	
}
