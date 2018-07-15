package com.inspection.model

class FacilityDataModel {

    companion object {

        @Volatile
        private var INSTANCE: FacilityDataModel? = null

        fun getInstance(): FacilityDataModel =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: FacilityDataModel().also { INSTANCE = it }
                }

        fun setInstance(facilityDataModel: FacilityDataModel){
            INSTANCE = facilityDataModel
        }
    }

    fun clear() {
        INSTANCE = null
    }

    var annualVisitationId = -1
    var tblFacilities = ArrayList<TblFacilities>()
    var tblPaymentMethods = ArrayList<TblPaymentMethods>()
    var tblBusinessType = ArrayList<TblBusinessType>()
    var tblContractType = ArrayList<TblContractType>()
    var tblTerminationCodeType = ArrayList<TblTerminationCodeType>()
    var tblFacilityServiceProvider = ArrayList<TblFacilityServiceProvider>()
    var tblOfficeType = ArrayList<TblOfficeType>()
    var tblFacilityManagers = ArrayList<TblFacilityManagers>()
    var tblTimezoneType = ArrayList<TblTimezoneType>()
    var tblVisitationTracking = ArrayList<TblVisitationTracking>()
    var tblFacilityType = ArrayList<TblFacilityType>()
    var tblSurveySoftwares = ArrayList<TblSurveySoftwares>()
    var tblAddress = ArrayList<TblAddress>()
    var tblPhone = ArrayList<TblPhone>()
    var tblFacilityEmail = ArrayList<TblFacilityEmail>()
    var tblHours = ArrayList<TblHours>()
    var tblFacilityClosure = ArrayList<TblFacilityClosure>()
    var tblLanguage = ArrayList<TblLanguage>()
    var tblPersonnel = ArrayList<TblPersonnel>()
    var tblAmendmentOrderTracking = ArrayList<TblAmendmentOrderTracking>()
    var tblAARPortalAdmin = ArrayList<TblAARPortalAdmin>()
    var tblScopeofService = ArrayList<TblScopeofService>()
    var tblPrograms = ArrayList<TblPrograms>()
    var tblFacilityServices = ArrayList<TblFacilityServices>()
    var tblAffiliations = ArrayList<TblAffiliations>()
    var tblDeficiency = ArrayList<TblDeficiency>()
    var tblComplaintFiles = ArrayList<TblComplaintFiles>()
    var NumberofComplaints = ArrayList<numberofComplaints>()
    var NumberofJustifiedComplaints = ArrayList<numberofJustifiedComplaints>()
    var JustifiedComplaintRatio = ArrayList<justifiedComplaintRatio>()
    var tblFacilityPhotos = ArrayList<TblFacilityPhotos>()

    class TblFacilities {

        companion object {

            var isInputsValid = false

        }

        var FACID = 0
        var FACNo = 0
        var BusinessName = ""
        var EntityName = ""
        var ACTIVE = 1
        var TerminationComments = ""
        var TerminationDate = ""
        var FacilityAnnualInspectionMonth = 0
        var InspectionCycle = ""
        var AutomotiveSpecialist = ""
        var AutomotiveSpecialistSignature = ""
        var InspectionCycle1 = ""
        var AssignedTo = ""
        var AdminAssistants = ""
        var WebSite = ""
        var InternetAccess = true
        var TaxIDNumber = ""
        var FacilityRepairOrderCount = 0
        var SvcAvailability = 0
        var AutomotiveRepairNumber = ""
        var AutomotiveRepairExpDate = ""
        var ContractCurrentDate = ""
        var ContractInitialDate = ""
        var BillingMonth = 0
        var BillingAmount = 0
        var InsuranceExpDate = ""
    }

    class TblBusinessType {
        var BusTypeName = ""
    }

    class TblContractType {
        var ContractTypeName = ""
    }

    class TblFacilityServiceProvider {
        var SrvProviderId = ""
        var ProviderNum = 0
    }

    class TblTerminationCodeType{
        var TerminationCodeName=""
    }

    class TblOfficeType {
        var OfficeName = ""
    }

    class TblFacilityManagers {
        var ManagerID = ""
        var Manager = ""
    }

    class TblTimezoneType {
        var TimezoneName = ""
    }

    class TblVisitationTracking {
        var DatePerformed = ""
        var performedBy = ""
        var AARSigns = ""
        var CertificateOfApproval = ""
        var MemberBenefitPoster = ""
        var QualityControl = ""
        var StaffTraining = ""
    }

    class TblFacilityType {
        var FacilityTypeName = ""
    }

    class TblSurveySoftwares {
    }

    class TblPaymentMethods {


        var PmtMethodID = ""
    }

    class TblAddress {
        companion object {

            var locIsInputsValid = false

        }
        var LocationTypeID = ""
        var FAC_Addr1 = ""
        var FAC_Addr2 = ""
        var CITY = ""
        var County = ""
        var ST = ""
        var ZIP = ""
        var ZIP4 = ""
        var LATITUDE = ""
        var LONGITUDE = ""
        var BranchNumber = ""
        var BranchName = ""
    }

    class TblPhone {
        companion object {

            var phoneIsInputsValid = false

        }
        var PhoneTypeID = ""
        var PhoneNumber = ""
    }

    class TblFacilityEmail {
        companion object {

            var emailIsInputsValid = false

        }
        var emailTypeId = ""
        var email = ""
    }

    class TblHours {
        var MonOpen = ""
        var MonClose = ""
        var TueOpen = ""
        var TueClose = ""
        var WedOpen = ""
        var WedClose = ""
        var ThuOpen = ""
        var ThuClose = ""
        var FriOpen = ""
        var FriClose = ""
        var SatOpen = ""
        var SatClose = ""
        var SunOpen = ""
        var SunClose = ""
        var NightDrop = false
        var NightDropInstr = ""
    }

    class TblFacilityClosure {
        var ClosureID = ""
        var effDate = ""
        var expDate = ""
        var Comments = ""
    }

    class TblLanguage {
        var LangTypeID = ""
    }

    class TblPersonnel {
        companion object {
            var personnelIsInputsValid = false
            var iscertInputValid = false
        }
        var PersonnelTypeID=""
        var FirstName=""
        var LastName=""
        var CertificationNum=""
        var RSP_UserName=""
        var RSP_Email=""
        var SeniorityDate=""
        var startDate=""
        var ContractSigner=""
        var Addr1=""
        var Addr2=""
        var CITY=""
        var ST=""
        var ZIP=""
        var ZIP4=""
        var Phone=""
        var email=""
        var PrimaryMailRecipient=""
        var CertificationTypeId=""
        var CertificationDate=""
        var ExpirationDate=""
    }

    class TblAmendmentOrderTracking {
        var AOID = ""
        var AOTEmployee = ""
        var ReasonID = ""
        var EventID = ""
        var EventTypeID = ""
    }

    class TblAARPortalAdmin {
        companion object {
            var isInputsValid = false
        }
        var startDate = ""
        var AddendumSigned = ""
        var CardReaders = ""
        var PortalInspectionDate = ""
        var LoggedIntoPortal = ""
        var NumberUnacknowledgedTows = ""
        var InProgressTows = ""
        var InProgressWalkIns = ""
    }

    class TblScopeofService {
        companion object {
            var isInputsValid = false
        }
        var FixedLaborRate = ""
        var DiagnosticsRate = ""
        var LaborMin = ""
        var LaborMax = ""
        var NumOfBays = ""
        var NumOfLifts = ""
        var WarrantyTypeID = ""
    }

    class TblPrograms {
        companion object {
            var isInputsValid = false
        }
        var ProgramTypeID = ""
        var programtypename = ""
        var effDate = ""
        var expDate = ""
        var Comments = ""
    }

    class TblFacilityServices {
        companion object {
            var isInputsValid = false
        }
        var ServiceID = ""
        var effDate = ""
        var expDate = ""
        var Comments = ""
    }

    class TblAffiliations {

    }

    class TblDeficiency {
        companion object {
            var isInputsValid = false
        }
        var DefTypeID = ""
        var VisitationDate = ""
        var ClearedDate = ""
        var EnteredDate = ""
        var Comments : String? = ""
    }

    class TblComplaintFiles {
        var ComplaintID = ""
        var FirstName = ""
        var LastName = ""
        var ReceivedDate = ""
    }

    class numberofComplaints {
        var NumberofComplaintslast12months = ""
    }

    class numberofJustifiedComplaints {
        var NumberofJustifiedComplaintslast12months = ""
    }

    class justifiedComplaintRatio {
        var JustifiedComplaintRatio = ""
    }

    class TblFacilityPhotos {
        var ApprovalRequested = ""
        var Approved = ""
        var FileName = ""
        var FileDescription = ""
        var LastUpdateBy = ""
        var LastUpdateDate = ""
        var ApprovedBy = ""
        var ApprovedDate = ""
    }
}