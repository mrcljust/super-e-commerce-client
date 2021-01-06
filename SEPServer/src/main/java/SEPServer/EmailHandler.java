package SEPServer;

import SEPCommon.Auction;
import SEPCommon.Response;
import java.util.Properties;
import javax.mail.*;		
import javax.mail.internet.*;


// source: https://mkyong.com/java/javamail-api-sending-email-via-gmail-smtp-example/
// By mkyong | Last updated: April 10, 2019
public class EmailHandler {
	static Session session;
	static Properties properties;
	
	private static void setProperties()
	{
		properties = new Properties();
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
        session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("sepgruppeb@gmail.com", "wisem2020");
            }
        });
	}
	
	protected static Response sendAuctionEndedEmail(Auction auction)  {
		//Verkaufsbestätigungsmail an den Verkäufer der Auktion schicken
	        
		setProperties();
		
        try {
	        Message msg = new MimeMessage(session);
	        msg.setFrom(new InternetAddress("sepgruppeb@gmail.com"));;
	        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(auction.getSeller().getEmail()));
	        
			msg.setSubject("Ihre Auktion '" + auction.getTitle() + "' wurde abgeschlossen");
	
	        
	        msg.setText("Glückwunsch zur erfolgreichen Auktion '" + auction.getTitle() + "' (ID " + auction.getId() + "). Der Gewinner der Auktion ist " + auction.getCurrentBidder().getAddress().getFullname() + " (E-Mail: " + auction.getCurrentBidder().getEmail() + "). Das Höchstgebot lag bei " + auction.getCurrentBid() + "$.");
	        
	        Transport.send(msg);
			
			return Response.Success;
		
		} catch (MessagingException e) {
			e.printStackTrace();
			return Response.Failure;
		}
	}
	
	protected static Response sendAuctionEndedBuyerNoBalanceEmail(Auction auction) {
		//Verkaufsbestätigungsmail an den Verkäufer der Auktion schicken
		//folgender Spezialfall: User hat kein Guthaben mehr - entsprechende Mail verschicken
		
		setProperties();
        
		try {
	        Message msg = new MimeMessage(session);
	        msg.setFrom(new InternetAddress("sepgruppeb@gmail.com"));;
	        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(auction.getSeller().getEmail()));
			msg.setSubject("Ihre Auktion '" + auction.getTitle() + "' wurde abgebrochen");
	        msg.setText("Ihre Auktion '" + auction.getTitle() + "' (ID " + auction.getId() + ") wurde beendet. Da der Käufer (" + auction.getCurrentBidder().getAddress().getFullname() + ", E-Mail: " + auction.getCurrentBidder().getEmail() + ") kein Guthaben mehr hat, wurde die Auktion abgebrochen. Das Höchstgebot und der Bieter wurden zurückgesetzt.");

	        Transport.send(msg);
			
			return Response.Success;
		} catch (MessagingException e) {
			e.printStackTrace();
			return Response.Failure;
		}
	}
	
	protected static Response sendAuctionEndedBuyerNoBidderEmail(Auction auction) {
		//Kein Gebot auf die Auktion
		
		setProperties();
	        
		try {
	        Message msg = new MimeMessage(session);
	        msg.setFrom(new InternetAddress("sepgruppeb@gmail.com"));;
	        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(auction.getSeller().getEmail()));
			msg.setSubject("Ihre Auktion '" + auction.getTitle() + "' wurde ohne Bieter beendet");
	        msg.setText("Ihre Auktion '" + auction.getTitle() + "' (ID " + auction.getId() + ") wurde beendet. Leider gab es keine Gebote.");

	        Transport.send(msg);
			
			return Response.Success;
		} catch (MessagingException e) {
			e.printStackTrace();
			return Response.Failure;
		}
		
	}
}
