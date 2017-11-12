package com.inspection.model;

import java.util.ArrayList;

/**
 * Created by sheri on 2/15/2017.
 */

public class CompetitorModel {

    private String Address;
    private String City;
    private int CompetitorID;
    private float Lat;
    private float Lon;
    private String CompetitorName;
    private String St;
    private ArrayList<CompetitorModel.CompetitorGeoFence> CompetitorGeoFence;
    private ArrayList<CompetitorModel.CompetitorPhone> CompetitorPhone;
    private int Zip;

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

    public int getCompetitorID() {
        return CompetitorID;
    }

    public void setCompetitorID(int competitorID) {
        this.CompetitorID = competitorID;
    }

    public float getLat() {
        return Lat;
    }

    public void setLat(float lat) {
        this.Lat = lat;
    }

    public float getLon() {
        return Lon;
    }

    public void setLon(float lon) {
        this.Lon = lon;
    }

    public String getCompetitorName() {
        return CompetitorName;
    }

    public void setCompetitorName(String competitorName) {
        this.CompetitorName = competitorName;
    }

    public String getSt() {
        return St;
    }

    public void setSt(String st) {
        this.St = st;
    }

    public ArrayList<CompetitorModel.CompetitorGeoFence> getCompetitorGeoFence() {
        return CompetitorGeoFence;
    }

    public void setCompetitorGeoFence(ArrayList<CompetitorModel.CompetitorGeoFence> competitorGeoFence) {
        this.CompetitorGeoFence = competitorGeoFence;
    }

    public ArrayList<CompetitorModel.CompetitorPhone> getCompetitorPhone() {
        return CompetitorPhone;
    }

    public void setCompetitorPhone(ArrayList<CompetitorModel.CompetitorPhone> competitorPhone) {
        this.CompetitorPhone = competitorPhone;
    }

    public int getZip() {
        return Zip;
    }

    public void setZip(int zip) {
        this.Zip = zip;
    }

    public class CompetitorGeoFence {
        private int CampaignID;
        private String CampaignName;
        private int GeoFenceID;
        private int GeoFenceMinutes;
        private float Latitude;
        private float Longitude;
        private int Radius;

        public int getCampaignID() {
            return CampaignID;
        }

        public void setCampaignID(int campaignID) {
            this.CampaignID = campaignID;
        }

        public String getCampaignName() {
            return CampaignName;
        }

        public void setCampaignName(String campaignName) {
            this.CampaignName = campaignName;
        }

        public int getGeoFenceID() {
            return GeoFenceID;
        }

        public void setGeoFenceID(int geoFenceID) {
            this.GeoFenceID = geoFenceID;
        }

        public int getGeoFenceMinutes() {
            return GeoFenceMinutes;
        }

        public void setGeoFenceMinutes(int geoFenceMinutes) {
            this.GeoFenceMinutes = geoFenceMinutes;
        }

        public float getLatitude() {
            return Latitude;
        }

        public void setLatitude(float latitude) {
            this.Latitude = latitude;
        }

        public float getLongitude() {
            return Longitude;
        }

        public void setLongitude(float longitude) {
            this.Longitude = longitude;
        }

        public int getRadius() {
            return Radius;
        }

        public void setRadius(int radius) {
            this.Radius = radius;
        }
    }

    public class CompetitorPhone {
        private int CampaignID;
        private String CampaignName;
        private int PhoneID;
        private int PhoneMinutes;
        private String PhoneNum;

        public int getCampaignID() {
            return CampaignID;
        }

        public void setCampaignID(int campaignID) {
            this.CampaignID = campaignID;
        }

        public String getCampaignName() {
            return CampaignName;
        }

        public void setCampaignName(String campaignName) {
            this.CampaignName = campaignName;
        }

        public int getPhoneID() {
            return PhoneID;
        }

        public void setPhoneID(int phoneID) {
            this.PhoneID = phoneID;
        }

        public int getPhoneMinutes() {
            return PhoneMinutes;
        }

        public void setPhoneMinutes(int phoneMinutes) {
            this.PhoneMinutes = phoneMinutes;
        }

        public String getPhoneNum() {
            return PhoneNum;
        }

        public void setPhoneNum(String phoneNum) {
            this.PhoneNum = phoneNum;
        }
    }
}
