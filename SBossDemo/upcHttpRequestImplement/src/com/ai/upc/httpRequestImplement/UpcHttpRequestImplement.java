/**
 * 
 */
package com.ai.upc.httpRequestImplement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

import com.ai.upc.httpRequestInterface.UpcHttpRequestInterface;

/**
 * @author idot
 *
 */
@SuppressWarnings("serial")
public class UpcHttpRequestImplement extends UnicastRemoteObject implements UpcHttpRequestInterface {

	public UpcHttpRequestImplement() throws RemoteException {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String sendGetRequest(String url, String param) throws Exception, RemoteException {
		// TODO Auto-generated method stub
	       String result = "";
	        BufferedReader in = null;
	        try {
	            String urlNameString = url + "?" + param;
	            URL realUrl = new URL(urlNameString);
	            // 打开和URL之间的连接
	            URLConnection connection = realUrl.openConnection();
	            // 设置通用的请求属性
	            connection.setRequestProperty("accept", "*/*");
	            connection.setRequestProperty("connection", "Keep-Alive");
	            connection.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; Trident/7.0; rv:11.0) like Gecko");
	            connection.setRequestProperty("X-Requested-With","XMLHttpRequest");
	            connection.setRequestProperty("Authorization","Basic MDF1cGM6MTIzNDU2");
	            connection.setRequestProperty("Cookie","base.login.name=01upc; JSESSIONID=2371A115AC64661C55BBF1CE8648319E");
	            
	            // 建立实际的连接
	            connection.connect();
	            // 获取所有响应头字段
	            Map<String, List<String>> map = connection.getHeaderFields();
	            // 遍历所有的响应头字段
	            for (String key : map.keySet()) {
	                System.out.println(key + "--->" + map.get(key));
	            }
	            // 定义 BufferedReader输入流来读取URL的响应
	            in = new BufferedReader(new InputStreamReader(
	                    connection.getInputStream()));
	            String line;
	            while ((line = in.readLine()) != null) {
	                result += line;
	            }
	        } catch (Exception e) {
	            System.out.println("发送GET请求出现异常！" + e);
	            e.printStackTrace();
	        }
	        // 使用finally块来关闭输入流
	        finally {
	            try {
	                if (in != null) {
	                    in.close();
	                }
	            } catch (Exception e2) {
	                e2.printStackTrace();
	            }
	        }
	        return result;
	    }

	@Override
	public String sendPostRequest(String url, String param) throws Exception, RemoteException{
		// TODO Auto-generated method stub
		 PrintWriter out = null;
	        BufferedReader in = null;
	        String result = "";
	        try {
	            URL realUrl = new URL(url);
	            // 打开和URL之间的连接
	            URLConnection conn = realUrl.openConnection();
	            // 设置通用的请求属性
	            conn.setRequestProperty("accept", "text/plain, */*");
	            conn.setRequestProperty("connection", "Keep-Alive");
	            conn.setRequestProperty("user-agent",
	                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; Trident/7.0; rv:11.0) like Gecko");
	            conn.setRequestProperty("X-Requested-With","XMLHttpRequest");
	            conn.setRequestProperty("Authorization","Basic MDF1cGM6MTIzNDU2");
	            conn.setRequestProperty("Cookie","base.login.name=01upc; JSESSIONID=8E8194225E0AEC9FF824483B03F9C3E2");
	            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
	            conn.setRequestProperty("Accept-Language", "zh-Hans-CN, zh-Hans; q=0.8, en-US; q=0.5, en; q=0.3");
	            conn.setRequestProperty("Cache-Control", "no-cache");
	            conn.setRequestProperty("Content-Type", ":application/x-www-form-urlencoded; charset=UTF-8");
	            // 发送POST请求必须设置如下两行
	            conn.setDoOutput(true);
	            conn.setDoInput(true);
	            // 获取URLConnection对象对应的输出流
	            out = new PrintWriter(conn.getOutputStream());
	            // 发送请求参数
	            out.print(param);
	            // flush输出流的缓冲
	            out.flush();
	            // 定义BufferedReader输入流来读取URL的响应
	            in = new BufferedReader(
	                    new InputStreamReader(conn.getInputStream()));
	            String line;
	            while ((line = in.readLine()) != null) {
	                result += line;
	            }
	        } catch (Exception e) {
	            System.out.println("发送 POST 请求出现异常！"+e);
	            e.printStackTrace();
	        }
	        //使用finally块来关闭输出流、输入流
	        finally{
	            try{
	                if(out!=null){
	                    out.close();
	                }
	                if(in!=null){
	                    in.close();
	                }
	            }
	            catch(IOException ex){
	                ex.printStackTrace();
	            }
	        }
	        return result;
	}
}
