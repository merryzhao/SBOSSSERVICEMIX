package com.ai.sboss.order.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("acquireCustomerOrderInventoryProcessor")
public class AcquireCustomerOrderInventoryProcessor implements Processor {

	private static Logger LOGGER = Logger
			.getLogger(AcquireCustomerOrderInventoryProcessor.class);
	private static final String FORMAT_ACQUIRE = "servicecode=acquireCustomerOrderInventory&WEB_HUB_PARAMS={\"data\":{\"customerOrderId\":\"<order_id>\"},\"header\":{\"Content-Type\":\"application/json\"}}";

	@Override
	public void process(Exchange paramExchange) throws Exception {
		LOGGER.info("Call CRM's acquireCustomerOrderInventory service ...");
		Message inMessage = paramExchange.getIn();

		LOGGER.info("Inputted order JSON data : "
				+ inMessage.getHeader(Exchange.HTTP_QUERY).toString());

		// Construct URL for acquireCustomerOrderInventory service
		JSONObject param = JSONObject.fromObject(inMessage.getHeader(
				Exchange.HTTP_QUERY).toString());
		String query = FORMAT_ACQUIRE.replace("<order_id>",
				param.getString("order_id"));
		
		LOGGER.info("AcquireCustomerOrderInventory URL's is " + query);

		// Call acquireCustomerOrderInventory service and extract order detailed
		// information
		inMessage.setHeader(Exchange.HTTP_QUERY, query);
	}
}