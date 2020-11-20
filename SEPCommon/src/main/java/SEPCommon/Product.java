package SEPCommon;

public class Product {

	private int id;
	private String name;
	private double price;
	private String seller;
	private String category;
	private String description;
	
	
	//Konstruktor mit ID
	public Product (int _id, String _name, double _price, String _seller, String _category, String _description )
	{
		id=_id;
		name=_name;
		price=_price;
		seller=_seller;
		category=_category;
		description=_description;
	}

	//Konstruktor ohne ID
	public Product (String _name, double _price, String _seller, String _category, String _description)
	{
		name=_name;
		price=_price;
		seller=_seller;
		category=_category;
		description=_description;
	}
	
	//Getter Methoden
	
	public int getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public double getPrice()
	{
		return price;
	}
	
	public String getSeller()
	{
		return seller;
	}
	
	public String getCategory()
	{
		return category;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	//Setter Methoden
	
	public void setId(int _id)
	{
		id=_id;
	}
	
	public void setName(String _name)
	{
		name=_name;
	}
	
	public void setprice(double _price)
	{
		price=_price;
	}
	
	public void setSeller(String _seller)
	{
		seller=_seller;
	}
	
	public void setCategory(String _category)
	{
		category=_category;
	}
	
	public void setDescription(String _description)
	{
		description=_description;
	}
}
