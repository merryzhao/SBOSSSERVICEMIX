package com.ai.sboss.order.processor;

import org.apache.camel.Exchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

import net.sf.json.JSONObject;

@Component("findServiceProcessInstanceProcessor")
public class FindServiceProcessInstanceProcessor implements IBasicInProcessor {
	private final static String TEMPLATE = "{\"data\":<DATA>, \"desc\":\"\"}";
	private final static Log LOGGER = LogFactory.getLog(FindServiceProcessInstanceProcessor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject prepare = getInputParam(exchange);
		exchange.getIn().setHeaders(null);
		exchange.getIn().setBody(TEMPLATE.replace("<DATA>", prepare.toString()));
		LOGGER.info("请求JSON=>"+prepare.toString());
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		JSONObject inputparam = JSONObject.fromObject(exchange.getIn().getBody(String.class));
		if (!inputparam.has("instance_id")) {
			//TODO: 这里后续需要用我们的异常来改写
			throw new IllegalArgumentException("服务流程实例ID instance_id 必须传入！");
		}
		String arrangementInstanceId = inputparam.getString("instance_id");
		JSONObject retJson = new JSONObject();
		retJson.put("arrangementinstanceid", arrangementInstanceId);
		return retJson;
	}

}
