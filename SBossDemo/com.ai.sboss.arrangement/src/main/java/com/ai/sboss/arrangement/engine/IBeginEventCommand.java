package com.ai.sboss.arrangement.engine;

import com.ai.sboss.arrangement.exception.BizException;

/**
 * @author yinwenjie
 */
public interface IBeginEventCommand extends ICommand  {
	/**
	 * 初始化开始命令
	 * @param arrangementInstanceId 流程实例的编号信息 
	 */
	public void init(String arrangementInstanceId) throws BizException;
}