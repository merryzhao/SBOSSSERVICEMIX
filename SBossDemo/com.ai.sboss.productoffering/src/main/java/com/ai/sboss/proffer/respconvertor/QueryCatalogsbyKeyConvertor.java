package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryCatalogsbyKeyConvertor implements IBasicOutProcessor {
	private Logger logger = Logger.getLogger(QueryCatalogsbyKeyConvertor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		String retString = convert2requst(exchange.getIn().getBody(String.class));
		exchange.getIn().setHeaders(null);
		exchange.getIn().setBody(retString);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		if (StringUtils.equals(JSONObject.fromObject(data).getString("hub_code"), "0")) {
			return null;
		}
//		JSONArray catalog_list = new JSONArray();
		JSONObject dataval = JSONObject.fromObject(data).getJSONObject("data");
		JSONArray catalog_list = dataval.getJSONArray("catalogs");
		logger.info("crm queried==>"+catalog_list.toString());
		JSONArray retArray = new JSONArray();
		for (int i = 0; i < catalog_list.size(); ++i) {
			JSONObject catalog = catalog_list.getJSONObject(i);
			JSONObject tempObject = new JSONObject();
			tempObject.put("catalog_id", catalog.getLong("catalogId"));
			tempObject.put("catalog_name", catalog.getString("name"));
			tempObject.put("level", 0);
			tempObject.put("has_children", Boolean.FALSE);
			tempObject.put("parent_id", catalog.getString("parentCatalog"));
			retArray.add(tempObject);
		}
		JSONObject retObject = new JSONObject();
		retObject.put("catalog_list", retArray.toString());
		
		return retObject.toString();
	}

}
