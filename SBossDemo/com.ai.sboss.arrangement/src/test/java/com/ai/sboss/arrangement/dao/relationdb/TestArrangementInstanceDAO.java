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
import com.ai.sboss.arrangement.engine.dao.relationdb.IArrangementInstanceDAO;
import com.ai.sboss.arrangement.entity.PageEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-*.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestArrangementInstanceDAO {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestArrangementInstanceDAO.class);
	
	@Autowired
	private IArrangementInstanceDAO arrangementInstanceDAO;
	
	private static JointInstanceEntity preparedJointInstanceEntity;
	private static ArrangementInstanceEntity preparedArrangementInstanceEntity;

	@BeforeClass
	public static void setUp() {
		preparedArrangementInstanceEntity = new ArrangementInstanceEntity();
		preparedArrangementInstanceEntity.setUid(UUID.randomUUID().toString());
		preparedArrangementInstanceEntity.setCreator("consumer1");
		preparedArrangementInstanceEntity.setCreateTime(1440691200000L);
		preparedArrangementInstanceEntity.setStatu("1");
		preparedArrangementInstanceEntity.setDisplayName("hahafake");
		preparedArrangementInstanceEntity.setCreatorScope("industry");
		preparedArrangementInstanceEntity.setBusinessID("21546790");
		ArrangementEntity arrangement = new ArrangementEntity();
		arrangement.setUid("4eb9e271-c9fc-4153-b33e-ce631acda97d");
		preparedArrangementInstanceEntity.setArrangement(arrangement);

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
	 * 对 ArrangementInstanceEntity createArrangementInstance(ArrangementInstanceEntity arrangementInstance) throws BizException 
	 * 方法进行测试，主要观察SQL语句。
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test1_createArrangementInstance() {
		TestArrangementInstanceDAO.LOGGER.info("===测试createArrangementInstance===");
		ArrangementInstanceEntity arrangementInstance = null;
		try {
			arrangementInstance = this.arrangementInstanceDAO.createArrangementInstance(preparedArrangementInstanceEntity);
		} catch (BizException e) {
			TestArrangementInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}

		Assert.assertNotNull(arrangementInstance);
	}
	
	/**
	 * 对 PageEntity queryArrangementInstancesByUserid(String userid, String statu, Integer pageNumber , Integer perNumber) throws BizException  
	 * 方法进行测试，主要观察SQL语句。
	 */
	@SuppressWarnings("unchecked")
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test2_queryArrangementInstancesByUserid() {
		TestArrangementInstanceDAO.LOGGER.info("===测试queryArrangementInstancesByUserid===");
		PageEntity result = null;
		try {
			result = this.arrangementInstanceDAO.queryArrangementInstancesByUserid("consumer1", null, 0, 20);
		} catch (BizException e) {
			TestArrangementInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}

		Assert.assertNotNull(result);
		List<ArrangementInstanceEntity> instanceresult = (List<ArrangementInstanceEntity>) result.getResults();
		Assert.assertTrue(instanceresult.size() > 0);
	}
	
	/**
	 * 对 List<ArrangementInstanceEntity> queryArrangementInstancesByBusinessID(String businessid, String statu) throws BizException   
	 * 方法进行测试，主要观察SQL语句。
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test3_queryArrangementInstancesByBusinessID() {
		TestArrangementInstanceDAO.LOGGER.info("===测试queryArrangementInstancesByBusinessID===");
		ArrangementInstanceEntity result = null;
		//错误查询
		try {
			result = this.arrangementInstanceDAO.queryArrangementInstancesByBusinessID("21546790");
		} catch (BizException e) {
			TestArrangementInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		Assert.assertTrue(result == null);

		//正确查询
		try {
			result = this.arrangementInstanceDAO.queryArrangementInstancesByBusinessID("21546790");
		} catch (BizException e) {
			TestArrangementInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		Assert.assertNotNull(result);
	}
	
	/**
	 * 对 void updateArrangementInstance(ArrangementInstanceEntity arrangementInstance) throws BizException;   
	 * 方法进行测试，主要观察SQL语句。
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test4_updateArrangementInstance() {
		TestArrangementInstanceDAO.LOGGER.info("===测试updateArrangementInstance===");
		//错误查询
		preparedArrangementInstanceEntity.setCreator(null);
		try {
			this.arrangementInstanceDAO.updateArrangementInstance(preparedArrangementInstanceEntity);
		} catch (BizException e) {
			Assert.assertTrue(e.getResponseCode() == ResponseCode._402);
		}

		//正确查询
		preparedArrangementInstanceEntity.setCreator("new Name");
		ArrangementInstanceEntity result = null;
		try {
			this.arrangementInstanceDAO.updateArrangementInstance(preparedArrangementInstanceEntity);
			result = this.arrangementInstanceDAO.getEntity(preparedArrangementInstanceEntity.getUid());
		} catch (BizException e) {
			TestArrangementInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		Assert.assertNotNull(result);
		Assert.assertTrue(StringUtils.equals("new Name", result.getCreator()));
	}
	
	
	/**
	 * 对 void deleteArrangementInstance(String arrangementInstanceuid) throws BizException 
	 * 方法进行测试，主要观察SQL语句。
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test8_deleteArrangementInstance() {
		TestArrangementInstanceDAO.LOGGER.info("===测试deleteArrangementInstance===");
		try {
			this.arrangementInstanceDAO.deleteArrangementInstance(preparedArrangementInstanceEntity.getUid());
		} catch (BizException e) {
			TestArrangementInstanceDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
	}
}
