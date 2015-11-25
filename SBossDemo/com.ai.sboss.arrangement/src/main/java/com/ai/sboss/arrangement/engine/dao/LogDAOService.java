package com.ai.sboss.arrangement.engine.dao;

import com.ai.sboss.arrangement.exception.BizException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 该接口向模块外部提供关于流程日志相关的持久层服务
 * （包括流程实例/任务实例 流程日志、流程实例上下文日志）
 * @author yinwenjie
 */
public interface LogDAOService {
	/**
	 * 按照流转日志的id，查询本次流转时，上下文中参数值的变化日志
	 * @param jointInstanceFlowLogId 指定的流传日志编号
	 * @return 
	 * @throws BizException
	 */
	public JSONArray queryInstanceContextParamLogByFlowLog(String jointInstanceFlowLogId) throws BizException;
	
	/**
	 * 按照流转日志id，查询流转日志的基本信息。并不包括流程实例日志对应的上下文变化日志
	 * @param jointInstanceFlowLogId 置顶的流转日志编号
	 * @return
	 * @throws BizException
	 */
	public JSONObject queryJointInstanceFlowLogById(String jointInstanceFlowLogId) throws BizException;
	
	/**
	 * 查询指定的任务实例最后一次正向流转的日志。这种查询主要是为了进行逆向流转时，查询之前那次正向流转的日志信息。<br>
	 * 所以传入的jointInstanceId将作为formJointInstanceId的查询条件。
	 * @param jointInstanceId 指定的任务实例编号
	 * @return
	 * @throws BizException
	 */
	public JSONObject queryLastForwardFlowLog(String jointInstanceId) throws BizException;
}