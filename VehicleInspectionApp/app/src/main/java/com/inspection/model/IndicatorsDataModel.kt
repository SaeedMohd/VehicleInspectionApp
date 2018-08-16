package com.inspection.model

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

    class TblBilling {
        var BillingPlan = false
        var Billing = false
        var BillingAdjustments = false
        var Payments = false
        var VendorRevenue = false
    }

    class TblScopeOfServices {
        var GeneralInfo = false
        var Programs = false
        var FacilityServices = false
        var Affiliations = false
    }

    class TblDeffeciencies {
        var Deffeciency= false
    }

    class TblFacility {
        var GeneralInfo= false
        var RSP= false
    }

    class TblVisitation {
        var Visitation= false
    }

    fun init(){
        var SoS = TblScopeOfServices()
        SoS.GeneralInfo=false;
        SoS.Programs =false;
        SoS.FacilityServices=false;
        tblScopeOfServices.add(0,SoS)
        var Def = TblDeffeciencies()
        Def.Deffeciency=false;
        tblDeffeciencies.add(0,Def)
        var Visitation = TblVisitation()
        Visitation.Visitation=false;
        tblVisitation.add(0,Visitation)
        var Facility= TblFacility()
        Facility.GeneralInfo=false;
        Facility.RSP=false;
        tblFacility.add(0,Facility)
    }

    fun validateBusinessRules() {
        validateSoSSection()
        validateDeffecienciesSection()
        validateVisitationSection()
        validateFacilitySection()
//        refreshIndicatorsView()
    }

    fun validateSoSSection() {//Scope Of Services
        validateSoSGeneral()
        validateSOSPrograms()
        validateSOSFacilityServices()
        validateSOSAffiliations()
    }

    fun validateDeffecienciesSection() {
        validateDeffeciency()
    }

    fun validateVisitationSection() {
        validateVisitation()
    }

    fun validateFacilitySection(){
        validateFacilityGeneralInfo()
        validateFacilityRSP()
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

    fun validateSOSPrograms () {
        var isValid = true
        FacilityDataModel.getInstance().tblPrograms.apply {
            (0 until size).forEach {
                if (get(it).programtypename.equals("")) isValid = false;
                if (get(it).effDate.equals("")) isValid = false
                if (get(it).ProgramTypeID.equals("")) isValid = false
                if (get(it).Comments.equals("")) isValid = false
            }
        }
        tblScopeOfServices[0].Programs = isValid
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

    fun validateSOSAffiliations() {
        var isValid = true
        FacilityDataModel.getInstance().tblAffiliations.apply {
            (0 until size).forEach {
                if (get(it).effDate.equals("")) isValid = false
            }
        }
        tblScopeOfServices[0].Affiliations = isValid
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

    fun validateVisitation() {
        var isValid = true
        FacilityDataModel.getInstance().tblVisitationTracking.apply {
            (0 until size).forEach {
                if (get(it).visitationType!!.equals(FacilityDataModel.VisitationTypes.AdHoc)){

                } else {
                    if(get(it).facilityRepresentativeSignature == null) isValid=false
                }
                if (get(it).facilityRepresentativeName.equals("")) isValid = false
                if (get(it).automotiveSpecialistName.equals("")) isValid = false
                if (get(it).automotiveSpecialistSignature==null) isValid = false
                if (get(it).emailVisitationPdfToFacility && get(it).email.equals("")) isValid = false
                if (get(it).waiveVisitations&& get(it).waiverComments.equals("")) isValid = false
            }
        }
        tblVisitation[0].Visitation= isValid
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
    }

    fun validateFacilityRSP() {
        var isValid = true
        FacilityDataModel.getInstance().tblAARPortalAdmin.apply {
            (0 until size).forEach {
                if (!get(it).startDate.equals("") && get(it).AddendumSigned.equals("")) isValid = false
                if (!get(it).startDate.equals("") && get(it).CardReaders.equals("")) isValid = false
                if (get(it).PortalInspectionDate.equals("")) isValid = false
                if (get(it).NumberUnacknowledgedTows.equals("")) isValid = false
                if (get(it).InProgressTows.equals("")) isValid = false
                if (get(it).InProgressWalkIns.equals("")) isValid = false
                if (get(it).LoggedIntoPortal.equals("")) isValid = false
            }
        }
        tblFacility[0].RSP= isValid
    }

    fun validateFacilityLocation() {
        var isValid = true
        FacilityDataModel.getInstance().tblAddress.apply {
            (0 until size).forEach {
//                if (!get(it).LocationTypeID.equals(Location) && get(it).AddendumSigned.equals("")) isValid = false
//                if (!get(it).startDate.equals("") && get(it).CardReaders.equals("")) isValid = false
//                if (get(it).PortalInspectionDate.equals("")) isValid = false
//                if (get(it).NumberUnacknowledgedTows.equals("")) isValid = false
//                if (get(it).InProgressTows.equals("")) isValid = false
//                if (get(it).InProgressWalkIns.equals("")) isValid = false
//                if (get(it).LoggedIntoPortal.equals("")) isValid = false
            }
        }
        tblFacility[0].RSP= isValid
    }
}