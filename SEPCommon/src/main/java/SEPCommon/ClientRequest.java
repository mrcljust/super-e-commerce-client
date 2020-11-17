
package SEPCommon;

import java.io.Serializable;
import java.net.Authenticator.RequestorType;
import java.util.HashMap;
import java.util.Map;

public class ClientRequest {
	private Request requestType;
	private Map<String, Object> requestMap = new HashMap<String, Object>();

	public ClientRequest(Request res, HashMap<String, Object> reqMap)
	{
		requestType=res;
		requestMap=reqMap;
	}
}
