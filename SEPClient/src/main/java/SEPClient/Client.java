package SEPClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
	private Socket ClientSocket;
	private DataInputStream Din; // Daten empfangen
	private DataOutputStream Dos; // Daten schicken

	public boolean start() {
		// Socket-Verbindung zum Server herstellen
		try {
			// Client Socket erstellen
			ClientSocket = new Socket("localhost", 40001);
			System.out.println("Verbindung zum Server hergestellt");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void SendRequestToServer() {

	}

	public void stop() {
		try {
			if (Din != null)
				Din.close();

			if (Dos != null)
				Dos.close();

			if (ClientSocket != null)
				ClientSocket.close();
		} catch (IOException e) {
			// Beim schlieﬂen Problem aufgetreten
			e.printStackTrace();
		}

	}

}
