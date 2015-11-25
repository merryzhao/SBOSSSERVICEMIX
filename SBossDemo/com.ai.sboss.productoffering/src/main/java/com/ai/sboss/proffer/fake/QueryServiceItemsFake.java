package com.ai.sboss.proffer.fake;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class QueryServiceItemsFake implements Processor {
	Logger logger = Logger.getLogger(QueryServiceDataFake.class);
	
	public final String SERVICEITEM_FMT = "{\"data\":{\"page_num\":<PAGE>,\"catalog_list\":<CATALIST>,\"service_list\":<SEVLIST>},\"desc\":{\"result_code\":1,\"result_msg\":\"\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	private final String SERVICE_LIST = "data/fakedata/service.properties";
	private final String PRODUCT_CATALOG = "data/fakedata/servicecatalog.properties";

	private String getRelateCatalog(String fileName, String keywords) throws IOException {
		LineNumberReader linereader = new LineNumberReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
		JSONArray ret = new JSONArray();
		String content = linereader.readLine();
		if (content == null) {
			linereader.close();
			return ret.toString();
		}
		
		JSONArray catalog_list = JSONObject.fromObject(content).getJSONArray("catalog_list");
		String name = null;
		JSONObject catalogObject = new JSONObject();
		for (int i = 0; i < catalog_list.size(); ++i) {
			name = catalog_list.getJSONObject(i).getString("catalog_name");
			if (name.contains(keywords)) {
				
				catalogObject.put("catalog_id", catalog_list.getJSONObject(i).getString("catalog_id"));
				catalogObject.put("catalog_name", name);
				ret.add(catalogObject);
			}
		}
		linereader.close();
		return ret.toString();
	}

	private String getRelateServices(String fileName, String keywords) throws IOException {
		LineNumberReader linereader = new LineNumberReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
		JSONArray ret = new JSONArray();
		String content = linereader.readLine();
		if (content == null) {
			linereader.close();
			return ret.toString();
		}
		JSONArray service_list = JSONObject.fromObject(content).getJSONObject("data").getJSONArray("service_list");
		String name = null;
		for (int i = 0; i < service_list.size(); ++i) {
			name = service_list.getJSONObject(i).getString("service_name");
			if (name.contains(keywords)) {
				ret.add(service_list.getJSONObject(i));
			}
		}
		linereader.close();
		return ret.toString();
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		String keywords = JSONObject.fromObject(in.getBody(String.class)).getString("keyword");
		String reponseservice = getRelateServices(SERVICE_LIST, keywords);
		String reponsecatalog = getRelateCatalog(PRODUCT_CATALOG, keywords);
		
		in.setBody(SERVICEITEM_FMT.replace("<PAGE>","0").replace("<CATALIST>",reponsecatalog).replace("<SEVLIST>", reponseservice));
		
	}

}
