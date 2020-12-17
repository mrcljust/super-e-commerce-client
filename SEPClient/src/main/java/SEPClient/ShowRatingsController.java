package SEPClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import SEPCommon.Customer;
import SEPCommon.Rating;
import SEPCommon.Seller;
import SEPCommon.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
		if(viewOwnRatings )
		{
			//eigene Bewertungen
			if(user instanceof Seller)
			{
		    	ShowRatings_txtSellerBuyerName.setText("Meine erhaltenen Bewertungen - " + ((Seller)user).getBusinessname() + " (Benutzer " + user.getUsername() + ") (ID " + user.getId() + ", Gewerbekunde)");
			}
			else if(user instanceof Customer)
			{
		    	ShowRatings_txtSellerBuyerName.setText("Meine erhaltenen Bewertungen - " + user.getUsername() + " (ID " + user.getId() + ", Privatkunde)");
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
    private TableColumn<Rating, Date> TableRatings_ColumnDate;

    @FXML
    private TableColumn<Rating, String> TableRatings_ColumnBy;

    @FXML
    private TableColumn<Rating, Integer> TableRatings_ColumnStars;

    @FXML
    private TableColumn<Rating, String> TableRatings_ColumnText;

    @FXML
    private Button ShowRatings_CreateRatingForBuyer;

    @FXML
    private Button ShowRatings_ReturnButton;

    @FXML
    private Label ShowRatings_txtAverageRating;

    @FXML
    private Label ShowRatings_txtRatingCount;

    @FXML
    void ShowRatings_CreateRatingForBuyer_Click(ActionEvent event) {

    }

    @FXML
    void ShowRatings_ReturnButton_Click(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) ShowRatings_ReturnButton.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
    }
}
