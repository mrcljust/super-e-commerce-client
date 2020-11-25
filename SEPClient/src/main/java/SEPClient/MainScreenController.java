package SEPClient;

import java.util.HashMap;

import SEPCommon.ClientRequest;
import SEPCommon.Customer;
import SEPCommon.Product;
import SEPCommon.Request;
import SEPCommon.Response;
import SEPCommon.Seller;
import SEPCommon.ServerResponse;
import SEPCommon.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	Product[] lastSearchResult;
	boolean currentSearchEvent = false;
	
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
    	categoryChangedListener();
    }
    
    public void refreshView()
    {
    	MainScreen_LabelWallet.setText("Guthaben: " + SEPCommon.Methods.round(user.getWallet(), 2) + "$");
    	
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
    	
    	//Alle Kategorien Item hinzufügen
    	MainScreen_ChoiceBox_Category.getItems().add("Alle Kategorien");
    	MainScreen_ChoiceBox_Category.getSelectionModel().select("Alle Kategorien");
    	
    	//Werte an die Spalten der Kataloglisten zuweisen
    	catalogIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        catalogProductColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        catalogPriceColumn.setCellValueFactory(new PropertyValueFactory<>("priceString"));
        catalogSellerColumn.setCellValueFactory(new PropertyValueFactory<>("businessname"));
        catalogCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        lastviewedIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    	lastviewedProductColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    	lastviewedPriceColumn.setCellValueFactory(new PropertyValueFactory<>("priceString"));
    	lastviewedSellerColumn.setCellValueFactory(new PropertyValueFactory<>("businessname"));
    	lastviewedCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
    	
    	if(MainScreen_ListCatalog.getSelectionModel().getSelectedItem() != null)
    	{
	    	MainScreen_ListCatalog.getSelectionModel().clearSelection();
    	}
		
		if(MainScreen_ListLastViewed.getSelectionModel().getSelectedItem() != null)
    	{
	    	MainScreen_ListLastViewed.getSelectionModel().clearSelection();
    	}
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
			
			//Kategorien in Liste einfügen
			for(Product p: products)
			{
				String pCategory = p.getCategory();
				if(!MainScreen_ChoiceBox_Category.getItems().contains(pCategory))
				{
					if(pCategory!="") //leeren Kategorie-String nicht hinzuf�gen
					{
						MainScreen_ChoiceBox_Category.getItems().add(pCategory);
					}
				}
			}
			
			MainScreen_ListCatalog.setItems(ObservableProducts);
		}
    }
    
    private void loadLastViewedProducts() {
    	MainScreen_ListLastViewed.getItems().clear();
        
        HashMap<String, Object> requestMap = new HashMap<String, Object>();
    	requestMap.put("User", user);
        ClientRequest req = new ClientRequest(Request.LastViewedProducts, requestMap);
    	Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);
		
		if(queryResponse.getResponseType() == Response.Success) {
			//Product Array
			Product[] products = (Product[])queryResponse.getResponseMap().get("Products");
			ObservableList<Product> ObservableProducts = FXCollections.observableArrayList(products);
			
			MainScreen_ListLastViewed.setItems(ObservableProducts);
		}
    }
    
	//ChoiceBox Categories Selection Change Listener
	//wird aufgerufen, wenn eine Kategorie ausgewählt wird
    private void categoryChangedListener() {
	    MainScreen_ChoiceBox_Category.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				categoryChangedEvent(newValue.intValue());
			}
	      });
    }
    
    private void selectionsChangedListener() {

    	//ListCatalog Selection Change Listener
    	MainScreen_ListCatalog.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
    		//was passiert, wenn ein Eintrag in der ListCatalog ausgewählt wird
    		if(newSelection != null)
    		{
    		updateArticleInfo(true);
    		addToLastViewedItems();
    		}
    	});
    	
    	//ListLastViewed Selection Change Listener
	    MainScreen_ListLastViewed.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
	    	//was passiert, wenn ein Eintrag in der ListLastViewed ausgewählt wird
	    	if(newSelection != null)
	    	{
		    	updateArticleInfo(false);
	    	}
	    });
	}
    
    private void updateArticleInfo(boolean selectionInCatalog)
    {
    	if(selectionInCatalog==true)
    	{
    		//Artikel in der ListCatalog ausgewählt
    		if(MainScreen_ListCatalog.getSelectionModel().getSelectedItem() != null)
	    	{
		    	MainScreen_ListLastViewed.getSelectionModel().clearSelection();
	    		//Item in der Katalog-Liste angewählt
		    	
		    	//Daten einfügen
		    	MainScreen_LabelProductTitle.setText(MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getName());
		    	MainScreen_LabelProductPrice.setText("Preis: " + MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getPrice() + "$");
		    	MainScreen_LabelProductSeller.setText("Verk�ufer: " + MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getSeller().getBusinessname() + " (Benutzer " + MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getSeller().getUsername() + ")");
		    	String selectedCategory = MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getCategory();
		    	if(selectedCategory=="")
		    	{
			    	MainScreen_LabelProductCategory.setText("Kategorie: (keine Kategorie)");
		    	}
		    	else
		    	{
			    	MainScreen_LabelProductCategory.setText("Kategorie: " + selectedCategory);
		    	}
		    	MainScreen_TextProductDescription.setText(MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getDescription());
		    	
		    	MainScreen_ButtonBuyProduct.setVisible(true);
		    	MainScreen_TextProductDescription.setVisible(true);
		    	
		    	//Kaufen Button nur für Customer enablen
		    	if(user instanceof Customer)
		    	{
		    		MainScreen_ButtonBuyProduct.setDisable(false);
		    	}
		    	else
		    	{
		    		MainScreen_ButtonBuyProduct.setDisable(true);
				}
	    	}
    	}
    	else
    	{
    		//Artikel in ListLastViewed ausgewählt
    		if(MainScreen_ListLastViewed.getSelectionModel().getSelectedItem() != null)
	    	{
		    	MainScreen_ListCatalog.getSelectionModel().clearSelection();
	    		//Item in der zuletzt angesehen Liste angewählt
		    	
		    	//Daten einfügen
		    	MainScreen_LabelProductTitle.setText(MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getName());
		    	MainScreen_LabelProductPrice.setText("Preis: " + MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getPrice() + "$");
		    	MainScreen_LabelProductSeller.setText("Verk�ufer: " + MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getSeller().getBusinessname() + " (Benutzer " + MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getSeller().getUsername() + ")");
		    	String selectedCategory = MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getCategory();
		    	if(selectedCategory=="")
		    	{
			    	MainScreen_LabelProductCategory.setText("Kategorie: (keine Kategorie)");
		    	}
		    	else
		    	{
			    	MainScreen_LabelProductCategory.setText("Kategorie: " + selectedCategory);
		    	}
		    	MainScreen_TextProductDescription.setText(MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getDescription());
		    
		    	MainScreen_ButtonBuyProduct.setVisible(true);
		    	MainScreen_TextProductDescription.setVisible(true);
		    	
		    	//Kaufen Button nur für Customer enablen
		    	if(user instanceof Customer)
		    	{
		    		MainScreen_ButtonBuyProduct.setDisable(false);
		    	}
		    	else
		    	{
		    		MainScreen_ButtonBuyProduct.setDisable(true);
				}
	    	}
    	}
    }
    
    private void addToLastViewedItems() {
    	//Zu zuletzt angesehenen Produkten hinzuf�gen
    	Product viewedProduct = MainScreen_ListCatalog.getSelectionModel().getSelectedItem();
    	
    	boolean alreadyInLastViewed = false;
    	if(MainScreen_ListLastViewed.getItems() != null)
    	{
			for(Product p: MainScreen_ListLastViewed.getItems())
	    	{
	    		if(p != null && p.getId() == viewedProduct.getId())
	    		{
	    			alreadyInLastViewed = true;
	    		}
	    	}
    	}
    	if(!alreadyInLastViewed)
    	{
    		//noch nicht in der Liste der zuletzt angesehenen Produkte, hinzuf�gen
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
    
    private void categoryChangedEvent(int newValue) {
    	//Katalog leeren
    	String selectedCategoryString = (MainScreen_ChoiceBox_Category.getItems().get((Integer) newValue)); //Name der selektierten Kategorie
		MainScreen_ListCatalog.getItems().clear(); //Katalog Liste leeren
		
		//keine Kategorie, also alle Kategorien
		if(newValue==0) {
			//Alle Kategorien ausgewählt und kein Suchbegriff ist eingegeben
			if(currentSearchEvent) {
				LoadAllProducts();
				currentSearchEvent=false;
			}
			//Alle Kategorien ausgewählt und Suchbegriff ist eingegeben
			else {
				searchChangedEvent();
			}
			
		} else {
			//Sonstige Kategorie ausgewählt
			HashMap<String, Object> requestMap = new HashMap<String, Object>();
	    	requestMap.put("Category", selectedCategoryString);
	    	
	    	ClientRequest req = new ClientRequest(Request.FetchProducts, requestMap);
	    	Client client = Client.getClient();
			ServerResponse queryResponse = client.sendClientRequest(req);
			if(queryResponse.getResponseType() != null)	{
				Product[] articleInCategory = (Product[])queryResponse.getResponseMap().get("Products"); //Produkte in Kategorie
				Product[] articlesInCategoryAndSearch = null;
				
				if(lastSearchResult == null) {
					//Letzte Suche war leer bzw. noch keine Suche getätigt
					articlesInCategoryAndSearch=articleInCategory;
				}
				else {
					//aktuelle Suche
					int i=0;
					for(Product p : lastSearchResult) {
						i++;
					}
					if(i>0)	{
						articlesInCategoryAndSearch = new Product[i];
						int ii=0;
						for(Product p : lastSearchResult) {
							if(p.getCategory().equals(selectedCategoryString)) {
								articlesInCategoryAndSearch[ii]=p; 
								ii++;
							}
						}
					}
				}
				ObservableList<Product> ObservableProducts = FXCollections.observableArrayList(articlesInCategoryAndSearch);
				
				MainScreen_ListCatalog.setItems(ObservableProducts);
			}
		}
		currentSearchEvent=false;
    }
    
    private void searchChangedEvent()
    {
    	//Katalog leeren
		MainScreen_ListCatalog.getItems().clear();
		
		
		Product[] articlesInSearch = null;
		//Kein Suchbegriff eingegeben
		if(MainScreen_txtSearch.getText()==null || MainScreen_txtSearch.getText().isEmpty() || MainScreen_txtSearch.getText().isBlank() || MainScreen_txtSearch.getText() == "") {
			currentSearchEvent=true;
			lastSearchResult=null;
			categoryChangedEvent(MainScreen_ChoiceBox_Category.getSelectionModel().getSelectedIndex());
		}
		
		//Suchbegriff eingegeben
		else {
			HashMap<String, Object> requestMap = new HashMap<String, Object>();
	    	requestMap.put("SearchString", MainScreen_txtSearch.getText());
	    	
	    	ClientRequest req = new ClientRequest(Request.FetchProducts, requestMap);
	    	Client client = Client.getClient();
			ServerResponse queryResponse = client.sendClientRequest(req);
			
			
			//wenn es keine Probleme gibt, kann Suche starten
			if(queryResponse.getResponseType() != null)	{
				//Verkapselte Suche (Kategorie und Suchbegriff)
				articlesInSearch = (Product[])queryResponse.getResponseMap().get("Products");
				Product[] articlesInSearchAndCategory = null;
				//Alle Kategorien ausgewählt
				if(MainScreen_ChoiceBox_Category.getSelectionModel().getSelectedIndex()==0)	{
					articlesInSearchAndCategory = articlesInSearch;
				}
				else {
					//bestimmte Kategorie ausgewählt
					int i=0;
					for(Product p : articlesInSearch) {
						i++;
					}
					if(i>0)	{
						articlesInSearchAndCategory = new Product[i];
						int ii=0;
						for(Product p : articlesInSearch) {
							if(p.getCategory().equals(MainScreen_ChoiceBox_Category.getSelectionModel().getSelectedItem()))	{
								articlesInSearchAndCategory[ii]=p; 
								ii++;
							}
						}
					}
				}
				
				//Artikel in Katalog anzeigen
				ObservableList<Product> ObservableProducts = FXCollections.observableArrayList(articlesInSearchAndCategory);
				
				MainScreen_ListCatalog.setItems(ObservableProducts);
			}
		}
		lastSearchResult=articlesInSearch;
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

    	FXMLHandler.ShowMessageBox("� 'Super-E-commerce-Platform' wurde entwickelt von Denis Artjuch, Yannis Bromby, Kamil Chahrour, Marcel Just und Hannah Kalker. Gruppe B, Modul Software Entwicklung & Programmierung, Universit�t Duisburg-Essen, 2020/21.",
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
    	EditAccountController.setUser(user);
    	FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonEditAccount.getScene().getWindow(), "EditAccount", "Konto bearbeiten", true, true);
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
    	searchChangedEvent();
    }

    @FXML
    //OfferProduct oeffnen
    void MainScreen_btnSellProductClick(ActionEvent event) {
    	OfferProductController.setUser(user);
    	
    	//ggf. Kategorien mit �bergeben
    	if(MainScreen_ChoiceBox_Category.getItems() != null)
    	{
    		OfferProductController.setCategoryList(MainScreen_ChoiceBox_Category.getItems());
    	}
    	FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonSellProduct.getScene().getWindow(), "OfferProduct", "Produkt(e) anbieten", true, true);
    }
    
    @FXML
    void MainScreen_BuyProductClick (ActionEvent event)
    {
    	Product productToBuy = null;
    	
    	//Zu kaufendes Produkt festlegen
    	if(MainScreen_ListCatalog.getSelectionModel().getSelectedItem() != null)
    	{
    		productToBuy = MainScreen_ListCatalog.getSelectionModel().getSelectedItem();
    	}
    	else if(MainScreen_ListLastViewed.getSelectionModel().getSelectedItem() != null)
    	{
    		productToBuy = MainScreen_ListLastViewed.getSelectionModel().getSelectedItem();
    	}
    	else
    	{
			FXMLHandler.ShowMessageBox("Es ist kein Produkt ausgewählt.", "Fehler", "Fehler", AlertType.ERROR, true, false);
			return;
    	}
    	
    	//Prüfen, ob genug Guthaben vorhanden ist
    	if(user.getWallet()<productToBuy.getPrice())
    	{
			FXMLHandler.ShowMessageBox("Ihr Guthaben reicht nicht aus, um das ausgewählte Produkt zu kaufen.", "Fehler", "Fehler", AlertType.ERROR, true, false);
			return;
    	}
    	
    	//Client BuyItem Request senden
    	//Es wird bei dieser Request automatisch das Käuferkonto um den Produktpreis verringert
    	//und das Verkäuferkonto um den Produktpreis erhöht
    	HashMap<String, Object> requestMap = new HashMap<String, Object>();
    	requestMap.put("User", user);
    	requestMap.put("Product", productToBuy);
        ClientRequest req = new ClientRequest(Request.BuyItem, requestMap);
    	Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);
		
		
		//Antwort auslesen
		if(queryResponse.getResponseType() == Response.NoDBConnection)
		{
			FXMLHandler.ShowMessageBox("Es konnte keine Verbindung zur Datenbank hergestellt werden, es wurde daher kein Kauf durchgeführt.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
			return;
		}
		else if(queryResponse.getResponseType() == Response.Failure)
		{
			FXMLHandler.ShowMessageBox("Beim Kaufen des Artikels ist ein unbekannter Fehler aufgetreten.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
			return;
		}
		else if(queryResponse.getResponseType() == Response.Success)
		{
			FXMLHandler.ShowMessageBox("Sie haben den Artikel '" + productToBuy.getName() + "' erfolgreich für " + productToBuy.getPriceString() + " gekauft.",
					"Kauf erfolgreich", "Kauf erfolgreich", AlertType.CONFIRMATION, true,
					false);
			//MainScreen oeffnen
			user.setWallet(user.getWallet() - productToBuy.getPrice());
			MainScreenController.setUser(user);
			refreshView();
		}
    }
    
}

