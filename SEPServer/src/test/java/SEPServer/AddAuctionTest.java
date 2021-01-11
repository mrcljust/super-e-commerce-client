package SEPServer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import SEPCommon.Address;
import SEPCommon.Auction;
import SEPCommon.Customer;
import SEPCommon.Response;
import SEPCommon.ShippingType;

import org.junit.*;


public class AddAuctionTest {

	SEPServer.SQL sql;
	
	@Before
	public void beforeTest()
	{
		sql = new SQL();
		//ein User mit der ID 1 muss vorab in der DB existieren
	}
	
	@After
	public void afterTest()
	{
		
	}
	
	@Test
	public void addAuctionTest1() throws SQLException
	{
		//Customer und Auction Objekt erstellen
		//Beim Customer-Objekt ist nur die ID wichtig bzw. wird beim Anlegen einer Auktion zur Hinterlegung in der DB benötigt
		Address sellerAddress = new Address("Name", "Land", 12345, "Essen", "Bspstr.", "14c");
		Customer seller = new Customer(1, "Username", "email@email.de", "passwort", new byte[1], 100, sellerAddress);
		
		Auction auction = new Auction("Titel", "Beschreibung", new byte[5], 5.0, 10.0, ShippingType.Shipping, seller, LocalDateTime.now(), LocalDateTime.now().plusHours(8));
		
		//SQL Verbindung herstellen und pruefen
		sql.connect();
		assertEquals(true, sql.checkConnection());
		
		//Auktion anlegen und prüfen
		Response addAuctionResponse = sql.addAuction(auction);
		assertEquals(Response.Success, addAuctionResponse);
		
		//Pruefen ob Auktion angelegt ist
		//Zuletzt inserierte Auktion selecten und Werte pruefen
		PreparedStatement fetchLastAuction = SQL.connection.prepareStatement("SELECT * FROM auctions ORDER BY auction_id DESC LIMIT 1", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ResultSet fetchLastAuctionResult = fetchLastAuction.executeQuery();
		fetchLastAuctionResult.first();
		
		assertEquals("Titel", fetchLastAuctionResult.getString("title"));
		assertEquals("Beschreibung", fetchLastAuctionResult.getString("description"));
		assertArrayEquals(new byte[5], fetchLastAuctionResult.getBytes("image"));
		assertEquals(5.0, fetchLastAuctionResult.getDouble("minbid"), 0);
		assertEquals(0.0, fetchLastAuctionResult.getDouble("currentbid"), 0);
		assertEquals(0, fetchLastAuctionResult.getInt("currentbidder_id"));
		assertEquals(10.0, fetchLastAuctionResult.getDouble("startprice"), 0);
		assertEquals(1, fetchLastAuctionResult.getInt("seller_id"));
		assertEquals(0, fetchLastAuctionResult.getInt("emailsent"));
		assertEquals((fetchLastAuctionResult.getTimestamp("starttime").toLocalDateTime()).plusHours(8), fetchLastAuctionResult.getTimestamp("enddate").toLocalDateTime());
	
		//Auktion wieder löschen
		PreparedStatement deleteStatement = SQL.connection.prepareStatement("DELETE FROM auctions ORDER BY auction_id DESC LIMIT 1");
		deleteStatement.execute();
		
		System.out.println("Auktion anlegen - Test erfolgreich");
	}
}
