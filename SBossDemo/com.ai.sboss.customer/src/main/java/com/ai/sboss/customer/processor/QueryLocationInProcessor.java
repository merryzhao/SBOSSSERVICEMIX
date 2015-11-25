/**
 * 
 */
package com.ai.sboss.customer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

/**
 * @author monica
 *
 */
public class QueryLocationInProcessor implements IBasicInProcessor{

	private static final String HUB_FORMAT_QUERY = "servicecode=queryLocation&WEB_HUB_PARAMS={\"data\":<PARAM_JSON>,\"header\":{\"Content-Type\":\"application/json\"}}";
	protected Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		JSONObject properInput = getInputParam(exchange);
		String query = HUB_FORMAT_QUERY.replace("<PARAM_JSON>", properInput.toString());
		logger.info("query->" + query);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, query);
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		JSONObject ret = new JSONObject();
		JSONObject input = JSONObject.fromObject(exchange.getIn().getBody(String.class));
		Long placeId = input.getLong("placeId");
		ret.put("placeId", placeId);
		return ret;
	}

}
