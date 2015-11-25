package com.ai.sboss.order.processor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import net.minidev.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

/**
 * 该processor用于构造 查询CRM7中订单列表 的参数信息，以便route的下一步能够正常调用CRM的订单列表查询接口
 * @author yinwenjie
 */
@Component("queryOrderListProcessor")
public class QueryOrderListProcessor implements IBasicInProcessor {
	/**
	 * 这个常量是crm request的固定参数servicecode=queryOrderList
	 */
	private static final String CRM_SERVICE_CODE = "queryOrderList"; 
	
	/**
	 * 这个常量是crm request的固定参数Content-Type=application/json
	 */
	private static final String CRM_SERVICE_HEAD_CONTENT_TYPE = "application/json";
	
	/**
	 * 日志信息
	 */
	private static final Log LOGGER = LogFactory.getLog(QueryOrderListProcessor.class);

	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject properParams = getInputParam(exchange);
		
		//向CRM发送参数了
		String urlParams = "bridgeEndpoint=true&servicecode=" + QueryOrderListProcessor.CRM_SERVICE_CODE + "&WEB_HUB_PARAMS=" + properParams;
		QueryOrderListProcessor.LOGGER.info("向CRM发送的参数为：" + urlParams);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, urlParams);
		exchange.getIn().setHeader(Exchange.HTTP_METHOD,"POST");
	}

	/**
	 * 实现这个方法，从客户端jetty请求过来的URL带有参数信息，以便将这些参数信息进行获取
	 * @see com.asiainfo.lab2020.activititest.service.camel.ai.sboss.common.interfaces.IBasicInProcessor#getInputParam(org.apache.camel.Exchange)
	 */
	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		
		//=============1、拿取jetty过来的参数，注意：这里不用自己去转，前面的generalJettyInProcessor已经帮忙转好
		JSONObject ret = JSONObject.fromObject(message.getBody(String.class));
		QueryOrderListProcessor.LOGGER.info("jetty URL 传来的参数信息：" + ret);
		String customerId = ((Long)ret.getLong("userId")).toString();
		
		//==============2、判断传来的HTTP必要参数是不是正确的
		//查询角色正误性判断
		String orderRole = ret.has("order_role") == true?ret.getString("order_role") : null;
		Boolean error = false;
		String errorMessage = "";
		if(StringUtils.isEmpty(orderRole)) {
			error = true;
			errorMessage += "“查询角色（order_role）”必须有值,";
		}
		if(!StringUtils.isEmpty(orderRole) 
			&& !StringUtils.equals(orderRole, "1") && !StringUtils.equals(orderRole, "2")) {
			error = true;
			errorMessage += "“查询角色（order_role）”只能是“买家角色（1）”或者“卖家角色（2）”,";
		}
		//将Order_role的值转换为crm内部值并更换相应的user_id
		//TODO:这里还是fake文件的方式，稍后可能需要去crm中查一把。
		if (StringUtils.equals(orderRole, "2")) {
			orderRole = "9";
		}
		//查询订单状态(order_state)正误性判断
		String orderState = ret.has("order_state") == true?ret.getString("order_state") : null;
		if(StringUtils.isEmpty(orderState)) {
			error = true;
			errorMessage += "“订单状态(order_state)”必须有值,";
		}
		if(error) {
			throw new IllegalArgumentException(errorMessage);
		}
		
		/*
		 * 3、构造CRM中需要的参数信息，就是构造WEB_HUB_PARAMS参数
		 * 要构造出来的格式类似于：
		 * WEB_HUB_PARAMS：{"data":{"orderState":"11","orderRole":"1"},"heade":{"Content-Type":"application/json"}}
		 * */
		JSONObject crmParamsJson = new JSONObject();
		//data
		JSONObject crmParamsJson_data = new JSONObject();
		crmParamsJson_data.put("orderState", orderState);
		crmParamsJson_data.put("orderRole", orderRole);
		crmParamsJson_data.put("customerId", customerId);
		crmParamsJson.put("data", crmParamsJson_data);
		//heade
		JSONObject crmParamsJson_heade = new JSONObject();
		crmParamsJson_heade.put("Content-Type", QueryOrderListProcessor.CRM_SERVICE_HEAD_CONTENT_TYPE);
		crmParamsJson.put("heade", crmParamsJson_heade);
		return crmParamsJson;
	}


}
