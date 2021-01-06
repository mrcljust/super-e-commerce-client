package SEPServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import SEPCommon.Auction;
import SEPCommon.AuctionType;
import SEPCommon.ClientRequest;
import SEPCommon.Customer;
import SEPCommon.Order;
import SEPCommon.Product;
import SEPCommon.Rating;
import SEPCommon.Request;
import SEPCommon.Response;
import SEPCommon.ServerResponse;
import SEPCommon.User;

public class ServerThread implements Runnable {
	private Socket client;
	private int clientID;
	private SQL sql;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	ServerThread(Socket clientSocket, int _clientID)
	{
		this.client = clientSocket;
		this.clientID = _clientID;
		sql = new SQL();
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
				System.out.println("ClientRequest - Client-ID " + this.clientID + " - " + clientreq.getRequestType() + " - " + clientreq.getRequestMap());
				
				
				//Verschiedene Requests handlen
				
				//Request RegisterUser
				//HASHMAP: "User" - UserObjekt
				if(requestType == Request.RegisterUser)
				{
					User argUser = (User)requestMap.get("User");
					
					//SQL-Abfrage ausführen
					Response responseType = sql.registerUser(argUser);
					ServerResponse response = new ServerResponse(responseType, null);
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
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
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
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
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
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
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
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
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
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
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
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
					if(requestMap == null)
					{
						//alle Produkte suchen
						
						//SQL-Abfrage ausführen
						products = sql.fetchAllProducts();
					}
					else if(requestMap.containsKey("Category"))
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
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
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
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
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
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
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
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request AddItems
				//HASHMAP: "User" - Userobjekt (Verkäufer), "Products" - Hinzuzufügende Produkte
				else if(requestType == Request.AddItems)
				{
					User argUser = (User)requestMap.get("User");
					Product[] argProducts = (Product[])requestMap.get("Products");
					
					//SQL-Abfrage ausführen
					Response responseType = sql.addItems(argUser, argProducts);
					ServerResponse response = new ServerResponse(responseType, null);
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request BuyItem
				//HASHMAP: "User" - Userobjekt (Käufer), "Product" - Zu kaufendes Produkt
				else if(requestType == Request.BuyItem)
				{
					User argUser = (User)requestMap.get("User");
					Product argProduct = (Product)requestMap.get("Product");
					
					//SQL-Abfrage ausführen
					Response responseType = sql.buyItem(argUser, argProduct);
					ServerResponse response = new ServerResponse(responseType, null);
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
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
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request CreateAuction
				//HASHMAP: "Auction" - Auction-Objekt
				else if(requestType == Request.CreateAuction)
				{
					Auction argAuction = (Auction)requestMap.get("Auction");
					
					//SQL-Abfrage ausführen
					Response responseType = sql.addAuction(argAuction);
					ServerResponse response = new ServerResponse(responseType, null);
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request SendBid
				//HASHMAP: "Auction" - Auction-Objekt, "Bidder" - Customer-Objekt des Bieters, "Bid" - Bietbetrag
				else if(requestType == Request.SendBid)
				{
					Auction argAuction = (Auction)requestMap.get("Auction");
					Customer argCustomer = (Customer)requestMap.get("Bidder");
					Double argBid = (Double)requestMap.get("Bid");
					
					//SQL-Abfrage ausführen
					Response responseType = sql.sendBid(argAuction, argCustomer, argBid);
					ServerResponse response = new ServerResponse(responseType, null);
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request FetchAuctions,
				//HASHMAP: "AuctionType" - AuctionType-Objekt --> Active, Ended, Future
				
				//Request FetchAuctions,
				//HASHMAP: "AuctionType" - AuctionType-Objekt, "User" - Userobjekt --> MyBids, MyAuctions, SavedAuctions
				
				//Request FetchAuctions mit Suchbegriff
				//HASHMAP: "AuctionType" - AuctionType-Objekt, "SearchString" - Suchbegriff
				else if(requestType == Request.FetchAuctions)
				{
					AuctionType argAuctionType = (AuctionType)requestMap.get("AuctionType");
					Auction[] auctions = null;
					
					if(requestMap.containsKey("User"))
					{
						User argUser = (User)requestMap.get("User");
						if(argAuctionType==AuctionType.MyAuctions)
						{
							auctions = sql.fetchOwnAuctions(argUser);
						}
						else if(argAuctionType==AuctionType.MyBids)
						{
							auctions = sql.fetchAuctionsUserBiddedOn(argUser);
						}
						else if(argAuctionType==AuctionType.SavedAuctions)
						{
							auctions = sql.fetchSavedAuctions(argUser);
						}
						else if(argAuctionType==AuctionType.PurchasedAuctions)
						{
							auctions = sql.fetchPurchasedAuctions(argUser);
						}
						else if(argAuctionType==AuctionType.SoldAuctions)
						{
							auctions = sql.fetchSoldAuctions(argUser);
						}
					}
					else if(requestMap.containsKey("SearchString"))
					{
						//nach Produkten mit Suchbegriff suchen
						String searchString = (String)requestMap.get("SearchString");
						
						//SQL-Abfrage ausführen
						auctions = sql.fetchAuctionsByString(searchString, argAuctionType);
					}
					else //kein SearchString
					{
						//SQL-Abfrage ausführen
						auctions = sql.fetchAuctions(argAuctionType);
					}
					
					Response responseType;
					HashMap<String, Object> responseMap = new HashMap<String, Object>();
					
					if(auctions==null)
					{
						//keine DB Verbindung oder sonstiger Fehler
						responseType = Response.Failure;
					}
					else
					{
						responseType = Response.Success;
						responseMap.put("Auctions", auctions);
					}
					
					ServerResponse response = new ServerResponse(responseType, responseMap);
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request SaveAuction
				//HASHMAP: "Auction" - Zu speichernde Auktion, "User" - Userobjekt, 
				
				else if(requestType == Request.SaveAuction)
				{
					Auction argAuction = (Auction)requestMap.get("Auction");
					User argUser = (User)requestMap.get("User");
					
					//SQL-Abfrage ausführen
					Response responseType = sql.saveAuction(argUser, argAuction);
					ServerResponse response = new ServerResponse(responseType, null);
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request DeleteOrder
				//HASHMAP: "Order" - Order-Objekt, "Buyer" - Customer-Objekt des Kaeufers
				else if(requestType == Request.DeleteOrder)
				{
					Order argOrder = (Order)requestMap.get("Order");
					
					//SQL-Abfrage ausführen
					Response responseType = sql.deleteOrder(argOrder);
					ServerResponse response = new ServerResponse(responseType, null);
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request SendRating
				//HASHMAP: "Rating" - Rating-Objekt
				else if(requestType == Request.SendRating)
				{
					Rating argRating = (Rating)requestMap.get("Rating");
					
					//SQL-Abfrage ausführen
					Response responseType = sql.SendRating(argRating);
					ServerResponse response = new ServerResponse(responseType, null);
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request FetchRatings
				//HASHMAP: "User" - User-Objekt, "FetchAvg" - Boolean, ob durchschnittliche Bewertungen oder Rating[] 
				else if(requestType == Request.FetchRatings)
				{
					User argUser = (User)requestMap.get("User");
					Boolean argFetchAvg = (Boolean)requestMap.get("FetchAvg");
					
					Response responseType;
					HashMap<String, Object> responseMap = new HashMap<String, Object>();
					
					if(argFetchAvg)
					{
						//durchschnittliche Bewertungen
						
						//SQL-Abfrage ausführen
						double[] avgDoubleArray = sql.fetchAvgRating(argUser);
						
						if(avgDoubleArray==null)
						{
							responseType = Response.Failure;
						}
						else
						{
							// index 0 ist Average, Index 1 ist Anzahl
							responseType = Response.Success;
							double avgRating = avgDoubleArray[0];
							Integer ratingCount = (int)avgDoubleArray[1];
							responseMap.put("Average", avgRating);
							responseMap.put("Amount", ratingCount);
						}
					}
					else
					{
						//alle Bewertungen
						
						//SQL-Abfrage ausführen
						Rating[] ratings = sql.fetchRatings(argUser);
						if(ratings==null)
						{
							responseType = Response.Failure;
						}
						else
						{
							responseType = Response.Success;
							responseMap.put("Ratings", ratings);
						}
					}
					
					ServerResponse response = new ServerResponse(responseType, responseMap);
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request FetchOrders
				//HASHMAP: "Buyer" - User-Objekt
				else if(requestType == Request.FetchOrders)
				{
					User argUser = (User)requestMap.get("Buyer");
					Order[] orders = sql.fetchOrders(argUser);
					
					Response responseType;
					HashMap<String, Object> responseMap = new HashMap<String, Object>();
					
					if(orders==null)
					{
						//keine DB Verbindung oder sonstiger Fehler
						responseType = Response.Failure;
					}
					else
					{
						responseType = Response.Success;
						responseMap.put("Orders", orders);
					}
					
					ServerResponse response = new ServerResponse(responseType, responseMap);
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request FetchSales
				//HASHMAP: "User" - User-Objekt
				else if(requestType == Request.FetchSales)
				{
					User argUser = (User)requestMap.get("User");
					Order[] sales = sql.fetchSales(argUser);
					
					Response responseType;
					HashMap<String, Object> responseMap = new HashMap<String, Object>();
					
					if(sales==null)
					{
						//keine DB Verbindung oder sonstiger Fehler
						responseType = Response.Failure;
					}
					else
					{
						responseType = Response.Success;
						responseMap.put("Sales", sales);
					}
					
					ServerResponse response = new ServerResponse(responseType, responseMap);
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
					out.writeObject(response);
				}
				
				//Request GetServerDateTime
				else if(requestType == Request.GetServerDateTime)
				{
					HashMap<String, Object> responseMap = new HashMap<String, Object>();
					LocalDateTime serverDateTime = LocalDateTime.now();
					responseMap.put("ServerDateTime", serverDateTime);
					ServerResponse response = new ServerResponse(Response.Success, responseMap);
					
					System.out.println("Sende ServerResponse - Client-ID " + this.clientID + " - " + response.getResponseType() + " - " + response.getResponseMap());
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
