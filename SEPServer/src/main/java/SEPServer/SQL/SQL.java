package SEPServer.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import SEPCommon.Response;
import SEPCommon.User;
import SEPCommon.Product;

import java.sql.Date;

public class SQL{

	private boolean isConnected;
	
	public boolean connect() {
		isConnected=true;
		return true;
	}

	public Response registerUser(User user) {
		//Passwort, Emailvorgaben usw. werden clientseitig geprüft
		
		//wenn User erfolgreich registriert wurde Response.Success returnen
		//wenn Email vergeben: Response.Emailtaken returnen
		//wenn User vergeben: Response.UsernameTaken returnen
		//wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!isConnected)
		{
			connect();
		}
		
		return Response.Success;
	}

	public Response loginUser(String emailOrUsername, String password) {
		//wenn User erfolgreich eingeloggt wurde Response.Success returnen
		//wenn Daten nicht gefunden oder nicht übereinstimmen (nicht differenzieren) Response.Failure returnen
		//wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!isConnected)
		{
			connect();
		}
		
		return Response.Success;
	}

	public Response editUser(User user) {
		//User anhand ID in Datenbank finden und alle Werte mit den Werten des Objekts user überschreiben
		
		//Wenn User erfolgreich abgeändert wurden Response.Success returnen
		//wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		//wenn sonstiger Fehler auftritt ggf. Response.Failure returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!isConnected)
		{
			connect();
		}
		
		return Response.Success;
	}

	public Response deleteUser(User user) {
		//User anhand ID aus der Datenbank löschen
		
		//Wenn User erfolgreich gelöscht Response.Success returnen
		//wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		//wenn sonstiger Fehler auftritt ggf. Response.Failure returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!isConnected)
		{
			connect();
		}
		
		return Response.Success;
	}

	public Response increaseWallet(User user, double amount) {
		//Wallet anhand User-ID in der Datenbank um den Betrag amount erhöhen
		
		//Wenn Wallet erfolgreich erhöht Response.Success returnen
		//wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		//wenn sonstiger Fehler auftritt ggf. Response.Failure returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!isConnected)
		{
			connect();
		}
		
		return Response.Success;
	}

	public Response decreaseWallet(User user, double amount) {
		//Wallet anhand User-ID in der Datenbank um den Betrag amount vermindern
		
		//Wenn Wallet erfolgreich vermindert Response.Success returnen
		//wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		//wenn sonstiger Fehler auftritt ggf. Response.Failure returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!isConnected)
		{
			connect();
		}
		
		return Response.Success;
	}

	public Product[] fetchProducts() {
		//Alle in der DB vorhandenen Produkte in einem ProductArray ausgeben
		
		//Wenn erfolgreich gefetcht, Product-Array returnen
		//wenn keine Verbindung zu DB: null returnen
		//wenn sonstiger Fehler auftritt ggf. null returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!isConnected)
		{
			connect();
		}
		
		return null;
	}

	public Product[] fetchProductsByCategory(String category) {
		//Produkte mit der Kategorie category in der DB suchen und als Product-Array ausgeben
		
		//Wenn erfolgreich gefetcht, Product-Array returnen
		//wenn keine Verbindung zu DB: null returnen
		//wenn sonstiger Fehler auftritt ggf. null returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!isConnected)
		{
			connect();
		}
		
		return null;
	}

	public Product[] fetchProductsByString(String searchString) {
		//Produkte mit dem Begriff searchString im Namen, Beschreibung oder Kategorie in der DB suchen und als Product-Array ausgeben
		
		//Wenn erfolgreich gefetcht, Product-Array returnen
		//wenn keine Verbindung zu DB: null returnen
		//wenn sonstiger Fehler auftritt ggf. null returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!isConnected)
		{
			connect();
		}
		
		return null;
	}

	public Product[] fetchLastViewedProducts(User user) {
		//Zuletzt betrachtete Produkt-IDs des Users user aus der DB abfragen.
		//Anschließend Produkt-Array der betroffenen Produkt-IDs ausgeben
		
		//Wenn erfolgreich gefetcht, Product-Array returnen
		//wenn keine Verbindung zu DB: null returnen
		//wenn sonstiger Fehler auftritt (keine Produkte angesehen o.ä.) ggf. null returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!isConnected)
		{
			connect();
		}
		
		return null;
	}

	public Response addItem(User seller, Product product) {
		//Neues Produkt in der Datenbank anlegen. Die seller_id ist die ID des Objekts seller
		
		//Wenn Produkt erfolgreich angelegt, Response.Success returnen
		//wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		//wenn sonstiger Fehler auftritt ggf. Response.Failure returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!isConnected)
		{
			connect();
		}
		
		return Response.Success;
	}

	public Response addItems(User seller, Product[] products) {
		//Neue Produkte in der Datenbank anhand des Arrays products anlegen. Die seller_id ist die ID des Objekts seller
		
		//Wenn Produkte erfolgreich angelegt, Response.Success returnen
		//wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		//wenn sonstiger Fehler auftritt ggf. Response.Failure returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!isConnected)
		{
			connect();
		}
		
		return Response.Success;
	}

	public Response buyItem(User buyer, Product product) {
		//Neuen Datenbankeintrag in die Tabelle orders. buyer_id ist die ID vom Objekt buyer, die seller_id, Preis, Produktinfos können dem Objekt product entnommen werden
		
		//Wenn Produkte erfolgreich gekauft, Response.Success returnen
		//wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		//wenn sonstiger Fehler auftritt ggf. Response.Failure returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!isConnected)
		{
			connect();
		}
		
		return Response.Success;
	}

	public User getUserDataByEmail(String email) {
		//Anhand der email in der DB das entsprechende User-Objekt suchen und ein vollständiges User-Objekt mit id und allen anderen Werten aus der DB returnen
		
		//Wenn Userdaten erfolgreich gefetcht, User-Objekt returnen
		//wenn keine Verbindung zu DB: null returnen
		//wenn sonstiger Fehler auftritt ggf. null returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!isConnected)
		{
			connect();
		}
		
		return null;
	}

	public User getUserDataByUsername(String username) {
		//Anhand des username in der DB das entsprechende User-Objekt suchen und ein vollständiges User-Objekt mit id und allen anderen Werten aus der DB returnen
		
		//Wenn Userdaten erfolgreich gefetcht, User-Objekt returnen
		//wenn keine Verbindung zu DB: null returnen
		//wenn sonstiger Fehler auftritt ggf. null returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!isConnected)
		{
			connect();
		}
		
		return null;
	}
}
