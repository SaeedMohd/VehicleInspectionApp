package com.inspection.model.SUBMITIONS

class ScopeOfService {


    companion object {

        @Volatile
        private var INSTANCE: ScopeOfService? = null

        fun getInstance(): ScopeOfService =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: ScopeOfService().also { INSTANCE = it }
                }

        fun setInstance(scopeOfService: ScopeOfService) {
            INSTANCE = scopeOfService
        }
    }

    fun clear() {
        INSTANCE = null
    }

   var FACID =""
    var  LaborRateID=""
    var  LaborMax=""
    var   LaborMin=""
    var   FixedLaborRate=""
    var   numOfLifts=""
    var   insertBy="juhuhgu"



}