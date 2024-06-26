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




class TblFacilities {
    var isInputsValid = false
    var FACID = 0
    var FACNo = 0
    var BusinessName = ""
    var EntityName = ""
    var ACTIVE = 1
    var BusTypeID = 0
    var TerminationComments = ""
    var TerminationDate = ""
    var FacilityAnnualInspectionMonth = 0
    var InspectionCycle = ""
    var AutomotiveSpecialist = ""
    var AutomotiveSpecialistSignature = ""
    var InspectionCycle1 = ""
    var AssignedTo = ""
    var assignedToID = ""
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
    var ContractTypeID = 0
    var officeID = 0
    var StatusComment = ""
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
    Annual, Quarterly, AdHoc, Deficiency
}

enum class VisitationStatus {
    NotStarted, Overdue, InProgress
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
    var VisitationTypeID = ""
    var waiveVisitations = false
    var waiverComments = ""
    var waiverSignature : Bitmap? = null
    var VisitationReasonTypeID = ""
    var VisitationMethodTypeID = ""
    var visitationID = ""
}

class TblVisitationDetailsData{
    var facnum = 0
    var clubcode = 0
    var StaffTraining = ""
    var QualityControl = ""
    var AARSigns = ""
    var CertificaterOfApproval = ""
    var MemberBenefitPoster = ""
    var insertBy=""
    var insertDate=""
    var updateBy = ""
    var updateDate=""
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

class TblShopHolidayTimes{
    var FacNum = ""
    var clubcode = ""
    var type = ""
    var startdate = ""
    var enddate = ""
    var comments = ""
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
    var RSP_Phone=""
    var email=""
    var PrimaryMailRecipient=false
    var CertificationTypeId=""
    var CertificationDate=""
    var endDate = ""
    var ContractStartDate=""
    var ContractEndDate=""
    var OEMstartDate = ""
    var ASE_Cert_URL = ""
    var OEMendDate = ""
    var ReportRecipient = false
    var NotificationRecipient = false
    var ComplaintContact = false
    var CertificationNum_ASE = ""
    var updateBy = ""
    var updateDate = ""
    var insertBy = ""
    var insertDate = ""
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
    var DiscountCap = ""
    var DiscountAmount = ""
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
    var active = ""
}

class TblDeficiency {
    var isInputsValid = false
    var DefID = ""
    var DefTypeID = ""
    var VisitationDate = ""
    var ClearedDate = ""
    var EnteredDate = ""
    var Comments : String? = ""
    var DueDate = ""
    var DefActionID = ""
    var DefActionTypeID = ""
    var ActionDate = ""
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
    var RevenueSourceName = ""
    var BillingMonthNumber = 1
    var BillingMonth = ""
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
    var PrintedRecLinkName = ""
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
    var BillingPlanCatgName=""
    var BillingPlanTypeName=""
    var BillingPlanFrequencyTypeName=""
    var Cost=""
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

class InvoiceInfo {
    var InvoiceStatusName = ""
    var BillingMonthNumber = 0
    var BillingMonth = ""
    var BillBalanceDue = ""
    var BillingAmount = ""
    var AmountReceived = ""
    var InvoiceId = ""
    var FACID = 0
    var InvoiceNumber = ""
    var InvoiceAmount = ""
    var CreditAmount = ""
    var BillingDueDate = ""
    var InvoicePrintDate = ""
    var InvoiceStatusId =""
    var ACHParticipant = ""
    var InvoiceFileName = ""
    var insertBy = ""
    var insertDate = ""
    var updateBy = ""
    var updateDate = ""
}


class TblVendorRevenue {
    var FACID = 0
    var VendorRevenueID = 0
    var DateOfCheck = ""
    var Amount = ""
    var ReceiptDate = ""
    var ReceiptNumber = ""
    var RevenueSourceID = 0
    var RevenueSourceName = ""
    var StateRevenueAcct = ""
    var Comments = ""
    var insertBy = ""
    var insertDate = ""
    var updateBy = ""
    var updateDate = ""
}

class TblBillingHistory {
    var InvoiceStatusName = ""
    var BillingMonthNumber = 0
    var BillingMonth = ""
    var BillBalanceDue = ""
    var BillingAmount = 0.0
    var AmountReceived = ""
    var ReceiptDate = ""
    var FACID = 0
    var InvoiceId = -1
    var InvoiceNumber = ""
    var InvoiceAmount = ""
    var CreditAmount = 0.0
    var BillingDueDate = ""
    var InvoicePrintDate = ""
    var InvoiceStatusId = 0
    var ACHParticipant = false
    var InvoiceFileName = ""
    var InsertBy = ""
    var insertDate = ""
    var UpdateBy = ""
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
    var ServiceID = ""
    var VehicleCategoryID = ""
}

class TblPersonnelCertification{
    var PersonnelID = 0
    var TechName = ""
    var CertID = ""
    var Category = ""
    var CertificationTypeId = ""
    var CertificationDate = ""
    var ExpirationDate = ""
    var CertDesc = ""
}

class TblBillingAdjustments {
    var AdjustmentId = 0
    var FacId = 0
    var EffectiveDate = ""
    var Type = ""
    var Description = ""
    var Amount = 0.0
    var Comments = ""
    var LastUpdateBy = ""
    var LastUpdateDate = ""
}

class TblAAAPortalEmailFacilityRepTable {
    var Year = ""
    var Quarter = ""
    var Month = ""
    var Club_x0020_Code = ""
    var ContractSID = ""
    var Facility_x0020__x0023_ = ""
    var RO_x0020_Count = ""
    var Records_x0020_Received = ""
    var Valid_x0020_Phone_x0020_Numbers = ""
    var Passed_x0020_Business_x0020_Rules = ""
    var Total_x0020_Responses = ""
    var Q1_x0020_Satisfied = ""
    var Q7_x0020_Return = ""
    var Q2_x0020_Repair = ""
    var Q3_x0020_Personnel = ""
    var Q4_x0020_Estimate = ""
    var Q5_x0020_Clean = ""
    var Q6_x0020_Ready = ""
    var Q8_x0020_Member = ""
    var Q9_x0020_Choose = ""
}

class TblFacVehicles{
    var FACID = 0
    var VehicleID = -1
    var insertBy = ""
    var insertDate = ""
    var updateBy = ""
    var updateDate = ""
}

class TblGeocodes {
    var GeoCodeTypeID = 0
    var GeocodeTypeName = ""
    var LATITUDE = ""
    var LONGITUDE = ""
    var SortOrder = 0
}


class AffiliateVendorFacilities {
    var AffiliateVendorTypeID = 0
    var AffiliateVendorName = ""
    var active = true
    var FacID = ""
    var AffiliateVendorFacilityID = ""
    var AffiliateVendor = ""
}

class TblPromotions {
    var PromoID = 0
    var Description = ""
    var active = true
    var PromoPage = ""
    var SearchResultsHeader = ""
    var SearchDescription = ""
    var CouponFileName = ""
    var EffDate = ""
    var ExpDate = ""
    var HoverText = ""
    var ToolTip = ""
    var UpdateDate = ""
    var UpdateBy = ""
    var PromoTypeName = ""
    var Participant = ""
    var CouponText = ""
    var Disclaimer = ""
    var ParticipantUpdateBy = ""
    var ParticipantUpdateDate = ""
}


class TblPersonnelSigner {
    var PersonnelID = -1
    var Addr1 = ""
    var Addr2 = ""
    var CITY = ""
    var ST = ""
    var ZIP = ""
    var ZIP4 = ""
    var Phone = ""
    var email = ""
    var ContractStartDate = ""
    var insertBy = ""
    var insertDate = ""
    var updateBy = ""
    var updateDate = ""
    var FirstName = ""
    var active = 0
}


class PRGFacilityPhotos {
    var filedescription = ""
    var downstreamapps = ""
    var filename = ""
    var approvedby = ""
    var approvalrequested = false
    var approveddate = ""
    var facid = 0
    var clubCode = 0
    var lastupdatedate = ""
    var lastupdateby = ""
    var approved = false
    var photoid = 0
}

class PRGLogChanges {
    var recordid = 0
    var sessionid = ""
    var facid = 0
    var clubcode = 0
    var userid = ""
    var groupname = ""
    var screenname = ""
    var sectionname = ""
    var action = false
    var datachanged = ""
    var changedate = ""
}

class PRGVisitationHeader {
    var recordid = 0
    var sessionid = ""
    var facid = 0
    var clubcode = 0
    var userid = ""
    var visitationtype = ""
    var visitationreason = ""
    var emailpdf = false
    var emailto = ""
    var waivevisitation = false
    var visitationid = ""
    var waivecomments = ""
    var facilityrep = ""
    var automotivespecialist = ""
    var stafftraining = ""
    var qualitycontrol = ""
    var aarsigns = ""
    var certificateofapproval = ""
    var memberbenefitposter = ""
    var changedate = ""
    var visitmethod = ""
    var comments = ""
}

class PRGCompletedVisitations {
    var recordid = 0
    var facid = 0
    var clubcode = 0
    var visitationtype = ""
    var visitationid = ""
    var completiondate = ""
    var completionmonth = 0
    var waivecomments = ""
}

class PRGVisitationsLog {
    var recordid = 0
    var facid = 0
    var clubcode = 0
    var visitationtype = ""
    var sessionid = ""
    var changedate = ""
    var changemonth = 0
    var facannualinspectionmonth = 0
    var inspectioncycle=""
    var completed = false
    var cancelled = false
    var userid = ""
    var visitedscreens = ""
}

class PRGFacilityDetails {
    var facid = 0
    var clubcode = 0
    var napanumber = ""
    var nationalnumber = ""
    var sessionid = ""
}

class PRGPersonnelDetails {
    var facnum = 0
    var clubcode = 0
    var personnelid = ""
    var asecerturl = ""
    var oemenddate = ""
    var oemstartdate = ""
    var reportrecipient = 0
    var notificationrecipient = 0
}

class PRGFacilityDirectors {
    var facnum = 0
    var clubcode = 0
    var specialistemail = ""
    var specialistid = 0
    var directorid = 0
    var directoremail = ""
}

class PRGFacilityShopHolidayTimes{
    var FacNum = ""
    var clubcode = ""
    var type = ""
    var startdate = ""
    var enddate = ""
    var comments = ""
}


class PRGAppVersion {
    var version = ""
    var message = ""
    var enabled = 0
    var duplicateCheckEnabled = 0
    var useNewPDFFormat = false
}


class PRGRepairDiscountFactors {
    var clubcode = ""
    var maxdiscountamount = ""
    var discountpercentage = ""
}


