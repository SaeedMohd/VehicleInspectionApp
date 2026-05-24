package com.inspection.fragments


import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.*
import android.util.Log
import android.util.Patterns
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.inspection.FormsActivity
import com.inspection.MainActivity
import com.inspection.R
import com.inspection.Utils.*
import com.inspection.Utils.Constants.UpdateFacilityLanguageData
import com.inspection.adapter.LanguageListAdapter
import com.inspection.databinding.FragmentAaravLocationBinding
import com.inspection.databinding.FragmentArravDeficiencyBinding
import com.inspection.databinding.FragmentArravlocationBinding
import com.inspection.model.*
//import kotlinx.android.synthetic.main.facility_group_layout.*
//import kotlinx.android.synthetic.main.fragment_aarav_location.*
//import kotlinx.android.synthetic.main.fragment_aarav_location.mainViewLinearId2
//import kotlinx.android.synthetic.main.fragment_aarav_personnel.*
//import kotlinx.android.synthetic.main.fragment_arravfacility_continued.*
//import kotlinx.android.synthetic.main.fragment_arravlocation.*
//import kotlinx.android.synthetic.main.fragment_visitation_form.alertVisitationRIcon
//import kotlinx.android.synthetic.main.fragment_visitation_form.alertVisitationYIcon
//import kotlinx.android.synthetic.main.fragment_visitation_form.emailEditText
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException


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
    private var states= arrayOf("select state","Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "District of Columbia", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming")
    private var hoursArray:Array<String>? = null
    var saveLangRequired = false
    var saveHoursRequired = false
    var saveGeoCodesRequired = false
    private var fusedLocationProviderClient : FusedLocationProviderClient? = null
    private var btnToBeUpdated = 0
    var emailValid = true
    var languagesGridView: ExpandableHeightGridView? = null
    internal var arrayAdapter: LanguageListAdapter? = null
    var langListItems=ArrayList<TypeTablesModel.languageType>()
    private var _binding: FragmentAaravLocationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater!!.inflate(R.layout.fragment_aarav_location, container, false)
        languagesGridView = view.findViewById(R.id.languagesGridView)

        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAaravLocationBinding.bind(view)
//      ArrayAdapter<String> adapterJazyky = new ArrayAdapter<String>(this,
//              R.layout.spinner_text_layout.xml, {"one","two","etc...."});
        val officeTimes = resources.getStringArray(R.array.officeTimes)
        binding.sunCloseSpinner.adapter = ArrayAdapter.createFromResource(requireActivity(),R.array.officeTimes,R.layout.spinner_time_item)
        binding.sunOpenSpinner.adapter = ArrayAdapter.createFromResource(requireActivity(),R.array.officeTimes,R.layout.spinner_time_item)
        binding.monCloseSpinner.adapter = ArrayAdapter.createFromResource(requireActivity(),R.array.officeTimes,R.layout.spinner_time_item)
        binding.monOpenSpinner.adapter = ArrayAdapter.createFromResource(requireActivity(),R.array.officeTimes,R.layout.spinner_time_item)
        binding.tueCloseSpinner.adapter = ArrayAdapter.createFromResource(requireActivity(),R.array.officeTimes,R.layout.spinner_time_item)
        binding.tueOpenSpinner.adapter = ArrayAdapter.createFromResource(requireActivity(),R.array.officeTimes,R.layout.spinner_time_item)
        binding.wedCloseSpinner.adapter = ArrayAdapter.createFromResource(requireActivity(),R.array.officeTimes,R.layout.spinner_time_item)
        binding.wedOpenSpinner.adapter = ArrayAdapter.createFromResource(requireActivity(),R.array.officeTimes,R.layout.spinner_time_item)
        binding.thuCloseSpinner.adapter = ArrayAdapter.createFromResource(requireActivity(),R.array.officeTimes,R.layout.spinner_time_item)
        binding.thuOpenSpinner.adapter = ArrayAdapter.createFromResource(requireActivity(),R.array.officeTimes,R.layout.spinner_time_item)
        binding.friCloseSpinner.adapter = ArrayAdapter.createFromResource(requireActivity(),R.array.officeTimes,R.layout.spinner_time_item)
        binding.friOpenSpinner.adapter = ArrayAdapter.createFromResource(requireActivity(),R.array.officeTimes,R.layout.spinner_time_item)
        binding.satCloseSpinner.adapter = ArrayAdapter.createFromResource(requireActivity(),R.array.officeTimes,R.layout.spinner_time_item)
        binding.satOpenSpinner.adapter = ArrayAdapter.createFromResource(requireActivity(),R.array.officeTimes,R.layout.spinner_time_item)


        FacilityDataModel.getInstance().tblHours[0].apply {
            if (SunClose.equals("00:00:01 AM")) SunClose="Closed"
            if (MonClose.equals("00:00:01 AM")) MonClose="Closed"
            if (TueClose.equals("00:00:01 AM")) TueClose="Closed"
            if (WedClose.equals("00:00:01 AM")) WedClose="Closed"
            if (ThuClose.equals("00:00:01 AM")) ThuClose="Closed"
            if (FriClose.equals("00:00:01 AM")) FriClose="Closed"
            if (SatClose.equals("00:00:01 AM")) SatClose="Closed"
            if (SunOpen.equals("00:00:01 AM")) SunOpen="Closed"
            if (MonOpen.equals("00:00:01 AM")) MonOpen="Closed"
            if (TueOpen.equals("00:00:01 AM")) TueOpen="Closed"
            if (WedOpen.equals("00:00:01 AM")) WedOpen="Closed"
            if (ThuOpen.equals("00:00:01 AM")) ThuOpen="Closed"
            if (FriOpen.equals("00:00:01 AM")) FriOpen="Closed"
            if (SatOpen.equals("00:00:01 AM")) SatOpen="Closed"
        }
        FacilityDataModelOrg.getInstance().tblHours[0].apply {
            if (SunClose.equals("00:00:01 AM")) SunClose="Closed"
            if (MonClose.equals("00:00:01 AM")) MonClose="Closed"
            if (TueClose.equals("00:00:01 AM")) TueClose="Closed"
            if (WedClose.equals("00:00:01 AM")) WedClose="Closed"
            if (ThuClose.equals("00:00:01 AM")) ThuClose="Closed"
            if (FriClose.equals("00:00:01 AM")) FriClose="Closed"
            if (SatClose.equals("00:00:01 AM")) SatClose="Closed"
            if (SunOpen.equals("00:00:01 AM")) SunOpen="Closed"
            if (MonOpen.equals("00:00:01 AM")) MonOpen="Closed"
            if (TueOpen.equals("00:00:01 AM")) TueOpen="Closed"
            if (WedOpen.equals("00:00:01 AM")) WedOpen="Closed"
            if (ThuOpen.equals("00:00:01 AM")) ThuOpen="Closed"
            if (FriOpen.equals("00:00:01 AM")) FriOpen="Closed"
            if (SatOpen.equals("00:00:01 AM")) SatOpen="Closed"
        }


        hoursArray = resources.getStringArray(R.array.officeTimes)
        fillLocationTableView()
        fillGeoCodesTable()
        fillHolidaysTableView()
        fillPhoneTableView()
        fillOpenHoursTableView()
        fillClosedHoursTableView()
        fillEmailTableView()
        setAlertColoring()
        binding.copyHoursBtn.setOnClickListener {
            binding.alphaBackgroundForDialogs.visibility = View.VISIBLE
            binding.copyHoursDialog.visibility = View.VISIBLE
            (activity as FormsActivity).overrideBackButton = true

            binding.copyButtonCV.setOnClickListener {
                var openValue = 0
                var closeValue = 0
                when (binding.fromDaySpinner.selectedItemPosition) {
                    0 -> {
                        openValue = binding.sunOpenSpinner.selectedItemPosition
                        closeValue = binding.sunCloseSpinner.selectedItemPosition
                    }
                    1 -> {
                        openValue = binding.monOpenSpinner.selectedItemPosition
                        closeValue = binding.monCloseSpinner.selectedItemPosition
                    }
                    2 -> {
                        openValue = binding.tueOpenSpinner.selectedItemPosition
                        closeValue = binding.tueCloseSpinner.selectedItemPosition
                    }
                    3 -> {
                        openValue = binding.wedOpenSpinner.selectedItemPosition
                        closeValue = binding.wedCloseSpinner.selectedItemPosition
                    }
                    4 -> {
                        openValue = binding.thuOpenSpinner.selectedItemPosition
                        closeValue = binding.thuCloseSpinner.selectedItemPosition
                    }
                    5 -> {
                        openValue = binding.friOpenSpinner.selectedItemPosition
                        closeValue = binding.friCloseSpinner.selectedItemPosition
                    }
                    6 -> {
                        openValue = binding.satOpenSpinner.selectedItemPosition
                        closeValue = binding.satCloseSpinner.selectedItemPosition
                    }
                }
                if (binding.toSunCB.isChecked) {
                    binding.sunOpenSpinner.setSelection(openValue)
                    binding.sunCloseSpinner.setSelection(closeValue)
                }
                if (binding.toMonCB.isChecked) {
                    binding.monOpenSpinner.setSelection(openValue)
                    binding.monCloseSpinner.setSelection(closeValue)
                }
                if (binding.toTueCB.isChecked) {
                    binding.tueOpenSpinner.setSelection(openValue)
                    binding.tueCloseSpinner.setSelection(closeValue)
                }
                if (binding.toWedCB.isChecked) {
                    binding.wedOpenSpinner.setSelection(openValue)
                    binding.wedCloseSpinner.setSelection(closeValue)
                }
                if (binding.toThuCB.isChecked) {
                    binding.thuOpenSpinner.setSelection(openValue)
                    binding.thuCloseSpinner.setSelection(closeValue)
                }
                if (binding.toFriCB.isChecked) {
                    binding.friOpenSpinner.setSelection(openValue)
                    binding.friCloseSpinner.setSelection(closeValue)
                }
                if (binding.toSatCB.isChecked) {
                    binding.satOpenSpinner.setSelection(openValue)
                    binding.satCloseSpinner.setSelection(closeValue)
                }
                saveHoursRequired = true
                binding.exitCopyDialogeBtnId.callOnClick()
            }
            binding.fromDaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    binding.toSunCB.isEnabled = (position!=0)
                    binding.toMonCB.isEnabled = (position!=1)
                    binding.toTueCB.isEnabled = (position!=2)
                    binding.toWedCB.isEnabled = (position!=3)
                    binding.toThuCB.isEnabled = (position!=4)
                    binding.toFriCB.isEnabled = (position!=5)
                    binding.toSatCB.isEnabled = (position!=6)
                }
            }

        }


        IndicatorsDataModel.getInstance().tblFacility[0].LocationVisited = true
        // SAEED TO BE REVIEWED
        (requireActivity().supportFragmentManager.findFragmentById(R.id.fragment) as? HasTabIndicators)?.refreshTabIndicators()
