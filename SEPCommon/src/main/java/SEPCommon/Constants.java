package SEPCommon;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Constants {
	
	//Konstante Adressen, Variablen, die an mehreren Klassen verwendet werden

	public final static String SERVERIP = "localhost";
	public final static int PORT = 40001;
	
	public final static String SQLSERVERIP = "localhost";
	public final static int SQLPORT = 3306;
	//SQL Connection String mit Zeitzone um Zeitzonenfehler zu umgehen
	public final static String SQLCONNECTIONSTRING = "jdbc:mysql://localhost:3306/sep?serverTimezone=Europe/Berlin";
	public final static String SQLUSER = "root";
	public final static int TIMEOUT= 30000; //30sek
	
	public final static String CLIENT_LOGO_RESOURCE_PATH = "/SEPClient/UI/sep-logo.png";
	
	public final static DecimalFormat DOUBLEFORMAT = new DecimalFormat("#0.00");
	
	static ZoneId CET = ZoneId.of("CET");

	public final static DateTimeFormatter DATEFORMAT = DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm").withZone(CET);
	public final static DateTimeFormatter DATEFORMATDAYONLY = DateTimeFormatter.ofPattern("dd.MM.YYYY").withZone(CET);
	
	public final static String CURRENCY = "$";
}
