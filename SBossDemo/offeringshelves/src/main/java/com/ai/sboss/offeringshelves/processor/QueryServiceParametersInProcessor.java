/**
 * 
 */
package com.ai.sboss.offeringshelves.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

/**
 * @author idot
 *
 */
public class QueryServiceParametersInProcessor implements IBasicInProcessor{

	protected Logger logger = Logger.getLogger(this.getClass());
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		JSONObject properInput = getInputParam(exchange);
		exchange.getIn().setBody(properInput.toString());
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		JSONObject ret = new JSONObject();
		JSONObject input = JSONObject.fromObject(exchange.getIn().getBody(String.class));
		ret.put("catalogNodeId", input.getLong("catalogNodeId"));
		return ret;
	}

}
