/**
 * 
 */
package com.ai.hadoop.operationinterfaces;

import java.io.IOException;
import java.rmi.Remote;
import java.util.List;

/**
 * @author idot
 * operations of hadoop related
 */
public interface HadoopOperations extends Remote{
	
	//本地上传文件到HDFS
	public void copyLocalFileToHDFS(String hdfsUrl, String hdfsFolderPath, String localFilePath) throws IOException;
	
	//从HDFS下载文件
	public void copyFileFromHDFS(String hdfsUrl, String hdfsFilePath, String localPath) throws IOException;
	
	//从HDFS上读取一个文件内容
	public void readFileFromDFS(String hdfsUrl, String hdfsFilePath) throws IOException;
	
	//从HDFS上读取一个文件夹的所有文件内容
	public List<String> readHDFSListAll(String hdfsUrl, String hdfsFolderPath) throws Exception;
	
	//向本地文件写数据
	public void writeDataToLocalFile(String data, String localFilePath) throws Exception;
}
