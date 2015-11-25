package com.ai.sboss.arrangement.dao.relationdb;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.ai.sboss.arrangement.JUnit4ClassRunner;
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointDAO;
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointTradeMappingDAO;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointTradeMappingEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * jointDAO持久层测试
 * 
 * @author yinwenjie
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-*.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestTradeMappingDAO {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestTradeMappingDAO.class);

	private static JointEntity preparedJointEntity = new JointEntity();
	private static Map<String, String> tradeMappings;
	/**
	 * 要测试的接口
	 */
	@Autowired
	private IJointTradeMappingDAO jointTradeMappingDAO;
	@Autowired
	private IJointDAO jointDAO;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/*
		 * 如果启动前需要一些全局化的加载，在这里进行
		 */
		preparedJointEntity = new JointEntity();
		preparedJointEntity.setUid(java.util.UUID.randomUUID().toString());
		preparedJointEntity.setDisplayName("haha");
		preparedJointEntity.setAbsOffsettime(1000000L);
		preparedJointEntity.setRelateOffsettime(1000000L);
		tradeMappings = new HashMap<String, String>();

		tradeMappings.put("110120119", "industry");
		tradeMappings.put("110120120", "consumer");
		tradeMappings.put("110120121", "producer");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		/*
		 * 如果完成后需要一些全局化的清理，在这里进行
		 */
	}

	/**
	 * 正式的测试代码写在这里
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test1_bindJointTrade() {
		TestTradeMappingDAO.LOGGER.info("====测试bindJointTrade====");
		try {
			this.jointDAO.createJoint(preparedJointEntity);
			for (Map.Entry<String, String> tradeinfo : tradeMappings.entrySet()) {
				this.jointTradeMappingDAO.bindJointTrade(preparedJointEntity.getUid(), tradeinfo);
			}
		} catch (BizException e) {
			TestTradeMappingDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式

	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test2_getJointTradeMappingSet() {

		TestTradeMappingDAO.LOGGER.info("====测试getJointTradeMappingSet====");
		List<JointTradeMappingEntity> tradeMappingEntities = null;
		try {
			tradeMappingEntities = this.jointTradeMappingDAO.queryJointTradeMappingSet(preparedJointEntity.getUid());
		} catch (BizException e) {
			TestTradeMappingDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(tradeMappingEntities);

		TestTradeMappingDAO.LOGGER.info("size ==>" + tradeMappingEntities.size());
		assertTrue(tradeMappingEntities.size() == 3);
		for (JointTradeMappingEntity tradeinfo : tradeMappingEntities) {
			if (StringUtils.equals(tradeinfo.getTradeid(), "110120119")) {
				assertTrue(StringUtils.equals("industry", tradeinfo.getScope()));
			} else if (StringUtils.equals(tradeinfo.getTradeid(), "110120120")) {
				assertTrue(StringUtils.equals("consumer", tradeinfo.getScope()));
			} else if (StringUtils.equals(tradeinfo.getTradeid(), "110120121")) {
				assertTrue(StringUtils.equals("producer", tradeinfo.getScope()));
			} else {
				assertTrue(false);
			}
		}
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test3_bindJointTrade() {
		TestTradeMappingDAO.LOGGER.info("====测试bindJointTrade====");
		List<JointTradeMappingEntity> tradeMappingEntities = null;
		JointTradeMappingEntity appendtradeinfo = new JointTradeMappingEntity();
		appendtradeinfo.setJoint(preparedJointEntity);
		appendtradeinfo.setUid(java.util.UUID.randomUUID().toString());
		appendtradeinfo.setTradeid("110120122");
		appendtradeinfo.setScope("industry");
		try {
			this.jointTradeMappingDAO.bindJointTrade(appendtradeinfo);
			tradeMappingEntities = this.jointTradeMappingDAO.queryJointTradeMappingSet(preparedJointEntity.getUid());
		} catch (BizException e) {
			TestTradeMappingDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(tradeMappingEntities);
		TestTradeMappingDAO.LOGGER.info("size ==>" + tradeMappingEntities.size());
		assertTrue(tradeMappingEntities.size() == 4);
		for (JointTradeMappingEntity tradeinfo : tradeMappingEntities) {
			if (StringUtils.equals(tradeinfo.getTradeid(), "110120119") || StringUtils.equals(tradeinfo.getTradeid(), "110120122")) {
				assertTrue(StringUtils.equals("industry", tradeinfo.getScope()));
			} else if (StringUtils.equals(tradeinfo.getTradeid(), "110120120")) {
				assertTrue(StringUtils.equals("consumer", tradeinfo.getScope()));
			} else if (StringUtils.equals(tradeinfo.getTradeid(), "110120121")) {
				assertTrue(StringUtils.equals("producer", tradeinfo.getScope()));
			} else {
				assertTrue(false);
			}
		}
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test4_releaseAllJointTradeMapping() {

		TestTradeMappingDAO.LOGGER.info("====测试releaseAllJointTradeMapping====");
		List<JointTradeMappingEntity> tradeMappingEntities = null;
		try {
			this.jointTradeMappingDAO.releaseAllJointTradeMapping(preparedJointEntity.getUid());
			tradeMappingEntities = this.jointTradeMappingDAO.queryJointTradeMappingSet(preparedJointEntity.getUid());
		} catch (BizException e) {
			TestTradeMappingDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertTrue(tradeMappingEntities == null);

	}
}