package com.matics.model;

public class AccountDetailModel {

	private UserAccountModel userAccountModel;
	private UserProfileModel userProfileModel;
	private VehicleProfileModel vehicleProfileModel;

	public AccountDetailModel(){

	}

	public AccountDetailModel(UserAccountModel userAccountModel, UserProfileModel userProfileModel, VehicleProfileModel vehicleProfileModel) {
		this.userAccountModel=userAccountModel;
		this.userProfileModel = userProfileModel;
		this.vehicleProfileModel=vehicleProfileModel;
		}

	public UserAccountModel getUserAccountModel() {
		return userAccountModel;
	}

	public void setUserAccountModel(UserAccountModel userAccountModel) {
		this.userAccountModel = userAccountModel;
	}

	public UserProfileModel getUserProfileModel() {
		return userProfileModel;
	}

	public void setUserProfileModel(UserProfileModel userProfileModel) {
		this.userProfileModel = userProfileModel;
	}

	public VehicleProfileModel getVehicleProfileModel() {
		return vehicleProfileModel;
	}

	public void setVehicleProfileModel(VehicleProfileModel vehicleProfileModel) {
		this.vehicleProfileModel = vehicleProfileModel;
	}
	
	

}
