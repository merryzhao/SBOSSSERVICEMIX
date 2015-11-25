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
import com.ai.sboss.arrangement.engine.dao.relationdb.IArrangementSelfMappingDAO;
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointDAO;
import com.ai.sboss.arrangement.entity.PageEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementJointMappingEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementSelfMappingEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * arrangementDAO持久层测试
 * 
 * @author chaos
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-*.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestArrangementDAO {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestArrangementDAO.class);

	/**
	 * 测试用到的接口
	 */
	@Autowired
	private IArrangementSelfMappingDAO arrangementSelfMappingDAO;
	@Autowired
	private IArrangementDAO arrangementDAO;
	@Autowired
	private IJointDAO jointDAO;
	@Autowired
	private IArrangementJointMappingDAO arrangementJointMappingDAO;
	
	private static ArrangementEntity preparedParentArrangementEntity = new ArrangementEntity();
	private static ArrangementEntity preparedoldchildArrangementEntity = new ArrangementEntity();
	private static ArrangementEntity preparednewchildArrangementEntity = new ArrangementEntity();

	private static JointEntity parentJointEntity = new JointEntity();
	private static JointEntity child1JointEntity = new JointEntity();
	private static JointEntity child2JointEntity = new JointEntity();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/*
		 * 如果启动前需要一些全局化的加载，在这里进行
		 */
		//准备复合流程
		preparedParentArrangementEntity.setCreator("parent");
		preparedParentArrangementEntity.setCreatorScope("industry");
		preparedParentArrangementEntity.setTradeScope("industry");
		preparedParentArrangementEntity.setUid(java.util.UUID.randomUUID().toString());
		preparedParentArrangementEntity.setDisplayName("parent_process");
		preparedParentArrangementEntity.setFlows("parent_noflow");
		preparedParentArrangementEntity.setTradeid("10000");

		preparedoldchildArrangementEntity.setCreator("child");
		preparedoldchildArrangementEntity.setCreatorScope("consumer");
		preparedoldchildArrangementEntity.setUid(java.util.UUID.randomUUID().toString());
		preparedoldchildArrangementEntity.setDisplayName("child1_process");
		preparedoldchildArrangementEntity.setFlows("child1_noflow");
		preparedoldchildArrangementEntity.setTradeid("10000");
		preparedoldchildArrangementEntity.setTradeScope("consumer");

		preparednewchildArrangementEntity.setCreator("child");
		preparednewchildArrangementEntity.setCreatorScope("producer");
		preparednewchildArrangementEntity.setUid(java.util.UUID.randomUUID().toString());
		preparednewchildArrangementEntity.setDisplayName("child2_process");
		preparednewchildArrangementEntity.setFlows("child2_noflow");
		preparednewchildArrangementEntity.setTradeid("10002");
		preparednewchildArrangementEntity.setTradeScope("producer");
		
		parentJointEntity.setUid(java.util.UUID.randomUUID().toString());
		parentJointEntity.setDisplayName("parentjoint");
		parentJointEntity.setAbsOffsettime(1000L);
		parentJointEntity.setRelateOffsettime(100000L);
		
		child1JointEntity.setUid(java.util.UUID.randomUUID().toString());
		child1JointEntity.setDisplayName("child1joint");
		child1JointEntity.setAbsOffsettime(1000L);
		child1JointEntity.setRelateOffsettime(100000L);
		
		child2JointEntity.setUid(java.util.UUID.randomUUID().toString());
		child2JointEntity.setDisplayName("chil2joint");
		child2JointEntity.setAbsOffsettime(1000L);
		child2JointEntity.setRelateOffsettime(100000L);
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		/*
		 * 如果测试完成后需要一些全局化的清理工作，在这里进行
		 */
		preparedoldchildArrangementEntity = null;
		preparedParentArrangementEntity = null;
		preparednewchildArrangementEntity = null;
		parentJointEntity = null;
		child1JointEntity = null;
		child2JointEntity = null;
	}

	/**
	 * 正式的测试代码写在这里
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test1_createArrangement() {
		TestArrangementDAO.LOGGER.info("====测试createArrangement====");
		//准备数据
		try {
			jointDAO.createJoint(parentJointEntity);
			jointDAO.createJoint(child1JointEntity);
			jointDAO.createJoint(child2JointEntity);
			arrangementDAO.createArrangement(preparedParentArrangementEntity);
			arrangementDAO.createArrangement(preparedoldchildArrangementEntity);
			arrangementDAO.createArrangement(preparednewchildArrangementEntity);
			Set<ArrangementJointMappingEntity> parentMappingSet = new HashSet<ArrangementJointMappingEntity>();
			ArrangementJointMappingEntity parentMapping = new ArrangementJointMappingEntity();
			parentMapping.setJoint(parentJointEntity);
			parentMapping.setParentArrangement(preparedParentArrangementEntity);
			parentMapping.setUid(java.util.UUID.randomUUID().toString());
			parentMapping.setVisible(true);
			parentMappingSet.add(parentMapping);
			
			Set<ArrangementJointMappingEntity> child1MappingSet = new HashSet<ArrangementJointMappingEntity>();
			ArrangementJointMappingEntity child1Mapping = new ArrangementJointMappingEntity();
			child1Mapping.setJoint(child1JointEntity);
			child1Mapping.setParentArrangement(preparedoldchildArrangementEntity);
			child1Mapping.setUid(java.util.UUID.randomUUID().toString());
			child1Mapping.setVisible(true);
			child1MappingSet.add(child1Mapping);
			Set<ArrangementJointMappingEntity> child2MappingSet = new HashSet<ArrangementJointMappingEntity>();
			ArrangementJointMappingEntity child2Mapping = new ArrangementJointMappingEntity();
			child2Mapping.setJoint(child2JointEntity);
			child2Mapping.setParentArrangement(preparednewchildArrangementEntity);
			child2Mapping.setUid(java.util.UUID.randomUUID().toString());
			child2Mapping.setVisible(true);
			child2MappingSet.add(child2Mapping);
			
			arrangementJointMappingDAO.bindArrangementJointmapping(preparedParentArrangementEntity.getUid(), parentMappingSet);
			arrangementJointMappingDAO.bindArrangementJointmapping(preparedoldchildArrangementEntity.getUid(), child1MappingSet);
			arrangementJointMappingDAO.bindArrangementJointmapping(preparednewchildArrangementEntity.getUid(), child2MappingSet);
			
			Set<ArrangementSelfMappingEntity> childMappingSet = new HashSet<ArrangementSelfMappingEntity>();
			ArrangementSelfMappingEntity child1processMapping = new ArrangementSelfMappingEntity();
			child1processMapping.setArrangement(preparedoldchildArrangementEntity);
			child1processMapping.setParentArrangement(preparedParentArrangementEntity);
			child1processMapping.setUid(java.util.UUID.randomUUID().toString());
			child1processMapping.setVisible(true);
			ArrangementSelfMappingEntity child2processMapping = new ArrangementSelfMappingEntity();
			child2processMapping.setArrangement(preparednewchildArrangementEntity);
			child2processMapping.setParentArrangement(preparedParentArrangementEntity);
			child2processMapping.setUid(java.util.UUID.randomUUID().toString());
			child2processMapping.setVisible(true);
			childMappingSet.add(child1processMapping);
			childMappingSet.add(child2processMapping);
			arrangementSelfMappingDAO.bindArrangementChildArrangements(preparedParentArrangementEntity.getUid(), childMappingSet);
			
		} catch (BizException e) {
			TestArrangementDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		
	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test2_queryArrangementByTradeidWithoutSet() {

		TestArrangementDAO.LOGGER.info("====测试queryArrangementByTradeidWithoutSet====");
		List<ArrangementEntity> arrangementList = null;
		try {
			arrangementList = this.arrangementDAO.queryArrangementByTradeidWithoutSet("10000", null);
		} catch (BizException e) {
			TestArrangementDAO.LOGGER.error(e.getMessage(), e);
			return;
		}
		// 断言式
		assertNotNull(arrangementList);
		TestArrangementDAO.LOGGER.info("size is =>" + arrangementList.size());
		assertTrue(arrangementList.size() == 2);
		
		try {
			arrangementList = this.arrangementDAO.queryArrangementByTradeidWithoutSet("10000", "industry");
		} catch (BizException e) {
			TestArrangementDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(arrangementList);
		TestArrangementDAO.LOGGER.info("size is =>" + arrangementList.size());
		assertTrue(arrangementList.size() == 1);
		assertTrue(StringUtils.equals(arrangementList.get(0).getCreator(), "parent"));
		assertTrue(StringUtils.equals(arrangementList.get(0).getDisplayName(), "parent_process"));
		assertTrue(StringUtils.equals(arrangementList.get(0).getFlows(), "parent_noflow"));
	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test3_queryArrangementByTradeidPageWithoutSet() {

		TestArrangementDAO.LOGGER.info("====测试queryArrangementByTradeidPageWithoutSet====");
		PageEntity queryPage = null;
		//测试返回结果并分页
		try {
			queryPage = this.arrangementDAO.queryArrangementByTradeidPageWithoutSet("10000", null, 0, 1);
		} catch (BizException e) {
			TestArrangementDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryPage);
		assertNotNull(queryPage.getResults());
		assertTrue(queryPage.getResults().size() == 1);
		
		//测试返回相应结果
		queryPage = null;
		try {
			queryPage = this.arrangementDAO.queryArrangementByTradeidPageWithoutSet("10000", null, 0, 10);
		} catch (BizException e) {
			TestArrangementDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryPage);
		assertNotNull(queryPage.getResults());
		assertTrue(queryPage.getResults().size() == 2);
		
		//测试错误查询情况是否复合接口描述
		queryPage = null;
		try {
			queryPage = this.arrangementDAO.queryArrangementByTradeidPageWithoutSet("10005", null, 0, 10);
		} catch (BizException e) {
			TestArrangementDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryPage);
		assertTrue(queryPage.getResults() == null);
	}
	
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test4_getArrangementWithoutSet() {

		TestArrangementDAO.LOGGER.info("====测试getArrangementWithoutSet====");
		ArrangementEntity queryArrangement = null;
		//测试正确返回
		try {
			queryArrangement = this.arrangementDAO.getArrangementWithoutSet(preparedParentArrangementEntity.getUid());
		} catch (BizException e) {
			TestArrangementDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryArrangement);
		assertTrue(StringUtils.equals(queryArrangement.getCreator(), "parent"));
		assertTrue(StringUtils.equals(queryArrangement.getCreatorScope(), "industry"));
		assertTrue(StringUtils.equals(queryArrangement.getDisplayName(), "parent_process"));
		
		queryArrangement = null;
		//测试错误返回
		try {
			queryArrangement = this.arrangementDAO.getArrangementWithoutSet(java.util.UUID.randomUUID().toString());
		} catch (BizException e) {
			TestArrangementDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertTrue(queryArrangement == null);
	}
	
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test5_updateArrangement() {

		TestArrangementDAO.LOGGER.info("====测试updateArrangement====");
		ArrangementEntity queryArrangement = null;
		//准备数据
		preparedParentArrangementEntity.setDisplayName("newParent_process");
		try {
			this.arrangementDAO.updateArrangement(preparedParentArrangementEntity);
			queryArrangement = arrangementDAO.getArrangementWithoutSet(preparedParentArrangementEntity.getUid());
		} catch (BizException e) {
			TestArrangementDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryArrangement);
		assertTrue(StringUtils.equals(queryArrangement.getCreator(), "parent"));
		assertTrue(StringUtils.equals(queryArrangement.getCreatorScope(), "industry"));
		assertTrue(!StringUtils.equals(queryArrangement.getDisplayName(), "parent_process"));
		assertTrue(StringUtils.equals(queryArrangement.getDisplayName(), "newParent_process"));
	}
	
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test6_getArrangementWithSet() {

		TestArrangementDAO.LOGGER.info("====测试getArrangementWithSet====");
		ArrangementEntity queryArrangement = null;
		//准备数据
		try {
			queryArrangement = arrangementDAO.getArrangementWithSet(preparedParentArrangementEntity.getUid());
		} catch (BizException e) {
			TestArrangementDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryArrangement);
		assertTrue(StringUtils.equals(queryArrangement.getCreator(), "parent"));
		assertTrue(StringUtils.equals(queryArrangement.getCreatorScope(), "industry"));
		assertTrue(StringUtils.equals(queryArrangement.getDisplayName(), "newParent_process"));
		Set<ArrangementSelfMappingEntity> childprocesses = queryArrangement.getChildArrangements();
		Set<ArrangementJointMappingEntity> childnodes = queryArrangement.getJointmapping();
		assertTrue(childnodes.size() == 1);
		ArrangementJointMappingEntity currentjoint = null;
		for (ArrangementJointMappingEntity joint:childnodes) {
			currentjoint = joint;
		}
		assertTrue(StringUtils.equals(currentjoint.getJoint().getUid(), parentJointEntity.getUid()));
		assertTrue(currentjoint.getVisible());
		TestArrangementDAO.LOGGER.info("childprocesses size=>"+childprocesses.size());
		assertTrue(childprocesses.size() == 2);
	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test7_deleteArrangement() {

		TestArrangementDAO.LOGGER.info("====测试deleteArrangement====");
		List<ArrangementEntity> queryArrangements = null;
		//准备数据
		try {
			arrangementJointMappingDAO.releaseAllArrangementJointmapping(preparedoldchildArrangementEntity.getUid());
			arrangementJointMappingDAO.releaseAllArrangementJointmapping(preparednewchildArrangementEntity.getUid());
			arrangementJointMappingDAO.releaseAllArrangementJointmapping(preparedParentArrangementEntity.getUid());
			
			arrangementSelfMappingDAO.releaseAllArrangementChildArrangements(preparedParentArrangementEntity.getUid());
			jointDAO.deleteJoint(parentJointEntity.getUid());
			jointDAO.deleteJoint(child1JointEntity.getUid());
			jointDAO.deleteJoint(child2JointEntity.getUid());
			arrangementDAO.deleteArrangement(preparedoldchildArrangementEntity.getUid());
			arrangementDAO.deleteArrangement(preparednewchildArrangementEntity.getUid());
			arrangementDAO.deleteArrangement(preparedParentArrangementEntity.getUid());
			queryArrangements = arrangementDAO.queryArrangementByTradeidWithoutSet("10000", null);
		} catch (BizException e) {
			TestArrangementDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertTrue(queryArrangements == null);
		
	}
	
}