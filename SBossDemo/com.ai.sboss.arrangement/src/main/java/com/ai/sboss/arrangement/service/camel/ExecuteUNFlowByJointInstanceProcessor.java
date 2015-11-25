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
import com.ai.sboss.arrangement.service.IManageOps;
import com.ai.sboss.arrangement.utils.JSONUtils;

/**
 * 利用camel向外部提供的  逆向执行指定的任务实例  的接口
 * @author yinwenjie
 */
@Component("executeUNFlowByJointInstanceProcessor")
public class ExecuteUNFlowByJointInstanceProcessor implements IBasicInProcessor {

	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(ExecuteUNFlowByJointInstanceProcessor.class);
	
	/**
	 * 最外层的管理操作对象
	 */
	@Autowired
	private IManageOps manageOps;
	
	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		//1、================获取jointInstanceid, executor, properties信息
		String jointInstanceid = null; 
		String executor = null;
		JsonEntity jsonEntity = null;
		try {
			JSONObject httpDateParams = this.getInputParam(exchange);
			jointInstanceid = httpDateParams.has("jointInstanceid")?httpDateParams.getString("jointInstanceid"):null;
			executor = httpDateParams.has("executor")?httpDateParams.getString("executor"):null;
			LOGGER.info("========得到参数：" + httpDateParams.toString());
		} catch(BizException e) {
			ExecuteUNFlowByJointInstanceProcessor.LOGGER.error(e.getMessage(), e);
			jsonEntity = new JsonEntity();
			jsonEntity.setData("");
			jsonEntity.getDesc().setResult_msg(e.getMessage());
			jsonEntity.getDesc().setResult_code(ResponseCode._501);
		}
		
		//2、================进行调用
		if(jsonEntity == null) {
			jsonEntity = this.manageOps.executeUNFlowByJointInstanceid(jointInstanceid, executor);
			LOGGER.info("========得到结果：" + jsonEntity.toString());
		}
		
		//返回
		exchange.getIn().setBody(JSONUtils.toString(jsonEntity));
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.camel.IBasicInProcessor#getInputParam(org.apache.camel.Exchange)
	 */
	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		JSONObject ret = JSONUtils.toJSONObject(message.getBody(String.class), new String[]{""});
		return ret;
	}
}