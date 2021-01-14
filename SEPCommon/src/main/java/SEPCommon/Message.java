package SEPCommon;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {

	private int id;
	private User sender;
	private User receiver;
	private String message;
	private LocalDateTime date;
	
	public Message(User _sender, User _receiver, String _message)
	{
		//wird beim Erstellen (clientseitig) aufgerufen
		sender=_sender;
		receiver=_receiver;
		message=_message;
	}
	
	public Message(int _id, User _sender, User _receiver, String _message, LocalDateTime _date)
	{
		id=_id;
		sender=_sender;
		receiver=_receiver;
		message=_message;
		date=_date;
	}
}
