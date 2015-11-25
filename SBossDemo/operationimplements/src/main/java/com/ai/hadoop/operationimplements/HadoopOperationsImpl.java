/**
 * 
 */
package com.ai.hadoop.operationimplements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.ai.hadoop.operationinterfaces.HadoopOperations;

/**
 * @author idot
 *
 */
@SuppressWarnings("serial")
public class HadoopOperationsImpl extends UnicastRemoteObject implements HadoopOperations{

	public HadoopOperationsImpl() throws RemoteException {
		// TODO Auto-generated constructor stub
	}

	public void copyLocalFileToHDFS(String hdfsUrl, String hdfsFolderPath,
			String localFilePath) throws IOException {
		// TODO Auto-generated method stub
		FileSystem fileSystem = FileSystem.get(
				URI.create(hdfsUrl), new Configuration());
		fileSystem.copyFromLocalFile(new Path(localFilePath), new Path(hdfsFolderPath));
		//System.out.println("done");
	}

	public void copyFileFromHDFS(String hdfsUrl, String hdfsFilePath,
			String localPath) throws IOException {
		// TODO Auto-generated method stub
		FileSystem fileSystem = FileSystem.get(
				URI.create(hdfsUrl), new Configuration());
		fileSystem.copyToLocalFile(false, new Path(hdfsFilePath), new Path(localPath), true);  //默认为false，与本地文件有关
	}

	public void readFileFromDFS(String hdfsUrl, String hdfsFilePath)
			throws IOException {
		// TODO Auto-generated method stub
		FileSystem fileSystem = FileSystem.get(
				URI.create(hdfsUrl), new Configuration());
	    InputStream src = fileSystem.open(new Path(hdfsFilePath));
		BufferedReader buff = null;
		buff = new BufferedReader(new InputStreamReader(src));
		String str = null;
		str = buff.readLine();
		String[] strList = str.split("\t");
		/*for(int i = 0; i < strList.length; i++){
			System.out.println(strList[i]);
		}*/
	}

	public List<String> readHDFSListAll(String hdfsUrl, String hdfsFolderPath)
			throws Exception {
		// TODO Auto-generated method stub
		// 流读入和写入
		FileSystem hdfs = FileSystem.get(
				URI.create(hdfsUrl), new Configuration());
		InputStream in = null;
		// 获取HDFS的conf
		// 读取HDFS上的文件系统
		// FileSystem hdfs=FileSystem.get(conf);
		// 使用缓冲流，进行按行读取的功能
		BufferedReader buff = null;
		// 获取日志文件的根目录
		Path listf = new Path(hdfsFolderPath);
		// 获取根目录下的所有2级子文件目录
		FileStatus stats[] = hdfs.listStatus(listf);
		//存放读取结果
		List<String> readRes = new ArrayList<String>();
		for (int i = 0; i < stats.length; i++) {
			// 获取子目录下的文件路径
			FileStatus temp[] = hdfs.listStatus(new Path(stats[i].getPath().toString()));
			for (int k = 0; k < temp.length; k++) {
				//System.out.println("文件路径名:" + temp[k].getPath().toString());
				// 获取Path
				Path p = new Path(temp[k].getPath().toString());
				// 打开文件流
				in = hdfs.open(p);
				// BufferedReader包装一个流
				buff = new BufferedReader(new InputStreamReader(in));
				String str = null;
				while ((str = buff.readLine()) != null) {
					//System.out.println(str);
					readRes.add(str);
				}
				buff.close();
				in.close();
			}
		}
		/*for(int k = 0; k< readRes.size(); k++){
			System.out.println(readRes.get(k).toString());
		}*/
		hdfs.close();
		return readRes;
	}

	public void writeDataToLocalFile(String data, String localFilePath)
			throws Exception {
		// TODO Auto-generated method stub
		File file =new File(localFilePath);
	    //if file doesnt exists, then create it
	    if(!file.exists()){
	    	file.createNewFile();
	    }
	    //true = append file
	    FileOutputStream out=new FileOutputStream(file,false); //如果追加方式用true  
	    //System.out.print(file.getName());
	    out.write(data.getBytes());
	    out.close();
	}
}
