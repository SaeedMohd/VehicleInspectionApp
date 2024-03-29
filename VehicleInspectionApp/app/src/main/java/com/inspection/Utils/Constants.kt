package com.inspection.Utils

import com.inspection.BuildConfig
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
    public  val devPort = "5001/"
    public  val uatPort = "5002/"
    public val prodPort = "5000/"
    public var permanentURL = "http://144.217.24.163:" + if (BuildConfig.FLAVOR.equals("dev")) devPort else (if (BuildConfig.FLAVOR.equals("uat")) uatPort else prodPort)
    public var permanentURLWithDomain = "http://jet-matics.com:" + if (BuildConfig.FLAVOR.equals("dev")) devPort else (if (BuildConfig.FLAVOR.equals("uat")) uatPort else prodPort)
//    public var permanentURL = "https://inspection" + if (BuildConfig.FLAVOR.equals("dev")) "dev" else (if (BuildConfig.FLAVOR.equals("uat")) "uat" else "") + ".jet-matics.com/"
    private val tempURL = "https://dev.facilityappointment.com/ACEAPI.asmx/"
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
//    val personnelTypeURL = "http://144.217.24.163:5000/getPersonnelTypes?facilityId="
//    val personnelDetailsURL = "http://144.217.24.163:5000/getPersonnelsDetails?facilityId=%s&personnelTypeId=%d"
//    val allPersonnel = "http://144.217.24.163:5000/getAllPersonnelsDetails"
//    val personnelDetailsWithIdUrl = "http://144.217.24.163:5000/getPersonnelDetailsWithId?personnelId="
//    val facilityLocationsURL = "http://144.217.24.163:5000/getFacilityAddresses?facilityId="
//    val paymentMethodsURL = "http://144.217.24.163:5000/getPaymentMethods"
//    val facilityHoursURL = "http://144.217.24.163:5000/getFacilityHours?facilityId="
//    val facilityScopeOfSvcURL = "http://144.217.24.163:5000/getScopeOfServicDetails?facilityId="
//    val programTypesURL = "http://144.217.24.163:5000/getProgramTypes"
//    val facilityCompleteURL = "http://144.217.24.163:5000/getFacilities?facilityLike="
//    val facilityEmailPhoneURL = "http://144.217.24.163:5000/getFacilityEmailAndPhone?facilityId="
//    val contractSignerDtlsURL = "http://144.217.24.163:5000/getContractSignerDetails?personnelId="
//    val getFacilityWithIdUrl = "http://144.217.24.163:5000/getFacilityWithId?facilityId="
//    val getLastInspectionForFacility = "http://144.217.24.163:5000/getLastAnnualVisitationInspectionForFacility?facilityId="
//    val getEmailFromFacilityAndId = "http://144.217.24.163:5000/getEmailFromFacilityAndId?facilityId=%s&emailId=%d"
//    val getPhoneNumberWithFacilityAndId = "http://144.217.24.163:5000/getPhoneNumberWithFacilityAndId?facilityId=%s&phoneId=%d"
//    val getVehicleServicesURL = "http://144.217.24.163:5000/getVehicleServices"
//    val getFacilityPrograms = "http://144.217.24.163:5000/getFacilityPrograms?facilityId="
//    val getFacilityVisitationRecords = "http://144.217.24.163:5000/getVisitationRecords?facilityName="
//    val getAnnualVisitations = "http://144.217.24.163:5000/getAnnualVisitations?%s"
//    val getFacilityAffiliations = "http://144.217.24.163:5000/getFacilityAffiliations?facilityId="
//    val getAffTypesURL = "http://144.217.24.163:5000/getAffiliationTypes"
//    val getAllPersonnelDetails = "http://144.217.24.163:5000/getAllPersonnelsDetails"
//    val getVisitationPlanningList = "http://144.217.24.163:5000/getVisitationPlanningList?facilityName=%s&month=%s&year=%s"
//    val submitFacilityAddress = "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateFacilityAddressData?facnum="
//    val getCopyFacilityData = "https://dev.facilityappointment.com/ACEAPI.asmx/GetFacilityData?facnum=2518&clubcode=004"
//    var facNo= if (FacilityDataModel.getInstance().tblFacilities.size>0) FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() else "2089"

    val enableLocationTracking = true
    val getFacilityComplaintsURL = permanentURL + "getFacilityComplaints?facilityId="
    val getfacilitiesURL = permanentURL + "getFacilities?facilityName="
    val getVehiclesURL = "http://144.217.24.163:5000/getVehicles"
    val getAllFacilities = permanentURL + "getAllFacilities"
    val getAllSpecialists = permanentURL + "getAllSpecialists"
    val getSpecialistNameFromEmail = permanentURL + "getSpecialistNameFromEmail?specialistEmail="
    val getSpecialistDetails = permanentURL + "getSpecialistDetails?specialistEmail="
    val submitContactInfoAddress = permanentURL + "updateFacilityAddressData?facnum="
    val submitFacilityGeneralInfo = permanentURL + "updateFacilityData?facNum="
    val submitFacilityHours = permanentURL + "updateFacilityHoursData?facNum="
    val submitFacilityGeoCodes = permanentURL + "updateFacilityGeoCodeData?FacID="
    val submitFacilityEmail = permanentURL + "updateFacilityEmailData?facnum="
    val authenticateUrl = permanentURL + "authenticate?email="
    val resetPassword = permanentURL + "resetPassword?email="
    val changePassword = permanentURL + "changePassword?email="

    val submitFacilityPhone = permanentURL + "updateFacilityPhoneData?facNum="
    //AAA APIs WebServices
    val getClubCodes = permanentURL + "getClubCodes?clubCode="
    val checkFileExists = permanentURL + "checkFileExists?FileName="
    val getSignature = permanentURL + "getSignature?file="

    val uploadFile = permanentURL + "uploadFile?email="
    val uploadPhoto = permanentURL + "uploadPhoto?fileNameToSave="
    val getFacilityData = permanentURL + "getFacilityData?facnum=%d&clubcode=%s"

    val getTypeTables = permanentURL + "getTableTypes"
    val getAppVersion = permanentURL + "getAppVersion"
    val getVisitations = permanentURL + "getVisitations?"
    val logTracking = permanentURL + "logTracking?sessionId="
    val getFacilitiesWithFilters = permanentURL + "getFacilitiesWithFilters?"
    val getImages = permanentURL + "getImage?file="
    val getImagesWithDomain = permanentURLWithDomain + "getImage?file="
    val getPDF = permanentURL + "getPDFForApp?visitationID="
    val getFacilityPhotos = permanentURL + "getFacilityPhotos?facId="
    val getRepairDiscountFactors = permanentURL + "getRepairDiscountFactors?clubCode="
    val getPersonnelDetails = permanentURL + "getPRGPersonnelDetails?clubCode="
    val getPRGFacilityDetails = permanentURL + "getPRGFacilityDetails?clubCode="
    val getFacilityDirectors = permanentURL + "getFacilityDirectors?clubCode="
    val getFacilityHolidays = permanentURL + "getFacilityHolidays?clubCode="
    val updateFacilityPhotos = permanentURL + "updateFacilityPhotos?facId="
    val getLoggedActions = permanentURL + "getLoggedActions?facNum="
    val getLoggedActionsBySession = permanentURL + "getLoggedActionsBySession?facNum="
    val logCreatePDF = permanentURL + "logCreatePDF?log="
    val getVisitationHeader = permanentURL + "getVisitationHeader?facNum="
    val getVisitationHeaderByID = permanentURL + "getVisitationHeaderByID?facNum="

    val getPRGCompletedVisitations = permanentURL + "getPRGCompletedVisitations"
    val getPRGVisitationsLog = permanentURL + "getPRGVisitationsLog"
    val saveVisitedScreens = permanentURL + "saveVisitedScreens?facId="

    val sendCompletedPDF = permanentURL + "sendCompletedPDF?visitationID="
    val UpdateAARPortalAdminData = permanentURL + "updateAARPortalAdminData?facNum="
    val rspLoginGet = "https://rsp.national.aaa.com/app/login"
    val rspLoginPost = "https://rsp.national.aaa.com/login?username=ace_cherya&password=Surfing12345678!"
    val UpdateAARPortalTrackingData = permanentURL + "updateAARPortalTracking?facNum="
    val UpdateAmendmentOrderTrackingData = permanentURL + "updateAmendmentOrderTrackingData?facNum="
    val UpdateDeficiencyData = permanentURL + "updateDeficiencyData?facNum="
    val UpdateProgramsData = permanentURL + "updateProgramsData?facNum="
    val AddAffiliateVendorFacilities = permanentURL + "AddAffiliateVendorFacilities?facNum="
    val DeleteAffiliateVendorFacilities = permanentURL + "DeleteAffiliateVendorFacilities?facNum="
    val UpdateFacilityServicesData = permanentURL + "updateFacilityServicesData?facNum="
    val UpdateAffiliationsData= permanentURL + "updateAffiliationsData?facNum="
    val UpdatePaymentMethodsData=permanentURL + "updatePaymentMethodsData?facnum="

    val UpdateFacilityLanguageData=permanentURL + "updateFacilityLanguageData?facNum="
    val UpdateFacilityVehicles= permanentURL + "updateVehicles?facnum="
    val UpdateVehicleServices=permanentURL + "updateVehicleServices?facnum="
    val UpdateFacilityPromotions=permanentURL + "updateFacilityPromotions?facnum="

    val UpdatePersonnelCertification=permanentURL + "updatePersonnelCertification?facNum="
    val CreatePRGUser=permanentURL + "createRSPUser?facNum="
    val UpdateFacilityPersonnelData=permanentURL + "updateFacilityPersonnelData?facNum="
    val UpdateFacilityPersonnelSignerData=permanentURL + "updateFacilityPersonnelSignerData?facNum="
    val UpdateScopeofServiceData =permanentURL + "updateScopeOfServiceData?facNum="
    val UpdateVisitationDetailsData=permanentURL + "updateVisitationDetailsData?facnum="
    val UpdateVisitationDetailsDataProgress=permanentURL + "updateVisitationDetailsDataProgress?facnum="
    val UpdateVisitationTrackingData=permanentURL + "updateVisitationTrackingData?facnum="

    val getSpecialistIdsForClubCode = permanentURL + "getSpecialistsForClubCode?"

    var visitationIDForPDF = ""
    var specialistEmailForPDF = ""
    var facNoForPDF = ""
    var facNameForPDF = ""
    var typeForPDF = ""
}
