package com.ai.sboss.arrangement.service;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.AbstractEngineFactory;
import com.ai.sboss.arrangement.engine.IStartupControlService;
import com.ai.sboss.arrangement.engine.IStreamingControlService;
import com.ai.sboss.arrangement.entity.JsonEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

import net.sf.json.JSONObject;

/**
 * @author Chaos
 * @author yinwenjie
 */
@Component("concreteManageOps")
public class ConcreteManageOps implements IManageOps {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(ConcreteManageOps.class);
	
	/**
	 * 流程引擎驱动的抽象工厂（实际上这个工厂只可能有一个实现）
	 */
	@Autowired
	private AbstractEngineFactory engineFactory;
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IManageOps#commitProcess(java.lang.String)
	 */
	@Override
	public JsonEntity commitProcess(String process) {
		//TODO 暂未实现
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IManageOps#activateProcess(java.lang.Object)
	 */
	@Override
	public JsonEntity activateProcess(Object processId) {
		//TODO 暂未实现
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IManageOps#invalidProcess(java.lang.Object)
	 */
	@Override
	public JsonEntity invalidProcess(Object processId) {
		//TODO 暂未实现
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IManageOps#executeFlowByBusinessid(java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public JsonEntity executeFlowByBusinessid(String businessid, String executor, Map<String, Object> properties) {
		IStreamingControlService streamingControlService = this.engineFactory.getStreamingControlService();
		JSONObject resultObject = null;
		try {
			resultObject = streamingControlService.executeFlowByBusinessid(businessid, executor, properties);
		} catch (BizException e) {
			ConcreteManageOps.LOGGER.error(e.getMessage(), e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData(resultObject);
		returnJson.getDesc().setResult_msg(null);
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IManageOps#executeFlowByArrangementInstanceid(java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public JsonEntity executeFlowByArrangementInstanceid(String arrangementInstanceid, String executor, Map<String, Object> properties) {
		IStreamingControlService streamingControlService = this.engineFactory.getStreamingControlService();
		JSONObject resultObject = null;
		try {
			resultObject = streamingControlService.executeJointByArrangementInstanceid(arrangementInstanceid, executor, properties);
		} catch (BizException e) {
			ConcreteManageOps.LOGGER.error(e.getMessage(), e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData(resultObject);
		returnJson.getDesc().setResult_msg(null);
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IManageOps#executeFlowByJointInstanceid(java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public JsonEntity executeFlowByJointInstanceid(String jointInstanceid, String executor, Map<String, Object> properties) {
		IStreamingControlService streamingControlService = this.engineFactory.getStreamingControlService();
		JSONObject resultObject = null;
		try {
			resultObject = streamingControlService.executeFlowByJointInstanceid(jointInstanceid, executor, properties);
		} catch (BizException e) {
			ConcreteManageOps.LOGGER.error(e.getMessage(), e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData(resultObject);
		returnJson.getDesc().setResult_msg(null);
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IManageOps#executeUNFlowByJointInstanceid(java.lang.String, java.lang.String)
	 */
	@Override
	public JsonEntity executeUNFlowByJointInstanceid(String jointInstanceid, String executor) {
		IStreamingControlService streamingControlService = this.engineFactory.getStreamingControlService();
		JSONObject resultObject = null;
		try {
			resultObject = streamingControlService.executeUNFlowByJointInstanceid(jointInstanceid, executor);
		} catch (BizException e) {
			ConcreteManageOps.LOGGER.error(e.getMessage(), e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData(resultObject);
		returnJson.getDesc().setResult_msg(null);
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IManageOps#startUpArrangementByArrangmentid(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public JsonEntity startUpArrangementByArrangmentid(JSONObject arrangementInstanceJson) {
		IStartupControlService startupControlService = this.engineFactory.getStartupControlService();

		JSONObject resultObject = null;
		try {
			resultObject = startupControlService.startArrangementInstance(arrangementInstanceJson);
		} catch (BizException e) {
			ConcreteManageOps.LOGGER.error(e.getMessage(), e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData(resultObject);
		returnJson.getDesc().setResult_msg(null);
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}
}
