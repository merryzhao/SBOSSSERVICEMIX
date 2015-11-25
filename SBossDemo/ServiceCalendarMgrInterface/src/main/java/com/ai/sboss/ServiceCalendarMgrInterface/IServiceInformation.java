package com.ai.sboss.ServiceCalendarMgrInterface;

import java.io.Serializable;

public interface IServiceInformation extends Serializable {
	
	public String getServiceName();
	
	public long getServiceId();
	
	public String getServiceCode();
	
	public long getCatalogId();
}
