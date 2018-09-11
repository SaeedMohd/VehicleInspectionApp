package com.inspection.Utils

import com.inspection.model.FacilityDataModel
import java.text.SimpleDateFormat
import java.util.*

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
    //var clubCode=""
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
    val getSpecialistNameFromEmail = "http://144.217.24.163:5000/getSpecialistNameFromEmail?specialistEmail="
    val getVisitationPlanningList = "http://144.217.24.163:5000/getVisitationPlanningList?facilityName=%s&month=%s&year=%s"
    val submitContactInfoAddress = "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateFacilityAddressData?facnum="
    val submitFacilityGeneralInfo = "https://dev.facilityappointment.com/ACEAPI.asmx/updateFacilityInfo?facNum="
    val submitFacilityHours = "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateFacilityHoursData?facnum="
    val submitFacilityEmail = "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateFacilityEmailData?facnum="
    val submitFacilityAddress = "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateFacilityAddressData?facnum="
    val submitFacilityPhone = "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateFacilityPhoneData?facNum="
    //AAA APIs WebServices
    val getClubCodes = "http://144.217.24.163:5000/getClubCodes?clubCode="
    val getFacilityData = "https://dev.facilityappointment.com/ACEAPI.asmx/GetFacilityData?facnum=%d&clubcode=%s"
    val getCopyFacilityData = "https://dev.facilityappointment.com/ACEAPI.asmx/GetFacilityData?facnum=2518&clubcode=004"
    val getTypeTables = "https://dev.facilityappointment.com/ACEAPI.asmx/GetTypeTables"
    val getVisitations = "https://dev.facilityappointment.com/ACEAPI.asmx/GetVisitations?"
    val getFacilitiesWithFilters = "http://144.217.24.163:5000/getFacilitiesWithFilters?"

    var facNo= if (FacilityDataModel.getInstance().tblFacilities.size>0) FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() else "2089"
    val UpdateAARPortalAdminData = "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateAARPortalAdminData?facNum="
    val UpdateAmendmentOrderTrackingData = "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateAmendmentOrderTrackingData?facNum="
    val UpdateDeficiencyData = "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateDeficiencyData?facNum="
    val UpdateProgramsData = "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateProgramsData?facNum="
    val UpdateFacilityServicesData ="https://dev.facilityappointment.com/ACEAPI.asmx/UpdateFacilityServicesData?facNum="
    val UpdateAffiliationsData="https://dev.facilityappointment.com/ACEAPI.asmx/UpdateAffiliationsData?facNum="
    val UpdatePaymentMethodsData="https://dev.facilityappointment.com/ACEAPI.asmx/UpdatePaymentMethodsData?facnum="
    val UpdateFacilityLanguageData="https://dev.facilityappointment.com/ACEAPI.asmx/UpdateFacilityLanguageData?facNum="

    val getSpecialistIdsForClubCode = "http://144.217.24.163:5000/getSpecialistsForClubCode?"
}
