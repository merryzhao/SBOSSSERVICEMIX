package com.ai.sboss.arrangement.translation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.ai.sboss.arrangement.JUnit4ClassRunner;
import com.ai.sboss.arrangement.entity.orm.ArrangementInstanceEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;


/**
 * 测试arrangement实例翻译服务
 * @author yinwenjie
 *
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:application-*.xml"})
public class TestArrangementInstanceTranslationService {
	private static final Log LOGGER = LogFactory.getLog(TestArrangementInstanceTranslationService.class);
	
	/**
	 * 翻译服务工厂
	 */
	@Autowired
	private TranslationAbstractFactory translationAbstractFactory;
	
	/**
	 * 
	 */
	@Test
	public void testTranslationEntity_json() {
		/*
		 * 首先取得json测试数据，在：json/arrangementinstancejson
		 * 
		 * 然后开始翻译，并查看翻译效果
		 * */
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("json/arrangementinstancejson");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		int maxLen = 9028;
		int realLen;
		byte[] contexts = new byte[9028];
		try {
			while((realLen = in.read(contexts, 0, maxLen)) != -1) {
				out.write(contexts, 0, realLen);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			TestArrangementInstanceTranslationService.LOGGER.error(e.getMessage(), e);
			return;
		}
		
		String jsonString = new String(out.toByteArray());
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		
		//好了，开始翻译
		ArrangementInstanceTranslationService arrangementInstanceTranslationService = this.translationAbstractFactory.getArrangementInstanceTranslationService();
		ArrangementInstanceEntity arrangementInstanceEntity = null;
		try {
			arrangementInstanceEntity = arrangementInstanceTranslationService.translationEntity(jsonObject);
		} catch (BizException e) {
			TestArrangementInstanceTranslationService.LOGGER.error(e.getMessage(), e);
		}
		
		Assert.assertNotNull(arrangementInstanceEntity);
	}
	
	@Test
	public void testTranslationFlows_XML() {
		String goodxmlText = "<begin id=\"begin\"/><process source=\"flow:begin\" target=\"joint:123\" /><process source=\"joint:123\" target=\"joint:456\"/><process source=\"joint:456\" target=\"flow:end\"/><end id=\"end\"/>";
		String brokenxmlText = "<begin id=\"begin\"/><begin id=\"begin\"/><process source=\"flow:begin\" target=\"joint:XXXXXX\" /><process source=\"joint:XXXXXX\" target=\"joint:XXXXXX\"/><process source=\"joint:XXXXXX\" target=\"flow:end\"/><end id=\"end\"/>";
		//开始翻译
		ArrangementInstanceTranslationService arrangementInstanceTranslationService = this.translationAbstractFactory.getArrangementInstanceTranslationService();
		List<Map.Entry<String, String>> flowListMap = null;
		try {
			flowListMap = arrangementInstanceTranslationService.translationFlows(goodxmlText);
		} catch (BizException e) {
			TestArrangementInstanceTranslationService.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		Assert.assertNotNull(flowListMap);
		TestArrangementInstanceTranslationService.LOGGER.info("List size == "+flowListMap.size());
		TestArrangementInstanceTranslationService.LOGGER.info("===============");
		for (int i = 0; i < flowListMap.size(); ++i) {
			TestArrangementInstanceTranslationService.LOGGER.info(flowListMap.get(i).getKey()+"->"+flowListMap.get(i).getValue());
		}
		TestArrangementInstanceTranslationService.LOGGER.info("===============");
		Assert.assertTrue(flowListMap.size() == 2);
		
		//错误尝试
		flowListMap = null;
		try {
			flowListMap = arrangementInstanceTranslationService.translationFlows(brokenxmlText);
		} catch (BizException e) {
			Assert.assertTrue(e.getResponseCode() == ResponseCode._402);
		}
		Assert.assertNull(flowListMap);
	}
}
