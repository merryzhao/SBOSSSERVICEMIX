//package wordparse;
//
//
//
//import java.rmi.Naming;
//import java.rmi.registry.LocateRegistry;
//
//
//public class WordParseServiceStart {
//	public static void main(String[] arg){
//		try{
//			IWordParsePkgSV wordSparsePkg = new WordParseImpl();
//			LocateRegistry.createRegistry(8090);
//			Naming.bind("//localhost:8090/wordSparse", wordSparsePkg);
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		System.out.println("wordParse service pkg started!");
//	}
//}
