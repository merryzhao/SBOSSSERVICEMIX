package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ai.sboss.arrangement.engine.dao.InstanceDAOService;
import com.ai.sboss.arrangement.entity.PageEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.InstanceContextParamEntity;
import com.ai.sboss.arrangement.entity.orm.InstanceContextParamLogEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointInputParamsEntity;
import com.ai.sboss.arrangement.entity.orm.JointInputParamsInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceFlowEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceFlowLogEntity;
import com.ai.sboss.arrangement.entity.orm.JointOutputParamsEntity;
import com.ai.sboss.arrangement.entity.orm.JointOutputParamsInstanceEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;
import com.ai.sboss.arrangement.utils.JSONUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 持久层InstanceDAOService的一个mysql关系型数据库门面的实现
 * 
 * @author yinwenjie
 */
@Transactional
@Component("instanceDAOServiceImpl")
public class InstanceDAOServiceImpl implements InstanceDAOService {

	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(InstanceDAOServiceImpl.class);

	@Autowired
	private IJointInstanceDAO jointInstanceDAO;

	@Autowired
	private IArrangementInstanceDAO arrangementInstanceDAO;

	@Autowired
	private IInstanceContextParamDAO instanceContextParamDAO;

	@Autowired
	private IInstanceContextParamLogDAO instanceContextParamLogDAO;

	@Autowired
	private IJointInstanceFlowDAO jointInstanceFlowDAO;

	@Autowired
	private IJointInstanceFlowLogDAO jointInstanceFlowLogDAO;

	@Autowired
	private IJointInputParamsInstanceDAO jointInputParamsInstanceDAO;

	@Autowired
	private IJointOutputParamsInstanceDAO jointOutputParamsInstanceDAO;

	@Autowired
	private IJointInputParamsDAO jointInputParamsDAO;

