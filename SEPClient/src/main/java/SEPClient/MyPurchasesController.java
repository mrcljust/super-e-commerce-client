package SEPClient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import SEPCommon.Auction;
import SEPCommon.AuctionType;
import SEPCommon.ClientRequest;
import SEPCommon.Constants;
import SEPCommon.Customer;
import SEPCommon.Order;
import SEPCommon.Rating;
import SEPCommon.Request;
import SEPCommon.Response;
import SEPCommon.ServerResponse;
import SEPCommon.ShippingType;

public class MyPurchasesController {

	private static Customer customer = null;

	public static void setCustomer(Customer _customer) {
		customer = _customer;
	}

	public void initialize() throws IOException {
		MyPurchases_ListOrders.setItems(loadAllOrders());
		
		//Werte an die Spalten der ListOrders zuweisen
    	ordersIdColumn.setCellValueFactory(new PropertyValueFactory<Order, Integer>("id"));
    	ordersProductnameColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("productName"));
    	ordersPriceColumn.setCellValueFactory(new PropertyValueFactory<Order, Double>("productPrice"));
        //Anzeigewert für Gebot anpassen
    	ordersPriceColumn.setCellFactory(tc -> new TableCell<Order, Double>() {
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
        ordersDateColumn.setCellValueFactory(new PropertyValueFactory<Order, LocalDateTime>("date"));
        ordersDateColumn.setCellFactory(tc -> new TableCell<Order, LocalDateTime>() {
    	    @Override
    	    protected void updateItem(LocalDateTime date, boolean empty) {
    	        super.updateItem(date, empty);
    	        if (empty || date==null) {
    	            setText(null);
    	        } else {
    	            setText(date.format(SEPCommon.Constants.DATEFORMAT));
    	        }
    	    }
    	});
        ordersRatingGivenColumn.setCellValueFactory(new PropertyValueFactory<Order, Rating>("sellerRating")); //evtl BuyerRating
        ordersRatingGivenColumn.setCellFactory(tc -> new TableCell<Order, Rating>() {
    	    @Override
    	    protected void updateItem(Rating sellerRating, boolean empty) {
    	        super.updateItem(sellerRating, empty);
    	        if(empty) {
    	        	setText(null);
    	        } else if (sellerRating==null) {
    	            setText("Noch nicht abgegeben");
    	        } else {
    	            setText("Abgegeben");
    	        }
    	    }
    	});
		
        MyPurchases_ListAuctions.setItems(loadAllAuctions());
        
		//Werte an die Spalten der ListAuctions zuweisen
    	auctionsIdColumn.setCellValueFactory(new PropertyValueFactory<Auction, Integer>("id"));
    	auctionsEndColumn.setCellValueFactory(new PropertyValueFactory<Auction, LocalDateTime>("enddate"));
    	auctionsEndColumn.setCellFactory(tc -> new TableCell<Auction, LocalDateTime>() {
    	    @Override
    	    protected void updateItem(LocalDateTime date, boolean empty) {
    	        super.updateItem(date, empty);
    	        if (empty || date==null) {
    	            setText(null);
    	        } else {
    	            setText(date.format(SEPCommon.Constants.DATEFORMAT));
    	        }
    	    }
    	});
    	auctionsNameColumn.setCellValueFactory(new PropertyValueFactory<Auction, String>("title"));
    	auctionsShippingColumn.setCellValueFactory(new PropertyValueFactory<Auction, ShippingType>("shippingType"));
        //Anzeigewert für Versandart anpassen
    	auctionsShippingColumn.setCellFactory(tc -> new TableCell<Auction, ShippingType>() {
    	    @Override
    	    protected void updateItem(ShippingType sh, boolean empty) {
    	        super.updateItem(sh, empty);
    	        if (empty || sh==null) {
    	            setText(null);
    	        } else {
    	            setText(sh.toString());
    	        }
    	    }
    	});
    	auctionsPriceColumn.setCellValueFactory(new PropertyValueFactory<Auction, Double>("currentBid"));
        //Anzeigewert für Gebot anpassen
    	auctionsPriceColumn.setCellFactory(tc -> new TableCell<Auction, Double>() {
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
        auctionsRatingGivenColumn.setCellValueFactory(new PropertyValueFactory<Auction, Rating>("sellerRating")); //evtl BuyerRating
        auctionsRatingGivenColumn.setCellFactory(tc -> new TableCell<Auction, Rating>() {
    	    @Override
    	    protected void updateItem(Rating sellerRating, boolean empty) {
    	        super.updateItem(sellerRating, empty);
    	        if(empty) {
    	        	setText(null);
    	        } else if (sellerRating==null) {
    	            setText("Noch nicht abgegeben");
    	        } else {
    	            setText("Abgegeben");
    	        }
    	    }
    	});
        
        
        rateSellerListener();
        
        
	}

	
    @FXML
    private TableView<Order> MyPurchases_ListOrders;

    @FXML
    private TableColumn<Order, Integer> ordersIdColumn;

    @FXML
    private TableColumn<Order, LocalDateTime> ordersDateColumn;

    @FXML
    private TableColumn<Order, String> ordersProductnameColumn;

    @FXML
    private TableColumn<Order, Double> ordersPriceColumn;

    @FXML
    private TableColumn<Order, Rating> ordersRatingGivenColumn;

    @FXML
    private TableView<Auction> MyPurchases_ListAuctions;

    @FXML
    private TableColumn<Auction, Integer> auctionsIdColumn;

    @FXML
    private TableColumn<Auction, LocalDateTime> auctionsEndColumn;

    @FXML
    private TableColumn<Auction, String> auctionsNameColumn;
    
    @FXML
    private TableColumn<Auction, ShippingType> auctionsShippingColumn;

    @FXML
    private TableColumn<Auction, Double> auctionsPriceColumn;

    @FXML
    private TableColumn<Auction, Rating> auctionsRatingGivenColumn;

    @FXML
    private Button MyPurchases_CreateRating_Order;

    @FXML
    private Button MyPurchases_CreateRating_Auction;
    
    @FXML
    private Button MyPurchases_DeleteOrderButton;

    @FXML
    private Button MyPurchases_Return;

    @FXML
    void MyPurchases_CreateRating_Auction_Click(ActionEvent event) {
    	
    }

    @FXML
    void MyPurchases_CreateRating_Order_Click(ActionEvent event) {
    	//FXMLHandler.OpenSceneInStage((Stage) MyPurchases_CreateRating_Order.getScene().getWindow(), "CreateRating", "Bewerten", true, true);
    }
    	
    
    
    @FXML
    void MyPurchases_DeleteOrderButton_Click(ActionEvent event) {

    }
    
    private ObservableList<Order> loadAllOrders() { 
    	
    	if(MyPurchases_ListOrders.getItems()!=null)
    	{
    		MyPurchases_ListOrders.getItems().clear();
    	}
        
    	
    	HashMap<String, Object> requestMap = new HashMap<String, Object>();
    	requestMap.put("Buyer", customer);
    	
    	ClientRequest req = new ClientRequest(Request.FetchOrders, requestMap);
    	Client client = Client.getClient();
    	ServerResponse queryResponse = client.sendClientRequest(req);
    	
    	if(queryResponse!=null && queryResponse.getResponseMap() != null && queryResponse.getResponseMap().get("Orders")!=null){
    		
    		Order [] orders = (Order[])queryResponse.getResponseMap().get("Orders");
    		ObservableList<Order> ObservableOrders = FXCollections.observableArrayList(orders);
    		ObservableOrders.removeIf(n -> (n==null));
    		
    		return ObservableOrders;
    	}
    	return null;
    }
    
    private void rateSellerListener() { 
    	
    	//TODO: es muss noch geprüft werden, ob Bewertung schon angegeben wurde
    	MyPurchases_ListOrders.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
    		if(newSelection != null) {
    			MyPurchases_CreateRating_Order.setDisable(false);
    		}
    	});
    	
    	MyPurchases_ListAuctions.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
    		if(newSelection != null) {
    			MyPurchases_CreateRating_Auction.setDisable(false);
    		}
    	});
    	
    }
    
    private void cancelOrderListener() {
    	  MyPurchases_ListOrders.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
    		  if((newSelection != null) && (ordersDateColumn.getCellData(newSelection) == LocalDateTime.now())) { //das prüft auch die Uhrzeit.., neues Format?
    			  MyPurchases_CreateRating_Order.setDisable(false);
    		  }
    	  });
    	
    }
   
    
    private ObservableList<Auction> loadAllAuctions() {
    	if(MyPurchases_ListAuctions.getItems()!=null)
    	{
    		MyPurchases_ListAuctions.getItems().clear();
    	}
        
    	
    	HashMap<String, Object> requestMap = new HashMap<String, Object>();
    	requestMap.put("AuctionType", AuctionType.PurchasedAuctions);
    	requestMap.put("User", customer);
    	
    	ClientRequest req = new ClientRequest(Request.FetchAuctions, requestMap);
    	Client client = Client.getClient();
    	ServerResponse queryResponse = client.sendClientRequest(req);
    	
    	if(queryResponse!=null && queryResponse.getResponseMap() != null && queryResponse.getResponseMap().get("Auctions")!=null){
    		
    		Auction [] auctions = (Auction[])queryResponse.getResponseMap().get("Auctions");
    		ObservableList<Auction> ObservableAuctions = FXCollections.observableArrayList(auctions);
    		ObservableAuctions.removeIf(n -> (n==null));
    		
    		return ObservableAuctions;
    	}
    	return null;
    }
    

    @FXML
    void MyPurchases_Return_Click(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) MyPurchases_Return.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
    }
}
