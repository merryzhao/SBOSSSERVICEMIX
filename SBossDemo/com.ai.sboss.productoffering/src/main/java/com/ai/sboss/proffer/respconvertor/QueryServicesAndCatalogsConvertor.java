package com.ai.sboss.proffer.respconvertor;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryServicesAndCatalogsConvertor implements IBasicOutProcessor {
	Logger logger = Logger.getLogger(QueryServicesAndCatalogsConvertor.class);
	private final static String responsefmt = "{\"data\":<DATA>,\"desc\":{\"result_code\":<RET_CODE>,\"result_msg\":\"<RET_MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		String retString = convert2requst(exchange.getIn().getBody(String.class));
		exchange.getIn().setHeaders(null);
		exchange.getIn().setHeader("Content-Type", "application/json");
		exchange.getIn().setBody(retString);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		if (StringUtils.isEmpty(data)) {
			return responsefmt.replace("<DATA>", "").replace("<RET_MSG>", "failed").replace("<RET_CODE>", "0");
		}
		
		/*JSONArray cataloglist = JSONArray.fromObject(JsonPath.read(data, "$.catalog_list"));
		JSONArray servicelist = JSONArray.fromObject(JsonPath.read(data, "$.service_list"));*/
		/*JSONObject retJsonObject = JSONObject.fromObject(data);*/
		return responsefmt.replace("<DATA>", data).replace("<RET_MSG>", "success").replace("<RET_CODE>", "1");
	}

}
