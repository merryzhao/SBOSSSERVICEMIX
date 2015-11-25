package fileProcessorInterface.fileProcessorInterface;

import java.rmi.Remote;
import java.util.HashMap;
import java.util.Properties;

public interface ISendMail extends Remote {
	public boolean sendMail(Properties props, HashMap<String, String> mailItem) throws Exception;

}
