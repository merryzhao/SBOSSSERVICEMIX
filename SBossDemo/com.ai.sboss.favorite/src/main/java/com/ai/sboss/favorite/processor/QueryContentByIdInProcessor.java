/**
 * 
 */
package com.ai.sboss.favorite.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

/**
 * @author Chaos
 *
 */
public class QueryContentByIdInProcessor implements IBasicInProcessor {
	protected Logger logger = Logger.getLogger(this.getClass());
	private static final String HUB_FORMAT_QUERY = "servicecode=queryContentById&WEB_HUB_PARAMS={\"data\":<PARAM_JSON>,\"header\":{\"Content-Type\":\"application/json\"}}";

	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject properInput = getInputParam(exchange);
		String query = HUB_FORMAT_QUERY.replace("<PARAM_JSON>", properInput.toString());
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, query);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.common.interfaces.IBasicInProcessor#getInputParam(org.apache.camel.Exchange)
	 */
	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		JSONObject param = JSONObject.fromObject(exchange.getIn().getBody(String.class));
		logger.info("QueryContentByIdInProcessor param--->" + param);
		JSONObject ret = new JSONObject();
		ret.put("contentEntryId", param.getLong("contentId"));
		return ret;
	}

}
