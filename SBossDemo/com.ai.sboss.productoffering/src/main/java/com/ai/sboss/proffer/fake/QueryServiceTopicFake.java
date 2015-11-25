package com.ai.sboss.proffer.fake;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import com.ai.sboss.common.processor.SbssBasicProcessor;

public class QueryServiceTopicFake implements Processor  {
	final private String CRMFMT = "{\"data\":<DATA>,\"hub_code\":\"1\",\"hub_value\":[],\"code\":\"\",\"value\":\"\"}";
	Logger logger = Logger.getLogger(QueryServiceTopicFake.class);
	private final String PRODUCT_DETAIL = "data/fakedata/servicetopic.properties";

	private int getFileLineNumber(String fileName) throws IOException {
		LineNumberReader lineLineNumberReader = new LineNumberReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
		while (lineLineNumberReader.readLine() != null) {
		}
		int lineNumber = lineLineNumberReader.getLineNumber();
		lineLineNumberReader.close();
		return lineNumber;
	}

	private String readSelectedLine(String fileName, int lineNumber) throws IOException {

		if (lineNumber < 0 || lineNumber > getFileLineNumber(fileName)) {
			logger.error("Query id out of index");
			return "";
		}
		
		if (lineNumber == 0) {
			lineNumber = 1;
		}

		LineNumberReader linereader = new LineNumberReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
		String content = "";
		while (content != null) {
			content = linereader.readLine();
			if (lineNumber == linereader.getLineNumber()) {
				break;
			}
		}
		linereader.close();
		return content;
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject param = JSONObject.fromObject(exchange.getIn().getBody(String.class));
		//Long pageNum = param.getLong("page_num");
		//Long pageSize = param.getLong("page_size");
		Long topicId = 0L;
		if (param.containsKey("topic_id")) {
			topicId = param.getLong("topic_id");
		}
		
		String rawret = readSelectedLine(PRODUCT_DETAIL, topicId.intValue());
		String crmret = CRMFMT.replace("<DATA>", rawret);
		exchange.getIn().setBody(crmret);
	}
}
