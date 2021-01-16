package SEPCommon;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

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
	
	//Methode zum Erstellen eines MD5-Hashcodes aus einem String
	//QUELLE: https://www.geeksforgeeks.org/md5-hash-in-java/
	//(08.05.2020, bilal-hungund)
	public static String getMd5Encryption(String input) 
    { 
        try { 
  
            // Static getInstance method is called with hashing MD5 
            MessageDigest md = MessageDigest.getInstance("MD5"); 
  
            // digest() method is called to calculate message digest 
            //  of an input digest() return array of byte 
            byte[] messageDigest = md.digest(input.getBytes()); 
  
            // Convert byte array into signum representation 
            BigInteger no = new BigInteger(1, messageDigest); 
  
            // Convert message digest into hex value 
            String hashtext = no.toString(16); 
            while (hashtext.length() < 32) { 
                hashtext = "0" + hashtext; 
            } 
            return hashtext; 
        }  
  
        // For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) { 
            throw new RuntimeException(e); 
        } 
    }
	
	//Methode zum Umwandeln eines java.util.Date zu java.time.LocalDate
	//QUELLE: https://www.baeldung.com/java-date-to-localdate-and-localdatetime
	//(September 25, 2020, baeldung) - abgeändert
	public static LocalDate convertToLocalDate(Date dateToConvert) {
		Instant instant = dateToConvert.toInstant();
		ZonedDateTime zdt = instant.atZone(ZoneId.of("CET"));
		LocalDate localDate = zdt.toLocalDate();
	    return localDate;
	}
	
	//Methode zum Umwandeln eines java.util.Date zu java.time.LocalTime
	//QUELLE: https://www.baeldung.com/java-date-to-localdate-and-localdatetime
	//(September 25, 2020, baeldung) - abgeändert
	public static LocalTime convertToLocalTime(Date dateToConvert) {
		Instant instant = dateToConvert.toInstant();
		ZonedDateTime zdt = instant.atZone(ZoneId.of("CET"));
		LocalTime localTime = zdt.toLocalTime();
	    return localTime;
	}
	
	public static ZonedDateTime convertLocalDateTimeToCET(LocalDateTime localDateTimeToConvert)
	{
		ZoneId zone = ZoneId.of("CET");
		return localDateTimeToConvert.atZone(zone);
	}
	
	//Methode zum Berechnen der Distanz in Meter zwischen zwei LatLong
	//QUELLE: GmapsFX API (com.dlsc.gmapsfx.javascript.object.LatLong.distanceFrom)
	//umgeschrieben um mit der Google-eigenen API zu arbeiten (hat augenscheinlich keine Methode zum Distanz berechnen)
	public static double getDistance(double lat1, double lng1, double lat2, double lng2)
	{
		double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLon = (lng2 - lng1) * Math.PI / 180;
        final double EarthRadiusMeters = 6378137.0; // meters
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1 * Math.PI / 180)
                * Math.cos(lat2 * Math.PI / 180)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = EarthRadiusMeters * c;
        return d;
	}
}