//        (activity as FormsActivity).contactInfoButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
        setServices()
        setFieldsListeners()
        (activity as FormsActivity).saveRequired = false
        refreshButtonsState()

    }

    fun refreshButtonsState(){
        binding.saveButton.isEnabled = (activity as FormsActivity).saveRequired
        binding.cancelButton.isEnabled = (activity as FormsActivity).saveRequired
    }




    fun setFieldsListeners(){

//        editGeo1Long.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(3, 6))
        binding.editGeo1Long.inputFilterDecimal(
                // this values must be positive (0+) unless it throw exception
                maxDigitsIncludingPoint = 5,
                maxDecimalPlaces = 6,
                signed = true
        )
        binding.editGeo1Long.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                try {
                    val s=p0.toString().toFloat()
                    if (s>0 || s<=-181) {
                        Utility.showValidationAlertDialog(activity,"Please enter value between 0 and -180")
                    } else {
                        if (FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Maps") }.isNullOrEmpty()) {
                            var item = TblGeocodes()
                            item.GeoCodeTypeID = TypeTablesModel.getInstance().GeocodeType.filter { s->s.GeocodeTypeName.contains("Maps")}[0].GeocodeTypeID
                            item.GeocodeTypeName = TypeTablesModel.getInstance().GeocodeType.filter { s->s.GeocodeTypeName.contains("Maps")}[0].GeocodeTypeName
                            item.LONGITUDE = p0.toString()
                            FacilityDataModel.getInstance().tblGeocodes.add(item)
                        } else {
                            FacilityDataModel.getInstance().tblGeocodes.filter { s -> s.GeocodeTypeName.contains("Maps") }[0].LONGITUDE = p0.toString()
                        }
                        (activity as FormsActivity).saveRequired = true
                        saveGeoCodesRequired = true
                        refreshButtonsState()
                    }
                } catch(e: java.lang.Exception) {

                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        binding.editGeo2Long.inputFilterDecimal(
                // this values must be positive (0+) unless it throw exception
                maxDigitsIncludingPoint = 5,
                maxDecimalPlaces = 6,
                signed = true
        )
        binding.editGeo2Long.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                try {//HERE
                    val s=p0.toString().toFloat()
                    if (s>0 || s<=-181) {
                        Utility.showValidationAlertDialog(activity,"Please enter value between 0 and -180")
                    } else {
                        if (FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Tow") }.isNullOrEmpty()) {
                            var item = TblGeocodes()
                            item.GeoCodeTypeID = TypeTablesModel.getInstance().GeocodeType.filter { s->s.GeocodeTypeName.contains("Tow")}[0].GeocodeTypeID
                            item.GeocodeTypeName = TypeTablesModel.getInstance().GeocodeType.filter { s->s.GeocodeTypeName.contains("Tow")}[0].GeocodeTypeName
                            item.LONGITUDE = p0.toString()
                            FacilityDataModel.getInstance().tblGeocodes.add(item)
                        } else {
                            FacilityDataModel.getInstance().tblGeocodes.filter { s -> s.GeocodeTypeName.contains("Tow") }[0].LONGITUDE = p0.toString()
                        }
                        (activity as FormsActivity).saveRequired = true
                        saveGeoCodesRequired = true
                        refreshButtonsState()
                    }
                } catch(e: java.lang.Exception) {

                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        binding.editGeo3Long.inputFilterDecimal(
                maxDigitsIncludingPoint = 5, maxDecimalPlaces = 6, signed = true
        )
        binding.editGeo3Long.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                try {
                    val s=p0.toString().toFloat()
                    if (s>0 || s<=-181) {
                        Utility.showValidationAlertDialog(activity,"Please enter value between 0 and -180")
                    } else {
                        if (FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Cust") }.isNullOrEmpty()) {
                            var item = TblGeocodes()
                            item.GeoCodeTypeID = TypeTablesModel.getInstance().GeocodeType.filter { s->s.GeocodeTypeName.contains("Cust")}[0].GeocodeTypeID
                            item.GeocodeTypeName = TypeTablesModel.getInstance().GeocodeType.filter { s->s.GeocodeTypeName.contains("Cust")}[0].GeocodeTypeName
                            item.LONGITUDE = p0.toString()
                            FacilityDataModel.getInstance().tblGeocodes.add(item)
                        } else {
                            FacilityDataModel.getInstance().tblGeocodes.filter { s -> s.GeocodeTypeName.contains("Cust")}[0].LONGITUDE = p0.toString()
                        }
                        (activity as FormsActivity).saveRequired = true
                        saveGeoCodesRequired = true
                        refreshButtonsState()
                    }
                } catch(e: java.lang.Exception) {

                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        binding.editGeo1Lat.inputFilterDecimal(maxDigitsIncludingPoint = 3, maxDecimalPlaces = 6, signed = false)
        binding.editGeo1Lat.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                try {//HERE2
                    val s=p0.toString().toFloat()
                    if (s<0 || s>90) {
                        Utility.showValidationAlertDialog(activity,"Please enter value between 0 and 90")
                    } else {
                        if (FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Maps")}.isNullOrEmpty()) {
                            var item = TblGeocodes()
                            item.GeoCodeTypeID = TypeTablesModel.getInstance().GeocodeType.filter { s->s.GeocodeTypeName.contains("Maps")}[0].GeocodeTypeID
                            item.GeocodeTypeName = TypeTablesModel.getInstance().GeocodeType.filter { s->s.GeocodeTypeName.contains("Maps")}[0].GeocodeTypeName
                            item.LATITUDE = p0.toString()
                            FacilityDataModel.getInstance().tblGeocodes.add(item)
                        } else {
                            FacilityDataModel.getInstance().tblGeocodes.filter { s -> s.GeocodeTypeName.contains("Maps")}[0].LATITUDE = p0.toString()
                        }
                        (activity as FormsActivity).saveRequired = true
                        saveGeoCodesRequired = true
                        refreshButtonsState()
                    }
                } catch(e: java.lang.Exception) {

                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        binding.editGeo2Lat.inputFilterDecimal(maxDigitsIncludingPoint = 3, maxDecimalPlaces = 6, signed = false)
        binding.editGeo2Lat.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                try {
                    val s=p0.toString().toFloat()
                    if (s<0 || s>90) {
                        Utility.showValidationAlertDialog(activity,"Please enter value between 0 and 90")
                    } else {
                        if (FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeoCodeTypeID==1 }.isNullOrEmpty()) {
                            var item = TblGeocodes()
                            item.GeoCodeTypeID = TypeTablesModel.getInstance().GeocodeType.filter { s->s.GeocodeTypeName.contains("Tow")}[0].GeocodeTypeID
                            item.GeocodeTypeName = TypeTablesModel.getInstance().GeocodeType.filter { s->s.GeocodeTypeName.contains("Tow")}[0].GeocodeTypeName
                            item.LATITUDE = p0.toString()
                            FacilityDataModel.getInstance().tblGeocodes.add(item)
                        } else {
                            FacilityDataModel.getInstance().tblGeocodes.filter { s -> s.GeocodeTypeName.contains("Tow")}[0].LATITUDE = p0.toString()
                        }
                        (activity as FormsActivity).saveRequired = true
                        saveGeoCodesRequired = true
                        refreshButtonsState()
                    }
                } catch(e: java.lang.Exception) {

                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        binding.editGeo3Lat.inputFilterDecimal(maxDigitsIncludingPoint = 3, maxDecimalPlaces = 6, signed = false)
        binding.editGeo3Lat.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                try {
                    val s=p0.toString().toFloat()
                    if (s<0 || s>90) {
                        Utility.showValidationAlertDialog(activity,"Please enter value between 0 and 90")
                    } else {
                        if (FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Cust")}.isNullOrEmpty()) {
                            var item = TblGeocodes()
                            item.GeoCodeTypeID = TypeTablesModel.getInstance().GeocodeType.filter { s->s.GeocodeTypeName.contains("Cust") }[0].GeocodeTypeID
                            item.GeocodeTypeName = TypeTablesModel.getInstance().GeocodeType.filter { s->s.GeocodeTypeName.contains("Cust") }[0].GeocodeTypeName
                            item.LATITUDE = p0.toString()
                            FacilityDataModel.getInstance().tblGeocodes.add(item)
                        } else {
                            FacilityDataModel.getInstance().tblGeocodes.filter { s -> s.GeocodeTypeName.contains("Cust")}[0].LATITUDE = p0.toString()
                        }
                        (activity as FormsActivity).saveRequired = true
                        saveGeoCodesRequired = true
                        refreshButtonsState()
                    }
                } catch(e: java.lang.Exception) {

                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        binding.cancelButton.setOnClickListener {
            binding.cancelButton.hideKeyboard()
            FacilityDataModel.getInstance().tblHours[0].SunClose = FacilityDataModelOrg.getInstance().tblHours[0].SunClose
            FacilityDataModel.getInstance().tblHours[0].SunOpen = FacilityDataModelOrg.getInstance().tblHours[0].SunOpen
            FacilityDataModel.getInstance().tblHours[0].SatClose = FacilityDataModelOrg.getInstance().tblHours[0].SatClose
            FacilityDataModel.getInstance().tblHours[0].SatOpen = FacilityDataModelOrg.getInstance().tblHours[0].SatOpen
            FacilityDataModel.getInstance().tblHours[0].MonOpen= FacilityDataModelOrg.getInstance().tblHours[0].MonOpen
            FacilityDataModel.getInstance().tblHours[0].MonClose= FacilityDataModelOrg.getInstance().tblHours[0].MonClose
            FacilityDataModel.getInstance().tblHours[0].TueOpen = FacilityDataModelOrg.getInstance().tblHours[0].TueOpen
            FacilityDataModel.getInstance().tblHours[0].TueClose = FacilityDataModelOrg.getInstance().tblHours[0].TueClose
            FacilityDataModel.getInstance().tblHours[0].WedClose= FacilityDataModelOrg.getInstance().tblHours[0].WedClose
            FacilityDataModel.getInstance().tblHours[0].WedOpen= FacilityDataModelOrg.getInstance().tblHours[0].WedOpen
            FacilityDataModel.getInstance().tblHours[0].ThuOpen= FacilityDataModelOrg.getInstance().tblHours[0].ThuOpen
            FacilityDataModel.getInstance().tblHours[0].ThuClose= FacilityDataModelOrg.getInstance().tblHours[0].ThuClose
            FacilityDataModel.getInstance().tblHours[0].FriClose= FacilityDataModelOrg.getInstance().tblHours[0].FriClose
            FacilityDataModel.getInstance().tblHours[0].FriOpen= FacilityDataModelOrg.getInstance().tblHours[0].FriOpen
            FacilityDataModel.getInstance().tblHours[0].NightDrop= FacilityDataModelOrg.getInstance().tblHours[0].NightDrop
            FacilityDataModel.getInstance().tblHours[0].NightDropInstr= FacilityDataModelOrg.getInstance().tblHours[0].NightDropInstr
            FacilityDataModel.getInstance().tblLanguage.clear()
            FacilityDataModel.getInstance().tblGeocodes.clear()

            FacilityDataModelOrg.getInstance().tblGeocodes.apply {
                (0 until size).forEach {
                    var geoCodeItem = TblGeocodes()
                    geoCodeItem.GeocodeTypeName = get(it).GeocodeTypeName
                    geoCodeItem.GeoCodeTypeID = get(it).GeoCodeTypeID
                    geoCodeItem.LATITUDE = get(it).LATITUDE
                    geoCodeItem.LONGITUDE = get(it).LONGITUDE
                    FacilityDataModel.getInstance().tblGeocodes.add(geoCodeItem)
                }
            }

            FacilityDataModelOrg.getInstance().tblLanguage.apply {
                (0 until size).forEach {
                    var langItem = TblLanguage()
                    langItem.LangTypeID = get(it).LangTypeID
                    FacilityDataModel.getInstance().tblLanguage.add(langItem)
                }
            }

            setServices()
            fillGeoCodesTable()
            fillOpenHoursTableView()
            fillHolidaysTableView()
            fillClosedHoursTableView()
            (activity as FormsActivity).saveRequired = false
            saveHoursRequired = false
            saveLangRequired = false
            saveGeoCodesRequired = false
            refreshButtonsState()
//            Utility.showMessageDialog(activity,"Confirmation ...","Changes cancelled succesfully")
            Utility.showUnifiedConfirmationDialog(activity,  "Changes cancelled successfully")
        }


        binding.sunOpenSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!binding.sunOpenSpinner.tag.equals(p2) || binding.sunOpenSpinner.tag.equals("-1")) {
                    binding.sunOpenSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].SunOpen = binding.sunOpenSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    saveHoursRequired = true
                    refreshButtonsState()
                }
            }
        }
        binding.sunCloseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!binding.sunCloseSpinner.tag.equals(p2) || binding.sunCloseSpinner.tag.equals("-1")) {
                    binding.sunCloseSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].SunClose = binding.sunCloseSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    saveHoursRequired = true
                    refreshButtonsState()
                }
            }
        }
        binding.monCloseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!binding.monCloseSpinner.tag.equals(p2) || binding.monCloseSpinner.tag.equals("-1")) {
                    binding.monCloseSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].MonClose = binding.monCloseSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    saveHoursRequired = true
                    refreshButtonsState()
                }
            }
        }
        binding.monOpenSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!binding.monOpenSpinner.tag.equals(p2) || binding.monOpenSpinner.tag.equals("-1")) {
                    binding.monOpenSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].MonOpen = binding.monOpenSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    saveHoursRequired = true
                    refreshButtonsState()
                }
            }
        }
        binding.tueCloseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!binding.tueCloseSpinner.tag.equals(p2) || binding.tueCloseSpinner.tag.equals("-1")) {
                    binding.tueCloseSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].TueClose = binding.tueCloseSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    saveHoursRequired = true
                    refreshButtonsState()
                }
            }
        }
        binding.tueOpenSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!binding.tueOpenSpinner.tag.equals(p2) || binding.tueOpenSpinner.tag.equals("-1")) {
                    binding.tueOpenSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].TueOpen = binding.tueOpenSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    saveHoursRequired = true
                    refreshButtonsState()
                }
            }
        }
        binding.wedOpenSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!binding.wedOpenSpinner.tag.equals(p2) || binding.wedOpenSpinner.tag.equals("-1")) {
                    binding.wedOpenSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].WedOpen = binding.wedOpenSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    saveHoursRequired = true
                    refreshButtonsState()
                }
            }
        }
        binding.wedCloseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!binding.wedCloseSpinner.tag.equals(p2) || binding.wedCloseSpinner.tag.equals("-1")) {
                    binding.wedCloseSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].WedClose = binding.wedCloseSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    saveHoursRequired = true
                    refreshButtonsState()
                }
            }
        }
        binding.thuCloseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!binding.thuCloseSpinner.tag.equals(p2) || binding.thuCloseSpinner.tag.equals("-1")) {
                    binding.thuCloseSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].ThuClose = binding.thuCloseSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    saveHoursRequired = true
                    refreshButtonsState()
                }
            }
        }
        binding.thuOpenSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!binding.thuOpenSpinner.tag.equals(p2) || binding.thuOpenSpinner.tag.equals("-1")) {
                    binding.thuOpenSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].ThuOpen = binding.thuOpenSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    saveHoursRequired = true
                    refreshButtonsState()
                }
            }
        }
        binding.friOpenSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!binding.friOpenSpinner.tag.equals(p2) || binding.friOpenSpinner.tag.equals("-1")) {
                    binding.friOpenSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].FriOpen = binding.friOpenSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    saveHoursRequired = true
                    refreshButtonsState()
                }
            }
        }
        binding.friCloseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!binding.friCloseSpinner.tag.equals(p2) || binding.friCloseSpinner.tag.equals("-1")) {
                    binding.friCloseSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].FriClose = binding.friCloseSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    saveHoursRequired = true
                    refreshButtonsState()
                }
            }
        }
        binding.satCloseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!binding.satCloseSpinner.tag.equals(p2) || binding.satCloseSpinner.tag.equals("-1")) {
                    binding.satCloseSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].SatClose = binding.satCloseSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    saveHoursRequired = true
                    refreshButtonsState()
                }
            }
        }
        binding.satOpenSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!binding.satOpenSpinner.tag.equals(p2) || binding.satOpenSpinner.tag.equals("-1")) {
                    binding.satOpenSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].SatOpen = binding.satOpenSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    saveHoursRequired = true
                    refreshButtonsState()
                }
            }
        }


        binding.facilityIsOpenEffDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireContext(),R.style.CustomDatePickerDialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                binding.facilityIsOpenEffDateBtn!!.text = sdf.format(c.time)
                HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
            }, year, month, day)
            dpd.show()
        }
        binding.facilityIsOpenExpDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireContext(),R.style.CustomDatePickerDialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                binding.facilityIsOpenExpDateBtn!!.text = sdf.format(c.time)
                HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
            }, year, month, day)
            dpd.show()
        }

        binding.exitAddEmailDialogeBtnId.setOnClickListener({
            binding.addNewEmailDialog.visibility = View.GONE
            binding.alphaBackgroundForDialogs.visibility = View.GONE
//            enableAllAddButnsAndDialog()
            (activity as FormsActivity).overrideBackButton = false
        })

        binding.exitCopyDialogeBtnId.setOnClickListener({
            binding.copyHoursDialog.visibility = View.GONE
            binding.alphaBackgroundForDialogs.visibility = View.GONE
//            enableAllAddButnsAndDialog()
            (activity as FormsActivity).overrideBackButton = false
        })


        binding.exitUpdateEmailDialogeBtnId.setOnClickListener({
            binding.editEmailDialog.visibility = View.GONE
            binding.alphaBackgroundForDialogs.visibility = View.GONE
            (activity as FormsActivity).overrideBackButton = false
//            enableAllAddButnsAndDialog()
        })

//        exitAddLocationDialogeBtnId.setOnClickListener({
//            addNewLocationDialog.visibility = View.GONE
//                     alphaBackgroundForDialogs.visibility = View.GONE
//            enableAllAddButnsAndDialog()
//        })

        binding.exitEditLocationDialogeBtnId.setOnClickListener({
            binding.editLocationDialog.visibility = View.GONE
            binding.alphaBackgroundForDialogs.visibility = View.GONE
            (activity as FormsActivity).overrideBackButton = false
//            enableAllAddButnsAndDialog()
        })
        binding.exitUpdatePhoneDialogeBtnId.setOnClickListener({
            binding.alphaBackgroundForDialogs.visibility = View.GONE
            binding.editPhoneDialog.visibility = View.GONE
            (activity as FormsActivity).overrideBackButton = false
//            enableAllAddButnsAndDialog()
        })
        binding.exitAddPhoneDialogeBtnId.setOnClickListener({
            binding.addNewPhoneDialog.visibility = View.GONE
            binding.alphaBackgroundForDialogs.visibility = View.GONE
            (activity as FormsActivity).overrideBackButton = false
//            enableAllAddButnsAndDialog()
        })

//        addNewLocationButton.setOnClickListener({
//
//            disableAllAddButnsAndDialog()
//            showLocationDialog()
//        })

        binding.addNewPhoneButton.setOnClickListener({
//            disableAllAddButnsAndDialog()
            (activity as FormsActivity).overrideBackButton = true
            showPhoneDialog()
        })

        binding.addNewEmailButton.setOnClickListener {
//            disableAllAddButnsAndDialog()
            (activity as FormsActivity).overrideBackButton = true
            showEmailDialog()
        }

        setLocations()



        binding.locationSubmitButton.setOnClickListener({
            // missing validation for states when the lookup is ready

            var location =TblAddress().locIsInputsValid

//            if (newStateSpinner.selectedItem.toString().contains("select")){
//                location=false
//                stateTextView.setError("required field")
//
//            }    else
//            { stateTextView.setError(null)}
//
//
//            if (newLocAddr1Text.text.isNullOrEmpty()) {
//                location=false
//                newLocAddr1Text.setError("please enter address 1")
//            }
//            if (newLocCityText.text.isNullOrEmpty()) {
//                location=false
//                newLocCityText.setError("please enter city")
//            }
//            if (newLocCountryText.text.isNullOrEmpty()) {
//                location=false
//                newLocCountryText.setError("please enter country")
//            }
//            if (newLocZipText.text.isNullOrEmpty()) {
//                location=false
//                newLocZipText.setError("please enter country")
//            }
//            if (newLocTypeSpinner.selectedItem.equals("Physical") && newLocLongText.text.isNullOrEmpty()) {
//                location=false
//                newLocLongText.setError("please enter longitude")
//            }
//            if (newLocTypeSpinner.selectedItem.equals("Physical") && newLocLatText.text.isNullOrEmpty()) {
//                location=false
//                newLocLatText.setError("please enter latitude")
//            }
//            if (newLocBranchNoText.text.isNullOrEmpty()) {
//                location=false
//                newLocBranchNoText.setError("please enter branch number")
//            }
//            if (newLocBranchNameText.text.isNullOrEmpty()) {
//                location=false
//                newLocBranchNameText.setError("please enter branch name")
//            }
//                if (newLocAddr1Text.text.isNullOrEmpty()||newLocCityText.text.isNullOrEmpty()||newLocCountryText.text.isNullOrEmpty()
//                ||newLocZipText.text.isNullOrEmpty()||(newLocTypeSpinner.selectedItem.equals("Physical") && newLocLongText.text.isNullOrEmpty())
//                ||(newLocTypeSpinner.selectedItem.equals("Physical") && newLocLatText.text.isNullOrEmpty())
//                ||newLocBranchNoText.text.isNullOrEmpty()||newLocBranchNameText.text.isNullOrEmpty()||newLocBranchNameText.text.isNullOrEmpty()
//                ||newStateSpinner.selectedItem.toString().contains("select")){
//                    location=false
//                    Toast.makeText(context,"please fill required fields",Toast.LENGTH_SHORT).show()
//
//                }else {
//                    location=true
//                    submitFacilityAddress()
//                    enableAllAddButnsAndDialog()
//                        }

        })


        binding.phoneSubmitButton.setOnClickListener({
            if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                var phoneValide=TblPhone().phoneIsInputsValid
                if (binding.newPhoneNoText.text.isNullOrEmpty()) {
                    binding.newPhoneNoText.setError("please enter phone number")
                    phoneValide=false
                } else {
                    phoneValide=true
                    submitFacilityPhone()
    //                enableAllAddButnsAndDialog()
                }
            } else {
                Utility.showInternetWarningDialog(requireContext(),(requireActivity() as FormsActivity).networkStatusErrorMsg)
            }
        })

        binding.emailSubmitButton.setOnClickListener({
            if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                var emailValid=TblFacilityEmail().emailIsInputsValid
                if (binding.newEmailAddrText.text.isNullOrEmpty()) {
                    emailValid=false
                    binding.newEmailAddrText.setError("Required Field")
                } else if (!Utility.isEmailValid(binding.newEmailAddrText.text.toString())) {
                    Utility.showValidationAlertDialog(activity,"Please enter a valid Email address")
                }else {
                    emailValid=true
                    submitFacilityEmail()
    //                enableAllAddButnsAndDialog()
                }
            } else {
                Utility.showInternetWarningDialog(requireContext(),(requireActivity() as FormsActivity).networkStatusErrorMsg)
            }
        })

        binding.saveButton.setOnClickListener(View.OnClickListener {
//            contactInfoLoadingText.text = "Saving ..."
//            contactInfoLoadingView.visibility = View.VISIBLE
            if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                if (saveGeoCodesRequired) {
                    var msg = validateGeoCodesInputs()
                 if (!msg.equals("")) {
                     Utility.showValidationAlertDialog(activity,msg)
                 } else {
                     binding.contactInfoLoadingText.text = "Saving ..."
                     binding.contactInfoLoadingView.visibility = View.VISIBLE
                     submitGeoCodes()
                     if (saveHoursRequired) submitHours()
                     if (saveLangRequired) submitLanguages()
                 }
                } else {
                    binding.contactInfoLoadingText.text = "Saving ..."
                    binding.contactInfoLoadingView.visibility = View.VISIBLE
                    if (saveHoursRequired) submitHours()
                    if (saveLangRequired) submitLanguages()
                }
            } else {
                Utility.showInternetWarningDialog(requireContext(),(requireActivity() as FormsActivity).networkStatusErrorMsg)
            }
        })

        binding.nightDropCheck.setOnCheckedChangeListener { compoundButton, b ->
            FacilityDataModel.getInstance().tblHours[0].NightDrop = b
            HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
            HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
            (activity as FormsActivity).saveRequired = true
            saveHoursRequired = true
            refreshButtonsState()
        }

        binding.nightDropInstText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblHours[0].NightDropInstr = p0.toString()
                HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                (activity as FormsActivity).saveRequired = true
                saveHoursRequired = true
                refreshButtonsState()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })




    }

    private fun setAlertColoring() {
        var toolTipStr = ""
        binding.alertLocationYIcon.tooltipText = toolTipStr
        binding.alertLocationRIcon.tooltipText = toolTipStr
        FacilityDataModel.getInstance().tblFacilityEmail.apply {
            (0 until size).forEach {
                if (!get(it).emailID.equals("-1")) {
                    if (!emailFormatValidation(get(it).email)) {
                        binding.alertLocationRIcon.isVisible = true
                        binding.alertLocationYIcon.isVisible = false
                        binding.alertLocationRIcon.isClickable = true
                        toolTipStr = "${get(it).email} Format is incorrect"
                        binding.alertLocationRIcon.setOnClickListener({
//                            Utility.showMessageDialog(requireContext(), "Notification", toolTipStr)
                            Utility.showUnifiedInformationDialog(activity,toolTipStr)
                        })
                    } else {
                        binding.alertLocationRIcon.isVisible = false
                        binding.alertLocationYIcon.isVisible = false
                    }
                }
            }
        }



        val animation: Animation = AlphaAnimation(1.0f, 0.0f)
        animation.duration = 500 //1 second duration for each animation cycle
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = Animation.INFINITE //repeating indefinitely
        animation.repeatMode = Animation.REVERSE //animation will start from end point once ended.
        binding.alertLocationRIcon.startAnimation(animation) //to start animation
        binding.alertLocationYIcon.startAnimation(animation) //to start animation

    }


    fun emailFormatValidation(target: CharSequence): Boolean {
        // issue reported for some emails

        if (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())
            emailValid = true else emailValid = false


        return emailValid
    }

