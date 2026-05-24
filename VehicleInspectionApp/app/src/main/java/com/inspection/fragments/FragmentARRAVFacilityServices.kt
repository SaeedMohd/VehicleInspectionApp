package com.inspection.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.FormsActivity
import com.inspection.R
import com.inspection.Utils.*
import com.inspection.Utils.Constants.UpdateFacilityServicesData
import com.inspection.Utils.Utility.showValidationAlertDialog
import com.inspection.databinding.FragmentAaravLocationBinding
import com.inspection.databinding.FragmentArravFacilityServicesBinding
import com.inspection.model.*
//import kotlinx.android.synthetic.main.fragment_arrav_affliations.*
//import kotlinx.android.synthetic.main.fragment_arrav_facility_services.*
//import kotlinx.android.synthetic.main.fragment_arrav_facility_services.progressBarText
//import kotlinx.android.synthetic.main.scope_of_service_group_layout.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
import java.util.Locale.getDefault

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVFacilityServices.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVFacilityServices.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVFacilityServices : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null

    var servicesArray= ArrayList<String>()
    private var _binding: FragmentArravFacilityServicesBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arrav_facility_services, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentArravFacilityServicesBinding.bind(view)
        IndicatorsDataModel.getInstance().tblScopeOfServices[0].FacilityServicesVisited= true
