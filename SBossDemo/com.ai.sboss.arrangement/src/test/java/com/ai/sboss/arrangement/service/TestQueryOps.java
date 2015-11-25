package com.ai.sboss.arrangement.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.ai.sboss.arrangement.JUnit4ClassRunner;
import com.ai.sboss.arrangement.entity.JsonEntity;
import com.ai.sboss.arrangement.exception.ResponseCode;
import com.ai.sboss.arrangement.utils.JSONUtils;

@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:application-*.xml"})
public class TestQueryOps {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestQueryOps.class);
	
	@Autowired
	private IQueryOps queryOps;
	
	@Test
	@Transactional
	public void testQueryJointByTradeid() {
		//不加入scope参数
		JsonEntity jsonResult = this.queryOps.queryJointByTradeid("1234354", null);
		TestQueryOps.LOGGER.info("jsonResult = " + JSONUtils.toString(jsonResult));
		Assert.assertNotNull(jsonResult);
		Assert.assertNotNull(jsonResult.getData());
		Assert.assertEquals(jsonResult.getDesc().getResult_code(), ResponseCode._200);
		
		//加入scope参数
		jsonResult = this.queryOps.queryJointByTradeid("567890", "industry");
		TestQueryOps.LOGGER.info("jsonResult = " + JSONUtils.toString(jsonResult));
		Assert.assertNotNull(jsonResult);
		Assert.assertNotNull(jsonResult.getData());
		Assert.assertEquals(jsonResult.getDesc().getResult_code(), ResponseCode._200);
	}
	
	@Test
	@Transactional
	public void testQueryarrAngementByTradeid() {
		JsonEntity jsonResult =  this.queryOps.queryDefaultArrangementByTradeid("1234354");
		TestQueryOps.LOGGER.info("jsonResult = " + JSONUtils.toString(jsonResult , new String[]{"parentArrangement","outputParams","inputParams","trades","jointmapping","childArrangements"}));
		Assert.assertNotNull(jsonResult);
		Assert.assertNotNull(jsonResult.getData());
		Assert.assertEquals(jsonResult.getDesc().getResult_code(), ResponseCode._200);
	}
}
