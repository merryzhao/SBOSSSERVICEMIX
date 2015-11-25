package com.ai.sboss.arrangement.event.amqp;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import com.ai.sboss.arrangement.event.ArrangementExceptionEventSender;
import com.ai.sboss.arrangement.event.ExceptionEvent;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;
import com.ai.sboss.arrangement.utils.JSONUtils;

/**
 *  “业务流程异常”事件发送器的一个AMQP实现<br>
 * 将事件通知发送到AMQP的队列中
 * @author yinwenjie
 */
public class ArrangementExceptionEventAMQPSender implements ArrangementExceptionEventSender {
	
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
	 * @see com.ai.sboss.arrangement.event.ArrangementExceptionEventSender#senderExceptionEvent(com.ai.sboss.arrangement.event.ExceptionEvent)
	 */
	@Override
	public void senderExceptionEvent(ExceptionEvent event) throws BizException {
		//发送到AMQP消息队列：转成json，然后直接发送
		if(event == null) {
			throw new BizException("ExceptionEvent不能为null，请检查", ResponseCode._404);
		}
		
		String eventContext = JSONUtils.toString(event);
		byte[] bodyBytes = eventContext.getBytes();
		
		Message amqpMessage = new Message(bodyBytes, new MessageProperties());
		//因为在配置文件中，已经制定了route-key，所以这里直接提交内容就行了。
		this.amqpTemplate.send(amqpMessage);
	}
}