//    fun enableAllAddButnsAndDialog(){
//
//        for (i in 0 until mainViewLinearId.childCount) {
//            val child = mainViewLinearId.getChildAt(i)
//            child.isEnabled = true
//        }
//        for (i in 0 until mainViewLinearId2.childCount) {
//            val child = mainViewLinearId2.getChildAt(i)
//            child.isEnabled = true
//        }
//        for (i in 0 until mainViewLinearId3.childCount) {
//            val child = mainViewLinearId3.getChildAt(i)
//            child.isEnabled = true
//        }
//        var childViewCount = phoneTbl.getChildCount();
//        for ( i in 1..childViewCount-1) {
//            var row : TableRow= phoneTbl.getChildAt(i) as TableRow;
//            for (j in 0..row.getChildCount()-1) {
//                var tv : TextView= row.getChildAt(j) as TextView
//                tv.isEnabled=true
//
//            }
//
//        }
//        var locationChildViewCount = locationTbl.getChildCount();
//
//        for ( i in 1..locationChildViewCount-1) {
//            var row : TableRow= locationTbl.getChildAt(i) as TableRow;
//
//            for (j in 0..row.getChildCount()-1) {
//
//                var tv : TextView= row.getChildAt(j) as TextView
//                tv.isEnabled=true
//
//            }
//
//        }
//        var emailChildViewCount = emailTbl.getChildCount();
//
//        for ( i in 1..emailChildViewCount-1) {
//            var row : TableRow= emailTbl.getChildAt(i) as TableRow;
//
//            for (j in 0..row.getChildCount()-1) {
//
//                var tv : TextView= row.getChildAt(j) as TextView
//                tv.isEnabled=true
//
//            }
//
//        }
//
//
//
//    }
//    fun disableAllAddButnsAndDialog(){
//
//        for (i in 0 until mainViewLinearId.childCount) {
//            val child = mainViewLinearId.getChildAt(i)
//            child.isEnabled = false
//        }
//
//        for (i in 0 until mainViewLinearId2.childCount) {
//            val child = mainViewLinearId2.getChildAt(i)
//            child.isEnabled = false
//        }
//
//        for (i in 0 until mainViewLinearId3.childCount) {
//            val child = mainViewLinearId3.getChildAt(i)
//            child.isEnabled = false
//        }
//
//
//
//        var childViewCount = phoneTbl.getChildCount();
//
//        for ( i in 1..childViewCount-1) {
//            var row : TableRow= phoneTbl.getChildAt(i) as TableRow;
//
//            for (j in 0..row.getChildCount()-1) {
//
//                var tv : TextView= row.getChildAt(j) as TextView
//                tv.isEnabled=false
//
//            }
//
//        }
//        var locationChildViewCount = locationTbl.getChildCount();
//
//        for ( i in 1..locationChildViewCount-1) {
//            var row : TableRow= locationTbl.getChildAt(i) as TableRow;
//
//            for (j in 0..row.getChildCount()-1) {
//
//                var tv : TextView= row.getChildAt(j) as TextView
//                tv.isEnabled=false
//
//            }
//
//        }
//        var emailChildViewCount = emailTbl.getChildCount();
//
//        for ( i in 1..emailChildViewCount-1) {
//            var row : TableRow= emailTbl.getChildAt(i) as TableRow;
//
//            for (j in 0..row.getChildCount()-1) {
//
//                var tv : TextView= row.getChildAt(j) as TextView
//                tv.isEnabled=false
//
//            }
//
//        }
//
//
//
//    }

//    fun languageGridViewCLick(v : View){
//        (activity as FormsActivity).saveRequired = true
//        refreshButtonsState()
//    }

    private fun setServices() {

        langListItems.clear()

        for (model in TypeTablesModel.getInstance().LanguageType) {

            langListItems.add(model)

        }

        arrayAdapter = LanguageListAdapter(requireContext(), R.layout.lang_checkbox_item, this ,langListItems)

        languagesGridView?.adapter = arrayAdapter
        languagesGridView?.isExpanded=true

//        languagesGridView?.setOnClickListener({
//            (activity as FormsActivity).saveRequired = true
//            refreshButtonsState()
//        })

    }

    private var locationTypeList = ArrayList<TypeTablesModel.locationType>()
    private var locationypeArray = ArrayList<String>()

    private var phoneTypeList = ArrayList<TypeTablesModel.locationPhoneType>()
    private var phoneTypeArray = ArrayList<String>()

    private var emailTypeList = ArrayList<TypeTablesModel.emailType>()
    private var emailTypeArray = ArrayList<String>()



