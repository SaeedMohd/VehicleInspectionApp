package com.inspection.model;


/**
 * Created by devsherif on 3/2/15.
 */
public class AccelerationModel {
    public int MobileUserProfileID;
    public String vin;
    public float accelerationX;
    public float accelerationY;
    public float accelerationZ;
    public String latitude;
    public String longitude;
    public String sendDate;

    public int getMobileUserProfileID() {
        return MobileUserProfileID;
    }

    public void setMobileUserProfileID(int mobileUserProfileID) {
        this.MobileUserProfileID = mobileUserProfileID;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public float getAccelerationX() {
        return accelerationX;
    }

    public void setAccelerationX(float accelerationX) {
        this.accelerationX = accelerationX;
    }

    public float getAccelerationY() {
        return accelerationY;
    }

    public void setAccelerationY(float accelerationY) {
        this.accelerationY = accelerationY;
    }

    public float getAccelerationZ() {
        return accelerationZ;
    }

    public void setAccelerationZ(float accelerationZ) {
        this.accelerationZ = accelerationZ;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }
}
