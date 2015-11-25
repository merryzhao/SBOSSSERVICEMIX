/**
 * 
 */
package com.ai.sboss.proffer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.xpath.compiler.Keywords;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

/**
 * @author Chaos
 *
 */
public class QueryServicesAndCatalogsInProcessor implements IBasicInProcessor {

	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject crmparam = getInputParam(exchange);
		//清空header 避免参数重复传递
		exchange.getIn().setHeaders(null);
		exchange.getIn().setBody(crmparam.toString());
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		JSONObject inputparam = JSONObject.fromObject(in.getBody(String.class));
		String keywords = "";
		if (inputparam.containsKey("keyword")) {
			keywords = inputparam.getString("keyword");
		}
		Long pagenum = inputparam.getLong("page_num");
		Long pagesize = inputparam.getLong("page_size");
		JSONObject ret = new JSONObject();
		
		ret.put("keyword", keywords);
		ret.put("page_size", pagesize);
		ret.put("page_num", pagenum);
		
		return ret;
	}

}
