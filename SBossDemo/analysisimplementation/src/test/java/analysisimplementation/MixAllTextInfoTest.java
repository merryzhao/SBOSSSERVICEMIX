///**
// * 
// */
//package analysisimplementation;
//
//import java.util.List;
//
//
//
//
//import org.junit.Test;
//
//import net.sf.json.JSONObject;
//
//import com.ai.sboss.datanalysis.util.MixAllTextInfoImpl;
//import com.ai.sboss.datanalysis.util.MixAllTextInfoInterface;
//
///**
// * @author idot
// *
// */
//public class MixAllTextInfoTest{
//						
////	@Test
//	public void testGetAllCatalogs(){
//		String fileName = "industryList.txt";
//		MixAllTextInfoInterface mixAllTextInfoInterface = new MixAllTextInfoImpl();
//		List<JSONObject> resultJsonList = mixAllTextInfoInterface.getAllCatalogs(fileName);
//		System.out.println("resultJsonList:" + resultJsonList);
//	}
//	
////	@Test
//	public void testGetAllOfferings(){
//		MixAllTextInfoInterface mixAllTextInfoInterface = new MixAllTextInfoImpl();
//		List<JSONObject> allCatalogTrees = mixAllTextInfoInterface.getAllCatalogs("industryList.txt");
//		List<JSONObject> allOfferingList = mixAllTextInfoInterface.getAllOfferings(allCatalogTrees);
//		System.out.println("allOfferingList:" + allOfferingList);
//	}
//	
//	@Test
//	public void testGetTotalOfferingInfo(){
//		MixAllTextInfoInterface mixAllTextInfoInterface = new MixAllTextInfoImpl();
//		List<JSONObject> allCatalogTrees = mixAllTextInfoInterface.getAllCatalogs("industryList.txt");
//		List<JSONObject> totalOfferings = mixAllTextInfoInterface.getAllOfferings(allCatalogTrees);
//		List<JSONObject> totalOfferingInfo = mixAllTextInfoInterface.getTotalOfferingInfo(totalOfferings);
//		System.out.println("totalOfferingInfo:" + totalOfferingInfo);
//	}
//}
