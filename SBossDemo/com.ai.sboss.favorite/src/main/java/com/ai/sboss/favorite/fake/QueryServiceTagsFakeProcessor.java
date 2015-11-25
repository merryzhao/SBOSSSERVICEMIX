package com.ai.sboss.favorite.fake;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class QueryServiceTagsFakeProcessor implements Processor {
	private final static String FAKEFILE = "data/fakedata/favouritecontentlist.properties";
	private final static String RET_FMT = "{\"data\":{\"service_tags\":<TAGS>},\"desc\":{\"result_code\":200,\"result_msg\":\"success\",\"data_mode\":0,\"digest\":\"\"}}";
	Logger logger = Logger.getLogger(QueryServiceTagsFakeProcessor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		JSONArray tagslist = new JSONArray();
		try {
			LineNumberReader linereader = new LineNumberReader(new FileReader(
					FAKEFILE));
			String filecontent = linereader.readLine();
			if (filecontent != null) {
				JSONArray contentlist = JSONArray.fromObject(filecontent);
				for (int j = 0; j < contentlist.size(); ++j) {
					JSONObject content = contentlist.getJSONObject(j);
					tagslist.addAll(content.getJSONArray("service_tags"));
				}
			}
			linereader.close();
		} catch (IOException e) {
			logger.error(FAKEFILE+e.toString());
		}
		
		exchange.getIn().setBody(RET_FMT.replace("<TAGS>", tagslist.toString()));
	}

}
