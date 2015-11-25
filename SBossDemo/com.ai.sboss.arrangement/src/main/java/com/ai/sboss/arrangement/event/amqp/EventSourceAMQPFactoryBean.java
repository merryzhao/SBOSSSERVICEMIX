package com.ai.sboss.arrangement.event.amqp;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.ai.sboss.arrangement.event.ArrangementBeginListener;
import com.ai.sboss.arrangement.event.ArrangementEndListener;
import com.ai.sboss.arrangement.event.ArrangementExceptionListener;
import com.ai.sboss.arrangement.event.ArrangementFlowListener;
import com.ai.sboss.arrangement.event.EventSourceAbstractFactory;

import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * 事件监听工厂的“AMQP”实现。<br>
 * 这个工厂监听指定的AMQP队列，并在队列中有消息达到后，根据消息类型激活相应的处理方法。<br>
 * 注意，默认情况下，必须自行构造ack信息给消息队列。
 * @author yinwenjie
 */
public class EventSourceAMQPFactoryBean extends EventSourceAbstractFactory {

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

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSourceAbstractFactory#addArrangementExceptionListener(com.ai.sboss.arrangement.event.ArrangementExceptionListener)
	 */
	@Override
	public void addArrangementExceptionListener(ArrangementExceptionListener listener) throws BizException {
		if(listener == null) {
			throw new BizException("错误的监听对象，请检查", ResponseCode._404);
		}
		
		EventSourceAMQPFactoryBean.EXCEPTION_LISTENERS.add(listener);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSourceAbstractFactory#addArrangementBeginListener(com.ai.sboss.arrangement.event.ArrangementBeginListener)
	 */
	@Override
	public void addArrangementBeginListener(ArrangementBeginListener listener) throws BizException {
		if(listener == null) {
			throw new BizException("错误的监听对象，请检查", ResponseCode._404);
		}
		
		EventSourceAMQPFactoryBean.BEGIN_LISTENERS.add(listener);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSourceAbstractFactory#addArrangementEndListener(com.ai.sboss.arrangement.event.ArrangementEndListener)
	 */
	@Override
	public void addArrangementEndListener(ArrangementEndListener listener) throws BizException {
		if(listener == null) {
			throw new BizException("错误的监听对象，请检查", ResponseCode._404);
		}
		
		EventSourceAMQPFactoryBean.END_LISTENERS.add(listener);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSourceAbstractFactory#addArrangementFlowListener(com.ai.sboss.arrangement.event.ArrangementFlowListener)
	 */
	@Override
	public void addArrangementFlowListener(ArrangementFlowListener listener) throws BizException {
		if(listener == null) {
			throw new BizException("错误的监听对象，请检查", ResponseCode._404);
		}
		
		EventSourceAMQPFactoryBean.FLOW_LISTENERS.add(listener);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSourceAbstractFactory#removeArrangementExceptionListener(com.ai.sboss.arrangement.event.ArrangementExceptionListener)
	 */
	@Override
	public void removeArrangementExceptionListener(ArrangementExceptionListener listener) throws BizException {
		if(listener == null) {
			throw new BizException("错误的监听对象，请检查", ResponseCode._404);
		}
		
		EventSourceAMQPFactoryBean.EXCEPTION_LISTENERS.remove(listener);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSourceAbstractFactory#removeArrangementBeginListener(com.ai.sboss.arrangement.event.ArrangementBeginListener)
	 */
	@Override
	public void removeArrangementBeginListener(ArrangementBeginListener listener) throws BizException {
		if(listener == null) {
			throw new BizException("错误的监听对象，请检查", ResponseCode._404);
		}
		
		EventSourceAMQPFactoryBean.BEGIN_LISTENERS.remove(listener);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSourceAbstractFactory#removeArrangementEndListener(com.ai.sboss.arrangement.event.ArrangementEndListener)
	 */
	@Override
	public void removeArrangementEndListener(ArrangementEndListener listener) throws BizException {
		if(listener == null) {
			throw new BizException("错误的监听对象，请检查", ResponseCode._404);
		}
		
		EventSourceAMQPFactoryBean.END_LISTENERS.remove(listener);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSourceAbstractFactory#removeArrangementFlowListener(com.ai.sboss.arrangement.event.ArrangementFlowListener)
	 */
	@Override
	public void removeArrangementFlowListener(ArrangementFlowListener listener) throws BizException {
		if(listener == null) {
			throw new BizException("错误的监听对象，请检查", ResponseCode._404);
		}
		
		EventSourceAMQPFactoryBean.FLOW_LISTENERS.remove(listener);
	}
	
	/**
	 * 包内使用的方法，用户获取当前对ArrangementException-Event的监听对象集合
	 * @return
	 */
	ConcurrentLinkedQueue<ArrangementExceptionListener> getArrangementExceptionListeners() {
		return EventSourceAMQPFactoryBean.EXCEPTION_LISTENERS;
	}
	
	/**
	 * 包内使用的方法，用户获取当前对ArrangementBegin-Event的监听对象集合
	 * @return
	 */
	ConcurrentLinkedQueue<ArrangementBeginListener> getArrangementBeginListeners() {
		return EventSourceAMQPFactoryBean.BEGIN_LISTENERS;
	}
	
	/**
	 * 包内使用的方法，用户获取当前对ArrangementEnd-Event的监听对象集合
	 * @return
	 */
	ConcurrentLinkedQueue<ArrangementEndListener> getArrangementEndListeners() {
		return EventSourceAMQPFactoryBean.END_LISTENERS;
	}
	
	/**
	 * 包内使用的方法，用户获取当前对ArrangementEnd-Event的监听对象集合
	 * @return
	 */
	ConcurrentLinkedQueue<ArrangementFlowListener> getArrangementFlowListeners() {
		return EventSourceAMQPFactoryBean.FLOW_LISTENERS;
	}
}