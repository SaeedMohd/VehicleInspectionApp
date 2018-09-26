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
        var Location = false
        var Personnel = false
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
        Facility.Location=false;
        Facility.Personnel=false;
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
        validateFacilityLocation()
        validateFacilityPersonnel()
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

    fun validateFacilityRSP() {
        var isValid = true
        if (FacilityDataModel.getInstance().tblAARPortalAdmin.size==1 && FacilityDataModel.getInstance().tblAARPortalAdmin[0].CardReaders.equals("-1")) isValid=false
        else {
            FacilityDataModel.getInstance().tblAARPortalAdmin.apply {
                (0 until size).forEach {
                    if (!get(it).CardReaders.equals("-1")) {
                        if (!get(it).startDate.equals("") && get(it).AddendumSigned.equals("")) isValid = false
                        if (!get(it).startDate.equals("") && get(it).CardReaders.equals("")) isValid = false
                        if (get(it).PortalInspectionDate.equals("")) isValid = false
                        if (get(it).NumberUnacknowledgedTows.equals("")) isValid = false
                        if (get(it).InProgressTows.equals("")) isValid = false
                        if (get(it).InProgressWalkIns.equals("")) isValid = false
                        if (get(it).LoggedIntoPortal.equals("")) isValid = false
                    }
                }
            }
        }
        tblFacility[0].RSP= isValid
        Log.v("Facility RSP: --->",isValid.toString());
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
}