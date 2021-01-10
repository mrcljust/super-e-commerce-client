package Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import SEPCommon.Address;
import SEPCommon.Auction;
import SEPCommon.Customer;
import SEPCommon.Response;
import SEPCommon.ShippingType;
import SEPServer.SQL;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;



public class AddAuctionTest {

	SQL sql;
	
	@BeforeAll
	public void beforeTest()
	{
		sql = new SQL();
	}
	
	@AfterAll
	public void afterTest()
	{
		
	}
	
	@Test
	public void addAuctionTest1() throws SQLException
	{
		//Beim Sellerobjekt ist nur die ID wichtig bzw. wird beim Anlegen einer Auktion zur Hinterlegung in der DB benötigt
		Address sellerAddress = new Address("Name", "Land", 12345, "Essen", "Bspstr.", "14c");
		Customer seller = new Customer(1, "Username", "email@email.de", "passwort", new byte[1], 100, sellerAddress);
		
		Auction auction = new Auction("Titel", "Beschreibung", new byte[1], 5.0, 10.0, ShippingType.Shipping, seller, LocalDateTime.now(), LocalDateTime.now().plusHours(8));
		
		//SQL Verbindung herstellen und pruefen
		sql.connect();
		assertEquals(true, sql.checkConnection());
		
		//Auktion anlegen und prüfen
		Response addAuctionResponse = sql.addAuction(auction);
		assertEquals(Response.Success, addAuctionResponse);
		
		//Pruefen ob Auktion angelegt ist
		//Zuletzt inserierte Auktion selecten und Werte pruefen
		PreparedStatement fetchLastAuction = SQL.connection.prepareStatement("SELECT * FROM auctions ORDER BY ID DESC LIMIT 1");
		ResultSet fetchLastAuctionResult = fetchLastAuction.executeQuery();
		
		assertEquals("Titel", fetchLastAuctionResult.getString("title"));
		assertEquals("Beschreibung", fetchLastAuctionResult.getString("description"));
		assertEquals(new byte[1], fetchLastAuctionResult.getByte("image"));
		assertEquals(5.0, fetchLastAuctionResult.getDouble("minbid"));
		assertEquals(0.0, fetchLastAuctionResult.getDouble("currentbid"));
		assertEquals(0, fetchLastAuctionResult.getInt("currentbidder_id"));
		assertEquals(10.0, fetchLastAuctionResult.getDouble("startprice"));
		assertEquals(1, fetchLastAuctionResult.getInt("seller_id"));
		assertEquals(0, fetchLastAuctionResult.getInt("emailsent"));
		assertTrue((fetchLastAuctionResult.getTimestamp("starttime").toLocalDateTime()).plusHours(8) == fetchLastAuctionResult.getTimestamp("enddate").toLocalDateTime());
	}
	
}
