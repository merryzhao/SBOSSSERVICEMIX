package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryOfferingPriceConvertor implements IBasicOutProcessor {
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	Logger logger = Logger.getLogger(QueryOfferingPriceConvertor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		String data = exchange.getIn().getBody(String.class);
		String returnJson = convert2requst(data);
		logger.info("Got returned info:"+returnJson);
		exchange.getIn().setBody(returnJson);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		// TODO Auto-generated method stub
		logger.info("data===>"+data);
		JSONObject crmret = JSONObject.fromObject(data);
		if (crmret.getString("hub_code").equals("0")) {
			return RETFMT.replace("<DATA>", "{}").replace("<CODE>", crmret.getString("hub_code"))
					.replace("<MSG>", crmret.getString("value"));
		}
		JSONArray crmparamList = new JSONArray();
		JSONObject retJsonObject = new JSONObject();
		JSONArray priceResults=crmret.getJSONArray("data");
		retJsonObject.put("result_code", "1");
		for(int i=0;i<priceResults.size();i++){
			JSONObject priceResult=priceResults.getJSONObject(i);
			logger.info("priceResult================>"+priceResult);
			if(!priceResult.isNullObject()){
				JSONArray message=priceResult.getJSONArray("messageList");
				if(message!=null && message.size()>0){
					retJsonObject.put("hub_code", "0");
					retJsonObject.put("value", message.getJSONObject(0).getString("resultMessage"));
				}
				JSONObject crmparam = new JSONObject();
				crmparam.put("service_id", priceResult.getString("offeringId"));
				crmparam.put("role_id", priceResult.getString("roleId"));
				crmparam.put("original_price", priceResult.getString("originalPrice"));
				crmparam.put("discount_price", priceResult.getString("discountPrice"));
				crmparamList.add(crmparam);
			}
			
		}
		retJsonObject.put("price_result_list", crmparamList);
		
		return RETFMT.replace("<DATA>", retJsonObject.toString()).replace("<CODE>", crmret.getString("hub_code"))
				.replace("<MSG>", crmret.getString("value"));
	}

}
