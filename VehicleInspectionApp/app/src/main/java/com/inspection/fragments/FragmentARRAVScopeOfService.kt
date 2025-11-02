package com.inspection.fragments

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.transition.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.view.get
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.inspection.R
//import kotlinx.android.synthetic.main.fragment_arrav_scope_of_service.*
import org.json.JSONException
import org.json.JSONObject
import com.google.gson.GsonBuilder
import com.inspection.FormsActivity
import com.inspection.MainActivity
import com.inspection.MainActivity.Companion.activity
import com.inspection.MainActivity.Companion.handler
import com.inspection.R.id.numberOfLiftsEditText
import com.inspection.Utils.*
import com.inspection.Utils.Constants.UpdateScopeofServiceData
import com.inspection.databinding.FragmentArravScopeOfServiceBinding
import com.inspection.databinding.FragmentArrayVehicleServicesBinding
import com.inspection.model.*
//import kotlinx.android.synthetic.main.scope_of_service_group_layout.*
import java.lang.Exception
import java.util.*
import kotlin.jvm.java
import kotlin.properties.Delegates


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVScopeOfService.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVScopeOfService.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVScopeOfService : Fragment() {
    private var _binding: FragmentArravScopeOfServiceBinding? = null
    private val binding get() = _binding!!
    var warrantyArray = ArrayList<String>()
    var discountPercentageArray = ArrayList<String>()
//    var discountPercentArray = ArrayList<String>()

    var fillMethodCalled = false
    var temp_warranty = ""
    var temp_fixedLaborRate = ""
    var temp_diagnosticLaborRate = ""
    var temp_laborRateMatrixMax = ""
    var temp_laborRateMatrixMin = ""
    var temp_numberOfBaysEditText_ = ""
    var temp_numberOfLiftsEditText_ = ""
    var testString=""
    var prevDiscountPercentage = ""
    var prevMaxDiscountAmount = ""
    var isFirstLoading = true


    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arrav_scope_of_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentArravScopeOfServiceBinding.bind(view)
        implementOnAnyFragment=false
        validationProblemFoundForOtherFragments=false
        binding.cancelButton.setOnClickListener {
            binding.cancelButton.hideKeyboard()
            FacilityDataModel.getInstance().tblScopeofService[0].DiagnosticsRate= temp_diagnosticLaborRate
            FacilityDataModel.getInstance().tblScopeofService[0].FixedLaborRate= temp_fixedLaborRate
            FacilityDataModel.getInstance().tblScopeofService[0].LaborMax= temp_laborRateMatrixMax
            FacilityDataModel.getInstance().tblScopeofService[0].LaborMin= temp_laborRateMatrixMin
            FacilityDataModel.getInstance().tblScopeofService[0].NumOfBays= temp_numberOfBaysEditText_
            FacilityDataModel.getInstance().tblScopeofService[0].NumOfLifts= temp_numberOfLiftsEditText_
            FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID= temp_warranty
            FacilityDataModel.getInstance().tblScopeofService[0].DiscountCap=prevMaxDiscountAmount
            FacilityDataModel.getInstance().tblScopeofService[0].DiscountAmount=prevDiscountPercentage.replace("%","")
//            PRGDataModel.getInstance().tblPRGRepairDiscountFactors[0].discountpercentage = prevDiscountPercentage.replace("%","")
//            PRGDataModel.getInstance().tblPRGRepairDiscountFactors[0].maxdiscountamount = prevMaxDiscountAmount
            setFields()
            (activity as FormsActivity).saveRequired = false
            refreshButtonsState()
//            Utility.showMessageDialog(activity,"Confirmation ...","Changes cancelled succesfully ---")
            Utility.showUnifiedConfirmationDialog(activity,"Changes cancelled successfully")
        }

        for (typeWarranty in TypeTablesModel.getInstance().WarrantyPeriodType){
                warrantyArray.add(typeWarranty.WarrantyTypeName)
        }
        var warrantyAdapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, warrantyArray)
        warrantyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.warrantyPeriodVal.adapter = warrantyAdapter


        for (discountPercentage in TypeTablesModel.getInstance().DiscountAmountType){
            discountPercentageArray.add(discountPercentage.TypeName)
        }

        var discountPercentageAdapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, discountPercentageArray )
        discountPercentageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.disountpercentageDropListId.adapter = discountPercentageAdapter

        saveBtnPressed()
        setFields()
        handleRadioButtonsSelection()
        setFieldsListener()

        binding.laborRateRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            handleRadioButtonsSelection()
        }

        IndicatorsDataModel.getInstance().tblScopeOfServices[0].GeneralInfoVisited= true
        // SAEED TO BE REVIEWED
        requireActivity().findViewById<Button>(R.id.sosgeneralInformationButton).setTextColor(Color.parseColor("#26C3AA"))
