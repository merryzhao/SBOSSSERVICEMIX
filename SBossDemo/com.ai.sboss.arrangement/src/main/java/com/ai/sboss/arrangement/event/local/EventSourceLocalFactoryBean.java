package com.ai.sboss.arrangement.event.local;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.context.ApplicationContext;

import com.ai.sboss.arrangement.event.ArrangementBeginListener;
import com.ai.sboss.arrangement.event.ArrangementEndListener;
import com.ai.sboss.arrangement.event.ArrangementExceptionListener;
import com.ai.sboss.arrangement.event.ArrangementFlowListener;
import com.ai.sboss.arrangement.event.EventSourceAbstractFactory;
import com.ai.sboss.arrangement.event.ReceiveConfigure;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * 事件监听工厂的“本地”实现。<br>
 * 所谓本地实现，就是单个编排系统工作时，有这个节点本身发出事件信息到“事件生产者-消费者”模型，然后由这个节点本省的事件监听者进行消费的实现机制<br>
 * 从实现角度来看，这个factory不止是一个事件发送工厂的实现，而且在“生产者-消费者”模型中，还充当者“事件消费者”的角色。
 * @author yinwenjie
 */
public class EventSourceLocalFactoryBean extends EventSourceAbstractFactory {
	/**
	 * 需要进行异常监听的集合放在这里
	 */
	private static final ConcurrentLinkedQueue<ArrangementExceptionListener>  EXCEPTION_LISTENERS = new ConcurrentLinkedQueue<ArrangementExceptionListener>();
	
	/**
	 * 需要进行起始监听的集合放在这里
	 */
	private static final ConcurrentLinkedQueue<ArrangementBeginListener> BEGIN_LISTENERS = new ConcurrentLinkedQueue<ArrangementBeginListener>();
	
	/**
	 * 需要进行“结束事件”监听的对象集合放在这里
	 */
	private static final ConcurrentLinkedQueue<ArrangementEndListener> END_LISTENERS = new ConcurrentLinkedQueue<ArrangementEndListener>();
	
	/**
	 * 需要进行“流转事件”监听的对象集合放在这里
	 */
	private static final ConcurrentLinkedQueue<ArrangementFlowListener> FLOW_LISTENERS = new ConcurrentLinkedQueue<ArrangementFlowListener>();
	
	
	public EventSourceLocalFactoryBean() {
		
	}
	
