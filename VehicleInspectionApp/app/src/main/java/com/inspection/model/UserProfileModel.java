package com.inspection.model;

public class UserProfileModel {

	int AccountID;
	String DeviceID;
	String Phone_ID;
	String Email;
	String Email2;
	String Email3;
	String UserName;
	String FirstName;
	boolean IsShop = false;
	String LastName;
	int MobileUserProfileId;
	String PhoneNumber;
	String Phone2;
	String Phone3;
	String Facebook;
	String Address;
	String ST;
	String City;
	String Zip;
    String SocialMedia;
    String SocialMediaID;
//	String AccSpecID;
	
	public UserProfileModel(){
		
	}



	public UserProfileModel(int accountID, String deviceID, String phone_ID,
							String email, String email2, String email3, String userName,
							String firstName, String lastName, int mobileUserProfileId,
							String phoneNumber, String phone2, String phone3, String facebook,
							String address, String sT, String city, String zip, String socialMedia, String socialMediaID) {//String accSpecID) {
		super();
		AccountID = accountID;
		DeviceID = deviceID;
		Phone_ID = phone_ID;
		Email = email;
		Email2 = email2;
		Email3 = email3;
		UserName = userName;
		FirstName = firstName;
		LastName = lastName;
		MobileUserProfileId = mobileUserProfileId;
		PhoneNumber = phoneNumber;
		Phone2 = phone2;
		Phone3 = phone3;
		Facebook = facebook;
		Address = address;
		ST = sT;
		City = city;
		Zip = zip;
        SocialMedia = socialMedia;
        SocialMediaID = socialMediaID;
//		AccSpecID = accSpecID;
	}



	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		this.Email = email;
	}
	
	

	public int getAccountID() {
		return AccountID;
	}

	public void setAccountID(int accountID) {
		AccountID = accountID;
	}

	public String getDeviceID() {
		return DeviceID;
	}

	public void setDeviceID(String deviceID) {
		DeviceID = deviceID;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getFirstName() {
		return FirstName;
	}

	public void setFirstName(String firstName) {
		this.FirstName = firstName;
	}

	public String getLastName() {
		return LastName;
	}

	public void setLastName(String lastName) {
		this.LastName = lastName;
	}

	public int getMobileUserProfileId() {
		return MobileUserProfileId;
	}

	public void setMobileUserProfileId(int mobileUserProfileId) {
		this.MobileUserProfileId = mobileUserProfileId;
	}

	public String getPhoneNumber() {
		return PhoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.PhoneNumber = phoneNumber;
	}

	public String getFacebook() {
		return Facebook;
	}

	public void setFacebook(String facebook) {
		Facebook = facebook;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getCity() {
		return City;
	}

	public void setCity(String city) {
		City = city;
	}

	public String getZip() {
		return Zip;
	}

	public void setZip(String zip) {
		Zip = zip;
	}

	public String getEmail2() {
		return Email2;
	}

	public void setEmail2(String email2) {
		Email2 = email2;
	}

	public String getEmail3() {
		return Email3;
	}

	public void setEmail3(String email3) {
		Email3 = email3;
	}

	public String getPhoneNumber2() {
		return Phone2;
	}

	public void setPhoneNumber2(String phoneNumber2) {
		Phone2 = phoneNumber2;
	}

	public String getPhoneNumber3() {
		return Phone3;
	}

	public void setPhoneNumber3(String phoneNumber3) {
		Phone3 = phoneNumber3;
	}

	public String getState() {
		return ST;
	}

	public void setState(String state) {
		ST = state;
	}

	public String getPhone_ID() {
		return Phone_ID;
	}

	public void setPhone_ID(String phone_ID) {
		Phone_ID = phone_ID;
	}

    public String getSocialMedia() {
        return SocialMedia;
    }

    public void setSocialMedia(String socialMedia) {
        SocialMedia = socialMedia;
    }

    public String getSocialMediaID() {
        return SocialMediaID;
    }

    public void setSocialMediaID(String socialMediaID) {
        SocialMediaID = socialMediaID;
    }

//	public String getAccSpecID() {
//		return AccSpecID;
//	}
//
//	public void setAccSpecID(String accSpecID) {
//		AccSpecID = accSpecID;
//	}

	public boolean isShop() {
		return IsShop;
	}

	public void setShop(boolean shop) {
		IsShop = shop;
	}
}
