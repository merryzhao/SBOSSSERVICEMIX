package com.ai.sboss.arrangement.engine.dao;

import java.util.Map;

import com.ai.sboss.arrangement.exception.BizException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 该接口向模块外部提供关于流程实例数据相关的持久层存储、查询服务
 * @author yinwenjie
 */
public interface InstanceDAOService {
	/**
	 * 创建实例信息。<br>
	 * 注意：这里的arrangementInstance的对象必须包含所有的必要参数（对应的流程模板、可能关联的父级流程实例、对应的任务实例节点）<br>
	 * 这里不会负责创建子流程
	 * @param arrangementInstance 必须传入的 任务实例
	 * @return 若创建成功则返回流程实例的ID， 若创建失败则返回null
	 * @throws BizException
	 */
	public String createArrangementInstance(JSONObject arrangementInstance) throws BizException ;
	
	/**
	 * 创建任务实例流程信息。<br>
	 * 注意：这里的jointInstanceFlow的相关对象必须是已经持久化好了的<br>
	 * @param jointInstanceFlow 必须传入的 任务实例
	 * @return 若创建成功则返回任务实例流程的ID， 若创建失败则返回null
	 * @throws BizException
	 */
	public JSONObject createJointInstanceFlow(JSONObject jointInstanceFlow) throws BizException ;
	
	/**
	 * 根据流程实例ID，删除一个任务实例。<br>
	 * 注意：这里会删除流程实例本身的信息，同时删除其关联的其他实例（如：任务实例，子流程实例）
	 * @param jointInstanceuid 需要删除的实例uid编号。
	 * @throws BizException
	 */
	public void deleteArrangementInstance(String arrangementInstanceuid) throws BizException ;
	
	/**
	 * 更新一个流程实例基本信息。
	 * @param arrangementInstance 传入流程实例JSON对象。
	 * @throws BizException
	 */
	public void updateArrangementInstance(JSONObject arrangementInstance) throws BizException;
	
	/**
	 * 按照流程实例的编号，查询这个流程实例的基本信息（不包括查询流程实例对象中的各种关联实体信息）
	 * @param arrangementInstanceuid 指定的流程实例编号
	 * @return 
	 * @throws BizException
	 */
	public JSONObject queryArrangementInstanceByArrangementInstanceID(String arrangementInstanceuid) throws BizException;
	
	/**
	 * 按照流程实例的绑定的业务编号信息，查询这个流程实例的基本信息（不包括查询流程实例对象中的各种关联实体信息）
	 * @param businessid 指定的流程实例绑定的业务编号信息
	 * @return 
	 * @throws BizException
	 */
	public JSONObject queryArrangementInstanceByBusinessID(String businessid) throws BizException;
	
	/**
	 * 按照 任务实例/流程实例 的创建者或者执行者，查询归属的任务实例信息。
	 * @param userid 必须传入的 任务实例
	 * @param nowPage 当前的页码数，从0开始计算。如果不传入就是从0开始计数
	 * @param maxPageRows 每页最大的显示数量，默认为20条
	 * @return 返回的信息将会按照创建的先后顺序进行排序（按照任务的uid进行排序）
	 */
	public JSONObject queryJointInstancesByUserid(String userid , Integer nowPage , Integer maxPageRows) throws BizException ;
	
	/**
	 * 按照 绑定的外部业务编号，查询对应的任务实例信息
	 * @param businessID 任务编号信息
	 * @return 返回的任务信息将会按照创建的先后顺序进行排序（按照任务的uid进行排序）
	 * @throws BizException 
	 */
	public JSONArray queryJointInstancesByBusinessID(String businessID) throws BizException ;
	
	/**
	 * 按照 绑定的流程实例编号，查询对应的任务流转实例信息
	 * @param arrangementInstanceuid 流程实例编号
	 * @return 返回的任务流转实例信息将会按照流程的先后顺序进行排序
	 * @throws BizException 
	 */
	public JSONArray querySortedJointInstanceFlowByArrangementInstanceId(String arrangementInstanceuid) throws BizException ;
	
