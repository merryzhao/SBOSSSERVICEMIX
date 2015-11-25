package com.ai.sboss.favorite.fake;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class QueryCollectedContentsFakeProcessor implements Processor {
	private final static String FAKEFILE = "data/fakedata/favouritecontentlist.properties";
	private final static String RET_FMT = "{\"data\":{\"collected_contents\":<LIST>},\"desc\":{\"result_code\":200,\"result_msg\":\"success\",\"data_mode\":0,\"digest\":\"\"}}";
	Logger logger = Logger.getLogger(QueryCollectedContentsFakeProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();
		String params = "";
		if ("POST".equals(inMessage.getHeader(Exchange.HTTP_METHOD))) {
			params = inMessage.getBody(String.class);
		} else if ("GET".equals(inMessage.getHeader(Exchange.HTTP_METHOD))) {
			params = inMessage.getHeader(Exchange.HTTP_QUERY, String.class);
		}

		JSONObject requestjson = JSONObject.fromObject(params).getJSONObject("data");

		String contentList = "";
		try {
			LineNumberReader linereader = new LineNumberReader(new FileReader(
					FAKEFILE));
			contentList = linereader.readLine();
			if (contentList.equals("") || contentList == null) {
				logger.error(FAKEFILE + "no data");
			}
			linereader.close();
		} catch (IOException e) {
			logger.info(FAKEFILE + " read failed:" + e.toString());
		}
		if (requestjson.containsKey("content_tags")) {
			contentList = queryByTags(requestjson.getJSONArray("content_tags"),
					contentList);
		} else if (requestjson.containsKey("content_origin")) {
			contentList = queryByOrigin(
					requestjson.getString("content_origin"), contentList);
		} else if (requestjson.containsKey("content_favorite_flag")) {
			contentList = queryByFavorite(
					requestjson.getLong("content_favorite_flag"), contentList);
		} else {
			contentList = "invalid arguments";
		}
		logger.info("Found ret:" + contentList);
		inMessage.setBody(RET_FMT.replace("<LIST>", contentList));
	}

	private String queryByTags(JSONArray tags, String contents) {
		JSONArray ret = JSONArray.fromObject(contents);
		for (int j = 0; j < ret.size(); ++j) {
			JSONArray content_tags = ret.getJSONObject(j).getJSONArray("content_tags");
			if (!content_tags.contains(tags.get(0))) {
				ret.remove(j);
			}

		}
		return ret.toString();
	}

	private String queryByOrigin(String origin, String contents) {
		JSONArray ret = JSONArray.fromObject(contents);
		for (int j = 0; j < ret.size(); ++j) {
			JSONArray content_origin = ret.getJSONObject(j).getJSONArray("content_origin");
			if (!content_origin.equals(origin)) {
				ret.remove(j);
			}
		}
		return ret.toString();
	}

	private String queryByFavorite(Long favorite, String contents) {
		
		JSONArray ret = JSONArray.fromObject(contents);
		for (int j = 0; j < ret.size(); ++j) {
			Long isFavorite = ret.getJSONObject(j).getLong("content_favorite_flag");
			if (!isFavorite.equals(favorite)) {
				ret.remove(j);
			}
		}
		return ret.toString();
	}
}
