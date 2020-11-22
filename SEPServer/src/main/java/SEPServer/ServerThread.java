package SEPServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import SEPCommon.ClientRequest;
import SEPCommon.Product;
import SEPCommon.Request;
import SEPCommon.Response;
import SEPCommon.ServerResponse;
import SEPCommon.User;
import SEPServer.SQL.SQL;

public class ServerThread implements Runnable {
	private Socket client;
	private int clientID;
	private SEPServer.SQL.SQL sql = new SQL();
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	ServerThread(Socket clientSocket, int _clientID)
	{
		this.client = clientSocket;
		this.clientID = _clientID;
		System.out.println("Client-Verbindung (ID " + this.clientID + ") angenommen.");
	}
	
	@Override
	public void run() {
		ClientRequest clientreq = null;
		try
		{
			in = new ObjectInputStream(client.getInputStream());
			out = new ObjectOutputStream(client.getOutputStream());
			
			//Endlosschleife
			while((clientreq = (ClientRequest)in.readObject()) != null)
			{
				//ClientRequest clientreq = (ClientRequest)_in.readObject();
				SEPCommon.Request requestType = clientreq.getRequestType();
				Map<String, Object> requestMap = clientreq.getRequestMap();
				System.out.println("ClientRequest - " + clientreq.getRequestType() + " - " + clientreq.getRequestMap());
				
				
				//Verschiedene Requests handlen
				
				//Request RegisterUser
				//HASHMAP: "User" - UserObjekt
				if(requestType == Request.RegisterUser)
				{
					User argUser = (User)requestMap.get("User");
					
					//SQL-Abfrage ausführen
					Response responseType = sql.registerUser(argUser);
					ServerResponse response = new ServerResponse(responseType, null);
					
					System.out.println("Sende ServerResponse - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request LoginUser
				//HASHMAP: "UserOrEmail" - Benutzername oder Email, "Password" - Passwort
				else if(requestType == Request.LoginUser)
				{
					String userOrEmail = (String)requestMap.get("UserOrEmail");
					String password = (String)requestMap.get("Password");
					
					//SQL-Abfrage ausführen
					Response responseType = sql.loginUser(userOrEmail, password);
					ServerResponse response = new ServerResponse(responseType, null);
					
					System.out.println("Sende ServerResponse - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request EditUser
				//HASHMAP: "User" - Userobjekt mit geänderten Werten
				else if(requestType == Request.EditUser)
				{
					User argUser = (User)requestMap.get("User");
					
					//SQL-Abfrage ausführen
					Response responseType = sql.editUser(argUser);
					ServerResponse response = new ServerResponse(responseType, null);
					
					System.out.println("Sende ServerResponse - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request DeleteUser
				//HASHMAP: "User" - Zu löschendes Userobjekt
				else if(requestType == Request.DeleteUser)
				{
					User argUser = (User)requestMap.get("User");
					
					//SQL-Abfrage ausführen
					Response responseType = sql.deleteUser(argUser);
					ServerResponse response = new ServerResponse(responseType, null);
					
					System.out.println("Sende ServerResponse - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request IncreateWallet
				//HASHMAP: "User" - Userobjekt, "Amount" - Zu erhoehende Menge
				else if(requestType == Request.IncreaseWallet)
				{
					User argUser = (User)requestMap.get("User");
					double amount = (double)requestMap.get("Amount");
					
					//SQL-Abfrage ausführen
					Response responseType = sql.increaseWallet(argUser, amount);
					ServerResponse response = new ServerResponse(responseType, null);
					
					System.out.println("Sende ServerResponse - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request DecreaseWallet
				//HASHMAP: "User" - Userobjekt, "Amount" - Zu erhoehende Menge
				else if(requestType == Request.DecreaseWallet)
				{
					User argUser = (User)requestMap.get("User");
					double amount = (double)requestMap.get("Amount");
					
					//SQL-Abfrage ausführen
					Response responseType = sql.decreaseWallet(argUser, amount);
					ServerResponse response = new ServerResponse(responseType, null);
					
					System.out.println("Sende ServerResponse - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request FetchProducts
				//HASHMAP: leer
				//Request FetchProducts mit Kategorie
				//HASHMAP: "Category" - Kategorie
				//Request FetchProducts mit Suchbegriff
				//HASHMAP: "SearchString" - Suchbegriff
				else if(requestType == Request.FetchProducts)
				{
					Product[] products = null;
					Response responseType;
					HashMap<String, Object> responseMap = new HashMap<String, Object>();
					if(requestMap.containsKey("Category"))
					{
						//nach Produkten mit Kategorie suchen
						String category = (String)requestMap.get("Category");
						
						//SQL-Abfrage ausführen
						products = sql.fetchProductsByCategory(category);
					}
					else if(requestMap.containsKey("SearchString"))
					{
						//nach Produkten mit Suchbegriff suchen
						String searchString = (String)requestMap.get("SearchString");
						
						//SQL-Abfrage ausführen
						products = sql.fetchProductsByString(searchString);
					}
					else
					{
						//alle Produkte suchen
						
						//SQL-Abfrage ausführen
						products = sql.fetchProducts();
					}
					
					if(products==null)
					{
						//keine DB Verbindung oder sonstiger Fehler
						responseType = Response.Failure;
					}
					else
					{
						responseType = Response.Success;
						responseMap.put("Products", products);
					}
					
					ServerResponse response = new ServerResponse(responseType, responseMap);
					
					System.out.println("Sende ServerResponse - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request LastViewedProducts
				//HASHMAP: "User" - Userobjekt
				else if(requestType == Request.LastViewedProducts)
				{
					User argUser = (User)requestMap.get("User");
					Product[] products = null;
					Response responseType;
					HashMap<String, Object> responseMap = new HashMap<String, Object>();
					
					//SQL-Abfrage ausführen
					products = sql.fetchLastViewedProducts(argUser);
					
					if(products==null)
					{
						//keine DB Verbindung oder sonstiger Fehler
						responseType = Response.Failure;
					}
					else
					{
						responseType = Response.Success;
						responseMap.put("Products", products);
					}
					
					ServerResponse response = new ServerResponse(responseType, responseMap);
					
					System.out.println("Sende ServerResponse - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request AddLastViewedProduct
				//HASHMAP: "ViewedProductID" - ID des angesehenen Produkts (int), "User" - Userobjekt, 
				
				else if(requestType == Request.AddLastViewedProduct)
				{
					User argUser = (User)requestMap.get("User");
					int viewedProductId = (int)requestMap.get("ViewedProductID");
					
					//SQL-Abfrage ausführen
					Response responseType = sql.addLastViewedProduct(viewedProductId, argUser);
					ServerResponse response = new ServerResponse(responseType, null);
					
					System.out.println("Sende ServerResponse - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request AddItem
				//HASHMAP: "User" - Userobjekt (Verkäufer), "Product" - Hinzuzufügendes Produkt
				else if(requestType == Request.AddItem)
				{
					User argUser = (User)requestMap.get("User");
					Product argProduct = (Product)requestMap.get("Product");
					
					//SQL-Abfrage ausführen
					Response responseType = sql.addItem(argUser, argProduct);
					ServerResponse response = new ServerResponse(responseType, null);
					
					System.out.println("Sende ServerResponse - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request AddItems
				//HASHMAP: "User" - Userobjekt (Verkäufer), "Products" - Hinzuzufügende Produkte
				else if(requestType == Request.AddItem)
				{
					User argUser = (User)requestMap.get("User");
					Product[] argProducts = (Product[])requestMap.get("Products");
					
					//SQL-Abfrage ausführen
					Response responseType = sql.addItems(argUser, argProducts);
					ServerResponse response = new ServerResponse(responseType, null);
					
					System.out.println("Sende ServerResponse - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request BuyItem
				//HASHMAP: "User" - Userobjekt (Käufer), "Product" - Zu kaufendes Produkt
				else if(requestType == Request.AddItem)
				{
					User argUser = (User)requestMap.get("User");
					Product argProduct = (Product)requestMap.get("Product");
					
					//SQL-Abfrage ausführen
					Response responseType = sql.buyItem(argUser, argProduct);
					ServerResponse response = new ServerResponse(responseType, null);
					
					System.out.println("Sende ServerResponse - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request GetUserData mit Email
				//HASHMAP: "Email" - Emailadresse des Users
				//Request GetUserData mit Benutzername
				//HASHMAP: "Username" - Benutzername des Users
				else if(requestType == Request.GetUserData)
				{
					User user = null;
					Response responseType;
					HashMap<String, Object> responseMap = new HashMap<String, Object>();
					if(requestMap.containsKey("Email"))
					{
						//Userdaten durch Email
						String email = (String)requestMap.get("Email");
						
						//SQL-Abfrage ausführen
						user = sql.getUserDataByEmail(email);
					}
					else if(requestMap.containsKey("Username"))
					{
						//Userdaten durch Username
						String username = (String)requestMap.get("Username");
						
						//SQL-Abfrage ausführen
						user = sql.getUserDataByUsername(username);
					}
					
					if(user==null)
					{
						//keine DB Verbindung oder sonstiger Fehler
						responseType = Response.Failure;
					}
					else
					{
						responseType = Response.Success;
						responseMap.put("User", user);
					}
					
					ServerResponse response = new ServerResponse(responseType, responseMap);
					
					System.out.println("Sende ServerResponse - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
			}
		}
		catch(IOException e)
		{
			System.out.println("Client-Verbindung (ID " + this.clientID + ") geschlossen.");
		} catch (ClassNotFoundException e) {
			//sollte nicht auftreten, aber muss gecatcht werden.
			e.printStackTrace();
		} catch(NullPointerException e) {
			//kann ignoriert werden
		}
		finally
		{
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				//kann ignoriert werden
				e.printStackTrace();
			} catch(NullPointerException e) {
				//kann ignoriert werden
			}
		}
	}

}
