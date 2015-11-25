//package analysisimplementation;
//
//import static org.junit.Assert.*;
//
//import java.net.MalformedURLException;
//import java.rmi.Naming;
//import java.rmi.NotBoundException;
//import java.rmi.RemoteException;
//import java.util.List;
//
//import org.junit.Test;
//
//import wordparse.IWordParsePkgSV;
//
//import com.ai.sboss.datanalysis.core.OfferingRecommendorInterface;
//import com.ai.sboss.datanalysis.util.MixAllTextInfoInterface;
//import com.ai.sboss.datanalysisimpl.core.TextRelOfferingRecommendorImpl;
//import com.ai.sboss.datanalysisimpl.util.MixAllTextInfoImpl;
//
//public class analysisTest {
//
//	@Test
//	public void test() throws MalformedURLException, RemoteException, NotBoundException {
//		MixAllTextInfoInterface textProcessor = new MixAllTextInfoImpl();
//		List<String> contentLine = textProcessor.readFileByLines("contentJson.txt");
//			OfferingRecommendorInterface offerRecommendor = (OfferingRecommendorInterface) Naming
//					.lookup("//0.0.0.0:8090/AnalysisService");
//			try {
//				offerRecommendor.contentRelOfferingAnalysis(contentLine.get(0));
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}	
//	}
//
//}
