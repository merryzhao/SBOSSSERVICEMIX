package com.ai.sboss.arrangement.translation;

import java.io.InputStream;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.ai.sboss.arrangement.JUnit4ClassRunner;
import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;

/**
 * 测试转换服务中，关于arrangement.xml转换为arrangement-entity的服务。
 * 以及arrangement-xml转换为arrangement-json的服务
 * @author yinwenjie
 *
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:application-*.xml"})
public class TestArrangementXMLTranslationService {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestArrangementXMLTranslationService.class);
	
	@Autowired
	private TranslationAbstractFactory translationAbstractFactory;
	
	/**
	 * 
	 */
	@Test
	public void testEntity() {
		/*
		 * 1、首先测试一个xml规范的文本
		 * 2、测试各种xml不规范的文本
		 * */
		ArrangementTranslationService arrangementXMLTranslationService = this.translationAbstractFactory.getArrangementTranslationService();
		
		//1、======================
		//加载的xml文件
		InputStream fileIN = null;
		try {
			fileIN = this.getClass().getClassLoader().getResourceAsStream("xml/arrangementDefinition.xml");
		} catch (Exception e) {
			TestArrangementXMLTranslationService.LOGGER.error(e.getMessage() , e);
			Assert.assertTrue(false);
		}
		//开始解析
		ArrangementEntity resultArrangement = null;
		try {
			resultArrangement = arrangementXMLTranslationService.translationEntity(fileIN);
			fileIN.close();
		} catch (Exception e) {
			TestArrangementXMLTranslationService.LOGGER.error(e.getMessage() , e);
			Assert.assertTrue(false);
		}
		Assert.assertNotNull(resultArrangement);
		Assert.assertTrue(resultArrangement.getJointmapping().size() >= 1);
	}
	
	@Test
	public void testJSON() {
		ArrangementTranslationService arrangementXMLTranslationService = this.translationAbstractFactory.getArrangementTranslationService();
		
		//1、======================
		//加载的xml文件
		InputStream fileIN = null;
		try {
			fileIN = this.getClass().getClassLoader().getResourceAsStream("xml/arrangementDefinition.xml");
		} catch (Exception e) {
			TestArrangementXMLTranslationService.LOGGER.error(e.getMessage() , e);
		}
		
		//开始转换json
		JSONObject jsonResult = null;
		try {
			jsonResult = arrangementXMLTranslationService.translationJSON(fileIN);
			fileIN.close();
		} catch(Exception e) {
			TestArrangementXMLTranslationService.LOGGER.error(e.getMessage() , e);
		}
		
		Assert.assertNotNull(jsonResult);
	}
}