	/**
	 * 按照流程实例编号（注意，是流程实例编号），查询这个流程实例所关联的符合状态条件的“任务实例”。<br>
	 * 如果没有传入“任务实例状态”条件，则是查询这个流程实例所关联的所有任务实例（并不保证任务实例执行的先后顺序）。
	 * @param arrangementInstanceuid 指定的流程实例编号
	 * @param jointStatu 任务实例状态，任务实例状态只有以下几种值：<p>
	 * 		waiting 这个任务实例已经完成了初始化，等待执行<p>
	 * 		executing 这个任务的前置任务/子流程，已经正常执行完成了，目前正轮到这个任务实例进行执行。<br>
	 * 			注意还有一种情况，就是流程实例回退的时候，回退到了这个任务实例上。<p>
	 * 		followed 这个任务已经正常执行完成，但是其所处的流程实例还没有全部执行完成<p>
	 * 		revoked 这个任务实例之前已经正常执行完成，但操作者进行了回退操作，这个任务实例的执行状态已经被回退。从业务执行特性来看，相当于waiting状态<p>
	 * 		completed 这个任务实例已经正常执行完成，并且其所处的流程实例也已经全部执行完成<p>
	 * 		terminated 这个任务实例所对应的流程实例已经被操作者强制终止了。<br>
	 * @return 
	 * @throws BizException
	 */
	public JSONArray queryJointInstancesByArrangementInstanceID(String arrangementInstanceuid , String jointStatu) throws BizException;
	
	/**
	 * 按照任务实例的编号，查询这个任务实例的基本信息（不包括查询任务实例对象中的各种关联实体信息）
	 * @param jointInstanceuid 指定的任务实例编号
	 * @return 
	 * @throws BizException
	 */
	public JSONObject queryJointInstancesByJointInstanceID(String jointInstanceuid) throws BizException;
	
	/**
	 * 查询指定的任务实例，其下一个任务实例的基本信息
	 * @param jointInstanceId 当前的任务实例编号
	 * @return 
	 * @throws BizException
	 */
	public JSONObject queryNextJointInstanceByJointInstanceId(String jointInstanceId) throws BizException;
	
	/**
	 * 按照jointInstanceuid任务实例编号，查询其对应的入参实例信息集合
	 * @param jointInstanceuid 指定的joint任务实例编号信息
	 * @param required 是否只查询必要参数，若true，则只查询必要的入参实例，若false,则查询所有入参
	 * @return 如果有符合条件的入参实例集合信息将被返回；其他情况返回null
	 * @throws BizException
	 */
	public JSONArray queryInputParamsInstanceByJointInstanceID(String jointInstanceuid, Boolean required) throws BizException;
	
	/**
	 * 按照jointInstanceid任务实例编号，查询其对应的出参实例信息集合
	 * @param jointInstanceuid 指定的joint任务实例编号信息
	 * @param required 是否只查询必要参数，若true，则只查询必要的出参实例，若false,则查询所有出参
	 * @return 如果有符合条件的出参实例集合信息将被返回；其他情况返回null
	 * @throws BizException
	 */
	public JSONArray queryOutputParamsInstanceByJointInstanceID(String jointInstanceid, Boolean required) throws BizException;
	
	/**
	 * 查看流程实例上下文的参数信息。按照流程实例编号查询
	 * @param arrangementInstanceId 流程实例编号查询
	 * @return 
	 */
	public JSONArray queryContextParamByArrangementInstanceId(String arrangementInstanceId) throws BizException;
	
	/**
	 * 对指定的流程实例进行数据层面的流转。注意，是数据层面，因为解析、验证、camel调用等工作是在StreamingControlServiceImpl中完成的。<br>
	 * 所以这个数据层的服务只负责对数据层进行更改。
	 * @param arrangementInstanceId 指定的流程实例Id，这是为了避免多次查询
	 * @param jointInstanceId 指定的任务实例Id。
	 * @param executor 任务实例的执行者
	 * @param propertiesValues 本次任务实例流转所需要变更的上下文参数信息
	 * @throws BizException
	 */
	public void flowingJointInstance(String arrangementInstanceId , String jointInstanceId , String executor , Map<String, Object> propertiesValues) throws BizException;
	
	/**
	 * 回退当前的任务实例到执行前的状态。注意，是数据层面，因为解析、验证、等工作是在StreamingControlServiceImpl中完成的。<br>
	 * 所以这个数据层的服务只负责对数据层进行更改。
	 * @param arrangementInstanceId 指定的流程实例Id，这是为了避免多次查询
	 * @param jointInstanceId 指定的任务实例Id。
	 * @param executor 回退操作执行者
	 * @throws BizException
	 */
	public void unflowingJointInstance(String arrangementInstanceId, String jointInstanceId , String executor) throws BizException;
	
	/**
	 * 更新一个任务实例状态信息。
	 * @param jointInstanceuid 需要修改的流程实例uid。
	 * @param jointStatu 新状态
	 * @throws BizException
	 */
	public void updateJointInstanceStatu(String jointInstanceuid, String jointStatu) throws BizException;
	
	/**
	 * 更新一个任务实例流转向导的状态信息。
	 * @param flowid 需要修改的流程实例流转的uid。
	 * @param jointStatu 新的状态。注意这种更新方式不适合更新为“Followed”状态。因为“Followed”状态还必须传入exeTime和执行人
	 * @throws BizException
	 */
	public void updateJointFlowStatuByFlowId(String flowid, String jointStatu) throws BizException;
}
