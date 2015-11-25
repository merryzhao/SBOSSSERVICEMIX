package com.ai.sboss.arrangement.service.camel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
 * 利用camel向外部提供的  正向执行指定的任务实例  的接口
 * @author yinwenjie
 */
@Component("executeFlowByJointInstanceProcessor")
public class ExecuteFlowByJointInstanceProcessor implements IBasicInProcessor {
	
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(ExecuteFlowByJointInstanceProcessor.class);
	
	/**
	 * 最外层的管理操作对象
	 */
	@Autowired
	private IManageOps manageOps;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		//1、================获取jointInstanceid, executor, properties信息
		String jointInstanceid = null; 
		String executor = null;
		JSONObject propertieObjects = null;
		JsonEntity jsonEntity = null;
		try {
			JSONObject httpDateParams = this.getInputParam(exchange);
			jointInstanceid = httpDateParams.has("jointInstanceid")?httpDateParams.getString("jointInstanceid"):null;
			executor = httpDateParams.has("executor")?httpDateParams.getString("executor"):null;
			propertieObjects = httpDateParams.has("properties")?httpDateParams.getJSONObject("properties"):null;
			LOGGER.info("========得到参数：" + httpDateParams.toString());
		} catch(BizException e) {
			ExecuteFlowByJointInstanceProcessor.LOGGER.error(e.getMessage(), e);
			jsonEntity = new JsonEntity();
			jsonEntity.setData("");
			jsonEntity.getDesc().setResult_msg(e.getMessage());
			jsonEntity.getDesc().setResult_code(ResponseCode._501);
		}
		
		//2、================组装可能存在的properties K-V对
		Map<String, Object> properties = null;
		if(jsonEntity == null && propertieObjects != null) {
			properties = new HashMap<String, Object>();
			@SuppressWarnings("unchecked")
			Iterator<String> propertieKeys = propertieObjects.keys();
			while(propertieKeys.hasNext()) {
				String propertieKey = propertieKeys.next();
				Object propertieValue = propertieObjects.get(propertieKey);
				properties.put(propertieKey, propertieValue);
			}
		}
		
		//3、================进行调用
		if(jsonEntity == null) {
			jsonEntity = this.manageOps.executeFlowByJointInstanceid(jointInstanceid, executor, properties);
			LOGGER.info("========得到结果：" + jsonEntity.toString());
		}
		
		//返回
		exchange.getIn().setBody(JSONUtils.toString(jsonEntity));
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		JSONObject ret = JSONUtils.toJSONObject(message.getBody(String.class), new String[]{""});
		return ret;
	}
}