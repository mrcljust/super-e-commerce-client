package SEPServer;

import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import SEPCommon.Address;
import SEPCommon.Customer;
import SEPCommon.Message;
import SEPCommon.Response;

public class SendMessageTest {

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
	public void sendMessageTest1() throws SQLException
	{
				//Sender und Receiver (2x Customer) Objekte erstellen
				//Bei den Objekten ist nur die ID wichtig bzw. wird beim Speichern einer Nachricht in der DB benötigt
				Address senderAddress = new Address("Name", "Land", 12345, "Essen", "Bspstr.", "14c");
				Address receiverAddress = new Address("Name2", "Land2", 123456, "Essen2", "Bspstr.2", "14c2");
				Customer sender = new Customer(8888, "Username", "email@email.de", "passwort2", new byte[1], 100, senderAddress);
				Customer receiver = new Customer(9999, "Username2", "email2@email.de", "passwort2", new byte[2], 200, receiverAddress);
				
				//Message-Objekt erstellen
				String messageText = "Hallo, dies ist eine Nachricht.";
				Message message = new Message(sender, receiver, messageText);
				
				//SQL Verbindung herstellen und pruefen
				sql.connect();
				assertEquals(true, sql.checkConnection());
				
				//Nachricht in Datenbank speichern ("verschicken") und pruefen
				Response sendMessageResponse = sql.sendMessage(message);
				assertEquals(Response.Success, sendMessageResponse);
				
				//Pruefen ob Nachricht in Datenbank existiert
				//Zuletzt versandte/ gespeicherte Message auswählen
				PreparedStatement fetchLastMessage = SQL.connection.prepareStatement("SELECT * FROM messages ORDER BY message_id DESC LIMIT 1", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet fetchLastMessageResult = fetchLastMessage.executeQuery();
				fetchLastMessageResult.first();
				
				//Werte der zuletzt gespeicherten Nachricht in der DB pruefen
				assertEquals(8888, fetchLastMessageResult.getInt("sender_id"));
				assertEquals(9999, fetchLastMessageResult.getInt("receiver_id"));
				assertEquals(messageText, fetchLastMessageResult.getString("text"));

				//Message aus der DB wieder löschen
				PreparedStatement deleteStatement = SQL.connection.prepareStatement("DELETE FROM messages ORDER BY message_id DESC LIMIT 1");
				deleteStatement.execute();
				
				System.out.println("Nachricht senden - Test erfolgreich");
	}
	
}
