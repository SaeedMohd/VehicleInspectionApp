package com.inspection.model

class TypeTablesModel {

    companion object {

        @Volatile
        private var INSTANCE: TypeTablesModel? = null

        fun getInstance(): TypeTablesModel =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: TypeTablesModel().also { INSTANCE = it }
                }

        fun setInstance(typeTablesModel: TypeTablesModel) {
            INSTANCE = typeTablesModel
        }
    }

    fun clear() {
        INSTANCE = null
    }

    var AARDeficiencyType = ArrayList<aarDeficiencyType>()
    var AARDeficiencyActionsType = ArrayList<aARDeficiencyActionsType>()
    var AdjustmentDescriptionType = ArrayList<adjustmentDescriptionType>()
    var AffiliationDetailType = ArrayList<affiliationDetailType>()
    var AmendmentOrderTrackingEventsType = ArrayList<amendmentOrderTrackingEventsType>()
    var tblAmendmentOrderTrackingSubReasonsType = ArrayList<TblAmendmentOrderTrackingSubReasonsType>()
    var AwardsDistinctionsType = ArrayList<awardsDistinctionsType>()
    var BillingPlanCategoryType = ArrayList<billingPlanCategoryType>()
    var BillingPlanFrequencyType = ArrayList<billingPlanFrequencyType>()
    var BillingPlanType = ArrayList<billingPlanType>()
    var BusinessType = ArrayList<businessType>()
    var ClubCodeType = ArrayList<clubCodeType>()
    var CommentsType = ArrayList<commentsType>()
    var tblComplaintFilesJustificationReasonType = ArrayList<TblComplaintFilesJustificationReasonType>()
    var tblComplaintFilesJustificationType = ArrayList<TblComplaintFilesJustificationType>()
    var ComplaintFilesReasonType = ArrayList<complaintFilesReasonType>()
    var ComplaintFilesResolutionType = ArrayList<complaintFilesResolutionType>()
    var ComplaintInitiatedType = ArrayList<complaintInitiatedType>()
    var ContractType = ArrayList<contractType>()
    var DepartmentType = ArrayList<departmentType>()
    var DownstreamAppsType = ArrayList<downstreamAppsType>()
    var EmailType = ArrayList<emailType>()
    var EmployeeAdminLevelType = ArrayList<employeeAdminLevelType>()
    var tblExceptionType = ArrayList<TblExceptionType>()
    var FacilityAvailabilityType = ArrayList<facilityAvailabilityType>()
    var FacilityStatusType = ArrayList<facilityStatusType>()
    var FacilityType = ArrayList<facilityType>()
    var InvoicePaymentType = ArrayList<invoicePaymentType>()
    var tblInvoiceStatusType = ArrayList<TblInvoiceStatusType>()
    var LaborRateType = ArrayList<laborRateType>()
    var LanguageType = ArrayList<languageType>()
    var LocationPhoneType = ArrayList<locationPhoneType>()
    var LocationType = ArrayList<locationType>()
    var OfficeType = ArrayList<officeType>()
    var OtherSearchFiltersType = ArrayList<otherSearchFiltersType>()
    var PaymentMethodsType = ArrayList<paymentMethodsType>()
    var PersonnelCertificationType = ArrayList<personnelCertificationType>()
    var PersonnelType = ArrayList<personnelType>()
    var PositionType = ArrayList<positionType>()
    var ProgramsType = ArrayList<programsType>()
    var PromoType = ArrayList<promoType>()
    var RevenueSourceType = ArrayList<revenueSourceType>()
    var ScopeofServiceType = ArrayList<scopeofServiceType>()
    var ScopeofServiceTypeByVehicleType = ArrayList<scopeofServiceTypeByVehicleType>()
    var SearchFilterCategoryType = ArrayList<searchFilterCategoryType>()
    var ServiceAvailabilityType = ArrayList<serviceAvailabilityType>()
    var ServiceProviderType = ArrayList<serviceProviderType>()
    var ServicesType = ArrayList<servicesType>()
    var ShopManagementPartsOrderMethodType = ArrayList<shopManagementPartsOrderMethodType>()
    var ShopManagementPartsSuppliersType = ArrayList<shopManagementPartsSuppliersType>()
    var ShopManagementSoftwareDetailsType = ArrayList<shopManagementSoftwareDetailsType>()
    var ShopManagementSoftwareType = ArrayList<shopManagementSoftwareType>()
    var TerminationCodeType = ArrayList<terminationCodeType>()
    var TimezoneType = ArrayList<timezoneType>()
    var VehicleMakesType = ArrayList<vehicleMakesType>()
    var VehiclesMakesCategoryType = ArrayList<vehiclesMakesCategoryType>()
    var VehiclesType = ArrayList<vehiclesType>()
    var WarrantyPeriodType = ArrayList<warrantyPeriodType>()

    class aarDeficiencyType {
        var DeficiencyTypeID = ""
        var DeficiencyName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class aARDeficiencyActionsType {
        var DeficiencyActionTypeID = ""
        var DeficiencyName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class adjustmentDescriptionType {
        var AdjustmentDescId = ""
        var Type = ""
        var Description = ""
        var Active = ""
        var InsertBy = ""
        var InsertDate = ""
        var UpdateBy = ""
        var UpdateDate = ""
    }

    class affiliationDetailType {
        var AffiliationTypeDetailID = ""
        var AffiliationDetailTypeName = ""
        var AARAffiliationTypeID = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class amendmentOrderTrackingEventsType {
        var AmendmentEventID = ""
        var AmendmentEventName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class TblAmendmentOrderTrackingSubReasonsType {
        var AmendmentSubReasonID = ""
        var AmendmentSubReasonName = ""
        var AmendmentReasonID = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }


    class awardsDistinctionsType {
        var AwardsDistinctionsTypeId = ""
        var AwardDistinctionName = ""
        var AwardDescription = ""
        var active = ""
        var updateBy = ""
        var updatedate = ""
        var insertBy = ""
        var insertdate = ""
    }

    class billingPlanCategoryType {
        var BillingPlanCatgTypeID = ""
        var BillingPlanCatgName = ""
        var EffectiveDate = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class billingPlanFrequencyType {
        var BillingPlanFrequencyTypeID = ""
        var BillingPlanFrequencyTypeName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class billingPlanType {
        var BillingPlanTypeID = ""
        var ClubCode = ""
        var StateCode = ""
        var BillingPlanCatgID = ""
        var BillingPlanTypeName = ""
        var MiniBayCount = ""
        var MaxBayCount = ""
        var Cost = ""
        var EffectiveDate = ""
        var ExpirationDate = ""
        var PrintBillFlag = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class businessType {
        var BusTypeID = ""
        var BusTypeName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class clubCodeType {
        var ClubCodeId = ""
        var ClubCodeName = ""
        var ClubCodeDescription = ""
        var AchID = ""
        var StateRevenueAccount = ""
        var BillingPhoneNbr = ""
        var BillingAddressLn1 = ""
        var BillingAddressLn2 = ""
        var BillingCity = ""
        var BillingState = ""
        var BillingZipCode = ""
        var ClubLogoPath = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
        var BillingCategory = ""
        var CentralPrint = ""
        var StateID = ""
    }

    class commentsType {
        var CommentTypeID = ""
        var CommentTypeName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class TblComplaintFilesJustificationReasonType {
        var ComplaintJustificationReasonID = ""
        var ComplaintJustificationReasonName = ""
        var Active = ""
        var InsertBy = ""
        var InsertDate = ""
        var UpdateBy = ""
        var UpdateDate = ""
    }

    class TblComplaintFilesJustificationType {
        var ComplaintJustificationID = ""
        var ComplaintJustificationName = ""
        var Active = ""
        var InsertBy = ""
        var InsertDate = ""
        var UpdateBy = ""
        var UpdateDate = ""
    }

    class complaintFilesReasonType {
        var ComplaintReasonID = ""
        var ComplaintReasonName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class complaintFilesResolutionType {
        var ComplaintResolutionID = ""
        var ComplaintResolutionName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class complaintInitiatedType{
        var ComplaintInitID = ""
        var ComplaintInitName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class contractType{
        var ContractTypeID = ""
        var ContractTypeName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class departmentType{
        var DepartmentTypeID = ""
        var DepartmentTypeName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class downstreamAppsType{
        var DownstreamAppId = ""
        var AppName = ""
        var Active = ""
        var InsertBy = ""
        var InsertDate = ""
        var UpdateBy = ""
        var UpdateDate = ""
    }

    class emailType{
        var EmailID = ""
        var EmailName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class employeeAdminLevelType{
        var EmpAdminID = ""
        var EmpAdminName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class TblExceptionType{
        var ExceptionTypeID = ""
        var ExceptionTypeName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class facilityAvailabilityType{
        var FacilityAvailabilityTypeId = ""
        var FacilityAvailabilityName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class facilityStatusType{
        var FacilityStatusID = ""
        var FacilityStatusName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class facilityType{
        var FacilityTypeID = ""
        var FacilityTypeName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class invoicePaymentType{
        var PaymentTypeId = ""
        var PaymentName = ""
        var InsertBy = ""
        var InsertDate = ""
        var UpdateBy = ""
        var UpdateDate = ""
        var active = ""
    }

    class TblInvoiceStatusType{
        var InvoiceStatusTypeId = ""
        var InvoiceStatusName = ""
        var BillSeqInCycle = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class laborRateType{
        var LaborRateID = ""
        var LaborRateName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class languageType{
        var LangTypeID = ""
        var LangTypeName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class locationPhoneType{
        var LocPhoneID = ""
        var LocPhoneName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class locationType{
        var LocTypeID = ""
        var LocTypeName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class officeType{
        var OfficeID = ""
        var OfficeName = ""
        var ClubCode = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class otherSearchFiltersType{
        var OtherFilterTypeID = ""
        var OtherFilterName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
        var IconFileName = ""
        var HoverText = ""
        var ToolTip = ""
        var EditableFlag = ""
    }

    class paymentMethodsType{
        var PmtMethodID = ""
        var PmtMethodName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class personnelCertificationType{
        var PersonnelCertID = ""
        var PersonnelCertName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class personnelType{
        var PersonnelTypeID = ""
        var PersonnelTypeName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
        var RspRole = ""
        var RspRoleCode = ""
    }

    class positionType{
        var PositionID = ""
        var PositionName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
        var RspRole = ""
        var RspRoleCode = ""
    }

    class programsType{
        var ProgramTypeID = ""
        var ProgramTypeName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
        var IconFileName = ""
        var HoverText = ""
        var ToolTip = ""
    }

    class promoType{
        var PromoTypeID = ""
        var PromoTypeName = ""
        var Active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class revenueSourceType{
        var RevenueSourceID = ""
        var RevenueSourceName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class scopeofServiceType{
        var ScopeServiceID = ""
        var ScopeServiceName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
        var IconFileName = ""
        var HoverText = ""
        var ToolTip = ""
    }

    class scopeofServiceTypeByVehicleType{
        var VehiclesTypeID = ""
        var ScopeServiceID = ""
        var ScopeServiceName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class searchFilterCategoryType{
        var FilterCategoryId = ""
        var FilterCategoryName = ""
        var FilterTable = ""
        var active = ""
        var InsertBy = ""
        var InsertDate = ""
        var UpdateBy = ""
        var UpdateDate = ""
        var DownStreamCategoryName = ""
        var SearchPageVisible = ""
        var SeqNum = ""
    }

    class serviceAvailabilityType{
        var SrvAvaID = ""
        var SrvAvaName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class serviceProviderType{
        var SrvProviderID = ""
        var SrvProviderName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class servicesType{
        var ServiceTypeID = ""
        var ServiceTypeName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class shopManagementPartsOrderMethodType{
        var ShopMgmtOrderID = ""
        var ShopMgmtOrderName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class shopManagementPartsSuppliersType{
        var ShopMgmtSupplierID = ""
        var ShopMgmtSupplierName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class shopManagementSoftwareDetailsType{
        var ShopSoftwareDetailID = ""
        var ShopSoftwareDetailName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class shopManagementSoftwareType{
        var ShopMgmtSoftwareID = ""
        var ShopMgmtSoftwareName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class terminationCodeType{
        var TerminationCodeID = ""
        var TerminationCodeName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class timezoneType{
        var TimezoneID = ""
        var TimezoneName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class vehicleMakesType{
        var VehMakeTypeId = ""
        var VehMakeName = ""
        var Active = ""
        var InsertBy = ""
        var InsertDate = ""
        var UpdateBy = ""
        var UpdateDate = ""
    }

    class vehiclesMakesCategoryType{
        var VehCategoryID = ""
        var VehCategoryName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class vehiclesType{
        var VehiclesTypeID = ""
        var VehiclesTypeName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }

    class warrantyPeriodType{
        var WarrantyTypeID = ""
        var WarrantyTypeName = ""
        var active = ""
        var insertBy = ""
        var insertDate = ""
        var updateBy = ""
        var updateDate = ""
    }
}