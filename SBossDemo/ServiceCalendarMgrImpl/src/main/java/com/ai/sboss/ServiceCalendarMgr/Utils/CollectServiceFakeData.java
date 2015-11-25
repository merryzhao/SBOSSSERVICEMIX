package com.ai.sboss.ServiceCalendarMgr.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.ai.sboss.ServiceCalendarMgrImpl.ServiceCalendarMgrImpl;
import com.ai.sboss.ServiceCalendarMgrInterface.IServiceInformation;

public class CollectServiceFakeData implements ICollectServiceFakeData {
	private static Logger LOGGER = Logger
			.getLogger(CollectServiceFakeData.class.getName());

	private String m_fakeDataFileName = StringUtils.EMPTY;

	private List<IServiceInformation> m_listOfServiceDatas = null;

	public CollectServiceFakeData() {
		m_fakeDataFileName = ReadFileDefineValues.SERVICEFAKEDATA_FOLDER+File.separator+ReadFileDefineValues.SERVICEFAKEDATA_FILENAME;
	}

	public List<IServiceInformation> getServiceDatasByServiceName(
			String serviceName) {

		if (StringUtils.isEmpty(serviceName)) {
			return null;
		}

		loadFakeServiceData();

		return findServicDatasByServiceName(serviceName);
	}

	public JSONArray getServiceDatasAdvByServiceName(String serviceName) {
		LOGGER.log(Level.INFO,
				"call service of getServiceDatasAdvByServiceName ...");
		if (StringUtils.isEmpty(serviceName)) {
			return null;
		}

		return findServicDatasAdvByServiceName(serviceName);
	}

	private void loadFakeServiceData() {

		if (null != m_listOfServiceDatas && !m_listOfServiceDatas.isEmpty()) {
			return;
		}

		String fileContent = StringUtils.EMPTY;
		try {
			IReadFile readFile = new ReadConcreteFile();
			fileContent = readFile.readFile(m_fakeDataFileName);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		if (!fileContent.isEmpty()) {

			if (null == m_listOfServiceDatas) {
				m_listOfServiceDatas = new ArrayList<IServiceInformation>();
			}

			JSONObject fakeJSONObject = JSONObject.fromObject(fileContent);
			JSONArray fakeJSONDataArray = fakeJSONObject
					.getJSONArray(ReadFileDefineValues.JSONDATA_KEY);
			for (Object jsonObj : fakeJSONDataArray) {

				if (jsonObj instanceof JSONObject) {

					JSONObject jsonDataObj = (JSONObject) jsonObj;

					IServiceInformation serviceInfo = new ServiceExchangeInformation(
							jsonDataObj
									.getString(ReadFileDefineValues.SERVICENAME_KEY),
							jsonDataObj
									.getLong(ReadFileDefineValues.SERVICEID_KEY),
							jsonDataObj
									.getString(ReadFileDefineValues.SERVICECODE_KEY),
							jsonDataObj
									.getLong(ReadFileDefineValues.CATALOGID_KEY));

					m_listOfServiceDatas.add(serviceInfo);
				}
			}
			LOGGER.log(Level.INFO,
					"load Service List=>"+m_listOfServiceDatas.toString());
		}
	}

	private List<IServiceInformation> findServicDatasByServiceName(
			String serviceName) {

		List<IServiceInformation> listOfRetServiceDatas = new ArrayList<IServiceInformation>();

		if (null != m_listOfServiceDatas && !m_listOfServiceDatas.isEmpty()) {

			for (IServiceInformation serInfo : m_listOfServiceDatas) {

				if (serInfo.getServiceName().contains(serviceName)) {

					IServiceInformation serviceInfo = new ServiceExchangeInformation(
							serInfo.getServiceName(), serInfo.getServiceId(),
							serInfo.getServiceCode(), serInfo.getCatalogId());

					listOfRetServiceDatas.add(serviceInfo);
				}
			}
		}

		return listOfRetServiceDatas;
	}

	private JSONArray findServicDatasAdvByServiceName(String serviceName) {

		String fileContent = StringUtils.EMPTY;
		try {
			IReadFile readFile = new ReadConcreteFile();
			LOGGER.info("serviceFakeDataSet.process=>"+m_fakeDataFileName);
			fileContent = readFile.readFile(m_fakeDataFileName);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		JSONArray retJSONArray = new JSONArray();
		if (!fileContent.isEmpty()) {

			JSONObject fakeJSONObject = JSONObject.fromObject(fileContent);
			JSONArray fakeJSONDataArray = fakeJSONObject
					.getJSONArray(ReadFileDefineValues.JSONDATA_KEY);
			for (Object jsonObj : fakeJSONDataArray) {

				if (jsonObj instanceof JSONObject) {
					JSONObject jsonDataObj = (JSONObject) jsonObj;
					LOGGER.info("Scan key=>"+jsonDataObj.getString(ReadFileDefineValues.SERVICENAME_KEY));
					if (jsonDataObj.getString(ReadFileDefineValues.SERVICENAME_KEY).contains(serviceName)){
						retJSONArray.add(jsonDataObj);
					}
				}
			}
		} else {
			LOGGER.info("fileContent empty");
		}
		
		return retJSONArray;
	}
}
