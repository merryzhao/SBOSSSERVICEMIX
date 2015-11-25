package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryServicesbyKeyInnerConvertor implements IBasicOutProcessor {
	private Logger logger = Logger.getLogger(QueryServicesbyKeyInnerConvertor.class);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		String retString = convert2requst(in.getBody(String.class));
		in.setHeaders(null);
		logger.info("queried services==>"+retString);
		in.setBody(retString);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject crmret = JSONObject.fromObject(data);
		if (StringUtils.equals(crmret.getString("hub_code"), "0")) {
			return null;
		}
		JSONObject dataval = crmret.getJSONObject("data");
		int offeringListSize = dataval.getJSONArray("offerings").size();
		/*JSONArray offeringBriefList = new JSONArray();*/
		StringBuffer offeringBriefList = new StringBuffer();
		for(int i = 0; i < offeringListSize; i ++){
			JSONObject objectTemp = new JSONObject();
			objectTemp.put("offerId", dataval.getJSONArray("offerings").getJSONObject(i).getLong("offerId"));
			objectTemp.put("offerCode",dataval.getJSONArray("offerings").getJSONObject(i).getString("offeringCode"));
			logger.info("objectTemp==>"+objectTemp.toString());
			offeringBriefList.append(objectTemp.toString());
			if(0 != i || offeringListSize != i){
				offeringBriefList.append(";");
			}
		}
		
		return offeringBriefList.toString();
	}

}
