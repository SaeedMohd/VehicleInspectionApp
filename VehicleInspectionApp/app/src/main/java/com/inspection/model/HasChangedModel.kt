package com.inspection.model

import android.util.Log

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

    var groupSoS = ArrayList<GroupSoS>()
    var groupSoSGeneralInfo = ArrayList<GroupSoSGeneralInfo>()
    var groupSoSVehicleService= ArrayList<GroupSoSVehicleService>()
    var groupSoSVehicles  = ArrayList<GroupSoSVehicles>()
    var groupSoSPrograms = ArrayList<GroupSoSPrograms>()
    var groupSoSPromotions = ArrayList<GroupSoSPromotions>()
    var groupSoSAffiliations = ArrayList<GroupSoSAffiliations>()
    var groupSoSAwards = ArrayList<GroupSoSAwards>()
    var groupSoSOthers = ArrayList<GroupSoSOthers>()
    var groupSoSFacilityServices = ArrayList<GroupSoSFacilityServices>()


    var groupDeficiency = ArrayList<GroupDeficiency>()
    var groupPhoto= ArrayList<GroupPhoto>()
    var groupDeficiencyDef = ArrayList<GroupDefeciencyDef>()


    class GroupDeficiency {
        var DeficiencyDef = false
    }

    class GroupPhoto {
        var Photos = false
    }

    class GroupDefeciencyDef {
        var DeficiencyDef = false
    }



    class GroupFacility {
        var FacilityGeneral = false
        var FacilityContactInfo = false
        var FacilityRSP = false
        var FacilityPersonnel = false
//        var FacilityAmendmentsOrderTracking = false
    }

    class GroupFacilityGeneralInfo {
        var FacilityGeneral = false
        var FacilityTimeZone= false
        var FacilityGeneralPaymentMethods= false
        var FacilityType= false
        var AffiliateVendor = false
        var FacilityNAPANo = false
        var FacilityNationalNo = false

    }

    class GroupFacilityRSP {
        var FacilityRSP = false
    }

    class GroupFacilityContactInfo {
        var FacilityAddress= false
        var FacilityPhone= false
        var FacilityEmail= false
        var FacilityHours= false
        var FacilityLanguages= false
    }

    class GroupFacilityPersonnel {
        var FacilityPersonnel = false
    }


    class GroupSoS {
        var SoSGeneralInfo = false
        var SoSVehicleService = false
        var SoSFacilityServices = false
        var SoSPrograms = false
        var SoSVehicles = false
        var SoSAffiliations = false
        var SoSPromotions = false
        var SoSAwards = false
        var SoSOthers = false
    }

    class GroupSoSGeneralInfo {
        var SoSGeneral = false
        var SoSDiscPercentage = false
        var SoSDiscAmount = false
    }

    class GroupSoSVehicleService {
        var SoSVehicleService = false
    }

    class GroupSoSFacilityServices {
        var SoSFacilityServices = false
    }

    class GroupSoSPrograms {
        var SoSPrograms = false
    }

    class GroupSoSVehicles {
        var SoSVehicles = false
    }

    class GroupSoSAffiliations {
        var SoSAffiliations = false
    }

    class GroupSoSPromotions {
        var SoSPromotions = false
    }

    class GroupSoSAwards {
        var SoSAwards = false
    }

    class GroupSoSOthers {
        var SoSOthers = false
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
        GFContactInfo.FacilityLanguages= false
        groupFacilityContactInfo.add(GFContactInfo)
        var GFRSP= GroupFacilityRSP()
        GFRSP.FacilityRSP = false
        groupFacilityRSP.add(GFRSP)
        var GFGeneral = GroupFacilityGeneralInfo()
        GFGeneral.FacilityGeneral = false
        GFGeneral.FacilityGeneralPaymentMethods = false
        GFGeneral.AffiliateVendor = false
        GFGeneral.FacilityTimeZone = false
        GFGeneral.FacilityType = false
        GFGeneral.FacilityNAPANo = false
        GFGeneral.FacilityNationalNo = false

        groupFacilityGeneralInfo.add(GFGeneral)
        var GF = GroupFacility()
        GF.FacilityGeneral= false;
        GF.FacilityContactInfo= false;
        groupFacility.add(GF)
        var SoSGeneralInfo = GroupSoSGeneralInfo()
        SoSGeneralInfo.SoSGeneral=false
        SoSGeneralInfo.SoSDiscAmount=false
        SoSGeneralInfo.SoSDiscPercentage=false
        groupSoSGeneralInfo.add(SoSGeneralInfo)
        var SoSVehicleServices = GroupSoSVehicleService()
        SoSVehicleServices.SoSVehicleService=false
        groupSoSVehicleService.add(SoSVehicleServices)
        var SoSVehicles= GroupSoSVehicles()
        SoSVehicles.SoSVehicles=false
        groupSoSVehicles.add(SoSVehicles)
        var SoSPromotions= GroupSoSPromotions()
        SoSPromotions.SoSPromotions=false
        groupSoSPromotions.add(SoSPromotions)
        var SoSPrograms= GroupSoSPrograms()
        SoSPrograms.SoSPrograms=false
        groupSoSPrograms.add(SoSPrograms)
        var SoSAffiliations= GroupSoSAffiliations()
        SoSAffiliations.SoSAffiliations=false
        groupSoSAffiliations.add(SoSAffiliations)
        var SoSAwards= GroupSoSAwards()
        SoSAwards.SoSAwards=false
        groupSoSAwards.add(SoSAwards)
        var SoSOthers= GroupSoSOthers()
        SoSOthers.SoSOthers=false
        groupSoSOthers.add(SoSOthers)
        var SoSFacilityServices= GroupSoSFacilityServices()
        SoSFacilityServices.SoSFacilityServices=false
        groupSoSFacilityServices.add(SoSFacilityServices)
        var SOS = GroupSoS()
        SOS.SoSAffiliations= false;
        SOS.SoSAwards= false;
        SOS.SoSFacilityServices= false;
        SOS.SoSGeneralInfo= false;
        SOS.SoSOthers= false;
        SOS.SoSPrograms= false;
        SOS.SoSPromotions= false;
        SOS.SoSVehicleService= false;
        SOS.SoSVehicles= false;
        groupSoS.add(SOS)


        var photoDef = GroupPhoto()
        photoDef.Photos=false
        groupPhoto.add(photoDef)

        var DeficiencyDef= GroupDefeciencyDef()
        DeficiencyDef.DeficiencyDef=false
        groupDeficiencyDef.add(DeficiencyDef)
        var Deficiency = GroupDeficiency()
        Deficiency.DeficiencyDef = false
        groupDeficiency.add(Deficiency)
    }


    fun changeWasMadeFroAny () : Boolean {
        Log.v("ForFacilityGGroup-->", changeDoneForFacilityGeneralGroup().toString())
        Log.v("ForSoSGroup-->", changeDoneForSoSGroup().toString())
        Log.v("ForPhotoDef-->", changeDoneForPhotoDef().toString())
        Log.v("ForDeficiencyDef-->", changeDoneForDeficiencyDef().toString())
        return (changeDoneForFacilityGeneralGroup() || changeDoneForSoSGroup() || changeDoneForPhotoDef() || changeDoneForDeficiencyDef())
    }

    fun changeDoneForFacilityGeneralGroup() : Boolean{
        var changeWasDone = false
        if (changeDoneForFacilityGeneralInfo() || changeDoneForFacilityContactInfo() || changeDoneForFacilityPersonnel() || changeDoneForFacilityRSP()){
            changeWasDone = true
        }
        return changeWasDone
    }

    fun changeDoneForSoSGroup() : Boolean{
        var changeWasDone = false
        if (changeDoneForFacilityGeneralInfo() || changeDoneForFacilityContactInfo() || changeDoneForFacilityPersonnel() || changeDoneForFacilityRSP()){
            changeWasDone = true
        }
        return changeWasDone
    }


    fun changeDoneForFacilityGeneralInfo() : Boolean {
        if (groupFacilityGeneralInfo[0].FacilityGeneral || groupFacilityGeneralInfo[0].FacilityGeneralPaymentMethods || groupFacilityGeneralInfo[0].FacilityTimeZone || groupFacilityGeneralInfo[0].FacilityType || groupFacilityGeneralInfo[0].FacilityNAPANo || groupFacilityGeneralInfo[0].FacilityNationalNo || groupFacilityGeneralInfo[0].AffiliateVendor) {
            groupFacility[0].FacilityGeneral=true
            return true
        } else {
            groupFacility[0].FacilityGeneral=false
            return false
        }
    }

    fun changeDoneForSoSGeneral() : Boolean {
        if (groupSoSGeneralInfo[0].SoSGeneral) {
            groupSoS[0].SoSGeneralInfo=true
            return true
        } else {
            groupSoS[0].SoSGeneralInfo=false
            return false
        }
    }

    fun changeDoneForSoSVehicleServices() : Boolean {
        if (groupSoSVehicleService[0].SoSVehicleService) {
            groupSoS[0].SoSVehicleService=true
            return true
        } else {
            groupSoS[0].SoSVehicleService=false
            return false
        }
    }

    fun changeDoneForSoSVehicles() : Boolean {
        if (groupSoSVehicles[0].SoSVehicles) {
            groupSoS[0].SoSVehicles=true
            return true
        } else {
            groupSoS[0].SoSVehicles=false
            return false
        }
    }

    fun changeDoneForSoSPrograms() : Boolean {
        if (groupSoSPrograms[0].SoSPrograms) {
            groupSoS[0].SoSPrograms=true
            return true
        } else {
            groupSoS[0].SoSPrograms=false
            return false
        }
    }

    fun changeDoneForSoSAffiliations() : Boolean {
        if (groupSoSAffiliations[0].SoSAffiliations) {
            groupSoS[0].SoSAffiliations=true
            return true
        } else {
            groupSoS[0].SoSAffiliations=false
            return false
        }
    }


    fun changeDoneForSoSFacilityServices() : Boolean {
        if (groupSoSFacilityServices[0].SoSFacilityServices) {
            groupSoS[0].SoSFacilityServices=true
            return true
        } else {
            groupSoS[0].SoSFacilityServices=false
            return false
        }
    }

    fun changeDoneForDeficiencyDef() : Boolean {
        if (groupDeficiencyDef[0].DeficiencyDef) {
            groupDeficiency[0].DeficiencyDef=true
            return true
        } else {
            groupDeficiency[0].DeficiencyDef=false
            return false
        }
    }

    fun changeDoneForPhotoDef() : Boolean {
        if (groupPhoto[0].Photos) {
            groupPhoto[0].Photos=true
            return true
        } else {
            groupPhoto[0].Photos==false
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
        if (groupFacilityContactInfo[0].FacilityAddress || groupFacilityContactInfo[0].FacilityEmail || groupFacilityContactInfo[0].FacilityHours || groupFacilityContactInfo[0].FacilityPhone || groupFacilityContactInfo[0].FacilityLanguages) {
            groupFacility[0].FacilityContactInfo=true
            return true
        } else {
            groupFacility[0].FacilityContactInfo=false
            return false
        }
    }

    fun checkRSPFacilityChange () {
        var changeWasDone = false
        if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].CardReaders!=FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].CardReaders) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate!=FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].startDate) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate!=FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].endDate) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned!=FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].AddendumSigned) changeWasDone = true
        groupFacilityRSP[0].FacilityRSP = changeWasDone
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

    fun checkGeneralInfoTblLanguagesChange () {
        var changeWasDone = false
        if (FacilityDataModelOrg.getInstance().tblLanguage.size > 0) {
            if (FacilityDataModel.getInstance().tblLanguage[0].LangTypeID != FacilityDataModelOrg.getInstance().tblLanguage[0].LangTypeID) changeWasDone = true
        } else changeWasDone = true
        groupFacilityContactInfo[0].FacilityLanguages = changeWasDone
    }

    fun checkGeneralInfoTblAffiliateVendor () {
        var changeWasDone = false
        if (FacilityDataModelOrg.getInstance().tblAffiliateVendorFacilities.size > 0) {
            if (FacilityDataModel.getInstance().tblAffiliateVendorFacilities[0].AffiliateVendor != FacilityDataModelOrg.getInstance().tblAffiliateVendorFacilities[0].AffiliateVendor) changeWasDone = true
        } else changeWasDone = true
        groupFacilityGeneralInfo[0].AffiliateVendor = changeWasDone
    }


    fun checkIfChangeWasDoneforVisitation() : Boolean{
        var changeWasDone = false
        if (changeDoneForFacilityGeneralGroup()){
            changeWasDone = true
        }
        return changeWasDone
    }

    fun checkIfChangeWasDoneforSoSVehicleServices() : Boolean{
        var changeWasDone = false
        if (changeDoneForSoSVehicleServices()){
            changeWasDone = true
        }
        return changeWasDone
    }

    fun checkIfChangeWasDoneforSoSVehicles() : Boolean{
        var changeWasDone = false
        if (changeDoneForSoSVehicles()){
            changeWasDone = true
        }
        return changeWasDone
    }

    fun checkIfChangeWasDoneforSoSGeneral() {
        var changeWasDone = false
        if (FacilityDataModel.getInstance().tblScopeofService[0].DiagnosticsRate!=FacilityDataModelOrg.getInstance().tblScopeofService[0].DiagnosticsRate) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblScopeofService[0].FixedLaborRate!=FacilityDataModelOrg.getInstance().tblScopeofService[0].FixedLaborRate) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblScopeofService[0].LaborMax!=FacilityDataModelOrg.getInstance().tblScopeofService[0].LaborMax) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblScopeofService[0].LaborMin!=FacilityDataModelOrg.getInstance().tblScopeofService[0].LaborMin) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblScopeofService[0].NumOfBays!=FacilityDataModelOrg.getInstance().tblScopeofService[0].NumOfBays) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblScopeofService[0].NumOfLifts!=FacilityDataModelOrg.getInstance().tblScopeofService[0].NumOfLifts) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID!=FacilityDataModelOrg.getInstance().tblScopeofService[0].WarrantyTypeID) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblScopeofService[0].DiscountAmount!=FacilityDataModelOrg.getInstance().tblScopeofService[0].DiscountAmount) changeWasDone = true
        else if (FacilityDataModel.getInstance().tblScopeofService[0].DiscountCap!=FacilityDataModelOrg.getInstance().tblScopeofService[0].DiscountCap) changeWasDone = true
        groupSoSGeneralInfo[0].SoSGeneral= changeWasDone
    }

    fun checkIfChangeWasDoneforSoSPrograms() {
        var changeWasDone = false
        if (changeDoneForSoSPrograms()){
            changeWasDone = true
        }
        groupSoSPrograms[0].SoSPrograms= changeWasDone
    }

    fun checkIfChangeWasDoneforSoSAffiliations() {
        var changeWasDone = false
        if (changeDoneForSoSAffiliations()){
            changeWasDone = true
        }
        groupSoSAffiliations[0].SoSAffiliations= changeWasDone
    }

    fun checkIfChangeWasDoneforSoSFacilityServices() {
        var changeWasDone = false
        if (changeDoneForSoSFacilityServices()){
            changeWasDone = true
        }
        groupSoSFacilityServices[0].SoSFacilityServices = changeWasDone
    }

}