/**
 * 
 */
package com.ai.sboss.customer.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

/**
 * @author monica
 *
 */
public class CustomerInfoOutProcessor implements IBasicOutProcessor{

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
		JSONObject custInfoJsonObj = JSONObject.fromObject(data);
		
		
		custInfoJsonObj.getJSONObject("data").put("user_address", custInfoJsonObj.getString("user_address"));
		custInfoJsonObj.remove("user_address");
		//清除返回的json中的无效字段
		if(custInfoJsonObj.getJSONObject("data").containsKey("contact_address")){
			custInfoJsonObj.getJSONObject("data").remove("contact_address");
		}
		return custInfoJsonObj.toString();
	}

}
