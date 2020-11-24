package SEPCommon;

import java.text.DecimalFormat;

public class Constants {
	
	//Konstante Adressen, Variablen, die an mehreren Klassen verwendet werden

	public final static String SERVERIP = "localhost";
	public final static int PORT = 40001;
	
	public final static String SQLSERVERIP = "localhost";
	public final static int SQLPORT = 3306;
	//SQL Connection String mit Zeitzone um Zeitzonenfehler zu umgehen
	public final static String SQLCONNECTIONSTRING = "jdbc:mysql://localhost:3306/sep?serverTimezone=Europe/Berlin";
	public final static String SQLUSER = "root";
	public final static int TIMEOUT= 15000;
	
	public final static String CLIENT_LOGO_RESOURCE_PATH = "/SEPClient/UI/sep-logo.png";
}
