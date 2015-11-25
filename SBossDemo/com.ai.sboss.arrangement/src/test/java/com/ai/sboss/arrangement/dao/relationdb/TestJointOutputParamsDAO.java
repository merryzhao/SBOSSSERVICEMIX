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
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointOutputParamsDAO;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointOutputParamsEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * jointOutputParamsDAO持久层测试
 * 
 * @author chaos
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-*.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJointOutputParamsDAO {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestJointOutputParamsDAO.class);

	/**
	 * 要测试的接口
	 */
	@Autowired
	private IJointOutputParamsDAO outputParamsDAO;
	@Autowired
	private IJointDAO jointDAO;
	private static JointEntity preparedJointEntity = null;
	private static Set<JointOutputParamsEntity> outputParamsEntities;

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
		outputParamsEntities = new HashSet<JointOutputParamsEntity>();
		for (int i = 0; i < 3; ++i) {
			JointOutputParamsEntity outputParamsEntity = new JointOutputParamsEntity();
			outputParamsEntity.setDefaultValue("1");
			outputParamsEntity.setName("outputparam");
			outputParamsEntity.setRequired(true);
			outputParamsEntity.setType("String");
			outputParamsEntity.setJoint(preparedJointEntity);
			outputParamsEntities.add(outputParamsEntity);
		}

		preparedJointEntity.setOutputParams(outputParamsEntities);
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
	public void test1_bindJointOutputParams() {
		TestJointOutputParamsDAO.LOGGER.info("====测试bindJointOutputParams====");
		try {
			this.jointDAO.createJoint(preparedJointEntity);
			this.outputParamsDAO.bindJointOutputParams(preparedJointEntity.getUid(), outputParamsEntities);
		} catch (BizException e) {
			TestJointOutputParamsDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test2_queryoutputParamsByjointuid() {
		TestJointOutputParamsDAO.LOGGER.info("====测试queryoutputParamsByjointuid====");
		List<JointOutputParamsEntity> outputParamList = null;
		try {
			outputParamList = this.outputParamsDAO.queryOutputParamsByjointuid(preparedJointEntity.getUid());
		} catch (BizException e) {
			TestJointOutputParamsDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(outputParamList);
		TestJointOutputParamsDAO.LOGGER.info("size ==>" + outputParamList.size());
		assertTrue(outputParamList.size() == 3);
		TestJointOutputParamsDAO.LOGGER.info("default ==>" + outputParamList.get(0).getDefaultValue());
		assertTrue(outputParamList.get(0).getDefaultValue().equals("1"));
		TestJointOutputParamsDAO.LOGGER.info("name ==>" + outputParamList.get(0).getName());
		assertTrue(outputParamList.get(0).getName().equals("outputparam"));
		TestJointOutputParamsDAO.LOGGER.info("type ==>" + outputParamList.get(0).getType());
		assertTrue(outputParamList.get(0).getType().equals("String"));
		assertTrue(outputParamList.get(0).getRequired());

	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test3_bindJointOutputParams() {
		TestJointOutputParamsDAO.LOGGER.info("====测试重新绑定====");
		// 准备新参数
		Set<JointOutputParamsEntity> bindoutputParamsEntities = new HashSet<JointOutputParamsEntity>();
		for (int i = 0; i < 2; ++i) {
			JointOutputParamsEntity outputParamsEntity = new JointOutputParamsEntity();
			outputParamsEntity.setDefaultValue("0");
			outputParamsEntity.setName("newoutputparam");
			outputParamsEntity.setRequired(false);
			outputParamsEntity.setType("String");
			outputParamsEntity.setJoint(preparedJointEntity);
			bindoutputParamsEntities.add(outputParamsEntity);
		}
		List<JointOutputParamsEntity> outputParamList = null;
		try {
			this.outputParamsDAO.releaseAllJointOutputParams(preparedJointEntity.getUid());
			this.outputParamsDAO.bindJointOutputParams(preparedJointEntity.getUid(), bindoutputParamsEntities);
			outputParamList = this.outputParamsDAO.queryOutputParamsByjointuid(preparedJointEntity.getUid());
		} catch (BizException e) {
			TestJointOutputParamsDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(outputParamList);
		assertTrue(outputParamList.size() == 2);
		assertTrue(outputParamList.get(0).getDefaultValue().equals("0"));
		assertTrue(outputParamList.get(0).getName().equals("newoutputparam"));
		assertTrue(outputParamList.get(0).getType().equals("String"));
		assertFalse(outputParamList.get(0).getRequired());
	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test4_releaseAllJointoutputParams() {
		TestJointOutputParamsDAO.LOGGER.info("====测试releaseAllJointoutputParams====");
		List<JointOutputParamsEntity> outputParamList = null;
		try {
			this.outputParamsDAO.releaseAllJointOutputParams(preparedJointEntity.getUid());
			outputParamList = this.outputParamsDAO.queryOutputParamsByjointuid(preparedJointEntity.getUid());
		} catch (BizException e) {
			TestJointOutputParamsDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertTrue(outputParamList == null);

	}
}