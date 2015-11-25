package com.ai.sboss.arrangement.translation;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * ArrangementInstanceTranslationService翻译服务接口的唯一实现
 * @author yinwenjie
 */
@Scope("prototype")
@Component("arrangementInstanceTranslationServiceImpl")
public class ArrangementInstanceTranslationServiceImpl implements ArrangementInstanceTranslationService {
	
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(ArrangementInstanceTranslationServiceImpl.class);
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.translation.ArrangementInstanceTranslationService#translationEntity(java.lang.String)
	 */
	@Override
	public ArrangementInstanceEntity translationEntity(String xmlText)  throws BizException {
		//TODO 暂未实现
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.translation.ArrangementInstanceTranslationService#translationEntity(net.sf.json.JSONObject)
	 */
	@Override
	public ArrangementInstanceEntity translationEntity(JSONObject jsonObject)  throws BizException {
		/*
		 * 注意，翻译功能就是翻译功能，不会涉及到任何的数据对比，可行性验证。
		 * 1、首先进行arrangementInstance节点的解析，其中必须填写的信息包括：
		 * arrangementid、businessid、creator、creatorScope，其他的信息包括：
		 * id、displayName
		 * 
		 * 2、然后解析jointinstances节点。注意整个jointinstances节点集合都可能没有。
		 * 但是目前情况来看不会出现“全部以自动节点”构成的流程实例，所以这个节点的信息现在来看是有的
		 * 
		 * 如果存在jointinstances节点下的元素，那么必填信息包括：
		 * jointid
		 * 可填信息包括：
		 * absOffsettime、relateOffsettime、promptOffsettime、offsetTitle、offsetVisible、expandTypeId、executor
		 * */
		ArrangementInstanceEntity arrangementInstance = new ArrangementInstanceEntity();
		
		//1、=======================
		JSONObject arrangementInstanceJSONObject = jsonObject.has("arrangementInstance")?jsonObject.getJSONObject("arrangementInstance"):null;
		if(arrangementInstanceJSONObject == null) {
			throw new BizException("json-arrangementInstance，必须传入。请检查！", ResponseCode._403);
		}
		
		//arrangementid
		String arrangementid = null;
		arrangementid = arrangementInstanceJSONObject.has("arrangementid")?arrangementInstanceJSONObject.getString("arrangementid"):null;
		if(StringUtils.isEmpty(arrangementid)) {
			throw new BizException("json：arrangementInstance-arrangementid，必须传入。请检查！", ResponseCode._403);
		}
		//businessid
		String businessid = null;
		businessid = arrangementInstanceJSONObject.has("businessid")?arrangementInstanceJSONObject.getString("businessid"):null;
		if(StringUtils.isEmpty(businessid)) {
			throw new BizException("json：arrangementInstance-businessid，必须传入。请检查！", ResponseCode._403);
		}
		//creator
		String creator = null;
		creator = arrangementInstanceJSONObject.has("creator")?arrangementInstanceJSONObject.getString("creator"):null;
		if(StringUtils.isEmpty(businessid)) {
			throw new BizException("json：arrangementInstance-creator，必须传入。请检查！", ResponseCode._403);
		}
		//creatorScope
		String creatorScope = null;
		creatorScope = arrangementInstanceJSONObject.has("creatorScope")?arrangementInstanceJSONObject.getString("creatorScope"):null;
		if(StringUtils.isEmpty(businessid)) {
			throw new BizException("json：arrangementInstance-creatorScope，必须传入。请检查！", ResponseCode._403);
		}
		//creatorScope只能有三种值：industry、producer、consumer
		if(!StringUtils.equals(creatorScope, "industry") && !StringUtils.equals(creatorScope, "producer")
			&& !StringUtils.equals(creatorScope, "consumer")) {
			throw new BizException("creatorScope只能有三种值：industry、producer、consumer。请检查！", ResponseCode._403);
		}
		//id
		String id = null;
		id = arrangementInstanceJSONObject.has("id")?arrangementInstanceJSONObject.getString("id"):null;
		//为什么还要进行一次判断，那是因为可能的写法："id":""
		if(StringUtils.isEmpty(id)) {
			id = null;
		}
		//displayName
		String displayName = null;
		displayName = arrangementInstanceJSONObject.has("displayName")?arrangementInstanceJSONObject.getString("displayName"):null;
		if(StringUtils.isEmpty(displayName)) {
			displayName = null;
		}
		
		ArrangementEntity arrangement = new ArrangementEntity();
		arrangement.setUid(arrangementid);
		arrangementInstance.setArrangement(arrangement);
		arrangementInstance.setCreateTime(System.currentTimeMillis());
		arrangementInstance.setCreator(creator);
		arrangementInstance.setBusinessID(businessid);
		arrangementInstance.setCreatorScope(creatorScope);
		arrangementInstance.setEndTime(null);
		arrangementInstance.setDisplayName(displayName);
		//TODO 在编排系统的初期版本中，暂时不支持“子流程”的同时启动
		arrangementInstance.setChildArrangementInstances(null);
		arrangementInstance.setParentInstance(null);
		//还没有正式完成流程实例的启动工作前，这个流程的状态都是没有的
		arrangementInstance.setStatu(null);
		arrangementInstance.setUid(id);
		
		//2、==============================
		JSONArray jointinstancesArray = arrangementInstanceJSONObject.has("jointinstances")?arrangementInstanceJSONObject.getJSONArray("jointinstances"):null;
		//没有，当然就算咯。但一般都说明了流程实例的定义错误了。
		if(jointinstancesArray == null) {
			arrangementInstance.setChildArrangementInstances(null);
			return arrangementInstance;
		}
		
		Set<JointInstanceEntity> jointInstances = new LinkedHashSet<JointInstanceEntity>();
		for(int index = 0 ; index < jointinstancesArray.size() ; index++) {
			JointInstanceEntity jointInstance = new JointInstanceEntity();
			JSONObject  jointinstanceItem = jointinstancesArray.getJSONObject(index);
			
			//jointid
			String jointid = null;
			jointid = jointinstanceItem.has("jointid")?jointinstanceItem.getString("jointid"):null;
			if(StringUtils.isEmpty(jointid)) {
				throw new BizException("jointinstances-jointid，必须传入。请检查！", ResponseCode._403);
			}
			//absOffsettime
			String absOffsettime;
			absOffsettime =  jointinstanceItem.has("absOffsettime")?jointinstanceItem.getString("absOffsettime"):null;
			//只能为整数（负数也有可能哦）
			Pattern pattern = Pattern.compile("^\\-?[0-9]*$");
			if(absOffsettime != null && !pattern.matcher(absOffsettime).find()) {
				//这里不应该报出异常，仅需要从模版中重新读取相应的默认数据
				absOffsettime = null;
			}
			
			//properties
			String properties;
			properties =  jointinstanceItem.has("param_list")?jointinstanceItem.getString("param_list"):new JSONArray().toString();
			
			String relateOffsettime;
			relateOffsettime =  jointinstanceItem.has("relateOffsettime")?jointinstanceItem.getString("relateOffsettime"):null;
			//只能为整数（负数也有可能哦）
			if(relateOffsettime != null && !pattern.matcher(relateOffsettime).find()) {
				relateOffsettime = null;
			}
			
			//promptOffsettime
			String promptOffsettime;
			promptOffsettime =  jointinstanceItem.has("promptOffsettime")?jointinstanceItem.getString("promptOffsettime"):null;
			//offsetTitle
			String offsetTitle;
			offsetTitle =  jointinstanceItem.has("offsetTitle")?jointinstanceItem.getString("offsetTitle"):null;
			
			//offsetVisible
			String offsetVisible;
			offsetVisible =  jointinstanceItem.has("offsetVisible")?jointinstanceItem.getString("offsetVisible"):null;
			//只可能有三种值：producer | consumer | both
			if(offsetVisible != null && !StringUtils.equals(offsetVisible, "producer")
				&& !StringUtils.equals(offsetVisible, "consumer") && !StringUtils.equals(offsetVisible, "both")) {
				offsetVisible = null;
			}
			
			//expandTypeId
			String expandTypeId = null;
			expandTypeId =  jointinstanceItem.has("expandTypeId")?jointinstanceItem.getString("expandTypeId"):null;
			//只能为自然数
			pattern = Pattern.compile("^[0-9]*$");
			if(expandTypeId != null && !pattern.matcher(expandTypeId).find()) {
				expandTypeId = null;
			}
			
			//weight
			String weight = jointinstanceItem.has("weight")?jointinstanceItem.getString("weight"):null;
			//只能为自然数
			pattern = Pattern.compile("^[0-9]*$");
			if(weight != null && !pattern.matcher(weight).find()) {
				weight = null;
			}
			
			//executor
			String executor = null;
			executor =  jointinstanceItem.has("executor")?jointinstanceItem.getString("executor"):null;
			
			//开始建立对象
			jointInstance.setArrangementInstance(arrangementInstance);
			jointInstance.setCamelUri(null);
			jointInstance.setCreator(arrangementInstance.getCreator());
			jointInstance.setExecutor(executor);
			jointInstance.setInputParamInstances(null);
			
			JointEntity joint = new JointEntity();
			joint.setUid(jointid);
			jointInstance.setJoint(joint);
			jointInstance.setOutputParamInstance(null);
			jointInstance.setProperties(properties);
			
			//还没有正式完成流程实例的启动工作前，这个流程的状态都是没有的
			jointInstance.setStatu(null);
			jointInstance.setUid(null);
			
			//以下是和时间线计算有关的属性，除了用户定义的输入参数外，都为null
			jointInstance.setAbsOffsettime(absOffsettime != null?Long.parseLong(absOffsettime):null);
			jointInstance.setRelateOffsettime(relateOffsettime != null?Long.parseLong(relateOffsettime):null);
			jointInstance.setPromptOffsettime(promptOffsettime);
			jointInstance.setOffsetVisible(offsetVisible);
			jointInstance.setOffsetTitle(offsetTitle);
			jointInstance.setExeTime(null);
			jointInstance.setExpandTypeId(expandTypeId != null?Integer.parseInt(expandTypeId):null);
			jointInstance.setWeight(weight != null?Long.parseLong(weight):0L);
			
			
			jointInstances.add(jointInstance);
		}
		arrangementInstance.setJointInstances(jointInstances);
		
		//返回咯
		return arrangementInstance;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map.Entry<String, String>> translationFlows(String xmlText) throws BizException {
		if (StringUtils.isEmpty(xmlText)) {
			return null;
		}
		Document xmlDoc = null;
		try {
			//需要主动为流程加上根元素
			xmlDoc = DocumentHelper.parseText("<flows>" + xmlText + "</flows>");
		} catch (Exception e) {
			ArrangementInstanceTranslationServiceImpl.LOGGER.error(e.getMessage(), e);
			throw new BizException("流程解析失败"+e.getMessage(), ResponseCode._501);
		}
		
		Element rootElement = xmlDoc.getRootElement();
		//强制约束flow的格式，必须有<begin id="begin">与<end id="end">
		List<Element> begins = xmlDoc.selectNodes("/flows/begin");
		if (begins == null || !(begins.size() == 1 && StringUtils.equals(begins.get(0).valueOf("@id"),"begin"))) {
			throw new BizException("流程定义错误，必须有且仅有一个begin", ResponseCode._402);
		}
		List<Element> ends = xmlDoc.selectNodes("/flows/end");
		if (ends == null || !(ends.size() == 1 && StringUtils.equals(ends.get(0).valueOf("@id"),"end"))) {
			throw new BizException("流程定义错误，必须有且仅有一个end", ResponseCode._402);
		}
		/* 
		 * 逐一的解析flow中的process
		 * 如果流程的格式为
		 * <flows>
		 * 	<begin id="begin"/>
		 *	<process source="flow:begin" target="joint:XXXXXX" />
		 *	<process source="joint:XXXXXX" target="joint:XXXXXX"/>
		 *	<process source="joint:XXXXXX" target="flow:end"/>
		 *	<end id="end"/>
		 * </flows>
		 * 我们需要忽略掉对于source="flow:begin"和target="flow:end"的情况
		 * 如果上一步的target与这一步的source相同，那么这一步的source不用再重复添加了
		 */
		Iterator<?> processList = rootElement.elementIterator("process");
		List<Map.Entry<String, String>> retList = new ArrayList<Map.Entry<String, String>>();
		String lastnode = null;
		for (;processList.hasNext();) {
			Element element = (Element)processList.next();
			String[] currentsourcenode = element.valueOf("@source").split(":");
			String[] currenttargetnode = element.valueOf("@target").split(":");
			if (!(StringUtils.equals(currentsourcenode[0], "flow") 
					&& StringUtils.equals(currentsourcenode[1], "begin")) && !StringUtils.equals(lastnode, element.valueOf("@source"))) {
				retList.add(new AbstractMap.SimpleEntry(currentsourcenode[1], UUID.randomUUID().toString()));
			}
			if (!(StringUtils.equals(currenttargetnode[0], "flow") 
					&& StringUtils.equals(currenttargetnode[1], "end"))) {
				retList.add(new AbstractMap.SimpleEntry(currenttargetnode[1], UUID.randomUUID().toString()));
				lastnode = element.valueOf("@target");
			}
		}
		return retList;
	}
}