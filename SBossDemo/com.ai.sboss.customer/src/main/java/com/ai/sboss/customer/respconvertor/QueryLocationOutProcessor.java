/**
 * 
 */
package com.ai.sboss.customer.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

/**
 * @author monica
 *
 */
public class QueryLocationOutProcessor implements IBasicOutProcessor{

	private Logger logger = Logger.getLogger(QueryLocationOutProcessor.class);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		Message inMessage = exchange.getIn();
		String ret = convert2requst(inMessage.getBody(String.class));
		inMessage.setBody(ret);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		// TODO Auto-generated method stub
		logger.info("data=====>" + data);
		JSONObject location = JSONObject.fromObject(data).getJSONObject("data");
		JSONObject result = new JSONObject();
		
		String area = location.getString("addressArea");
		String address = location.getString("addressDetail");
		String longitude = location.getJSONObject("geographicLocation").getJSONObject("property").
				getJSONObject("geometry").getJSONObject("point").getString("x");
		String latitude = location.getJSONObject("geographicLocation").getJSONObject("property").
				getJSONObject("geometry").getJSONObject("point").getString("y");
		
		//邮编暂时写死
		String postcode = "610054";
		result.put("area", area);
		result.put("address", address);
		result.put("longitude", longitude);
		result.put("latitude", latitude);
		result.put("postcode", postcode);
		
		return result.toString();
	}

}
