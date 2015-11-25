/**
 * 
 */
package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

/**
 * @author Chaos
 *
 */
public class QueryServiceCatalogByTextConvertor implements IBasicOutProcessor {
	private final static String CRMFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	Logger logger = Logger.getLogger(QueryServiceCatalogByTextConvertor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		String retString = convert2requst(in.getBody(String.class));
		Message retMessage = exchange.getOut();
		retMessage.setBody(retString);
		exchange.setIn(retMessage);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		if (StringUtils.isEmpty(data)) {
			return CRMFMT.replace("<DATA>", "").replace("<CODE>", "0").replace("<MSG>", "failed");
		}
		JSONObject catalogList = JSONObject.fromObject(data);
		String crmret = CRMFMT.replace("<DATA>", catalogList.toString()).replace("<CODE>", "1").replace("<MSG>", "success");
		return crmret;
	}
}
