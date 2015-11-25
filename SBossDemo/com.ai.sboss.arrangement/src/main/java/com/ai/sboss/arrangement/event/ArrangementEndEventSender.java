package com.ai.sboss.arrangement.event;

import com.ai.sboss.arrangement.exception.BizException;

/**
 * 流程实例正常结束事件的事件发送器定义
 * @author yinwenjie
 *
 */
public interface ArrangementEndEventSender {
	/**
	 * 向一个具体的事件通知实现机制（例如像本地的事件“生产者-消费者”机制，像消息队列服务，向远程缓存系统）发送“正常结束事件”通知
	 * @param event 发送的“流程正常结束”事件
	 */
	public void senderEndEvent(EndEvent event) throws BizException;
}
