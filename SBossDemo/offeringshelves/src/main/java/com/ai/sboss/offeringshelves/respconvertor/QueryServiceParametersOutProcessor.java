/**
 * 
 */
package com.ai.sboss.offeringshelves.respconvertor;

import java.rmi.Naming;












import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;













import com.ai.sboss.common.interfaces.IBasicOutProcessor;
import com.ai.sboss.upc.httpRequestInterface.UpcHttpRequestInterface;

/**
 * @author idot
 *
 */
public class QueryServiceParametersOutProcessor implements IBasicOutProcessor{

	protected Logger logger = Logger.getLogger(this.getClass());
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":1,\"result_msg\":\"success\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		Message inMessage = exchange.getIn();
		String ret = convert2requst(inMessage.getBody(String.class));
		inMessage.setBody(ret);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		// TODO Auto-generated method stub
		
		JSONArray value_list = new JSONArray();
		JSONArray parameter_list = new JSONArray();
		JSONObject finalJson = new JSONObject();
		JSONObject parameters_temp = new JSONObject();
		//获取前端传入的参数
		JSONObject param = JSONObject.fromObject(data);		
		Long catalogNodeId = param.getLong("catalogNodeId");
		String parameters = "charSpecType=ACCESS&selectedChars=&uiTempId=" + catalogNodeId;
		logger.info("parameters------------" + parameters);
		
		//采用RMI的协议方式，调用upc接口，其中，charSpecType的类型写死为ACCESS（解决版本问题）
		UpcHttpRequestInterface upcHttpRequestInterface = (UpcHttpRequestInterface) Naming.lookup("rmi://10.5.1.249:8091/upcHttpRequestService");
		
		upcHttpRequestInterface.loginIn();
		
		logger.info("upcHttpRequestInterface------------" + upcHttpRequestInterface);
		String resultJson = upcHttpRequestInterface.sendPostRequest("http://10.1.228.153:8090/ALUPC-jc2/?service=ajax&page=PCEUICharTable&listener=queryAllCharSpecInfoWithObjType&m=81000011&p=PCEUICharTable&" + parameters, "");
		logger.info("request url------------------------" + ("http://10.1.228.153:8090/ALUPC-jc2/?service=ajax&page=PCEUICharTable&listener=queryAllCharSpecInfoWithObjType&m=81000011&p=PCEUICharTable&"+ parameters));
		logger.info("resultjson------------" + resultJson);
		
		JSONObject resultJsonObj = JSONObject.fromObject(resultJson);
		//从回传的json字符串获取context，进行最终需要返回的json字符串的拼凑
		int result = Integer.valueOf(resultJsonObj.getJSONObject("context").getString("x_resultcode"));
		String errMsg = (String) resultJsonObj.getJSONObject("context").get("x_resultinfo");
		
		//从回传的json字符串获取data，得到parameter_list
		JSONArray resultParaArray = resultJsonObj.getJSONArray("data");	
		
		for(int i = 0; i < resultParaArray.size(); i ++){
			value_list.clear();
			JSONObject paraSingle = resultParaArray.getJSONObject(i);
			logger.info("paraSingle-----------------" + paraSingle);
			Long para_id = paraSingle.getLong("charSpecId");
			String para_code = paraSingle.getString("code");
			String para_name = paraSingle.getString("charSpecName");
			int para_type = paraSingle.getInt("valueType");
			int is_customized =  paraSingle.getInt("isCustomized");
			String para_default = "";
			//对于每个特征规格，只有一个默认值，属于该特征规格的全局变量
			if(paraSingle.containsKey("charSpecValues")){
				JSONArray paraValueArray = paraSingle.getJSONArray("charSpecValues");
				logger.info("paraValueArray------------------" + paraValueArray);
				//tempMap用于存储所有的键、值对
				Map<String, String> tempMap = new HashMap<String, String>();
				for(int j = 0; j < paraValueArray.size(); j++){	
					tempMap.clear();
					String value_code = String.valueOf(paraValueArray.getJSONObject(j).get("charValueId"));
					String value = (String) paraValueArray.getJSONObject(j).get("value");
					int is_default = (int) paraValueArray.getJSONObject(j).getInt("isDefault");
					if(is_default == 1){
						para_default = value;
					}
					tempMap.put("value_code", value_code);
					tempMap.put("value", value);
					value_list.add(tempMap);
				}
				
				logger.info("value_list---------------" + value_list);
			}
			parameters_temp.put("para_id", para_id);
			parameters_temp.put("para_code", para_code);
			parameters_temp.put("para_name", para_name);
			parameters_temp.put("para_type", para_type);
			parameters_temp.put("is_customized", is_customized);
			parameters_temp.put("value_list", value_list.toString());
			logger.info("***Single value_list output***"+value_list.toString());
			parameters_temp.put("para_default", para_default);
			
			parameter_list.add(parameters_temp);
		}
		logger.info("parameter_list--------------" + parameter_list);
		
		//拼接最终返回给前端的json字符串
		finalJson.put("result", result);
		finalJson.put("errMsg", errMsg);
		finalJson.put("parameter_list", parameter_list);
		logger.info("finalJson------------------" + finalJson);
		
		return RETFMT.replace("<DATA>", finalJson.toString());
	}
}
