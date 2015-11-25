package com.ai.sboss.proffer.fake;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.apache.log4j.Logger;

public class QueryAllServiceCatalogDataFake {
	private final static String CRMFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":1,\"result_msg\":\"\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	Logger logger = Logger.getLogger(QueryAllServiceCatalogDataFake.class);
	private final String PRODUCT_DETAIL = "data/fakedata/servicecatalog.properties";

	public String getResponse() throws Exception {
		String reponse = readSelectedLine(PRODUCT_DETAIL, 1);
		String crmret = CRMFMT.replace("<DATA>", reponse);
		return crmret;
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
