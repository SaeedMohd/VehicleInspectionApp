package com.inspection.model

class PRGDataModel {

    companion object {

        @Volatile
        private var INSTANCE: PRGDataModel? = null

        fun getInstance(): PRGDataModel =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: PRGDataModel().also { INSTANCE = it }
                }

        fun setInstance(prgDataModel: PRGDataModel) {
            INSTANCE = prgDataModel
        }
    }

    fun clear() {
        INSTANCE = null
    }

    var tblPRGFacilitiesPhotos = ArrayList<PRGFacilityPhotos>()
    var tblPRGLogChanges = ArrayList<PRGLogChanges>()
    var tblPRGVisitationHeader= ArrayList<PRGVisitationHeader>()
    var tblPRGCompletedVisitations= ArrayList<PRGCompletedVisitations>()
    var tblPRGVisitationsLog= ArrayList<PRGVisitationsLog>()
    var tblPRGFacilityDetails= ArrayList<PRGFacilityDetails>()
    var tblPRGRepairDiscountFactors= ArrayList<PRGRepairDiscountFactors>()
    var tblPRGPersonnelDetails= ArrayList<PRGPersonnelDetails>()
    var tblPRGFacilityDirectors= ArrayList<PRGFacilityDirectors>()

}