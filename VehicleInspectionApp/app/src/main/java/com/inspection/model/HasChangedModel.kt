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
    var groupFacilityContactInfo = ArrayList<GroupFacilityContactInfo>()
    var groupFacilityPersonnel = ArrayList<GroupFacilityPersonnel>()

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

    class GroupFacilityContactInfo {
        var FacilityAddress= false
        var FacilityPhone= false
        var FacilityEmail= false
        var FacilityHours= false

    }

    class GroupFacilityPersonnel {
        var FacilityPersonnel = false
    }

    fun init() {
        var GFPersonnel = GroupFacilityPersonnel()
        GFPersonnel.FacilityPersonnel= false
        groupFacilityPersonnel.add(GFPersonnel)
        var GFContactInfo = GroupFacilityContactInfo()
        GFContactInfo.FacilityAddress= false
        GFContactInfo.FacilityEmail= false
        GFContactInfo.FacilityHours= false
        GFContactInfo.FacilityPhone= false
        groupFacilityContactInfo.add(GFContactInfo)
        var GFRSP= GroupFacilityRSP()
        GFRSP.FacilityRSP = false
        groupFacilityRSP.add(GFRSP)
        var GFGeneral = GroupFacilityGeneralInfo()
        GFGeneral.FacilityGeneral = false
        GFGeneral.FacilityGeneralPaymentMethods = false
        GFGeneral.FacilityTimeZone = false
        GFGeneral.FacilityType = false
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

    fun changeDoneForFacilityPersonnel() : Boolean {
        if (groupFacilityPersonnel[0].FacilityPersonnel) {
            groupFacility[0].FacilityPersonnel=true
            return true
        } else {
            groupFacility[0].FacilityPersonnel=false
            return false
        }
    }

    fun changeDoneForFacilityContactInfo() : Boolean {
        if (groupFacilityContactInfo[0].FacilityAddress || groupFacilityContactInfo[0].FacilityEmail || groupFacilityContactInfo[0].FacilityHours || groupFacilityContactInfo[0].FacilityPhone) {
            groupFacility[0].FacilityContactInfo=true
            return true
        } else {
            groupFacility[0].FacilityContactInfo=false
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

    fun checkGeneralInfoTblHoursChange () {
        var changeWasDone = false
        if (FacilityDataModel.getInstance().tblHours[0].SatOpen!=FacilityDataModelOrg.getInstance().tblHours[0].SatOpen) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblHours[0].SatClose!=FacilityDataModelOrg.getInstance().tblHours[0].SatClose) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblHours[0].SunOpen!=FacilityDataModelOrg.getInstance().tblHours[0].SunOpen) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblHours[0].SunClose!=FacilityDataModelOrg.getInstance().tblHours[0].SatClose) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblHours[0].MonOpen!=FacilityDataModelOrg.getInstance().tblHours[0].MonOpen) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblHours[0].MonClose!=FacilityDataModelOrg.getInstance().tblHours[0].MonClose) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblHours[0].TueOpen!=FacilityDataModelOrg.getInstance().tblHours[0].TueOpen) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblHours[0].TueClose!=FacilityDataModelOrg.getInstance().tblHours[0].TueClose) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblHours[0].WedOpen!=FacilityDataModelOrg.getInstance().tblHours[0].WedOpen) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblHours[0].WedClose!=FacilityDataModelOrg.getInstance().tblHours[0].WedClose) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblHours[0].ThuOpen!=FacilityDataModelOrg.getInstance().tblHours[0].ThuOpen) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblHours[0].ThuClose!=FacilityDataModelOrg.getInstance().tblHours[0].ThuClose) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblHours[0].FriOpen!=FacilityDataModelOrg.getInstance().tblHours[0].FriOpen) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblHours[0].FriClose!=FacilityDataModelOrg.getInstance().tblHours[0].FriClose) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblHours[0].NightDrop!=FacilityDataModelOrg.getInstance().tblHours[0].NightDrop) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblHours[0].NightDropInstr!=FacilityDataModelOrg.getInstance().tblHours[0].NightDropInstr) changeWasDone = true
        groupFacilityContactInfo[0].FacilityHours = changeWasDone

    }


}