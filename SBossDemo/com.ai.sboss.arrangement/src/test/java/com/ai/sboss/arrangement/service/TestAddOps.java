package com.ai.sboss.arrangement.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.ai.sboss.arrangement.JUnit4ClassRunner;
import com.ai.sboss.arrangement.entity.JsonEntity;
import com.ai.sboss.arrangement.exception.ResponseCode;
import com.ai.sboss.arrangement.utils.JSONUtils;

@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:application-*.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestAddOps {
	
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestAddOps.class);
	
	/**
	 * 
	 */
	@Autowired
	private IAddOps assOps;
	
	/**
	 * 读取的xml文件信息
	 */
	private String jointXmlText;
	
	@Before
	public void initXML() throws Exception {
		/*
		 * 使用xml/jointDefinition.xml模板进行joint的添加
		 * */
		InputStream filein = this.getClass().getClassLoader().getResourceAsStream("xml/jointDefinition1.xml");
		ByteArrayOutputStream byteout = new ByteArrayOutputStream();
		
		//开始读取
		int realLen;
		byte[] contextBytes = new byte[9620];
		while((realLen = filein.read(contextBytes, 0, 9620)) != -1) {
			byteout.write(contextBytes, 0, realLen);
		}
		filein.close();
		byte[] fileBytes = byteout.toByteArray();
		byteout.close();
		
//		//存储到全局
//		this.jointXmlText = new String(fileBytes);
//		filein = this.getClass().getClassLoader().getResourceAsStream("xml/jointDefinition2.xml");
//		byteout = new ByteArrayOutputStream();
//		
//		//开始读取
//		contextBytes = new byte[9620];
//		while((realLen = filein.read(contextBytes, 0, 9620)) != -1) {
//			byteout.write(contextBytes, 0, realLen);
//		}
//		filein.close();
//		fileBytes = byteout.toByteArray();
//		byteout.close();
//		this.jointXmlText2 = new String(fileBytes);
		/*
		 * 使用xml/jointDefinition.xml模板进行arrangement的添加
		 * */
//		filein = this.getClass().getClassLoader().getResourceAsStream("xml/jointDefinition1.xml");
//		byteout = new ByteArrayOutputStream();
//		//开始读取
//		contextBytes = new byte[9620];
//		while((realLen = filein.read(contextBytes, 0, 9620)) != -1) {
//			byteout.write(contextBytes, 0, realLen);
//		}
//		filein.close();
//		fileBytes = byteout.toByteArray();
//		byteout.close();
		
		//存储到全局
		this.jointXmlText = new String(fileBytes);
	}
	
	@Test
	public void test1_Joint() {
		JsonEntity jsonEntity = this.assOps.addJointItem(this.jointXmlText);
		TestAddOps.LOGGER.info("jsonEntity = " + JSONUtils.toString(jsonEntity));
		Assert.assertNotNull(jsonEntity);
		
		Assert.assertEquals(jsonEntity.getDesc().getResult_code(), ResponseCode._200);
	}
	
	@Test
	public void test2_Arrangement() {
		JsonEntity jsonEntity = this.assOps.addDefaultArrangementItem(JSONObject.fromObject(this.jointXmlText));
		TestAddOps.LOGGER.info("jsonEntity = " + JSONUtils.toString(jsonEntity));
		Assert.assertNotNull(jsonEntity);
		
		Assert.assertEquals(jsonEntity.getDesc().getResult_code(), ResponseCode._200);
	}
}
