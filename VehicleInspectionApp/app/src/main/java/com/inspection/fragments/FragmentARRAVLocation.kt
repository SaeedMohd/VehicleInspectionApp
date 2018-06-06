package com.inspection.fragments


import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.inspection.R
import com.inspection.Utils.toast
import com.inspection.model.AAALocations
import com.inspection.model.FacilityDataModel
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.fragment_aarav_location.*
import kotlinx.android.synthetic.main.fragment_arravlocation.*
import java.util.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentARRAVLocation.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVLocation : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var facLocationsList = ArrayList<AAALocations>()
    private var facLocationsArray = ArrayList<String>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_aarav_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alphaBackgroundForDialogs.setOnClickListener({
            addNewLocationDialog.visibility = View.GONE
            addNewPhoneDialog.visibility = View.GONE
            addNewEmailDialog.visibility = View.GONE
            alphaBackgroundForDialogs.visibility = View.GONE
        })

        addNewLocationButton.setOnClickListener({
            showLocationDialog()
        })

        addNewPhoneButton.setOnClickListener({
            showPhoneDialog()
        })

        addNewEmailButton.setOnClickListener {
            showEmailDialog()
        }

        setLocations()
    }

    private var locationTypeList = ArrayList<TypeTablesModel.locationType>()
    private var locationypeArray = ArrayList<String>()

    private var phoneTypeList = ArrayList<TypeTablesModel.locationPhoneType>()
    private var phoneTypeArray = ArrayList<String>()

    private var availabilityTypeList = ArrayList<TypeTablesModel.facilityAvailabilityType>()
    private var availabilityTypeArray = ArrayList<String>()



    private fun showLocationDialog() {
        alphaBackgroundForDialogs.visibility = View.VISIBLE
        addNewLocationDialog.visibility = View.VISIBLE
        locationTypeList = TypeTablesModel.getInstance().LocationType
        locationypeArray.clear()
        for (fac in locationTypeList) {
            locationypeArray.add(fac.LocTypeName)
        }

        var locTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, locationypeArray)
        locTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newLocTypeSpinner.adapter = locTypeAdapter
//        locationDialogView.newLoc2TypeSpinner.adapter = locTypeAdapter


    }

    private fun showPhoneDialog() {
        alphaBackgroundForDialogs.visibility = View.VISIBLE
        addNewPhoneDialog.visibility = View.VISIBLE
        phoneTypeList = TypeTablesModel.getInstance().LocationPhoneType
        phoneTypeArray.clear()
        for (fac in phoneTypeList) {
            phoneTypeArray.add(fac.LocPhoneName)
        }

        var phoneTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, phoneTypeArray)
        phoneTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newPhoneTypeSpinner.adapter = phoneTypeAdapter
    }

    private fun showEmailDialog() {
        alphaBackgroundForDialogs.visibility = View.VISIBLE
        addNewEmailDialog.visibility = View.VISIBLE
    }

    fun prepareLocationPage(){
        setLocations()
    }

    private fun setLocations() {
//        for (fac in FacilityDataModel.getInstance().tblAddress) {
//            if (fac.LocationTypeID.toInt() == 1) {
//                phyloc1addr1branchname.text = if (fac.BranchName.isNullOrEmpty()) "" else fac.BranchName
//                phyloc1addr1branchno.text = if (fac.BranchNumber.isNullOrEmpty()) "" else fac.BranchNumber
//                phyloc1addr1latitude.setText(if (fac.LATITUDE.isNullOrEmpty()) "" else fac.LATITUDE)
//                phyloc1addr1longitude.setText(if (fac.LONGITUDE.isNullOrEmpty()) "" else fac.LONGITUDE)
//                phylocAddr1address.text = if (fac.FAC_Addr1.isNullOrEmpty()) "" else fac.FAC_Addr1
//                phylocAddr2address.text = if (fac.FAC_Addr2.isNullOrEmpty()) "" else fac.FAC_Addr2
//            } else if (fac.LocationTypeID.toInt() == 2) {
//                mailaddr1branchname.text = if (fac.BranchName.isNullOrEmpty()) "" else fac.BranchName
//                mailaddr1branchname.text = if ((fac.BranchName.isNullOrEmpty())) "" else fac.BranchName
//                mailaddr1branchno.text = if (fac.BranchNumber.isNullOrEmpty()) "" else fac.BranchNumber
//                mailAddr1address.text = if (fac.FAC_Addr1.isNullOrEmpty()) "" else fac.FAC_Addr1
//                mailAddr2address.text = if (fac.FAC_Addr2.isNullOrEmpty()) "" else fac.FAC_Addr2
//            } else if (fac.LocationTypeID.toInt() == 3) {
//                billaddr1branchname.text = if (fac.BranchName.isNullOrEmpty()) "" else fac.BranchName
//                billaddr1branchno.text = if (fac.BranchNumber.isNullOrEmpty()) "" else fac.BranchNumber
//                billAddr1address.text = if (fac.FAC_Addr1.isNullOrEmpty()) "" else fac.FAC_Addr1
//                billAddr2address.text = if (fac.FAC_Addr2.isNullOrEmpty()) "" else fac.FAC_Addr2
//            }
//        }
//        progressbarLocation.visibility = View.INVISIBLE
    }

    fun validateInputs(): Boolean {
        var isInputsValid = true

        phyloc1addr1latitude.setError(null)
//        phyloc1addr2latitude.setError(null)
//        phyloc1addr2longitude.setError(null)
        phyloc1addr1longitude.setError(null)

        if (phyloc1addr1latitude.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            phyloc1addr1latitude.setError("Required Field")
        }

//        if(loc1addr2latitude.text.toString().isNullOrEmpty()) {
//            isInputsValid=false
//            loc1addr2latitude.setError("Required Field")
//        }

        if (phyloc1addr1longitude.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            phyloc1addr1longitude.setError("Required Field")
        }

//        if(loc1addr2longitude.text.toString().isNullOrEmpty()) {
//            isInputsValid=false
//            loc1addr2longitude.setError("Required Field")
//        }


        return isInputsValid
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val isValidating = "param1"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentARRAVLocation.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(isValidating: Boolean): FragmentARRAVLocation {
            val fragment = FragmentARRAVLocation()
            val args = Bundle()
            args.putBoolean(this.isValidating, isValidating)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
