package com.inspection.model

class HasChangedModel {

    companion object {

        @Volatile
        private var INSTANCE: HasChangedModel? = null

        fun getInstance(): HasChangedModel =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: HasChangedModel().also { INSTANCE = it }
                }

        fun setInstance(hasChangedModel: HasChangedModel) {
            INSTANCE = hasChangedModel
        }
    }

    fun clear() {
        INSTANCE = null
    }

    var groupFacility = ArrayList<GroupFacility>()
    var groupFacilityGeneralInfo = ArrayList<GroupFacilityGeneralInfo>()
    var groupFacilityRSP = ArrayList<GroupFacilityRSP>()

    class GroupFacility {
        var FacilityGeneral = false
        var FacilityContactInfo = false
        var FacilityRSP = false
        var FacilityPersonnel = false
        var FacilityAmendmentsOrderTracking = false
    }

    class GroupFacilityGeneralInfo {
        var FacilityGeneral = false
        var FacilityTimeZone= false
        var FacilityGeneralPaymentMethods= false
        var FacilityType= false
    }

    class GroupFacilityRSP {
        var FacilityRSP = false
    }

    class GroupFacilityCOntactInfo {
        var FacilityRSP = false
    }

    fun init() {
        var GFRSP= GroupFacilityRSP()
        GFRSP.FacilityRSP = false
        groupFacilityRSP.add(GFRSP)
        var GFGeneral = GroupFacilityGeneralInfo()
        GFGeneral.FacilityGeneral = false
        GFGeneral.FacilityGeneralPaymentMethods = false
        GFGeneral.FacilityTimeZone = false
        GFGeneral.FacilityType = false
//        GFGeneral.SaveCompleted=false
        groupFacilityGeneralInfo.add(GFGeneral)
        var GF = GroupFacility()
        GF.FacilityGeneral= false;
        GF.FacilityContactInfo= false;
        groupFacility.add(0, GF)
    }

    fun changeDoneForFacilityGeneralInfo() : Boolean {
        if (groupFacilityGeneralInfo[0].FacilityGeneral || groupFacilityGeneralInfo[0].FacilityGeneralPaymentMethods || groupFacilityGeneralInfo[0].FacilityTimeZone || groupFacilityGeneralInfo[0].FacilityType) {
            groupFacility[0].FacilityGeneral=true
            return true
        } else {
            groupFacility[0].FacilityGeneral=false
            return false
        }
    }

    fun changeDoneForFacilityRSP() : Boolean {
        if (groupFacilityRSP[0].FacilityRSP) {
            groupFacility[0].FacilityRSP=true
            return true
        } else {
            groupFacility[0].FacilityRSP=false
            return false
        }
    }

    fun checkGeneralInfoTblFacilitiesChange () {
        var changeWasDone = false
        if (FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate!=FacilityDataModelOrg.getInstance().tblFacilities[0].InsuranceExpDate) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblFacilities[0].WebSite!=FacilityDataModelOrg.getInstance().tblFacilities[0].WebSite) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblFacilities[0].InternetAccess!=FacilityDataModelOrg.getInstance().tblFacilities[0].InternetAccess) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount!=FacilityDataModelOrg.getInstance().tblFacilities[0].FacilityRepairOrderCount) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability!=FacilityDataModelOrg.getInstance().tblFacilities[0].SvcAvailability) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate!=FacilityDataModelOrg.getInstance().tblFacilities[0].AutomotiveRepairExpDate) changeWasDone = true
        groupFacilityGeneralInfo[0].FacilityGeneral = changeWasDone
//        groupFacilityGeneralInfo[0].SaveCompleted = !changeWasDone
//        return changeWasDone
    }


}