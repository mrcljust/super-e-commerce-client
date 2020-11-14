package SEPClient;

public class Main {
	private static Client client;
	
	public static void main(String[] args) {
		UIHandler.main(args);
		client = new Client();
		client.start();
		
	}
}
