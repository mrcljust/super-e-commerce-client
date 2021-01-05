package SEPClient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import SEPCommon.Auction;
import SEPCommon.AuctionType;
import SEPCommon.ClientRequest;
import SEPCommon.Constants;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

public class MainScreenController {

	private static User user = null;
	private Product[] lastSearchResult;
	private boolean currentSearchEvent = false;
	private boolean avoidCategoryChangedEvent = false;
	private boolean avoidClearAuctions = false;
	
	public static void setUser(User _user)
	{
		user = _user;
	}
	
    @FXML
    public void initialize() {
    	startView();
    	refreshViewArticles();
    	
    	selectionsChangedListener();
    	categoryChangedListener();
    	tabChangedListener();
    }
    
    private void startView()
    {
    	//Wird einmal am Start aufgerufen um Zellwerte sowie ToggleGroups festzulegen
    	
    	//Werte an die Zellen der Katalogliste zuweisen
    	catalogIdColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("id"));
        catalogProductColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        catalogSellerColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("businessname"));
        
        catalogPriceColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
        //Anzeigewert f�r Preis anpassen
        catalogPriceColumn.setCellFactory(tc -> new TableCell<Product, Double>() {
    	    @Override
    	    protected void updateItem(Double price, boolean empty) {
    	        super.updateItem(price, empty);
    	        if (empty || price==null) {
    	            setText(null);
    	        } else {
    	            setText(Constants.DOUBLEFORMAT.format(price) + Constants.CURRENCY);
    	        }
    	    }
    	});
        
        catalogCategoryColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("category"));
        //Anzeigewert f�r Kategorie anpassen
        catalogCategoryColumn.setCellFactory(tc -> new TableCell<Product, String>() {
    	    @Override
    	    protected void updateItem(String category, boolean empty) {
    	        super.updateItem(category, empty);
    	        if (empty) {
    	            setText(null);
    	        } else if(category=="" || category==null) {
    	        	setText("(Keine Kategorie)");
    	        }else {
    	            setText(category);
    	        }
    	    }
    	});

    	//Werte an die Zellen der LastViewedListe zuweisen
        lastviewedIdColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("id"));
    	lastviewedProductColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
    	lastviewedSellerColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("businessname"));
    	
    	lastviewedPriceColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
        //Anzeigewert f�r Preis anpassen
    	lastviewedPriceColumn.setCellFactory(tc -> new TableCell<Product, Double>() {
    	    @Override
    	    protected void updateItem(Double price, boolean empty) {
    	        super.updateItem(price, empty);
    	        if (empty || price==null) {
    	            setText(null);
    	        } else {
    	            setText(Constants.DOUBLEFORMAT.format(price) + Constants.CURRENCY);
    	        }
    	    }
    	});
    	
    	lastviewedCategoryColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("category"));
        //Anzeigewert f�r Kategorie anpassen
    	lastviewedCategoryColumn.setCellFactory(tc -> new TableCell<Product, String>() {
    	    @Override
    	    protected void updateItem(String category, boolean empty) {
    	        super.updateItem(category, empty);
    	        if (empty) {
    	            setText(null);
    	        } else if(category=="" || category==null) {
    	        	setText("(Keine Kategorie)");
    	        }else {
    	            setText(category);
    	        }
    	    }
    	});
    	
    	//Werte an die Zellen der Auktionslisten zuweisen
    	auctionsCatalogIdColumn.setCellValueFactory(new PropertyValueFactory<Auction, Integer>("id"));
        auctionsCatalogTitleColumn.setCellValueFactory(new PropertyValueFactory<Auction, String>("title"));
        
        auctionsCatalogCurrentBidColumn.setCellValueFactory(new PropertyValueFactory<Auction, Double>("currentBid"));
        //Anzeigewert f�r Gebot anpassen
        auctionsCatalogCurrentBidColumn.setCellFactory(tc -> new TableCell<Auction, Double>() {
    	    @Override
    	    protected void updateItem(Double price, boolean empty) {
    	        super.updateItem(price, empty);
    	        if (empty || price==null) {
    	            setText(null);
    	        } else {
    	            setText(Constants.DOUBLEFORMAT.format(price) + Constants.CURRENCY);
    	        }
    	    }
    	});
        
        auctionsCatalogMinBidColumn.setCellValueFactory(new PropertyValueFactory<Auction, Double>("minBid"));
        //Anzeigewert f�r Gebot anpassen
        auctionsCatalogMinBidColumn.setCellFactory(tc -> new TableCell<Auction, Double>() {
    	    @Override
    	    protected void updateItem(Double price, boolean empty) {
    	        super.updateItem(price, empty);
    	        if (empty || price==null) {
    	            setText(null);
    	        } else {
    	            setText(Constants.DOUBLEFORMAT.format(price) + Constants.CURRENCY);
    	        }
    	    }
    	});
        
        auctionsCatalogStartColumn.setCellValueFactory(new PropertyValueFactory<Auction, LocalDateTime>("starttime"));
        //Anzeigewert f�r Startdatum anpassen
        auctionsCatalogStartColumn.setCellFactory(tc -> new TableCell<Auction, LocalDateTime>() {
    	    @Override
    	    protected void updateItem(LocalDateTime date, boolean empty) {
    	        super.updateItem(date, empty);
    	        if (empty) {
    	            setText("Kein Datum");
    	        } if(date==null) {
    	        	setText(null);
    	        }else {
    	            setText(date.format(SEPCommon.Constants.DATEFORMAT));
    	        }
    	    }
    	});
        
        auctionsCatalogEndColumn.setCellValueFactory(new PropertyValueFactory<Auction, LocalDateTime>("enddate"));
        //Anzeigewert f�r Enddatum anpassen
        auctionsCatalogEndColumn.setCellFactory(tc -> new TableCell<Auction, LocalDateTime>() {
    	    @Override
    	    protected void updateItem(LocalDateTime date, boolean empty) {
    	        super.updateItem(date, empty);
    	        if (empty) {
    	            setText("Kein Datum");
    	        } if(date==null) {
    	        	setText(null);
    	        }else {
    	            setText(date.format(SEPCommon.Constants.DATEFORMAT));
    	        }
    	    }
    	});
		
        //ToggleGroups
	    ToggleGroup radioViewAuctionsGroup = new ToggleGroup();
	    radioAllAuctions.setToggleGroup(radioViewAuctionsGroup);
	    radioMyBids.setToggleGroup(radioViewAuctionsGroup);
	    radioMyAuctions.setToggleGroup(radioViewAuctionsGroup);
	    radioSavedAuctions.setToggleGroup(radioViewAuctionsGroup);

	    ToggleGroup radioAuctionTypeGroup = new ToggleGroup();
	    radioCurrentAuctions.setToggleGroup(radioAuctionTypeGroup);
	    radioEndedAuctions.setToggleGroup(radioAuctionTypeGroup);
	    radioFutureAuctions.setToggleGroup(radioAuctionTypeGroup);
    }
    
    private void refreshUserDetails()
    {
    	//Aktualisierte Nutzerdetails laden
    	MainScreen_LabelWallet.setText("Guthaben: " + Constants.DOUBLEFORMAT.format(SEPCommon.Methods.round(user.getWallet(), 2)) + Constants.CURRENCY);
    	
		//Standardbild setzen
    	Image defaultImage = new Image(getClass().getResource("/SEPClient/UI/no-image.jpg").toString());
    	MainScreen_ImgProfilePicture.setImage(defaultImage);
    	
    	if(user instanceof Seller)
    	{
    		//Gewerbekunde
        	MainScreen_LabelLoggedInAs.setText("Angemeldet als: " + user.getUsername() + " (ID " + user.getId() + ", Gewerbekunde)");
    		MainScreen_ButtonAddWallet.setDisable(true);
    		MainScreen_ButtonSellProduct.setDisable(false);
    		MainScreen_ButtonMyProducts.setDisable(true); //eig false, aber erst in 3. Iteration ben�tigt
    		MainScreen_ButtonPurchases.setDisable(true);
    		MainScreen_ButtonSales.setDisable(false);
    		MainScreen_ButtonCreateAuction.setDisable(true);
    	}
    	else
    	{
    		//Privatkunde
        	MainScreen_LabelLoggedInAs.setText("Angemeldet als: " + user.getUsername() + " (ID " + user.getId() + ", Privatkunde)");
    		MainScreen_ButtonAddWallet.setDisable(false);
    		MainScreen_ButtonSellProduct.setDisable(true);
    		MainScreen_ButtonMyProducts.setDisable(true);
    		MainScreen_ButtonPurchases.setDisable(false);
    		MainScreen_ButtonSales.setDisable(false);
    		MainScreen_ButtonCreateAuction.setDisable(false);
    	}
    	
    	//Bild setzen
    	InputStream in = new ByteArrayInputStream(user.getPicture());
		Image img = new Image(in);
		if(!img.isError())
		{
			MainScreen_ImgProfilePicture.setImage(img);
		}
    }
    
    private void refreshViewArticles()
    {
    	refreshUserDetails();
		
		//Aktuelle Produktdetails leeren
		clearProductDetails();
		AnchorPaneArticleDetails.setVisible(true);
		AnchorPaneAuctionDetails.setVisible(false);
		
		//Selektierte Artikel ggf. deselektieren
    	if(MainScreen_ListCatalog.getSelectionModel().getSelectedItem() != null)
    	{
	    	MainScreen_ListCatalog.getSelectionModel().clearSelection();
    	}
		
		if(MainScreen_ListLastViewed.getSelectionModel().getSelectedItem() != null)
    	{
	    	MainScreen_ListLastViewed.getSelectionModel().clearSelection();
    	}

		//Suche leeren
    	MainScreen_txtSearch.setText("");
    	currentSearchEvent=false;
    	lastSearchResult=null;
		
    	//Produkte laden
    	LoadAllProducts();
    	loadLastViewedProducts();
    	
    	//Alle Kategorien ausw�hlen
    	MainScreen_ChoiceBox_Category.getSelectionModel().select(0);
    }
    
    private void clearProductDetails()
    {
    	//Aktuelle Produktinfos leeren
    	MainScreen_LabelProductTitle.setText("");
    	MainScreen_LabelProductPrice.setText("");
    	MainScreen_LabelProductSeller.setText("");
    	MainScreen_LabelProductCategory.setText("");
    	MainScreen_txtAverageRating.setText("");
    	MainScreen_txtRatingCount.setText("");
    	MainScreen_WebViewProductDescription.getEngine().loadContent("");
    	MainScreen_ButtonBuyProduct.setVisible(false);
    	MainScreen_ButtonShowRatings.setVisible(false);
    	MainScreen_WebViewProductDescription.setVisible(false);
    }
    
    private void refreshViewAuctions(AuctionType auctionType)
    {
    	//AuctionType auctionType = aktuell ausgewaehltes Fenster
    	
    	refreshUserDetails();
		
		//Aktuelle Auktionsdetails leeren
		clearAuctionDetails();
		AnchorPaneAuctionDetails.setVisible(true);
		AnchorPaneArticleDetails.setVisible(false);
    	
		//Selektierte Auktion ggf. deselektieren
    	if(MainScreen_ListAuctions.getSelectionModel().getSelectedItem() != null)
    	{
    		MainScreen_ListAuctions.getSelectionModel().clearSelection();
    	}
    	
    	//Ansicht anpassen, Auktionen laden
    	if(auctionType==AuctionType.Active)
    	{
    		radioAllAuctions.setSelected(true);
        	radioCurrentAuctions.setSelected(true);
        	
        	radioCurrentAuctions.setVisible(true);
        	radioEndedAuctions.setVisible(true);
        	radioFutureAuctions.setVisible(true);
        	
        	MainScreen_txtSearchAuctions.setText("");
        	MainScreen_txtSearchAuctions.setVisible(true);
        	MainScreen_btnAuctionsSearchOK.setVisible(true);
        	
    		//Falls ein Suchbegriff eingegeben wurde, wird die Suche ausgef�hrt, ansonsten alle aktuellen Auktionen geladen
        	auctionsSearchChangedEvent(AuctionType.Active);
    	}
    	else if(auctionType==AuctionType.Ended)
    	{
    		radioAllAuctions.setSelected(true);
        	radioEndedAuctions.setSelected(true);
        	
        	radioCurrentAuctions.setVisible(true);
        	radioEndedAuctions.setVisible(true);
        	radioFutureAuctions.setVisible(true);
        	
        	MainScreen_txtSearchAuctions.setText("");
        	MainScreen_txtSearchAuctions.setVisible(true);
        	MainScreen_btnAuctionsSearchOK.setVisible(true);
    		
    		//Falls ein Suchbegriff eingegeben wurde, wird die Suche ausgef�hrt, ansonsten alle geendeten Auktionen geladen
        	auctionsSearchChangedEvent(AuctionType.Ended);
    	}
    	else if(auctionType==AuctionType.Future)
    	{
    		radioAllAuctions.setSelected(true);
        	radioFutureAuctions.setSelected(true);
        	
        	radioCurrentAuctions.setVisible(true);
        	radioEndedAuctions.setVisible(true);
        	radioFutureAuctions.setVisible(true);
        	
        	MainScreen_txtSearchAuctions.setText("");
        	MainScreen_txtSearchAuctions.setVisible(true);
        	MainScreen_btnAuctionsSearchOK.setVisible(true);
    		
    		//Falls ein Suchbegriff eingegeben wurde, wird die Suche ausgef�hrt, ansonsten alle zuk�nftigen Auktionen geladen
        	auctionsSearchChangedEvent(AuctionType.Future);
    	}
    	else if(auctionType==AuctionType.SavedAuctions)
    	{
    		radioSavedAuctions.setSelected(true);
    		
    		radioCurrentAuctions.setSelected(false);
        	radioEndedAuctions.setSelected(false);
        	radioFutureAuctions.setSelected(false);
        	
        	radioCurrentAuctions.setVisible(false);
        	radioEndedAuctions.setVisible(false);
        	radioFutureAuctions.setVisible(false);

        	MainScreen_txtSearchAuctions.setText("");
        	MainScreen_txtSearchAuctions.setVisible(false);
        	MainScreen_btnAuctionsSearchOK.setVisible(false);
        	
    		//Alle vom User gespeicherten Auktionen werden geladen
        	MainScreen_ListAuctions.setItems(LoadAuctions(AuctionType.SavedAuctions));
    	}
    	else if(auctionType==AuctionType.MyBids)
    	{
    		radioMyBids.setSelected(true);
    		
    		radioCurrentAuctions.setSelected(false);
        	radioEndedAuctions.setSelected(false);
        	radioFutureAuctions.setSelected(false);
        	
        	radioCurrentAuctions.setVisible(false);
        	radioEndedAuctions.setVisible(false);
        	radioFutureAuctions.setVisible(false);

        	MainScreen_txtSearchAuctions.setText("");
        	MainScreen_txtSearchAuctions.setVisible(false);
        	MainScreen_btnAuctionsSearchOK.setVisible(false);

    		//Alle Auktionen mit Geboten vom User werden geladen
        	MainScreen_ListAuctions.setItems(LoadAuctions(AuctionType.MyBids));
    	}
    	else if(auctionType==AuctionType.MyAuctions)
    	{
    		radioMyAuctions.setSelected(true);
    		
    		radioCurrentAuctions.setSelected(false);
        	radioEndedAuctions.setSelected(false);
        	radioFutureAuctions.setSelected(false);
        	
        	radioCurrentAuctions.setVisible(false);
        	radioEndedAuctions.setVisible(false);
        	radioFutureAuctions.setVisible(false);

        	MainScreen_txtSearchAuctions.setText("");
        	MainScreen_txtSearchAuctions.setVisible(false);
        	MainScreen_btnAuctionsSearchOK.setVisible(false);

    		//Alle vom User eingestellten Auktionen werden geladen
        	MainScreen_ListAuctions.setItems(LoadAuctions(AuctionType.MyAuctions));
    	}
    	
    }
    
    private void clearAuctionDetails()
    {
    	//Aktuelle Auktionsinfos leeren
    	MainScreen_LabelProductTitleAuction.setText("");
    	MainScreen_LabelMinBidAuction.setText("");
    	MainScreen_LabelStartPriceAuction.setText("");
    	MainScreen_LabelAuctionSeller.setText("");
    	MainScreen_LabelCurrentBidAuction.setText("");
    	MainScreen_LabelShippingAuction.setText("");
    	MainScreen_LabelTimeAuction.setText("");
    	MainScreen_txtAverageRatingAuction.setText("");
    	MainScreen_txtRatingCountAuction.setText("");
    	MainScreen_WebViewAuctionDescription.getEngine().loadContent("");
    	MainScreen_ImgAuction.setVisible(false);
    	MainScreen_txtDollarBidAmount.setVisible(false);
    	MainScreen_TextboxBidAmount.setVisible(false);
    	MainScreen_ButtonBidAuction.setVisible(false);
    	MainScreen_ButtonSaveAuction.setVisible(false);
    	MainScreen_ButtonShowRatingsAuction.setVisible(false);
    	MainScreen_WebViewAuctionDescription.setVisible(false);
    }
    
    private void LoadAllProducts()
    {
    	if(MainScreen_ListCatalog.getItems()!=null)
    	{
        	MainScreen_ListCatalog.getItems().clear();
    	}
    	
    	if(MainScreen_ChoiceBox_Category.getItems()!=null)
    	{
    		MainScreen_ChoiceBox_Category.getItems().clear();
        	//Alle Kategorien Item hinzuf�gen
        	MainScreen_ChoiceBox_Category.getItems().add("Alle Kategorien");
        	MainScreen_ChoiceBox_Category.getSelectionModel().select("Alle Kategorien");
    	}
    	
    	ClientRequest req = new ClientRequest(Request.FetchProducts, null);
    	Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);
		
		if(queryResponse!=null && queryResponse.getResponseMap() != null && queryResponse.getResponseMap().get("Products")!=null)
		{
			//Product Array
			Product[] products = (Product[])queryResponse.getResponseMap().get("Products");
			ObservableList<Product> ObservableProducts = FXCollections.observableArrayList(products);
			ObservableProducts.removeIf(n -> (n==null));
			
			//Kategorien in Liste einf�gen
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
    	if(MainScreen_ListLastViewed.getItems()!=null)
    	{
        	MainScreen_ListLastViewed.getItems().clear();
    	}
        
        HashMap<String, Object> requestMap = new HashMap<String, Object>();
    	requestMap.put("User", user);
        ClientRequest req = new ClientRequest(Request.LastViewedProducts, requestMap);
    	Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);
		
		if(queryResponse.getResponseType() == Response.Success) {
			//Product Array
			Product[] products = (Product[])queryResponse.getResponseMap().get("Products");
			ObservableList<Product> ObservableProducts = FXCollections.observableArrayList(products);
			ObservableProducts.removeIf(n -> (n==null));
			
			MainScreen_ListLastViewed.setItems(ObservableProducts);
		}
		//nicht weiter auf Fehler pr�fen, da es ja nicht notwendig ist, dass zuletzt angesehene Produkte dargestellt werden
    }
    
    private ObservableList<Auction> LoadAuctions(AuctionType auctionType)
    {
    	if(MainScreen_ListAuctions.getItems()!=null)
    	{
    		if(avoidClearAuctions)
    		{
    			avoidClearAuctions=false;
    		}
    		else {
            	MainScreen_ListAuctions.getItems().clear();
			}
    	}
    	
        HashMap<String, Object> requestMap = new HashMap<String, Object>();
    	requestMap.put("AuctionType", auctionType);
    	
    	if(auctionType==AuctionType.MyBids||auctionType==AuctionType.MyAuctions||auctionType==AuctionType.SavedAuctions)
    	{
    		//f�r die AuktionsTypen wird ein User-Objekt ben�tigt
    		requestMap.put("User", user);
    	}
    	
    	ClientRequest req = new ClientRequest(Request.FetchAuctions, requestMap);
    	Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);
		
		if(queryResponse!=null && queryResponse.getResponseMap() != null && queryResponse.getResponseMap().get("Auctions")!=null)
		{
			Auction[] auctions = (Auction[])queryResponse.getResponseMap().get("Auctions");
			ObservableList<Auction> ObservableAuctions = FXCollections.observableArrayList(auctions);
			ObservableAuctions.removeIf(n -> (n==null));
			
			return ObservableAuctions;
		}
		return null;
    }
    
    private void categoryChangedListener() {
    	//Listener mit Hilfe folgender Quelle geschrieben: https://stackoverflow.com/questions/14522680/javafx-choicebox-events
        //Antwort von zhujik, Jan 25 '13 at 14:08
    	
    	//ChoiceBox Categories Selection Change Listener
    	//wird aufgerufen, wenn eine Kategorie ausgew�hlt wird
    	
	    MainScreen_ChoiceBox_Category.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				categoryChangedEvent(newValue.intValue());
			}
	      });
    }
    
    private void selectionsChangedListener() {
    	//Listener mit Hilfe folgender Quelle geschrieben: https://stackoverflow.com/questions/26424769/javafx8-how-to-create-listener-for-selection-of-row-in-tableview
    	//Antwort von James_D, Oct 17 '14 at 14:11
    	
    	//ListCatalog Selection Change Listener
    	
    	MainScreen_ListCatalog.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
    		//was passiert, wenn ein Eintrag in der ListCatalog ausgew�hlt wird
    		if(newSelection != null)
    		{
    		updateArticleInfo(true);
    		addToLastViewedItems();
    		}
    	});
    	   	
    	//ListLastViewed Selection Change Listener
    	
	    MainScreen_ListLastViewed.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
	    	//was passiert, wenn ein Eintrag in der ListLastViewed ausgew�hlt wird
	    	if(newSelection != null)
	    	{
		    	updateArticleInfo(false);
	    	}
	    });
	    
    	//MainScreen_ListAuctions Selection Change Listener
	    
	    MainScreen_ListAuctions.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
	    	//Eintrag in ListAuctions ausgewaehlt
	    	if(newSelection != null)
	    	{
		    	updateAuctionInfo();
	    	}
	    });
	}
    
    private void tabChangedListener() {
    	//Quelle: https://stackoverflow.com/questions/17522686/javafx-tabpane-how-to-listen-to-selection-changes
    	//Antwort von Mohammad Jafar Mashhadi, Jul 8 '13 at 9:17
    	
    	//Listener prueft ob der gewaehlte Tab geaendert wird
    	tabPane.getSelectionModel().selectedItemProperty().addListener(
    		    new ChangeListener<Tab>() {
    		        @Override
    		        public void changed(ObservableValue<? extends Tab> ov, Tab oldTab, Tab newTab) {
    		        	System.out.println(newTab.getText());
    		        	if(newTab==tabLiveAuctions)
    		        	{
    		        		tabLiveAuctions_Select();
    		        	}
    		        	else if(newTab==tabArticles)
    		        	{
    		        		tabArticles_Select();
    		        	}
    		        }
    		    }
    		);
    }
    
    private void updateArticleInfo(boolean selectionInCatalog)
    {
    	//selectionInCatalog = true --> Selektion im Katalog ge�ndert
    	//selectionInCatalog = false --> Selektion in LastViewed ge�ndert
    	if(selectionInCatalog==true)
    	{
    		//Artikel in der ListCatalog ausgew�hlt
    		if(MainScreen_ListCatalog.getSelectionModel().getSelectedItem() != null)
	    	{
		    	MainScreen_ListLastViewed.getSelectionModel().clearSelection();
	    		//Item in der Katalog-Liste angew�hlt
		    	
		    	//Daten einf�gen
		    	MainScreen_LabelProductTitle.setText(MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getName());
		    	MainScreen_LabelProductPrice.setText("Preis: " + MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getPriceString());
		    	MainScreen_LabelProductSeller.setText("Verk�ufer: " + MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getSeller().getBusinessname() + " (Benutzer " + MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getSeller().getUsername() + ")");
		    	MainScreen_txtAverageRating.setText("Durchschnittliche Bewertung: X.XX");
		    	MainScreen_txtRatingCount.setText("(Anzahl: XX)");
		    	String selectedCategory = MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getCategory();
		    	if(selectedCategory=="")
		    	{
			    	MainScreen_LabelProductCategory.setText("Kategorie: (Keine Kategorie)");
		    	}
		    	else
		    	{
			    	MainScreen_LabelProductCategory.setText("Kategorie: " + selectedCategory);
		    	}
		    	MainScreen_WebViewProductDescription.getEngine().loadContent(MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getDescription().replace(System.lineSeparator(), "<br/>")); //<br/> = neue Zeile in HTML
		    	
		    	
		    	HashMap<String, Object> requestMap = new HashMap<String, Object>();
				requestMap.put("User", MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getSeller());
				requestMap.put("FetchAvg", true);
		    	
		    	ClientRequest req = new ClientRequest(Request.FetchRatings, requestMap);
		    	Client client = Client.getClient();
				ServerResponse queryResponse = client.sendClientRequest(req);
				
				if(queryResponse.getResponseType()!=null && queryResponse.getResponseType()==Response.Success)	{
					Double ratingAvg = (Double)queryResponse.getResponseMap().get("Average");
					int ratingCount = (Integer)queryResponse.getResponseMap().get("Amount");
					if(ratingAvg!=null)
					{
						MainScreen_txtAverageRating.setText("Durchschnittliche Bewertung: " + SEPCommon.Constants.DOUBLEFORMAT.format(ratingAvg));
				    	MainScreen_txtRatingCount.setText("(Anzahl: " + ratingCount + ")");
				    	MainScreen_ButtonShowRatings.setDisable(false);
				    	MainScreen_txtRatingCount.setVisible(true);
					}
				}
				else {
					MainScreen_txtAverageRating.setText("Bisher keine Bewertung erhalten");
			    	MainScreen_ButtonShowRatings.setDisable(true);
			    	MainScreen_txtRatingCount.setVisible(false);
				}
		    	
		    	MainScreen_ButtonShowRatings.setVisible(true);
		    	MainScreen_ButtonBuyProduct.setVisible(true);
		    	MainScreen_WebViewProductDescription.setVisible(true);
		    	
		    	//Kaufen Button nur f�r Customer enablen
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
    		//Artikel in ListLastViewed ausgew�hlt
    		if(MainScreen_ListLastViewed.getSelectionModel().getSelectedItem() != null)
	    	{
		    	MainScreen_ListCatalog.getSelectionModel().clearSelection();
	    		//Item in der zuletzt angesehen Liste angew�hlt
		    	
		    	//Daten einf�gen
		    	MainScreen_LabelProductTitle.setText(MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getName());
		    	MainScreen_LabelProductPrice.setText("Preis: " + MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getPriceString());
		    	MainScreen_LabelProductSeller.setText("Verk�ufer: " + MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getSeller().getBusinessname() + " (Benutzer " + MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getSeller().getUsername() + ")");
		    	MainScreen_txtAverageRating.setText("Durchschnittliche Bewertung: X.XX");
		    	MainScreen_txtRatingCount.setText("(Anzahl: XX)");
		    	String selectedCategory = MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getCategory();
		    	if(selectedCategory=="")
		    	{
			    	MainScreen_LabelProductCategory.setText("Kategorie: (Keine Kategorie)");
		    	}
		    	else
		    	{
			    	MainScreen_LabelProductCategory.setText("Kategorie: " + selectedCategory);
		    	}
		    	MainScreen_WebViewProductDescription.getEngine().loadContent(MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getDescription().replace(System.lineSeparator(), "<br/>")); //<br/> = neue Zeile in HTML
		    	
		    	HashMap<String, Object> requestMap = new HashMap<String, Object>();
				requestMap.put("User", MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getSeller());
				requestMap.put("FetchAvg", true);
		    	
		    	ClientRequest req = new ClientRequest(Request.FetchRatings, requestMap);
		    	Client client = Client.getClient();
				ServerResponse queryResponse = client.sendClientRequest(req);
				
				if(queryResponse.getResponseType()!=null && queryResponse.getResponseType()==Response.Success)	{
					Double ratingAvg = (Double)queryResponse.getResponseMap().get("Average");
					int ratingCount = (Integer)queryResponse.getResponseMap().get("Amount");
					if(ratingAvg!=null)
					{
						MainScreen_txtAverageRating.setText("Durchschnittliche Bewertung: " + SEPCommon.Constants.DOUBLEFORMAT.format(ratingAvg));
				    	MainScreen_txtRatingCount.setText("(Anzahl: " + ratingCount + ")");
				    	MainScreen_ButtonShowRatings.setDisable(false);
				    	MainScreen_txtRatingCount.setVisible(true);
					}
				}
				else {
					MainScreen_txtAverageRating.setText("Bisher keine Bewertung erhalten");
			    	MainScreen_ButtonShowRatings.setDisable(true);
			    	MainScreen_txtRatingCount.setVisible(false);
				}
		    	
		    	MainScreen_ButtonShowRatings.setVisible(true);
		    	MainScreen_ButtonBuyProduct.setVisible(true);
		    	MainScreen_WebViewProductDescription.setVisible(true);
		    	
		    	//Kaufen Button nur f�r Customer enablen
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
    
    private void updateAuctionInfo()
    {
    	//Auktion angeklickt
    	if(MainScreen_ListAuctions.getSelectionModel().getSelectedItem() != null)
    	{
    		//Daten einf�gen
    		
    		MainScreen_LabelProductTitleAuction.setText(MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getTitle());
        	MainScreen_LabelMinBidAuction.setText("Mindestgebot: " + SEPCommon.Constants.DOUBLEFORMAT.format(MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getMinBid()) + "$");
        	MainScreen_LabelStartPriceAuction.setText("Startpreis: " + SEPCommon.Constants.DOUBLEFORMAT.format(MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getStartPrice()) + "$");
        	MainScreen_LabelAuctionSeller.setText("Verk�ufer: " + MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getSeller().getAddress().getFullname() + " (Benutzer " + MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getSeller().getUsername() + ")");
        	MainScreen_LabelCurrentBidAuction.setText("Aktuelles Gebot: " + SEPCommon.Constants.DOUBLEFORMAT.format(MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getCurrentBid()) + "$");
        	MainScreen_LabelShippingAuction.setText("Versandart: " + MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getShippingType().toString());
        	MainScreen_LabelTimeAuction.setText("Auktionszeitraum: " + MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getStarttime().format(SEPCommon.Constants.DATEFORMAT) + " - " + MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getEnddate().format(SEPCommon.Constants.DATEFORMAT));
        	MainScreen_txtAverageRatingAuction.setText("");
        	MainScreen_txtRatingCountAuction.setText("");
        	MainScreen_WebViewAuctionDescription.getEngine().loadContent(MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getDescription().replace(System.lineSeparator(), "<br/>")); //<br/> = neue Zeile in HTML
        	
        	//Standardbild setzen
        	Image defaultImage = new Image(getClass().getResource("/SEPClient/UI/no-image.jpg").toString());
	    	MainScreen_ImgAuction.setImage(defaultImage);

	    	InputStream in = new ByteArrayInputStream(MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getImage());
			Image img = new Image(in);
			if(!img.isError())
			{
				MainScreen_ImgAuction.setImage(img);
			}
        	MainScreen_ImgAuction.setVisible(true);
        	
        	MainScreen_ButtonSaveAuction.setVisible(true);
        	
        	HashMap<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("User", MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getSeller());
			requestMap.put("FetchAvg", true);
	    	
	    	ClientRequest req = new ClientRequest(Request.FetchRatings, requestMap);
	    	Client client = Client.getClient();
			ServerResponse queryResponse = client.sendClientRequest(req);
			
			if(queryResponse.getResponseType()!=null && queryResponse.getResponseType()==Response.Success)	{
				Double ratingAvg = (Double)queryResponse.getResponseMap().get("Average");
				int ratingCount = (Integer)queryResponse.getResponseMap().get("Amount");
				if(ratingAvg!=null)
				{
					MainScreen_txtAverageRatingAuction.setText("Durchschnittliche Bewertung: " + SEPCommon.Constants.DOUBLEFORMAT.format(ratingAvg));
			    	MainScreen_txtRatingCountAuction.setText("(Anzahl: " + ratingCount + ")");
			    	MainScreen_ButtonShowRatingsAuction.setDisable(false);
			    	MainScreen_txtRatingCountAuction.setVisible(true);
				}
			}
			else {
				MainScreen_txtAverageRatingAuction.setText("Bisher keine Bewertung erhalten");
		    	MainScreen_ButtonShowRatingsAuction.setDisable(true);
		    	MainScreen_txtRatingCountAuction.setVisible(false);
			}
        	
        	MainScreen_TextboxBidAmount.setVisible(true);
        	MainScreen_txtDollarBidAmount.setVisible(true);
        	MainScreen_ButtonBidAuction.setVisible(true);
        	
        	MainScreen_TextboxBidAmount.setDisable(true);
        	MainScreen_ButtonBidAuction.setDisable(true);
        	MainScreen_ButtonSaveAuction.setDisable(false);
        	
        	if(user.getId() == MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getSeller().getId())
        	{
        		//Verkaeufer = aktueller Nutzer, nicht bieten lassen und Auktion merken deaktivieren
            	MainScreen_TextboxBidAmount.setDisable(true);
            	MainScreen_ButtonBidAuction.setDisable(true);
            	MainScreen_ButtonSaveAuction.setDisable(true);
        	}
        	else
        	{
        		if(radioCurrentAuctions.isSelected() || radioMyBids.isSelected() || radioSavedAuctions.isSelected())
        		{
        			//auf beendete, zukuenftige, eigene Auktionen kann nicht geboten werden.
        			MainScreen_TextboxBidAmount.setDisable(false);
                	MainScreen_ButtonBidAuction.setDisable(false);
            		MainScreen_ButtonSaveAuction.setDisable(false);
        		}
        		
            	if(radioSavedAuctions.isSelected())
            	{
            		//Wenn aktuell in der Liste gespeicherter Auktionen, Auktionen merken deaktivieren, da die aktuell
            		//selektierte Auktion bereits gemerkt ist
                	MainScreen_ButtonSaveAuction.setDisable(true);
            	}
            	
            	if(user instanceof Seller)
            	{
    				//Aktueller Nutzer ist Gewerbekunde, deaktiviere das Bieten
                	MainScreen_TextboxBidAmount.setDisable(true);
                	MainScreen_ButtonBidAuction.setDisable(true);
    			}
			}
        	
        	MainScreen_ButtonShowRatingsAuction.setVisible(true);
        	MainScreen_WebViewAuctionDescription.setVisible(true);
	    	
	    	//Kaufen Button nur f�r Customer enablen
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
    
    private void addToLastViewedItems() {
    	//Zu zuletzt angesehenen Produkten hinzuf�gen, wenn nicht bereits vhd.
    	Product viewedProduct = MainScreen_ListCatalog.getSelectionModel().getSelectedItem();
    	
    	boolean alreadyInLastViewed = false;
    	
    	//Vor ClientRequest an den Server pruefen ob das Produkt bereits in den zuletzt angesehenen Produkten ist
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
    
    private void saveAuction()
    {
    	//Zu gespeicherten Auktionen hinzuf�gen
    	Auction viewedAuction = MainScreen_ListAuctions.getSelectionModel().getSelectedItem();
    	
    	boolean alreadyInSavedAuctions = false;
    	
    	//Vor ClientRequest an den Server pruefen ob die Auktion bereits bei dem User gespeichert ist
    	avoidClearAuctions=true;
    	ObservableList<Auction> savedAuctions = LoadAuctions(AuctionType.SavedAuctions); 
    	
    	if(savedAuctions != null)
    	{
			for(Auction a: savedAuctions)
	    	{
	    		if(a != null && a.getId() == viewedAuction.getId())
	    		{
	    			alreadyInSavedAuctions = true;
	    		}
	    	}
    	}
    	if(!alreadyInSavedAuctions)
    	{
    		//Auktion noch nicht bei User gespeichert, hinzuf�gen
    		HashMap<String, Object> requestMap = new HashMap<String, Object>();
	    	requestMap.put("Auction", viewedAuction);
	    	requestMap.put("User", user);
	    	ClientRequest req = new ClientRequest(Request.SaveAuction, requestMap);
	    	Client client = Client.getClient();
			ServerResponse queryResponse = client.sendClientRequest(req);
			if(queryResponse.getResponseType() == Response.Success)
			{
				//MainScreen_ButtonRefresh.fire();
				return;
			}
    	}
    	else {
			FXMLHandler.ShowMessageBox("Die ausgew�hlte Auktion ist bereits in Ihrer Merkliste gespeichert.", "Bereits gespeichert", "Bereits gespeichert", AlertType.INFORMATION, true, false);
			return;
		}
    }
    
    private void categoryChangedEvent(int newValue) {
    	//Katalog leeren

    	if(MainScreen_ListCatalog.getItems()!=null)
    	{
    		MainScreen_ListCatalog.getItems().clear();
    	}
		
		//keine Kategorie, also alle Kategorien
    	if(newValue>-1)
    	{
        	String selectedCategoryString = (MainScreen_ChoiceBox_Category.getItems().get((Integer) newValue)); //Name der selektierten Kategorie
    		
        	if(MainScreen_ListCatalog.getItems()!=null)
        	{
            	MainScreen_ListCatalog.getItems().clear(); //Katalog Liste leeren
        	}
    		
    		//keine Kategorie
    		if(newValue==0) {
    			//Alle Kategorien ausgew�hlt und kein Suchbegriff ist eingegeben
    			if(avoidCategoryChangedEvent)
    			{
    				avoidCategoryChangedEvent=false;
    				return;
    			}
    			
    			if(currentSearchEvent) {
    				currentSearchEvent=false;
    				avoidCategoryChangedEvent=true;
    				LoadAllProducts();
    				return;
    			}
    			//Alle Kategorien ausgew�hlt und Suchbegriff ist eingegeben
    			else {
    				searchChangedEvent();
    			}
    			
    		} else {
    			//Sonstige Kategorie ausgew�hlt
    			HashMap<String, Object> requestMap = new HashMap<String, Object>();
    	    	requestMap.put("Category", selectedCategoryString);
    	    	
    	    	ClientRequest req = new ClientRequest(Request.FetchProducts, requestMap);
    	    	Client client = Client.getClient();
    			ServerResponse queryResponse = client.sendClientRequest(req);
    			if(queryResponse.getResponseType() != null)	{
    				Product[] articleInCategory = (Product[])queryResponse.getResponseMap().get("Products"); //Produkte in Kategorie
    				Product[] articlesInCategoryAndSearch = null;
    				
    				if(lastSearchResult == null) {
    					//Letzte Suche war leer bzw. noch keine Suche get�tigt
    					articlesInCategoryAndSearch=articleInCategory;
    				}
    				else {
    					//aktuelle Suche
    					int i=0;
    					for(Product p : lastSearchResult) {
    						if(p.getCategory().equals(selectedCategoryString)) {
        						i++;
    						}
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
    				
    				if(articlesInCategoryAndSearch!=null)
    				{
    					ObservableList<Product> ObservableProducts = FXCollections.observableArrayList(articlesInCategoryAndSearch);
    					
    					MainScreen_ListCatalog.setItems(ObservableProducts);
    				}
    			}
    		}
    		currentSearchEvent=false;
    	}
    }
    
    private void searchChangedEvent()
    {
    	//Katalog leeren
    	
    	if(MainScreen_ListCatalog.getItems()!=null)
    	{
    		MainScreen_ListCatalog.getItems().clear();
    	}
		
		
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
				
				if(articlesInSearch != null)
				{
					if(MainScreen_ChoiceBox_Category.getSelectionModel().getSelectedIndex()==0)	{
						//Alle Kategorien ausgew�hlt
						articlesInSearchAndCategory = articlesInSearch;
					}
					else {

						//bestimmte Kategorie ausgew�hlt
						int i=0;
						for(Product p : articlesInSearch) {
							if(p.getCategory().equals(MainScreen_ChoiceBox_Category.getSelectionModel().getSelectedItem()))	{
								i++;
							}
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
				}

				
				//Artikel in Katalog anzeigen
				if(articlesInSearchAndCategory!=null)
				{
					ObservableList<Product> ObservableProducts = FXCollections.observableArrayList(articlesInSearchAndCategory);
					
					MainScreen_ListCatalog.setItems(ObservableProducts);
				}
			}
		}
		lastSearchResult=articlesInSearch;
    }
    
    private void auctionsSearchChangedEvent(AuctionType auctionType)
    {
    	//Auktionen leeren
    	if(MainScreen_ListAuctions.getItems()!=null)
    	{
    		MainScreen_ListAuctions.getItems().clear();
    	}
		
		if(MainScreen_txtSearchAuctions.getText()==null || MainScreen_txtSearchAuctions.getText().isEmpty() || MainScreen_txtSearchAuctions.getText().isBlank() || MainScreen_txtSearchAuctions.getText() == "")
		{
			//Wenn kein Text eingegeben, alle laden
	    	if(radioCurrentAuctions.isSelected())
	    	{
	    		MainScreen_ListAuctions.setItems(LoadAuctions(AuctionType.Active));
	    	}
	    	else if(radioEndedAuctions.isSelected())
	    	{
	    		MainScreen_ListAuctions.setItems(LoadAuctions(AuctionType.Ended));
	    	}
	    	else if(radioFutureAuctions.isSelected())
	    	{
	    		MainScreen_ListAuctions.setItems(LoadAuctions(AuctionType.Future));
	    	}
	    	return;
		}
		
		//Ansonsten Suche durchfuehren
		Auction[] auctionsInSearch = null;

		HashMap<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put("AuctionType", auctionType);
    	requestMap.put("SearchString", MainScreen_txtSearchAuctions.getText());
    	
    	ClientRequest req = new ClientRequest(Request.FetchAuctions, requestMap);
    	Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);
		
		if(queryResponse.getResponseType() != null)	{
			auctionsInSearch = (Auction[])queryResponse.getResponseMap().get("Auctions");
			if(auctionsInSearch!=null)
			{
				ObservableList<Auction> ObservableAuctions = FXCollections.observableArrayList(auctionsInSearch);
				
				MainScreen_ListAuctions.setItems(ObservableAuctions);
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
    private Button MainScreen_btnSearchOK;

    @FXML
    private Button MainScreen_ButtonPurchases;
    
    @FXML
    private Button MainScreen_ButtonSales;
    
    @FXML
    private Button MainScreen_ButtonMyRatings;
    
    @FXML
    private Button MainScreen_ButtonCreateAuction;
    
    @FXML
    private Button MainScreen_ButtonShowRatings;

    @FXML
    private Label MainScreen_LabelLoggedInAs;

    @FXML
    private TabPane tabPane;
    
    @FXML
    private Tab tabArticles;
    
    @FXML
    private AnchorPane AnchorPaneArticleDetails;
    
    @FXML
    private AnchorPane AnchorPaneAuctionDetails;
    
    @FXML
    private TextField MainScreen_txtSearch;
    
    @FXML
    private WebView MainScreen_WebViewProductDescription;
    
    @FXML
    private TableView<Product> MainScreen_ListLastViewed;

    @FXML
    private TableView<Product> MainScreen_ListCatalog;
    
    @FXML
    private TableColumn<Product, Integer> lastviewedIdColumn;

    @FXML
    private TableColumn<Product, String> lastviewedProductColumn;

    @FXML
    private TableColumn<Product, Double> lastviewedPriceColumn;

    @FXML
    private TableColumn<Product, String> lastviewedSellerColumn;

    @FXML
    private TableColumn<Product, String> lastviewedCategoryColumn;

    @FXML
    private TableColumn<Product, Integer> catalogIdColumn;

    @FXML
    private TableColumn<Product, String> catalogProductColumn;

    @FXML
    private TableColumn<Product, Double> catalogPriceColumn;

    @FXML
    private TableColumn<Product, String> catalogSellerColumn;

    @FXML
    private TableColumn<Product, String> catalogCategoryColumn;
    
    @FXML
    private Tab tabLiveAuctions;

    @FXML
    private TextField MainScreen_txtSearchAuctions;

    @FXML
    private Button MainScreen_btnAuctionsSearchOK;

    @FXML
    private TableView<Auction> MainScreen_ListAuctions;

    @FXML
    private TableColumn<Auction, Integer> auctionsCatalogIdColumn;

    @FXML
    private TableColumn<Auction, String> auctionsCatalogTitleColumn;

    @FXML
    private TableColumn<Auction, Double> auctionsCatalogCurrentBidColumn;

    @FXML
    private TableColumn<Auction, Double> auctionsCatalogMinBidColumn;

    @FXML
    private TableColumn<Auction, LocalDateTime> auctionsCatalogStartColumn;

    @FXML
    private TableColumn<Auction, LocalDateTime> auctionsCatalogEndColumn;

    @FXML
    private RadioButton radioAllAuctions;

    @FXML
    private RadioButton radioMyBids;

    @FXML
    private RadioButton radioMyAuctions;

    @FXML
    private RadioButton radioSavedAuctions;

    @FXML
    private RadioButton radioCurrentAuctions;

    @FXML
    private RadioButton radioEndedAuctions;

    @FXML
    private RadioButton radioFutureAuctions;

    @FXML
    private Label MainScreen_LabelProductTitle;

    @FXML
    private Label MainScreen_LabelProductSeller;

    @FXML
    private Label MainScreen_LabelProductPrice;

    @FXML
    private Label MainScreen_LabelProductCategory;
    
    @FXML
    private Label MainScreen_lblAverageRating;
    
    @FXML
    private Label MainScreen_txtRatingCount;
    
    @FXML
    private Label MainScreen_txtAverageRating;
    
    @FXML
    private Label MainScreen_txtDollarBidAmount;
    
    @FXML
    private Label MainScreen_LabelProductTitleAuction;

    @FXML
    private Label MainScreen_LabelStartPriceAuction;

    @FXML
    private Label MainScreen_LabelAuctionSeller;

    @FXML
    private Button MainScreen_ButtonBidAuction;

    @FXML
    private WebView MainScreen_WebViewAuctionDescription;

    @FXML
    private Label MainScreen_txtRatingCountAuction;

    @FXML
    private Label MainScreen_txtAverageRatingAuction;

    @FXML
    private Button MainScreen_ButtonShowRatingsAuction;

    @FXML
    private Label MainScreen_LabelMinBidAuction;

    @FXML
    private Label MainScreen_LabelCurrentBidAuction;

    @FXML
    private Label MainScreen_LabelShippingAuction;

    @FXML
    private Label MainScreen_LabelTimeAuction;

    @FXML
    private ImageView MainScreen_ImgAuction;

    @FXML
    private TextField MainScreen_TextboxBidAmount;

    @FXML
    private Button MainScreen_ButtonSaveAuction;
    
    @FXML
    private Button MainScreen_ButtonRefresh;
    
    @FXML
    private ImageView MainScreen_ImgProfilePicture;

    @FXML
    void MainScreen_CloseButtonMenuClick(ActionEvent event) {
    	System.exit(0);
    }
    
    @FXML
    void MainScreen_RefreshButtonMenuClick(ActionEvent event) {
    	MainScreen_ButtonRefresh.fire();
    }

    @FXML
    void MainScreen_InfoButtonMenuClick(ActionEvent event) {

    	FXMLHandler.ShowMessageBox("� 'Super-E-commerce-Platform' wurde entwickelt von Denis Artjuch, Yannis Bromby, Marcel Just und Hannah Kalker. Gruppe B, Modul Software Entwicklung & Programmierung, Universit�t Duisburg-Essen, 2020/21.",
    			"Super-E-commerce-Platform", "Super-E-commerce-Platform", AlertType.INFORMATION, true,
				false);
    }

    @FXML
    void MainScreen_btnAddWalletClick(ActionEvent event) {
    	WalletController.setUser(user);
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonAddWallet.getScene().getWindow(), "Wallet", "Guthaben aufladen", false, true);
    }

    @FXML
    void MainScreen_btnEditAccountClick(ActionEvent event) {
    	EditAccountController.setUser(user);
    	FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonEditAccount.getScene().getWindow(), "EditAccount", "Konto bearbeiten", false, true);
    }

    @FXML
    void MainScreen_btnLogoutClick(ActionEvent event) {
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonLogout.getScene().getWindow(), "Start", "Super-E-commerce-Platform", false, true);
    }
    
    @FXML
    void MainScreen_ButtonCreateAuctionClick(ActionEvent event) {
    	CreateAuctionController.setCustomer((Customer)user); //nur f�r Customer enabled
    	FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonCreateAuction.getScene().getWindow(), "CreateAuction", "Auktion erstellen", false, true);
    }
    
    @FXML
    void MainScreen_ButtonMyRatingsClick(ActionEvent event) {
    	ShowRatingsController.setUser(user);
    	ShowRatingsController.setViewOwnRatings(true);
    	FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonMyRatings.getScene().getWindow(), "ShowRatings", "Meine Bewertungen", false, true);
    }
    
    @FXML
    void MainScreen_btnMyProductsClick(ActionEvent event) {
    	//3. Iteration ToDo
    }

    @FXML
    void MainScreen_btnMyPurchasesClick(ActionEvent event) {
    	MyPurchasesController.setCustomer((Customer)user); //Button ist nur f�r Customer enabled
    	FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonPurchases.getScene().getWindow(), "MyPurchases", "Meine K�ufe", false, true);
    }
    
    @FXML
    void MainScreen_ButtonSalesClick(ActionEvent event) {
    	MySalesController.setUser((User)user); //Button ist nur f�r Customer enabled
    	FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonSales.getScene().getWindow(), "MySales", "Meine Verk�ufe", false, true);
    }
    
    @FXML
    void MainScreen_btnSellProductClick(ActionEvent event) {
        //OfferProduct oeffnen
    	OfferProductController.setUser(user);
    	
    	//ggf. Kategorien mit �bergeben
    	if(MainScreen_ChoiceBox_Category.getItems() != null)
    	{
    		OfferProductController.setCategoryList(MainScreen_ChoiceBox_Category.getItems());
    	}
    	FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonSellProduct.getScene().getWindow(), "OfferProduct", "Produkt(e) anbieten", false, true);
    }
    
    @FXML
    void MainScreen_ButtonShowRatingsClick(ActionEvent event)
    {
    	//Alle Bewertungen eines Verk�ufers anzeigen
    	if(MainScreen_ListCatalog.getSelectionModel().getSelectedItem() != null)
    	{
    		ShowRatingsController.setUser(MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getSeller());
    	}
    	else if(MainScreen_ListLastViewed.getSelectionModel().getSelectedItem() != null)
    	{
    		ShowRatingsController.setUser(MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getSeller());
    	}
		ShowRatingsController.setViewOwnRatings(false);
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonShowRatings.getScene().getWindow(), "ShowRatings", "Bewertungen des Verk�ufers", false, true);
    }
    
    @FXML
    void MainScreen_ButtonShowRatingsAuctionClick (ActionEvent event)
    {
    	//Alle Bewertungen eines Verk�ufers anzeigen
    	if(MainScreen_ListAuctions.getSelectionModel().getSelectedItem() != null)
    	{
    		ShowRatingsController.setUser(MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getSeller());
    	}
		ShowRatingsController.setViewOwnRatings(false);
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonShowRatingsAuction.getScene().getWindow(), "ShowRatings", "Bewertungen des Verk�ufers", false, true);
    }
    
    @FXML
    void MainScreen_SaveAuctionClick (ActionEvent event)
    {
    	saveAuction();
    }
    
    @FXML
    void MainScreen_ButtonRefresh_Click (ActionEvent event)
    {
    	//Ansicht aktualisieren - aufrufbar mit MainScreen_ButtonRefresh.fire();
    	refreshUserDetails();
    	if(tabPane.getSelectionModel().getSelectedItem()==tabArticles)
    	{
    		refreshViewArticles();
    	}
    	else if(tabPane.getSelectionModel().getSelectedItem()==tabLiveAuctions)
    	{
    		AuctionType auctionType = AuctionType.Active;
    		if(radioAllAuctions.isSelected() && radioCurrentAuctions.isSelected())
    			auctionType=AuctionType.Active;
    		else if(radioAllAuctions.isSelected() && radioEndedAuctions.isSelected())
    			auctionType=AuctionType.Ended;
    		else if(radioAllAuctions.isSelected() && radioFutureAuctions.isSelected())
    			auctionType=AuctionType.Future;
    		else if(radioMyAuctions.isSelected())
    			auctionType=AuctionType.MyAuctions;
    		else if(radioMyBids.isSelected())
    			auctionType=AuctionType.MyBids;
    		else if(radioSavedAuctions.isSelected())
    			auctionType=AuctionType.SavedAuctions;
    		
			refreshViewAuctions(auctionType);
    	}
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
			FXMLHandler.ShowMessageBox("Es ist kein Produkt ausgew�hlt.", "Fehler", "Fehler", AlertType.ERROR, true, false);
			return;
    	}
    	

    	//clienseitig Pr�fen, ob genug Guthaben vorhanden ist
    	if(user.getWallet()<productToBuy.getPrice())
    	{
			FXMLHandler.ShowMessageBox("Ihr Guthaben reicht nicht aus, um das ausgew�hlte Produkt zu kaufen.", "Fehler", "Fehler", AlertType.ERROR, true, false);
			return;
    	}

    	//Client BuyItem Request senden

    	//Es wird bei dieser Request automatisch das K�uferkonto um den Produktpreis verringert
    	//und das Verk�uferkonto um den Produktpreis erh�ht
    	//In der Request wird gepr�ft, ob genug Guthaben vorhanden ist.

    	HashMap<String, Object> requestMap = new HashMap<String, Object>();
    	requestMap.put("User", user);
    	requestMap.put("Product", productToBuy);
        ClientRequest req = new ClientRequest(Request.BuyItem, requestMap);
    	Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);
		
		
		//Antwort auslesen
		if(queryResponse.getResponseType() == Response.NoDBConnection)
		{
			FXMLHandler.ShowMessageBox("Es konnte keine Verbindung zur Datenbank hergestellt werden, es wurde daher kein Kauf durchgef�hrt.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
			return;
		}
		else if(queryResponse.getResponseType() == Response.InsufficientBalance)
		{
			FXMLHandler.ShowMessageBox("Ihr Guthaben reicht nicht aus, um das ausgew�hlte Produkt zu kaufen.",
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
			FXMLHandler.ShowMessageBox("Sie haben den Artikel '" + productToBuy.getName() + "' erfolgreich f�r " + productToBuy.getPriceString() + " gekauft.",
					"Kauf erfolgreich", "Kauf erfolgreich", AlertType.CONFIRMATION, true,
					false);
			//MainScreen oeffnen
			user.setWallet(user.getWallet() - productToBuy.getPrice());
			MainScreenController.setUser(user);
			refreshUserDetails();
			refreshViewArticles();
		}
    }
    
    @FXML
    void MainScreen_BidAuctionClick (ActionEvent event)
    {
    	//Zugriff nur fuer Privatkunden und ob der aktuelle User der Seller ist wird vorher beim Selektieren eprueft.
    	if(MainScreen_ListAuctions.getSelectionModel().getSelectedItem() != null)
    	{
        	Auction selectedAuction = MainScreen_ListAuctions.getSelectionModel().getSelectedItem();
        	String bidAmountString = MainScreen_TextboxBidAmount.getText().trim();
        	double bidAmount;
        	
        	//Pr�fen ob Gebotsbetrag eingegeben ist
        	if (bidAmountString=="" || bidAmountString==null) {
    			FXMLHandler.ShowMessageBox("Bitte geben Sie einen Gebotsbetrag ein.", "Fehler", "Fehler", AlertType.ERROR, true, false);			
    			return;
    		}
        	
        	//Pr�fen ob Preis double ist und in Variable speichern (vorher ggf. , durch . ersetzen)
        	try
        	{
        		bidAmount = Double.parseDouble(bidAmountString.replace(",", "."));
        	}
        	catch (NumberFormatException e)
    		{
    			FXMLHandler.ShowMessageBox("Bitte geben Sie Ihr Gebot im folgenden Format ein: ##,##" + System.lineSeparator() + "(Ohne W�hrungszeichen und mit . oder ,)", "Fehler", "Fehler", AlertType.ERROR, true, false);			
    			MainScreen_TextboxBidAmount.setText("");
    			return;
    		}
        	
        	//clienseitig pr�fen, ob genug Guthaben vorhanden ist
        	if(user.getWallet()<bidAmount)
        	{
    			FXMLHandler.ShowMessageBox("Ihr Guthaben reicht nicht aus, um das Gebot abzugeben. Bitte laden Sie Ihr Guthaben auf.", "Fehler", "Fehler", AlertType.ERROR, true, false);
    			MainScreen_TextboxBidAmount.setText("");
    			return;
        	}

        	//SendBid Request senden

        	HashMap<String, Object> requestMap = new HashMap<String, Object>();
        	requestMap.put("Auction", selectedAuction);
        	requestMap.put("Bidder", user);
        	requestMap.put("Bid", bidAmount);
            ClientRequest req = new ClientRequest(Request.SendBid, requestMap);
        	Client client = Client.getClient();
    		ServerResponse queryResponse = client.sendClientRequest(req);
    		
    		
    		//Antwort auslesen
    		if(queryResponse.getResponseType() == Response.NoDBConnection)
    		{
    			FXMLHandler.ShowMessageBox("Es konnte keine Verbindung zur Datenbank hergestellt werden, es wurde daher kein Gebot abgegeben.",
    					"Fehler", "Fehler", AlertType.ERROR, true,
    					false);
    			MainScreen_TextboxBidAmount.setText("");
    			return;
    		}
    		else if(queryResponse.getResponseType() == Response.InsufficientBalance)
    		{
    			FXMLHandler.ShowMessageBox("Ihr Guthaben reicht nicht aus, um das Gebot abzugeben. Bitte laden Sie Ihr Guthaben auf.", "Fehler", "Fehler", AlertType.ERROR, true, false);
    			MainScreen_TextboxBidAmount.setText("");
    			return;
    		}
    		else if(queryResponse.getResponseType() == Response.BidTooLow)
    		{
    			FXMLHandler.ShowMessageBox("Ihr eingegebenes Gebot ist niedriger/gleich wie das aktuelle H�chstgebot, oder geringer als das Mindestgebot.",
    					"Fehler", "Fehler", AlertType.ERROR, true,
    					false);
    			MainScreen_TextboxBidAmount.setText("");
    			return;
    		}
    		else if(queryResponse.getResponseType() == Response.AuctionNotStartedYet)
    		{
    			FXMLHandler.ShowMessageBox("Die Auktion, auf die Sie bieten wollen, ist noch nicht gestartet. Bitte bieten Sie zu einem sp�teren Zeitpunkt.",
    					"Fehler", "Fehler", AlertType.ERROR, true,
    					false);
    			MainScreen_TextboxBidAmount.setText("");
    			return;
    		}
    		else if(queryResponse.getResponseType() == Response.AuctionAlreadyEnded)
    		{
    			FXMLHandler.ShowMessageBox("Die Auktion, auf die Sie bieten wollen, wurde bereits beendet.",
    					"Fehler", "Fehler", AlertType.ERROR, true,
    					false);
    			MainScreen_TextboxBidAmount.setText("");
    			return;
    		}
    		else if(queryResponse.getResponseType() == Response.Failure)
    		{
    			FXMLHandler.ShowMessageBox("Beim Abgeben des Gebots ist ein unbekannter Fehler aufgetreten.",
    					"Fehler", "Fehler", AlertType.ERROR, true,
    					false);
    			MainScreen_TextboxBidAmount.setText("");
    			return;
    		}
    		else if(queryResponse.getResponseType() == Response.Success)
    		{
    			FXMLHandler.ShowMessageBox("Ihr Gebot wurde erfolgreich abgegeben. Sie sind aktuell der H�chstbieter. Zum Zeitpunkt des Auktionsendes muss Ihr Konto gen�gend Guthaben aufweisen, wenn Sie H�chstbieter sind, ansonsten wird die Auktion abgebrochen.",
    					"Gebot erfolgreich", "Gebot erfolgreich", AlertType.CONFIRMATION, true, false);
    			
    			
    			//Guthaben wird clientseitig nicht reduziert, da es erst nach einer Auktion abgezogen wird.
    			//user.setWallet(user.getWallet() - bidAmount);
    			//MainScreenController.setUser(user);
    			MainScreen_TextboxBidAmount.setText("");
    			refreshViewAuctions(AuctionType.Active);
    			updateAuctionInfo();
    		}
    	}
    }
    
    void tabArticles_Select() {
    	refreshViewArticles();
    }

    void tabLiveAuctions_Select() {
    	refreshViewAuctions(AuctionType.Active);
    }

    @FXML
    void MainScreen_btnSearchOKClick(ActionEvent event) {
    	searchChangedEvent();
    }
    
    @FXML
    void MainScreen_txtSearch_KeyPressed(KeyEvent event) {
    	//Taste wird gedr�ckt
    	//Bei Enter: Button Search Klick simulieren
    	if (event.getCode().equals(KeyCode.ENTER))
        {
    		MainScreen_btnSearchOK.fire();
        }
    }
    
    @FXML
    void MainScreen_btnAuctionsSearchOK_Click(ActionEvent event)
    {
    	//Suche kann bei aktiven, beendeten, zuk�nftigen Auktionen aufgerufen werden.
    	if(radioCurrentAuctions.isSelected())
    	{
    		auctionsSearchChangedEvent(AuctionType.Active);
    	}
    	else if(radioEndedAuctions.isSelected())
    	{
    		auctionsSearchChangedEvent(AuctionType.Ended);
    	}
    	else if(radioFutureAuctions.isSelected())
    	{
    		auctionsSearchChangedEvent(AuctionType.Future);
    	}
    }
    
    @FXML
    void MainScreen_txtSearchAuctions_KeyPressed(KeyEvent event) {
    	//Taste wird gedr�ckt
    	//Bei Enter: Button Auction Search Klick simulieren
    	if (event.getCode().equals(KeyCode.ENTER))
        {
    		MainScreen_btnAuctionsSearchOK.fire();
        }
    }
    
    @FXML
    void radioAllAuctions_Click(ActionEvent event) {
    	refreshViewAuctions(AuctionType.Active);
    }

    @FXML
    void radioCurrentAuctions_Click(ActionEvent event) {
    	refreshViewAuctions(AuctionType.Active);;
    }

    @FXML
    void radioEndedAuctions_Click(ActionEvent event) {
    	refreshViewAuctions(AuctionType.Ended);
    }

    @FXML
    void radioFutureAuctions_Click(ActionEvent event) {
    	refreshViewAuctions(AuctionType.Future);
    }

    @FXML
    void radioMyAuctions_Click(ActionEvent event) {
    	refreshViewAuctions(AuctionType.MyAuctions);
    }

    @FXML
    void radioMyBids_Click(ActionEvent event) {
    	refreshViewAuctions(AuctionType.MyBids);
    }

    @FXML
    void radioSavedAuctions_Click(ActionEvent event) {
    	refreshViewAuctions(AuctionType.SavedAuctions);
    }
}
