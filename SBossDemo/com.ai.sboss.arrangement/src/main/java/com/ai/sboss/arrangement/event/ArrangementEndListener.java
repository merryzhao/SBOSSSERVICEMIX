package com.ai.sboss.arrangement.event;

import com.ai.sboss.arrangement.exception.BizException;

/**
 * 流程结束事件的事件监听接口
 * @author yinwenjie
 *
 */
public interface ArrangementEndListener {
	/**
	 * 当某个流程正常完成业务流转时，这个方法会被触发。
	 * @param event
	 * @throws BizException
	 */
	public void onArrangementEnd(EndEvent event) throws BizException;
}
