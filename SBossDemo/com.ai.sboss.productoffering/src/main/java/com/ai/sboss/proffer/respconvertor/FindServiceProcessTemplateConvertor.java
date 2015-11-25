package com.ai.sboss.proffer.respconvertor;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class FindServiceProcessTemplateConvertor implements IBasicOutProcessor {
	final static Log LOGGER = LogFactory.getLog(FindServiceProcessTemplateConvertor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		String retString = convert2requst(exchange.getIn().getBody(String.class));
		exchange.getIn().setHeaders(null);
		exchange.getIn().setBody(retString);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject outputJSON = JSONObject.fromObject(data);
		JSONObject desc = outputJSON.getJSONObject("desc");
		if (!StringUtils.equals("_200", desc.getString("result_code"))) {
			return outputJSON.toString();
		}
		JSONObject arrangementTemplateJSON = outputJSON.getJSONObject("data");
		LOGGER.info("系统返回：" + arrangementTemplateJSON.toString());
		JSONObject outputTemplate = new JSONObject();
		outputTemplate.put("process_id", arrangementTemplateJSON.getString("uid"));
		outputTemplate.put("process_name", arrangementTemplateJSON.getString("displayName"));
		outputTemplate.put("process_steps_number", arrangementTemplateJSON.getJSONArray("jointmapping").size());
		JSONArray processSteps = new JSONArray();
		for (int index = 0; index < arrangementTemplateJSON.getJSONArray("jointmapping").size(); ++index) {
			JSONObject tempobj = new JSONObject();
			JSONObject currentJointMapping = arrangementTemplateJSON.getJSONArray("jointmapping").getJSONObject(index);
			JSONObject currentJoint = currentJointMapping.getJSONObject("joint");
			tempobj.put("name", currentJoint.getString("displayName"));
			tempobj.put("id", currentJoint.getString("uid"));
			tempobj.put("weight", currentJointMapping.has("weight") ? currentJointMapping.getLong("weight") : 0L);
			tempobj.put("operation", currentJoint.getString("camelUri"));
			tempobj.put("icon", null);
			processSteps.add(tempobj);
		}
		outputTemplate.put("process_steps", processSteps);

		outputJSON.put("data", outputTemplate);
		return outputJSON.toString();
	}

}
