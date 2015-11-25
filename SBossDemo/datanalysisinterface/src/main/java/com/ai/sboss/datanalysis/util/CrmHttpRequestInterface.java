/**
 * 
 */
package com.ai.sboss.datanalysis.util;

/**
 * @author idot
 *
 */
public interface CrmHttpRequestInterface {

	//发送get请求
	public String sendGetRequest(String url, String param);
	
	//发送post请求
	public String sendPostRequest(String url, String param);
}
