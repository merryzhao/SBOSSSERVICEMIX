package com.ai.sboss.ServiceCalendarMgr.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReadFileDefineValues {

	public static final String PRODUCTOFFERINGFILES_FOLDER = "ProductOfferingFiles";
	public static final String PRODCUTOFFERING_FILENAME_PREFIE_NAME = "Offering_";
	public static final String PRODCUTOFFERING_FILENAME_EXTEND_NAME = ".process";

	public static final String PRODUCTOFFERINGINSTANCEFILES_FOLDER = "ProductOfferingInstanceFiles";

	public static final String SERVICEFAKEDATA_FOLDER = "ServiceFakeData";
	public static final String SERVICEFAKEDATA_FILENAME = "serviceFakeDataSet.process";

	public static final String JSONDATA_KEY = "data";

	public static final String CUSTOMERID_KEY = "customerId";
	public static final String CUSTOMERDESC_KEY = "customerDesc";
	public static final String STEPDESC_KEY = "stepDesc";
	public static final String OFFERID_KEY = "offerId";
	public static final String OFFERNAME_KEY = "offerName";

	public static final String OFFERPROCESS_KEY = "offerProcess";
	public static final String TIMEOFFSET_KEY = "timeOffset";
	public static final String TIMESTAMP_KEY = "timeStamp";

	public static final String TIMEFORMAT_STYLE = "yyyy-mm-dd HH:mm:ss.S";

	public static final String SERVICENAME_KEY = "serviceName";
	public static final String SERVICEID_KEY = "serviceId";
	public static final String SERVICECODE_KEY = "serviceCode";
	public static final String CATALOGID_KEY = "catalogId";

	public static String getProductOfferingFileNameByOfferingId(
			String targetResPath, long productOfferingId) {

		return targetResPath + "/" + PRODCUTOFFERING_FILENAME_PREFIE_NAME
				+ String.valueOf(productOfferingId)
				+ PRODCUTOFFERING_FILENAME_EXTEND_NAME;
	}
	
	public static String getOfferingInstanceFolderPath(){
		
		/*URL localURL = ReadFileDefineValues.class .getProtectionDomain().getCodeSource().getLocation();
		String filePath = StringUtils.EMPTY;  
        try {  
            filePath = URLDecoder.decode(localURL.getPath(), "utf-8");
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        if (filePath.endsWith(".jar")) 
        {
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1); 
        }*/
		String filePath = ReadFileDefineValues.PRODUCTOFFERINGINSTANCEFILES_FOLDER;
		File file = new File(filePath);
		System.out.println("absoult PRODUCTOFFERINGINSTANCEFILES_FOLDER=>"+file.getAbsolutePath());
		if  (!file.exists()  && !file.isDirectory())      
		{       
		    System.out.println("filePath:不存在");  
		    file.mkdir();    
		}
        
        System.out.println("filePath==>"+filePath);
        return filePath;
	}

	public static String getOfferingInstanceFileName(long customerId, long customerOrderId, long offeringId) {
		
		String filePath = getOfferingInstanceFolderPath();

		createNewFolder(filePath);
        
		String folderPath = filePath + "/"
				+ String.valueOf(customerId);
		createNewFolder(folderPath);

		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd-HH-mm-ss");

		if (customerOrderId > 0) {
			return filePath
					+ "/" + String.valueOf(customerId) + "/"
					+ String.valueOf(customerId) + "_"
					+ String.valueOf(customerOrderId) + "_"
					+ String.valueOf(offeringId) + "_" + dateFormat.format(now)
					+ PRODCUTOFFERING_FILENAME_EXTEND_NAME;
		} else {
			return filePath
					+ "/" + String.valueOf(customerId) + "/"
					+ String.valueOf(customerId) + "_"
					+ String.valueOf(offeringId) + "_" + dateFormat.format(now)
					+ PRODCUTOFFERING_FILENAME_EXTEND_NAME;
		}
	}

	public static boolean createNewFolder(String folderPath) {
		File file = new File(folderPath);
		if (!file.exists() && !file.isDirectory()) {
			boolean bIsOK = file.mkdir();
			bIsOK = file.setWritable(true);
			bIsOK = file.setReadable(true);
			return bIsOK;
		} else {
			return false;
		}
	}
}
