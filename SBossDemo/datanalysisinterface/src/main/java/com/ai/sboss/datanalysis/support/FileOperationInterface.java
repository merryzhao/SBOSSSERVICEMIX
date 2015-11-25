/**
 * 
 */
package com.ai.sboss.datanalysis.support;

import java.util.List;

/**
 * @author idot
 *
 */
public interface FileOperationInterface {

	/**
	 * 从fake文件中读取所有的顶层目录
	 * @param fileName
	 * @return
	 */
	public List<Long> getAllCatalogId(String fileName);
}
