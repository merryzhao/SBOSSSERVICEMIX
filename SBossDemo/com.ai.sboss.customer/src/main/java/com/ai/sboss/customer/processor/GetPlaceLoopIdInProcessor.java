/**
 * 
 */
package com.ai.sboss.customer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

/**
 * @author monica
 *
 */
public class GetPlaceLoopIdInProcessor implements Processor{

	Logger logger = Logger.getLogger(GetPlaceLoopIdInProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		logger.info("index is"+ exchange.getProperty("CamelSplitIndex"));
		JSONObject address = new JSONObject();
		JSONObject Place = JSONObject.fromObject(exchange.getIn().getBody(String.class));
		String placeId = Place.getString("placeId");
		address.put("placeId",placeId);
		exchange.getIn().setBody(address.toString());
	}

}
