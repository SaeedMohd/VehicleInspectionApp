package com.inspection.model

import android.content.Context
import android.graphics.Bitmap

open class MainClass{

    class TblPhone {
        var phoneIsInputsValid = false
        var PhoneTypeID = ""
        var PhoneNumber = ""
    }

}

//class Main1 : MainClass{
//    constructor() : super()
//    companion object {
//
//        @Volatile
//        private var INSTANCE: Main1? = null
//
//        fun getInstance(): Main1 =
//                INSTANCE ?: synchronized(this) {
//                    INSTANCE ?: Main1().also { INSTANCE = it }
//                }
//
//        fun setInstance(main1: Main1){
//            INSTANCE = main1
//        }
//    }
////    init {
////        var x = TblPhone()
////        x.PhoneNumber="1234"
////        x.PhoneTypeID="0"
////        tblPhone.add(x)
////    }
//
//}

//class Main2 : MainClass{
//    constructor() : super()
//    companion object {
//
//        @Volatile
//        private var INSTANCE: Main2? = null
//
//        fun getInstance(): Main2 =
//                INSTANCE ?: synchronized(this) {
//                    INSTANCE ?: Main2().also { INSTANCE = it }
//                }
//
//        fun setInstance(main2: Main2){
//            INSTANCE = main2
//        }
//    }
////    init {
////        var x = TblPhone()
////        x.PhoneNumber="1234"
////        x.PhoneTypeID="0"
////        tblPhone.add(x)
////    }
//}


class TblFacilities {
    var isInputsValid = false
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
    var SvcAvailability = ""
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
    var ProviderNum = ""
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

enum class VisitationTypes {
    Annual, Quarterly, AdHoc
}

class TblVisitationTracking {
    var DatePerformed = ""
    var performedBy = ""
    var AARSigns = ""
    var CertificateOfApproval = ""
    var MemberBenefitPoster = ""
    var QualityControl = ""
    var StaffTraining = ""
    var facilityRepresentativeName = ""
    var automotiveSpecialistName = ""
    var facilityRepresentativeSignature: Bitmap? = null
    var automotiveSpecialistSignature: Bitmap? = null
    var facilityRepresentativeDeficienciesSignature: Bitmap? = null
    var emailVisitationPdfToFacility = false
    var email = ""
    var visitationType : VisitationTypes? = null
    var waiveVisitations = false
    var waiverComments = ""
    var waiverSignature : Bitmap? = null

}

class TblFacilityType {
    var FacilityTypeName = ""
}

class TblSurveySoftwares {
    var FACID=0
    var SoftwareSurveyNum = 0
    var insertBy = ""
    var insertDate=""
    var updateBy = ""
    var updateDate = ""
}

class TblPaymentMethods {
    var PmtMethodID = ""
}

class TblAddress {
    var locIsInputsValid = false
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
    var phoneIsInputsValid = false
    var PhoneTypeID = ""
    var PhoneNumber = ""
    var PhoneID = ""
}

class TblFacilityEmail {
    var emailIsInputsValid = false
    var emailTypeId = ""
    var email = ""
    var emailID = ""
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
    var personnelIsInputsValid = false
    var iscertInputValid = false
    var PersonnelTypeID=0
    var PersonnelID=0
    var FirstName=""
    var LastName=""
    var CertificationNum=""
    var RSP_UserName=""
    var RSP_Email=""
    var SeniorityDate=""
    var startDate=""
    var ContractSigner=false
    var Addr1=""
    var Addr2=""
    var CITY=""
    var ST=""
    var ZIP=""
    var ZIP4=""
    var Phone=""
    var email=""
    var PrimaryMailRecipient=false
    var CertificationTypeId=""
    var CertificationDate=""
    var ExpirationDate=""
}
//    <CertificationNum/><ContractSigner>true</ContractSigner><Addr2/><PrimaryMailRecipient>true</PrimaryMailRecipient>

class TblAmendmentOrderTracking {
    var AOID = ""
    var AOTEmployee = ""
    var ReasonID = ""
    var EventID = ""
    var EventTypeID = ""
}

class TblAARPortalAdmin {
    var isInputsValid = false
    var startDate = ""
    var AddendumSigned = ""
    var endDate = ""
    var CardReaders = ""
}

class TblAARPortalTracking {
    var FACID = ""
    var TrackingID = ""
    var PortalInspectionDate = ""
    var LoggedIntoPortal = ""
    var NumberUnacknowledgedTows = ""
    var InProgressTows = ""
    var InProgressWalkIns = ""
    var insertBy = ""
    var insertDate = ""
    var updateBy = ""
    var updateDate = ""
    var active = ""
}

class TblScopeofService {
    var isInputsValid = false

