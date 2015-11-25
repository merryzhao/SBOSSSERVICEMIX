/**
 * 
 */
package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

/**
 * @author monica
 * 查询更新的proposal列表
 */
public class FindUpdateDataForProposalOutProcessor implements IBasicOutProcessor{

	private static final Logger LOGGER = Logger.getLogger(FindUpdateDataForProposalOutProcessor.class);

	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":<MSG>,\"data_mode\":\"1\",\"digest\":\"\"}}";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		Message inMessage = exchange.getIn();
		final String retResult = convert2requst(inMessage.getBody(String.class));
		LOGGER.info(retResult);
		inMessage.setBody(retResult);
	}

	@Override
	public String convert2requst(String exchange) throws Exception {
		// TODO Auto-generated method stub
		JSONObject input = JSONObject.fromObject(exchange);
		LOGGER.info("FindUpdateDataForProposalOutProcessor input --->" + input.toString());

		Object dataValue = input.get("data");
		if (null != dataValue) {
			if (dataValue.toString().isEmpty()) {
				return RETFMT.replace("<DATA>", "[]").replace("<CODE>", input.getString("hub_code")).replace("<MSG>", StringUtils.EMPTY);
			} else {
				return RETFMT.replace("<DATA>", getConvertedResult(input)).replace("<CODE>", input.getString("hub_code")).replace("<MSG>", "success");
			}
		} else {
			return RETFMT.replace("<DATA>", "[]").replace("<CODE>", input.getString("0")).replace("<MSG>", StringUtils.EMPTY);
			}
		}
	
	private String getConvertedResult(JSONObject input){
		
		//定义返回的jsonobject
		JSONObject resultObj = new JSONObject();
		
		JSONArray inputArray = input.getJSONArray("data");
		for(int i = 0; i < inputArray.size(); i++){
			JSONObject singleEle = inputArray.getJSONObject(i);
			//根据recommenderRule中的recommenderRuleType判断是更换proposal，还是更换offering
			JSONObject recommanderRule = singleEle.getJSONObject("recommanderRule");
			//现在只是为了满足气泡发光条件，因此不需要挂接服务
			if(recommanderRule != null){
				int ruleType = recommanderRule.getInt("recommanderRuleType");
				Long originId = singleEle.getLong("origionId");
				if(ruleType == 1){   //替换proposal
					resultObj.put("proposal_id", originId);
					//如果是替换proposal，则其返回的json中的service_id为空
					resultObj.put("service_id", 0);
				}
				if(ruleType == 3){   //替换offering
					//如果是替换offering，则返回的json中的proposal_id为原始的proposal_id
					resultObj.put("proposal_id", 0);
					resultObj.put("service_id", originId);
				}
			}
		}

		LOGGER.info("FindUpdateDataForProposalOutProcessor resultObj --->" + resultObj.toString());		
		return resultObj.toString();
	}
}
