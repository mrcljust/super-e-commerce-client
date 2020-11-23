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
    public void initialize() {
    	refreshView();
    	LoadAllProducts();
    }
    
    public void refreshView()
    {
    	MainScreen_LabelWallet.setText("Guthaben: " + user.getWallet() + "$");
    	
    	if(user instanceof Seller)
    	{
    		//Gewerbekunde
        	MainScreen_LabelLoggedInAs.setText("Angemeldet als: " + user.getUsername() + " (ID " + user.getId() + ", Gewerbekunde)");
    		MainScreen_ButtonAddWallet.setDisable(true);
    		MainScreen_ButtonSellProduct.setDisable(false);
    		MainScreen_ButtonMyProducts.setDisable(false);
    		MainScreen_ButtonPurchases.setDisable(true);
    	}
    	else
    	{
    		//Privatkunde
        	MainScreen_LabelLoggedInAs.setText("Angemeldet als: " + user.getUsername() + " (ID " + user.getId() + ", Privatkunde)");
    		MainScreen_ButtonAddWallet.setDisable(false);
    		MainScreen_ButtonSellProduct.setDisable(true);
    		MainScreen_ButtonMyProducts.setDisable(true);
    		MainScreen_ButtonPurchases.setDisable(false);
    	}
    }
    
    public void LoadAllProducts()
    {
    	
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
    	System.exit(0);
    }

    @FXML
    void MainScreen_InfoButtonMenuClick(ActionEvent event) {
    	FXMLHandler.ShowMessageBox("© 'Super-E-commerce-Platform' wurde entwickelt von Denis Artjuch, Yannis Bromby, Kamil Chahrour, Marcel Just und Hannah Kalker. Gruppe B, Modul Software Entwicklung & Programmierung, Universität Duisburg-Essen, 2020/21.",
				"Super-E-commerce-Platform", "Super-E-commerce-Platform", AlertType.INFORMATION, true,
				false);
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
    //OfferProduct öffnen
    void MainScreen_btnSellProductClick(ActionEvent event) {
    	OfferProductController.setUser(user);
    	FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonSellProduct.getScene().getWindow(), "OfferProduct", "Produkt(e) anbieten", true, true);
    }
    
    @FXML
    void MainScreen_BuyProductClick (ActionEvent event) {
    	
    }
    
} //push

