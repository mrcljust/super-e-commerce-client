package SEPServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		// Die Methode erm�glicht eine Registrierung auf der Plattform
		// Passwort, Emailvorgaben usw. werden clientseitig gepr�ft
		
		// wenn User erfolgreich registriert wurde wird Response.Success zur�ck gegeben
		// wenn Email vergeben: Response.Emailtaken zur�ckgeben
		// wenn User vergeben: Response.UsernameTaken zur�ckgeben
		// wenn keine Verbindung zu DB: Response.NoDBConnection zur�ckgeben
		// wenn Bild zu gro�: Response.ImageTooBig zur�ckgeben
		
		// Verbindung herstellen, wenn keine Verbindung besteht
		if (!checkConnection())
		{
			return Response.NoDBConnection;
		}
		
		if(user instanceof Seller)
		{
			// Registrierung Gewerbekunde
			
			// User-Objekt �bergeben
			Seller seller = (Seller)user;
			
			// Pr�fen, ob Email oder Username schon existieren
			try
			{
				// SQL Abfrage 
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email=?");
				// F�r Email ? die �bergebene Email einsetzten und schauen ob ein Eintrag schon vorliegt
				statement.setString(1, seller.getEmail());
				ResultSet emailQuery = statement.executeQuery();
				boolean emailHasEntries = emailQuery.next();
				if(emailHasEntries)
				{
					// 1. Fall: Email vergeben
					return Response.EmailTaken;
				}
			} catch (SQLException e) {
				// Fehlerfall dokumentieren und Fehlermeldung zur�ckgeben
				e.printStackTrace();
				return Response.NoDBConnection;
			}
			
			try
			{
				// SQL Abfrage 
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username=?");
				// Gleiche Pr�fung f�r Username
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
				
				// ? Values mit �bergebenen Daten f�llen (auch Adress wird gef�llt)
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
				
				//Bild einf�gen (Bild ist optional)
				if(seller.getPicture()!=null)
				{
					stmt.setBytes(11, seller.getPicture());
				}
				else
				{
					// Seller m�chte kein Profilbild
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
			// User-Objekt �bergeben
			Customer customer = (Customer)user;
			
			//Pr�fen, ob Email oder Username schon existieren
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
				
				
				// ? Values mit �bergebenen Daten f�llen (auch Adresse wird gef�llt)
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
				
				//Bild einf�gen (ist optional)
				if(customer.getPicture()!=null)
				{
					// Seller m�chte kein Profilbild
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
		// wenn User erfolgreich eingeloggt wurde Response.Success zur�ckgeben
		// wenn Username / Email nicht gefunden, oder wenn das eingegebene Passwort dazu nicht passt Response.Failure zur�ckgeben
		// wenn keine Verbindung zu DB: Response.NoDBConnection zur�ckgeben
		
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
				// Email und Passwort �bergeben
				statement.setString(1, emailOrUsername);
				statement.setString(2, password);
				ResultSet loginQuery = statement.executeQuery();
				
				// Eingabedaten pr�fen
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
				// Username und �asswort �bergeben
				statement.setString(1, emailOrUsername);
				statement.setString(2, password);
				ResultSet loginQuery = statement.executeQuery();
				
				// Eingabedaten pr�fen
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
		// Anhand der Email in der DB das entsprechende User-Objekt suchen und ein vollst�ndiges User-Objekt mit Id und allen anderen Werten aus der DB zur�ckgeben
		
		// Wenn Userdaten erfolgreich gefetcht, User-Objekt zur�ckgeben
		// wenn keine Verbindung zu DB: null zur�ckgeben
		// wenn sonstiger Fehler auftritt ggf. null zur�ckgeben
		
		// Verbindung herstellen, wenn keine Verbindung besteht
		if (!checkConnection())
		{
			return null;
		}
		
		try
		{
			// SQL Abfrage
			PreparedStatement userDataStatement = connection.prepareStatement("SELECT * FROM users WHERE email=?");
			
			// ? Values f�llen
			userDataStatement.setString(1, email);
			ResultSet userDataQuery = userDataStatement.executeQuery();
			
			// Email pr�fen
			if(userDataQuery.next())
			{
				// Privat- oder Gewerbekunde?
				String accountType = userDataQuery.getString("type");
				Address address = new Address(userDataQuery.getString("fullname"), userDataQuery.getString("country"), userDataQuery.getInt("postalcode"), userDataQuery.getString("city"), userDataQuery.getString("street"), userDataQuery.getString("number"));
				
				// Privatkunde
				if(accountType.equals("Customer"))
				{
					Customer customer = new Customer(userDataQuery.getInt("id"), userDataQuery.getString("username"), userDataQuery.getString("email"), userDataQuery.getString("password"), userDataQuery.getBytes("image"), userDataQuery.getDouble("wallet"), address);
					// Privatkunden-Obejekt zur�ckgeben
					return customer;
				}
				
				// Gewerbekunde
				else if(accountType.equals("Seller"))
				{
					Seller seller = new Seller(userDataQuery.getInt("id"), userDataQuery.getString("username"), userDataQuery.getString("email"), userDataQuery.getString("password"), userDataQuery.getBytes("image"), userDataQuery.getDouble("wallet"), address, userDataQuery.getString("companyname"));
					// Gewerbekunden-Objekt zur�ckgeben
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
		// Analog zur vorherigen Methode: Anhand des Username in der DB das entsprechende User-Objekt suchen und ein vollst�ndiges User-Objekt mit Id und allen anderen Werten aus der DB zur�ckgeben
		
		// Wenn Userdaten erfolgreich gefetcht, User-Objekt zur�ckgeben
		// wenn keine Verbindung zu DB: null zur�ckgeben
		// wenn sonstiger Fehler auftritt ggf. null zur�ckgeben
		
		// Verbindung herstellen, wenn keine Verbindung besteht
		if (!checkConnection())
		{
			return null;
		}
		
		try
		{
			// SQL Abfrage
			PreparedStatement userDataStatement = connection.prepareStatement("SELECT * FROM users WHERE username=?");


			// ? Values f�llen
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
					// Privatkunden-Objekt zur�ckgeben
					return customer;
				}
				
				// Gewerbekunde
				else if(accountType.equals("Seller"))
				{
					Seller seller = new Seller(userDataQuery.getInt("id"), userDataQuery.getString("username"), userDataQuery.getString("email"), userDataQuery.getString("password"), userDataQuery.getBytes("image"), SEPCommon.Methods.round(userDataQuery.getDouble("wallet"), 2), address, userDataQuery.getString("companyname"));
					// Gewerbekunden-Objekt zur�ckgeben
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
		// User anhand ID in Datenbank finden und alle Werte mit den Werten des Objekts users �berschreiben 
 
		// Wenn User erfolgreich abge�ndert wurden Response.Success zur�ckgeben
		// wenn keine Verbindung zu DB: Response.NoDBConnection zur�ckgeben
		// wenn sonstiger Fehler auftritt ggf. Response.Failure zur�ckgeben

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
					
			// Pr�fen, ob Email oder Username bei einem anderen Benutzer schon existieren, da diese beiden unique sind
			try
			{
				// SQL Abfrage 
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email=?");
				// ? Values f�llen
				statement.setString(1, seller.getEmail());
				ResultSet emailQuery = statement.executeQuery();
				boolean emailHasEntries = emailQuery.next();
				if(emailHasEntries)
				{
					//Pr�fen, ob Email f�r diesen User oder f�r anderen vergeben
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
				// ? Values f�llen
				statement.setString(1, seller.getUsername());
				ResultSet usernameQuery = statement.executeQuery();
				boolean usernameHasEntries = usernameQuery.next();
				if(usernameHasEntries)
				{
					//Pr�fen, ob Email f�r diesen User oder f�r anderen vergeben
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
			
			// Pr�fen, ob Email oder Username bei einem anderen Benutzer schon existieren, da diese beiden unique sind
			try
			{
				// SQL Abfrage 
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email=?");
				// ? Values f�llen
				statement.setString(1, customer.getEmail());
				ResultSet emailQuery = statement.executeQuery();
				boolean emailHasEntries = emailQuery.next();
				if(emailHasEntries)
				{
					//Pr�fen, ob Email f�r diesen User oder f�r anderen vergeben
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
				// ? Values f�llen
				statement.setString(1, customer.getUsername());
				ResultSet usernameQuery = statement.executeQuery();
				boolean usernameHasEntries = usernameQuery.next();
				if(usernameHasEntries)
				{
					//Pr�fen, ob Email f�r diesen User oder f�r anderen vergeben
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
				System.out.println(stmt);
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
		// User anhand ID aus der Datenbank l�schen

		// Wenn User erfolgreich gel�scht Response.Success zur�ckgeben
		// wenn keine Verbindung zu DB: Response.NoDBConnection zur�ckgeben
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
					// Zuerst Produkte des Anbieters und dann den Anbieter selbst l�schen
					Statement statement = connection.createStatement();
					statement.execute("DELETE FROM products WHERE seller_id ='" + userId + "'");
					statement.execute("DELETE FROM users WHERE id ='" + userId + "'");
					return Response.Success;
		   
			} catch (SQLException e) {
				// Fehler zur�ckgeben
				return Response.Failure;
			}
		}
		
		// Privatkunde
		else 
		{
				try
				{
					Statement statement = connection.createStatement();
					// Bei Privatkunden muss nur der User selbst gel�scht werden
					statement.execute("DELETE FROM users WHERE id ='" + userId + "'");
					return Response.Success;
				
				} catch (SQLException e) {
					// Fehler zur�ckgeben
					return Response.Failure;
				}
		}
	}

	protected Response increaseWallet(User user, double MoreMoney) {
		// Wallet anhand User-ID in der Datenbank um den Betrag amount erh�hen

		// Wenn Wallet erfolgreich erh�ht Response.Success returnen
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
			while (AllProducts.next()) {  //Tupel Z�hlen
				sqlcounter++;
			}
			ResultSet AllProducts2= pstmt.executeQuery(); // nach der 1 Schleife pointer zeigt auf Null -> ggf k�nnte man pointer resetten
			

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
			System.out.println("connection probleme");
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

			ResultSet AllProductsByCategory2 = statement.executeQuery(); // nach der 1 Schleife pointer zeigt auf Null -> ggf k�nnte man pointer resetten, NCAR
			

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
			
			ResultSet AllProductsByFullString2 = pstmt.executeQuery(); // nach der 1 Schleife pointer zeigt auf Null -> ggf k�nnte man pointer resetten(?) NCAR
			
			
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
		//Anschlie�end Produkt-Array der betroffenen Produkt-IDs ausgeben
		
		//Wenn erfolgreich gefetcht, Product-Array returnen
		//wenn keine Verbindung zu DB: null returnen
		//wenn sonstiger Fehler auftritt (keine Produkte angesehen o.�.) ggf. null returnen
		
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
				lastViewedProducts = new Product[lastViewedIds.length]; //R�ckgabearray mit Gr��e der Anzahl der IDs im Array
				
				int newArrayCounter = 0;
				for(String viewedIdStr : lastViewedIds)
				{
					try {
					int viewedId = Integer.parseInt(viewedIdStr);
					
					//F�r jede ID im Array lastViewedIds, die Produkdaten aus der DB holen
					//anschlie�end jeweils ein Product-Object anhand der gefetchten Daten aus der DB erstellen
					//und in das Array lastViewedProducts, welches am Ende zur�ckgegeben wird schreiben
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
						//Produkt mittlerweile gel�scht
						//ignorieren, f�r diese ID kein Produkt (null) in das Array schreiben.
						lastViewedProducts[newArrayCounter] = null;
					}
				}
				return lastViewedProducts;
			}
			else
			{
				//kein Entry mit der UserId - eigentlich nicht m�glich.
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Response addLastViewedProduct(int viewedProductId, User user) {
		//viewedProductId zu zuletzt betrachtete Produkt-IDs des Users user in der DB hinzuf�gen (max. 10 zuletzt betrachtete IDs).
		
		//Wenn erfolgreich hinzugef�gt, Response.Success returnen
		//wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		//wenn sonstiger Fehler auftritt ggf. Response.Failure returnen
		
		//Verbindung herstellen, wenn keine Verbindung besteht
		if (!checkConnection())
		{
			return Response.NoDBConnection;
		}
		
		//Aktuelle zuletzt angesehene Produkte holen, um zu entscheiden, ob eines ersetzt werden muss oder nur hinzugef�gt werden muss
		Product[] currentLastViewedProducts = fetchLastViewedProducts(user);
		
		String newLastViewedProductsString = "";
		if(currentLastViewedProducts!=null)
		{
			if(currentLastViewedProducts.length==10)
			{
				//Maximale L�nge (10), setze viewedProductId an den Anfang und ersetze die letzte Id
				newLastViewedProductsString += String.valueOf(viewedProductId);
				
				for(int i=0;i<9;i++)
				{
					if(currentLastViewedProducts[i]!=null)
					{
						//wenn currentLastViewedProducts[i] = null, ist das Produkt gel�scht. Dann ist es aus der Liste der zuletzt aufgerufenen Produkte zu entfernen
						newLastViewedProductsString += "," + String.valueOf(currentLastViewedProducts[i].getId());
					}
				}
			}
			else
			{
				//Maximale L�nge (10) noch nicht erreicht, setze viewedProductId an den Anfang und schiebe ggf. die anderen ein Feld nach hinten
				newLastViewedProductsString += String.valueOf(viewedProductId);
				
				for(int i=0;i<currentLastViewedProducts.length;i++)
				{
					if(currentLastViewedProducts[i]!=null)
					{
						//wenn currentLastViewedProducts[i] = null, ist das Produkt gel�scht. Dann ist es aus der Liste der zuletzt aufgerufenen Produkte zu entfernen
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
		
		//zun�chst �berpr�fen, ob Kategorie bereits existiert:
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
		
		//Ung�ltigkeit usw. wird clientseitig gepr�ft
		
		int sellerid = seller.getId();
		
		for(Product p : products)
		{
			//F�r jedes Produkt p pr�fen ob Kategorie existiert, wenn ja ID auslesen, ansonsten Kategorie anlegen
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
					
					//ID nach Anlegen der Kategorie auslesen (Query selectCategoryID erneut ausf�hren)
					selectCategoryIDResult = selectCategoryID.executeQuery();
					if(selectCategoryIDResult.next())
					{
						categoryid = selectCategoryIDResult.getInt("id");
					}
					else
					{
						//Kategorie existiert immer noch nicht (sollte nicht auftreten, da schon eine Exception aufgetreten w�re)
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
		// buyer, die seller_id, Preis, Produktinfos k�nnen dem Objekt product entnommen werden

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
			
			//in der DB pr�fen, ob das Guthaben ausreicht
			if (wallettemp - product.getPrice() < 0) {
				return Response.InsufficientBalance;
			} else {
				//Guthaben reicht aus.
				//Guthaben beim K�ufer vermindern
				//Guthaben beim Verk�ufer erh�hen
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

	protected Response addAuction(Auction auction) {				//done sofern keine weitere nicht ber�cksichtigte F�lle (Check L1300)
		if (!checkConnection()) {
			return null;
		}
		try {
			PreparedStatement pstmt=connection.prepareStatement("INSERT INTO auctions(currentbid, currentbidder_id, description, emailsent, enddate, image, minbid, seller_id, shippingtype_id, startprice, starttime, title)"
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,)");

			pstmt.setDouble(1, auction.getCurrentBid());
			pstmt.setInt(2, auction.getCurrentBidder().getId());
			pstmt.setString(3, auction.getDescription());
			pstmt.setBoolean(4, false);
			pstmt.setDate(5, (java.sql.Date) auction.getEnddate()); // cast weil unterschiedliche arten von date in java und sql
			pstmt.setBytes(6, auction.getSeller().getPicture());
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
			pstmt.setDouble(10, auction.getMinBid());
			pstmt.setDate(11, (java.sql.Date) auction.getStarttime());		//cast unterschiedliche Dates
			pstmt.setString(12, auction.getTitle());

			pstmt.execute();

			return Response.Success;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.Failure;
		}

	}

	protected Response sendBid(Auction auction, Customer bidder, double bid) {
		// Das Objekt bidder bietet auf das Objekt auction die Menge bid
		// 1. Fall Auktion bereits beendet
		// 2. Fall Bid ist zu niedrig
		// 3. Fall Bid ist in Ordnung und die Auktion l�uft noch
		
		//Quelle: https://stackoverflow.com/questions/12584992/how-to-get-current-server-time-in-java#:~:text=If%20you%20like%20to%20return,retrieve%20the%20current%20system%20time.
		//Autor: Aaron Blenkush
		// Edited: Jan 8'14 at 18:47
		Date serverDate= new Date();
	//	SimpleDateFormat df= new SimpleDateFormat("dd/MM/YYYY HH:mm a");
		//String currentServerDate= df.format(serverDate);
		
		
		Date endDate = auction.getEnddate();
		Date startDate=auction.getStarttime();
		//SimpleDateFormat auctionEndDate = new SimpleDateFormat("dd/MM/YYYY HH:mm a");
		//String endDate = auctionEndDate.format(date);

		if (!checkConnection()) {
			return Response.NoDBConnection;
		}

		else if (serverDate.after(startDate)&& serverDate.before(endDate)) { // 1.Fall CurrentServerDate ist vor Enddatum (alles gut)
			if (auction.getCurrentBid() >= bid) {
				return Response.BidTooLow;
			} else if (decreaseWallet(bidder, bid) == Response.Failure) {
				return Response.InsufficientBalance;
			} else {
				if (bid > auction.getCurrentBid() && decreaseWallet(bidder, bid) == Response.Success) {
					try {
						PreparedStatement pstmt = connection
								.prepareStatement("UPDATE auctions SET currentbid=?, currentbidder_id=? VALUES(?,?)");
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
			}
		} else {
			return Response.AuctionAlreadyEnded;
		}
	}

	protected Response saveAuction(User buyer, Auction auction) {
		// Auktion in die Merkliste des Objekt buyer setzen
		if (!checkConnection()) {
			return Response.NoDBConnection;
		}

		return null;
	}

	protected Order[] fetchOrders(User buyer) {
		// Alle orders die das Objekt buyer gekauft hat in einem Order-Array
		// zur�ckgeben.
		// Wenn erfolgreich gefetcht, Product-Array returnen
		// wenn keine Verbindung zu DB: null returnen
		// wenn sonstiger Fehler auftritt (keine Produkte angesehen o.�.) ggf. null
		// returnen
		// order array bef�llen daf�r braucht man: Tabelle:orders(orderid), products f�r
		// product Objekt, sellerrating, buyerrating
		// �berpr�fen ob Ratinhs vorhanden, dann sql statement etc ansonsten objekt=null

		if (!checkConnection()) {
			return null;
		}
		try {

			PreparedStatement pstmtOrders = connection.prepareStatement("SELECT * \r\n" + "FROM users\r\n"
					+ "JOIN orders\r\n" + "ON users.id=orders.buyer_id\r\n" + "JOIN products\r\n"
					+ "ON products.id=orders.product_id" + "WHERE users.id=" + buyer.getId());

			int arraycounterAllOrders = 0;
			int sqlcounterAllOrders = 0;
			ResultSet allOrdersResultSet = pstmtOrders.executeQuery();

			while (allOrdersResultSet.next()) { // Tupel z�hlen
				sqlcounterAllOrders++;
			}
			allOrdersResultSet.beforeFirst(); // zur�cksetzen des pointers auf 0
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
						newSeller, allOrdersResultSet.getString("categories.title"),
						allOrdersResultSet.getString("products.description"));

				int orderId = allOrdersResultSet.getInt("orders.order_id");
				PreparedStatement pstmtBuyerRatings = connection.prepareStatement(
						"Select * FROM Ratings JOIN Users ON ratings.sender_id=users.id JOIN orders ON ratings.order_id="
								+ orderId + "WHERE users.id=" + buyer.getId());

				PreparedStatement pstmtSellerRatings = connection.prepareStatement(		
						"Select * FROM Ratings JOIN Users ON ratings.receiver_id=users.id JOIN orders ON ratings.order_id="
								+ orderId + "WHERE users.id=" + newSeller.getId());

				ResultSet allBuyerRatings = pstmtBuyerRatings.executeQuery();
				ResultSet allSellerRatings = pstmtSellerRatings.executeQuery();

				Rating newBuyerRating = null;
				if (allBuyerRatings.next() != false) {
					newBuyerRating = new Rating(allBuyerRatings.getInt("ratings.id"),
							allBuyerRatings.getInt("ratings.stars"), allBuyerRatings.getString("ratings.text"), // selbst wenn String bspw leer, wird leer sein
							allBuyerRatings.getInt("ratings.sender_id"),
							allBuyerRatings.getInt("ratings.receiver_id"), allBuyerRatings.getInt("ratings.order_id"),
							false);
				}

				Rating newSellerRating = null;
				if (allSellerRatings.next() != false) {

					newSellerRating = new Rating(allSellerRatings.getInt("ratings.id"),
							allSellerRatings.getInt("ratings.stars"), null,
							allSellerRatings.getInt("ratings.sender_id"),
							allSellerRatings.getInt("ratings.receiver_id"), allSellerRatings.getInt("ratings.order_id"),
							false);

				}

				allOrdersArray[arraycounterAllOrders] = new Order(allOrdersResultSet.getInt("orders.id"), newProduct,
						allOrdersResultSet.getDate("orders.purchasedate"), newBuyerRating, newSellerRating);

				arraycounterAllOrders++;

			}
			return allOrdersArray;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	protected Auction[] fetchPurchasedAuctions(User buyer) {
		// selbst gekaufte Auktionen (beendet)
		if (!checkConnection()) {
			return null;
		}

		return null;
	}

	protected Auction[] fetchAuctions(AuctionType auctionType) {
		// je nach AuctionType alle aktuell laufenden, beendeten oder zuk�nftigen
		// Auktionen zur�ckgeben

		// AuctionType:
		// AuctionType.Active = aktive Auktionen -> serverzeit >= starttime AND serverzeit <=endttime 
		// AuctionType.Ended = beendete Auktionen -> serverzeit > endtime
		// AuctionType.Future = zuk�nftige Auktionen -> serverzeit < starttime
		Date serverDate= new Date();
		if (!checkConnection()) {
			return null;
		}
		try {
			if (auctionType == AuctionType.Active) {

				PreparedStatement sqlTime;
				sqlTime = connection.prepareStatement("Select * FROM auctions");

				Date sqlStartTime = sqlTime.getResultSet().getDate("auctions.starttime");
				Date sqlEndTime = sqlTime.getResultSet().getDate("auctions.enddate");
				PreparedStatement pstmtAllActiveAuctions = connection.prepareStatement(
						"Select * FROM auctions JOIN shippingtype ON auctions.shippingtype_id=shippingtype.id JOIN users ON auctions.seller_id=users.id WHERE"
								+ serverDate + ">=" + sqlStartTime + "AND" + serverDate + " <=" + sqlEndTime);

				int arraycounterAllActiveAuctions = 0;
				int sqlcounterAllActiveAuctions = 0;
				ResultSet allActiveAuctionsResultSet = pstmtAllActiveAuctions.executeQuery();
				while (allActiveAuctionsResultSet.next()) { // Tupel z�hlen
					sqlcounterAllActiveAuctions++;
				}
				allActiveAuctionsResultSet.beforeFirst(); // zur�cksetzen des pointers auf 0
				Auction[] allActiveAuctionsArray = new Auction[sqlcounterAllActiveAuctions];
				while(allActiveAuctionsResultSet.next()) {
					Address newAddress = new Address(allActiveAuctionsResultSet.getString("users.fullname"),
							allActiveAuctionsResultSet.getString("users.country"), allActiveAuctionsResultSet.getInt("users.postalcode"),
							allActiveAuctionsResultSet.getString("users.city"), allActiveAuctionsResultSet.getString("users.street"),
							allActiveAuctionsResultSet.getString("users.number"));
					Customer newSeller = new Customer(allActiveAuctionsResultSet.getInt("users.id"),
							allActiveAuctionsResultSet.getString("users.username"), allActiveAuctionsResultSet.getString("users.email"),
							allActiveAuctionsResultSet.getString("users.password"), allActiveAuctionsResultSet.getBytes("users.image"),
							allActiveAuctionsResultSet.getDouble("users.wallet"), newAddress);
					
					int thisAuctionId= allActiveAuctionsResultSet.getInt("auctions.currentbidder_id");
					int currentBidderId = pstmtAllActiveAuctions.getResultSet().getInt("auctions.currentbidder_id");
					
					PreparedStatement pstmtCurrentBidder= connection.prepareStatement("Select * FROM auctions JOIN users ON users.id="+ currentBidderId+ "WHERE auctions.auction_id="+ thisAuctionId);
					ResultSet currentBidderInformation=pstmtCurrentBidder.executeQuery();
					
					Address newAddressCurrentbidder=null;
					Customer currentBidder=null;
					
					if(currentBidderInformation.next()!=false) {
					 newAddressCurrentbidder = new Address(currentBidderInformation.getString("users.fullname"),
								currentBidderInformation.getString("users.country"), currentBidderInformation.getInt("users.postalcode"),
								currentBidderInformation.getString("users.city"), currentBidderInformation.getString("users.street"),
								currentBidderInformation.getString("users.number"));
					 currentBidder = new Customer(currentBidderInformation.getInt("users.id"),
								currentBidderInformation.getString("users.username"),
								currentBidderInformation.getString("users.email"),
								currentBidderInformation.getString("users.password"),
								currentBidderInformation.getBytes("users.image"),
								currentBidderInformation.getDouble("users.wallet"), newAddressCurrentbidder);
					}
					
					ShippingType shippingtype=null;
					if(allActiveAuctionsResultSet.getInt("auctions.shippingtype_id")==1) {
						shippingtype=ShippingType.Shipping;
					}
					else if(allActiveAuctionsResultSet.getInt("auctions.shippingtype_id")==2) {
						shippingtype=ShippingType.PickUp;
					}
					allActiveAuctionsArray[arraycounterAllActiveAuctions] = new Auction(
							allActiveAuctionsResultSet.getInt("auctions.auction_id"),
							allActiveAuctionsResultSet.getString("auctions.title"),
							allActiveAuctionsResultSet.getString("auctions.description"),
							allActiveAuctionsResultSet.getBytes("auctions.image"),
							allActiveAuctionsResultSet.getDouble("auctions.startprice"), shippingtype, newSeller,
							currentBidder, allActiveAuctionsResultSet.getDouble("auctions.currentbid"),
							allActiveAuctionsResultSet.getDate("auctions.starttime"),
							allActiveAuctionsResultSet.getDate("auctions.enddate"));
	
					arraycounterAllActiveAuctions++;
				}
			}

			
			
			
			else if (auctionType == AuctionType.Ended) {
				PreparedStatement sqlTime = connection.prepareStatement("Select * FROM auctions");
				Date sqlEndTime = sqlTime.getResultSet().getDate("auctions.enddate");
				PreparedStatement pstmtAllEndedAuctions = connection.prepareStatement(
						"Select * FROM auctions JOIN shippingtype ON auctions.shippingtype_id=shippingtype.id JOIN users ON auctions.seller_id=users.id WHERE"
								+ serverDate + ">" + sqlEndTime);

				int arraycounterAllEndedAuctions = 0;
				int sqlcounterAllEndedAuctions = 0;
				ResultSet allEndedAuctionsResultSet = pstmtAllEndedAuctions.executeQuery();
				while (allEndedAuctionsResultSet.next()) { // Tupel z�hlen
					sqlcounterAllEndedAuctions++;
				}
				allEndedAuctionsResultSet.beforeFirst(); // zur�cksetzen des pointers auf 0
				Auction[] allEndedAuctionsArray = new Auction[sqlcounterAllEndedAuctions];


				while (allEndedAuctionsResultSet.next()) {
					Address newAddress = new Address(allEndedAuctionsResultSet.getString("users.fullname"),
							allEndedAuctionsResultSet.getString("users.country"),
							allEndedAuctionsResultSet.getInt("users.postalcode"),
							allEndedAuctionsResultSet.getString("users.city"),
							allEndedAuctionsResultSet.getString("users.street"),
							allEndedAuctionsResultSet.getString("users.number"));
					Customer newSeller = new Customer(allEndedAuctionsResultSet.getInt("users.id"),
							allEndedAuctionsResultSet.getString("users.username"),
							allEndedAuctionsResultSet.getString("users.email"),
							allEndedAuctionsResultSet.getString("users.password"),
							allEndedAuctionsResultSet.getBytes("users.image"),
							allEndedAuctionsResultSet.getDouble("users.wallet"), newAddress);

					int thisAuctionId = allEndedAuctionsResultSet.getInt("auctions.currentbidder_id");
					int currentBidderId = pstmtAllEndedAuctions.getResultSet().getInt("auctions.currentbidder_id");
					
					PreparedStatement pstmtCurrentBidder = connection
							.prepareStatement("Select * FROM auctions JOIN users ON users.id=" + currentBidderId
									+ "WHERE auctions.auction_id=" + thisAuctionId);
					ResultSet currentBidderInformation = pstmtCurrentBidder.executeQuery();

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

					int auctionId = allEndedAuctionsResultSet.getInt("auctions.auction_id");

					PreparedStatement pstmtSellerRatingsEndedAuction = connection.prepareStatement(
							"Select * FROM Ratings JOIN Users ON ratings.sender_id=users.id JOIN auctions ON ratings.auction_id="
									+ auctionId + "WHERE users.id=" + newSeller.getId());

					PreparedStatement pstmtBuyerEndedAuction = connection.prepareStatement(
							"Select * FROM Ratings JOIN Users ON ratings.receiver_id=users.id JOIN auctions ON ratings.order_id="
									+ auctionId + "WHERE users.id=" + currentBidder.getId());

					ResultSet allSellerRatings = pstmtSellerRatingsEndedAuction.executeQuery();
					ResultSet allBuyerRatings = pstmtBuyerEndedAuction.executeQuery();

					Rating newSellerRating = null;
					if (allSellerRatings.next() != false) {
						newSellerRating = new Rating(allSellerRatings.getInt("ratings.id"),
								allSellerRatings.getInt("ratings.stars"), allSellerRatings.getString("ratings.text"), //selbst wenn String leer ist wird bef�llt
								allSellerRatings.getInt("ratings.sender_id"),
								allSellerRatings.getInt("ratings.receiver_id"),
								allSellerRatings.getInt("ratings.order_id"), false);
					}

					Rating newBuyerRating = null;
					if (allBuyerRatings.next() != false) {

						newSellerRating = new Rating(allBuyerRatings.getInt("ratings.id"),
								allBuyerRatings.getInt("ratings.stars"), null,
								allBuyerRatings.getInt("ratings.sender_id"),
								allBuyerRatings.getInt("ratings.receiver_id"),
								allBuyerRatings.getInt("ratings.order_id"), false);

					}
					ShippingType shippingtype = null;
					if (allEndedAuctionsResultSet.getInt("auctions.shippingtype_id") == 1) {
						shippingtype = ShippingType.Shipping;
					} else if (allEndedAuctionsResultSet.getInt("auctions.shippingtype_id") == 2) {
						shippingtype = ShippingType.PickUp;
					}

					allEndedAuctionsArray[arraycounterAllEndedAuctions] = new Auction(
							allEndedAuctionsResultSet.getInt("auctions.auction_id"),
							allEndedAuctionsResultSet.getString("auctions.title"),
							allEndedAuctionsResultSet.getString("auctions.description"),
							allEndedAuctionsResultSet.getBytes("auctions.image"),
							allEndedAuctionsResultSet.getInt("auctions.startprice"),
							allEndedAuctionsResultSet.getInt("auctions.currentbid"), shippingtype, newSeller,
							currentBidder, newSellerRating, newBuyerRating,
							allEndedAuctionsResultSet.getDate("auctions.starttime"),
							allEndedAuctionsResultSet.getDate("auctions.enddate"));
				}
				arraycounterAllEndedAuctions++;
				// test
			} else if (auctionType == AuctionType.Future) {
				PreparedStatement sqlTime = connection.prepareStatement("Select * FROM auctions");
				Date sqlStartTime = sqlTime.getResultSet().getDate("auctions.starttime");
				PreparedStatement pstmtAllFutureAuctions = connection.prepareStatement(
						"Select * FROM auctions JOIN shippingtype ON auctions.shippingtype_id=shippingtype.id JOIN users ON auctions.seller_id=users.id WHERE"
								+ serverDate + "<" + sqlStartTime);
				int arraycounterAllFutureAuctions = 0;
				int sqlcounterAllOrders = 0;
				ResultSet allFutureAuctionsResultSet = pstmtAllFutureAuctions.executeQuery();
				while (allFutureAuctionsResultSet.next()) { // Tupel z�hlen
					sqlcounterAllOrders++;
				}
				allFutureAuctionsResultSet.beforeFirst(); // zur�cksetzen des pointers auf 0
				Auction[] allFutureAuctionsArray = new Auction[sqlcounterAllOrders];

				while (allFutureAuctionsResultSet.next()) {

					Address newAddress = new Address(allFutureAuctionsResultSet.getString("users.fullname"),
							allFutureAuctionsResultSet.getString("users.country"),
							allFutureAuctionsResultSet.getInt("users.postalcode"),
							allFutureAuctionsResultSet.getString("users.city"),
							allFutureAuctionsResultSet.getString("users.street"),
							allFutureAuctionsResultSet.getString("users.number"));
					Customer newSeller = new Customer(allFutureAuctionsResultSet.getInt("users.id"),
							allFutureAuctionsResultSet.getString("users.username"),
							allFutureAuctionsResultSet.getString("users.email"),
							allFutureAuctionsResultSet.getString("users.password"),
							allFutureAuctionsResultSet.getBytes("users.image"),
							allFutureAuctionsResultSet.getDouble("users.wallet"), newAddress);

					ShippingType shippingtype = null;
					if (allFutureAuctionsResultSet.getInt("auctions.shippingtype_id") == 1) {
						shippingtype = ShippingType.Shipping;
					} else if (allFutureAuctionsResultSet.getInt("auctions.shippingtype_id") == 2) {
						shippingtype = ShippingType.PickUp;
					}
					allFutureAuctionsArray[arraycounterAllFutureAuctions] = new Auction(
							allFutureAuctionsResultSet.getString("auctions.title"),
							allFutureAuctionsResultSet.getString("auctions.description"),
							allFutureAuctionsResultSet.getBytes("auctions.image"),
							allFutureAuctionsResultSet.getDouble("auctions.minbid"), shippingtype, newSeller,
							allFutureAuctionsResultSet.getDate("auctions.starttime"),
							allFutureAuctionsResultSet.getDate("auctions.enddate"));

					arraycounterAllFutureAuctions++;
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	

		return null;
	}
	
	protected Auction[] fetchOwnAuctions(User buyer) {
		// selbst eingestellte Auktionen (aktuell + beendete + zuk�nftige)
		if (!checkConnection()) {
			return null;
		}

		return null;
	}

	protected Auction[] fetchAuctionsUserBiddedOn(User buyer) {
		// Auktionen auf die buyer geboten hat (aktuell + beendete)
		if (!checkConnection()) {
			return null;
		}

		return null;
	}

	protected Auction[] fetchSavedAuctions(User buyer) {
		// Auktionen die buyer gespeichert hat (aktuell + beendete + zuk�nftige)
		
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
				
				String saved = fetchSavedAuctionsIdsResult.getString("saved");
				if(saved=="" || saved==null || saved.isEmpty() || saved.isBlank())
					return null; //keine Auktionen bisher gespeichert
				
				String[] savedAuctionsIds = saved.split(","); //in der DB sind die IDs durch "," seppariert, daher splitten und Array der IDs erstellen
				savedAuctions = new Auction[savedAuctionsIds.length]; //R�ckgabearray mit Gr��e der Anzahl der IDs im Array
				
				int newArrayCounter = 0;
				for(String viewedIdStr : savedAuctionsIds) 
				{
					try {
						int viewedId = Integer.parseInt(viewedIdStr);
						
						//F�r jede ID im Array saveAuctionsId, die Auktionsdaten aus der DB holen
						//anschlie�end jeweils ein Auction-Object anhand der gefetchten Daten aus der DB erstellen
						//und in das Array savedAuctions, welches am Ende zur�ckgegeben wird schreiben
						PreparedStatement fetchAuctionInfo = connection.prepareStatement("SELECT * FROM auctions JOIN users ON (auctions.seller_Id = users.id) WHERE auctions.id='" + viewedId + "'");
						ResultSet fetchAuctionsInfoResult = fetchAuctionInfo.executeQuery();
						if(fetchAuctionsInfoResult.next())
						{
							
							Address address = new Address(fetchAuctionsInfoResult.getString("users.fullname"),
									fetchAuctionsInfoResult.getString("users.country"), fetchAuctionsInfoResult.getInt("users.postalcode"),
									fetchAuctionsInfoResult.getString("users.city"), fetchAuctionsInfoResult.getString("users.street"),
									fetchAuctionsInfoResult.getString("users.number"));
							Customer seller = new Customer(fetchAuctionsInfoResult.getInt("users.id"), fetchAuctionsInfoResult.getString("users.username"),
									fetchAuctionsInfoResult.getString("users.email"), fetchAuctionsInfoResult.getString("users.password"), 
									fetchAuctionsInfoResult.getBytes("users.picture"), fetchAuctionsInfoResult.getDouble("users.wallet"), address);
							
							
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
									fetchAuctionsInfoResult.getBytes("auctions.image"), fetchAuctionsInfoResult.getDouble("auctions.minbid"), shippingtype,
									seller, customer, fetchAuctionsInfoResult.getDouble("auctions.currentbid"), fetchAuctionsInfoResult.getDate("auctions.starttime"), fetchAuctionsInfoResult.getDate("auctions.enddate"));
							
							savedAuctions[newArrayCounter] = auction;
						}
						newArrayCounter++;
						} catch (NumberFormatException e) {
							//Auction mittlerweile gel�scht
							//ignorieren, f�r diese ID keine Auktion (null) in das Array schreiben.
							savedAuctions[newArrayCounter] = null;
						}
					}
					return savedAuctions;
				}
				else
				{
					//kein Entry mit der BuyerId - eigentlich nicht m�glich.
					return null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
				
	}

	protected Auction[] fetchAuctionsByString(String searchstring, AuctionType auctionType) {
		// Auktionen mit searchString im Titel/Name zur�ckgeben

		// je nach AuctionType die aktuell laufenden, beendeten oder zuk�nftigen
		// Auktionen mit dem searchstring zur�ckgeben

		// AuctionType:
		// AuctionType.Active = aktive Auktionen
		// AuctionType.Ended = beendete Auktionen
		// AuctionType.Future = zuk�nftige Auktionen

		if (!checkConnection()) {
			return null;
		}

		return null;
	}

	protected Response SendRating(Rating rating) {
		// Bewertung mit Sternen und Text zur�ckgeben
		
		// Wenn erfolgreich Response.Success zur�ckgeben
		// Wenn keine Verbindung zur DB: NoDBConnection zur�ckgeben
		// Sonstiger Fehler Response.Failure zur�ckgeben
		
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

		return null;
	}

	protected double[] fetchAvgRating(User user) {
		// index 0 ist Average, Index 1 ist Anzahl
		if (!checkConnection()) {
			return null;
		}

		return null;
	}

	protected Response deleteOrder(Order order, Customer buyer) {
		// Order anhand von ID aus der Datenbank l�schen
	
		// Wenn Order erfolgreich gel�scht Response.Success zur�ckgeben
		// Wenn keine Verbindung zu DB: Response.NoDBConnection zur�ckgeben
		// Verbindung herstellen, wenn keine Verbindung besteht
		Date serverDate= new Date();
		// OrderID speichern
		int orderID = order.getId();
		// Order Date speichern
		Date date = order.getDate();
		Double price = order.getProduct().getPrice();
		
		// Datum auf dd/MM/YYYY begrenzen (Stornierung nur am gleichen Tag m�glich)
		SimpleDateFormat date1 = new SimpleDateFormat("dd/MM/YYYY");
		String orderGetDate = date1.format(date);
		String ServerDate = date1.format(serverDate);
		
		if (!checkConnection()) {
		return Response.NoDBConnection;
		}
		
		// Order kann am gleichen Tag der Bestellung noch storniert werden
		if(orderGetDate.equals(ServerDate)) {
			try
			{
				// Order aus Datenbank l�schen anahnd der ID
				Statement statement = connection.createStatement();
				statement.execute("DELETE FROM orders WHERE id ='" + orderID + "'");
							
				Seller seller = order.getProduct().getSeller();
				increaseWallet(buyer, price);
				decreaseWallet(seller, price);
				
				return Response.Success; 
				
			} catch (SQLException e) {
				// Fehler zur�ckgeben
				return Response.Failure;
			}
		}
		// Order ist zu lange her und kann nicht mehr gel�scht werden
		else {
			return Response.Failure;
		}			
}

	protected Response checkForNewFinishedAuctions() {
		// Checken ob es beendete Auktionen gibt, zu welchen noch keine
		// Verkaufsbest�tigungsemail verschickt wurde,
		// falls ja, das Guthaben des K�ufers reduzieren, und das Guthaben des Verk�ufers erh�hen (wie bei BuyItem)
		// Spezialfall: User hat inzwischen kein Guthaben mehr (wird beim Gebot gepr�ft, aber er kann es zwischenzeitlich
		// ausgegeben haben) - in diesem Fall die Zeile currentbidder_id auf "" und currentbid auf 0 - also keinen K�ufer
		// in der DB speichern
		// (Spalte emailsent in der Auctions-DB-Tabelle), ggf Email schicken
		if (!checkConnection()) {
			return Response.NoDBConnection;
		}

		EmailHandler.sendAuctionEndedEmail(null);
		return null;
	}

	public static void main(String[]args) {
		SQL testSQLObject= new SQL();
	}
}