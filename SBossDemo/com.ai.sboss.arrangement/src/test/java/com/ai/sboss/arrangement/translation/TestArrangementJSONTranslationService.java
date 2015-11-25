package com.ai.sboss.arrangement.translation;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.ai.sboss.arrangement.JUnit4ClassRunner;
import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;
import com.ai.sboss.arrangement.exception.BizException;

@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:application-*.xml"})
public class TestArrangementJSONTranslationService {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestArrangementJSONTranslationService.class);
	
	@Autowired
	private ArrangementTranslationService arrangementJSONTranslationService;
	
	/**
	 * 最初的json对象
	 */
	private JSONObject arrangementJSONObject;
	
	@Before
	public void before() throws Exception {
		/*
		 * 初始化的过程，主要是从arrangementjson文件中读取json信息的过程
		 * 文件的读取位置在：json/arrangementjson
		 * */
		InputStream filein = this.getClass().getClassLoader().getResourceAsStream("json/arrangementjson");
		ByteArrayOutputStream bytesout = new ByteArrayOutputStream();
		
		//开始读取
		int maxlen = 9065;
		int reallen;
		byte[] readContext = new byte[maxlen];
		while((reallen = filein.read(readContext, 0, maxlen)) != -1) {
			bytesout.write(readContext, 0, reallen);
		}
		filein.close();
		bytesout.close();
		
		//生成json对象
		String jsonTxt = new String(bytesout.toByteArray());
		this.arrangementJSONObject = JSONObject.fromObject(jsonTxt);
	}
	
	@Test
	public void testjson() {
		
		ArrangementEntity arrangement = null;
		try {
			arrangement = this.arrangementJSONTranslationService.translationEntity(this.arrangementJSONObject);
		} catch(BizException e) {
			TestArrangementJSONTranslationService.LOGGER.error(e.getMessage() , e);
			Assert.assertTrue(false);
			return;
		}
		
		Assert.assertNotNull(arrangement);
		
		Assert.assertTrue(arrangement.getChildArrangements().size() > 0);
		Assert.assertTrue(arrangement.getJointmapping().size() > 0);
		Assert.assertTrue(arrangement.getFlows() != null);
		TestArrangementJSONTranslationService.LOGGER.info("flows:" + arrangement.getFlows());
	}
}