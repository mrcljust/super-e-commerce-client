package SEPClient;

import java.io.IOException;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import SEPCommon.Auction;
import SEPCommon.Customer;
import SEPCommon.Order;

public class MyPurchasesController {

	private static Customer customer = null;

	public static void setCustomer(Customer _customer) {
		customer = _customer;
	}

	public void initialize() throws IOException {

	}

    @FXML
    private TableView<Order> MyPurchases_ListOrders;

    @FXML
    private TableColumn<Order, Integer> ordersIdColumn;

    @FXML
    private TableColumn<Order, Date> ordersDateColumn;

    @FXML
    private TableColumn<Order, String> ordersProductnameColumn;

    @FXML
    private TableColumn<Order, Double> ordersPriceColumn;

    @FXML
    private TableColumn<Order, String> ordersRatingGivenColumn;

    @FXML
    private TableView<Auction> MyPurchases_ListAuctions;

    @FXML
    private TableColumn<Auction, Integer> auctionsIdColumn;

    @FXML
    private TableColumn<Auction, Date> auctionsEndColumn;

    @FXML
    private TableColumn<Auction, String> auctionsNameColumn;

    @FXML
    private TableColumn<Auction, Double> auctionsPriceColumn;

    @FXML
    private TableColumn<Auction, String> auctionsRatingGivenColumn;

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

    }
    
    @FXML
    void MyPurchases_DeleteOrderButton_Click(ActionEvent event) {

    }
    

    @FXML
    void MyPurchases_Return_Click(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) MyPurchases_Return.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
    }
}
