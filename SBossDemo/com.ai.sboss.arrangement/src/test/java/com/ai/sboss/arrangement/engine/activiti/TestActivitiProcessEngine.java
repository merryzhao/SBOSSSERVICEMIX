/**
 * 
 */
package com.ai.sboss.arrangement.engine.activiti;

import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;

import com.ai.sboss.arrangement.JUnit4ClassRunner;

/**
 * ActivitiProcessEngine测试
 * 
 * @author chaos
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-*.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestActivitiProcessEngine {
//	/**
//	 * 日志
//	 */
//	private static final Log LOGGER = LogFactory.getLog(TestActivitiProcessEngine.class);
//	
//	private static List<String> deployList = new ArrayList<String>(); 
//
//	@Autowired
//	private IStartupControlService activitiProcessEngine;
//
//	@BeforeClass
//	public static void setUp() throws Exception {
//		TestActivitiProcessEngine.LOGGER.info("===setUp===");
//	}
//
//	@AfterClass
//	public static void tearDown() {
//		TestActivitiProcessEngine.LOGGER.info("===tearDown===");
//		deployList = null;
//	}
//	
//	@Test
//	@Rollback(false)
//	public void test1_deploy() {
//		TestActivitiProcessEngine.LOGGER.info("===测试deploy===");
//		String ret = null;
//		try {
////			ret = activitiProcessEngine.deployProcess("bpmn/testCamelProcess3.bpmn");
//			deployList.add(ret);
//		} catch (BizException e) {
//			TestActivitiProcessEngine.LOGGER.error(e.toString());
//			assertTrue(false);
//			return;
//		}
//		assertTrue(ret != null);
//		TestActivitiProcessEngine.LOGGER.info("===business ID==="+ret);
//	}
//
//	@Test
//	@Rollback(false)
//	public void test2_getProcessList() {
//		TestActivitiProcessEngine.LOGGER.info("====测试getProcessList====");
//		List<String> ret = null;
//		try {
//			ret = activitiProcessEngine.getProcessList();
//		} catch (BizException e) {
//			TestActivitiProcessEngine.LOGGER.error(e.toString());
//			assertTrue(false);
//			return;
//		}
//		assertNotNull(ret);
//	}
//	
//	@Test
//	@Rollback(false)
//	public void test9_deleteProcess() {
//		List<String> ret = null;
//		TestActivitiProcessEngine.LOGGER.info("====测试deleteProcess====");
//		try {
//			for (String id: deployList) {
//				activitiProcessEngine.deleteProcess(id);
//			}
//			ret = activitiProcessEngine.getProcessList();
//		} catch (BizException e) {
//			// TODO: handle exception
//		}
//		assertTrue(ret == null);
//	}
}
