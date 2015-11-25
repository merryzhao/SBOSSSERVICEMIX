/**
 * 
 */
package com.ai.sboss.upc.httpRequestInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author idot
 *
 */
public interface UpcHttpRequestInterface extends Remote{
	
	//发送get请求
	public String sendGetRequest(String url, String param) throws Exception, RemoteException;
	
	//发送post请求
	public String sendPostRequest(String url, String param) throws Exception, RemoteException;
	
	//动态获取sessionId
	public String getSessionId() throws Exception;
	
	//先登录以便获取到sessionId
	public String loginIn() throws Exception;
}
