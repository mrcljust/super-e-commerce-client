package SEPCommon;

import java.io.Serializable;
import java.util.Date;

public class Auction implements Serializable {

	private int id;
	private String title;
	private String description;
	private byte[] image;
	private double minBid;
	private double maxBid;
	private String shippingType;
	private Customer seller;
	private Customer maxBidder;
	private Rating sellerRating;
	private Rating buyerRating;
	private Date date;
	private boolean isEnded;

	public Auction(int _id, String _title, String _description, byte[] _image, double _minBid, String _shippingType,			//laufende Auktion aus DB holen	
			Customer _seller) {
		this.id = _id;
		this.title = _title;
		this.description = _description;
		this.image = _image;
		this.minBid = _minBid;
		this.shippingType = _shippingType;
		this.seller = _seller;
		this.isEnded=false;

	}

	public Auction(int _id, String _title, String _description, byte[] _image, double _minBid, double _maxBid,					//Dieser Konstruktor für gespeichert in DB
			String _shippingType, Customer _seller, Customer _maxBidder, Rating _sellerRating, Rating _buyerRating,
			Date _date) {
		this.id = _id;
		this.title = _title;
		this.description = _description;
		this.image = _image;
		this.minBid = _minBid;
		this.maxBid = _maxBid;
		shippingType = _shippingType;
		this.seller = _seller;
		this.maxBidder = _maxBidder;
		this.sellerRating = _sellerRating;
		this.buyerRating = _buyerRating;
		this.date = _date;
		this.isEnded=true;
	}

	public Auction(String _title, String _description, byte[] _image, double _minBid, String _shippingType,			//neue Auktion anlegen
			Customer _seller) {
		this.title = _title;
		this.description = _description;
		this.image = _image;
		this.minBid = _minBid;
		shippingType = _shippingType;
		this.seller = _seller;
		this.isEnded=false;
	}

	public Response SendBid(double bid, Customer bidder) {
		return null;
	}

	public double getMaxBid() {
		return maxBid;
	}

	public void setMaxBid(double maxBid) {
		this.maxBid = maxBid;
	}

	public Customer getMaxBidder() {
		return maxBidder;
	}

	public void setMaxBidder(Customer maxBidder) {
		this.maxBidder = maxBidder;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public double getMinBid() {
		return minBid;
	}

	public void setMinBid(double minBid) {
		this.minBid = minBid;
	}

	public String getShippingType() {
		return shippingType;
	}

	public void setShippingType(String shippingType) {
		shippingType = shippingType;
	}

	public Customer getSeller() {
		return seller;
	}

	public void setSeller(Customer seller) {
		this.seller = seller;
	}

}
