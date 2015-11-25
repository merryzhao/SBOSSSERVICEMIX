package com.ai.sboss.arrangement.engine;

import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.dao.ArrangementDAOAbstractFactory;
import com.ai.sboss.arrangement.engine.dao.InstanceDAOService;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * 编排系统中，流程引擎实现 流程流转服务的唯一实现类
 * @author yinwenjie
 */
@Component("_processorEngine_streamingControlServiceImpl")
public class StreamingControlServiceImpl extends AbstractCommadQueueManager implements IStreamingControlService {

	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(StreamingControlServiceImpl.class);
	
	@Autowired
	private IBeforeEventCommand beforeEventCommand;
	
	@Autowired
	private IAfterEventCommand afterEventCommand;
	
	@Autowired
	private IFlowCommand flowCommand;
	
	@Autowired
	private ArrangementDAOAbstractFactory arrangementDAOAbstractFactory;
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.IStreamingControlService#executeFlowByBusinessid(java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public JSONObject executeFlowByBusinessid(String businessid, String executor, Map<String, Object> properties) throws BizException {
		if(StringUtils.isEmpty(businessid) || StringUtils.isEmpty(executor)) {
			throw new BizException("businessid和executor必须传入，请检查！", ResponseCode._403);
		}
		
		InstanceDAOService instanceDAOService = this.arrangementDAOAbstractFactory.getInstanceDAOService();
		JSONArray jointInstanceObjects = instanceDAOService.queryJointInstancesByBusinessID(businessid);
		
		if(jointInstanceObjects == null || jointInstanceObjects.isEmpty()) {
			throw new BizException("没有发现这个业务编号匹配的任务实例集合，可能是因为这个业务编号并不存在", ResponseCode._502);
		}
		
		// 寻找状态为executing的任务实例状态
		String statu = null;
		String jointInstanceid = null;
		for(int index = 0 ; index < jointInstanceObjects.size() ; index++) {
			JSONObject jointInstanceObject = jointInstanceObjects.getJSONObject(index);
			statu = jointInstanceObject.getString("statu");
			jointInstanceid = jointInstanceObject.getString("uid");
			
			if(StringUtils.equals(statu, "executing")) {
				break;
			}
		}
		//没有寻找到executing状态的任务实例
		if(StringUtils.isEmpty(jointInstanceid) || StringUtils.isEmpty(statu)) {
			throw new BizException("没有寻找到executing状态的任务实例", ResponseCode._502);
		}
		
		return this.executeFlowByJointInstanceid(jointInstanceid, executor, properties);
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.IStreamingControlService#executeJointByArrangementInstanceid(java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public JSONObject executeJointByArrangementInstanceid(String arrangementInstanceid, String executor,Map<String, Object> properties) throws BizException {
		if(StringUtils.isEmpty(arrangementInstanceid) || StringUtils.isEmpty(executor)) {
			throw new BizException("arrangementInstanceid和executor必须传入，请检查！", ResponseCode._403);
		}
		
		InstanceDAOService instanceDAOService = this.arrangementDAOAbstractFactory.getInstanceDAOService();
		JSONArray jointInstanceObjects = instanceDAOService.queryJointInstancesByArrangementInstanceID(arrangementInstanceid, "executing");
		if(jointInstanceObjects == null || jointInstanceObjects.isEmpty()) {
			throw new BizException("没有发现这个业务编号匹配的任务实例集合，可能是因为这个流程实例并不存在", ResponseCode._502);
		}
		
		// 寻找状态为executing的任务实例状态
		String statu = null;
		String jointInstanceid = null;
		for(int index = 0 ; index < jointInstanceObjects.size() ; index++) {
			JSONObject jointInstanceObject = jointInstanceObjects.getJSONObject(index);
			statu = jointInstanceObject.getString("statu");
			jointInstanceid = jointInstanceObject.getString("uid");
			
			if(StringUtils.equals(statu, "executing")) {
				break;
			}
		}
		//没有寻找到executing状态的任务实例
		if(StringUtils.isEmpty(jointInstanceid) || StringUtils.isEmpty(statu)) {
			throw new BizException("没有寻找到executing状态的任务实例", ResponseCode._502);
		}
		
