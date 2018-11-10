package com.inspection.model

import android.os.Debug
import android.util.Log

class IndicatorsDataModel {

        companion object {

            @Volatile
            private var INSTANCE: IndicatorsDataModel? = null

            fun getInstance(): IndicatorsDataModel =
                    INSTANCE ?: synchronized(this) {
                        INSTANCE ?: IndicatorsDataModel().also { INSTANCE = it }
                    }

            fun setInstance(indicatorsDataModel: IndicatorsDataModel){
                INSTANCE = indicatorsDataModel
            }
        }

        fun clear() {
            INSTANCE = null
        }

        var annualVisitationId = -1

    var tblBilling = ArrayList<TblBilling>()
    var tblScopeOfServices = ArrayList<TblScopeOfServices>()
    var tblDeffeciencies= ArrayList<TblDeffeciencies>()
    var tblVisitation= ArrayList<TblVisitation>()
    var tblFacility= ArrayList<TblFacility>()
    var tblComplaints= ArrayList<TblComplaints>()
    var tblSurveys= ArrayList<TblSurveys>()
    var tblPhotos= ArrayList<TblPhotos>()

    class TblBilling {
        var BillingPlan = false
        var BillingPlanVisited = false
        var Billing = false
        var BillingVisited = false
        var BillingAdjustments = false
        var BillingAdjustmentsVisited = false
        var Payments = false
        var PaymentsVisited = false
        var VendorRevenue = false
        var VendorRevenueVisited = false
        var BillingHistory = false
        var BillingHistoryVisited = false
    }

    class TblScopeOfServices {
        var GeneralInfo = false
        var GeneralInfoVisited = false
        var Programs = false
        var ProgramsVisited = false
        var FacilityServices = false
        var FacilityServicesVisited = false
        var Affiliations = false
        var AffiliationsVisited = false
        var VehicleServicesVisited = false
        var VehiclesVisited = false
    }

    class TblDeffeciencies {
        var Deffeciency= false
        var visited = false
    }

    class TblSurveys {
        var Surveys= false
        var visited = false
    }

    class TblPhotos {
        var Photos = false
        var visited = false
    }

    class TblComplaints {
        var Complaints= false
        var visited = false
    }

    class TblFacility {
        var GeneralInfo = false
        var GeneralInfoVisited = false
        var RSP = false
        var RSPVisited = false
        var Location = false
        var LocationVisited = false
        var Personnel = false
        var PersonnelVisited = false
    }

    class TblVisitation {
        var Visitation= false
        var visited = false
    }

    fun init(){
        var SoS = TblScopeOfServices()
        SoS.GeneralInfo=false;
        SoS.Programs =false;
        SoS.FacilityServices=false;
        SoS.Affiliations=false
        SoS.AffiliationsVisited=false
        SoS.FacilityServicesVisited=false
        SoS.GeneralInfoVisited = false
        SoS.ProgramsVisited=false
        SoS.VehicleServicesVisited= false
        SoS.VehiclesVisited=false
        tblScopeOfServices.add(0,SoS)
        var Def = TblDeffeciencies()
        Def.Deffeciency=false;
        Def.visited =false;
        tblDeffeciencies.add(0,Def)
        var Complaints = TblComplaints()
        Complaints.Complaints=true;
        Complaints.visited=false
        tblComplaints.add(0,Complaints)

        var Surveys = TblSurveys()
        Surveys.Surveys=true;
        Surveys.visited=false
        tblSurveys.add(0,Surveys)

        var Photos = TblPhotos()
        Photos.Photos=true;
        Photos.visited=false
        tblPhotos.add(0,Photos)

        var Visitation = TblVisitation()
        Visitation.Visitation=false;
        Visitation.visited=false
        tblVisitation.add(0,Visitation)
        var Facility= TblFacility()
        Facility.GeneralInfo=false;
        Facility.GeneralInfoVisited=false
        Facility.RSP=false;
        Facility.RSPVisited=false
        Facility.Location=false;
        Facility.LocationVisited=false
        Facility.Personnel=false;
        Facility.PersonnelVisited=false
        tblFacility.add(0,Facility)
        var Billing = TblBilling()
        Billing.Billing=true;
        Billing.BillingVisited=false
        Billing.BillingAdjustments=true
        Billing.BillingAdjustmentsVisited=false
        Billing.BillingHistory=true
        Billing.BillingHistoryVisited=false
        Billing.BillingPlan=true
        Billing.BillingPlanVisited=false
        Billing.Payments=true
        Billing.PaymentsVisited=false
        Billing.VendorRevenue=true
        Billing.VendorRevenueVisited=false
        tblBilling.add(0,Billing)
    }

