package com.ai.sboss.arrangement.engine;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.ai.sboss.arrangement.JUnit4ClassRunner;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * 测试流转
 * @author yinwenjie
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-*.xml"})
public class TestFlowingJointInstance {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestFlowingJointInstance.class);
	
	@Autowired
	private IStreamingControlService streamingControlService;
	
	/**
	 * 测试正向流转1
	 */
	@Test
	public void testForwardFlow1() {
		String jointInstanceid = "4eb9e271-c9fc-4153-b33e-ce631000001";
		String executor = "producer1";
		try {
			Map<String, Object> values = new HashMap<String , Object>();
			values.put("userid", "consumer1");
			this.streamingControlService.executeFlowByJointInstanceid(jointInstanceid, executor, values);
		} catch (BizException e) {
			TestFlowingJointInstance.LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 测试正向流转2
	 */
	@Test
	public void testForwardFlow2() {
		String jointInstanceid = "4eb9e271-c9fc-4153-b33e-ce631000002";
		String executor = "producer1";
		try {
			Map<String, Object> values = new HashMap<String , Object>();
			values.put("userid", "consumer1");
			this.streamingControlService.executeFlowByJointInstanceid(jointInstanceid, executor, values);
		} catch (BizException e) {
			TestFlowingJointInstance.LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 测试逆向流转1
	 */
	@Test
	public void testUNForwardFlow2() {
		String jointInstanceid = "4eb9e271-c9fc-4153-b33e-ce631000001";
		String executor = "producer1";
		try {
			this.streamingControlService.executeUNFlowByJointInstanceid(jointInstanceid, executor);
		} catch (BizException e) {
			TestFlowingJointInstance.LOGGER.error(e.getMessage(), e);
		}
	}
}
