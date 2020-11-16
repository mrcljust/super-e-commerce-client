package SEPCommon;

public class Product {

	private int id;
	private String name;
	private double price;
	private String seller;
	private String category;
	private String description;
	 
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
