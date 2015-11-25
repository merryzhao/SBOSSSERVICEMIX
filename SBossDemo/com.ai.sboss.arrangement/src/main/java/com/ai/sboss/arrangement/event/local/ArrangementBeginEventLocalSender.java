package com.ai.sboss.arrangement.event.local;

import org.apache.commons.lang.StringUtils;

import com.ai.sboss.arrangement.event.ArrangementBeginEventSender;
import com.ai.sboss.arrangement.event.BeginEvent;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * “业务流程开始流转”事件发送器的一个本地实现<br>
 * 将事件通知发送到本地的一个 事件“生产者-消费者”模型中
 * @author yinwenjie
 */
public class ArrangementBeginEventLocalSender implements ArrangementBeginEventSender {

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.ArrangementBeginEventSender#senderBeginEvent(com.ai.sboss.arrangement.event.BeginEvent)
	 */
	@Override
	public void senderBeginEvent(BeginEvent event)  throws BizException {
		/*
		 * 处理步骤包括：
		 * 1、检查beginEvent中的重要属性是否已经填写
		 * 2、获取本地事件机制中唯一的EventMessageHeap“待发送事件”队列。
		 * 3、将这个BeginEvent，打入队列。完成
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
