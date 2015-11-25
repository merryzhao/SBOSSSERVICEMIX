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

import com.ai.sboss.arrangement.engine.dao.ParamsDAOService;
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointDAO;
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointInputParamsDAO;
import com.ai.sboss.arrangement.engine.dao.relationdb.IJointOutputParamsDAO;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointInputParamsEntity;
import com.ai.sboss.arrangement.entity.orm.JointOutputParamsEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * arrangementDAO持久层测试
 * 
 * @author chaos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-*.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestParamsDAOService {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestParamsDAOService.class);

	/**
	 * 要测试的接口
	 */
	@Autowired
	private IJointDAO jointDAO;
	@Autowired
	private ParamsDAOService paramsDAOService;
	@Autowired
	private IJointInputParamsDAO jointInputParamsDAO;
	@Autowired
	private IJointOutputParamsDAO jointOutputParamsDAO;
	
	private static JointEntity jointEntity = new JointEntity();
	private static JointEntity jointEntitywithoutparams = new JointEntity();
	private static Set<JointInputParamsEntity> inputParamsEntities = new HashSet<JointInputParamsEntity>();
	private static Set<JointOutputParamsEntity> outputParamsEntities = new HashSet<JointOutputParamsEntity>();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/*
		 * 如果启动前需要一些全局化的加载，在这里进行
		 */
		jointEntity.setUid(java.util.UUID.randomUUID().toString());
		jointEntity.setDisplayName("displayName");
		jointEntity.setAbsOffsettime(1000L);
		jointEntity.setRelateOffsettime(100000L);
		
		jointEntitywithoutparams.setUid(java.util.UUID.randomUUID().toString());
		jointEntitywithoutparams.setDisplayName("noparam");
		jointEntitywithoutparams.setAbsOffsettime(1000L);
		jointEntitywithoutparams.setRelateOffsettime(100000L);
		
		JointInputParamsEntity inputStringEntity = new JointInputParamsEntity();
		inputStringEntity.setDefaultValue("0");
		inputStringEntity.setName("inputparamstring");
		inputStringEntity.setDisplayName("inputparamstring");
		inputStringEntity.setRequired(true);
		inputStringEntity.setType("String");
		inputStringEntity.setDisplayType("String");
		inputStringEntity.setJoint(jointEntity);
		JointInputParamsEntity inputLongEntity = new JointInputParamsEntity();
		inputLongEntity.setDefaultValue("100");
		inputLongEntity.setName("inputparamlong");
		inputLongEntity.setDisplayName("inputparamlong");
		inputLongEntity.setRequired(true);
		inputLongEntity.setType("Long");
		inputLongEntity.setDisplayType("Long");
		inputLongEntity.setJoint(jointEntity);
		
		inputParamsEntities.add(inputLongEntity);
		inputParamsEntities.add(inputStringEntity);
		
		JointOutputParamsEntity outputParamsEntity = new JointOutputParamsEntity();
		outputParamsEntity.setDefaultValue("1");
		outputParamsEntity.setName("outputparam");
		outputParamsEntity.setRequired(true);
		outputParamsEntity.setType("String");
		outputParamsEntity.setJoint(jointEntity);
		outputParamsEntities.add(outputParamsEntity);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		/*
		 * 如果测试完成后需要一些全局化的清理工作，在这里进行
		 */
		jointEntity = null;
		inputParamsEntities = null;
		outputParamsEntities = null;
	}

	/**
	 * 正式的测试代码写在这里
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test1_updateJointInputParams() {
		TestParamsDAOService.LOGGER.info("====测试updateJointInputParams====");
		//准备数据
		try {
			jointDAO.createJoint(jointEntity);
			paramsDAOService.updateJointInputParams(jointEntity.getUid(), inputParamsEntities);
		} catch (BizException e) {
			TestParamsDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test2_updateJointOutputParams() {
		TestParamsDAOService.LOGGER.info("====测试updateJointOutputParams====");
		//准备数据
		try {
			paramsDAOService.updateJointOutputParams(jointEntity.getUid(), outputParamsEntities);
		} catch (BizException e) {
			TestParamsDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
	}
	
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test3_queryInputParamsByjointuid() {
		TestParamsDAOService.LOGGER.info("====测试queryInputParamsByjointuid====");
		//准备数据
		//测试返回空
		List<JointInputParamsEntity> queryInputParamsEntities = null;
		try {
			queryInputParamsEntities = paramsDAOService.queryInputParamsByjointuid(jointEntitywithoutparams.getUid());
		} catch (BizException e) {
			TestParamsDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertTrue(queryInputParamsEntities == null);
		//测试正确返回
		try {
			queryInputParamsEntities = paramsDAOService.queryInputParamsByjointuid(jointEntity.getUid());
		} catch (BizException e) {
			TestParamsDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		assertNotNull(queryInputParamsEntities);
		assertTrue(queryInputParamsEntities.size() == 2);
		assertTrue(queryInputParamsEntities.get(0).getRequired());
	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test4_queryOutputParamsByjointuid() {
		TestParamsDAOService.LOGGER.info("====测试queryOutputParamsByjointuid====");
		//准备数据
		//测试返回空
		List<JointOutputParamsEntity> queryOutputParamsEntities = null;
		try {
			queryOutputParamsEntities = paramsDAOService.queryOutputParamsByjointuid(jointEntitywithoutparams.getUid());
		} catch (BizException e) {
			TestParamsDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertTrue(queryOutputParamsEntities == null);
		//测试正确返回
		try {
			queryOutputParamsEntities = paramsDAOService.queryOutputParamsByjointuid(jointEntity.getUid());
		} catch (BizException e) {
			TestParamsDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		assertNotNull(queryOutputParamsEntities);
		assertTrue(queryOutputParamsEntities.size() == 1);
		assertTrue(queryOutputParamsEntities.get(0).getRequired());
		assertTrue(StringUtils.equals(queryOutputParamsEntities.get(0).getName(), "outputparam"));
		assertTrue(StringUtils.equals(queryOutputParamsEntities.get(0).getDefaultValue(), "1"));
	}
	
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test5_cleanup() {
		TestParamsDAOService.LOGGER.info("====cleanup====");
		//准备数据
		//测试返回空
		List<JointOutputParamsEntity> queryOutputParamsEntities = null;
		List<JointInputParamsEntity> queryInputParamsEntities = null;
		try {
			jointOutputParamsDAO.releaseAllJointOutputParams(jointEntity.getUid());
			queryOutputParamsEntities = paramsDAOService.queryOutputParamsByjointuid(jointEntity.getUid());
			jointInputParamsDAO.releaseAllJointInputParams(jointEntity.getUid());
			queryInputParamsEntities = paramsDAOService.queryInputParamsByjointuid(jointEntity.getUid());
			
			jointDAO.deleteJoint(jointEntity.getUid());
			jointDAO.deleteJoint(jointEntitywithoutparams.getUid());
		} catch (BizException e) {
			TestParamsDAOService.LOGGER.error(e.getMessage(), e);
			assertTrue(false);
			return;
		}
		// 断言式
		assertTrue(queryInputParamsEntities == null);
		assertTrue(queryOutputParamsEntities == null);
	}
	
	

}