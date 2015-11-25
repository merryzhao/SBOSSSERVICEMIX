/**
 * 
 */
package com.ai.upc.httpRequestInterface;

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
}
