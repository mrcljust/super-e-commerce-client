package SEPServer;

import static org.junit.Assert.assertEquals;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import SEPCommon.Address;
import SEPCommon.Product;
import SEPCommon.Response;
import SEPCommon.Seller;

public class UpdatePriceTest {

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
	public void updatePriceTest1() throws SQLException
	{
		//sql verbindung herstellen und pruefen
		sql.connect();
		assertEquals(true, sql.checkConnection());
		
		//seller-objekt und product-objekt erstellen
		//die id ist wichtig, da sie in der sql-datenbank gespeichert wird
		Address sellerAddress = new Address("Name", "Land", 12345, "Essen", "Bspstr.", "14c");
		Seller seller = new Seller(10000, "Username", "email@email.de", "passwort", new byte[1], 100, sellerAddress, "Gewerbename UG");
		
		Product product = new Product("Produktname", 99.99, seller, "Testkategorie", "Beispielbeschreibung");
		
		//product-objekt in datenbank speichern, anschließend werte pruefen
		Response addItemResponse = sql.addItem(seller, product);
		assertEquals(Response.Success, addItemResponse);
		
		//zuletzt inseriertes produkt selecten und price sowie oldprice Werte pruefen (muessen identisch sein)
		PreparedStatement fetchLastProduct = SQL.connection.prepareStatement("SELECT * FROM products ORDER BY id DESC LIMIT 1", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ResultSet fetchLastProductResult = fetchLastProduct.executeQuery();
		fetchLastProductResult.first();
		
		assertEquals(99.99, fetchLastProductResult.getDouble("price"), 0); //0 = maximal zulaessiger abstand zwischen den zwei werten
		assertEquals(99.99, fetchLastProductResult.getDouble("oldprice"), 0);
		
		//aus der db ausgelesene produkt id dem product-Objekt zuweisen, damit anschliessend der preis geaendert werden kann
		product.setId(fetchLastProductResult.getInt("id"));
		
		//preis reduzieren auf 49,99
		Response updatePriceResponse = sql.updatePrice(product, 49.99);
		assertEquals(Response.Success, updatePriceResponse);
		
		//query erneut ausfuehren und price sowie oldprice Werte pruefen (muessen sich nun unterscheiden)
		fetchLastProductResult = fetchLastProduct.executeQuery();
		fetchLastProductResult.first();
		
		assertEquals(49.99, fetchLastProductResult.getDouble("price"), 0); //0 = maximal zulaessiger abstand zwischen den zwei werten
		assertEquals(99.99, fetchLastProductResult.getDouble("oldprice"), 0);
		
		//Produkt wieder löschen
		PreparedStatement deleteStatement = SQL.connection.prepareStatement("DELETE FROM products ORDER BY id DESC LIMIT 1");
		deleteStatement.execute();
		
		System.out.println("Preis aktualisieren - Test erfolgreich");
	} 
	
}