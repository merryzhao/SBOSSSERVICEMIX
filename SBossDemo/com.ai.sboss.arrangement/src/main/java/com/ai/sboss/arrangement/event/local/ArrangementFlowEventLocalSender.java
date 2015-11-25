package com.ai.sboss.arrangement.event.local;

import org.apache.commons.lang.StringUtils;

import com.ai.sboss.arrangement.event.ArrangementFlowEventSender;
import com.ai.sboss.arrangement.event.FlowEvent;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * “业务流程正常流转”事件发送器的一个本地实现<br>
 * 将事件通知发送到本地的一个 事件“生产者-消费者”模型中
 * @author yinwenjie
 * 
 */
public class ArrangementFlowEventLocalSender implements ArrangementFlowEventSender {

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.ArrangementFlowEventSender#senderFlowEvent(com.ai.sboss.arrangement.event.FlowEvent)
	 */
	@Override
	public void senderFlowEvent(FlowEvent event)  throws BizException {
		/*
		 * 具体处理过程的描述，请参见
		 * void com.ai.sboss.arrangement.event.local.ArrangementBeginEventLocalSender.senderBeginEvent(BeginEvent event) throws BizException
		 * */
		//1、================
		boolean error = false;
		String errorMessage = "";
		if(event == null) {
			errorMessage = "事件描述信息必须传入";
			throw new BizException(errorMessage, ResponseCode._401);
		}
		if(StringUtils.isEmpty(event.getArrangementInstanceId())) {
			error = true;
			errorMessage += "编排流程实例编号，必须传入\r\n";
		}
		if(StringUtils.isEmpty(event.getArrangementId())) {
			error = true;
			errorMessage += "编排流程编号，必须传入\r\n";
		}
		if(error) {
			throw new BizException(errorMessage, ResponseCode._401);
		}
		
		
		//2、3、===============
		EventMessageHeap eventMessageHeap = EventMessageHeap.getNewInstance();
		eventMessageHeap.pushNewEventMessage(event);
	}
}
