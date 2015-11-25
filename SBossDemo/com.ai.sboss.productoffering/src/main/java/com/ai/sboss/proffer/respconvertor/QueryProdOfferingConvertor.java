package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ai.sboss.common.respconvertor.CrmRespConvertor;

public class QueryProdOfferingConvertor extends CrmRespConvertor {

	@Override
	public String convert2Resp(String json) {
		JSONObject prodOfferings = JSONObject.fromObject(json);
		JSONObject prodOfferingsBody = prodOfferings.getJSONObject("data");
		JSONArray prodOfferingList = prodOfferingsBody.getJSONArray("offerings");
		JSONObject idJsonTemp = new JSONObject();
		JSONArray arrayResponseJson = new JSONArray();
		for (int i=0; i<prodOfferingsBody.size(); i++){
			idJsonTemp.put("id", prodOfferingList.getJSONObject(i).getString("offerId"));
			arrayResponseJson.add(idJsonTemp);
			idJsonTemp.clear();
		}		
		return arrayResponseJson.toString();
	}
}
