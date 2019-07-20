package com.inspection.model

class CSIFacilitySingelton {
    companion object {

        @Volatile
        private var INSTANCE: CSIFacilitySingelton? = null

        fun getInstance(): CSIFacilitySingelton =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: CSIFacilitySingelton().also { INSTANCE = it }
                }

        fun setInstance(csiFacilitySingelton: CSIFacilitySingelton){
            INSTANCE = csiFacilitySingelton
        }
    }

    fun clear() {
        INSTANCE = null
    }


    var csiFacilities = ArrayList<CsiFacility>()
}