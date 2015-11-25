package com.ai.sboss.arrangement.service.camel;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.entity.JsonEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;
import com.ai.sboss.arrangement.service.IAddOps;
import com.ai.sboss.arrangement.utils.JSONUtils;

/**
 * 利用camel向外部提供的 "添加任务节点模板" 的接口。<br>
 * 虽然这个接口，前端的同事说8月份不用，但是还是提供了。
 * @author yinwenjie
 */
@Component("addJointItemProcessor")
public class AddJointItemProcessor implements IBasicInProcessor {
	
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(AddJointItemProcessor.class);
	
	@Autowired
	private IAddOps addOps;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		JsonEntity jsonEntity = null;
		try {
			//这个就是json中的data元素了
			JSONObject httpDateParams = this.getInputParam(exchange);
			jsonEntity = this.addOps.addJointItem(httpDateParams);
		} catch(BizException e) {
			AddJointItemProcessor.LOGGER.error(e.getMessage(), e);
			jsonEntity = new JsonEntity();
			jsonEntity.setData("");
			jsonEntity.getDesc().setResult_msg(e.getMessage());
			jsonEntity.getDesc().setResult_code(ResponseCode._501);
		}
		
		//返回
		exchange.getIn().setBody(JSONUtils.toString(jsonEntity , new String[]{"joint","jointinstances"}));
		//exchange.getIn().setHeaders(null);
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.camel.IBasicInProcessor#getInputParam(org.apache.camel.Exchange)
	 */
	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		JSONObject ret = JSONObject.fromObject(message.getBody(String.class));
		return ret;
	}
}
