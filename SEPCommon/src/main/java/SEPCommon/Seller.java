package SEPCommon;

public class Seller extends User {

	public String businessname;

	public Seller(int _id, String _username, String _email, String _password, byte[] _picture, double _wallet,
			Address _address, String _businessname)
	{
		id=_id;
		username=_username;
		email=_email;
		password=_password;
		picture=_picture;
		wallet=_wallet;
		address=_address;
		businessname = _businessname;

	}

	public Seller(String _username, String _email, String _password, byte[] _picture, double _wallet, Address _address,
			String _businessname)
	{
		username=_username;
		email=_email;
		password=_password;
		picture=_picture;
		wallet=_wallet;
		address=_address;
		businessname = _businessname;

	}

}
