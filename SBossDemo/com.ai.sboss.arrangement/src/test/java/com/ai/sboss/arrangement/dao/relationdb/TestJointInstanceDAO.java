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
import com.ai.sboss.arrangement.entity.PageEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.utils.JSONUtils;

@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-*.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJointInstanceDAO {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestJointInstanceDAO.class);

	@Autowired
	private IJointInstanceDAO jointInstanceDAO;

	private static JointInstanceEntity preparedJointInstanceEntity;
	private static ArrangementInstanceEntity preparedArrangementInstanceEntity;

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

	}

	@AfterClass
	public static void tearDown() {
		preparedJointInstanceEntity = null;
		preparedArrangementInstanceEntity = null;
	}

	/**
	 * 对 JointInstanceEntity createJointInstance(JointInstanceEntity
	 * jointInstance) throws BizException; 方法进行测试，主要观察SQL语句。
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test1_createJointInstance() {
		JointInstanceEntity jointInstance = null;
		try {
			jointInstance = this.jointInstanceDAO.createJointInstance(preparedJointInstanceEntity);
		} catch (BizException e) {
			TestJointInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}

		Assert.assertNotNull(jointInstance);
	}

	/**
	 * 对 PageEntity
	 * com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceDAO.
	 * queryJointInstancesByUserid(String userid, Integer pageNumber, Integer
	 * perNumber) throws BizException 方法进行测试，主要观察SQL语句。注意执行计划检查索引使用情况
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test2_QueryJointInstancesByUserid() {
		TestJointInstanceDAO.LOGGER.info("===测试queryJointInstancesByUserid===");
		PageEntity pageEntity = null;
		try {
			pageEntity = this.jointInstanceDAO.queryJointInstancesByUserid("deleteaftertest", 0,
					20);
		} catch (BizException e) {
			TestJointInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}

		Assert.assertNotNull(pageEntity);

		// 显示查询信息
		TestJointInstanceDAO.LOGGER.info("getMaxPageRows = " + pageEntity.getMaxPageRows());
		TestJointInstanceDAO.LOGGER.info("getNowPage = " + pageEntity.getNowPage());
		List<Object[]> resultsByObject = pageEntity.getResultsByObject();

		Assert.assertNotNull(resultsByObject);
		Assert.assertTrue(resultsByObject.size() > 0);
		// 转json
		String json = JSONUtils.toString(resultsByObject,
				new String[] { "joint", "arrangementInstance" });
		TestJointInstanceDAO.LOGGER.info("json = " + json);
	}

	/**
	 * 对 List<JointInstanceEntity> queryJointInstancesByBusinessID(String
	 * businessid) throws BizException 方法进行测试，主要观察SQL语句。注意执行计划检查索引使用情况
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test3_queryJointInstancesByBusinessID() {
		TestJointInstanceDAO.LOGGER.info("===测试queryJointInstancesByBusinessID===");
		List<JointInstanceEntity> resultList = null;
		try {
			resultList = this.jointInstanceDAO.queryJointInstancesByBusinessID("21546789");
		} catch (BizException e) {
			TestJointInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		Assert.assertNotNull(resultList);

		// 显示查询信息
		TestJointInstanceDAO.LOGGER.info("resultSize = " + resultList.size());
		// 转json
		String json = JSONUtils.toString(resultList,
				new String[] { "joint", "arrangementInstance" });
		TestJointInstanceDAO.LOGGER.info("json = " + json);
	}

	/**
	 * 对 List<JointInstanceEntity> queryJointInstanceEntityByInstanceID(String
	 * arrangementInstanceuid , String jointStatu) throws BizException
	 * 方法进行测试，主要观察SQL语句。注意执行计划检查索引使用情况
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test4_queryJointInstanceEntityByInstanceID() {
		TestJointInstanceDAO.LOGGER.info("===测试queryJointInstanceEntityByInstanceID===");
		List<JointInstanceEntity> resultList = null;
		// 查询错误情况
		try {
			resultList = this.jointInstanceDAO.queryJointInstanceEntityByInstanceID(
					preparedArrangementInstanceEntity.getUid(), "executing");
		} catch (BizException e) {
			TestJointInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		Assert.assertTrue(resultList == null);

		try {
			resultList = this.jointInstanceDAO.queryJointInstanceEntityByInstanceID(
					preparedArrangementInstanceEntity.getUid(), null);
		} catch (BizException e) {
			TestJointInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		Assert.assertNotNull(resultList);
		Assert.assertTrue(StringUtils.equals("waiting", resultList.get(0).getStatu()));
	}

	/**
	 * 对 void deleteJointInstance(String jointInstanceuid) throws BizException
	 * 方法进行测试，主要观察SQL语句。注意执行计划检查索引使用情况
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test8_deleteJointInstance() {
		TestJointInstanceDAO.LOGGER.info("===删除jointInstance===");
		try {
			this.jointInstanceDAO.deleteJointInstance(preparedJointInstanceEntity.getUid());
		} catch (BizException e) {
			TestJointInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
	}
}
