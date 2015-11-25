package com.ai.sboss.arrangement.translation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementJointMappingEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementSelfMappingEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;
import com.ai.sboss.arrangement.utils.JSONUtils;

/**
 * ArrangementJSONTranslationService接口的唯一实现
 * @author yinwenjie
 */
@Scope("prototype")
@Component("arrangementTranslationServiceImpl")
public class ArrangementTranslationServiceImpl implements ArrangementTranslationService {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(ArrangementTranslationServiceImpl.class);
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.translation.ArrangementJSONTranslationService#translationEntity(net.sf.json.JSONObject)
	 */
	@Override
	public ArrangementEntity translationEntity(JSONObject jsonObject) throws BizException {
		/*
		 * 解析步骤如下：
		 * 1、首先通过得到的jsonObject，解析arrangement的基本信息，包括
		 * 		creator、creatorScope、displayName、tradeid、tradescope、id
		 * 		如果没有id，系统会自动创建一个
		 * 2、然后解析其中的joints集合（joints集合一定会有）。每一个joint包括
		 * 		id、visible、absOffsettime、relateOffsettime
		 * 3、然后解析可能的arrangements集合（arrangements集合不一定会有）。每一个arrangement包括：
		 * 		id、visible
		 * 4、flows集合中的所有步骤，全部统一解析为xml格式
		 * */
		
		//1、==============arrangement
		JSONObject arrangementObject = jsonObject.getJSONObject("arrangement");
		if(arrangementObject == null) {
			throw new BizException("没有发现json中有效的arrangement节点，请检查", ResponseCode._404);
		}
		ArrangementEntity arrangementEntity = this.parseArrangementJSON(arrangementObject);
		
		//2、==============joints
		JSONArray jointsArray = arrangementObject.has("joints")?arrangementObject.getJSONArray("joints"):null;
		if(jointsArray == null || jointsArray.isEmpty()) {
			throw new BizException("没有发现json中有效的joints节点，请检查", ResponseCode._404);
		}
		Set<ArrangementJointMappingEntity> childJointsSet = this.parseJointsArray(arrangementEntity, jointsArray);
		arrangementEntity.setJointmapping(childJointsSet);
		
		//3、===============arrangements
		JSONArray arrangementsArray = arrangementObject.has("arrangements")?arrangementObject.getJSONArray("arrangements"):null;
		if(arrangementsArray != null && !arrangementsArray.isEmpty()) {
			Set<ArrangementSelfMappingEntity>  childArrangements = this.parseChildArrangementsArray(arrangementEntity, arrangementsArray);
			arrangementEntity.setChildArrangements(childArrangements);
		}
		
		//4、================flows-xml信息
		JSONArray flowsArray = arrangementObject.has("flows")?arrangementObject.getJSONArray("flows"):null;
		if(jointsArray == null || jointsArray.isEmpty()) {
			throw new BizException("没有发现json中有效的flows节点，请检查", ResponseCode._404);
		}
		arrangementEntity.setFlows(this.parseFlowsteps(flowsArray));
		
		return arrangementEntity;
	}
	
