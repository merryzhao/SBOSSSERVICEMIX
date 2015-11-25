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


public class ModifyServiceTagsFakeProcessor implements Processor {
	private final static String FAKEFILE = "data/fakedata/favouriteservicelist.properties";
	private final static String RET_FMT = "{\"data\":{{\"result_code\":\"<CODE>\",\"result_msg\":\"<MSG>\"}},\"desc\":{\"result_code\":200,\"result_msg\":\"success\",\"data_mode\":0,\"digest\":\"\"}}";
	Logger logger = Logger.getLogger(ModifyServiceTagsFakeProcessor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();
		String params = "";
		if ("POST".equals(inMessage.getHeader(Exchange.HTTP_METHOD))) {
			params = inMessage.getBody(String.class);
		} else if ("GET".equals(inMessage.getHeader(Exchange.HTTP_METHOD))) {
			params = inMessage.getHeader(Exchange.HTTP_QUERY, String.class);
		}
		Long favoriteId = JSONObject.fromObject(params).getJSONObject("data").getLong("favorite_id");
		JSONArray newtags = JSONObject.fromObject(params)
				.getJSONObject("data").getJSONArray("service_tags");
		JSONArray favoritelist = readFavoriteList();
		boolean foundtarget = false;
		for (int i = 0; i < favoritelist.size(); ++i) {
			if (favoritelist.getJSONObject(i).getLong("favorite_id") == favoriteId) {
				favoritelist.getJSONObject(i).put("service_tags", newtags);
				foundtarget = true;
			}
		}
		if (foundtarget) {
			inMessage.setBody(RET_FMT.replace("<CODE>", "1").replace("<MSG>", "修改服务标签成功"));
		} else {
			inMessage.setBody(RET_FMT.replace("<CODE>", "0").replace("<MSG>", "未找到对应服务"));
		}
		writeBack(favoritelist.toString());
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
	
	private JSONArray readFavoriteList() {
		JSONArray favoritelist = new JSONArray();

		try {
			LineNumberReader linereader = new LineNumberReader(new FileReader(FAKEFILE));
			String content = linereader.readLine();
			if (content != null) {
				favoritelist = JSONArray.fromObject(content);
			}
			linereader.close();
		} catch (IOException e) {
			logger.error("Fake file:" + FAKEFILE + " not found");
		}
		return favoritelist;
	}
}
