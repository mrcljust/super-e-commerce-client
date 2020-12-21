package SEPClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import SEPCommon.Auction;
import SEPCommon.ClientRequest;
import SEPCommon.Order;
import SEPCommon.Preferences;
import SEPCommon.Rating;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class CreateRatingController {

	private static User sender = null;
	private static User recipient = null;
	private static Order order = null;
	private static Auction auction = null;
	private static boolean ratingIsBySeller = false;

	public static void setSender(User _sender) {
		sender = _sender;
	}
	
	public static void setRecipient(User _recipient) {
		recipient = _recipient;
	}

	public static void setOrder(Order _order) {
		order = _order;
	}

	public static void setAuction(Auction _auction) {
		auction = _auction;
	}
	
	public static void setRatingIsBySeller(Boolean val) {
		ratingIsBySeller = val;
	}

	public void initialize() throws IOException {
    	CreateRating_Stars.getItems().addAll(1, 2, 3, 4, 5);
    	CreateRating_Stars.getSelectionModel().select(4);
    	
    	//entweder Auktion oder Order zugewiesen
    	
    	//Standardbild setzen
    	Image defaultImage = new Image(getClass().getResource("/SEPClient/UI/no-image.jpg").toString());
    	CreateRating_ImgProfilePicture.setImage(defaultImage);
    	
    	//Bild setzen
    	InputStream in = new ByteArrayInputStream(recipient.getPicture());
		Image img = new Image(in);
		CreateRating_ImgProfilePicture.setImage(img);
		
		if(order!=null)
		{
			CreateRating_txtIDDate.setText("Bestell-ID " + order.getId() + " vom " + SEPCommon.Constants.DATEFORMATDAYONLY.format(order.getDate()));
		}
		else if(auction!=null)
		{
			CreateRating_txtIDDate.setText("Auktion-ID " + auction.getId() + " vom " + SEPCommon.Constants.DATEFORMATDAYONLY.format(auction.getEnddate()));
		}
		
		if(ratingIsBySeller)
		{
			//Rating vom VK fuer Kaeufer
			CreateRating_txtSellerBuyerName.setText("Bewertung für den Käufer " + recipient.getUsername() + " (ID " + recipient.getId() + ") abgeben");				
		}
		else
		{
			//Rating vom Kaeufer fuer VK
			CreateRating_txtSellerBuyerName.setText("Bewertung für den Verkäufer " + recipient.getUsername() + " (ID " + recipient.getId() + ") abgeben");
		}
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
    	
    	Integer rating = CreateRating_Stars.getValue();
    	String report = CreateRating_Text.getText();
    	
    	if (rating == null) {
    		FXMLHandler.ShowMessageBox("Bitte füllen Sie alle mit einem Stern (*) versehenen Felder aus.", "Fehler", "Fehler", AlertType.ERROR, true, false);			
			return;
    	}
    	
    	Rating newRating = null;
    	if(order!=null)
    	{
			newRating = new Rating(rating, report, sender.getId(), recipient.getId(), order.getId(), false);
    	}
    	else if(auction!=null)
    	{
			newRating = new Rating(rating, report, sender.getId(), recipient.getId(), auction.getId(), true);
    	}
		
    	
    	
    	HashMap <String, Object> requestMap = new HashMap<String, Object>();
    	requestMap.put("Rating", newRating);
    	
    	
    	
    	ClientRequest req = new ClientRequest(Request.SendRating, requestMap);
    	Client client = Client.getClient();
    	ServerResponse queryResponse = client.sendClientRequest(req);
    	
    	if(queryResponse.getResponseType() == Response.NoDBConnection) {
			FXMLHandler.ShowMessageBox("Es konnte keine Verbindung zur Datenbank hergestellt werden, Ihre Bewertung wurde daher nicht abgeschickt.",
					"Fehler", "Fehler", AlertType.ERROR, true, false);
			return;
		}

    	else if (queryResponse.getResponseType() == Response.Failure) {
    		FXMLHandler.ShowMessageBox("Es ist ein Fehler beim Verarbeiten Ihrer Anfrage aufgetreten, Ihre Bewertung wurde daher nicht abgeschickt.",
					"Fehler", "Fehler", AlertType.ERROR, true, false);
			return;
		}
    	
    	else if (queryResponse.getResponseType() == Response.Success) {
			FXMLHandler.ShowMessageBox("Ihre Bewertung wurde erfolgreich übermittelt.",
					"Bewertung gespeichert", "Bewertung gespeichert", AlertType.INFORMATION, true, false);
	    	if(ratingIsBySeller)
	    	{
	        	FXMLHandler.OpenSceneInStage((Stage) CreateRating_ButtonReturn.getScene().getWindow(), "MySales", "Meine Verkäufe", true, true);
	    	}
	    	else {
	        	FXMLHandler.OpenSceneInStage((Stage) CreateRating_ButtonReturn.getScene().getWindow(), "MyPurchases", "Meine Käufe", true, true);
			}
		}
    	
    }

    @FXML
    void CreateRating_ButtonReturn_Click(ActionEvent event) {
    	if(ratingIsBySeller)
    	{
        	FXMLHandler.OpenSceneInStage((Stage) CreateRating_ButtonReturn.getScene().getWindow(), "MySales", "Meine Verkäufe", true, true);
    	}
    	else {
        	FXMLHandler.OpenSceneInStage((Stage) CreateRating_ButtonReturn.getScene().getWindow(), "MyPurchases", "Meine Käufe", true, true);
		}
    }
}
