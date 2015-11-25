package com.ai.sboss.favorite.processor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class QueryCollectedContentsInProcessor implements IBasicInProcessor {
	private Logger logger = Logger.getLogger(QueryCollectedContentsInProcessor.class);
	private static final String HUB_FORMAT_QUERY = "servicecode=queryCollectedContents&WEB_HUB_PARAMS={\"data\":<PARAM_JSON>,\"header\":{\"Content-Type\":\"application/json\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject inputparam = getInputParam(exchange);
		String query = HUB_FORMAT_QUERY.replace("<PARAM_JSON>", inputparam.toString());
		query = query.replaceAll("\\[", "%5B").replaceAll("\\]", "%5D");
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, query);
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		String body = exchange.getIn().getBody(String.class);
		if (body == null || body.equals("")) {
			return null;
		}

		// construct crm parameter
		JSONObject jsonBody = JSONObject.fromObject(body);
		JSONArray conditionType = new JSONArray();
		JSONObject queryConditionValue = new JSONObject();

		Long customerId = 2020L;
		JSONObject cond = new JSONObject();
		cond.put("condType", "customerId");
		JSONArray condvalue = new JSONArray();
		condvalue.add(customerId.toString());
		cond.put("condValue", condvalue);
		conditionType.add(cond);

		if (jsonBody.containsKey("content_tags")) {
			cond = new JSONObject();
			cond.put("condType", "contentTags");
			condvalue = new JSONArray();
			JSONArray taglist = jsonBody.getJSONArray("content_tags");
			for (int i = 0; i < taglist.size(); ++i) {
				condvalue.add(taglist.getJSONObject(i).getString("tag_name"));
			}
			cond.put("condValue", condvalue);
			conditionType.add(cond);
		}

		if (jsonBody.containsKey("content_origin")) {
			cond = new JSONObject();
			cond.put("condType", "contentOrigin");
			condvalue = new JSONArray();
			condvalue.add(jsonBody.getString("content_origin"));
			cond.put("condValue", condvalue);
			conditionType.add(cond);
		}
		
		if (jsonBody.containsKey("content_favorite_flag")) {
			cond = new JSONObject();
			cond.put("condType", "favoriteFlag");
			condvalue = new JSONArray();
			Long favoriteflag = jsonBody.getLong("content_favorite_flag");
			condvalue.add(favoriteflag.toString());
			cond.put("condValue", condvalue);
			conditionType.add(cond);
		}
		
		if (jsonBody.containsKey("content_color_flags")) {
			cond = new JSONObject();
			cond.put("condType", "colorFlags");
			condvalue = jsonBody.getJSONArray("content_color_flags");
			cond.put("condValue", condvalue);
			conditionType.add(cond);
		}
		
		queryConditionValue.put("conditionType", conditionType);
		queryConditionValue.put("pageSize", jsonBody.getLong("page_size"));
		queryConditionValue.put("pageNumber", jsonBody.getLong("page_no"));
		JSONObject queryCondition = new JSONObject();
		queryCondition.put("queryConditionValue", queryConditionValue);
		logger.info("==>"+queryCondition.toString());

		return queryCondition;
	}

}
