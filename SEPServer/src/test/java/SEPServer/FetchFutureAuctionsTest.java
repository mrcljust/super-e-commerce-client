package SEPServer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.time.LocalDateTime;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import SEPCommon.Address;
import SEPCommon.Auction;
import SEPCommon.AuctionType;
import SEPCommon.Customer;
import SEPCommon.Response;
import SEPCommon.ShippingType;

public class FetchFutureAuctionsTest {

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
	public void fetchFutureAuctionTest1() throws SQLException
	{
		//Customer und zukünftiges Auction-Objekt erstellen
		//Beim Customer-Objekt ist nur die ID wichtig bzw. wird beim Anlegen einer Auktion zur Hinterlegung in der DB benötigt
		Address sellerAddress = new Address("Name", "Land", 12345, "Essen", "Bspstr.", "14c");
		Customer seller = new Customer(1, "Username", "email@email.de", "passwort", new byte[1], 100, sellerAddress);
		
		Auction auction = new Auction("Titel", "Beschreibung", new byte[5], 5.0, 10.0, ShippingType.Shipping, seller, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(8));
		
		//SQL Verbindung herstellen und pruefen
		sql.connect();
		assertEquals(true, sql.checkConnection());
		
		//Auktion anlegen und prüfen
		Response addFutureAuctionResponse = sql.addAuction(auction);
		assertEquals(Response.Success, addFutureAuctionResponse);
		
		Auction[] fetchedFutureAuctions = sql.fetchAuctions(AuctionType.Future);
		
		Auction futureAuctionFromDB = fetchedFutureAuctions[fetchedFutureAuctions.length-1];
		
		assertEquals("Titel", futureAuctionFromDB.getTitle());
		assertEquals("Beschreibung", futureAuctionFromDB.getDescription());
		assertArrayEquals(new byte[5], futureAuctionFromDB.getImage());
		assertEquals(5.0, futureAuctionFromDB.getMinBid(), 0);
		assertEquals(0.0, futureAuctionFromDB.getCurrentBid(), 0);
		assertEquals(null, futureAuctionFromDB.getCurrentBidder());
		assertEquals(10.0, futureAuctionFromDB.getStartPrice(), 0);
		assertEquals(1, futureAuctionFromDB.getSeller().getId());
		assertEquals(futureAuctionFromDB.getStarttime().plusHours(7), futureAuctionFromDB.getEnddate());
	
		System.out.println("Zukünftige Auktionen abfragen - Test erfolgreich");
	}
}
