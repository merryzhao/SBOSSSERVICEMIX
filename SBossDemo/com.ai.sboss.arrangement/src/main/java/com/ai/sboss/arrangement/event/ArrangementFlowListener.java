package com.ai.sboss.arrangement.event;

import com.ai.sboss.arrangement.exception.BizException;

/**
 * 业务流转过程事件的事件监听接口
 * @author yinwenjie
 *
 */
public interface ArrangementFlowListener {
	/**
	 * 当某个流程进行流转前，这个方法被触发。这个事件一般发生在流程中上一个任务结束时
	 * @param event 
	 * @throws BizException 
	 */
	public void onFlowBegin(FlowEvent event) throws BizException;
	
	/**
	 * @param event
	 * @throws BizException
	 */
	public void onFlowEnd(FlowEvent event) throws BizException;
}
