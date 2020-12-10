package SEPClient;

import java.io.IOException;

import SEPCommon.Auction;
import SEPCommon.Order;
import SEPCommon.User;

public class CreateRatingController {

	private static Order order = null;
	private static User user = null;
	private static Auction auction = null;

	public static void setUser(User _user) {
		user = _user;
	}

	public static void setOrder(Order _order) {
		order = _order;
	}

	public static void setAuction(Auction _auction) {
		auction = _auction;
	}

	public void initialize() throws IOException {

	}

}
