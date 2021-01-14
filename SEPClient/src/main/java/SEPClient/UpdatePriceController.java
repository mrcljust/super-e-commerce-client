package SEPClient;

import java.io.IOException;

import SEPCommon.Product;
import SEPCommon.Seller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdatePriceController {

	private static Seller seller = null;
	private static Product product = null;
	
	public static void setSeller(Seller _seller)
	{
		seller=_seller;
	}
	
	public static void setProduct(Product _product)
	{
		product=_product;
	}
	
	public void initialize() throws IOException {
		
	}
	
	@FXML
    private Label NewPrice_txtProductID;

    @FXML
    private Label NewPrice_txtProductName;

    @FXML
    private Label NewPrice_txtProductCurrentPrice;

    @FXML
    private TextField NewPrice_txtNewPrice;

    @FXML
    private Button NewPrice_ButtonOK;

    @FXML
    private Button NewPrice_ButtonReturn;

    @FXML
    void NewPrice_ButtonOK_Click(ActionEvent event) {

    }

    @FXML
    void NewPrice_ButtonReturn_Click(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) NewPrice_ButtonReturn.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
    }
}
