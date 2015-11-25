package com.ai.sboss.arrangement.event.local;

import com.ai.sboss.arrangement.event.ArrangementBeginEventSender;
import com.ai.sboss.arrangement.event.ArrangementEndEventSender;
import com.ai.sboss.arrangement.event.ArrangementExceptionEventSender;
import com.ai.sboss.arrangement.event.ArrangementFlowEventSender;
import com.ai.sboss.arrangement.event.EventSendAbstractFactory;
import com.ai.sboss.arrangement.event.SenderConfigure;

/**
 * 事件发送工厂的本地实现。<br>
 * 所谓本地实现，就是单个编排系统工作时，有这个节点本身发出事件信息到“事件生产者-消费者”模型，然后由这个节点本省的事件监听者进行消费的实现机制<br>
 * @author yinwenjie
 */
public class EventSendLocalFactoryBean extends EventSendAbstractFactory {
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
