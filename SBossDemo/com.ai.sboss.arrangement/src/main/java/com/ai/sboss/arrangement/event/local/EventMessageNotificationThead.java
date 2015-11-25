package com.ai.sboss.arrangement.event.local;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ai.sboss.arrangement.event.ArrangementBeginListener;
import com.ai.sboss.arrangement.event.ArrangementEndListener;
import com.ai.sboss.arrangement.event.ArrangementEvent;
import com.ai.sboss.arrangement.event.ArrangementExceptionListener;
import com.ai.sboss.arrangement.event.ArrangementFlowListener;
import com.ai.sboss.arrangement.event.BeginEvent;
import com.ai.sboss.arrangement.event.EndEvent;
import com.ai.sboss.arrangement.event.EventType;
import com.ai.sboss.arrangement.event.ExceptionEvent;
import com.ai.sboss.arrangement.event.FlowEvent;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * 事件统治者线程，最终的某个消息事件信息，由其送给相关的listener。<br>
 * @author yinwenjie
 */
public class EventMessageNotificationThead extends Thread {
	
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(EventMessageNotificationThead.class);
	
	/**
	 * 可能需要进行的“流程开始”事件的通知
	 */
	private ArrangementBeginListener arrangementBeginListener;
	
	/**
	 * 可能需要进行的“流程结束”事件的通知
	 */
	private ArrangementEndListener arrangementEndListener;
	
	/**
	 * 可能需要进行“流程异常”事件的通知
	 */
	private ArrangementExceptionListener arrangementExceptionListener;
	
	/**
	 * 可能需要进行“流程流转”事件的通知
	 */
	private ArrangementFlowListener arrangementFlowListener;
	
	/**
	 * 本次所发生的事件
	 */
	private ArrangementEvent event;
	
	/**
	 * 构造进行“流程开始”事件通知的通知者
	 */
	public EventMessageNotificationThead() {
		
	}
	
	@Override
	public void run() {
		/*
		 * 第一版的通知是非阻塞式的，在后续还会增加阻塞式和非阻塞式共存的方式进行事件通知。
		 * 为什么后续还要加入阻塞式的方式呢？因为存在使用listener从外部改变流程状态的需求。
		 * 
		 * 接下来我们来看目前的处理：
		 * 1、首先判断event的类型，根据类型直接进行相关的通知即可
		 * 2、如果是FlowEvent时间，根据事件的类型还要调用不同的方法
		 * */
		
		//1、===================================
		if(this.event instanceof BeginEvent) {
			try {
				this.arrangementBeginListener.onArrangementBegin((BeginEvent)event);
			} catch(BizException e) {
				EventMessageNotificationThead.LOGGER.error(e.getMessage(), e);
			}
		}
		
		if(this.event instanceof ExceptionEvent) {
			try {
				this.arrangementExceptionListener.onArrangementException((ExceptionEvent)event);
			} catch(BizException e) {
				EventMessageNotificationThead.LOGGER.error(e.getMessage(), e);
			}
		}
		
		if(this.event instanceof EndEvent) {
			try {
				this.arrangementEndListener.onArrangementEnd((EndEvent)event);
			} catch(BizException e) {
				EventMessageNotificationThead.LOGGER.error(e.getMessage(), e);
			}
		}
		
		//2、===========================
		if(this.event instanceof FlowEvent) {
			try {
				FlowEvent flowEvent = (FlowEvent)this.event;
				//如果条件成立，说明是“流转前”事件
				if(flowEvent.getEventType() == EventType.BEFOREFLOW) {
					this.arrangementFlowListener.onFlowBegin(flowEvent);
				} else if(flowEvent.getEventType() == EventType.AFTERFLOW) {
					this.arrangementFlowListener.onFlowEnd(flowEvent); 
				}
			} catch(BizException e) {
				EventMessageNotificationThead.LOGGER.error(e.getMessage(), e);
			}
		}
	}

	public void setArrangementBeginListener(
			ArrangementBeginListener arrangementBeginListener) {
		this.arrangementBeginListener = arrangementBeginListener;
	}

	public void setArrangementEndListener(
			ArrangementEndListener arrangementEndListener) {
		this.arrangementEndListener = arrangementEndListener;
	}

	public void setArrangementExceptionListener(
			ArrangementExceptionListener arrangementExceptionListener) {
		this.arrangementExceptionListener = arrangementExceptionListener;
	}

	public void setArrangementFlowListener(
			ArrangementFlowListener arrangementFlowListener) {
		this.arrangementFlowListener = arrangementFlowListener;
	}

	public void setEvent(ArrangementEvent event) {
		this.event = event;
	}
	
}
