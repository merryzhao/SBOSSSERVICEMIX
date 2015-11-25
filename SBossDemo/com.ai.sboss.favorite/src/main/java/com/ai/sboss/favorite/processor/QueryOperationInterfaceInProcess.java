package com.ai.sboss.favorite.processor;

import java.rmi.Naming;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.hadoop.operationinterfaces.HadoopOperations;
import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class QueryOperationInterfaceInProcess implements IBasicInProcessor {
	protected Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject properInput = getInputParam(exchange);
		Long contentId = properInput.getLong("contentId");
		String servicelist = queryRelateServices(contentId);

		exchange.getIn().setBody(servicelist);
	}

	private String queryRelateServices(Long contentId) {
		try {
			HadoopOperations rhello = (HadoopOperations) Naming.lookup("rmi://localhost:8092/hadoopservice");
			List<String> resultlist = rhello.readHDFSListAll("hdfs://10.1.228.151:9000", "/user/aihadoop/analysis/content/article/2015-06-10");
			for (String item:resultlist) {
				String[] map = item.split("\t");
				if (map[0].trim().equals(contentId.toString())) {
					return map[1];
				}
			}
			return "";
		} catch (Exception e) {
			logger.error("HadoopOperations RMI call failed" + e.toString());
			return "";
		}
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		JSONObject ret = JSONObject.fromObject(exchange.getIn().getBody(String.class));
		return ret;
	}

}
