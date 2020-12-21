package SEPCommon;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Auction implements Serializable {

	private int id;
	private String title;
	private String description;
	private byte[] image;
	private double minBid;
	private double startPrice;
	private double currentBid;
	private ShippingType shippingType;
	private Customer seller;
	private Customer currentBidder;
	private Rating sellerRating;
	private Rating buyerRating;
	private LocalDateTime starttime;
	private LocalDateTime enddate;
	private boolean isEnded;
	
	
	public Rating getSellerRating() {
		return sellerRating;
	}

	public void setSellerRating(Rating _sellerRating) {
		this.sellerRating = _sellerRating;
	}

	public Rating getBuyerRating() {
		return buyerRating;
	}

	public void setBuyerRating(Rating _buyerRating) {
		this.buyerRating = _buyerRating;
	}

	public LocalDateTime getStarttime() {
		return starttime;
	}

	public void setStarttime(LocalDateTime _starttime) {
		this.starttime = _starttime;
	}

	public LocalDateTime getEnddate() {
		return enddate;
	}

	public void setEnddate(LocalDateTime _enddate) {
		this.enddate = _enddate;
	}

	public boolean isEnded() {
		return isEnded;
	}

	public void setEnded(boolean _isEnded) {
		this.isEnded = _isEnded;
	}
	
	public void setStartPrice(double _startprice) {
		this.startPrice = _startprice;
	}
	
	public double getStartPrice()
	{
		return startPrice;
	}



	public Auction(int _id, String _title, String _description, byte[] _image, double _minBid, double _startprice, ShippingType _shippingType,
			Customer _seller, Customer _currentBidder, Double _currentBid, LocalDateTime _starttime, LocalDateTime _enddate) {
		//wird aufgerufen, wenn laufende Auktion aus DB geholt wird
		this.id = _id;
		this.title = _title;
		this.description = _description;
		this.image = _image;
		this.minBid = _minBid;
		this.startPrice = _startprice;
		this.shippingType = _shippingType;
		this.seller = _seller;
		this.isEnded = false;
		this.starttime = _starttime;
		this.enddate = _enddate;
		this.currentBid = _currentBid;
		this.currentBidder = _currentBidder;
	}

	public Auction(int _id, String _title, String _description, byte[] _image, double _minBid, double _startprice, double _currentBid,
			ShippingType _shippingType, Customer _seller, Customer _currentBidder, Rating _sellerRating, Rating _buyerRating,
			LocalDateTime _starttime, LocalDateTime _enddate) {
		//wird bei beendeten Auktionen in der DB aufgerufen.
		this.id = _id;
		this.title = _title;
		this.description = _description;
		this.image = _image;
		this.minBid = _minBid;
		this.currentBid = _currentBid;
		shippingType = _shippingType;
		this.seller = _seller;
		this.currentBidder = _currentBidder;
		this.sellerRating = _sellerRating;
		this.buyerRating = _buyerRating;
		this.starttime = _starttime;
		this.enddate = _enddate;
		this.isEnded=true;
	}

	public Auction(String _title, String _description, byte[] _image, double _minBid, double _startprice, ShippingType _shippingType,	
			Customer _seller, LocalDateTime _starttime, LocalDateTime _enddate) {
		//wird aufgerufen, wenn neue Auktion angelegt wird (noch keine ID vergeben)
		this.title = _title;
		this.description = _description;
		this.image = _image;
		this.minBid = _minBid;
		shippingType = _shippingType;
		this.seller = _seller;
		this.starttime = _starttime;
		this.enddate = _enddate;
		this.isEnded=false;
	}

	public double getCurrentBid() {
		return currentBid;
	}

	public void setCurrentBid(double _currentBid) {
		this.currentBid = _currentBid;
	}

	public Customer getCurrentBidder() {
		return currentBidder;
	}

	public void setCurrentBidder(Customer _currentBidder) {
		this.currentBidder = _currentBidder;
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

	public void setTitle(String _title) {
		this.title = _title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String _description) {
		this.description = _description;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] _image) {
		this.image = _image;
	}

	public double getMinBid() {
		return minBid;
	}

	public void setMinBid(double _minBid) {
		this.minBid = _minBid;
	}

	public ShippingType getShippingType() {
		return shippingType;
	}

	public void setShippingType(ShippingType _shippingType) {
		this.shippingType = _shippingType;
	}

	public Customer getSeller() {
		return seller;
	}

	public void setSeller(Customer _seller) {
		this.seller = _seller;
	}

}