//        (activity as FormsActivity).sosgeneralInformationButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
        (activity as FormsActivity).saveRequired = false
        refreshButtonsState()
//        val timer = Timer()
//        val timerTask = object : TimerTask() {
//            override fun run() {
//                handler.post {
//                    isFirstLoading = false
//                    refreshButtonsState()
//                }
//            }
//        }
//        timer.schedule(timerTask, 0, 1000)
            isFirstLoading = false
    }

    fun refreshButtonsState(){
        if (isFirstLoading) (activity as FormsActivity).saveRequired = false
        binding.saveBtnId.isEnabled = (activity as FormsActivity).saveRequired
        binding.cancelButton.isEnabled = (activity as FormsActivity).saveRequired
    }

    fun setFieldsListener (){
        if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
            FacilityDataModel.getInstance().tblScopeofService[0].apply {
                var laborMaxWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                FacilityDataModel.getInstance().tblScopeofService[0].LaborMax = s.toString()
                HasChangedModel.getInstance().checkIfChangeWasDoneforSoSGeneral()
                HasChangedModel.getInstance().changeDoneForSoSGeneral()
                if (!isFirstLoading) (activity as FormsActivity).saveRequired = true
                refreshButtonsState()
            }

            override fun afterTextChanged(s: Editable) {
                watcher_LaborMax=s.toString()
                if (FacilityDataModelOrg.getInstance().tblScopeofService[0].LaborMax!=watcher_LaborMax){

                }
            }
        }
        var laborMinWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                FacilityDataModel.getInstance().tblScopeofService[0].LaborMin = s.toString()
                HasChangedModel.getInstance().checkIfChangeWasDoneforSoSGeneral()
                HasChangedModel.getInstance().changeDoneForSoSGeneral()
                if (!isFirstLoading) (activity as FormsActivity).saveRequired = true
                refreshButtonsState()
            }

            override fun afterTextChanged(s: Editable) {

                watcher_LaborMin=s.toString()


                if (FacilityDataModelOrg.getInstance().tblScopeofService[0].LaborMin!=watcher_LaborMin){


                }

            }
        }
        var fixedLaborWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                FacilityDataModel.getInstance().tblScopeofService[0].FixedLaborRate = s.toString()
                HasChangedModel.getInstance().checkIfChangeWasDoneforSoSGeneral()
                HasChangedModel.getInstance().changeDoneForSoSGeneral()
                if (!isFirstLoading) (activity as FormsActivity).saveRequired = true
                refreshButtonsState()
            }

            override fun afterTextChanged(s: Editable) {

                watcher_FixedLaborRate=s.toString()
                testString=s.toString()

                if (FacilityDataModelOrg.getInstance().tblScopeofService[0].FixedLaborRate!=watcher_FixedLaborRate){


                }
            }
        }
        var diagnosticWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                FacilityDataModel.getInstance().tblScopeofService[0].DiagnosticsRate = s.toString()
                HasChangedModel.getInstance().checkIfChangeWasDoneforSoSGeneral()
                HasChangedModel.getInstance().changeDoneForSoSGeneral()
                if (!isFirstLoading) (activity as FormsActivity).saveRequired = true
                refreshButtonsState()
            }

            override fun afterTextChanged(s: Editable) {
                watcher_DiagnosticsRate=s.toString()
                if (FacilityDataModelOrg.getInstance().tblScopeofService[0].DiagnosticsRate!=watcher_DiagnosticsRate){
                }
            }
        }
        var noOfBaysWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                FacilityDataModel.getInstance().tblScopeofService[0].NumOfBays = s.toString()
                HasChangedModel.getInstance().checkIfChangeWasDoneforSoSGeneral()
                HasChangedModel.getInstance().changeDoneForSoSGeneral()
                if (!isFirstLoading) (activity as FormsActivity).saveRequired = true
                refreshButtonsState()
            }

            override fun afterTextChanged(s: Editable) {

                watcher_NumOfBays=s.toString()

                if (FacilityDataModelOrg.getInstance().tblScopeofService[0].NumOfBays!=watcher_NumOfBays){


                }
            }
        }



