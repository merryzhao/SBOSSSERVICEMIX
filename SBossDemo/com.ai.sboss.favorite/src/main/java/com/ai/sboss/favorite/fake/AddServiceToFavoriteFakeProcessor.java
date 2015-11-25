package com.ai.sboss.favorite.fake;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Writer;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class AddServiceToFavoriteFakeProcessor implements Processor {
	private final static String FAKEFILE = "data/fakedata/favouriteservicelist.properties";
	private final static String SERVICESRC = "data/fakedata/productdetailinfo.properties";
	private final static String RET_FMT = "{\"data\":{{\"result_code\":\"<CODE>\",\"result_msg\":\"<MSG>\"}},\"desc\":{\"result_code\":200,\"result_msg\":\"success\",\"data_mode\":0,\"digest\":\"\"}}";
	Logger logger = Logger.getLogger(DelServiceFromFavoriteFakeProcessor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();
		String params = "";
		if ("POST".equals(inMessage.getHeader(Exchange.HTTP_METHOD))) {
			params = inMessage.getBody(String.class);
		} else if ("GET".equals(inMessage.getHeader(Exchange.HTTP_METHOD))) {
			params = inMessage.getHeader(Exchange.HTTP_QUERY, String.class);
		}
		Long serviceId = JSONObject.fromObject(params).getJSONObject("data").getLong("service_id");
		boolean foundtarget = false;
		JSONArray favoriteserviceList = new JSONArray();
		try {
			LineNumberReader linereader = new LineNumberReader(new FileReader(
					FAKEFILE));
			String filecontent = linereader.readLine();
			if (filecontent != null) {
				favoriteserviceList = JSONArray.fromObject(filecontent);
			}
			linereader.close();
		} catch (IOException e) {
			logger.error(FAKEFILE+e.toString());
		}
		JSONObject selectedservice = new JSONObject();
		try {
			LineNumberReader linereader = new LineNumberReader(new FileReader(
					SERVICESRC));
			String filecontent = "";
			while ((filecontent = linereader.readLine()) != null) {
				JSONObject service = JSONObject.fromObject(filecontent).getJSONObject("data");
				if (service.getLong("service_id") == serviceId) {
					foundtarget = true;
					selectedservice.put("service_id", service.getLong("service_id"));
					selectedservice.put("service_name", service.getString("service_name"));
					selectedservice.put("service_intro", service.getString("service_intro"));
					selectedservice.put("service_price", service.getString("service_price"));
					selectedservice.put("service_thumbnail_url","");
					selectedservice.put("service_tags","[]");
					favoriteserviceList.add(selectedservice);
					break;
				}
			}
			linereader.close();
		} catch (IOException e) {
			logger.error(SERVICESRC+e.toString());
		}
		
		if (foundtarget) {
			inMessage.setBody(RET_FMT.replace("<CODE>", "1").replace("<MSG>", "修改收藏内容成功"));
			writeBack(favoriteserviceList.toString());
		} else {
			inMessage.setBody(RET_FMT.replace("<CODE>", "0").replace("<MSG>", "未找到对应收藏内容"));
		}
	}
	
	private boolean writeBack(String content) {
		try {
			Writer writer = new FileWriter(new File(FAKEFILE), false);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			logger.error("Fake file:" + FAKEFILE + " not found");
		}
		return true;
	}

}
