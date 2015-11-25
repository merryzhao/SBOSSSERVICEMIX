package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryServiceDetailConvertor implements IBasicOutProcessor {
	Logger logger = Logger.getLogger(QueryServiceDetailConvertor.class);
	private final static String responsefmt = "{\"data\":<DATA>,\"desc\":{\"result_code\":<RET_CODE>,\"result_msg\":\"<RET_MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		String retdata = convert2requst(in.getBody(String.class));
		if (retdata != null) {
			retdata = responsefmt.replace("<DATA>", retdata).replace("<RET_CODE>", Integer.toString(1))
					.replace("<RET_MSG>", "sucess");
		} else {
			retdata = responsefmt.replace("<DATA>", "{}").replace("<RET_CODE>", Integer.toString(0))
					.replace("<RET_MSG>", "failed");
		}
		in.setBody(retdata);

	}

	@Override
	public String convert2requst(String data) throws Exception {
		if (data == null) {
			return null;
		}
		logger.info("queryServiceDetailConvetor_data=====>" + data);
		JSONObject aggregatedJsonObject = JSONObject.fromObject(data);
//		aggregatedJsonObject.remove("provider_id");
		if (aggregatedJsonObject.containsKey("provider_id")) {
//			JSONObject provider = aggregatedJsonObject.getJSONObject("provider");
//			aggregatedJsonObject.put("provider_id", provider.getLong("id"));
//			aggregatedJsonObject.put("provider_portrait_url", provider.getString("imageurl"));
//			aggregatedJsonObject.put("provider_name", provider.getString("name"));
//			aggregatedJsonObject.remove("provider");
			Long service_provider_id = aggregatedJsonObject.getLong("provider_id");
			if(service_provider_id == 0){
				aggregatedJsonObject.put("service_provider", -1);
			}
			else{
				aggregatedJsonObject.put("service_provider", service_provider_id);
			}
			aggregatedJsonObject.remove("provider_id");
		}
		logger.info("queryServiceDetailConvertor_aggregatedJsonObject======>" + aggregatedJsonObject);
		return aggregatedJsonObject.toString();
	}
}
