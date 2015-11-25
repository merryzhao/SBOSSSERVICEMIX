package com.ai.sboss.arrangement.engine.local;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.IFlowCommand;
import com.ai.sboss.arrangement.engine.dao.ArrangementDAOAbstractFactory;
import com.ai.sboss.arrangement.engine.dao.InstanceDAOService;
import com.ai.sboss.arrangement.entity.JsonEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * 这是进行“任务流转”命令处理的本地实现，也可能是唯一的一种实现
 * @author yinwenjie
 */
@Component("_processorEngine_flowCommand")
public class FlowCommandLocalImpl extends ApplicationObjectSupport implements IFlowCommand {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(FlowCommandLocalImpl.class); 
	
	@Autowired
	private ArrangementDAOAbstractFactory arrangementDAOAbstractFactory;
	
	/**
	 * 任务实例的编号信息
	 */
	private String jointInstanceId;
	
	/**
	 * 流程实例的编号信息
	 */
	private String arrangementInstanceId;
	
	/**
	 * 本次流转操作时，从外部API接口传入的操作人员指定的参数值
	 */
	private Map<String, Object> propertiesValues;
	
	/**
	 * 命令的执行者
	 */
	private String executor;
	
	/**
	 * 这个属性至关重要，用来表示这个命令的正向命令是否已经正确执行过。<br>
	 * FlowCommandLocalImpl实现中，在两种情况下会执行逆向undo()操作：<br>
	 * 1、在execute执行失败后，控制器将会调用undo方法进行回滚
	 * 2、控制程序直接undo当前任务实例的执行状态
	 * 
	 * 在情况1下，由于execute调用的数据层本来就有事务的支持，所以在控制器调用undo的时候，实际上是不需要执行任何操作的。
	 * 在情况2下，用户直接调用undo，这时就应该会退到任务实例执行前的状态。
	 * 
	 * 所以我们需要一个对象executeError，来表示是情况1呢还是情况2
	 */
	private boolean executeError = false;
	
