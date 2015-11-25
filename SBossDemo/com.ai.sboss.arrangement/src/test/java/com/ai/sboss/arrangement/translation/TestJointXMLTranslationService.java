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
import com.ai.sboss.arrangement.entity.orm.JointEntity;

/**
 * 测试转换服务中，关于joint-xml转换为joint-entity的服务。以及joint-xml转换为joint-json的服务
 * @author yinwenjie
 *
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:application-*.xml"})
public class TestJointXMLTranslationService {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestJointXMLTranslationService.class);
	
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
		JointTranslationService jointXMLTranslationService = this.translationAbstractFactory.getJointTranslationService();
		
		//1、======================
		//加载的xml文件
		InputStream fileIN = null;
		try {
			fileIN = this.getClass().getClassLoader().getResourceAsStream("xml/jointDefinition.xml");
		} catch (Exception e) {
			TestJointXMLTranslationService.LOGGER.error(e.getMessage() , e);
		}
		//开始解析
		JointEntity resultJoint = null;
		try {
			resultJoint = jointXMLTranslationService.translationEntity(fileIN);
			fileIN.close();
		} catch (Exception e) {
			TestJointXMLTranslationService.LOGGER.error(e.getMessage() , e);
		}
		Assert.assertNotNull(resultJoint);
		Assert.assertTrue(resultJoint.getTrades().size() != 0);
		
		//2、======================
		//由于JointXMLTranslationService中预制了对享元的支持，所以这里要重新启动一个JointXMLTranslationService
//		JointXMLTranslationService jointXMLTranslationService1 = this.translationAbstractFactory.getJointXMLTranslationService();
//		Assert.assertNotSame(jointXMLTranslationService, jointXMLTranslationService1);
		fileIN = null;
		try {
			fileIN = this.getClass().getClassLoader().getResourceAsStream("xml/jointDefinitionerror.xml");
		} catch (Exception e) {
			TestJointXMLTranslationService.LOGGER.error(e.getMessage() , e);
		}
		//开始解析
		resultJoint = null;
		try {
			resultJoint = jointXMLTranslationService.translationEntity(fileIN);
			fileIN.close();
		} catch (Exception e) {
			TestJointXMLTranslationService.LOGGER.error(e.getMessage() , e);
		}
	}
	
	@Test
	public void testJSON() {
		JointTranslationService jointXMLTranslationService = this.translationAbstractFactory.getJointTranslationService();
		
		//1、======================
		//加载的xml文件
		InputStream fileIN = null;
		try {
			fileIN = this.getClass().getClassLoader().getResourceAsStream("xml/jointDefinition.xml");
		} catch (Exception e) {
			TestJointXMLTranslationService.LOGGER.error(e.getMessage() , e);
		}
		
		//开始转换json
		JSONObject jsonResult = null;
		try {
			jsonResult = jointXMLTranslationService.translationJSON(fileIN);
			fileIN.close();
		} catch(Exception e) {
			TestJointXMLTranslationService.LOGGER.error(e.getMessage() , e);
		}
		
		Assert.assertNotNull(jsonResult);
	}
}