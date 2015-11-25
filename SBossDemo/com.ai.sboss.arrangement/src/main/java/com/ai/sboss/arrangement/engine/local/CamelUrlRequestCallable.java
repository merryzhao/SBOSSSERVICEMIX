package com.ai.sboss.arrangement.engine.local;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.eclipse.jetty.http.HttpStatus;

import com.ai.sboss.arrangement.entity.DescEntity;
import com.ai.sboss.arrangement.entity.JsonEntity;
import com.ai.sboss.arrangement.exception.ResponseCode;
import com.ai.sboss.arrangement.utils.JSONUtils;

/**
 * 这个线程用于进行
 * @author yinwenjie
 */
public class CamelUrlRequestCallable implements Callable<JsonEntity> {

	/**
	 * http连接器
	 */
	private HttpClient httpClient;
	
	/**
	 * 本次请求的camel的url路径
	 */
	private String camelUri;
	
	/**
	 * 本次请求的http参数
	 */
	private JsonEntity requestParams;
	
	/**
	 * 构造函数中，我们将建立httpClient对象。并确保keepalived是打开的。
	 * 这样，只要线程池中这个线程不销毁，http连接的时间将会缩短。
	 */
	public CamelUrlRequestCallable() {
		this.httpClient = new HttpClient();
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public JsonEntity call() throws Exception {
		/*
		 * 处理过程包括：
		 * 1、通过传入的参数，组合URL所需要的参数信息
		 * 2、获取请求返回值，并根据返回结果判断请求处理成功
		 * 3、提取返回格式中的data部分内容，作为返回出去的最终结果
		 * */
		
		//1、===================组合，并请求
		PostMethod postMethod = new PostMethod(this.camelUri);
		StringRequestEntity stringRequestEntity = new StringRequestEntity(JSONUtils.toString(this.requestParams), "text/html", "UTF-8");
		postMethod.setRequestEntity(stringRequestEntity);
		Header httpHeader = new Header("keepalive", "true");
		postMethod.setRequestHeader(httpHeader);
		
		int responceCode = this.httpClient.executeMethod(postMethod);
		//开始得到返回结果
		JsonEntity responseParams = new JsonEntity();
		DescEntity descEntity = new DescEntity();
		
		//2、===================
		//如果条件成立，说明请求成功了（其他都失败了）
		if(responceCode == HttpStatus.ACCEPTED_202 || responceCode == HttpStatus.FOUND_302 || responceCode == 200) {
			byte[] responseBytes = postMethod.getResponseBody();
			String responseBody = new String(responseBytes , "UTF-8");
			
			//转json
			Map<String, Class<? extends Object>> classMapping = new HashMap<String , Class<? extends Object>>();
			classMapping.put("data", JSONObject.class);
			classMapping.put("desc", DescEntity.class);
			responseParams = (JsonEntity)JSONUtils.toBean(responseBody, JsonEntity.class , classMapping , new String[]{""});
		} else {
			responseParams.setData(new Object()); 
			responseParams.setDesc(descEntity);
			if(responceCode == HttpStatus.NOT_FOUND_404) {
				descEntity.setResult_msg("NOT_FOUND");
				descEntity.setResult_code(ResponseCode._404);
			} else if(responceCode == HttpStatus.UNAUTHORIZED_401) {
				descEntity.setResult_msg("UNAUTHORIZED");
				descEntity.setResult_code(ResponseCode._401);
			} else if(responceCode == HttpStatus.FORBIDDEN_403) {
				descEntity.setResult_msg("FORBIDDEN");
				descEntity.setResult_code(ResponseCode._403);
			} else if(responceCode == HttpStatus.INTERNAL_SERVER_ERROR_500) {
				descEntity.setResult_msg("INTERNAL_SERVER_ERROR");
				descEntity.setResult_code(ResponseCode._501);
			} else if(responceCode == HttpStatus.BAD_GATEWAY_502) {
				descEntity.setResult_msg("BAD_GATEWAY");
				descEntity.setResult_code(ResponseCode._502);
			} else {
				descEntity.setResult_msg("OTHER_BUG");
				descEntity.setResult_code(ResponseCode._503);
			}
		}
		
		return responseParams;
	}

	/**
	 * @param camelUri the camelUri to set
	 */
	public void setCamelUri(String camelUri) {
		this.camelUri = camelUri;
	}

	/**
	 * @param requestParams the requestParams to set
	 */
	public void setRequestParams(JsonEntity requestParams) {
		this.requestParams = requestParams;
	}
}
