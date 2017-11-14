package com.inspection.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VehicleProfileModel implements Serializable {

    @SerializedName("VIN")
    public String VIN = "";

    @SerializedName("lastupdate")
    public String lastupdate = "";

    @SerializedName("MFYear")
    public String MFYear = "";

    @SerializedName("Make")
    public String Make = "";

    @SerializedName("Model")
    public String Model = "";

    @SerializedName("Mileage")
    public String Mileage = "";

    @SerializedName("AndroidPhoneId")
    public String AndroidPhoneId = "";

    @SerializedName("VinRetrievable")
    public boolean VinRetrievable = false;

    @SerializedName("License_Num")
    public String License_Num = "";

    @SerializedName("License_State")
    public String License_State = "";

    @SerializedName("BtID")
    public String BtID = "";

    @SerializedName("VINID")
    public String VINID = "";

    @SerializedName("Reason")
    public String Reason = "";

    @SerializedName("VehicleHealth")
    public int VehicleHealth = 70;

    public VehicleProfileModel() {

    }


    public String getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(String lastupdate) {
        this.lastupdate = lastupdate;
    }

    public String getYear() {
        return MFYear;
    }

    public void setYear(String year) {
        this.MFYear = year;
    }

    public String getMake() {
        return Make;
    }

    public void setMake(String make) {
        this.Make = make;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        this.Model = model;
    }

    public String getMileage() {
        return Mileage;
    }

    public void setMileage(String mileage) {
        this.Mileage = mileage;
    }

    public String getDevice_Id() {
        return AndroidPhoneId;
    }

    public void setDevice_Id(String device_Id) {
        AndroidPhoneId = device_Id;
    }

    public boolean getVinRetrievable() {
        return VinRetrievable;
    }

    public void setVinRetrievable(boolean vinRetrievable) {
        this.VinRetrievable = vinRetrievable;
    }

    public String getLicense_Num() {
        return License_Num;
    }

    public void setLicense_Num(String license_Num) {
        License_Num = license_Num;
    }

    public String getLicense_State() {
        return License_State;
    }

    public void setLicense_State(String license_State) {
        License_State = license_State;
    }

    public String getBtID() {
        return BtID;
    }

    public void setBtID(String btID) {
        BtID = btID;
    }


    public String getVIN() {
        return VIN;
    }

    public void setVIN(String vIN) {
        VIN = vIN;
    }

    public String getVINID() {
        return VINID;
    }

    public void setVINID(String vINID) {
        VINID = vINID;
    }


    public String getMFYear() {
        return MFYear;
    }


    public void setMFYear(String mFYear) {
        MFYear = mFYear;
    }


    public String getAndroidPhoneId() {
        return AndroidPhoneId;
    }


    public void setAndroidPhoneId(String androidPhoneId) {
        AndroidPhoneId = androidPhoneId;
    }


    public String getReason() {
        return Reason;
    }


    public void setReason(String reason) {
        Reason = reason;
    }


    public int getVehicleHealth() {
        return VehicleHealth;
    }


    public void setVehicleHealth(int vehicleHealth) {
        VehicleHealth = vehicleHealth;
    }


}
