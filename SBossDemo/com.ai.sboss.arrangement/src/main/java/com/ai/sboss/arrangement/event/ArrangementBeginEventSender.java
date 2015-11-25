package com.ai.sboss.arrangement.event;

import com.ai.sboss.arrangement.exception.BizException;

/**
 * 流程实例开始事件的事件发送器接口定义
 * @author yinwenjie
 *
 */
public interface ArrangementBeginEventSender {
	/**
	 * 向一个具体的事件通知实现机制（例如像本地的事件“生产者-消费者”机制，像消息队列服务，向远程缓存系统）发送“开始事件”通知
	 * @param event 发送的“业务流转开始”事件
	 */
	public void senderBeginEvent(BeginEvent event) throws BizException;
}
