package SEPCommon;

public class Address {

	public String country;
	public int zipcode;
	public String city;
	public String street;
	public String number;

	public Address(String _country, int _zipcode, String _city, String _street, String _number)
	{
		country=_country;
		zipcode=_zipcode;
		city=_city;
		street=_street;
		number=_number;
	}
}
