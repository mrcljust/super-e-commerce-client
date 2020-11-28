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

	private static Client client; // von anderen Klassen auf Client zuzugreifen
	public boolean isStarted = false;

	public static Client getClient() // Methode in Controlle aufgerufen, um ueber Client zu schicken
	{ // kommt an Client Objekt um Request zu schicken
		return client;
	}

	public void start() {
		// Socket-Verbindung zum Server herstellen
		client=this;
		try {
			// Client Socket erstellen
			clientSocket = new Socket(SEPCommon.Constants.SERVERIP, SEPCommon.Constants.PORT); //alle Daten die Server und Client zugriff drauf haben OOP
			clientSocket.setSoTimeout(SEPCommon.Constants.TIMEOUT); 		//Timeout damit bei langer Verbindungszeit exception geworfen wird
			System.out.println("Verbindung zum Server hergestellt.");
		
            dos = new ObjectOutputStream(clientSocket.getOutputStream());		
            dis = new ObjectInputStream(clientSocket.getInputStream());			
            isStarted=true; //Client gestartet
            
		} catch (IOException e) {
	
			e.printStackTrace();
			isStarted=false;
		}
	}
	

	public ServerResponse sendClientRequest(ClientRequest req)		//req übergeben von Controllern bspw. RegisterController Zeile 197
	{
		ServerResponse serverResponse = null;						//vor jedem Aufruf Initialisierung ServerResponse null
		try {
			System.out.println("Sende ClientRequest - " + req.getRequestType() + " - " + req.getRequestMap()); //geschweifte Klammern (Map), getRequestType() Enum zurück
			
			//Anfrage senden
			dos.writeObject(req);
			
			//Antwort auslesen
			serverResponse = (ServerResponse)dis.readObject(); //Casten damit serverresponse in ServerResponse gespeichert wird, ohne Cast einfach normales Objekt
			System.out.println("ServerResponse - " + serverResponse.getResponseType() + " - " + serverResponse.getResponseMap());

			return serverResponse;

		} catch (IOException e) {
			e.printStackTrace();
			return new ServerResponse(Response.Failure, null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return new ServerResponse(Response.Failure, null);
		}
	}

	/*public void stop() {
		try {
			if (dis != null)
				dis.close();

			if (dos != null)
				dos.close();

			if (clientSocket != null)
				clientSocket.close();
		} catch (IOException e) {
			// Beim schliessen Problem aufgetreten
			e.printStackTrace();
		}

	}
*/
}