//        (activity as FormsActivity).facilityServicesButton.setTextColor(Color.parseColor("#26C3AA"))
        (requireActivity().supportFragmentManager.findFragmentById(R.id.fragment) as? HasTabIndicators)?.refreshTabIndicators()
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()

        binding.exitFCServicesDialogeBtnId.setOnClickListener {
            binding.facilityServicesCard.visibility=View.GONE
            (activity as FormsActivity).overrideBackButton = false
            binding.alphaBackgroundForFCServicesDialogs.visibility = View.GONE
        }

        binding.editExitFCServicesDialogeBtnId.setOnClickListener {
            binding.editFacilityServicesCard.visibility=View.GONE
            (activity as FormsActivity).overrideBackButton = false
            binding.alphaBackgroundForFCServicesDialogs.visibility = View.GONE
        }

        binding.showNewserviceDialogueButton.setOnClickListener {
            binding.commentsEditTextVal.setText("")
            binding.fceffectiveDateTextviewVal.setText("SELECT DATE")
            binding.fcexpirationDateTextviewVal.setText("SELECT DATE")
            binding.fcServicesTextviewVal.setSelection(0)
            binding.commentsEditTextVal.setError(null)
            binding.fceffectiveDateTextviewVal.setError(null)
            (activity as FormsActivity).overrideBackButton = true
            binding.facilityServicesCard.visibility=View.VISIBLE
            binding.alphaBackgroundForFCServicesDialogs.visibility = View.VISIBLE
        }

        binding.fcexpirationDateTextviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.fcexpirationDateTextviewVal.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.fcexpirationDateTextviewVal.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(),R.style.CustomDatePickerDialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                binding.fcexpirationDateTextviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        binding.fceffectiveDateTextviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.fceffectiveDateTextviewVal.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.fceffectiveDateTextviewVal.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(),R.style.CustomDatePickerDialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                binding.fceffectiveDateTextviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        binding.editFcexpirationDateTextviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.editFcexpirationDateTextviewVal.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.editFcexpirationDateTextviewVal.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(),R.style.CustomDatePickerDialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                binding.editFcexpirationDateTextviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        binding.editFceffectiveDateTextviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.editFceffectiveDateTextviewVal.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.editFceffectiveDateTextviewVal.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(),R.style.CustomDatePickerDialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                binding.editFceffectiveDateTextviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        servicesArray.add("Select Service")

        for (fac in TypeTablesModel.getInstance().ServicesType) {


            servicesArray.add(fac.ServiceTypeName)
        }

        var servicesAdapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, servicesArray)
        servicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.fcServicesTextviewVal.adapter = servicesAdapter
        binding.editFcServicesTextviewVal.adapter = servicesAdapter

        binding.submitNewserviceButton.setOnClickListener {
            if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                if (validateInputs()){
                    binding.progressBarText.text = "Saving ..."
                    binding.FCLoadingView.visibility = View.VISIBLE



                    var item = TblFacilityServices()
                    for (fac in TypeTablesModel.getInstance().ServicesType) {
                        if (binding.fcServicesTextviewVal.getSelectedItem().toString().equals(fac.ServiceTypeName))

                            item.ServiceID =fac.ServiceTypeID
                    }
                    item.effDate = if (binding.fceffectiveDateTextviewVal.text.equals("SELECT DATE")) "" else binding.fceffectiveDateTextviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
                    item.expDate = if (binding.fcexpirationDateTextviewVal.text.equals("SELECT DATE")) "" else binding.fcexpirationDateTextviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
                    item.Comments=binding.commentsEditTextVal.text.toString()
                    Log.v("FAC SERVICES ADD --- ",UpdateFacilityServicesData + FacilityDataModel.getInstance().tblFacilities[0].FACNo +"&clubCode="+FacilityDataModel.getInstance().clubCode+"&facilityServicesId=&serviceId=${item.ServiceID}&effDate=${item.effDate}&expDate=${item.expDate}&comments=${item.Comments}&active=1&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat()+"&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate="+Date().toApiSubmitFormat())
                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateFacilityServicesData + FacilityDataModel.getInstance().tblFacilities[0].FACNo +"&clubCode="+FacilityDataModel.getInstance().clubCode+"&facilityServicesId=&serviceId=${item.ServiceID}&effDate=${item.effDate}&expDate=${item.expDate}&comments=${item.Comments}&active=1&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat()+"&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate="+Date().toApiSubmitFormat() + Utility.getLoggingParameters(activity, 0, getFacServiceChanges(0,0)),
                        { response ->
                            requireActivity().runOnUiThread {
                                if (response.toString().contains("returnCode>0<",false)) {
                                    HasChangedModel.getInstance().updateChangedData("Facility Services","","", getFacServiceChanges(0,0))
                                    item.FacilityServicesID= response.toString().substring(response.toString().indexOf("<FacilityServicesID")+20,response.toString().indexOf("</FacilityServicesID"))
                                    FacilityDataModel.getInstance().tblFacilityServices.add(item)
                                    FacilityDataModelOrg.getInstance().tblFacilityServices.add(item)
                                    Utility.showSubmitAlertDialog(activity, true, "Facility Services")
                                    fillPortalTrackingTableView()
                                    altFacServiceTableRow(2)
                                    (activity as FormsActivity).saveDone = true
                                    HasChangedModel.getInstance().groupSoSFacilityServices[0].SoSFacilityServices=true
                                    HasChangedModel.getInstance().changeDoneForSoSFacilityServices()
                                } else {
                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                                    Utility.showSubmitAlertDialog(activity, false, "Facility Services (Error: "+errorMessage+" )")
                                }
                                binding.facilityServicesCard.visibility = View.GONE
                                binding.FCLoadingView.visibility = View.GONE
                                (activity as FormsActivity).overrideBackButton = false
                                binding.progressBarText.text = "Loading ..."
                                binding.alphaBackgroundForFCServicesDialogs.visibility = View.GONE
                            }
                        },
                        {
                    Utility.showSubmitAlertDialog(activity, false, "Facility Services (Error: "+it.message+" )")
                        binding.facilityServicesCard.visibility = View.GONE
                        binding.FCLoadingView.visibility = View.GONE
                    (activity as FormsActivity).overrideBackButton = false
                        binding.progressBarText.text = "Loading ..."
                        binding.alphaBackgroundForFCServicesDialogs.visibility = View.GONE
                }))
                }else {
                    showValidationAlertDialog(activity, "Please fill all required fields \nExpiration Date should be after Effective Date")
    //                showValidationAlertDialog(activity,"Please fill all the required fields")
                }
            } else {
                Utility.showInternetWarningDialog(requireContext(),(requireActivity() as FormsActivity).networkStatusErrorMsg)
            }
        }
        fillPortalTrackingTableView();
        altFacServiceTableRow(2)
    }

    fun getFacServiceChanges(action : Int, rowId: Int) : String { // 0: Add 1: Edit
        var strChanges = ""
        try {
            if (action == 0) {
                strChanges = "Facility Service added with "
                strChanges += "Service (" + binding.fcServicesTextviewVal.getSelectedItem()
                    .toString() + ") - "
                strChanges += "Effective Date (" + if (binding.fceffectiveDateTextviewVal.text.equals("SELECT DATE")) "" else binding.fceffectiveDateTextviewVal.text.toString() + ") - "
                strChanges += "Expiration Date (" + if (binding.fcexpirationDateTextviewVal.text.equals("SELECT DATE")) "" else binding.fcexpirationDateTextviewVal.text.toString() + ") - "
                strChanges += "Comments (" + binding.commentsEditTextVal.text.toString() + ")"
            }
            val Comments = binding.editCommentsEditTextVal.text.toString()
            val effDate =
                if (binding.editFceffectiveDateTextviewVal.text.equals("SELECT DATE")) "" else binding.editFceffectiveDateTextviewVal.text.toString()
            val expDate =
                if (binding.editFcexpirationDateTextviewVal.text.equals("SELECT DATE")) "" else binding.editFcexpirationDateTextviewVal.text.toString()
            val facilityService = binding.editFcServicesTextviewVal.selectedItem.toString()
            if (action == 1) {
                strChanges += "Facility Service: " + TypeTablesModel.getInstance().ServicesType.filter { s ->
                    s.ServiceTypeID.equals(
                        FacilityDataModelOrg.getInstance().tblFacilityServices[rowId].ServiceID
                    )
                }[0].ServiceTypeName + " "
                if (Comments != FacilityDataModelOrg.getInstance().tblFacilityServices[rowId].Comments) {
                    strChanges += "Comments changed from (" + FacilityDataModelOrg.getInstance().tblFacilityServices[rowId].Comments + ") to (${Comments}) - "
                }
                if (effDate != FacilityDataModelOrg.getInstance().tblFacilityServices[rowId].effDate.apiToAppFormatMMDDYYYY()) {
                    strChanges += "Effective Date changed from (" + FacilityDataModelOrg.getInstance().tblFacilityServices[rowId].effDate.apiToAppFormatMMDDYYYY() + ") to (" + effDate + ") - "
                }
                if (expDate != FacilityDataModelOrg.getInstance().tblFacilityServices[rowId].expDate.apiToAppFormatMMDDYYYY()) {
                    strChanges += "Expiration Date changed from (" + FacilityDataModelOrg.getInstance().tblFacilityServices[rowId].expDate.apiToAppFormatMMDDYYYY() + ") to (" + expDate + ") - "
                }
                if (facilityService != (TypeTablesModel.getInstance().ServicesType.filter { s ->
                        s.ServiceTypeID.equals(
                            FacilityDataModelOrg.getInstance().tblFacilityServices[rowId].ServiceID
                        )
                    }[0].ServiceTypeName)) {
                    strChanges += "Service changed from (" + TypeTablesModel.getInstance().ServicesType.filter { s ->
                        s.ServiceTypeID.equals(
                            FacilityDataModelOrg.getInstance().tblFacilityServices[rowId].ServiceID
                        )
                    }[0].ServiceTypeName + ") to (" + facilityService + ") - "
                }
            }
            strChanges = strChanges.removeSuffix(" - ")
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
        }
        return strChanges
    }

    fun fillPortalTrackingTableView() {
        val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        if (binding.aarPortalTrackingTableLayout.childCount > 1) {
            for (i in binding.aarPortalTrackingTableLayout.childCount - 1 downTo 1) {
                binding.aarPortalTrackingTableLayout.removeViewAt(i)
            }
        }

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 3F
        rowLayoutParam.column = 0
        rowLayoutParam.leftMargin = 10
        rowLayoutParam.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1.5F
        rowLayoutParam1.column = 1
        rowLayoutParam1.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1.5F
        rowLayoutParam2.column = 2
        rowLayoutParam2.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 0

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 3F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam3.width = 0

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam4.width = 0

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableLayout.LayoutParams.WRAP_CONTENT


        FacilityDataModel.getInstance().tblFacilityServices.apply {
            (0 until size).forEach {
                if (!get(it).FacilityServicesID.equals("-1")) {
                    var tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow
                    tableRow.minimumHeight = 30


                    var textView1 = TextView(context)
                    textView1.layoutParams = rowLayoutParam
                    textView1.gravity = Gravity.CENTER_VERTICAL
                    textView1.textSize = 14f
                    textView1.setTextColor(Color.BLACK)
                    textView1.minimumHeight = 30

                    for (fac in TypeTablesModel.getInstance().ServicesType) {
                        if (get(it).ServiceID.equals(fac.ServiceTypeID))

                            textView1.text = fac.ServiceTypeName
                    }
                    tableRow.addView(textView1)

                    var textView = TextView(context)
                    textView.layoutParams = rowLayoutParam1
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 14f
                    textView.setTextColor(Color.BLACK)
                    textView.minimumHeight = 30
                    if (get(it).effDate.isNullOrBlank()) {
                        textView.text = ""
                    } else {
                        try {
                            textView.text = get(it).effDate.apiToAppFormatMMDDYYYY()
                        } catch (e: Exception) {

                            textView.text = get(it).effDate

                        }
                    }
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam2
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 14f
                    textView.setTextColor(Color.BLACK)
                    textView.minimumHeight = 30
                    TableRow.LayoutParams()
                    if (get(it).expDate.isNullOrBlank()) {
                        textView.text = ""
                    } else {
                        try {
                            textView.text = get(it).expDate.apiToAppFormatMMDDYYYY()
                        } catch (e: Exception) {

                            textView.text = get(it).expDate

                        }
                    }
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam3
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 14f
                    textView.setTextColor(Color.BLACK)
                    textView.minimumHeight = 30
                    textView.text = get(it).Comments
                    tableRow.addView(textView)


                    val updateButton = Button(context)
                    updateButton.layoutParams = rowLayoutParam4
                    updateButton.setTextColor(Color.BLUE)
                    updateButton.text = "EDIT"
                    updateButton.textSize = 14f
                    updateButton.minimumHeight = 30
                    updateButton.gravity = Gravity.CENTER
                    updateButton.setBackgroundColor(Color.TRANSPARENT)
                    tableRow.addView(updateButton)

                    binding.aarPortalTrackingTableLayout.addView(tableRow)
                    updateButton.setOnClickListener {
                        var currentTableRowIndex = binding.aarPortalTrackingTableLayout.indexOfChild(tableRow)
                        var currentfacilityDataModelIndex = currentTableRowIndex - 1
                        binding.editCommentsEditTextVal.setText(FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].Comments)
                        binding.editFceffectiveDateTextviewVal.setText(if (FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].effDate.equals("")) "SELECT DATE" else FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].effDate.apiToAppFormatMMDDYYYY())
                        binding.editFcexpirationDateTextviewVal.setText(if (FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].expDate.equals("")) "SELECT DATE" else FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].expDate.apiToAppFormatMMDDYYYY())


                        var i = servicesArray.indexOf(textView1.text)
                        binding.editFcServicesTextviewVal.setSelection(i)
                        (activity as FormsActivity).overrideBackButton = true
                        binding.editFacilityServicesCard.visibility = View.VISIBLE
                        binding.alphaBackgroundForFCServicesDialogs.visibility = View.VISIBLE
                        binding.editSubmitNewserviceButton.setOnClickListener {
                            if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                                if (edit_validateInputs()) {
                                    binding.progressBarText.text = "Saving ..."
                                    binding.FCLoadingView.visibility = View.VISIBLE
                                    (activity as FormsActivity).overrideBackButton = false
                                    var item = TblFacilityServices()
                                    for (fac in TypeTablesModel.getInstance().ServicesType) {
                                        if (binding.editFcServicesTextviewVal.getSelectedItem().toString().equals(fac.ServiceTypeName))
                                            item.ServiceID = fac.ServiceTypeID
                                    }
                                    item.effDate = if (binding.editFceffectiveDateTextviewVal.text.equals("SELECT DATE")) "" else binding.editFceffectiveDateTextviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
                                    item.expDate = if (binding.editFcexpirationDateTextviewVal.text.equals("SELECT DATE")) "" else binding.editFcexpirationDateTextviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
                                    item.Comments = binding.editCommentsEditTextVal.text.toString()
                                    item.FacilityServicesID = FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].FacilityServicesID
                                    Log.v("FAC SERVICES EDIT --- ",UpdateFacilityServicesData + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=" + FacilityDataModel.getInstance().clubCode + "&facilityServicesId=${item.FacilityServicesID}&serviceId=${item.ServiceID}&effDate=${item.effDate}&expDate=${item.expDate}&comments=${item.Comments}&active=1&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=" + Date().toApiSubmitFormat())
                                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateFacilityServicesData + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=" + FacilityDataModel.getInstance().clubCode + "&facilityServicesId=${item.FacilityServicesID}&serviceId=${item.ServiceID}&effDate=${item.effDate}&expDate=${item.expDate}&comments=${item.Comments}&active=1&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=" + Date().toApiSubmitFormat() + Utility.getLoggingParameters(activity, 1, getFacServiceChanges(1,currentfacilityDataModelIndex)),
                                        { response ->
                                            requireActivity().runOnUiThread {
                                                if (response.toString().contains("returnCode>0<", false)) {
                                                    HasChangedModel.getInstance().updateChangedData("Facility Services","","", getFacServiceChanges(1,currentfacilityDataModelIndex))
                                                    Utility.showSubmitAlertDialog(activity, true, "Facility Services")
                                                    FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].Comments = item.Comments
                                                    FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].effDate = item.effDate
                                                    FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].expDate = item.expDate
                                                    FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].ServiceID = item.ServiceID
                                                    FacilityDataModelOrg.getInstance().tblFacilityServices[currentfacilityDataModelIndex].Comments = item.Comments
                                                    FacilityDataModelOrg.getInstance().tblFacilityServices[currentfacilityDataModelIndex].effDate = item.effDate
                                                    FacilityDataModelOrg.getInstance().tblFacilityServices[currentfacilityDataModelIndex].expDate = item.expDate
                                                    FacilityDataModelOrg.getInstance().tblFacilityServices[currentfacilityDataModelIndex].ServiceID = item.ServiceID
                                                    fillPortalTrackingTableView()
                                                    altFacServiceTableRow(2)
                                                    (activity as FormsActivity).saveDone = true
                                                    HasChangedModel.getInstance().groupSoSFacilityServices[0].SoSFacilityServices = true
                                                    HasChangedModel.getInstance().changeDoneForSoSFacilityServices()
                                                } else {
                                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                                                    Utility.showSubmitAlertDialog(activity, false, "Facility Services (Error: "+errorMessage+" )")
                                                }
                                                binding.editFacilityServicesCard.visibility = View.GONE
                                                binding.FCLoadingView.visibility = View.GONE
                                                (activity as FormsActivity).overrideBackButton = false
                                                binding.progressBarText.text = "Loading ..."
                                                binding.alphaBackgroundForFCServicesDialogs.visibility = View.GONE
                                            }
                                        },
                                        {
                                    Utility.showSubmitAlertDialog(activity, false, "Facility Services (Error: "+it.message+" )")
                                        binding.editFacilityServicesCard.visibility = View.GONE
                                        binding.FCLoadingView.visibility = View.GONE
                                    (activity as FormsActivity).overrideBackButton = false
                                        binding.progressBarText.text = "Loading ..."
                                    binding.alphaBackgroundForFCServicesDialogs.visibility = View.GONE
                                }))
                                } else {
    //                                showValidationAlertDialog(activity, "Please fill all the required fields")
                                    showValidationAlertDialog(activity, "Please fill all required fields \nExpiration Date should be after Effective Date")
                                }
                            } else {
                                Utility.showInternetWarningDialog(requireContext(),(requireActivity() as FormsActivity).networkStatusErrorMsg)
                            }
                        }
                    }
                }
            }
        }
    }


    fun altFacServiceTableRow(alt_row : Int) {
        var childViewCount = binding.aarPortalTrackingTableLayout.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= binding.aarPortalTrackingTableLayout.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(getResources().getDrawable(
                        R.drawable.alt_row_color));
            } else {
                row.setBackground(getResources().getDrawable(
                        R.drawable.row_color));
            }

        }
    }





    fun validateInputs() : Boolean {

        var facServicesValide= TblFacilityServices().isInputsValid
        facServicesValide = true

        binding.fceffectiveDateTextviewVal.setError(null)
        binding.fcServiceSpinner.setError(null)
        binding.commentsEditTextVal.setError(null)
        if(binding.fceffectiveDateTextviewVal.text.toString().uppercase(getDefault()).equals("SELECT DATE")) {
            facServicesValide = false
            binding.fceffectiveDateTextviewVal.setError("Required Field")
        }

        if(!binding.fcexpirationDateTextviewVal.text.toString().uppercase(getDefault()).equals("SELECT DATE")) {
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val effDate = SimpleDateFormat(myFormat, Locale.US).parse(binding.fceffectiveDateTextviewVal!!.text.toString())
            val expDate = SimpleDateFormat(myFormat, Locale.US).parse(binding.fcexpirationDateTextviewVal!!.text.toString())
            if (expDate.before(effDate)) {
                facServicesValide = false
                binding.fcexpirationDateTextviewVal.setError("Should be after Effective Date")
            }
        }


        if(binding.fcServicesTextviewVal.selectedItemPosition.equals(0)) {
            facServicesValide = false
            binding.fcServiceSpinner.setError("Required Field")
        }

//        if(binding.commentsEditTextVal.text.isNullOrEmpty()) {
//            facServicesValide = false
//            binding.commentsEditTextVal.setError("Required Field")
//        }

        return facServicesValide
    }

    fun edit_validateInputs() : Boolean {

        var facServicesValide= TblFacilityServices().isInputsValid
        facServicesValide = true

        binding.editFceffectiveDateTextviewVal.setError(null)
        binding.editFcServiceSpinner.setError(null)
        binding.editCommentsEditTextVal.setError(null)
        if(binding.editFceffectiveDateTextviewVal.text.toString().uppercase(getDefault()).equals("SELECT DATE")) {
            facServicesValide = false
            binding.editFceffectiveDateTextviewVal.setError("Required Field")
        }

        if(!binding.editFcexpirationDateTextviewVal.text.toString().uppercase(getDefault()).equals("SELECT DATE")) {
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val effDate = SimpleDateFormat(myFormat, Locale.US).parse(binding.editFceffectiveDateTextviewVal!!.text.toString())
            val expDate = SimpleDateFormat(myFormat, Locale.US).parse(binding.editFcexpirationDateTextviewVal!!.text.toString())
            if (expDate.before(effDate)) {
                facServicesValide = false
                binding.editFcexpirationDateTextviewVal.setError("Should be after Effective Date")
            }
        }

        if(binding.editFcServicesTextviewVal.selectedItemPosition.equals(0)) {
            facServicesValide = false
            binding.editFcServiceSpinner.setError("Required Field")
        }

//        if(edit_binding.commentsEditTextVal.text.isNullOrEmpty()) {
//            facServicesValide = false
//            edit_binding.commentsEditTextVal.setError("Required Field")
//        }

        return facServicesValide
    }


//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//
//    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
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

    companion object {
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
        fun newInstance(param1: String, param2: String): FragmentARRAVFacilityServices {
            val fragment = FragmentARRAVFacilityServices()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    fun updateDialogs() {
        if (binding.facilityServicesCard != null) binding.facilityServicesCard.visibility = View.GONE
        if (binding.editFacilityServicesCard != null) binding.editFacilityServicesCard.visibility = View.GONE
        if (binding.alphaBackgroundForFCServicesDialogs != null) binding.alphaBackgroundForFCServicesDialogs.visibility = View.GONE
    }

}// Required empty public constructor
