package com.ai.sboss.arrangement.engine;

import java.util.Set;

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
import com.ai.sboss.arrangement.engine.dao.ArrangementDAOAbstractFactory;
import com.ai.sboss.arrangement.engine.dao.ArrangementDAOService;
import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementJointMappingEntity;
import com.ai.sboss.arrangement.exception.BizException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-*.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestStartupControlService {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestStartupControlService.class);
	
	@Autowired
	private IStartupControlService startupControlService;
	
	@Autowired
	private ArrangementDAOAbstractFactory arrangementDAOFactory;
	
	private static String preparedArrangementId;
	

	@BeforeClass
	public static void setUp() {
		preparedArrangementId = "9d53755c-85ac-46c4-8e31-b7858cb88613";
	}

	@AfterClass
	public static void tearDown() {

	}
	
	/**
	 * 对 ArrangementInstanceEntity createArrangementInstance(ArrangementInstanceEntity arrangementInstance) throws BizException 
	 * 方法进行测试，主要观察SQL语句。
	 */
	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback(false)
	public void test1_createArrangementInstance() {
		TestStartupControlService.LOGGER.info("===测试createArrangementInstance===");
		ArrangementDAOService arrangementDAOService = arrangementDAOFactory.getArrangementDAOService();
		Set<ArrangementJointMappingEntity> jointMappings = null;
		try {
			ArrangementEntity targetarrangement =  arrangementDAOService.getArrangementWithSet(preparedArrangementId);
			jointMappings = targetarrangement.getJointmapping();
		} catch (BizException e1) {
			e1.printStackTrace();
		}
		JSONObject arrangementInstanceJSON = new JSONObject();
		JSONObject arrangementInstance = new JSONObject();
		arrangementInstance.put("arrangementid", preparedArrangementId);
		arrangementInstance.put("businessid", "123456");
		arrangementInstance.put("creator", "Chaos");
		arrangementInstance.put("creatorScope", "consumer");
		JSONArray jointinstances = new JSONArray();
		for (ArrangementJointMappingEntity jointMapping:jointMappings) {
			JSONObject tempobj = new JSONObject();
			tempobj.put("jointid", jointMapping.getJoint().getUid());
			tempobj.put("absOffsettime", jointMapping.getAbsOffsettime());
			tempobj.put("relateOffsettime", jointMapping.getRelateOffsettime());
			tempobj.put("executor", "chaos");
			jointinstances.add(tempobj);
		}
		arrangementInstance.put("jointinstances", jointinstances);
		arrangementInstanceJSON.put("arrangementInstance", arrangementInstance);

		try {
			startupControlService.startArrangementInstance(arrangementInstanceJSON);
		} catch (BizException e) {
			TestStartupControlService.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}

	}
}
