package SEPServer;

import SEPCommon.Auction;
import SEPCommon.Response;

public class EmailHandler {

	
	protected static Response sendAuctionEndedEmail(Auction auction) {
		//Verkaufsbest�tigungsmail an den K�ufer der Auktion schicken
		//folgender Spezialfall: User hat kein Guthaben mehr - kein K�ufer f�r Auktion hinterlegen und entsprechende Mail verschicken
		return null;
	}
}
