package com.ai.sboss.arrangement.engine;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.dao.ArrangementDAOAbstractFactory;
import com.ai.sboss.arrangement.engine.dao.ArrangementDAOService;
import com.ai.sboss.arrangement.engine.dao.InstanceDAOService;
import com.ai.sboss.arrangement.engine.dao.JointDAOService;
import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementJointMappingEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceFlowEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;
import com.ai.sboss.arrangement.translation.ArrangementInstanceTranslationService;
import com.ai.sboss.arrangement.translation.TranslationAbstractFactory;

import net.sf.json.JSONObject;

/**
 * 编排系统中，流程引擎实现 流程实例启动服务的唯一实现类
 * 
 * @author yinwenjie
 */
@Component("_processorEngine_startupControlServiceImpl")
public class StartupControlServiceImpl extends AbstractCommadQueueManager implements IStartupControlService {
	private final static Log LOGGER = LogFactory.getLog(StartupControlServiceImpl.class);

	/**
	 * 发送实例初始化“前”事件的命令
	 */
	@Autowired
	@Qualifier("_processorEngine_beginEventCommand")
	private IBeginEventCommand beginEventCommand;

	/**
	 * 发送实例初始化“后”事件的命令
	 */
	@Autowired
	@Qualifier("_processorEngine_afterEventCommand")
	private ICommand afterEventCommand;

	/**
	 * 进行实例初始化的主命令
	 */
	@Autowired
	@Qualifier("_processorEngine_instanceCommand")
	private IInstanceCommand instanceCommand;

	@Autowired
	private TranslationAbstractFactory translationFactory;

