/**
 * 
 */
package com.ai.sboss.favorite.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

/**
 * @author Chaos
 *
 */
public class GetContentDescriptionOutProcessor implements IBasicOutProcessor {
	protected Logger logger = Logger.getLogger(this.getClass());
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";

	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		String ret = convert2requst(in.getBody(String.class));
		in.setBody(ret);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.common.interfaces.IBasicOutProcessor#convert2requst(java.lang.String)
	 */
	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject ret = JSONObject.fromObject(data);
		JSONObject returnJson = new JSONObject();
		String retmsg = "";
		
		if (ret.getString("hub_code").equals("1")) {
			JSONObject dataJson = ret.getJSONObject("data");
			returnJson.put("description", dataJson.getString("intro"));
			retmsg = "sucess";
		} else {
			retmsg = "failed";
		}
		
		return RETFMT.replace("<DATA>", returnJson.toString()).replace("<CODE>", ret.getString("hub_code"))
				.replace("<MSG>", retmsg);
	}

}
