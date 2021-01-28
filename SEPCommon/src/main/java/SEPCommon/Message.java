package SEPCommon;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {

	private int id;
	private User sender;
	private String senderName;
	private User receiver;
	private String message;
	private LocalDateTime date;
	
	public Message(User _sender, User _receiver, String _message)
	{
		//wird beim Erstellen (clientseitig) aufgerufen
		sender=_sender;
		senderName=_sender.getAddress().getFullname() + " (ID " + _sender.getId() + ")";
		receiver=_receiver;
		message=_message;
	}
	
	public Message(int _id, User _sender, User _receiver, String _message, LocalDateTime _date)
	{
		id=_id;
		sender=_sender;
		senderName=_sender.getAddress().getFullname() + " (ID " + _sender.getId() + ")";
		receiver=_receiver;
		message=_message;
		date=_date;
	}
	
	public String getSenderName()
	{
		return senderName;
	}
	
	public void setDate(LocalDateTime _date)
	{
		date=_date;
	}
	
	public void setSender(User _sender)
	{
		sender=_sender;
	}
	
	public void setReceiver(User _receiver)
	{
		receiver=_receiver;
	}
	
	public void setMessage(String _message)
	{
		message=_message;
	}
	
	public int getId()
	{
		return id;
	}
	
	public LocalDateTime getDate()
	{
		return date;
	}
	
	public User getSender()
	{
		return sender;
	}
	
	public User getReceiver()
	{
		return receiver;
	}
	
	public String getMessage()
	{
		return message;
	}
}
