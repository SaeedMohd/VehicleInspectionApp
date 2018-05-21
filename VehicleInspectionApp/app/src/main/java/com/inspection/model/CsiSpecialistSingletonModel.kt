package com.inspection.model

class CsiSpecialistSingletonModel {

    companion object {

        @Volatile
        private var INSTANCE: CsiSpecialistSingletonModel? = null

        fun getInstance(): CsiSpecialistSingletonModel =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: CsiSpecialistSingletonModel().also { INSTANCE = it }
                }

        fun setInstance(csiSpecialistSingletonModel: CsiSpecialistSingletonModel){
            INSTANCE = csiSpecialistSingletonModel
        }
    }

    fun clear() {
        INSTANCE = null
    }


    var csiSpecialists= ArrayList<CsiSpecialist>()


}