//    private fun showLocationDialog() {
//
//        alphaBackgroundForDialogs.visibility = View.VISIBLE
//        addNewLocationDialog.visibility = View.VISIBLE
//
//        locationTypeList = TypeTablesModel.getInstance().LocationType
//        locationypeArray.clear()
//        for (fac in locationTypeList) {
//            locationypeArray.add(fac.LocTypeName)
//        }
//
//        var locTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, locationypeArray)
//        locTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        newLocTypeSpinner.adapter = locTypeAdapter
////        locationDialogView.newLoc2TypeSpinner.adapter = locTypeAdapter
//
//
//    }


    fun getAddressChanges() : String {
        var strChanges = ""
        try {
            if (binding.newLocLatText.text.toString() != FacilityDataModelOrg.getInstance().tblAddress[0].LATITUDE) {
                strChanges += "Lattitude changed from (" + FacilityDataModelOrg.getInstance().tblAddress[0].LATITUDE + ") to (" + binding.newLocLatText.text.toString() + ") - "
            }
            if (binding.newLocLongText.text.toString() != FacilityDataModelOrg.getInstance().tblAddress[0].LONGITUDE) {
                strChanges += "Longitude changed from (" + FacilityDataModelOrg.getInstance().tblAddress[0].LONGITUDE + ") to (" + binding.newLocLongText.text.toString() + ") - "
            }
            strChanges = strChanges.removeSuffix(" - ")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strChanges
    }


    fun getEmailChanges(action : Int,rowId: Int) : String {
        var strChanges = ""
        try {
        if (action==0) {
            strChanges += "New entry added as Email (" + binding.newEmailAddrText.text.toString() + ") and type (" + binding.newEmailTypeSpinner.selectedItem.toString() + ")"
        } else {
            if (binding.newChangesEmailText.text.toString() != FacilityDataModelOrg.getInstance().tblFacilityEmail[rowId].email) {
                strChanges += "Email changed from (" + FacilityDataModelOrg.getInstance().tblFacilityEmail[rowId].email + ") to (" + binding.newChangesEmailText.text.toString() + ") - "
            }
        }
        strChanges = strChanges.removeSuffix(" - ")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strChanges
    }


    fun getHoursChanges() : String {
        var strChanges = ""
        try {
            val sunClose = binding.sunCloseSpinner.selectedItem.toString()
            val monClose = binding.monCloseSpinner.selectedItem.toString()
            val tueClose = binding.tueCloseSpinner.selectedItem.toString()
            val wedClose = binding.wedCloseSpinner.selectedItem.toString()
            val thuClose = binding.thuCloseSpinner.selectedItem.toString()
            val friClose = binding.friCloseSpinner.selectedItem.toString()
            val satClose = binding.satCloseSpinner.selectedItem.toString()
            val sunOpen = binding.sunOpenSpinner.selectedItem.toString()
            val monOpen = binding.monOpenSpinner.selectedItem.toString()
            val tueOpen = binding.tueOpenSpinner.selectedItem.toString()
            val wedOpen = binding.wedOpenSpinner.selectedItem.toString()
            val thuOpen = binding.thuOpenSpinner.selectedItem.toString()
            val friOpen = binding.friOpenSpinner.selectedItem.toString()
            val satOpen = binding.satOpenSpinner.selectedItem.toString()
            // HAVE TO HANDLE CLOSED AND OR 00:00:01

            if ((sunClose != FacilityDataModelOrg.getInstance().tblHours[0].SunClose) && (FacilityDataModelOrg.getInstance().tblHours[0].SunClose != "")) {
                strChanges += "Sunday closing time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].SunClose + ") to (" + sunClose + ") - "
            }
            if ((monClose != FacilityDataModelOrg.getInstance().tblHours[0].MonClose) && (FacilityDataModelOrg.getInstance().tblHours[0].MonClose != "")) {
                strChanges += "Monday closing time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].MonClose + ") to (" + monClose + ") - "
            }
            if ((tueClose != FacilityDataModelOrg.getInstance().tblHours[0].TueClose) && (FacilityDataModelOrg.getInstance().tblHours[0].TueClose != "")) {
                strChanges += "Tuesday closing time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].TueClose + ") to (" + tueClose + ") - "
            }
            if ((wedClose != FacilityDataModelOrg.getInstance().tblHours[0].WedClose) && (FacilityDataModelOrg.getInstance().tblHours[0].WedClose != "")) {
                strChanges += "Wednesday closing time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].WedClose + ") to (" + wedClose + ") - "
            }
            if ((thuClose != FacilityDataModelOrg.getInstance().tblHours[0].ThuClose) && (FacilityDataModelOrg.getInstance().tblHours[0].ThuClose != "")) {
                strChanges += "Thursday closing time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].ThuClose + ") to (" + thuClose + ") - "
            }
            if ((friClose != FacilityDataModelOrg.getInstance().tblHours[0].FriClose) && (FacilityDataModelOrg.getInstance().tblHours[0].FriClose != "")) {
                strChanges += "Friday closing time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].FriClose + ") to (" + friClose + ") - "
            }
            if ((satClose != FacilityDataModelOrg.getInstance().tblHours[0].SatClose) && (FacilityDataModelOrg.getInstance().tblHours[0].SunClose != "")) {
                strChanges += "Saturday closing time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].SatClose + ") to (" + satClose + ") - "
            }

            if ((sunOpen != FacilityDataModelOrg.getInstance().tblHours[0].SunOpen) && (FacilityDataModelOrg.getInstance().tblHours[0].SunOpen != "")) {
                strChanges += "Sunday opening time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].SunOpen + ") to (" + sunOpen + ") - "
            }
            if ((monOpen != FacilityDataModelOrg.getInstance().tblHours[0].MonOpen) && (FacilityDataModelOrg.getInstance().tblHours[0].MonOpen != "")) {
                strChanges += "Monday opening time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].MonOpen + ") to (" + monOpen + ") - "
            }
            if ((tueOpen != FacilityDataModelOrg.getInstance().tblHours[0].TueOpen) && (FacilityDataModelOrg.getInstance().tblHours[0].TueOpen != "")) {
                strChanges += "Tuesday opening time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].TueOpen + ") to (" + tueOpen + ") - "
            }
            if ((wedOpen != FacilityDataModelOrg.getInstance().tblHours[0].WedOpen) && (FacilityDataModelOrg.getInstance().tblHours[0].WedOpen != "")) {
                strChanges += "Wednesday opening time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].WedOpen + ") to (" + wedOpen + ") - "
            }
            if ((thuOpen != FacilityDataModelOrg.getInstance().tblHours[0].ThuOpen) && (FacilityDataModelOrg.getInstance().tblHours[0].ThuOpen != "")) {
                strChanges += "Thursday opening time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].ThuOpen + ") to (" + thuOpen + ") - "
            }
            if ((friOpen != FacilityDataModelOrg.getInstance().tblHours[0].FriOpen) && (FacilityDataModelOrg.getInstance().tblHours[0].FriOpen != "")) {
                strChanges += "Friday opening time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].FriOpen + ") to (" + friOpen + ") - "
            }
            if ((satOpen != FacilityDataModelOrg.getInstance().tblHours[0].SatOpen) && (FacilityDataModelOrg.getInstance().tblHours[0].SatOpen != "")) {
                strChanges += "Saturday opening time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].SatOpen + ") to (" + satOpen + ") - "
            }
            if (binding.nightDropCheck.isChecked != FacilityDataModelOrg.getInstance().tblHours[0].NightDrop) {
                strChanges += "Night Drop availability changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].NightDrop + ") to (" + binding.nightDropCheck.isChecked + ") - "
            }
            if (binding.nightDropInstText.text.toString() != FacilityDataModelOrg.getInstance().tblHours[0].NightDropInstr) {
                strChanges += "Night Drop instructions changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].NightDropInstr + ") to (" + binding.nightDropInstText.text.toString() + ") - "
            }
            strChanges = strChanges.removeSuffix(" - ")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strChanges
    }

    fun getLanguageChanges() : String {
        var strChanges = ""
        try {
//            if (FacilityDataModel.getInstance().tblLanguage.size != FacilityDataModelOrg.getInstance().tblLanguage.size) {
//                strChanges += "Facility Languages changed from ("
//                FacilityDataModelOrg.getInstance().tblLanguage.apply {
//                    (0 until size).forEach {
//                        strChanges += TypeTablesModel.getInstance().LanguageType.filter { s ->
//                            s.LangTypeID.equals(
//                                get(it).LangTypeID
//                            )
//                        }[0].LangTypeName + " - "
//                    }
//                }
//                strChanges = strChanges.removeSuffix(" - ")
//                strChanges += ") to ("
//                FacilityDataModel.getInstance().tblLanguage.apply {
//                    (0 until size).forEach {
//                        strChanges += TypeTablesModel.getInstance().LanguageType.filter { s ->
//                            s.LangTypeID.equals(
//                                get(it).LangTypeID
//                            )
//                        }[0].LangTypeName + " - "
//                    }
//                }
//                strChanges = strChanges.removeSuffix(" - ")
//                strChanges += ")"
//            }
//            if (FacilityDataModel.getInstance().tblLanguage.size != FacilityDataModelOrg.getInstance().tblLanguage.size) {
                strChanges += "Added ("
                FacilityDataModel.getInstance().tblLanguage.apply {
                    (0 until size).forEach {
                        if (FacilityDataModelOrg.getInstance().tblLanguage.filter { s->s.LangTypeID.equals(get(it).LangTypeID)}.isEmpty()) {
                            strChanges += TypeTablesModel.getInstance().LanguageType.filter { s ->
                                s.LangTypeID.equals(
                                    get(it).LangTypeID
                                )
                            }[0].LangTypeName + " - "
                        }
                    }
                }
                strChanges = strChanges.removeSuffix(" - ")
                strChanges += ") "

                strChanges += " Removed ("

                FacilityDataModelOrg.getInstance().tblLanguage.apply {
                    (0 until size).forEach {
                        if (FacilityDataModel.getInstance().tblLanguage.filter { s->s.LangTypeID.equals(get(it).LangTypeID)}.isEmpty()) {
                            strChanges += TypeTablesModel.getInstance().LanguageType.filter { s ->
                                s.LangTypeID.equals(
                                    get(it).LangTypeID
                                )
                            }[0].LangTypeName + " - "
                        }
                    }
                }
                strChanges = strChanges.removeSuffix(" - ")
                strChanges += ")"
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strChanges
    }

    fun getGeoCodesChanges() : String {
        var strChanges = ""
        try {
            if ((!FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Maps")}.isNullOrEmpty()) && (FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Maps")}.isNullOrEmpty())) {
                strChanges += "GeoCode Type (Maps & Driving Directions) added - "
            } else if ((!FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Maps")}.isNullOrEmpty()) && (!FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Maps")}.isNullOrEmpty())) {
                if (FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Maps")}[0].LATITUDE != FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Maps")}[0].LATITUDE) {
                    strChanges += "GeoCode Type (Maps & Driving Directions) Latitude changed from (" + FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Maps")}[0].LATITUDE + ") to (" + FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Maps")}[0].LATITUDE + ") - "
                }
                if (FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Maps")}[0].LONGITUDE != FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Maps")}[0].LONGITUDE) {
                    strChanges += "GeoCode Type (Maps & Driving Directions) Longitude changed from (" + FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Maps")}[0].LONGITUDE + ") to (" + FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Maps")}[0].LONGITUDE + ") - "
                }
            }
            if ((!FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Tow")}.isNullOrEmpty()) && (FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Tow")}.isNullOrEmpty())) {
                strChanges += "GeoCode Type (Tow Truck Drop-Off) added - "
            } else if ((!FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Tow")}.isNullOrEmpty()) && (!FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Tow")}.isNullOrEmpty())) {
                if (FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Tow")}[0].LATITUDE != FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Tow")}[0].LATITUDE) {
                    strChanges += "GeoCode Type (Tow Truck Drop-Off) Latitude changed from (" + FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Tow")}[0].LATITUDE + ") to (" + FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Tow")}[0].LATITUDE + ") - "
                }
                if (FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Tow")}[0].LONGITUDE != FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Tow")}[0].LONGITUDE) {
                    strChanges += "GeoCode Type (Tow Truck Drop-Off) Longitude changed from (" + FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Tow")}[0].LONGITUDE + ") to (" + FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Tow")}[0].LONGITUDE + ") - "
                }
            }
            if ((!FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Cust")}.isNullOrEmpty()) && (FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Cust")}.isNullOrEmpty())) {
                strChanges += "GeoCode Type (Customer Waiting Area) added - "
            } else if ((!FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Cust")}.isNullOrEmpty()) && (!FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Cust")}.isNullOrEmpty())) {
                if (FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Cust")}[0].LATITUDE != FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Cust")}[0].LATITUDE) {
                    strChanges += "GeoCode Type (Customer Waiting Area) Latitude changed from (" + FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Cust")}[0].LATITUDE + ") to (" + FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Cust")}[0].LATITUDE + ") - "
                }
                if (FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Cust")}[0].LONGITUDE != FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Cust")}[0].LONGITUDE) {
                    strChanges += "GeoCode Type (Customer Waiting Area) Longitude changed from (" + FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Cust")}[0].LONGITUDE + ") to (" + FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Cust")}[0].LONGITUDE + ") - "
                }
            }
            strChanges = strChanges.removeSuffix(" - ")
            strChanges += ")"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.v("GeoCode Changes -- ",strChanges)
        return strChanges
    }

    fun getPhoneChanges(action : Int,rowId: Int) : String {
        var strChanges = ""
        try {
            if (action == 0) {
                strChanges += "New entry added as Phone Number (" + binding.newPhoneNoText.text.toString() + ") and type (" + binding.newPhoneTypeSpinner.selectedItem.toString() + ")"
            } else {
                if (binding.newChangesPhoneNoText.text.toString() != FacilityDataModelOrg.getInstance().tblPhone[rowId].PhoneNumber) {
                    strChanges += "Phone Number  changed from (" + FacilityDataModelOrg.getInstance().tblPhone[rowId].PhoneNumber + ") to (" + binding.newChangesPhoneNoText.text.toString() + ") - "
                }
            }
            strChanges = strChanges.removeSuffix(" - ")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strChanges
    }





    private fun showLocationDialog(index: Int) {
        binding.alphaBackgroundForDialogs.visibility = View.VISIBLE
        binding.editLocationDialog.visibility = View.VISIBLE
        (activity as FormsActivity).overrideBackButton = true

        binding.newLocLatText.setText(FacilityDataModel.getInstance().tblAddress[index].LATITUDE)
        binding.newLocLongText.setText(FacilityDataModel.getInstance().tblAddress[index].LONGITUDE)

        binding.locationSubmitButton.setOnClickListener {
            if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                if (getAddressChanges().isNullOrEmpty()) {
                binding.contactInfoLoadingView.visibility = View.GONE
                binding.editLocationDialog.visibility = View.GONE
                binding.alphaBackgroundForDialogs.visibility = View.GONE
                binding.contactInfoLoadingText.text = "Loading ..."
                (activity as FormsActivity).overrideBackButton = false
            } else {
                binding.contactInfoLoadingText.text = "Saving ..."
                binding.contactInfoLoadingView.visibility = View.VISIBLE
                binding.editLocationDialog.visibility = View.GONE
                binding.alphaBackgroundForDialogs.visibility = View.GONE
                (activity as FormsActivity).overrideBackButton = false
//            enableAllAddButnsAndDialog()
//            var rowIndex=phoneTbl.indexOfChild(tableRow)
//            var phoneFacilityChangedIndex= rowIndex-1


                val insertDate = Date().toApiSubmitFormat()
                val insertBy = ApplicationPrefs.getInstance(activity).loggedInUserID
                val updateDate = Date().toApiSubmitFormat()
                val updateBy = ApplicationPrefs.getInstance(activity).loggedInUserID
                val LocationTypeID = TypeTablesModel.getInstance().LocationType.filter { s -> s.LocTypeName.equals("Physical") }[0].LocTypeID
                val facAddr1 = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].FAC_Addr1
                val facAddr2 = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].FAC_Addr2
                val facCity = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].CITY
                val facCountry = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].County
                val facST = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].ST
                val facZip = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].ZIP
                val facZip4 = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].ZIP4
                var facBranchName = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].BranchName
                facBranchName = URLEncoder.encode(facBranchName.toString() , "UTF-8");
                val facBranchNo = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].BranchNumber
                val Latitude = binding.newLocLatText.text.toString()
                val Longitude = binding.newLocLongText.text.toString()

                val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()

                val clubCode = FacilityDataModel.getInstance().clubCode
                var urlString = facilityNo + "&clubcode=" + clubCode + "&BranchName=" + facBranchName + "&LATITUDE=" + Latitude + "&LONGITUDE=" + Longitude + "&BranchNumber=" + facBranchNo + "&locationTypeID=" + LocationTypeID + "&FAC_Addr1=" + facAddr1 + "&FAC_Addr2=" + facAddr2 + "&CITY=" + facCity + "&Country=" + facCountry + "&ST=" + facST + "&ZIP=" + facZip + "&ZIP4=" + facZip4 + "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&active=1&geocodeTypeID=3"
                Log.v("Location Address --- ", Constants.submitContactInfoAddress + urlString)
                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitContactInfoAddress + urlString + Utility.getLoggingParameters(activity, 0, getAddressChanges()),
                    { response ->
                        requireActivity().runOnUiThread {
                            if (response.toString().contains("returnCode>0<", false)) {
                                Utility.showSubmitAlertDialog(activity, true, "Facility Location")
                                FacilityDataModel.getInstance().tblAddress[index].LATITUDE = binding.newLocLatText.text.toString()
                                FacilityDataModel.getInstance().tblAddress[index].LONGITUDE = binding.newLocLongText.text.toString()
                                FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeoCodeTypeID==3 }[0].LATITUDE = binding.newLocLatText.text.toString()
                                FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeoCodeTypeID==3 }[0].LONGITUDE = binding.newLocLongText.text.toString()
                                FacilityDataModelOrg.getInstance().tblAddress[index].LATITUDE = binding.newLocLatText.text.toString()
                                FacilityDataModelOrg.getInstance().tblAddress[index].LONGITUDE = binding.newLocLongText.text.toString()
                                fillLocationTableView()
                                fillGeoCodesTable()
                                (activity as FormsActivity).saveDone = true
                                HasChangedModel.getInstance().groupFacilityContactInfo[0].FacilityAddress = true
                                HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                            } else {
                                var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
                                Utility.showSubmitAlertDialog(activity, false, "Facility Location (Error: " + errorMessage + " )")
                            }
                            binding.contactInfoLoadingView.visibility = View.GONE
                            binding.contactInfoLoadingText.text = "Loading ..."
                            //                            enableAllAddButnsAndDialog()
                        }
                    },
                    {

                Utility.showSubmitAlertDialog(activity, true, "Facility Location (Error: " + it.message + " )")
                    binding.contactInfoLoadingView.visibility = View.GONE
                    binding.contactInfoLoadingText.text = "Loading ..."
//                        enableAllAddButnsAndDialog()
                Log.v("error while submitting", "LOCATION Details")
            }))
            }
            } else {
                Utility.showInternetWarningDialog(requireContext(),(requireActivity() as FormsActivity).networkStatusErrorMsg)
            }
        }

    }


    private fun updateGeoCode(geoCodeTypeID: Int){
        val insertDate = Date().toApiSubmitFormat()
        val insertBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val updateDate = Date().toApiSubmitFormat()
        val updateBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val LocationTypeID = TypeTablesModel.getInstance().LocationType.filter { s -> s.LocTypeName.equals("Physical") }[0].LocTypeID
        val facAddr1 = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].FAC_Addr1
        val facAddr2 = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].FAC_Addr2
        val facCity = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].CITY
        val facCountry = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].County
        val facST = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].ST
        val facZip = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].ZIP
        val facZip4 = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].ZIP4
        var facBranchName = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].BranchName
        facBranchName = URLEncoder.encode(facBranchName.toString() , "UTF-8");
        val facBranchNo = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].BranchNumber
        var Latitude = ""
        var Longitude = ""
        if (geoCodeTypeID==3) {
            Latitude = binding.editGeo1Lat.text.toString()
            Longitude = binding.editGeo1Long.text.toString()
        } else if (geoCodeTypeID==1) {
            Latitude = binding.editGeo2Lat.text.toString()
            Longitude = binding.editGeo2Long.text.toString()
        } else {
            Latitude = binding.editGeo3Lat.text.toString()
            Longitude = binding.editGeo3Long.text.toString()
        }

        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()

        val clubCode = FacilityDataModel.getInstance().clubCode
        var urlString = facilityNo + "&clubcode=" + clubCode + "&BranchName=" + facBranchName + "&LATITUDE=" + Latitude + "&LONGITUDE=" + Longitude + "&BranchNumber=" + facBranchNo + "&locationTypeID=" + LocationTypeID + "&FAC_Addr1=" + facAddr1 + "&FAC_Addr2=" + facAddr2 + "&CITY=" + facCity + "&Country=" + facCountry + "&ST=" + facST + "&ZIP=" + facZip + "&ZIP4=" + facZip4 + "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&active=1&geocodeTypeID="+geoCodeTypeID.toString()
        Log.v("Location GeoCodes --- ", Constants.submitContactInfoAddress + urlString)
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitContactInfoAddress + urlString + Utility.getLoggingParameters(activity, 0, getAddressChanges()),
            { response ->
                requireActivity().runOnUiThread {
                    if (response.toString().contains("returnCode>0<", false)) {
                        Utility.showSubmitAlertDialog(activity, true, "Facility GeoCodes")
                        if (geoCodeTypeID==3) {
                            FacilityDataModel.getInstance().tblAddress.filter { s->s.LocationTypeID.equals("1") }[0].LATITUDE = Latitude
                            FacilityDataModel.getInstance().tblAddress.filter { s->s.LocationTypeID.equals("1") }[0].LONGITUDE = Longitude
                            FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeoCodeTypeID==geoCodeTypeID }[0].LATITUDE = Latitude
                            FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeoCodeTypeID==geoCodeTypeID }[0].LONGITUDE = Longitude
                        }
                        fillLocationTableView()
                        FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeoCodeTypeID==geoCodeTypeID }[0].LATITUDE = Latitude
                        FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeoCodeTypeID==geoCodeTypeID }[0].LONGITUDE = Longitude
                        FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeoCodeTypeID==geoCodeTypeID }[0].LATITUDE = Latitude
                        FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeoCodeTypeID==geoCodeTypeID }[0].LONGITUDE = Longitude
