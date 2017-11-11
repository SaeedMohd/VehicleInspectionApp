package com.matics.model;

public class UserAccountModel {

	private String AccountFullName;
	private int AccountId;
	private String Address;
	private String City;
	private String Email;
	private Error Error;
	private String FacebookURL;
	private String Phone;
	private String PhotoURL;
	private String ReferFriendURL;
	private String RequestAnAppointmentURL;
	private String RequestaQuote;
	private String State;
	private String TwitterURL;
	private String Zip;
	private String Lon;
	private String Lat;
	
	
	
	public UserAccountModel(){
		
	}

	public UserAccountModel(String accountFullName, int accountId,
			String address, String city,
			com.matics.model.UserAccountModel.Error error, String facebookURL,
			String phone, String photoURL, String referFriendURL,
			String requestAnAppointmentURL, String requestaQuote, String state,
			String twitterURL, String zip, String lon, String lat) {
		super();
		AccountFullName = accountFullName;
		AccountId = accountId;
		Address = address;
		City = city;
		Error = error;
		FacebookURL = facebookURL;
		Phone = phone;
		PhotoURL = photoURL;
		ReferFriendURL = referFriendURL;
		RequestAnAppointmentURL = requestAnAppointmentURL;
		RequestaQuote = requestaQuote;
		State = state;
		TwitterURL = twitterURL;
		Zip = zip;
		Lon = lon;
		Lat = lat;
	}

	private class Error{
		String ErrorDetail;
		boolean IsSuccess;
		int OneVehicleHealth;
		String VIN;
		int[] VehicleHealth;
		
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getAccountFullName() {
		return AccountFullName;
	}

	public void setAccountFullName(String accountFullName) {
		this.AccountFullName = accountFullName;
	}

	public int getAccountId() {
		return AccountId;
	}

	public void setAccountId(int accountId) {
		this.AccountId = accountId;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		this.Address = address;
	}

	public String getCity() {
		return City;
	}

	public void setCity(String city) {
		this.City = city;
	}

	public Error getError() {
		return Error;
	}

	public void setError(Error error) {
		this.Error = error;
	}

	public String getFacebookUrl() {
		return FacebookURL;
	}

	public void setFacebookUrl(String facebookUrl) {
		this.FacebookURL = facebookUrl;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		this.Phone = phone;
	}

	public String getPhotoUrl() {
		return PhotoURL;
	}

	public void setPhotoUrl(String photoUrl) {
		this.PhotoURL = photoUrl;
	}

	public String getReferFriendURL() {
		return ReferFriendURL;
	}

	public void setReferFriendURL(String referFriendURL) {
		this.ReferFriendURL = referFriendURL;
	}

	public String getRequestAnAppointmentURL() {
		return RequestAnAppointmentURL;
	}

	public void setRequestAnAppointmentURL(String requestAnAppointmentURL) {
		this.RequestAnAppointmentURL = requestAnAppointmentURL;
	}

	public String getFacebookURL() {
		return FacebookURL;
	}



	public void setFacebookURL(String facebookURL) {
		FacebookURL = facebookURL;
	}



	public String getPhotoURL() {
		return PhotoURL;
	}



	public void setPhotoURL(String photoURL) {
		PhotoURL = photoURL;
	}



	public String getRequestAQuote() {
		return RequestaQuote;
	}



	public void setRequestAQuote(String requestAQuote) {
		RequestaQuote = requestAQuote;
	}



	public String getState() {
		return State;
	}

	public void setState(String state) {
		this.State = state;
	}

	public String getTwitterURL() {
		return TwitterURL;
	}

	public void setTwitterURL(String twitterURL) {
		this.TwitterURL = twitterURL;
	}

	public String getZip() {
		return Zip;
	}

	public void setZip(String zip) {
		this.Zip = zip;
	}

	public String getRequestaQuote() {
		return RequestaQuote;
	}

	public void setRequestaQuote(String requestaQuote) {
		RequestaQuote = requestaQuote;
	}

	public String getLon() {
		return Lon;
	}

	public void setLon(String lon) {
		Lon = lon;
	}

	public String getLat() {
		return Lat;
	}

	public void setLat(String lat) {
		Lat = lat;
	}
	 
	 
	

}
