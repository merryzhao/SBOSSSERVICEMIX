/**
 * 
 */
package com.ai.sboss.customer.processor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

/**
 * @author monica
 *
 */
public class customerInfoConstructInProcessor implements Processor{

	Logger logger = Logger.getLogger(customerInfoConstructInProcessor.class);
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":1,\"result_msg\":\"\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		JSONArray locationList = JSONArray.fromObject("["+exchange.getIn().getBody().toString()+"]");
		String pageNum = Integer.toString(locationList.size());
		JSONObject data = new JSONObject();
		
		data.put("page_num", pageNum);
		data.put("location_list", locationList.toString());
		String returnJson = RETFMT.replace("<DATA>", data.toString());
		exchange.getIn().setBody(returnJson);
	}

}
