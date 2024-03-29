package SEPCommon;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Order implements Serializable {

	private int id;
	private Product product;
	private String productName;
	private Double productPrice;
	private LocalDateTime date;
	private Rating sellerRating;
	private Rating buyerRating;
	private Seller seller;
	private Customer buyer;

	public Order(int _id, Product _Product, LocalDateTime _date, Rating _sellerRating, Rating _buyerRating, Seller _seller, Customer _customer) {

		this.id = _id;
		this.product= _Product;
		productName = _Product.getName();
		productPrice = _Product.getPrice();
		this.date = _date;
		this.sellerRating = _sellerRating;
		this.buyerRating = _buyerRating;
		this.seller = _seller;
		this.buyer = _customer;
	}
	
	public Seller getSeller()
	{
		return seller;
	}
	
	public Customer getBuyer()
	{
		return buyer;
	}

	public int getId() {
		return id;

	}
	
	public double getProductPrice()
	{
		return productPrice;
	}
	
	public String getProductName()
	{
		return productName;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public Rating getSellerRating() {
		return sellerRating;
	}

	public void setSellerRating(Rating sellerRating) {
		this.sellerRating = sellerRating;
	}

	public Rating getBuyerRating() {
		return buyerRating;
	}

	public void setBuyerRating(Rating buyerRating) {
		this.buyerRating = buyerRating;
	}

}
