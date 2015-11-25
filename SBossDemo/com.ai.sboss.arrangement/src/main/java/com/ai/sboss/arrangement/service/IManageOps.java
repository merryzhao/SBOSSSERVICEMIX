package com.ai.sboss.arrangement.service;

import java.util.Map;

import com.ai.sboss.arrangement.entity.JsonEntity;

import net.sf.json.JSONObject;

/**
 * 使用（实例化）业务流程实例操作相关的接口服务.
 * @author Chaos
 * @author yinwenjie
 */
public interface IManageOps {
	/**
	 * 提交保存行业流程模板.
	 * @param process 
	 * @return 
	 */
	public JsonEntity commitProcess(String process);

	/**
	 * 使能相应的流程.
	 * @param processId 
	 * @return 
	 */
	public JsonEntity activateProcess(Object processId);

	/**
	 * 下架相应的流程.
	 * @param processId 
	 * @return 
	 */
	public JsonEntity invalidProcess(Object processId);
	
	/**
	 * 正向执行指定的任务实例（按照流程实例所绑定的业务编号）。并且返回响应的结果
	 * @param businessid 流程实例所绑定的业务编号
	 * @param executor 当前的执行人，这个执行人必须与任务实例预定的执行人一致
	 * @param properties 如果在执行这个任务实例的过程中，需要传入特定的值，则可以在这里传入。注意这些特定值不会计入流程实例上下文中
	 * @return 
	 */
	public JsonEntity executeFlowByBusinessid(String businessid, String executor , Map<String, Object> properties);
	
	/**
	 * 正向执行指定的任务实例编号。并且返回响应的结果
	 * @param arrangementInstanceid 
	 * @param executor 当前的执行人，这个执行人必须与任务实例预定的执行人一致
	 * @param properties 如果在执行这个任务实例的过程中，需要传入特定的值，则可以在这里传入。注意这些特定值不会计入流程实例上下文中
	 * @return
	 */
	public JsonEntity executeFlowByArrangementInstanceid(String arrangementInstanceid, String executor , Map<String, Object> properties);
	
	/**
	 * 正向执行指定的任务实例。并且返回响应的结果
	 * @param jointInstanceid 本次执行的任务实例编号；注意当前任务实例的状态必须是executing状态
	 * @param executor 当前的执行人，这个执行人必须与任务实例预定的执行人一致
	 * @param properties 如果在执行这个任务实例的过程中，需要传入特定的值，则可以在这里传入。注意这些特定值不会计入流程实例上下文中
	 * @return 
	 */
	public JsonEntity executeFlowByJointInstanceid(String jointInstanceid, String executor , Map<String, Object> properties);
	
	/**
	 * 逆向执行任务实例到上一个任务实例。注意当前的任务实例必须是completed状态，且这个任务的下一个任务必须是executing。只有这种情况能够回退
	 * @param jointInstanceid 指定的需要进行回退的任务实例编号
	 * @param executor  当前的执行人，这个执行人必须与任务实例预定的执行人一致
	 * @return 
	 */
	public JsonEntity executeUNFlowByJointInstanceid(String jointInstanceid,String executor);
	
	/**
	 * 启动流程。
	 * @param arrangementInstanceJson 以外部传入的符合arrangementInstance-jso规范的数据为依据
	 * @return 
	 */
	public JsonEntity startUpArrangementByArrangmentid(JSONObject arrangementInstanceJson);
}