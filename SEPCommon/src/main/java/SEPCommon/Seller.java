package SEPCommon;

public class Seller extends User {

	private String businessname;

	//Konstruktor mit ID
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

	//Konstruktor ohne ID
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

	//Getter Methoden
	
	private String getBusinessname()
	{
		return businessname;
	}
	
	//Setter Methoden
	
	private void setBusinessname(String _businessname)
	{
		businessname=_businessname;
	}
}
