package SEPClient;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import SEPCommon.ClientRequest;
import SEPCommon.Customer;
import SEPCommon.Rating;
import SEPCommon.Request;
import SEPCommon.Response;
import SEPCommon.Seller;
import SEPCommon.ServerResponse;
import SEPCommon.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ShowRatingsController {

	private static User user = null;
	private static boolean viewOwnRatings = false;

	public static void setUser(User _user) {
		user = _user;
	}

	public static void setViewOwnRatings(boolean _viewOwnRatings) {
		viewOwnRatings = _viewOwnRatings;
	}

	public void initialize() throws IOException {
		ShowRatings_txtAverageRating.setText("");
		ShowRatings_txtRatingCount.setText("");
		TableRatings_ColumnDate.setCellValueFactory(new PropertyValueFactory<Rating, LocalDateTime>("date"));
		//Datumsformat
		TableRatings_ColumnDate.setCellFactory(tc -> new TableCell<Rating, LocalDateTime>() {
    	    @Override
    	    protected void updateItem(LocalDateTime date, boolean empty) {
    	        super.updateItem(date, empty);
    	        if (empty || date==null) {
    	            setText(null);
    	        } else {
    	            setText(date.format(SEPCommon.Constants.DATEFORMAT));
    	        }
    	    }
    	});
		TableRatings_ColumnBy.setCellValueFactory(new PropertyValueFactory<Rating, String>("senderId"));
		TableRatings_ColumnStars.setCellValueFactory(new PropertyValueFactory<Rating, Integer>("stars"));
		TableRatings_ColumnText.setCellValueFactory(new PropertyValueFactory<Rating, String>("text"));
		
		ShowRatings_ListRatings.setItems(loadAllRatings());
		loadAvgRating();
		
		if(viewOwnRatings)
		{
			//eigene Bewertungen
			if(user instanceof Seller)
			{
		    	ShowRatings_txtSellerBuyerName.setText("Meine Bewertungen - " + ((Seller)user).getBusinessname() + " (Benutzer " + user.getUsername() + ") (ID " + user.getId() + ", Gewerbekunde)");
			}
			else if(user instanceof Customer)
			{
		    	ShowRatings_txtSellerBuyerName.setText("Meine Bewertungen - " + user.getUsername() + " (ID " + user.getId() + ", Privatkunde)");
			}
			}
		else
		{
			if(user instanceof Seller)
			{
		    	ShowRatings_txtSellerBuyerName.setText("Bewertungen von " + ((Seller)user).getBusinessname() + " (Benutzer " + user.getUsername() + ") (ID " + user.getId() + ", Gewerbekunde)");
			}
			else if(user instanceof Customer)
			{
		    	ShowRatings_txtSellerBuyerName.setText("Bewertungen von " + user.getUsername() + " (ID " + user.getId() + ", Privatkunde)");
			}
		}
		
		//Standardbild setzen
    	Image defaultImage = new Image(getClass().getResource("/SEPClient/UI/no-image.jpg").toString());
    	ShowRatings_ImgProfilePicture.setImage(defaultImage);
    	
    	//Bild setzen
    	InputStream in = new ByteArrayInputStream(user.getPicture());
		Image img = new Image(in);
		ShowRatings_ImgProfilePicture.setImage(img);
	}

	@FXML
    private Label ShowRatings_txtSellerBuyerName;

    @FXML
    private ImageView ShowRatings_ImgProfilePicture;

    @FXML
    private TableView<Rating> ShowRatings_ListRatings;
    
    @FXML
    private TableColumn<Rating, LocalDateTime> TableRatings_ColumnDate;

    @FXML
    private TableColumn<Rating, String> TableRatings_ColumnBy;

    @FXML
    private TableColumn<Rating, Integer> TableRatings_ColumnStars;

    @FXML
    private TableColumn<Rating, String> TableRatings_ColumnText;

    @FXML
    private Button ShowRatings_ReturnButton;

    @FXML
    private Label ShowRatings_txtAverageRating;

    @FXML
    private Label ShowRatings_txtRatingCount;
    
    
    private ObservableList<Rating> loadAllRatings() {
    	
    	if (ShowRatings_ListRatings.getItems() != null) {
    		ShowRatings_ListRatings.getItems().clear();
    	}
    	
    	HashMap <String,Object> requestMap = new HashMap<String, Object>();
    	requestMap.put("User", user);
    	requestMap.put("FetchAvg", false);
    	
    	ClientRequest req = new ClientRequest (Request.FetchRatings, requestMap);
    	Client client = Client.getClient();
    	ServerResponse queryResponse = client.sendClientRequest(req);
    	
    	if (queryResponse != null && queryResponse.getResponseMap() != null && queryResponse.getResponseMap().get("Ratings") != null) {
    		
    		Rating [] ratings = (Rating[])queryResponse.getResponseMap().get("Ratings");
    		ObservableList<Rating> ObservableRatings = FXCollections.observableArrayList(ratings);
    		ObservableRatings.removeIf(n -> (n == null));
    		
    		return ObservableRatings;
    		
    	}
    	
    	return null;
    }
    
    private void loadAvgRating() {
    	HashMap <String,Object> requestMap = new HashMap<String, Object>();
    	requestMap.put("User", user);
    	requestMap.put("FetchAvg", true);
    	
    	ClientRequest req = new ClientRequest (Request.FetchRatings, requestMap);
    	Client client = Client.getClient();
    	ServerResponse queryResponse = client.sendClientRequest(req);
    	
    	if (queryResponse != null && queryResponse.getResponseMap() != null && queryResponse.getResponseType() == Response.Success) {
    		ShowRatings_txtAverageRating.setText(queryResponse.getResponseMap().get("Average").toString());
    		ShowRatings_txtRatingCount.setText(queryResponse.getResponseMap().get("Amount").toString());
    	}
    	else {
			//fehler oder keine bewertungen
    		ShowRatings_txtAverageRating.setText("keine");
    		ShowRatings_txtRatingCount.setText("keine");
		}
    }
    
    @FXML
    void ShowRatings_ReturnButton_Click(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) ShowRatings_ReturnButton.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
    }
}