	@Autowired
	private ArrangementDAOAbstractFactory arrangementDAOFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.IStartupControlService#
	 * startArrangementInstance(net.sf.json.JSONObject)
	 */
	@Override
	public JSONObject startArrangementInstance(JSONObject arrangementInstanceJSON) throws BizException {
		/*
		 * 启动一个流程，启动一个流程要做以下几步： 1、翻译：调用者给过来的是一个json/xml格式的启动请求，
		 * 里面至少包括了，要启动的流程模板的ID，业务系统绑定的编号business
		 * 还可能包括，某个任务模板新设置的绝对偏移事件、相对偏移时间、新的执行者角色、新的执行者编号
		 * 
		 * 要把以上的这些信息，翻译成实例。这个工作交给“ArrangementInstanceTranslationService”
		 * 
		 * 2、翻译工作只负责翻译，但是并不负责信息完善和验证：
		 * 什么意思呢?请求信息的JSON/XML格式中，只有各种id信息和补充信息，但是并不包括基本信息。
		 * 例如：arrangementInstance对应的arrangement的名称、包含的全部任务、这些都没有
		 * 再例如：翻译得到的arrangementInstance中可能没有任何jointinstances的信息，但实际上这个模板对应的信息是有“
		 * 手动执行”节点的。
		 * 
		 * 所以需要使用数据层的ArrangementDAOService服务完善这些基本信息：
		 * arrangement的基本信息、关联的全部任务等。并且，还需要验证这些信息的正确性。
		 * 
		 * 3、确定任务执行顺序： 在arrangement的flow属性中，记录了这个流程模板的所有任务某班的执行先后顺序。
		 * 通过这个顺序建立JointInstanceFlowEntity。并验证执行过程是否正确。
		 * 注意，出参入参的流程实例上下文，都需要在流程启动时进行初始化（只是不需要有值而已）,还要根据时间线计算可能的任务执行时间
		 * 
		 * =========================================
		 * 通过以上3个大的步骤，我们所需的流程启动前的数据准备工作就完成了 接下来让我们调用命令，启动流程
		 * =========================================
		 * 
		 * 4、调用IInstanceCommand启动流程，实际上里面一般都是数据层的写入操作
		 * 
		 * 5、调用IBeginEventCommand发送流程启动前的事件消息，到事件队列中
		 * 
		 * 6、整个操作过程完成，返回创建的流程实例（包括其中的任务实例）给调用方
		 */

		// 1.翻译
		ArrangementInstanceTranslationService arrangementInstanceTranslationService = this.translationFactory
				.getArrangementInstanceTranslationService();
		ArrangementInstanceEntity startArrangementIntance = arrangementInstanceTranslationService
				.translationEntity(arrangementInstanceJSON);

		// 2.完善
		startArrangementIntance = informationCompletion(startArrangementIntance);
		// TODO:对比流程模版对应的任务模版校验任务实例的正确性
		if (!validArrangementInstanceInformation(startArrangementIntance)) {
			StartupControlServiceImpl.LOGGER.error("输入流程实例信息错误。");
			return null;
		}

		// 3.确定任务执行顺序
		// 解析flows
		List<Map.Entry<String, String>> flowIdList = arrangementInstanceTranslationService
				.translationFlows(startArrangementIntance.getArrangement().getFlows());
		if (flowIdList == null) {
			throw new BizException("流程转移错误，请检查！", ResponseCode._501);
		}
		List<Map.Entry<String, Object>> instanceNodes = getFlowInstanceNodeByUID(startArrangementIntance, flowIdList);
		List<JointInstanceFlowEntity> jointInstanceFlowEntities = new ArrayList<JointInstanceFlowEntity>();
		// ArrangementInstanceEntity lastArrangementInstance = null;
		for (int i = 0; i < instanceNodes.size(); ++i) {
			JointInstanceFlowEntity jointInstanceFlowEntity = new JointInstanceFlowEntity();

			if (StringUtils.equals(instanceNodes.get(i).getKey(), "arrangement")) {
				// TODO: Not Implemented
				throw new BizException("Not Implemented", ResponseCode._501);
			}
			if (StringUtils.equals(instanceNodes.get(i).getKey(), "joint")) {
				JointInstanceEntity jointInstance = (JointInstanceEntity) instanceNodes.get(i).getValue();
				jointInstanceFlowEntity.setJointInstance(jointInstance);
				jointInstanceFlowEntity.setJoint(jointInstance.getJoint());
				jointInstanceFlowEntity.setExecutor(jointInstance.getExecutor());
				jointInstanceFlowEntity.setExpectedExeTime(jointInstance.getExpectedExeTime());
				jointInstanceFlowEntity.setStatu(jointInstance.getStatu());
				jointInstanceFlowEntity.setPreviouJointInstance(null);
				jointInstanceFlowEntity.setPreviouArrangementInstance(null);
				jointInstanceFlowEntity.setNextJointInstance(null);
				jointInstanceFlowEntity.setNextArrangementInstance(null);
				if (i != 0 && StringUtils.equals(instanceNodes.get(i - 1).getKey(), "joint")) {
					jointInstanceFlowEntity.setPreviouJointInstance((JointInstanceEntity) instanceNodes.get(i - 1).getValue());
				} else if (i != 0 && StringUtils.equals(instanceNodes.get(i - 1).getKey(), "arragement")) {
					jointInstanceFlowEntity.setPreviouArrangementInstance((ArrangementInstanceEntity) instanceNodes.get(i - 1).getValue());
				}
				if (i != (instanceNodes.size() - 1) && StringUtils.equals(instanceNodes.get(i + 1).getKey(), "joint")) {
					jointInstanceFlowEntity.setNextJointInstance((JointInstanceEntity) instanceNodes.get(i + 1).getValue());
				} else if (i != (instanceNodes.size() - 1) && StringUtils.equals(instanceNodes.get(i + 1).getKey(), "arragement")) {
					jointInstanceFlowEntity.setNextArrangementInstance((ArrangementInstanceEntity) instanceNodes.get(i + 1).getValue());
				}
				jointInstanceFlowEntities.add(jointInstanceFlowEntity);
			}
		}

		// 4、调用IInstanceCommand启动流程，实际上里面一般都是数据层的写入操作
		this.instanceCommand.init(startArrangementIntance, jointInstanceFlowEntities);
		// 5、调用IBeginEventCommand发送流程启动前的事件消息，到事件队列中
		this.beginEventCommand.init(startArrangementIntance.getUid());

		// 准备命令
		this.initCommandsQueue(this.instanceCommand, this.beginEventCommand);
		ICommand nowCommand = null;
		Boolean success = true;
		while ((nowCommand = this.nextCommand()) != null) {
			try {
				nowCommand.execute();
			} catch (BizException be) {
				StartupControlServiceImpl.LOGGER.warn(be.getMessage(), be);
				success = false;
				break;
			}
		}

		// 6、整个操作过程完成，返回创建的流程实例（包括其中的任务实例）给调用方
		String arrangementInstanceId = null;
		if (success) {
			arrangementInstanceId = startArrangementIntance.getUid();
		}
		InstanceDAOService instanceDAOService = this.arrangementDAOFactory.getInstanceDAOService();
		return instanceDAOService.queryArrangementInstanceByArrangementInstanceID(arrangementInstanceId);

	}

