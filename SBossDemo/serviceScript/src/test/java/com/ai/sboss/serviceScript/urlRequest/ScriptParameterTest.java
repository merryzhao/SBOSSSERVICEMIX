package com.ai.sboss.serviceScript.urlRequest;

import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ai.sboss.serviceScript.parameter.IScriptParameter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:META-INF/spring/application-config.xml" })
public class ScriptParameterTest {

	private static final Logger LOGGER = Logger
			.getLogger(ScriptParameterTest.class);

	@Resource(name = "httpRequest")
	private IUrlRequest urlRequest;

	@Resource(name = "serviceScriptParameter")
	private IScriptParameter scriptParamOfService;

	@Resource(name = "orderScriptParameter")
	private IScriptParameter scriptParamOfOrder;

	@Resource(name = "processScriptParameter")
	private IScriptParameter scriptParamOfProcess;

	private final String serviceInputParam = "{\"service_id\":25043300,\"order_role\":1,\"user_id\":1}";

	private final String orderInputParam = "{\"order_id\":100000017217,\"order_role\":1,\"user_id\":1}";
	
	private final String processInputParam = "{\"process_id\":\"f1ca7509-e22d-4792-a245-7635b437658768\", \"step_id\":1,\"order_role\":1,\"user_id\":1}";

	@Test
	public void sendPostTestOfService() throws UnsupportedEncodingException {
		JSONObject inputJsonObj = JSONObject.fromObject(serviceInputParam);
		Assert.assertTrue(null != urlRequest);
		final String responseValue = scriptParamOfService
				.getScriptValue(inputJsonObj);
		LOGGER.info(responseValue);
		Assert.assertTrue(!responseValue.isEmpty());
	}

	@Test
	public void sendPostTestOfOrder() throws UnsupportedEncodingException {
		JSONObject inputJsonObj = JSONObject.fromObject(orderInputParam);
		Assert.assertTrue(null != urlRequest);
		final String responseValue = scriptParamOfOrder
				.getScriptValue(inputJsonObj);
		LOGGER.info(responseValue);
		Assert.assertTrue(!responseValue.isEmpty());
	}

	@Test
	public void sendPostTestOfProcess() throws UnsupportedEncodingException {
		JSONObject inputJsonObj = JSONObject.fromObject(processInputParam);
		Assert.assertTrue(null != urlRequest);
		final String responseValue = scriptParamOfProcess
				.getScriptValue(inputJsonObj);
		LOGGER.info(responseValue);
		Assert.assertTrue(!responseValue.isEmpty());
	}
}