package SEPClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import SEPCommon.ClientRequest;
import SEPCommon.Response;
import SEPCommon.ServerResponse;

public class Client {
	private Socket clientSocket;
	private ObjectInputStream dis; // Daten empfangen
	private ObjectOutputStream dos; // Daten schicken
	public boolean startSuccess = false;
	private static Client client; 
	public boolean isStarted = false;

	public static Client getClient()
	{
		return client;
	}
	
	public void start() {
		// Socket-Verbindung zum Server herstellen
		client=this;
		try {
			// Client Socket erstellen
			clientSocket = new Socket(SEPCommon.Constants.SERVERIP, SEPCommon.Constants.PORT);
			clientSocket.setSoTimeout(10000);
			System.out.println("Verbindung zum Server hergestellt.");
			
            dos = new ObjectOutputStream(clientSocket.getOutputStream());
            dis = new ObjectInputStream(clientSocket.getInputStream());
            
            isStarted=true;
            
		} catch (IOException e) {
			startSuccess=false;
			e.printStackTrace();
			isStarted=false;
		}
	}
	
	//@SuppressWarnings("unused")
	@SuppressWarnings("unused")
	public ServerResponse sendClientRequest(ClientRequest req)
	{
		ServerResponse serverResponse = null;
		try {
			System.out.println("Sende ClientRequest - " + req.getRequestType() + " - " + req.getRequestMap());
			
			//Anfrage senden
			dos.writeObject(req);
			
			//Antwort auslesen
			serverResponse = (ServerResponse)dis.readObject();
			System.out.println("ServerResponse - " + serverResponse.getResponseType() + " - " + serverResponse.getResponseMap());
			
			if(serverResponse==null)
			{
				return new ServerResponse(Response.Failure, null);
			}
			else
			{
				ServerResponse serverRes = (ServerResponse)serverResponse;
				return serverRes;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new ServerResponse(Response.Failure, null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return new ServerResponse(Response.Failure, null);
		}
	}

	public void stop() {
		try {
			if (dis != null)
				dis.close();

			if (dos != null)
				dos.close();

			if (clientSocket != null)
				clientSocket.close();
		} catch (IOException e) {
			// Beim schlieﬂen Problem aufgetreten
			e.printStackTrace();
		}

	}

}
