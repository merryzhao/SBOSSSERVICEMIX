/**
 * 
 */
package com.ai.sboss.datanalysisimpl.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ai.sboss.datanalysis.support.FileOperationInterface;

/**
 * @author idot
 *
 */
public class FileOperationImpl implements FileOperationInterface{

	public List<Long> getAllCatalogId(String fileName) {
		// TODO Auto-generated method stub
		List<Long> catalogIdList = new ArrayList<Long>();
		File file = new File(fileName);
		BufferedReader  reader = null;
		try {  
            //System.out.println("以行为单位读取文件内容，一次读一整行：");  
            reader = new BufferedReader(new FileReader(file));  
            String tempString = null;  
            int line = 1;  
            // 一次读入一行，直到读入null为文件结束  
            while ((tempString = reader.readLine()) != null) {  
                // 显示行号  
                //System.out.println("line " + line + ": " + tempString); 
                catalogIdList.add(Long.parseLong(tempString));
                line++; 
            }  
            reader.close();  
        } catch (IOException e) {  
            e.printStackTrace();  			
        } finally {
        	if(reader != null){
        		try{
        			reader.close();
        		}
        		catch(IOException e){
        			e.printStackTrace();
        		}
        	}
        }
		return catalogIdList;
	}
}
