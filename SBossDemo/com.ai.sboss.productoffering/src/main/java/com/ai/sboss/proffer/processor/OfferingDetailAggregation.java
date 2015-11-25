package com.ai.sboss.proffer.processor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class OfferingDetailAggregation implements Processor {
	Logger logger = Logger.getLogger(OfferingDetailAggregation.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject jsonData = JSONObject.fromObject(exchange.getIn().getBody()).getJSONObject("data");
		JSONArray params  = jsonData.getJSONArray("productOfferingSpecCharList");
		JSONObject outputJson = new JSONObject();
		
		String offeringId, offeringName;
		String service_intro_url = new String();
		String offeringPrice = new String();
		
		offeringId = jsonData.getString("offerId");
		offeringName = jsonData.getString("offeringName");
				
		for(int i = 0; i< params.size(); i++) {
			
			JSONArray valList = params.getJSONObject(i).getJSONArray("productOfferingSpecCharValList");
			for (int j = 0; j < valList.size(); j++) {
				if (valList.getJSONObject(j).getJSONObject("specCharValId").getJSONObject("specChar").getString("charSpecName").equals("intro_url")) {
					// service_intro_url = value_temp;
					service_intro_url = valList.getJSONObject(j).getJSONObject("specCharValId").getString("value");
					logger.info("intro_url....."+service_intro_url);
				}else if (valList.getJSONObject(j).getJSONObject("specCharValId").getJSONObject("specChar").getString("charSpecName").equals("portrait_url")) {
					// provider_portrait_url = value_temp;
					// provider_portrait_url = valList.getJSONObject(j).getJSONObject("specCharValId").getString("value");
				} else if (valList.getJSONObject(j).getJSONObject("specCharValId").getJSONObject("specChar").getString("charSpecName").equals("accessType")) {
					// provider_name = value_temp;
					// provider_name = valList.getJSONObject(j).getJSONObject("specCharValId").getString("displayVal");
				}else if(valList.getJSONObject(j).getJSONObject("specCharValId").getJSONObject("specChar").getString("charSpecName").equals("price")){
					offeringPrice = valList.getJSONObject(j).getJSONObject("specCharValId").getString("displayVal");
				}
			}
		}
		outputJson.put("service_id", offeringId);
		outputJson.put("service_name", offeringName);
		outputJson.put("service_price",offeringPrice);
		outputJson.put("service_intro_url",service_intro_url);
//Confirm this input data type, string or object?
		exchange.getIn().setBody(outputJson);
	}
}
