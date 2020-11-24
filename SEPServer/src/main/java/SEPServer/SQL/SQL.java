package SEPServer.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

import com.mysql.cj.protocol.x.SyncFlushDeflaterOutputStream;

import SEPCommon.Response;
import SEPCommon.Seller;
import SEPCommon.User;
import SEPServer.Server;
import SEPCommon.Address;
import SEPCommon.Constants;
import SEPCommon.Customer;
import SEPCommon.Product;

public class SQL {

	private boolean isConnected;
	private static Connection connection;

	public boolean connect() {
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

	public Response registerUser(User user) {
		// Die Methode ermï¿½glicht eine Registrierung auf der Plattform
		// Passwort, Emailvorgaben usw. werden clientseitig geprï¿½ft
		
		// wenn User erfolgreich registriert wurde wird Response.Success zurï¿½ck gegeben
		// wenn Email vergeben: Response.Emailtaken zurï¿½ckgeben
		// wenn User vergeben: Response.UsernameTaken zurï¿½ckgeben
		// wenn keine Verbindung zu DB: Response.NoDBConnection zurï¿½ckgeben
		// wenn Bild zu groï¿½: Response.ImageTooBig zurï¿½ckgeben
		
		// Verbindung herstellen, wenn keine Verbindung besteht
		if (!checkConnection())
		{
			return Response.NoDBConnection;
		}
		
		if(user instanceof Seller)
		{
			// Registrierung Gewerbekunde
			
			// User- und Adress-Objekt ï¿½bergeben
			Seller seller = (Seller)user;
			Address sellerAddress = seller.getAddress();
			
			// Prï¿½fen, ob Email oder Username schon existieren
			try
			{
				// SQL Abfrage 
				Statement statement = connection.createStatement();
				ResultSet emailQuery = statement.executeQuery("SELECT * FROM users WHERE email='" + seller.getEmail() + "'");
				boolean emailHasEntries = emailQuery.next();
				if(emailHasEntries)
				{
					// 1. Fall: Email vergeben
					return Response.EmailTaken;
				}
			} catch (SQLException e) {
				return Response.NoDBConnection;
			}
			
			try
			{
				// SQL Abfrage 
				Statement statement = connection.createStatement();
				ResultSet usernameQuery = statement.executeQuery("SELECT * FROM users WHERE username='" + seller.getUsername() + "'");
				boolean usernameHasEntries = usernameQuery.next();
				if(usernameHasEntries)
				{
					// 2. Fall: Username vergeben
					return Response.UsernameTaken;
				}
			} catch (SQLException e) {
				return Response.NoDBConnection;
			}
			
			try {
				// Eintrag in Datenbank
				PreparedStatement stmt = connection.prepareStatement("INSERT INTO users(type,username,password,email,fullname,street,number,postalcode,city,country,image,wallet,companyname,lastviewed) "
						+ "VALUES ('Seller', '" + seller.getUsername() + "', '" + seller.getPassword() + "', '" + seller.getEmail() + "', '" + sellerAddress.getFullname() + "', '" + sellerAddress.getStreet() + "', '" + sellerAddress.getNumber() + "', " + sellerAddress.getZipcode() + ", '" + sellerAddress.getCity() + "', '" + sellerAddress.getCountry() + "',?, " + seller.getWallet() + ", '" + seller.getBusinessname() + "', '')");
				// Bild einfï¿½gen
				if(seller.getPicture()!=null)
				{
					stmt.setBytes(1, seller.getPicture());
				}
				else
				{
					stmt.setString(1, "");
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
			// User- und Adress-Objekt ï¿½bergeben
			Customer customer = (Customer)user;
			Address customerAddress = customer.getAddress();
			
			//Prï¿½fen, ob Email oder Username schon existieren
			try
			{
				// SQL Abfrage 
				Statement statement = connection.createStatement();
				ResultSet emailQuery = statement.executeQuery("SELECT * FROM users WHERE email='" + customer.getEmail() + "'");
				boolean emailHasEntries = emailQuery.next();
				if(emailHasEntries)
				{
					// 1. Fall: Email vergeben
					return Response.EmailTaken;
				}
			} catch (SQLException e) {
				return Response.NoDBConnection;
			}
			
			try
			{
				// SQL Abfrage 
				Statement statement = connection.createStatement();
				ResultSet usernameQuery = statement.executeQuery("SELECT * FROM users WHERE username='" + customer.getUsername() + "'");
				boolean usernameHasEntries = usernameQuery.next();
				if(usernameHasEntries)
				{
					// 2. Fall: Username vergeben
					return Response.UsernameTaken;
				}
			} catch (SQLException e) {
				return Response.NoDBConnection;
			}
			
			try {
				// Eintrag in Datenbank
				PreparedStatement stmt = connection.prepareStatement("INSERT INTO users(type,username,password,email,fullname,street,number,postalcode,city,country,image,wallet,companyname,lastviewed) "
						+ "VALUES ('Customer', '" + customer.getUsername() + "', '" + customer.getPassword() + "', '" + customer.getEmail() + "', '" + customerAddress.getFullname() + "', '" + customerAddress.getStreet() + "', '" + customerAddress.getNumber() + "', " + customerAddress.getZipcode() + ", '" + customerAddress.getCity() + "', '" + customerAddress.getCountry() + "', ?, " + customer.getWallet() + ", '', '')");
				// Bild einfï¿½gen
				if(customer.getPicture()!=null)
				{
					stmt.setBytes(1, customer.getPicture());
				}
				else
				{
					stmt.setString(1, "");
				}
				
				stmt.execute();
				// 3. Fall: Erfolreich
				return Response.Success;
			} catch (SQLException e) {
				// Ausnahme: Bilddatei passt nicht
				return Response.ImageTooBig;
			}
		}
	}

	public Response loginUser(String emailOrUsername, String password) {
		// wenn User erfolgreich eingeloggt wurde Response.Success zurï¿½ckgeben
		// wenn Username / Email nicht gefunden, oder wenn das eingegebene Passwort dazu nicht passt Response.Failure zurï¿½ckgeben
		// wenn keine Verbindung zu DB: Response.NoDBConnection zurï¿½ckgeben
		
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
				Statement statement = connection.createStatement();
				ResultSet loginQuery = statement.executeQuery("SELECT * FROM users WHERE email='" + emailOrUsername + "' AND password='" + password + "'");
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
				return Response.NoDBConnection;
			}
		}
		else
		{
			//anmelden mit Benutzernamen
			try
			{
				// SQL Abfrage
				Statement statement = connection.createStatement();
				ResultSet loginQuery = statement.executeQuery("SELECT * FROM users WHERE username='" + emailOrUsername + "' AND password='" + password + "'");
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
				return Response.NoDBConnection;
			}
		}
	}

	public Response editUser(User user) {
		// User anhand ID in Datenbank finden und alle Werte mit den Werten des Objekts users ï¿½berschreiben 
 
		// Wenn User erfolgreich abgeï¿½ndert wurden Response.Success zurï¿½ckgeben
		// wenn keine Verbindung zu DB: Response.NoDBConnection zurï¿½ckgeben
		// wenn sonstiger Fehler auftritt ggf. Response.Failure zurï¿½ckgeben

		//Verbindung herstellen, wenn keine Verbindung besteht
				if (!checkConnection())
				{
					return Response.NoDBConnection;
				}
				int userId = user.getId();
				
				if(user instanceof Seller)
				{
					//Gewerbekunde
					Seller seller = (Seller)user;
					Address sellerAddress = seller.getAddress();
													
					try {
						//Statement statement = connection.createStatement();
						PreparedStatement stmt = connection.prepareStatement("UPDATE users(type,username,password,email,fullname,street,number,postalcode,city,country,image,wallet,companyname,lastviewed) WHERE id ='" + userId + "'"
								+ "VALUES ('Seller', '" + seller.getUsername() + "', '" + seller.getPassword() + "', '" + seller.getEmail() + "', '" + sellerAddress.getFullname() + "', '" + sellerAddress.getStreet() + "', '" + sellerAddress.getNumber() + "', " + sellerAddress.getZipcode() + ", '" + sellerAddress.getCity() + "', '" + sellerAddress.getCountry() + "',?, " + seller.getWallet() + ", '" + seller.getBusinessname() + "', '')");
						if(seller.getPicture()!=null)
						{
							stmt.setBytes(1, seller.getPicture());
						}
						else
						{
							stmt.setString(1, "");
						}
						stmt.execute();

						return Response.Success;

					} catch (SQLException e) {
						return Response.ImageTooBig;
					} 
				}
				
				else
				{
					//Privatkunde
					Customer customer = (Customer)user;
					Address customerAddress = customer.getAddress();
					
					try {
						//Statement statement = connection.createStatement();
						PreparedStatement stmt = connection.prepareStatement("UPDATE users(type,username,password,email,fullname,street,number,postalcode,city,country,image,wallet,companyname,lastviewed) WHERE id ='" + userId + "'"
								+ "VALUES ('Customer', '" + customer.getUsername() + "', '" + customer.getPassword() + "', '" + customer.getEmail() + "', '" + customerAddress.getFullname() + "', '" + customerAddress.getStreet() + "', '" + customerAddress.getNumber() + "', " + customerAddress.getZipcode() + ", '" + customerAddress.getCity() + "', '" + customerAddress.getCountry() + "', ?, " + customer.getWallet() + ", '', '')" );
						if(customer.getPicture()!=null)
						{
							stmt.setBytes(1, customer.getPicture());
						}
						else
						{
							stmt.setString(1, "");
						}
						
						stmt.execute();
						return Response.Success;
					} catch (SQLException e) {
						return Response.ImageTooBig;
					}
				}
			}  

	public Response deleteUser(User user) {
		// User anhand ID aus der Datenbank lï¿½schen

		// Wenn User erfolgreich gelï¿½scht Response.Success zurï¿½ckgeben
		// wenn keine Verbindung zu DB: Response.NoDBConnection zurï¿½ckgeben
		// Verbindung herstellen, wenn keine Verbindung besteht
		int userId = user.getId();
		
		if (!checkConnection()) {
			return Response.NoDBConnection;
		}
		
		// Gewerbekunde
		if(user instanceof Seller)
		{ 
			try
			{
					// Zuerst Produkte des Anbieters und dann den Anbieter selbst lï¿½schen
					Statement statement = connection.createStatement();
					statement.executeQuery("DELETE FROM products WHERE seller_id ='" + userId + "'");
					statement.executeQuery("DELETE FROM users WHERE id ='" + userId + "'");
					return Response.Success;
		   
			} catch (SQLException e) {
				return Response.NoDBConnection;
			}
		}
		
		// Privatkunde
		else 
		{
				try
				{
					Statement statement = connection.createStatement();
					// Bei Privatkunden muss nur der User selbst gelï¿½scht werden
					statement.executeQuery("DELETE FROM users WHERE id ='" + userId + "'");
					return Response.Success;
				
				} catch (SQLException e) {
					return Response.NoDBConnection;
				}
		}
	}

	public Response increaseWallet(User user, double amount) {
		// Wallet anhand User-ID in der Datenbank um den Betrag amount erhÃƒÂ¶hen

		// Wenn Wallet erfolgreich erhÃƒÂ¶ht Response.Success returnen
		// wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		// wenn sonstiger Fehler auftritt ggf. Response.Failure returnen

		// Verbindung herstellen, wenn keine Verbindung besteht
		int userId = user.getId();
		double MoreMoney = amount;
		double currentBalance = user.getWallet();
		double newBalance = currentBalance + MoreMoney;
		String increaseWalletQuery = "UPDATE users SET wallet='" + newBalance + "' WHERE id=" + userId;

		if (!checkConnection()) {
			return Response.NoDBConnection;
		}
		try {
			Statement statement = connection.createStatement();
			statement.execute(increaseWalletQuery);

		} catch (SQLException e) {

			return Response.NoDBConnection;
		}

		return Response.Success;
	}

	public Response decreaseWallet(User user, double amount) {
		// Wallet anhand User-ID in der Datenbank um den Betrag amount vermindern

		// Wenn Wallet erfolgreich vermindert Response.Success returnen
		// wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		// wenn sonstiger Fehler auftritt ggf. Response.Failure returnen

		// Verbindung herstellen, wenn keine Verbindung besteht
		int userId = user.getId();
		double lessMoney = amount;
		double currentBalance = user.getWallet();
		double newBalance = currentBalance - lessMoney;
		String decreaseWalletQuery = "UPDATE users SET wallet='" + newBalance + "' WHERE id=" + userId;

		if (!checkConnection()) {
			return Response.NoDBConnection;
		}
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(decreaseWalletQuery);
		} catch (SQLException e) {

			return Response.NoDBConnection;
		}

		return Response.Success;

	}

