package com.inspection.model

enum class StagedCertStatus { PENDING, SAVING, SAVED, FAILED }

class StagedCertificateEntry {
    var certificationTypeId: String = ""
    var certificationTypeName: String = ""
    var certificationDate: String = ""
    var expirationDate: String = ""
    var certDesc: String = ""
    var personnelId: Int = 0
    var status: StagedCertStatus = StagedCertStatus.PENDING
    var errorMessage: String = ""
}
