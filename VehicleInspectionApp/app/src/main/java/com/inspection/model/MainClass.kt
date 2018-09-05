package com.inspection.model

import android.content.Context

open class MainClass{
    var tblPhone = ArrayList<TblPhone>()

    class TblPhone {
        var phoneIsInputsValid = false
        var PhoneTypeID = ""
        var PhoneNumber = ""
    }

}

class Main1 : MainClass{
    constructor() : super()
    companion object {

        @Volatile
        private var INSTANCE: Main1? = null

        fun getInstance(): Main1 =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: Main1().also { INSTANCE = it }
                }

        fun setInstance(main1: Main1){
            INSTANCE = main1
        }
    }
    init {
        var x = TblPhone()
        x.PhoneNumber="1234"
        x.PhoneTypeID="0"
        tblPhone.add(x)
    }

}

class Main2 : MainClass{
    constructor() : super()
    companion object {

        @Volatile
        private var INSTANCE: Main2? = null

        fun getInstance(): Main2 =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: Main2().also { INSTANCE = it }
                }

        fun setInstance(main2: Main2){
            INSTANCE = main2
        }
    }
    init {
        var x = TblPhone()
        x.PhoneNumber="1234"
        x.PhoneTypeID="0"
        tblPhone.add(x)
    }
}