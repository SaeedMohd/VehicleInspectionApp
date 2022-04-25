package com.inspection.model

class HasChangedDetailsModel {
    companion object {
        @Volatile
        private var INSTANCE: HasChangedDetailsModel? = null
        fun getInstance(): HasChangedDetailsModel =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: HasChangedDetailsModel().also { INSTANCE = it }
                }
        fun setInstance(hasChangedDetailsModel: HasChangedDetailsModel) {
            INSTANCE = hasChangedDetailsModel
        }
    }

    fun clear() {
        HasChangedDetailsModel.INSTANCE = null
    }

    class VisitationScreenChanges {
        var memberBenefits = false
        var aarSign = false
        var qcProcess = false
        var staffTrainingProcess = false
        var certificateOfApproval = false
    }

    class FacilityGeneralInfoScreenChanges {
        var memberBenefits = false
        var aarSign = false
        var qcProcess = false
        var staffTrainingProcess = false
        var certificateOfApproval = false
    }


}