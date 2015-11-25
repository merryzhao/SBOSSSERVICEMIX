package com.ai.sboss.serviceScript.urlRequest;

public interface IUrlRequest {

	public String sendGet(String url, String param);

	public String sendPost(String url, String param);

}
