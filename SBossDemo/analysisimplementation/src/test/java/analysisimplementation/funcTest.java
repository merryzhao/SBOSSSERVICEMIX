//package analysisimplementation;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import org.junit.Test;
//
//import net.sf.json.JSONObject;
//import com.ai.sboss.datanalysis.core.DataAnalysisCoreInterface;
//import com.ai.sboss.datanalysis.core.DataMatchCoreInterface;
//import com.ai.sboss.datanalysis.core.TextDataAnalysisCoreImpl;
//import com.ai.sboss.datanalysis.core.TextDataMatchCoreImpl;
//import com.ai.sboss.datanalysis.util.MixAllTextInfoImpl;
//import com.ai.sboss.datanalysis.util.MixAllTextInfoInterface;
//
//import junit.framework.TestCase;
//
//public class funcTest extends TestCase {
//	@Test
//	public void test() {
//		//fail("Not yet implemented");
//		DataAnalysisCoreInterface tst = new TextDataAnalysisCoreImpl();
//		List<String> testContent = new ArrayList<String>();
//		MixAllTextInfoInterface textProcessor = new MixAllTextInfoImpl();
//		testContent = textProcessor.readFileByLines("content.txt");
//		
//		List<JSONObject> allCatalogTrees = textProcessor.getAllCatalogs("industryList.txt");
//		List<JSONObject> totalOfferings = textProcessor.getAllOfferings(allCatalogTrees);
//		List<JSONObject> totalOfferingInfo = textProcessor.getTotalOfferingInfo(totalOfferings);
//		
//		for(int i = 0; i< totalOfferingInfo.size(); i++){
//			String offerStr = totalOfferingInfo.get(i).toString();
//			HashMap<String, Integer> parseContentRes = tst.dataAnalysisCore(testContent.get(0));
//			HashMap<String, Integer> parseOfferingRes = tst.dataAnalysisCore(textProcessor.offeringTextInfoExtract(offerStr, "offeringName") + textProcessor.offeringTextInfoExtract(offerStr, "comments")+textProcessor.offeringTextInfoExtract(offerStr, "charSpecList"));		DataMatchCoreInterface matchSV = new TextDataMatchCoreImpl();
//			float matchValue = matchSV.matchContentOffering(parseContentRes, parseOfferingRes);
//			System.out.println("matchValue:"+matchValue+"\t"+totalOfferingInfo.get(i).getString("offeringName"));
//		}
//		
//	}
//}