//                var discountPercentagerWatcher = object : TextWatcher {
//                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//                    }
//
//                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                        PRGDataModel.getInstance().tblPRGRepairDiscountFactors[0].discountpercentage = s.toString().replace("%", "")
//                        if (!prevDiscountPercentage.equals(s.toString())) (activity as FormsActivity).saveRequired = true
//                        refreshButtonsState()
//                    }
//
//                    override fun afterTextChanged(s: Editable) {
//                    }
//                }


                var maxDiscountAmountWatcher = object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    }
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                        PRGDataModel.getInstance().tblPRGRepairDiscountFactors[0].maxdiscountamount = s.toString()
//                        if (!prevMaxDiscountAmount.equals(s.toString())) {
//                            (activity as FormsActivity).saveRequired = true
//                            HasChangedModel.getInstance().groupSoSGeneralInfo[0].SoSDiscAmount = true
//                            HasChangedModel.getInstance().changeDoneForSoSGeneral()
//                        }
//                        refreshButtonsState()
                        FacilityDataModel.getInstance().tblScopeofService[0].DiscountCap = s.toString()
                        HasChangedModel.getInstance().checkIfChangeWasDoneforSoSGeneral()
                        HasChangedModel.getInstance().changeDoneForSoSGeneral()
                        if (!isFirstLoading) (activity as FormsActivity).saveRequired = true
                        refreshButtonsState()
                    }
                    override fun afterTextChanged(s: Editable) {
                    }
                }

        var noOfLiftsWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                FacilityDataModel.getInstance().tblScopeofService[0].NumOfLifts = s.toString()
                HasChangedModel.getInstance().checkIfChangeWasDoneforSoSGeneral()
                HasChangedModel.getInstance().changeDoneForSoSGeneral()
                if (!isFirstLoading) (activity as FormsActivity).saveRequired = true
                refreshButtonsState()
            }

            override fun afterTextChanged(s: Editable) {
                watcher_NumOfLifts=s.toString()
                if (FacilityDataModelOrg.getInstance().tblScopeofService[0].NumOfLifts!=watcher_NumOfLifts){
                }
            }
        }

                binding.laborRateMatrixMaxEditText.addTextChangedListener(laborMaxWatcher)
                binding.laborRateMatrixMinEditText.addTextChangedListener(laborMinWatcher)
                binding.fixedLaborRateEditText.addTextChangedListener(fixedLaborWatcher)
                binding.diagnosticRateEditText.addTextChangedListener(diagnosticWatcher)
                binding.numberOfBaysEditText.addTextChangedListener(noOfBaysWatcher)
                binding.numberOfLiftsEditText.addTextChangedListener(noOfLiftsWatcher)
                binding.maxdDiscountAmountEditText.addTextChangedListener(maxDiscountAmountWatcher)

            }
        }

        binding.warrantyPeriodVal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!binding.warrantyPeriodVal.tag.equals(position) || binding.warrantyPeriodVal.tag.equals("-1")) {
                    binding.warrantyPeriodVal.tag = "-1"
                    FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID = warrantyArray[position]
                    HasChangedModel.getInstance().checkIfChangeWasDoneforSoSGeneral()
                    HasChangedModel.getInstance().changeDoneForSoSGeneral()
                    if (!isFirstLoading) (activity as FormsActivity).saveRequired = true
                    refreshButtonsState()
                }
            }
        }

        binding.disountpercentageDropListId.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                PRGDataModel.getInstance().tblPRGRepairDiscountFactors[0].discountpercentage = disountpercentageDropListId.getItemAtPosition(position).toString().replace("%","")
