package SEPCommon;

public class Preferences {
	
	//Klasse zum Speichern von Nutzerdaten im Programm
	//QUELLE: https://www.vogella.com/tutorials/JavaPreferences/article.html
	//(Lars Vogel, 26.09.2016)
	private static java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userRoot()
			.node("SEPCommon.Preferences");

	public static void savePref(String prefName, String prefValue) {		//zu speichernde Strings, Name womit man das identifzieren kann (beliebige Wahl) wie Maps sozusagen
		prefs.put(prefName, prefValue);										
	}

	public static String getPref(String prefName) {			//gespeicherte Strings zu bekommen
		return prefs.get(prefName, "");
	}

	public static void removePref(String prefName) {		//EditAccountController, löscht die verbundene Map
		prefs.remove(prefName);
	}
}
