package com.ai.sboss.favorite.processor;

import java.rmi.Naming;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

import fileProcessorInterface.fileProcessorInterface.IArticleProcessor;

public class FileProcessInProcessor implements IBasicInProcessor {
	//private Logger logger = Logger.getLogger(FileProcessInProcessor.class);
	private final String FILEPROCESSRMIADDR = "rmi://localhost:8001/fileprocess";
	private final String FILEPORCESSCACHEPATH = "data/fileprocesscache";

	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject args = getInputParam(exchange);
		if (!args.isEmpty()) {
			IArticleProcessor fileprocess = (IArticleProcessor)Naming.lookup(FILEPROCESSRMIADDR);
			Long contentId = args.getLong("contentId");
			Long favoriteId = args.getLong("favorite_id");
			String ret = fileprocess.getMessageFromWebSite(args.getString("url"), contentId.toString(), FILEPORCESSCACHEPATH, favoriteId);
			//logger.info("ret is==>"+ret);
			exchange.getIn().setBody(ret);
		} else {
			exchange.getIn().setBody(null);
		}
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		JSONObject ret = null;
		String body = exchange.getIn().getBody(String.class);
		if (body != null) {
			ret = JSONObject.fromObject(body);
		} else {
			ret = new JSONObject();
		}
		return ret;
	}

}
