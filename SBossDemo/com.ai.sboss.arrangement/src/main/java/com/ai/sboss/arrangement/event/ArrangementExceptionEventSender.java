package com.ai.sboss.arrangement.event;

import com.ai.sboss.arrangement.exception.BizException;

/**
 * 异常事件发送器的接口定义
 * @author yinwenjie
 *
 */
public interface ArrangementExceptionEventSender { 
	/**
	 * 向一个具体的事件通知实现机制（例如像本地的事件“生产者-消费者”机制，像消息队列服务，向远程缓存系统）发送“流转异常事件”通知
	 * @param event 发送的“流转异常”事件
	 */
	public void senderExceptionEvent(ExceptionEvent event) throws BizException;
}