package com.ai.sboss.arrangement.event;

import com.ai.sboss.arrangement.exception.BizException;

/**
 * 业务流转异常事件的监听接口
 * @author yinwenjie
 *
 */
public interface ArrangementExceptionListener {
	/**
	 * 当某个流程流转出现异常时，这个方法会被触发。
	 * @param event
	 * @throws BizException
	 */
	public void onArrangementException(ExceptionEvent event) throws BizException;
}
