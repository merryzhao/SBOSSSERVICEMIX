/**
 * 
 */
package com.ai.sboss.customer.processor;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

/**
 * @author monica
 *
 */
public class GetPlaceIdListInProcessor implements Processor{

	Logger logger = Logger.getLogger(GetPlaceIdListInProcessor.class);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		logger.info("exchange---->body----->"+exchange.getIn().getBody(String.class));
		
		JSONObject customerInfo = JSONObject.fromObject(exchange.getIn().getBody(String.class)).getJSONObject("data"); 
		JSONArray addressArray = customerInfo.getJSONArray("contact_address");
		List<Long> placeIdList = new ArrayList<Long>();
		for (int i = 0; i < addressArray.size(); i++) {
			JSONObject single = addressArray.getJSONObject(i);
			if (single.containsKey("placeIds")) {
				JSONArray tempList = single.getJSONArray("placeIds");
				for (int j = 0; j < tempList.size(); j++) {
					placeIdList.add(tempList.getLong(j));
				}
			}
		}
		
		int placeIdListSize = placeIdList.size();
		exchange.getIn().setHeader("aiLoopSize", placeIdList);
		
		StringBuffer placeIdBriefList = new StringBuffer();
		for(int i = 0; i < placeIdListSize; i ++){
			JSONObject objectTemp = new JSONObject();
			objectTemp.put("placeId", placeIdList.get(i));
			
			placeIdBriefList.append(objectTemp.toString());
			if((placeIdListSize-1) != i){
				placeIdBriefList.append(";");
			}
		}
		logger.info("placeIdBriefList------->" + placeIdBriefList.toString());
		exchange.getIn().setBody(placeIdBriefList.toString());
	}

}
