package SEPCommon;

public class Customer extends User {
	public Customer(String _username, String _email, String _password, byte[] _picture, double _wallet, Address _address)
	{
		username=_username;
		email=_email;
		password=_password;
		picture=_picture;
		wallet=_wallet;
		address=_address;
	}

	public Customer(int _id, String _username, String _email, String _password, byte[] _picture, double _wallet,
			Address _address)
	{
		id=_id;
		username=_username;
		email=_email;
		password=_password;
		picture=_picture;
		wallet=_wallet;
		address=_address;
	}
}
