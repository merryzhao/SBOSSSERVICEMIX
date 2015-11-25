package com.ai.sboss.order.respconvertor;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component("findServiceProcessInstanceConvertor")
public class FindServiceProcessInstanceConvertor implements IBasicOutProcessor {
	final static Log LOGGER = LogFactory.getLog(FindServiceProcessInstanceConvertor.class);
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
		JSONObject arrangementInstanceJSON = outputJSON.getJSONObject("data");
		LOGGER.info("系统返回：" + arrangementInstanceJSON.toString());
		JSONObject outputInstance = new JSONObject();
		outputInstance.put("instance_id", arrangementInstanceJSON.getString("uid"));
		outputInstance.put("display_name", arrangementInstanceJSON.getString("displayName"));
		outputInstance.put("business_id", arrangementInstanceJSON.getString("businessID"));
		outputInstance.put("instance_statu", arrangementInstanceJSON.getString("statu"));
		outputInstance.put("instance_creator", arrangementInstanceJSON.getString("creator"));
		outputInstance.put("creator_scope", arrangementInstanceJSON.getString("creatorScope"));
		outputInstance.put("create_time", arrangementInstanceJSON.getLong("createTime"));
		outputInstance.put("end_time", arrangementInstanceJSON.getLong("endTime"));
		outputInstance.put("process_template", arrangementInstanceJSON.getString("arrangementuid"));
		//TODO:暂时不考虑子流程
		outputInstance.put("parent_instance", null);

		JSONArray instance_steps = new JSONArray();
		for (int index = 0; index < arrangementInstanceJSON.getJSONArray("jointflows").size(); ++index) {
			JSONObject tempobj = new JSONObject();
			JSONObject currentJointFlow = arrangementInstanceJSON.getJSONArray("jointflows")
										.getJSONObject(index);
			JSONObject currentJoint = currentJointFlow.getJSONObject("jointInstance");
			
			tempobj.put("task_id", currentJoint.getString("uid"));
			tempobj.put("task_operation", currentJoint.getString("camelUri"));
			tempobj.put("task_name", currentJoint.getString("offsetTitle"));
			tempobj.put("task_node_name", currentJoint.getString("offsetTitle"));
			tempobj.put("task_ executor", currentJointFlow.getString("executor"));
			tempobj.put("task_statu", currentJoint.getString("statu"));
			tempobj.put("abs_offsettime", currentJoint.getLong("absOffsettime"));
			tempobj.put("relate_offsettime", currentJoint.getLong("relateOffsettime"));
			tempobj.put("prompt_offsettime", currentJoint.getString("promptOffsettime"));
			tempobj.put("visible", currentJoint.getString("offsetVisible"));
			tempobj.put("expect_exetime", currentJoint.getLong("expectedExeTime"));
			tempobj.put("exetime", currentJoint.getLong("exeTime"));
			tempobj.put("weight", currentJoint.has("weight")?currentJoint.getLong("weight"):0L);
			tempobj.put("expand_id", currentJoint.has("expandTypeId")?currentJoint.getLong("expandTypeId"):0L);
			tempobj.put("param_list", currentJoint.has("properties")?currentJoint.getString("properties"):new JSONArray().toString());
		
			instance_steps.add(tempobj);
		}
		outputInstance.put("instance_steps", instance_steps);
		//TODO:暂时不考虑子流程
		outputInstance.put("child_instances", new JSONArray());
		
		outputJSON.put("data", outputInstance);
		return outputJSON.toString();
	}

}
