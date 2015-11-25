package com.ai.sboss.arrangement.service.camel;

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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 利用camel向外部提供的启动流程的接口
 * @author chaos
 */
@Component("startUpArrangementProcessor")
public class StartUpArrangementProcessor implements IBasicInProcessor {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(StartUpArrangementProcessor.class);
	
	/**
	 * 整个编排系统最顶层暴露给客户端的服务接口
	 */
	@Autowired
	private IManageOps manageOps;
	
	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		/*
		 * 1、由于从http过来的参数信息（json格式），已经由GeneralJettyInProcessor处理好了，所以只需要验证所需要的参数是不是都有
		 * 		特别是必要的条件参数是否传入(这里不需要报错，因为“2”的步骤已经包含了所有异常的处理)
		 * 2、直接调用manageOps中相应的方法得到查询结果，并且输出
		 * */
		//1、================
		JsonEntity jsonEntity = null;
		JSONObject httpDateParams = null;
		try {
			httpDateParams = this.getInputParam(exchange);
			StartUpArrangementProcessor.LOGGER.info("========得到参数：" + httpDateParams.toString());
		} catch(BizException e) {
			StartUpArrangementProcessor.LOGGER.error(e.getMessage(), e);
			jsonEntity = new JsonEntity();
			jsonEntity.setData("");
			jsonEntity.getDesc().setResult_msg(e.getMessage());
			jsonEntity.getDesc().setResult_code(ResponseCode._501);
		}
		
		//2、================
		if(jsonEntity == null) {
			jsonEntity = this.manageOps.startUpArrangementByArrangmentid(httpDateParams);
			StartUpArrangementProcessor.LOGGER.info("========得到结果：" + jsonEntity.toString());
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
		JSONObject inputParam = JSONObject.fromObject(message.getBody(String.class));
		JSONObject arrangementInstace = new JSONObject();
		arrangementInstace.put("arrangementid", inputParam.getString("arrangement_id"));
		arrangementInstace.put("businessid", inputParam.getString("business_id"));
		arrangementInstace.put("creator", inputParam.getString("user_id"));
		arrangementInstace.put("creatorScope", inputParam.getString("creator_scope"));
		String displayName = inputParam.containsKey("display_name")?inputParam.getString("display_name"):null;
		arrangementInstace.put("displayName", displayName);
		JSONArray inputjointinstances = inputParam.getJSONArray("jointinstances");
		JSONArray jointinstances = new JSONArray();
		if (inputjointinstances != null && !inputjointinstances.isEmpty()) {
			for (int index = 0; index < inputjointinstances.size(); ++index) {
				JSONObject tempobj = new JSONObject();
				tempobj.put("jointid", inputjointinstances.getJSONObject(index).getString("joint"));
				//下面五个参数认为是可选的
				if (inputjointinstances.getJSONObject(index).has("executor")) {
					tempobj.put("executor", inputjointinstances.getJSONObject(index).getString("executor"));
				}
				if (inputjointinstances.getJSONObject(index).has("offset_visible")) {
					tempobj.put("offsetVisible", inputjointinstances.getJSONObject(index).getString("offset_visible"));
				}
				if (inputjointinstances.getJSONObject(index).has("abs_offsettime")) {
					tempobj.put("absOffsettime", inputjointinstances.getJSONObject(index).getLong("abs_offsettime"));
				}
				if (inputjointinstances.getJSONObject(index).has("relate_offsettime")) {
					tempobj.put("relateOffsettime", inputjointinstances.getJSONObject(index).getLong("relate_offsettime"));
				}
				if (inputjointinstances.getJSONObject(index).has("expand_typeid")) {
					tempobj.put("expandTypeId", inputjointinstances.getJSONObject(index).getString("expand_typeid"));
				}
				jointinstances.add(tempobj);
			}
			arrangementInstace.put("jointinstances", jointinstances);
		}
		
		
		JSONObject ret = new JSONObject();
		ret.put("arrangementInstance", arrangementInstace);
		return ret;
	}	
}
