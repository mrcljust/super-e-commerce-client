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
        msg.setRecipients(Message.RecipientType.TO,
        InternetAddress.parse("yannisbromby@gmx.de", false));
        msg.setSubject("Auktion abgeschlossen "+System.currentTimeMillis());
        msg.setText("Glückwunsch zur erfolgreichen Auktion. Das Produt wurde zu x $ an y verkauft.");
        msg.setHeader("X-Mailer", "Auktion");
        msg.setSentDate(new Date());
        SMTPTransport t =
            (SMTPTransport)session.getTransport("smtps");
        t.connect("smtp.gmail.com", "sepgruppeb@gmail.com", "<wisem2020>");
        t.sendMessage(msg, msg.getAllRecipients());
        System.out.println("Response: " + t.getLastServerResponse());
        t.close();
		
		return Response.Success;
		} finally {
			return null;
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
	        msg.setSubject("Auktion abgeschlossen "+System.currentTimeMillis());
	        msg.setText("Glückwunsch zur erfolgreichen Auktion. Das Produt wurde zu x $ an y verkauft.");
	        msg.setHeader("X-Mailer", "Auktion");
	        msg.setSentDate(new Date());
	        SMTPTransport t =
	            (SMTPTransport)session.getTransport("smtps");
	        t.connect("smtp.gmail.com", "sepgruppeb@gmail.com", "<wisem2020>");
	      //  t.sendMessage(msg, msg.getAllRecipients());
	        System.out.println("Response: " + t.getLastServerResponse());
	        t.close();
			
			return Response.Success;
			} finally {
				return null;
			} 
		}
}
