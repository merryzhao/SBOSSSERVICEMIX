package com.ai.sboss.ServiceCalendarMgrImpl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import com.ai.sboss.ServiceCalendarMgr.Utils.CollectServiceFakeData;
import com.ai.sboss.ServiceCalendarMgr.Utils.ICollectServiceFakeData;
import com.ai.sboss.ServiceCalendarMgr.Utils.IReadFile;
import com.ai.sboss.ServiceCalendarMgr.Utils.IWriteFile;
import com.ai.sboss.ServiceCalendarMgr.Utils.ReadConcreteFile;
import com.ai.sboss.ServiceCalendarMgr.Utils.ReadFileDefineValues;
import com.ai.sboss.ServiceCalendarMgr.Utils.WriteConcreteFile;
import com.ai.sboss.ServiceCalendarMgrInterface.IServiceCalendarMgr;
import com.ai.sboss.ServiceCalendarMgrInterface.IServiceInformation;

@SuppressWarnings("serial")
public class ServiceCalendarMgrImpl extends UnicastRemoteObject implements
		IServiceCalendarMgr {

	private static Logger LOGGER = Logger
			.getLogger(ServiceCalendarMgrImpl.class.getName());

	private String productOfferingResPath = StringUtils.EMPTY;

	protected ServiceCalendarMgrImpl() throws RemoteException {

		super();

		productOfferingResPath = ReadFileDefineValues.PRODUCTOFFERINGFILES_FOLDER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.ServiceCalendarMgrInterface.IServiceCalendarMgr#
	 * getServiceProcess(long, long)
	 * 
	 * Note: The customerId(parameter) is not necessary for current
	 */
	public String getServiceProcess(long productOfferingId, long customerId)
			throws RemoteException {

		LOGGER.log(Level.INFO, "Call service of getServiceProcess ...");

		String fileName = ReadFileDefineValues
				.getProductOfferingFileNameByOfferingId(productOfferingResPath,
						productOfferingId);
		LOGGER.log(Level.INFO, "query productoffering from==>"+fileName);
		IReadFile readFileObj = new ReadConcreteFile();
		String retContent = StringUtils.EMPTY;
		try {
			retContent = readFileObj.readFile(fileName);
		} catch (IOException ex) {
			ex.printStackTrace();
			LOGGER.log(Level.FINER, ex.getMessage());
		}

		if (retContent.isEmpty()) {
			LOGGER.log(Level.INFO, "The content of file is empty ...");
			return retContent;
		}

		JSONObject objOfOffering = JSONObject.fromObject(retContent);
		if (null != objOfOffering) {
			return objOfOffering.toString();
		} else {
			return StringUtils.EMPTY;
		}
	}

	public List<String> getAllServiceProcess() throws RemoteException {

		LOGGER.log(Level.INFO, "Call service of getAllServiceProcess ...");

		List<String> listOfFileContents = null;
		IReadFile readFileObj = new ReadConcreteFile();
		try {
			listOfFileContents = readFileObj
					.readFilesByPath(productOfferingResPath);
		} catch (IOException ex) {
			ex.printStackTrace();
			LOGGER.log(Level.FINER, ex.getMessage());
		}

		for (int nIndex = 0; nIndex < listOfFileContents.size(); ++nIndex) {
			JSONObject objOfOffering = JSONObject.fromObject(listOfFileContents
					.get(nIndex));
			if (null != objOfOffering) {
				listOfFileContents.set(nIndex, objOfOffering.toString());
			}
		}

		LOGGER.log(Level.INFO, "End of the service of getAllServiceProcess ...");

		return listOfFileContents;
	}

	public boolean saveServiceProcessInst(long customerId,
			long customerOrderId, long productOfferingId,
			Timestamp serviceExecTime) throws RemoteException {

		if (customerId < 1 || customerOrderId < 1 || productOfferingId < 1
				|| null == serviceExecTime) {
			return false;
		}

		return saveServiceProcessInstForInner(customerId, customerOrderId,
				productOfferingId, serviceExecTime);
	}

	public boolean saveServiceProcessInst(long customerId,
			long productOfferingId, Timestamp serviceExecTime)
			throws RemoteException {

		if (customerId < 1 || productOfferingId < 1 || null == serviceExecTime) {
			return false;
		}

		return saveServiceProcessInstForInner(customerId, 0L,
				productOfferingId, serviceExecTime);
	}

	public String getServiceProcessInst(long customerId) throws RemoteException {

		LOGGER.log(Level.INFO, "Call service of getServiceProcessInst ...");

		List<String> fileContentList = null;
		JSONObject jsonBlock = new JSONObject();
		JSONObject subJson = new JSONObject();
		String offerName = new String();
		String stepDesc = new String();
		JSONArray svProcessArray = null;
		JSONArray responseJson = new JSONArray();

		SortedMap<Long, String> svProcessSort = new TreeMap<Long, String>();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				ReadFileDefineValues.TIMEFORMAT_STYLE);
		Timestamp ts = null;

		IReadFile readFileObj = new ReadConcreteFile();
		try {
			System.out.println(ReadFileDefineValues.getOfferingInstanceFolderPath());
			fileContentList = readFileObj.readFilesByPath(ReadFileDefineValues
					.getOfferingInstanceFolderPath() + "/" + customerId);
		} catch (IOException ex) {
			ex.printStackTrace();
			LOGGER.log(Level.FINER, ex.getMessage());
		}
		// 取出JSON块
		for (int nIndex = 0; nIndex < fileContentList.size(); nIndex++) {
			jsonBlock = JSONObject.fromObject(fileContentList.get(nIndex)
					.toString());

			subJson = JSONObject.fromObject(jsonBlock.get(
					ReadFileDefineValues.JSONDATA_KEY).toString());
			offerName = subJson.getString(ReadFileDefineValues.OFFERNAME_KEY);
			svProcessArray = subJson
					.getJSONArray(ReadFileDefineValues.OFFERPROCESS_KEY);
			for (int i = 0; i < svProcessArray.size(); i++) {
				JSONObject tempJson = JSONObject.fromObject(svProcessArray.get(
						i).toString());
				Long timeStamp = 0L;
				timeStamp = tempJson.getLong(ReadFileDefineValues.TIMESTAMP_KEY);

				stepDesc = offerName + ":" + tempJson.getString(ReadFileDefineValues.STEPDESC_KEY);
				if (svProcessSort.containsKey(timeStamp)) {
					timeStamp = timeStamp + 1;
				}
				svProcessSort.put(timeStamp, stepDesc);
			}

		}

		JSONObject tmpJson = new JSONObject();
		for (int j = 0; j < svProcessSort.size(); j++) {
			String offerDesc = svProcessSort.values().toArray()[j].toString();
			Long stepTime  = (Long) svProcessSort.keySet().toArray()[j];
			
			int sIndex = offerDesc.indexOf(":");
			String tmpOfferName = offerDesc.substring(0, sIndex);
			String tmpStepDesc = offerDesc.substring(sIndex+1);
			tmpJson.put(ReadFileDefineValues.TIMESTAMP_KEY, stepTime);
			tmpJson.put(ReadFileDefineValues.OFFERNAME_KEY, tmpOfferName);
			tmpJson.put(ReadFileDefineValues.STEPDESC_KEY,tmpStepDesc);
			responseJson.add(tmpJson);
		}

		LOGGER.log(Level.INFO, "End of the service of getAllServiceProcess ...");
		return responseJson.toString();
	}

	private boolean saveServiceProcessInstForInner(long customerId,
			long customerOrderId, long productOfferingId,
			Timestamp serviceExecTime) throws RemoteException {

		LOGGER.log(Level.INFO,
				"Call service of saveServiceProcessInstForInner ...");

		// Get original ProdcutOffering data from local
		String offeringContent = getServiceProcess(productOfferingId,
				customerId);
		if (StringUtils.isEmpty(offeringContent)) {
			LOGGER.log(Level.SEVERE, "ProductOffering's content is empty ...");
			return false;
		}

		// Update ProductOffering's JSON data
		JSONObject newOfferingInstance = addFiledsToProductOffering(
				offeringContent, customerId, serviceExecTime);

		// Save ProductOffering content to file
		IWriteFile writeFileObj = new WriteConcreteFile();
		final String fileName = ReadFileDefineValues
				.getOfferingInstanceFileName(customerId, customerOrderId,
						productOfferingId);
		boolean bIsOK = writeFileObj.writeFile(fileName, newOfferingInstance.toString());

		LOGGER.log(Level.INFO,
				"End of the service of saveServiceProcessInstForInner ...");

		return bIsOK;
	}

	@SuppressWarnings("deprecation")
	private JSONObject addFiledsToProductOffering(String productOffering,
			long customerId, Timestamp serviceExecTime) {
		System.out.println(productOffering);
		JSONObject objOfOffering = JSONObject.fromObject(productOffering);
		JSONObject dataObj = objOfOffering
				.getJSONObject(ReadFileDefineValues.JSONDATA_KEY);

		// Add customer fields
		dataObj.put(ReadFileDefineValues.CUSTOMERID_KEY, customerId);
		dataObj.put(ReadFileDefineValues.CUSTOMERDESC_KEY, StringUtils.EMPTY);

		// Add timeStamp to OfferProcess
		JSONArray arrayOfOfferProcess = dataObj
				.getJSONArray(ReadFileDefineValues.OFFERPROCESS_KEY);
		for (Object offerProcessObj : arrayOfOfferProcess) {
			JSONObject offerProcessJSONObj = (JSONObject) offerProcessObj;
			if (null == offerProcessJSONObj) {
				continue;
			}

			final int timeOffset = offerProcessJSONObj
					.getInt(ReadFileDefineValues.TIMEOFFSET_KEY);

			Timestamp newTimestamp = (Timestamp) serviceExecTime.clone();
			newTimestamp.setSeconds(newTimestamp.getSeconds() + timeOffset * 60);
			offerProcessJSONObj.put(ReadFileDefineValues.TIMESTAMP_KEY,
					newTimestamp.getTime());
		}

		return objOfOffering;
	}

	public List<? extends IServiceInformation> getServiceDatasByServiceName(
			String serviceName) throws RemoteException {

		ICollectServiceFakeData collectedFakeData = new CollectServiceFakeData();

		return collectedFakeData.getServiceDatasByServiceName(serviceName);
	}

	public JSONArray getServiceDatasAdvByServiceName(String serviceName)
			throws RemoteException {
		LOGGER.log(Level.INFO,
				"call service of getServiceDatasByServiceName ...");
		ICollectServiceFakeData collectedFakeData = new CollectServiceFakeData();

		return collectedFakeData.getServiceDatasAdvByServiceName(serviceName);
	}

	public static void main(String[] args) throws RemoteException {
		LOGGER.info("----------------------");
		ServiceCalendarMgrImpl serviceCalendarMgr = new ServiceCalendarMgrImpl();
		Timestamp testTime = new Timestamp(System.currentTimeMillis());
		boolean bIsOk = serviceCalendarMgr.saveServiceProcessInst(888, 999,
				201507201404L, testTime);

		/*
		 * System.out.println("getServiceProcess: \n" +
		 * serviceCalendarMgr.getServiceProcess(201506010105L, 0));
		 * 
		 * System.out.println("getAllServiceProcess: \n" +
		 * serviceCalendarMgr.getAllServiceProcess());
		 * 
		 * System.out.println("getServiceDatasByServiceName:"); List<? extends
		 * IServiceInformation> listOfSerInfo =
		 * serviceCalendarMgr.getServiceDatasByServiceName("Pick"); for
		 * (IServiceInformation serInfo : listOfSerInfo){
		 * System.out.println(serInfo.toString() + "\n"); }
		 */

		
		/*
		 * serviceCalendarMgr.saveServiceProcessInst(20, 300, 201506010105L,
		 * testTime);
		 */
		//serviceCalendarMgr.saveServiceProcessInst(20, 201506010105L, testTime);

		// System.out.println("getServiceProcessInst: \n" +
		//serviceCalendarMgr.getServiceProcessInst(20);
	}
}
