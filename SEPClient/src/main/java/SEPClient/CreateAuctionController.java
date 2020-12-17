package SEPClient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import SEPCommon.Customer;

public class CreateAuctionController {

	private static Customer customer = null;

	public static void setCustomer(Customer _customer) {
		customer = _customer;
	}

	public void initialize() throws IOException {
    	ToggleGroup radioGroupShipping = new ToggleGroup();
    	Auction_radioShipping.setToggleGroup(radioGroupShipping);
    	Auction_radioPickUp.setToggleGroup(radioGroupShipping);
    	
    	ToggleGroup radioGroupStarttime = new ToggleGroup();
    	Auction_radioStartNow.setToggleGroup(radioGroupStarttime);
    	Auction_radioStartOther.setToggleGroup(radioGroupStarttime);
    	
    	Auction_EndDatePicker.setValue(LocalDate.now(ZoneId.of("CET")));
    	Auction_EndTime.setText(LocalTime.now(ZoneId.of("CET")).toString().substring(0, 5));
	}
	
    @FXML
    private TextField Auction_txtName;

    @FXML
    private TextField Auction_txtStartPrice;

    @FXML
    private TextField Auction_txtMinBid;

    @FXML
    private HTMLEditor Auction_txtDescription;

    @FXML
    private RadioButton Auction_radioStartNow;

    @FXML
    private RadioButton Auction_radioStartOther;

    @FXML
    private DatePicker Auction_StartDatePicker;

    @FXML
    private TextField Auction_StartTime;

    @FXML
    private DatePicker Auction_EndDatePicker;

    @FXML
    private TextField Auction_EndTime;

    @FXML
    private ImageView Auction_imgPicture;

    @FXML
    private Button Auction_ButtonChooseImage;

    @FXML
    private Button Auction_ButtonDeleteImage;

    @FXML
    private RadioButton Auction_radioShipping;

    @FXML
    private RadioButton Auction_radioPickUp;

    @FXML
    private Button Auction_Insert;

    @FXML
    private Button Auction_Return;

    @FXML
    void Auction_DeletePictureClick(ActionEvent event) {

    }

    @FXML
    void Auction_EndDatePicker_Choice(ActionEvent event) {

    }

    @FXML
    void Auction_InsertClick(ActionEvent event) {

    }

    @FXML
    void Auction_OpenPictureClick(ActionEvent event) {

    }

    @FXML
    void Auction_ReturnClick(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) Auction_Return.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
    }

    @FXML
    void Auction_StartDatePicker_Choice(ActionEvent event) {

    }

    @FXML
    void Auction_radioStartNow_Click(ActionEvent event) {
    	Auction_StartDatePicker.setDisable(true);
    	Auction_StartTime.setDisable(true);
    	Auction_StartDatePicker.setValue(null);
    }

    @FXML
    void Auction_radioStartOther_Click(ActionEvent event) {
    	Auction_StartDatePicker.setDisable(false);
    	Auction_StartTime.setDisable(false);
    	Auction_StartDatePicker.setValue(LocalDate.now(ZoneId.of("CET")));
    	Auction_StartTime.setText(LocalTime.now(ZoneId.of("CET")).toString().substring(0, 5));
    }

}