	/**
	 * 用于执行camel请求的线程池
	 */
	@Autowired
	@Qualifier("camel_url_threadpool_executor")
	private ThreadPoolExecutor camelThreadPoolExecutor;
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.ICommand#init()
	 */
	@Override
	public void init() throws BizException {
		
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.IFlowCommand#init(java.lang.String, java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public void init(String jointInstanceId, String arrangementInstanceId, String executor, Map<String, Object> propertiesValues) throws BizException {
		this.jointInstanceId = jointInstanceId;
		this.arrangementInstanceId = arrangementInstanceId;
		this.executor = executor;
		this.propertiesValues = propertiesValues;
		this.init();
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.ICommand#execute()
	 */
	@Override
	public void execute() throws BizException {
		/* 
		 * 首先要说明，只有满足以下的任务实例才能进行流转。
		 * 		a、当前准备进行实例流转的对象，其状态statu为executing，
		 * 		b、这个任务实例所对应的流程实例，其状态statu为executing，
		 * 		c、其执行过程（例如对应的camel的执行过程）是没有问题的，出参和入参的记录是OK的
		 * 
		 * 流转过程包括：
		 * 1、验证这个任务实例是否可以进行流转。
		 * 		a、查询和验证任务实例
		 * 		b、查询和验证流程实例
		 * 		c、查询和验证入参实例
		 * 
		 * 2、如果这个任务实例存在camelUri的引用
		 * 		a、则需要通过inputParamsInstances和contextParams进行传入的url参数的构造
		 * 		b、除了从contextParams上下文中查询需要的参数外，还要从外部传入的propertiesValues中寻找值，并且如果两种来源发生冲突，则以后一个为依据
		 * 		c、在经过上面的赋值过程后，如果“必填字段”还是没有值，则系统会报错了
		 * 
		 * 3、如果这个任务实例存在camelUri的引用，则开始执行camelUri信息（使用json进行参数传递），并等待返回
		 * 		返回的信息是一个按照约定规范进行返回的json信息，主要看json中的code信息。
		 * 		properties中存储了返回值。
		 * 
		 * 4、从camelUri正常返回的resultsData，其中哪些参数需要记录到上下文中，这个事情的依据是JointOutputParamsInstanceEntity中的描述
		 * 		那么这一步要做的事情是从resultsData中读取JointOutputParamsInstanceEntity的参数到上下文中，并且在后续的日志信息中记录这些变化
		 * 
		 * 5、根据情况更改任务实例JointInstanceEntity和任务实例流转记录JointInstanceFlowEntity的状态（还包括了执行时间、执行者）
		 * 		a、如果当前任务实例没有后续需要流转的任务实例或者子流程，说明这个流程实例已经完成流转。更新当前流程实例、所有任务实例状态为completed
		 * 		b、如果当前任务实例有后续的任务实例X，则更新这个任务实例X的状态为executing
		 * 		c、如果当前任务实例有后续的子流程实例Y，则更新这个流程实例Y的状态为executing
		 * 		这个信息参见FlowCommandLocalImpl中的描述
		 * 
		 * 6、最后的更新数据是arrangementInstance，因为这时arrangementInstance有可能是waiting或者executing又或者revoked状态中的一种。
		 * 都需要更新成executing状态
		 * 
		 * 7、从上下文中读取原有的数据记录，以便随后进行的比较
		 * 
		 * 8、写“任务流转日志”和“上下文变化日志”，以便记录变化情况。
		 * 		a、先写“任务流转日志”。主要是取得任务流转日志的id。
		 * 		b、再根据传入的“参数信息”和原有的“参数信息”进行对比，写入上下文参数变化情况。
		 * */
		//TODO 异常抛出的方式，还需要在进行调整，不然不能在所有异常的情况下更改executeError属性
		
		this.executeError = false;
		InstanceDAOService instanceDAOService = this.arrangementDAOAbstractFactory.getInstanceDAOService();
		JSONObject jointInstanceObject = instanceDAOService.queryJointInstancesByJointInstanceID(this.jointInstanceId);
		if(jointInstanceObject == null) {
			this.executeError = true;
			throw new BizException("没有发现指定的任务实例，请检查", ResponseCode._403);
		}
		JSONObject arrangementInstanceObject = instanceDAOService.queryArrangementInstanceByArrangementInstanceID(this.arrangementInstanceId);
		if(arrangementInstanceObject == null || !arrangementInstanceObject.has("uid")) {
			this.executeError = true;
			throw new BizException("没有发现指定的流程实例，请检查", ResponseCode._403);
		}
		//查询入参信息
		JSONArray inputParamsInstances = instanceDAOService.queryInputParamsInstanceByJointInstanceID(this.jointInstanceId, null);
		//查询出参信息
		JSONArray outputParamsInstances = instanceDAOService.queryOutputParamsInstanceByJointInstanceID(this.jointInstanceId, null);
		//查询上下文信息
		JSONArray contextParams = instanceDAOService.queryContextParamByArrangementInstanceId(this.arrangementInstanceId);
		
		//1、=======================
		String jointInstanceStatu = jointInstanceObject.getString("statu");
		String arrangementInstanceStatu = arrangementInstanceObject.getString("statu");
		String camelUri = jointInstanceObject.has("camelUri")?jointInstanceObject.getString("camelUri"):null;
		//进行jointInstanceStatu和arrangementInstanceStatu的验证
		if(!StringUtils.equals(jointInstanceStatu, "executing")
			|| (!StringUtils.equals(arrangementInstanceStatu, "executing") 
			&& !StringUtils.equals(arrangementInstanceStatu, "waiting") 
			&& !StringUtils.equals(arrangementInstanceStatu, "revoked"))) {
			this.executeError = true;
			throw new BizException("任务实例和流程实例状态错误，请检查", ResponseCode._403);
		}
		
		//2、=======================a、构造给camel的参数
		//TODO 这段代码结构还需要进行优化
		JsonEntity requestParams = new JsonEntity();
		JSONObject requestParamItems = new JSONObject();
		for(int index = 0 ; !StringUtils.isEmpty(camelUri) 
				&& inputParamsInstances != null && !inputParamsInstances.isEmpty() 
				&& index < inputParamsInstances.size() ; index++) {
			JSONObject inputParamsInstanceItem = inputParamsInstances.getJSONObject(index);
			
			String name = inputParamsInstanceItem.getString("name");
			String type = inputParamsInstanceItem.getString("type");
			Boolean required = inputParamsInstanceItem.getBoolean("required");
			String defaultValue = inputParamsInstanceItem.has("defaultValue")?inputParamsInstanceItem.getString("defaultValue"):null;
			
			//试图从上下文取值
			boolean contextFound = false;
			for(int contextIndex = 0 ; contextParams != null && !contextParams.isEmpty() && contextIndex < contextParams.size() ; contextIndex ++) {
				JSONObject contextItem = contextParams.getJSONObject(contextIndex);
				String uid = contextItem.getString("uid");
				String contextItem_name = contextItem.getString("name");
				String contextItem_type = contextItem.getString("type");
				String contextItem_nowValue = contextItem.has("nowValue")?contextItem.getString("nowValue"):null;
				//还是要做判断，经过2015-08-31的事情后，我已经不相信第三方传来的参数了
				if(StringUtils.isEmpty(contextItem_name) || StringUtils.isEmpty(contextItem_type)) {
					FlowCommandLocalImpl.LOGGER.warn("contextItem_name(" + contextItem_name + ") | contextItem_type(" + contextItem_type + ") 发现空值，这条信息(uid:" + uid + ")不可用");
					continue;
				}
				
				//如果条件成立，说明流程实例上下文中有这个参数的值
				if(StringUtils.endsWith(name, contextItem_name)) {
					contextFound = true;
					//如果条件成立，那么当前值需要进行默认值的设置
					if(contextItem_nowValue == null && required && defaultValue != null) {
						contextItem_nowValue = defaultValue;
					} 
					
					//String、JSON、XML、Date直接处理成字符串
					if(StringUtils.equals(contextItem_type, "String")
						|| StringUtils.equals(contextItem_type, "JSON")
						|| StringUtils.equals(contextItem_type, "XML")
						|| StringUtils.equals(contextItem_type, "Date")) {
						requestParamItems.put(contextItem_name, contextItem_nowValue);
					}
					//Long、Integer要满足条件才能转，否则为""
					if((StringUtils.equals(contextItem_type, "Long")
						|| StringUtils.equals(contextItem_type, "Integer"))
						&& !StringUtils.isEmpty(contextItem_nowValue)) {
						
						Pattern pattern = Pattern.compile("^\\-?[0-9]*$");
						if(pattern.matcher(contextItem_nowValue).find()) {
							requestParamItems.put(contextItem_name, Long.parseLong(contextItem_nowValue));
						} else {
							FlowCommandLocalImpl.LOGGER.warn("contextItem_name : " + contextItem_name + "的值并不是整数，无法转换");
							requestParamItems.put(contextItem_name, "");
						} 
					}
					//Float、Double要满足条件餐能转，否则为""
					if((StringUtils.equals(contextItem_type, "Float")
						|| StringUtils.equals(contextItem_type, "Double"))
						&& !StringUtils.isEmpty(contextItem_nowValue)) {
						
						Pattern pattern = Pattern.compile("^\\-?[0-9]*[\\.]?[0-9]*$");
						if(pattern.matcher(contextItem_nowValue).find()) {
							requestParamItems.put(contextItem_name, Double.parseDouble(contextItem_nowValue));
						} else {
							FlowCommandLocalImpl.LOGGER.warn("contextItem_name : " + contextItem_name + "的值并不是浮点数，无法转换");
							requestParamItems.put(contextItem_name, "");
						}
					}
					break;
				}
			}
			
			//如果执行到这里，其contextFound还是false，说明上下文中没有这个值，这是试图取inputParamsInstances中设置的默认值
			if(!contextFound) {
				FlowCommandLocalImpl.LOGGER.warn("name : " + name + " 未发现上下文中的值，寻求inputParamsInstance中的默认值(defaultValue) ");
				//String、JSON、XML、Date直接处理成字符串
				if(!StringUtils.isEmpty(defaultValue) && (StringUtils.equals(type, "String")
					|| StringUtils.equals(type, "JSON")
					|| StringUtils.equals(type, "XML")
					|| StringUtils.equals(type, "Date"))) {
					requestParamItems.put(name, defaultValue);
				}
				//Long、Integer要满足条件才能转，否则为""
				if(!StringUtils.isEmpty(defaultValue) && (StringUtils.equals(type, "Long")
					|| StringUtils.equals(type, "Integer"))) {
					
					Pattern pattern = Pattern.compile("^\\-?[0-9]*$");
					if(pattern.matcher(defaultValue).find()) {
						requestParamItems.put(name, Long.parseLong(defaultValue));
					} else {
						FlowCommandLocalImpl.LOGGER.warn("name:" + name + " 的值并不是整数，无法转换");
						requestParamItems.put(name, "");
					}
				}
				//Float、Double要满足条件才能转，否则为""
				if(!StringUtils.isEmpty(defaultValue) && (StringUtils.equals(type, "Float")
					|| StringUtils.equals(type, "Double"))) {
					
					Pattern pattern = Pattern.compile("^\\-?[0-9]*[\\.]?[0-9]*$");
					if(pattern.matcher(defaultValue).find()) {
						requestParamItems.put(name, Double.parseDouble(defaultValue));
					} else {
						FlowCommandLocalImpl.LOGGER.warn("name:" + name + " 的值并不是浮点数，无法转换");
						requestParamItems.put(name, "");
					}
				}
			}
		}
		
		//2、=======================b、从外部传入的propertiesValues中寻找值
		//注意propertiesValues的值并不直接计入流程实例上下文,但是要作为调用可能的camel时，的URL参数使用
		if(this.propertiesValues != null && !this.propertiesValues.isEmpty() && !StringUtils.isEmpty(camelUri)) {
			Set<String> propertyKeys = this.propertiesValues.keySet();
			propertiesValuesTODO:for (String propertyKey : propertyKeys) {
				Object propertiesValue = this.propertiesValues.get(propertyKey);
				if(propertiesValue == null) {
					continue;
				}
				
				//查询所需要的入参，看看有没有匹配的值
				for(int index = 0 ; inputParamsInstances != null && !inputParamsInstances.isEmpty() && index < inputParamsInstances.size() ; index++) {
					JSONObject inputParamsInstanceItem = inputParamsInstances.getJSONObject(index);
					String name = inputParamsInstanceItem.getString("name");
					String type = inputParamsInstanceItem.getString("type");
					
					//如果条件成立，则需要依据这个信息作为入参
					if(StringUtils.equals(propertyKey, name)) {
						FlowCommandLocalImpl.LOGGER.info("外部参数name:" + name + " 匹配，使用该值（" + propertiesValue + "）！");
						//String、JSON、XML、Date直接处理成字符串
						if(StringUtils.equals(type, "String")
							|| StringUtils.equals(type, "JSON")
							|| StringUtils.equals(type, "XML")
							|| StringUtils.equals(type, "Date")) {
							requestParamItems.put(name, propertiesValue.toString());
						}
						//Long、Integer要满足条件才能转，否则为""
						if((StringUtils.equals(type, "Long")
							|| StringUtils.equals(type, "Integer"))
							&& !StringUtils.isEmpty(propertiesValue.toString())) {
							
							Pattern pattern = Pattern.compile("^\\-?[0-9]*$");
							if(pattern.matcher(propertiesValue.toString()).find()) {
								requestParamItems.put(name, Long.parseLong(propertiesValue.toString()));
							} else {
								FlowCommandLocalImpl.LOGGER.warn("name : " + name + "的值并不是整数，无法转换");
								requestParamItems.put(name, "");
							} 
						}
						//Float、Double要满足条件餐能转，否则为""
						if((StringUtils.equals(type, "Float")
							|| StringUtils.equals(type, "Double"))
							&& !StringUtils.isEmpty(propertiesValue.toString())) {
							
							Pattern pattern = Pattern.compile("^\\-?[0-9]*[\\.]?[0-9]*$");
							if(pattern.matcher(propertiesValue.toString()).find()) {
								requestParamItems.put(name, Double.parseDouble(propertiesValue.toString()));
							} else {
								FlowCommandLocalImpl.LOGGER.warn("name : " + name + "的值并不是浮点数，无法转换");
								requestParamItems.put(name, "");
							}
						}
						
						continue propertiesValuesTODO;
					}
				}
				
				//如果执行到这里，说明这个从外部传入的properties不需要设置在上下文中
				//但是要作为传给camel调用的一个url参数
				requestParamItems.put(propertyKey, propertiesValue);
			}
		}
		
		//2、=======================c：
		//在经过上面的赋值过程后，如果“必填字段”还是没有值，则系统会报错了
		for(int index = 0 ; inputParamsInstances != null && !inputParamsInstances.isEmpty() && index < inputParamsInstances.size() ; index++) {
			JSONObject inputParamsInstanceItem = inputParamsInstances.getJSONObject(index);
			String name = inputParamsInstanceItem.getString("name");
			Boolean required = inputParamsInstanceItem.getBoolean("required");
			
			if(required && (!requestParamItems.has(name) || requestParamItems.get(name) == null)) {
				this.executeError = true;
				throw new BizException("参数name：" + name + " 为必须传入的参数，但是没有发现流程实例上下文中存在值，也没有找到默认值，最后也没有找到调试时的传参。执行失败，请检查！ ", ResponseCode._403);
			}
		}
		requestParams.setData(requestParamItems);
		
		//3、=======================开始执行camel
		//使用线程池，并且使用future进行状态等待，等到执行完成后，这个线程再进行执行
		JsonEntity reponseReslut = null;
		if(!StringUtils.isEmpty(camelUri)) {
			ApplicationContext applicationContext = this.getApplicationContext();
			CamelUrlRequestCallable camelUrlRequestCallable = (CamelUrlRequestCallable)applicationContext.getBean("camelUrlRequestCallable");
			if(camelUrlRequestCallable == null) {
				throw new BizException("no found com.ai.sboss.arrangement.engine.local.CamelUrlRequestCallable!", ResponseCode._501);
			}
			camelUrlRequestCallable.setCamelUri(camelUri);
			camelUrlRequestCallable.setRequestParams(requestParams);
			
			//进行camel调用
			Future<JsonEntity> future = this.camelThreadPoolExecutor.submit(camelUrlRequestCallable);
			try {
				synchronized (future) {
					while(!future.isDone()) {
						future.wait(1l);
					}
				}
				reponseReslut = future.get();
			} catch (IllegalArgumentException e) {
				FlowCommandLocalImpl.LOGGER.error(e.getMessage(), e);
				this.executeError = true;
				throw new BizException(e.getMessage(), ResponseCode._501);
			} catch (IllegalMonitorStateException e) {
				FlowCommandLocalImpl.LOGGER.error(e.getMessage(), e);
				this.executeError = true;
				throw new BizException(e.getMessage(), ResponseCode._502);
			} catch (InterruptedException e) {
				FlowCommandLocalImpl.LOGGER.error(e.getMessage(), e);
				this.executeError = true;
				throw new BizException(e.getMessage(), ResponseCode._503);
			} catch (ExecutionException e) {
				FlowCommandLocalImpl.LOGGER.error(e.getMessage(), e);
				this.executeError = true;
				throw new BizException(e.getMessage(), ResponseCode._503);
			}
			
			//判断执行结果(只有responseCode == _200才算执行成功，否则都视为执行失败)
			ResponseCode responseCode = reponseReslut.getDesc().getResult_code();
			String errorMessage = reponseReslut.getDesc().getResult_msg();
			if(responseCode != ResponseCode._200) {
				this.executeError = true;
				throw new BizException(errorMessage, responseCode);
			}
		}
		
		//4、==========================从可能的reponseReslut中抽取返回值
		JSONObject returnData = null;
		Map<String, Object> propertiesOutValues = new HashMap<String , Object>();
		if(!StringUtils.isEmpty(camelUri) && reponseReslut != null 
			&& (returnData = (JSONObject)reponseReslut.getData()) != null
			&& outputParamsInstances != null && !outputParamsInstances.isEmpty()) {
			
			for (int index = 0 ; index < outputParamsInstances.size() ; index++) {
				JSONObject outputParamsInstanceObject = outputParamsInstances.getJSONObject(index);
				String name = outputParamsInstanceObject.getString("name");
				String type = outputParamsInstanceObject.getString("type");
				Boolean required = Boolean.parseBoolean(outputParamsInstanceObject.getString("required"));
				String defaultValue = outputParamsInstanceObject.has("defaultValue")?outputParamsInstanceObject.getString("defaultValue"):null;
				
				//如果条件成立（返回值没有这个参数，并且这个参数也不是必须的参数），那么这个返回值被忽略
				if(!returnData.has(name) && !required) {
					continue;
				}
				//如果条件成立（返回值没有这个参数，但是这个参数有默认值），那么按照默认值赋值
				else if(!returnData.has(name) && required && defaultValue != null) {
					propertiesOutValues.put(name, defaultValue);
					continue;
				}
				
				//String、JSON、XML、Date直接处理成字符串
				String outputValue = returnData.getString(name);
				if(StringUtils.equals(type, "String")
					|| StringUtils.equals(type, "JSON")
					|| StringUtils.equals(type, "XML")
					|| StringUtils.equals(type, "Date")) {
					propertiesOutValues.put(name, outputValue);
				}
				//Long、Integer要满足条件才能转，否则为""
				if((StringUtils.equals(type, "Long")
					|| StringUtils.equals(type, "Integer"))
					&& !StringUtils.isEmpty(outputValue)) {
					
					Pattern pattern = Pattern.compile("^\\-?[0-9]*$");
					if(pattern.matcher(outputValue.toString()).find()) {
						propertiesOutValues.put(name, Long.parseLong(outputValue));
					} else {
						FlowCommandLocalImpl.LOGGER.warn("name : " + name + "的值并不是整数，无法转换");
						propertiesOutValues.put(name, "");
					} 
				}
				//Float、Double要满足条件餐能转，否则为""
				if((StringUtils.equals(type, "Float")
					|| StringUtils.equals(type, "Double"))
					&& !StringUtils.isEmpty(outputValue)) {
					
					Pattern pattern = Pattern.compile("^\\-?[0-9]*[\\.]?[0-9]*$");
					if(pattern.matcher(outputValue).find()) {
						propertiesOutValues.put(name, Double.parseDouble(outputValue));
					} else {
						FlowCommandLocalImpl.LOGGER.warn("name : " + name + "的值并不是浮点数，无法转换");
						propertiesOutValues.put(name, "");
					}
				}
			}
		}
		//TODO 另外，传入的propertiesOutValues和camel返回的propertiesOutValues 两个参数集合的差集 是否需要存储上下文呢？需要考虑一下
		
		//5、6、7、8=======================变更流程状态（为了保证数据层的操作在一个事务中，所以执行过程交给DAO层的service）
		try {
			instanceDAOService.flowingJointInstance(this.arrangementInstanceId, this.jointInstanceId, this.executor, propertiesOutValues);
		} catch(BizException e) {
			FlowCommandLocalImpl.LOGGER.error(e.getMessage(), e);
			this.executeError = true;
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.ICommand#undo()
	 */
	@Override
	public void undo() throws BizException {
		if(this.executeError) {
			FlowCommandLocalImpl.LOGGER.warn("由于本次执行是因为execute执行失败引起的，事务本身已经回滚。所以这里不需要做任何事情");
			return;
		}
		
		InstanceDAOService instanceDAOService = this.arrangementDAOAbstractFactory.getInstanceDAOService();
		instanceDAOService.unflowingJointInstance(this.arrangementInstanceId, this.jointInstanceId, this.executor);
	}
}