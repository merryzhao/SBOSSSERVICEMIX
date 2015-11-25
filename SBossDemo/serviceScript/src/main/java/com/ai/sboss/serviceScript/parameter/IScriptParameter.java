package com.ai.sboss.serviceScript.parameter;

import java.io.UnsupportedEncodingException;

import net.sf.json.JSONObject;

public interface IScriptParameter {
	public String getScriptValue(JSONObject inputParam) throws UnsupportedEncodingException;
}