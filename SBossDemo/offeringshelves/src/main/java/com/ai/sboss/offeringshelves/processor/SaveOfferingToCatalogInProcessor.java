/**
 * 
 */
package com.ai.sboss.offeringshelves.processor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

/**
 * @author idot
 *
 */
public class SaveOfferingToCatalogInProcessor implements IBasicInProcessor{

	protected Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		JSONObject properInput = getInputParam(exchange);
		logger.info("---------" + properInput);
		exchange.getIn().setBody(properInput.toString());
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		JSONObject ret = new JSONObject();
		JSONObject input = JSONObject.fromObject(exchange.getIn().getBody(String.class));
		ret.put("nodeId", input.getLong("nodeId"));
		logger.info("---------" + input.getJSONArray("offeringList"));
		JSONArray offeringList = input.getJSONArray("offeringList");
		ret.put("offeringList", offeringList);
		return ret;
	}

}
