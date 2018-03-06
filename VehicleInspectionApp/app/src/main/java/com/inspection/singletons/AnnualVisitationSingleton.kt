package com.inspection.singletons

import com.inspection.MainActivity
import java.text.SimpleDateFormat

/**
 * Created by devsherif on 3/5/18.
 */
class AnnualVisitationSingleton {


    var facilityId = -1

    //General Information Detials
    var facilityName: String? = ""
    var automotiveSpecialist: String? = ""
    var facilityRepresentative: String? = ""
    var inspectionType = -1
    var monthDue = -1
    var changesMade = false
    var dateOfVisitation : Long? = -1

    //Facility Details
    var contractType = -1
    var contractNumber = -1
    var office = -1
    var assignedTo = ""
    var dba = ""
    var entityName = ""
    var businessType = ""
    var timeZone = -1
    var webSiteUrl = ""
    var wifiAvailable = false
    var taxId = ""
    var repairOrderCount = -1
    var serviceAvailability = -1
    var facilityType = -1
    var ardNumber = -1
    var ardExpirationDate = -1L
    fun setArdExpirationDate(dateString: String) {
        ardExpirationDate = dateString.toDate()
    }

    var providerType = ""
    var shopManagementSystem = ""
    var currentContractDate = ""
    var initialContractDate = -1L
    fun setInitialContractDate(dateString: String) {
        initialContractDate = dateString.toDate()
    }

    var billingMonth = -1
    var billingAmount = -1

    var insuranceExpirationDate = -1L
    fun setInsuranceExpirationDate(dateString: String){
        insuranceExpirationDate = dateString.toDate()
    }


    //Facility Continued
    var paymentMethods = ""

    var mondayOpen = ""
    var mondayClose = ""
    var tuesdayOpen = ""
    var tuesdayClose = ""
    var wednesdayOpen = ""
    var wednesdayClose = ""
    var thursdayOpen = ""
    var thursdayClose = ""
    var fridayOpen = ""
    var fridayClose = ""
    var saturdayOpen = ""
    var saturdayClose = ""
    var sundayOpen = ""
    var sundayClose = ""

    var isNightDrop = false
    var nightInstructions = ""

    var emailType = -1
    var emailAddress = ""

    var phoneType = -1
    var phoneNumber = ""

    // Location Information
    var physicalLocationAddress = ""
    var physicalLocationLatitude = ""
    var physicalLocationLongitude = ""
    var physicalLocationBranchNumber = -1
    var physicalLocationBranchName = ""
    var physicalLocationAddress2 = ""

    var mailingLocationAddress = ""
    var mailingLocationBranchNumber = -1
    var mailingLocationBranchName = ""
    var mailingLocationAddress2 = ""

    var billingLocationAddress = ""
    var billingLocationBranchNumber = -1
    var billingLocationBranchName = ""
    var billingLocationAddress2 = ""

    //Personnel Information
    var personnelType = -1
    var personnelFirstName = ""
    var personnelLastName = ""

    var certificationNumber = ""
    var rspUserId = -1
    var rspEmailAddress = ""
    var seniorityDate = -1L
    var peronnelStartDate = -1L
    var personnelEndDate = -1

    var isContractSigner = false
    var contractSignerAddress = ""
    var contractSignerAddress2 = ""
    var contractSignerCity = ""
    var contractSignerState = ""
    var contractSignerZip = ""
    var contractSignerPhone = ""
    var contractSignerEmail = ""
    var contractSignerStartDate = -1L
    var contractSignerEndDate = -1L

    var isPrimaryMailReciepient = false

    var a1CertificationDate = -1L
    var a1ExpirationDate = -1L
    var a2CertificationDate = -1L
    var a2ExpirationDate = -1L
    var a3CertificationDate = -1L
    var a3ExpirationDate = -1L
    var a4CertificationDate = -1L
    var a4ExpirationDate = -1L
    var a5CertificationDate = -1L
    var a5ExpirationDate = -1L
    var a6CertificationDate = -1L
    var a6ExpirationDate = -1L
    var a7CertificationDate = -1L
    var a7ExpirationDate = -1L
    var a8CertificationDate = -1L
    var a8ExpirationDate = -1L
    var c1CertificationDate = -1L
    var c1ExpirationDate = -1L




    fun String.toDate(): Long {
        return dbFormat.parse(this).time
    }

    private val dbFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val appFormat = SimpleDateFormat("dd MMM yyyy")

    companion object {

        @Volatile
        private var INSTANCE: AnnualVisitationSingleton? = null

        fun getInstance(): AnnualVisitationSingleton =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: AnnualVisitationSingleton().also { INSTANCE = it }
                }
    }



}