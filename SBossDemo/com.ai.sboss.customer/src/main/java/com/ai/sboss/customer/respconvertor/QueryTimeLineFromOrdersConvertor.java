package com.ai.sboss.customer.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryTimeLineFromOrdersConvertor implements IBasicOutProcessor {
	private Logger logger = Logger.getLogger(QueryTimeLineFromOrdersConvertor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		String retString = convert2requst(exchange.getIn().getBody(String.class));
		exchange.getIn().setHeaders(null);
		exchange.getIn().setBody(retString);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject crmObject = JSONObject.fromObject(data);
		JSONObject retObject = new JSONObject();
		logger.info("QueryTimeLineFromOrdersConvertor=========>"+crmObject.toString());
		if (StringUtils.equals(crmObject.getJSONObject("desc").getString("result_code"), "0")) {
			return retObject.put("order_list", new JSONArray()).toString();
		}
		retObject.put("order_list", crmObject.getJSONArray("data").toString());
		return retObject.toString();
	}

}
