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
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointInputParamsInstanceDAO;
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceDAO;
import com.ai.sboss.arrangement.entity.orm.ArrangementInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointInputParamsEntity;
import com.ai.sboss.arrangement.entity.orm.JointInputParamsInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-*.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJointInputParamInstanceDAO {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestJointInputParamInstanceDAO.class);

	@Autowired
	private IJointInstanceDAO jointInstanceDAO;

	@Autowired
	private IJointInputParamsInstanceDAO jointInputParamsInstanceDAO;

	private static JointInstanceEntity preparedJointInstanceEntity;
	private static ArrangementInstanceEntity preparedArrangementInstanceEntity;
	private static JointInputParamsInstanceEntity jointInputParamsInstanceEntity;

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

		jointInputParamsInstanceEntity = new JointInputParamsInstanceEntity();
		jointInputParamsInstanceEntity.setUid(UUID.randomUUID().toString());
		jointInputParamsInstanceEntity.setName("fake_delete_after_test");
		jointInputParamsInstanceEntity.setType("String");
		jointInputParamsInstanceEntity.setRequired(false);
		jointInputParamsInstanceEntity.setDisplayType("字符型");
		jointInputParamsInstanceEntity.setDisplayName("displayName_for_fake");
		jointInputParamsInstanceEntity.setJointInstance(preparedJointInstanceEntity);
		JointInputParamsEntity jointInputParam = new JointInputParamsEntity();
		jointInputParam.setUid("4ee2d49b-9234-4c0a-98ea-1b70fbccc6c3");
		jointInputParamsInstanceEntity.setJointInputParam(jointInputParam);
	}

	@AfterClass
	public static void tearDown() {
		preparedJointInstanceEntity = null;
		preparedArrangementInstanceEntity = null;
		jointInputParamsInstanceEntity = null;
	}

	/**
	 * 对JointInputParamsInstanceEntity
	 * createInputParamsInstance(JointInputParamsInstanceEntity
	 * inputParamInstance) throws BizException 方法进行测试，主要观察SQL语句。注意执行计划检查索引使用情况
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test1_createInputParamsInstance() {
		TestJointInputParamInstanceDAO.LOGGER.info("===测试createInputParamsInstance===");
		JointInputParamsInstanceEntity result = null;
		JointInstanceEntity resultjoint = null;
		try {
			resultjoint = this.jointInstanceDAO.createJointInstance(preparedJointInstanceEntity);
			result = this.jointInputParamsInstanceDAO
					.createInputParamsInstance(jointInputParamsInstanceEntity);
			
			resultjoint = this.jointInstanceDAO.getEntity(preparedJointInstanceEntity.getUid());
			TestJointInputParamInstanceDAO.LOGGER.info("===创建JointInstance成功->"+resultjoint.getUid());
			result = this.jointInputParamsInstanceDAO.getEntity(jointInputParamsInstanceEntity.getUid());
			TestJointInputParamInstanceDAO.LOGGER.info("===创建JointInputParamsInstance成功->"+result.getUid());
		} catch (BizException e) {
			TestJointInputParamInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		Assert.assertNotNull(result);
		Assert.assertTrue(
				StringUtils.equals(result.getUid(), jointInputParamsInstanceEntity.getUid()));
	}

	/**
	 * 对void updateInputParamsInstance(JointInputParamsInstanceEntity
	 * inputParamInstance) throws BizException 方法进行测试，主要观察SQL语句。注意执行计划检查索引使用情况
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test2_updateInputParamsInstance() {
		TestJointInputParamInstanceDAO.LOGGER.info("===测试updateInputParamsInstance===");
		// 错误测试
		jointInputParamsInstanceEntity.setType("wrong type");
		try {
			this.jointInputParamsInstanceDAO
					.updateInputParamsInstance(jointInputParamsInstanceEntity);
		} catch (BizException e) {
			Assert.assertTrue(e.getResponseCode() == ResponseCode._402);
		}
		// 正确测试
		jointInputParamsInstanceEntity.setType("Long");
		try {
			this.jointInputParamsInstanceDAO
					.updateInputParamsInstance(jointInputParamsInstanceEntity);
		} catch (BizException e) {
			TestJointInputParamInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
	}

	/**
	 * 对List<JointInputParamsInstanceEntity>
	 * queryInputParamsInstanceByJointInstanceID(String jointInstanceid, Boolean
	 * required) throws BizException; 方法进行测试，主要观察SQL语句。注意执行计划检查索引使用情况
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test3_queryInputParamsInstanceByJointInstanceID() {
		TestJointInputParamInstanceDAO.LOGGER
				.info("===测试queryInputParamsInstanceByJointInstanceID===");
		List<JointInputParamsInstanceEntity> results = null;
		// 错误测试
		try {
			results = this.jointInputParamsInstanceDAO.queryInputParamsInstanceByJointInstanceID(
					preparedJointInstanceEntity.getUid(), true);
		} catch (BizException e) {
			TestJointInputParamInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		Assert.assertTrue(results == null);
		// 正确测试
		try {
			results = this.jointInputParamsInstanceDAO.queryInputParamsInstanceByJointInstanceID(
					preparedJointInstanceEntity.getUid(), null);
		} catch (BizException e) {
			TestJointInputParamInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		Assert.assertNotNull(results);
		Assert.assertTrue(1 == results.size());
	}

	/**
	 * 对void deleteInputParamInstanceByInstanceID(String inputParamInstanceid)
	 * throws BizException 及void
	 * deleteInputParamsInstancesByJointInstanceID(String jointInstanceid)
	 * throws BizException 方法进行测试，主要观察SQL语句。注意执行计划检查索引使用情况
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test4_deleteInputParamInstanceByInstanceID() {
		TestJointInputParamInstanceDAO.LOGGER.info("===测试删除===");
		try {
			
			// 下面两条语句是互斥的，不能同时执行，需要对当前情况时打开相应语句进行测试
			/*this.jointInputParamsInstanceDAO
					.deleteInputParamInstanceByInstanceID(jointInputParamsInstanceEntity.getUid());*/
			this.jointInputParamsInstanceDAO.deleteInputParamsInstancesByJointInstanceID(
					preparedJointInstanceEntity.getUid());
			
			this.jointInstanceDAO.deleteJointInstance(preparedJointInstanceEntity.getUid());
		} catch (BizException e) {
			TestJointInputParamInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
	}
}
