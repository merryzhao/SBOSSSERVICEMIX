package com.ai.sboss.arrangement.dao.relationdb;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointInputParamsDAO;
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointOutputParamsDAO;
import com.ai.sboss.arrangement.entity.PageEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointInputParamsEntity;
import com.ai.sboss.arrangement.entity.orm.JointOutputParamsEntity;
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
public class TestJointDAO {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestJointDAO.class);

	/**
	 * 要测试的接口
	 */
	@Autowired
	private IJointDAO jointDAO;
	@Autowired
	private IJointInputParamsDAO jointInputParamsDAO;
	@Autowired
	private IJointOutputParamsDAO jointOutputParamsDAO;

	private static JointEntity[] jointEntities = new JointEntity[6];

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/*
		 * 如果启动前需要一些全局化的加载，在这里进行
		 */
		for (int i = 0; i < 6; ++i) {
			jointEntities[i] = new JointEntity();
			jointEntities[i].setUid(java.util.UUID.randomUUID().toString());
			jointEntities[i].setDisplayName("displayName");
			jointEntities[i].setAbsOffsettime(1000L);
			jointEntities[i].setRelateOffsettime(100000L);
		}

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		/*
		 * 如果完成后需要一些全局化的清理，在这里进行
		 */
	}

	private void createJoints() {
		Set<JointInputParamsEntity> inputParamsEntities = new HashSet<JointInputParamsEntity>();
		JointInputParamsEntity inputParamsEntity = new JointInputParamsEntity();
		inputParamsEntity.setDefaultValue("0");
		inputParamsEntity.setName("inputparam");
		inputParamsEntity.setDisplayName("inputparam");
		inputParamsEntity.setRequired(true);
		inputParamsEntity.setType("String");
		inputParamsEntity.setDisplayType("String");

		Set<JointOutputParamsEntity> outputParamsEntities = new HashSet<JointOutputParamsEntity>();
		JointOutputParamsEntity outputParamsEntity = new JointOutputParamsEntity();
		outputParamsEntity.setDefaultValue("1");
		outputParamsEntity.setName("outputparam");
		outputParamsEntity.setRequired(true);
		outputParamsEntity.setType("String");
		try {
			for (int i = 0; i < 6; ++i) {
				this.jointDAO.createJoint(jointEntities[i]);
				inputParamsEntities.clear();
				inputParamsEntity.setJoint(jointEntities[i]);
				inputParamsEntities.add(inputParamsEntity);
				jointInputParamsDAO.bindJointInputParams(jointEntities[i].getUid(), inputParamsEntities);

				outputParamsEntities.clear();
				outputParamsEntity.setJoint(jointEntities[i]);
				outputParamsEntities.add(outputParamsEntity);
				jointOutputParamsDAO.bindJointOutputParams(jointEntities[i].getUid(), outputParamsEntities);
			}
		} catch (BizException e) {
			TestJointDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
	}

	/**
	 * 正式的测试代码写在这里
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test1_createJoint() {
		TestJointDAO.LOGGER.info("====测试createJoint====");
		createJoints();
		// 断言式
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test2_updateJointTrades() {
		TestJointDAO.LOGGER.info("====测试updateJointTrades====");
		try {
			Map<String, String> tradeMappings = new HashMap<String, String>();
			tradeMappings.put("110120119", "industry");
			this.jointDAO.updateJointTrades(jointEntities[0].getUid(), tradeMappings);
			tradeMappings.clear();
			tradeMappings.put("110120119", "consumer");
			this.jointDAO.updateJointTrades(jointEntities[1].getUid(), tradeMappings);
			tradeMappings.clear();
			tradeMappings.put("110120119", "producer");
			this.jointDAO.updateJointTrades(jointEntities[2].getUid(), tradeMappings);
			tradeMappings.clear();
			tradeMappings.put("110120120", "industry");
			this.jointDAO.updateJointTrades(jointEntities[3].getUid(), tradeMappings);
			tradeMappings.clear();
			tradeMappings.put("110120120", "consumer");
			this.jointDAO.updateJointTrades(jointEntities[4].getUid(), tradeMappings);
			tradeMappings.clear();
			tradeMappings.put("110120120", "producer");
			this.jointDAO.updateJointTrades(jointEntities[5].getUid(), tradeMappings);
		} catch (BizException e) {
			TestJointDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test3_queryJointByTradeid() {
		TestJointDAO.LOGGER.info("====测试queryJointByTradeid====不用scope");

		List<JointEntity> queryJoints = null;
		try {
			queryJoints = this.jointDAO.queryJointByTradeid("110120119", null);
		} catch (BizException e) {
			TestJointDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryJoints);
		TestJointDAO.LOGGER.info("====queryJoints size====>" + queryJoints.size());
		assertTrue(queryJoints.size() == 3);
		assertTrue(StringUtils.equals(queryJoints.get(0).getDisplayName(), "displayName"));
		assertTrue(queryJoints.get(0).getAbsOffsettime() == 1000L);
		assertTrue(queryJoints.get(0).getRelateOffsettime() == 100000L);

		TestJointDAO.LOGGER.info("====测试queryJointByTradeid====使用scope");
		queryJoints = null;
		try {
			queryJoints = this.jointDAO.queryJointByTradeid("110120119", "industry");
		} catch (BizException e) {
			TestJointDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryJoints);
		assertTrue(queryJoints.size() == 1);
		assertTrue(StringUtils.equals(queryJoints.get(0).getDisplayName(), "displayName"));
		assertTrue(queryJoints.get(0).getAbsOffsettime() == 1000L);
		assertTrue(queryJoints.get(0).getRelateOffsettime() == 100000L);

		TestJointDAO.LOGGER.info("====测试queryJointByTradeidPage====不使用scope");
		PageEntity queryJointsPage = null;
		try {
			queryJointsPage = this.jointDAO.queryJointByTradeidPage("110120119", null, 0, 2);
		} catch (BizException e) {
			TestJointDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryJointsPage);
		assertTrue(queryJointsPage.getResults().size() == 2);
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test4_queryJointByDisplayName() {
		TestJointDAO.LOGGER.info("====测试queryJointByDisplayName====");
		JointEntity queryJoint = null;
		try {
			queryJoint = this.jointDAO.queryJointByDisplayName("displayName");
		} catch (BizException e) {
			TestJointDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryJoint);
		assertTrue(StringUtils.equals(queryJoint.getDisplayName(), "displayName"));
		assertTrue(queryJoint.getAbsOffsettime() == 1000L);
		assertTrue(queryJoint.getRelateOffsettime() == 100000L);
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test5_queryJointByDisplayNameWithParams() {
		TestJointDAO.LOGGER.info("====测试queryJointByDisplayNameWithParams====");
		JointEntity queryJoint = null;
		try {
			queryJoint = this.jointDAO.queryJointByDisplayNameWithParams("displayName");
		} catch (BizException e) {
			TestJointDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryJoint);
		Set<JointInputParamsEntity> queryInputParamsEntities = null;
		Set<JointOutputParamsEntity> queryOutputParamsEntities = null;
		Set<JointTradeMappingEntity> queryTradeMappingEntities = null;
		assertTrue(StringUtils.equals(queryJoint.getDisplayName(), "displayName"));
		assertTrue(queryJoint.getAbsOffsettime() == 1000L);
		assertTrue(queryJoint.getRelateOffsettime() == 100000L);
		queryInputParamsEntities = queryJoint.getInputParams();
		assertTrue(queryInputParamsEntities != null);
		assertTrue(queryInputParamsEntities.size() == 1);
		queryOutputParamsEntities = queryJoint.getOutputParams();
		assertTrue(queryOutputParamsEntities != null);
		assertTrue(queryOutputParamsEntities.size() == 1);
		queryTradeMappingEntities = queryJoint.getTrades();
		assertTrue(queryTradeMappingEntities != null);
		assertTrue(queryTradeMappingEntities.size() == 1);

	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test6_updateJoint() {
		TestJointDAO.LOGGER.info("====测试updateJoint====");
		JointEntity queryJoint = null;
		try {
			jointEntities[0].setDisplayName("haha");
			this.jointDAO.updateJoint(jointEntities[0]);
			queryJoint = jointDAO.getJointWithoutParams(jointEntities[0].getUid());
		} catch (BizException e) {
			TestJointDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		} // 断言式 
		assertNotNull(queryJoint);
		assertTrue(!StringUtils.equals(queryJoint.getDisplayName(), "displayName"));
		assertTrue(StringUtils.equals(queryJoint.getDisplayName(), "haha"));
		assertTrue(queryJoint.getAbsOffsettime() == 1000L);
		assertTrue(queryJoint.getRelateOffsettime() == 100000L);
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test7_deleteJoint() {
		List<JointEntity> queryJoints = null;
		TestJointDAO.LOGGER.info("====测试deleteJoint====");
		queryJoints = null;
		try {
			for (int i = 0; i < 6; ++i) {
				this.jointDAO.deleteJoint(jointEntities[i].getUid());
			}
			queryJoints = jointDAO.queryJointByTradeid("110120119", "consumer");
		} catch (BizException e) {
			TestJointDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertTrue(queryJoints == null);

	}
}