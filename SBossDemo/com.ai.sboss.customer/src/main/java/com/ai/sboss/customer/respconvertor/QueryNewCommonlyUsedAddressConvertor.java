package com.ai.sboss.customer.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

/*public class QueryNewCommonlyUsedAddressConvertor extends CrmRespConvertor {
 Logger logger = Logger.getLogger(QueryNewCommonlyUsedAddressConvertor.class);

 @Override
 public String convert2Resp(String array) {
 String custEmailKey = "$.data[0].email";
 String custMobileKey = "$.data[0].mobile";
 String value = "{\"email\":\"" + JsonPath.read(array, custEmailKey) + "\", \"phone\":\"" + JsonPath.read(array, custMobileKey) + "\"}";
 logger.info(value);
 return value;
 }

 }*/
public class QueryNewCommonlyUsedAddressConvertor implements Processor {
	Logger logger = Logger
			.getLogger(QueryNewCommonlyUsedAddressConvertor.class);
	private final String MEDIU_FMT = "{\"email\":\"<EMAIL>\",\"phone\":\"<PHONE>\"}";

	/*
	 * @Override public String convert2Resp(String array) { String custEmailKey
	 * = "$.data[0].email"; String custMobileKey = "$.data[0].mobile"; String
	 * value = "{\"email\":\"" + JsonPath.read(array, custEmailKey) +
	 * "\", \"phone\":\"" + JsonPath.read(array, custMobileKey) + "\"}";
	 * logger.info(value); return value; }
	 */

	public void process(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();
		logger.info("QueryNewCommonlyUsedAddressConvertor");
		try {
			JSONArray mediu = JSONObject
					.fromObject( exchange.getIn().getBody(String.class))
					.getJSONObject("data").getJSONArray("data");
			
			JSONObject primarymediu = mediu.getJSONObject(0);
			String ret = MEDIU_FMT.replace("<EMAIL>",
					primarymediu.getString("email")).replace("<PHONE>",
					primarymediu.getString("mobile"));
			
			inMessage.setBody(ret);
			logger.info("QueryNewCommonlyUsedAddressConvertorFAKE"+ret);
			inMessage.setHeader(Exchange.FILE_NAME, "MEDIU");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("query failed in QueryNewCommonlyUsedAddressConvertor");
		}
	}

}