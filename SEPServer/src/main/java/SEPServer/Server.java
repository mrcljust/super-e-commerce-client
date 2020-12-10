package SEPServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
	private ServerSocket listener;
	private static ArrayList<ServerThread> clients = new ArrayList<>();
	//ExecutorService zum Ausf¸hren der Client-Threads. 999 maximale Threads gleichzeitig.
	private static ExecutorService pool = Executors.newFixedThreadPool(999);
	
	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}
	
	public void start() {

		int clientid=1;
		try
		{
			//ServerSocket erstellen
			listener = new ServerSocket(SEPCommon.Constants.PORT);
			System.out.println("Der Server ist bereit, Client-Verbindungen anzunehmen.");
			
			//Endlosschleife (unbegrenzte Menge an Client-Connections annehmen)
			while(true)
			{
				Socket clientSocket = listener.accept();
				ServerThread clientThread = new ServerThread(clientSocket, clientid);
				clientid++;
				clients.add(clientThread);
				
				pool.execute(clientThread);
			}
		} 
		catch (IOException e)
		{
			System.out.println("Fehler beim Initialisieren des Server-Sockets: " + e.getLocalizedMessage() + " (L‰uft der Server bereits?)");
		}
		finally
		{
			stop();
		}

	}

	public void stop() {
		try {
			if (listener != null) {
				listener.close();
				System.out.println("Server-Socket geschlossen");
			}
			
			pool.shutdown();
			
			//10 Sekunden warten, ob alle Threads beendet wurden, ansonsten Shutdown erzwingen
			if (pool.awaitTermination(10, TimeUnit.SECONDS))
			{
				  System.out.println("Alle Client-Sockets geschlossen");
			}
			else
			{
				  System.out.println("Schlieﬂen aller Client-Sockets erzwingen...");
				  pool.shutdownNow();
			}
		}
		catch (IOException e)
		{
			System.out.println("Fehler beim Schlieﬂen des Server-Sockets: " + e.getLocalizedMessage());
		}
		catch (InterruptedException e)
		{
			System.out.println("Fehler beim Schlieﬂen der Client-Sockets: " + e.getLocalizedMessage());
		}
	}
}
