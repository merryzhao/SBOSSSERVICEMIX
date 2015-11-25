package com.ai.sboss.ServiceCalendarMgr.Utils;

import java.util.List;

import net.sf.json.JSONArray;

import com.ai.sboss.ServiceCalendarMgrInterface.IServiceInformation;

public interface ICollectServiceFakeData {
	
	public List<IServiceInformation> getServiceDatasByServiceName(
			String serviceName);
	
	public JSONArray getServiceDatasAdvByServiceName(String serviceName);
}
