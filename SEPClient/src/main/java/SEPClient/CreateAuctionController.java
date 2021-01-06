package SEPClient;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.coobird.thumbnailator.Thumbnails;
import SEPCommon.Auction;
import SEPCommon.ClientRequest;
import SEPCommon.Customer;
import SEPCommon.Request;
import SEPCommon.Response;
import SEPCommon.ServerResponse;
import SEPCommon.ShippingType;

public class CreateAuctionController {

	private static Customer customer = null;

	public static void setCustomer(Customer _customer) {
		customer = _customer;
	}

	public void initialize() throws IOException {
    	ToggleGroup radioGroupShipping = new ToggleGroup();
    	Auction_radioShipping.setToggleGroup(radioGroupShipping);
    	Auction_radioPickUp.setToggleGroup(radioGroupShipping);
    	
    	ToggleGroup radioGroupStarttime = new ToggleGroup();
    	Auction_radioStartNow.setToggleGroup(radioGroupStarttime);
    	Auction_radioStartOther.setToggleGroup(radioGroupStarttime);
    	
    	//Aktuelles Datum in EndDatePicker
    	Auction_EndDatePicker.setValue(SEPCommon.Methods.convertToLocalDate(new Date()));
	}
	
    @FXML
    private TextField Auction_txtName;

    @FXML
    private TextField Auction_txtStartPrice;

    @FXML
    private TextField Auction_txtMinBid;

    @FXML
    private HTMLEditor Auction_txtDescription;

    @FXML
    private RadioButton Auction_radioStartNow;

    @FXML
    private RadioButton Auction_radioStartOther;

    @FXML
    private DatePicker Auction_StartDatePicker;

    @FXML
    private TextField Auction_StartTime;

    @FXML
    private DatePicker Auction_EndDatePicker;

    @FXML
    private TextField Auction_EndTime;

    @FXML
    private ImageView Auction_imgPicture;

    @FXML
    private Button Auction_ButtonChooseImage;

    @FXML
    private Button Auction_ButtonDeleteImage;

    @FXML
    private RadioButton Auction_radioShipping;

    @FXML
    private RadioButton Auction_radioPickUp;

    @FXML
    private Button Auction_Insert;

    @FXML
    private Button Auction_Return;

    @FXML
    void Auction_DeletePictureClick(ActionEvent event) {
    	//Standardbild setzen
    	Image defaultImage = new Image(getClass().getResource("/SEPClient/UI/no-image.jpg").toString());
    	Auction_imgPicture.setImage(defaultImage);
    }

    @FXML
    void Auction_EndDatePicker_Choice(ActionEvent event) {

    }

