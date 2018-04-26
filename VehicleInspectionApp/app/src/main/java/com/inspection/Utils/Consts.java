package com.inspection.Utils;

import java.text.SimpleDateFormat;

/**
 * QuickBlox team
 */
public class Consts {
    static final String APP_ID = "37950";
    static final String AUTH_KEY = "wA7By6LqUzWFnrE";
    static final String AUTH_SECRET = "5-cV7pRd-t73fEJ";
    static final String ACCOUNT_KEY = "bk42gqT5SCqr6y1Tuzxp";

    public static final String VERSION_NUMBER = "1.0";

    public static final int CALL_ACTIVITY_CLOSE = 1000;

    //CALL ACTIVITY CLOSE REASONS
    public static final int CALL_ACTIVITY_CLOSE_WIFI_DISABLED = 1001;
    public static final String WIFI_DISABLED = "wifi_disabled";

    public final static String OPPONENTS = "opponents";
    public static final String CONFERENCE_TYPE = "conference_type";

    public static final SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat appFormat = new SimpleDateFormat("dd MMM yyyy");

    // WebServices
    public static final String personnelTypeURL = "http://144.217.24.163:5000/getPersonnelTypes?facilityId=";
    public static final String personnelDetailsURL = "http://144.217.24.163:5000/getPersonnelsDetails?facilityId=%s&personnelTypeId=%d";
    public static final String allPersonnel = "http://144.217.24.163:5000/getAllPersonnelsDetails";
    public static final String personnelDetailsWithIdUrl = "http://144.217.24.163:5000/getPersonnelDetailsWithId?personnelId=";
    public static final String facilityLocationsURL = "http://144.217.24.163:5000/getFacilityAddresses?facilityId=";
    public static final String paymentMethodsURL = "http://144.217.24.163:5000/getPaymentMethods";
    public static final String facilityHoursURL = "http://144.217.24.163:5000/getFacilityHours?facilityId=";
    public static final String facilityScopeOfSvcURL = "http://144.217.24.163:5000/getScopeOfServicDetails?facilityId=";
    public static final String programTypesURL = "http://144.217.24.163:5000/getProgramTypes";
    public static final String facilityCompleteURL = "http://144.217.24.163:5000/getFacilities?facilityLike=";
    public static final String getFacilityComplaintsURL= "http://144.217.24.163:5000/getFacilityComplaints?facilityId=";
    public static final String facilityEmailPhoneURL = "http://144.217.24.163:5000/getFacilityEmailAndPhone?facilityId=";
    public static final String contractSignerDtlsURL = "http://144.217.24.163:5000/getContractSignerDetails?personnelId=";
    public static final String getfacilitiesURL = "http://144.217.24.163:5000/getFacilities?facilityName=";
    public static final String getFacilityWithIdUrl = "http://144.217.24.163:5000/getFacilityWithId?facilityId=";
    public static final String getLastInspectionForFacility= "http://144.217.24.163:5000/getLastAnnualVisitationInspectionForFacility?facilityId=";
    public static final String getEmailFromFacilityAndId = "http://144.217.24.163:5000/getEmailFromFacilityAndId?facilityId=%s&emailId=%d";
    public static final String getPhoneNumberWithFacilityAndId = "http://144.217.24.163:5000/getPhoneNumberWithFacilityAndId?facilityId=%s&phoneId=%d";
    public static final String getVehicleServicesURL = "http://144.217.24.163:5000/getVehicleServices";
    public static final String getVehiclesURL = "http://144.217.24.163:5000/getVehicles";
    public static final String getFacilityPrograms= "http://144.217.24.163:5000/getFacilityPrograms?facilityId=";
    public static final String getFacilityVisitationRecords= "http://144.217.24.163:5000/getVisitationRecords?facilityName=";
    public static final String getAnnualVisitations= "http://144.217.24.163:5000/getAnnualVisitations?%s";
    public static final String getFacilityAffiliations= "http://144.217.24.163:5000/getFacilityAffiliations?facilityId=";
    public static final String getAffTypesURL = "http://144.217.24.163:5000/getAffiliationTypes";
    public static final String getAllPersonnelDetails = "http://144.217.24.163:5000/getAllPersonnelsDetails";


    //AAA APIs WebServices
    public static final String getFacilityData= "https://dev.facilityappointment.com/ACEAPI.asmx/ProcessRequest?facnum=%d&clubcode=%s";
}
