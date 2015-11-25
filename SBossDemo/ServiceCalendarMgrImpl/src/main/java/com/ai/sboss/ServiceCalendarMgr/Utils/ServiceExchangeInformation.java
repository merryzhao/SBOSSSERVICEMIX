package com.ai.sboss.ServiceCalendarMgr.Utils;

import org.apache.commons.lang.StringUtils;

import com.ai.sboss.ServiceCalendarMgrInterface.IServiceInformation;

public class ServiceExchangeInformation implements IServiceInformation {

	private static final long serialVersionUID = 2928230912305623496L;

	private String m_serviceName = StringUtils.EMPTY;

	private long m_serviceId = 0L;

	private String m_serviceCode = StringUtils.EMPTY;

	private long m_catalogId = 0L;

	public ServiceExchangeInformation(String serviceName, long serviceId,
			String serviceCode, long catalogId) {
		this.m_serviceName = serviceName;
		this.m_serviceId = serviceId;
		this.m_serviceCode = serviceCode;
		this.m_catalogId = catalogId;
	}

	public String getServiceName() {
		return this.m_serviceName;
	}

	public long getServiceId() {
		return this.m_serviceId;
	}

	public String getServiceCode() {
		return this.m_serviceCode;
	}

	public long getCatalogId() {
		return this.m_catalogId;
	}

	@Override
	public String toString() {
		return "ServiceName: " + this.getServiceName() + "\n" + "ServiceId: "
				+ this.getServiceId() + "\n" + "ServiceCode: "
				+ this.getServiceCode() + "\n" + "CatalogId: "
				+ this.getCatalogId();
	}
}
