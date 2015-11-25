package com.ai.sboss.arrangement.engine.local;

import java.util.HashSet;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.IBeforeEventCommand;
import com.ai.sboss.arrangement.engine.dao.ArrangementDAOAbstractFactory;
import com.ai.sboss.arrangement.engine.dao.InstanceDAOService;
import com.ai.sboss.arrangement.entity.orm.InstanceContextParamEntity;
import com.ai.sboss.arrangement.event.ArrangementExceptionEventSender;
import com.ai.sboss.arrangement.event.ArrangementFlowEventSender;
import com.ai.sboss.arrangement.event.EventSendAbstractFactory;
import com.ai.sboss.arrangement.event.EventType;
import com.ai.sboss.arrangement.event.ExceptionEvent;
import com.ai.sboss.arrangement.event.FlowEvent;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * “流转前”事件命令的本地实现。从目前的业务特点来说，只可能有“本地实现”
 * @author yinwenjie
 */
@Component("_processorEngine_beforeEventCommand")
public class BeforeEventCommandLocalImpl implements IBeforeEventCommand {
	
	@Autowired
	private EventSendAbstractFactory eventSendAbstractFactory;
	
	@Autowired
	private ArrangementDAOAbstractFactory arrangementDAOAbstractFactory;
	
	/**
	 * 任务实例的编号信息
	 */
	private String jointInstanceId;
	
