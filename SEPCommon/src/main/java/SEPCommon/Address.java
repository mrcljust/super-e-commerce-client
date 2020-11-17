package SEPCommon;

public class Address {

	private String country;
	private int zipcode;
	private String city;
	private String street;
	private String number;

	public Address(String _country, int _zipcode, String _city, String _street, String _number)
	{
		country=_country;
		zipcode=_zipcode;
		city=_city;
		street=_street;
		number=_number;
	}
}
