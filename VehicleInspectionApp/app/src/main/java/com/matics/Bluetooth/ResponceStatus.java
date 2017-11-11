package com.matics.Bluetooth;

public class ResponceStatus {
	
	int resCode;
	String responseStatus;
	String resultJson;

	public String getResultJson() {
		return resultJson;
	}

	public void setResultJson(String resultJson) {
		this.resultJson = resultJson;
	}

	public ResponceStatus() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getResCode() {
		return resCode;
	}

	public void setResCode(int serverResponseCode) {
		this.resCode = serverResponseCode;
	}

	public String getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}
}
