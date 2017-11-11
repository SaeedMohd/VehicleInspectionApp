package com.matics.model;

/**
 * Created by devsherif on 9/17/15.
 */
public class VehicleServicesModel {

    private String ServiceDesc;
    private String ServiceDate;

    public void setServiceDesc(String serviceDesc) {
        ServiceDesc = serviceDesc;
    }

    public void setServiceDate(String serviceDate) {
        ServiceDate = serviceDate;
    }

    public String getServiceDesc() {
        return ServiceDesc;
    }

    public String getServiceDate() {
        return ServiceDate;
    }
}
