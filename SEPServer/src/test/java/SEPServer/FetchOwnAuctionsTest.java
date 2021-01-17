package SEPServer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import SEPCommon.Address;
import SEPCommon.Auction;
import SEPCommon.Customer;
import SEPCommon.Response;
import SEPCommon.ShippingType;
import SEPCommon.User;

public class FetchOwnAuctionsTest {

SQL sql;
	
	@Before
	public void beforeTest()
	{
		sql = new SQL();
	}
	
	@After
	public void afterTest()
	{
		
	}
	
	@Test
	public void fetchOwnAuctionsTest1() throws SQLException
	{
		//SQL Verbindung herstellen und pruefen
		sql.connect();
		assertEquals(true, sql.checkConnection());
		
		//Customer-Objekt erstellen
		Address sellerAddress = new Address("Name", "Land", 12345, "Essen", "Bspstr.", "14c");
		String emailString = "emailownauctionstest@email.de";
		Customer seller = new Customer("UsernameOwnAuctionsTest", emailString, SEPCommon.Methods.getMd5Encryption("passwort"), new byte[1], 100, sellerAddress);		//Passwort encrypten um  auch vergleichen zu können, Verkäufer muss Customer sein
		
		//User anlegen und prüfen
		Response registerUserResponse = sql.registerUser(seller);																										//den zuvor erstellten Seller registrieren
		Response expectedResponses[] = {Response.Success, Response.EmailTaken, Response.UsernameTaken};																	//Array mit möglichen Ausgängen
		List<Response> expectedResponsesList = Arrays.asList(expectedResponses);		
		assertTrue(expectedResponsesList.contains(registerUserResponse));																								//array zu List, und dann überprüfen ob in List vorhanden	
		
		//User aus DB holen, um ID zu erhalten und prüfen
		User sellerFromDB = sql.getUserDataByEmail(emailString);
		
		assertEquals("UsernameOwnAuctionsTest", sellerFromDB.getUsername());
		assertEquals(emailString, sellerFromDB.getEmail());
		assertEquals(SEPCommon.Methods.getMd5Encryption("passwort"), sellerFromDB.getPassword());
		assertArrayEquals(new byte[1], sellerFromDB.getPicture());
		assertEquals(100, sellerFromDB.getWallet(), 0);
		assertEquals("Name", sellerFromDB.getAddress().getFullname());
		assertEquals("Land", sellerFromDB.getAddress().getCountry());
		assertEquals(12345, sellerFromDB.getAddress().getZipcode());
		assertEquals("Essen", sellerFromDB.getAddress().getCity());
		assertEquals("Bspstr.", sellerFromDB.getAddress().getStreet());
		assertEquals("14c", sellerFromDB.getAddress().getNumber());
		
		//Auction-Objekt erstellen
		Auction auction = new Auction("Titel", "Beschreibung", new byte[5], 5.0, 10.0, ShippingType.Shipping, (Customer)sellerFromDB, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(8));
		
		//Auktion anlegen und prüfen
		Response addOwnAuctionResponse = sql.addAuction(auction);
		assertEquals(Response.Success, addOwnAuctionResponse);
		
		Auction[] fetchedOwnAuctions = sql.fetchOwnAuctions(sellerFromDB);
		
		Auction ownAuctionFromDB = fetchedOwnAuctions[fetchedOwnAuctions.length-1];																						//-1 ist letztes Objekt, da neuste Auktion am ende 
		
		assertEquals("Titel", ownAuctionFromDB.getTitle());
		assertEquals("Beschreibung", ownAuctionFromDB.getDescription());
		assertArrayEquals(new byte[5], ownAuctionFromDB.getImage());
		assertEquals(5.0, ownAuctionFromDB.getMinBid(), 0);
		assertEquals(0.0, ownAuctionFromDB.getCurrentBid(), 0);
		assertEquals(null, ownAuctionFromDB.getCurrentBidder());
		assertEquals(10.0, ownAuctionFromDB.getStartPrice(), 0);
		assertEquals(sellerFromDB.getId(), ownAuctionFromDB.getSeller().getId());
		assertEquals(ownAuctionFromDB.getStarttime().plusHours(7), ownAuctionFromDB.getEnddate());
	
		
		//Auktion wieder löschen
		PreparedStatement deleteStatement = SQL.connection.prepareStatement("DELETE FROM auctions ORDER BY auction_id DESC LIMIT 1");
		deleteStatement.execute();
		
		//User wieder löschen
		sql.deleteUser(sellerFromDB);
		System.out.println("Eigene Auktionen abfragen - Test erfolgreich");
	}
}
