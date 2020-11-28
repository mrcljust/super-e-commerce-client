package SEPClient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

public class MainScreenController {

	private static User user = null;
	private Product[] lastSearchResult;
	private boolean currentSearchEvent = false;
	
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
    		MainScreen_ButtonMyProducts.setDisable(true); //eig false, aber in der 1. Iteration noch nicht benötigt
    		MainScreen_ButtonPurchases.setDisable(true);
    	}
    	else
    	{
    		//Privatkunde
        	MainScreen_LabelLoggedInAs.setText("Angemeldet als: " + user.getUsername() + " (ID " + user.getId() + ", Privatkunde)");
    		MainScreen_ButtonAddWallet.setDisable(false);
    		MainScreen_ButtonSellProduct.setDisable(true);
    		MainScreen_ButtonMyProducts.setDisable(true);
    		MainScreen_ButtonPurchases.setDisable(true); //eig false, aber in der 1. Iteration noch nicht benötigt
    	}
    	
    	InputStream in = new ByteArrayInputStream(user.getPicture());
		Image img = new Image(in);
		MainScreen_ImgProfilePicture.setImage(img);
    	
    	//Aktuelle Produktinfos leeren
    	MainScreen_LabelProductTitle.setText("");
    	MainScreen_LabelProductPrice.setText("");
    	MainScreen_LabelProductSeller.setText("");
    	MainScreen_LabelProductCategory.setText("");
    	MainScreen_WebVIewProductDescription.getEngine().loadContent("");
    	MainScreen_ButtonBuyProduct.setVisible(false);
    	MainScreen_WebVIewProductDescription.setVisible(false);
    	
    	//Alle Kategorien Item hinzufügen
    	MainScreen_ChoiceBox_Category.getItems().add("Alle Kategorien");
    	MainScreen_ChoiceBox_Category.getSelectionModel().select("Alle Kategorien");
    	
    	//Werte an die Spalten der Kataloglisten zuweisen
    	catalogIdColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("id"));
        catalogProductColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        catalogPriceColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
        //Anzeigewert für Preis anpassen
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
        catalogSellerColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("businessname"));
        //Anzeigewert für Kategorie anpassen
        catalogCategoryColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("category"));
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

        lastviewedIdColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("id"));
    	lastviewedProductColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        //Anzeigewert für Preis anpassen
    	lastviewedPriceColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
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
    	lastviewedSellerColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("businessname"));
    	lastviewedCategoryColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("category"));
        //Anzeigewert für Kategorie anpassen
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
    	
    	if(MainScreen_ListCatalog.getSelectionModel().getSelectedItem() != null)
    	{
	    	MainScreen_ListCatalog.getSelectionModel().clearSelection();
    	}
		
		if(MainScreen_ListLastViewed.getSelectionModel().getSelectedItem() != null)
    	{
	    	MainScreen_ListLastViewed.getSelectionModel().clearSelection();
    	}
    }
    
    private void LoadAllProducts()
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
			ObservableProducts.removeIf(n -> (n==null));
			
			//Kategorien in Liste einfügen
			for(Product p: products)
			{
				String pCategory = p.getCategory();
				if(!MainScreen_ChoiceBox_Category.getItems().contains(pCategory))
				{
					if(pCategory!="") //leeren Kategorie-String nicht hinzufï¿½gen
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
			ObservableProducts.removeIf(n -> (n==null));
			
			MainScreen_ListLastViewed.setItems(ObservableProducts);
		}
    }
    
	//ChoiceBox Categories Selection Change Listener
	//wird aufgerufen, wenn eine Kategorie ausgewählt wird
    
    //Listener mit Hilfe folgender Quelle geschrieben: https://stackoverflow.com/questions/14522680/javafx-choicebox-events
    //Antwort von zhujik, Jan 25 '13 at 14:08
    private void categoryChangedListener() {
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
    		//was passiert, wenn ein Eintrag in der ListCatalog ausgewählt wird
    		if(newSelection != null)
    		{
    		updateArticleInfo(true);
    		addToLastViewedItems();
    		}
    	});
    	
    	//Listener mit Hilfe folgender Quelle geschrieben: https://stackoverflow.com/questions/26424769/javafx8-how-to-create-listener-for-selection-of-row-in-tableview
    	//Antwort von James_D, Oct 17 '14 at 14:11
    	
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
    	//selectionInCatalog = true --> Selektion im Katalog geändert
    	//selectionInCatalog = false --> Selektion in LastViewed geändert
    	if(selectionInCatalog==true)
    	{
    		//Artikel in der ListCatalog ausgewählt
    		if(MainScreen_ListCatalog.getSelectionModel().getSelectedItem() != null)
	    	{
		    	MainScreen_ListLastViewed.getSelectionModel().clearSelection();
	    		//Item in der Katalog-Liste angewählt
		    	
		    	//Daten einfügen
		    	MainScreen_LabelProductTitle.setText(MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getName());
		    	MainScreen_LabelProductPrice.setText("Preis: " + MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getPriceString());
		    	MainScreen_LabelProductSeller.setText("Verkäufer: " + MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getSeller().getBusinessname() + " (Benutzer " + MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getSeller().getUsername() + ")");
		    	String selectedCategory = MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getCategory();
		    	if(selectedCategory=="")
		    	{
			    	MainScreen_LabelProductCategory.setText("Kategorie: (Keine Kategorie)");
		    	}
		    	else
		    	{
			    	MainScreen_LabelProductCategory.setText("Kategorie: " + selectedCategory);
		    	}
		    	MainScreen_WebVIewProductDescription.getEngine().loadContent(MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getDescription().replace(System.lineSeparator(), "<br/>")); //<br/> = neue Zeile in HTML
		    	
		    	MainScreen_ButtonBuyProduct.setVisible(true);
		    	MainScreen_WebVIewProductDescription.setVisible(true);
		    	
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
		    	MainScreen_LabelProductPrice.setText("Preis: " + MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getPriceString());
		    	MainScreen_LabelProductSeller.setText("Verkäufer: " + MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getSeller().getBusinessname() + " (Benutzer " + MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getSeller().getUsername() + ")");
		    	String selectedCategory = MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getCategory();
		    	if(selectedCategory=="")
		    	{
			    	MainScreen_LabelProductCategory.setText("Kategorie: (Keine Kategorie)");
		    	}
		    	else
		    	{
			    	MainScreen_LabelProductCategory.setText("Kategorie: " + selectedCategory);
		    	}
		    	MainScreen_WebVIewProductDescription.getEngine().loadContent(MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getDescription().replace(System.lineSeparator(), "<br/>")); //<br/> = neue Zeile in HTML
		    	
		    	MainScreen_ButtonBuyProduct.setVisible(true);
		    	MainScreen_WebVIewProductDescription.setVisible(true);
		    	
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
    	//Zu zuletzt angesehenen Produkten hinzufï¿½gen
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
    		//noch nicht in der Liste der zuletzt angesehenen Produkte, hinzufï¿½gen
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
    	if(newValue>-1)
    	{
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
				
				if(articlesInSearch != null)
				{
					if(MainScreen_ChoiceBox_Category.getSelectionModel().getSelectedIndex()==0)	{
						//Alle Kategorien ausgewählt
						articlesInSearchAndCategory = articlesInSearch;
					}
					else {
						//bestimmte Kategorie ausgewählt
						
						//Arraygröße bestimmen
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
    private Label MainScreen_LabelLoggedInAs;

    @FXML
    private TextField MainScreen_txtSearch;
    
    @FXML
    private WebView MainScreen_WebVIewProductDescription;
    
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
    private Label MainScreen_LabelProductTitle;

    @FXML
    private Label MainScreen_LabelProductSeller;

    @FXML
    private Label MainScreen_LabelProductPrice;

    @FXML
    private Label MainScreen_LabelProductCategory;
    
    @FXML
    private ImageView MainScreen_ImgProfilePicture;

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
    	WalletController.setUser(user);
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonEditAccount.getScene().getWindow(), "Wallet", "Guthaben aufladen", false, true);
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
    void MainScreen_txtSearch_KeyPressed(KeyEvent event) {
    	//Taste wird gedrückt
    	//Bei Enter: Button Search Klick simulieren
    	if (event.getCode().equals(KeyCode.ENTER))
        {
    		MainScreen_btnSearchOK.fire();
        }
    }

    @FXML
    //OfferProduct oeffnen
    void MainScreen_btnSellProductClick(ActionEvent event) {
    	OfferProductController.setUser(user);
    	
    	//ggf. Kategorien mit übergeben
    	if(MainScreen_ChoiceBox_Category.getItems() != null)
    	{
    		OfferProductController.setCategoryList(MainScreen_ChoiceBox_Category.getItems());
    	}
    	FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonSellProduct.getScene().getWindow(), "OfferProduct", "Produkt(e) anbieten", false, true);
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
    	
    	//Client BuyItem Request senden
    	//Es wird bei dieser Request automatisch das Käuferkonto um den Produktpreis verringert
    	//und das Verkäuferkonto um den Produktpreis erhöht
    	//In der Request wird geprüft, ob genug Guthaben vorhanden ist.
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
		else if(queryResponse.getResponseType() == Response.InsufficientBalance)
		{
			FXMLHandler.ShowMessageBox("Ihr Guthaben reicht nicht aus, um das ausgewählte Produkt zu kaufen.",
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
