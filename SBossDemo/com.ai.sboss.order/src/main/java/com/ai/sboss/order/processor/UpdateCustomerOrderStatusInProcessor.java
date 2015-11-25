package com.ai.sboss.order.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

@Component("updateCustomerOrderStatusInProcessor")
public class UpdateCustomerOrderStatusInProcessor implements IBasicInProcessor {
	/**
	 * 日志信息
	 */
	private static final Log LOGGER = LogFactory.getLog(UpdateCustomerOrderStatusInProcessor.class);
	/**
	 * 这个常量是crm request的固定参数servicecode=queryOrderList
	 */
	private static final String CRM_SERVICE_CODE = "updateStateOfOrder";

	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject inputParam = getInputParam(exchange);
		// 向CRM发送参数了
		String urlParams = "servicecode=" + UpdateCustomerOrderStatusInProcessor.CRM_SERVICE_CODE + "&WEB_HUB_PARAMS={\"data\":" + inputParam.toString()+",\"header\":{\"Content-Type\":\"application/json\"}}";
		UpdateCustomerOrderStatusInProcessor.LOGGER.info("向CRM发送的参数为：" + urlParams);
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
		retObject.put("custOrderId", ((Long)inputJsonObject.getLong("order_number")).toString());
		retObject.put("orderState", ((Long)inputJsonObject.getLong("order_status")).toString());
		retObject.put("remarks", "Update status of customer order");
		return retObject;
	}

}
