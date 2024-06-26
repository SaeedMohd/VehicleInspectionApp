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
        var PromotionsVisited = false
    }

    class TblDeffeciencies {
        var Deffeciency= false
        var visited = false
    }

    class TblSurveys {
        var Surveys= false
        var visited = true
    }

    class TblPhotos {
        var Photos = false
        var visited = true
    }

    class TblComplaints {
        var Complaints= false
        var visited = true
    }

    class TblFacility {
        var GeneralInfo = false
        var GeneralInfoVisited = false
        var RSP = false
        var RSPVisited = false
        var Location = false
        var LocationVisited = false
        var Personnel = false
        var PersonnelVisited = true
        var VisitationTrackingVisited = true
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
        SoS.PromotionsVisited=false
        tblScopeOfServices.add(0,SoS)
        var Def = TblDeffeciencies()
        Def.Deffeciency=false;
        Def.visited =true;
        tblDeffeciencies.add(0,Def)
        var Complaints = TblComplaints()
        Complaints.Complaints=true;
        Complaints.visited=true
        tblComplaints.add(0,Complaints)

        var Surveys = TblSurveys()
        Surveys.Surveys=true;
        Surveys.visited=true
        tblSurveys.add(0,Surveys)

        var Photos = TblPhotos()
        Photos.Photos=true;
        Photos.visited=true
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
        Facility.VisitationTrackingVisited=true
        tblFacility.add(0,Facility)
        var Billing = TblBilling()
        Billing.Billing=true;
        Billing.BillingVisited=true
        Billing.BillingAdjustments=true
        Billing.BillingAdjustmentsVisited=true
        Billing.BillingHistory=true
        Billing.BillingHistoryVisited=true
        Billing.BillingPlan=true
        Billing.BillingPlanVisited=true
        Billing.Payments=true
        Billing.PaymentsVisited=true
        Billing.VendorRevenue=true
        Billing.VendorRevenueVisited=true
        tblBilling.add(0,Billing)
    }


    fun resetAllVisitedFlags(){
        tblFacility[0].GeneralInfoVisited = false
        tblFacility[0].LocationVisited = false
        tblFacility[0].PersonnelVisited = false
        tblFacility[0].RSPVisited = false
        tblScopeOfServices[0].AffiliationsVisited = false
        tblScopeOfServices[0].FacilityServicesVisited = false
        tblScopeOfServices[0].GeneralInfoVisited = false
        tblScopeOfServices[0].ProgramsVisited = false
        tblScopeOfServices[0].VehicleServicesVisited = false
        tblScopeOfServices[0].VehiclesVisited = false
        tblScopeOfServices[0].PromotionsVisited = false
        tblBilling[0].BillingAdjustmentsVisited = true
        tblBilling[0].BillingHistoryVisited = true
        tblBilling[0].BillingPlanVisited = true
        tblBilling[0].BillingVisited = true
        tblBilling[0].PaymentsVisited = true
        tblBilling[0].VendorRevenueVisited = true
        tblComplaints[0].visited = true
        tblDeffeciencies[0].visited = true
        tblPhotos[0].visited = true
        tblSurveys[0].visited = true
        tblVisitation[0].visited = false
    }


    fun getVisitedScreen() : String {
        var visitedScreens = ""
        visitedScreens += if (tblVisitation[0].visited) "1" else "0"
        visitedScreens += if (tblFacility[0].GeneralInfoVisited) "1" else "0"
        visitedScreens += if (tblFacility[0].RSPVisited) "1" else "0"
        visitedScreens += if (tblFacility[0].LocationVisited) "1" else "0"
        visitedScreens += if (tblFacility[0].PersonnelVisited) "1" else "0"
        visitedScreens += if (tblScopeOfServices[0].GeneralInfoVisited) "1" else "0"
        visitedScreens += if (tblScopeOfServices[0].VehicleServicesVisited) "1" else "0"
        visitedScreens += if (tblScopeOfServices[0].ProgramsVisited) "1" else "0"
        visitedScreens += if (tblScopeOfServices[0].FacilityServicesVisited) "1" else "0"
        visitedScreens += if (tblScopeOfServices[0].VehiclesVisited) "1" else "0"
//        visitedScreens += if (tblScopeOfServices[0].PromotionsVisited) "1" else "0"
        visitedScreens += if (tblScopeOfServices[0].AffiliationsVisited) "1" else "0"
        visitedScreens += if (tblDeffeciencies[0].visited) "1" else "0"
        visitedScreens += if (tblComplaints[0].visited) "1" else "1"
        visitedScreens += if (tblBilling[0].BillingPlanVisited) "1" else "1"
        visitedScreens += if (tblBilling[0].BillingVisited) "1" else "1"
        visitedScreens += if (tblBilling[0].PaymentsVisited) "1" else "1"
        visitedScreens += if (tblBilling[0].VendorRevenueVisited) "1" else "1"
        visitedScreens += if (tblBilling[0].BillingHistoryVisited) "1" else "1"
        visitedScreens += if (tblBilling[0].BillingAdjustmentsVisited) "1" else "1"
        visitedScreens += if (tblSurveys[0].visited) "1" else "1"
        visitedScreens += if (tblPhotos[0].visited) "1" else "1"
        return visitedScreens
    }

    fun markVisitedScreen(visitedScreens : String)  {
        tblVisitation[0].visited = visitedScreens.substring(0,1)=="1"
        tblFacility[0].GeneralInfoVisited = visitedScreens.substring(1,2)=="1"
        tblFacility[0].RSPVisited = visitedScreens.substring(2,3)=="1"
        tblFacility[0].LocationVisited = visitedScreens.substring(3,4)=="1"
        tblFacility[0].PersonnelVisited = visitedScreens.substring(4,5)=="1"
        tblFacility[0].VisitationTrackingVisited = true
        tblScopeOfServices[0].GeneralInfoVisited = visitedScreens.substring(5,6)=="1"
        tblScopeOfServices[0].VehicleServicesVisited = visitedScreens.substring(6,7)=="1"
        tblScopeOfServices[0].ProgramsVisited = visitedScreens.substring(7,8)=="1"
        tblScopeOfServices[0].FacilityServicesVisited = visitedScreens.substring(8,9)=="1"
        tblScopeOfServices[0].VehiclesVisited = visitedScreens.substring(9,10)=="1"
        tblScopeOfServices[0].AffiliationsVisited = visitedScreens.substring(10,11)=="1"
//        tblDeffeciencies[0].visited = visitedScreens.substring(11,12)=="1"
        tblDeffeciencies[0].visited = true
        tblComplaints[0].visited  = visitedScreens.substring(12,13)=="1"
//        tblBilling[0].BillingPlanVisited= visitedScreens.substring(13,14)=="1"
//        tblBilling[0].BillingVisited = visitedScreens.substring(14,15)=="1"
//        tblBilling[0].PaymentsVisited = visitedScreens.substring(15,16)=="1"
//        tblBilling[0].VendorRevenueVisited = visitedScreens.substring(16,17)=="1"
//        tblBilling[0].BillingHistoryVisited = visitedScreens.substring(17,18)=="1"
//        tblBilling[0].BillingAdjustmentsVisited = visitedScreens.substring(18,19)=="1"
        tblBilling[0].BillingPlanVisited= true
        tblBilling[0].BillingVisited = true
        tblBilling[0].PaymentsVisited = true
        tblBilling[0].VendorRevenueVisited = true
        tblBilling[0].BillingHistoryVisited = true
        tblBilling[0].BillingAdjustmentsVisited = true
        tblSurveys[0].visited = visitedScreens.substring(19,20)=="1"
        tblPhotos[0].visited = visitedScreens.substring(20)=="1"
    }

    fun validateAllScreensVisited() : Boolean {
        return validateSoSSectionVisited() && validateDeffecienciesSectionVisited() && validateVisitationSectionVisited() && validateFacilitySectionVisited() && validateComplaintsSectionVisited() && validateBillingSectionVisited() && validatePhotosSectionVisited() && validateSurveysSectionVisited()
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

    fun validateSoSSectionVisited() : Boolean {//Scope Of Services
        return validateSoSGeneralVisited() && validateSOSProgramsVisited() && validateSOSFacilityServicesVisited() && validateSOSAffiliationsVisited() && validateSOSVehicleVisited() && validateSOSVehicleServicesVisited() && validateSOSPromotionsVisited()
    }

    fun validateSoSSection() {//Scope Of Services
        validateSoSGeneral()
        validateSOSPrograms()
        validateSOSFacilityServices()
        validateSOSAffiliations()
    }

    fun validateDeffecienciesSectionVisited()  : Boolean {
        return validateDeffeciencyVisited()
    }

    fun validateDeffecienciesSection() {
        validateDeffeciency()
    }

    fun validateComplaintsSectionVisited()  : Boolean {
        return validateComplaintsVisited()
    }

    fun validateComplaintsSection() {
        validateComplaints()
    }

    fun validateSurveysSectionVisited()  : Boolean {
        return validateSurveysVisited()
    }

    fun validateSurveysSection() {
        validateSurveys()
    }

    fun validatePhotosSectionVisited()  : Boolean {
        return validatePhotosVisited()
    }

    fun validatePhotosSection() {
        validatePhotos()
    }

    fun validateVisitationSectionVisited()  : Boolean {
        return validateVisitationVisited()
    }

    fun validateVisitationSection() {
        validateVisitation()
    }

    fun validateBillingSection() {

    }

    fun validateBillingSectionVisited() : Boolean {
        return validateBillingPlanVisited() && validateBillingVisited() && validateVendorRevenueVisited() && validatePaymentsVisited() && validateBillingHistoryVisited() && validateBillingAdjustmentsVisited()
    }

    fun validateBillingAdjustmentsVisited() : Boolean {
        return tblBilling[0].BillingAdjustmentsVisited
    }

    fun validateBillingHistoryVisited()  : Boolean {
        return tblBilling[0].BillingHistoryVisited
    }

    fun validatePaymentsVisited()  : Boolean {
        return tblBilling[0].PaymentsVisited
    }

    fun validateVendorRevenueVisited()  : Boolean {
        return tblBilling[0].VendorRevenueVisited
    }

    fun validateBillingPlanVisited()  : Boolean {
        return tblBilling[0].BillingPlanVisited
    }

    fun validateBillingVisited()  : Boolean {
        return tblBilling[0].BillingVisited
    }

    fun validateFacilitySectionVisited()  : Boolean {
        return validateFacilityGeneralInfoVisited() && validateFacilityRSPVisited() && validateFacilityLocationVisited () && validateFacilityPersonnelVisited()
    }

    fun validateFacilitySection(){
        validateFacilityGeneralInfo()
        validateFacilityRSP()
        validateFacilityLocation()
        validateFacilityPersonnel()
    }

    fun validateSoSGeneralVisited() : Boolean {
        return tblScopeOfServices[0].GeneralInfoVisited
    }

    fun validateSOSVehicleServicesVisited() : Boolean {
        return tblScopeOfServices[0].VehicleServicesVisited
    }

    fun validateSOSVehicleVisited() : Boolean {
        return tblScopeOfServices[0].VehiclesVisited
    }

    fun validateSOSPromotionsVisited() : Boolean {
        return tblScopeOfServices[0].PromotionsVisited
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

    fun validateSOSProgramsVisited () : Boolean {
        return tblScopeOfServices[0].ProgramsVisited
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

    fun validateSOSFacilityServicesVisited(): Boolean {
        return tblScopeOfServices[0].FacilityServicesVisited
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

    fun validateSOSAffiliationsVisited() : Boolean {
        return tblScopeOfServices[0].AffiliationsVisited
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

    fun validateComplaintsVisited() : Boolean{
        return tblComplaints[0].visited
    }
    fun validateComplaints(){

    }

    fun validateSurveys(){

    }

    fun validateSurveysVisited() : Boolean{
        return tblSurveys[0].visited
    }

    fun validatePhotos(){

    }

    fun validatePhotosVisited() : Boolean{
        return tblPhotos[0].visited
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

    fun validateDeffeciencyVisited() : Boolean{
        return true //FacilityDataModel.getInstance().tblDeficiency.filter { s->s.ClearedDate.isNullOrEmpty() }.isEmpty()
//        tblDeffeciencies[0].visited

    }

    fun validateVisitation() {
        var isValid = true

        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType != null) {
            if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType!!.equals(VisitationTypes.AdHoc) || FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType!!.equals(VisitationTypes.Deficiency)) {
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

    fun validateVisitationVisited() : Boolean{
        return tblVisitation[0].visited
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

    fun validateFacilityGeneralInfoVisited() : Boolean{
        return tblFacility[0].GeneralInfoVisited
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

    fun validateFacilityRSPVisited() : Boolean{
        return tblFacility[0].RSPVisited
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

    fun validateFacilityLocationVisited() : Boolean{
        return tblFacility[0].LocationVisited
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

    fun validateFacilityPersonnelVisited() : Boolean {
        return tblFacility[0].PersonnelVisited
    }
}