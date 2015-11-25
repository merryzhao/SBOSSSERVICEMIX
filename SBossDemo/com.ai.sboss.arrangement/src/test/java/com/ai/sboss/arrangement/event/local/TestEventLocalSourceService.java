package com.ai.sboss.arrangement.event.local;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import com.ai.sboss.arrangement.JUnit4ClassRunner;
import com.ai.sboss.arrangement.event.ArrangementBeginEventSender;
import com.ai.sboss.arrangement.event.ArrangementBeginListener;
import com.ai.sboss.arrangement.event.BeginEvent;
import com.ai.sboss.arrangement.event.EventSendAbstractFactory;
import com.ai.sboss.arrangement.event.EventSourceAbstractFactory;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * 这个测试，检测事件模块的“本地事件”驱动中的
 * @author yinwenjie
 *
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:application-*.xml"})
public class TestEventLocalSourceService {
	
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestEventLocalSourceService.class);
	
	/**
	 * 事件监听工厂
	 */
	@Autowired
	@Qualifier("eventSourceFactoryBean")
	private EventSourceAbstractFactory sourceAbstractFactory;
	
	@Autowired
	@Qualifier("eventSendFactory")
	private EventSendAbstractFactory sendAbstractFactory;
	
	@Autowired
	@Qualifier("otherBeginEventLListener")
	private ArrangementBeginListener otherBeginEventLListener;
	
	@Autowired
	@Qualifier("someBeginEventListener")
	private ArrangementBeginListener someBeginEventListener;
	
	/**
	 * 初始化的东西在这里
	 */
	@Before
	public void beforeStartup() {
		/*
		 * 在正式注册前，就将两个监听注册进来。
		 * */
		try {
			this.sourceAbstractFactory.addArrangementBeginListener(this.otherBeginEventLListener);
			this.sourceAbstractFactory.addArrangementBeginListener(this.someBeginEventListener);
		} catch (BizException e) {
			TestEventLocalSourceService.LOGGER.error(e.getMessage(), e);
		}
	}
	
	@Test
	public void test() {
		/*
		 * 在这个test中，我们使用EventSourceAbstractFactory（背后的实现是，EventSourceLocalFactoryBean），注册两个BeginEventLListener
		 * ，然后使用EventSendAbstractFactory（背后的实现是，EventSendLocalFactoryBean），获取beginEvent事件驱动服务。
		 * 
		 * 然后发出事件驱动指令，并观察OtherBeginEventLListener和SomeBeginEventListener两个监听类的工作情况，特别是spring的ioc容器在注解模式下工作是否正常
		 * */
		ArrangementBeginEventSender arrangementBeginEventSender = this.sendAbstractFactory.createNewArrangementBeginEvent();
		//构造beginevent
		BeginEvent beginEvent = new BeginEvent();
		String arrangementId = UUID.randomUUID().toString();
		beginEvent.setArrangementId(arrangementId);
		TestEventLocalSourceService.LOGGER.info("===========arrangementId:" + arrangementId);
		String arrangementInstanceId = UUID.randomUUID().toString();
		beginEvent.setArrangementInstanceId(arrangementInstanceId);
		TestEventLocalSourceService.LOGGER.info("===========arrangementInstanceId:" + arrangementInstanceId);
		
		//发送
		try {
			arrangementBeginEventSender.senderBeginEvent(beginEvent);
		} catch (BizException e) {
			TestEventLocalSourceService.LOGGER.error(e.getMessage(), e);
		}
		
		//由于这个守护线程终止就看不到东西了，所以这里等待一下，以便测试用例完成
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				TestEventLocalSourceService.LOGGER.error(e.getMessage(), e);
			}
		}
	}
}
