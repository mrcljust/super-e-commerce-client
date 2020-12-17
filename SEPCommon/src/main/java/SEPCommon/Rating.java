package SEPCommon;

import java.io.Serializable;

public class Rating implements Serializable {

	private int id;
	private int stars;
	private String text;
	private int senderId;
	private int receiverId;
	private int orderId;
	private int auctionId;

	public Rating(int _id, int _stars, String _text, int _senderId, int _receiverId, int _orderOrAuctionId, boolean isAuction) {

		this.id = _id;
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

	public Rating(int _stars, String _text, int _senderId, int _receiverId, int _orderOrAuctionId, boolean isAuction) {

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
