package com.ai.sboss.offeringshelves.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

/*
 * 通过传入的参数，确定
 * 1. 发布类型：销售品、产品 
 * 2. 发布目标主机类型，crm、billing 
 */
public class ServiceLaunchInProcessor implements IBasicInProcessor {

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
		// JSONObject ret = new JSONObject();
		JSONObject input = JSONObject.fromObject(exchange.getIn().getBody(
				String.class));
		// ret.put("catalognode_id", input.getLong("catalognode_id"));
		return input;
	}

}
