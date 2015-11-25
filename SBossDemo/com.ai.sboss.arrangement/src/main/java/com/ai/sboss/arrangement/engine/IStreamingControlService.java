package com.ai.sboss.arrangement.engine;

import java.util.Map;

import net.sf.json.JSONObject;

import com.ai.sboss.arrangement.exception.BizException;

/**
 * “实例正向流转业务”，命令控制器需要实现的接口<br>
 * 在整个编排系统中，“实例正向流转业务”只可能有一个控制器的具体实现。
 * 这里安排IStreamingControlService接口的原因，完全是为了准守spring中定义与实现分离的Ioc容器规范
 * @author yinwenjie
 *
 */
public interface IStreamingControlService {
	
	/**
	 * 按照指定的流程实例实例所绑定的业务编号（businessid），对当前正处于最前列“waiting”任务实例，进行流转。<br>
	 * 如果这时，任务实例所需要的必要的“入参”信息或者“执行者”不匹配，将会报出错误。
	 * @param businessid 流程实例所绑定的业务编号
	 * @param executor 当前的执行人，这个执行人必须与任务实例预定的执行人一致
	 * @param properties 如果在执行这个任务实例的过程中，需要传入特定的值，则可以在这里传入。注意这些特定值不会计入流程实例上下文中
	 * @return 
	 */
	public JSONObject executeFlowByBusinessid(String businessid, String executor , Map<String, Object> properties) throws BizException;
	
	/**
	 * 按照指定的流程实例编号，对当前正处于最前列“waiting”任务实例，进行流转。<br>
	 * 如果这时，任务实例所需要的必要的“入参”信息或者“执行者”不匹配，将会报出错误。
	 * @param arrangementInstanceid 指定的流程实例编号
	 * @param executor 当前任务实例的执行者
	 * @param properties 传入的入参信息，如果这里没有入参信息，引擎将去流程实例上下文中取得必要的入参数据
	 * @return 当前指定的任务实例的执行结果，将被返回给调用者
	 * @throws BizException
	 */
	public JSONObject executeJointByArrangementInstanceid(String arrangementInstanceid , String executor , Map<String, Object> properties) throws BizException; 
	
	/**
	 * 按照指定的任务实例编号，对任务实例，进行流转。<br>
	 * 如果这时 还轮不到 这个任务实例进行流转，或者任务实例所需要的必要的“入参”信息 又或者 “执行者”不匹配，将会报出错误。
	 * @param jointInstanceid 指定的任务实例编号
	 * @param executor 当前任务实例的执行者
	 * @param properties 传入的入参信息，如果这里没有入参信息，引擎将去流程实例上下文中取得必要的入参数据
	 * @return 当前指定的任务实例的执行结果，将被返回给调用者
	 * @throws BizException
	 */
	public JSONObject executeFlowByJointInstanceid(String jointInstanceid , String executor , Map<String, Object> properties) throws BizException;
	
	/**
	 * 当一个任务实例完成执行后（处于followed状态），并且下一个任务实例还没有完成执行（处于executing状态）<br>
	 * 有且只有这种情况下，这个任务实例（处于followed状态的）可以进行逆向执行
	 * @param jointInstanceid 要执行反向流转的任务实例编号
	 * @param executor 执行者
	 * @return 
	 * @throws BizException
	 */
	public JSONObject executeUNFlowByJointInstanceid(String jointInstanceid , String executor) throws BizException;
}