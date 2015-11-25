package com.ai.sboss.arrangement.dao.relationdb;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
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
import com.ai.sboss.arrangement.engine.dao.relationdb.IArrangementDAO;
import com.ai.sboss.arrangement.engine.dao.relationdb.IArrangementJointMappingDAO;
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointDAO;
import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementJointMappingEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * jointDAO持久层测试
 * 
 * @author yinwenjie
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-*.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestArrangementJointMappingDAO {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestArrangementJointMappingDAO.class);

	private static ArrangementEntity preparedArrangementEntity = new ArrangementEntity();
	private static JointEntity preparedJointEntity = new JointEntity();

	/**
	 * 要测试的接口
	 */
	@Autowired
	private IJointDAO jointDAO;
	@Autowired
	private IArrangementJointMappingDAO arrangementJointMappingDAO;
	@Autowired
	private IArrangementDAO arrangementDAO;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/*
		 * 如果启动前需要一些全局化的加载，在这里进行
		 */
		preparedArrangementEntity.setCreator("haha");
		preparedArrangementEntity.setCreatorScope("industry");
		preparedArrangementEntity.setTradeScope("industry");
		preparedArrangementEntity.setUid(java.util.UUID.randomUUID().toString());
		preparedArrangementEntity.setDisplayName("unittestprocess");
		preparedArrangementEntity.setFlows("noflownow");
		preparedArrangementEntity.setTradeid("12345678");

		preparedJointEntity.setUid(java.util.UUID.randomUUID().toString());
		preparedJointEntity.setDisplayName("displayName");
		preparedJointEntity.setAbsOffsettime(1000L);
		preparedJointEntity.setRelateOffsettime(100000L);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		/*
		 * 如果完成后需要一些全局化的清理，在这里进行
		 */
		preparedArrangementEntity = null;
		preparedJointEntity = null;
	}

	/**
	 * 正式的测试代码写在这里
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test1_preparedata() {
		TestArrangementJointMappingDAO.LOGGER.info("====准备测试数据====");
		// 准备数据

		try {
			arrangementDAO.createArrangement(preparedArrangementEntity);
			jointDAO.createJoint(preparedJointEntity);
		} catch (BizException e) {
			TestArrangementJointMappingDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
	}
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test2_bindArrangementJointmapping() {
		TestArrangementJointMappingDAO.LOGGER.info("====测试bindArrangementJointmapping====");
		try {

			Set<ArrangementJointMappingEntity> mappingSet = new HashSet<ArrangementJointMappingEntity>();
			ArrangementJointMappingEntity newmapping = new ArrangementJointMappingEntity();
			newmapping.setJoint(preparedJointEntity);
			newmapping.setParentArrangement(preparedArrangementEntity);
			newmapping.setVisible(true);
			mappingSet.add(newmapping);
			arrangementJointMappingDAO.releaseAllArrangementJointmapping(preparedArrangementEntity.getUid());
			arrangementJointMappingDAO.bindArrangementJointmapping(preparedArrangementEntity.getUid(), mappingSet);
		} catch (BizException e) {
			TestArrangementJointMappingDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test3() {
		TestArrangementJointMappingDAO.LOGGER.info("====测试getArrangementJointmappingSet====");
		List<ArrangementJointMappingEntity> queryMappingEntities = null;
		try {
			queryMappingEntities = arrangementJointMappingDAO.getArrangementJointmappingSet(preparedArrangementEntity.getUid());
		} catch (BizException e) {
			TestArrangementJointMappingDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryMappingEntities);
		assertTrue(queryMappingEntities.size() == 1);
		assertTrue(StringUtils.equals(queryMappingEntities.get(0).getJoint().getDisplayName(), "displayName"));
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test4() {
		TestArrangementJointMappingDAO.LOGGER.info("====测试releaseAllArrangementJointmapping====");
		List<ArrangementJointMappingEntity> queryMappingEntities = null;
		try {
			arrangementJointMappingDAO.releaseAllArrangementJointmapping(preparedArrangementEntity.getUid());
			queryMappingEntities = arrangementJointMappingDAO.getArrangementJointmappingSet(preparedArrangementEntity.getUid());
			jointDAO.deleteJoint(preparedJointEntity.getUid());
			arrangementDAO.deleteArrangement(preparedArrangementEntity.getUid());
		} catch (BizException e) {
			TestArrangementJointMappingDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryMappingEntities == null);
	}
}