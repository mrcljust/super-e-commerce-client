package SEPServer;

import SEPCommon.Auction;
import SEPCommon.Response;

public class EmailHandler {

	
	protected static Response sendAuctionEndedEmail(Auction auction) {
		//Verkaufsbest�tigungsmail an den Verk�ufer der Auktion schicken
		return null;
	}
	
	protected static Response sendAuctionEndedBuyerNoBalanceEmail(Auction auction) {
		//Verkaufsbest�tigungsmail an den Verk�ufer der Auktion schicken
		//folgender Spezialfall: User hat kein Guthaben mehr - entsprechende Mail verschicken
		return null;
	}
}
