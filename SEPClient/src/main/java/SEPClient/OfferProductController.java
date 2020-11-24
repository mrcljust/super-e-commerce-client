package SEPClient;

import SEPCommon.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class OfferProductController {

	static User user = null;
	
	public static void setUser(User _user)
	{
		user = _user;
	}
	
    @FXML
    private TextField Sell_txtName;

    @FXML
    private TextField Sell_txtPreis;

    @FXML
    private Button Sell_ButtonSellConfirm;

    @FXML
    private TextField Sell_txtCategory;

    @FXML
    private TextArea Sell_txtDescription;

    @FXML
    private Button Sell_ButtonSellCsv;

    @FXML
    private Button Sell_ButtonChooseFile;

    @FXML
    private TextField Sell_txtCSV;

    @FXML
    void Sell_ChooseFile(ActionEvent event) {

    }

    @FXML
    void Sell_SellConfirmClick(ActionEvent event) {

    }

    @FXML
    void Sell_SellCsvClick(ActionEvent event) {

    }

}
