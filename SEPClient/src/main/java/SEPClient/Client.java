package SEPClient;

import java.io.IOException;
import java.net.Socket;

public class Client {
	private Socket socket;

	public void start()
	{
		//Socket-Verbindung zum Server herstellen
		try
		{
			socket = new Socket("localhost", 40001);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