    var FixedLaborRate = ""
    var DiagnosticsRate = ""
    var LaborMin = ""
    var LaborMax = ""
    var NumOfBays = ""
    var NumOfLifts = ""
    var WarrantyTypeID = ""
}

class TblPrograms {
    var isInputsValid = false
    var ProgramID = ""
    var ProgramTypeID = ""
    var programtypename = ""
    var effDate = ""
    var expDate = ""
    var Comments = ""
}

class TblFacilityServices {
    var isInputsValid = false
    var ServiceID = ""
    var effDate = ""
    var expDate = ""
    var Comments = ""
    var FacilityServicesID=""
}

class TblAffiliations {
    var AffiliationID = 0
    var AffiliationTypeID = 0
    var AffiliationTypeDetailID = 0
    var effDate = ""
    var expDate = ""
    var comment = ""
}

class TblDeficiency {
    var isInputsValid = false
    var DefID = ""
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
    var ComplaintReasonName = ""
    var ComplaintResolutionName = ""
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

class TblBilling {
    var FACID = 0
    var BillingID = 0
    var ACHParticipant = 0
    var RevenueSourceID = 0
    var BillingMonthNumber = 1
    var BillingDate = ""
    var SecondBillDate = ""
    var BillingAmount = 0.0
    var PaymentDate = ""
    var PaymentAmount = 0.0
    var PendingAmount = 0.0
    var CreditAmountDue = ""
    var insertBy = ""
    var insertDate = ""
    var updateBy = ""
    var updateDate = ""
    var BillingPlanID = 0
    var ResubmitFlag = 0
}

class TblBillingPlan {
    var FACID = 0
    var BillingPlanID = 0
    var BillingPlanTypeID = 0
    var BillingPlanCatgID = 0
    var FrequencyTypeID = 1
    var EffectiveDate = ""
    var ExpirationDate = ""
    var insertBy = ""
    var insertDate = ""
    var updateBy = ""
    var updateDate = ""
}

class TblFacilityBillingDetail {
    var FACID = 0
    var FacBillId = 0
    var BillingPlanID = 0
    var BillingPlanTypeID = 0
    var BillDueDate = ""
    var BillingInvoiceDate = ""
    var BillAmount = 0.0
    var BillSeqInCycle = 0
    var insertBy = ""
    var insertDate = ""
}

class TblInvoiceInfo {
    var FACID = 0
    var InvoiceId = 0
    var InvoiceNumber = ""
    var InvoiceAmount = 0.0
    var CreditAmount = 0.0
    var BillingDueDate = ""
    var InvoicePrintDate = ""
    var InvoiceStatusId = 0
    var ACHParticipant = false
    var InvoiceFileName = ""
    var insertBy = ""
    var insertDate = ""
    var updateBy = ""
    var updateDate = ""
}

class TblVendorRevenue {
    var FACID = 0
    var VendorRevenueID = 0
    var DateofCheck = ""
    var Amount = 0.0
    var ReceiptDate = ""
    var ReceiptNumber = ""
    var RevenueSourceID = 0
    var StateRevenueAcct = ""
    var Comments = ""
    var insertBy = ""
    var insertDate = ""
    var updateBy = ""
    var updateDate = ""
}

class TblBillingHistoryReport {
    var FacID = 0
    var BillingHistoryReportID = 0
    var FacNo = 0
    var FacName = ""
    var TransactionDate = ""
    var TransactionType = ""
    var TransactionDesc = ""
    var ReferenceNo = ""
    var Amount = 0.0
    var insertDate = ""
    var updateDate = ""
}

class TblComments {
    var FACID = 0
    var SeqNum = 0
    var CommentTypeID = 0
    var Comment = ""
    var insertDate = ""
}

class TblVehicleServices{
    var FACID = 0
    var VehiclesTypeID = 0
    var ScopeServiceID = 0
    var insertBy = ""
    var insertDate = ""
}

class TblPersonnelCertification{
    var PersonnelID = 0
    var TechName = ""
    var CertID = ""
    var CertificationTypeId = ""
    var CertificationDate = ""
    var ExpirationDate = ""
    var CertDesc = ""
}