	/**
	 * 该私有方法负责解析arrangement的基本信息
	 * @param agreementObject
	 * @return 
	 * @throws BizException
	 */
	private ArrangementEntity parseArrangementJSON(JSONObject arrangementObject) throws BizException {
		//id
		String id = !arrangementObject.has("id")?null:arrangementObject.getString("id");
		//如果没有id，则自己生成一个
		if(id == null) {
			id = UUID.randomUUID().toString();
		}
		//creator
		String creator = !arrangementObject.has("creator")?null:arrangementObject.getString("creator"); 
		if(creator == null) {
			throw new BizException("arrangement-creator必须设置，请检查", ResponseCode._404);
		}
		//creatorScope
		String creatorScope = !arrangementObject.has("creatorScope")?null:arrangementObject.getString("creatorScope");
		if(creatorScope == null) {
			throw new BizException("arrangement-creatorScope必须设置，请检查", ResponseCode._404);
		}
		//creatorScope只能有三种值
		if(!StringUtils.equals(creatorScope, "industry") && !StringUtils.equals(creatorScope, "producer")
			&& !StringUtils.equals(creatorScope, "consumer")) {
			throw new BizException("arrangement-creatorScope只能设置三种值（industry | producer | consumer），请检查", ResponseCode._404);
		}
		
		//displayName
		String displayName = !arrangementObject.has("displayName")?null:arrangementObject.getString("displayName");
		if(displayName == null) {
			throw new BizException("arrangement-displayName必须设置，请检查", ResponseCode._404);
		}
		//tradeid
		String tradeid = !arrangementObject.has("tradeid")?null:arrangementObject.getString("tradeid");
		if(tradeid == null) {
			throw new BizException("arrangement-tradeid必须设置，请检查", ResponseCode._404);
		}
		//tradescope
		String tradescope = !arrangementObject.has("tradescope")?null:arrangementObject.getString("tradescope");
		if(tradescope == null) {
			throw new BizException("arrangement-tradescope必须设置，请检查", ResponseCode._404);
		}
		//tradescope只能有三种值
		if(!StringUtils.equals(tradescope, "industry") && !StringUtils.equals(tradescope, "producer")
			&& !StringUtils.equals(tradescope, "consumer")) {
			throw new BizException("arrangement-tradescope只能设置三种值（industry | producer | consumer），请检查", ResponseCode._404);
		}
		
		//开始赋值
		ArrangementEntity arrangementEntity = new ArrangementEntity();
		arrangementEntity.setCreator(creator);
		arrangementEntity.setCreatorScope(creatorScope);
		arrangementEntity.setDisplayName(displayName);
		arrangementEntity.setTradeid(tradeid);
		arrangementEntity.setTradeScope(tradescope);
		arrangementEntity.setUid(id);
		return arrangementEntity;
	}

	
	/**
	 * 该私有方法负责解析arrangement中的各joint任务信息
	 * @param arrangement 子任务joint对应的父级arrangement
	 * @param jointsObject
	 * @return
	 * @throws BizException
	 */
	private Set<ArrangementJointMappingEntity> parseJointsArray(ArrangementEntity arrangement , JSONArray jointsArray) throws BizException {
		Set<ArrangementJointMappingEntity> childJointsSet = new LinkedHashSet<ArrangementJointMappingEntity>();
		
		for (int index = 0 ; jointsArray != null && index < jointsArray.size() ; index++) {
			ArrangementJointMappingEntity jointMapping = new ArrangementJointMappingEntity();
			
			JSONObject jointJson = jointsArray.getJSONObject(index);
			//jointid
			String jointid = !jointJson.has("id")?null:jointJson.getString("id");
			if(jointid == null) {
				throw new BizException("joint-id必须设置，请检查", ResponseCode._404);
			}
			//visible
			String visible = !jointJson.has("visible")?null:jointJson.getString("visible");
			if(visible == null) {
				throw new BizException("joint-id必须设置，请检查", ResponseCode._404);
			}
			//visible必须为布尔型
			if(!StringUtils.equalsIgnoreCase(visible, "true") && !StringUtils.equalsIgnoreCase(visible, "false")) {
				throw new BizException("joint-visible必须设置为 true | false，请检查", ResponseCode._404);
			}
			
			//absOffsettime:可能重新定义的absOffsettime
			String absOffsettime = !jointJson.has("absOffsettime")?null:jointJson.getString("absOffsettime");
			if(absOffsettime != null) {
				//只能为正整数
				Pattern pattern = Pattern.compile("^[0-9]*$");
				if(!pattern.matcher(absOffsettime).find()) {
					throw new BizException("joint-absOffsettime只能设置正整数，请检查", ResponseCode._501);
				}
			}
			
			//weight
			String weight = !jointJson.has("weight")?null:jointJson.getString("weight");
			if(weight != null) {
				//只能为正整数
				Pattern pattern = Pattern.compile("^[0-9]*$");
				if(!pattern.matcher(weight).find()) {
					throw new BizException("joint-weight只能设置正整数，请检查", ResponseCode._501);
				}
			}
			
			//relateOffsettime:可能重新定义的relateOffsettime
			String relateOffsettime = !jointJson.has("relateOffsettime")?null:jointJson.getString("relateOffsettime");
			if(relateOffsettime != null) {
				//只能为正整数
				Pattern pattern = Pattern.compile("^[0-9]*$");
				if(!pattern.matcher(relateOffsettime).find()) {
					throw new BizException("joint-relateOffsettime只能设置正整数，请检查", ResponseCode._501);
				}
			}
			
			jointMapping.setAbsOffsettime(absOffsettime == null?null:Long.parseLong(absOffsettime));
			JointEntity joint = new JointEntity();
			joint.setUid(jointid);
			jointMapping.setJoint(joint);
			jointMapping.setWeight(weight!=null?Long.parseLong(weight):0L);
			jointMapping.setParentArrangement(arrangement);
			jointMapping.setRelateOffsettime(relateOffsettime == null?null:Long.parseLong(relateOffsettime));
			jointMapping.setUid(UUID.randomUUID().toString());
			jointMapping.setVisible(visible == null?true:Boolean.parseBoolean(visible));
			childJointsSet.add(jointMapping);
		}
		
		return childJointsSet;
	}

	
	/**
	 * 该私有方法负责解析arrangement中可能的子级流程信息
	 * @param arrangement
	 * @param childArrangementsArray
	 * @return
	 * @throws BizException
	 */
	private Set<ArrangementSelfMappingEntity> parseChildArrangementsArray(ArrangementEntity arrangement , JSONArray childArrangementsArray) throws BizException {
		Set<ArrangementSelfMappingEntity> childArrangementsSet = new LinkedHashSet<ArrangementSelfMappingEntity>();
		
		for (int index = 0 ; childArrangementsArray != null && index < childArrangementsArray.size() ; index++) {
			ArrangementSelfMappingEntity childArrangement = new ArrangementSelfMappingEntity();
			
			JSONObject childArrangementJSON = childArrangementsArray.getJSONObject(index);	
			//arrangementid
			String arrangementid = !childArrangementJSON.has("id")?null:childArrangementJSON.getString("id");
			if(StringUtils.isEmpty(arrangementid)) {
				throw new BizException("arrangement-id必须设置，请检查", ResponseCode._404);
			}
			//visible
			String visible = !childArrangementJSON.has("visible")?null:childArrangementJSON.getString("visible");
			if(visible == null) {
				throw new BizException("arrangement-visible必须设置，请检查", ResponseCode._404);
			}
			//visible必须为布尔型
			if(!StringUtils.equalsIgnoreCase(visible, "true") && !StringUtils.equalsIgnoreCase(visible, "false")) {
				throw new BizException("arrangement-visible必须设置为 true | false，请检查", ResponseCode._404);
			}
			
			ArrangementEntity childArrangementEntity = new ArrangementEntity();
			childArrangementEntity.setUid(arrangementid);
			
			childArrangement.setArrangement(childArrangementEntity);
			childArrangement.setParentArrangement(arrangement);
			childArrangement.setUid(UUID.randomUUID().toString());
			childArrangement.setVisible(visible == null?true:Boolean.parseBoolean(visible));
			childArrangementsSet.add(childArrangement);
		}
		
		return childArrangementsSet;
	}
	
