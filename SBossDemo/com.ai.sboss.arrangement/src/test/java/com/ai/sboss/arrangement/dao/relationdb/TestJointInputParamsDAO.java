package com.ai.sboss.arrangement.dao.relationdb;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointInputParamsDAO;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointInputParamsEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * jointInputParamsDAO持久层测试
 * 
 * @author chaos
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-*.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJointInputParamsDAO {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestJointInputParamsDAO.class);

	/**
	 * 要测试的接口
	 */
	@Autowired
	private IJointInputParamsDAO inputParamsDAO;
	@Autowired
	private IJointDAO jointDAO;
	private static Set<JointInputParamsEntity> inputParamsEntities;
	private static JointEntity preparedJointEntity = null;

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
		inputParamsEntities = new HashSet<JointInputParamsEntity>();
		for (int i = 0; i < 3; ++i) {
			JointInputParamsEntity inputParamsEntity = new JointInputParamsEntity();
			inputParamsEntity.setDefaultValue("1");
			inputParamsEntity.setName("inputparam" + i);
			inputParamsEntity.setDisplayName("inputparam");
			inputParamsEntity.setRequired(true);
			inputParamsEntity.setType("String");
			inputParamsEntity.setDisplayType("String");
			inputParamsEntity.setJoint(preparedJointEntity);
			inputParamsEntities.add(inputParamsEntity);
		}
		preparedJointEntity.setInputParams(inputParamsEntities);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		/*
		 * 如果测试完成后需要一些全局化的清理工作，在这里进行
		 */
		preparedJointEntity = null;
		
	}

	/**
	 * 正式的测试代码写在这里
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test1_bindJointInputParams() {
		TestJointInputParamsDAO.LOGGER.info("====测试bindJointInputParams====");
		try {
			this.jointDAO.createJoint(preparedJointEntity);
			this.inputParamsDAO.bindJointInputParams(preparedJointEntity.getUid(), inputParamsEntities);
		} catch (BizException e) {
			TestJointInputParamsDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test2_queryInputParamsByjointuid() {
		TestJointInputParamsDAO.LOGGER.info("====测试queryInputParamsByjointuid====");
		List<JointInputParamsEntity> inputParamList = null;
		try {
			inputParamList = this.inputParamsDAO.queryInputParamsByjointuid(preparedJointEntity.getUid());
		} catch (BizException e) {
			TestJointInputParamsDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(inputParamList);
		TestJointInputParamsDAO.LOGGER.info("size ==>" + inputParamList.size());
		assertTrue(inputParamList.size() == 3);
		TestJointInputParamsDAO.LOGGER.info("default ==>" + inputParamList.get(0).getDefaultValue());
		assertTrue(inputParamList.get(0).getDefaultValue().equals("1"));
		TestJointInputParamsDAO.LOGGER.info("name ==>" + inputParamList.get(0).getDisplayName());
		assertTrue(inputParamList.get(0).getDisplayName().equals("inputparam"));
		TestJointInputParamsDAO.LOGGER.info("type ==>" + inputParamList.get(0).getDisplayType());
		assertTrue(inputParamList.get(0).getDisplayType().equals("String"));
		assertTrue(inputParamList.get(0).getRequired());
	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test3_rebindJointInputParams() {
		TestJointInputParamsDAO.LOGGER.info("====测试重新绑定====");
		// 准备新参数
		Set<JointInputParamsEntity> bindinputParamsEntities = new HashSet<JointInputParamsEntity>();
		for (int i = 0; i < 2; ++i) {
			JointInputParamsEntity inputParamsEntity = new JointInputParamsEntity();
			inputParamsEntity.setDefaultValue("0");
			inputParamsEntity.setName("newinputparam" + i);
			inputParamsEntity.setDisplayName("newinputparam");
			inputParamsEntity.setRequired(false);
			inputParamsEntity.setType("String");
			inputParamsEntity.setDisplayType("String");
			inputParamsEntity.setJoint(preparedJointEntity);
			bindinputParamsEntities.add(inputParamsEntity);
		}
		List<JointInputParamsEntity> inputParamList = null;
		try {
			this.inputParamsDAO.releaseAllJointInputParams(preparedJointEntity.getUid());
			this.inputParamsDAO.bindJointInputParams(preparedJointEntity.getUid(), bindinputParamsEntities);
			inputParamList = this.inputParamsDAO.queryInputParamsByjointuid(preparedJointEntity.getUid());
		} catch (BizException e) {
			TestJointInputParamsDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(inputParamList);
		assertTrue(inputParamList.size() == 2);
		assertTrue(inputParamList.get(0).getDefaultValue().equals("0"));
		assertTrue(inputParamList.get(0).getDisplayName().equals("newinputparam"));
		assertTrue(inputParamList.get(0).getDisplayType().equals("String"));
		assertFalse(inputParamList.get(0).getRequired());
		TestJointInputParamsDAO.LOGGER.error("new input params after bind ==>" + inputParamList.get(0).getDisplayName());

	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test4_releaseAllJointInputParams() {
		TestJointInputParamsDAO.LOGGER.info("====测试releaseAllJointInputParams====");
		List<JointInputParamsEntity> inputParamList = null;
		try {
			this.inputParamsDAO.releaseAllJointInputParams(preparedJointEntity.getUid());
			inputParamList = this.inputParamsDAO.queryInputParamsByjointuid(preparedJointEntity.getUid());
		} catch (BizException e) {
			TestJointInputParamsDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertTrue(inputParamList==null);
	}
}