package SEPClient;

import java.io.IOException;
import java.net.Socket;

public class Client {
	private Socket socket;

	public boolean start()
	{
		//Socket-Verbindung zum Server herstellen
		try
		{
			socket = new Socket("localhost", 40001);
			System.out.println("Verbindung zum Server hergestellt");
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
