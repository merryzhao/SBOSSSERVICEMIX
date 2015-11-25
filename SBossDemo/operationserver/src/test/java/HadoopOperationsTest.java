/**
 * 
 */


import org.junit.Test;

import com.ai.hadoop.operationimplements.HadoopOperationsImpl;
import com.ai.hadoop.operationinterfaces.HadoopOperations;

/**
 * @author idot
 *
 */
public class HadoopOperationsTest {

	@Test
	public void rmiTestingHadoopLoadFile() throws Exception{
		HadoopOperations rhello = new HadoopOperationsImpl(); 
		rhello.readHDFSListAll("hdfs://10.1.228.151:9000","/user/aihadoop/analysis/content/article/2015-06-10");
	}
}
