package SEPCommon;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {

	private int id;
	private Product product;
	private Date date;
	private Rating sellerRating;
	private Rating buyerRating;

	public Order(int _id, Product _Product, Date _date, Rating _sellerRating, Rating _buyerRating) {

		this.id = _id;
		this.product= _Product;
		this.date = _date;
		this.sellerRating = _sellerRating;
		this.buyerRating = _buyerRating;
	}

	public int getId() {
		return id;

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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
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
