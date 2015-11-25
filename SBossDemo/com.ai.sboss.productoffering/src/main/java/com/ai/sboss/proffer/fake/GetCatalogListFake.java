package com.ai.sboss.proffer.fake;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class GetCatalogListFake implements Processor {
	Logger logger = Logger.getLogger(GetCatalogListFake.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		String retString = exchange.getIn().getBody(String.class);
		JSONArray cataloglist = JSONObject.fromObject(retString).getJSONObject("data").getJSONArray("catalog_list");
		JSONObject retObject = new JSONObject();
		retObject.put("cataloglist", cataloglist.toString());
		exchange.getIn().setHeaders(null);
		exchange.getIn().setBody(retObject.toString());
	}
}
