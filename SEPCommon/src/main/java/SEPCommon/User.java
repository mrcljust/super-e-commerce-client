package SEPCommon;

import java.io.Serializable;

public class User implements Serializable{

	protected int id;
	protected String username;
	protected String email;
	protected String password;
	protected byte[] picture;
	protected double wallet;
	protected Address address;
	
	//Konstruktoren in den jeweiligen erbenden Klassen (Customer und Seller)
	
	//Getter Methoden
	
	public int getId()
	{
		return id;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public byte[] getPicture()
	{
		return picture;
	}
	
	public double getWallet()
	{
		return wallet;
	}
	
	public Address getAddress()
	{
		return address;
	}
	
	//Setter Methoden
	
	public void setId(int _id)
	{
		id=_id;
	}
	
	public void setUsername(String _username)
	{
		username=_username;
	}
	
	public void getEmail(String _email)
	{
		email=_email;
	}
	
	public void setPassword(String _password)
	{
		password=_password;
	}
	
	public void setPicture(byte[] _picture)
	{
		picture=_picture;
	}
	
	public void setWallet(double _wallet)
	{
		wallet=_wallet;
	}
	
	public void setAddress(Address _address)
	{
		address=_address;
	}
}