//                            fillGeoCodesTable()
                        (activity as FormsActivity).saveDone = true
                        HasChangedModel.getInstance().groupFacilityContactInfo[0].FacilityAddress = true
                        HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    } else {
                        var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
                        Utility.showSubmitAlertDialog(activity, false, "Facility Location (Error: " + errorMessage + " )")
                    }
                    binding.contactInfoLoadingView.visibility = View.GONE
                    binding.contactInfoLoadingText.text = "Loading ..."
                    //                            enableAllAddButnsAndDialog()
                }
            },
            {

        Utility.showSubmitAlertDialog(activity, true, "Facility GeoCodes (Error: " + it.message + " )")
            binding.contactInfoLoadingView.visibility = View.GONE
            binding.contactInfoLoadingText.text = "Loading ..."
//                        enableAllAddButnsAndDialog()
        Log.v("error while submitting", "GeoCode Details")
    }))
    }
    private fun getLocationTypeName(typeID: String): String {
        var typeName = ""
        for (fac in TypeTablesModel.getInstance().LocationType) {
            if (fac.LocTypeID.equals(typeID)) {
                typeName= fac.LocTypeName
            }
        }
        return typeName
    }

    private fun getPhoneTypeName(typeID: String): String {
        var typeName = ""
        for (fac in TypeTablesModel.getInstance().LocationPhoneType) {
            if (fac.LocPhoneID.equals(typeID)) {
                typeName= fac.LocPhoneName
            }
        }
        return typeName
    }

    private fun getEmailTypeName(typeID: String): String {
        var typeName = ""
        for (fac in TypeTablesModel.getInstance().EmailType) {
            if (fac.EmailID.equals(typeID)) {
                typeName= fac.EmailName
            }
        }
        return typeName
    }

    private fun showPhoneDialog() {
        binding.alphaBackgroundForDialogs.visibility = View.VISIBLE
        binding.addNewPhoneDialog.visibility = View.VISIBLE

        phoneTypeList = TypeTablesModel.getInstance().LocationPhoneType
        phoneTypeArray.clear()
        for (fac in phoneTypeList) {
            phoneTypeArray.add(fac.LocPhoneName)
        }

        var phoneTypeAdapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, phoneTypeArray)
        phoneTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.newPhoneTypeSpinner.adapter = phoneTypeAdapter
    }

    private fun showEmailDialog() {
        binding.alphaBackgroundForDialogs.visibility = View.VISIBLE
        binding.addNewEmailDialog.visibility = View.VISIBLE
        emailTypeList = TypeTablesModel.getInstance().EmailType
        emailTypeArray.clear()
        for (fac in emailTypeList) {
            emailTypeArray.add(fac.EmailName)
        }

        var emailTypeAdapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, emailTypeArray)
        emailTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.newEmailTypeSpinner.adapter = emailTypeAdapter
    }

    fun prepareLocationPage(){
        setLocations()
    }

    private fun setLocations() {

        var citiesAdapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, states)
        citiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        newStateSpinner.adapter = citiesAdapter


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

    fun updateDialogs() {
        if (binding.editEmailDialog != null) binding.editEmailDialog.visibility = View.GONE
        if (binding.addNewPhoneDialog != null) binding.addNewPhoneDialog.visibility = View.GONE
        if (binding.editLocationDialog != null) binding.editLocationDialog.visibility = View.GONE
        if (binding.editPhoneDialog != null) binding.editPhoneDialog.visibility = View.GONE
        if (binding.addNewEmailDialog != null) binding.addNewEmailDialog.visibility = View.GONE
        if (binding.alphaBackgroundForDialogs != null) binding.alphaBackgroundForDialogs.visibility = View.GONE
        if (binding.copyHoursDialog != null) binding.copyHoursDialog.visibility = View.GONE
    }


    fun validateInputs(): Boolean {
        var isInputsValid = true

//        binding.phyloc1addr1latitude.setError(null)
//        phyloc1addr2latitude.setError(null)
//        phyloc1addr2longitude.setError(null)
//        binding.phyloc1addr1longitude.setError(null)
//        stateTextView.setError(null)


//        if (binding.phyloc1addr1latitude.text.toString().isNullOrEmpty()) {
//            isInputsValid = false
//            binding.phyloc1addr1latitude.setError("Required Field")
//        }

        if (binding.nightDropCheck.isChecked && binding.nightDropInstText.text.isNullOrEmpty()){
            isInputsValid = false
            binding.nightDropInstText.setError("Required Field")
        }

//        if (newStateSpinner.selectedItem.toString().contains("select")){
//
//            isInputsValid = false
//            stateTextView.setError("required field")
//
//
//        }

//        if(loc1addr2latitude.text.toString().isNullOrEmpty()) {
//            isInputsValid=false
//            loc1addr2latitude.setError("Required Field")
//        }

//        if (binding.phyloc1addr1longitude.text.toString().isNullOrEmpty()) {
//            isInputsValid = false
//            binding.phyloc1addr1longitude.setError("Required Field")
//        }

//        if(loc1addr2longitude.text.toString().isNullOrEmpty()) {
//            isInputsValid=false
//            loc1addr2longitude.setError("Required Field")
//        }


        return isInputsValid
    }

    fun validateGeoCodesInputs(): String {
        var isInputsValid = true
        var returnMsg = ""
        binding.editGeo1Lat.setError(null)
        binding.editGeo2Lat.setError(null)
        binding.editGeo3Lat.setError(null)
        binding.editGeo1Long.setError(null)
        binding.editGeo2Long.setError(null)
        binding.editGeo3Long.setError(null)
        if (binding.editGeo1Lat.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            binding.editGeo1Lat.setError("Required Field")
            returnMsg = "Map & Driving Directions Latitude is required"
        }

        if (binding.editGeo1Long.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            binding.editGeo1Long.setError("Required Field")
            if (returnMsg.equals("")) {
                returnMsg = "Map & Driving Directions Longitude is required"
            } else {
                returnMsg += "\nMap & Driving Directions Longitude is required"
            }
        }

        if (binding.editGeo2Lat.text.toString().isNullOrEmpty() && !binding.editGeo2Long.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            binding.editGeo2Lat.setError("Required Field")
            if (returnMsg.equals("")) {
                returnMsg = "Tow Truck Drop-Off Latitude is required"
            } else {
                returnMsg += "\nTow Truck Drop-Off Latitude is required"
            }
        }

        if (!binding.editGeo2Lat.text.toString().isNullOrEmpty() && binding.editGeo2Long.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            binding.editGeo2Long.setError("Required Field")
            if (returnMsg.equals("")) {
                returnMsg = "Tow Truck Drop-Off Longitude is required"
            } else {
                returnMsg += "\nTow Truck Drop-Off Longitude is required"
            }
        }

        if (binding.editGeo3Lat.text.toString().isNullOrEmpty() && !binding.editGeo3Long.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            binding.editGeo3Lat.setError("Required Field")
            if (returnMsg.equals("")) {
                returnMsg = "Customer Waiting Area Latitude is required"
            } else {
                returnMsg += "\nCustomer Waiting Area Latitude is required"
            }
        }

        if (!binding.editGeo3Lat.text.toString().isNullOrEmpty() && binding.editGeo3Long.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            binding.editGeo3Long.setError("Required Field")
            if (returnMsg.equals("")) {
                returnMsg = "Customer Waiting Area Longitude is required"
            } else {
                returnMsg += "\nCustomer Waiting Area Longitude is required"
            }
        }
        return returnMsg
    }

    fun fillPhoneTableView() {
//        val rowLayoutParam = TableRow.LayoutParams()
//        rowLayoutParam.weight = 1F
//        rowLayoutParam.column = 0
//      //  rowLayoutParam.height = TableLayout.LayoutParams.WRAP_CONTENT

        if (binding.phoneTbl.childCount>1) {
            for (i in binding.phoneTbl.childCount - 1 downTo 1) {
                binding.phoneTbl.removeViewAt(i)
            }
        }

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.leftMargin=10
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0
        rowLayoutParam.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0
        rowLayoutParam1.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 0
        rowLayoutParam2.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableLayout.LayoutParams.WRAP_CONTENT


        FacilityDataModel.getInstance().tblPhone.apply {
            (0 until size).forEach {
                if (!get(it).PhoneID.equals("-1")) {
                    var tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow
                    tableRow.minimumHeight = 30

                    var textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
//                    textView.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 14f
                    textView.setTextColor(Color.BLACK)
                    textView.minimumHeight=30
                    textView.text = getPhoneTypeName(get(it).PhoneTypeID)
                    tableRow.addView(textView)

                    val textView2 = TextView(context)
                    textView2.layoutParams = rowLayoutParam1
                    textView2.gravity = Gravity.CENTER_VERTICAL
                    textView2.textSize = 14f
                    textView2.setTextColor(Color.BLACK)
                    textView2.minimumHeight=30
                    textView2.text = PhoneNumberUtils.formatNumber(get(it).PhoneNumber,"US")
                    tableRow.addView(textView2)

                    val editPhoneBtn = TextView(context)
                    editPhoneBtn.layoutParams = rowLayoutParam1
                    editPhoneBtn.setTextColor(Color.BLUE)
                    editPhoneBtn.text = "EDIT"
                    editPhoneBtn.textSize = 14f
                    editPhoneBtn.minimumHeight=30
                    editPhoneBtn.gravity = Gravity.CENTER
                    editPhoneBtn.setBackgroundColor(Color.TRANSPARENT)
                    tableRow.addView(editPhoneBtn)

                    editPhoneBtn.setOnClickListener {
                        if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                            var rowIndex = binding.phoneTbl.indexOfChild(tableRow)
                            var phoneFacilityChangedIndex = rowIndex - 1
                            binding.newChangesPhoneNoText.text.clear()
                            binding.alphaBackgroundForDialogs.visibility = View.VISIBLE
                            binding.editPhoneDialog.visibility = View.VISIBLE
                            (activity as FormsActivity).overrideBackButton = true
                            phoneTypeList = TypeTablesModel.getInstance().LocationPhoneType
                            phoneTypeArray.clear()
                            for (fac in phoneTypeList) {
                                phoneTypeArray.add(fac.LocPhoneName)
                            }


                            var phoneTypeAdapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, phoneTypeArray)
                            phoneTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            binding.newPhoneTypeSpinner.adapter = phoneTypeAdapter

                            binding.newChangesPhoneNoText.setText(FacilityDataModel.getInstance().tblPhone[phoneFacilityChangedIndex].PhoneNumber)

                            binding.phoneSaveChangesButton.setOnClickListener {
                                var phoneTypeID = ""
                                if (binding.newChangesPhoneNoText.text.isNullOrEmpty()) {
                                    binding.newChangesPhoneNoText.setError("please enter required field")
                                } else {
                                    val phoneNo = binding.newChangesPhoneNoText.text.toString()
                                    for (phoneTypeTableId in TypeTablesModel.getInstance().LocationPhoneType) {
                                        if (phoneTypeTableId.LocPhoneName == textView.text.toString()) {
                                            phoneTypeID = phoneTypeTableId.LocPhoneID.toString()
                                        }
                                    }
                                    val insertDate = Date().toApiSubmitFormat()
                                    val insertBy = ApplicationPrefs.getInstance(activity).loggedInUserID
                                    val updateDate = Date().toApiSubmitFormat()
                                    val updateBy = ApplicationPrefs.getInstance(activity).loggedInUserID
                                    val activeVal = "0"
                                    val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
                                    val clubCode = FacilityDataModel.getInstance().clubCode
                                    var urlString = facilityNo + "&clubCode=" + clubCode + "&phoneTypeId=" + phoneTypeID + "&phoneNumber=" + phoneNo + "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&extension=&description=&phoneId=${FacilityDataModel.getInstance().tblPhone[phoneFacilityChangedIndex].PhoneID}&active=1"
                                    Log.v("Data To Submit", urlString)
                                    binding.contactInfoLoadingText.text = "Saving ..."
                                    binding.contactInfoLoadingView.visibility = View.VISIBLE
                                    binding.editPhoneDialog.visibility = View.GONE
                                    binding.alphaBackgroundForDialogs.visibility = View.GONE
                                    (activity as FormsActivity).overrideBackButton = false
                                    Log.v("Phone Edit --- ",Constants.submitFacilityPhone + urlString)
                                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitFacilityPhone + urlString+ Utility.getLoggingParameters(activity, 0, getPhoneChanges(1,phoneFacilityChangedIndex)),
                                        { response ->
                                            requireActivity().runOnUiThread {
                                                if (response.toString().contains("returnCode>0<", false)) {
                                                    HasChangedModel.getInstance().updateChangedData("Location and Contact Information","Facility Phones","",getPhoneChanges(1,phoneFacilityChangedIndex))
                                                    Utility.showSubmitAlertDialog(activity, true, "Facility Phone")
                                                    FacilityDataModel.getInstance().tblPhone[phoneFacilityChangedIndex].PhoneNumber = binding.newChangesPhoneNoText.text.toString()
                                                    FacilityDataModelOrg.getInstance().tblPhone[phoneFacilityChangedIndex].PhoneNumber = binding.newChangesPhoneNoText.text.toString()
                                                    fillPhoneTableView()
                                                    (activity as FormsActivity).saveDone = true
                                                    checkIfChangeDone("PHONE")
                                                } else {
                                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                                                    Utility.showSubmitAlertDialog(activity, false, "Facility Phone (Error: "+errorMessage+" )")
                                                }
                                                binding.contactInfoLoadingView.visibility = View.GONE
                                                binding.contactInfoLoadingText.text = "Loading ..."
                                            }
                                        },
                                        {
                                        binding.contactInfoLoadingView.visibility = View.GONE
                                        binding.contactInfoLoadingText.text = "Loading ..."
                                    Utility.showSubmitAlertDialog(activity, false, "Facility Phone (Error: "+it.message+" )")
                                    Log.v("error while submitting", "Phone Details")
                                }))


                                }
                            }
                        } else {
                            Utility.showInternetWarningDialog(requireContext(),(requireActivity() as FormsActivity).networkStatusErrorMsg)
                        }
                    }
                    binding.phoneTbl.addView(tableRow)
                }
            }
        }
        altPhoneTableRow(2)
    }

    fun fillEmailTableView() {
        if (binding.emailTbl.childCount>1) {
            for (i in binding.emailTbl.childCount - 1 downTo 1) {
                binding.emailTbl.removeViewAt(i)
            }
        }

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.marginStart =10
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.gravity = Gravity.CENTER_VERTICAL

        FacilityDataModel.getInstance().tblFacilityEmail.apply {
            (0 until size).forEach {
                if (!get(it).emailID.equals("-1")) {
                    var tableRow = TableRow(context)

                    var textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 14f
                    textView.setTextColor(Color.BLACK)
                    textView.minimumHeight = 30
                    textView.text = getEmailTypeName(get(it).emailTypeId)
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam1
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.text = get(it).email
                    textView.minimumHeight = 30
                    textView.textSize = 14f
                    textView.setTextColor(Color.BLACK)
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam1
                    textView.setTextColor(Color.BLUE)
                    textView.text = "EDIT"
                    textView.textSize = 14f
                    textView.minimumHeight = 30
                    textView.gravity = Gravity.CENTER
                    textView.setBackgroundColor(Color.TRANSPARENT)


                    textView.setOnClickListener {
                        var rowIndex = binding.emailTbl.indexOfChild(tableRow)
                        var emailFacilityChangedIndex = rowIndex - 1
                        binding.newChangesEmailText.text.clear()
                        binding.alphaBackgroundForDialogs.visibility = View.VISIBLE
                        (activity as FormsActivity).overrideBackButton = true
                        binding.editEmailDialog.visibility = View.VISIBLE
                        emailTypeList = TypeTablesModel.getInstance().EmailType
                        emailTypeArray.clear()
                        for (fac in emailTypeList) {
                            emailTypeArray.add(fac.EmailName)
                        }

                        var emailTypeAdapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, emailTypeArray)
                        emailTypeAdapter .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        binding.newEmailTypeSpinner.adapter = emailTypeAdapter

                        binding.newChangesEmailText.setText(FacilityDataModel.getInstance().tblFacilityEmail[emailFacilityChangedIndex].email)

                        binding.emailSaveChangesButton.setOnClickListener {
                            if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                                var emailTypeID = ""
                                if (binding.newChangesEmailText.text.isNullOrEmpty()) {
                                    binding.newChangesEmailText.setError("please enter required field")
                                } else if (!Utility.isEmailValid(binding.newChangesEmailText.text.toString())) {
                                    Utility.showValidationAlertDialog(activity,"Please enter a valid Email address")
                                } else {
                                    val emailAddress = binding.newChangesEmailText.text.toString()
                                    for (emailTypeTableId in TypeTablesModel.getInstance().EmailType) {
                                        if (emailTypeTableId.EmailName == textView.text.toString()) {
                                            emailTypeID = emailTypeTableId.EmailID.toString()
                                        }
                                    }
                                    val insertDate = Date().toApiSubmitFormat()
                                    val insertBy = "sa"
                                    val updateDate = Date().toApiSubmitFormat()
                                    val updateBy = "sa"
                                    val activeVal = "0"

                                    val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()

                                    val clubCode = FacilityDataModel.getInstance().clubCode
                                    var urlString = facilityNo + "&clubcode=" + clubCode + "&emailTypeId=" + FacilityDataModel.getInstance().tblFacilityEmail[emailFacilityChangedIndex].emailTypeId + "&email=" + emailAddress+ "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&extension=&description=&emailId=${FacilityDataModel.getInstance().tblFacilityEmail[emailFacilityChangedIndex].emailID}&active=1"
                                    Log.v("Data To Submit", urlString)
                                    binding.contactInfoLoadingText.text = "Saving ..."
                                    binding.contactInfoLoadingView.visibility = View.VISIBLE
                                    binding.editEmailDialog.visibility = View.GONE
                                    binding.alphaBackgroundForDialogs.visibility = View.GONE
                                    (activity as FormsActivity).overrideBackButton = false
                                    Log.v("Email Edit --- ",Constants.submitFacilityEmail + urlString)
                                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitFacilityEmail + urlString+ Utility.getLoggingParameters(activity, 0, getEmailChanges(1,emailFacilityChangedIndex)),
                                        { response ->
                                            requireActivity().runOnUiThread {
                                                if (response.toString().contains("returnCode>0<", false)) {
                                                    HasChangedModel.getInstance().updateChangedData("Location and Contact Information","Facility Emails","",getEmailChanges(1,emailFacilityChangedIndex))
                                                    Utility.showSubmitAlertDialog(activity, true, "Facility Email")
                                                    FacilityDataModel.getInstance().tblFacilityEmail[emailFacilityChangedIndex].email = binding.newChangesEmailText.text.toString()
                                                    FacilityDataModelOrg.getInstance().tblFacilityEmail[emailFacilityChangedIndex].email = binding.newChangesEmailText.text.toString()
                                                    fillEmailTableView()
                                                    checkIfChangeDone("EMAIL")
                                                    (activity as FormsActivity).saveDone = true
                                                    setAlertColoring()
                                                } else {
                                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                                                    Utility.showSubmitAlertDialog(activity, false, "Facility Email (Error: "+errorMessage+" )")
                                                }
                                                binding.contactInfoLoadingView.visibility = View.GONE
                                                binding.contactInfoLoadingText.text = "Loading ..."
                                            }
                                        },
                                        {
                                        binding.contactInfoLoadingView.visibility = View.GONE
                                        binding.contactInfoLoadingText.text = "Loading ..."
                                    Utility.showSubmitAlertDialog(activity, false, "Facility Email (Error: "+it.message+" )")
                                }))


                                }
                            } else {
                            Utility.showInternetWarningDialog(requireContext(),(requireActivity() as FormsActivity).networkStatusErrorMsg)
                            }
                        }
                    }

                    tableRow.addView(textView)
                    binding.emailTbl.addView(tableRow)
                }
            }

        }
        altEmailTableRow(2)

    }

    fun checkIfChangeDone(strType : String)  {
        var changeWasDone = false
        if (strType.equals("EMAIL")){
            for (i in 0 .. FacilityDataModel.getInstance().tblFacilityEmail.size-1) {
                if (!FacilityDataModel.getInstance().tblFacilityEmail[i].email.equals(FacilityDataModelOrg.getInstance().tblFacilityEmail[i].email)) changeWasDone=true
            }
            HasChangedModel.getInstance().groupFacilityContactInfo[0].FacilityEmail = changeWasDone
            HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
        }
        if (strType.equals("PHONE")){
            for (i in 0 .. FacilityDataModel.getInstance().tblPhone.size-1) {
                if (!FacilityDataModel.getInstance().tblPhone[i].PhoneNumber.equals(FacilityDataModelOrg.getInstance().tblPhone[i].PhoneNumber)) changeWasDone=true
            }
            HasChangedModel.getInstance().groupFacilityContactInfo[0].FacilityPhone = changeWasDone
            HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
        }
    }

    fun fillHolidaysTableView() {

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0

        if (binding.holidaysTbl.childCount>1) {
            for (i in binding.holidaysTbl.childCount - 1 downTo 1) {
                binding.holidaysTbl.removeViewAt(i)
            }
        }

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 0

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.width = 0

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParamRow.weight=1F

        PRGDataModel.getInstance().tblPRGFacilityShopHolidayTimes.apply {
            (0 until size).forEach {
                if (!get(it).comments.equals("-1")) {
                    var tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow
                    tableRow.minimumHeight = 30


                    var textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.gravity = Gravity.CENTER
                    textView.minimumHeight = 30
                    textView.textSize = 14f
                    textView.setTextColor(Color.BLACK)
                    textView.text = get(it).type
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam1
                    textView.gravity = Gravity.CENTER
                    textView.minimumHeight = 30
                    textView.textSize = 14f
                    textView.setTextColor(Color.BLACK)
                    textView.text = if (get(it).startdate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).startdate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam2
                    textView.gravity = Gravity.CENTER
                    textView.minimumHeight = 30
                    textView.textSize = 14f
                    textView.setTextColor(Color.BLACK)
                    textView.text = if (get(it).enddate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).enddate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam3
                    textView.gravity = Gravity.CENTER
                    textView.minimumHeight = 30
                    textView.textSize = 14f
                    textView.setTextColor(Color.BLACK)
                    textView.text = get(it).comments
                    tableRow.addView(textView)

                    binding.holidaysTbl.addView(tableRow)
                }
            }
        }
        altHolidayTableRow(2)

    }


    fun fillOpenHoursTableView() {


        FacilityDataModel.getInstance().tblHours.apply {
            (0 until size).forEach {
                binding.sunOpenSpinner.setSelection(hoursArray!!.indexOf(if (get(it).SunOpen.isNullOrEmpty()) "Closed" else get(it).SunOpen))
                binding.sunOpenSpinner.tag = binding.sunOpenSpinner.selectedItemPosition
                binding.monOpenSpinner.setSelection(hoursArray!!.indexOf(if (get(it).MonOpen.isNullOrEmpty()) "Closed" else get(it).MonOpen))
                binding.monOpenSpinner.tag = binding.monOpenSpinner.selectedItemPosition
                binding.tueOpenSpinner.setSelection(hoursArray!!.indexOf(if (get(it).TueOpen.isNullOrEmpty()) "Closed" else get(it).TueOpen))
                binding.tueOpenSpinner.tag = binding.tueOpenSpinner.selectedItemPosition
                binding.wedOpenSpinner.setSelection(hoursArray!!.indexOf(if (get(it).WedOpen.isNullOrEmpty()) "Closed" else get(it).WedOpen))
                binding.wedOpenSpinner.tag = binding.wedOpenSpinner.selectedItemPosition
                binding.thuOpenSpinner.setSelection(hoursArray!!.indexOf(if (get(it).ThuOpen.isNullOrEmpty()) "Closed" else get(it).ThuOpen))
                binding.thuOpenSpinner.tag = binding.thuOpenSpinner.selectedItemPosition
                binding.friOpenSpinner.setSelection(hoursArray!!.indexOf(if (get(it).FriOpen.isNullOrEmpty()) "Closed" else get(it).FriOpen))
                binding.friOpenSpinner.tag = binding.friOpenSpinner.selectedItemPosition
                binding.satOpenSpinner.setSelection(hoursArray!!.indexOf(if (get(it).SatOpen.isNullOrEmpty()) "Closed" else get(it).SatOpen))
                binding.satOpenSpinner.tag = binding.satOpenSpinner.selectedItemPosition
                binding.nightDropCheck.isChecked = get(it).NightDrop
                binding.nightDropInstText.setText(get(it).NightDropInstr)

            }
        }
    }
    fun fillClosedHoursTableView() {

        FacilityDataModel.getInstance().tblHours.apply {
            (0 until size).forEach {
                binding.sunCloseSpinner.setSelection(hoursArray!!.indexOf(if (get(it).SunClose.isNullOrEmpty()) "Closed" else get(it).SunClose))
                binding.sunCloseSpinner.tag = binding.sunCloseSpinner.selectedItemPosition
                binding.monCloseSpinner.setSelection(hoursArray!!.indexOf(if (get(it).MonClose.isNullOrEmpty()) "Closed" else get(it).MonClose))
                binding.monCloseSpinner.tag = binding.monCloseSpinner.selectedItemPosition
                binding.tueCloseSpinner.setSelection(hoursArray!!.indexOf(if (get(it).TueClose.isNullOrEmpty()) "Closed" else get(it).TueClose))
                binding.tueCloseSpinner.tag = binding.tueCloseSpinner.selectedItemPosition
                binding.wedCloseSpinner.setSelection(hoursArray!!.indexOf(if (get(it).WedClose.isNullOrEmpty()) "Closed" else get(it).WedClose))
                binding.wedCloseSpinner.tag = binding.wedCloseSpinner.selectedItemPosition
                binding.thuCloseSpinner.setSelection(hoursArray!!.indexOf(if (get(it).ThuClose.isNullOrEmpty()) "Closed" else get(it).ThuClose))
                binding.thuCloseSpinner.tag = binding.thuCloseSpinner.selectedItemPosition
                binding.friCloseSpinner.setSelection(hoursArray!!.indexOf(if (get(it).FriClose.isNullOrEmpty()) "Closed" else get(it).FriClose))
                binding.friCloseSpinner.tag = binding.friCloseSpinner.selectedItemPosition
                binding.satCloseSpinner.setSelection(hoursArray!!.indexOf(if (get(it).SatClose.isNullOrEmpty()) "Closed" else get(it).SatClose))
                binding.satCloseSpinner.tag = binding.satCloseSpinner.selectedItemPosition


            }
        }
    }


    fun fillLocationTableView() {

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 0.8F
        rowLayoutParam.column = 0
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0
        rowLayoutParam.leftMargin = 2
        rowLayoutParam.gravity = Gravity.CENTER

        if (binding.locationTbl.childCount>1) {
            for (i in binding.locationTbl.childCount - 1 downTo 1) {
                binding.locationTbl.removeViewAt(i)
            }
        }

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1.4F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0
        rowLayoutParam1.gravity = Gravity.CENTER

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1.4F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 0
        rowLayoutParam2.gravity = Gravity.CENTER

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.width = 0
        rowLayoutParam3.gravity = Gravity.CENTER

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.width = 0
        rowLayoutParam4.gravity = Gravity.CENTER

        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 0.8F
        rowLayoutParam5.column = 5
        rowLayoutParam5.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam5.width = 0
        rowLayoutParam5.gravity = Gravity.CENTER

        val rowLayoutParam6 = TableRow.LayoutParams()
        rowLayoutParam6.weight = 1F
        rowLayoutParam6.column = 6
        rowLayoutParam6.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam6.width = 0
        rowLayoutParam6.gravity = Gravity.CENTER

        val rowLayoutParam7 = TableRow.LayoutParams()
        rowLayoutParam7.weight = 1F
        rowLayoutParam7.column = 7
        rowLayoutParam7.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam7.width = 0
        rowLayoutParam7.gravity = Gravity.CENTER

        val rowLayoutParam8 = TableRow.LayoutParams()
        rowLayoutParam8.weight = 1F
        rowLayoutParam8.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam8.column = 8
        rowLayoutParam8.width = 0
        rowLayoutParam8.gravity = Gravity.CENTER

        val rowLayoutParam9 = TableRow.LayoutParams()
        rowLayoutParam9.weight = 1F
        rowLayoutParam9.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam9.column = 9
        rowLayoutParam9.width = 0
        rowLayoutParam9.gravity = Gravity.CENTER

        val rowLayoutParam10 = TableRow.LayoutParams()
        rowLayoutParam10.weight = 1F
        rowLayoutParam10.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam10.column = 10
        rowLayoutParam10.width = 0
        rowLayoutParam10.gravity = Gravity.CENTER

//        val rowLayoutParam11 = TableRow.LayoutParams()
//        rowLayoutParam11.weight = 0.8F
//        rowLayoutParam11.height = TableRow.LayoutParams.WRAP_CONTENT
//        rowLayoutParam11.column = 11
//        rowLayoutParam11.width = 0
//        rowLayoutParam11.gravity = Gravity.CENTER

        var dateTobeFormated = ""

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParamRow.weight=1F


        FacilityDataModel.getInstance().tblAddress.apply {
            (0 until size).forEach {
                var tableRow = TableRow(context)
                tableRow.layoutParams = rowLayoutParamRow
                tableRow.minimumHeight = 30


                var textView = TextView(context)
                textView.layoutParams = rowLayoutParam
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.minimumHeight=30
                textView.text = getLocationTypeName(get(it).LocationTypeID)
                textView.textSize = 12f
                textView.setTextColor(Color.BLACK)
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam1
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.minimumHeight=30
                textView.setEms(8)
                textView.text = get(it).FAC_Addr1
                textView.textSize = 12f
                textView.setTextColor(Color.BLACK)
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.minimumHeight=30
                textView.setEms(8)
                textView.textSize = 12f
                textView.setTextColor(Color.BLACK)
                textView.text = get(it).FAC_Addr2
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam3
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.minimumHeight=30
                textView.text = get(it).CITY
                textView.textSize = 12f
                textView.setTextColor(Color.BLACK)
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam4
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.text = get(it).County
                textView.minimumHeight=30
                textView.textSize = 12f
                textView.setTextColor(Color.BLACK)
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam5
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.text = get(it).ST
                textView.minimumHeight=30
                textView.textSize = 12f
                textView.setTextColor(Color.BLACK)
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam6
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.text = get(it).ZIP + "-" + get(it).ZIP4
                textView.minimumHeight=30
                textView.textSize = 12f
                textView.setTextColor(Color.BLACK)
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam7
                textView.gravity = Gravity.CENTER_VERTICAL
                if (!getLocationTypeName(get(it).LocationTypeID).equals("Physical")) {
                    textView.text = ""
                } else {
                    textView.text = get(it).LATITUDE
                }
                textView.minimumHeight=30
                textView.textSize = 12f
                textView.setTextColor(Color.BLACK)
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam8
                textView.gravity = Gravity.CENTER_VERTICAL
                if (!getLocationTypeName(get(it).LocationTypeID).equals("Physical")) {
                    textView.text = ""
                } else {
                    textView.text = get(it).LONGITUDE
                }
                textView.minimumHeight=30
                textView.textSize = 12f
                textView.setTextColor(Color.BLACK)
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam9
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.text = get(it).BranchNumber
                textView.minimumHeight=30
                textView.textSize = 12f
                textView.setTextColor(Color.BLACK)
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam10
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.text = get(it).BranchName
                textView.minimumHeight=30
                textView.textSize = 12f
                textView.setTextColor(Color.BLACK)
                tableRow.addView(textView)

//                var editButton = TextView(context)
//                editButton.layoutParams = rowLayoutParam11
//                editButton.setTextColor(Color.BLUE)
//                editButton.minimumHeight=30
//                editButton.text = "EDIT"
//                editButton.textSize = 12f
//                editButton .gravity = Gravity.CENTER
//                editButton .setBackgroundColor(Color.TRANSPARENT)
//                editButton.tag = it
//
//                tableRow.addView(editButton)
//                if (!getLocationTypeName(get(it).LocationTypeID).equals("Physical")){
//                    editButton.visibility = View.INVISIBLE
//                }
//
//                editButton.setOnClickListener { s ->
//                    showLocationDialog(it)
//                }

                binding.locationTbl.addView(tableRow)

            }
        }


        altLocationTableRow(2)

    }

    fun fillGeoCodesTable () {
        if (!FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Maps")}.isNullOrEmpty()) {
                binding.editGeo1Lat.setText(FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Maps")}[0].LATITUDE)
                binding.editGeo1Long.setText(FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Maps")}[0].LONGITUDE)
        }
        if (!FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Tow")}.isNullOrEmpty()) {
            binding.editGeo2Lat.setText(FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Tow")}[0].LATITUDE)
            binding.editGeo2Long.setText(FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Tow")}[0].LONGITUDE)
        }
        if (!FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Cust")}.isNullOrEmpty()) {
            binding.editGeo3Lat.setText(FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Cust")}[0].LATITUDE)
            binding.editGeo3Long.setText(FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeocodeTypeName.contains("Cust")}[0].LONGITUDE)
        }

        binding.btnUpdate1.setOnClickListener {
            btnToBeUpdated = 1
            captureLocation()
        }
        binding.btnOpen1.setOnClickListener {
            if (binding.editGeo1Lat.text.isNullOrEmpty() || binding.editGeo1Long.text.isNullOrEmpty()) {

            } else {
                val gmmIntentUri =
                        Uri.parse("geo:${binding.editGeo1Lat.text.toString()},${binding.editGeo1Long.text.toString()}?z=16&q=${binding.editGeo1Lat.text.toString()},${binding.editGeo1Long.text.toString()}(Maps &amp; Driving Directions)")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
        }

        binding.btnOpen2.setOnClickListener {
            if (binding.editGeo2Lat.text.isNullOrEmpty() || binding.editGeo2Long.text.isNullOrEmpty()) {

            } else {
                val gmmIntentUri =
                        Uri.parse("geo:${binding.editGeo2Lat.text.toString()},${binding.editGeo2Long.text.toString()}?z=16&q=${binding.editGeo2Lat.text.toString()},${binding.editGeo2Long.text.toString()}(Tow Truck Drop-Off)")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
        }

        binding.btnOpen3.setOnClickListener {
            if (binding.editGeo3Lat.text.isNullOrEmpty() || binding.editGeo3Long.text.isNullOrEmpty()) {

            } else {
                val gmmIntentUri =
                        Uri.parse("geo:${binding.editGeo3Lat.text.toString()},${binding.editGeo3Long.text.toString()}?z=16&q=${binding.editGeo3Lat.text.toString()},${binding.editGeo3Long.text.toString()}(Customer Waiting Area)")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
        }

        binding.btnUpdate2.setOnClickListener {
//            if (editGeo2Long.text.isNullOrEmpty() || editGeo2Lat.text.isNullOrEmpty()){
//                Utility.showValidationAlertDialog(activity, "Please add Latitude and Longitude")
//            } else
//                updateGeoCode(1)
            btnToBeUpdated = 2
            captureLocation()
        }
        binding.btnUpdate3.setOnClickListener {
//            if (editGeo3Long.text.isNullOrEmpty() || editGeo3Lat.text.isNullOrEmpty()){
//                Utility.showValidationAlertDialog(activity, "Please add Latitude and Longitude")
//            } else
//                updateGeoCode(2)
            btnToBeUpdated = 3
            captureLocation()
        }
    }

    fun submitFacilityEmail(){
        val emailTypeID = TypeTablesModel.getInstance().EmailType.filter { s -> s.EmailName==binding.newEmailTypeSpinner.selectedItem.toString()}[0].EmailID
        val email = if (binding.newEmailAddrText.text.isNullOrEmpty())  "" else binding.newEmailAddrText.text
        val insertDate = Date().toApiSubmitFormat()
        val insertBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val updateDate = Date().toApiSubmitFormat()
        val updateBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val activeVal = "0"
        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
        val clubCode = FacilityDataModel.getInstance().clubCode

        val newEmail = TblFacilityEmail()
        newEmail.email = email.toString()
        newEmail.emailTypeId = emailTypeID
//        var seqNo = FacilityDataModel.getInstance().tblFacilityEmail.size+1
        var urlString = facilityNo+"&clubcode="+clubCode+"&emailTypeId="+emailTypeID+"&email="+email+"&insertBy="+insertBy+"&insertDate="+insertDate+"&updateBy="+updateBy+"&updateDate="+updateDate+"&emailId="
        Log.v("Data To Submit", urlString)
        binding.contactInfoLoadingText.text = "Saving ..."
        binding.contactInfoLoadingView.visibility = View.VISIBLE
        binding.addNewEmailDialog.visibility = View.GONE
        binding.alphaBackgroundForDialogs.visibility = View.GONE
        (activity as FormsActivity).overrideBackButton = false
        Log.v("Email ADD --- ",Constants.submitFacilityEmail + urlString)
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitFacilityEmail + urlString + Utility.getLoggingParameters(activity, 0, getEmailChanges(0,0)),
            { response ->
                requireActivity().runOnUiThread {
                    if (response.toString().contains("returnCode>0<",false)) {
                        HasChangedModel.getInstance().updateChangedData("Location and Contact Information","Facility Emails","",getEmailChanges(0,0))
                        Utility.showSubmitAlertDialog(activity, true, "Facility Email")
                        if (FacilityDataModel.getInstance().tblFacilityEmail.size==1 && FacilityDataModel.getInstance().tblFacilityEmail[0].emailID.equals("-1")){
                            FacilityDataModel.getInstance().tblFacilityEmail.removeAt(0)
                            FacilityDataModelOrg.getInstance().tblFacilityEmail.removeAt(0)
                        }
                        newEmail.emailID = response.toString().substring(response.toString().indexOf("<emailID")+8,response.toString().indexOf("</emailID"))
                        FacilityDataModel.getInstance().tblFacilityEmail.add(newEmail)
                        FacilityDataModelOrg.getInstance().tblFacilityEmail.add(newEmail)
                        fillEmailTableView()
                        setAlertColoring()
                        (activity as FormsActivity).saveDone = true
                        HasChangedModel.getInstance().groupFacilityContactInfo[0].FacilityEmail= true
                        HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    } else {
                        var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                        Utility.showSubmitAlertDialog(activity, false, "Facility Email (Error: "+errorMessage+" )")
                    }
                    binding.contactInfoLoadingView.visibility = View.GONE
                    binding.contactInfoLoadingText.text = "Loading ..."

                }
            },
            {
            binding.contactInfoLoadingView.visibility = View.GONE
            binding.contactInfoLoadingText.text = "Loading ..."

        Utility.showSubmitAlertDialog(activity,false,"Facility Email (Error: "+it.message+" )")
    }))
    }


    fun submitGeoCodes(){

        val facID = FacilityDataModel.getInstance().tblFacilities[0].FACID
        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
        val clubCode = FacilityDataModel.getInstance().clubCode
        val LocationTypeID = "1"
        val facAddr1 = URLEncoder.encode(FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].FAC_Addr1,"UTF-8")
        val facAddr2 = URLEncoder.encode(FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].FAC_Addr2,"UTF-8")
        val facCity = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].CITY
        val facCountry = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].County
        val facST = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].ST
        val facZip = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].ZIP
        val facZip4 = ""
        var facBranchName = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].BranchName
        facBranchName = URLEncoder.encode(facBranchName.toString() , "UTF-8");
        val facBranchNo = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].BranchNumber

        val geocodeTypeID_Map = TypeTablesModel.getInstance().GeocodeType.filter { s->s.GeocodeTypeName.contains("Maps")}[0].GeocodeTypeID
        val LATITUDE_Map = binding.editGeo1Lat.text.toString()
        val LONGITUDE_Map = binding.editGeo1Long.text.toString()
        val geocodeTypeID_Tow = TypeTablesModel.getInstance().GeocodeType.filter { s->s.GeocodeTypeName.contains("Tow")}[0].GeocodeTypeID
        var LATITUDE_Tow = ""
        var LONGITUDE_Tow = ""
        val geocodeTypeID_Cst = TypeTablesModel.getInstance().GeocodeType.filter { s->s.GeocodeTypeName.contains("Cust")}[0].GeocodeTypeID
        var LATITUDE_Cst = ""
        var LONGITUDE_Cst = ""
        if (!binding.editGeo2Lat.toString().isNullOrEmpty()) {
            LATITUDE_Tow = binding.editGeo2Lat.text.toString()
            LONGITUDE_Tow = binding.editGeo2Long.text.toString()
        }
        if (!binding.editGeo3Lat.toString().isNullOrEmpty()) {
            LATITUDE_Cst = binding.editGeo3Lat.text.toString()
            LONGITUDE_Cst = binding.editGeo3Long.text.toString()
        }

        Log.v("REQUEST -->", Constants.submitFacilityGeoCodes + "${facID}&facnum=${facilityNo}&clubcode=${clubCode}" +
                "&LocationTypeID_P=${LocationTypeID}&FAC_Addr1_P=${facAddr1}&FAC_Addr2_P=${facAddr2}&CITY_P=${facCity}&ST_P=${facST}&ZIP_P=${facZip}&County_P=${facCountry}&BranchName_P=${facBranchName}&BranchNumber_P=${facBranchNo}" +
                "&geocodeTypeID_Map=${geocodeTypeID_Map}&LATITUDE_Map=${LATITUDE_Map}&LONGITUDE_Map=${LONGITUDE_Map}&geocodeTypeID_Tow=${geocodeTypeID_Tow}&LATITUDE_Tow=${LATITUDE_Tow}&LONGITUDE_Tow=${LONGITUDE_Tow}&geocodeTypeID_Cst=${geocodeTypeID_Cst}&LATITUDE_Cst=${LATITUDE_Cst}&LONGITUDE_Cst=${LONGITUDE_Cst}" +
                "&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat()+
                "&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=${Date().toApiSubmitFormat()}" + Utility.getLoggingParameters(activity, 0, getGeoCodesChanges()))

        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitFacilityGeoCodes + "${facID}&facnum=${facilityNo}&clubcode=${clubCode}" +
                "&LocationTypeID_P=${LocationTypeID}&FAC_Addr1_P=${facAddr1}&FAC_Addr2_P=${facAddr2}&CITY_P=${facCity}&ST_P=${facST}&ZIP_P=${facZip}&County_P=${facCountry}&BranchName_P=${facBranchName}&BranchNumber_P=${facBranchNo}" +
                "&geocodeTypeID_Map=${geocodeTypeID_Map}&LATITUDE_Map=${LATITUDE_Map}&LONGITUDE_Map=${LONGITUDE_Map}&geocodeTypeID_Tow=${geocodeTypeID_Tow}&LATITUDE_Tow=${LATITUDE_Tow}&LONGITUDE_Tow=${LONGITUDE_Tow}&geocodeTypeID_Cst=${geocodeTypeID_Cst}&LATITUDE_Cst=${LATITUDE_Cst}&LONGITUDE_Cst=${LONGITUDE_Cst}" +
                "&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat()+
                "&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=${Date().toApiSubmitFormat()}" + Utility.getLoggingParameters(activity, 0, getGeoCodesChanges()),
            { response ->
                requireActivity().runOnUiThread {
                    if (response.toString().contains("returnCode>0<",false)) {
                        HasChangedModel.getInstance().updateChangedData("Location and Contact Information","Facility GeoCodes","",getGeoCodesChanges())
                        if (FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeoCodeTypeID==geocodeTypeID_Map}.isNotEmpty()) {
                            FacilityDataModel.getInstance().tblGeocodes.filter { s -> s.GeoCodeTypeID == geocodeTypeID_Map }[0].LATITUDE =
                                LATITUDE_Map
                            FacilityDataModel.getInstance().tblGeocodes.filter { s -> s.GeoCodeTypeID == geocodeTypeID_Map }[0].LONGITUDE =
                                LONGITUDE_Map
                        }
                        if (FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeoCodeTypeID==geocodeTypeID_Cst}.isNotEmpty()) {
                            FacilityDataModel.getInstance().tblGeocodes.filter { s -> s.GeoCodeTypeID == geocodeTypeID_Cst }[0].LATITUDE =
                                LATITUDE_Cst
                            FacilityDataModel.getInstance().tblGeocodes.filter { s -> s.GeoCodeTypeID == geocodeTypeID_Cst }[0].LONGITUDE =
                                LONGITUDE_Cst
                        }
                        if (FacilityDataModel.getInstance().tblGeocodes.filter { s->s.GeoCodeTypeID==geocodeTypeID_Tow}.isNotEmpty()) {
                            FacilityDataModel.getInstance().tblGeocodes.filter { s -> s.GeoCodeTypeID == geocodeTypeID_Tow }[0].LATITUDE =
                                LATITUDE_Tow
                            FacilityDataModel.getInstance().tblGeocodes.filter { s -> s.GeoCodeTypeID == geocodeTypeID_Tow }[0].LONGITUDE =
                                LONGITUDE_Tow
                        }
                        if (FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeoCodeTypeID==geocodeTypeID_Map}.isNotEmpty()) {
                            FacilityDataModelOrg.getInstance().tblGeocodes.filter { s -> s.GeoCodeTypeID == geocodeTypeID_Map }[0].LATITUDE =
                                LATITUDE_Map
                            FacilityDataModelOrg.getInstance().tblGeocodes.filter { s -> s.GeoCodeTypeID == geocodeTypeID_Map }[0].LONGITUDE =
                                LONGITUDE_Map
                        }
                        if (FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeoCodeTypeID==geocodeTypeID_Cst}.isNotEmpty()) {
                            FacilityDataModelOrg.getInstance().tblGeocodes.filter { s -> s.GeoCodeTypeID == geocodeTypeID_Cst }[0].LATITUDE =
                                LATITUDE_Cst
                            FacilityDataModelOrg.getInstance().tblGeocodes.filter { s -> s.GeoCodeTypeID == geocodeTypeID_Cst }[0].LONGITUDE =
                                LONGITUDE_Cst
                        }
                        if (FacilityDataModelOrg.getInstance().tblGeocodes.filter { s->s.GeoCodeTypeID==geocodeTypeID_Tow}.isNotEmpty()) {
                            FacilityDataModelOrg.getInstance().tblGeocodes.filter { s -> s.GeoCodeTypeID == geocodeTypeID_Tow }[0].LATITUDE =
                                LATITUDE_Tow
                            FacilityDataModelOrg.getInstance().tblGeocodes.filter { s -> s.GeoCodeTypeID == geocodeTypeID_Tow }[0].LONGITUDE =
                                LONGITUDE_Tow
                        }

                        fillGeoCodesTable()

