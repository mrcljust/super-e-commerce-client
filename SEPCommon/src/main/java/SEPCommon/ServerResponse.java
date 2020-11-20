package SEPCommon;

import java.util.HashMap;
import java.util.Map;

public class ServerResponse {

	private Response responseType;
	HashMap<String, Object> responseMap = new HashMap<String, Object>();

	public ServerResponse(Response res, HashMap<String,Object> resMap)
	{
		responseType=res;
		responseMap=resMap;
	}
	
	public Response getResponseType()
	{
		return responseType;
	}
	
	public Map<String, Object> getResponseMap()
	{
		return responseMap;
	}
}
