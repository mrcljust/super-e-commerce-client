package SEPClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import SEPCommon.ClientRequest;
import SEPCommon.Product;
import SEPCommon.Request;
import SEPCommon.Response;
import SEPCommon.Seller;
import SEPCommon.ServerResponse;
import SEPCommon.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class OfferProductController {

	static User user = null;
	
	public static void setUser(User _user)
	{
		user = _user;
	}
	
    @FXML
    public void initialize() {
    	//CSV-Verkaufen Button erst aktivieren, wenn Datei ausgewählt ist.
    	Sell_ButtonSellCsv.setDisable(true);
    }
	
    @FXML
    private TextField Sell_txtName;

    @FXML
    private TextField Sell_txtPreis;

    @FXML
    private Button Sell_ButtonSellConfirm;

    @FXML
    private TextField Sell_txtCategory;

    @FXML
    private TextArea Sell_txtDescription;

    @FXML
    private Button Sell_ButtonSellCsv;

    @FXML
    private Button Sell_ButtonChooseFile;
    
    @FXML
    private Button Sell_ReturnButton;

    @FXML
    private TextField Sell_txtCSV;

    @FXML
    void Sell_ChooseFile(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle(".csv-Datei auswählen");
    	File file = fileChooser.showOpenDialog(FXMLHandler.getStage());
    	if(file!=null)
    	{
    	    if(!file.toURI().toString().contains(".csv"))
    	    {
    	    	//Ungültige Datei ohne .csv im Pfad ausgewählt
    	    	FXMLHandler.ShowMessageBox("Bitte wählen Sie eine .csv-Datei aus.",
    					"Fehler", "Fehler", AlertType.ERROR, true,
    					false);
        	    Sell_ButtonSellCsv.setDisable(true);
    	    	return;
    	    }
    	    
    	    Sell_txtCSV.setText(file.getAbsolutePath().toString());
    	    Sell_ButtonSellCsv.setDisable(false);
	    }
    }

    @FXML
    void Sell_SellConfirmClick(ActionEvent event) {

    }

    @FXML
    void Sell_SellCsvClick(ActionEvent event) {
    	String csvFilePathString = Sell_txtCSV.getText();
    	File csvFile = new File(csvFilePathString);
    	Seller seller = (Seller)user; //Typecasten. Das Fenster OfferProduct kann nur von Sellern aufgerufen werden. Zum Erstellen von Produkten wird ein Seller-Objekt benötigt.
    	
		try {
			BufferedReader csvReader = new BufferedReader(new FileReader(csvFile));
			List<String> lines = new ArrayList<>();
			
			String currentLine = null;
			while ((currentLine = csvReader.readLine()) != null) {
			    lines.add(currentLine);
			}
			csvReader.close();
			
			//Erste Zeile splitten um Identifier zu erhalten
			String[] csvIdentifier = lines.get(0).split(";");
			int errorcount = 0;
			List<Product> csvProducts = new ArrayList<>();
			for(int i=1;i<lines.size();i++)
			{
				try
				{
					String[] lineValues = lines.get(i).split(";");
					String productName = lineValues[0]; //1. Spalte
					String category = lineValues[1]; //2. Spalte
					double price = Double.parseDouble(lineValues[2].replace(",", ".")); //3. Spalte. , wird durch . ersetzt, damit hier kein Fehler beim Erstellen eines Doubles auftritt.
					String description = lineValues[3]; //4. Spalte
					//ggf extra Spalten mit in die Beschreibung schreiben
					for(int ii=4;ii<lineValues.length;ii++)
					{
						String descriptionTemp = description;
						description = csvIdentifier[ii] + ": " + lineValues[ii] + System.lineSeparator() + descriptionTemp; 
					}
					Product newProduct = new Product(productName, price, seller, category, description);
					csvProducts.add(newProduct);
				}
				catch (NumberFormatException e)
				{
					//aus einer Zeile kann kein Produkt-Array erstellt werden (z.B. wenn in der Preis-Spalte kein gültiger Double eingetragen ist)
					//Zähle den FehlerCounter hoch
					e.printStackTrace();
					errorcount++;
				}
			}
			
			//Prüfen ob in jeder Zeile ein Fehler ist, da dann keine Produkte hinzugefügt werden müssen
			//lines.size()-1, da die erste Zeile der csv-Dateien ja die Identifier sind.
			if(errorcount>=(lines.size()-1))
			{
				FXMLHandler.ShowMessageBox("Alle Zeilen der .csv-Datei enthalten Fehler, es wurde daher kein Produkt inseriert. Eventuell ist die Formatierung nicht korrekt?",
						"Fehler", "Fehler", AlertType.ERROR, true,
						false);
				return;
			}
			
			//mindestens ein Produkt im csvProducts-Array, welches zu listen ist.
			//Sende ClientRequest an den Server
			HashMap<String, Object> requestMap = new HashMap<String, Object>();
	    	requestMap.put("User", user);
	    	Product[] csvProductsArray = csvProducts.toArray(new Product[csvProducts.size()]);
	    	requestMap.put("Products", csvProductsArray);
	        ClientRequest req = new ClientRequest(Request.AddItems, requestMap);
	    	Client client = Client.getClient();
			ServerResponse queryResponse = client.sendClientRequest(req);
			
			
			//Wenn alle Produkte erfolgreich angelegt, Response.Success returnen
			//wenn keine Verbindung zu DB: Response.NoDBConnection returnen
			//wenn sonstiger Fehler auftritt ggf. Response.Failure returnen
			//wenn nur ein Teil der Produkte angelegt wird ggf. Response.Failure returnen
			
			if(queryResponse.getResponseType() == Response.NoDBConnection)
			{
				FXMLHandler.ShowMessageBox("Es konnte keine Verbindung zur Datenbank hergestellt werden, es wurde daher kein Produkt inseriert.",
						"Fehler", "Fehler", AlertType.ERROR, true,
						false);
				return;
			}
			else if(queryResponse.getResponseType() == Response.Failure)
			{
				FXMLHandler.ShowMessageBox("Bei mindestens einem Produkt kam es zu einem Fehler beim Inserieren.",
						"Fehler", "Fehler", AlertType.ERROR, true,
						false);
				return;
			}
			else if(queryResponse.getResponseType() == Response.Success)
			{
				if(errorcount==0)
				{
					FXMLHandler.ShowMessageBox("Es wurde(n) " + (lines.size()-1) + " Datensätze aus der .csv-Datei ausgelesen und erfolgreich inseriert.",
							"Erfolg", "Erfolg", AlertType.CONFIRMATION, true,
							false);
				}
				else
				{
					FXMLHandler.ShowMessageBox("Es wurde(n) " + (lines.size()-1) + " Datensätze aus der .csv-Datei ausgelesen. Hiervon war(en) " + errorcount + " Datensätze fehlerhaft (zum Beispiel falsch formatiert), daher wurde(n) " + ((lines.size()-1)-errorcount) + " Datensätze inseriert.",
							"Erfolg", "Erfolg", AlertType.CONFIRMATION, true, false);
				}
				//MainScreen oeffnen
				MainScreenController.setUser(user);
				FXMLHandler.OpenSceneInStage((Stage) Sell_ButtonSellCsv.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
	    	FXMLHandler.ShowMessageBox("Fehler beim Lesen der .csv-Datei, der Vorgang wird abgebrochen. Es wurde kein Produkt inseriert.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
		} 
    }
    
    @FXML
    void Sell_ReturnButtonClick(ActionEvent event) {
    	
    }

}
