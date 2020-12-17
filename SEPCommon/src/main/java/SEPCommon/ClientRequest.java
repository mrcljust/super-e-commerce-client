
package SEPCommon;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ClientRequest implements Serializable { //Serializable damit man von objectstream zu normalen object casten
	private Request requestType;					// genauer gesagt: bspw Objekt, verschickt das über einen Stream, und dann wieder zu Objekt
	private Map<String, Object> requestMap = new HashMap<String, Object>(); 	//private damit  man nicht Klassennutzer attribute direkt verändern können, dafür gibt es setter/getter Methoden

	public ClientRequest(Request res, HashMap<String, Object> reqMap)		//in Map String User, und Userobjekt bspw.
	{																		//Konstruktoren, um neues Objekt zu initialisierne (Clientrequest)
		requestType=res;													//test
		requestMap=reqMap;
		
	}
	
	public Request getRequestType()
	{
		return requestType;
	}
	
	public Map<String, Object> getRequestMap()				//durch getter Methoden können anderen Klassen darauf zugreifen, somit auch auf die wERTE
	{
		return requestMap;
	}
}
