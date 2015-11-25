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
public class QueryProviderInfoInProcessor implements IBasicInProcessor{

	private static final String HUB_FORMAT_QUERY = "servicecode=queryProvider&WEB_HUB_PARAMS={\"data\":<PARAM_JSON>,\"header\":{\"Content-Type\":\"application/json\"}}";
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
		int role_type = input.getInt("role_type");
		if (role_type == 2){
			ret.put("providerId", input.getLong("user_id"));
		}
		return ret;
	}

}
