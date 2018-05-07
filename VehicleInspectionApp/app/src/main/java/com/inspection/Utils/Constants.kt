package com.inspection.Utils

import java.text.SimpleDateFormat

/**
 * QuickBlox team
 */
object Constants {
    internal val APP_ID = "37950"
    internal val AUTH_KEY = "wA7By6LqUzWFnrE"
    internal val AUTH_SECRET = "5-cV7pRd-t73fEJ"
    internal val ACCOUNT_KEY = "bk42gqT5SCqr6y1Tuzxp"

    val VERSION_NUMBER = "1.0"

    val CALL_ACTIVITY_CLOSE = 1000

    //CALL ACTIVITY CLOSE REASONS
    val CALL_ACTIVITY_CLOSE_WIFI_DISABLED = 1001
    val WIFI_DISABLED = "wifi_disabled"

    val OPPONENTS = "opponents"
    val CONFERENCE_TYPE = "conference_type"

    val dbFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val appFormat = SimpleDateFormat("dd MMM yyyy")

    // WebServices
    val personnelTypeURL = "http://144.217.24.163:5000/getPersonnelTypes?facilityId="
    val personnelDetailsURL = "http://144.217.24.163:5000/getPersonnelsDetails?facilityId=%s&personnelTypeId=%d"
    val allPersonnel = "http://144.217.24.163:5000/getAllPersonnelsDetails"
    val personnelDetailsWithIdUrl = "http://144.217.24.163:5000/getPersonnelDetailsWithId?personnelId="
    val facilityLocationsURL = "http://144.217.24.163:5000/getFacilityAddresses?facilityId="
    val paymentMethodsURL = "http://144.217.24.163:5000/getPaymentMethods"
    val facilityHoursURL = "http://144.217.24.163:5000/getFacilityHours?facilityId="
    val facilityScopeOfSvcURL = "http://144.217.24.163:5000/getScopeOfServicDetails?facilityId="
    val programTypesURL = "http://144.217.24.163:5000/getProgramTypes"
    val facilityCompleteURL = "http://144.217.24.163:5000/getFacilities?facilityLike="
    val getFacilityComplaintsURL = "http://144.217.24.163:5000/getFacilityComplaints?facilityId="
    val facilityEmailPhoneURL = "http://144.217.24.163:5000/getFacilityEmailAndPhone?facilityId="
    val contractSignerDtlsURL = "http://144.217.24.163:5000/getContractSignerDetails?personnelId="
    val getfacilitiesURL = "http://144.217.24.163:5000/getFacilities?facilityName="
    val getFacilityWithIdUrl = "http://144.217.24.163:5000/getFacilityWithId?facilityId="
    val getLastInspectionForFacility = "http://144.217.24.163:5000/getLastAnnualVisitationInspectionForFacility?facilityId="
    val getEmailFromFacilityAndId = "http://144.217.24.163:5000/getEmailFromFacilityAndId?facilityId=%s&emailId=%d"
    val getPhoneNumberWithFacilityAndId = "http://144.217.24.163:5000/getPhoneNumberWithFacilityAndId?facilityId=%s&phoneId=%d"
    val getVehicleServicesURL = "http://144.217.24.163:5000/getVehicleServices"
    val getVehiclesURL = "http://144.217.24.163:5000/getVehicles"
    val getFacilityPrograms = "http://144.217.24.163:5000/getFacilityPrograms?facilityId="
    val getFacilityVisitationRecords = "http://144.217.24.163:5000/getVisitationRecords?facilityName="
    val getAnnualVisitations = "http://144.217.24.163:5000/getAnnualVisitations?%s"
    val getFacilityAffiliations = "http://144.217.24.163:5000/getFacilityAffiliations?facilityId="
    val getAffTypesURL = "http://144.217.24.163:5000/getAffiliationTypes"
    val getAllPersonnelDetails = "http://144.217.24.163:5000/getAllPersonnelsDetails"
    val getAllFacilities = "http://144.217.24.163:5000/getAllFacilities"
    val getAllSpecialists = "http://144.217.24.163:5000/getAllSpecialists"


    //AAA APIs WebServices
    val getFacilityData = "https://dev.facilityappointment.com/ACEAPI.asmx/ProcessRequest?facnum=%d&clubcode=%s"
}