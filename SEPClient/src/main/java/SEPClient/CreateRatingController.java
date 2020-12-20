package SEPClient;

import java.io.IOException;
import java.util.HashMap;

import SEPCommon.Auction;
import SEPCommon.ClientRequest;
import SEPCommon.Order;
import SEPCommon.Preferences;
import SEPCommon.Request;
import SEPCommon.Response;
import SEPCommon.ServerResponse;
import SEPCommon.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class CreateRatingController {

	private static User user = null;
	private static Order order = null;
	private static Auction auction = null;

	public static void setUser(User _user) {
		user = _user;
	}

	public static void setOrder(Order _order) {
		order = _order;
	}

	public static void setAuction(Auction _auction) {
		auction = _auction;
	}

	public void initialize() throws IOException {
    	CreateRating_Stars.getItems().addAll(1, 2, 3, 4, 5);
    	CreateRating_Stars.getSelectionModel().select(5);
    	
    	//entweder Auktion oder Order zugewiesen
	}
	
    @FXML
    private ImageView CreateRating_ImgProfilePicture;
	
    @FXML
    private Label CreateRating_txtSellerBuyerName;

    @FXML
    private Label CreateRating_txtIDDate;
	
    @FXML
    private ChoiceBox<Integer> CreateRating_Stars;

    @FXML
    private TextArea CreateRating_Text;

    @FXML
    private Button CreateRating_ButtonOK;

    @FXML
    private Button CreateRating_ButtonReturn;

    @FXML
    void CreateRating_ButtonOK_Click(ActionEvent event) {
    	//datum und name fehlt noch 
    	
    	String rating = CreateRating_Stars.getValue().toString();
    	String report = CreateRating_Text.getText();
    	
    	if (rating == null || rating == "") {
    		FXMLHandler.ShowMessageBox("Bitte füllen Sie alle mit einem Stern (*) versehenen Felder aus.", "Fehler", "Fehler", AlertType.ERROR, true, false);			
			return;
    	}
    	
    	HashMap <String, Object> requestMap = new HashMap<String, Object>();
    	requestMap.put("User", user);
    	
    	ClientRequest req = new ClientRequest(Request.SendRating, requestMap);
    	Client client = Client.getClient();
    	ServerResponse queryResponse = client.sendClientRequest(req);
    	
    	if(queryResponse.getResponseType() == Response.NoDBConnection) {
			FXMLHandler.ShowMessageBox("Es konnte keine Verbindung zur Datenbank hergestellt werden, Ihre Bewertung wurde daher nicht abgeschickt.",
					"Fehler", "Fehler", AlertType.ERROR, true, false);
			return;
		}
    	
    	else if (queryResponse.getResponseType() == Response.Success) {
			FXMLHandler.ShowMessageBox("Ihre Bewertung wurde erfolgreich übermittelt.",
					"Bewertung abgeschlossen", "Bewertung abgeschlossen", AlertType.INFORMATION, true, false);
		}
    	
    }

    @FXML
    void CreateRating_ButtonReturn_Click(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) CreateRating_ButtonReturn.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
    }
}
