package SEPServer;

import SEPCommon.Auction;
import SEPCommon.Response;

public class EmailHandler {

	
	protected static Response sendAuctionEndedEmail(Auction auction) {
		//Verkaufsbestätigungsmail an den Käufer der Auktion schicken
		//folgender Spezialfall: User hat kein Guthaben mehr - kein Käufer für Auktion hinterlegen und entsprechende Mail verschicken
		return null;
	}
}
