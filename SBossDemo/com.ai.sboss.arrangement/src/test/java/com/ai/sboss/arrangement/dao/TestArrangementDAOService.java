package com.ai.sboss.arrangement.dao;

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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.ai.sboss.arrangement.engine.dao.ArrangementDAOService;
import com.ai.sboss.arrangement.engine.dao.relationdb.IArrangementJointMappingDAO;
import com.ai.sboss.arrangement.engine.dao.relationdb.IArrangementSelfMappingDAO;
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointDAO;
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointInputParamsDAO;
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointOutputParamsDAO;
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointTradeMappingDAO;
import com.ai.sboss.arrangement.entity.PageEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementJointMappingEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementSelfMappingEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointInputParamsEntity;
import com.ai.sboss.arrangement.entity.orm.JointTradeMappingEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * arrangementDAO持久层测试
 * 
 * @author chaos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-*.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestArrangementDAOService {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestArrangementDAOService.class);

	/**
	 * 要测试的接口
	 */
	@Autowired
	private IJointDAO jointDAO;
	@Autowired
	private IJointInputParamsDAO jointInputParamsDAO;
	@Autowired
	private IJointOutputParamsDAO jointOutputParamsDAO;
	@Autowired
	private IArrangementJointMappingDAO arrangementJointMappingDAO;
	@Autowired
	private IArrangementSelfMappingDAO arrangementSelfMappingDAO;
	@Autowired
	private ArrangementDAOService arrangementDAOService;
	@Autowired
	private IJointTradeMappingDAO jointTradeMappingDAO;

	private static JointEntity parentJointEntity = new JointEntity();
	private static JointEntity childJointEntity = new JointEntity();
	private static JointEntity extendJointEntity = new JointEntity();
	private static ArrangementEntity parentArrangement = new ArrangementEntity();
	private static ArrangementEntity childArrangement = new ArrangementEntity();
	private static JointInputParamsEntity parentjointInputEntity = new JointInputParamsEntity();
	private static JointInputParamsEntity childjointInputEntity = new JointInputParamsEntity();

	private static JointTradeMappingEntity parentTradeEntity = new JointTradeMappingEntity();
	private static JointTradeMappingEntity childTradeEntity = new JointTradeMappingEntity();

	private static ArrangementJointMappingEntity childJointMapping = new ArrangementJointMappingEntity();
	private static ArrangementJointMappingEntity parentJointMapping = new ArrangementJointMappingEntity();
	private static ArrangementJointMappingEntity extendJointMapping = new ArrangementJointMappingEntity();
	
	private static ArrangementSelfMappingEntity arrangementSelfMappingEntity = new ArrangementSelfMappingEntity();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/*
		 * 如果启动前需要一些全局化的加载，在这里进行
		 */
		parentJointEntity.setUid(java.util.UUID.randomUUID().toString());
		parentJointEntity.setDisplayName("parent_joint");
		parentJointEntity.setAbsOffsettime(1000L);
		parentJointEntity.setRelateOffsettime(100000L);

		childJointEntity.setUid(java.util.UUID.randomUUID().toString());
		childJointEntity.setDisplayName("child_joint");
		childJointEntity.setAbsOffsettime(1000L);
		childJointEntity.setRelateOffsettime(100000L);
		
		extendJointEntity.setUid(java.util.UUID.randomUUID().toString());
		extendJointEntity.setDisplayName("extend_joint");
		extendJointEntity.setAbsOffsettime(1000L);
		extendJointEntity.setRelateOffsettime(100000L);
		
		parentjointInputEntity.setDefaultValue("1");
		parentjointInputEntity.setName("parentjointinputparam");
		parentjointInputEntity.setDisplayName("parentjointinputparam");
		parentjointInputEntity.setRequired(true);
		parentjointInputEntity.setType("String");
		parentjointInputEntity.setDisplayType("String");
		parentjointInputEntity.setJoint(parentJointEntity);
		parentjointInputEntity.setUid(java.util.UUID.randomUUID().toString());

		childjointInputEntity.setDefaultValue("2");
		childjointInputEntity.setName("childjointinputparam");
		childjointInputEntity.setDisplayName("childjointinputparam");
		childjointInputEntity.setRequired(true);
		childjointInputEntity.setType("String");
		childjointInputEntity.setDisplayType("String");
		childjointInputEntity.setUid(java.util.UUID.randomUUID().toString());
		childjointInputEntity.setJoint(childJointEntity);

		parentTradeEntity.setJoint(parentJointEntity);
		parentTradeEntity.setScope("industry");
		parentTradeEntity.setTradeid("10000");
		parentTradeEntity.setUid(java.util.UUID.randomUUID().toString());

		childTradeEntity.setJoint(childJointEntity);
		childTradeEntity.setScope("consumer");
		childTradeEntity.setTradeid("10000");
		childTradeEntity.setUid(java.util.UUID.randomUUID().toString());

		parentArrangement.setCreator("haha");
		parentArrangement.setUid(java.util.UUID.randomUUID().toString());
		parentArrangement.setCreatorScope("base");
		parentArrangement.setDisplayName("parentArrangement");
		parentArrangement.setFlows("parent_flow");
		parentArrangement.setTradeid("10000");
		parentArrangement.setTradeScope("industry");

		childArrangement.setCreator("heihei");
		childArrangement.setUid(java.util.UUID.randomUUID().toString());
		childArrangement.setCreatorScope("extend");
		childArrangement.setDisplayName("childArrangement");
		childArrangement.setFlows("child_flow");
		childArrangement.setTradeid("10000");
		childArrangement.setTradeScope("consumer");

		Set<JointInputParamsEntity> inputParamsEntities = new HashSet<JointInputParamsEntity>();
		inputParamsEntities.add(childjointInputEntity);
		childJointEntity.setInputParams(inputParamsEntities);

		Set<JointInputParamsEntity> inputParamsEntities2 = new HashSet<JointInputParamsEntity>();
		inputParamsEntities2.add(parentjointInputEntity);
		parentJointEntity.setInputParams(inputParamsEntities2);

		parentJointMapping.setAbsOffsettime(100L);
		parentJointMapping.setJoint(parentJointEntity);
		parentJointMapping.setParentArrangement(parentArrangement);
		parentJointMapping.setRelateOffsettime(1000L);
		parentJointMapping.setVisible(true);
		parentJointMapping.setUid(java.util.UUID.randomUUID().toString());
		
		extendJointMapping.setAbsOffsettime(100L);
		extendJointMapping.setJoint(extendJointEntity);
		extendJointMapping.setParentArrangement(parentArrangement);
		extendJointMapping.setRelateOffsettime(1000L);
		extendJointMapping.setVisible(true);
		extendJointMapping.setUid(java.util.UUID.randomUUID().toString());

		childJointMapping.setAbsOffsettime(100L);
		childJointMapping.setJoint(childJointEntity);
		childJointMapping.setParentArrangement(childArrangement);
		childJointMapping.setRelateOffsettime(1000L);
		childJointMapping.setVisible(true);
		childJointMapping.setUid(java.util.UUID.randomUUID().toString());
		
		arrangementSelfMappingEntity.setArrangement(childArrangement);
		arrangementSelfMappingEntity.setParentArrangement(parentArrangement);
		arrangementSelfMappingEntity.setUid(java.util.UUID.randomUUID().toString());
		arrangementSelfMappingEntity.setVisible(true);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		/*
		 * 如果测试完成后需要一些全局化的清理工作，在这里进行
		 */
		parentJointEntity = null;
		childJointEntity = null;
		parentArrangement = null;
		childArrangement = null;
		parentjointInputEntity = null;
		childjointInputEntity = null;
		parentTradeEntity = null;
		childTradeEntity = null;
		childJointMapping = null;
		parentJointMapping = null;
	}

	/**
	 * 正式的测试代码写在这里
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test1_createArrangement() {
		TestArrangementDAOService.LOGGER.info("====测试createArrangement====");

		// 准备数据
		Set<ArrangementJointMappingEntity> childJointMappingSet = new HashSet<ArrangementJointMappingEntity>();
		childJointMappingSet.add(childJointMapping);
		Set<ArrangementJointMappingEntity> parentJointMappingSet = new HashSet<ArrangementJointMappingEntity>();
		parentJointMappingSet.add(parentJointMapping);
		childArrangement.setJointmapping(childJointMappingSet);
		parentArrangement.setJointmapping(parentJointMappingSet);
		Set<ArrangementSelfMappingEntity> childArrangementMappingSet = new HashSet<ArrangementSelfMappingEntity>();
		childArrangementMappingSet.add(arrangementSelfMappingEntity);
		parentArrangement.setChildArrangements(childArrangementMappingSet);

		try {
			jointDAO.createJoint(childJointEntity);
			jointDAO.createJoint(parentJointEntity);
			jointDAO.createJoint(extendJointEntity);
			arrangementDAOService.createArrangement(childArrangement);
			arrangementDAOService.createArrangement(parentArrangement);
		} catch (BizException e) {
			TestArrangementDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test2_queryArrangementByTradeidWithoutSet() {
		TestArrangementDAOService.LOGGER.info("====测试queryArrangementByTradeidWithoutSet====");

		List<ArrangementEntity> queryArrangementEntities = null;
		try {
			queryArrangementEntities = arrangementDAOService.queryArrangementByTradeidWithoutSet("10005", null);
		} catch (BizException e) {
			TestArrangementDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertTrue(queryArrangementEntities == null);

		// 准备数据
		queryArrangementEntities = null;
		try {
			queryArrangementEntities = arrangementDAOService.queryArrangementByTradeidWithoutSet("10000", null);
		} catch (BizException e) {
			TestArrangementDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryArrangementEntities);
		assertTrue(queryArrangementEntities.size() == 2);
		
		queryArrangementEntities = null;
		try {
			queryArrangementEntities = arrangementDAOService.queryArrangementByTradeidWithoutSet("10000", "industry");
		} catch (BizException e) {
			TestArrangementDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryArrangementEntities);
		assertTrue(queryArrangementEntities.size() == 1);
		assertTrue(StringUtils.equals(queryArrangementEntities.get(0).getFlows(), "parent_flow"));
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test3_queryArrangementByTradeidPageWithoutSet() {
		TestArrangementDAOService.LOGGER.info("====测试queryArrangementByTradeidPageWithoutSet====");

		// 准备数据
		PageEntity queryEntity = null;
		try {
			queryEntity = arrangementDAOService.queryArrangementByTradeidPageWithoutSet("10000", null, 0, 1);
		} catch (BizException e) {
			TestArrangementDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryEntity);
		assertNotNull(queryEntity.getResults());
		assertTrue(queryEntity.getResults().size() == 1);
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test4_getArrangementWithoutSet() {
		TestArrangementDAOService.LOGGER.info("====测试getArrangementWithoutSet====");

		// 准备数据
		ArrangementEntity queryArrangementEntity = null;
		try {
			queryArrangementEntity = arrangementDAOService.getArrangementWithoutSet(parentArrangement.getUid());
		} catch (BizException e) {
			TestArrangementDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryArrangementEntity);
		assertTrue(StringUtils.equals(queryArrangementEntity.getFlows(), "parent_flow"));
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test5_getArrangementWithSet() {
		TestArrangementDAOService.LOGGER.info("====测试getArrangementWithSet====");

		// 准备数据
		ArrangementEntity queryArrangementEntity = null;
		try {
			queryArrangementEntity = arrangementDAOService.getArrangementWithSet(parentArrangement.getUid());
		} catch (BizException e) {
			TestArrangementDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryArrangementEntity);
		assertTrue(StringUtils.equals(queryArrangementEntity.getFlows(), "parent_flow"));
		Set<ArrangementJointMappingEntity> queryJointEntities = queryArrangementEntity.getJointmapping();
		assertTrue(queryJointEntities.size() == 1);
		Set<ArrangementSelfMappingEntity> queryChildMapping = queryArrangementEntity.getChildArrangements();
		assertTrue(queryChildMapping.size() == 1);
		
		for (ArrangementJointMappingEntity jointMappingEntity: queryJointEntities) {
			assertTrue(StringUtils.equals(jointMappingEntity.getJoint().getUid(), parentJointEntity.getUid()));
		}
		
		for (ArrangementSelfMappingEntity childMappingEntity: queryChildMapping) {
			assertTrue(StringUtils.equals(childMappingEntity.getArrangement().getUid(), childArrangement.getUid()));
		}
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test6_updateArrangement() {
		TestArrangementDAOService.LOGGER.info("====测试updateArrangement====");

		
		// 准备数据
		parentArrangement.setDisplayName("new_parent_arrangement_name");
		ArrangementEntity queryArrangementEntity = null;
		try {
			arrangementDAOService.updateArrangement(parentArrangement);
			queryArrangementEntity = arrangementDAOService.getArrangementWithoutSet(parentArrangement.getUid());
		} catch (BizException e) {
			TestArrangementDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryArrangementEntity);
		assertTrue(!StringUtils.equals(queryArrangementEntity.getDisplayName(), "parentArrangement"));
		assertTrue(StringUtils.equals(queryArrangementEntity.getDisplayName(), "new_parent_arrangement_name"));
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test7_updateArrangementJointmapping() {
		TestArrangementDAOService.LOGGER.info("====测试updateArrangementJointmapping====");

		// 准备数据
		Set<ArrangementJointMappingEntity> parentJointMappingSet = new HashSet<ArrangementJointMappingEntity>();
		parentJointMapping.setUid(java.util.UUID.randomUUID().toString());
		parentJointMappingSet.add(parentJointMapping);
		parentJointMappingSet.add(extendJointMapping);
		ArrangementEntity queryArrangementEntity = null;
		try {
			arrangementDAOService.updateArrangementJointmapping(parentArrangement.getUid(), parentJointMappingSet);
			queryArrangementEntity = arrangementDAOService.getArrangementWithSet(parentArrangement.getUid());
		} catch (BizException e) {
			TestArrangementDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		Set<ArrangementJointMappingEntity> queryJointEntities = queryArrangementEntity.getJointmapping();
		assertTrue(queryJointEntities.size() == 2);
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test8_updateArrangementChildArrangements() {
		TestArrangementDAOService.LOGGER.info("====测试updateArrangementChildArrangements====");

		// 准备数据
		Set<ArrangementSelfMappingEntity> childArrangementMappingSet = new HashSet<ArrangementSelfMappingEntity>();
		String olduid = arrangementSelfMappingEntity.getUid();
		arrangementSelfMappingEntity.setUid(java.util.UUID.randomUUID().toString());
		childArrangementMappingSet.add(arrangementSelfMappingEntity);
		parentArrangement.setChildArrangements(childArrangementMappingSet);
		ArrangementEntity queryArrangementEntity = null;
		try {
			arrangementDAOService.updateArrangementChildArrangements(parentArrangement.getUid(), childArrangementMappingSet);
			queryArrangementEntity = arrangementDAOService.getArrangementWithSet(parentArrangement.getUid());
		} catch (BizException e) {
			assertTrue(e.getResponseCode() == ResponseCode._401);
		}
		// 断言式
		Set<ArrangementSelfMappingEntity> queryChildMapping = queryArrangementEntity.getChildArrangements();
		assertTrue(queryChildMapping.size() == 1);
		for (ArrangementSelfMappingEntity child:queryChildMapping) {
			assertTrue(!StringUtils.equals(olduid, child.getUid()));
			assertTrue(StringUtils.equals(arrangementSelfMappingEntity.getUid(), child.getUid()));
		}
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test9_deleteArrangement() {
		TestArrangementDAOService.LOGGER.info("====测试deleteArrangement====");

		// 准备数据
		try {
			jointTradeMappingDAO.releaseAllJointTradeMapping(parentArrangement.getUid());
			jointTradeMappingDAO.releaseAllJointTradeMapping(childArrangement.getUid());
			jointInputParamsDAO.releaseAllJointInputParams(parentArrangement.getUid());
			jointInputParamsDAO.releaseAllJointInputParams(childArrangement.getUid());
			jointOutputParamsDAO.releaseAllJointOutputParams(parentArrangement.getUid());
			jointOutputParamsDAO.releaseAllJointOutputParams(childArrangement.getUid());

			arrangementSelfMappingDAO.releaseAllArrangementChildArrangements(parentArrangement.getUid());
			arrangementJointMappingDAO.releaseAllArrangementJointmapping(childArrangement.getUid());
			arrangementJointMappingDAO.releaseAllArrangementJointmapping(parentArrangement.getUid());

			jointDAO.deleteJoint(childJointEntity.getUid());
			jointDAO.deleteJoint(extendJointEntity.getUid());
			jointDAO.deleteJoint(parentJointEntity.getUid());

			arrangementDAOService.deleteArrangement(parentArrangement.getUid());
			arrangementDAOService.deleteArrangement(childArrangement.getUid());
		} catch (BizException e) {
			TestArrangementDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
	}
}