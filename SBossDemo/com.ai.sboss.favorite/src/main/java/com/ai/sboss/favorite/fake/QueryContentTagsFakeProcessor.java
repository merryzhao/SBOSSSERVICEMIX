package com.ai.sboss.favorite.fake;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class QueryContentTagsFakeProcessor implements Processor {
	private final static String RET_FMT = "{\"data\":{{\"service_tags\":\"<TAGS>\"}},\"desc\":{\"result_code\":200,\"result_msg\":\"success\",\"data_mode\":0,\"digest\":\"\"}}";
	private final static String FAKEFILE = "data/fakedata/favouriteservicelist.properties";
	Logger logger = Logger.getLogger(QueryContentTagsFakeProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {

		JSONArray favoritelist = readFavoriteList();
		JSONArray retArray = new JSONArray();
		for (int i = 0; i < favoritelist.size(); ++i) {
			JSONArray tags = favoritelist.getJSONObject(i).getJSONArray("service_tags");
			for (int j = 0; j < tags.size(); ++j) {
				JSONObject tag = tags.getJSONObject(j);
				if (!retArray.contains(tag)) {
					retArray.add(tag);
				}
			}

		}
		exchange.getIn().setBody(RET_FMT.replace("<TAGS>", retArray.toString()));
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