//                if (!disountpercentageDropListId.getItemAtPosition(position).toString().equals(prevDiscountPercentage+"%")) {
//                        HasChangedModel.getInstance().groupSoSGeneralInfo[0].SoSDiscPercentage = true
//                        (activity as FormsActivity).saveRequired = true
//                }
//                    refreshButtonsState()
                    if (!binding.disountpercentageDropListId.tag.equals(position) || binding.disountpercentageDropListId.tag.equals("-1")) {
                        binding.disountpercentageDropListId.tag = "-1"
                    FacilityDataModel.getInstance().tblScopeofService[0].DiscountAmount = binding.disountpercentageDropListId.selectedItem.toString().replace("%","")
                    HasChangedModel.getInstance().checkIfChangeWasDoneforSoSGeneral()
                    HasChangedModel.getInstance().changeDoneForSoSGeneral()
                        if (!isFirstLoading) (activity as FormsActivity).saveRequired = true
                    refreshButtonsState()
                }
            }
        }

        (activity as FormsActivity).saveRequired = false
        refreshButtonsState()

    }


    fun setFields() {
        if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
            FacilityDataModel.getInstance().tblScopeofService[0].apply {
                binding.fixedLaborRateEditText.setText(if (FixedLaborRate.isNullOrEmpty()) "0" else FixedLaborRate)
                binding.diagnosticRateEditText.setText(if (DiagnosticsRate.isNullOrEmpty()) "0" else DiagnosticsRate)
                binding.numberOfBaysEditText.setText(if (NumOfBays.isNullOrEmpty()) "0" else NumOfBays)
                binding.numberOfLiftsEditText.setText(if (NumOfLifts.isNullOrEmpty()) "0" else NumOfLifts)
                binding.laborRateMatrixMaxEditText.setText(if (LaborMax.isNullOrEmpty()) "0" else LaborMax)
                binding.laborRateMatrixMinEditText.setText(if (LaborMin.isNullOrBlank()) "0" else LaborMin)
                binding.maxdDiscountAmountEditText.setText(DiscountCap)
                temp_fixedLaborRate = binding.fixedLaborRateEditText.text.toString()
                temp_diagnosticLaborRate  = binding.diagnosticRateEditText.text.toString()
                temp_numberOfBaysEditText_ = binding.numberOfBaysEditText.text.toString()
                temp_numberOfLiftsEditText_ = binding.numberOfLiftsEditText.text.toString()
                temp_laborRateMatrixMax = binding.laborRateMatrixMaxEditText.text.toString()
                temp_laborRateMatrixMin = binding.laborRateMatrixMinEditText.text.toString()
                prevDiscountPercentage = DiscountAmount
                prevMaxDiscountAmount = DiscountCap
                binding.disountpercentageDropListId.setSelection(0)
                if (discountPercentageArray.indexOf(DiscountAmount+'%') > -1) {
                    binding.disountpercentageDropListId.setSelection(
                        discountPercentageArray.indexOf(
                            DiscountAmount + '%'
                        )
                    )
                    binding.disountpercentageDropListId.tag =
                        binding.disountpercentageDropListId.selectedItemPosition
                } else {
                    binding.disountpercentageDropListId.setSelection(0)
                    binding.disountpercentageDropListId.tag = binding.disountpercentageDropListId.selectedItemPosition
                }
                binding.warrantyPeriodVal.tag=0
//                binding.warrantyPeriodVal.setSelection(0)
                for (typeWarranty in TypeTablesModel.getInstance().WarrantyPeriodType) {
                    for (facWarranty in FacilityDataModel.getInstance().tblScopeofService) {
                        if (facWarranty.WarrantyTypeID == typeWarranty.WarrantyTypeID) {
                            temp_warranty = facWarranty.WarrantyTypeID
                            for (warSpinner in warrantyArray) {
                                if (typeWarranty.WarrantyTypeName == warSpinner) {
                                    var i = warrantyArray.indexOf(warSpinner)
                                    binding.warrantyPeriodVal.setSelection(i)
                                    binding.warrantyPeriodVal.tag=i
                                }
                            }
                        } else { // This reset the spinner to the first item if the warranty type is not found
//                            binding.warrantyPeriodVal.setSelection(0)
//                            binding.warrantyPeriodVal.tag=0
                        }
                    }
                }

            }
        }
