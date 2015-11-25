package com.ai.sboss.arrangement.event;

import com.ai.sboss.arrangement.exception.BizException;

/**
 * 流程实例发生“下一步”流转事件的事件发送器接口定义
 * @author yinwenjie
 *
 */
public interface ArrangementFlowEventSender { 
	/**
	 * 向一个具体的事件通知实现机制（例如像本地的事件“生产者-消费者”机制，像消息队列服务，向远程缓存系统）发送“正常流转事件”通知
	 * @param event 发送的“正常流转”事件，其中的事件类型（包括“流转开始前”、“流转结束”）是一定要说明的
	 */
	public void senderFlowEvent(FlowEvent event) throws BizException;
}
