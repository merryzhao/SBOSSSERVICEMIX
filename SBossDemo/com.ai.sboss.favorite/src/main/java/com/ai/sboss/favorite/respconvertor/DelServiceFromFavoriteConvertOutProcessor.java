package com.ai.sboss.favorite.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;



public class DelServiceFromFavoriteConvertOutProcessor implements IBasicOutProcessor {
	protected Logger logger = Logger.getLogger(this.getClass());
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {

		logger.info("query contenttags res---->"+exchange.getIn().getBody(String.class));
		String data = exchange.getIn().getBody(String.class);
		String returnJson = convert2requst(data);
		exchange.getIn().setBody(returnJson);
	}

	

	@Override
	public String convert2requst(String data) {
		JSONObject ret = JSONObject.fromObject(data);
		logger.info("param res---->"+data);
		JSONObject dataJson = ret.getJSONObject("data");
		JSONObject returnJson = new JSONObject();
		returnJson.put("result_code", dataJson.getString("resultCode"));
		returnJson.put("result_msg", dataJson.getString("result_msg"));
		
		return RETFMT.replace("<DATA>", returnJson.toString()).replace("<CODE>", dataJson.getString("resultCode"))
				.replace("<MSG>", dataJson.getString("result_msg"));
	}

}