//                        HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
//                        HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                        (activity as FormsActivity).saveRequired = false
                        saveGeoCodesRequired = false
                        refreshButtonsState()
                        (activity as FormsActivity).saveDone = true
                        Utility.showSubmitAlertDialog(activity, true, "Facility GeoCodes")

                    } else {
                        var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                        Utility.showSubmitAlertDialog(activity, false, "Facility GeoCodes (Error: "+errorMessage+" )")
                    }
                    binding.contactInfoLoadingView.visibility = View.GONE
                    binding.contactInfoLoadingText.text = "Loading ..."
                }
            }, {
                Log.v("error while loading", "error submitting geocodes")
                Utility.showSubmitAlertDialog(activity,false,"Facility GeoCodes (Error: "+it.message+" )")
                binding.contactInfoLoadingView.visibility = View.GONE
                binding.contactInfoLoadingText.text = "Loading ..."
            }))

    }

    fun submitHours(){

        val nightDrop= if (binding.nightDropCheck.isChecked) "1" else "0"
        val nightDropInstructions= URLEncoder.encode(binding.nightDropInstText.text.toString(),"UTF-8")
//        val sunClose = if (sunCloseSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else sunCloseSpinner.selectedItem.toString()
//        val monClose = if (monCloseSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else monCloseSpinner.selectedItem.toString()
//        val tueClose = if (tueCloseSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else tueCloseSpinner.selectedItem.toString()
//        val wedClose = if (wedCloseSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else wedCloseSpinner.selectedItem.toString()
//        val thuClose = if (thuCloseSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else thuCloseSpinner.selectedItem.toString()
//        val friClose = if (friCloseSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else friCloseSpinner.selectedItem.toString()
//        val satClose = if (satCloseSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else satCloseSpinner.selectedItem.toString()
//        val sunOpen = if (sunOpenSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else sunOpenSpinner.selectedItem.toString()
//        val monOpen = if (monOpenSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else monOpenSpinner.selectedItem.toString()
//        val tueOpen = if (tueOpenSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else tueOpenSpinner.selectedItem.toString()
//        val wedOpen = if (wedOpenSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else wedOpenSpinner.selectedItem.toString()
//        val thuOpen = if (thuOpenSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else thuOpenSpinner.selectedItem.toString()
//        val friOpen = if (friOpenSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else friOpenSpinner.selectedItem.toString()
//        val satOpen = if (satOpenSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else satOpenSpinner.selectedItem.toString()
        val sunClose = if (binding.sunCloseSpinner.selectedItem.toString().equals("Closed")) "" else binding.sunCloseSpinner.selectedItem.toString()
        val monClose = if (binding.monCloseSpinner.selectedItem.toString().equals("Closed")) "" else binding.monCloseSpinner.selectedItem.toString()
        val tueClose = if (binding.tueCloseSpinner.selectedItem.toString().equals("Closed")) "" else binding.tueCloseSpinner.selectedItem.toString()
        val wedClose = if (binding.wedCloseSpinner.selectedItem.toString().equals("Closed")) "" else binding.wedCloseSpinner.selectedItem.toString()
        val thuClose = if (binding.thuCloseSpinner.selectedItem.toString().equals("Closed")) "" else binding.thuCloseSpinner.selectedItem.toString()
        val friClose = if (binding.friCloseSpinner.selectedItem.toString().equals("Closed")) "" else binding.friCloseSpinner.selectedItem.toString()
        val satClose = if (binding.satCloseSpinner.selectedItem.toString().equals("Closed")) "" else binding.satCloseSpinner.selectedItem.toString()
        val sunOpen = if (binding.sunOpenSpinner.selectedItem.toString().equals("Closed")) "" else binding.sunOpenSpinner.selectedItem.toString()
        val monOpen = if (binding.monOpenSpinner.selectedItem.toString().equals("Closed")) "" else binding.monOpenSpinner.selectedItem.toString()
        val tueOpen = if (binding.tueOpenSpinner.selectedItem.toString().equals("Closed")) "" else binding.tueOpenSpinner.selectedItem.toString()
        val wedOpen = if (binding.wedOpenSpinner.selectedItem.toString().equals("Closed")) "" else binding.wedOpenSpinner.selectedItem.toString()
        val thuOpen = if (binding.thuOpenSpinner.selectedItem.toString().equals("Closed")) "" else binding.thuOpenSpinner.selectedItem.toString()
        val friOpen = if (binding.friOpenSpinner.selectedItem.toString().equals("Closed")) "" else binding.friOpenSpinner.selectedItem.toString()
        val satOpen = if (binding.satOpenSpinner.selectedItem.toString().equals("Closed")) "" else binding.satOpenSpinner.selectedItem.toString()
        val facAvail = "1"

        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitFacilityHours + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&monOpen=${monOpen}&tueOpen=${tueOpen}&wedOpen=${wedOpen}&thuOpen=${thuOpen}" +
                "&friOpen=${friOpen}&satOpen=${satOpen}&sunOpen=${sunOpen}&monClose=${monClose}&tueClose=${tueClose}&wedClose=${wedClose}&thuClose=${thuClose}&friClose=${friClose}" +
                "&satClose=${satClose}&sunClose=${sunClose}&nightDrop=${nightDrop}&nightDropInstr=${nightDropInstructions}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat()+
                "&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=${Date().toApiSubmitFormat()}&facAvailability=${facAvail}&availEffDate=${Date().toApiSubmitFormat()}&availExpDate=${Date().toApiSubmitFormat()}" + Utility.getLoggingParameters(activity, 0, getHoursChanges()),
            { response ->
                requireActivity().runOnUiThread {
                    if (response.toString().contains("returnCode>0<",false)) {
                        HasChangedModel.getInstance().updateChangedData("Location and Contact Information","Facility Hours","",getHoursChanges())
                        FacilityDataModel.getInstance().tblHours[0].MonClose = monClose
                        FacilityDataModel.getInstance().tblHours[0].SunClose = sunClose
                        FacilityDataModel.getInstance().tblHours[0].SatClose = satClose
                        FacilityDataModel.getInstance().tblHours[0].FriClose = friClose
                        FacilityDataModel.getInstance().tblHours[0].ThuClose = thuClose
                        FacilityDataModel.getInstance().tblHours[0].WedClose = wedClose
                        FacilityDataModel.getInstance().tblHours[0].TueClose = tueClose
                        FacilityDataModel.getInstance().tblHours[0].MonOpen = monOpen
                        FacilityDataModel.getInstance().tblHours[0].SunOpen = sunOpen
                        FacilityDataModel.getInstance().tblHours[0].SatOpen = satOpen
                        FacilityDataModel.getInstance().tblHours[0].FriOpen = friOpen
                        FacilityDataModel.getInstance().tblHours[0].ThuOpen = thuOpen
                        FacilityDataModel.getInstance().tblHours[0].WedOpen = wedOpen
                        FacilityDataModel.getInstance().tblHours[0].TueOpen = tueOpen
                        FacilityDataModel.getInstance().tblHours[0].NightDrop= binding.nightDropCheck.isChecked
                        FacilityDataModel.getInstance().tblHours[0].NightDropInstr = binding.nightDropInstText.text.toString()
                        FacilityDataModelOrg.getInstance().tblHours[0].MonClose = monClose
                        FacilityDataModelOrg.getInstance().tblHours[0].SunClose = sunClose
                        FacilityDataModelOrg.getInstance().tblHours[0].SatClose = satClose
                        FacilityDataModelOrg.getInstance().tblHours[0].FriClose = friClose
                        FacilityDataModelOrg.getInstance().tblHours[0].ThuClose = thuClose
                        FacilityDataModelOrg.getInstance().tblHours[0].WedClose = wedClose
                        FacilityDataModelOrg.getInstance().tblHours[0].TueClose = tueClose
                        FacilityDataModelOrg.getInstance().tblHours[0].MonOpen = monOpen
                        FacilityDataModelOrg.getInstance().tblHours[0].SunOpen = sunOpen
                        FacilityDataModelOrg.getInstance().tblHours[0].SatOpen = satOpen
                        FacilityDataModelOrg.getInstance().tblHours[0].FriOpen = friOpen
                        FacilityDataModelOrg.getInstance().tblHours[0].ThuOpen = thuOpen
                        FacilityDataModelOrg.getInstance().tblHours[0].WedOpen = wedOpen
                        FacilityDataModelOrg.getInstance().tblHours[0].TueOpen = tueOpen
                        FacilityDataModelOrg.getInstance().tblHours[0].NightDrop= binding.nightDropCheck.isChecked
                        FacilityDataModelOrg.getInstance().tblHours[0].NightDropInstr = binding.nightDropInstText.text.toString()
                        (activity as FormsActivity).saveRequired = false
                        (activity as FormsActivity).saveDone = true
                        saveHoursRequired = false
                        HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                        HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                        refreshButtonsState()
                        Utility.showSubmitAlertDialog(activity, true, "Facility Hours / Night Drop")

                    } else {
                        var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                        Utility.showSubmitAlertDialog(activity, false, "Facility Hours / Night Drop (Error: "+errorMessage+" )")
                    }
                    binding.contactInfoLoadingView.visibility = View.GONE
                    binding.contactInfoLoadingText.text = "Loading ..."
                }
            }, {
        Log.v("error while loading", "error submitting hours")
        Utility.showSubmitAlertDialog(activity,false,"Facility Hours / Night Drop (Error: "+it.message+" )")
            binding.contactInfoLoadingView.visibility = View.GONE
            binding.contactInfoLoadingText.text = "Loading ..."
    }))

    }

    fun submitLanguages(){
        var langTypeId=""
        try {
            for (i in 0..LanguageListAdapter.langArray.size-1) {
                langTypeId += LanguageListAdapter.langArray[i].LangTypeID + ","
            }
            langTypeId=langTypeId.dropLast(1)
        } catch (e: Exception){
            Log.v("ERROR --- >", e.message.toString())
        }
        Log.v("LANGUAGES --- ",UpdateFacilityLanguageData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubcode=${FacilityDataModel.getInstance().clubCode}&langTypeId=${langTypeId}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat())
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateFacilityLanguageData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&langTypeId=${langTypeId}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat() + Utility.getLoggingParameters(activity, 0, getLanguageChanges()),
            { response ->
                requireActivity().runOnUiThread {
                    if (response.toString().contains("returnCode>0<",false)) {
                        HasChangedModel.getInstance().updateChangedData("Location and Contact Information","Facility Languages","",getLanguageChanges())
                        Utility.showSubmitAlertDialog(activity, true, "Facility Languages")
                        (activity as FormsActivity).saveRequired = false
                        FacilityDataModelOrg.getInstance().tblLanguage.clear()
                        FacilityDataModel.getInstance().tblLanguage.apply {
                            (0 until size).forEach {
                                var langItem = TblLanguage()
                                langItem.LangTypeID = get(it).LangTypeID
                                FacilityDataModelOrg.getInstance().tblLanguage.add(langItem)
                            }
                        }
                        HasChangedModel.getInstance().checkGeneralInfoTblLanguagesChange()
                        HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                        (activity as FormsActivity).saveDone = true
                        saveLangRequired = false
                        refreshButtonsState()
                    } else {
                        var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                        Utility.showSubmitAlertDialog(activity, false, "Facility Languages (Error: "+errorMessage+" )")
                    }

                    binding.contactInfoLoadingView.visibility = View.GONE
                    binding.contactInfoLoadingText.text = "Loading ..."
                }
            },
            {
            Utility.showSubmitAlertDialog(activity, false, "Facility Languages (Error: "+it.message+" )")
            binding.contactInfoLoadingView.visibility = View.GONE
            binding.contactInfoLoadingText.text = "Loading ..."
    }))
    }

    fun submitFacilityPhone(){
        val phoneTypeID = TypeTablesModel.getInstance().LocationPhoneType.filter { s -> s.LocPhoneName==binding.newPhoneTypeSpinner.selectedItem.toString()}[0].LocPhoneID
        val phoneNo = if (binding.newPhoneNoText.text.isNullOrEmpty())  "" else binding.newPhoneNoText.text
        val insertDate = Date().toApiSubmitFormat()
        val insertBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val updateDate = Date().toApiSubmitFormat()
        val updateBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val activeVal = "0"
        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
        val clubCode = FacilityDataModel.getInstance().clubCode
        val newPhone = TblPhone()
        newPhone.PhoneNumber = phoneNo.toString()
        newPhone.PhoneTypeID= phoneTypeID
//        var seqNo = FacilityDataModel.getInstance().tblPhone.size+1
        var urlString = facilityNo+"&clubCode="+clubCode+"&phoneTypeId="+phoneTypeID+"&phoneNumber="+phoneNo+"&insertBy="+insertBy+"&insertDate="+insertDate+"&updateBy="+updateBy+"&updateDate="+updateDate+"&extension=&description=&phoneId=&active=1"
        Log.v("Data To Submit", urlString)
        binding.contactInfoLoadingText.text = "Saving ..."
        binding.contactInfoLoadingView.visibility = View.VISIBLE
        binding.addNewPhoneDialog.visibility = View.GONE
        binding.alphaBackgroundForDialogs.visibility = View.GONE
        (activity as FormsActivity).overrideBackButton = false
        Log.v("PHONE ADD --- ",Constants.submitFacilityPhone + urlString)
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitFacilityPhone + urlString+ Utility.getLoggingParameters(activity, 0, getPhoneChanges(0,0)),
            { response ->
                requireActivity().runOnUiThread {
                    if (response.toString().contains("returnCode>0<", false)) {
                        HasChangedModel.getInstance().updateChangedData("Location and Contact Information","Facility Phones","",getPhoneChanges(0,0))
                        Utility.showSubmitAlertDialog(activity, true, "Facility Phone")
                        if (FacilityDataModel.getInstance().tblPhone.size==1 && FacilityDataModel.getInstance().tblPhone[0].PhoneID.equals("-1")){
                            FacilityDataModel.getInstance().tblPhone.removeAt(0)
                            FacilityDataModelOrg.getInstance().tblPhone.removeAt(0)
                        }
                        newPhone.PhoneID = response.toString().substring(response.toString().indexOf("<PhoneID")+9,response.toString().indexOf("</PhoneID"))
                        FacilityDataModel.getInstance().tblPhone.add(newPhone)
                        HasChangedModel.getInstance().groupFacilityContactInfo[0].FacilityPhone = true
                        HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                        (activity as FormsActivity).saveDone = true
                        fillPhoneTableView()
                    } else {
                        var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                        Utility.showSubmitAlertDialog(activity, false, "Facility Phone (Error: "+errorMessage+" )")
                    }
                    binding.contactInfoLoadingView.visibility = View.GONE
                    binding.contactInfoLoadingText.text = "Loading ..."
                }
            },
            {
            binding.contactInfoLoadingView.visibility = View.GONE
            binding.contactInfoLoadingText.text = "Loading ..."
        Utility.showSubmitAlertDialog(activity, false, "Facility Phone (Error: "+it.message+" )")
    }))
    }

    fun altEmailTableRow(alt_row : Int) {
        var childViewCount = binding.emailTbl.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= binding.emailTbl.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(getResources().getDrawable(
                        R.drawable.alt_row_color));
            } else {
                row.setBackground(getResources().getDrawable(
                        R.drawable.row_color));
            }

        }
    }
    fun altPhoneTableRow(alt_row : Int) {
        var childViewCount = binding.phoneTbl.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= binding.phoneTbl.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(getResources().getDrawable(
                        R.drawable.alt_row_color));
            } else {
                row.setBackground(getResources().getDrawable(
                        R.drawable.row_color));
            }

        }
    }

    fun checkLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.activity as MainActivity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 350);
        } else {
            try {
                val task = fusedLocationProviderClient!!.getLastLocation();
                task.addOnSuccessListener {
                    if (it != null) {
                        Log.v("Location Captured", it.getLatitude().toString() + " " + it.getLongitude());
                        if (it.longitude.toString().split(".")[1].isNotEmpty()) {
                            if (it.longitude.toString().split(".")[1].length>6) {
                                binding.editGeo1Long.setText(String.format("%.6f", it.longitude))
                                binding.editGeo2Long.setText(String.format("%.6f", it.longitude))
                                binding.editGeo3Long.setText(String.format("%.6f", it.longitude))
                            } else {
                                binding.editGeo1Long.setText(it.longitude.toString())
                                binding.editGeo2Long.setText(it.longitude.toString())
                                binding.editGeo3Long.setText(it.longitude.toString())
                            }
                        }
                        if (it.latitude.toString().split(".")[1].isNotEmpty()) {
                            if (it.latitude.toString().split(".")[1].length>6) {
                                binding.editGeo1Lat.setText(String.format("%.6f", it.latitude))
                                binding.editGeo2Lat.setText(String.format("%.6f", it.latitude))
                                binding.editGeo3Lat.setText(String.format("%.6f", it.latitude))
                            } else {
                                binding.editGeo1Lat.setText(it.latitude.toString())
                                binding.editGeo2Lat.setText(it.latitude.toString())
                                binding.editGeo3Lat.setText(it.latitude.toString())
                            }
                        }
                    } else {
//                        Utility.showMessageDialog(activity,"Information","Unable to capture current location")
                        Utility.showUnifiedErrorDialog(activity,"Unable to capture current location")
                    }
                }
//                task.addOnFailureListener {e: Exception ->
//                    Utility.showMessageDialog(activity,"Information","Unable to capture current location - ${e.message}")
//                }
            } catch (e: SecurityException) {

            }
        }
    }

    private fun captureLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (checkLocationPermissions()) {
            try {
                val task = fusedLocationProviderClient!!.getCurrentLocation(PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                        return CancellationTokenSource().token
                    }
                    override fun isCancellationRequested(): Boolean {
                        return false
                    }
                })
                task.addOnSuccessListener {
                    if (it != null) {
                        Log.v("Location Captured", it.getLatitude().toString() + " " + it.getLongitude());
                        if (btnToBeUpdated ==1) {
                            if (it.longitude.toString().split(".")[1].isNotEmpty()) {
                                if (it.longitude.toString().split(".")[1].length>6) {
                                    binding.editGeo1Long.setText(String.format("%.6f", it.longitude))
                                } else {
                                    binding.editGeo1Long.setText(it.longitude.toString())
                                }
                            }
                            if (it.latitude.toString().split(".")[1].isNotEmpty()) {
                                if (it.latitude.toString().split(".")[1].length>6) {
                                    binding.editGeo1Lat.setText(String.format("%.6f", it.latitude))
                                } else {
                                    binding.editGeo1Lat.setText(it.latitude.toString())
                                }
                            }
//                            editGeo1Long.setText(String.format("%.6f", it.longitude))
//                            editGeo2Long.setText(String.format("%.6f", it.latitude))
                        } else if (btnToBeUpdated ==2) {
                            if (it.longitude.toString().split(".")[1].isNotEmpty()) {
                                if (it.longitude.toString().split(".")[1].length>6) {
                                    binding.editGeo2Long.setText(String.format("%.6f", it.longitude))
                                } else {
                                    binding.editGeo2Long.setText(it.longitude.toString())
                                }
                            }
                            if (it.latitude.toString().split(".")[1].isNotEmpty()) {
                                if (it.latitude.toString().split(".")[1].length>6) {
                                    binding.editGeo2Lat.setText(String.format("%.6f", it.latitude))
                                } else {
                                    binding.editGeo2Lat.setText(it.latitude.toString())
                                }
                            }
                        } else {
                            if (it.longitude.toString().split(".")[1].isNotEmpty()) {
                                if (it.longitude.toString().split(".")[1].length>6) {
                                    binding.editGeo3Long.setText(String.format("%.6f", it.longitude))
                                } else {
                                    binding.editGeo3Long.setText(it.longitude.toString())
                                }
                            }
                            if (it.latitude.toString().split(".")[1].isNotEmpty()) {
                                if (it.latitude.toString().split(".")[1].length>6) {
                                    binding.editGeo3Lat.setText(String.format("%.6f", it.latitude))
                                } else {
                                    binding.editGeo3Lat.setText(it.latitude.toString())
                                }
                            }
                        }
                    } else {
//                        Utility.showMessageDialog(activity,"Information","Unable to capture current location")
                        Utility.showUnifiedErrorDialog(activity,"Unable to capture current location")
                    }
                }
            } catch (e: SecurityException) {

            }
        } else {
            if (!checkLocationPermissions()) {
                if (MainActivity.activity != null) {
                    requestPermissionAndContinue();
                }
            } else {
                try {
                    val task = fusedLocationProviderClient!!.getLastLocation();
                    task.addOnSuccessListener {
                        if (it != null) {
                            Log.v("Location Captured", it.getLatitude().toString() + " " + it.getLongitude());
                            if (btnToBeUpdated ==1) {
                                if (it.longitude.toString().split(".")[1].isNotEmpty()) {
                                    if (it.longitude.toString().split(".")[1].length>6) {
                                        binding.editGeo1Long.setText(String.format("%.6f", it.longitude))
                                    } else {
                                        binding.editGeo1Long.setText(it.longitude.toString())
                                    }
                                }
                                if (it.latitude.toString().split(".")[1].isNotEmpty()) {
                                    if (it.latitude.toString().split(".")[1].length>6) {
                                        binding.editGeo1Lat.setText(String.format("%.6f", it.latitude))
                                    } else {
                                        binding.editGeo1Lat.setText(it.latitude.toString())
                                    }
                                }
//                            editGeo1Long.setText(String.format("%.6f", it.longitude))
//                            editGeo2Long.setText(String.format("%.6f", it.latitude))
                            } else if (btnToBeUpdated ==2) {
                                if (it.longitude.toString().split(".")[1].isNotEmpty()) {
                                    if (it.longitude.toString().split(".")[1].length>6) {
                                        binding.editGeo2Long.setText(String.format("%.6f", it.longitude))
                                    } else {
                                        binding.editGeo2Long.setText(it.longitude.toString())
                                    }
                                }
                                if (it.latitude.toString().split(".")[1].isNotEmpty()) {
                                    if (it.latitude.toString().split(".")[1].length>6) {
                                        binding.editGeo2Lat.setText(String.format("%.6f", it.latitude))
                                    } else {
                                        binding.editGeo2Lat.setText(it.latitude.toString())
                                    }
                                }
                            } else {
                                if (it.longitude.toString().split(".")[1].isNotEmpty()) {
                                    if (it.longitude.toString().split(".")[1].length>6) {
                                        binding.editGeo3Long.setText(String.format("%.6f", it.longitude))
                                    } else {
                                        binding.editGeo3Long.setText(it.longitude.toString())
                                    }
                                }
                                if (it.latitude.toString().split(".")[1].isNotEmpty()) {
                                    if (it.latitude.toString().split(".")[1].length>6) {
                                        binding.editGeo3Lat.setText(String.format("%.6f", it.latitude))
                                    } else {
                                        binding.editGeo3Lat.setText(it.latitude.toString())
                                    }
                                }
                            }
                        } else {
//                            Utility.showMessageDialog(activity,"Information","Unable to capture current location")
                            Utility.showUnifiedErrorDialog(activity,"Unable to capture current location")
                        }
                    }
                } catch (e: SecurityException) {

                }
            }
        }
    }

    fun round(value: Double, places: Int): Double {
        var value = value
        require(places >= 0)
        val factor = Math.pow(10.0, places.toDouble()) as Long
        value = value * factor
        val tmp = Math.round(value)
        return tmp as Double / factor
    }

    fun altLocationTableRow(alt_row : Int) {
        var childViewCount = binding.locationTbl.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= binding.locationTbl.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(getResources().getDrawable(
                        R.drawable.alt_row_color));
            } else {
                row.setBackground(getResources().getDrawable(
                        R.drawable.row_color));
            }

        }
    }

    fun altHolidayTableRow(alt_row : Int) {
        var childViewCount = binding.holidaysTbl.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= binding.holidaysTbl.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(getResources().getDrawable(
                        R.drawable.alt_row_color));
            } else {
                row.setBackground(getResources().getDrawable(
                        R.drawable.row_color));
            }

        }
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