		return this.executeFlowByJointInstanceid(jointInstanceid, executor, properties);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.IStreamingControlService#executeJointByJointInstanceid(java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public JSONObject executeFlowByJointInstanceid(String jointInstanceid, String executor,Map<String, Object> properties) throws BizException {
		/*
		 * 这里描述了整个任务实例流转全过程的处理方式：
		 * 1、查找：根据jointInstanceid查询得到任务实例、关联的流程实例、流转状态的等实例的基本信息
		 * 
		 * 2、验证：基础信息能够取出只能说明传入的jointInstanceid是正确的，并不能说明这个jointInstance在当前是可以执行的，一个jointInstance能够执行要满足以下条件：
		 * 		a、当前jointInstance的statu为executing状态
		 * 		b、当前jointInstance所必须的入参，都能够拿到值（要么来自这个方法传入的，要么来自数据层记录的流程实例上下文的）
		 * 		c、当前jointInstance的“指定执行者”，和这个方法传入的executor是一致的
		 * 
		 * 3、准备数据：完成了以上两步工作，都是正确的，那么将这个jointInstance所需的入参都和基本属性取出来，以便后面使用
		 * 
		 * ===============================================
		 * 完成以上3步准备工作，就可以开始进行流程流转了：
		 * 
		 * 4、调用IBeforeEventCommand发送任务实例执行前的事件消息，到事件队列中
		 * 
		 * 5、调用IFlowCommand进行任务实例流转，并且等待返回的结果（一般都是从底层camel得到处理结果），
		 *    记得更改数据层，当前任务实例的各种状态和属性数据哦。。。
		 * 
		 * 6、如果整个操作过程是正常的，那么调用afterEventCommand，发送执行情况
		 * 	    如果整个过程存在异常，那么调用afterEventCommand，发送异常情况。并且使用命令模式逆向执行命令
		 * ===============================================
		 * 
		 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		 * 以下的代码为“递归”，专门为了处理“后续任务实例是‘自动流程’的情况准备的”
		 * 
		 * 7、正确执行完成后，查询当前处于“executing”状态的任务实例。
		 * 如果确定它是一个“自动流程”那么要继续执行了
		 * 这里要注意回退原则，正常执行完成的，就不会再回退了
		 * 
		 * */
		if(StringUtils.isEmpty(jointInstanceid)) {
			throw new BizException("jointInstanceid必须传入，请检查！", ResponseCode._403);
		}
		
		//1、==========================
		InstanceDAOService instanceDAOService = this.arrangementDAOAbstractFactory.getInstanceDAOService();
		JSONObject jointInstanceObject = instanceDAOService.queryJointInstancesByJointInstanceID(jointInstanceid);
		//如果条件成立，说明并没有查到记录
		if(jointInstanceObject == null || !jointInstanceObject.has("uid")) {
			throw new BizException("没有发现jointInstanceid对应的任务实例，请检查！", ResponseCode._404);
		}
		String arrangementInstanceuid = jointInstanceObject.getString("arrangementInstanceuid");
		if(StringUtils.isEmpty(arrangementInstanceuid)) {
			throw new BizException("没有发现jointInstanceid对应的流程实例，请检查！", ResponseCode._404);
		}
		JSONObject arrangementInstanceObject = instanceDAOService.queryArrangementInstanceByArrangementInstanceID(arrangementInstanceuid);
		String arrangementInstanceStatu = arrangementInstanceObject.getString("statu");
		String jointInstanceuid = jointInstanceObject.getString("uid");
		String jointInstanceStatu = jointInstanceObject.getString("statu");
		String jointInstanceExecutor = jointInstanceObject.getString("executor");
		
		//2、==========================验证
		if(StringUtils.isEmpty(jointInstanceuid) || !StringUtils.equals(jointInstanceStatu, "executing")) {
			throw new BizException("当前任务实例的状态并非“executing”，不能被执行。请检查", ResponseCode._502);
		}
		if(!StringUtils.isEmpty(jointInstanceExecutor) && !StringUtils.equals(jointInstanceExecutor, executor)) {
			throw new BizException("任务实例设置的“执行者”，和当前“执行者”不匹配。请检查", ResponseCode._502);
		}
		if(StringUtils.isEmpty(arrangementInstanceStatu) || (!StringUtils.equals(arrangementInstanceStatu, "executing")
			&& !StringUtils.equals(arrangementInstanceStatu, "waiting"))) {
			throw new BizException("当前流程实例的状态并非“executing | waiting”，不能被执行。请检查", ResponseCode._502);
		}
		
