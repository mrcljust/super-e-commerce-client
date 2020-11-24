package SEPClient;

import java.util.HashMap;

import SEPCommon.ClientRequest;
import SEPCommon.Product;
import SEPCommon.Request;
import SEPCommon.Response;
import SEPCommon.Seller;
import SEPCommon.ServerResponse;
import SEPCommon.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
    	loadLastViewedProducts();
    	selectionsChangedListener();
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
    	
    	//Aktuelle Produktinfos leeren
    	MainScreen_LabelProductTitle.setText("");
    	MainScreen_LabelProductPrice.setText("");
    	MainScreen_LabelProductSeller.setText("");
    	MainScreen_LabelProductCategory.setText("");
    	MainScreen_TextProductDescription.setText("");
    	MainScreen_ButtonBuyProduct.setVisible(false);
    	MainScreen_TextProductDescription.setVisible(false);
    	
    	//Alle Kategorien Item hinzufÃ¼gen
    	MainScreen_ChoiceBox_Category.getItems().add("Alle Kategorien");
    	MainScreen_ChoiceBox_Category.getSelectionModel().select("Alle Kategorien");
    	
    	//Werte an die Spalten der Kataloglisten zuweisen
    	catalogIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        catalogProductColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        catalogPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        catalogSellerColumn.setCellValueFactory(new PropertyValueFactory<>("businessname"));
        catalogCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        lastviewedIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    	lastviewedProductColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    	lastviewedPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    	lastviewedSellerColumn.setCellValueFactory(new PropertyValueFactory<>("businessname"));
    	lastviewedCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
    }
    
    public void LoadAllProducts()
    {
    	MainScreen_ListCatalog.getItems().clear();
    	
    	ClientRequest req = new ClientRequest(Request.FetchProducts, null);
    	Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);
		
		if(queryResponse!=null && queryResponse.getResponseMap() != null && queryResponse.getResponseMap().get("Products")!=null)
		{
			//Product Array
			Product[] products = (Product[])queryResponse.getResponseMap().get("Products");
			ObservableList<Product> ObservableProducts = FXCollections.observableArrayList(products);
			
			//Kategorien in Liste einfÃ¼gen
			for(Product p: products)
			{
				String pCategory = p.getCategory();
				if(!MainScreen_ChoiceBox_Category.getItems().contains(pCategory))
				{
					MainScreen_ChoiceBox_Category.getItems().add(pCategory);
				}
			}
			
			MainScreen_ListCatalog.setItems(ObservableProducts);
		}
    }
    
    private void loadLastViewedProducts()
    {
    	MainScreen_ListLastViewed.getItems().clear();
    	
    	lastviewedIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    	lastviewedProductColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    	lastviewedPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    	lastviewedSellerColumn.setCellValueFactory(new PropertyValueFactory<>("businessname"));
    	lastviewedCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        
        HashMap<String, Object> requestMap = new HashMap<String, Object>();
    	requestMap.put("User", user);
        ClientRequest req = new ClientRequest(Request.LastViewedProducts, requestMap);
    	Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);
		
		if(queryResponse.getResponseType() == Response.Success)
		{
			//Product Array
			Product[] products = (Product[])queryResponse.getResponseMap().get("Products");
			ObservableList<Product> ObservableProducts = FXCollections.observableArrayList(products);
			
			MainScreen_ListLastViewed.setItems(ObservableProducts);
		}
    }
    
    private void selectionsChangedListener()
    {
    	//ListCatalog Selection Change Listener
    	MainScreen_ListCatalog.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
    		//was passiert, wenn ein Eintrag in der ListCatalog ausgewählt wird
    		updateArticleInfo(false);
    		addToLastViewedItems();
    	});
    	
    	//ListLastViewed Selection Change Listener
	    MainScreen_ListLastViewed.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
	    	//was passiert, wenn ein Eintrag in der ListLastViewed ausgewählt wird
	    	updateArticleInfo(false);
	    });
	}
    
    private void updateArticleInfo(boolean selectionInCatalog)
    {
    	//selectionInCatalog = true: Eintrag in ListCatalog ausgewählt
    	//selectionInCatalog = false: Eintrag in ListLastViewed ausgewählt
    	
    }
    
    private void addToLastViewedItems() {
    	//Zu zuletzt angesehenen Produkten hinzufügen
    	Product viewedProduct = MainScreen_ListCatalog.getSelectionModel().getSelectedItem();
    	
    	boolean alreadyInLastViewed = false;
    	if(MainScreen_ListLastViewed.getItems() != null)
    	{
	    	for(Product p: MainScreen_ListLastViewed.getItems())
	    	{
	    		if(p.getId() == viewedProduct.getId())
	    		{
	    			alreadyInLastViewed = true;
	    		}
	    	}
    	}
    	if(!alreadyInLastViewed)
    	{
    		//noch nicht in der Liste der zuletzt angesehenen Produkte, hinzufügen
    		HashMap<String, Object> requestMap = new HashMap<String, Object>();
	    	requestMap.put("User", user);
	    	requestMap.put("ViewedProductID", viewedProduct.getId());
	    	ClientRequest req = new ClientRequest(Request.AddLastViewedProduct, requestMap);
	    	Client client = Client.getClient();
			ServerResponse queryResponse = client.sendClientRequest(req);
			if(queryResponse.getResponseType() == Response.Success)
			{
				loadLastViewedProducts();
			}
    	}
	}
    
    
	
    @FXML
    private ChoiceBox<String> MainScreen_ChoiceBox_Category;
    
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
    private TableView<Product> MainScreen_ListLastViewed;

    @FXML
    private TableView<Product> MainScreen_ListCatalog;
    
    @FXML
    private TableColumn<?, ?> lastviewedIdColumn;

    @FXML
    private TableColumn<?, ?> lastviewedProductColumn;

    @FXML
    private TableColumn<?, ?> lastviewedPriceColumn;

    @FXML
    private TableColumn<?, ?> lastviewedSellerColumn;

    @FXML
    private TableColumn<?, ?> lastviewedCategoryColumn;

    @FXML
    private TableColumn<?, ?> catalogIdColumn;

    @FXML
    private TableColumn<?, ?> catalogProductColumn;

    @FXML
    private TableColumn<?, ?> catalogPriceColumn;

    @FXML
    private TableColumn<?, ?> catalogSellerColumn;

    @FXML
    private TableColumn<?, ?> catalogCategoryColumn;

    @FXML
    private Label MainScreen_LabelProductTitle;

    @FXML
    private Label MainScreen_LabelProductSeller;

    @FXML
    private Label MainScreen_LabelProductPrice;

    @FXML
    private Label MainScreen_LabelProductCategory;

    @FXML
    private TextArea MainScreen_TextProductDescription;
    
    @FXML
    private ImageView MainScreen_ImgProfilePicture;

    @FXML
    void MainScreen_CloseButtonMenuClick(ActionEvent event) {
    	System.exit(0);
    }

    @FXML
    void MainScreen_InfoButtonMenuClick(ActionEvent event) {
    	FXMLHandler.ShowMessageBox("ï¿½ 'Super-E-commerce-Platform' wurde entwickelt von Denis Artjuch, Yannis Bromby, Kamil Chahrour, Marcel Just und Hannah Kalker. Gruppe B, Modul Software Entwicklung & Programmierung, Universitï¿½t Duisburg-Essen, 2020/21.",
				"Super-E-commerce-Platform", "Super-E-commerce-Platform", AlertType.INFORMATION, true,
				false);
    }

    @FXML
    void MainScreen_btnAddWalletClick(ActionEvent event) {
    	WalletController.setUser(user);
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonEditAccount.getScene().getWindow(), "Wallet", "Guthaben aufladen", true, true);
    }

    @FXML
    void MainScreen_btnEditAccountClick(ActionEvent event) {
    	
    }

    @FXML
    void MainScreen_btnLogoutClick(ActionEvent event) {
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonLogout.getScene().getWindow(), "Start", "Super-E-commerce-Platform", true, true);
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
    //OfferProduct Ã¶ffnen
    void MainScreen_btnSellProductClick(ActionEvent event) {
    	OfferProductController.setUser(user);
    	FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonSellProduct.getScene().getWindow(), "OfferProduct", "Produkt(e) anbieten", true, true);
    }
    
    @FXML
    void MainScreen_BuyProductClick (ActionEvent event) {
    	
    }
    
}

