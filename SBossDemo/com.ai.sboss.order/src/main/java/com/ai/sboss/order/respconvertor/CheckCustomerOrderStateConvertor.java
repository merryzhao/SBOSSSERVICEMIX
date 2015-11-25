package com.ai.sboss.order.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

@Component("checkCustomerOrderStateConvertor")
public class CheckCustomerOrderStateConvertor implements IBasicOutProcessor {

	private final static Log LOGGER = LogFactory
			.getLog(CheckCustomerOrderStateConvertor.class);

	private final static String RESULT_FMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<RET_CODE>,\"result_msg\":\"<RET_MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		String retString = convert2requst(exchange.getIn()
				.getBody(String.class));
		exchange.getIn().setHeaders(null);
		exchange.getIn().setHeader("Content-Type", "application/json");
		exchange.getIn().setBody(retString);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject crmJsonObject = JSONObject.fromObject(data);
		LOGGER.info("Got CRM Response:"+data);
		JSONObject retValue = new JSONObject();
		if (StringUtils.equals(crmJsonObject.getString("hub_code"), "0")) {
			retValue.put("result", false);
			return RESULT_FMT.replace("<DATA>", retValue.toString()).replace("<RET_CODE>", "0").replace("<RET_MSG>", "failed");
		}
		
		retValue.put("result", crmJsonObject.getBoolean("data"));
		return RESULT_FMT.replace("<DATA>", retValue.toString()).replace("<RET_CODE>", "1").replace("<RET_MSG>", "success");
	}

}
