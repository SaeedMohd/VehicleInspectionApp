package com.inspection.adapter;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetApplicableServicesResultData {

	@SerializedName("Component")
	public String Component;

	@SerializedName("DueMileage")
	public String DueMileage;
	
	@SerializedName("DueMonths")
	public String DueMonths;
	
	@SerializedName("Type")
	public String Type;
	
	@SerializedName("aapplicableServices")
	public List<aapplicableServicesData>aapplicableServicesData;

}
