package com.inspection.model

import android.graphics.Bitmap

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

    fun exportPDFData(){
        INSTANCE
    }

    //Visitation Fragment Related fields
    var annualVisitationId = -1
    var clubCode = ""

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
    var tblShopHolidayTimes = ArrayList<TblShopHolidayTimes>()
    var tblFacilityClosure = ArrayList<TblFacilityClosure>()
    var tblLanguage = ArrayList<TblLanguage>()
    var tblPersonnel = ArrayList<TblPersonnel>()
    var tblAmendmentOrderTracking = ArrayList<TblAmendmentOrderTracking>()
    var tblAARPortalAdmin = ArrayList<TblAARPortalAdmin>()
    var tblAARPortalTracking = ArrayList<TblAARPortalTracking>()
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
    var tblBilling = ArrayList<TblBilling>()
    var tblBillingPlan = ArrayList<TblBillingPlan>()
    var tblFacilityBillingDetail = ArrayList<TblFacilityBillingDetail>()
    var tblInvoiceInfo = ArrayList<TblInvoiceInfo>()
    var tblVendorRevenue = ArrayList<TblVendorRevenue>()
    var tblBillingHistory = ArrayList<TblBillingHistory>()
    var tblComments = ArrayList<TblComments>()
    var tblVehicleServices= ArrayList<TblVehicleServices>()
    var tblPersonnelCertification= ArrayList<TblPersonnelCertification>()
    var tblBillingAdjustments = ArrayList<TblBillingAdjustments>()
    var tblAAAPortalEmailFacilityRepTable = ArrayList<TblAAAPortalEmailFacilityRepTable>()
    var tblInvoiceInfoUpdated = ArrayList<InvoiceInfo>()
    var tblFacVehicles = ArrayList<TblFacVehicles>()
    var tblPersonnelSigner = ArrayList<TblPersonnelSigner>()
    var tblGeocodes = ArrayList<TblGeocodes>()
    var tblAffiliateVendorFacilities = ArrayList<AffiliateVendorFacilities>()
    var tblPromotions = ArrayList<TblPromotions>()
    var tblFacilityBillingHeader = ArrayList<TblFacilityBillingHeader>()
    var FacilityPhotos = ArrayList<FacilityPhotos>()

}