package com.ai.sboss.arrangement.engine.local;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.IBeginEventCommand;
import com.ai.sboss.arrangement.engine.dao.ArrangementDAOAbstractFactory;
import com.ai.sboss.arrangement.engine.dao.InstanceDAOService;
import com.ai.sboss.arrangement.entity.orm.InstanceContextParamEntity;
import com.ai.sboss.arrangement.event.ArrangementBeginEventSender;
import com.ai.sboss.arrangement.event.ArrangementExceptionEventSender;
import com.ai.sboss.arrangement.event.BeginEvent;
import com.ai.sboss.arrangement.event.EventSendAbstractFactory;
import com.ai.sboss.arrangement.event.EventType;
import com.ai.sboss.arrangement.event.ExceptionEvent;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author yinwenjie
 */
@Component("_processorEngine_beginEventCommand")
public class BeginEventCommandLocalImpl implements IBeginEventCommand {

	@Autowired
	EventSendAbstractFactory eventSendAbstractFactory;

	@Autowired
	private ArrangementDAOAbstractFactory arrangementDAOAbstractFactory;

	/**
	 * 流程实例的编号信息
	 */
	private String arrangementInstanceId;

	@Override
	public void init() throws BizException {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(String arrangementInstanceId) throws BizException {
		this.arrangementInstanceId = arrangementInstanceId;
		this.init();
	}

	@Override
	public void execute() throws BizException {
		// 1、================验证
		ArrangementBeginEventSender arrangementBeginEventSender = this.eventSendAbstractFactory
				.createNewArrangementBeginEvent();
		InstanceDAOService instanceDAOService = this.arrangementDAOAbstractFactory.getInstanceDAOService();
		JSONObject arrangementInstanceJSON = instanceDAOService
				.queryArrangementInstanceByArrangementInstanceID(this.arrangementInstanceId);
		if (arrangementInstanceJSON == null || arrangementInstanceJSON.isEmpty()) {
			throw new BizException("没有发现指定的流程实例，请检查", ResponseCode._403);
		}

		// 2、================开始赋值
		BeginEvent beginEvent = new BeginEvent();
		beginEvent.setArrangementId(arrangementInstanceJSON.getString("arrangementuid"));
		beginEvent.setArrangementInstanceId(this.arrangementInstanceId);

		// ======设置上下文
		JSONArray arrangementInstanceContexts = instanceDAOService
				.queryContextParamByArrangementInstanceId(this.arrangementInstanceId);
		if (arrangementInstanceContexts == null || arrangementInstanceContexts.isEmpty()) {
			beginEvent.setArrangementInstanceContext(null);
		} else {
			Set<InstanceContextParamEntity> params = new HashSet<InstanceContextParamEntity>();
			for (int index = 0; index < arrangementInstanceContexts.size(); index++) {
				JSONObject arrangementInstanceItem = arrangementInstanceContexts.getJSONObject(index);
				String uid = arrangementInstanceItem.getString("uid");
				String name = arrangementInstanceItem.getString("name");
				String type = arrangementInstanceItem.getString("type");
				String nowValue = arrangementInstanceItem.has("nowValue")
						? arrangementInstanceItem.getString("nowValue") : null;
				String displayType = arrangementInstanceItem.has("displayType")
						? arrangementInstanceItem.getString("displayType") : null;
				String displayName = arrangementInstanceItem.has("displayName")
						? arrangementInstanceItem.getString("displayName") : null;

				InstanceContextParamEntity instanceContextParam = new InstanceContextParamEntity();
				instanceContextParam.setDisplayName(displayName);
				instanceContextParam.setDisplayType(displayType);
				instanceContextParam.setName(name);
				instanceContextParam.setType(type);
				instanceContextParam.setUid(uid);
				instanceContextParam.setNowValue(nowValue);

				params.add(instanceContextParam);
			}
			beginEvent.setArrangementInstanceContext(params);
		}
		// 发送
		arrangementBeginEventSender.senderBeginEvent(beginEvent);
	}

	@Override
	public void undo() throws BizException {
		// 1、================验证
		ArrangementExceptionEventSender arrangementExceptionEventSender = this.eventSendAbstractFactory
				.createNewExceptionEvent();
		InstanceDAOService instanceDAOService = this.arrangementDAOAbstractFactory.getInstanceDAOService();
		JSONObject arrangementInstanceObject = instanceDAOService
				.queryArrangementInstanceByArrangementInstanceID(this.arrangementInstanceId);
		if (arrangementInstanceObject == null || !arrangementInstanceObject.has("uid")) {
			throw new BizException("没有发现指定的流程实例，请检查", ResponseCode._403);
		}

		// 2、================开始赋值
		ExceptionEvent exceptionEvent = new ExceptionEvent();
		// arrangementuid
		String arrangementuid = arrangementInstanceObject.getString("arrangementuid");
		exceptionEvent.setArrangementId(arrangementuid);
		// arrangementInstanceuid
		exceptionEvent.setArrangementInstanceId(arrangementInstanceId);
		// EventType
		exceptionEvent.setEventType(EventType.EXCEPTION);
		exceptionEvent.setExceptionMess("IBeginEventCommand-Exception");

		// ======设置上下文
		JSONArray arrangementInstanceContexts = instanceDAOService
				.queryContextParamByArrangementInstanceId(this.arrangementInstanceId);
		if (arrangementInstanceContexts == null || arrangementInstanceContexts.isEmpty()) {
			exceptionEvent.setArrangementInstanceContext(null);
		} else {
			Set<InstanceContextParamEntity> params = new HashSet<InstanceContextParamEntity>();
			for (int index = 0; index < arrangementInstanceContexts.size(); index++) {
				JSONObject arrangementInstanceItem = arrangementInstanceContexts.getJSONObject(index);
				String uid = arrangementInstanceItem.getString("uid");
				String name = arrangementInstanceItem.getString("name");
				String type = arrangementInstanceItem.getString("type");
				String nowValue = arrangementInstanceItem.has("nowValue")
						? arrangementInstanceItem.getString("nowValue") : null;
				String displayType = arrangementInstanceItem.has("displayType")
						? arrangementInstanceItem.getString("displayType") : null;
				String displayName = arrangementInstanceItem.has("displayName")
						? arrangementInstanceItem.getString("displayName") : null;

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

		// 发送
		arrangementExceptionEventSender.senderExceptionEvent(exceptionEvent);
	}

}
