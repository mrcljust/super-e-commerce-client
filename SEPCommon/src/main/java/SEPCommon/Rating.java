package SEPCommon;

import java.io.Serializable;

public class Rating implements Serializable {

	private int id;
	private int stars;
	private String text;
	private int senderId;
	private int receiverId;
	private int orderId;

	public Rating(int _id, int _stars, String _text, int _senderId, int _receiverId, int _orderId) {

		this.id = _id;
		this.stars = _stars;
		this.text = _text;
		this.senderId = _senderId;
		this.receiverId = _receiverId;
		this.orderId = _orderId;
	}

	public Rating(int _stars, String _text, int _senderId, int _receiverId, int _orderId) {

		this.stars = _stars;
		this.text = _text;
		this.senderId = _senderId;
		this.receiverId = _receiverId;
		this.orderId = _orderId;
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

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
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
