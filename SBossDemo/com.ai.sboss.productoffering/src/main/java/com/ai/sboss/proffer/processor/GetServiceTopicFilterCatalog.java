package com.ai.sboss.proffer.processor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class GetServiceTopicFilterCatalog implements Processor {
	private Logger logger = Logger.getLogger(GetServiceTopicFilterCatalog.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		String retObject = getInputParam(exchange);
		exchange.getIn().setHeaders(null);
		exchange.getIn().setBody(retObject);
	}

	public String getInputParam(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();
		String body = inMessage.getBody(String.class);
		JSONObject bodyJsonObject = JSONObject.fromObject(body);
		
		JSONArray cataloglist = bodyJsonObject.getJSONArray("cataloglist");
		logger.info("CRM获取到的catalogList是："+cataloglist.toString());
		Long pagenum = bodyJsonObject.getLong("page_num");
		Long pagesize = bodyJsonObject.getLong("page_size");
		Long queryCatalog = -1L;
		if (bodyJsonObject.containsKey("topic_id")) {
			queryCatalog = bodyJsonObject.getLong("topic_id");
			logger.info("需要筛选出来的ID是:"+queryCatalog);
		}
		
		cataloglist = filterCatalog(cataloglist, queryCatalog);
		StringBuilder retStringBuilder = new StringBuilder();
		for (int i = 0; i < cataloglist.size(); ++i) {
			cataloglist.getJSONObject(i).put("page_num", pagenum);
			cataloglist.getJSONObject(i).put("page_size", pagesize);
			retStringBuilder.append(cataloglist.getJSONObject(i).toString());
			if (i != cataloglist.size()) {
				retStringBuilder.append(";");
			}
		}
		logger.info("got catalog list==>"+cataloglist.toString());
		return retStringBuilder.toString();
	}

	private JSONArray filterCatalog(JSONArray cataloglist, Long queryCatalog) {
		if (queryCatalog == -1L) {
			return cataloglist;
		}
		JSONArray retArray = new JSONArray();
		for (int i = 0; i < cataloglist.size(); ++i) {
			JSONObject currentcatalog = cataloglist.getJSONObject(i);
			if (currentcatalog.getLong("catalog_id") == queryCatalog) {
				retArray.add(currentcatalog);
				return retArray;
			}
		}
		return retArray;
	}

}
