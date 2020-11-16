package SEPCommon;

public class Seller extends User {

	public String businessname;

	public Seller(int id, String username, String email, String password, byte[] picture, double wallet,
			Address address, String businessname) {
		super(id, username, email, password, picture, wallet, address);

	}

	public Seller(String username, String email, String password, byte[] picture, double wallet, Address address,
			String businessname) {
		super(username, email, password, picture, wallet, address);

	}

}
