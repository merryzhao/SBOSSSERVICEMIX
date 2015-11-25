package com.ai.sboss.arrangement.event.amqp;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import com.ai.sboss.arrangement.event.ArrangementBeginListener;
import com.ai.sboss.arrangement.event.ArrangementEndListener;
import com.ai.sboss.arrangement.event.ArrangementExceptionListener;
import com.ai.sboss.arrangement.event.ArrangementFlowListener;
import com.ai.sboss.arrangement.event.BeginEvent;
import com.ai.sboss.arrangement.event.EndEvent;
import com.ai.sboss.arrangement.event.EventType;
import com.ai.sboss.arrangement.event.ExceptionEvent;
import com.ai.sboss.arrangement.event.FlowEvent;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;
import com.ai.sboss.arrangement.utils.JSONUtils;
import com.rabbitmq.client.Channel;

/**
 * @author yinwenjie
 */
public class ReceiveEventQueueListener extends MessageListenerAdapter {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(ReceiveEventQueueListener.class);
	
	/**
	 * 在“AMQP-event”包中实现的事件监听器集合工厂。
	 * 通过这个工厂，ReceiveEventQueueLitener可以获取哪些监听对象注册了listener
	 */
	private EventSourceAMQPFactoryBean eventSourceAMQPFactoryBean;

	public EventSourceAMQPFactoryBean getEventSourceAMQPFactoryBean() {
		return this.eventSourceAMQPFactoryBean;
	}

	public void setEventSourceAMQPFactoryBean(EventSourceAMQPFactoryBean eventSourceAMQPFactoryBean) {
		this.eventSourceAMQPFactoryBean = eventSourceAMQPFactoryBean;
	}

	/* (non-Javadoc)
	 * @see org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter#onMessage(org.springframework.amqp.core.Message, com.rabbitmq.client.Channel)
	 */
	@Override
	public void onMessage(Message message, Channel channel) throws BizException {
		/*
		 * 具体的处理过程：
		 * 1、收到队列的信息后（事件信息的格式请参见系统的规范文档），判断当前的事件类型，实例化成不同的对象
		 * 2、每一个message事件都需要通知 “对应集合”中所有的监听对象
		 * 3、执行这些监听（监听器内部是否有执行的异常，和是否ack无关）
		 * 4、在这里，无论事件监听是否出现问题，都需要手动ack消息队列。
		 * */
		byte[] messageBytes = message.getBody();
		MessageProperties messageProperties = message.getMessageProperties();
		long deliveryTag = messageProperties.getDeliveryTag();
		
		//1、============解析信息
		String messageContext = null;
		JSONObject eventJSONObject = null;
		String eventType = null;

		messageContext = new String(messageBytes);
		eventJSONObject = JSONUtils.toJSONObject(messageContext, new String[]{});
		eventType = eventJSONObject.has("eventType")?eventJSONObject.get("eventType").toString():null;
		//如果条件成立，则不会输出ack，这个消息将会向下传递
		if(eventType == null) {
			String errorMessage = "没有发现eventType";
			ReceiveEventQueueListener.LOGGER.warn(errorMessage + "messageContext = " + messageContext);
		}
		//TODO 这里暂时ack，为了清除测试环境的错误数据
		try {
			channel.basicAck(deliveryTag, false);
		} catch (IOException e) {
			ReceiveEventQueueListener.LOGGER.error(e.getMessage() , e);
			throw new BizException(e.getMessage(), ResponseCode._501);
		}
		
		//2、============视情况取得对应的监听集合
		//注意，这个循环只是为了保证代码符合“单一return”约束规范
		for(int index = 0 ; index < 1 ; index++) {
			if(EventType.valueOf(eventType) == EventType.BEGIN) {
				ConcurrentLinkedQueue<ArrangementBeginListener> beginListeners = this.eventSourceAMQPFactoryBean.getArrangementBeginListeners();
				//如果条件成立，说明没有需要监听的目标对象
				if(beginListeners == null || beginListeners.isEmpty()) {
					break;
				}
				
				BeginEvent beginEvent = (BeginEvent)JSONUtils.toBean(messageContext, BeginEvent.class , new String[]{});
				//TODO 依次执行监听，另外，这里的beginEvent最好就是克隆，而不是使用同一个event
				for (ArrangementBeginListener arrangementBeginListener : beginListeners) {
					//关注异常，这样不至于影响ReceiveEventQueueListener本身
					arrangementBeginListener.onArrangementBegin(beginEvent);
				}
			} else if(EventType.valueOf(eventType) == EventType.END) {
				ConcurrentLinkedQueue<ArrangementEndListener> endListeners = this.eventSourceAMQPFactoryBean.getArrangementEndListeners();
				//如果条件成立，说明没有需要监听的目标对象
				if(endListeners == null || endListeners.isEmpty()) {
					break;
				}
				EndEvent endEvent = (EndEvent)JSONUtils.toBean(messageContext, EndEvent.class , new String[]{});
				for (ArrangementEndListener arrangementEndListener : endListeners) {
					//关注异常，这样不至于影响ReceiveEventQueueListener本身
					arrangementEndListener.onArrangementEnd(endEvent);
				}
			} else if(EventType.valueOf(eventType) == EventType.EXCEPTION) {
				ConcurrentLinkedQueue<ArrangementExceptionListener> exceptionListeners = this.eventSourceAMQPFactoryBean.getArrangementExceptionListeners();
				//如果条件成立，说明没有需要监听的目标对象
				if(exceptionListeners == null || exceptionListeners.isEmpty()) {
					break;
				}
				ExceptionEvent exceptionEvent = (ExceptionEvent)JSONUtils.toBean(messageContext, ExceptionEvent.class , new String[]{});
				for (ArrangementExceptionListener arrangementExceptionListener : exceptionListeners) {
					//关注异常，这样不至于影响ReceiveEventQueueListener本身
					arrangementExceptionListener.onArrangementException(exceptionEvent);
				}
			} else if(EventType.valueOf(eventType) == EventType.AFTERFLOW || EventType.valueOf(eventType) == EventType.BEFOREFLOW) {
				ConcurrentLinkedQueue<ArrangementFlowListener> arrangementFlowListeners = this.eventSourceAMQPFactoryBean.getArrangementFlowListeners();
				//如果条件成立，说明没有需要监听的目标对象
				if(arrangementFlowListeners == null || arrangementFlowListeners.isEmpty()) {
					break;
				}
				FlowEvent flowEvent = (FlowEvent)JSONUtils.toBean(messageContext, FlowEvent.class , new String[]{});
				for (ArrangementFlowListener arrangementFlowListener : arrangementFlowListeners) {
					//关注异常，这样不至于影响ReceiveEventQueueListener本身
					if(EventType.valueOf(eventType) == EventType.AFTERFLOW) {
						arrangementFlowListener.onFlowEnd(flowEvent);
					} else {
						arrangementFlowListener.onFlowBegin(flowEvent);
					}
				}
			}
		}
		
		//3、============只要1、2步执行时OK的，就要ack；不管listener的执行是否正常
		try {
			channel.basicAck(deliveryTag, false);
		} catch (IOException e) {
			ReceiveEventQueueListener.LOGGER.error(e.getMessage() , e);
			throw new BizException(e.getMessage(), ResponseCode._501);
		}
	}
}