package com.ai.sboss.order.processor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("getOrderIdListProcessor")
public class GetOrderIdListProcessor implements Processor {

	private static Logger LOGGER = Logger
			.getLogger(GetOrderIdListProcessor.class);

	@Override
	public void process(Exchange paramExchange) throws Exception {
		Message inMessage = paramExchange.getIn();
		LOGGER.info("Inputted customer order list : "
				+ inMessage.getBody(String.class));

		JSONObject customerOrders = JSONObject.fromObject(inMessage
				.getBody(String.class));
		JSONArray orderList = customerOrders.getJSONObject("data")
				.getJSONArray("customerOrderList");
		if (customerOrders.isEmpty()) {
			LOGGER.info("The result is Empty");
		} else {
			LOGGER.info("Loop for Customer Order : " + orderList.toString());

			String indexFlag = "";
			JSONArray orderIdList = new JSONArray();
			final int sizeOfOrderList = orderList.size();
			inMessage.setHeader("AILoopSize", sizeOfOrderList);
			for (int nIndex = 0; nIndex < sizeOfOrderList; ++nIndex) {
				
				JSONObject tmpObj = orderList.getJSONObject(nIndex);
				
				LOGGER.info("Customer order Id : " + tmpObj.getString("ordCustId"));
				
				JSONObject newObj = new JSONObject();
				newObj.put("order_id", tmpObj.getString("ordCustId"));
				orderIdList.add(newObj);

				if (0 == nIndex) {
					indexFlag = Integer.valueOf(nIndex).toString();
				} else {
					indexFlag = indexFlag + ","
							+ Integer.valueOf(nIndex).toString();
				}
			}
			
			LOGGER.info("The set of Index is " + indexFlag);
			LOGGER.info("The set of OrderId is " + orderIdList.toString());

			inMessage.setHeader("orderIdList", orderIdList);
			inMessage.setBody(indexFlag);
		}
	}
}