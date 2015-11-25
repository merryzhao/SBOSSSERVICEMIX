package com.ai.sboss.order.fake;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import net.sf.json.JSONObject;

import org.apache.camel.StreamCache;
import org.apache.log4j.Logger;

public class QueryOrderDetailFake {

	Logger logger = Logger.getLogger(QueryOrderDetailFake.class);
	private final String ORDER_DETAIL = "data/fakedata/order.properties";

	public String queryOrder(StreamCache isc) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		isc.writeTo(baos);
		logger.info(baos.toString());
		int orderId = JSONObject.fromObject(baos.toString()).getInt("order_id");
		String reponse = readSelectedLine(ORDER_DETAIL, orderId);
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
