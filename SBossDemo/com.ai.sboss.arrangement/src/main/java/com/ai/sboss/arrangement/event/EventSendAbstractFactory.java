package com.ai.sboss.arrangement.event;

import org.springframework.context.support.ApplicationObjectSupport;

/**
 * 事件信息发生后的发送工厂。实现这个工厂，可以对系统的事件模块进行隔离
 * @author yinwenjie
 */
public abstract class EventSendAbstractFactory extends ApplicationObjectSupport {
	/**
	 * 事件发送器工厂的配置信息
	 */
	private SenderConfigure configure;
	
	protected SenderConfigure getConfigure() {
		return configure;
	}
	
	public void setConfigure(SenderConfigure configure) {
		this.configure = configure;
	}
	
	/**
	 * 这个抽象方法规定了实现事件发送工厂的具体工厂，必须定义“异常事件发送器”
	 */
	public abstract ArrangementExceptionEventSender createNewExceptionEvent();
	
	/**
	 * 这个抽象方法规定了实现事件发送工厂的具体工厂，必须定义“流程实例开始事件”事件发送器
	 */
	public abstract ArrangementBeginEventSender createNewArrangementBeginEvent();
	
	/**
	 * 这个抽象方法规定了实现事件发送工厂的具体工厂，必须定义“流程实例正常结束事件”的事件发送器
	 */
	public abstract ArrangementEndEventSender createArrangementEndEvent();
	
	/**
	 * 这个抽象方法规定了实现事件发送工厂的具体工厂，必须定义“流程实例流转事件”的事件发送器
	 */
	public abstract ArrangementFlowEventSender createArrangementFlowEvent();
}