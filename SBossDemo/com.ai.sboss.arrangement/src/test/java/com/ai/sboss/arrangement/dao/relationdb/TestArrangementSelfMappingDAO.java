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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.ai.sboss.arrangement.engine.dao.relationdb.IArrangementDAO;
import com.ai.sboss.arrangement.engine.dao.relationdb.IArrangementSelfMappingDAO;
import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementSelfMappingEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * jointDAO持久层测试
 * 
 * @author yinwenjie
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-*.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestArrangementSelfMappingDAO {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestArrangementSelfMappingDAO.class);

	private static ArrangementEntity preparedParentArrangementEntity = new ArrangementEntity();
	private static ArrangementEntity preparedoldchildArrangementEntity = new ArrangementEntity();
	private static ArrangementEntity preparednewchildArrangementEntity = new ArrangementEntity();

	/**
	 * 测试使用到的接口
	 */
	@Autowired
	private IArrangementSelfMappingDAO arrangementSelfMappingDAO;
	@Autowired
	private IArrangementDAO arrangementDAO;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/*
		 * 如果启动前需要一些全局化的加载，在这里进行
		 */
		preparedParentArrangementEntity.setCreator("parent");
		preparedParentArrangementEntity.setCreatorScope("industry");
		preparedParentArrangementEntity.setTradeScope("industry");
		preparedParentArrangementEntity.setUid(java.util.UUID.randomUUID().toString());
		preparedParentArrangementEntity.setDisplayName("parent_process");
		preparedParentArrangementEntity.setFlows("parent_noflownow");
		preparedParentArrangementEntity.setTradeid("10000");

		preparedoldchildArrangementEntity.setCreator("child");
		preparedoldchildArrangementEntity.setCreatorScope("consumer");
		preparedoldchildArrangementEntity.setTradeScope("consumer");
		preparedoldchildArrangementEntity.setUid(java.util.UUID.randomUUID().toString());
		preparedoldchildArrangementEntity.setDisplayName("oldchild_process");
		preparedoldchildArrangementEntity.setFlows("oldchild_noflownow");
		preparedoldchildArrangementEntity.setTradeid("10001");

		preparednewchildArrangementEntity.setCreator("child");
		preparednewchildArrangementEntity.setCreatorScope("producer");
		preparednewchildArrangementEntity.setTradeScope("producer");
		preparednewchildArrangementEntity.setUid(java.util.UUID.randomUUID().toString());
		preparednewchildArrangementEntity.setDisplayName("newchild_process");
		preparednewchildArrangementEntity.setFlows("newchild_noflownow");
		preparednewchildArrangementEntity.setTradeid("10002");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		/*
		 * 如果完成后需要一些全局化的清理，在这里进行
		 */
		preparedoldchildArrangementEntity = null;
		preparedParentArrangementEntity = null;
		preparednewchildArrangementEntity = null;
	}

	/**
	 * 正式的测试代码写在这里
	 */
	// @Test //作为测试样例不再使用
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test1_bindArrangementChildArrangements() {
		TestArrangementSelfMappingDAO.LOGGER.info("====测试bindArrangementChildArrangements====");
		// 准备数据
		try {
			arrangementDAO.createArrangement(preparedParentArrangementEntity);
			arrangementDAO.createArrangement(preparedoldchildArrangementEntity);
			arrangementDAO.createArrangement(preparednewchildArrangementEntity);
		} catch (BizException e) {
			TestArrangementSelfMappingDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		Set<ArrangementSelfMappingEntity> mappingSet = new HashSet<ArrangementSelfMappingEntity>();
		ArrangementSelfMappingEntity mappingEntity = new ArrangementSelfMappingEntity();
		mappingEntity.setArrangement(preparedoldchildArrangementEntity);
		mappingEntity.setParentArrangement(preparedParentArrangementEntity);
		mappingEntity.setUid(java.util.UUID.randomUUID().toString());
		mappingEntity.setVisible(true);
		mappingSet.add(mappingEntity);

		try {
			arrangementSelfMappingDAO.bindArrangementChildArrangements(preparedParentArrangementEntity.getUid(),
					mappingSet);
		} catch (BizException e) {
			TestArrangementSelfMappingDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test2_getArrangementSelfMappingSet() {
		TestArrangementSelfMappingDAO.LOGGER.info("====测试getArrangementSelfMappingSet====");
		List<ArrangementSelfMappingEntity> querymappingList = null;
		try {
			querymappingList = arrangementSelfMappingDAO
								.getArrangementSelfMappingSet(preparedParentArrangementEntity.getUid());
		} catch (BizException e) {
			TestArrangementSelfMappingDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		assertNotNull(querymappingList);
		assertTrue(querymappingList.size() == 1);
		assertTrue(querymappingList.get(0).getVisible());
		assertTrue(StringUtils.equals(querymappingList.get(0).getArrangement().getCreator(),"child"));
		assertTrue(StringUtils.equals(querymappingList.get(0).getArrangement().getDisplayName(),"oldchild_process"));
		assertTrue(StringUtils.equals(querymappingList.get(0).getArrangement().getTradeid(),"10001"));
		
	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test3_rebindArrangementChildArrangements() {
		TestArrangementSelfMappingDAO.LOGGER.info("====测试rebindArrangementChildArrangements====");
		// 准备数据
		Set<ArrangementSelfMappingEntity> mappingSet = new HashSet<ArrangementSelfMappingEntity>();
		ArrangementSelfMappingEntity mappingEntity = new ArrangementSelfMappingEntity();
		mappingEntity.setArrangement(preparednewchildArrangementEntity);
		mappingEntity.setParentArrangement(preparedParentArrangementEntity);
		mappingEntity.setUid(java.util.UUID.randomUUID().toString());
		mappingEntity.setVisible(false);
		mappingSet.add(mappingEntity);
		List<ArrangementSelfMappingEntity> querymappingList = null;

		try {
			arrangementSelfMappingDAO.releaseAllArrangementChildArrangements(preparedParentArrangementEntity.getUid());
			arrangementSelfMappingDAO.bindArrangementChildArrangements(preparedParentArrangementEntity.getUid(),
					mappingSet);
			querymappingList = arrangementSelfMappingDAO
					.getArrangementSelfMappingSet(preparedParentArrangementEntity.getUid());
		} catch (BizException e) {
			TestArrangementSelfMappingDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		assertNotNull(querymappingList);
		assertTrue(querymappingList.size() == 1);
		assertTrue(!querymappingList.get(0).getVisible());
		assertTrue(StringUtils.equals(querymappingList.get(0).getArrangement().getCreator(),"child"));
		assertTrue(StringUtils.equals(querymappingList.get(0).getArrangement().getDisplayName(),"newchild_process"));
		assertTrue(StringUtils.equals(querymappingList.get(0).getArrangement().getTradeid(),"10002"));
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test4_releaseAllArrangementChildArrangements() {
		TestArrangementSelfMappingDAO.LOGGER.info("====测试releaseAllArrangementChildArrangements====");
		List<ArrangementSelfMappingEntity> querymappingList = null;
		try {
			arrangementSelfMappingDAO.releaseAllArrangementChildArrangements(preparedParentArrangementEntity.getUid());
			querymappingList = arrangementSelfMappingDAO
					.getArrangementSelfMappingSet(preparedParentArrangementEntity.getUid());
			arrangementDAO.delete(preparednewchildArrangementEntity.getUid());
			arrangementDAO.delete(preparedoldchildArrangementEntity.getUid());
			arrangementDAO.delete(preparedParentArrangementEntity.getUid());
		} catch (BizException e) {
			TestArrangementSelfMappingDAO.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertTrue(querymappingList == null);
	}
}