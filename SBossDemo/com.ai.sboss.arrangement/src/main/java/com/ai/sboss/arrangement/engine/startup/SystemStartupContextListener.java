package com.ai.sboss.arrangement.engine.startup;

import javassist.util.proxy.ProxyObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 系统上下文的监听器，这个监听器主要记录已经完成启动的spring的上下文。在系统的实现过程中，可以在全局使用
 * @author yinwenjie
 */
public class SystemStartupContextListener implements ApplicationListener<ContextRefreshedEvent> {
	/**
	 * 系统的spring上下文
	 */
	private static ApplicationContext APPLICATION_CONTEXT;
	
	private static final Log LOGGER = LogFactory.getLog(SystemStartupContextListener.class);
	
	static {
		@SuppressWarnings("rawtypes")
		Class Tclass = ProxyObject.class;
		SystemStartupContextListener.LOGGER.info("ProxyObject接口加载 ：" + Tclass);
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		//如果条件成立，说明已经完成了spring的初始化，忽略本次调用
		if(event.getApplicationContext().getParent() != null) {
			return;
		}
		
		SystemStartupContextListener.APPLICATION_CONTEXT = event.getApplicationContext();
	}
	
	/**
	 * 得到全系统的spring上下文
	 * @return 
	 */
	public static ApplicationContext getApplicationContext() {
		return SystemStartupContextListener.APPLICATION_CONTEXT;
	}
}