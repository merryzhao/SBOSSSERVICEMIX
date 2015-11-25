package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONObject;

import com.ai.sboss.common.respconvertor.CrmRespConvertor;

public class QueryServiceProviderConvertor extends CrmRespConvertor {

	@Override
	public String convert2Resp(String json) {
		JSONObject offeringProvider = JSONObject.fromObject(json);
		JSONObject offeringProviderBody = offeringProvider.getJSONObject("data");
		return offeringProviderBody.toString();
	}
}