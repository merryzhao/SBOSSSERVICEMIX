package com.ai.sboss.proffer.processor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class GetOfferingIdListProcessor implements Processor{
	Logger logger = Logger.getLogger(GetOfferingIdListProcessor.class);
	@Override
	//////////panzy3 add 6/2/2015///////////////////
	public void process(Exchange exchange) throws Exception {
		logger.info("exchange---->body----->"+exchange.getIn().getBody(String.class));
		JSONObject offerings = JSONObject.fromObject(exchange.getIn().getBody(String.class)); 
		JSONArray offeringList = offerings.getJSONObject("data").getJSONArray("offerings");
		int offeringListSize = offeringList.size();
		exchange.getIn().setHeader("aiLoopSize", offeringListSize);
		
		/*JSONArray offeringBriefList = new JSONArray();*/
		StringBuffer offeringBriefList = new StringBuffer();
		for(int i = 0; i < offeringListSize; i ++){
			JSONObject objectTemp = new JSONObject();
			logger.info("offeringId----->"+offeringList.getJSONObject(i).getString("offerId"));
			objectTemp.put("offerId", offeringList.getJSONObject(i).getString("offerId"));
			objectTemp.put("offerCode",offeringList.getJSONObject(i).getString("offeringCode"));
			offeringBriefList.append(objectTemp.toString());
			if(0 != i || offeringListSize != i){
				offeringBriefList.append(";");
			}
		}
		logger.info("offeringBriefList------->" + offeringBriefList.toString());
		exchange.getIn().setBody(offeringBriefList.toString());
	}
}
