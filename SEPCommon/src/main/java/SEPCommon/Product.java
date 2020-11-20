package SEPCommon;

public class Product {

	public int id;
	public String name;
	public double price;
	public String seller;
	public String category;
	public String description;
	 
	public Product (String _name, double _price, String _seller, String _category, String _description)
	{
		name=_name;
		price=_price;
		seller=_seller;
		category=_category;
		description=_description;
	}
	public Product (int _id, String _name, double _price, String _seller, String _category, String _description )
	{
		id=_id;
		name=_name;
		price=_price;
		seller=_seller;
		category=_category;
		description=_description;
	}
}