    fun validateAllScreensVisited() {
        validateSoSSectionVisited()
        validateDeffecienciesSectionVisited()
        validateVisitationSectionVisited()
        validateFacilitySectionVisited()
        validateComplaintsSectionVisited()
        validateBillingSectionVisited()
        validatePhotosSectionVisited()
        validateSurveysSectionVisited()
    }

    fun validateBusinessRules() {
        validateSoSSection()
        validateDeffecienciesSection()
        validateVisitationSection()
        validateFacilitySection()
        validateComplaintsSection()
        validateBillingSection()
        validatePhotosSection()
        validateSurveysSection()
//        refreshIndicatorsView()
    }

    fun validateSoSSectionVisited() {//Scope Of Services
        validateSoSGeneralVisited()
        validateSOSProgramsVisited()
        validateSOSFacilityServicesVisited()
        validateSOSAffiliationsVisited()
        validateSOSVehicleVisited()
        validateSOSVehicleServicesVisited()
    }

    fun validateSoSSection() {//Scope Of Services
        validateSoSGeneral()
        validateSOSPrograms()
        validateSOSFacilityServices()
        validateSOSAffiliations()
    }

    fun validateDeffecienciesSectionVisited() {
        validateDeffeciencyVisited()
    }

    fun validateDeffecienciesSection() {
        validateDeffeciency()
    }

    fun validateComplaintsSectionVisited() {
        validateComplaintsVisited()
    }

    fun validateComplaintsSection() {
        validateComplaints()
    }

    fun validateSurveysSectionVisited() {
        validateSurveysVisited()
    }

    fun validateSurveysSection() {
        validateSurveys()
    }

    fun validatePhotosSectionVisited() {
        validatePhotosVisited()
    }

    fun validatePhotosSection() {
        validatePhotos()
    }

    fun validateVisitationSectionVisited() {
        validateVisitationVisited()
    }

    fun validateVisitationSection() {
        validateVisitation()
    }

    fun validateBillingSection() {

    }

    fun validateBillingSectionVisited() {
        validateBillingPlanVisited()
        validateBillingVisited()
        validateVendorRevenueVisited()
        validatePaymentsVisited()
        validateBillingHistoryVisited()
        validateBillingAdjustmentsVisited()
    }

    fun validateBillingAdjustmentsVisited() {
        tblBilling[0].BillingAdjustmentsVisited = true
    }

    fun validateBillingHistoryVisited() {
        tblBilling[0].BillingHistoryVisited = true
    }

    fun validatePaymentsVisited() {
        tblBilling[0].PaymentsVisited = true
    }

    fun validateVendorRevenueVisited() {
        tblBilling[0].VendorRevenueVisited = true
    }

    fun validateBillingPlanVisited() {
        tblBilling[0].BillingPlanVisited = true
    }

    fun validateBillingVisited() {
        tblBilling[0].BillingVisited = true
    }

    fun validateFacilitySectionVisited(){
        validateFacilityGeneralInfoVisited()
        validateFacilityRSPVisited()
        validateFacilityLocationVisited()
        validateFacilityPersonnelVisited()
    }

    fun validateFacilitySection(){
        validateFacilityGeneralInfo()
        validateFacilityRSP()
        validateFacilityLocation()
        validateFacilityPersonnel()
    }

    fun validateSoSGeneralVisited() {
        tblScopeOfServices[0].GeneralInfoVisited = true
    }

    fun validateSOSVehicleServicesVisited() {
        tblScopeOfServices[0].VehicleServicesVisited = true
    }

    fun validateSOSVehicleVisited() {
        tblScopeOfServices[0].VehiclesVisited= true
    }

