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

public class QueryCollectedServicesFakeProcessor implements Processor {
	private final static String FAKEFILE = "data/fakedata/favouriteservicelist.properties";
	Logger logger = Logger.getLogger(QueryCollectedServicesFakeProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("***********QueryCollectedServicesFakeProcessor************");
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

		Message inMessage = exchange.getIn();
		Long params = JSONObject.fromObject(inMessage.getBody(String.class)).getLong("product_id");
		JSONObject found = null;
		for (int i = 0; i < favoritelist.size(); ++i) {
			JSONObject service = favoritelist.getJSONObject(i);
			if (service.getLong("service_id") == params) {
				found = service;
				break;
			}

		}
		if (found != null) {
			inMessage.setBody(found.toString());
		} else {
			logger.error("service not found");
			inMessage.setBody("{}");
		}
	}

}
