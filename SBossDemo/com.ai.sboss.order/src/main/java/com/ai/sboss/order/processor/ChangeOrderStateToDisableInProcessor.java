package com.ai.sboss.order.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

@Component("changeOrderStateToDisableInProcessor")
public class ChangeOrderStateToDisableInProcessor implements IBasicInProcessor {

	private static final Log LOGGER = LogFactory
			.getLog(ChangeOrderStateToDisableInProcessor.class);

	private static final String CRM_SERVICE_CODE = "changeOrderStateToDisable";

	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject inputParam = getInputParam(exchange);

		// 向CRM发送参数了
		String urlParams = "servicecode=" + CRM_SERVICE_CODE
				+ "&WEB_HUB_PARAMS={\"data\":" + inputParam.toString()
				+ ",\"header\":{\"Content-Type\":\"application/json\"}}";

		LOGGER.info("CheckCustomerOrderStateInProcessor向CRM发送的参数为：" + urlParams);

		exchange.getIn().setHeaders(null);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, urlParams);
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		String bodyString = exchange.getIn().getBody(String.class);
		JSONObject retObject = new JSONObject();
		if (StringUtils.isEmpty(bodyString)) {
			return retObject;
		}
		JSONObject inputJsonObject = JSONObject.fromObject(bodyString);
		retObject.put("custOrder_id", inputJsonObject.getLong("order_id"));
//		retObject.put("user_id", inputJsonObject.getLong("user_id"));
//		retObject.put("proposal_id", inputJsonObject.getLong("proposal_id"));
		return retObject;
	}

}
