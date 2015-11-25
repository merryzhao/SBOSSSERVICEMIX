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

import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointInputParamsEntity;
import com.ai.sboss.arrangement.entity.orm.JointOutputParamsEntity;
import com.ai.sboss.arrangement.entity.orm.JointTradeMappingEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;
import com.ai.sboss.arrangement.utils.JSONUtils;

/**
 * @author yinwenjie
 *
 */
@Scope("prototype")
@Component("jointTranslationServiceImpl")
public class JointTranslationServiceImpl implements JointTranslationService {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(JointTranslationServiceImpl.class);

	@Override
	public JointEntity translationEntity(String xmlText) throws BizException {
		if (StringUtils.isEmpty(xmlText)) {
			throw new BizException("xmlText参数必须传入，并且满足定义joint-xml定义规则", ResponseCode._404);
		}

		ByteArrayInputStream byteInput = new ByteArrayInputStream(xmlText.getBytes());
		JointEntity result = this.translationEntity(byteInput);
		// 这里一定要自己关闭
		try {
			byteInput.close();
		} catch (IOException e) {
			JointTranslationServiceImpl.LOGGER.error(e.getMessage(), e);
			throw new BizException(e.getMessage(), ResponseCode._501);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.translation.JointXMLTranslationService#
	 * translationEntity(java.io.InputStream)
	 */
	@Override
	public JointEntity translationEntity(InputStream xmlStream) throws BizException {
		if (xmlStream == null) {
			throw new BizException("xmlStream参数必须传入，并且满足定义joint-xml 定义规则", ResponseCode._404);
		}

		/*
		 * 处理过程描述如下： 1、首先通过xpath取/joint，取得joint的基本信息。如果出现异常则终止
		 * 取/joint/offsettime，取得默认的时间偏移信息
		 * ================2015-08-26==============
		 * joint模板，对于时间线的属性和要就进行了补全，新增了offsetTitle、offsetVisible、expandTypeId属性
		 * 
		 * 2、取/joint/trades节点下所有子节点，取得所有行业信息（如果发现错误的定义则终止） 注意，这里要保证tradeid没有重复
		 * 3、取/joint/inputs节点下所有子节点，取得所有的入参信息（入参信息可以什么都没有，但是若发现错误的定义，则终止）
		 * 注意，为了保证顺序，这里采用linkedHashSet进行存储 还要保证其input-name没有重复
		 * 4、取/joint/outputs节点下的所有子节点，取得所有的出参信息（出参信息可以什么都没有，但是若发现错误的定义，则终止）
		 * 注意，为了保证顺序，这里采用linkedHashSet进行存储 还要保证其output-name没有重复
		 */
		Document xmlDoc = null;
		try {
			SAXReader saxReader = new SAXReader();
			xmlDoc = saxReader.read(xmlStream);
		} catch (DocumentException e) {
			JointTranslationServiceImpl.LOGGER.error(e.getMessage(), e);
			throw new BizException(e.getMessage(), ResponseCode._501);
		}

		// 1、=======================
		Element rootElement = xmlDoc.getRootElement();
		// 系统偏移量/joint/offsettime
		Element offsettimeElement = null;
		offsettimeElement = (Element) xmlDoc.selectSingleNode("/joint/offsettime");
		if (offsettimeElement == null) {
			JointTranslationServiceImpl.LOGGER.error("/joint/offsettime节点必须设置，请检查");
			throw new BizException("/joint/offsettime节点必须设置，请检查", ResponseCode._501);
		}
		JointEntity resultJoint = this.parseJointEntity(rootElement, offsettimeElement);

		// 2、==========================
		@SuppressWarnings("unchecked")
		List<Element> tradeNodes = (List<Element>) xmlDoc.selectNodes("/joint/trades/trade");
		if (tradeNodes == null || tradeNodes.isEmpty()) {
			throw new BizException("/joint/trades/trade至少有一个节点，请检查", ResponseCode._501);
		}
		// 在计算前一定要保证tradeid没有重复
		@SuppressWarnings("unchecked")
		List<Attribute> tradeidAttrs = xmlDoc.selectNodes("/joint/trades/trade/attribute::id");
		List<String> tradeidList = new ArrayList<String>();
		for (Attribute attribute : tradeidAttrs) {
			String tradeid = attribute.getStringValue().trim();
			if (StringUtils.isEmpty(tradeid)) {
				throw new BizException("/joint/trades/trade[id]：不能为空，请检查", ResponseCode._501);
			}

			// 说明tradeid有重复
			if (tradeidList.contains(tradeid)) {
				throw new BizException("/joint/trades/trade：" + tradeid + "重复，请检查", ResponseCode._501);
			}
			tradeidList.add(tradeid);
		}

		Set<JointTradeMappingEntity> tradeNodeSet = this.parseJointTradeMappings(resultJoint, tradeNodes);
		resultJoint.setTrades(tradeNodeSet);

		// 3、==============================
		@SuppressWarnings("unchecked")
		List<Element> inputParamNodes = (List<Element>) xmlDoc.selectNodes("/joint/inputs/param");
		// 如果条件成立，才进行inputparams的解析；否则不进行
		if (inputParamNodes != null && !inputParamNodes.isEmpty()) {
			// 在计算前一定要保证name没有重复
			@SuppressWarnings("unchecked")
			List<Attribute> inputsNameAttrs = xmlDoc.selectNodes("/joint/inputs/param/attribute::name");
			List<String> inputsNameList = new ArrayList<String>();
			for (Attribute attribute : inputsNameAttrs) {
				String inputsName = attribute.getStringValue().trim();
				if (StringUtils.isEmpty(inputsName)) {
					throw new BizException("/joint/inputs/param[name]不能为空，请检查", ResponseCode._501);
				}

				// 说明tradeid有重复
				if (inputsNameList.contains(inputsName)) {
					throw new BizException("/joint/inputs/param：" + inputsName + "重复，请检查", ResponseCode._501);
				}
				inputsNameList.add(inputsName);
			}

			Set<JointInputParamsEntity> inputParamsSet = this.parseJointInputParamsEntity(resultJoint, inputParamNodes);
			resultJoint.setInputParams(inputParamsSet);
		}

		// 4、==============================
		@SuppressWarnings("unchecked")
		List<Element> outputParamNodes = (List<Element>) xmlDoc.selectNodes("/joint/outputs/param");
		// 如果条件成立，才进行outputs的解析；否则不进行
		if (outputParamNodes != null && !outputParamNodes.isEmpty()) {
			// 在计算前一定要保证name没有重复
			@SuppressWarnings("unchecked")
			List<Attribute> outputsNameAttrs = xmlDoc.selectNodes("/joint/outputs/param/attribute::name");
			List<String> outputsNameList = new ArrayList<String>();
			for (Attribute attribute : outputsNameAttrs) {
				String outputsName = attribute.getStringValue().trim();
				if (StringUtils.isEmpty(outputsName)) {
					throw new BizException("/joint/outputs/param[name]不能为空，请检查", ResponseCode._501);
				}

				// 说明outputs-name有重复
				if (outputsNameList.contains(outputsName)) {
					throw new BizException("/joint/outputs/param：" + outputsName + "重复，请检查", ResponseCode._501);
				}
				outputsNameList.add(outputsName);
			}

			Set<JointOutputParamsEntity> outputParamsSet = this.parseJointOutputParamsEntity(resultJoint,
					outputParamNodes);
			resultJoint.setOutputParams(outputParamsSet);
		}

		return resultJoint;
	}

	/**
	 * @param jointElement
	 * @param offsettimeElement
	 * @return
	 * @throws BizException
	 */
	private JointEntity parseJointEntity(Element jointElement, Element offsettimeElement) throws BizException {
		// =============================以下是根节点的属性
		// id（如果设置了id，就用设置的，）
		Attribute idNameAttr = jointElement.attribute("id");
		String id = null;
		if (idNameAttr == null || StringUtils.isEmpty(id = idNameAttr.getStringValue())) {
			id = UUID.randomUUID().toString();
		}

		// 任务中文名
		Attribute displayNameAttr = jointElement.attribute("displayName");
		String displayName = null;
		if (displayNameAttr == null || StringUtils.isEmpty((displayName = displayNameAttr.getStringValue()))) {
			throw new BizException("displayName必须填写信息", ResponseCode._401);
		}
		// 对应的camelUri信息
		Attribute camelUriAttr = jointElement.attribute("camelUri");
		String camelUri = camelUriAttr == null ? null : camelUriAttr.getStringValue();
		if (StringUtils.isEmpty(camelUri)) {
			camelUri = null;
		}
		// 任务默认执行者
		Attribute executorAttr = jointElement.attribute("executor");
		String executor = executorAttr == null ? null : executorAttr.getStringValue();
		if (StringUtils.isEmpty(executor)) {
			executor = null;
		} // 执行者只有三个范围
		else if (!StringUtils.equals(executor, "industry") && !StringUtils.equals(executor, "producer")
				&& !StringUtils.equals(executor, "consumer")) {
			throw new BizException("joint-executor只能指定三种值（industry | producer | consumer），请检查", ResponseCode._404);
		}

		// ==================以下是offsettime节点的属性
		// abs相对于整个实际时间线原点的偏移
		Attribute absAttr = offsettimeElement.attribute("abs");
		String abs = absAttr == null ? null : absAttr.getStringValue();
		if (StringUtils.isEmpty(abs)) {
			abs = null;
		}
		// 必须为正整数
		Pattern pattern = Pattern.compile("^[0-9]*$");
		if (abs != null && !pattern.matcher(abs).find()) {
			throw new BizException("/joint/offsettime[abs]只能设置正整数，请检查", ResponseCode._501);
		}
		// relate相对于当前任务时间原点的偏移
		Attribute relateAttr = offsettimeElement.attribute("relate");
		String relate = relateAttr == null ? null : relateAttr.getStringValue();
		if (StringUtils.isEmpty(relate)) {
			relate = null;
		}
		if (relate != null && !pattern.matcher(relate).find()) {
			throw new BizException("/joint/offsettime[relate]只能设置正整数，请检查", ResponseCode._501);
		}
		// offsetTitle
		Attribute offsetTitleAttr = offsettimeElement.attribute("offsetTitle");
		String offsetTitle = offsetTitleAttr == null ? null : offsetTitleAttr.getStringValue();
		if (StringUtils.isEmpty(offsetTitle)) {
			offsetTitle = "";
		}
		// offsetVisible
		Attribute offsetVisibleAttr = offsettimeElement.attribute("offsetVisible");
		String offsetVisible = offsetVisibleAttr == null ? null : offsetVisibleAttr.getStringValue();
		if (StringUtils.isEmpty(offsetVisible)) {
			offsetVisible = "both";
		}
		// offsetVisible只能有producer | consumer | both 三个值
		if (offsetVisible != null && !StringUtils.equals(offsetVisible, "producer")
				&& !StringUtils.equals(offsetVisible, "consumer") && !StringUtils.equals(offsetVisible, "both")) {
			throw new BizException("/joint/offsettime[offsetVisible]只能设置三种值：producer | consumer | both，请检查",
					ResponseCode._501);
		}
		// expandTypeId
		Attribute expandTypeIdAttr = offsettimeElement.attribute("expandTypeId");
		String expandTypeId = expandTypeIdAttr == null ? null : expandTypeIdAttr.getStringValue();
		if (StringUtils.isEmpty(expandTypeId)) {
			expandTypeId = null;
		}
		// expandTypeId只能是数字
		pattern = Pattern.compile("^[0-9]*$");
		if (expandTypeId != null && !pattern.matcher(expandTypeId).find()) {
			throw new BizException("/joint/offsettime[expandTypeId]只能设置数字，请检查", ResponseCode._501);
		}
		// promptOffsettime(任务提醒信息)
		String promptOffsettime = offsettimeElement.getText();

		// 开始创建joint对象，并赋值基本信息
		JointEntity resultJoint = new JointEntity();
		resultJoint.setDisplayName(displayName);
		resultJoint.setUid(id == null ? UUID.randomUUID().toString() : id);
		resultJoint.setExecutor(executor);
		resultJoint.setCamelUri(camelUri);

		resultJoint.setAbsOffsettime(abs == null ? null : Long.parseLong(abs));
		resultJoint.setPromptOffsettime(promptOffsettime);
		resultJoint.setRelateOffsettime(relate == null ? null : Long.parseLong(relate));
		resultJoint.setOffsetTitle(offsetTitle);
		resultJoint.setOffsetVisible(offsetVisible);
		resultJoint.setExpandTypeId(expandTypeId == null ? null : Integer.parseInt(expandTypeId));

		return resultJoint;
	}

	private Set<JointTradeMappingEntity> parseJointTradeMappings(JointEntity resultJoint, List<Element> tradeNodes)
			throws BizException {
		Set<JointTradeMappingEntity> tradeNodeSet = new LinkedHashSet<JointTradeMappingEntity>();
		for (Element tradeNode : tradeNodes) {
			// tradeid
			Attribute tradeidAttr = tradeNode.attribute("id");
			String tradeid = null;
			if (tradeidAttr == null || StringUtils.isEmpty(tradeid = tradeidAttr.getStringValue())) {
				throw new BizException("/joint/trades/trade必须设置id，请检查", ResponseCode._501);
			}
			// scope
			Attribute tradescopeAttr = tradeNode.attribute("scope");
			String scope = null;
			if (tradescopeAttr == null || StringUtils.isEmpty(scope = tradescopeAttr.getStringValue())) {
				throw new BizException("/joint/trades/trade必须设置scope，请检查", ResponseCode._501);
			}
			// 注意，scope只能有三个值industry、producer、consumer
			if (!StringUtils.equals(scope, "industry") && !StringUtils.equals(scope, "producer")
					&& !StringUtils.equals(scope, "consumer")) {
				throw new BizException("/joint/trades/trade[scope]只能有三个值：industry、producer、consumer，请检查",
						ResponseCode._501);
			}

			JointTradeMappingEntity trademapping = new JointTradeMappingEntity();
			trademapping.setJoint(resultJoint);
			trademapping.setScope(scope);
			trademapping.setTradeid(tradeid);
			trademapping.setUid(UUID.randomUUID().toString());
			tradeNodeSet.add(trademapping);
		}

		return tradeNodeSet;
	}

	private Set<JointInputParamsEntity> parseJointInputParamsEntity(JointEntity resultJoint,
			List<Element> inputParamNodes) throws BizException {
		Set<JointInputParamsEntity> inputParamsSet = new LinkedHashSet<JointInputParamsEntity>();

		for (Element inputParamNode : inputParamNodes) {
			// name
			Attribute nameAttr = inputParamNode.attribute("name");
			String name = nameAttr == null ? null : nameAttr.getStringValue();
			if (StringUtils.isEmpty(name)) {
				throw new BizException("/joint/inputs/param[name]必须设置，请检查", ResponseCode._501);
			}

			// type
			Attribute typeAttr = inputParamNode.attribute("type");
			String type = typeAttr == null ? null : typeAttr.getStringValue();
			if (StringUtils.isEmpty(type)) {
				throw new BizException("/joint/inputs/param[type]必须设置，请检查", ResponseCode._501);
			}
			// type只能有这些值：String，Boolean，Integer，Long，Float，Double，JSON，XML，Date
			if (!StringUtils.endsWithIgnoreCase(type, "String") && !StringUtils.endsWithIgnoreCase(type, "Boolean")
					&& !StringUtils.endsWithIgnoreCase(type, "Integer") && !StringUtils.endsWithIgnoreCase(type, "Long")
					&& !StringUtils.endsWithIgnoreCase(type, "Float") && !StringUtils.endsWithIgnoreCase(type, "Double")
					&& !StringUtils.endsWithIgnoreCase(type, "JSON") && !StringUtils.endsWithIgnoreCase(type, "XML")
					&& !StringUtils.endsWithIgnoreCase(type, "Date")) {
				throw new BizException(
						"/joint/inputs/param[type]"
								+ "type只能有这些值：String，Boolean，Integer，Long，Float，Double，JSON，XML，Date，请检查",
						ResponseCode._501);
			}

			// displayName
			Attribute cdisplayNameAttr = inputParamNode.attribute("displayName");
			String cdisplayName = cdisplayNameAttr == null ? null : cdisplayNameAttr.getStringValue();
			if (StringUtils.isEmpty(cdisplayName)) {
				cdisplayName = "";
			}

			// dispalyType
			Attribute dispalyTypeAttr = inputParamNode.attribute("dispalyType");
			String dispalyType = dispalyTypeAttr == null ? null : dispalyTypeAttr.getStringValue();
			if (StringUtils.isEmpty(dispalyType)) {
				dispalyType = "";
			}

			// required
			Attribute requiredAttr = inputParamNode.attribute("required");
			String required = requiredAttr == null ? null : requiredAttr.getStringValue();
			if (StringUtils.isEmpty(required)) {
				required = "true";
			}
			// 注意required只能有两个值true或者false
			if (!StringUtils.endsWithIgnoreCase(required, "true")
					&& !StringUtils.endsWithIgnoreCase(required, "false")) {
				throw new BizException("/joint/inputs/param[required]必须是布尔值，请检查", ResponseCode._501);
			}
			required = required.toLowerCase();

			// default
			Attribute cdefaultAttr = inputParamNode.attribute("default");
			String cdefault = cdefaultAttr == null ? null : cdefaultAttr.getStringValue();
			if (StringUtils.isEmpty(cdefault)) {
				cdefault = null;
			}

			JointInputParamsEntity inputParamsEntity = new JointInputParamsEntity();
			inputParamsEntity.setDefaultValue(cdefault);
			inputParamsEntity.setDisplayName(cdisplayName);
			inputParamsEntity.setDisplayType(dispalyType);
			inputParamsEntity.setJoint(resultJoint);
			inputParamsEntity.setName(name);
			inputParamsEntity.setRequired(Boolean.valueOf(required));
			inputParamsEntity.setType(type);
			inputParamsEntity.setUid(UUID.randomUUID().toString());
			inputParamsSet.add(inputParamsEntity);
		}

		return inputParamsSet;
	}

	private Set<JointOutputParamsEntity> parseJointOutputParamsEntity(JointEntity resultJoint,
			List<Element> outputParamNodes) throws BizException {
		Set<JointOutputParamsEntity> outputParamsSet = new LinkedHashSet<JointOutputParamsEntity>();

		for (Element outputParamNode : outputParamNodes) {
			// name
			Attribute nameAttr = outputParamNode.attribute("name");
			String name = null;
			if (nameAttr == null || StringUtils.isEmpty(name = nameAttr.getStringValue())) {
				throw new BizException("/joint/outputs/param[name]必须设置，请检查", ResponseCode._501);
			}

			// type
			Attribute typeAttr = outputParamNode.attribute("type");
			String type = null;
			if (typeAttr == null || StringUtils.isEmpty(type = typeAttr.getStringValue())) {
				throw new BizException("/joint/outputs/param[type]必须设置，请检查", ResponseCode._501);
			}
			// type只能有这些值：String，Boolean，Integer，Long，Float，Double，JSON，XML，Date
			if (!StringUtils.endsWithIgnoreCase(type, "String") && !StringUtils.endsWithIgnoreCase(type, "Boolean")
					&& !StringUtils.endsWithIgnoreCase(type, "Integer") && !StringUtils.endsWithIgnoreCase(type, "Long")
					&& !StringUtils.endsWithIgnoreCase(type, "Float") && !StringUtils.endsWithIgnoreCase(type, "Double")
					&& !StringUtils.endsWithIgnoreCase(type, "JSON") && !StringUtils.endsWithIgnoreCase(type, "XML")
					&& !StringUtils.endsWithIgnoreCase(type, "Date")) {
				throw new BizException(
						"/joint/outputs/param[type]"
								+ "type只能有这些值：String，Boolean，Integer，Long，Float，Double，JSON，XML，Date，请检查",
						ResponseCode._501);
			}

			// required
			Attribute requiredAttr = outputParamNode.attribute("required");
			String required;
			if (requiredAttr == null || StringUtils.isEmpty(required = requiredAttr.getStringValue())) {
				required = "true";
			}
			// 注意required只能有两个值true或者false
			if (!StringUtils.endsWithIgnoreCase(required, "true")
					&& !StringUtils.endsWithIgnoreCase(required, "false")) {
				throw new BizException("/joint/outputs/param[required]必须是布尔值，请检查", ResponseCode._501);
			}
			required = required.toLowerCase();

			// default
			Attribute cdefaultAttr = outputParamNode.attribute("default");
			String cdefault;
			if (cdefaultAttr == null || StringUtils.isEmpty(cdefault = cdefaultAttr.getStringValue())) {
				cdefault = null;
			}

			JointOutputParamsEntity outputParamsEntity = new JointOutputParamsEntity();
			outputParamsEntity.setDefaultValue(cdefault);
			outputParamsEntity.setJoint(resultJoint);
			outputParamsEntity.setName(name);
			outputParamsEntity.setRequired(Boolean.parseBoolean(required));
			outputParamsEntity.setType(type);
			outputParamsSet.add(outputParamsEntity);
		}

		return outputParamsSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.translation.JointXMLTranslationService#
	 * translationJSON(java.lang.String)
	 */
	@Override
	public JSONObject translationJSON(String xmlText) throws BizException {
		/*
		 * 
		 * */
		if (StringUtils.isEmpty(xmlText)) {
			throw new BizException("xmlText参数必须传入，并且满足定义joint-xml定义规则", ResponseCode._404);
		}

		ByteArrayInputStream byteInput = new ByteArrayInputStream(xmlText.getBytes());
		JSONObject jsonObject = this.translationJSON(byteInput);
		// 这里一定要自己关闭
		try {
			byteInput.close();
		} catch (IOException e) {
			JointTranslationServiceImpl.LOGGER.error(e.getMessage(), e);
			throw new BizException(e.getMessage(), ResponseCode._501);
		}
		return jsonObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.translation.JointXMLTranslationService#
	 * translationJSON(java.io.InputStream)
	 */
	@Override
	public JSONObject translationJSON(InputStream xmlStream) throws BizException {
		/*
		 * 实现方式为：首先转成对象，然后对象直接转成JSONObject 同样的，这个方法内部并不负责关闭inputstream
		 */

		JointEntity jointEntity = this.translationEntity(xmlStream);
		return JSONUtils.toJSONObject(jointEntity, new String[] { "joint" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.translation.JointJSONTranslationService#
	 * translationEntity(net.sf.json.JSONObject)
	 */
	@Override
	public JointEntity translationEntity(JSONObject jsonObject) throws BizException {
		if (jsonObject == null) {
			throw new BizException("xmlStream参数必须传入，并且满足定义joint-xml 定义规则", ResponseCode._404);
		}

		/*
		 * 处理过程描述如下： 1、首先通过json取得joint的基本信息。如果出现异常则终止
		 * ================2015-08-26==============
		 * joint模板，对于时间线的属性和要就进行了补全，新增了offsetTitle、offsetVisible、expandTypeId属性
		 * 
		 * 2、取trades节点下所有子节点，取得所有行业信息（如果发现错误的定义则终止） 注意，如果没有找到对应的行业id，则报错
		 * 3、取inputs节点下所有子节点，取得所有的入参信息（入参信息可以什么都没有，但是若发现错误的定义，则终止）
		 * 注意，为了保证顺序，这里采用linkedHashSet进行存储 还要保证其input-name没有重复
		 * 4、取outputs节点下的所有子节点，取得所有的出参信息（出参信息可以什么都没有，但是若发现错误的定义，则终止）
		 * 注意，为了保证顺序，这里采用linkedHashSet进行存储 还要保证其output-name没有重复
		 */
		// 1、==============joint
		JSONObject jointObject = jsonObject.getJSONObject("joint");
		if (jointObject == null) {
			throw new BizException("没有发现json中有效的joint节点，请检查", ResponseCode._404);
		}
		JointEntity jointEntity = this.parseJointJSON2Entity(jointObject);

		// 2、==============trades
		JSONArray tradesArray = jointObject.has("trades") ? jointObject.getJSONArray("trades") : null;
		if (tradesArray == null || tradesArray.isEmpty()) {
			throw new BizException("没有发现json中有效的trades节点，请检查", ResponseCode._404);
		}
		Set<JointTradeMappingEntity> childTradesSet = this.parseTradessArray(jointEntity, tradesArray);
		jointEntity.setTrades(childTradesSet);

		// 3、==============inputs
		JSONArray inputsArray = jointObject.has("inputs") ? jointObject.getJSONArray("inputs") : null;
		if (inputsArray != null && !inputsArray.isEmpty()) {
			// 必须保证无重复input param name
			List<String> inputsNameList = new ArrayList<String>();

			for (int index = 0; inputsArray != null && index < inputsArray.size(); index++) {
				JSONObject inputObject = inputsArray.getJSONObject(index);
				String inputsName = inputObject.has("name") ? inputObject.getString("name") : null;

				if (StringUtils.isEmpty(inputsName)) {
					throw new BizException("inputs-param不能为空，请检查", ResponseCode._501);
				}

				// 说明inputsName有重复
				if (inputsNameList.contains(inputsName)) {
					throw new BizException("inputs-param：" + inputsName + "重复，请检查", ResponseCode._501);
				}
				inputsNameList.add(inputsName);
			}

			Set<JointInputParamsEntity> inputParamsSet = this.parseJointInputParamsJSON2Entity(jointEntity,
					inputsArray);
			jointEntity.setInputParams(inputParamsSet);
		}

		// 4、==============================
		JSONArray outputsArray = jointObject.has("outputs") ? jointObject.getJSONArray("outputs") : null;
		// 如果条件成立，才进行inputparams的解析；否则不进行
		if (outputsArray != null && !outputsArray.isEmpty()) {
			// 在计算前一定要保证name没有重复
			List<String> outputsNameList = new ArrayList<String>();

			for (int index = 0; outputsArray != null && index < outputsArray.size(); index++) {
				JSONObject outputObject = outputsArray.getJSONObject(index);
				String outputsName = outputObject.has("name") ? outputObject.getString("name") : null;

				if (StringUtils.isEmpty(outputsName)) {
					throw new BizException("outputs-name不能为空，请检查", ResponseCode._501);
				}

				// 说明outputs-name有重复
				if (outputsNameList.contains(outputsName)) {
					throw new BizException("outputs-name：" + outputsName + "重复，请检查", ResponseCode._501);
				}
				outputsNameList.add(outputsName);
			}

			Set<JointOutputParamsEntity> outputParamsSet = this.parseJointOutputParamsJSON2Entity(jointEntity,
					outputsArray);
			jointEntity.setOutputParams(outputParamsSet);
		}

		return jointEntity;
	}

	/**
	 * 该私有方法负责解析joint的基本信息
	 * 
	 * @param agreementObject
	 * @return
	 * @throws BizException
	 */
	private JointEntity parseJointJSON2Entity(JSONObject jointObject) throws BizException {
		// id
		String id = null;
		// 如果没有id，则自己生成一个
		if (!jointObject.has("id") || StringUtils.isEmpty(id = jointObject.getString("id"))) {
			id = UUID.randomUUID().toString();
		}
		// displayName
		String displayName = null;
		if (!jointObject.has("displayName")
				|| StringUtils.isEmpty(displayName = jointObject.getString("displayName"))) {
			throw new BizException("joint-displayName必须设置，请检查", ResponseCode._404);
		}
		// camelUri
		String camelUri = null;
		if (!jointObject.has("camelUri") || StringUtils.isEmpty(camelUri = jointObject.getString("camelUri"))) {
			camelUri = null;
		}
		// executor
		String executor = null;
		if (!jointObject.has("executor") || StringUtils.isEmpty(executor = jointObject.getString("executor"))) {
			executor = null;
		}
		// 执行者只有三个范围
		else if (!StringUtils.equals(executor, "industry") && !StringUtils.equals(executor, "producer")
				&& !StringUtils.equals(executor, "consumer")) {
			throw new BizException("joint-executor只能指定三种值（industry | producer | consumer），请检查", ResponseCode._404);
		}
		// absoffsettime
		String absoffsettime = null;
		if (!jointObject.has("absoffsettime")
				|| StringUtils.isEmpty(absoffsettime = jointObject.getString("absoffsettime"))) {
			absoffsettime = null;
		}
		// absoffsettime只能是自然数（等于null当然就不做判断咯）
		Pattern pattern = Pattern.compile("^[0-9]*$");
		if (absoffsettime != null && !pattern.matcher(absoffsettime).find()) {
			throw new BizException("joint-absoffsettime只能设置自然数，请检查", ResponseCode._501);
		}
		// relateoffsettime
		String relateoffsettime = null;
		if (!jointObject.has("relateoffsettime")
				|| StringUtils.isEmpty(relateoffsettime = jointObject.getString("relateoffsettime"))) {
			relateoffsettime = null;
		}
		// relateoffsettime只能是自然数（等于null当然就不做判断咯）
		if (relateoffsettime != null && !pattern.matcher(relateoffsettime).find()) {
			throw new BizException("joint-relateoffsettime只能设置自然数，请检查", ResponseCode._501);
		}
		// offsetTitle
		String offsetTitle = null;
		if (!jointObject.has("offsetTitle")
				|| StringUtils.isEmpty(offsetTitle = jointObject.getString("offsetTitle"))) {
			offsetTitle = "";
		}
		// offsetVisible
		String offsetVisible = null;
		if (!jointObject.has("offsetVisible")
				|| StringUtils.isEmpty(offsetVisible = jointObject.getString("offsetVisible"))) {
			offsetVisible = "both";
		}
		// offsetVisible只能有producer | consumer | both 三个值
		if (offsetVisible != null && !StringUtils.equals(offsetVisible, "producer")
				&& !StringUtils.equals(offsetVisible, "consumer") && !StringUtils.equals(offsetVisible, "both")) {
			throw new BizException("joint-offsetVisible只能设置三种值：producer | consumer | both，请检查", ResponseCode._501);
		}
		// expandTypeId
		String expandTypeId;
		if (!jointObject.has("expandTypeId")
				|| StringUtils.isEmpty(expandTypeId = jointObject.getString("expandTypeId"))) {
			expandTypeId = null;
		}
		// expandTypeId只能是数字
		pattern = Pattern.compile("^[0-9]*$");
		if (expandTypeId != null && !pattern.matcher(expandTypeId).find()) {
			throw new BizException("joint-expandTypeId只能设置数字，请检查", ResponseCode._501);
		}

		// promptOffsettime(任务提示)
		String promptOffsettime = null;
		if (!jointObject.has("promptOffsettime")
				|| StringUtils.isEmpty(promptOffsettime = jointObject.getString("promptOffsettime"))) {
			promptOffsettime = null;
		}

		// 开始赋值
		JointEntity jointEntity = new JointEntity();
		jointEntity.setDisplayName(displayName);
		jointEntity.setExecutor(executor);
		jointEntity.setUid(id);
		jointEntity.setCamelUri(camelUri);

		jointEntity.setAbsOffsettime(absoffsettime == null ? null : Long.parseLong(absoffsettime));
		jointEntity.setRelateOffsettime(relateoffsettime == null ? null : Long.parseLong(relateoffsettime));
		jointEntity.setPromptOffsettime(promptOffsettime);
		jointEntity.setOffsetTitle(offsetTitle);
		jointEntity.setOffsetVisible(offsetVisible);
		jointEntity.setExpandTypeId(expandTypeId == null ? null : Integer.parseInt(expandTypeId));
		return jointEntity;
	}

	/**
	 * @param jointEntity
	 * @param tradesArray
	 * @return
	 * @throws BizException
	 */
	private Set<JointTradeMappingEntity> parseTradessArray(JointEntity jointEntity, JSONArray tradesArray)
			throws BizException {
		Set<JointTradeMappingEntity> childTradesSet = new LinkedHashSet<JointTradeMappingEntity>();

		for (int index = 0; tradesArray != null && index < tradesArray.size(); index++) {
			JointTradeMappingEntity tradeMapping = new JointTradeMappingEntity();

			JSONObject jointJson = tradesArray.getJSONObject(index);
			// tradeid
			String tradeid = null;
			if (!jointJson.has("tradeid") || StringUtils.isEmpty(tradeid = jointJson.getString("tradeid"))) {
				throw new BizException("trade-id必须设置，请检查", ResponseCode._404);
			}

			// tradescope
			String tradescope = null;
			if (!jointJson.has("tradescope") || StringUtils.isEmpty(tradescope = jointJson.getString("tradescope"))) {
				throw new BizException("trade-scope必须设置，请检查", ResponseCode._404);
			}
			// tradescope只能有三种值
			if (!StringUtils.equals(tradescope, "industry") && !StringUtils.equals(tradescope, "producer")
					&& !StringUtils.equals(tradescope, "consumer")) {
				throw new BizException("trade-scope只能指定三种值（industry | producer | consumer），请检查", ResponseCode._404);
			}

			tradeMapping.setJoint(jointEntity);
			tradeMapping.setScope(tradescope);
			tradeMapping.setTradeid(tradeid);
			tradeMapping.setUid(UUID.randomUUID().toString());
			childTradesSet.add(tradeMapping);
		}

		return childTradesSet;
	}

	/**
	 * @param jointEntity
	 * @param inputsArray
	 * @return
	 * @throws BizException
	 */
	private Set<JointInputParamsEntity> parseJointInputParamsJSON2Entity(JointEntity jointEntity, JSONArray inputsArray)
			throws BizException {
		Set<JointInputParamsEntity> inputParamsSet = new LinkedHashSet<JointInputParamsEntity>();

		for (int index = 0; index < inputsArray.size(); index++) {
			JSONObject inputParamObject = inputsArray.getJSONObject(index);

			// name
			String name = null;
			if (!inputParamObject.has("name") || StringUtils.isEmpty(name = inputParamObject.getString("name"))) {
				throw new BizException("inputs-name必须设置，请检查", ResponseCode._501);
			}

			// type
			String type = null;
			if (!inputParamObject.has("type") || StringUtils.isEmpty(type = inputParamObject.getString("type"))) {
				throw new BizException("inputs-type必须设置，请检查", ResponseCode._501);
			}
			// type只能有这些值：String，Boolean，Integer，Long，Float，Double，JSON，XML，Date
			if (!StringUtils.endsWithIgnoreCase(type, "String") && !StringUtils.endsWithIgnoreCase(type, "Boolean")
					&& !StringUtils.endsWithIgnoreCase(type, "Integer") && !StringUtils.endsWithIgnoreCase(type, "Long")
					&& !StringUtils.endsWithIgnoreCase(type, "Float") && !StringUtils.endsWithIgnoreCase(type, "Double")
					&& !StringUtils.endsWithIgnoreCase(type, "JSON") && !StringUtils.endsWithIgnoreCase(type, "XML")
					&& !StringUtils.endsWithIgnoreCase(type, "Date")) {
				throw new BizException(
						"inputs-type" + "只能有这些值：String，Boolean，Integer，Long，Float，Double，JSON，XML，Date，请检查",
						ResponseCode._501);
			}

			// displayName
			String displayName;
			if (!inputParamObject.has("displayName")
					|| StringUtils.isEmpty(displayName = inputParamObject.getString("displayName"))) {
				displayName = "";
			}

			// dispalyType
			String dispalyType;
			if (!inputParamObject.has("dispalyType")
					|| StringUtils.isEmpty(dispalyType = inputParamObject.getString("dispalyType"))) {
				dispalyType = "";
			}

			// required
			String required;
			if (!inputParamObject.has("required")
					|| StringUtils.isEmpty(required = inputParamObject.getString("required"))) {
				required = "true";
			}
			// 注意required只能有两个值true或者false
			if (!StringUtils.endsWithIgnoreCase(required, "true")
					&& !StringUtils.endsWithIgnoreCase(required, "false")) {
				throw new BizException("inputs-required必须是布尔值，请检查", ResponseCode._501);
			}
			required = required.toLowerCase();

			// default
			String cddefault;
			if (!inputParamObject.has("default")
					|| StringUtils.isEmpty(cddefault = inputParamObject.getString("default"))) {
				cddefault = null;
			}

			JointInputParamsEntity inputParamsEntity = new JointInputParamsEntity();
			inputParamsEntity.setDefaultValue(cddefault);
			inputParamsEntity.setDisplayName(displayName);
			inputParamsEntity.setDisplayType(dispalyType);
			inputParamsEntity.setJoint(jointEntity);
			inputParamsEntity.setName(name);
			inputParamsEntity.setRequired(Boolean.valueOf(required));
			inputParamsEntity.setType(dispalyType);
			inputParamsEntity.setUid(UUID.randomUUID().toString());
			inputParamsSet.add(inputParamsEntity);
		}

		return inputParamsSet;
	}

	/**
	 * @param jointEntity
	 * @param outputsArray
	 * @return
	 * @throws BizException
	 */
	private Set<JointOutputParamsEntity> parseJointOutputParamsJSON2Entity(JointEntity jointEntity,
			JSONArray outputsArray) throws BizException {
		Set<JointOutputParamsEntity> outputParamsSet = new LinkedHashSet<JointOutputParamsEntity>();

		for (int index = 0; index < outputsArray.size(); index++) {
			JSONObject outputParamObject = outputsArray.getJSONObject(index);

			// name
			String name = null;
			if (!outputParamObject.has("name") || StringUtils.isEmpty(name = outputParamObject.getString("name"))) {
				throw new BizException("outputs-name必须设置，请检查", ResponseCode._501);
			}

			// type
			String type = null;
			if (!outputParamObject.has("type") || StringUtils.isEmpty(type = outputParamObject.getString("type"))) {
				throw new BizException("outputs-type必须设置，请检查", ResponseCode._501);
			}
			// type只能有这些值：String，Boolean，Integer，Long，Float，Double，JSON，XML，Date
			if (!StringUtils.endsWithIgnoreCase(type, "String") && !StringUtils.endsWithIgnoreCase(type, "Boolean")
					&& !StringUtils.endsWithIgnoreCase(type, "Integer") && !StringUtils.endsWithIgnoreCase(type, "Long")
					&& !StringUtils.endsWithIgnoreCase(type, "Float") && !StringUtils.endsWithIgnoreCase(type, "Double")
					&& !StringUtils.endsWithIgnoreCase(type, "JSON") && !StringUtils.endsWithIgnoreCase(type, "XML")
					&& !StringUtils.endsWithIgnoreCase(type, "Date")) {
				throw new BizException(
						"outputs-type" + "只能有这些值：String，Boolean，Integer，Long，Float，Double，JSON，XML，Date，请检查",
						ResponseCode._501);
			}

			// required
			String required;
			if (!outputParamObject.has("required")
					|| StringUtils.isEmpty(required = outputParamObject.getString("required"))) {
				required = "true";
			}
			// 注意required只能有两个值true或者false
			if (!StringUtils.endsWithIgnoreCase(required, "true")
					&& !StringUtils.endsWithIgnoreCase(required, "false")) {
				throw new BizException("outputs-required必须是布尔值，请检查", ResponseCode._501);
			}
			required = required.toLowerCase();

			// default
			String cddefault;
			if (!outputParamObject.has("default")
					|| StringUtils.isEmpty(cddefault = outputParamObject.getString("default"))) {
				cddefault = null;
			}

			JointOutputParamsEntity outputParamsEntity = new JointOutputParamsEntity();
			outputParamsEntity.setDefaultValue(cddefault);
			outputParamsEntity.setName(name);
			outputParamsEntity.setType(type);
			outputParamsEntity.setJoint(jointEntity);
			outputParamsEntity.setRequired(Boolean.parseBoolean(required));
			outputParamsEntity.setUid(UUID.randomUUID().toString());
			outputParamsSet.add(outputParamsEntity);
		}

		return outputParamsSet;
	}
}
