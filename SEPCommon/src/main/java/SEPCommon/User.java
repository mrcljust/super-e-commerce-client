package SEPCommon;

public class User {

	private int id;
	private String username;
	private String email;
	private int password;
	private byte[] picture;
	private double wallet;
	private Address address;

	public User(String username, String email, String password, byte[] pciture, double wallet, Address address) {

	}

	public User(int id, String username, String email, String password, byte[] pciture, double wallet,
			Address address) {

	}
}
