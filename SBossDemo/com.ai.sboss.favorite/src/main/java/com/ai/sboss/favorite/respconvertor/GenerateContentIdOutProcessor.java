package com.ai.sboss.favorite.respconvertor;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class GenerateContentIdOutProcessor implements IBasicOutProcessor {
	Logger logger = Logger.getLogger(GenerateContentIdOutProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		String ret = convert2requst(exchange.getIn().getBody(String.class));
		exchange.getIn().setBody(ret);
	}

	@Override
	public String convert2requst(String data) throws UnsupportedEncodingException {
		JSONObject crmret = JSONObject.fromObject(data);
		if (crmret.getString("hub_code").equals("1")) {
			//logger.info("GenerateContentIdOutProcessor=>"+crmret.getJSONObject("data"));
			JSONObject targetcontent = crmret.getJSONObject("data").getJSONArray("content").getJSONObject(0);
			JSONObject ret = new JSONObject();
			String favoriteId = crmret.getJSONObject("data").getString("favoriteEntryId");
			ret.put("url", URLDecoder.decode(targetcontent.getString("url"), "UTF-8"));
			ret.put("contentId", targetcontent.getLong("contentId"));
			ret.put("favorite_id", new Long(favoriteId));
			
			return ret.toString();
		}
		return null;
	}

}