	public Product[] fetchAllProducts() {
		// Alle in der DB vorhandenen Produkte in einem ProductArray ausgeben

		// Wenn erfolgreich gefetcht, Product-Array returnen
		// wenn keine Verbindung zu DB: null returnen
		// wenn sonstiger Fehler auftritt ggf. null returnen

		// Verbindung herstellen, wenn keine Verbindung besteht
		String allProductsQuery = "SELECT * \r\n" 
				+ "FROM products\r\n" 
				+ "JOIN categories\r\n"
				+ "ON categories.id=products.category_id\r\n"
				+ "JOIN users\r\n"
				+ "ON users.id=products.seller_id";

		int counter = 0;
		if (!checkConnection()) {
			System.out.println("connection problem");

			return null;
		}
		try {
			PreparedStatement pstmt = connection.prepareStatement(allProductsQuery);
			ResultSet AllProducts = pstmt.executeQuery();

			int sqlcounter = 0;
			while (AllProducts.next()) {  //Tupel Zählen
				sqlcounter++;
			}
			PreparedStatement pstmt2= connection.prepareStatement(allProductsQuery); // nach der 1 Schleife pointer zeigt auf Null -> ggf könnte man pointer resetten glaueb aber nien, weil Statement danach closed
			ResultSet AllProducts2= pstmt2.executeQuery();
			

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
				allProducts[counter] = new Product(AllProducts2.getInt("products.id"),
						AllProducts2.getString("products.title"), AllProducts2.getDouble("products.price"), newSeller,
						AllProducts2.getString("categories.title"), AllProducts2.getString("products.description"));

				counter++;
				System.out.println("works");
				System.out.println(counter);
			}
			//test

			return allProducts;

		} catch (SQLException e) {
			System.out.println("other problem");
			System.out.println(e.getMessage());
			return null;
		}

	}

	public Product[] fetchProductsByCategory(String category) {
		// Produkte mit der Kategorie category in der DB suchen und als Product-Array
		// ausgeben

		// Wenn erfolgreich gefetcht, Product-Array returnen
		// wenn keine Verbindung zu DB: null returnen
		// wenn sonstiger Fehler auftritt ggf. null returnen

		// Verbindung herstellen, wenn keine Verbindung besteht
		String queryByCategory = "SELECT *\r\n" 
					 		   + "FROM products \r\n" 
					 		   + "JOIN categories\r\n"
					 		   + "ON (products.category_ID = categories.ID)\r\n" 
					 		   + "JOIN users\r\n"
					 		   + "ON users.id=products.seller_id\r\n"
					 		   + "WHERE categories.title='" + category+ "'";
		if (!checkConnection()) {
			System.out.println("connection probleme");
			return null;
		}
		try {
			int counter = 0;
			int sqlcounter = 0;
			PreparedStatement statement = connection.prepareStatement(queryByCategory);
			ResultSet AllProductsByCategory = statement.executeQuery();

			while (AllProductsByCategory.next()) {
				sqlcounter++;
			}

			PreparedStatement pstmt2 = connection.prepareStatement(queryByCategory);
			ResultSet AllProductsByCategory2 = pstmt2.executeQuery();
			

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
				allProductsSameCategory[counter] = new Product(AllProductsByCategory2.getInt("products.id"),
						AllProductsByCategory2.getString("products.title"), AllProductsByCategory2.getDouble("products.price"), newSeller,
						AllProductsByCategory2.getString("categories.title"), AllProductsByCategory2.getString("products.description"));
				
				
			
				counter++;
				System.out.println("works");
			}

			return allProductsSameCategory;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}

	}

	public Product[] fetchProductsByString(String searchString) {
		// Produkte mit dem Begriff searchString im Namen, Beschreibung oder Kategorie
		// in der DB suchen und als Product-Array ausgeben

		// Wenn erfolgreich gefetcht, Product-Array returnen
		// wenn keine Verbindung zu DB: null returnen
		// wenn sonstiger Fehler auftritt ggf. null returnen

		// Verbindung herstellen, wenn keine Verbindung besteht
		String query = "SELECT * \r\n"
					 + "FROM Products\r\n"
					 + "JOIN Categories\r\n"
					 + "ON (Products.category_ID = Categories.ID)\r\n"
					 + "JOIN users\r\n"
			 		 + "ON users.id=products.seller_id\r\n"
					 + "WHERE Products.Title LIKE '%"+ searchString+"%'";
		
					// + "OR Products.Description LIKE"+ searchString+ "%\r\n"
					// + "OR Categories.Title LIKE" + searchString+"%\r\n";
		
		if (!checkConnection()) {
			return null;
		}
		try {
			int counter = 0;
			int sqlcounter = 0;
			PreparedStatement pstmt = connection.prepareStatement(query);
			ResultSet AllProductsByFullString = pstmt.executeQuery();

			while (AllProductsByFullString.next()) {
				sqlcounter++;
			}
			
			PreparedStatement pstmt2 = connection.prepareStatement(query);
			ResultSet AllProductsByFullString2 = pstmt2.executeQuery();
			
			
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
				allProductsByString[counter] = new Product(AllProductsByFullString2.getInt("products.id"),
						AllProductsByFullString2.getString("products.title"), AllProductsByFullString2.getDouble("products.price"), newSeller,
						AllProductsByFullString2.getString("categories.title"), AllProductsByFullString2.getString("products.description"));
				System.out.println("funktioniert");
			}
			return allProductsByString;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}

	}
	
	public Product[] fetchLastViewedProducts(User user) {
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
			
			
			if(fetchLastViewedProductIdsResult.next())
			{
				String lastviewed = fetchLastViewedProductIdsResult.getString("lastviewed");
				if(lastviewed=="" || lastviewed==null || lastviewed.isEmpty() || lastviewed.isBlank())
					return null;
				
				String[] lastViewedIds = lastviewed.split(",");
				lastViewedProducts = new Product[lastViewedIds.length];
				
				int newArrayCounter = 0;
				for(String viewedIdStr : lastViewedIds)
				{
					int viewedId = Integer.parseInt(viewedIdStr);
					
					//Produkt-Daten aus DB holen
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
						
						Product product = new Product(viewedId, fetchProductsInfoResult.getString("products.title"), fetchProductsInfoResult.getDouble("products.price"), seller, fetchProductsInfoResult.getString("categories.title"), fetchProductsInfoResult.getString("products.description"));
						lastViewedProducts[newArrayCounter] = product;
					}
					newArrayCounter++;
				}
				return lastViewedProducts;
			}
			else
			{
				//kein Entry mit der UserId - eigentlich nicht möglich.
				return null;
			}
		} catch (SQLException e) {
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
				//Maximale Länge (10), setze viewedProductId an den Anfang und ersetze die erste Id
				newLastViewedProductsString += String.valueOf(viewedProductId);
				
				for(int i=1;i<10;i++)
				{
					newLastViewedProductsString += "," + String.valueOf(currentLastViewedProducts[i].getId());
				}
			}
			else
			{
				//Maximale Länge (10) noch nicht erreicht, setze viewedProductId an den Anfang und schiebe ggf. die anderen ein Feld nach hinten
				newLastViewedProductsString += String.valueOf(viewedProductId);
				
				for(int i=0;i<currentLastViewedProducts.length;i++)
				{
					newLastViewedProductsString += "," + String.valueOf(currentLastViewedProducts[i].getId());
				}
			}
		}
		else
		{
			//Keine zuletzt angesehenen Produkte gespeichert oder Fehler
			newLastViewedProductsString += String.valueOf(viewedProductId);
		}
		
		try {
			PreparedStatement updateLastViewedProductIds = connection.prepareStatement("UPDATE users SET lastviewed='" + newLastViewedProductsString + "' WHERE id='" + user.getId() + "'");
			updateLastViewedProductIds.execute();
			return Response.Success;
		} catch (SQLException e) {
			//Fehler aufgetreten
			return Response.Failure;
		}
	}

	public Response addItem(User seller, Product product) {
		// Neues Produkt in der Datenbank anlegen. Die seller_id ist die ID des Objekts
		// seller

		// Wenn Produkt erfolgreich angelegt, Response.Success returnen
		// wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		// wenn sonstiger Fehler auftritt ggf. Response.Failure returnen

		// Verbindung herstellen, wenn keine Verbindung besteht
		if (!checkConnection()) {
			return Response.NoDBConnection;
		}

		return Response.Success;
	}

	public Response addItems(User seller, Product[] products) {
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
		
		//Ungï¿½ltigkeit usw. wird clientseitig geprï¿½ft
		
		int sellerid = seller.getId();
		
		for(Product p : products)
		{
			//Fï¿½r jedes Produkt p prï¿½fen ob Kategorie existiert, wenn ja ID auslesen, ansonsten Kategorie anlegen
			int categoryid;
			

			try {
				PreparedStatement selectCategoryID = connection.prepareStatement("SELECT id FROM categories WHERE title='" + p.getCategory() + "'");
				ResultSet selectCategoryIDResult = selectCategoryID.executeQuery();
				if(selectCategoryIDResult.next())
				{
					//Kategorie existiert bereits, schreibe ID in Variable categoryid
					categoryid = selectCategoryIDResult.findColumn("id");
				}
				else
				{
					//Kategorie existiert noch nicht
					//Lege Kategorie an
					PreparedStatement createCategory = connection.prepareStatement("INSERT INTO categories(title) VALUES('" + p.getCategory() + "'");
					createCategory.execute();
					
					//ID nach Anlegen der Kategorie auslesen
					selectCategoryIDResult = selectCategoryID.executeQuery();
					if(selectCategoryIDResult.next())
					{
						categoryid = selectCategoryIDResult.findColumn("id");
					}
					else
					{
						//Kategorie existiert immer noch nicht (sollte nicht auftreten, da schon eine Exception aufgetreten wï¿½re)
						return Response.Failure;
					}
				}
				
				//Produkt p anlegen
				PreparedStatement insertProduct = connection.prepareStatement("INSERT INTO products(seller_id, title, price, category_id, description)"
							+ "VALUES ('" + sellerid + "', '" + p.getName() + "', '" + p.getPrice() + "', '" + categoryid + "', '" + p.getDescription() + "'");
				insertProduct.execute();
			} catch (SQLException e) {
				e.printStackTrace();
				//Fehler aufgetreten
				return Response.Failure;
			}
		}
		return Response.Success;
	}

	public Response buyItem(User buyer, Product product) {
		// Neuen Datenbankeintrag in die Tabelle orders. buyer_id ist die ID vom Objekt
		// buyer, die seller_id, Preis, Produktinfos kÃ¯Â¿Â½nnen dem Objekt product
		// entnommen
		// werden

		// Wenn Produkte erfolgreich gekauft, Response.Success returnen
		// wenn keine Verbindung zu DB: Response.NoDBConnection returnen
		// wenn sonstiger Fehler auftritt ggf. Response.Failure returnen

		// Verbindung herstellen, wenn keine Verbindung besteht

		int buyerid = buyer.getId();
		Seller seller = product.getSeller();
		int sellerid = seller.getId();
		String newOrder = "INSERT INTO orders\r\n" + " VALUES('" + product.getId() + "', '" + sellerid + "', '"
				+ buyerid + "', '" + product.getPrice() + "')";

		if (!checkConnection()) {
			return Response.NoDBConnection;
		}

		if (buyer.getWallet() - product.getPrice() < 0) {
			return Response.Failure;
		} else {
			if(decreaseWallet(buyer, product.getPrice())==Response.Success)
			{
				if(increaseWallet(seller, product.getPrice())==Response.Success)
				{
					try {
						PreparedStatement addNewOrder = connection.prepareStatement(newOrder);
						addNewOrder.execute();
						return Response.Success;
					}catch (SQLException e) {
						e.printStackTrace();
						return Response.Failure;
					}
				} else { return Response.Failure;}
			} else { return Response.Failure;}
		}
	}

	public User getUserDataByEmail(String email) {
		// Anhand der Email in der DB das entsprechende User-Objekt suchen und ein vollstï¿½ndiges User-Objekt mit id und allen anderen Werten aus der DB zurï¿½ckgeben
		
		// Wenn Userdaten erfolgreich gefetcht, User-Objekt zurï¿½ckgeben
		// wenn keine Verbindung zu DB: null zurï¿½ckgeben
		// wenn sonstiger Fehler auftritt ggf. null zurï¿½ckgeben
		
		// Verbindung herstellen, wenn keine Verbindung besteht
		if (!checkConnection())
		{
			return null;
		}
		
		try
		{
			// SQL Abfrage
			Statement statement = connection.createStatement();
			// Email prï¿½fen
			ResultSet userDataQuery = statement.executeQuery("SELECT * FROM users WHERE email='" + email + "'");
			if(userDataQuery.next())
			{
				// Privat- oder Gewerbekunde?
				String accountType = userDataQuery.getString("type");
				Address address = new Address(userDataQuery.getString("fullname"), userDataQuery.getString("country"), userDataQuery.getInt("postalcode"), userDataQuery.getString("city"), userDataQuery.getString("street"), userDataQuery.getString("number"));
				
				// Privatkunde
				if(accountType.equals("Customer"))
				{
					Customer customer = new Customer(userDataQuery.getInt("id"), userDataQuery.getString("username"), userDataQuery.getString("email"), userDataQuery.getString("password"), userDataQuery.getBytes("image"), userDataQuery.getDouble("wallet"), address);
					// Privatkunden-Obejekt zurï¿½ckgeben
					return customer;
				}
				
				// Gewerbekunde
				else if(accountType.equals("Seller"))
				{
					Seller seller = new Seller(userDataQuery.getInt("id"), userDataQuery.getString("username"), userDataQuery.getString("email"), userDataQuery.getString("password"), userDataQuery.getBytes("image"), userDataQuery.getDouble("wallet"), address, userDataQuery.getString("companyname"));
					// Gewerbekunden-Objekt zurï¿½ckgeben
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
			return null;
		}
	}

	public User getUserDataByUsername(String username){
		// Analog zur vorherigen Methode: Anhand des Username in der DB das entsprechende User-Objekt suchen und ein vollstï¿½ndiges User-Objekt mit id und allen anderen Werten aus der DB zurï¿½ckgeben
		
		// Wenn Userdaten erfolgreich gefetcht, User-Objekt zurï¿½ckgeben
		// wenn keine Verbindung zu DB: null zurï¿½ckgeben
		// wenn sonstiger Fehler auftritt ggf. null zurï¿½ckgeben
		
		// Verbindung herstellen, wenn keine Verbindung besteht
		if (!checkConnection())
		{
			return null;
		}
		
		try
		{
			// SQL Abfrage
			Statement statement = connection.createStatement();
			// Username prï¿½fen
			ResultSet userDataQuery = statement.executeQuery("SELECT * FROM users WHERE username='" + username + "'");
			
			if(userDataQuery.next())
			{
				String accountType = userDataQuery.getString("type");
				Address address = new Address(userDataQuery.getString("fullname"), userDataQuery.getString("country"), userDataQuery.getInt("postalcode"), userDataQuery.getString("city"), userDataQuery.getString("street"), userDataQuery.getString("number"));
				
				// Privatkunde
				if(accountType.equals("Customer"))
				{
					Customer customer = new Customer(userDataQuery.getInt("id"), userDataQuery.getString("username"), userDataQuery.getString("email"), userDataQuery.getString("password"), userDataQuery.getBytes("image"), userDataQuery.getDouble("wallet"), address);
					// Privatkunden-Objekt zurï¿½ckgeben
					return customer;
				}
				
				// Gewerbekunde
				else if(accountType.equals("Seller"))
				{
					Seller seller = new Seller(userDataQuery.getInt("id"), userDataQuery.getString("username"), userDataQuery.getString("email"), userDataQuery.getString("password"), userDataQuery.getBytes("image"), userDataQuery.getDouble("wallet"), address, userDataQuery.getString("companyname"));
					// Gewerbekunden-Objekt zurï¿½ckgeben
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
			e.printStackTrace(); 
			return null;
		}
	}
	
	public static void main(String[] args) {
		//Zum testen
		SQL testObject= new SQL();
		testObject.connect();
	
		User testUser = new Customer(23, "test", "marcel@test", "pw123", null, 23.0, new Address("test", "test", 0, "test", "test", "a"));
		testObject.fetchLastViewedProducts(testUser);
	}
}