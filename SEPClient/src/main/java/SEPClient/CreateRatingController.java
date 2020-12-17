package SEPClient;

import java.io.IOException;

import SEPCommon.Auction;
import SEPCommon.Order;
import SEPCommon.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;

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

    }

    @FXML
    void CreateRating_ButtonReturn_Click(ActionEvent event) {

    }
}
