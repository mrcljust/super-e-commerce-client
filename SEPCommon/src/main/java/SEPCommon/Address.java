package SEPCommon;

public class Address {

	private String country;
	private int zipcode;
	private String city;
	private String street;
	private String number; //String wegen Buchstabenzusätzen (z.B. Nr 249a)

	public Address(String _country, int _zipcode, String _city, String _street, String _number)
	{
		//Erstellt ein Address-Objekt mit den gegebenen Werten
		country=_country;
		zipcode=_zipcode;
		city=_city;
		street=_street;
		number=_number;
	}
	
	//Getter Methoden
	
	public String getCountry()
	{
		return country;
	}
	
	public int getZipcode()
	{
		return zipcode;
	}
	
	public String getCity()
	{
		return city;
	}
	
	public String getStreet()
	{
		return street;
	}
	
	public String getNumber()
	{
		return number;
	}
	
	//Setter Methoden

	public void setCountry(String _country)
	{
		country=_country;
	}
	
	public void setZipcode(int _zipcode)
	{
		zipcode=_zipcode;
	}
	
	public void setCity(String _city)
	{
		city=_city;
	}
	
	public void setStreet(String _street)
	{
		street=_street;
	}
	
	public void setNumber(String _number)
	{
		number=_number;
	}
}
