package com.inspection.model

class TodayVisitationModel {
    var facNum: Int = 0
    var facName: String = ""
    var clubCode: String = ""
    var status: String = ""
    var type : VisitationTypes = VisitationTypes.Annual
    var city: String = ""
    var facAnnualMonth: Int = 1
    var longitude: Double = 0.0
    var latitude: Double = 0.0
    var order: Int = 0
    var notes: String = ""
    var etaLabel: String = ""

}


class ApplicantMatchingFacilitiesModel {
    var placeid: String = ""
    val clientfacnum: String = ""
    val clubcode: String = ""
    val facname: String = ""
    val address1: String = ""
    val status: String = ""
}


class CompletedVisitationModel {
    var facno: String = ""
    var facName: String = ""
    var clubcode: String = ""
    var visitationid: String = ""
    var specialist: String = ""
    var facilityrep: String = ""
    var visitationmethod: String = ""
    var visitationreason: String = ""
    var waivevisitation: Int = 0
    var waivecomments: String = ""
    var insertdate: String = ""
    var status: String = ""
    var facid: String = ""
    var visitationtype : VisitationTypes = VisitationTypes.Annual
}

class VisitationsStats(
    val tot_week: Int,
    val tot_month: Int
)

data class DailyCount(
    val date: String, // "2025-11-12"
    val count: Int
)