package SEPServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private ServerSocket serverSocket;
	private Socket socket;
	private DataInputStream dis; // Daten empfangen
	private DataOutputStream dos; // Daten schicken
	public void start() {

		try {
			serverSocket = new ServerSocket(40001); // Server Socket erstellt
			System.out.println("Server ist bereit um Anfragen anzunehmen");

			Socket socket = serverSocket.accept(); // Client Socket
			System.out.println("Verbindung hergestellt");
			// hier kann die Verbindung genutzt werden

			serverSocket.close(); // Verbindung geschlossen

			socket.close();

		} catch (IOException e) {
			System.out.println("Fehler beim Initialisieren des Server-Sockets: " + e.getMessage());
		} finally {
			stop();
		}

	}

	public void stop() {
		try {
			if (serverSocket != null) {
				serverSocket.close();
				System.out.println("Server-Socket geschlossen");
			}
			if (socket != null) {
				socket.close();
				System.out.println("Socket geschlossen");
			}
		} catch (IOException e) {
			System.out.println("Fehler beim Schlieﬂen des Server-Sockets: " + e.getMessage());
		}
	}
	
	public void receiveRequest() {
		
	}

}
