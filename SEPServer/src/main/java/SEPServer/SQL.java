package SEPServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import SEPCommon.Response;
import SEPCommon.Seller;
import SEPCommon.ShippingType;
import SEPCommon.User;
import SEPCommon.Address;
import SEPCommon.Auction;
import SEPCommon.AuctionType;
import SEPCommon.Constants;
import SEPCommon.Customer;
import SEPCommon.Order;
import SEPCommon.Product;
import SEPCommon.Rating;

public class SQL {

	private boolean isConnected;
	private static Connection connection;			//Connection zum connecten mit DB
	
	private boolean connect() {
		try {
			connection = DriverManager.getConnection(Constants.SQLCONNECTIONSTRING, Constants.SQLUSER, null);
			isConnected = true;
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private Boolean checkConnection() {
		if (!isConnected) {
			connect();
			if (!isConnected) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	protected Response registerUser(User user) {
		// Die Methode ermöglicht eine Registrierung auf der Plattform
		// Passwort, Emailvorgaben usw. werden clientseitig geprüft
		
		// wenn User erfolgreich registriert wurde wird Response.Success zurück gegeben
		// wenn Email vergeben: Response.Emailtaken zurückgeben
		// wenn User vergeben: Response.UsernameTaken zurückgeben
		// wenn keine Verbindung zu DB: Response.NoDBConnection zurückgeben
		// wenn Bild zu groß: Response.ImageTooBig zurückgeben
		
		// Verbindung herstellen, wenn keine Verbindung besteht
		if (!checkConnection())
		{
			return Response.NoDBConnection;
		}
		
		if(user instanceof Seller)
		{
			// Registrierung Gewerbekunde
			
			// User-Objekt übergeben
			Seller seller = (Seller)user;
			
			// Prüfen, ob Email oder Username schon existieren
			try
			{
				// SQL Abfrage 
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email=?");
				// Für Email ? die übergebene Email einsetzten und schauen ob ein Eintrag schon vorliegt
				statement.setString(1, seller.getEmail());
				ResultSet emailQuery = statement.executeQuery();
				boolean emailHasEntries = emailQuery.next();
				if(emailHasEntries)
				{
					// 1. Fall: Email vergeben
					return Response.EmailTaken;
				}
			} catch (SQLException e) {
				// Fehlerfall dokumentieren und Fehlermeldung zurückgeben
				e.printStackTrace();
				return Response.NoDBConnection;
			}
			
			try
			{
				// SQL Abfrage 
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username=?");
				// Gleiche Prüfung für Username
				statement.setString(1, seller.getUsername());
				ResultSet usernameQuery = statement.executeQuery();
				boolean usernameHasEntries = usernameQuery.next();
				if(usernameHasEntries)
				{
					// 2. Fall: Username vergeben
					return Response.UsernameTaken;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return Response.NoDBConnection;
			}
			
			try {
				// Eintrag in Datenbank
				PreparedStatement stmt = connection.prepareStatement("INSERT INTO users(type,username,password,email,fullname,street,number,postalcode,city,country,image,wallet,companyname,lastviewed) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				
				// ? Values mit übergebenen Daten füllen (auch Adress wird gefüllt)
				stmt.setString(1, "Seller");
				stmt.setString(2, seller.getUsername());
				stmt.setString(3, seller.getPassword());
				stmt.setString(4, seller.getEmail());
				stmt.setString(5, seller.getAddress().getFullname());
				stmt.setString(6, seller.getAddress().getStreet());
				stmt.setString(7, seller.getAddress().getNumber());
				stmt.setInt(8, seller.getAddress().getZipcode());
				stmt.setString(9, seller.getAddress().getCity());
				stmt.setString(10, seller.getAddress().getCountry());
				stmt.setDouble(12, SEPCommon.Methods.round(seller.getWallet(), 2));
				stmt.setString(13, seller.getBusinessname());
				stmt.setString(14, ""); //keine bisher angesehenen Artikel
				
				//Bild einfügen (Bild ist optional)
				if(seller.getPicture()!=null)
				{
					stmt.setBytes(11, seller.getPicture());
				}
				else
				{
					// Seller möchte kein Profilbild
					stmt.setString(11, "");
				}
				stmt.execute();
                // 3. Fall: Erfolgreiche Registrierung Gewerbekunde
				return Response.Success;

			} catch (SQLException e) {
				// Ausnahme: Bilddatei passt nicht
				return Response.ImageTooBig;
			}
		}
		else
		{
			// Registrierung Privatkunde
			// User-Objekt übergeben
			Customer customer = (Customer)user;
			
			//Prüfen, ob Email oder Username schon existieren
			try
			{
				// SQL Abfrage 
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email=?");
				statement.setString(1, customer.getEmail());
				ResultSet emailQuery = statement.executeQuery();
				boolean emailHasEntries = emailQuery.next();
				if(emailHasEntries)
				{
					// 1. Fall: Email vergeben
					return Response.EmailTaken;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return Response.NoDBConnection;
			}
			
			try
			{
				// SQL Abfrage 
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username=?");
				statement.setString(1, customer.getUsername());
				ResultSet usernameQuery = statement.executeQuery();
				boolean usernameHasEntries = usernameQuery.next();
				if(usernameHasEntries)
				{
					// 2. Fall: Username vergeben
					return Response.UsernameTaken;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return Response.NoDBConnection;
			}
			
			try {
				// Eintrag in Datenbank
				PreparedStatement stmt = connection.prepareStatement(
						"INSERT INTO users(type,username,password,email,fullname,street,number,postalcode,city,country,image,wallet,companyname,lastviewed) "
								+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				
				
				// ? Values mit übergebenen Daten füllen (auch Adresse wird gefüllt)
				stmt.setString(1, "Customer");
				stmt.setString(2, customer.getUsername());
				stmt.setString(3, customer.getPassword());
				stmt.setString(4, customer.getEmail());
				stmt.setString(5, customer.getAddress().getFullname());
				stmt.setString(6, customer.getAddress().getStreet());
				stmt.setString(7, customer.getAddress().getNumber());
				stmt.setInt(8, customer.getAddress().getZipcode());
				stmt.setString(9, customer.getAddress().getCity());
				stmt.setString(10, customer.getAddress().getCountry());
				stmt.setDouble(12, SEPCommon.Methods.round(customer.getWallet(), 2));
				stmt.setString(13, ""); //kein Gewerbename
				stmt.setString(14, ""); //keine bisher angesehenen Artikel
				
				//Bild einfügen (ist optional)
				if(customer.getPicture()!=null)
				{
					// Seller möchte kein Profilbild
					stmt.setBytes(11, customer.getPicture());
				}
				else
				{
					stmt.setString(11, "");
				}
				
				stmt.execute();
				// 3. Fall: Erfolreich
				return Response.Success;
			} catch (SQLException e) {
				// Ausnahme: Bilddatei passt nicht
				e.printStackTrace();
				return Response.ImageTooBig;
			}
		}
	}

	protected Response loginUser(String emailOrUsername, String password) {
		// wenn User erfolgreich eingeloggt wurde Response.Success zurückgeben
		// wenn Username / Email nicht gefunden, oder wenn das eingegebene Passwort dazu nicht passt Response.Failure zurückgeben
		// wenn keine Verbindung zu DB: Response.NoDBConnection zurückgeben
		
		// Verbindung herstellen, wenn keine Verbindung besteht
		if (!checkConnection())
		{
			return Response.NoDBConnection;
		}
		
		// Email muss ein @ Symbol enthalten und kann dadurch von Username unterschieden werden
		if(emailOrUsername.contains("@"))
		{
			// anmelden mit Email
			try
			{
				// SQL Abfrage
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email=? AND password=?");
				// Email und Passwort übergeben
				statement.setString(1, emailOrUsername);
				statement.setString(2, password);
				ResultSet loginQuery = statement.executeQuery();
				
				// Eingabedaten prüfen
				boolean hasEntries = loginQuery.next();
				if(hasEntries)
				{
					// Login erfolreich
					return Response.Success;
				}
				else
				{
					// Login fehlgeschlagen
					return Response.Failure;
				}
			} catch (SQLException e) {
				// Fehler abfangen
				e.printStackTrace();
				return Response.NoDBConnection;
			}
		}
		else
		{
			//anmelden mit Benutzernamen
			try
			{
				// SQL Abfrage
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
				// Username und Üasswort übergeben
				statement.setString(1, emailOrUsername);
				statement.setString(2, password);
				ResultSet loginQuery = statement.executeQuery();
				
				// Eingabedaten prüfen
				boolean hasEntries = loginQuery.next();
				if(hasEntries)
				{
					// Login erfolreich
					return Response.Success;
				}
				else
				{
					// Login fehlerhaft
					return Response.Failure;
				}
			} catch (SQLException e) {
				// Fehler abfangen
				e.printStackTrace();
				return Response.NoDBConnection;
			}
		}
	}
	
	protected User getUserDataByEmail(String email) {
		// Login kann mit Email oder Username erfolgen
		// Anhand der Email in der DB das entsprechende User-Objekt suchen und ein vollständiges User-Objekt mit Id und allen anderen Werten aus der DB zurückgeben
		
		// Wenn Userdaten erfolgreich gefetcht, User-Objekt zurückgeben
		// wenn keine Verbindung zu DB: null zurückgeben
		// wenn sonstiger Fehler auftritt ggf. null zurückgeben
		
		// Verbindung herstellen, wenn keine Verbindung besteht
		if (!checkConnection())
		{
			return null;
		}
		
		try
		{
			// SQL Abfrage
			PreparedStatement userDataStatement = connection.prepareStatement("SELECT * FROM users WHERE email=?");
			
			// ? Values füllen
			userDataStatement.setString(1, email);
			ResultSet userDataQuery = userDataStatement.executeQuery();
			
			// Email prüfen
			if(userDataQuery.next())
			{
				// Privat- oder Gewerbekunde?
				String accountType = userDataQuery.getString("type");
				Address address = new Address(userDataQuery.getString("fullname"), userDataQuery.getString("country"), userDataQuery.getInt("postalcode"), userDataQuery.getString("city"), userDataQuery.getString("street"), userDataQuery.getString("number"));
				
				// Privatkunde
				if(accountType.equals("Customer"))
				{
					Customer customer = new Customer(userDataQuery.getInt("id"), userDataQuery.getString("username"), userDataQuery.getString("email"), userDataQuery.getString("password"), userDataQuery.getBytes("image"), userDataQuery.getDouble("wallet"), address);
					// Privatkunden-Obejekt zurückgeben
					return customer;
				}
				
				// Gewerbekunde
				else if(accountType.equals("Seller"))
				{
					Seller seller = new Seller(userDataQuery.getInt("id"), userDataQuery.getString("username"), userDataQuery.getString("email"), userDataQuery.getString("password"), userDataQuery.getBytes("image"), userDataQuery.getDouble("wallet"), address, userDataQuery.getString("companyname"));
					// Gewerbekunden-Objekt zurückgeben
					return seller;
				}
				// Kein Eintrag zur Email
				else
				{
					return null;
				}
			}
			return null;
		} catch (SQLException e) {
			// Fehler abfangen
			e.printStackTrace();
			return null;
		}
	}

	protected User getUserDataByUsername(String username){
		// Login kann mit Email oder Username erfolgen
		// Analog zur vorherigen Methode: Anhand des Username in der DB das entsprechende User-Objekt suchen und ein vollständiges User-Objekt mit Id und allen anderen Werten aus der DB zurückgeben
		
		// Wenn Userdaten erfolgreich gefetcht, User-Objekt zurückgeben
		// wenn keine Verbindung zu DB: null zurückgeben
		// wenn sonstiger Fehler auftritt ggf. null zurückgeben
		
		// Verbindung herstellen, wenn keine Verbindung besteht
		if (!checkConnection())
		{
			return null;
		}
		
		try
		{
			// SQL Abfrage
			PreparedStatement userDataStatement = connection.prepareStatement("SELECT * FROM users WHERE username=?");


			// ? Values füllen
			userDataStatement.setString(1, username);
			ResultSet userDataQuery = userDataStatement.executeQuery();
			
			if(userDataQuery.next())
			{
				// Privat- oder Gewerbekunde?
				String accountType = userDataQuery.getString("type");
				Address address = new Address(userDataQuery.getString("fullname"), userDataQuery.getString("country"), userDataQuery.getInt("postalcode"), userDataQuery.getString("city"), userDataQuery.getString("street"), userDataQuery.getString("number"));
				
				// Privatkunde
				if(accountType.equals("Customer"))
				{
					Customer customer = new Customer(userDataQuery.getInt("id"), userDataQuery.getString("username"), userDataQuery.getString("email"), userDataQuery.getString("password"), userDataQuery.getBytes("image"), SEPCommon.Methods.round(userDataQuery.getDouble("wallet"), 2), address);
					// Privatkunden-Objekt zurückgeben
					return customer;
				}
				
				// Gewerbekunde
				else if(accountType.equals("Seller"))
				{
					Seller seller = new Seller(userDataQuery.getInt("id"), userDataQuery.getString("username"), userDataQuery.getString("email"), userDataQuery.getString("password"), userDataQuery.getBytes("image"), SEPCommon.Methods.round(userDataQuery.getDouble("wallet"), 2), address, userDataQuery.getString("companyname"));
					// Gewerbekunden-Objekt zurückgeben
					return seller;
				}
				else
				{
					// Kein Eintrag zum Username
					return null;
				}
			}
			return null;
		} catch (SQLException e) {
			// Fehler abfangen
			e.printStackTrace(); 
			return null; 
		} 
	}

	protected Response editUser(User user) {
		// User anhand ID in Datenbank finden und alle Werte mit den Werten des Objekts users überschreiben 
 
		// Wenn User erfolgreich abgeändert wurden Response.Success zurückgeben
		// wenn keine Verbindung zu DB: Response.NoDBConnection zurückgeben
		// wenn sonstiger Fehler auftritt ggf. Response.Failure zurückgeben

		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!checkConnection())
		{
			return Response.NoDBConnection;
		}
		// User Id speichern
		int userId = user.getId();
		
		if(user instanceof Seller)
		{
			//Gewerbekunde
			Seller seller = (Seller)user;
					
			// Prüfen, ob Email oder Username bei einem anderen Benutzer schon existieren, da diese beiden unique sind
			try
			{
				// SQL Abfrage 
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email=?");
				// ? Values füllen
				statement.setString(1, seller.getEmail());
				ResultSet emailQuery = statement.executeQuery();
				boolean emailHasEntries = emailQuery.next();
				if(emailHasEntries)
				{
					//Prüfen, ob Email für diesen User oder für anderen vergeben
					if(emailQuery.getInt("id") != userId)
					{
						//anderer Benutzer verwendet die Email
						// 1. Fall: Email vergeben
						return Response.EmailTaken;
					}
				}
			} catch (SQLException e) {
				// Fehler abfangen
				e.printStackTrace();
				return Response.NoDBConnection;
			}
			
			try
			{
				// SQL Abfrage 
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username=?");
				// ? Values füllen
				statement.setString(1, seller.getUsername());
				ResultSet usernameQuery = statement.executeQuery();
				boolean usernameHasEntries = usernameQuery.next();
				if(usernameHasEntries)
				{
					//Prüfen, ob Email für diesen User oder für anderen vergeben
					if(usernameQuery.getInt("id") != userId)
					{
						//anderer Benutzer verwendet den Benutzernamen
						// 2. Fall: Benutzername vergeben
						return Response.UsernameTaken;
					}
				}
			} catch (SQLException e) {
				// Fehler abfangen
				e.printStackTrace();
				return Response.NoDBConnection;
			}
			
			//Eintrag in Datenbank
			try {
				PreparedStatement stmt;
				stmt = connection.prepareStatement("UPDATE users "
						+ "SET username = ?, password = ?, email = ?, fullname = ?, street = ?, number = ?, "
						+ "postalcode = ?, city = ?, country = ?, image = ?, wallet = ?, companyname = ? "
						+ "WHERE id=" + userId);
				// Neue Angaben bekommen
				stmt.setString(1, seller.getUsername());
				stmt.setString(2, seller.getPassword());
				stmt.setString(3, seller.getEmail());
				stmt.setString(4, seller.getAddress().getFullname());
				stmt.setString(5, seller.getAddress().getStreet());
				stmt.setString(6, seller.getAddress().getNumber());
				stmt.setInt(7, seller.getAddress().getZipcode());
				stmt.setString(8, seller.getAddress().getCity());
				stmt.setString(9, seller.getAddress().getCountry());
				stmt.setDouble(11, SEPCommon.Methods.round(seller.getWallet(), 2));
				stmt.setString(12, seller.getBusinessname());
				
				// Bild ist optional
				if(seller.getPicture()!=null)
				{
					stmt.setBytes(10, seller.getPicture());
				}
				else
				{
					stmt.setString(10, "");
				}
				stmt.execute();

				return Response.Success;

			} catch (SQLException e) {
				// Fehler abfangen
				e.printStackTrace();
				return Response.ImageTooBig;
			} 
		}
		
		else
		{
			//Privatkunde
			Customer customer = (Customer)user;
			
			// Prüfen, ob Email oder Username bei einem anderen Benutzer schon existieren, da diese beiden unique sind
			try
			{
				// SQL Abfrage 
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email=?");
				// ? Values füllen
				statement.setString(1, customer.getEmail());
				ResultSet emailQuery = statement.executeQuery();
				boolean emailHasEntries = emailQuery.next();
				if(emailHasEntries)
				{
					//Prüfen, ob Email für diesen User oder für anderen vergeben
					if(emailQuery.getInt("id") != userId)
					{
						//anderer Benutzer verwendet die Email
						// 1. Fall: Email vergeben
						return Response.EmailTaken;
					}
				}
			} catch (SQLException e) {
				// Fehler abfangen
				e.printStackTrace();
				return Response.NoDBConnection;
			}
			
			try
			{
				// SQL Abfrage 
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username=?");
				// ? Values füllen
				statement.setString(1, customer.getUsername());
				ResultSet usernameQuery = statement.executeQuery();
				boolean usernameHasEntries = usernameQuery.next();
				if(usernameHasEntries)
				{
					//Prüfen, ob Email für diesen User oder für anderen vergeben
					if(usernameQuery.getInt("id") != userId)
					{
						//anderer Benutzer verwendet den Benutzernamen
						// 2. Fall: Benutzername vergeben
						return Response.UsernameTaken;
					}
				}
			} catch (SQLException e) {
				// Fehler abfangen
				e.printStackTrace();
				return Response.NoDBConnection;
			}
			
			//Eintrag in Datenbank
			try {
				PreparedStatement stmt;
				stmt = connection.prepareStatement("UPDATE users "
						+ "SET username = ?, password = ?, email = ?, fullname = ?, street = ?, number = ?, "
						+ "postalcode = ?, city = ?, country = ?, image = ?, wallet = ? "
						+ "WHERE id=" + userId);
				// Neue Angaben bekommen
				stmt.setString(1, customer.getUsername());
				stmt.setString(2, customer.getPassword());
				stmt.setString(3, customer.getEmail());
				stmt.setString(4, customer.getAddress().getFullname());
				stmt.setString(5, customer.getAddress().getStreet());
				stmt.setString(6, customer.getAddress().getNumber());
				stmt.setInt(7, customer.getAddress().getZipcode());
				stmt.setString(8, customer.getAddress().getCity());
				stmt.setString(9, customer.getAddress().getCountry());
				stmt.setDouble(11, SEPCommon.Methods.round(customer.getWallet(), 2));
				
				// Bild ist optional
				if(customer.getPicture()!=null)
				{
					stmt.setBytes(10, customer.getPicture());
				}
				else
				{
					stmt.setString(10, "");
				}
				stmt.execute();

				return Response.Success;
			} catch (SQLException e) {
				// Fehler abfangen
				e.printStackTrace();
				return Response.ImageTooBig;
			}
		}
	}  

	protected Response deleteUser(User user) {
		// User anhand ID aus der Datenbank löschen

		// Wenn User erfolgreich gelöscht Response.Success zurückgeben
		// wenn keine Verbindung zu DB: Response.NoDBConnection zurückgeben
		// Verbindung herstellen, wenn keine Verbindung besteht
		
		// User Id speichern 
		int userId = user.getId();
		
		if (!checkConnection()) {
			return Response.NoDBConnection;
		}
		
		// Gewerbekunde
		if(user instanceof Seller)
		{ 
			try
			{
					// Zuerst Produkte des Anbieters und dann den Anbieter selbst löschen
					Statement statement = connection.createStatement();
					statement.execute("DELETE FROM products WHERE seller_id ='" + userId + "'");
					statement.execute("DELETE FROM users WHERE id ='" + userId + "'");
					return Response.Success;
		   
			} catch (SQLException e) {
				// Fehler zurückgeben
				return Response.Failure;
			}
		}
		
		// Privatkunde
		else 
		{
				try
				{
					Statement statement = connection.createStatement();
					// Bei Privatkunden muss nur der User selbst gelöscht werden
					statement.execute("DELETE FROM users WHERE id ='" + userId + "'");
					return Response.Success;
				
				} catch (SQLException e) {
					// Fehler zurückgeben
					return Response.Failure;
				}
		}
	}

	protected Response increaseWallet(User user, double MoreMoney) {
		// Wallet anhand User-ID in der Datenbank um den Betrag amount erhöhen

		// Wenn Wallet erfolgreich erhöht Response.Success returnen
		// wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		// wenn sonstiger Fehler auftritt ggf. Response.Failure returnen

		// Verbindung herstellen, wenn keine Verbindung besteht
		int userId = user.getId();
		
		String getCurrentWalletQuery = "SELECT wallet FROM users WHERE id='" + userId + "'";

		if (!checkConnection()) {
			return Response.NoDBConnection;
		}
		
		try {
			double wallettemp = 0;
			Statement statement = connection.createStatement();
			ResultSet walletSet = statement.executeQuery(getCurrentWalletQuery);
			if(walletSet.next())
			{
				wallettemp = walletSet.getDouble("wallet");
			}
			
			double newBalance = wallettemp + MoreMoney;
			
			String increaseWalletQuery = "UPDATE users SET wallet='" + SEPCommon.Methods.round(newBalance, 2) + "' WHERE id=" + userId;
			statement.execute(increaseWalletQuery);

			return Response.Success;
		} catch (SQLException e) {

			return Response.NoDBConnection;
		}
	}

	protected Response decreaseWallet(User user, double lessMoney) {
		// Wallet anhand User-ID in der Datenbank um den Betrag amount vermindern

		// Wenn Wallet erfolgreich vermindert Response.Success returnen
		// wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		// wenn sonstiger Fehler auftritt ggf. Response.Failure returnen

		// Verbindung herstellen, wenn keine Verbindung besteht
		int userId = user.getId();
		
		String getCurrentWalletQuery = "SELECT wallet FROM users WHERE id='" + userId + "'";

		if (!checkConnection()) {
			return Response.NoDBConnection;
		}
		
		try {
			double wallettemp = 0;
			Statement statement = connection.createStatement();
			ResultSet walletSet = statement.executeQuery(getCurrentWalletQuery);
			if(walletSet.next())
			{
				wallettemp = walletSet.getDouble("wallet");
			}
			
			double newBalance = wallettemp - lessMoney;
			
			String increaseWalletQuery = "UPDATE users SET wallet='" + SEPCommon.Methods.round(newBalance, 2) + "' WHERE id=" + userId;
			statement.execute(increaseWalletQuery);

			return Response.Success;
		} catch (SQLException e) {

			return Response.NoDBConnection;
		}
	}

	protected Product[] fetchAllProducts() {															//ProductArray aufgerufen in Mainscreen
		// Alle in der DB vorhandenen Produkte in einem ProductArray ausgeben

		// Wenn erfolgreich gefetcht, Product-Array returnen
		// wenn keine Verbindung zu DB: null returnen
		// wenn sonstiger Fehler auftritt ggf. null returnen

		// Verbindung herstellen, wenn keine Verbindung besteht

		if (!checkConnection()) {
		

			return null;
		}
		try {
			PreparedStatement pstmt = connection.prepareStatement("SELECT * \r\n" 
					+ "FROM products\r\n" 
					+ "JOIN categories\r\n"
					+ "ON categories.id=products.category_id\r\n"
					+ "JOIN users\r\n"
					+ "ON users.id=products.seller_id");
			ResultSet AllProducts = pstmt.executeQuery();

			int arrayCounter = 0;
			int sqlcounter = 0;
			while (AllProducts.next()) {  //Tupel Zählen
				sqlcounter++;
			}
			ResultSet AllProducts2= pstmt.executeQuery(); // nach der 1 Schleife pointer zeigt auf Null -> ggf könnte man pointer resetten
			

			Product[] allProducts = new Product[sqlcounter];
			
			while (AllProducts2.next()) {

				Address newAddress = new Address(AllProducts2.getString("users.fullname"),
						AllProducts2.getString("users.country"), AllProducts2.getInt("users.postalcode"),
						AllProducts2.getString("users.city"), AllProducts2.getString("users.street"),
						AllProducts2.getString("users.number"));
				Seller newSeller = new Seller(AllProducts2.getInt("users.id"), AllProducts2.getString("users.username"),
						AllProducts2.getString("users.email"), AllProducts2.getString("users.password"),
						AllProducts2.getBytes("users.image"), AllProducts2.getDouble("users.wallet"), newAddress,
						AllProducts2.getString("users.companyname"));
				allProducts[arrayCounter] = new Product(AllProducts2.getInt("products.id"),
						AllProducts2.getString("products.title"), AllProducts2.getDouble("products.price"), newSeller,
						AllProducts2.getString("categories.title"), AllProducts2.getString("products.description"));

				arrayCounter++;
			}
			return allProducts;

		} catch (SQLException e) {
			
			return null;
		}

	}

	protected Product[] fetchProductsByCategory(String category) { //Array ausgelesen in Mainscreen
		// Produkte mit der Kategorie category in der DB suchen und als Product-Array
		// ausgeben

		// Wenn erfolgreich gefetcht, Product-Array returnen
		// wenn keine Verbindung zu DB: null returnen
		// wenn sonstiger Fehler auftritt ggf. null returnen

		// Verbindung herstellen, wenn keine Verbindung besteht
		if (!checkConnection()) {
			return null;
		}
		
		
		try {
			int arrayCounter = 0;
			int sqlcounter = 0;
			PreparedStatement statement;
			statement = connection.prepareStatement("SELECT *\r\n" 
			 		   + "FROM products \r\n" 
			 		   + "JOIN categories\r\n"
			 		   + "ON (products.category_ID = categories.ID)\r\n" 
			 		   + "JOIN users\r\n"
			 		   + "ON users.id=products.seller_id\r\n"
			 		   + "WHERE categories.title=?");
			statement.setString(1, category);		
			ResultSet AllProductsByCategory = statement.executeQuery();

			while (AllProductsByCategory.next()) {
				sqlcounter++;
			}

			ResultSet AllProductsByCategory2 = statement.executeQuery(); // nach der 1 Schleife pointer zeigt auf Null -> ggf könnte man pointer resetten, NCAR
			

			Product[] allProductsSameCategory = new Product[sqlcounter];

			while (AllProductsByCategory2.next()) {
				
				
				Address newAddress = new Address(AllProductsByCategory2.getString("users.fullname"),
						AllProductsByCategory2.getString("users.country"), AllProductsByCategory2.getInt("users.postalcode"),
						AllProductsByCategory2.getString("users.city"), AllProductsByCategory2.getString("users.street"),
						AllProductsByCategory2.getString("users.number"));
				Seller newSeller = new Seller(AllProductsByCategory2.getInt("users.id"), AllProductsByCategory2.getString("users.username"),
						AllProductsByCategory2.getString("users.email"), AllProductsByCategory2.getString("users.password"),
						AllProductsByCategory2.getBytes("users.image"), AllProductsByCategory2.getDouble("users.wallet"), newAddress,
						AllProductsByCategory2.getString("users.companyname"));
				allProductsSameCategory[arrayCounter] = new Product(AllProductsByCategory2.getInt("products.id"),
						AllProductsByCategory2.getString("products.title"), AllProductsByCategory2.getDouble("products.price"), newSeller,
						AllProductsByCategory2.getString("categories.title"), AllProductsByCategory2.getString("products.description"));
				arrayCounter++;
			}

			return allProductsSameCategory;
		} catch (SQLException e) {
		
			return null;
		}

	}

	protected Product[] fetchProductsByString(String searchString) {
		// Produkte mit dem Begriff searchString im Namen, Beschreibung oder Kategorie
		// in der DB suchen und als Product-Array ausgeben

		// Wenn erfolgreich gefetcht, Product-Array returnen
		// wenn keine Verbindung zu DB: null returnen
		// wenn sonstiger Fehler auftritt ggf. null returnen

		// Verbindung herstellen, wenn keine Verbindung besteht
		
		if (!checkConnection()) {
			return null;
		}
		try {
			int arrayCounter = 0;
			int sqlcounter = 0;
			PreparedStatement pstmt;
			pstmt = connection.prepareStatement("SELECT * \r\n"
					 + "FROM Products\r\n"
					 + "JOIN Categories\r\n"
					 + "ON (Products.category_ID = Categories.ID)\r\n"
					 + "JOIN users\r\n"
			 		 + "ON users.id=products.seller_id\r\n"
					 + "WHERE Products.Title LIKE ?");				//? Wildcard
			// + "OR Products.Description LIKE"+ searchString+ "%\r\n"
			// + "OR Categories.Title LIKE" + searchString+"%\r\n";
			pstmt.setString(1,"%"+ searchString+"%");				//1, erstes Wildcard
			ResultSet AllProductsByFullString = pstmt.executeQuery();

			while (AllProductsByFullString.next()) {
				sqlcounter++;
			}
			
			ResultSet AllProductsByFullString2 = pstmt.executeQuery(); // nach der 1 Schleife pointer zeigt auf Null -> ggf könnte man pointer resetten(?) NCAR
			
			
			Product[] allProductsByString = new Product[sqlcounter];
			while (AllProductsByFullString2.next()) {
				Address newAddress = new Address(AllProductsByFullString2.getString("users.fullname"),
						AllProductsByFullString2.getString("users.country"), AllProductsByFullString2.getInt("users.postalcode"),
						AllProductsByFullString2.getString("users.city"), AllProductsByFullString2.getString("users.street"),
						AllProductsByFullString2.getString("users.number"));
				Seller newSeller = new Seller(AllProductsByFullString2.getInt("users.id"), AllProductsByFullString2.getString("users.username"),
						AllProductsByFullString2.getString("users.email"), AllProductsByFullString2.getString("users.password"),
						AllProductsByFullString2.getBytes("users.image"), AllProductsByFullString2.getDouble("users.wallet"), newAddress,
						AllProductsByFullString2.getString("users.companyname"));
				allProductsByString[arrayCounter] = new Product(AllProductsByFullString2.getInt("products.id"),
						AllProductsByFullString2.getString("products.title"), AllProductsByFullString2.getDouble("products.price"), newSeller,
						AllProductsByFullString2.getString("categories.title"), AllProductsByFullString2.getString("products.description"));
				arrayCounter++;
			}
			return allProductsByString;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	protected Product[] fetchLastViewedProducts(User user) {
		//Zuletzt betrachtete Produkt-IDs des Users user aus der DB abfragen.
		//Anschließend Produkt-Array der betroffenen Produkt-IDs ausgeben
		
		//Wenn erfolgreich gefetcht, Product-Array returnen
		//wenn keine Verbindung zu DB: null returnen
		//wenn sonstiger Fehler auftritt (keine Produkte angesehen o.ä.) ggf. null returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!checkConnection())
		{
			return null;
		}
		
		Product[] lastViewedProducts;
		
		try {
			PreparedStatement fetchLastViewedProductIds = connection.prepareStatement("SELECT lastviewed FROM users WHERE id='" + user.getId() + "'");
			ResultSet fetchLastViewedProductIdsResult = fetchLastViewedProductIds.executeQuery();
			
			
			if(fetchLastViewedProductIdsResult.next()) //wenn eine Spalte lastviewed gefunden wurde
			{
				String lastviewed = fetchLastViewedProductIdsResult.getString("lastviewed");
				if(lastviewed=="" || lastviewed==null || lastviewed.isEmpty() || lastviewed.isBlank())
					return null; //keine Produkte bisher geviewed
				
				String[] lastViewedIds = lastviewed.split(","); //in der DB sind die IDs durch , seppariert, daher splitten und Array der IDs erstellen
				lastViewedProducts = new Product[lastViewedIds.length]; //Rückgabearray mit Größe der Anzahl der IDs im Array
				
				int newArrayCounter = 0;
				for(String viewedIdStr : lastViewedIds)
				{
					try {
					int viewedId = Integer.parseInt(viewedIdStr);
					
					//Für jede ID im Array lastViewedIds, die Produkdaten aus der DB holen
					//anschließend jeweils ein Product-Object anhand der gefetchten Daten aus der DB erstellen
					//und in das Array lastViewedProducts, welches am Ende zurückgegeben wird schreiben
					PreparedStatement fetchProductInfo = connection.prepareStatement("SELECT * FROM products JOIN users ON (products.seller_id=users.id) JOIN categories ON (products.category_id = categories.id) WHERE products.id='" + viewedId + "'");
					ResultSet fetchProductsInfoResult = fetchProductInfo.executeQuery();
					if(fetchProductsInfoResult.next())
					{
						Address address = new Address(fetchProductsInfoResult.getString("users.fullname"),
								fetchProductsInfoResult.getString("users.country"), fetchProductsInfoResult.getInt("users.postalcode"),
								fetchProductsInfoResult.getString("users.city"), fetchProductsInfoResult.getString("users.street"),
								fetchProductsInfoResult.getString("users.number"));
						Seller seller = new Seller(fetchProductsInfoResult.getInt("users.id"), fetchProductsInfoResult.getString("users.username"),
								fetchProductsInfoResult.getString("users.email"), fetchProductsInfoResult.getString("users.password"),
								fetchProductsInfoResult.getBytes("users.image"), fetchProductsInfoResult.getDouble("users.wallet"), address,
								fetchProductsInfoResult.getString("users.companyname"));
						
						Product product = new Product(viewedId, fetchProductsInfoResult.getString("products.title"),
								fetchProductsInfoResult.getDouble("products.price"), seller,
								fetchProductsInfoResult.getString("categories.title"),fetchProductsInfoResult.getString("products.description"));
						lastViewedProducts[newArrayCounter] = product;
					}
					newArrayCounter++;
					} catch (NumberFormatException e) {
						//Produkt mittlerweile gelöscht
						//ignorieren, für diese ID kein Produkt (null) in das Array schreiben.
						lastViewedProducts[newArrayCounter] = null;
					}
				}
				return lastViewedProducts;
			}
			else
			{
				//kein Entry mit der UserId - eigentlich nicht möglich.
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Response addLastViewedProduct(int viewedProductId, User user) {
		//viewedProductId zu zuletzt betrachtete Produkt-IDs des Users user in der DB hinzufügen (max. 10 zuletzt betrachtete IDs).
		
		//Wenn erfolgreich hinzugefügt, Response.Success returnen
		//wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		//wenn sonstiger Fehler auftritt ggf. Response.Failure returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!checkConnection())
		{
			return Response.NoDBConnection;
		}
		
		//Aktuelle zuletzt angesehene Produkte holen, um zu entscheiden, ob eines ersetzt werden muss oder nur hinzugefügt werden muss
		Product[] currentLastViewedProducts = fetchLastViewedProducts(user);
		
		String newLastViewedProductsString = "";
		if(currentLastViewedProducts!=null)
		{
			if(currentLastViewedProducts.length==10)
			{
				//Maximale Lünge (10), setze viewedProductId an den Anfang und ersetze die letzte Id
				newLastViewedProductsString += String.valueOf(viewedProductId);
				
				for(int i=0;i<9;i++)
				{
					if(currentLastViewedProducts[i]!=null)
					{
						//wenn currentLastViewedProducts[i] = null, ist das Produkt gelöscht. Dann ist es aus der Liste der zuletzt aufgerufenen Produkte zu entfernen
						newLastViewedProductsString += "," + String.valueOf(currentLastViewedProducts[i].getId());
					}
				}
			}
			else
			{
				//Maximale Länge (10) noch nicht erreicht, setze viewedProductId an den Anfang und schiebe ggf. die anderen ein Feld nach hinten
				newLastViewedProductsString += String.valueOf(viewedProductId);
				
				for(int i=0;i<currentLastViewedProducts.length;i++)
				{
					if(currentLastViewedProducts[i]!=null)
					{
						//wenn currentLastViewedProducts[i] = null, ist das Produkt gelöscht. Dann ist es aus der Liste der zuletzt aufgerufenen Produkte zu entfernen
						newLastViewedProductsString += "," + String.valueOf(currentLastViewedProducts[i].getId());
					}
				}
			}
		}
		else
		{
			//Keine zuletzt angesehenen Produkte gespeichert oder Fehler
			newLastViewedProductsString += String.valueOf(viewedProductId);
		}
		
		try {
			PreparedStatement updateLastViewedProductIds = connection.prepareStatement("UPDATE users SET lastviewed='" + newLastViewedProductsString
					+ "' WHERE id='" + user.getId() + "'");
			updateLastViewedProductIds.execute();
			return Response.Success;
		} catch (SQLException e) {
			//Fehler aufgetreten
			e.printStackTrace();
			return Response.Failure;
		}
	}

	protected Response addItem(User seller, Product product) {
		// Neues Produkt in der Datenbank anlegen. Die seller_id ist die ID des Objekts seller

		// Wenn Produkt erfolgreich angelegt, Response.Success returnen
		// wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		// wenn sonstiger Fehler auftritt ggf. Response.Failure returnen

		// Verbindung herstellen, wenn keine Verbindung besteht
		if (!checkConnection()) {
			return Response.NoDBConnection;
		}
		
		int categoryid;
		
		//zunächst überprüfen, ob Kategorie bereits existiert:
		try {
			PreparedStatement selectCategoryID = connection.prepareStatement("SELECT id, title FROM categories WHERE title=?");
			selectCategoryID.setString(1, product.getCategory());
			ResultSet selectCategoryIDResult = selectCategoryID.executeQuery();
			if (selectCategoryIDResult.next()) {
				//wenn Kategorie bereits existiert, speichere ID in der Variable categoryid
				categoryid = selectCategoryIDResult.getInt("id");
			} 
			else {
				//wenn Kategorie noch nicht existiert, muss erst eine neue Kategorie angelegt werden
				PreparedStatement createCategory = connection.prepareStatement("INSERT INTO categories(title) "
						+ "VALUES(?)");
				createCategory.setString(1, product.getCategory());
				createCategory.execute();
				
				//nachdem Kategorie erstellt wurde, kann wie vorher vorgegangen werden
				selectCategoryIDResult = selectCategoryID.executeQuery();
				if (selectCategoryIDResult.next()) {
					categoryid = selectCategoryIDResult.getInt("id");
				} else {
					return Response.Failure;
				}
			}
			//jetzt kann Produkt angelegt werden
			PreparedStatement insertProduct = connection.prepareStatement("INSERT INTO products(seller_id, title, price, category_id, description) "
					+ "VALUES (?, ?, ?, ?, ?)");
		
			insertProduct.setInt(1, seller.getId()); 
			insertProduct.setString(2,  product.getName()); 
			insertProduct.setDouble(3,  SEPCommon.Methods.round(product.getPrice(), 2));
			insertProduct.setInt(4, categoryid);
			insertProduct.setString(5, product.getDescription());
			insertProduct.execute();
			return Response.Success;		
			
		} catch (SQLException e) {
			//es ist ein Fehler aufgetreten:
			e.printStackTrace();
			return Response.Failure;
		}
	}

	protected Response addItems(User seller, Product[] products) {
		//Neue Produkte in der Datenbank anhand des Arrays products anlegen. Die seller_id ist die ID des Objekts seller
		
		//Wenn alle Produkte erfolgreich angelegt, Response.Success returnen
		//wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		//wenn sonstiger Fehler auftritt ggf. Response.Failure returnen
		//wenn nur ein Teil der Produkte angelegt wird ggf. Response.Failure returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		
		if (!checkConnection())
		{
			//Fehler beim Herstellen der DB-Verbindung
			return Response.NoDBConnection;
		}
		
		//Ungültigkeit usw. wird clientseitig geprüft
		
		int sellerid = seller.getId();
		
		for(Product p : products)
		{
			//Für jedes Produkt p prüfen ob Kategorie existiert, wenn ja ID auslesen, ansonsten Kategorie anlegen
			int categoryid;
			
			try {
				PreparedStatement selectCategoryID;
				selectCategoryID = connection.prepareStatement("SELECT id, title FROM categories "
						+ "WHERE title=?");
				selectCategoryID.setString(1, p.getCategory());
				ResultSet selectCategoryIDResult = selectCategoryID.executeQuery();
				if(selectCategoryIDResult.next())
				{
					//Kategorie existiert bereits, schreibe ID in Variable categoryid
					categoryid = selectCategoryIDResult.getInt("id");
				}
				else
				{
					//Kategorie existiert noch nicht
					//Lege Kategorie an
					PreparedStatement createCategory;
					createCategory = connection.prepareStatement("INSERT INTO categories(title) "
							+ "VALUES(?)");
					createCategory.setString(1, p.getCategory());
					createCategory.execute();
					
					//ID nach Anlegen der Kategorie auslesen (Query selectCategoryID erneut ausführen)
					selectCategoryIDResult = selectCategoryID.executeQuery();
					if(selectCategoryIDResult.next())
					{
						categoryid = selectCategoryIDResult.getInt("id");
					}
					else
					{
						//Kategorie existiert immer noch nicht (sollte nicht auftreten, da schon eine Exception aufgetreten wäre)
						return Response.Failure;
					}
				}
				
				//Produkt p anlegen
				PreparedStatement insertProduct;
				insertProduct = connection.prepareStatement("INSERT INTO products(seller_id, title, price, category_id, description) "
							+ "VALUES (?, ?, ?, ?, ?)");
				
				insertProduct.setInt(1, sellerid); //An Stelle des 1. ? setzen
				insertProduct.setString(2,  p.getName()); // ...
				insertProduct.setDouble(3,  SEPCommon.Methods.round(p.getPrice(), 2));
				insertProduct.setInt(4, categoryid);
				insertProduct.setString(5, p.getDescription());
				insertProduct.execute();
			} catch (SQLException e) {
				e.printStackTrace();
				//Fehler aufgetreten
				return Response.Failure;
			}
		}
		return Response.Success;
	}

	protected Response buyItem(User buyer, Product product) {
		// Neuen Datenbankeintrag in die Tabelle orders. buyer_id ist die ID vom Objekt
		// buyer, die seller_id, Preis, Produktinfos können dem Objekt product entnommen werden

		// Wenn Produkte erfolgreich gekauft, Response.Success returnen
		// wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		// wenn nicht genug Guthaben: Response.InsufficientBalance returnen
		// wenn sonstiger Fehler auftritt ggf. Response.Failure returnen

		// Verbindung herstellen, wenn keine Verbindung besteht

		int buyerid = buyer.getId();
		Seller seller = product.getSeller();
		int sellerid = seller.getId();
		String newOrder = "INSERT INTO orders(product_id, seller_id, buyer_id, price)" + " VALUES('" + product.getId() + "', '" + sellerid + "', '"
				+ buyerid + "', '" + SEPCommon.Methods.round(product.getPrice(), 2) + "')";

		String getCurrentWalletQuery = "SELECT wallet FROM users WHERE id='" + buyerid + "'";

		if (!checkConnection()) {
			return Response.NoDBConnection;
		}
		
		try{
			//Guthaben aus der DB auslesen
			double wallettemp = 0;
			Statement statement = connection.createStatement();
			ResultSet walletSet = statement.executeQuery(getCurrentWalletQuery);
			if(walletSet.next())
			{
				wallettemp = walletSet.getDouble("wallet");
			}
			
			//in der DB prüfen, ob das Guthaben ausreicht
			if (wallettemp - product.getPrice() < 0) {
				return Response.InsufficientBalance;
			} else {
				//Guthaben reicht aus.
				//Guthaben beim Käufer vermindern
				//Guthaben beim Verkäufer erhöhen
				if (decreaseWallet(buyer, product.getPrice()) == Response.Success) {
					if (increaseWallet(seller, product.getPrice()) == Response.Success) {
						PreparedStatement addNewOrder = connection.prepareStatement(newOrder);
						addNewOrder.execute();
						return Response.Success;
					} else {
						return Response.Failure;
					}
				} else {
					return Response.Failure;
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			return Response.Failure;
		}
	}

	protected Response addAuction(Auction auction) {				//fertig
		if (!checkConnection()) {
			return null;
		}
		try {
			PreparedStatement pstmt=connection.prepareStatement("INSERT INTO auctions(currentbid, currentbidder_id, description, emailsent, enddate, image, minbid, seller_id, shippingtype_id, startprice, starttime, title)"
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");

			pstmt.setDouble(1, auction.getStartPrice());
			pstmt.setInt(2, 0);
			pstmt.setString(3, auction.getDescription());
			pstmt.setBoolean(4, false);
			pstmt.setTimestamp(5, java.sql.Timestamp.valueOf(auction.getEnddate())); // cast weil unterschiedliche arten von date in java und sql
			pstmt.setBytes(6, auction.getImage());
			pstmt.setDouble(7, auction.getMinBid());
			pstmt.setInt(8, auction.getSeller().getId());
			if(auction.getShippingType() == ShippingType.Shipping)
			{
				pstmt.setInt(9, 1); 
			}
			else if(auction.getShippingType() == ShippingType.PickUp)
			{
				pstmt.setInt(9, 2); 
			}
			pstmt.setDouble(10, auction.getStartPrice());
			pstmt.setTimestamp(11, java.sql.Timestamp.valueOf(auction.getStarttime()));		//cast unterschiedliche Dates
			pstmt.setString(12, auction.getTitle());

			pstmt.execute();

			return Response.Success;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.Failure;
		}

	}

	protected Response sendBid(Auction auction, Customer bidder, double bid) {				//fertig
		// Das Objekt bidder bietet auf das Objekt auction die Menge bid
		// 1. Fall Auktion bereits beendet
		// 2. Fall Bid ist zu niedrig
		// 3. Fall Bid ist in Ordnung und die Auktion läuft noch

		// Quelle:
		// https://stackoverflow.com/questions/12584992/how-to-get-current-server-time-in-java#:~:text=If%20you%20like%20to%20return,retrieve%20the%20current%20system%20time.
		// Autor: Aaron Blenkush
		// Edited: Jan 8'14 at 18:47
		LocalDateTime serverDate = LocalDateTime.now();
		// String currentServerDate= SEPCommon.Constants.DATEFORMAT.format(serverDate);

		LocalDateTime endDate = auction.getEnddate();
		LocalDateTime startDate = auction.getStarttime();
		// String endDate = SEPCommon.Constants.DATEFORMAT.format(date);

		if (!checkConnection()) {
			return Response.NoDBConnection;
		}

		else if (serverDate.isAfter(startDate) == true) {
			if (serverDate.isBefore(endDate) == true) { // 1.Fall CurrentServerDate ist vor Enddatum (alles gut)
				if (auction.getCurrentBid() >= bid || auction.getMinBid() > bid || auction.getStartPrice() > bid) {
					return Response.BidTooLow;
				} else if (bidder.getWallet()-bid <0) {
					return Response.InsufficientBalance;
				} else if (bid > auction.getCurrentBid() && bidder.getWallet()-bid >=0) {
					try {
						PreparedStatement pstmt = connection
								.prepareStatement("UPDATE auctions SET currentbid=?, currentbidder_id=? WHERE auction_id=" + auction.getId());
						pstmt.setDouble(1, bid);
						pstmt.setInt(2, bidder.getId());
						pstmt.execute();
						return Response.Success;
					} catch (SQLException e) {
						e.printStackTrace();
						return Response.Failure;
					}
				} else {
					return Response.Failure;
				}

			} else {
				return Response.AuctionAlreadyEnded;
			}

		} else {
			return Response.AuctionNotStartedYet;
		}

	}

	protected Response saveAuction(User buyer, Auction auction) {		
		// ID der Auktion in die Merkliste des Objekt buyer setzen
		
		//Wenn erfolgreich hinzugefügt, Response.Success returnen
		//wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		//wenn sonstiger Fehler auftritt ggf. Response.Failure returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!checkConnection())
		{
			return Response.NoDBConnection;
		}
		
		int viewedAuctionId = auction.getId();
		
		//Aktuelle zuletzt angesehene Produkte holen, um zu entscheiden, ob eines ersetzt werden muss oder nur hinzugefügt werden muss
		Auction[] currentSavedAuctions = fetchSavedAuctions(buyer);
		
		String newSavedAuctionsProductsString = "";
		if(currentSavedAuctions!=null)
		{
			if(currentSavedAuctions.length==50)
			{
				//Maximale Lünge (50), setze viewedProductId an den Anfang und ersetze die letzte Id
				newSavedAuctionsProductsString += String.valueOf(viewedAuctionId);
				
				for(int i=0;i<9;i++)
				{
					if(currentSavedAuctions[i]!=null)
					{
						//wenn currentLastViewedProducts[i] = null, ist das Produkt gelöscht. Dann ist es aus der Liste der zuletzt aufgerufenen Produkte zu entfernen
						newSavedAuctionsProductsString += "," + String.valueOf(currentSavedAuctions[i].getId());
					}
				}
			}
			else
			{
				//Maximale Länge (10) noch nicht erreicht, setze viewedProductId an den Anfang und schiebe ggf. die anderen ein Feld nach hinten
				newSavedAuctionsProductsString += String.valueOf(viewedAuctionId);
				
				for(int i=0;i<currentSavedAuctions.length;i++)
				{
					if(currentSavedAuctions[i]!=null)
					{
						//wenn currentLastViewedProducts[i] = null, ist das Produkt gelöscht. Dann ist es aus der Liste der zuletzt aufgerufenen Produkte zu entfernen
						newSavedAuctionsProductsString += "," + String.valueOf(currentSavedAuctions[i].getId());
					}
				}
			}
		}
		else
		{
			//Keine zuletzt angesehenen Produkte gespeichert oder Fehler
			newSavedAuctionsProductsString += String.valueOf(viewedAuctionId);
		}
		
		try {
			PreparedStatement updateSavedAuctionIds = connection.prepareStatement("UPDATE users SET savedauctions='" + newSavedAuctionsProductsString
					+ "' WHERE id='" + buyer.getId() + "'");
			updateSavedAuctionIds.execute();
			return Response.Success;
		} catch (SQLException e) {
			//Fehler aufgetreten
			e.printStackTrace();
			return Response.Failure;
		}
	}

	protected Order[] fetchOrders(User buyer) {												//fertig
		// Alle orders die das Objekt buyer gekauft hat in einem Order-Array
		// zurückgeben.
		// Wenn erfolgreich gefetcht, Product-Array returnen
		// wenn keine Verbindung zu DB: null returnen
		// wenn sonstiger Fehler auftritt (keine Produkte angesehen o.ä.) ggf. null
		// returnen
		// order array befüllen dafür braucht man: Tabelle:orders(orderid), products für
		// product Objekt, sellerrating, buyerrating
		// überprüfen ob Ratinhs vorhanden, dann sql statement etc ansonsten objekt=null

		if (!checkConnection()) {
			return null;
		}
		try {

			PreparedStatement pstmtOrders = connection.prepareStatement("SELECT * FROM users "
					+ "JOIN orders ON users.id=orders.buyer_id JOIN products "
					+ "ON products.id=orders.product_id WHERE users.id=" + buyer.getId(), ResultSet.TYPE_SCROLL_SENSITIVE, 
                    ResultSet.CONCUR_UPDATABLE);
			
			//Quelle: https://stackoverflow.com/questions/6367737/resultset-exception-set-type-is-type-forward-only-why
			//answered Jun 16 '11 at 6:14  by Adithya Surampudi
			int arraycounterAllOrders = 0;
			int sqlcounterAllOrders = 0;
			ResultSet allOrdersResultSet = pstmtOrders.executeQuery();

			while (allOrdersResultSet.next()) { // Tupel zählen
				sqlcounterAllOrders++;
			}
			allOrdersResultSet.beforeFirst(); // zurücksetzen des pointers auf 0
			Order[] allOrdersArray = new Order[sqlcounterAllOrders];

			while (allOrdersResultSet.next()) {

				Address newAddress = new Address(allOrdersResultSet.getString("users.fullname"),
						allOrdersResultSet.getString("users.country"), allOrdersResultSet.getInt("users.postalcode"),
						allOrdersResultSet.getString("users.city"), allOrdersResultSet.getString("users.street"),
						allOrdersResultSet.getString("users.number"));
				Seller newSeller = new Seller(allOrdersResultSet.getInt("users.id"),
						allOrdersResultSet.getString("users.username"), allOrdersResultSet.getString("users.email"),
						allOrdersResultSet.getString("users.password"), allOrdersResultSet.getBytes("users.image"),
						allOrdersResultSet.getDouble("users.wallet"), newAddress,
						allOrdersResultSet.getString("users.companyname"));
				Product newProduct = new Product(allOrdersResultSet.getInt("products.id"),
						allOrdersResultSet.getString("products.title"), allOrdersResultSet.getDouble("products.price"),
						newSeller, allOrdersResultSet.getString("products.title"),
						allOrdersResultSet.getString("products.description"));

				int orderId = allOrdersResultSet.getInt("orders.order_id");
				PreparedStatement pstmtBuyerRatings = connection.prepareStatement(
						"Select * FROM Ratings JOIN Users ON ratings.sender_id=users.id JOIN orders ON ratings.order_id="
								+ orderId + " WHERE users.id=" + buyer.getId(),
								ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

				PreparedStatement pstmtSellerRatings = connection.prepareStatement(		
						"Select * FROM Ratings JOIN Users ON ratings.receiver_id=users.id JOIN orders ON ratings.order_id="
								+ orderId + " WHERE users.id=" + newSeller.getId(),
								ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

				ResultSet allBuyerRatings = pstmtBuyerRatings.executeQuery();
				allBuyerRatings.beforeFirst();
				ResultSet allSellerRatings = pstmtSellerRatings.executeQuery();
				allSellerRatings.beforeFirst();
				Rating newSellerRating=null;
				Rating newBuyerRating=null;

				if (allSellerRatings.next() != false) {
					String sellerText = null;
					if (allSellerRatings.getString("ratings.text") != null) {
						sellerText = allSellerRatings.getString("ratings.text");
					}
					newSellerRating = new Rating(allSellerRatings.getInt("ratings.id"),
							allSellerRatings.getInt("ratings.stars"), sellerText,
							allSellerRatings.getInt("ratings.sender_id"),
							allSellerRatings.getInt("ratings.receiver_id"),
							allSellerRatings.getInt("ratings.order_id"), false);
				}

				if (allBuyerRatings.next() != false) {
					String buyerText = null;
					if (allSellerRatings.getString("ratings.text") != null) {

					}
					newBuyerRating = new Rating(allBuyerRatings.getInt("ratings.id"),
							allBuyerRatings.getInt("ratings.stars"), buyerText,
							allBuyerRatings.getInt("ratings.sender_id"),
							allBuyerRatings.getInt("ratings.receiver_id"),
							allBuyerRatings.getInt("ratings.order_id"), false);

				}

				allOrdersArray[arraycounterAllOrders] = new Order(allOrdersResultSet.getInt("orders.order_id"), newProduct,
						allOrdersResultSet.getTimestamp("orders.purchasedate").toLocalDateTime(), newBuyerRating, newSellerRating);

				arraycounterAllOrders++;

			}
			return allOrdersArray;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	protected Auction[] fetchPurchasedAuctions(User buyer) { // fertig
		LocalDateTime now = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(now);

		Auction[] allPurchasedAuctionsArray = null;
		try {
			PreparedStatement allPurchasedAuctions = connection.prepareStatement(
					"SELECT * FROM auctions JOIN users on auctions.currentbidder_id=" + buyer.getId()
							+ " WHERE DATE(auctions.enddate)> '" + timestamp + "'",
					ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			ResultSet purchasedAuctions = allPurchasedAuctions.executeQuery();
			int sumAuctions = 0;
			int arraycounter = 0;
			while (purchasedAuctions.next()) {
				sumAuctions++;
			}
			
			purchasedAuctions.beforeFirst();
			
			if(sumAuctions<=0)
			{
				return null;
			}
			allPurchasedAuctionsArray = new Auction[sumAuctions];
			while (purchasedAuctions.next()) {

				int wonAuctionId = purchasedAuctions.getInt("auctions.auction_id");

				PreparedStatement pstmtAllPurchasedAuctions = connection.prepareStatement(
						"Select * FROM auctions JOIN users ON (auctions.seller_id=users.id) WHERE auctions.auction_id="
								+ wonAuctionId,
						ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet allSellerInformation = pstmtAllPurchasedAuctions.executeQuery();
				allSellerInformation.first();

				Address newAddress = new Address(allSellerInformation.getString("users.fullname"),
						allSellerInformation.getString("users.country"),
						allSellerInformation.getInt("users.postalcode"), allSellerInformation.getString("users.city"),
						allSellerInformation.getString("users.street"), allSellerInformation.getString("users.number"));
				Customer newSeller = new Customer(allSellerInformation.getInt("users.id"),
						allSellerInformation.getString("users.username"), allSellerInformation.getString("users.email"),
						allSellerInformation.getString("users.password"), allSellerInformation.getBytes("users.image"),
						allSellerInformation.getDouble("users.wallet"), newAddress);

				int winnerId = buyer.getId();
				PreparedStatement pstmtCurrentBidder = connection
						.prepareStatement(
								"Select * FROM auctions JOIN users ON users.id=auctions.currentbidder_id WHERE auctions.auction_id= '"
										+ wonAuctionId + "' AND auctions.currentbidder_id='" + winnerId + "'",
								ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet currentBidderInformation = pstmtCurrentBidder.executeQuery();
				currentBidderInformation.beforeFirst();

				Address newAddressCurrentbidder = null;
				Customer currentBidder = null;

				if (currentBidderInformation.next() != false) {
					newAddressCurrentbidder = new Address(currentBidderInformation.getString("users.fullname"),
							currentBidderInformation.getString("users.country"),
							currentBidderInformation.getInt("users.postalcode"),
							currentBidderInformation.getString("users.city"),
							currentBidderInformation.getString("users.street"),
							currentBidderInformation.getString("users.number"));
					currentBidder = new Customer(currentBidderInformation.getInt("users.id"),
							currentBidderInformation.getString("users.username"),
							currentBidderInformation.getString("users.email"),
							currentBidderInformation.getString("users.password"),
							currentBidderInformation.getBytes("users.image"),
							currentBidderInformation.getDouble("users.wallet"), newAddressCurrentbidder);
				}
				Rating newSellerRating = null;
				Rating newBuyerRating = null;
			
				if (currentBidder != null) {
					PreparedStatement pstmtSellerRatingsEndedAuction = connection.prepareStatement(
							"Select * FROM Ratings JOIN Users ON ratings.sender_id=users.id JOIN auctions ON ratings.auction_id="
									+ wonAuctionId + " WHERE users.id=" + newSeller.getId());

					PreparedStatement pstmtBuyerEndedAuction = connection.prepareStatement(
							"Select * FROM Ratings JOIN Users ON ratings.receiver_id=users.id JOIN auctions ON ratings.order_id="
									+ wonAuctionId + " WHERE users.id=" + currentBidder.getId());

					ResultSet allSellerRatings = pstmtSellerRatingsEndedAuction.executeQuery();
					allSellerRatings.first();
					ResultSet allBuyerRatings = pstmtBuyerEndedAuction.executeQuery();
					allBuyerRatings.first();
					

					if (allSellerRatings.next() != false) {
						String sellerText = null;
						if (allSellerRatings.getString("ratings.text") != null) {
							sellerText = allSellerRatings.getString("ratings.text");
						}
						newSellerRating = new Rating(allSellerRatings.getInt("ratings.id"),
								allSellerRatings.getInt("ratings.stars"), sellerText,
								allSellerRatings.getInt("ratings.sender_id"),
								allSellerRatings.getInt("ratings.receiver_id"),
								allSellerRatings.getInt("ratings.order_id"), true);
					}

					if (allBuyerRatings.next() != false) {
						String buyerText = null;
						if (allSellerRatings.getString("ratings.text") != null) {

						}
						newBuyerRating = new Rating(allBuyerRatings.getInt("ratings.id"),
								allBuyerRatings.getInt("ratings.stars"), buyerText,
								allBuyerRatings.getInt("ratings.sender_id"),
								allBuyerRatings.getInt("ratings.receiver_id"),
								allBuyerRatings.getInt("ratings.order_id"), true);

					}
				}
				ShippingType shippingtype = null;
				if (purchasedAuctions.getInt("auctions.shippingtype_id") == 1) {
					shippingtype = ShippingType.Shipping;
				} else if (purchasedAuctions.getInt("auctions.shippingtype_id") == 2) {
					shippingtype = ShippingType.PickUp;
				}
				allPurchasedAuctionsArray[arraycounter] = new Auction(purchasedAuctions.getInt("auctions.auction_id"),
						purchasedAuctions.getString("auctions.title"),
						purchasedAuctions.getString("auctions.description"),
						purchasedAuctions.getBytes("auctions.image"),
						purchasedAuctions.getDouble("auctions.minbid"),
						purchasedAuctions.getDouble("auctions.startprice"),
						purchasedAuctions.getDouble("auctions.currentbid"), shippingtype, newSeller, currentBidder,
						newSellerRating, newBuyerRating,
						purchasedAuctions.getTimestamp("auctions.starttime").toLocalDateTime(),
						purchasedAuctions.getTimestamp("auctions.enddate").toLocalDateTime());

				arraycounter++;

			}

			return allPurchasedAuctionsArray;

		} catch (Exception e) {
			return null;
		}
	}

	protected Auction[] fetchAuctions(AuctionType auctionType) { // fertig
		// je nach AuctionType alle aktuell laufenden, beendeten oder zukünftigen
		// Auktionen zurückgeben

		// AuctionType:
		// AuctionType.Active = aktive Auktionen -> serverzeit >= starttime AND
		// serverzeit <=endttime
		// AuctionType.Ended = beendete Auktionen -> serverzeit > endtime
		// AuctionType.Future = zukünftige Auktionen -> serverzeit < starttime

		if (!checkConnection()) {
			return null;
		}
		try {
			if (auctionType == AuctionType.Active) {

				LocalDateTime now = LocalDateTime.now();
				Timestamp timestamp = Timestamp.valueOf(now);

				Auction[] allActiveAuctionsArray = null;
				PreparedStatement allActiveAuctions = connection.prepareStatement(
						"Select * FROM auctions WHERE DATE(auctions.enddate) >='" + timestamp
								+ "' AND DATE(auctions.starttime) <='" + timestamp + "'" ,
						ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet activeAuctions = allActiveAuctions.executeQuery();
				int sumAuctions = 0;
				int arraycounter = 0;
				while (activeAuctions.next()) {
					sumAuctions++;
				}
				activeAuctions.beforeFirst();

				if(sumAuctions<=0)
				{
					return null;
				}
				
				allActiveAuctionsArray = new Auction[sumAuctions];

				while (activeAuctions.next()) {
					int activeAuctionId = activeAuctions.getInt("auctions.auction_id");
					PreparedStatement pstmtAllActiveAuctions = connection.prepareStatement(
							"Select * FROM auctions JOIN users ON (auctions.seller_id=users.id) WHERE auctions.auction_id="
									+ activeAuctionId,
							ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					ResultSet allSellerInformation = pstmtAllActiveAuctions.executeQuery();
					allSellerInformation.first();

					Address newAddress = new Address(allSellerInformation.getString("users.fullname"),
							allSellerInformation.getString("users.country"),
							allSellerInformation.getInt("users.postalcode"),
							allSellerInformation.getString("users.city"),
							allSellerInformation.getString("users.street"),
							allSellerInformation.getString("users.number"));
					Customer newSeller = new Customer(allSellerInformation.getInt("users.id"),
							allSellerInformation.getString("users.username"),
							allSellerInformation.getString("users.email"),
							allSellerInformation.getString("users.password"),
							allSellerInformation.getBytes("users.image"),
							allSellerInformation.getDouble("users.wallet"), newAddress);

					int currentBidderId = activeAuctions.getInt("auctions.currentbidder_id");

					PreparedStatement pstmtCurrentBidder = connection.prepareStatement(
							"Select * FROM auctions JOIN users ON auctions.currentbidder_id=users.id WHERE auctions.auction_id="
									+ activeAuctionId + " AND auctions.currentbidder_id=" + currentBidderId ,
							ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					ResultSet currentBidderInformation = pstmtCurrentBidder.executeQuery();
					currentBidderInformation.first();

					Address newAddressCurrentbidder = null;
					Customer currentBidder = null;

					if (currentBidderInformation.next() != false) {
						newAddressCurrentbidder = new Address(currentBidderInformation.getString("users.fullname"),
								currentBidderInformation.getString("users.country"),
								currentBidderInformation.getInt("users.postalcode"),
								currentBidderInformation.getString("users.city"),
								currentBidderInformation.getString("users.street"),
								currentBidderInformation.getString("users.number"));
						currentBidder = new Customer(currentBidderInformation.getInt("users.id"),
								currentBidderInformation.getString("users.username"),
								currentBidderInformation.getString("users.email"),
								currentBidderInformation.getString("users.password"),
								currentBidderInformation.getBytes("users.image"),
								currentBidderInformation.getDouble("users.wallet"), newAddressCurrentbidder);
					}
					ShippingType shippingtype = null;
					if (activeAuctions.getInt("auctions.shippingtype_id") == 1) {
						shippingtype = ShippingType.Shipping;
					} else if (activeAuctions.getInt("auctions.shippingtype_id") == 2) {
						shippingtype = ShippingType.PickUp;
					}
					
					allActiveAuctionsArray[arraycounter] = new Auction(activeAuctions.getInt("auctions.auction_id"),
							activeAuctions.getString("auctions.title"),
							activeAuctions.getString("auctions.description"), activeAuctions.getBytes("auctions.image"),
							activeAuctions.getDouble("auctions.minbid"),activeAuctions.getDouble("auctions.startprice"),
							shippingtype, newSeller, currentBidder,
							activeAuctions.getDouble("auctions.currentbid"),
							activeAuctions.getTimestamp("auctions.starttime").toLocalDateTime(),
							activeAuctions.getTimestamp("auctions.enddate").toLocalDateTime());
					arraycounter++;

				}
				return allActiveAuctionsArray;

			}

			else if (auctionType == AuctionType.Ended) {

				LocalDateTime now = LocalDateTime.now();
				Timestamp timestamp = Timestamp.valueOf(now);

				Auction[] allEndedAuctionsArray = null;
				PreparedStatement allEndedAuctions = connection.prepareStatement(
						"Select * FROM auctions WHERE DATE(auctions.enddate) <'" + timestamp + "'",
						ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet endedAuctions = allEndedAuctions.executeQuery();
				int sumAuctions = 0;
				int arraycounter = 0;
				while (endedAuctions.next()) {
					sumAuctions++;
				}
				endedAuctions.beforeFirst();

				if(sumAuctions<=0)
				{
					return null;
				}
				
				allEndedAuctionsArray = new Auction[sumAuctions];

				while (endedAuctions.next()) {
					int endedAuctionId = endedAuctions.getInt("auctions.auction_id");
					PreparedStatement pstmtAllEndedAuctions = connection.prepareStatement(
							"Select * FROM auctions JOIN users ON (auctions.seller_id=users.id) WHERE auctions.auction_id="+ endedAuctionId,
							ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					ResultSet allSellerInformation = pstmtAllEndedAuctions.executeQuery();
					allSellerInformation.first();

					Address newAddress = new Address(allSellerInformation.getString("users.fullname"),
							pstmtAllEndedAuctions.getResultSet().getString("users.country"),
							pstmtAllEndedAuctions.getResultSet().getInt("users.postalcode"),
							pstmtAllEndedAuctions.getResultSet().getString("users.city"),
							pstmtAllEndedAuctions.getResultSet().getString("users.street"),
							pstmtAllEndedAuctions.getResultSet().getString("users.number"));
					Customer newSeller = new Customer(pstmtAllEndedAuctions.getResultSet().getInt("users.id"),
							pstmtAllEndedAuctions.getResultSet().getString("users.username"),
							pstmtAllEndedAuctions.getResultSet().getString("users.email"),
							pstmtAllEndedAuctions.getResultSet().getString("users.password"),
							pstmtAllEndedAuctions.getResultSet().getBytes("users.image"),
							pstmtAllEndedAuctions.getResultSet().getDouble("users.wallet"), newAddress);

					int currentBidderId = pstmtAllEndedAuctions.getResultSet().getInt("auctions.currentbidder_id");

					PreparedStatement pstmtCurrentBidder = connection.prepareStatement(
							"Select * FROM auctions JOIN users ON auctions.currentbidder_id=users.id WHERE auctions.auction_id="
									+ endedAuctionId + " AND auctions.currentbidder_id=" + currentBidderId,
							ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					ResultSet currentBidderInformation = pstmtCurrentBidder.executeQuery();
					currentBidderInformation.first();

					Address newAddressCurrentbidder = null;
					Customer currentBidder = null;

					if (currentBidderInformation.next() != false) {
						newAddressCurrentbidder = new Address(currentBidderInformation.getString("users.fullname"),
								currentBidderInformation.getString("users.country"),
								currentBidderInformation.getInt("users.postalcode"),
								currentBidderInformation.getString("users.city"),
								currentBidderInformation.getString("users.street"),
								currentBidderInformation.getString("users.number"));
						currentBidder = new Customer(currentBidderInformation.getInt("users.id"),
								currentBidderInformation.getString("users.username"),
								currentBidderInformation.getString("users.email"),
								currentBidderInformation.getString("users.password"),
								currentBidderInformation.getBytes("users.image"),
								currentBidderInformation.getDouble("users.wallet"), newAddressCurrentbidder);
					}
					Rating newSellerRating = null;
					Rating newBuyerRating = null;
					if (currentBidder != null) {
						PreparedStatement pstmtSellerRatingsEndedAuction = connection.prepareStatement(
								"Select * FROM Ratings JOIN Users ON ratings.sender_id=users.id JOIN auctions ON ratings.auction_id=auctions.auction_id WHERE users.id="
										+ newSeller.getId() + " AND auctions.auction_id=" + endedAuctionId );

						PreparedStatement pstmtBuyerEndedAuction = connection.prepareStatement(
								"Select * FROM Ratings JOIN Users ON ratings.receiver_id=users.id JOIN auctions ON ratings.auction_id=auctions.auction_id WHERE users.id="
										+ currentBidder.getId() + " AND auctions.auction_id=" + endedAuctionId);

						ResultSet allSellerRatings = pstmtSellerRatingsEndedAuction.executeQuery();
						allSellerRatings.first();		//ggf. beforeFirst checken
						ResultSet allBuyerRatings = pstmtBuyerEndedAuction.executeQuery();
						allBuyerRatings.first();

						if (allSellerRatings.next() != false) {
							String sellerText= null;
							if(allSellerRatings.getString("ratings.text")!=null) {
								sellerText=allSellerRatings.getString("ratings.text");
							}
							newSellerRating = new Rating(allSellerRatings.getInt("ratings.id"),
									allSellerRatings.getInt("ratings.stars"),
									sellerText,
									allSellerRatings.getInt("ratings.sender_id"),
									allSellerRatings.getInt("ratings.receiver_id"),
									allSellerRatings.getInt("ratings.order_id"), true);
						}

						if (allBuyerRatings.next() != false) {
							String buyerText=null;
							if(allSellerRatings.getString("ratings.text")!=null) {
								
							}
							newSellerRating = new Rating(allBuyerRatings.getInt("ratings.id"),
									allBuyerRatings.getInt("ratings.stars"), buyerText,
									allBuyerRatings.getInt("ratings.sender_id"),
									allBuyerRatings.getInt("ratings.receiver_id"),
									allBuyerRatings.getInt("ratings.order_id"), true);

						}
					}
					ShippingType shippingtype = null;
					if (endedAuctions.getInt("auctions.shippingtype_id") == 1) {
						shippingtype = ShippingType.Shipping;
					} else if (endedAuctions.getInt("auctions.shippingtype_id") == 2) {
						shippingtype = ShippingType.PickUp;
					}
					allEndedAuctionsArray[arraycounter] = new Auction(endedAuctions.getInt("auctions.auction_id"),
							endedAuctions.getString("auctions.title"), endedAuctions.getString("auctions.description"),
							endedAuctions.getBytes("auctions.image"), endedAuctions.getDouble("auctions.minbid"),
							endedAuctions.getDouble("auctions.startprice"),
							endedAuctions.getDouble("auctions.currentbid"), shippingtype, newSeller, currentBidder,
							newSellerRating, newBuyerRating,
							endedAuctions.getTimestamp("auctions.starttime").toLocalDateTime(),
							endedAuctions.getTimestamp("auctions.enddate").toLocalDateTime());

					arraycounter++;

				}

				return allEndedAuctionsArray;

			}

			else if (auctionType == AuctionType.Future) {

				LocalDateTime now = LocalDateTime.now();
				Timestamp timestamp = Timestamp.valueOf(now);

				PreparedStatement allFutureAuctions = connection.prepareStatement(
						"Select * FROM auctions " + "WHERE DATE(auctions.starttime) > '" + timestamp + "'",
						ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet futureAuctions = allFutureAuctions.executeQuery();
				int sumAuctions = 0;
				int arraycounter = 0;

				while (futureAuctions.next()) {
					sumAuctions++;
				}
				
				futureAuctions.beforeFirst();
				Auction[] allFutureAuctionsArray = null;
				
				if(sumAuctions<=0)
				{
					return null;
				}
				
				allFutureAuctionsArray = new Auction[sumAuctions];
				while (futureAuctions.next()) {

					int futureAuctionId = futureAuctions.getInt("auctions.auction_id");
					PreparedStatement pstmtAllfutureAuctions = connection.prepareStatement(
							"Select * FROM auctions JOIN users ON (auctions.seller_id=users.id) WHERE auctions.auction_id="
									+ futureAuctionId,
							ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					ResultSet allSellerInformation = pstmtAllfutureAuctions.executeQuery();
					allSellerInformation.first();

					Address newAddress = new Address(allSellerInformation.getString("users.fullname"),
							allSellerInformation.getString("users.country"),
							allSellerInformation.getInt("users.postalcode"),
							allSellerInformation.getString("users.city"),
							allSellerInformation.getString("users.street"),
							allSellerInformation.getString("users.number"));
					Customer newSeller = new Customer(allSellerInformation.getInt("users.id"),
							allSellerInformation.getString("users.username"),
							allSellerInformation.getString("users.email"),
							allSellerInformation.getString("users.password"),
							allSellerInformation.getBytes("users.image"),
							allSellerInformation.getDouble("users.wallet"), newAddress);

					ShippingType shippingtype = null;
					if (futureAuctions.getInt("auctions.shippingtype_id") == 1) {
						shippingtype = ShippingType.Shipping;
					} else if (futureAuctions.getInt("auctions.shippingtype_id") == 2) {
						shippingtype = ShippingType.PickUp;
					}

					allFutureAuctionsArray[arraycounter] = new Auction(futureAuctions.getInt("auctions.auction_id"), futureAuctions.getString("auctions.title"),
							futureAuctions.getString("auctions.description"), futureAuctions.getBytes("auctions.image"),
							futureAuctions.getDouble("auctions.minbid"), futureAuctions.getDouble("auctions.startprice"),
							shippingtype, newSeller, null, futureAuctions.getDouble("auctions.currentbid"),
							futureAuctions.getTimestamp("auctions.starttime").toLocalDateTime(),
							futureAuctions.getTimestamp("auctions.enddate").toLocalDateTime());
					
					arraycounter++;
				}

				return allFutureAuctionsArray;

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	protected Auction[] fetchOwnAuctions(User buyer) {

		if (!checkConnection()) {
			return null;
		}
		// selbst eingestellte Auktionen (aktuell + beendete + zukünftige)
		try {
			PreparedStatement pstmtAllOwnAuctions = connection.prepareStatement(
					"Select * FROM auctions WHERE auctions.seller_id=" + buyer.getId(),
					ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet allOwnAuctions = pstmtAllOwnAuctions.executeQuery();

			int sqlcounter = 0;
			int arraycounter = 0;

			while (allOwnAuctions.next()) {
				sqlcounter++;
			}
			allOwnAuctions.beforeFirst();

			if(sqlcounter<=0)
			{
				return null;
			}
			
			Auction[] allOwnAuctionsArray = new Auction[sqlcounter];
			while (allOwnAuctions.next()) {
				int currentAuctionId = allOwnAuctions.getInt("auctions.auction_id");

				if(buyer.getId()!=0) {
				PreparedStatement pstmtAllSellerInformation = connection.prepareStatement(
						"SELECT* FROM auctions JOIN users ON auctions.seller_id = users.id WHERE auctions.seller_id="
								+ buyer.getId() + " AND auctions.auction_id=" + currentAuctionId,
						ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

				ResultSet allSellerInformation = pstmtAllSellerInformation.executeQuery();
				allSellerInformation.first();

				Address newAddress = new Address(allSellerInformation.getString("users.fullname"),
						allSellerInformation.getString("users.country"),
						allSellerInformation.getInt("users.postalcode"), allSellerInformation.getString("users.city"),
						allSellerInformation.getString("users.street"), allSellerInformation.getString("users.number"));
				Customer newSeller = new Customer(allSellerInformation.getInt("users.id"),
						allSellerInformation.getString("users.username"), allSellerInformation.getString("users.email"),
						allSellerInformation.getString("users.password"), allSellerInformation.getBytes("users.image"),
						allSellerInformation.getDouble("users.wallet"), newAddress);

				int currentbidderId=allOwnAuctions.getInt("auctions.currentbidder_id");
				Address newAddressCurrentBidder = null;
				Customer currentBidder = null;

				if (allOwnAuctions.getInt("auctions.currentbidder_id") != 0) {

					PreparedStatement pstmtCurrentBidder = connection.prepareStatement(
							"Select * FROM auctions JOIN users on users.id=auctions.currentbidder_id WHERE auctions.auction_id="
									+ currentAuctionId + " AND auctions.currentbidder_id=" + currentbidderId,
							ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

					ResultSet currentBidderInformation = pstmtCurrentBidder.executeQuery();
					currentBidderInformation.first();

					newAddressCurrentBidder = new Address(currentBidderInformation.getString("users.fullname"),
							currentBidderInformation.getString("users.country"),
							currentBidderInformation.getInt("users.postalcode"),
							currentBidderInformation.getString("users.city"),
							currentBidderInformation.getString("users.street"),
							currentBidderInformation.getString("users.number"));
					currentBidder = new Customer(currentBidderInformation.getInt("users.id"),
							currentBidderInformation.getString("users.username"),
							currentBidderInformation.getString("users.email"),
							currentBidderInformation.getString("users.password"),
							currentBidderInformation.getBytes("users.image"),
							currentBidderInformation.getDouble("users.wallet"), newAddressCurrentBidder);
				}

				Rating newSellerRating = null;
				Rating newBuyerRating = null;
				if (currentBidder != null) {
							PreparedStatement pstmtSellerRatingsEndedAuction = connection.prepareStatement(
									"Select * FROM Ratings JOIN Users ON ratings.sender_id=users.id JOIN auctions ON ratings.auction_id=auctions.auction_id WHERE users.id="
											+ newSeller.getId() + " AND auctions.auction_id=" + currentAuctionId,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

							PreparedStatement pstmtBuyerEndedAuction = connection.prepareStatement(
									"Select * FROM Ratings JOIN Users ON ratings.receiver_id=users.id JOIN auctions ON ratings.auction_id=auctions.auction_id WHERE users.id="
											+ currentBidder.getId() + " AND auctions.auction_id=" + currentAuctionId,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

					ResultSet allSellerRatings = pstmtSellerRatingsEndedAuction.executeQuery();
					allSellerRatings.first();
					ResultSet allBuyerRatings = pstmtBuyerEndedAuction.executeQuery();
					allBuyerRatings.first();

					if (allSellerRatings.next() != false) {
						String sellerText = null;
						if (allSellerRatings.getString("ratings.text") != null) {
							sellerText = allSellerRatings.getString("ratings.text");
						}
						newSellerRating = new Rating(allSellerRatings.getInt("ratings.id"),
								allSellerRatings.getInt("ratings.stars"), sellerText,
								allSellerRatings.getInt("ratings.sender_id"),
								allSellerRatings.getInt("ratings.receiver_id"),
								allSellerRatings.getInt("ratings.order_id"), true);
					}

					if (allBuyerRatings.next() != false) {
						String buyerText = null;
						if (allSellerRatings.getString("ratings.text") != null) {

						}
						newSellerRating = new Rating(allBuyerRatings.getInt("ratings.id"),
								allBuyerRatings.getInt("ratings.stars"), buyerText,
								allBuyerRatings.getInt("ratings.sender_id"),
								allBuyerRatings.getInt("ratings.receiver_id"),
								allBuyerRatings.getInt("ratings.order_id"), true);

					}
				}
				ShippingType shippingtype = null;
				if (allOwnAuctions.getInt("auctions.shippingtype_id") == 1) {
					shippingtype = ShippingType.Shipping;
				} else if (allOwnAuctions.getInt("auctions.shippingtype_id") == 2) {
					shippingtype = ShippingType.PickUp;
				}

				allOwnAuctionsArray[arraycounter] = new Auction(allOwnAuctions.getInt("auctions.auction_id"),
						allOwnAuctions.getString("auctions.title"), allOwnAuctions.getString("auctions.description"),
						allOwnAuctions.getBytes("auctions.image"), allOwnAuctions.getDouble("auctions.minbid"),
						allOwnAuctions.getDouble("auctions.startprice"),
						allOwnAuctions.getDouble("auctions.currentbid"), shippingtype, newSeller, currentBidder,
						newSellerRating, newBuyerRating,
						allOwnAuctions.getTimestamp("auctions.starttime").toLocalDateTime(),
						allOwnAuctions.getTimestamp("auctions.enddate").toLocalDateTime());

				arraycounter++;

			}else {
				return allOwnAuctionsArray;
			}
			}
			return allOwnAuctionsArray;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	protected Auction[] fetchAuctionsUserBiddedOn(User buyer) {
		// Auktionen auf die buyer geboten hat (aktuell)
		if (!checkConnection()) {
			return null;
		}

		LocalDateTime now = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(now);
		int sqlcounter = 0;
		int arraycounter = 0;
		Auction[] allActiveAuctionsArray = null;
		try {
			PreparedStatement pstmtAllBiddedAuctions = connection
					.prepareStatement(
							"Select * FROM auctions " + "WHERE DATE(auctions.enddate) >= '" + timestamp
									+ "' AND DATE(auctions.starttime) <= '" + timestamp
									+ "' AND auctions.currentbidder_id=" + buyer.getId(),
							ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet allBiddedAuctions = pstmtAllBiddedAuctions.executeQuery();

			while (allBiddedAuctions.next()) {
				sqlcounter++;
			}
			allBiddedAuctions.beforeFirst();

			if(sqlcounter<=0)
			{
				return null;
			}
			
			allActiveAuctionsArray = new Auction[sqlcounter];

			while (allBiddedAuctions.next()) {

				int currentAuctionId = allBiddedAuctions.getInt("auctions.auction_id");
				PreparedStatement pstmtAllSellerInformation = connection.prepareStatement(
						"SELECT * FROM auctions JOIN users ON auctions.seller_id= users.id WHERE auctions.auction_id="
								+ currentAuctionId,
						ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

				ResultSet allSellerInformation = pstmtAllSellerInformation.executeQuery();
				allSellerInformation.first();

				Address newAddress = new Address(allSellerInformation.getString("users.fullname"),
						allSellerInformation.getString("users.country"),
						allSellerInformation.getInt("users.postalcode"), allSellerInformation.getString("users.city"),
						allSellerInformation.getString("users.street"), allSellerInformation.getString("users.number"));
				Customer newSeller = new Customer(allSellerInformation.getInt("users.id"),
						allSellerInformation.getString("users.username"), allSellerInformation.getString("users.email"),
						allSellerInformation.getString("users.password"), allSellerInformation.getBytes("users.image"),
						allSellerInformation.getDouble("users.wallet"), newAddress);

				int currentbidderId = buyer.getId();

				PreparedStatement pstmtCurrentBidder = connection.prepareStatement(
						"Select * FROM auctions JOIN users ON auctions.currentbidder_id=users.id WHERE auctions.auction_id="
								+ currentAuctionId + " AND auctions.currentbidder_id=" + currentbidderId,
						ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet currentBidderInformation = pstmtCurrentBidder.executeQuery();
				currentBidderInformation.beforeFirst();
				Address newAddressCurrentBidder = null;
				Customer currentBidder = null;

				if (currentBidderInformation.next() != false) {
					newAddressCurrentBidder = new Address(currentBidderInformation.getString("users.fullname"),
							currentBidderInformation.getString("users.country"),
							currentBidderInformation.getInt("users.postalcode"),
							currentBidderInformation.getString("users.city"),
							currentBidderInformation.getString("users.street"),
							currentBidderInformation.getString("users.number"));
					currentBidder = new Customer(currentBidderInformation.getInt("users.id"),
							currentBidderInformation.getString("users.username"),
							currentBidderInformation.getString("users.email"),
							currentBidderInformation.getString("users.password"),
							currentBidderInformation.getBytes("users.image"),
							currentBidderInformation.getDouble("users.wallet"), newAddressCurrentBidder);
				}
				ShippingType shippingtype = null;
				if (allBiddedAuctions.getInt("auctions.shippingtype_id") == 1) {
					shippingtype = ShippingType.Shipping;
				} else if (allBiddedAuctions.getInt("auctions.shippingtype_id") == 2) {
					shippingtype = ShippingType.PickUp;
				}
				allActiveAuctionsArray[arraycounter] = new Auction(allBiddedAuctions.getInt("auctions.auction_id"),
						allBiddedAuctions.getString("auctions.title"),
						allBiddedAuctions.getString("auctions.description"),
						allBiddedAuctions.getBytes("auctions.image"), allBiddedAuctions.getDouble("auctions.minbid"),
						allBiddedAuctions.getDouble("auctions.startprice"), shippingtype, newSeller, currentBidder,
						allBiddedAuctions.getDouble("auctions.currentbid"),
						allBiddedAuctions.getTimestamp("auctions.starttime").toLocalDateTime(),
						allBiddedAuctions.getTimestamp("auctions.enddate").toLocalDateTime());
				arraycounter++;

			}
			return allActiveAuctionsArray;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	protected Auction[] fetchSavedAuctions(User buyer) {
		// Auktionen die buyer gespeichert hat (aktuell + beendete + zukünftige)
		// maximal 50 Stk
		
		if (!checkConnection()) {
			return null;
		}
		
		Auction[] savedAuctions;
		
		try {
			PreparedStatement fetchSavedAuctionsIds = connection.prepareStatement("SELECT savedauctions FROM users WHERE id='" + buyer.getId() + "'");
			ResultSet fetchSavedAuctionsIdsResult = fetchSavedAuctionsIds.executeQuery();
			
			//wenn eine Spalte savedAuctions gefunden wurde
			if(fetchSavedAuctionsIdsResult.next()) 	
			{
				
				String saved = fetchSavedAuctionsIdsResult.getString("savedauctions");
				if(saved=="" || saved==null || saved.isEmpty() || saved.isBlank())
					return null; //keine Auktionen bisher gespeichert
				
				String[] savedAuctionsIds = saved.split(","); //in der DB sind die IDs durch "," seppariert, daher splitten und Array der IDs erstellen
				savedAuctions = new Auction[savedAuctionsIds.length]; //Rckgabearray mit Gre der Anzahl der IDs im Array
				
				int newArrayCounter = 0;
				for(String viewedIdStr : savedAuctionsIds) 
				{
					try {
						int viewedId = Integer.parseInt(viewedIdStr);
						
						//Fr jede ID im Array saveAuctionsId, die Auktionsdaten aus der DB holen
						//anschlieend jeweils ein Auction-Object anhand der gefetchten Daten aus der DB erstellen
						//und in das Array savedAuctions, welches am Ende zurckgegeben wird schreiben
						PreparedStatement fetchAuctionInfo = connection.prepareStatement("SELECT * FROM auctions JOIN users ON (auctions.seller_Id = users.id) WHERE auctions.auction_id='" + viewedId + "'");
						ResultSet fetchAuctionsInfoResult = fetchAuctionInfo.executeQuery();
						if(fetchAuctionsInfoResult.next())
						{
							
							Address address = new Address(fetchAuctionsInfoResult.getString("users.fullname"),
									fetchAuctionsInfoResult.getString("users.country"), fetchAuctionsInfoResult.getInt("users.postalcode"),
									fetchAuctionsInfoResult.getString("users.city"), fetchAuctionsInfoResult.getString("users.street"),
									fetchAuctionsInfoResult.getString("users.number"));
							Customer seller = new Customer(fetchAuctionsInfoResult.getInt("users.id"), fetchAuctionsInfoResult.getString("users.username"),
									fetchAuctionsInfoResult.getString("users.email"), fetchAuctionsInfoResult.getString("users.password"), 
									fetchAuctionsInfoResult.getBytes("users.image"), fetchAuctionsInfoResult.getDouble("users.wallet"), address);
							
							
							int shippingtypeId = fetchAuctionsInfoResult.getInt("auctions.shippingtype_id");
							ShippingType shippingtype = null;

							if(shippingtypeId==1)
							{
							    shippingtype = ShippingType.Shipping;
							}
							else if(shippingtypeId==2)
							{
							    shippingtype = ShippingType.PickUp;
							}
							
							int currentBidderId = fetchAuctionsInfoResult.getInt("auctions.currentbidder_id");
							PreparedStatement fetchCustomerData = connection.prepareStatement("SELECT * FROM users WHERE id='" + currentBidderId + "'");
							ResultSet fetchUserDataResult = fetchCustomerData.executeQuery();
							Customer customer = null;
							if(fetchUserDataResult.next())
							{
								customer = new Customer(fetchUserDataResult.getInt("id"), fetchUserDataResult.getString("username"),
										fetchUserDataResult.getString("email"), fetchUserDataResult.getString("password"), 
										fetchUserDataResult.getBytes("picture"), fetchUserDataResult.getDouble("wallet"), address);
							}
							
							Auction auction = new Auction(viewedId, fetchAuctionsInfoResult.getString("auctions.title"), fetchAuctionsInfoResult.getString("auctions.description"),
									fetchAuctionsInfoResult.getBytes("auctions.image"), fetchAuctionsInfoResult.getDouble("auctions.minbid"), fetchAuctionsInfoResult.getDouble("auctions.startprice"), shippingtype,
									seller, customer, fetchAuctionsInfoResult.getDouble("auctions.currentbid"), fetchAuctionsInfoResult.getTimestamp("auctions.starttime").toLocalDateTime(), fetchAuctionsInfoResult.getTimestamp("auctions.enddate").toLocalDateTime());
							
							savedAuctions[newArrayCounter] = auction;
						}
						newArrayCounter++;
						} catch (NumberFormatException e) {
							//Auction mittlerweile gelscht
							//ignorieren, fr diese ID keine Auktion (null) in das Array schreiben.
							savedAuctions[newArrayCounter] = null;
						}
					}
					return savedAuctions;
				}
				else
				{
					//kein Entry mit der BuyerId - eigentlich nicht mglich.
					return null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
	}

	protected Auction[] fetchAuctionsByString(String searchstring, AuctionType auctionType) {
		// Auktionen mit searchString im Titel/Name zurückgeben

		// je nach AuctionType die aktuell laufenden, beendeten oder zukünftigen anzeigen
		// Auktionen mit dem searchstring zurückgeben

		// AuctionType:
		// AuctionType.Active = aktive Auktionen
		// AuctionType.Ended = beendete Auktionen
		// AuctionType.Future = zukünftige Auktionen

		if (!checkConnection()) {
			return null;
		}
		try {
			if (auctionType == AuctionType.Active) {

				LocalDateTime now = LocalDateTime.now();
				Timestamp timestamp = Timestamp.valueOf(now);

				Auction[] allActiveAuctionsArray = null;
				PreparedStatement allActiveAuctions = connection.prepareStatement(
						"Select * FROM auctions WHERE DATE(auctions.enddate) >='" + timestamp
								+ "' AND DATE(auctions.starttime) <='" + timestamp + "'"
								+ " AND auctions.title LIKE ?",				//? Wildcard
						ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				allActiveAuctions.setString(1,"%"+ searchstring+"%");
				ResultSet activeAuctions = allActiveAuctions.executeQuery();
				int sumAuctions = 0;
				int arraycounter = 0;
				while (activeAuctions.next()) {
					sumAuctions++;
				}
				activeAuctions.beforeFirst();

				if(sumAuctions<=0)
				{
					return null;
				}
				
				allActiveAuctionsArray = new Auction[sumAuctions];

				while (activeAuctions.next()) {
					int activeAuctionId = activeAuctions.getInt("auctions.auction_id");
					PreparedStatement pstmtAllActiveAuctions = connection.prepareStatement(
							"Select * FROM auctions JOIN users ON (auctions.seller_id=users.id) WHERE auctions.auction_id="
									+ activeAuctionId,
							ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					ResultSet allSellerInformation = pstmtAllActiveAuctions.executeQuery();
					allSellerInformation.first();

					Address newAddress = new Address(allSellerInformation.getString("users.fullname"),
							allSellerInformation.getString("users.country"),
							allSellerInformation.getInt("users.postalcode"),
							allSellerInformation.getString("users.city"),
							allSellerInformation.getString("users.street"),
							allSellerInformation.getString("users.number"));
					Customer newSeller = new Customer(allSellerInformation.getInt("users.id"),
							allSellerInformation.getString("users.username"),
							allSellerInformation.getString("users.email"),
							allSellerInformation.getString("users.password"),
							allSellerInformation.getBytes("users.image"),
							allSellerInformation.getDouble("users.wallet"), newAddress);

					int currentBidderId = activeAuctions.getInt("auctions.currentbidder_id");

					PreparedStatement pstmtCurrentBidder = connection.prepareStatement(
							"Select * FROM auctions JOIN users ON auctions.currentbidder_id=users.id WHERE auctions.auction_id="
									+ activeAuctionId + " AND auctions.currentbidder_id=" + currentBidderId ,
							ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					ResultSet currentBidderInformation = pstmtCurrentBidder.executeQuery();
					currentBidderInformation.first();

					Address newAddressCurrentbidder = null;
					Customer currentBidder = null;

					if (currentBidderInformation.next() != false) {
						newAddressCurrentbidder = new Address(currentBidderInformation.getString("users.fullname"),
								currentBidderInformation.getString("users.country"),
								currentBidderInformation.getInt("users.postalcode"),
								currentBidderInformation.getString("users.city"),
								currentBidderInformation.getString("users.street"),
								currentBidderInformation.getString("users.number"));
						currentBidder = new Customer(currentBidderInformation.getInt("users.id"),
								currentBidderInformation.getString("users.username"),
								currentBidderInformation.getString("users.email"),
								currentBidderInformation.getString("users.password"),
								currentBidderInformation.getBytes("users.image"),
								currentBidderInformation.getDouble("users.wallet"), newAddressCurrentbidder);
					}
					ShippingType shippingtype = null;
					if (activeAuctions.getInt("auctions.shippingtype_id") == 1) {
						shippingtype = ShippingType.Shipping;
					} else if (activeAuctions.getInt("auctions.shippingtype_id") == 2) {
						shippingtype = ShippingType.PickUp;
					}
					
					allActiveAuctionsArray[arraycounter] = new Auction(activeAuctions.getInt("auctions.auction_id"),
							activeAuctions.getString("auctions.title"),
							activeAuctions.getString("auctions.description"), activeAuctions.getBytes("auctions.image"),
							activeAuctions.getDouble("auctions.minbid"),activeAuctions.getDouble("auctions.startprice"),
							shippingtype, newSeller, currentBidder,
							activeAuctions.getDouble("auctions.currentbid"),
							activeAuctions.getTimestamp("auctions.starttime").toLocalDateTime(),
							activeAuctions.getTimestamp("auctions.enddate").toLocalDateTime());
					arraycounter++;

				}
				return allActiveAuctionsArray;

			}

			else if (auctionType == AuctionType.Ended) {

				LocalDateTime now = LocalDateTime.now();
				Timestamp timestamp = Timestamp.valueOf(now);

				Auction[] allEndedAuctionsArray = null;
				PreparedStatement allEndedAuctions = connection.prepareStatement(
						"Select * FROM auctions WHERE DATE(auctions.enddate) <'" + timestamp + "'"
						+ " AND auctions.title LIKE ?",				//? Wildcard
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				allEndedAuctions.setString(1,"%"+ searchstring+"%");
				ResultSet endedAuctions = allEndedAuctions.executeQuery();
				int sumAuctions = 0;
				int arraycounter = 0;
				while (endedAuctions.next()) {
					sumAuctions++;
				}
				endedAuctions.beforeFirst();

				if(sumAuctions<=0)
				{
					return null;
				}
				
				allEndedAuctionsArray = new Auction[sumAuctions];

				while (endedAuctions.next()) {
					int endedAuctionId = endedAuctions.getInt("auctions.auction_id");
					PreparedStatement pstmtAllEndedAuctions = connection.prepareStatement(
							"Select * FROM auctions JOIN users ON (auctions.seller_id=users.id) WHERE auctions.auction_id="+ endedAuctionId,
							ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					ResultSet allSellerInformation = pstmtAllEndedAuctions.executeQuery();
					allSellerInformation.first();

					Address newAddress = new Address(allSellerInformation.getString("users.fullname"),
							pstmtAllEndedAuctions.getResultSet().getString("users.country"),
							pstmtAllEndedAuctions.getResultSet().getInt("users.postalcode"),
							pstmtAllEndedAuctions.getResultSet().getString("users.city"),
							pstmtAllEndedAuctions.getResultSet().getString("users.street"),
							pstmtAllEndedAuctions.getResultSet().getString("users.number"));
					Customer newSeller = new Customer(pstmtAllEndedAuctions.getResultSet().getInt("users.id"),
							pstmtAllEndedAuctions.getResultSet().getString("users.username"),
							pstmtAllEndedAuctions.getResultSet().getString("users.email"),
							pstmtAllEndedAuctions.getResultSet().getString("users.password"),
							pstmtAllEndedAuctions.getResultSet().getBytes("users.image"),
							pstmtAllEndedAuctions.getResultSet().getDouble("users.wallet"), newAddress);

					int currentBidderId = pstmtAllEndedAuctions.getResultSet().getInt("auctions.currentbidder_id");

					PreparedStatement pstmtCurrentBidder = connection.prepareStatement(
							"Select * FROM auctions JOIN users ON auctions.currentbidder_id=users.id WHERE auctions.auction_id="
									+ endedAuctionId + " AND auctions.currentbidder_id=" + currentBidderId,
							ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					ResultSet currentBidderInformation = pstmtCurrentBidder.executeQuery();
					currentBidderInformation.first();

					Address newAddressCurrentbidder = null;
					Customer currentBidder = null;

					if (currentBidderInformation.next() != false) {
						newAddressCurrentbidder = new Address(currentBidderInformation.getString("users.fullname"),
								currentBidderInformation.getString("users.country"),
								currentBidderInformation.getInt("users.postalcode"),
								currentBidderInformation.getString("users.city"),
								currentBidderInformation.getString("users.street"),
								currentBidderInformation.getString("users.number"));
						currentBidder = new Customer(currentBidderInformation.getInt("users.id"),
								currentBidderInformation.getString("users.username"),
								currentBidderInformation.getString("users.email"),
								currentBidderInformation.getString("users.password"),
								currentBidderInformation.getBytes("users.image"),
								currentBidderInformation.getDouble("users.wallet"), newAddressCurrentbidder);
					}
					Rating newSellerRating = null;
					Rating newBuyerRating = null;
					if (currentBidder != null) {
						PreparedStatement pstmtSellerRatingsEndedAuction = connection.prepareStatement(
								"Select * FROM Ratings JOIN Users ON ratings.sender_id=users.id JOIN auctions ON ratings.auction_id=auctions.auction_id WHERE users.id="
										+ newSeller.getId() + " AND auctions.auction_id=" + endedAuctionId );

						PreparedStatement pstmtBuyerEndedAuction = connection.prepareStatement(
								"Select * FROM Ratings JOIN Users ON ratings.receiver_id=users.id JOIN auctions ON ratings.auction_id=auctions.auction_id WHERE users.id="
										+ currentBidder.getId() + " AND auctions.auction_id=" + endedAuctionId);

						ResultSet allSellerRatings = pstmtSellerRatingsEndedAuction.executeQuery();
						allSellerRatings.first();		//ggf. beforeFirst checken
						ResultSet allBuyerRatings = pstmtBuyerEndedAuction.executeQuery();
						allBuyerRatings.first();

						if (allSellerRatings.next() != false) {
							String sellerText= null;
							if(allSellerRatings.getString("ratings.text")!=null) {
								sellerText=allSellerRatings.getString("ratings.text");
							}
							newSellerRating = new Rating(allSellerRatings.getInt("ratings.id"),
									allSellerRatings.getInt("ratings.stars"),
									sellerText,
									allSellerRatings.getInt("ratings.sender_id"),
									allSellerRatings.getInt("ratings.receiver_id"),
									allSellerRatings.getInt("ratings.order_id"), true);
						}

						if (allBuyerRatings.next() != false) {
							String buyerText=null;
							if(allSellerRatings.getString("ratings.text")!=null) {
								
							}
							newSellerRating = new Rating(allBuyerRatings.getInt("ratings.id"),
									allBuyerRatings.getInt("ratings.stars"), buyerText,
									allBuyerRatings.getInt("ratings.sender_id"),
									allBuyerRatings.getInt("ratings.receiver_id"),
									allBuyerRatings.getInt("ratings.order_id"), true);

						}
					}
					ShippingType shippingtype = null;
					if (endedAuctions.getInt("auctions.shippingtype_id") == 1) {
						shippingtype = ShippingType.Shipping;
					} else if (endedAuctions.getInt("auctions.shippingtype_id") == 2) {
						shippingtype = ShippingType.PickUp;
					}
					allEndedAuctionsArray[arraycounter] = new Auction(endedAuctions.getInt("auctions.auction_id"),
							endedAuctions.getString("auctions.title"), endedAuctions.getString("auctions.description"),
							endedAuctions.getBytes("auctions.image"), endedAuctions.getDouble("auctions.minbid"),
							endedAuctions.getDouble("auctions.startprice"),
							endedAuctions.getDouble("auctions.currentbid"), shippingtype, newSeller, currentBidder,
							newSellerRating, newBuyerRating,
							endedAuctions.getTimestamp("auctions.starttime").toLocalDateTime(),
							endedAuctions.getTimestamp("auctions.enddate").toLocalDateTime());

					arraycounter++;

				}

				return allEndedAuctionsArray;

			}

			else if (auctionType == AuctionType.Future) {

				LocalDateTime now = LocalDateTime.now();
				Timestamp timestamp = Timestamp.valueOf(now);

				PreparedStatement allFutureAuctions = connection.prepareStatement(
						"Select * FROM auctions " + "WHERE DATE(auctions.starttime) > '" + timestamp + "'"
						+ " AND auctions.title LIKE ?",				//? Wildcard
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				allFutureAuctions.setString(1,"%"+ searchstring+"%");
				ResultSet futureAuctions = allFutureAuctions.executeQuery();
				int sumAuctions = 0;
				int arraycounter = 0;

				while (futureAuctions.next()) {
					sumAuctions++;
				}
				
				futureAuctions.beforeFirst();
				Auction[] allFutureAuctionsArray = null;
				
				if(sumAuctions<=0)
				{
					return null;
				}
				
				allFutureAuctionsArray = new Auction[sumAuctions];
				while (futureAuctions.next()) {

					int futureAuctionId = futureAuctions.getInt("auctions.auction_id");
					PreparedStatement pstmtAllfutureAuctions = connection.prepareStatement(
							"Select * FROM auctions JOIN users ON (auctions.seller_id=users.id) WHERE auctions.auction_id="
									+ futureAuctionId,
							ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					ResultSet allSellerInformation = pstmtAllfutureAuctions.executeQuery();
					allSellerInformation.first();

					Address newAddress = new Address(allSellerInformation.getString("users.fullname"),
							allSellerInformation.getString("users.country"),
							allSellerInformation.getInt("users.postalcode"),
							allSellerInformation.getString("users.city"),
							allSellerInformation.getString("users.street"),
							allSellerInformation.getString("users.number"));
					Customer newSeller = new Customer(allSellerInformation.getInt("users.id"),
							allSellerInformation.getString("users.username"),
							allSellerInformation.getString("users.email"),
							allSellerInformation.getString("users.password"),
							allSellerInformation.getBytes("users.image"),
							allSellerInformation.getDouble("users.wallet"), newAddress);

					ShippingType shippingtype = null;
					if (futureAuctions.getInt("auctions.shippingtype_id") == 1) {
						shippingtype = ShippingType.Shipping;
					} else if (futureAuctions.getInt("auctions.shippingtype_id") == 2) {
						shippingtype = ShippingType.PickUp;
					}

					allFutureAuctionsArray[arraycounter] = new Auction(futureAuctions.getInt("auctions.auction_id"), futureAuctions.getString("auctions.title"),
							futureAuctions.getString("auctions.description"), futureAuctions.getBytes("auctions.image"),
							futureAuctions.getDouble("auctions.minbid"), futureAuctions.getDouble("auctions.startprice"),
							shippingtype, newSeller, null, futureAuctions.getDouble("auctions.currentbid"),
							futureAuctions.getTimestamp("auctions.starttime").toLocalDateTime(),
							futureAuctions.getTimestamp("auctions.enddate").toLocalDateTime());
					
					arraycounter++;
				}

				return allFutureAuctionsArray;

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	protected Response SendRating(Rating rating) {
		// Bewertung mit Sternen und Text zurückgeben
		
		// Wenn erfolgreich Response.Success zurückgeben
		// Wenn keine Verbindung zur DB: NoDBConnection zurückgeben
		// Sonstiger Fehler Response.Failure zurückgeben
		
		if (!checkConnection()) {
			return Response.NoDBConnection;
		}
		
		try {
		PreparedStatement insertRating = connection.prepareStatement("INSERT INTO ratings(order_id, auction_id, sender_id, receiver_id, stars, text) "
				+ "VALUES (?, ?, ?, ?, ?, ?)");
		
		insertRating.setInt(1, rating.getOrderId());
		insertRating.setInt(2, rating.getAuctionId());
		insertRating.setInt(3, rating.getSenderId());
		insertRating.setInt(4, rating.getReceiverId());
		insertRating.setInt(5, rating.getStars());
		insertRating.setString(6, rating.getText());
		insertRating.execute();
		return Response.Success;		
		
	} catch (SQLException e) {
		//es ist ein Fehler aufgetreten:
		e.printStackTrace();
		return Response.Failure;
	}
	}

	protected Rating[] fetchRatings(User user) {
		// Text und Punkte
		if (!checkConnection()) {
			return null;
		}
   int i = 0;
		try {
			
			PreparedStatement fetchRatingsInfo = connection.prepareStatement("SELECT * FROM ratings WHERE receiver_id=" + user.getId());

			ResultSet fetchRatingsInfoResult = fetchRatingsInfo.executeQuery();
			
			List<Rating> ratingList = new ArrayList<Rating>();
			while (fetchRatingsInfoResult.next())
			{
				//ggf tritt Exception auf, spaeter pruefen
				int orderId = fetchRatingsInfoResult.getInt("order_id");
				int auctionId = fetchRatingsInfoResult.getInt("auction_id");
				boolean isAuction;
				if(orderId>=0)
				{
					isAuction=false;
					ratingList.add(new Rating(fetchRatingsInfoResult.getInt("rating_id"), fetchRatingsInfoResult.getInt("stars"), fetchRatingsInfoResult.getString("text"), fetchRatingsInfoResult.getInt("sender_id"), fetchRatingsInfoResult.getInt("receiver_id"), orderId, isAuction));
					i = i + fetchRatingsInfoResult.getInt("stars");
				}
				else if(auctionId>=0)
				{
					isAuction=true;
					ratingList.add(new Rating(fetchRatingsInfoResult.getInt("rating_id"), fetchRatingsInfoResult.getInt("stars"), fetchRatingsInfoResult.getString("text"), fetchRatingsInfoResult.getInt("sender_id"), fetchRatingsInfoResult.getInt("receiver_id"), auctionId, isAuction));
				}
			}
			
			if(ratingList.size()<=0)
			{
				return null;
			}
			//Liste in Array umwandeln test
			Rating[] ratings = new Rating[ratingList.size()];
			ratingList.toArray(ratings);
			return ratings;
		} catch (SQLException e) {
			//es ist ein Fehler aufgetreten:
			e.printStackTrace();
			return null;
		}
	}

	protected double[] fetchAvgRating(User user) {
		// index 0 ist Average, Index 1 ist Anzahl der Bewertungen
		// Array mit Anzahl und Durchschnitt zurückgeben
		
		if (!checkConnection()) {
			return null;
		}
	
		double result = 0;
		Rating[] allRatings = fetchRatings(user);	
		if(allRatings!=null)
		{
			int numberOfRatings = allRatings.length;
			for (Rating rating : allRatings) {
				result += rating.getStars();
			}	
		
			double [] avgRatings = new double [2];
			avgRatings[0] = result / allRatings.length;
			avgRatings[1] = numberOfRatings;
			return avgRatings;
		}
		return null;
	}
	

	protected Response deleteOrder(Order order, Customer buyer) {
		// Order anhand von ID aus der Datenbank löschen
	
		// Wenn Order erfolgreich gelöscht Response.Success zurückgeben
		// Wenn keine Verbindung zu DB: Response.NoDBConnection zurückgeben
		// Verbindung herstellen, wenn keine Verbindung besteht
		LocalDateTime serverDate = LocalDateTime.now();
		// OrderID speichern
		int orderID = order.getId();
		// Order Date speichern
		LocalDateTime date = order.getDate();
		Double price = order.getProduct().getPrice();
		
		// Datum auf dd/MM/YYYY begrenzen (Stornierung nur am gleichen Tag möglich)
		String orderGetDate = date.format(SEPCommon.Constants.DATEFORMATDAYONLY);
		String ServerDate = serverDate.format(SEPCommon.Constants.DATEFORMATDAYONLY);
		
		if (!checkConnection()) {
		return Response.NoDBConnection;
		}
		
		// Order kann am gleichen Tag der Bestellung noch storniert werden
		if(orderGetDate.equals(ServerDate)) {
			try
			{
				// Order aus Datenbank löschen anahnd der ID
				Statement statement = connection.createStatement();
				statement.execute("DELETE FROM orders WHERE id ='" + orderID + "'");
							
				Seller seller = order.getProduct().getSeller();
				increaseWallet(buyer, price);
				decreaseWallet(seller, price);
				
				return Response.Success; 
				
			} catch (SQLException e) {
				// Fehler zurückgeben
				e.printStackTrace();
				return Response.Failure;
			}
		}
		// Order ist zu lange her und kann nicht mehr gelöscht werden
		else {
			return Response.OrderTooOld;
		}			
}

	protected Response checkForNewFinishedAuctions() {
		// Checken ob es beendete Auktionen gibt, zu welchen noch keine
		// Verkaufsbestätigungsemail verschickt wurde (Spalte emailsent = 0),
		// falls ja, das Guthaben des Käufers reduzieren, und das Guthaben des Verkäufers erhöhen (wie bei BuyItem)
		// und anschließend EmailHandler.sendAuctionEndedEmail versenden
		
		// Spezialfall: User hat inzwischen kein Guthaben mehr (wird beim Zeitpunkt des Gebots geprüft, aber er kann es zwischenzeitlich
		// ausgegeben haben) - in diesem Fall die Zeile currentbidder_id auf "0" und currentbid auf startprice - also keinen Käufer
		// in der DB speichern
		
		if (!checkConnection()) {
			return Response.NoDBConnection;
		}

		EmailHandler.sendAuctionEndedEmail(null);
		
		//bei o.g. Spezialfall:
		EmailHandler.sendAuctionEndedBuyerNoBalanceEmail(null);
		
		//nach dem Senden der Email emailsent bei der jeweiligen Aktion auf 1 setzen
		return null;
	}

	public static void main(String[]args) {
		SQL testSQLObject= new SQL();
		 LocalDateTime aDateTime = LocalDateTime.of(2018, 
                 Month.JULY, 29, 19, 30, 00);
		 LocalDateTime aDateTime2 = LocalDateTime.of(2021, 
                 Month.JULY, 30, 19, 30, 00);
		testSQLObject.addAuction(new Auction(200, "Hallo Beispiel", "Beispielhafte Beschreibung", new byte[1], 20.55, 20.00, ShippingType.PickUp, new Customer(100, "name", "", "", null, 20, null), new Customer(100, "name", "", "", null, 20, null), 20.55,aDateTime,aDateTime2));
	//	testSQLObject.fetchAuctions(AuctionType.Ended);
	//	testSQLObject.fetchAuctions(AuctionType.Active);
		 User denis= new Customer(100, null, null, null, null, 0, null);
		//testSQLObject.fetchAuctions(AuctionType.Future);
		testSQLObject.fetchAuctions(AuctionType.Active);
	}
}