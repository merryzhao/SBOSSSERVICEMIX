package com.ai.sboss.order.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("extractCustomerOrderDetailInvProcessor")
public class ExtractCustomerOrderDetailInvProcessor implements Processor {

	private static Logger LOGGER = Logger
			.getLogger(ExtractCustomerOrderDetailInvProcessor.class);

	@Override
	public void process(Exchange paramExchange) throws Exception {
		
		LOGGER.info("Extract detailed customer order information from acquireCustomerOrderInventory service ...");

		// Get and extract order information
		Message inMessage = paramExchange.getIn();
		JSONObject detailedOrderInfo = (JSONObject) inMessage.getBody();
		
		LOGGER.info("Extracted order detailed information : " + detailedOrderInfo.toString());

		// Construct new JSON data and set to Exchange's body
		JSONObject retJSONObj = extectOrderDetailedInfo(detailedOrderInfo);
		
		LOGGER.info("Constructed new order information : " + retJSONObj.toString());
		
		// Set to Exchange and return to front-end.
		inMessage.setBody(retJSONObj);
	}
	
	private JSONObject extectOrderDetailedInfo(JSONObject detailedOrderInfo){
		JSONObject retObject = new JSONObject();
		if (null == detailedOrderInfo)
		{
			return retObject; 
		}
		
		JSONObject jsonData = detailedOrderInfo.getJSONObject("data");
		//JSONObject jsonBizInteraction = jsonData.getJSONObject("businessInteraction");
		//JSONArray jsonCustomerOrderList = jsonData.getJSONArray("customerOrderItemList");
		
		
		retObject.put("order_id", jsonData.getString("ordCustId"));
		retObject.put("order_number", jsonData.getString("ordCustId"));
		retObject.put("order_state", jsonData.getString("state"));
		retObject.put("order_state_name", "Empty");
		retObject.put("order_create_time", jsonData.getString("createDate"));
		retObject.put("service_id", "Empty");
		retObject.put("service_name", "Empty");
		retObject.put("provider_id", "Empty");
		retObject.put("provider_portrait_rul", "Empty");
		retObject.put("service_time_duration", "Empty");
		retObject.put("order_price", "Empty");
		retObject.put("service_type", "Empty");
		
		return retObject;
	}

}