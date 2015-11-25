package com.ai.sboss.arrangement.event;

import com.ai.sboss.arrangement.exception.BizException;

/**
 * 业务流转开始事件的事件监听接口
 * @author yinwenjie
 *
 */
public interface ArrangementBeginListener {
	/**
	 * 当某个流程开始流转是（被实例化时），这个方法会被触发。一般情况下，是操作者调用了流程的instance方法
	 * @param event
	 * @throws BizException 
	 */
	public void onArrangementBegin(BeginEvent event) throws BizException;
}
