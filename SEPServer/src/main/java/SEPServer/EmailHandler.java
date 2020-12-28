package SEPServer;

import SEPCommon.Auction;
import SEPCommon.Response;
import java.io.*;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Date;
import javax.mail.*;		
import javax.mail.internet.*;
import com.sun.mail.smtp.*;


// source: https://stackoverflow.com/questions/73580/how-do-i-send-an-smtp-message-from-java
public class EmailHandler {

	
	protected static Response sendAuctionEndedEmail(Auction auction)  {
		//Verkaufsbestätigungsmail an den Verkäufer der Auktion schicken

        try {
		
		Properties props = System.getProperties();
        props.put("mail.smtps.host","smtp.gmail.com");
        props.put("mail.smtps.auth","true");
        Session session = Session.getInstance(props, null);
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("sepgruppeb@gmail.com"));;
        
        //msg.setRecipients(Message.RecipientType.TO,
        //InternetAddress.parse(auction.getSeller().getEmail(), false));
        msg.setRecipients(Message.RecipientType.TO,
        InternetAddress.parse("marcel-just@live.de", false));
        
			msg.setSubject("Ihre Auktion wurde abgeschlossen");

        
        //msg.setText("Glückwunsch zur erfolgreichen Auktion. Der Gewinner der Auktion ist " + auction.getCurrentBidder().getAddress().getFullname() + " (E-Mail: " + auction.getCurrentBidder().getEmail() + "). Das Höchstgebot lag bei " + auction.getCurrentBid() + "$.");
        msg.setText("Glückwunsch zur erfolgreichen Auktion. Der Gewinner der Auktion ist x (E-Mail: y). Das Höchstgebot lag bei z$.");
        
        msg.setHeader("X-Mailer", "Auktion");
        msg.setSentDate(new Date());
        SMTPTransport t =
            (SMTPTransport)session.getTransport("smtps");
        t.connect("smtp.gmail.com", "sepgruppeb@gmail.com", "<wisem2020>");
        t.sendMessage(msg, msg.getAllRecipients());
        System.out.println("Response: " + t.getLastServerResponse());
        t.close();
		
		return Response.Success;
		
		} catch (MessagingException e) {
			e.printStackTrace();
			return Response.Failure;
		}
	}
	
	protected static Response sendAuctionEndedBuyerNoBalanceEmail(Auction auction) {
		//Verkaufsbestätigungsmail an den Verkäufer der Auktion schicken
		//folgender Spezialfall: User hat kein Guthaben mehr - entsprechende Mail verschicken
		
		try {
			Properties props = System.getProperties();
	        props.put("mail.smtps.host","smtp.gmail.com");
	        props.put("mail.smtps.auth","true");
	        Session session = Session.getInstance(props, null);
	        Message msg = new MimeMessage(session);
	        msg.setFrom(new InternetAddress("sepgruppeb@gmail.com"));;
	        msg.setRecipients(Message.RecipientType.TO,
	        InternetAddress.parse("yannisbromby@gmx.de", false));
	        msg.setSubject("Auktion abgebrochen");
	        msg.setText("Da der Käufer kein Guthaben mehr hat, wurde Ihre Auktion abgebrochen.");
	        msg.setHeader("X-Mailer", "Auktion");
	        msg.setSentDate(new Date());
	        SMTPTransport t =
	            (SMTPTransport)session.getTransport("smtps");
	        t.connect("smtp.gmail.com", "sepgruppeb@gmail.com", "<wisem2020>");
	      //  t.sendMessage(msg, msg.getAllRecipients());
	        System.out.println("Response: " + t.getLastServerResponse());
	        t.close();
			
			return Response.Success;
		} catch (MessagingException e) {
			e.printStackTrace();
			return Response.Failure;
		}
	}
	
	protected static Response sendAuctionEndedBuyerNoBidderEmail(Auction auction) {
		//Kein Gebot auf die Auktion
		
		try {

			return null;
		} catch (Exception e) {
		e.printStackTrace();
		return Response.Failure;
	}
		
	}
}
