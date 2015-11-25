package com.ai.sboss.order.fake;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.StreamCache;
import org.apache.log4j.Logger;

public class QueryOrderListFake {

	Logger logger = Logger.getLogger(QueryOrderListFake.class);
	private final String ORDER_LIST = "data/fakedata/orderlist.properties";
	private final String RET_FMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":\"200\",\"result_msg\":\"success\",\"data_mode\":\"0\",\"digest\":\"\"}}";

	public String queryOrderList(StreamCache isc) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		isc.writeTo(baos);
		logger.info(baos.toString());
		int order_state = -1;
		String reponse = "";
		int order_role = JSONObject.fromObject(baos.toString()).getInt("order_role");
		if (JSONObject.fromObject(baos.toString()).containsKey("order_state")) {
			order_state = JSONObject.fromObject(baos.toString()).getInt("order_state");
			reponse = readResult(ORDER_LIST, order_role, order_state);
		} else {
			reponse = readAllResult(ORDER_LIST, order_role);
		}
		
		return RET_FMT.replace("<DATA>", reponse);
	}

	private String readResult(String fileName, int role, int state) throws IOException {
		LineNumberReader linereader = new LineNumberReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
		String content = "";
		JSONArray ret = new JSONArray();
		while ((content = linereader.readLine()) != null) {
			//content = linereader.readLine();
			if (JSONObject.fromObject(content).getInt("order_state") == state) {
				ret.add(JSONObject.fromObject(content));
			}
		}
		linereader.close();
		return ret.toString();
	}
	
	private String readAllResult(String fileName, int role) throws IOException {
		LineNumberReader linereader = new LineNumberReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
		String content = "";
		JSONArray ret = new JSONArray();
		while ((content= linereader.readLine()) != null) {
			ret.add(JSONObject.fromObject(content));
		}
		linereader.close();
		return ret.toString();
	}

}
