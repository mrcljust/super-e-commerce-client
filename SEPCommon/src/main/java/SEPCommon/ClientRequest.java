
package SEPCommon;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ClientRequest implements Serializable {
	private Request requestType;
	private Map<String, Object> requestMap = new HashMap<String, Object>();

	public ClientRequest(Request res, HashMap<String, Object> reqMap)		//in Map String User, und Userobjekt bspw.
	{
		requestType=res;
		requestMap=reqMap;
		//test
	}
	
	public Request getRequestType()
	{
		return requestType;
	}
	
	public Map<String, Object> getRequestMap()
	{
		return requestMap;
	}
}
