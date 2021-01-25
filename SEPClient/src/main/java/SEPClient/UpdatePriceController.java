package SEPClient;

import java.io.IOException;
import java.util.HashMap;

import SEPCommon.ClientRequest;
import SEPCommon.Product;
import SEPCommon.Request;
import SEPCommon.Response;
import SEPCommon.Seller;
import SEPCommon.ServerResponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
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
		
		NewPrice_txtProductID.setText("Produkt-ID: " + product.getId());
		NewPrice_txtProductName.setText("Produkt: " + product.getName());
		NewPrice_txtProductCurrentPrice.setText("Aktueller Preis: " + product.getPrice());
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
    	 
    	String newPriceString = NewPrice_txtNewPrice.getText().trim();
    	double newPrice;
    	
    	try {
    		newPrice = Double.parseDouble(newPriceString.replace(",", "."));
    	} 
		
		catch (NumberFormatException e)	{
			FXMLHandler.ShowMessageBox("Bitte geben Sie den neuen Preis im folgenden Format ein: ##,##" + System.lineSeparator() + "(Ohne W�hrungszeichen und mit . oder ,)", "Fehler", "Fehler", AlertType.ERROR, true, false);			
			NewPrice_txtNewPrice.setText("");
			return; 
		}
    	
    	if (newPrice <= 0) {
    		FXMLHandler.ShowMessageBox("Bitte geben Sie einen Preis ein, der mehr als 0$ betr�gt", "Fehler", "Fehler", AlertType.ERROR, true, false);			
			NewPrice_txtNewPrice.setText("");
			return; 
    	}
    		

    	HashMap <String, Object> requestMap = new HashMap<String, Object>();
    	requestMap.put("Product", product);
    	requestMap.put("NewPrice", newPrice);
    	
    	ClientRequest req = new ClientRequest(Request.UpdatePrice, requestMap);
    	Client client  = Client.getClient();
    	ServerResponse queryResponse = client.sendClientRequest(req);
    	
 		    
	    if (queryResponse.getResponseType() == Response.NoDBConnection) {
			FXMLHandler.ShowMessageBox("Es konnte keine Verbindung zur Datenbank hergestellt werden, der Preis konnte nicht ge�ndert werden.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
			return;
		}
	    
		else if(queryResponse.getResponseType() == Response.Failure)
		{
			FXMLHandler.ShowMessageBox("Bei der �nderungen des Preises ist ein unbekannter Fehler aufgetreten.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
			return;
		}
		

		else if(queryResponse.getResponseType() == Response.Success)
		{
			FXMLHandler.ShowMessageBox("Ihre Preisanpassung wurde erfolgreich angenommen.",
					"Preis ver�ndert", "Stornierung erfolgreich", AlertType.CONFIRMATION, true,
					false);
			FXMLHandler.OpenSceneInStage((Stage) NewPrice_ButtonReturn.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
		}
    }

    @FXML
    void NewPrice_ButtonReturn_Click(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) NewPrice_ButtonReturn.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
    }
}
