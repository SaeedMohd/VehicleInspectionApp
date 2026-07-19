package com.inspection.Utils

//import androidx.multidex.BuildConfig
//import com.inspection.BuildConfig
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.inspection.BuildConfig
import com.inspection.Utils.ApplicationPrefs.PREFS_NAME
import com.inspection.model.FacilityDataModel
import java.text.SimpleDateFormat
import java.util.*
import android.util.Base64
import java.io.ByteArrayOutputStream

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
    public var awsBucket = if (BuildConfig.FLAVOR.equals("dev")) "ace-aar-dev" else (if (BuildConfig.FLAVOR.equals("uat")) "ace-aar-uat" else "ace-facilities-aar")
//    public var permanentURL = "http://144.217.24.163:" + if (BuildConfig.FLAVOR.equals("dev")) devPort else (if (BuildConfig.FLAVOR.equals("uat")) uatPort else prodPort)
    public var permanentURL = if (BuildConfig.FLAVOR.equals("dev")) "http://144.217.24.163:${devPort}" else (if (BuildConfig.FLAVOR.equals("uat")) "https://inspectionuat.jet-matics.com/" else "https://inspection.valueaddedonline.com/")//"http://144.217.24.163:5000/")
//    public var permanentURL = if (BuildConfig.FLAVOR.equals("dev")) devPort else (if (BuildConfig.FLAVOR.equals("uat")) "https://inspectionuat.jet-matics.com/" else "inspection.valueaddedonline.com/")
    public var permanentURLWithDomain = if (BuildConfig.FLAVOR.equals("dev")) "http://jet-matics.com:${devPort}" else (if (BuildConfig.FLAVOR.equals("uat")) "https://inspectionuat.jet-matics.com/" else "https://inspection.valueaddedonline.com/")
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

    val uploadFile = permanentURL + "uploadFile?aws=Y&email="
    val uploadPhoto = permanentURL + "uploadPhoto?fileNameToSave="
    val getFacilityData = permanentURL + "getFacilityData?facnum=%d&clubcode=%s"
    val getCompletedVisitations = permanentURL + "getPDFCompletedVisitations?specialist="
    val generatePDF = permanentURL + "generatePDF?"
    val getAccountLastSynced = permanentURL + "getAccountLastSynced?facNum="
    val getRspByLogin = permanentURL + "getRspByLogin?clubCode="
    val refreshFacilityRsp = permanentURL + "refreshFacilityRsp?facNum="
    val getFacilityApporintmentProgram = permanentURL + "getFacilityApporintmentProgram?facNo="
    val getAaaByLogin = permanentURL + "getAaaByLogin?clubCode="
    val refreshFacilityAaa = permanentURL + "refreshFacilityAaa?facNum="
    val isDirector = permanentURL + "isDirector?email="
    val getDirectorOverview = permanentURL + "getDirectorOverview?email="
    val getPDFStats = permanentURL + "getPDFStats?specialist="
    val getResponseCount = permanentURL + "getResponseCount?"

    val IDLE_TIMEOUT = if (BuildConfig.FLAVOR.equals("uat")) 30 * 60 * 1000L else 20 * 60 * 1000L // 15 minutes
    val getTypeTables = permanentURL + "getTableTypes"
    val getS3Url = permanentURL + "getS3Url?objectKey="
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
    val updateFacilityPhotosData = permanentURL + "updateFacilityPhotosData?facId="
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
    val getRepairData = permanentURL + "getRepairData"
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

    val UpdatePRGDocs_JSON=permanentURL + "updatePRGDocs_JSON?facID="

    val UpdateFacilityPersonnelData=permanentURL + "updateFacilityPersonnelData?facNum="
    val UpdateFacilityPersonnelSignerData=permanentURL + "updateFacilityPersonnelSignerData?facNum="
    val UpdateScopeofServiceData =permanentURL + "updateScopeOfServiceData?facNum="
    val UpdateVisitationDetailsData=permanentURL + "updateVisitationDetailsData?facnum="
    val UpdateVisitationDetailsDataProgress=permanentURL + "updateVisitationDetailsDataProgress?facnum="
    val UpdateVisitationTrackingData=permanentURL + "updateVisitationTrackingData?facnum="
    val createVisitation=permanentURL + "createVisitation?"
    val getFacilityLocation=permanentURL + "getFacilityLocation?facNo="
    val getApplicantMatchingFacilities=permanentURL + "getApplicantMatchingFacilities?placeId="

    val getSpecialistIdsForClubCode = permanentURL + "getSpecialistsForClubCode?"
    val internetConnectionErrMsg = "\n" +
            "Please wait till the top left connection icon show proper signal"

    var visitationIDForPDF = ""
    var awsReference = ""
    var specialistEmailForPDF = ""
    var facNoForPDF = ""
    var facNameForPDF = ""
    var typeForPDF = ""
    // Handle saving Rep Signature
    const val EXPIRY_HOURS = 24
    fun getCurrentTimestamp(): Long = System.currentTimeMillis()
    fun isWithin24Hours(timestamp: Long): Boolean {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return diff <= 24 * 60 * 60 * 1000
    }
    fun saveBase64Image(context: Context, key: String, base64Image: String) {
        val prefs = context.getSharedPreferences("ImagePrefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("${key}_data", base64Image)
            .putLong("${key}_timestamp", getCurrentTimestamp())
            .apply()
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun clearExpiredImages(context: Context) {
        val prefs = context.getSharedPreferences("ImagePrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        prefs.all.forEach { (key, value) ->

            if (key.endsWith("_timestamp")) {
                val imageKey = key.removeSuffix("_timestamp")
                val timestamp = value as? Long ?: return@forEach
                print("${imageKey}_timestamp")
                if (!isWithin24Hours(timestamp)) {
                    editor.remove("${imageKey}_timestamp")
                    editor.remove("${imageKey}_data")
                }
            }
        }

        editor.apply()
    }

    fun decodeBase64ToBitmap(base64: String): Bitmap {
        val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    fun getBase64ImageIfValid(context: Context, key: String): String? {
        val prefs = context.getSharedPreferences("ImagePrefs", Context.MODE_PRIVATE)
        val timestamp = prefs.getLong("${key}_timestamp", -1L)

        return if (timestamp != -1L && isWithin24Hours(timestamp)) {
            prefs.getString("${key}_data", null)
        } else {
            null
        }
    }
}
