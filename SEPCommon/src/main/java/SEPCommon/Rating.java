package SEPCommon;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Rating implements Serializable {

	private int id;
	private int stars;
	private String text;
	private int senderId;
	private int receiverId;
	private int orderId;
	private int auctionId;
	private LocalDateTime date;

	public Rating(int _id, int _stars, String _text, int _senderId, int _receiverId, int _orderOrAuctionId, boolean isAuction, LocalDateTime _date) {

		//Konstruktor wenn Rating aus DB gelesen wird
		this.id = _id;
		this.stars = _stars;
		this.text = _text;
		this.senderId = _senderId;
		this.receiverId = _receiverId;
		this.date = _date;
		
		if(isAuction)
		{
			auctionId=_orderOrAuctionId;
		}
		else
		{
			orderId=_orderOrAuctionId;
		}
	}

	public Rating(int _stars, String _text, int _senderId, int _receiverId, int _orderOrAuctionId, boolean isAuction) {

		//Konstruktor wenn Rating erstellt wird clientseitig
		this.stars = _stars;
		this.text = _text;
		this.senderId = _senderId;
		this.receiverId = _receiverId;
		if(isAuction)
		{
			auctionId=_orderOrAuctionId;
		}
		else
		{
			orderId=_orderOrAuctionId;
		}
	}
	
	public LocalDateTime getDate()
	{
		return date;
	}
	
	public void setDate(LocalDateTime _date)
	{
		date=_date;
	}
	
	public int getOrderId()
	{
		return orderId;
	}
	
	public int getAuctionId()
	{
		return auctionId;
	}

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	public int getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(int receiverId) {
		this.receiverId = receiverId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	
	public void setAuctionId(int auctionId) {
		this.auctionId = auctionId;
	}

	public Rating(int _stars, String _text) {

		this.stars = _stars;
		this.text = _text;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