	@Autowired
	private IJointOutputParamsDAO jointOutputParamsDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.InstanceDAOService#
	 * queryArrangementInstanceByBusinessID(java.lang.String)
	 */
	@Override
	public JSONObject queryArrangementInstanceByBusinessID(String businessid) throws BizException {
		ArrangementInstanceEntity arrangementInstance = this.arrangementInstanceDAO
				.queryArrangementInstancesByBusinessID(businessid);
		if (arrangementInstance == null) {
			return new JSONObject();
		}

		// 为了避免事务异常，这里直接转为json
		JSONObject resultData = JSONUtils.toJSONObject(arrangementInstance,
				new String[] { "arrangement", "parentInstance", "jointInstances", "childArrangementInstances" });
		// arrangementuid的信息还是要有的
		resultData.put("arrangementuid", arrangementInstance.getArrangement().getUid());
		return resultData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.InstanceDAOService#
	 * queryArrangementInstanceByInstanceID(java.lang.String)
	 */
	@Override
	@Transactional("transactionManager")
	public JSONObject queryArrangementInstanceByArrangementInstanceID(String arrangementInstanceuid)
			throws BizException {
		ArrangementInstanceEntity arrangementInstance = this.arrangementInstanceDAO
				.queryArrangementInstancesByID(arrangementInstanceuid);
		if (arrangementInstance == null) {
			return new JSONObject();
		}

		// 为了避免事务异常，这里直接转为json
		JSONObject resultData = JSONUtils.toJSONObject(arrangementInstance,
				new String[] { "arrangement", "parentInstance", "jointInstances", "childArrangementInstances" });
		// arrangementuid的信息还是要有的
		resultData.put("arrangementuid", arrangementInstance.getArrangement().getUid());
		return resultData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.InstanceDAOService#
	 * queryJointInstancesByUserid(java.lang.String, java.lang.Integer,
	 * java.lang.Integer)
	 */
	@Override
	@Transactional("transactionManager")
	public JSONObject queryJointInstancesByUserid(String userid, Integer nowPage, Integer maxPageRows)
			throws BizException {
		/*
		 * 查询出来的信息，是以PageEntity对象包装，并且里面是以
		 * UUID,ABSOFFSETTIME,CAMELURI,EXECUTOR,PROMPTOFFSETTIME,
		 * RELATEOFFSETTIME,ARRANGEMENTINST,JOINT,CREATOR,EXPANDTYPEID,
		 * OFFSETTITLE,OFFSETVISIBLE
		 * 为顺序的List<Object[]>对象，我们要将这个list中的每一个Object[]转换成对象，
		 * 并计算出current_time_stamp（这条jointinstance对应的arrangement对应的creator +
		 * jointinstance的绝对偏移量：absOffsettime）
		 * 
		 * 最终转换为JSONObject并返回。这个过程比较麻烦，总的来说由以下过程构成
		 * 
		 * 1、查询得到指定的人员所创建、所参与的所有任务实例信息（带分页）。
		 * 
		 * 2、循环每一条记录
		 * 
		 * 3、计算current_time_stamp
		 * 
		 * 4、完成全部的对象转换后，注意这里还是PageEntity对象，最后形成JSONObject。
		 * 注意：jointInstance对应的ArrangementInstanceEntity不需要返回
		 */
		if (StringUtils.isEmpty(userid)) {
			throw new BizException("userid 必须作为查询条件传入", ResponseCode._403);
		}

		// 1、=========================
		PageEntity pageResult = this.jointInstanceDAO.queryJointInstancesByUserid(userid, nowPage, maxPageRows);
		// 如果条件成立，说明没有查询到任何信息，直接返回就行了
		List<Object[]> dataObjectsList = pageResult.getResultsByObject();
		if (pageResult == null || dataObjectsList == null || dataObjectsList.isEmpty()) {
			JSONObject translateJSONObject = new JSONObject();

			translateJSONObject.put("nowPage", nowPage);
			translateJSONObject.put("maxPageRows", maxPageRows);
			translateJSONObject.put("results", new JSONArray());
			return translateJSONObject;
		}

		// 转换后的值
		JSONObject translateJSONObject = new JSONObject();
		translateJSONObject.put("maxPageRows", pageResult.getMaxPageRows());
		translateJSONObject.put("nowPage", pageResult.getNowPage());
		// 2、=========================
		JSONArray resultDataArray = new JSONArray();
		for (Object[] columnsObjects : dataObjectsList) {
			JointInstanceEntity jointInstanceEntity = new JointInstanceEntity();

			jointInstanceEntity.setUid(columnsObjects[0].toString());
			jointInstanceEntity
					.setAbsOffsettime(columnsObjects[1] != null ? Long.parseLong(columnsObjects[1].toString()) : null);
			jointInstanceEntity.setCamelUri(columnsObjects[2] != null ? columnsObjects[2].toString() : null);
			jointInstanceEntity.setExecutor(columnsObjects[3] != null ? columnsObjects[3].toString() : null);
			jointInstanceEntity.setPromptOffsettime(columnsObjects[4] != null ? columnsObjects[4].toString() : null);
			jointInstanceEntity.setRelateOffsettime(
					columnsObjects[5] != null ? Long.parseLong(columnsObjects[5].toString()) : null);
			// arrangementInstance（只存储id），注意：hibernate使用了缓存，所以uid相同的arrangementInstance只会被查询一次
			ArrangementInstanceEntity arrangementInstance = this.arrangementInstanceDAO
					.getEntity(columnsObjects[6].toString());
			arrangementInstance.setUid(columnsObjects[6].toString());
			jointInstanceEntity.setArrangementInstance(arrangementInstance);
			// joint(只存储id)
			JointEntity joint = new JointEntity();
			joint.setUid(columnsObjects[7].toString());
			jointInstanceEntity.setJoint(joint);
			// creator
			jointInstanceEntity.setCreator(columnsObjects[8].toString());
			jointInstanceEntity
					.setExpandTypeId(columnsObjects[9] != null ? Integer.parseInt(columnsObjects[9].toString()) : null);
			jointInstanceEntity.setOffsetTitle(columnsObjects[10] != null ? columnsObjects[10].toString() : null);
			jointInstanceEntity.setOffsetVisible(columnsObjects[11] != null ? columnsObjects[11].toString() : null);

			// 3、=========================开始计算current_time_stamp
			// //TODO:这里的时间线运算逻辑考虑以后放置出去
			Long createTimeValue = arrangementInstance.getCreateTime();
			Long absOffsettime = jointInstanceEntity.getAbsOffsettime() == null ? 0l
					: jointInstanceEntity.getAbsOffsettime();
			Long currentTimeStamp = createTimeValue + absOffsettime;

			// 4、=========================形成JSONObject
			JSONObject resultData = JSONUtils.toJSONObject(jointInstanceEntity,
					new String[] { "joint", "arrangementInstance", "outputParamInstance", "inputParamInstances" });
			resultData.put("joint", joint.getUid());
			resultData.put("arrangementInstance", arrangementInstance.getUid());
			resultData.put("currentTimeStamp", currentTimeStamp);

			resultDataArray.add(resultData);
		}

		translateJSONObject.put("results", resultDataArray);
		return translateJSONObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.InstanceDAOService#
	 * queryJointInstancesByBusinessID(java.lang.String)
	 */
	@Override
	@Transactional("transactionManager")
	public JSONArray queryJointInstancesByBusinessID(String businessID) throws BizException {
		/*
		 * 处理过程参见queryJointInstancesByUserid(String userid, Integer nowPage,
		 * Integer maxPageRows) 只是查询途径、转换方式不一样而已
		 */
		// 1、===================
		List<JointInstanceEntity> jointInstances = this.jointInstanceDAO.queryJointInstancesByBusinessID(businessID);
		if (jointInstances == null || jointInstances.isEmpty()) {
			return new JSONArray();
		}

		// 2、=========================
		JSONArray resultDataArray = new JSONArray();
		for (JointInstanceEntity jointInstanceEntity : jointInstances) {
			// 查询arrangementInstance
			ArrangementInstanceEntity arrangementInstance = jointInstanceEntity.getArrangementInstance();

			// 3、=========================开始计算current_time_stamp
			Long createTimeValue = arrangementInstance.getCreateTime();
			Long absOffsettime = jointInstanceEntity.getAbsOffsettime() == null ? 0l
					: jointInstanceEntity.getAbsOffsettime();
			Long currentTimeStamp = createTimeValue + absOffsettime;

			// 4、=========================形成JSONObject
			JSONObject resultData = JSONUtils.toJSONObject(jointInstanceEntity,
					new String[] { "joint", "arrangementInstance", "outputParamInstance", "inputParamInstances" });
			resultData.put("arrangementInstance", arrangementInstance.getUid());
			resultData.put("currentTimeStamp", currentTimeStamp);

			resultDataArray.add(resultData);
		}

		return resultDataArray;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.InstanceDAOService#
	 * queryJointInstanceByArrangementInstanceuid(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@Transactional("transactionManager")
	public JSONArray queryJointInstancesByArrangementInstanceID(String arrangementInstanceuid, String jointStatu)
			throws BizException {
		List<JointInstanceEntity> joints = this.jointInstanceDAO
				.queryJointInstanceEntityByInstanceID(arrangementInstanceuid, jointStatu);
		if (joints == null || joints.isEmpty()) {
			return new JSONArray();
		}

		// 为了避免事务异常，这里直接转为json
		JSONArray results = JSONUtils.toJSONArray(joints,
				new String[] { "arrangementInstance", "inputParamInstances", "outputParamInstance", "joint" });
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.InstanceDAOService#
	 * queryJointInstancesByJointInstanceID(java.lang.String)
	 */
	@Override
	@Transactional("transactionManager")
	public JSONObject queryJointInstancesByJointInstanceID(String jointInstanceuid) throws BizException {
		JointInstanceEntity jointInstance = this.jointInstanceDAO.queryJointInstancesByID(jointInstanceuid);
		if (jointInstance == null) {
			return new JSONObject();
		}

		// 为了避免事务异常，这里直接转为json
		JSONObject resultData = JSONUtils.toJSONObject(jointInstance,
				new String[] { "arrangementInstance", "outputParamInstance", "inputParamInstances", "joint" });
		// 对应的arrangementInstance的uid还是要有的
		resultData.put("arrangementInstanceuid", jointInstance.getArrangementInstance().getUid());
		// 对应的jointuid还是要有的
		resultData.put("jointuid", jointInstance.getJoint().getUid());
		return resultData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.InstanceDAOService#
	 * queryNextJointInstanceByJointInstanceId(java.lang.String)
	 */
	@Override
	@Transactional("transactionManager")
	public JSONObject queryNextJointInstanceByJointInstanceId(String jointInstanceId) throws BizException {
		JointInstanceEntity jointInstance = this.jointInstanceFlowDAO
				.queryNextJointInstanceByJointInstanceId(jointInstanceId);
		if (jointInstance == null) {
			return new JSONObject();
		}

		// 为了避免事务异常，这里直接转为json
		JSONObject resultData = JSONUtils.toJSONObject(jointInstance,
				new String[] { "arrangementInstance", "outputParamInstance", "inputParamInstances", "joint" });
		// 对应的arrangementInstance的uid还是要有的
		resultData.put("arrangementInstanceuid", jointInstance.getArrangementInstance().getUid());
		// 对应的jointuid还是要有的
		resultData.put("jointuid", jointInstance.getJoint().getUid());
		return resultData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.InstanceDAOService#
	 * queryInputParamsInstanceByJointInstanceID(java.lang.String,
	 * java.lang.Boolean)
	 */
	@Override
	@Transactional("transactionManager")
	public JSONArray queryInputParamsInstanceByJointInstanceID(String jointInstanceuid, Boolean required)
			throws BizException {
		List<JointInputParamsInstanceEntity> paramsInstances = this.jointInputParamsInstanceDAO
				.queryInputParamsInstanceByJointInstanceID(jointInstanceuid, required);
		if (paramsInstances == null || paramsInstances.isEmpty()) {
			return new JSONArray();
		}

		// 为了避免事务异常，这里直接转为json
		JSONArray resultData = JSONUtils.toJSONArray(paramsInstances,
				new String[] { "jointInputParam", "jointInstance" });
		return resultData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.InstanceDAOService#
	 * queryOutputParamsInstanceByJointInstanceID(java.lang.String,
	 * java.lang.Boolean)
	 */
	@Override
	public JSONArray queryOutputParamsInstanceByJointInstanceID(String jointInstanceid, Boolean required)
			throws BizException {
		List<JointOutputParamsInstanceEntity> paramsInstances = this.jointOutputParamsInstanceDAO
				.queryOutputParamsInstanceByJointInstanceID(jointInstanceid, required);
		if (paramsInstances == null || paramsInstances.isEmpty()) {
			return new JSONArray();
		}

		// 为了避免事务异常，这里直接转为json
		JSONArray resultData = JSONUtils.toJSONArray(paramsInstances,
				new String[] { "jointOutputParam", "jointInstance" });
		return resultData;
	}

	@Override
	@Transactional("transactionManager")
	public JSONArray queryContextParamByArrangementInstanceId(String arrangementInstanceId) throws BizException {
		List<InstanceContextParamEntity> result = this.instanceContextParamDAO
				.queryContextParamByArrangementInstanceId(arrangementInstanceId);
		if (result == null) {
			return new JSONArray();
		}

		// 为了避免事务异常，这里直接转为json
		JSONArray resultData = JSONUtils.toJSONArray(result, new String[] { "arrangementInstance" });
		return resultData;
	}

	@Override
	@Transactional("transactionManager")
	public void flowingJointInstance(String arrangementInstanceId, String jointInstanceId, String executor,
			Map<String, Object> properties) throws BizException {
		/*
		 * 整个过程的包括：
		 * 
		 * 1、验证传来的instanceId是否正确。验证当前任务实例的状态是否正确。从上下文中读取原有的数据记录，以便随后进行的比较
		 * 
		 * 2、根据情况更改任务实例JointInstanceEntity和任务实例流转记录JointInstanceFlowEntity的状态（
		 * 还包括了执行时间、执行者） a、如果当前任务实例没有后续需要流转的任务实例或者子流程，说明这个流程实例已经完成流转。更新当前流程实例、
		 * 所有任务实例状态为completed b、如果当前任务实例有后续的任务实例X，则更新这个任务实例X的状态为executing
		 * c、如果当前任务实例有后续的子流程实例Y，则更新这个流程实例Y的状态为executing（这个在第一个版本中不需要处理）
		 * 这个信息参见FlowCommandLocalImpl中的描述.
		 * 
		 * 3、写入上下文的变化，注意，如果上下文信息中已经有这个参数，则是更新；其他情况是插入新的参数
		 * 
		 * 4、最后的更新数据是arrangementInstance，
		 * 因为这时arrangementInstance有可能是waiting或者executing又或者revoked状态中的一种。
		 * 都需要更新成executing状态
		 * 
		 * 5、写“任务流转日志”和“上下文变化日志”，以便记录变化情况。 a、先写“任务流转日志”。主要是取得任务流转日志的id。
		 * b、再根据传入的“参数信息”和原有的“参数信息”进行对比，写入上下文参数变化情况。
		 */
		// 1、========================
		// 查询得到的任务实例
		JointInstanceEntity jointInstance = this.jointInstanceDAO.getEntity(jointInstanceId);
		if (jointInstance == null) {
			throw new BizException("没有发现指定的任务实例，请检查", ResponseCode._403);
		}
		ArrangementInstanceEntity arrangementInstance = this.arrangementInstanceDAO.getEntity(arrangementInstanceId);
		if (arrangementInstance == null) {
			throw new BizException("没有发现指定的流程实例，请检查", ResponseCode._403);
		}
		List<InstanceContextParamEntity> instanceContextParams = this.instanceContextParamDAO
				.queryContextParamByArrangementInstanceId(arrangementInstanceId);
		// 当前指定的流程实例正在进行流转的任务实例
		JointInstanceFlowEntity nowJointInstanceFlow = this.jointInstanceFlowDAO
				.queryExecutingJointInstanceByArrangementInstanceId(arrangementInstanceId);
		if (nowJointInstanceFlow == null) {
			throw new BizException("没有查询到流程实例中处于executing状态的任务实例，请检查", ResponseCode._504);
		}
		if (!StringUtils.equals(jointInstance.getUid(), nowJointInstanceFlow.getJointInstance().getUid())) {
			throw new BizException("当前指定的任务实例与能够流转的任务实例，不相符，请检查", ResponseCode._504);
		}

		// 2、========================更新实例信息
		// 由于在第一个版本的流程引擎中，只支持顺序流转，所以这里查询下一个任务实例就行了
		JointInstanceEntity nextJointInstance = nowJointInstanceFlow.getNextJointInstance();
		String nextJointInstanceId = null;
		Date exeTime = new Date();
		// a的情况
		if (nextJointInstance == null) {
			// 首先还是要更新成fllowed状态
			this.jointInstanceDAO.updateFollowedJointInstanceStatuByJointInstanceId(jointInstanceId, exeTime.getTime(),
					executor);
			this.jointInstanceDAO.updateCompletedJointInstanceStatuByArrangementInstanceId(arrangementInstanceId);
			this.jointInstanceFlowDAO.updateCompletedJointInstanceStatuByArrangementInstanceId(arrangementInstanceId);
		}
		// b的情况
		else {
			this.jointInstanceDAO.updateFollowedJointInstanceStatuByJointInstanceId(jointInstanceId, exeTime.getTime(),
					executor);
			this.jointInstanceFlowDAO.updateFollowedJointInstanceStatuByFlowId(nowJointInstanceFlow.getUid(),
					exeTime.getTime(), executor);
			// 接下来是更改下一个实例的信息
			this.jointInstanceDAO.updateJointInstanceStatu(nextJointInstanceId = nextJointInstance.getUid(),
					"executing");
			JointInstanceFlowEntity nextJointInstanceFlow = this.jointInstanceFlowDAO
					.queryJointFlowByJointInstanceId(nextJointInstanceId);
			String nextJointFlowId = nextJointInstanceFlow.getUid();
			this.jointInstanceFlowDAO.updateJointFlowStatuByFlowId(nextJointFlowId, "executing");
		}

		// 3、========================上下文的变化
		if (properties != null && !properties.isEmpty()) {
			Set<String> keys = properties.keySet();
			FOUNDKEY: for (String key : keys) {
				Object nowValue = properties.get(key);
				String newestValue = null;
				// 首先看是否已经存在于流程实例的上下文中了，如果存在了则为更新操作；否则为新增操作
				for (int index = 0; instanceContextParams != null && !instanceContextParams.isEmpty()
						&& index < instanceContextParams.size(); index++) {
					InstanceContextParamEntity instanceContextParam = instanceContextParams.get(index);

					// 如果条件成立，说明已经存在于流程实例的上下文中，进行更新操作
					if (StringUtils.equals(instanceContextParam.getName(), key)) {
						// ===============================更新操作的代码段======================================
						if (nowValue == null) {
							newestValue = null;
						}
						// 字符串就不需要验证了
						else if (StringUtils.equals(instanceContextParam.getType(), "String")) {
							newestValue = nowValue.toString();
						}
						// 只能是true或者是false
						else if (StringUtils.equals(instanceContextParam.getType(), "Boolean")) {
							if (StringUtils.endsWithIgnoreCase(nowValue.toString(), "true")
									|| StringUtils.endsWithIgnoreCase(nowValue.toString(), "false")) {
								newestValue = nowValue.toString();
							} else {
								InstanceDAOServiceImpl.LOGGER.warn("上下文参数name：" + key + "对应的新值格式不对，忽略添加");
								continue FOUNDKEY;
							}
						}
						// 如果是Integer或者Long的情况
						else if (StringUtils.equals(instanceContextParam.getType(), "Integer")
								|| StringUtils.equals(instanceContextParam.getType(), "Long")) {
							Pattern pattern = Pattern.compile("^\\-?[0-9]*$");
							if (pattern.matcher(nowValue.toString()).find()) {
								newestValue = nowValue.toString();
							} else {
								InstanceDAOServiceImpl.LOGGER.warn("上下文参数name：" + key + "对应的新值格式不对，忽略添加");
								continue FOUNDKEY;
							}
						}
						// 如果是Float或者Double的情况
						else if (StringUtils.equals(instanceContextParam.getType(), "Float")
								|| StringUtils.equals(instanceContextParam.getType(), "Double")) {
							Pattern pattern = Pattern.compile("^\\-?[0-9]*\\.?[0-9]*$");
							if (pattern.matcher(nowValue.toString()).find()) {
								newestValue = nowValue.toString();
							} else {
								InstanceDAOServiceImpl.LOGGER.warn("上下文参数name：" + key + "对应的新值格式不对，忽略添加");
								continue FOUNDKEY;
							}
						}
						// 如果是json，则使用JSONObject进行检查
						else if (StringUtils.equals(instanceContextParam.getType(), "JSON")) {
							JSONObject.fromObject(nowValue.toString());
							newestValue = nowValue.toString();
						}
						// 如果是XML，就不检查了，因为不好检查
						// TODO 缺少XML的检查代码
						else if (StringUtils.equals(instanceContextParam.getType(), "XML")) {
							newestValue = nowValue.toString();
						}
						// 如果是Date，则转换成yyyy-MM-dd HH:mm:ss的格式
						else if (StringUtils.equals(instanceContextParam.getType(), "Date")) {
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							newestValue = simpleDateFormat.format(nowValue);
						} else {
							InstanceDAOServiceImpl.LOGGER.warn("上下文参数name：" + key + "不是合适的类型，忽略添加");
							continue FOUNDKEY;
						}

						this.instanceContextParamDAO.updateNewestContextParamValue(instanceContextParam.getUid(),
								newestValue);
						continue FOUNDKEY;
					}
				}

				// ========================================添加操作的代码段======================================
				// 到这里说明没有在上下文中，这时需要进行新增操作（当然如果没有查询到这个出参实例，则尽量加入，不能加入就放弃）
				// TODO 这里还需要优化，比如一次将出参信息都查询出来
				JointOutputParamsInstanceEntity jointOutputParamsInstance = this.jointOutputParamsInstanceDAO
						.queryOutputParamsInstanceByName(jointInstanceId, key);
				InstanceContextParamEntity instanceContextParam = new InstanceContextParamEntity();
				// 当然，如果这个不存在与实例入参的参数，又没有值，就忽略添加
				if (nowValue == null) {
					continue FOUNDKEY;
				}

				// 如果条件成立，说明这个参数并不在任务实例的出参设置中，那么进行更新就行了
				if (jointOutputParamsInstance == null) {
					InstanceDAOServiceImpl.LOGGER.warn("上下文参数name：" + key + "不是指定的入参，试图加入上下文，但不一定能够正确加入");
					instanceContextParam.setName(key);
					if (nowValue instanceof Long) {
						instanceContextParam.setType("Long");
						instanceContextParam.setNowValue(nowValue.toString());
					} else if (nowValue instanceof Integer) {
						instanceContextParam.setType("Integer");
						instanceContextParam.setNowValue(nowValue.toString());
					} else if (nowValue instanceof String) {
						instanceContextParam.setType("String");
						instanceContextParam.setNowValue(nowValue.toString());
					} else if (nowValue instanceof Boolean) {
						instanceContextParam.setType("Boolean");
						instanceContextParam.setNowValue(nowValue.toString());
					} else if (nowValue instanceof Float) {
						instanceContextParam.setType("Float");
						instanceContextParam.setNowValue(nowValue.toString());
					} else if (nowValue instanceof Double) {
						instanceContextParam.setType("Double");
						instanceContextParam.setNowValue(nowValue.toString());
					} else if (nowValue instanceof Date) {
						// 如果是日期类型，那么value要按照yyyy-MM-dd HH:mm:ss的格式进行转换
						instanceContextParam.setType("Date");
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						instanceContextParam.setNowValue(simpleDateFormat.format(nowValue));
					}
					// 如果是其他类型的参数值，则不进行添加了
					else {
						continue FOUNDKEY;
					}
				}
				// 否则说明这个参数已经在任务实例参数中设置了
				else {
					instanceContextParam.setName(jointOutputParamsInstance.getName());
					instanceContextParam.setType(jointOutputParamsInstance.getType());
					instanceContextParam.setNowValue(nowValue.toString());
				}
				instanceContextParam.setArrangementInstance(arrangementInstance);
				instanceContextParam.setUid(UUID.randomUUID().toString());
				this.instanceContextParamDAO.insert(instanceContextParam);
			}
		}

		// 4、========================更新arrangementInstance
		if (nextJointInstance == null) {
			this.arrangementInstanceDAO.updateArrangementInstanceCompletedStatu(arrangementInstanceId,
					new Date().getTime());
		} else {
			this.arrangementInstanceDAO.updateArrangementInstanceStatu(arrangementInstanceId, "executing");
		}

		// 5、========================a:写日志信息，包括流转状态的变化和流程实例上下文参数的变化
		JointInstanceFlowLogEntity log = new JointInstanceFlowLogEntity();
		log.setArrangementInstanceId(arrangementInstanceId);
		log.setExecutor(executor);
		log.setFlowExeTime(exeTime);
		log.setFlowType("forward");
		log.setFormJointInstanceId(jointInstanceId);
		log.setToJointInstanceId(nextJointInstanceId);
		String loguid = UUID.randomUUID().toString();
		log.setUid(loguid);
		this.jointInstanceFlowLogDAO.insert(log);

		// 5、=======================b:写入上下文参数变化情况
		if (properties != null && !properties.isEmpty()) {
			Set<String> keys = properties.keySet();
			for (String key : keys) {
				InstanceContextParamLogEntity instanceContextParamLog = new InstanceContextParamLogEntity();
				Object nowValue = properties.get(key);
				String newestValue = null;

				// 从instanceContextParams（原来的上下文参数集合中）寻找这个key是否已经存在，这样好寻找fromValue的值
				// （注意，这里nowValue已经不用验证了，因为之前的代码已经验证过，如果值的格式不正确，根本就到不了这里）
				boolean isfound = false;
				for (int index = 0; instanceContextParams != null && !instanceContextParams.isEmpty()
						&& index < instanceContextParams.size(); index++) {
					InstanceContextParamEntity instanceContextParam = instanceContextParams.get(index);
					if (StringUtils.equals(instanceContextParam.getName(), key)) {
						instanceContextParamLog.setFromValue(instanceContextParam.getNowValue());
						// 如果是日期格式，就要进行转换
						if (StringUtils.equals(instanceContextParam.getType(), "Date")) {
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							newestValue = simpleDateFormat.format(nowValue);
						} else {
							newestValue = nowValue == null ? null : nowValue.toString();
						}
						instanceContextParamLog.setToValue(newestValue);

						isfound = true;
						break;
					}
				}

				// 如果条件成立，说明这个参数不在当前的上下文中，属于新的参数那么fromValue=null
				if (!isfound) {
					instanceContextParamLog.setFromValue(null);
					if (nowValue == null) {
						instanceContextParamLog.setToValue(null);
					} else if (nowValue instanceof Long || nowValue instanceof Integer || nowValue instanceof String
							|| nowValue instanceof Boolean || nowValue instanceof Float || nowValue instanceof Double) {
						instanceContextParamLog.setToValue(nowValue.toString());
					} else if (nowValue instanceof Date) {
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						instanceContextParamLog.setToValue(simpleDateFormat.format(nowValue));
					}
				}

				instanceContextParamLog.setJointInstanceFlowLog(log);
				instanceContextParamLog.setKey(key);
				instanceContextParamLog.setUid(UUID.randomUUID().toString());
				this.instanceContextParamLogDAO.insert(instanceContextParamLog);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.InstanceDAOService#
	 * unflowingJointInstance(java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional("transactionManager")
	public void unflowingJointInstance(String arrangementInstanceId, String jointInstanceId, String executor)
			throws BizException {
		/*
		 * 首先我们要说清楚，什么情况下才会执行undo方法：
		 * 1、如果是由于execute()执行失败后，才执行undo的，后于execute数据操作层已经有事务支持了，
		 * 所以这种情况下执行undo是不做任何事情的 2、只有当外层在execute()没有执行失败的情况，调用undo，
		 * 那么说明操作者确实需要将目前处于executing状态的退回到上一个状态（包括流程实例上下文中的数据）
		 * 
		 * 然后，我们再说如何进行这个“正在运行的任务实例”的回退
		 * 1、验证当前的任务实例是否能够被回退，除了上面说到的处理前提外，只有处于“followed”状态
		 * 且当前这个实例的下一个实例处于“executing”的状态的任务实例能够被回退
		 * 
		 * 2、数据准备： a、查询这个任务实例对应的任务实例基本信息、流程实例基本信息、 b、当前上下文情况、当前任务实例流转向导、
		 * c、当前任务实例最后一次流转日志，上下文变化日志
		 * 
		 * 3、数据回退的时候： a、首先通过上下文变化日志，回退当前上下文情况（并且记录回退日志） b、通过流转日志，回退流转向导数据
		 * c、最后回退任务实例基本数据（流程实例基本数据是不需要回退的）
		 * 
		 * 4、由于数据回退本身也是操作，所以也是需要记录日志的： a、记录任务实例向导回退时，任务实例向导的变化日志
		 * b、记录上下文回退时，上下文的变化日志
		 */
		// 1、=======================验证
		if (StringUtils.isEmpty(arrangementInstanceId) || StringUtils.isEmpty(jointInstanceId)
				|| StringUtils.isEmpty(executor)) {
			throw new BizException("流程实例编号、任务实例编号、执行者信息都必须传入，请检查", ResponseCode._404);
		}
		// 要进行回退的流程实例
		JointInstanceEntity jointInstance = this.jointInstanceDAO.getEntity(jointInstanceId);
		if (jointInstance == null || StringUtils.isEmpty(jointInstance.getUid())) {
			throw new BizException("没有找到对应的任务实例，请检查！", ResponseCode._403);
		}
		String statu = jointInstance.getStatu();
		String jointInstanceExecutor = jointInstance.getExecutor();
		// 进行回退的流程流转信息
		JointInstanceFlowEntity jointInstanceFlow = this.jointInstanceFlowDAO
				.queryJointFlowByJointInstanceId(jointInstanceId);
		// 正常流转时，流转到的下一个流程实例
		JointInstanceEntity nextJointInstance = jointInstanceFlow.getNextJointInstance();
		if (nextJointInstance == null || StringUtils.isEmpty(nextJointInstance.getUid())) {
			throw new BizException("没有找到这个任务实例的下一个任务实例，请检查！", ResponseCode._403);
		}
		String nextStatu = nextJointInstance.getStatu();
		if (!StringUtils.equals(statu, "followed") || !StringUtils.equals(nextStatu, "executing")) {
			throw new BizException("当前任务实例和其下一个流程实例的状态不正确，不能进行回退。请检查！", ResponseCode._504);
		}
		if (!StringUtils.isEmpty(jointInstanceExecutor) && !StringUtils.equals(jointInstanceExecutor, executor)) {
			throw new BizException("任务实例设置的“执行者”，和当前“执行者”不匹配。请检查", ResponseCode._502);
		}

		// 2、=======================数据准备
		// 流程实例
		ArrangementInstanceEntity arrangementInstance = this.arrangementInstanceDAO.getEntity(arrangementInstanceId);
		if (arrangementInstance == null || StringUtils.isEmpty(arrangementInstance.getUid())) {
			throw new BizException("没有找到对应的流程实例，请检查！", ResponseCode._403);
		}
		// 上下文
		List<InstanceContextParamEntity> contextParams = this.instanceContextParamDAO
				.queryContextParamByArrangementInstanceId(arrangementInstanceId);
		// 取得当前对应的流转日志编号
		JointInstanceFlowLogEntity jointInstanceFlowLog = this.jointInstanceFlowLogDAO
				.queryLastForwardFlowLog(jointInstanceId);
		if (jointInstanceFlowLog == null || StringUtils.isEmpty(jointInstanceFlowLog.getUid())) {
			throw new BizException("没有找到对应的任务实例流转日志，请检查！", ResponseCode._403);
		}
		// 上下文日志
		List<InstanceContextParamLogEntity> contextParamsLogs = this.instanceContextParamLogDAO
				.queryInstanceContextParamLogByFlowLog(jointInstanceFlowLog.getUid());

		// 3、=======================a:首先通过上下文变化日志，回退当前上下文情况
		for (int index = 0; contextParamsLogs != null && index < contextParamsLogs.size(); index++) {
			InstanceContextParamLogEntity contextParamLog = contextParamsLogs.get(index);
			String fromValue = contextParamLog.getFromValue();
			String keylog = contextParamLog.getKey();

			for (int contextParamIndex = 0; contextParams != null
					&& contextParamIndex < contextParams.size(); contextParamIndex++) {
				InstanceContextParamEntity instanceContextParam = contextParams.get(contextParamIndex);
				String key = instanceContextParam.getName();
				// 如果条件成立，说明就是这个上下文的值要进行回退 | 且直接删除
				if (StringUtils.equals(keylog, key) && fromValue == null) {
					this.instanceContextParamDAO.delete(instanceContextParam.getUid());
					break;
				}
				// 如果条件成立，说明就是这个上下文的值要进行回退 | 且更新会原值
				else if (StringUtils.equals(keylog, key) && fromValue != null) {
					this.instanceContextParamDAO.updateNewestContextParamValue(instanceContextParam.getUid(),
							fromValue);
					break;
				}
			}
		}

		// 3、=======================a:通过流转日志，回退流转向导数据
		JointInstanceFlowEntity nextJointInstanceFlow = this.jointInstanceFlowDAO
				.queryJointFlowByJointInstanceId(nextJointInstance.getUid());
		this.jointInstanceFlowDAO.updateJointFlowStatuByFlowId(jointInstanceFlow.getUid(), "executing");
		this.jointInstanceFlowDAO.updateJointFlowStatuByFlowId(nextJointInstanceFlow.getUid(), "revoked");

		// 3、=======================c、最后回退任务实例基本数据
		this.jointInstanceDAO.updateJointInstanceStatu(jointInstanceId, "executing");
		this.jointInstanceDAO.updateJointInstanceStatu(nextJointInstance.getUid(), "revoked");

		// 4、=======================a、记录任务实例向导回退时，任务实例向导的变化日志
		JointInstanceFlowLogEntity newJointInstanceFlowLog = new JointInstanceFlowLogEntity();
		newJointInstanceFlowLog.setArrangementInstanceId(arrangementInstanceId);
		newJointInstanceFlowLog.setExecutor(executor);
		Date flowExeTime = new Date();
		newJointInstanceFlowLog.setFlowExeTime(flowExeTime);
		newJointInstanceFlowLog.setFlowType("backward");
		newJointInstanceFlowLog.setFormJointInstanceId(nextJointInstance.getUid());
		newJointInstanceFlowLog.setToJointInstanceId(jointInstanceId);
		newJointInstanceFlowLog.setUid(UUID.randomUUID().toString());
		this.jointInstanceFlowLogDAO.insert(newJointInstanceFlowLog);

		// 4、=======================b、记录上下文回退时，上下文的变化日志
		for (int index = 0; contextParamsLogs != null && index < contextParamsLogs.size(); index++) {
			InstanceContextParamLogEntity oldContextParamLog = contextParamsLogs.get(index);
			String fromValue = oldContextParamLog.getFromValue();
			String toValue = oldContextParamLog.getToValue();
			String keylog = oldContextParamLog.getKey();

			InstanceContextParamLogEntity newInstanceContextParamLog = new InstanceContextParamLogEntity();
			newInstanceContextParamLog.setFromValue(toValue);
			newInstanceContextParamLog.setJointInstanceFlowLog(newJointInstanceFlowLog);
			newInstanceContextParamLog.setKey(keylog);
			newInstanceContextParamLog.setToValue(fromValue);
			newInstanceContextParamLog.setUid(UUID.randomUUID().toString());
			this.instanceContextParamLogDAO.insert(newInstanceContextParamLog);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	@Transactional("transactionManager")
	public String createArrangementInstance(JSONObject arrangementInstance) throws BizException {
		/*
		 * 整个过程的包括： 从顶至下的建立实例对象并进行持久化。 1、将JSONObject转化为对象
		 * 2、先将最顶层的ArrangementInstanceEntity持久化 3、将所有相应的JointInstanceEntity持久化
		 * 4、将所有的InputParamsInstanceEntity与OutputParamsInstanceEntity持久化
		 */
		if (arrangementInstance.isEmpty() || arrangementInstance.isNullObject()) {
			throw new BizException("输入流程实例JSON对象错误，请检查。", ResponseCode._402);
		}
		// 反序列化参数
		Map<String, Class> template = new HashMap<String, Class>();
		template.put("jointInstances", JointInstanceEntity.class);
		ArrangementInstanceEntity arrangementInstanceEntity = (ArrangementInstanceEntity) JSONUtils.toBean(
				arrangementInstance.toString(), ArrangementInstanceEntity.class, template,
				new String[] { "parentInstance", "childArrangementInstances", "outputParamInstance",
						"inputParamInstances", "arrangementInstance", "jointmapping", "childArrangements",
						"arrangementInstances", });
		// 持久化流程实例
		ArrangementInstanceEntity createdArrangementInstance = this.arrangementInstanceDAO
				.createArrangementInstance(arrangementInstanceEntity);
		if (createdArrangementInstance != null) {
			Set<JointInstanceEntity> jointInstances = arrangementInstanceEntity.getJointInstances();
			if (jointInstances == null) {
				return createdArrangementInstance.getUid();
			}

			for (Iterator<JointInstanceEntity> i = jointInstances.iterator(); i.hasNext();) {
				JointInstanceEntity jointInstance = i.next();
				jointInstance.setArrangementInstance(createdArrangementInstance);
				JointInstanceEntity currentJointInstance = this.jointInstanceDAO.createJointInstance(jointInstance);
				JointEntity currentJointTemplate = currentJointInstance.getJoint();
				List<JointInputParamsEntity> jointInputParams = jointInputParamsDAO
						.queryInputParamsByjointuid(currentJointTemplate.getUid());
				if (jointInputParams != null) {
					for (JointInputParamsEntity jointInputParam : jointInputParams) {
						JointInputParamsInstanceEntity jointInputParamsInstance = new JointInputParamsInstanceEntity();
						jointInputParamsInstance.setDefaultValue(jointInputParam.getDefaultValue());
						jointInputParamsInstance.setDisplayName(jointInputParam.getDisplayName());
						jointInputParamsInstance.setDisplayType(jointInputParam.getDisplayType());
						jointInputParamsInstance.setJointInputParam(jointInputParam);
						jointInputParamsInstance.setName(jointInputParam.getName());
						jointInputParamsInstance.setRequired(jointInputParam.getRequired());
						jointInputParamsInstance.setType(jointInputParam.getType());
						jointInputParamsInstance.setJointInstance(currentJointInstance);
						this.jointInputParamsInstanceDAO.createInputParamsInstance(jointInputParamsInstance);
					}
				}

				List<JointOutputParamsEntity> jointOutputParams = jointOutputParamsDAO
						.queryOutputParamsByjointuid(currentJointTemplate.getUid());
				if (jointOutputParams != null) {
					for (JointOutputParamsEntity jointOutputParam : jointOutputParams) {
						JointOutputParamsInstanceEntity jointOutputParamsInstance = new JointOutputParamsInstanceEntity();
						jointOutputParamsInstance.setDefaultValue(jointOutputParam.getDefaultValue());
						jointOutputParamsInstance.setJointOutputParam(jointOutputParam);
						jointOutputParamsInstance.setName(jointOutputParam.getName());
						jointOutputParamsInstance.setRequired(jointOutputParam.getRequired());
						jointOutputParamsInstance.setType(jointOutputParam.getType());
						jointOutputParamsInstance.setJointInstance(currentJointInstance);
						this.jointOutputParamsInstanceDAO.createOutputParamsInstance(jointOutputParamsInstance);
					}
				}
			}
			return createdArrangementInstance.getUid();
		} else {
			return null;
		}
	}

	@Override
	@Transactional("transactionManager")
	public void deleteArrangementInstance(String arrangementInstanceuid) throws BizException {
		/*
		 * 整个过程的包括： 这里的整体顺序与创建的时候相反 1、将根据流程实例ID查询到Entity信息
		 * 2、将所有的InputParamsInstanceEntity与OutputParamsInstanceEntity删除
		 * 3、将所有相应的JointInstanceEntity持久化 4、最后将最顶层的ArrangementInstanceEntity持久化
		 */
		if (StringUtils.isEmpty(arrangementInstanceuid)) {
			throw new BizException("流程实例ID不能为空，请检查", ResponseCode._402);
		}

		ArrangementInstanceEntity arrangementInstanceEntity = this.arrangementInstanceDAO
				.getEntity(arrangementInstanceuid);
		if (arrangementInstanceEntity == null) {
			throw new BizException("输入的流程实例ID不存在，请检查", ResponseCode._404);
		}
		this.jointInstanceDAO.deleteJointInstancesByArrangementInstanceID(arrangementInstanceuid);
		this.arrangementInstanceDAO.deleteArrangementInstance(arrangementInstanceuid);
	}

	@Override
	@Transactional("transactionManager")
	public void updateArrangementInstance(JSONObject arrangementInstance) throws BizException {
		if (arrangementInstance.isEmpty() || arrangementInstance.isNullObject()) {
			throw new BizException("输入流程实例JSON对象错误，请检查。", ResponseCode._402);
		}
		ArrangementInstanceEntity arrangementInstanceEntity = (ArrangementInstanceEntity) JSONUtils.toBean(
				arrangementInstance.toString(), ArrangementInstanceEntity.class,
				new String[] { "jointInstances", "childArrangementInstances" });
		this.arrangementInstanceDAO.updateArrangementInstance(arrangementInstanceEntity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	@Transactional("transactionManager")
	public JSONObject createJointInstanceFlow(JSONObject jointInstanceFlow) throws BizException {
		if (jointInstanceFlow.isEmpty() || jointInstanceFlow.isNullObject()) {
			throw new BizException("输入任务实例流程JSON对象错误，请检查。", ResponseCode._402);
		}
		// 反序列化参数
		Map<String, Class> template = new HashMap<String, Class>();
		template.put("jointInstance", JointInstanceEntity.class);
		template.put("joint", JointEntity.class);
		template.put("previouJointInstance", JointInstanceEntity.class);
		template.put("nextJointInstance", JointInstanceEntity.class);
		template.put("previouArrangementInstance", ArrangementInstanceEntity.class);
		template.put("nextArrangementInstance", ArrangementInstanceEntity.class);
		JointInstanceFlowEntity jointInstanceFlowEntity = (JointInstanceFlowEntity) JSONUtils
				.toBean(jointInstanceFlow.toString(), JointInstanceFlowEntity.class, template, new String[] {});
		JointInstanceFlowEntity createdJointInstanceFlow = jointInstanceFlowDAO
				.createJointInstanceFlow(jointInstanceFlowEntity);
		// 序列化结果
		JSONObject retObject = JSONUtils.toJSONObject(createdJointInstanceFlow, new String[] {});
		return retObject;
	}

	@Override
	public void updateJointInstanceStatu(String jointInstanceuid, String jointStatu) throws BizException {
		this.jointInstanceDAO.updateJointInstanceStatu(jointInstanceuid, jointStatu);
	}

	@Override
	public void updateJointFlowStatuByFlowId(String flowid, String jointStatu) throws BizException {
		this.jointInstanceFlowDAO.updateJointFlowStatuByFlowId(flowid, jointStatu);
	}

	@Override
	@Transactional("transactionManager")
	public JSONArray querySortedJointInstanceFlowByArrangementInstanceId(String arrangementInstanceuid)
			throws BizException {
		List<JointInstanceFlowEntity> joints = this.jointInstanceFlowDAO
				.querySortedJointInstanceByArrangementInstanceId(arrangementInstanceuid);
		if (joints == null || joints.isEmpty()) {
			return new JSONArray();
		}
		// 为了避免事务异常，这里直接转为json
		JSONArray results = JSONUtils.toJSONArray(joints,
				new String[] { "arrangementInstance", "inputParamInstances", "outputParamInstance", "joint",
						"arrangement", "parentInstance", "jointInstances", "childArrangementInstances" });
		return results;
	}
}
