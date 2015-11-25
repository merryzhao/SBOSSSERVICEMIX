package com.ai.sboss.order.processor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("acquireCustomerOrderDetailLoopProcessor")
public class AcquireCustomerOrderDetailLoopProcessor implements Processor {
	
	private static Logger LOGGER = Logger.getLogger(AcquireCustomerOrderDetailLoopProcessor.class);

	@Override
	public void process(Exchange paramExchange) throws Exception {
		
		LOGGER.info("Handle loop call and get detailed order information ...");
		Message inMessage = paramExchange.getIn();
		
		// Extract request JSON object
		LOGGER.info("The index is null : " + null == paramExchange.getProperty("CamelSplitIndex") ? true : false);
		
		final int nIndexOfJSONArray = Integer.parseInt(paramExchange.getProperty("CamelSplitIndex").toString());
		LOGGER.info("The index is " + nIndexOfJSONArray);
		
		JSONArray orderList = JSONArray.fromObject(inMessage.getHeader("orderIdList"));
		JSONObject customerOrderId = (JSONObject)orderList.get(nIndexOfJSONArray);
		LOGGER.info("The customer order data : " + customerOrderId.toString());
		
		// Call next service to get order detailed information
		inMessage.setHeader(Exchange.HTTP_QUERY, customerOrderId);
	}
}
