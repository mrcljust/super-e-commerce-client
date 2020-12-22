package SEPServer;

import SEPCommon.Auction;
import SEPCommon.Response;
import java.io.*;
import java.net.InetAddress;
import java.util.Properties;
import java.util.Date;
//import javax.mail.*;		
//import javax.mail.internet.*;
//import com.sun.mail.smtp.*;

public class EmailHandler {

	
	protected static Response sendAuctionEndedEmail(Auction auction) {
		//Verkaufsbestätigungsmail an den Verkäufer der Auktion schicken
		
//		Properties props = System.getProperties();
//        props.put("mail.smtps.host","smtp.gmail.com");
//        props.put("mail.smtps.auth","true");
//        Session session = Session.getInstance(props, null);
//        Message msg = new MimeMessage(session);
//        msg.setFrom(new InternetAddress("mail@tovare.com"));;
//        msg.setRecipients(Message.RecipientType.TO,
//        InternetAddress.parse("tov.are.jacobsen@iss.no", false));
//        msg.setSubject("Heisann "+System.currentTimeMillis());
//        msg.setText("Med vennlig hilsennTov Are Jacobsen");
//        msg.setHeader("X-Mailer", "Tov Are's program");
//        msg.setSentDate(new Date());
//        SMTPTransport t =
//            (SMTPTransport)session.getTransport("smtps");
//        t.connect("smtp.gmail.com", "admin@tovare.com", "<insert password here>");
//        t.sendMessage(msg, msg.getAllRecipients());
//        System.out.println("Response: " + t.getLastServerResponse());
//        t.close();
		
		return null;
	}
	
	protected static Response sendAuctionEndedBuyerNoBalanceEmail(Auction auction) {
		//Verkaufsbestätigungsmail an den Verkäufer der Auktion schicken
		//folgender Spezialfall: User hat kein Guthaben mehr - entsprechende Mail verschicken
		
//		Properties props = System.getProperties();
//        props.put("mail.smtps.host","smtp.gmail.com");
//        props.put("mail.smtps.auth","true");
//        Session session = Session.getInstance(props, null);
//        Message msg = new MimeMessage(session);
//        msg.setFrom(new InternetAddress("mail@tovare.com"));;
//        msg.setRecipients(Message.RecipientType.TO,
//        InternetAddress.parse("tov.are.jacobsen@iss.no", false));
//        msg.setSubject("Heisann "+System.currentTimeMillis());
//        msg.setText("Med vennlig hilsennTov Are Jacobsen");
//        msg.setHeader("X-Mailer", "Tov Are's program");
//        msg.setSentDate(new Date());
//        SMTPTransport t =
//            (SMTPTransport)session.getTransport("smtps");
//        t.connect("smtp.gmail.com", "admin@tovare.com", "<insert password here>");
//        t.sendMessage(msg, msg.getAllRecipients());
//        System.out.println("Response: " + t.getLastServerResponse());
//        t.close();
		
		return null;
	}
}
