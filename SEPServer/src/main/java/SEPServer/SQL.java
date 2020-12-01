package SEPServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import SEPCommon.Response;
import SEPCommon.Seller;
import SEPCommon.User;
import SEPCommon.Address;
import SEPCommon.Constants;
import SEPCommon.Customer;
import SEPCommon.Product;

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
				PreparedStatement stmt = connection.prepareStatement("INSERT INTO users(type,username,password,email,fullname,street,number,postalcode,city,country,image,wallet,companyname,lastviewed) "
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

	protected Product[] fetchAllProducts() {															//ProductArray aufgerufen in test
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
}