		//3、==========================准备命令集合
		this.beforeEventCommand.init(jointInstanceuid, arrangementInstanceuid);
		this.flowCommand.init(jointInstanceuid, arrangementInstanceuid, executor, properties);
		this.afterEventCommand.init(jointInstanceuid, arrangementInstanceuid);
		this.initCommandsQueue(this.beforeEventCommand , this.flowCommand , this.afterEventCommand);
		
		//4、5、6、==========================开始
		ICommand nowCommand = null;
		Boolean success = true;
		BizException errorException = null;
		while((nowCommand = this.nextCommand()) != null) {
			try {
				nowCommand.execute();
			} catch(BizException be) {
				StreamingControlServiceImpl.LOGGER.warn(be.getMessage(), be);
				success = false;
				errorException = be;
				break;
			}
		}
		//开始进行逆向操作
		if(!success) {
			while((nowCommand = this.nextUndoCommand()) != null) {
				try {
					nowCommand.undo();
				} catch(BizException be) {
					StreamingControlServiceImpl.LOGGER.warn(be.getMessage(), be);
					success = false;
					break;
				}
			}
		}
		
		//7、========================“自动流程”，继续执行，直到全部正常结束或者某一个失败
		JSONArray executingJointInstances = instanceDAOService.queryJointInstancesByArrangementInstanceID(arrangementInstanceuid, "executing");
		try {
			if(success && executingJointInstances != null && !executingJointInstances.isEmpty()) {
				for(int index = 0 ; index < executingJointInstances.size() ; index++) {
					JSONObject nextJointInstance = executingJointInstances.getJSONObject(index);
				 	String nextJointInstanceid = nextJointInstance.getString("uid");
				 	String nextExecutor = nextJointInstance.has("executor")?nextJointInstance.getString("executor"):null;
				 	// 如果条件成立，说明是自动流程
				 	if(StringUtils.isEmpty(nextExecutor)) {
				 		this.executeFlowByJointInstanceid(nextJointInstanceid, null, properties);
				 	}
				}
			}
		} catch(BizException be) {
			StreamingControlServiceImpl.LOGGER.warn(be.getMessage(), be);
			success = false;
			errorException = be;
		}
		
		//8、========================准备进行返回
		if(errorException != null && !success) {
			throw errorException;
		}
		return instanceDAOService.queryJointInstancesByJointInstanceID(jointInstanceuid);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.IStreamingControlService#executeUNFlowByJointInstanceid(java.lang.String, java.lang.String)
	 */
	@Override
	public JSONObject executeUNFlowByJointInstanceid(String jointInstanceid,String executor) throws BizException {
		//执行过程，参见JSONObject com.ai.sboss.arrangement.engine.StreamingControlServiceImpl.executeFlowByJointInstanceid方法
		if(StringUtils.isEmpty(jointInstanceid) || StringUtils.isEmpty(executor)) {
			throw new BizException("jointInstanceid和executor必须传入，请检查！", ResponseCode._403);
		}
		
		//1、==========================
		InstanceDAOService instanceDAOService = this.arrangementDAOAbstractFactory.getInstanceDAOService();
		JSONObject jointInstanceObject = instanceDAOService.queryJointInstancesByJointInstanceID(jointInstanceid);
		//如果条件成立，说明并没有查到记录
		if(jointInstanceObject == null || !jointInstanceObject.has("uid")) {
			throw new BizException("没有发现jointInstanceid对应的任务实例，请检查！", ResponseCode._404);
		}
		String arrangementInstanceuid = jointInstanceObject.getString("arrangementInstanceuid");
		if(StringUtils.isEmpty(arrangementInstanceuid)) {
			throw new BizException("没有发现jointInstanceid对应的流程实例，请检查！", ResponseCode._404);
		}
		
		//3、==========================准备命令集合
		this.beforeEventCommand.init(jointInstanceid, arrangementInstanceuid);
		this.flowCommand.init(jointInstanceid, arrangementInstanceuid, executor, null);
		this.afterEventCommand.init(jointInstanceid, arrangementInstanceuid);
		this.initCommandsQueue(this.beforeEventCommand , this.flowCommand , this.afterEventCommand);
		
		//开始进行逆向操作
		ICommand nowCommand = null;
		this.peekLastDONOTReturn();
		while((nowCommand = this.nextUndoCommand()) != null) {
			try {
				nowCommand.undo();
			} catch(BizException be) {
				StreamingControlServiceImpl.LOGGER.warn(be.getMessage(), be);
				break;
			}
		}
		
		//========================准备进行返回
		return instanceDAOService.queryJointInstancesByJointInstanceID(jointInstanceid);
	}
}