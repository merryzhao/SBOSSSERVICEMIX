package com.ai.sboss.arrangement.engine;

import java.util.Map;

import com.ai.sboss.arrangement.exception.BizException;

/**
 * 任务实例流转命令的接口。
 * @author yinwenjie
 */
public interface IFlowCommand extends ICommand {	
	/**
	 * 初始化流转命令
	 * @param jointInstanceId 任务实例的编号信息 
	 * @param arrangementInstanceId 流程实例的编号信息 
	 * @param executor 命令的执行者
	 * @param propertiesValues 本次流转操作时，从外部API接口传入的操作人员指定的参数值
	 */
	public void init(String jointInstanceId , String arrangementInstanceId , String executor , Map<String, Object> propertiesValues) throws BizException;
}