	/**
	 * 流程实例的编号信息
	 */
	private String arrangementInstanceId;
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.ICommand#init()
	 */
	@Override
	public void init() throws BizException {
		
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.IBeforeEventCommand#init(java.lang.String, java.lang.String)
	 */
	@Override
	public void init(String jointInstanceId, String arrangementInstanceId) throws BizException {
		this.jointInstanceId = jointInstanceId;
		this.arrangementInstanceId = arrangementInstanceId;
		this.init();
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.ICommand#execute()
	 */
	@Override
	public void execute() throws BizException {
		//1、================验证
		ArrangementFlowEventSender arrangementFlowEventSender = this.eventSendAbstractFactory.createArrangementFlowEvent();
		InstanceDAOService instanceDAOService = this.arrangementDAOAbstractFactory.getInstanceDAOService();
		JSONObject jointInstanceObject = instanceDAOService.queryJointInstancesByJointInstanceID(this.jointInstanceId);
		if(jointInstanceObject == null) {
			throw new BizException("没有发现指定的任务实例，请检查", ResponseCode._403);
		}
		JSONObject arrangementInstanceObject = instanceDAOService.queryArrangementInstanceByArrangementInstanceID(this.arrangementInstanceId);
		if(arrangementInstanceObject == null || !arrangementInstanceObject.has("uid")) {
			throw new BizException("没有发现指定的流程实例，请检查", ResponseCode._403);
		}
		String arrangementuid = arrangementInstanceObject.getString("arrangementuid");
		
		//2、================开始赋值
		FlowEvent flowEvent = new FlowEvent();
		//arrangementuid
		flowEvent.setArrangementId(arrangementuid);
		//arrangementInstanceuid
		flowEvent.setArrangementInstanceId(this.arrangementInstanceId);
		//absOffsettime
		Long absOffsettime = jointInstanceObject.has("absOffsettime")?jointInstanceObject.getLong("absOffsettime"):null;
		flowEvent.setAbsOffsettime(absOffsettime);
		//promptOffsettime
		String promptOffsettime = jointInstanceObject.has("promptOffsettime")?jointInstanceObject.getString("promptOffsettime"):null;
		flowEvent.setPromptOffsettime(promptOffsettime);
		//relateOffsettime
		Long relateOffsettime = jointInstanceObject.has("relateOffsettime")?jointInstanceObject.getLong("relateOffsettime"):null;
		flowEvent.setRelateOffsettime(relateOffsettime);
		//expectedExeTime
		Long expectedExeTime = jointInstanceObject.has("expectedExeTime")?jointInstanceObject.getLong("expectedExeTime"):null;
		flowEvent.setExpectedExeTime(expectedExeTime);
		//jointId
		String jointId = jointInstanceObject.getString("jointuid");
		flowEvent.setJointId(jointId);
		//jointInstanceId
		flowEvent.setJointInstanceId(this.jointInstanceId);
		//expandTypeId
		Integer expandTypeId = jointInstanceObject.has("expandTypeId")?jointInstanceObject.getInt("expandTypeId"):null;
		flowEvent.setExpandTypeId(expandTypeId);
		//executor
		String executor = jointInstanceObject.has("executor")?jointInstanceObject.getString("executor"):null;
		flowEvent.setExecutor(executor);
		//creator
		String creator = jointInstanceObject.has("creator")?jointInstanceObject.getString("creator"):null;
		flowEvent.setCreator(creator);
		//offsetVisible 
		String offsetVisible = jointInstanceObject.has("offsetVisible")?jointInstanceObject.getString("offsetVisible"):null;
		flowEvent.setOffsetVisible(offsetVisible);
		//EventType
		flowEvent.setEventType(EventType.BEFOREFLOW);
		
		//======设置上下文
		JSONArray arrangementInstanceContexts = instanceDAOService.queryContextParamByArrangementInstanceId(this.arrangementInstanceId);
		if(arrangementInstanceContexts == null || arrangementInstanceContexts.isEmpty()) {
			flowEvent.setArrangementInstanceContext(null);
		} else {
			Set<InstanceContextParamEntity> params = new HashSet<InstanceContextParamEntity>();
			for(int index = 0 ; index < arrangementInstanceContexts.size() ; index++) {
				JSONObject arrangementInstanceItem = arrangementInstanceContexts.getJSONObject(index);
				String uid = arrangementInstanceItem.getString("uid");
				String name = arrangementInstanceItem.getString("name");
				String type = arrangementInstanceItem.getString("type");
				String nowValue = arrangementInstanceItem.has("nowValue")?arrangementInstanceItem.getString("nowValue"):null;
				String displayType = arrangementInstanceItem.has("displayType")?arrangementInstanceItem.getString("displayType"):null;
				String displayName = arrangementInstanceItem.has("displayName")?arrangementInstanceItem.getString("displayName"):null;
				
				InstanceContextParamEntity instanceContextParam = new InstanceContextParamEntity();
				instanceContextParam.setDisplayName(displayName);
				instanceContextParam.setDisplayType(displayType);
				instanceContextParam.setName(name);
				instanceContextParam.setType(type);
				instanceContextParam.setUid(uid);
				instanceContextParam.setNowValue(nowValue);
				
				params.add(instanceContextParam);
			}
			flowEvent.setArrangementInstanceContext(params);
		}
		
		//发送
		arrangementFlowEventSender.senderFlowEvent(flowEvent);
	}

	@Override
	public void undo() throws BizException {
		//1、================验证
		ArrangementExceptionEventSender arrangementExceptionEventSender = this.eventSendAbstractFactory.createNewExceptionEvent();
		InstanceDAOService instanceDAOService = this.arrangementDAOAbstractFactory.getInstanceDAOService();
		JSONObject jointInstanceObject = instanceDAOService.queryJointInstancesByJointInstanceID(this.jointInstanceId);
		if(jointInstanceObject == null) {
			throw new BizException("没有发现指定的任务实例，请检查", ResponseCode._403);
		}
		JSONObject arrangementInstanceObject = instanceDAOService.queryArrangementInstanceByArrangementInstanceID(this.arrangementInstanceId);
		if(arrangementInstanceObject == null || !arrangementInstanceObject.has("uid")) {
			throw new BizException("没有发现指定的流程实例，请检查", ResponseCode._403);
		}
		String arrangementuid = arrangementInstanceObject.getString("arrangementuid");
		
		//2、================开始赋值
		ExceptionEvent exceptionEvent = new ExceptionEvent();
		//arrangementuid
		exceptionEvent.setArrangementId(arrangementuid);
		//arrangementInstanceuid
		exceptionEvent.setArrangementInstanceId(arrangementInstanceId);
		//absOffsettime
		Long absOffsettime = jointInstanceObject.has("absOffsettime")?jointInstanceObject.getLong("absOffsettime"):null;
		exceptionEvent.setAbsOffsettime(absOffsettime);
		//promptOffsettime
		String promptOffsettime = jointInstanceObject.has("promptOffsettime")?jointInstanceObject.getString("promptOffsettime"):null;
		exceptionEvent.setPromptOffsettime(promptOffsettime);
		//relateOffsettime
		Long relateOffsettime = jointInstanceObject.has("relateOffsettime")?jointInstanceObject.getLong("relateOffsettime"):null;
		exceptionEvent.setRelateOffsettime(relateOffsettime);
		//expectedExeTime
		Long expectedExeTime = jointInstanceObject.has("expectedExeTime")?jointInstanceObject.getLong("expectedExeTime"):null;
		exceptionEvent.setExpectedExeTime(expectedExeTime);
		//jointId
		String jointId = jointInstanceObject.getString("jointuid");
		exceptionEvent.setJointId(jointId);
		//jointInstanceId
		exceptionEvent.setJointInstanceId(jointInstanceId);
		//expandTypeId
		Integer expandTypeId = jointInstanceObject.has("expandTypeId")?jointInstanceObject.getInt("expandTypeId"):null;
		exceptionEvent.setExpandTypeId(expandTypeId);
		//executor
		String executor = jointInstanceObject.has("executor")?jointInstanceObject.getString("executor"):null;
		exceptionEvent.setExecutor(executor);
		//creator
		String creator = jointInstanceObject.has("creator")?jointInstanceObject.getString("creator"):null;
		exceptionEvent.setCreator(creator);
		//offsetVisible 
		String offsetVisible = jointInstanceObject.has("offsetVisible")?jointInstanceObject.getString("offsetVisible"):null;
		exceptionEvent.setOffsetVisible(offsetVisible);
		//EventType
		exceptionEvent.setEventType(EventType.EXCEPTION);
		exceptionEvent.setExceptionMess("IBeforeEventCommand-Exception");
		
		//======设置上下文
		JSONArray arrangementInstanceContexts = instanceDAOService.queryContextParamByArrangementInstanceId(this.arrangementInstanceId);
		if(arrangementInstanceContexts == null || arrangementInstanceContexts.isEmpty()) {
			exceptionEvent.setArrangementInstanceContext(null);
		} else {
			Set<InstanceContextParamEntity> params = new HashSet<InstanceContextParamEntity>();
			for(int index = 0 ; index < arrangementInstanceContexts.size() ; index++) {
				JSONObject arrangementInstanceItem = arrangementInstanceContexts.getJSONObject(index);
				String uid = arrangementInstanceItem.getString("uid");
				String name = arrangementInstanceItem.getString("name");
				String type = arrangementInstanceItem.getString("type");
				String nowValue = arrangementInstanceItem.has("nowValue")?arrangementInstanceItem.getString("nowValue"):null;
				String displayType = arrangementInstanceItem.has("displayType")?arrangementInstanceItem.getString("displayType"):null;
				String displayName = arrangementInstanceItem.has("displayName")?arrangementInstanceItem.getString("displayName"):null;
				
				InstanceContextParamEntity instanceContextParam = new InstanceContextParamEntity();
				instanceContextParam.setDisplayName(displayName);
				instanceContextParam.setDisplayType(displayType);
				instanceContextParam.setName(name);
				instanceContextParam.setType(type);
				instanceContextParam.setUid(uid);
				instanceContextParam.setNowValue(nowValue);
				
				params.add(instanceContextParam);
			}
			exceptionEvent.setArrangementInstanceContext(params);
		}
		
		//发送
		arrangementExceptionEventSender.senderExceptionEvent(exceptionEvent);
	}
}