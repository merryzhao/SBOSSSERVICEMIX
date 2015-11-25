/**
 * 
 */
package com.ai.sboss.proffer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

/**
 * @author monica
 * 查询更新的推荐proposal列表
 */
public class FindUpdateDataForProposalInProcessor implements IBasicInProcessor{

	private static final Logger LOGGER = Logger.getLogger(FindUpdateDataForProposalInProcessor.class);

	private static final String FORMAT_QUERY = "servicecode=acquireUpdateDataForProposal&WEB_HUB_PARAMS={\"data\":<PARAM_JSON>,\"header\":{\"Content-Type\":\"application/json\"}}";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		JSONObject properInput = getInputParam(exchange);
		String query = FORMAT_QUERY.replace("<PARAM_JSON>", properInput.toString());
		LOGGER.info("FindUpdateDataForProposalInProcessor query --->" + query);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, query);
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		JSONObject param = JSONObject.fromObject(exchange.getIn().getBody(String.class));
		LOGGER.info("FindUpdateDataForProposalInProcessor param --->" + param);
		JSONObject ret = new JSONObject();
		ret.put("partyId", param.getLong("user_id"));
		ret.put("partyType", param.getInt("role_type"));
		ret.put("proposalId", param.getLong("proposal_id"));
		LOGGER.info("FindUpdateDataForProposalInProcessor ret --->" + ret.toString());
		return ret;
	}

}
