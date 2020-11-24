package SEPCommon;

import java.io.Serializable;

public class Product implements Serializable {

	private int id;
	private String name;
	private double price;
	private Seller seller;
	private String businessname; //wird benötigt, um den Verkäufer in den Katalog-TableViews im MainScreen anzuzeigen
	private String category;
	private String description;
	
	
	//Konstruktor mit ID
	public Product (int _id, String _name, double _price, Seller _seller, String _category, String _description )
	{
		id=_id;
		name=_name;
		price=_price;
		seller=_seller;
		category=_category;
		description=_description;
		businessname = _seller.getBusinessname();
	}

	//Konstruktor ohne ID
	public Product (String _name, double _price, Seller _seller, String _category, String _description)
	{
		name=_name;
		price=_price;
		seller=_seller;
		category=_category;
		description=_description;
		businessname = _seller.getBusinessname();
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
	
	public Seller getSeller()
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
	
	public String getBusinessname()
	{
		return businessname;
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
	
	public void setSeller(Seller _seller)
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
