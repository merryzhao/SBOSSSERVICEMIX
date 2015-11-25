package com.ai.sboss.proffer.fake;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import net.sf.json.JSONObject;

import org.apache.camel.StreamCache;
import org.apache.log4j.Logger;

public class QueryServiceCommentDataFake {
	Logger logger = Logger.getLogger(QueryServiceCommentDataFake.class);
	private final String PRODUCT_DETAIL = "data/fakedata/service.properties";

	public String getResponse(StreamCache isc) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		isc.writeTo(baos);
		logger.info("request parameters: " + baos.toString());
		int serviceId = JSONObject.fromObject(baos.toString()).getInt("page_num");
		String reponse = readSelectedLine(PRODUCT_DETAIL, serviceId);
		return reponse;
	}

	private int getFileLineNumber(String fileName) throws IOException {
		LineNumberReader lineLineNumberReader = new LineNumberReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
		while (lineLineNumberReader.readLine() != null) {
		}
		int lineNumber = lineLineNumberReader.getLineNumber();
		lineLineNumberReader.close();
		return lineNumber;
	}

	private String readSelectedLine(String fileName, int lineNumber) throws IOException {

		if (lineNumber <= 0 || lineNumber > getFileLineNumber(fileName)) {
			logger.error("Query id out of index");
			return "";
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
}
