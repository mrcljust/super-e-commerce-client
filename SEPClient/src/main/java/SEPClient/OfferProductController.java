package SEPClient;
import SEPCommon.Product;
import SEPCommon.Seller;
import SEPCommon.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class OfferProductController {
	
	static User user = null;
	
	public static void setUser(User _user)
	{
		user = _user;
	}
	
	@FXML
	private Button Sell_ButtonSellConfirm;
	
	@FXML
	private Button Sell_ButtonSellCsv;
	
	@FXML
	private Button Sell_ButtonChooseFile;

}

