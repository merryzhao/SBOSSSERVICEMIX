package com.ai.sboss.arrangement.dao.relationdb;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.Assert;
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
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceDAO;
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointOutputParamsInstanceDAO;
import com.ai.sboss.arrangement.entity.orm.ArrangementInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.JointOutputParamsEntity;
import com.ai.sboss.arrangement.entity.orm.JointOutputParamsInstanceEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-*.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJointOutputParamInstanceDAO {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestJointOutputParamInstanceDAO.class);

	@Autowired
	private IJointInstanceDAO jointInstanceDAO;

	@Autowired
	private IJointOutputParamsInstanceDAO jointOutputParamsInstanceDAO;

	private static JointInstanceEntity preparedJointInstanceEntity;
	private static ArrangementInstanceEntity preparedArrangementInstanceEntity;
	private static JointOutputParamsInstanceEntity jointOutputParamsInstanceEntity;

	@BeforeClass
	public static void setUp() {
		preparedArrangementInstanceEntity = new ArrangementInstanceEntity();
		preparedArrangementInstanceEntity.setUid("f1ca7509-e22d-4792-a245-7635b437658768");
		preparedArrangementInstanceEntity.setCreator("consumer1");
		preparedArrangementInstanceEntity.setCreateTime(1440691200000L);
		preparedArrangementInstanceEntity.setStatu("1");
		preparedArrangementInstanceEntity.setDisplayName("hahafake");
		preparedArrangementInstanceEntity.setCreatorScope("industry");
		preparedArrangementInstanceEntity.setBusinessID("21546789");

		JointEntity joint = new JointEntity();
		joint.setUid("0aacd4a3-d2c3-46ba-bc94-be0fc036a8d3");
		preparedJointInstanceEntity = new JointInstanceEntity();
		preparedJointInstanceEntity.setUid(UUID.randomUUID().toString());
		preparedJointInstanceEntity.setCreator("deleteaftertest");
		preparedJointInstanceEntity.setAbsOffsettime(123L);
		preparedJointInstanceEntity.setArrangementInstance(preparedArrangementInstanceEntity);
		preparedJointInstanceEntity.setJoint(joint);
		preparedJointInstanceEntity.setExpectedExeTime(123L);
		preparedJointInstanceEntity.setStatu("waiting");
		preparedJointInstanceEntity.setExeTime(123L);

		jointOutputParamsInstanceEntity = new JointOutputParamsInstanceEntity();
		jointOutputParamsInstanceEntity.setUid(UUID.randomUUID().toString());
		jointOutputParamsInstanceEntity.setName("fake_delete_after_test");
		jointOutputParamsInstanceEntity.setType("String");
		jointOutputParamsInstanceEntity.setRequired(false);
		jointOutputParamsInstanceEntity.setJointInstance(preparedJointInstanceEntity);
		JointOutputParamsEntity jointOutputParam = new JointOutputParamsEntity();
		jointOutputParam.setUid("99785047-b7b9-4fe6-be1c-1b9c12a36aba");
		jointOutputParamsInstanceEntity.setJointOutputParam(jointOutputParam);
	}

	@AfterClass
	public static void tearDown() {
		preparedJointInstanceEntity = null;
		preparedArrangementInstanceEntity = null;
		jointOutputParamsInstanceEntity = null;
	}

	/**
	 * 对JointOutputParamsInstanceEntity createOutputParamsInstance(JointOutputParamsInstanceEntity outputParamInstance) throws BizException
	 * 方法进行测试，主要观察SQL语句。注意执行计划检查索引使用情况
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test1_createOutputParamsInstance() {
		TestJointOutputParamInstanceDAO.LOGGER.info("===测试createOutputParamsInstance===");
		JointOutputParamsInstanceEntity result = null;
		try {
			this.jointInstanceDAO.createJointInstance(preparedJointInstanceEntity);
			result = this.jointOutputParamsInstanceDAO
					.createOutputParamsInstance(jointOutputParamsInstanceEntity);
		} catch (BizException e) {
			TestJointOutputParamInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		Assert.assertNotNull(result);
		Assert.assertTrue(
				StringUtils.equals(result.getUid(), jointOutputParamsInstanceEntity.getUid()));
	}

	/**
	 * 对void updateInputParamsInstance(JointOutputParamsInstanceEntity outputParamInstance) throws BizException
	 * 方法进行测试，主要观察SQL语句。注意执行计划检查索引使用情况
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test2_updateInputParamsInstance() {
		TestJointOutputParamInstanceDAO.LOGGER.info("===测试updateInputParamsInstance===");
		// 错误测试
		jointOutputParamsInstanceEntity.setType("wrong type");
		try {
			this.jointOutputParamsInstanceDAO
					.updateInputParamsInstance(jointOutputParamsInstanceEntity);
		} catch (BizException e) {
			Assert.assertTrue(e.getResponseCode() == ResponseCode._402);
		}
		// 正确测试
		jointOutputParamsInstanceEntity.setType("Long");
		try {
			this.jointOutputParamsInstanceDAO
					.updateInputParamsInstance(jointOutputParamsInstanceEntity);
		} catch (BizException e) {
			TestJointOutputParamInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
	}

	/**
	 * 对List<JointOutputParamsInstanceEntity>
	 * queryOutputParamsInstanceByJointInstanceID(String jointInstanceid, Boolean
	 * required) throws BizException; 方法进行测试，主要观察SQL语句。注意执行计划检查索引使用情况
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test3_queryOutputParamsInstanceByJointInstanceID() {
		TestJointOutputParamInstanceDAO.LOGGER
				.info("===测试queryOutputParamsInstanceByJointInstanceID===");
		List<JointOutputParamsInstanceEntity> results = null;
		// 错误测试
		try {
			results = this.jointOutputParamsInstanceDAO.queryOutputParamsInstanceByJointInstanceID(
					preparedJointInstanceEntity.getUid(), true);
		} catch (BizException e) {
			TestJointOutputParamInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		Assert.assertTrue(results == null);
		// 正确测试
		try {
			results = this.jointOutputParamsInstanceDAO.queryOutputParamsInstanceByJointInstanceID(
					preparedJointInstanceEntity.getUid(), null);
		} catch (BizException e) {
			TestJointOutputParamInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		Assert.assertNotNull(results);
		Assert.assertTrue(1 == results.size());
	}

	/**
	 * 对void deleteOutputParamsInstancesByJointInstanceID(String outputParamInstanceid)
	 * throws BizException 及void
	 * deleteOutputParamInstanceByInstanceID(String jointInstanceid)
	 * throws BizException 方法进行测试，主要观察SQL语句。注意执行计划检查索引使用情况
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test4_deleteOutputParamsInstancesByJointInstanceID() {
		TestJointOutputParamInstanceDAO.LOGGER.info("===测试删除===");
		try {
			
			// 下面两条语句是互斥的，不能同时执行，需要对当前情况时打开相应语句进行测试
			/*this.jointOutputParamsInstanceDAO
					.deleteOutputParamInstanceByInstanceID(jointOutputParamsInstanceEntity.getUid());*/
			this.jointOutputParamsInstanceDAO.deleteOutputParamsInstancesByJointInstanceID(
					preparedJointInstanceEntity.getUid());
			
			this.jointInstanceDAO.deleteJointInstance(preparedJointInstanceEntity.getUid());
		} catch (BizException e) {
			TestJointOutputParamInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
	}
}
