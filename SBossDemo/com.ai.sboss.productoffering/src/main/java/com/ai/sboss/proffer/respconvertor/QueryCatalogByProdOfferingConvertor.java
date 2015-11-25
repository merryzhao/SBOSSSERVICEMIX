package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryCatalogByProdOfferingConvertor implements IBasicOutProcessor {

	private Logger LOGGER = Logger.getLogger(QueryCatalogByProdOfferingConvertor.class);
	
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();
		final String retResult = convert2requst(inMessage.getBody(String.class));
		
		LOGGER.info(retResult);

		inMessage.setBody(retResult);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject input = JSONObject.fromObject(data);
		LOGGER.info(input.toString());
		
		Object dataValue = input.get("data");
		if (null != dataValue) {
			if (dataValue.toString().isEmpty()) {
				return RETFMT.replace("<DATA>", "[]")
						.replace("<CODE>", input.getString("hub_code"))
						.replace("<MSG>", StringUtils.EMPTY);
			} else {
			
				JSONArray listOfData = input.getJSONArray("data");
				if (null != listOfData && listOfData.size() > 0){
					JSONObject objOfJSON = listOfData.getJSONObject(0);
					JSONObject newObject = new JSONObject();
					newObject.put("catalog_id", objOfJSON.getLong("catalogId"));
					newObject.put("catalog_name", objOfJSON.getString("name"));
					newObject.put("catalog_icon", "");
					
					return RETFMT
							.replace("<DATA>",
									newObject.toString())
							.replace("<CODE>", input.getString("hub_code"))
							.replace("<MSG>", StringUtils.EMPTY);
				}else
				{
					return RETFMT
							.replace("<DATA>",
									"")
							.replace("<CODE>", input.getString("hub_code"))
							.replace("<MSG>", StringUtils.EMPTY);
				}
			}
		} else {
			return RETFMT.replace("<DATA>", "[]")
					.replace("<CODE>", input.getString("0"))
					.replace("<MSG>", StringUtils.EMPTY);
		}
	}

}
