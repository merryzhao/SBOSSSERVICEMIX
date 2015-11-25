package com.ai.sboss.serviceScript.parameter;

import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.ai.sboss.serviceScript.urlRequest.IUrlRequest;

public abstract class DefaultAbstractScriptParameter implements
		IScriptParameter {

	private static final Logger LOGGER = Logger
			.getLogger(DefaultAbstractScriptParameter.class);

	@Resource(name = "httpRequest")
	protected IUrlRequest urlRequest;
	
	protected JSONObject inputParam = null;

	public String getScriptValue(JSONObject inputParam) throws UnsupportedEncodingException {
		if (null == inputParam){
			throw new IllegalArgumentException("The object of input JSON parameter is empty");
		}
		
		this.inputParam = inputParam;
		
		// Construct URL String
		buildCRMFormatedUrl();

		// Get returned data from CRM system
		final String retPostData = getResponseBySendPost();
		LOGGER.info(retPostData);
		
		// Extract CRM's data and build front-end need's result
		return extractValuableInfo(retPostData);
	}

	protected abstract String getResponseBySendPost();

	protected abstract String extractValuableInfo(String requestData);
	
	protected abstract void buildCRMFormatedUrl();
}