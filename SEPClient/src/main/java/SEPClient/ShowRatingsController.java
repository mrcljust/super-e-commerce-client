package SEPClient;

import java.io.IOException;

import SEPCommon.User;

public class ShowRatingsController {

	private static User user = null;
	private static boolean viewOwnRatings = false;

	public static void setUser(User _user) {
		user = _user;
	}

	public static void setViewOwnRatings(boolean _viewOwnRatings) {
		viewOwnRatings = _viewOwnRatings;
	}

	public void initialize() throws IOException {

	}

}
