package SEPClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
	private Socket clientSocket;
	private DataInputStream dis; // Daten empfangen
	private DataOutputStream dos; // Daten schicken
	public boolean isStarted = false;
	
	public boolean start() {
		// Socket-Verbindung zum Server herstellen
		try {
			// Client Socket erstellen
			clientSocket = new Socket(SEPCommon.Constants.SERVERIP, SEPCommon.Constants.PORT);
			System.out.println("Verbindung zum Server hergestellt");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void sendClientRequest() {

	}

	public void handleServerRequest() {

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
