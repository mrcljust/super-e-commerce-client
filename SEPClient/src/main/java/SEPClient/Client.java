package SEPClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
	private Socket ClientSocket;
	private DataInputStream Din;
	private DataOutputStream Dout;
	

	public boolean start() {
		// Socket-Verbindung zum Server herstellen
		try {
			ClientSocket = new Socket("localhost", 40001);
			System.out.println("Verbindung zum Server hergestellt");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void stop() {
		try {
			if (Din != null)
				Din.close();

			if (Dout != null)
				Dout.close();

			if (ClientSocket != null)
				ClientSocket.close();
		} catch (IOException e) {
			// Beim schlieﬂen Problem aufgetreten
			e.printStackTrace();
		}

	}

}
