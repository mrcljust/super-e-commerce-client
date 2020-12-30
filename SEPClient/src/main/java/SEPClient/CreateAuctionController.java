package SEPClient;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

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
import SEPCommon.ClientRequest;
import SEPCommon.Customer;
import SEPCommon.Request;
import SEPCommon.Response;
import SEPCommon.ServerResponse;

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

    }

    @FXML
    void Auction_EndDatePicker_Choice(ActionEvent event) {

    }

    @FXML
    void Auction_InsertClick(ActionEvent event) {
    
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
        	//Startdatum und Startzeit in Date umwandeln. vorher noch prüfen ob die Startzeit im Format XX:XX ist
    		try {
    			String[] startTimeSplit = Auction_StartTime.getText().split(":");
            	int starthour = Integer.parseInt(startTimeSplit[0]);
            	int startminute = Integer.parseInt(startTimeSplit[1]);
            	startDateAndTime = SEPCommon.Methods.convertLocalDateTimeToCET(Auction_StartDatePicker.getValue().atTime(starthour, startminute)).toLocalDateTime();
    		
			} catch (Exception e) {
				FXMLHandler.ShowMessageBox("Bitte geben Sie eine gültige Start-Uhrzeit im folgenden Format ein: XX:XX", "Fehler", "Fehler", AlertType.ERROR, true, false);			
				return;
			}
    	}
    	
    	//Enddatum und Endzeit in Date umwandeln. vorher noch prüfen ob die Endzeit im Format XX:XX ist
    	try {
    		String[] endTimeSplit = Auction_EndTime.getText().split(":");
        	int endhour  = Integer.parseInt(endTimeSplit[0]);
        	int endminute  = Integer.parseInt(endTimeSplit[1]);
        	endDateAndTime = SEPCommon.Methods.convertLocalDateTimeToCET(Auction_EndDatePicker.getValue().atTime(endhour, endminute)).toLocalDateTime();
		} catch (Exception e) {
			FXMLHandler.ShowMessageBox("Bitte geben Sie eine gültige End-Uhrzeit im folgenden Format ein: XX:XX", "Fehler", "Fehler", AlertType.ERROR, true, false);			
			return;
		}
    	
    	//Anhand Serveruhrzeit prüfen ob gültig (in Zukunft) und ob Endzeit nach Startzeit
        ClientRequest req = new ClientRequest(Request.GetServerDateTime, null);
    	Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);
		
		
		//Antwort auslesen
		if(queryResponse.getResponseType() == Response.Success)
		{
			Date serverDate = (Date)queryResponse.getResponseMap().get("ServerDateTime");
			
			//HIER PRUEFEN (.isBefore() / .isAfter())
			
	    	//TESTAUSGABE
	    	System.out.println(startDateAndTime);
	    	System.out.println(endDateAndTime);
			System.out.println(serverDate);
		}
		else
		{
			FXMLHandler.ShowMessageBox("Das Datum vom Server kann nicht geprüft werden, ggf. ist der Server nicht erreichbar.", "Fehler", "Fehler", AlertType.ERROR, true, false);			
			return;
		}
		
		FXMLHandler.OpenSceneInStage((Stage) Auction_Return.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
		
    }

    @FXML
    void Auction_OpenPictureClick(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Bild auswählen");
		File file = fileChooser.showOpenDialog(FXMLHandler.getStage()); 
		if (file != null) {
			if(!file.toURI().toString().toLowerCase().contains(".png") && !file.toURI().toString().toLowerCase().contains(".jpg") && !file.toURI().toString().toLowerCase().contains(".jpeg"))
    	    	
			{
    			//Bild weder .jpg, .jpeg noch .png
    	    	FXMLHandler.ShowMessageBox("Bitte wählen Sie eine .jpg-, .jpeg- oder .png-Datei aus.",
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