	/**
	 * 该方法用于解析arrangement-json中，flows节点下的所有子节点信息(flowstep)。将他们解析成一个xml-string结构，先进行存储<br>
	 * 注意，经过15-08-19日的讨论，不再需要递归了，因为前段暂时不会传condition条件定义到后端
	 * @param fowsValues 
	 * @param childElements 
	 * @throws BizException 
	 * TODO 以后还要进行每一个flow值有效性的验证
	 */
	private String parseFlowsteps(JSONArray flowstepsArray) throws BizException {
		StringBuffer flowsXML = new StringBuffer("<begin id=\"begin\"/>");
		if(flowstepsArray == null || flowstepsArray.isEmpty()) {
			flowsXML.append("<process source=\"flow:begin\" target=\"flow:end\" />");
			flowsXML.append("<end id=\"end\"/>");
			return flowsXML.toString();
		}
		
		/*
		 * 1、每一个flowstep节点，根据type的不一样，形成的XML节点名称也是不一样的。
		 * 2、只有一个begin点和一个end点
		 * */
		//1、===========
		for (int index = 0 ; index < flowstepsArray.size() ; index++) {
			JSONObject flowsteopObject = flowstepsArray.getJSONObject(index);			
			//source
			String source = !flowsteopObject.has("source")?null:flowsteopObject.getString("source");
			if(source == null) {
				throw new BizException("flowstep-source必须设置，请检查", ResponseCode._404);
			}
			
			//target
			String target = !flowsteopObject.has("target")?null:flowsteopObject.getString("target");
			if(target == null) {
				throw new BizException("flowstep-target必须设置，请检查", ResponseCode._404);
			}
			
			//开始组装
			flowsXML.append("<process source=\"" + source + "\" target=\"" + target + "\" />");
		}
		flowsXML.append("<end id=\"end\"/>");
		return flowsXML.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.translation.ArrangementXMLTranslationService#translationEntity(java.lang.String)
	 */
	@Override
	public ArrangementEntity translationEntity(String xmlText) throws BizException {
		if(StringUtils.isEmpty(xmlText)) {
			throw new BizException("xmlText参数必须传入，并且满足定义arrangement-xml定义规则", ResponseCode._404);
		}
		
		ByteArrayInputStream byteInput = new ByteArrayInputStream(xmlText.getBytes());
		ArrangementEntity result = this.translationEntity(byteInput);
		//这里一定要自己关闭
		try {
			byteInput.close();
		} catch (IOException e) {
			ArrangementTranslationServiceImpl.LOGGER.error(e.getMessage(), e);
			throw new BizException(e.getMessage(), ResponseCode._501);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.translation.ArrangementXMLTranslationService#translationEntity(java.io.InputStream)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrangementEntity translationEntity(InputStream xmlStream) throws BizException {
		if(xmlStream == null) {
			throw new BizException("xmlStream参数必须传入，并且满足定义arrangement-xml 定义规则", ResponseCode._404);
		}
		
		/*
		 * 处理过程描述如下：
		 * 1、首先通过xpath取/arrangement，取得arrangement的基本信息。如果出现异常则终止
		 * 		取/joint/trade，取得流程所属的行业信息
		 * 2、取/joint/joints节点下所有子节点，取得流程所包含的任务节点信息（如果发现错误的定义则终止）
		 * 3、取/joint/arrangements节点下所有子节点，取得流程所包含的所有子流程信息（如果发现错误的定义则终止）
		 * 4、取/joint/flows节点，这个节点下的所有流程定义内容，
		 * 		将被直接解析成字符串作为arrangement的一个属性存在
		 * TODO 目前怎么存储流程的flow信息最方便流程实例化，还没有想好
		 * */
		Document xmlDoc = null;
		try {
			SAXReader saxReader = new SAXReader();
			xmlDoc = saxReader.read(xmlStream);
		} catch (DocumentException e) {
			ArrangementTranslationServiceImpl.LOGGER.error(e.getMessage() , e);
			throw new BizException(e.getMessage(), ResponseCode._501);
		}
		
		//1、==================
		Element rootElement = xmlDoc.getRootElement();
		//系统行业信息/arrangement/trade
		Element tradeElement = null;
		tradeElement = (Element)xmlDoc.selectSingleNode("/arrangement/trade");
		if(tradeElement == null) {
			ArrangementTranslationServiceImpl.LOGGER.error("/arrangement/trade节点必须设置，请检查");
			throw new BizException("/arrangement/trade节点必须设置，请检查", ResponseCode._501);
		}
		ArrangementEntity resultArrangement = this.parseArrangementEntity(rootElement, tradeElement);
		
		//2、==========================
		List<Element> jointNodes = (List<Element>)xmlDoc.selectNodes("/arrangement/joints/joint");
		if(jointNodes == null || jointNodes.isEmpty()) {
			throw new BizException("/arrangement/joints至少有一个节点，请检查", ResponseCode._501);
		}
		//在计算前一定要保证jointid没有重复
		List<Attribute> jointidAttrs = xmlDoc.selectNodes("/arrangement/joints/joint/attribute::id");
		List<String> jointidList = new ArrayList<String>();
		for (Attribute attribute : jointidAttrs) {
			String jointid = attribute.getStringValue().trim();
			if(StringUtils.isEmpty(jointid)) {
				throw new BizException("/arrangement/joints/joint[id]：不能为空，请检查", ResponseCode._501);
			}
			
			//说明jointid有重复
			if(jointidList.contains(jointid)) {
				throw new BizException("/arrangement/joints/joint：" + jointid + "重复，请检查", ResponseCode._501);
			}
			jointidList.add(jointid);
		}
		Set<ArrangementJointMappingEntity> jointSet = this.parseChildArrangementEntity(resultArrangement, jointNodes);
		resultArrangement.setJointmapping(jointSet);
		
		//3、==========================
		List<Element> childArrangementNodes = (List<Element>)xmlDoc.selectNodes("/arrangement/arrangements/arrangement");
		//子流程是可以没有设置的
		if(childArrangementNodes != null && !childArrangementNodes.isEmpty()) {
			//在计算前一定要保证arrangementid没有重复
			List<Attribute> arrangementIdAttrs = xmlDoc.selectNodes("/arrangement/arrangements/arrangement/attribute::id");
			List<String> arrangementIdList = new ArrayList<String>();
			for (Attribute attribute : arrangementIdAttrs) {
				String arrangementId = attribute.getStringValue().trim();
				if(StringUtils.isEmpty(arrangementId)) {
					throw new BizException("/arrangement/arrangements/arrangement[id]：不能为空，请检查", ResponseCode._501);
				}
				
				//说明jointname有重复
				if(arrangementIdList.contains(arrangementId)) {
					throw new BizException("/arrangement/arrangements/arrangement：" + arrangementId + "重复，请检查", ResponseCode._501);
				}
				arrangementIdList.add(arrangementId);
			}
			Set<ArrangementSelfMappingEntity> childArrangementSet = this.parseChildArrangement(resultArrangement, childArrangementNodes);
			resultArrangement.setChildArrangements(childArrangementSet);
		}
		
		//4、==========================
		Element flowsElement = (Element)xmlDoc.selectSingleNode("/arrangement/flows");
		if(flowsElement == null) {
			throw new BizException("/arrangement/flows必须被设置，以便实例化", ResponseCode._501);
		}
		//递归获取flows节点下的所有信息
		StringBuffer flowsValue = new StringBuffer("");
		this.recursiveFlowsNodes(flowsValue, flowsElement.elements());
		resultArrangement.setFlows(flowsValue.toString());
		
		return resultArrangement;
	}
	
	/**
	 * @param jointElement
	 * @param offsettimeElement
	 * @return
	 * @throws BizException
	 */
	private ArrangementEntity parseArrangementEntity(Element arrangementElement , Element tradeElement) throws BizException {
		//=============================以下是根节点的属性
		//流程编号（没有就生成一个）
		Attribute idAttr = arrangementElement.attribute("id");
		String id = null;
		if(idAttr == null  || StringUtils.isEmpty((id = idAttr.getStringValue()))) {
			id = UUID.randomUUID().toString();
		}
		//创建者
		Attribute creatorAttr = arrangementElement.attribute("creator");
		String creator = creatorAttr == null?null:creatorAttr.getStringValue();
		if(creator == null) {
			throw new BizException("arrangement-creator必须填写信息", ResponseCode._401);
		}
		//creatorScope：创建者范围
		Attribute creatorScopeAttr = arrangementElement.attribute("creatorScope");
		String creatorScope = creatorScopeAttr == null?null:creatorScopeAttr.getStringValue();
		if(creatorScope == null) {
			throw new BizException("arrangement-creatorScope必须填写信息", ResponseCode._401);
		}
		//注意，creatorScope只允许有三种值
		if(!StringUtils.endsWithIgnoreCase(creatorScope, "industry")
			&& !StringUtils.endsWithIgnoreCase(creatorScope, "producer")
			&& !StringUtils.endsWithIgnoreCase(creatorScope, "consumer")) {
			throw new BizException("arrangement-creatorScope只允许有三种值：industry、producer、consumer", ResponseCode._401);
		}
		//displayName展现流程的中文名称
		Attribute displayNameAttr = arrangementElement.attribute("displayName");
		String displayName = displayNameAttr == null?null:displayNameAttr.getStringValue();
		
		//==================以下是tradeElement节点的属性
		//id：行业编号
		Attribute tradeidAttr = tradeElement.attribute("id");
		String tradeid = tradeidAttr == null?null:tradeidAttr.getStringValue();
		if(tradeid == null) {
			throw new BizException("trade-id必须填写信息", ResponseCode._401);
		}
		//scope：行业中，使用这个流程的范围
		Attribute tradeScopeAttr = tradeElement.attribute("scope");
		String tradescope = tradeScopeAttr == null?null:tradeScopeAttr.getStringValue();
		if(tradescope == null) {
			throw new BizException("trade-scope必须填写信息", ResponseCode._401);
		}
		//注意，scope只允许有三种值
		if(!StringUtils.endsWithIgnoreCase(tradescope, "industry")
			&& !StringUtils.endsWithIgnoreCase(tradescope, "producer")
			&& !StringUtils.endsWithIgnoreCase(tradescope, "consumer")) {
			throw new BizException("trade-scope只允许有三种值：industry、producer、consumer", ResponseCode._401);
		}
		
		//开始创建arrangement对象，并赋值基本信息
		ArrangementEntity arrangement = new ArrangementEntity();
		arrangement.setCreator(creator);
		arrangement.setCreatorScope(creatorScope);
		arrangement.setDisplayName(displayName);
		arrangement.setTradeid(tradeid);
		arrangement.setUid(id);
		arrangement.setTradeScope(tradescope);
		
		return arrangement;
	}
	
	private Set<ArrangementJointMappingEntity> parseChildArrangementEntity(ArrangementEntity arrangement , List<Element> jointsNodes) throws BizException {
		Set<ArrangementJointMappingEntity> childJointsSet = new LinkedHashSet<ArrangementJointMappingEntity>();
		
		for (Element jointsNode : jointsNodes) {
			//id
			Attribute idAttr = jointsNode.attribute("id");
			String id = idAttr == null?null:idAttr.getStringValue();
			if(StringUtils.isEmpty(id)) {
				throw new BizException("/arrangement/joints/joint[id]必须设置，请检查", ResponseCode._501);
			} 
			
			//visible
			Attribute visibleAttr = jointsNode.attribute("visible");
			String visible = visibleAttr == null?null:visibleAttr.getStringValue();
			if(StringUtils.isEmpty(visible)) {
				visible = "true";
			}
			//注意visible只能有两个值true或者false
			if(!StringUtils.endsWithIgnoreCase(visible, "true") && !StringUtils.endsWithIgnoreCase(visible, "false")) {
				throw new BizException("/arrangement/joints/joint[visible]必须是布尔值，请检查", ResponseCode._501);
			}
			visible = visible.toLowerCase();
			
			//absOffsettime:可能重新定义的absOffsettime
			Attribute absOffsettimeAttr = jointsNode.attribute("absOffsettime");
			String absOffsettime = absOffsettimeAttr == null?null:absOffsettimeAttr.getStringValue();
			if(absOffsettime != null) {
				//只能为正整数
				Pattern pattern = Pattern.compile("^[0-9]*$");
				if(!pattern.matcher(absOffsettime).find()) {
					throw new BizException("/arrangement/joints/joint[absOffsettime]只能设置正整数，请检查", ResponseCode._501);
				}
			}
			
			//relateOffsettime:可能重新定义的relateOffsettime
			Attribute relateOffsettimeAttr = jointsNode.attribute("relateOffsettime");
			String relateOffsettime = relateOffsettimeAttr == null?null:relateOffsettimeAttr.getStringValue();
			if(relateOffsettime != null) {
				//只能为正整数
				Pattern pattern = Pattern.compile("^[0-9]*$");
				if(!pattern.matcher(relateOffsettime).find()) {
					throw new BizException("/arrangement/joints/joint[relateOffsettime]只能设置正整数，请检查", ResponseCode._501);
				}
			}
			
			//weight
			Attribute weightAttr = jointsNode.attribute("weight");
			String weight = (weightAttr == null?null:weightAttr.getStringValue());
			if(weight != null) {
				//只能为正整数
				Pattern pattern = Pattern.compile("^[0-9]*$");
				if(!pattern.matcher(weight).find()) {
					throw new BizException("/arrangement/joints/joint[weight]只能设置正整数，请检查", ResponseCode._501);
				}
			}
			
			ArrangementJointMappingEntity childJoint = new ArrangementJointMappingEntity();
			childJoint.setAbsOffsettime(absOffsettime == null?null:Long.parseLong(absOffsettime));
			childJoint.setRelateOffsettime(relateOffsettime == null?null:Long.parseLong(relateOffsettime));
			JointEntity joint = new JointEntity();
			joint.setUid(id);
			childJoint.setJoint(joint);
			childJoint.setParentArrangement(arrangement);
			childJoint.setUid(UUID.randomUUID().toString());
			childJoint.setVisible(Boolean.valueOf(visible));
			childJoint.setWeight(weight!=null?Long.parseLong(weight):0L);
			childJointsSet.add(childJoint);
		}
		
		return childJointsSet;
	}
	
	private Set<ArrangementSelfMappingEntity> parseChildArrangement(ArrangementEntity arrangement , List<Element> arrangementNodes) throws BizException {
		Set<ArrangementSelfMappingEntity> childArrangements = new LinkedHashSet<ArrangementSelfMappingEntity>();
		
		for (Element childArrangementNode : arrangementNodes) {
			//id
			Attribute idAttr = childArrangementNode.attribute("id");
			String id = idAttr == null?null:idAttr.getStringValue();
			if(StringUtils.isEmpty(id)) {
				throw new BizException("/arrangement/arrangements/arrangement[id]必须设置，请检查", ResponseCode._501);
			}
			
			//visible
			Attribute visibleAttr = childArrangementNode.attribute("visible");
			String visible = visibleAttr == null?null:visibleAttr.getStringValue();
			if(StringUtils.isEmpty(visible)) {
				visible = "true";
			}
			//注意visible只能有两个值true或者false
			if(!StringUtils.endsWithIgnoreCase(visible, "true") && !StringUtils.endsWithIgnoreCase(visible, "false")) {
				throw new BizException("/arrangement/arrangements/arrangement[visible]必须是布尔值，请检查", ResponseCode._501);
			}
			visible = visible.toLowerCase();
			
			ArrangementSelfMappingEntity childArrangementMappingEntity = new ArrangementSelfMappingEntity();
			ArrangementEntity childArrangement = new ArrangementEntity();
			childArrangement.setUid(id);
			childArrangementMappingEntity.setArrangement(childArrangement);
			childArrangementMappingEntity.setParentArrangement(arrangement);
			childArrangementMappingEntity.setUid(UUID.randomUUID().toString());
			childArrangementMappingEntity.setVisible(Boolean.parseBoolean(visible));
			childArrangements.add(childArrangementMappingEntity);
		}
		
		return childArrangements;
	}
	
	/**
	 * 该方法用于递归解析arrangement-xml中，flows节点下的所有子节点信息。将他们解析成一个字符串，先进行存储
	 * @param fowsValues
	 * @param childElements
	 * @throws BizException
	 */
	private void recursiveFlowsNodes(StringBuffer fowsValues , List<Element> childElements) throws BizException {
		if(childElements == null || childElements.isEmpty()) {
			return;
		}
		
		/*
		 * 单次递归的过程如下：
		 * 1、从childElement中依次取出element信息
		 * 2、构造这个element的各种属性，
		 * 3、递归这个element中可能的子级element（如没有就不递归咯）
		 * 4、封口这个element
		 * */
		//1、===========
		for (Element childElement : childElements) {
			fowsValues.append("<" + childElement.getName() + "");
			//2、===========
			@SuppressWarnings("unchecked")
			List<Attribute> attrs = childElement.attributes();
			for(int index = 0 ; attrs != null && index < attrs.size() ; index++) {
				Attribute attr = attrs.get(index);
				fowsValues.append(" " + attr.getName() + "=\"" + attr.getValue() + "\"");
			}
			fowsValues.append(">");
			
			//3、==========
			@SuppressWarnings("unchecked")
			List<Element> hisElements = childElement.elements();
			if(hisElements != null && !hisElements.isEmpty()) {
				this.recursiveFlowsNodes(fowsValues, hisElements);
			}
			
			//4、==========
			fowsValues.append("</" + childElement.getName() + ">");
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.translation.ArrangementXMLTranslationService#translationJSON(java.lang.String)
	 */
	@Override
	public JSONObject translationJSON(String xmlText) throws BizException {
		/*
		 * 
		 * */
		if(StringUtils.isEmpty(xmlText)) {
			throw new BizException("xmlText参数必须传入，并且满足定义joint-xml定义规则", ResponseCode._404);
		}
		
		ByteArrayInputStream byteInput = new ByteArrayInputStream(xmlText.getBytes());
		JSONObject jsonObject = this.translationJSON(byteInput);
		//这里一定要自己关闭
		try {
			byteInput.close();
		} catch (IOException e) {
			ArrangementTranslationServiceImpl.LOGGER.error(e.getMessage(), e);
			throw new BizException(e.getMessage(), ResponseCode._501);
		}
		return jsonObject;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.translation.ArrangementXMLTranslationService#translationJSON(java.io.InputStream)
	 */
	@Override
	public JSONObject translationJSON(InputStream xmlStream) throws BizException {
		/*
		 * 实现方式为：首先转成对象，然后对象直接转成JSONObject
		 * 同样的，这个方法内部并不负责关闭inputstream
		 * */
		ArrangementEntity arrangementEntity = this.translationEntity(xmlStream);
		return JSONUtils.toJSONObject(arrangementEntity, new String[]{"joint","parentArrangement"});
	}
}
