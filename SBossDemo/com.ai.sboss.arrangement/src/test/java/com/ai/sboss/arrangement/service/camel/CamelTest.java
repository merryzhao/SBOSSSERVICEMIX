package com.ai.sboss.arrangement.service.camel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.ai.sboss.arrangement.JUnit4ClassRunner;

/**
 * 加载这个测试类，并在test方法中锁定，以便加载配置信息后，开启camel的route并测试
 * @author yinwenjie
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:application-*.xml"})
public class CamelTest {
	
	private static final Log LOGGER = LogFactory.getLog(CamelTest.class);
	
	private static final Object WAIT_OBJECT = new Object();
	
	@Test
	public void testCamel() {
		synchronized (WAIT_OBJECT) {
			try {
				WAIT_OBJECT.wait();
			} catch (InterruptedException e) {
				CamelTest.LOGGER.error(e.getMessage(), e);
			}
		}
	}
}
