package SEPServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private ServerSocket serverSocket;
	private Socket socket;
	

	public void start()
	{
		try
		{
			serverSocket = new ServerSocket(40001);
			System.out.println("Server ist bereit um Anfragen anzunehmen");
			socket = serverSocket.accept();
			
			serverSocket.close();
			//hier kann die Verbindung genutzt werden
			
			socket.close();
			
		}
		catch(IOException e)
		{
			System.out.println("Fehler beim Initialisieren des Server-Sockets: "+ e.getMessage());
		}
		finally
		{
			stop();
		}
	}
	
	public void stop()
	{
		try
		{
			if(serverSocket!=null)
			{
				serverSocket.close();
				System.out.println("Server-Socket geschlossen");
			}
			if(socket!=null)
			{
				socket.close();
				System.out.println("Socket geschlossen");
			}
		}
		catch(IOException e)
		{
			System.out.println("Fehler beim Schlieﬂen des Server-Sockets: " + e.getMessage());
		}
	}

}
