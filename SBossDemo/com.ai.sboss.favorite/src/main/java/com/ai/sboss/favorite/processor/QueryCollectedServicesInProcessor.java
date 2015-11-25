package com.ai.sboss.favorite.processor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class QueryCollectedServicesInProcessor implements IBasicInProcessor {
	protected Logger logger = Logger.getLogger(this.getClass());
	private static final String HUB_FORMAT_QUERY = "servicecode=queryCollectedServices&WEB_HUB_PARAMS={\"data\":<PARAM_JSON>,\"header\":{\"Content-Type\":\"application/json\"}}";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject properInput = getInputParam(exchange);
		String query = HUB_FORMAT_QUERY.replace("<PARAM_JSON>", properInput.toString());
		query = query.replaceAll("\\[", "%5B").replaceAll("\\]", "%5D");
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, query);
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) {
		JSONObject ret = new JSONObject();
		JSONObject inputparam = JSONObject.fromObject(exchange.getIn().getBody(String.class));
		/* Fake CustomerId = 1 */
		JSONArray conditionTypeList = new JSONArray();
		
		/* {"condType":"customerId","condValue":["1"]} */
		Long customerId = new Long(1);
		JSONObject condition = new JSONObject();
		condition.put("condType", "customerId");
		JSONArray condValue = new JSONArray();
		condValue.add(customerId.toString());
		condition.put("condValue", condValue.toString());
		conditionTypeList.add(condition);
		
		/* {"condType":"serviceTags","condValue":["mp3"]} */
		condition = new JSONObject();
		condValue = new JSONArray();
		condition.put("condType", "serviceTags");
		JSONArray inputtaglist = inputparam.getJSONArray("service_tags");
		for (int i = 0; i < inputtaglist.size(); ++i) {
			condValue.add(inputtaglist.getString(i));
		}
		condition.put("condValue", condValue.toString());
		conditionTypeList.add(condition);
		JSONObject conditionType = new JSONObject();
		conditionType.put("conditionType", conditionTypeList);
		
		conditionType.put("pageSize", inputparam.getLong("page_size"));
		conditionType.put("pageNumber", inputparam.getLong("page_no"));
		ret.put("queryConditionValue", conditionType);
		
		return ret;
	}

}