//        if (PRGDataModel.getInstance().tblPRGRepairDiscountFactors.size > 0) {
//            maxdDiscountAmountEditText.setText(PRGDataModel.getInstance().tblPRGRepairDiscountFactors[0].maxdiscountamount)
//            disountpercentageDropListId.setSelection(0)
//            if (PRGDataModel.getInstance().tblPRGRepairDiscountFactors[0].discountpercentage.toString().isNotEmpty())
//                disountpercentageDropListId.setSelection((resources.getStringArray(R.array.discount_percentage)).indexOf(PRGDataModel.getInstance().tblPRGRepairDiscountFactors[0].discountpercentage+"%"))
//            prevDiscountPercentage = PRGDataModel.getInstance().tblPRGRepairDiscountFactors[0].discountpercentage+"%"
//            prevMaxDiscountAmount = PRGDataModel.getInstance().tblPRGRepairDiscountFactors[0].maxdiscountamount
//        }
        (activity as FormsActivity).saveRequired = false
        refreshButtonsState()
    }

    fun handleRadioButtonsSelection (){

        if (binding.fixedLaborRadioButton.isChecked) {
            binding.fixedLaborRateTextView.visibility = View.VISIBLE
            binding.fixedLaborRateEditText.visibility = View.VISIBLE
        } else {
            binding.fixedLaborRateTextView.visibility = View.GONE
            binding.fixedLaborRateEditText.visibility = View.GONE
        }
        binding.laborRateLL.isVisible = !binding.fixedLaborRadioButton.isChecked
        binding.laborRateView.isVisible = !binding.fixedLaborRadioButton.isChecked
    }



    var isFirstRun = true

    fun getSoSChanges() : String {
        var strChanges =""
        val fixedLaborRate = binding.fixedLaborRateEditText.text.toString()
        val diagnosticLaborRate = binding.diagnosticRateEditText.text.toString()
        val laborRateMatrixMax = binding.laborRateMatrixMaxEditText.text.toString()
        val laborRateMatrixMin = binding.laborRateMatrixMinEditText.text.toString()
        val numberOfBays = binding.numberOfBaysEditText.text.toString()
        val numberOfLifts = binding.numberOfLiftsEditText.text.toString()

        try {
            if (fixedLaborRate != FacilityDataModelOrg.getInstance().tblScopeofService[0].FixedLaborRate) {
                strChanges += "Fixed Labor Rate changed from (" + FacilityDataModelOrg.getInstance().tblScopeofService[0].FixedLaborRate + ") to (" + fixedLaborRate + ") - "
            }
            if (diagnosticLaborRate != FacilityDataModelOrg.getInstance().tblScopeofService[0].DiagnosticsRate) {
                strChanges += "Diagnostic Labor Rate changed from (" + FacilityDataModelOrg.getInstance().tblScopeofService[0].DiagnosticsRate + ") to (" + diagnosticLaborRate + ") - "
            }
            if (laborRateMatrixMax != FacilityDataModelOrg.getInstance().tblScopeofService[0].LaborMax) {
                strChanges += "Labor Rate Max ($) changed from (" + FacilityDataModelOrg.getInstance().tblScopeofService[0].LaborMax + ") to (" + laborRateMatrixMax + ") - "
            }
            if (laborRateMatrixMin != FacilityDataModelOrg.getInstance().tblScopeofService[0].LaborMin) {
                strChanges += "Labor Rate Min ($) changed from (" + FacilityDataModelOrg.getInstance().tblScopeofService[0].LaborMin + ") to (" + laborRateMatrixMin + ") - "
            }
            if (numberOfBays != FacilityDataModelOrg.getInstance().tblScopeofService[0].NumOfBays) {
                strChanges += "Number of Bays changed from (" + FacilityDataModelOrg.getInstance().tblScopeofService[0].NumOfBays + ") to (" + numberOfBays + ") - "
            }
            if (numberOfLifts != FacilityDataModelOrg.getInstance().tblScopeofService[0].NumOfLifts) {
                strChanges += "Number of Lifts changed from (" + FacilityDataModelOrg.getInstance().tblScopeofService[0].NumOfLifts + ") to (" + numberOfLifts + ") - "
            }
            if (binding.warrantyPeriodVal.getSelectedItem().toString() != (TypeTablesModel.getInstance().WarrantyPeriodType.filter { s -> s.WarrantyTypeID.equals(FacilityDataModelOrg.getInstance().tblScopeofService[0].WarrantyTypeID) }[0].WarrantyTypeName)) {
                strChanges += "Warranty Period changed from (" + TypeTablesModel.getInstance().WarrantyPeriodType.filter { s -> s.WarrantyTypeID.equals(FacilityDataModelOrg.getInstance().tblScopeofService[0].WarrantyTypeID) }[0].WarrantyTypeName + ") to (" + binding.warrantyPeriodVal.getSelectedItem().toString() + ") - "
            }
            strChanges = strChanges.removeSuffix(" - ")
            return strChanges
        }
        catch (e: Exception) {
            return strChanges
        }
        return strChanges
    }





    fun saveBtnPressed() {
        binding.saveBtnId.setOnClickListener {
            if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                if (validateInputs()) {
                    var fixedLaborRate = binding.fixedLaborRateEditText.text.toString()
                    var diagnosticLaborRate = binding.diagnosticRateEditText.text.toString()
                    var laborRateMatrixMax = binding.laborRateMatrixMaxEditText.text.toString()
                    var laborRateMatrixMin = binding.laborRateMatrixMinEditText.text.toString()
                    var numberOfBaysEditText = binding.numberOfBaysEditText.text.toString()
                    var numberOfLiftsEditText = binding.numberOfLiftsEditText.text.toString()
                    var warrantyTypeId = TypeTablesModel.getInstance().WarrantyPeriodType.filter { s->s.WarrantyTypeName.equals(binding.warrantyPeriodVal.selectedItem.toString()) }[0].WarrantyTypeID
                    var discountPercentage = binding.disountpercentageDropListId.selectedItem.toString().replace("%","")
                    var maxdiscountamount = binding.maxdDiscountAmountEditText.text.toString()

                    binding.progressBarText.text = "Saving ..."
                    binding.scopeOfServiceGeneralInfoLoadingView.visibility = View.VISIBLE
                    Log.v("SOS GENERAL --- ",UpdateScopeofServiceData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode="+FacilityDataModel.getInstance().clubCode+"&laborRateId=1&fixedLaborRate=$fixedLaborRate&laborMin=$laborRateMatrixMin&laborMax=$laborRateMatrixMax&diagnosticRate=$diagnosticLaborRate&numOfBays=$numberOfBaysEditText&numOfLifts=$numberOfLiftsEditText&warrantyTypeId=${warrantyTypeId}&active=1&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+ Date().toApiSubmitFormat()+"&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate="+Date().toApiSubmitFormat()+"&discountPercentage=${discountPercentage}&maxDiscountAmount=${maxdiscountamount}")
                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateScopeofServiceData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode="+FacilityDataModel.getInstance().clubCode+"&laborRateId=1&fixedLaborRate=$fixedLaborRate&laborMin=$laborRateMatrixMin&laborMax=$laborRateMatrixMax&diagnosticRate=$diagnosticLaborRate&numOfBays=$numberOfBaysEditText&numOfLifts=$numberOfLiftsEditText&warrantyTypeId=${warrantyTypeId}&active=1&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+ Date().toApiSubmitFormat()+"&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate="+Date().toApiSubmitFormat() + "&discountPercentage=${discountPercentage}&maxDiscountAmount=${maxdiscountamount}" + Utility.getLoggingParameters(activity, 1, getSoSChanges()),
                        { response ->
                            requireActivity().runOnUiThread {
                                if (response.toString().contains("returnCode>0<",false)) {
                                    HasChangedModel.getInstance().updateChangedData("Scope Of Services","General","",getSoSChanges())
                                    binding.scopeOfServiceGeneralInfoLoadingView.visibility = View.GONE
                                    binding.progressBarText.text = "Loading ..."
                                    Utility.showSubmitAlertDialog(activity, true, "Scope of Services General Information")
                                    (activity as FormsActivity).saveDone = true
//                                    PRGDataModel.getInstance().tblPRGRepairDiscountFactors[0].discountpercentage=discountPercentage
//                                    PRGDataModel.getInstance().tblPRGRepairDiscountFactors[0].maxdiscountamount=maxdiscountamount
                                    if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
                                        FacilityDataModel.getInstance().tblScopeofService[0].apply {
                                            DiscountCap = maxdiscountamount
                                            DiscountAmount = discountPercentage
                                            LaborMax = if (watcher_LaborMax.isNullOrBlank()) LaborMax else watcher_LaborMax
                                            LaborMin = if (watcher_LaborMin.isNullOrBlank()) LaborMin else watcher_LaborMin
                                            FixedLaborRate = if (watcher_FixedLaborRate.isNullOrBlank()) FixedLaborRate else watcher_FixedLaborRate
                                            DiagnosticsRate = if (watcher_DiagnosticsRate.isNullOrBlank()) DiagnosticsRate else watcher_DiagnosticsRate
                                            NumOfBays = if (watcher_NumOfBays.isNullOrBlank()) NumOfBays else watcher_NumOfBays
                                            NumOfLifts = if (watcher_NumOfLifts.isNullOrBlank()) NumOfLifts else watcher_NumOfLifts
                                            for (typeWarranty in TypeTablesModel.getInstance().WarrantyPeriodType) {
                                                if (typeWarranty.WarrantyTypeName == binding.warrantyPeriodVal.selectedItem) {
                                                    FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID = typeWarranty.WarrantyTypeID
                                                }
                                            }

                                        }
                                    }
                                    (activity as FormsActivity).saveRequired = false
                                    refreshButtonsState()
                                    setFields()
                                    HasChangedModel.getInstance().checkIfChangeWasDoneforSoSGeneral()
                                    HasChangedModel.getInstance().changeDoneForSoSGeneral()
                                } else {
                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                                    Utility.showSubmitAlertDialog(activity, false, "Scope of Services General Information (Error: "+ errorMessage+" )")
                                    binding.scopeOfServiceGeneralInfoLoadingView.visibility = View.GONE
                                    binding.progressBarText.text = "Loading ..."
                                }
                            }
                        },
                        {
                        binding.scopeOfServiceGeneralInfoLoadingView.visibility = View.GONE
                        binding.progressBarText.text = "Loading ..."
                    Utility.showSubmitAlertDialog(activity,false,"Scope of Services General Information (Error: "+it.message+" )")
                }))
                } else {
                    Utility.showValidationAlertDialog(activity,validateMsg)
                }
            } else {
                Utility.showInternetWarningDialog(requireContext(),(requireActivity() as FormsActivity).networkStatusErrorMsg)
            }
        }
    }

    fun validateInputs(): Boolean {
        validateMsg = ""
        scopeOfServiceValide = true
        binding.fixedLaborRateEditText.setError(null)
        binding.diagnosticRateEditText.setError(null)
        binding.laborRateMatrixMaxEditText.setError(null)
        binding.laborRateMatrixMinEditText.setError(null)
        if (binding.fixedLaborRateEditText.text.toString().isNullOrEmpty()) {
            scopeOfServiceValide = false
            binding.fixedLaborRateEditText.setError("Required Field")
            if (validateMsg.equals("")) validateMsg = "Please fill all the required fields"
        }

        if (binding.diagnosticRateEditText.text.toString().isNullOrEmpty()) {
            scopeOfServiceValide = false
            binding.diagnosticRateEditText.setError("Required Field")
            if (validateMsg.equals("")) validateMsg = "- Please fill all the required fields"
        }


        if (binding.laborRateMatrixMaxEditText.text.toString().isNullOrEmpty()) {
            scopeOfServiceValide = false
            binding.laborRateMatrixMaxEditText.setError("Required Field")
            if (validateMsg.equals("")) validateMsg = "- Please fill all the required fields"
        }

        if (binding.laborRateMatrixMinEditText.text.toString().isNullOrEmpty()) {
            scopeOfServiceValide = false
            binding.laborRateMatrixMinEditText.setError("Required Field")
            if (validateMsg.equals("")) validateMsg = "- Please fill all the required fields"
        }

        if (binding.maxdDiscountAmountEditText.text.toString().isNullOrEmpty()) {
            scopeOfServiceValide = false
            binding.maxdDiscountAmountEditText.setError("Required Field")
            if (validateMsg.equals("")) validateMsg = "- Please fill all the required fields"
        }

        if (!binding.laborRateMatrixMinEditText.text.toString().isNullOrEmpty() && !binding.laborRateMatrixMaxEditText.text.toString().isNullOrEmpty()) {
            var minRate =  binding.laborRateMatrixMinEditText.text.toString().toDouble()
            var maxRate =  binding.laborRateMatrixMaxEditText.text.toString().toDouble()
            if (minRate>maxRate) {
                scopeOfServiceValide = false
                binding.laborRateMatrixMinEditText.setError("Min Labor Rate should be less than Max Labor Rate")
                if (validateMsg.equals(""))
                    validateMsg = "- Min Labor Rate should be less than Max Labor Rate"
                else
                    validateMsg += "\n\n- Min Labor Rate should be less than Max Labor Rate"
            }
        }

        return scopeOfServiceValide

    }



