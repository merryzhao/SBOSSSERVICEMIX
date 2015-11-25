package com.ai.sboss.arrangement.event.amqp;

import com.ai.sboss.arrangement.event.ArrangementBeginEventSender;
import com.ai.sboss.arrangement.event.ArrangementEndEventSender;
import com.ai.sboss.arrangement.event.ArrangementExceptionEventSender;
import com.ai.sboss.arrangement.event.ArrangementFlowEventSender;
import com.ai.sboss.arrangement.event.SenderConfigure;

/**
 *  “AMQP消息队列实现”的事件发送工厂的配置信息（通过spring就可以轻松脱耦了）
 * @author yinwenjie
 */
public class SendAMQPConfigure implements SenderConfigure {
	/**
	 * 所使用的“开始事件”发送者的具体实现
	 */
	private ArrangementBeginEventSender beginEventSender;
	
	/**
	 * 所使用的“结束事件”发送者的具体实现
	 */
	private ArrangementEndEventSender endEventSender;
	
	/**
	 * 所使用的“异常事件”发送者的具体实现
	 */
	private ArrangementExceptionEventSender exceptionEventSender;
	
	/**
	 * 所使用的“正常流转”事件的具体实现
	 */
	private ArrangementFlowEventSender flowEventSender;
	
	public void setBeginEventSender(ArrangementBeginEventSender beginEventSender) {
		this.beginEventSender = beginEventSender;
	}

	public void setEndEventSender(ArrangementEndEventSender endEventSender) {
		this.endEventSender = endEventSender;
	}

	public void setExceptionEventSender(ArrangementExceptionEventSender exceptionEventSender) {
		this.exceptionEventSender = exceptionEventSender;
	}

	public void setFlowEventSender(ArrangementFlowEventSender flowEventSender) {
		this.flowEventSender = flowEventSender;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.SenderConfigure#getEndEventSender()
	 */
	@Override
	public ArrangementEndEventSender getEndEventSender() {
		return this.endEventSender;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.SenderConfigure#getExceptionEventSender()
	 */
	@Override
	public ArrangementExceptionEventSender getExceptionEventSender() {
		return this.exceptionEventSender;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.SenderConfigure#getFlowEventSender()
	 */
	@Override
	public ArrangementFlowEventSender getFlowEventSender() {
		return this.flowEventSender;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.SenderConfigure#getBeginEventSender()
	 */
	@Override
	public ArrangementBeginEventSender getBeginEventSender() {
		return this.beginEventSender;
	}

}