fun EditText.inputFilterDecimal(
        // maximum digits including point and without decimal places
        maxDigitsIncludingPoint: Int,
        maxDecimalPlaces: Int, // maximum decimal places
        signed: Boolean
){
    try {
        filters = arrayOf<InputFilter>(
                DecimalDigitsInputFilter(maxDigitsIncludingPoint, maxDecimalPlaces,signed)
        )
    }catch (e: PatternSyntaxException){
        isEnabled = false
        hint = e.message
    }
}

class DecimalDigitsInputFilter(
    private val maxDigitsIncludingPoint: Int,
    private val maxDecimalPlaces: Int,
    private val signed: Boolean
) : InputFilter {

    private val pattern: Pattern

    init {
        // Ensure safe values
        val safeMaxDigits = maxDigitsIncludingPoint.coerceAtLeast(1)
        val safeMaxDecimals = maxDecimalPlaces.coerceAtLeast(0)

        // Integer digits = total - decimals
        val maxIntegerDigits = (safeMaxDigits - safeMaxDecimals).coerceAtLeast(0)

        pattern = Pattern.compile(
            (if (signed) "-?" else "") +
                    "[0-9]{0,$maxIntegerDigits}" +
                    "(\\.[0-9]{0,$safeMaxDecimals})?"
        )
    }

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {

        val newValue = StringBuilder(dest ?: "")
            .replace(dstart, dend, source?.subSequence(start, end).toString())
            .toString()

        // Allow empty input
        if (newValue.isEmpty() || newValue == "-") return null

        val matcher = pattern.matcher(newValue)

        return if (matcher.matches()) {
            null   // ✅ accept input
        } else {
            ""     // ❌ reject input
        }
    }
}

//class DecimalDigitsInputFilter(
//        maxDigitsIncludingPoint: Int, maxDecimalPlaces: Int, signed: Boolean
//) : InputFilter {
//        private val pattern: Pattern = Pattern.compile(
//                (if (signed) "-" else "")+"[0-9]{0," + (maxDigitsIncludingPoint - 1) + "}+((\\.[0-9]{0,"
//                    + (maxDecimalPlaces - 1) + "})?)||(\\.)?"
//    )
//
//    override fun filter(
//            p0: CharSequence?,p1: Int,p2: Int,p3: Spanned?,p4: Int,p5: Int
//    ): CharSequence? {
//        p3?.apply {
//            val matcher: Matcher = pattern.matcher(p3)
//            return if (!matcher.matches()) "" else