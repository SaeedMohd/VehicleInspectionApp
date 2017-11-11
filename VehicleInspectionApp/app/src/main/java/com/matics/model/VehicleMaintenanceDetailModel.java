package com.matics.model;

public class VehicleMaintenanceDetailModel {

	String Mileage;
	String ServiceDate;
	String[] ServicePerformedList;

	public VehicleMaintenanceDetailModel(String mileage, String serviceDate,
			String[] servicePerformedList) {
		super();
		Mileage = mileage;
		ServiceDate = serviceDate;
		ServicePerformedList = servicePerformedList;
	}

	public String getMileage() {
		return Mileage;
	}

	public void setMileage(String mileage) {
		Mileage = mileage;
	}

	public String getServiceDate() {
		return ServiceDate;
	}

	public void setServiceDate(String serviceDate) {
		ServiceDate = serviceDate;
	}

	public String[] getServicePerformedList() {
		return ServicePerformedList;
	}

	public void setServicePerformedList(String[] servicePerformedList) {
		ServicePerformedList = servicePerformedList;
	}

}
