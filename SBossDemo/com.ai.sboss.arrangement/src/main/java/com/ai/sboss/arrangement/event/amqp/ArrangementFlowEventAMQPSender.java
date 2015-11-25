package com.ai.sboss.arrangement.event.amqp;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import com.ai.sboss.arrangement.event.ArrangementFlowEventSender;
import com.ai.sboss.arrangement.event.EventType;
import com.ai.sboss.arrangement.event.FlowEvent;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;
import com.ai.sboss.arrangement.utils.JSONUtils;

/**
 * “业务流程正常流转”事件发送器的一个AMQP实现
 * @author yinwenjie
 */
public class ArrangementFlowEventAMQPSender implements ArrangementFlowEventSender {
	
	/**
	 * AMQP功能模板
	 */
	private AmqpTemplate amqpTemplate;
	
	public AmqpTemplate getAmqpTemplate() {
		return amqpTemplate;
	}

	public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.ArrangementFlowEventSender#senderFlowEvent(com.ai.sboss.arrangement.event.FlowEvent)
	 */
	@Override
	public void senderFlowEvent(FlowEvent event) throws BizException {
		//发送到AMQP消息队列：转成json，然后直接发送
		if(event == null) {
			throw new BizException("FlowEvent不能为null，请检查", ResponseCode._404);
		}
		if(event.getEventType() == null) {
			throw new BizException("FlowEvent-EventType不能为null，请检查", ResponseCode._404);
		}
		if(event.getEventType() != EventType.AFTERFLOW && event.getEventType() != EventType.BEFOREFLOW) {
			throw new BizException("FlowEvent-EventType只能为两个值（AFTERFLOW | BEFOREFLOW），请检查", ResponseCode._404);
		}
		
		//为了适应多个系统，转换成json（不用担心数据层事务的问题，已经专门处理了）
		String eventContext = JSONUtils.toString(event);
		byte[] bodyBytes = eventContext.getBytes();
		
		Message amqpMessage = new Message(bodyBytes, new MessageProperties());
		//因为在配置文件中，已经制定了route-key，所以这里直接提交内容就行了。
		this.amqpTemplate.send(amqpMessage);
	}
}