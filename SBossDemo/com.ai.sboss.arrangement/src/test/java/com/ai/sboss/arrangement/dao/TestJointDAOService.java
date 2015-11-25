package com.ai.sboss.arrangement.dao;

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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.ai.sboss.arrangement.engine.dao.JointDAOService;
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointInputParamsDAO;
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointOutputParamsDAO;
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointTradeMappingDAO;
import com.ai.sboss.arrangement.entity.PageEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointInputParamsEntity;
import com.ai.sboss.arrangement.entity.orm.JointOutputParamsEntity;
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
public class TestJointDAOService {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestJointDAOService.class);

	/**
	 * 要测试的接口
	 */
	@Autowired
	private JointDAOService jointDAOService;
	@Autowired
	private IJointInputParamsDAO jointInputParamsDAO;
	@Autowired
	private IJointOutputParamsDAO jointOutputParamsDAO;
	@Autowired
	private IJointTradeMappingDAO jointTradeMappingDAO;

	private static JointEntity jointfirsttypeindustryEntity = new JointEntity();
	private static JointEntity jointfirsttypeconsumerEntity = new JointEntity();
	private static JointEntity jointsecondtypeproducerEntity = new JointEntity();

	private static JointInputParamsEntity firstinputparamEntity = new JointInputParamsEntity();
	private static JointOutputParamsEntity firstoutputparamEntity = new JointOutputParamsEntity();
	private static JointInputParamsEntity secondinputparamEntity = new JointInputParamsEntity();
	private static JointOutputParamsEntity secondoutputparamEntity = new JointOutputParamsEntity();
	private static JointInputParamsEntity inputparamEntity = new JointInputParamsEntity();
	private static JointTradeMappingEntity tradeMappingEntity = new JointTradeMappingEntity();
	private static JointTradeMappingEntity tradeMappingEntity2 = new JointTradeMappingEntity();
	private static JointTradeMappingEntity tradeMappingEntity3 = new JointTradeMappingEntity();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/*
		 * 如果启动前需要一些全局化的加载，在这里进行
		 */
		jointfirsttypeindustryEntity.setUid(java.util.UUID.randomUUID().toString());
		jointfirsttypeindustryEntity.setDisplayName("first_type_industry");
		jointfirsttypeindustryEntity.setAbsOffsettime(1000L);
		jointfirsttypeindustryEntity.setRelateOffsettime(100000L);

		jointfirsttypeconsumerEntity.setUid(java.util.UUID.randomUUID().toString());
		jointfirsttypeconsumerEntity.setDisplayName("first_type_consumer");
		jointfirsttypeconsumerEntity.setAbsOffsettime(1000L);
		jointfirsttypeconsumerEntity.setRelateOffsettime(100000L);

		jointsecondtypeproducerEntity.setUid(java.util.UUID.randomUUID().toString());
		jointsecondtypeproducerEntity.setDisplayName("second_type_consumer");
		jointsecondtypeproducerEntity.setAbsOffsettime(1000L);
		jointsecondtypeproducerEntity.setRelateOffsettime(100000L);

		inputparamEntity.setDefaultValue("1");
		inputparamEntity.setName("inputparamstring");
		inputparamEntity.setDisplayName("inputparamstring");
		inputparamEntity.setRequired(true);
		inputparamEntity.setType("String");
		inputparamEntity.setDisplayType("String");
		inputparamEntity.setJoint(jointfirsttypeconsumerEntity);
		inputparamEntity.setUid(java.util.UUID.randomUUID().toString());

		firstinputparamEntity.setDefaultValue("1");
		firstinputparamEntity.setName("inputparamstring");
		firstinputparamEntity.setDisplayName("inputparamstring");
		firstinputparamEntity.setRequired(true);
		firstinputparamEntity.setType("String");
		firstinputparamEntity.setDisplayType("String");
		firstinputparamEntity.setUid(java.util.UUID.randomUUID().toString());
		firstinputparamEntity.setJoint(jointfirsttypeindustryEntity);

		firstoutputparamEntity.setDefaultValue("1");
		firstoutputparamEntity.setName("outputparam");
		firstoutputparamEntity.setRequired(true);
		firstoutputparamEntity.setType("String");
		firstoutputparamEntity.setUid(java.util.UUID.randomUUID().toString());
		firstoutputparamEntity.setJoint(jointfirsttypeindustryEntity);

		secondinputparamEntity.setDefaultValue("2");
		secondinputparamEntity.setName("inputparamlong");
		secondinputparamEntity.setDisplayName("inputparamlong");
		secondinputparamEntity.setRequired(true);
		secondinputparamEntity.setType("Long");
		secondinputparamEntity.setDisplayType("Long");
		secondinputparamEntity.setUid(java.util.UUID.randomUUID().toString());
		secondinputparamEntity.setJoint(jointsecondtypeproducerEntity);

		secondoutputparamEntity.setDefaultValue("2");
		secondoutputparamEntity.setName("outputparam");
		secondoutputparamEntity.setRequired(true);
		secondoutputparamEntity.setType("Long");
		secondoutputparamEntity.setUid(java.util.UUID.randomUUID().toString());
		secondoutputparamEntity.setJoint(jointsecondtypeproducerEntity);

		tradeMappingEntity.setJoint(jointfirsttypeindustryEntity);
		tradeMappingEntity.setScope("industry");
		tradeMappingEntity.setTradeid("10000");
		tradeMappingEntity.setUid(java.util.UUID.randomUUID().toString());
		
		tradeMappingEntity2.setJoint(jointfirsttypeconsumerEntity);
		tradeMappingEntity2.setScope("consumer");
		tradeMappingEntity2.setTradeid("10000");
		tradeMappingEntity2.setUid(java.util.UUID.randomUUID().toString());
		
		tradeMappingEntity3.setJoint(jointsecondtypeproducerEntity);
		tradeMappingEntity3.setScope("producer");
		tradeMappingEntity3.setTradeid("10001");
		tradeMappingEntity3.setUid(java.util.UUID.randomUUID().toString());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		/*
		 * 如果测试完成后需要一些全局化的清理工作，在这里进行
		 */
		jointfirsttypeindustryEntity = null;
		jointfirsttypeconsumerEntity = null;
		jointsecondtypeproducerEntity = null;
		firstinputparamEntity = null;
		firstoutputparamEntity = null;
		secondinputparamEntity = null;
		secondoutputparamEntity = null;
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test1_createJoint() {
		TestJointDAOService.LOGGER.info("====测试createJoint====");

		// 测试错误
		try {
			jointDAOService.createJoint(jointfirsttypeconsumerEntity);
		} catch (BizException e) {
			assertTrue(e.getResponseCode() == ResponseCode._401);
		}
		// 断言式

		// 准备数据
		Set<JointTradeMappingEntity> tradeMappingEntities = new HashSet<JointTradeMappingEntity>();
		Set<JointInputParamsEntity> inputParamsEntities = new HashSet<JointInputParamsEntity>();
		Set<JointOutputParamsEntity> outputParamsEntities = new HashSet<JointOutputParamsEntity>();
		Set<JointTradeMappingEntity> tradeMappingEntities2 = new HashSet<JointTradeMappingEntity>();
		Set<JointInputParamsEntity> inputParamsEntities2 = new HashSet<JointInputParamsEntity>();

		Set<JointTradeMappingEntity> tradeMappingEntities3 = new HashSet<JointTradeMappingEntity>();
		Set<JointInputParamsEntity> inputParamsEntities3 = new HashSet<JointInputParamsEntity>();
		Set<JointOutputParamsEntity> outputParamsEntities3 = new HashSet<JointOutputParamsEntity>();
		tradeMappingEntities.add(tradeMappingEntity);
		inputParamsEntities.add(firstinputparamEntity);
		outputParamsEntities.add(firstoutputparamEntity);
		jointfirsttypeindustryEntity.setTrades(tradeMappingEntities);
		jointfirsttypeindustryEntity.setInputParams(inputParamsEntities);
		jointfirsttypeindustryEntity.setOutputParams(outputParamsEntities);
		tradeMappingEntities2.add(tradeMappingEntity2);
		inputParamsEntities2.add(inputparamEntity);
		jointfirsttypeconsumerEntity.setTrades(tradeMappingEntities2);
		jointfirsttypeconsumerEntity.setInputParams(inputParamsEntities2);

		tradeMappingEntities3.add(tradeMappingEntity3);
		inputParamsEntities3.add(secondinputparamEntity);
		outputParamsEntities3.add(secondoutputparamEntity);
		jointsecondtypeproducerEntity.setTrades(tradeMappingEntities3);
		jointsecondtypeproducerEntity.setInputParams(inputParamsEntities3);
		jointsecondtypeproducerEntity.setOutputParams(outputParamsEntities3);

		// 正确测试
		try {
			jointDAOService.createJoint(jointfirsttypeindustryEntity);
			jointDAOService.createJoint(jointfirsttypeconsumerEntity);
			jointDAOService.createJoint(jointsecondtypeproducerEntity);
		} catch (BizException e) {
			TestJointDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test2_queryJointByTradeid() {
		TestJointDAOService.LOGGER.info("====测试queryJointByTradeid====");
		List<JointEntity> jointEntities = null;
		try {
			jointEntities = jointDAOService.queryJointByTradeid("10002", null);
		} catch (BizException e) {
			TestJointDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertTrue(jointEntities == null);
		
		try {
			jointEntities = jointDAOService.queryJointByTradeid("10000", null);
		} catch (BizException e) {
			TestJointDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		assertNotNull(jointEntities);
		assertTrue(jointEntities.size() == 2);
		
		jointEntities = null;
		try {
			jointEntities = jointDAOService.queryJointByTradeid("10000", "industry");
		} catch (BizException e) {
			TestJointDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		assertNotNull(jointEntities);
		assertTrue(jointEntities.size() == 1);
		assertTrue(StringUtils.equals(jointEntities.get(0).getDisplayName(), "first_type_industry"));
	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test3_queryJointByTradeidPage() {
		TestJointDAOService.LOGGER.info("====测试queryJointByTradeidPage====");
		PageEntity queryPageEntity = null;
		try {
			queryPageEntity = jointDAOService.queryJointByTradeidPage("10002", null, 0, 2);
		} catch (BizException e) {
			TestJointDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryPageEntity);
		assertTrue(queryPageEntity.getResults() == null);
		
		queryPageEntity = null;
		try {
			queryPageEntity = jointDAOService.queryJointByTradeidPage("10000", null, 0, 1);
		} catch (BizException e) {
			TestJointDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryPageEntity);
		assertTrue(queryPageEntity.getResults().size() == 1);
	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test4_getJointWithoutParams() {
		TestJointDAOService.LOGGER.info("====测试getJointWitoutParams====");
		JointEntity queryJointEntity = null;
		try {
			queryJointEntity = jointDAOService.getJointWithoutParams(java.util.UUID.randomUUID().toString());
		} catch (BizException e) {
			TestJointDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertTrue(queryJointEntity == null);
		
		queryJointEntity = null;
		try {
			queryJointEntity = jointDAOService.getJointWithoutParams(jointfirsttypeindustryEntity.getUid());
		} catch (BizException e) {
			TestJointDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryJointEntity);
		assertTrue(StringUtils.equals("first_type_industry", queryJointEntity.getDisplayName()));
	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test5_getJointWithParams() {
		TestJointDAOService.LOGGER.info("====测试getJointWithParams====");
	
		JointEntity queryJointEntity = null;
		try {
			queryJointEntity = jointDAOService.getJointWithParams(jointfirsttypeindustryEntity.getUid());
		} catch (BizException e) {
			TestJointDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryJointEntity);
		assertTrue(StringUtils.equals("first_type_industry", queryJointEntity.getDisplayName()));
		Set<JointInputParamsEntity> inputParamsEntities = new HashSet<JointInputParamsEntity>();
		Set<JointOutputParamsEntity> outputParamsEntities = new HashSet<JointOutputParamsEntity>();
		Set<JointTradeMappingEntity> tradeMappingEntities = new HashSet<JointTradeMappingEntity>();
		inputParamsEntities = queryJointEntity.getInputParams();
		outputParamsEntities = queryJointEntity.getOutputParams();
		tradeMappingEntities = queryJointEntity.getTrades();
		assertTrue(inputParamsEntities.size() == 1);
		assertTrue(outputParamsEntities.size() == 1);
		assertTrue(tradeMappingEntities.size() == 1);
		
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test6_updateJoint() {
		TestJointDAOService.LOGGER.info("====测试updateJoint====");
	
		JointEntity queryJointEntity = null;
		jointfirsttypeindustryEntity.setDisplayName("newdisplayname");
		try {
			jointDAOService.updateJoint(jointfirsttypeindustryEntity);
			queryJointEntity = jointDAOService.getJointWithoutParams(jointfirsttypeindustryEntity.getUid());
		} catch (BizException e) {
			TestJointDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(queryJointEntity);
		assertTrue(!StringUtils.equals("first_type_industry", queryJointEntity.getDisplayName()));
		assertTrue(StringUtils.equals("newdisplayname", queryJointEntity.getDisplayName()));
	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test7_updateJointTrades() {
		TestJointDAOService.LOGGER.info("====测试updateJointTrades====");
	
		List<JointEntity> jointEntities = null;
		Map<String, String> tradeinfoSet = new HashMap<String, String>();
		tradeinfoSet.put("10002", "industry");
		
		try {
			jointDAOService.updateJointTrades(jointfirsttypeindustryEntity.getUid(), tradeinfoSet);
			jointEntities = jointDAOService.queryJointByTradeid("10002", null);
		} catch (BizException e) {
			TestJointDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertNotNull(jointEntities);
		assertTrue(StringUtils.equals("newdisplayname", jointEntities.get(0).getDisplayName()));
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test8_deleteJoint() {
		TestJointDAOService.LOGGER.info("====测试deleteJoint====");

		try {
			jointTradeMappingDAO.releaseAllJointTradeMapping(jointfirsttypeindustryEntity.getUid());
			jointTradeMappingDAO.releaseAllJointTradeMapping(jointfirsttypeconsumerEntity.getUid());
			jointTradeMappingDAO.releaseAllJointTradeMapping(jointsecondtypeproducerEntity.getUid());
			jointInputParamsDAO.releaseAllJointInputParams(jointfirsttypeindustryEntity.getUid());
			jointInputParamsDAO.releaseAllJointInputParams(jointfirsttypeconsumerEntity.getUid());
			jointInputParamsDAO.releaseAllJointInputParams(jointsecondtypeproducerEntity.getUid());
			jointOutputParamsDAO.releaseAllJointOutputParams(jointfirsttypeindustryEntity.getUid());
			jointOutputParamsDAO.releaseAllJointOutputParams(jointfirsttypeconsumerEntity.getUid());
			jointOutputParamsDAO.releaseAllJointOutputParams(jointsecondtypeproducerEntity.getUid());
			
			jointDAOService.deleteJoint(jointfirsttypeindustryEntity.getUid());
			jointDAOService.deleteJoint(jointfirsttypeconsumerEntity.getUid());
			jointDAOService.deleteJoint(jointsecondtypeproducerEntity.getUid());
		} catch (BizException e) {
			TestJointDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式

	}
}