    fun validateSoSGeneral() {
        var isValid = true
        // General Info
        if (FacilityDataModel.getInstance().tblScopeofService[0].DiagnosticsRate.equals("")) isValid=false;
        if (FacilityDataModel.getInstance().tblScopeofService[0].FixedLaborRate.equals("")) isValid=false;
        if (FacilityDataModel.getInstance().tblScopeofService[0].LaborMax.equals("")) isValid=false;
        if (FacilityDataModel.getInstance().tblScopeofService[0].LaborMin.equals("")) isValid=false;
        if (FacilityDataModel.getInstance().tblScopeofService[0].NumOfBays.equals("")) isValid=false;
        if (FacilityDataModel.getInstance().tblScopeofService[0].NumOfLifts.equals("")) isValid=false;
        if (FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID.equals("")) isValid=false;
        tblScopeOfServices[0].GeneralInfo = isValid
    }

    fun validateSOSProgramsVisited () {
        tblScopeOfServices[0].ProgramsVisited = true
    }

    fun validateSOSPrograms () {
        var isValid = true
        FacilityDataModel.getInstance().tblPrograms.apply {
            (0 until size).forEach {
                if (get(it).effDate.equals("")) isValid = false
                if (get(it).ProgramTypeID.equals("")) isValid = false
                if (get(it).Comments.equals("")) isValid = false
            }
        }
        tblScopeOfServices[0].Programs = isValid
    }

    fun validateSOSFacilityServicesVisited() {
        tblScopeOfServices[0].FacilityServicesVisited = true
    }

    fun validateSOSFacilityServices() {
        var isValid = true
        FacilityDataModel.getInstance().tblFacilityServices.apply {
            (0 until size).forEach {
                if (get(it).ServiceID.equals("")) isValid = false;
                if (get(it).effDate.equals("")) isValid = false
            }
        }
        tblScopeOfServices[0].FacilityServices = isValid
    }

    fun validateSOSAffiliationsVisited() {
        tblScopeOfServices[0].AffiliationsVisited = true
    }

    fun validateSOSAffiliations() {
        var isValid = true
        FacilityDataModel.getInstance().tblAffiliations.apply {
            (0 until size).forEach {
                if (get(it).effDate.equals("")) isValid = false
                if (get(it).AffiliationTypeID==0) isValid = false
                if (get(it).AffiliationTypeDetailID==0) isValid = false
            }
        }
        tblScopeOfServices[0].Affiliations = isValid
    }

    fun validateComplaintsVisited(){
        tblComplaints[0].visited=true
    }
    fun validateComplaints(){

    }

    fun validateSurveys(){

    }

    fun validateSurveysVisited(){
        tblSurveys[0].visited = true
    }

    fun validatePhotos(){

    }

    fun validatePhotosVisited(){
        tblPhotos[0].visited = true
    }

    fun validateDeffeciency(){
        var isValid = true
        FacilityDataModel.getInstance().tblDeficiency.apply {
            (0 until size).forEach {
                if (get(it).VisitationDate.equals("")) isValid = false
            }
        }
        tblDeffeciencies[0].Deffeciency= isValid
    }

    fun validateDeffeciencyVisited(){
        tblDeffeciencies[0].visited = true
    }

