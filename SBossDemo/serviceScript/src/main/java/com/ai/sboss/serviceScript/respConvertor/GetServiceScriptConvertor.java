package com.ai.sboss.serviceScript.respConvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;
import com.ai.sboss.serviceScript.parameter.IScriptParameter;

@Component("getServiceScriptConvertor")
public class GetServiceScriptConvertor implements IBasicOutProcessor {

	private static final Logger LOGGER = Logger
			.getLogger(GetServiceScriptConvertor.class);

	private final static String RESULT_FMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<RET_CODE>,\"result_msg\":\"<RET_MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";

	@Autowired
	@Qualifier(value = "serviceScriptParameter")
	private IScriptParameter serviceScriptParameter;

	@Override
	public void process(Exchange exchange) throws Exception {
		final String retString = convert2requst(exchange.getIn().getBody(
				String.class));

		exchange.getIn().setHeaders(null);
		exchange.getIn().setHeader("Content-Type",
				"application/json;charset=utf-8");
		exchange.getIn().setBody(retString);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		LOGGER.info("Needed data: " + data);
		System.out.println("Needed data: " + data);

		String scriptRetValue = StringUtils.EMPTY;
		if (!StringUtils.isEmpty(data)) {
			JSONObject inputJsonObj = JSONObject.fromObject(data);

			// Get script value
			scriptRetValue = serviceScriptParameter
					.getScriptValue(inputJsonObj);
		}

		return RESULT_FMT.replace("<DATA>", scriptRetValue)
				.replace("<RET_CODE>", "1").replace("<RET_MSG>", "success");
	}
}
