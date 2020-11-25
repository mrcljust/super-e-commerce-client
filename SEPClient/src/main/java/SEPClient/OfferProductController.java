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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class OfferProductController {

	static User user = null;
	static ObservableList<String> productCategories = null;
	
	public static void setUser(User _user)
	{
		user = _user;
	}
	
	public static void setCategoryList(ObservableList<String> list)
	{
		//Die Methode wird im MainScreen vor dem Aufruf des OfferProduct-Fensters aufgerufen. Anhand der ObservableList
		//werden die vorgeschlagenen Kategorien gelistet.
		productCategories = list;
	}
	
    @FXML
    public void initialize() {
    	//CSV-Verkaufen Button erst aktivieren, wenn Datei ausgewï¿½hlt ist.
    	Sell_ButtonSellCsv.setDisable(true);
    	
    	ToggleGroup radioGroup = new ToggleGroup();
    	Sell_radioNoCategory.setToggleGroup(radioGroup);
    	Sell_radioUseCategory.setToggleGroup(radioGroup);
    	Sell_radioNewCategory.setToggleGroup(radioGroup);
    	
    	//Kategorien vom MainScreenController ï¿½bergeben, Alle Kategorien vorher entfernen
    	if(productCategories!=null)
    	{
    		productCategories.remove("Alle Kategorien");
    		Sell_choiceCategory.setItems(productCategories);
    	}
    }
	
    @FXML
    private TextField Sell_txtName;

    @FXML
    private TextField Sell_txtPreis;

    @FXML
    private Button Sell_ButtonSellConfirm;

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
    private TextField Sell_txtNewCategory;

    @FXML
    private RadioButton Sell_radioNewCategory;

    @FXML
    private RadioButton Sell_radioUseCategory;

    @FXML
    private RadioButton Sell_radioNoCategory;

    @FXML
    private ChoiceBox<String> Sell_choiceCategory;


    @FXML
    void Sell_RadioNewCategory_Click(ActionEvent event) {
    	if(Sell_radioNewCategory.isSelected())
    	{
    		Sell_txtNewCategory.setDisable(false);
    		Sell_choiceCategory.setDisable(true);
    	}
    }

    @FXML
    void Sell_RadioUseCategory_Click(ActionEvent event) {
    	if(Sell_radioUseCategory.isSelected())
    	{
    		Sell_txtNewCategory.setDisable(true);
    		Sell_choiceCategory.setDisable(false);
    	}
    }
    
    @FXML
    void Sell_RadioNoCategory_Click(ActionEvent event) {
    	if(Sell_radioNoCategory.isSelected())
    	{
    		Sell_txtNewCategory.setDisable(true);
    		Sell_choiceCategory.setDisable(true);
    	}
    }

    @FXML
    void Sell_ChooseFile(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle(".csv-Datei auswï¿½hlen");
    	File file = fileChooser.showOpenDialog(FXMLHandler.getStage());
    	if(file!=null)
    	{
    	    if(!file.toURI().toString().contains(".csv"))
    	    {
    	    	//Ungï¿½ltige Datei ohne .csv im Pfad ausgewï¿½hlt
    	    	FXMLHandler.ShowMessageBox("Bitte wï¿½hlen Sie eine .csv-Datei aus.",
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
    	//Eingaben prüfen
    	String articlename = Sell_txtName.getText();
    	String description = Sell_txtDescription.getText();
    	String priceString = Sell_txtPreis.getText();
    	boolean categoryChosen = false;
    	String category = "";
    	double price;
    	
    	//Prüfen ob Kategorie ausgewählt ist und in Variable schreiben
    	if(Sell_radioNoCategory.isSelected())
    	{
    		categoryChosen=true;
    		category = "";
    	}
    	else if(Sell_radioUseCategory.isSelected() && Sell_choiceCategory.getSelectionModel().getSelectedItem() != null)
    	{
    		categoryChosen=true;
    		category = Sell_choiceCategory.getSelectionModel().getSelectedItem();
    	}
    	else if(Sell_radioNewCategory.isSelected() && Sell_txtNewCategory.getText() != null && Sell_txtNewCategory.getText() != "")
    	{
    		categoryChosen=true;
    		category = Sell_txtNewCategory.getText();
    	}
    	else
    	{
    		categoryChosen=false;
    		category = "";
    	}
    	
    	//Prüfen ob alle Felder ausgefüllt. Die Beschreibung ist keine Pflicht.
    	if (articlename=="" || articlename==null || priceString=="" || priceString==null) {
			FXMLHandler.ShowMessageBox("Bitte füllen Sie alle mit einem Stern (*) versehenen Felder aus.", "Fehler", "Fehler", AlertType.ERROR, true, false);			
			return; //Methode beenden
		}
    	
    	if(!categoryChosen)
    	{
    		//keine Kategorie eingegeben
			FXMLHandler.ShowMessageBox("Bitte geben Sie eine Kategorie ein, oder wählen Sie 'Keine Kategorie'.", "Fehler", "Fehler", AlertType.ERROR, true, false);			
			return; //Methode beenden
    	}
    	
    	//Prüfen ob Preis double ist (vorher ggf. , durch . ersetzen)
    	try
    	{
    		price = Double.parseDouble(priceString.replace(",", "."));
    	}
    	catch (NumberFormatException e)
		{
			FXMLHandler.ShowMessageBox("Bitte geben Sie den Preis im folgenden Format ein: ##.##" + System.lineSeparator() + "(Ohne Währungszeichen und mit .)", "Fehler", "Fehler", AlertType.ERROR, true, false);			
			return; //Methode beenden
		}
    	
    	//Eingaben ok, lege Produkt an
    	//Sende ClientRequest
    	HashMap<String, Object> requestMap = new HashMap<String, Object>();
    	requestMap.put("User", user);
    	Product newProduct = new Product(articlename, price, (Seller)user, category, description);
    	requestMap.put("Product", newProduct);
        ClientRequest req = new ClientRequest(Request.AddItem, requestMap);
    	Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);
		
		// Wenn Produkt erfolgreich angelegt, Response.Success returnen
		// wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		// wenn sonstiger Fehler auftritt ggf. Response.Failure returnen
		
		if(queryResponse.getResponseType() == Response.NoDBConnection)
		{
			FXMLHandler.ShowMessageBox("Es konnte keine Verbindung zur Datenbank hergestellt werden, es wurde daher kein Artikel inseriert.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
			return;
		}
		else if(queryResponse.getResponseType() == Response.Failure)
		{
			FXMLHandler.ShowMessageBox("Beim Inserieren des Artikels ist ein unbekannter Fehler aufgetreten.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
			return;
		}
		else if(queryResponse.getResponseType() == Response.Success)
		{
			FXMLHandler.ShowMessageBox("Der Artikel '" + articlename + "' wurde erfolgreich inseriert.",
					"Artikel inseriert", "Artikel inseriert", AlertType.CONFIRMATION, true,
					false);
			//MainScreen oeffnen
			MainScreenController.setUser(user);
			FXMLHandler.OpenSceneInStage((Stage) Sell_ButtonSellCsv.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
			return ;
		}
		}

    @FXML
    void Sell_SellCsvClick(ActionEvent event) {
    	String csvFilePathString = Sell_txtCSV.getText();
    	File csvFile = new File(csvFilePathString);
    	Seller seller = (Seller)user; //Typecasten. Das Fenster OfferProduct kann nur von Sellern aufgerufen werden. Zum Erstellen von Produkten wird ein Seller-Objekt benï¿½tigt.
    	
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
					//aus einer Zeile kann kein Produkt-Array erstellt werden (z.B. wenn in der Preis-Spalte kein gï¿½ltiger Double eingetragen ist)
					//Zï¿½hle den FehlerCounter hoch
					e.printStackTrace();
					errorcount++;
				}
			}
			
			//Prï¿½fen ob in jeder Zeile ein Fehler ist, da dann keine Produkte hinzugefï¿½gt werden mï¿½ssen
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
				FXMLHandler.ShowMessageBox("Es konnte keine Verbindung zur Datenbank hergestellt werden, es wurde daher kein Artikel inseriert.",
						"Fehler", "Fehler", AlertType.ERROR, true,
						false);
				return;
			}
			else if(queryResponse.getResponseType() == Response.Failure)
			{
				FXMLHandler.ShowMessageBox("Bei mindestens einem Artikel kam es zu einem Fehler beim Inserieren. Ggf. wurde ein Teil der Artikel inseriert.",
						"Fehler", "Fehler", AlertType.ERROR, true,
						false);
				return;
			}
			else if(queryResponse.getResponseType() == Response.Success)
			{
				if(errorcount==0)
				{
					FXMLHandler.ShowMessageBox("Es wurde(n) " + (lines.size()-1) + " Artikel aus der .csv-Datei ausgelesen und erfolgreich inseriert.",
							"Erfolg", "Erfolg", AlertType.CONFIRMATION, true,
							false);
				}
				else
				{
					FXMLHandler.ShowMessageBox("Es wurde(n) " + (lines.size()-1) + " Artikel aus der .csv-Datei ausgelesen. Hiervon war(en) " + errorcount + " Artikel fehlerhaft (zum Beispiel ein falsches Preis-Format), daher wurde(n) " + ((lines.size()-1)-errorcount) + " Artikel inseriert.",
							"Erfolg", "Erfolg", AlertType.CONFIRMATION, true, false);
				}
				//MainScreen oeffnen
				MainScreenController.setUser(user);
				FXMLHandler.OpenSceneInStage((Stage) Sell_ButtonSellCsv.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
	    	FXMLHandler.ShowMessageBox("Fehler beim Lesen der .csv-Datei, der Vorgang wird abgebrochen. Es wurde kein Artikel inseriert.",
					"Fehler", "Fehler", AlertType.ERROR, true,
					false);
		} 
    }
    
    @FXML
    void Sell_ReturnButtonClick(ActionEvent event) {
    	MainScreenController.setUser(user);
    	FXMLHandler.OpenSceneInStage((Stage) Sell_ReturnButton.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
    }

}
//push