    @FXML
    void Auction_InsertClick(ActionEvent event) throws IOException { //irgendwo fehler    	
    	String name = Auction_txtName.getText().trim();
    	String startingpriceString = Auction_txtStartPrice.getText().trim();
    	String minBidString = Auction_txtMinBid.getText().trim();
    	String description = Auction_txtDescription.getHtmlText().trim();
    	double startingPrice;
    	double minBid;
    	ShippingType shippingType;
    	
    	
    	
    	    	
    	
    	Image image = Auction_imgPicture.getImage();
    	BufferedImage imageBuffered = SwingFXUtils.fromFXImage(image, null);
    	
    	BufferedImage imageResized = Thumbnails.of(imageBuffered).size(512, 512).asBufferedImage();
    	ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
    	ImageIO.write(imageResized, "png", byteOutput);
    	byte[] imageByteArray = byteOutput.toByteArray();
    	byteOutput.close();
    	
    	 
    	
     	//DATUM IN CET KONVERTIEREN UND PRUEFEN
    	
    	
    	LocalDateTime startDateAndTime;
    	LocalDateTime endDateAndTime;

    	if(Auction_radioStartNow.isSelected())
    	{
        	//Startzeitpunkt jetzt
    		startDateAndTime = SEPCommon.Methods.convertLocalDateTimeToCET(LocalDateTime.now()).toLocalDateTime();
    	}
    	else
    	{
        	//Anderer Startzeitpunkt
        	//Startdatum und Startzeit in Date umwandeln. vorher noch pr�fen ob die Startzeit im Format XX:XX ist
    		try {
    			String[] startTimeSplit = Auction_StartTime.getText().split(":");
            	int starthour = Integer.parseInt(startTimeSplit[0]);
            	int startminute = Integer.parseInt(startTimeSplit[1]);
            	startDateAndTime = SEPCommon.Methods.convertLocalDateTimeToCET(Auction_StartDatePicker.getValue().atTime(starthour, startminute)).toLocalDateTime();
    		
			} catch (Exception e) {
				FXMLHandler.ShowMessageBox("Bitte geben Sie eine g�ltige Start-Uhrzeit im folgenden Format ein: XX:XX", "Fehler", "Fehler", AlertType.ERROR, true, false);			
				return;
			}
    	}
    	
    	//Enddatum und Endzeit in Date umwandeln. vorher noch pr�fen ob die Endzeit im Format XX:XX ist
    	try {
    		String[] endTimeSplit = Auction_EndTime.getText().split(":");
        	int endhour  = Integer.parseInt(endTimeSplit[0]);
        	int endminute  = Integer.parseInt(endTimeSplit[1]);
        	endDateAndTime = SEPCommon.Methods.convertLocalDateTimeToCET(Auction_EndDatePicker.getValue().atTime(endhour, endminute)).toLocalDateTime();
		} catch (Exception e) {
			FXMLHandler.ShowMessageBox("Bitte geben Sie eine g�ltige End-Uhrzeit im folgenden Format ein: XX:XX", "Fehler", "Fehler", AlertType.ERROR, true, false);			
			return;
		}
    	
    	//Anhand Serveruhrzeit pr�fen ob g�ltig (in Zukunft) und ob Endzeit nach Startzeit
        ClientRequest req = new ClientRequest(Request.GetServerDateTime, null);
    	Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);
		
		
		//Antwort auslesen
		if(queryResponse.getResponseType() == Response.Success)
		{
			LocalDateTime serverDate = (LocalDateTime)queryResponse.getResponseMap().get("ServerDateTime");
			
			//HIER PRUEFEN (.isBefore() / .isAfter())
			
	    	//TESTAUSGABE
	    	System.out.println(startDateAndTime);
	    	System.out.println(endDateAndTime);
			System.out.println(serverDate);
		}
		else
		{
			FXMLHandler.ShowMessageBox("Das Datum vom Server kann nicht gepr�ft werden, ggf. ist der Server nicht erreichbar.", "Fehler", "Fehler", AlertType.ERROR, true, false);			
			return;
		}
		
		
		if (name == "" || name == null || startingpriceString == "" || startingpriceString == null || minBidString == "" || minBidString == null) {
			
			FXMLHandler.ShowMessageBox("Bitte geben Sie Name, Startpreis und Mindestgebot an. ", "Fehler", "Fehler", AlertType.ERROR, true, false);
			return;
		}
		
		if (Auction_radioShipping.isSelected()) {
			shippingType = ShippingType.Shipping;
		} else {
			shippingType = ShippingType.PickUp;
		}
		
		

		try {
    		startingPrice = Double.parseDouble(startingpriceString.replace(",", "."));
    	} 
		
		catch (NumberFormatException e)	{
			FXMLHandler.ShowMessageBox("Bitte geben Sie den Startpreis im folgenden Format ein: ##,##" + System.lineSeparator() + "(Ohne W�hrungszeichen und mit . oder ,)", "Fehler", "Fehler", AlertType.ERROR, true, false);			
			Auction_txtStartPrice.setText("");
			return; 
		}
		
		try {
    		minBid = Double.parseDouble(minBidString.replace(",", "."));
    	} 
		
