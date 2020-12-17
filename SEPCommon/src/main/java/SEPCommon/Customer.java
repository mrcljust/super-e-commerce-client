package SEPCommon;

import java.io.Serializable;

public class Customer extends User implements Serializable {
	
	//Konstruktor mit ID
	public Customer(int _id, String _username, String _email, String _password, byte[] _picture, double _wallet, Address _address)
	{
		id=_id;
		username=_username;
		email=_email;
		password=_password;
		picture=_picture;
		wallet=_wallet;
		address=_address;
	}
	
	//Konstruktor ohne ID
	public Customer(String _username, String _email, String _password, byte[] _picture, double _wallet, Address _address)
	{
		username=_username;
		email=_email;
		password=_password;
		picture=_picture;
		wallet=_wallet;
		address=_address;
	}
}
