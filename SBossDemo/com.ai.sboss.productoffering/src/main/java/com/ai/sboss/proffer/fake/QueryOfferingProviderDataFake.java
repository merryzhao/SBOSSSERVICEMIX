package com.ai.sboss.proffer.fake;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class QueryOfferingProviderDataFake implements Processor {
	Logger logger = Logger.getLogger(QueryOfferingProviderDataFake.class);
	private final String ACCOUNT_LIST = "data/fakedata/accountlist.properties";

	private String queryAccount(Long queryId) throws IOException {
		//logger.info("===queryAccount:"+queryId+"===");
		LineNumberReader linereader = new LineNumberReader(new InputStreamReader(new FileInputStream(ACCOUNT_LIST), "UTF-8"));
		String content = "";
		while ((content=linereader.readLine()) != null) {
			if (!content.startsWith("#")) {
				JSONObject currentAccount = JSONObject.fromObject(content);
				if (currentAccount.getLong("id") == queryId) {
					logger.info("===got Account:"+queryId+"==info=>"+currentAccount.toString());
					break;
				}
			}
		}
		linereader.close();
		return content;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		String body = in.getBody(String.class);
		String ret = null;
		if (body != null) {
			Long providerId = JSONObject.fromObject(body).getLong("provider_id");
			String reponse = queryAccount(providerId);
			JSONObject retObject = new JSONObject();
			retObject.put("provider", reponse);
			ret = retObject.toString();
		}
		in.setBody(ret);		
	}
}