    fun validateVisitation() {
        var isValid = true

        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType != null) {
            if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType!!.equals(VisitationTypes.AdHoc)) {
            } else {
                if (FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature == null) isValid = false
            }
        }
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName.equals("")) isValid = false
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistName.equals("")) isValid = false
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature == null) isValid = false
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].emailVisitationPdfToFacility && FacilityDataModel.getInstance().tblVisitationTracking[0].email.equals("")) isValid = false
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].waiveVisitations && FacilityDataModel.getInstance().tblVisitationTracking[0].waiverComments.equals("")) isValid = false
        tblVisitation[0].Visitation = isValid
    }

    fun validateVisitationVisited() {
        tblVisitation[0].visited = true
    }

    fun validateFacilityGeneralInfo() {
        var isValid = true
        FacilityDataModel.getInstance().tblFacilities.apply {
            (0 until size).forEach {
                if (get(it).SvcAvailability.equals("")) isValid = false
                if (get(it).FacilityRepairOrderCount==0) isValid = false
                if (get(it).AutomotiveRepairExpDate.equals("")) isValid = false
            }
        }

        if (FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName.equals("")) isValid=false
        if (FacilityDataModel.getInstance().tblPaymentMethods[0].PmtMethodID.equals("")) isValid=false
        if (FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName.equals("") ) isValid=false

        tblFacility[0].GeneralInfo= isValid
        Log.v("Facility General: --->",isValid.toString())
    }

    fun validateFacilityGeneralInfoVisited() {
        tblFacility[0].GeneralInfoVisited = true
    }

    fun validateFacilityRSP() {
        var isValid = true
        if (FacilityDataModel.getInstance().tblAARPortalAdmin.size==1 && FacilityDataModel.getInstance().tblAARPortalAdmin[0].CardReaders.equals("-1")) isValid=false
        else {
            FacilityDataModel.getInstance().tblAARPortalAdmin.apply {
                (0 until size).forEach {
                    if (!get(it).CardReaders.equals("-1")) {
                        if (!get(it).startDate.equals("") && get(it).AddendumSigned.equals("")) isValid = false
                        if (!get(it).startDate.equals("") && get(it).CardReaders.equals("")) isValid = false
                    }
                }
            }
        }
        tblFacility[0].RSP= isValid
        Log.v("Facility RSP: --->",isValid.toString());
    }

    fun validateFacilityRSPVisited() {
        tblFacility[0].RSPVisited= true
    }

    fun validateFacilityLocation() {
        var isValid = true
        FacilityDataModel.getInstance().tblAddress.apply {
            (0 until size).forEach {
                if (get(it).LocationTypeID.equals(TypeTablesModel.getInstance().LocationType.filter { s-> s.LocTypeName=="Physical" }[0].LocTypeID) && get(it).LATITUDE.equals("")) isValid = false
                if (get(it).LocationTypeID.equals(TypeTablesModel.getInstance().LocationType.filter { s-> s.LocTypeName=="Physical" }[0].LocTypeID) && get(it).LONGITUDE.equals("")) isValid = false
            }
        }
        FacilityDataModel.getInstance().tblPhone.apply {
            (0 until size).forEach {
                if (!get(it).PhoneTypeID.equals("") && get(it).PhoneNumber.equals("")) isValid = false
            }
        }
        FacilityDataModel.getInstance().tblFacilityEmail.apply {
            (0 until size).forEach {
                if (!get(it).emailTypeId.equals("") && get(it).email.equals("")) isValid = false
            }
        }
        FacilityDataModel.getInstance().tblHours.apply {
            (0 until size).forEach {
//                if (get(it).FriClose.equals("")) isValid = false
//                if (get(it).FriOpen.equals("")) isValid = false
//                if (get(it).SatClose.equals("")) isValid = false
//                if (get(it).SatOpen.equals("")) isValid = false
//                if (get(it).SunClose.equals("")) isValid = false
//                if (get(it).SunOpen.equals("")) isValid = false
//                if (get(it).MonClose.equals("")) isValid = false
//                if (get(it).MonOpen.equals("")) isValid = false
//                if (get(it).TueClose.equals("")) isValid = false
//                if (get(it).TueOpen.equals("")) isValid = false
//                if (get(it).WedClose.equals("")) isValid = false
//                if (get(it).WedOpen.equals("")) isValid = false
//                if (get(it).ThuClose.equals("")) isValid = false
//                if (get(it).ThuOpen.equals("")) isValid = false
                if (get(it).NightDrop && get(it).NightDropInstr.equals("")) isValid = false
            }
        }
        tblFacility[0].Location= isValid
        Log.v("Facility Location: --->",isValid.toString());
    }

    fun validateFacilityLocationVisited() {
        tblFacility[0].LocationVisited=true
    }

    fun validateFacilityPersonnel(){
        var isValid = true
        FacilityDataModel.getInstance().tblPersonnel.apply {
            (0 until size).forEach {
                if (get(it).PersonnelTypeID==0) isValid = false
                if (get(it).FirstName.equals("")) isValid = false
                if (get(it).LastName.equals("")) isValid = false
//                if (get(it).ContractSigner){
//                    if (get(it).Addr1.equals("")) isValid = false
//                    if (get(it).Addr2.equals("")) isValid = false
//                    if (get(it).CITY.equals("")) isValid = false
//                    if (get(it).ST.equals("")) isValid = false
//                    if (get(it).ZIP.equals("")) isValid = false
//                    if (get(it).ZIP4.equals("")) isValid = false
//                    if (get(it).Phone.equals("")) isValid = false
//                    if (get(it).email.equals("")) isValid = false
//                    if (get(it).startDate.equals("")) isValid = false
//                }

            }
        }
        tblFacility[0].Personnel= isValid
        Log.v("Facility Personnel: ->",isValid.toString());
    }

    fun validateFacilityPersonnelVisited(){
        tblFacility[0].PersonnelVisited = true
    }
}