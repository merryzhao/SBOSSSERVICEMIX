package com.ai.sboss.arrangement.event.amqp;

import com.ai.sboss.arrangement.event.ArrangementBeginEventSender;
import com.ai.sboss.arrangement.event.ArrangementEndEventSender;
import com.ai.sboss.arrangement.event.ArrangementExceptionEventSender;
import com.ai.sboss.arrangement.event.ArrangementFlowEventSender;
import com.ai.sboss.arrangement.event.EventSendAbstractFactory;
import com.ai.sboss.arrangement.event.SenderConfigure;

/**
 * EventSendAbstractFactory抽象工程的AMQP协议的实现。通过这个工厂，将事件消息发送到配置的AMQP队列中
 * @author yinwenjie
 */
public class EventSendAMQPFactoryBean extends EventSendAbstractFactory {
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSendAbstractFactory#createNewExceptionEvent()
	 */
	@Override
	public ArrangementExceptionEventSender createNewExceptionEvent() {
		SenderConfigure senderConfigure = this.getConfigure();
		return senderConfigure.getExceptionEventSender();
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSendAbstractFactory#createNewArrangementBeginEvent()
	 */
	@Override
	public ArrangementBeginEventSender createNewArrangementBeginEvent() {
		SenderConfigure senderConfigure = this.getConfigure();
		return senderConfigure.getBeginEventSender();
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSendAbstractFactory#createArrangementEndEvent()
	 */
	@Override
	public ArrangementEndEventSender createArrangementEndEvent() {
		SenderConfigure senderConfigure = this.getConfigure();
		return senderConfigure.getEndEventSender();
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSendAbstractFactory#createArrangementFlowEvent()
	 */
	@Override
	public ArrangementFlowEventSender createArrangementFlowEvent() {
		SenderConfigure senderConfigure = this.getConfigure();
		return senderConfigure.getFlowEventSender();
	}
}
