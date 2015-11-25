/**
 * 
 */
package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

/**
 * @author monica
 *
 */
public class ServiceDetailCommentOutProcessor implements IBasicOutProcessor{

	protected Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		String data = exchange.getIn().getBody(String.class);
		String returnJson = convert2requst(data);
		exchange.getIn().setBody(returnJson);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		// TODO Auto-generated method stub
		logger.info("serviceDetailCommentOutProcessor_data====>" + data);
		JSONObject tempData = new JSONObject();
		Double service_rating = 0.0;
		JSONObject resultJsonObj = JSONObject.fromObject(data);
		if(resultJsonObj.containsKey("service_rating")){
			String service_rating_temp = resultJsonObj.getString("service_rating");
			tempData = resultJsonObj.getJSONObject("data");
			service_rating = Double.parseDouble(service_rating_temp);
		}
		tempData.put("service_rating", service_rating);
		resultJsonObj.remove("service_rating");
		return resultJsonObj.toString();		
	}

}
