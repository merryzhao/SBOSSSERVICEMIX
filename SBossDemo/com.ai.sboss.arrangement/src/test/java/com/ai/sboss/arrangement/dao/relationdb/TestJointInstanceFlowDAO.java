package com.ai.sboss.arrangement.dao.relationdb;

import java.util.HashSet;
import java.util.Set;
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
import com.ai.sboss.arrangement.engine.dao.InstanceDAOService;
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceFlowDAO;
import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceFlowEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.utils.JSONUtils;

import net.sf.json.JSONObject;

@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-*.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJointInstanceFlowDAO {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestJointInstanceFlowDAO.class);

	@Autowired
	private InstanceDAOService instanceDAOService;
	@Autowired
	private IJointInstanceFlowDAO jointInstanceFlowDAO;

	private static JointInstanceEntity[] preparedJointInstanceEntities = new JointInstanceEntity[5];
	private static ArrangementInstanceEntity preparedArrangementInstanceEntity;
	private static JointInstanceFlowEntity[] jointInstanceFlowEntities = new JointInstanceFlowEntity[5];

	@BeforeClass
	public static void setUp() {
		preparedArrangementInstanceEntity = new ArrangementInstanceEntity();
		preparedArrangementInstanceEntity.setUid(UUID.randomUUID().toString());
		preparedArrangementInstanceEntity.setCreator("consumer1");
		preparedArrangementInstanceEntity.setCreateTime(1440691200000L);
		ArrangementEntity arrangement = new ArrangementEntity();
		arrangement.setUid("4eb9e271-c9fc-4153-b33e-ce631acda97d");
		preparedArrangementInstanceEntity.setArrangement(arrangement);
		preparedArrangementInstanceEntity.setStatu("waiting");
		preparedArrangementInstanceEntity.setDisplayName("hahafake");
		preparedArrangementInstanceEntity.setCreatorScope("industry");
		preparedArrangementInstanceEntity.setBusinessID("21546789");

		Set<JointInstanceEntity> jointInstances = new HashSet<JointInstanceEntity>();

		JointEntity joint = new JointEntity();
		joint.setUid("a92bf03a-b8e0-41a4-9df4-6c11c49a8da4");
		preparedJointInstanceEntities[0] = new JointInstanceEntity();
		preparedJointInstanceEntities[0].setUid(UUID.randomUUID().toString());
		preparedJointInstanceEntities[0].setCreator("deleteaftertest1");
		preparedJointInstanceEntities[0].setAbsOffsettime(120L);
		preparedJointInstanceEntities[0].setArrangementInstance(preparedArrangementInstanceEntity);
		preparedJointInstanceEntities[0].setJoint(joint);
		preparedJointInstanceEntities[0].setExpectedExeTime(123L);
		preparedJointInstanceEntities[0].setStatu("waiting");
		preparedJointInstanceEntities[0].setExeTime(null);
		jointInstances.add(preparedJointInstanceEntities[0]);
		jointInstanceFlowEntities[0] = new JointInstanceFlowEntity();
		jointInstanceFlowEntities[0].setUid(UUID.randomUUID().toString());
		jointInstanceFlowEntities[0].setJointInstance(preparedJointInstanceEntities[0]);
		jointInstanceFlowEntities[0].setJoint(preparedJointInstanceEntities[0].getJoint());
		jointInstanceFlowEntities[0].setExecutor(preparedJointInstanceEntities[0].getExecutor());
		jointInstanceFlowEntities[0].setExpectedExeTime(preparedJointInstanceEntities[0].getExpectedExeTime());
		jointInstanceFlowEntities[0].setStatu(preparedJointInstanceEntities[0].getStatu());

		joint = new JointEntity();
		joint.setUid("59828a0a-7eba-4416-93ed-60f4a6e919b8");
		preparedJointInstanceEntities[1] = new JointInstanceEntity();
		preparedJointInstanceEntities[1].setUid(UUID.randomUUID().toString());
		preparedJointInstanceEntities[1].setCreator("deleteaftertest2");
		preparedJointInstanceEntities[1].setAbsOffsettime(130L);
		preparedJointInstanceEntities[1].setArrangementInstance(preparedArrangementInstanceEntity);
		preparedJointInstanceEntities[1].setJoint(joint);
		preparedJointInstanceEntities[1].setExpectedExeTime(123L);
		preparedJointInstanceEntities[1].setStatu("waiting");
		preparedJointInstanceEntities[1].setExeTime(null);
		jointInstances.add(preparedJointInstanceEntities[1]);
		jointInstanceFlowEntities[1] = new JointInstanceFlowEntity();
		jointInstanceFlowEntities[1].setUid(UUID.randomUUID().toString());
		jointInstanceFlowEntities[1].setJointInstance(preparedJointInstanceEntities[1]);
		jointInstanceFlowEntities[1].setJoint(preparedJointInstanceEntities[1].getJoint());
		jointInstanceFlowEntities[1].setExecutor(preparedJointInstanceEntities[1].getExecutor());
		jointInstanceFlowEntities[1].setExpectedExeTime(preparedJointInstanceEntities[1].getExpectedExeTime());
		jointInstanceFlowEntities[1].setStatu(preparedJointInstanceEntities[1].getStatu());

		joint = new JointEntity();
		joint.setUid("dfc64d73-322f-4230-a468-bc0c800dcf82");
		preparedJointInstanceEntities[2] = new JointInstanceEntity();
		preparedJointInstanceEntities[2].setUid(UUID.randomUUID().toString());
		preparedJointInstanceEntities[2].setCreator("deleteaftertest3");
		preparedJointInstanceEntities[2].setAbsOffsettime(140L);
		preparedJointInstanceEntities[2].setArrangementInstance(preparedArrangementInstanceEntity);
		preparedJointInstanceEntities[2].setJoint(joint);
		preparedJointInstanceEntities[2].setExpectedExeTime(123L);
		preparedJointInstanceEntities[2].setStatu("waiting");
		preparedJointInstanceEntities[2].setExeTime(null);
		jointInstances.add(preparedJointInstanceEntities[2]);
		jointInstanceFlowEntities[2] = new JointInstanceFlowEntity();
		jointInstanceFlowEntities[2].setUid(UUID.randomUUID().toString());
		jointInstanceFlowEntities[2].setJointInstance(preparedJointInstanceEntities[2]);
		jointInstanceFlowEntities[2].setJoint(preparedJointInstanceEntities[2].getJoint());
		jointInstanceFlowEntities[2].setExecutor(preparedJointInstanceEntities[2].getExecutor());
		jointInstanceFlowEntities[2].setExpectedExeTime(preparedJointInstanceEntities[2].getExpectedExeTime());
		jointInstanceFlowEntities[2].setStatu(preparedJointInstanceEntities[2].getStatu());

		joint = new JointEntity();
		joint.setUid("548faa57-3bb5-4437-8ccc-d5f683784d5e");
		preparedJointInstanceEntities[3] = new JointInstanceEntity();
		preparedJointInstanceEntities[3].setUid(UUID.randomUUID().toString());
		preparedJointInstanceEntities[3].setCreator("deleteaftertest4");
		preparedJointInstanceEntities[3].setAbsOffsettime(150L);
		preparedJointInstanceEntities[3].setArrangementInstance(preparedArrangementInstanceEntity);
		preparedJointInstanceEntities[3].setJoint(joint);
		preparedJointInstanceEntities[3].setExpectedExeTime(123L);
		preparedJointInstanceEntities[3].setStatu("waiting");
		preparedJointInstanceEntities[3].setExeTime(null);
		jointInstances.add(preparedJointInstanceEntities[3]);
		jointInstanceFlowEntities[3] = new JointInstanceFlowEntity();
		jointInstanceFlowEntities[3].setUid(UUID.randomUUID().toString());
		jointInstanceFlowEntities[3].setJointInstance(preparedJointInstanceEntities[3]);
		jointInstanceFlowEntities[3].setJoint(preparedJointInstanceEntities[3].getJoint());
		jointInstanceFlowEntities[3].setExecutor(preparedJointInstanceEntities[3].getExecutor());
		jointInstanceFlowEntities[3].setExpectedExeTime(preparedJointInstanceEntities[3].getExpectedExeTime());
		jointInstanceFlowEntities[3].setStatu(preparedJointInstanceEntities[3].getStatu());

		joint = new JointEntity();
		joint.setUid("0aacd4a3-d2c3-46ba-bc94-be0fc036a8d3");
		preparedJointInstanceEntities[4] = new JointInstanceEntity();
		preparedJointInstanceEntities[4].setUid(UUID.randomUUID().toString());
		preparedJointInstanceEntities[4].setCreator("deleteaftertest5");
		preparedJointInstanceEntities[4].setAbsOffsettime(160L);
		preparedJointInstanceEntities[4].setArrangementInstance(preparedArrangementInstanceEntity);
		preparedJointInstanceEntities[4].setJoint(joint);
		preparedJointInstanceEntities[4].setExpectedExeTime(123L);
		preparedJointInstanceEntities[4].setStatu("waiting");
		preparedJointInstanceEntities[4].setExeTime(null);
		jointInstances.add(preparedJointInstanceEntities[4]);
		preparedArrangementInstanceEntity.setJointInstances(jointInstances);
		jointInstanceFlowEntities[4] = new JointInstanceFlowEntity();
		jointInstanceFlowEntities[4].setUid(UUID.randomUUID().toString());
		jointInstanceFlowEntities[4].setJointInstance(preparedJointInstanceEntities[4]);
		jointInstanceFlowEntities[4].setJoint(preparedJointInstanceEntities[4].getJoint());
		jointInstanceFlowEntities[4].setExecutor(preparedJointInstanceEntities[4].getExecutor());
		jointInstanceFlowEntities[4].setExpectedExeTime(preparedJointInstanceEntities[4].getExpectedExeTime());
		jointInstanceFlowEntities[4].setStatu(preparedJointInstanceEntities[4].getStatu());

		jointInstanceFlowEntities[0].setPreviouJointInstance(null);
		jointInstanceFlowEntities[0].setNextJointInstance(preparedJointInstanceEntities[1]);
		jointInstanceFlowEntities[1].setPreviouJointInstance(preparedJointInstanceEntities[0]);
		jointInstanceFlowEntities[1].setNextJointInstance(preparedJointInstanceEntities[2]);
		jointInstanceFlowEntities[2].setPreviouJointInstance(preparedJointInstanceEntities[1]);
		jointInstanceFlowEntities[2].setNextJointInstance(preparedJointInstanceEntities[3]);
		jointInstanceFlowEntities[3].setPreviouJointInstance(preparedJointInstanceEntities[2]);
		jointInstanceFlowEntities[3].setNextJointInstance(preparedJointInstanceEntities[4]);
		jointInstanceFlowEntities[4].setPreviouJointInstance(preparedJointInstanceEntities[3]);
		jointInstanceFlowEntities[4].setNextJointInstance(null);

	}

	@AfterClass
	public static void tearDown() {
		for (int i = 0; i < 5; ++i) {
			preparedJointInstanceEntities[i] = null;
		}
		preparedArrangementInstanceEntity = null;
	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test0_createJointInstanceFlow() {
		TestJointInstanceFlowDAO.LOGGER.info("===准备测试数据===");
		JSONObject instanceDataJSON = JSONUtils.toJSONObject(preparedArrangementInstanceEntity,
				new String[] { "parentInstance", "childArrangementInstances" });
		String id = null;
		try {
			id = this.instanceDAOService.createArrangementInstance(instanceDataJSON);
		} catch (BizException e) {
			TestJointInstanceFlowDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		Assert.assertNotNull(id);
	}

	/**
	 * 对 JointInstanceFlowEntity createJointInstanceFlow(JointInstanceFlowEntity
	 * jointInstanceFlow) throws BizException 方法进行测试，主要观察SQL语句。
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test1_createJointInstanceFlow() {
		TestJointInstanceFlowDAO.LOGGER.info("===测试createJointInstanceFlow===");
		try {
			for (int i = 0; i < 5; ++i) {
				this.jointInstanceFlowDAO.createJointInstanceFlow(jointInstanceFlowEntities[i]);
			}
		} catch (BizException e) {
			TestJointInstanceFlowDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
	}
	
	/**
	 * 对 JointInstanceFlowEntity createJointInstanceFlow(JointInstanceFlowEntity
	 * jointInstanceFlow) throws BizException 方法进行测试，主要观察SQL语句。
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test2_updateJointInstanceStatu() {
		TestJointInstanceFlowDAO.LOGGER.info("===测试updateJointInstanceStatu===");
		JointInstanceFlowEntity queryflowEntity = null;
		try {
			this.jointInstanceFlowDAO.updateJointFlowStatuByFlowId(jointInstanceFlowEntities[0].getUid(), "executing");
			queryflowEntity = this.jointInstanceFlowDAO.getEntity(jointInstanceFlowEntities[0].getUid());
		} catch (BizException e) {
			TestJointInstanceFlowDAO.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		Assert.assertTrue(!StringUtils.equals("waiting", queryflowEntity.getStatu()));
		Assert.assertTrue(StringUtils.equals("executing", queryflowEntity.getStatu()));
	}

	/**
	 * 对 delete 方法进行测试，主要观察SQL语句。注意执行计划检查索引使用情况
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test8_delete() {
		TestJointInstanceFlowDAO.LOGGER.info("===测试deleteJointInstanceFlow===");
		try {
			instanceDAOService.deleteArrangementInstance(preparedArrangementInstanceEntity.getUid());
		} catch (BizException e) {
			Assert.assertTrue(false);
		}
		for (int i = 0; i < 5; ++i) {
			this.jointInstanceFlowDAO.delete(jointInstanceFlowEntities[i].getUid());
		}
	}
}