	public void init() {
		super.init();
		
		/*
		 * 初始化的过程，即启动对EventMessageHeap本地队列的获取。
		 * 为了快速实现第一版功能，这里我们只创建了和管理了一个消费者模型.
		 * 但是为了加快响应速度，我们使用了线程池来完成listener的通知：
		 * 
		 * 1、从configure中获取连接池、事件消费者线程。（事件处理者线程，就是要被送入线程池进行处理的线程）在需要时由spring上下文负责创建
		 * 2、启动消费者线程，开始监听EventMessageHeap本地队列
		 * 3、一旦队列中有需要事件需要通知出去，就送入一个事件处理者线程，并由事件处理者线程通知相关ConcurrentLinkedQueue中的一个监听者
		 * */
		ReceiveConfigure pc = this.getConfigure();
		if(!(pc instanceof ReceiveLocalConfigure)) {
			throw new RuntimeException("EventSourceLocalFactoryBean must use ReceiveLocalConfigure");
		}
		ReceiveLocalConfigure configure = (ReceiveLocalConfigure)pc;
		
		String eventConsumerThreadName = configure.getEventConsumerThreadName();
		ApplicationContext applicationContext = this.getApplicationContext();
		EventMessageConsumerThread consumerRunable = (EventMessageConsumerThread)applicationContext.getBean(eventConsumerThreadName);
		consumerRunable.setBeginListeners(EventSourceLocalFactoryBean.BEGIN_LISTENERS);
		consumerRunable.setEndListeners(EventSourceLocalFactoryBean.END_LISTENERS);
		consumerRunable.setExceptionListeners(EventSourceLocalFactoryBean.EXCEPTION_LISTENERS);
		consumerRunable.setFlowListeners(EventSourceLocalFactoryBean.FLOW_LISTENERS);
		consumerRunable.setNotificationTheadPool(configure.getListenerExecutors());
		consumerRunable.setMessageNotificationTheadName(configure.getEventNotificationThreadName());
		
		//启动事件消费的监听者
		Thread consumerThread = new Thread(consumerRunable);
		consumerThread.setDaemon(true);
		consumerThread.start();
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSourceAbstractFactory#addArrangementExceptionListener(com.ai.sboss.arrangement.event.ArrangementExceptionListener)
	 */
	@Override
	public void addArrangementExceptionListener(ArrangementExceptionListener listener) throws BizException {
		if(listener == null) {
			throw new BizException("错误的监听对象，请检查", ResponseCode._404);
		}
		
		EventSourceLocalFactoryBean.EXCEPTION_LISTENERS.add(listener);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSourceAbstractFactory#addArrangementBeginListener(com.ai.sboss.arrangement.event.ArrangementBeginListener)
	 */
	@Override
	public void addArrangementBeginListener(ArrangementBeginListener listener) throws BizException {
		if(listener == null) {
			throw new BizException("错误的监听对象，请检查", ResponseCode._404);
		}
		
		EventSourceLocalFactoryBean.BEGIN_LISTENERS.add(listener);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSourceAbstractFactory#addArrangementEndListener(com.ai.sboss.arrangement.event.ArrangementEndListener)
	 */
	@Override
	public void addArrangementEndListener(ArrangementEndListener listener) throws BizException {
		if(listener == null) {
			throw new BizException("错误的监听对象，请检查", ResponseCode._404);
		}
		
		EventSourceLocalFactoryBean.END_LISTENERS.add(listener);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSourceAbstractFactory#addArrangementFlowListener(com.ai.sboss.arrangement.event.ArrangementFlowListener)
	 */
	@Override
	public void addArrangementFlowListener(ArrangementFlowListener listener) throws BizException {
		if(listener == null) {
			throw new BizException("错误的监听对象，请检查", ResponseCode._404);
		}
		
		EventSourceLocalFactoryBean.FLOW_LISTENERS.add(listener);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSourceAbstractFactory#removeArrangementExceptionListener(com.ai.sboss.arrangement.event.ArrangementExceptionListener)
	 */
	@Override
	public void removeArrangementExceptionListener(ArrangementExceptionListener listener) throws BizException {
		if(listener == null) {
			throw new BizException("错误的监听对象，请检查", ResponseCode._404);
		}
		
		EventSourceLocalFactoryBean.EXCEPTION_LISTENERS.remove(listener);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSourceAbstractFactory#removeArrangementBeginListener(com.ai.sboss.arrangement.event.ArrangementBeginListener)
	 */
	@Override
	public void removeArrangementBeginListener(ArrangementBeginListener listener) throws BizException {
		if(listener == null) {
			throw new BizException("错误的监听对象，请检查", ResponseCode._404);
		}
		
		EventSourceLocalFactoryBean.BEGIN_LISTENERS.remove(listener);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSourceAbstractFactory#removeArrangementEndListener(com.ai.sboss.arrangement.event.ArrangementEndListener)
	 */
	@Override
	public void removeArrangementEndListener(ArrangementEndListener listener) throws BizException {
		if(listener == null) {
			throw new BizException("错误的监听对象，请检查", ResponseCode._404);
		}
		
		EventSourceLocalFactoryBean.END_LISTENERS.remove(listener);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSourceAbstractFactory#removeArrangementFlowListener(com.ai.sboss.arrangement.event.ArrangementFlowListener)
	 */
	@Override
	public void removeArrangementFlowListener(ArrangementFlowListener listener) throws BizException {
		if(listener == null) {
			throw new BizException("错误的监听对象，请检查", ResponseCode._404);
		}
		
		EventSourceLocalFactoryBean.FLOW_LISTENERS.remove(listener);
	}
}
