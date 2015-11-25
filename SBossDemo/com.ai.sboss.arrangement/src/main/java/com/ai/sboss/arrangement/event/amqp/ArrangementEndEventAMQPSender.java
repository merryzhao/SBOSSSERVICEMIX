package com.ai.sboss.arrangement.event.amqp;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import com.ai.sboss.arrangement.event.ArrangementEndEventSender;
import com.ai.sboss.arrangement.event.EndEvent;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;
import com.ai.sboss.arrangement.utils.JSONUtils;

/**
 * “业务流程正常结束流转”事件发送器的一个AMQP实现
 * @author yinwenjie
 */
public class ArrangementEndEventAMQPSender implements ArrangementEndEventSender {
	
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
	 * @see com.ai.sboss.arrangement.event.ArrangementEndEventSender#senderEndEvent(com.ai.sboss.arrangement.event.EndEvent)
	 */
	@Override
	public void senderEndEvent(EndEvent event) throws BizException {
		//发送到AMQP消息队列：转成json，然后直接发送
		if(event == null) {
			throw new BizException("EndEvent不能为null，请检查", ResponseCode._404);
		}
		
		String eventContext = JSONUtils.toString(event);
		byte[] bodyBytes = eventContext.getBytes();
		
		Message amqpMessage = new Message(bodyBytes, new MessageProperties());
		//因为在配置文件中，已经制定了route-key，所以这里直接提交内容就行了。
		this.amqpTemplate.send(amqpMessage);
	}
}