		catch (NumberFormatException e)	{
			FXMLHandler.ShowMessageBox("Bitte geben Sie das Mindestgebot im folgenden Format ein: ##,##" + System.lineSeparator() + "(Ohne W�hrungszeichen und mit . oder ,)", "Fehler", "Fehler", AlertType.ERROR, true, false);			
			Auction_txtMinBid.setText("");
			return; 
		}
		
		
		
		Auction newAuction = new Auction(name, description, imageByteArray, minBid, startingPrice, shippingType, customer, startDateAndTime, endDateAndTime);
		
		HashMap<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put("Auction", newAuction);
		
		req = new ClientRequest(Request.CreateAuction, requestMap);
		client = Client.getClient();
		queryResponse = client.sendClientRequest(req); 
		
		// Wenn Produkt erfolgreich angelegt, Response.Success returnen
				// wenn keine Verbindung zu DB: Response.NoDBConnection returnen
				// wenn sonstiger Fehler auftritt ggf. Response.Failure returnen
					
		if(queryResponse.getResponseType() == Response.NoDBConnection)
		{
			FXMLHandler.ShowMessageBox("Es konnte keine Verbindung zur Datenbank hergestellt werden, es wurde daher keine Auktion inseriert.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
			return;
		}
		else if(queryResponse.getResponseType() == Response.Failure)
		{
			FXMLHandler.ShowMessageBox("Beim Inserieren der Auktion ist ein unbekannter Fehler aufgetreten.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
			return;
		}
		else if(queryResponse.getResponseType() == Response.Success)
		{
			FXMLHandler.ShowMessageBox("Die Auktion '" + name + "' wurde erfolgreich inseriert.",
					"Auktion inseriert", "Auktion inseriert", AlertType.CONFIRMATION, true,
					false);
			//MainScreen oeffnen
			MainScreenController.setUser(customer);
			FXMLHandler.OpenSceneInStage((Stage) Auction_Insert.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
			return;
		}
		
    }

    @FXML
    void Auction_OpenPictureClick(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Bild ausw�hlen");
		File file = fileChooser.showOpenDialog(FXMLHandler.getStage()); 
		if (file != null) {
			if(!file.toURI().toString().toLowerCase().contains(".png") && !file.toURI().toString().toLowerCase().contains(".jpg") && !file.toURI().toString().toLowerCase().contains(".jpeg"))
    	    	
			{
    			//Bild weder .jpg, .jpeg noch .png
    	    	FXMLHandler.ShowMessageBox("Bitte w�hlen Sie eine .jpg-, .jpeg- oder .png-Datei aus.",
    					"Fehler", "Fehler", AlertType.ERROR, true,
    					false);
    	    	return;
    	    }
			Image selectedImage = new Image (file.toURI().toString());
			Auction_imgPicture.setImage(selectedImage);
		}
    	
    	
    	
    };

    @FXML
    void Auction_ReturnClick(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) Auction_Insert.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
    }

    @FXML
    void Auction_StartDatePicker_Choice(ActionEvent event) {
    	
    }

    @FXML
    void Auction_radioStartNow_Click(ActionEvent event) {
    	Auction_StartDatePicker.setDisable(true);
    	Auction_StartTime.setDisable(true);
    	Auction_StartTime.setText("");
    	Auction_StartDatePicker.setValue(null);
    }

    @FXML
    void Auction_radioStartOther_Click(ActionEvent event) {
    	Auction_StartDatePicker.setDisable(false);
    	Auction_StartTime.setDisable(false);
    	

    	//Aktuelles Datum in StartDatePicker
    	Auction_StartDatePicker.setValue(SEPCommon.Methods.convertToLocalDate(new Date()));

    	//Aktuelles Zeit in StartTime
    	LocalTime localTime = SEPCommon.Methods.convertToLocalTime(new Date());
    	Auction_StartTime.setText(localTime.getHour() + ":" + localTime.getMinute());
    }

}

