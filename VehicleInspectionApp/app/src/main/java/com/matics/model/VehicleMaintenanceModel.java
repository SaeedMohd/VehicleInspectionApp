package com.matics.model;

import java.util.Date;

public class VehicleMaintenanceModel {

//	String vin;
	String Facility;
	String InvoiceID;
	String ServiceDate;
	
	public VehicleMaintenanceModel(){
		
	}

	public VehicleMaintenanceModel(String facility, String invoiceID,
			String serviceDate) {
		super();
		Facility = facility;
		InvoiceID = invoiceID;
		ServiceDate = serviceDate;
	}

	public String getFacility() {
		return Facility;
	}

	public void setFacility(String facility) {
		Facility = facility;
	}

	public String getInvoiceID() {
		return InvoiceID;
	}

	public void setInvoiceID(String invoiceID) {
		InvoiceID = invoiceID;
	}

	public String getServiceDate() {
		return ServiceDate;
	}

	public void setServiceDate(String serviceDate) {
		ServiceDate = serviceDate;
	}

//	public String getVin() {
//		return vin;
//	}
//
//	public void setVin(String vin) {
//		this.vin = vin;
//	}

}
