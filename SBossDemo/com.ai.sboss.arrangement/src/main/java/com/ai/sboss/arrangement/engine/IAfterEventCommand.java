package com.ai.sboss.arrangement.engine;

import com.ai.sboss.arrangement.exception.BizException;

/**
 * @author yinwenjie
 */
public interface IAfterEventCommand extends ICommand {
	/**
	 * 初始化流转命令
	 * @param jointInstanceId 任务实例的编号信息 
	 * @param arrangementInstanceId 流程实例的编号信息 
	 */
	public void init(String jointInstanceId , String arrangementInstanceId) throws BizException;
}
