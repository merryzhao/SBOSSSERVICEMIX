package com.ai.sboss.offeringshelves.fake;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class QueryIndustryCatalogFakeProcessor implements Processor {
	private final static String FAKEFILE = "data/fakedata/industryCatalog.properties";
//	private final static String FAKEFILE = "E:/files/industryCatalog.properties";	
	private final static String RET_FMT = "{\"data\":<CATALOG_DATA>,\"desc\":{\"result_code\":200,\"result_msg\":\"success\",\"data_mode\":0,\"digest\":\"\"}}";
	Logger logger = Logger.getLogger(QueryIndustryCatalogFakeProcessor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		exchange.getIn().setHeaders(null);
		//JSONArray tagslist = new JSONArray();
		String catalogData= null;
		try {
			LineNumberReader linereader = new LineNumberReader(new FileReader(
					FAKEFILE));
			catalogData = linereader.readLine();
//			if (filecontent != null) {
//				JSONArray contentlist = JSONArray.fromObject(filecontent);
//				for (int j = 0; j < contentlist.size(); ++j) {
//					JSONObject content = contentlist.getJSONObject(j);
//					tagslist.addAll(content.getJSONArray("service_tags"));
//				}
//			}
			linereader.close();
		} catch (IOException e) {
			logger.error(FAKEFILE+e.toString());
		}
		
		exchange.getIn().setBody(RET_FMT.replace("<CATALOG_DATA>", catalogData));
	}

}
