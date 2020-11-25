package SEPCommon;

import java.io.Serializable;

public class Seller extends User implements Serializable {

	private String businessname;

	//Konstruktor mit ID (bestehende Seller aus der DB)
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
//test
	//Konstruktor ohne ID (neue Seller)
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
	
	public String getBusinessname()
	{
		return businessname;
	}
	
	//Setter Methoden
	
	public void setBusinessname(String _businessname)
	{
		businessname=_businessname;
	}
}
