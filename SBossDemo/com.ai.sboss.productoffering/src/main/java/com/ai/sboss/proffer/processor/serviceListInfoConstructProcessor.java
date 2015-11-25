package com.ai.sboss.proffer.processor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class serviceListInfoConstructProcessor implements Processor{
	Logger logger = Logger.getLogger(serviceListInfoConstructProcessor.class);
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":1,\"result_msg\":\"\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	@Override
	//////////panzy3 add 6/2/2015///////////////////
	public void process(Exchange exchange) throws Exception {
		
		JSONArray serviceList = JSONArray.fromObject("["+exchange.getIn().getBody().toString()+"]");
		String pageNum = Integer.toString(serviceList.size());
		JSONObject data = new JSONObject();
		
		for(int i = 0; i < serviceList.size(); i++){
			JSONObject singleService = serviceList.getJSONObject(i);
			Long finalProviderId = null;
			Long tempProviderId = singleService.getLong("provider_id");
			if(tempProviderId == 0){
				singleService.remove("provider_id");
				finalProviderId = -1L;
				singleService.put("provider_id", finalProviderId);
			}
		}
		
		data.put("page_num", pageNum);
		data.put("service_list", serviceList.toString());
		logger.info("serviceListInfoConstructProcessor.service_list=====>" + serviceList);
		String returnJson = RETFMT.replace("<DATA>", data.toString());
		exchange.getIn().setBody(returnJson);
	}
}