//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//
//    }



    override fun onPause() {
        super.onPause()

    }

    override fun onStop() {


        super.onStop()
    }

    override fun onDestroyView() {

        super.onDestroyView()
    }


    override fun onDetach() {


        mListener = null
        super.onDetach()



    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }


    override fun onDestroy() {
     //   Toast.makeText(context!!,"destroy",Toast.LENGTH_SHORT).show()

        super.onDestroy()


    }

    companion object {

        var implementOnAnyFragment=false
        var fixedLaborRate = ""
        var diagnosticLaborRate = ""
        var laborRateMatrixMax = ""
        var laborRateMatrixMin = ""
        var numberOfBaysEditText_ = ""
        var numberOfLiftsEditText_ = ""
        var dataChanged=false
        var validationProblemFoundForOtherFragments=false

        var watcher_LaborMax=""
        var watcher_LaborMin=""
        var watcher_FixedLaborRate=""
        var watcher_DiagnosticsRate=""
        var watcher_NumOfBays=""
        var watcher_NumOfLifts=""
        var typeIdCompare=""

        var scopeOfServiceValide = TblScopeofService().isInputsValid
        var validateMsg = ""
        var scopeOfServiceValideForOtherFragmentToTest = false


        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"


        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FacilityGeneralInformationFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FragmentARRAVScopeOfService {
            val fragment = FragmentARRAVScopeOfService()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