	@SuppressWarnings("unused")
	private List<Map.Entry<String, Object>> getFlowInstanceNodeByUID(ArrangementInstanceEntity arrangementIntance,
			List<Map.Entry<String, String>> flowIdNodes) {
		if (arrangementIntance == null || flowIdNodes == null) {
			return null;
		}
		List<Map.Entry<String, Object>> retList = new ArrayList<Map.Entry<String, Object>>();
		Set<ArrangementInstanceEntity> childArrangementInstances = arrangementIntance.getChildArrangementInstances();
		Set<JointInstanceEntity> jointInstances = arrangementIntance.getJointInstances();
		// TODO:这个查找逻辑考虑以后放到数据库中。
		for (int i = 0; i < flowIdNodes.size(); ++i) {
			// TODO:这里暂时不考虑子流程
			if (childArrangementInstances != null) {
				for (ArrangementInstanceEntity arrnagementInstance : childArrangementInstances) {
					if (StringUtils.equals(arrangementIntance.getArrangement().getUid(), flowIdNodes.get(i).getKey())
							&& StringUtils.isEmpty(arrangementIntance.getUid())) {
						arrangementIntance.setUid(flowIdNodes.get(i).getValue());
						retList.add(new AbstractMap.SimpleEntry<String, Object>("arragement", arrangementIntance));
					}
				}
			}
			if (jointInstances != null) {
				for (JointInstanceEntity jointInstance : jointInstances) {
					if (StringUtils.equals(jointInstance.getJoint().getUid(), flowIdNodes.get(i).getKey())
							&& StringUtils.isEmpty(jointInstance.getUid())) {
						jointInstance.setUid(flowIdNodes.get(i).getValue());
						retList.add(new AbstractMap.SimpleEntry<String, Object>("joint", jointInstance));
					}
				}
			}
		}
		return retList;
	}

	private boolean validArrangementInstanceInformation(ArrangementInstanceEntity arrangementIntance) throws BizException {
		boolean ret = true;
		Set<JointInstanceEntity> jointInstances = arrangementIntance.getJointInstances();
		JointDAOService jointDAOService = arrangementDAOFactory.getJointDAOService();
		// TODO:这个正则表达式还需要修改
		Pattern pattern = Pattern.compile("^((https|http)?://)+([0-9a-z_!~*\'()-]+.)*");
		for (JointInstanceEntity jointInstance : jointInstances) {
			if (StringUtils.isEmpty(jointInstance.getCreator())) {
				String camelUri = jointInstance.getCamelUri();
				if (StringUtils.isEmpty(camelUri) || !pattern.matcher(camelUri).find()) {
					StartupControlServiceImpl.LOGGER.error("camel任务链接未设置，或设置错误。");
					ret = false;
					break;
				}
			}
			// 对比模版是否应该传入执行者的任务被误传为自动流程了
			if (StringUtils.isEmpty(jointInstance.getExecutor())) {
				JointEntity jointTemplate = jointInstance.getJoint();
				jointTemplate = jointDAOService.getJointWithoutParams(jointTemplate.getUid());
				if (StringUtils.isEmpty(jointTemplate.getExecutor())) {
					StartupControlServiceImpl.LOGGER.error("非自动流程必须设置执行者。");
					ret = false;
					break;
				}
			}
		}
		// businessId没法检测，在转换的时候已经进行了安全校验
		return ret;
	}

