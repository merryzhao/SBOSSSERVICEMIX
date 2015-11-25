package com.ai.sboss.favorite.processor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

/**
 * @author monica
 * 获取从前端传入的content_id列表，并将其分割成符合要求的list
 */

public class GetContentIdsInProcessor implements IBasicOutProcessor {
	
	Logger logger = Logger.getLogger(GetContentIdsInProcessor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		JSONObject body = JSONObject.fromObject(in.getBody(String.class));
		if (!body.containsKey("data")) {
			throw new Exception("data is null, no content id!");
		}
		String ret = convert2requst(body.getString("data"));
		in.setBody(ret);
		
	}

	@Override
	public String convert2requst(String data) {
		
		// content_id与favorite_id是相同的
		logger.info("GetContentIdsInProcessor input --->" + data);
		JSONArray contentIdsArray = JSONArray.fromObject(data);
		//定义返回参数
		StringBuffer contentIdsList = new StringBuffer();
		for(int i = 0; i < contentIdsArray.size(); i++){
			JSONObject objectTemp = new JSONObject();
			objectTemp.put("contentId", contentIdsArray.getJSONObject(i).getLong("content_id"));
			contentIdsList.append(objectTemp);
			if(0 != i || contentIdsArray.size() != i){
				contentIdsList.append(";");
			}
		}
		logger.info("GetContentIdsInProcessor contentIdsList--->" + contentIdsList.toString());
		return contentIdsList.toString();
	}

}
