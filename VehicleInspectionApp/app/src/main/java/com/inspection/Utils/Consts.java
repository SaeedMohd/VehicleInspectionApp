package com.inspection.Utils;

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

    // WebServices
    public static final String personnelTypeURL = "http://www.jet-matics.com:5000/getPersonnelTypes?facilityId=540554";
    public static final String personnelDetailsURL = "http://jet-matics.com:5000/getPersonnelsDetails?";
    public static final String facilityLocationsURL = "http://www.jet-matics.com:5000/getFacilityAddresses?facilityId=541410";
    public static final String paymentMethodsURL = "http://www.jet-matics.com:5000/getPaymentMethods";
    public static final String facilityHoursURL = "http://www.jet-matics.com:5000/getFacilityHours?facilityId=540554";
    public static final String facilityScopeOfSvcURL = "http://www.jet-matics.com:5000/getScopeOfServicDetails?facilityId=540554";
    public static final String programTypesURL = "http://www.jet-matics.com:5000/getProgramTypes";
    public static final String facilityCompleteURL = "http://www.jet-matics.com:5000/getFacilities?facilityLike=";
    public static final String facilityComplaintsURL= "http://www.jet-matics.com:5000/getFacilityComplaints?facilityId=";
    public static final String facilityEmailPhoneURL = "http://www.jet-matics.com:5000/getFacilityEmailAndPhone?facilityId=";
    public static final String contractSignerDtlsURL = "http://www.jet-matics.com:5000/getContractSignerDetails?personnelId=";
    public static final String getfacilitiesURL = "http://www.jet-matics.com:5000/getFacilities?facilityName=";
    public static final String getLastInspectionForFacility= "http://www.jet-matics.com:5000/getLastAnnualVisitationInspectionForFacility?facilityId=";


}