	private ArrangementInstanceEntity informationCompletion(ArrangementInstanceEntity arrangementIntance) throws BizException {
		ArrangementDAOService arrangementDAOService = this.arrangementDAOFactory.getArrangementDAOService();
		// 获取真实的ArrangementEntity对象信息
		ArrangementEntity arrangementTemplate = arrangementIntance.getArrangement();
		arrangementTemplate = arrangementDAOService.getArrangementWithSet(arrangementTemplate.getUid());
		arrangementIntance.setStatu("waiting");
		arrangementIntance.setUid(UUID.randomUUID().toString());
		arrangementIntance.setArrangement(arrangementTemplate);
		if (StringUtils.isEmpty(arrangementIntance.getDisplayName())) {
			arrangementIntance.setDisplayName(arrangementTemplate.getDisplayName());
		}
		// 这里假设入参中必定带有jointInstance列表（因为每个任务实例需要执行者 TODO:疑问？如果是自动流程呢？）
		Set<ArrangementJointMappingEntity> jointMappings = arrangementTemplate.getJointmapping();
		Map<String, ArrangementJointMappingEntity> listMappings = new LinkedHashMap<String, ArrangementJointMappingEntity>();
		for (ArrangementJointMappingEntity jointMapping : jointMappings) {
			listMappings.put(jointMapping.getJoint().getUid(), jointMapping);
		}
		Set<JointInstanceEntity> jointInstances = arrangementIntance.getJointInstances();
		if (jointInstances == null) {
			LOGGER.error("流程实例中未包含任何有效的任务");
			throw new BizException("流程实例中未包含任何有效的任务", ResponseCode._401);
		}
		Set<JointInstanceEntity> totalJointInstances = new LinkedHashSet<JointInstanceEntity>();
		for (JointInstanceEntity jointInstance : jointInstances) {
			String jointuid = jointInstance.getJoint().getUid();
			ArrangementJointMappingEntity currentJointMapping = null;
			if (listMappings.containsKey(jointuid)) {
				currentJointMapping = listMappings.get(jointuid);
				listMappings.remove(jointuid);
			}
			jointInstance = autoFillJointInstance(jointInstance, jointuid, currentJointMapping, arrangementIntance);
			totalJointInstances.add(jointInstance);
		}
		// 补充自动任务（没有输入creator的任务）
		for (Map.Entry<String, ArrangementJointMappingEntity> jointMappingEntry : listMappings.entrySet()) {
			JointInstanceEntity jointInstance = autoFillJointInstance(null, jointMappingEntry.getKey(), jointMappingEntry.getValue(), arrangementIntance);
			totalJointInstances.add(jointInstance);
		}
		arrangementIntance.setJointInstances(totalJointInstances);
		return arrangementIntance;
	}
	//补全jointInstance信息
	private JointInstanceEntity autoFillJointInstance(JointInstanceEntity jointInstance, String jointuid,
			ArrangementJointMappingEntity jointMapping, ArrangementInstanceEntity arrangementIntance) throws BizException {
		if (jointInstance == null) {
			jointInstance = new JointInstanceEntity();
			//TODO:需要补充部分translate中的值
			jointInstance.setArrangementInstance(arrangementIntance);
			jointInstance.setCreator(arrangementIntance.getCreator());
			JointEntity joint = new JointEntity();
			joint.setUid(jointuid);
			jointInstance.setJoint(joint);
			jointInstance.setUid(null);
		}
		JointDAOService jointDAOService = this.arrangementDAOFactory.getJointDAOService();
		JointEntity jointEntityTemplate = jointDAOService.getJointWithParams(jointuid);
		jointInstance.setWeight(jointMapping.getWeight());
		jointInstance.setCamelUri(jointEntityTemplate.getCamelUri());
		if (jointInstance.getAbsOffsettime() == null) {
			jointInstance.setAbsOffsettime(jointMapping.getAbsOffsettime());
		}
		jointInstance.setExpectedExeTime(arrangementIntance.getCreateTime() + jointMapping.getAbsOffsettime());
		jointInstance.setStatu("waiting");
		if (jointInstance.getRelateOffsettime() == null) {
			jointInstance.setRelateOffsettime(jointMapping.getRelateOffsettime());
		}
		if (jointInstance.getPromptOffsettime() == null) {
			jointInstance.setPromptOffsettime(jointEntityTemplate.getPromptOffsettime());
		}
		if (StringUtils.isEmpty(jointInstance.getOffsetTitle())) {
			jointInstance.setOffsetTitle(jointEntityTemplate.getOffsetTitle());
		}
		if (StringUtils.isEmpty(jointInstance.getOffsetVisible())) {
			jointInstance.setOffsetVisible(jointEntityTemplate.getOffsetVisible());
		}
		return jointInstance;
	}
}
