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

public class MainScreenController {

	static User user = null;
	
	public static void setUser(User _user)
	{
		user = _user;
	}

	
    @FXML
    private ListView<Product> MainScreen_ListLastViewed;

    @FXML
    private Label MainScreen_LabelWallet;

    @FXML
    private Button MainScreen_ButtonEditAccount;

    @FXML
    private Button MainScreen_ButtonAddWallet;

    @FXML
    private Button MainScreen_ButtonLogout;

    @FXML
    private Button MainScreen_ButtonSellProduct;

    @FXML
    private Button MainScreen_ButtonBuyProduct;

    @FXML
    private Button MainScreen_ButtonMyProducts;

    @FXML
    private Button MainScreen_ButtonPurchases;

    @FXML
    private Label MainScreen_LabelLoggedInAs;

    @FXML
    private TextField MainScreen_txtSearch;

    @FXML
    private ListView<Product> MainScreen_ListCatalog;

    @FXML
    void MainScreen_CloseButtonMenuClick(ActionEvent event) {
    	
    }

    @FXML
    void MainScreen_InfoButtonMenuClick(ActionEvent event) {
    	
    }

    @FXML
    void MainScreen_btnAddWalletClick(ActionEvent event) {

    }

    @FXML
    void MainScreen_btnEditAccountClick(ActionEvent event) {
    	
    }

    @FXML
    void MainScreen_btnLogoutClick(ActionEvent event) {

    }

    @FXML
    void MainScreen_btnMyProductsClick(ActionEvent event) {

    }

    @FXML
    void MainScreen_btnMyPurchasesClick(ActionEvent event) {

    }

    @FXML
    void MainScreen_btnSearchOKClick(ActionEvent event) {

    }

    @FXML
    void MainScreen_btnSellProductClick(ActionEvent event) {

    }
}

