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

public class QueryServicesByContentFakeProcessor implements Processor {
	private final static String FAKEFILE = "data/fakedata/favouritecontentlist.properties";
	private final static String RET_FMT = "{\"data\":{\"collected_contents\":<LIST>},\"desc\":{\"result_code\":200,\"result_msg\":\"success\",\"data_mode\":0,\"digest\":\"\"}}";
	Logger logger = Logger.getLogger(QueryServicesByContentFakeProcessor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		JSONArray servicelist = new JSONArray();
		Message inMessage = exchange.getIn();
		String params = "";
		if ("POST".equals(inMessage.getHeader(Exchange.HTTP_METHOD))) {
			params = inMessage.getBody(String.class);
		} else if ("GET".equals(inMessage.getHeader(Exchange.HTTP_METHOD))) {
			params = inMessage.getHeader(Exchange.HTTP_QUERY, String.class);
		}
		JSONObject paramdata = JSONObject.fromObject(params).getJSONObject("data");
		Long serviceId = paramdata.getLong("favorite_id");
		boolean foundtarget = false;
		JSONArray contentlist = new JSONArray();
		try {
			LineNumberReader linereader = new LineNumberReader(new FileReader(
					FAKEFILE));
			String filecontent = linereader.readLine();
			if (filecontent != null) {
				contentlist = JSONArray.fromObject(filecontent);
				for (int j = 0; j < contentlist.size(); ++j) {
					JSONObject content = contentlist.getJSONObject(j);
					Long content_id = content.getLong("favorite_id");
					if (content_id.equals(serviceId)) {
						servicelist.addAll(content.getJSONArray("content_services"));
						foundtarget = !foundtarget;
						break;
					}
				}
			}
			linereader.close();
		} catch (IOException e) {
			logger.error(FAKEFILE+e.toString());
		}
		if (foundtarget && paramdata.containsKey("service_tags")) {
			String key = paramdata.getJSONArray("service_tags").getJSONObject(0).getString("tag_name");
			for (int i = 0; i < servicelist.size(); ++i) {
				if (servicelist.getJSONObject(i).getJSONArray("service_tags").contains(key)) {
					continue;
				}
				servicelist.remove(i);
			}
		}
		inMessage.setBody(RET_FMT.replace("<LIST>", servicelist.toString()));
	}

}
