package SEPServer;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
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
		
		sql.connect();
		assertEquals(true, sql.checkConnection());
		
		Address senderAddress = new Address("Name", "Land", 12345, "Essen", "Bspstr.", "14c");
		Address receiverAddress = new Address("Name", "Land", 67899, "Essen", "Musterstraße.", "30");
		String emailStringSender= "privat@privatSender.de";
		
		User sender= new Customer("PrivatSender", emailStringSender, SEPCommon.Methods.getMd5Encryption("passwortSender" ),new byte[1], 100.00, senderAddress);
		User receiver= new Customer(999,"PrivatReceiver", "privat@privatReceiver.de", SEPCommon.Methods.getMd5Encryption("passwortReceiver" ),new byte[1], 100.00, receiverAddress);
		
		//Sender in DB anlegen
		Response registerUserResponse = sql.registerUser(sender);																										//den zuvor erstellten Seller registrieren
		Response expectedResponses[] = {Response.Success, Response.EmailTaken, Response.UsernameTaken};																	//Array mit möglichen Ausgängen
		List<Response> expectedResponsesList = Arrays.asList(expectedResponses);		
		assertTrue(expectedResponsesList.contains(registerUserResponse));			
		
		
		// User aus DB holen, um ID zu erhalten und prüfen
		User senderFromDb =  sql.getUserDataByEmail(emailStringSender);
		
		assertEquals("PrivatSender", senderFromDb.getUsername());
		assertEquals(emailStringSender, senderFromDb.getEmail());
		assertEquals(SEPCommon.Methods.getMd5Encryption("passwortSender"), senderFromDb.getPassword());
		assertArrayEquals(new byte[1], senderFromDb.getPicture());
		assertEquals(100, senderFromDb.getWallet(), 0);
		assertEquals("Name", senderFromDb.getAddress().getFullname());
		assertEquals("Land", senderFromDb.getAddress().getCountry());
		assertEquals(12345, senderFromDb.getAddress().getZipcode());
		assertEquals("Essen", senderFromDb.getAddress().getCity());
		assertEquals("Bspstr.", senderFromDb.getAddress().getStreet());
		assertEquals("14c", senderFromDb.getAddress().getNumber());

		//Message Objekt erstellen
		Message message= new Message(senderFromDb, receiver, "Beam me up Scotty");
		
		//Message schicken und prüfen
		Response sendMessage=sql.sendMessage(message);
		assertEquals(Response.Success, sendMessage);
		
		Message[] fetchMessages=sql.fetchReceivedMessages(receiver);
		Message receivedMessage= fetchMessages[fetchMessages.length-1];
		
		assertEquals(senderFromDb.getId(), receivedMessage.getSender().getId());
		assertEquals(receiver.getId(), receivedMessage.getReceiver().getId());
		assertEquals("Beam me up Scotty", receivedMessage.getMessage());
		
		//Nachricht wieder löschen
		PreparedStatement deleteStatement = SQL.connection.prepareStatement("DELETE FROM messages ORDER BY messages.message_id DESC LIMIT 1");
		deleteStatement.execute();
		
		//user wieder löschen
		sql.deleteUser(senderFromDb);
		System.out.println("Nachrichten erhalten - Test erfolgreich");
		
	}
	
}
