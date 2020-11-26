package SEPServer;

public class Main {
	private static Server server;

	public static void main(String[] args) {
		server = new Server();
		server.start();
	}
}