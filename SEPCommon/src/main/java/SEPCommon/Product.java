package SEPCommon;

import java.io.Serializable;

public class Product implements Serializable {

	private int id;
	private String name;
	private double price;
	private double oldprice;
	private String priceString;
	private String oldpriceString;
	private Seller seller;
	private String businessname; //wird benötigt, um den Verkäufer in den Katalog-TableViews im MainScreen anzuzeigen
	private String category;
	private String description;
	private Double distance;
	
	
	//Konstruktor mit ID (bestehende Produkte, die aus der DB gelesen werden)
	public Product (int _id, String _name, double _price, Double _oldprice, Seller _seller, String _category, String _description )
	{
		id=_id;
		name=_name;
		price=_price;
		oldprice=_oldprice;
		priceString=Constants.DOUBLEFORMAT.format(_price) + Constants.CURRENCY;
		oldpriceString=Constants.DOUBLEFORMAT.format(_oldprice) + Constants.CURRENCY;
		seller=_seller;
		category=_category;
		description=_description;
		businessname = _seller.getBusinessname();
		distance=-1.0;
	}

	//Konstruktor ohne ID (neues Produkt)
	public Product (String _name, double _price, Seller _seller, String _category, String _description)
	{
		name=_name;
		price=_price;
		oldprice=_price;
		priceString=String.valueOf(_price) + "$";
		oldpriceString=String.valueOf(_price) + "$";
		seller=_seller;
		category=_category;
		description=_description;
		businessname = _seller.getBusinessname();
		distance=-1.0;
	}
	
	//Getter Methoden
	
	public double getDistance()
	{
		return distance;
	}
	
	public double getOldPrice()
	{
		return oldprice;
	}
	
	public void setOldPrice(double _oldprice)
	{
		oldprice=_oldprice;
	}
	
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
	
	public String getPriceString()
	{
		return priceString;
	}
	
	public String getOldPriceString()
	{
		return oldpriceString;
	}
	
	//Setter Methoden
	
	public void setDistance(double _distance)
	{
		distance=_distance;
	}
	
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
