package SEPServer;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
import SEPCommon.Message;
import SEPCommon.Response;
import SEPCommon.ShippingType;
import SEPCommon.User;


public class FetchMessagesTest {

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
	public void fetchMessagesTest1() throws SQLException
	{
		Address senderAddress = new Address("Name", "Land", 12345, "Essen", "Bspstr.", "14c");
		Address receiverAddress = new Address("Name", "Land", 67899, "Essen", "Musterstraße", "30");
		Customer sender= new Customer(1, "Privat1", "privat@privat.de", SEPCommon.Methods.getMd5Encryption("passwort" ),new byte[1], 100.00, senderAddress);
		Customer receiver= new Customer(2, "Privat1", "privat@privat.de", SEPCommon.Methods.getMd5Encryption("passwort" ),new byte[1], 100.00, receiverAddress);
		
		Message message= new Message(sender, receiver, "Beam me up Scotty");
		
		sql.connect();
		assertEquals(true, sql.checkConnection());
		
		Response sendMessage=sql.sendMessage(message);
		assertEquals(Response.Success, sendMessage);
		
		Message[] fetchMessages=sql.fetchReceivedMessages(receiver);
		
		Message receivedMessage= fetchMessages[fetchMessages.length-1];
		
		assertEquals(sender, receivedMessage.getSender());
		assertEquals(receiver, receivedMessage.getReceiver());
		assertEquals("Beam me up Scotty", receivedMessage.getMessage());
		
		//Nachricht wieder löschen
		PreparedStatement deleteStatement = SQL.connection.prepareStatement("DELETE FROM messages ORDER BY messages.message_id DESC LIMIT 1");
		deleteStatement.execute();
		
	}
	
}
