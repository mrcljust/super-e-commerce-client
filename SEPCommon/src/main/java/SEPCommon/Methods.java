package SEPCommon;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Methods {

	//Methode zum Runden von Zahlen auf eine beliebige Anzahl an Nachkommastellen
	//QUELLE: https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
	//(Antwort von May 11 '10 at 7:01, User Jonik)
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
}
