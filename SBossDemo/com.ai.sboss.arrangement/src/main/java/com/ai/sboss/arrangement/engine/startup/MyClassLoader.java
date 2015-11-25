package com.ai.sboss.arrangement.engine.startup;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;

/**
 * 自己的class定义器
 * 
 * @author yinwenjie
 */
public class MyClassLoader extends ClassLoader {
	
	/**
	 * 日志
	 */
	private static Log LOGGER = LogFactory.getLog(MyClassLoader.class);
	
	/**
	 * 当前的插件信息，从这里拿到bundle的classloader
	 */
	private Bundle nowBundle;
	
	/**
	 * osgi最顶层的类加载器
	 */
//	private ClassLoader parent;
	
	public MyClassLoader(Bundle nowBundle , ClassLoader parent) {
		super(parent);
		this.nowBundle = nowBundle;
//		this.parent = parent;
	}

	/* (non-Javadoc)
	 * @see java.lang.ClassLoader#findClass(java.lang.String)
	 */
	protected Class<?> findClass(String className) throws ClassNotFoundException {
		Class<?> clazz = this.findLoadedClass(className);
		
		//如果条件，说明在现有的classloader中没有找到，那么试图在文件中加载
		if (null == clazz) {
			try {
				InputStream fileC = this.getClassFileStream(className);
				if(fileC == null) {
					return null;
				}
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int maxlen = 4096;
				int reallen;
				byte[] contexts = new byte[4096];
				while((reallen = fileC.read(contexts, 0, maxlen)) != -1) {
					baos.write(contexts, 0, reallen);
				}
				fileC.close();
				byte[] bytes = baos.toByteArray();
				clazz = defineClass(className, bytes, 0, bytes.length);
			} catch (FileNotFoundException e) {
				MyClassLoader.LOGGER.error(e.getMessage(), e);
			} catch (IOException e) {
				MyClassLoader.LOGGER.error(e.getMessage(), e);
			}
		}
		return clazz;
	}

	private InputStream getClassFileStream(String name) {
		MyClassLoader.LOGGER.info("name = " + name);
		String filepath = name.replace('.', '/') + ".class";
		
		//利用这个bundle的URL加载这个类
		URL classURL = this.nowBundle.getResource(filepath);
		URLConnection classURLCon = null;
		InputStream fileC = null;
		try {
			classURLCon = classURL.openConnection();
			fileC = classURLCon.getInputStream();
		} catch (IOException e) {
			MyClassLoader.LOGGER.error(e.getMessage(), e);
			return null;
		}
		long bundleid = this.nowBundle.getBundleId();
		
		MyClassLoader.LOGGER.info("===============试图加载新的bundleid:" + bundleid + "classURL:" + classURL + "，fileC使用：" + fileC);
		return fileC;
	}
}
