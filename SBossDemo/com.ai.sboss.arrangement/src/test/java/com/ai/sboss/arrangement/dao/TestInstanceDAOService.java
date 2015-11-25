package com.ai.sboss.arrangement.dao;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceFlowEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.utils.JSONUtils;

import net.sf.json.JSONObject;

/**
 * 测试持久层顶层服务TestInstanceDAOService对外提供服务接口的正确性
 * 
 * @author yinwenjie
 *
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-*.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestInstanceDAOService {
	@Autowired
	private InstanceDAOService instanceDAOService;

	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestInstanceDAOService.class);

	private static JSONObject arrangementJSON;
	private static JointInstanceEntity[] preparedJointInstanceEntities = new JointInstanceEntity[5];
	private static ArrangementInstanceEntity preparedArrangementInstanceEntity;
	private static JointInstanceFlowEntity[] jointInstanceFlowEntities = new JointInstanceFlowEntity[5];
	private static String arrangementInstanceId = null;

	@BeforeClass
	public static void setUp() {
		arrangementJSON = new JSONObject();
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
		arrangementJSON = null;
	}

	/**
	 * 对 String createArrangementInstance(JSONObject arrangementInstance) throws
	 * BizException 方法进行测试，主要观察SQL语句。
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test1_createArrangementInstance() {
		TestInstanceDAOService.LOGGER.info("===测试createArrangementInstance===");
		
		arrangementJSON = JSONUtils.toJSONObject(preparedArrangementInstanceEntity, new String[]{"childArrangementInstances"});
		
		TestInstanceDAOService.LOGGER.info("===转换得到的JSON对象："+arrangementJSON.toString());
		
		try {
			arrangementInstanceId = this.instanceDAOService.createArrangementInstance(arrangementJSON);
		} catch (BizException e) {
			TestInstanceDAOService.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}

		Assert.assertNotNull(arrangementInstanceId);
		TestInstanceDAOService.LOGGER.info("创建流程实例"+arrangementInstanceId+"成功");
	}

	/**
	 * 测试 JSONObject com.ai.sboss.arrangement.engine.dao.InstanceDAOService.
	 * queryJointInstancesByUserid(String userid, Integer nowPage, Integer
	 * maxPageRows) throws BizException 方法
	 */
	@Test
	public void test2_QueryJointInstancesByUserid() {
		JSONObject resultObject = null;
		try {
			resultObject = this.instanceDAOService.queryJointInstancesByUserid("user1", 0, 1);
		} catch (BizException e) {
			TestInstanceDAOService.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}

		String json = resultObject.toString();
		Assert.assertNotNull(json);
		// 转json
		TestInstanceDAOService.LOGGER.info("json = " + json);

		// 再查重复记录，观察对arrangement的查询
		resultObject = null;
		try {
			resultObject = this.instanceDAOService.queryJointInstancesByUserid("user1", 0, 5);
		} catch (BizException e) {
			TestInstanceDAOService.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		json = resultObject.toString();
		Assert.assertNotNull(json);
		// 转json
		TestInstanceDAOService.LOGGER.info("json = " + json);
	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test3_createJointInstanceFlow() 
	{
		TestInstanceDAOService.LOGGER.info("===测试createJointInstanceFlow===");
		try {
			for (int i = 0; i < 5; ++i) {
				JSONObject jointInstanceFlowJSON = JSONUtils.toJSONObject(jointInstanceFlowEntities[i], new String[]{});
				this.instanceDAOService.createJointInstanceFlow(jointInstanceFlowJSON);
			}
		} catch (BizException e) {
			TestInstanceDAOService.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
	}
	
	/**
	 * 对 void deleteArrangementInstance(String arrangementInstanceuid) throws BizException
	 * 方法进行测试，主要观察SQL语句。
	 */
	//数据库表，不轻易做删除操作。
	//@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test8_deleteArrangementInstance() {
		TestInstanceDAOService.LOGGER.info("===测试deleteArrangementInstance===");
		TestInstanceDAOService.LOGGER.info("准备删除流程实例"+arrangementInstanceId);
		try {
			this.instanceDAOService.deleteArrangementInstance(preparedArrangementInstanceEntity.getUid());
		} catch (BizException e) {
			TestInstanceDAOService.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
	}
}