package com.inspection.fragments


import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.opengl.Visibility
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.adapter.StagedCertificatesAdapter
import com.inspection.model.StagedCertStatus
import com.inspection.model.StagedCertificateEntry
import com.google.gson.Gson
import com.inspection.FormsActivity
import com.inspection.R
import com.inspection.Utils.*
import com.inspection.Utils.Constants.UpdateFacilityPersonnelData
import com.inspection.Utils.Constants.UpdateFacilityPersonnelSignerData
import com.inspection.databinding.FragmentAaravPersonnelBinding
import com.inspection.databinding.FragmentArravAffliationsBinding
import com.inspection.model.*
//import kotlinx.android.synthetic.main.activity_item_detail.*
//import kotlinx.android.synthetic.main.app_adhoc_visitation_filter_fragment.*
//import kotlinx.android.synthetic.main.facility_group_layout.*
//import kotlinx.android.synthetic.main.fragment_aarav_personnel.*
//import kotlinx.android.synthetic.main.fragment_arrav_facility.*
//import kotlinx.android.synthetic.main.fragment_arrav_facility.cancelButton
//import kotlinx.android.synthetic.main.fragment_arrav_facility.saveButton
//import kotlinx.android.synthetic.main.fragment_arrav_programs.*
//import kotlinx.android.synthetic.main.fragment_array_vehicle_services.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
import java.util.Locale.getDefault


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentARRAVPersonnel.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVPersonnel : Fragment() {


    // TODO: Rename and change types of parameters
    var emailValid = true
    var zipFormat = true
    var selectedPersonnelID = 0
    var contractSignatureIsChecked = false
    private var mParam1: String? = null
    var countIfContractSignedBefore = 0
    private var mParam2: String? = null
    private var personnelTypeList = ArrayList<TypeTablesModel.personnelType>()
    private var certificationTypeList = ArrayList<TypeTablesModel.personnelCertificationType>()
    private var states = arrayOf(
        "Select State",
        "Alabama",
        "Alaska",
        "Arizona",
        "Arkansas",
        "California",
        "Colorado",
        "Connecticut",
        "Delaware",
        "District of Columbia",
        "Florida",
        "Georgia",
        "Hawaii",
        "Idaho",
        "Illinois",
        "Indiana",
        "Iowa",
        "Kansas",
        "Kentucky",
        "Louisiana",
        "Maine",
        "Maryland",
        "Massachusetts",
        "Michigan",
        "Minnesota",
        "Mississippi",
        "Missouri",
        "Montana",
        "Nebraska",
        "Nevada",
        "New Hampshire",
        "New Jersey",
        "New Mexico",
        "New York",
        "North Carolina",
        "North Dakota",
        "Ohio",
        "Oklahoma",
        "Oregon",
        "Pennsylvania",
        "Rhode Island",
        "South Carolina",
        "South Dakota",
        "Tennessee",
        "Texas",
        "Utah",
        "Vermont",
        "Virginia",
        "Washington",
        "West Virginia",
        "Wisconsin",
        "Wyoming"
    )
    private var statesAbbrev = arrayOf(
        "Select State",
        "AL",
        "AK",
        "AZ",
        "AR",
        "CA",
        "CO",
        "CT",
        "DE",
        "DC",
        "FL",
        "GA",
        "HI",
        "ID",
        "IL",
        "IN ",
        "IA",
        "KS",
        "KY",
        "LA",
        "ME",
        "MD",
        "MA",
        "MI",
        "MN",
        "MS",
        "MO",
        "MT",
        "NE",
        "NV",
        "NH",
        "NJ",
        "NM",
        "NY",
        "NC",
        "ND",
        "OH",
        "OK",
        "OR",
        "PA",
        "RI",
        "SC",
        "SD",
        "TN",
        "TX",
        "UT",
        "VT",
        "VA",
        "WA",
        "WV",
        "WI",
        "WY"
    )
    private var personTypeArray = ArrayList<String>()
    private var certTypeArray = ArrayList<String>()
    private var personTypeIDsArray = ArrayList<String>()
    private var personListArray = ArrayList<String>()
    private var statesArray = ArrayList<String>()
    private var personIDWithExpiredCerts = ArrayList<Int>()
    private var personIDWithAboutExpiredCerts = ArrayList<Int>()
    var hyperlinktxt: String = ""
    var validationMsg = ""
    var edithyperlinktxt: String = ""
    private val stagedCertificates = ArrayList<StagedCertificateEntry>()
    private var isSubmitting = false
    private lateinit var stagedAdapter: StagedCertificatesAdapter
    private var firstSelection =
        false // Variable used as the first item in the personnelType drop down is selected by default when the ata is loaded

    //    private val strFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dbFormat = SimpleDateFormat("yyyy-MM-dd")
    private val appFormat = SimpleDateFormat("dd MMM yyyy")
    private var _binding: FragmentAaravPersonnelBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_aarav_personnel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAaravPersonnelBinding.bind(view)
        scopeOfServiceChangesWatcher()
        preparePersonnelPage()
        if (FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PrimaryMailRecipient == true }
                .isNotEmpty()) {
            binding.primaryMailLbl.visibility = View.GONE
        } else {
            binding.primaryMailLbl.visibility = View.VISIBLE
        }

        setAlertColoring()
        fillPersonnelTableView()
        fillCertificationGridAligned()

//        rspUserId.setText(FacilityDataModel.getInstance().tblPersonnel[0].RSP_UserName.toString())
//        rspEmailId.setText(FacilityDataModel.getInstance().tblPersonnel[0].RSP_Email.toString())
        IndicatorsDataModel.getInstance().tblFacility[0].PersonnelVisited = true
        // SAEED TO BE REVIEWED
        (requireActivity().supportFragmentManager.findFragmentById(R.id.fragment) as? HasTabIndicators)?.refreshTabIndicators()
//        (activity as FormsActivity).personnelButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()

        binding.exitDialogeBtnId.setOnClickListener {
            (activity as FormsActivity).overrideBackButton = false
            binding.addNewPersonnelDialogue.visibility = View.GONE
            binding.alphaBackgroundForPersonnelDialogs.visibility = View.GONE
        }
        binding.editExitDialogeBtnId.setOnClickListener {
            (activity as FormsActivity).overrideBackButton = false
            binding.editAddNewPersonnelDialogue.visibility = View.GONE
            binding.alphaBackgroundForPersonnelDialogs.visibility = View.GONE
        }

        binding.exitCertificateDialogeBtnId.setOnClickListener {
            if (!isSubmitting) closeCertificateDialog()
        }

        binding.exitCertificateGridBtnId.setOnClickListener {
            (activity as FormsActivity).overrideBackButton = false
            binding.certificateGrid.visibility = View.GONE
            binding.alphaBackgroundForPersonnelDialogs.visibility = View.GONE
        }

        binding.exitEditCertificateDialogeBtnId.setOnClickListener {
            (activity as FormsActivity).overrideBackButton = false
            binding.editNewCertificateDialogue.visibility = View.GONE
            binding.alphaBackgroundForPersonnelDialogs.visibility = View.GONE
        }


        // --- Staged certificates RecyclerView setup ---
        stagedAdapter = StagedCertificatesAdapter(stagedCertificates) { index ->
            if (!isSubmitting) {
                stagedCertificates.removeAt(index)
                stagedAdapter.notifyItemRemoved(index)
                binding.submitAllBtn.isEnabled = stagedCertificates.isNotEmpty()
            }
        }
        binding.stagedCertificatesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.stagedCertificatesRecyclerView.adapter = stagedAdapter

        binding.AddNewCertBtn.setOnClickListener {
            if (selectedPersonnelID.equals(0)) {
                Utility.showValidationAlertDialog(
                    activity,
                    "Please select the related personnel from the list"
                )
            } else {
                binding.newCertTypeSpinner.setSelection(0)
                binding.newCertStartDateBtn.setText("SELECT DATE")
                binding.newCertEndDateBtn.setText("SELECT DATE")
                binding.newCertDescText.setText("")
                binding.newCertStartDateBtn.setError(null)
                binding.certTypeTextView.setError(null)
                binding.submitAllBtn.isEnabled = stagedCertificates.isNotEmpty()
                binding.closeCertDialogBtn.isEnabled = !isSubmitting
                (activity as FormsActivity).overrideBackButton = true
                binding.addNewCertificateDialogue.visibility = View.VISIBLE
                binding.alphaBackgroundForPersonnelDialogs.visibility = View.VISIBLE
            }
        }
        binding.openCertificateGrid.setPaintFlags(binding.openCertificateGrid.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        binding.openCertificateGrid.setText("Open Certification Grid")

        binding.openCertificateGrid.setOnClickListener {
//            fillCertificationGridAligned()
            (activity as FormsActivity).overrideBackButton = true
            binding.certificateGrid.visibility = View.VISIBLE
            binding.alphaBackgroundForPersonnelDialogs.visibility = View.VISIBLE
        }

        binding.editurlLink.isClickable = true;
        binding.editurlLink.movementMethod = LinkMovementMethod.getInstance()

        binding.addNewPRGRecordBtn.setOnClickListener {
            if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                binding.alphaBackgroundForPersonnelDialogs.visibility = View.VISIBLE
                binding.personnelLoadingText.text = "Saving ..."
                binding.personnelLoadingView.visibility = View.VISIBLE
                var urlString =
                    "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&insertBy=${
                        ApplicationPrefs.getInstance(activity).loggedInUserID
                    }&sessionId=" + ApplicationPrefs.getInstance(activity)
                        .getSessionID() + "&userId=" + ApplicationPrefs.getInstance(activity)
                        .getLoggedInUserID()
                Log.v("CREATE PRG USER --- ", Constants.CreatePRGUser + urlString)
                Volley.newRequestQueue(context)
                    .add(StringRequest(Request.Method.GET, Constants.CreatePRGUser + urlString,
                        { response ->
                            requireActivity().runOnUiThread {
                                if (response.toString().contains("returnCode>0<", false)) {
                                    HasChangedModel.getInstance().updateChangedData("Personnel Screen","Personnel","","PRG USER Created")
                                    if (response.toString().contains("ErrorFlag>0<", false)) {
                                        //                                    Utility.showSubmitAlertDialog(activity, true, "PRG USER")
                                        //                                    Utility.showMessageDialog(activity, "Confirmation...", "PRG User was created successfully")
                                        Utility.showUnifiedConfirmationDialog(
                                            activity,
                                            "PRG User was created successfully"
                                        )
                                        var item = TblPersonnel()
                                        item.PersonnelTypeID = 47
                                        item.PersonnelID = response.toString().substring(
                                            response.toString().indexOf("<PersonnelID") + 13,
                                            response.toString().indexOf("</PersonnelID")
                                        ).toInt()
                                        item.FirstName = response.toString().substring(
                                            response.toString().indexOf("<FirstName") + 11,
                                            response.toString().indexOf("</FirstName")
                                        )
                                        item.LastName = response.toString().substring(
                                            response.toString().indexOf("<LastName") + 10,
                                            response.toString().indexOf("</LastName")
                                        )
                                        item.RSP_UserName = response.toString().substring(
                                            response.toString().indexOf("<RSP_UserName") + 14,
                                            response.toString().indexOf("</RSP_UserName")
                                        )
                                        item.RSP_Email = response.toString().substring(
                                            response.toString().indexOf("<RSP_Email") + 11,
                                            response.toString().indexOf("</RSP_Email")
                                        )
                                        item.CertificationNum = ""
                                        item.CertificationNum_ASE = ""
                                        item.ContractSigner = false
                                        item.PrimaryMailRecipient = false
                                        item.startDate = Date().toApiSubmitFormat()
                                        item.endDate = ""
                                        item.SeniorityDate = ""
                                        item.ReportRecipient = false
                                        item.NotificationRecipient = false
                                        item.ComplaintContact = false
                                        item.insertBy = response.toString().substring(
                                            response.toString().indexOf("<insertBy") + 10,
                                            response.toString().indexOf("</insertBy")
                                        )
                                        item.insertDate = response.toString().substring(
                                            response.toString().indexOf("<insertDate") + 12,
                                            response.toString().indexOf("</insertDate")
                                        )
                                        item.updateBy = response.toString().substring(
                                            response.toString().indexOf("<updateBy") + 10,
                                            response.toString().indexOf("</updateBy")
                                        )
                                        item.updateDate = response.toString().substring(
                                            response.toString().indexOf("<updateDate") + 12,
                                            response.toString().indexOf("</updateDate")
                                        )
                                        FacilityDataModel.getInstance().tblPersonnel.add(item)
                                        FacilityDataModelOrg.getInstance().tblPersonnel.add(item)
                                        HasChangedModel.getInstance().groupFacilityPersonnel[0].FacilityPersonnel =
                                            true
                                        HasChangedModel.getInstance()
                                            .changeDoneForFacilityPersonnel()
                                        (activity as FormsActivity).saveDone = true
                                        fillPersonnelTableView()
                                        altTableRow(2)
                                        binding.alphaBackgroundForPersonnelDialogs.visibility =
                                            View.GONE
                                        binding.personnelLoadingView.visibility = View.GONE
                                        binding.personnelLoadingText.text = "Loading ..."
                                    } else {
                                        var errorMessage = response.toString().substring(
                                            response.toString().indexOf("<ErrorMsg") + 10,
                                            response.toString().indexOf("</ErrorMsg")
                                        )
                                        //                                    Utility.showMessageDialog(activity, "Sorry...", errorMessage)
                                        Utility.showUnifiedErrorDialog(activity, errorMessage)
                                    }
                                } else {
                                    var errorMessage = response.toString()
                                    //                                Utility.showMessageDialog(activity, "Sorry...", errorMessage)
                                    Utility.showUnifiedErrorDialog(activity, errorMessage)
                                }
                                binding.personnelLoadingView.visibility = View.GONE
                                binding.alphaBackgroundForPersonnelDialogs.visibility = View.GONE
                                binding.personnelLoadingText.text = "Loading ..."
                            }
                        }, {
                            //                Utility.showMessageDialog(activity, "Sorry...", "Error: " + it.message)
                            Utility.showUnifiedErrorDialog(activity, it.message)
                            binding.personnelLoadingView.visibility = View.GONE
                            binding.personnelLoadingText.text = "Loading ..."

                        })
                    ).setRetryPolicy(
                        DefaultRetryPolicy(
                            0,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                    )
            } else {
                Utility.showInternetWarningDialog(
                    requireContext(),
                    (requireActivity() as FormsActivity).networkStatusErrorMsg
                )
            }
        }

        binding.addNewPersnRecordBtn.setOnClickListener {
            binding.newFirstNameText.setText("")
            binding.newLastNameText.setText("")
            binding.newCertNoText.setText("")
            binding.newASECertNoText.setText("")
            binding.newStartDateBtn.setText("SELECT DATE")
            binding.newEndDateBtn.setText("SELECT DATE")
            binding.newSeniorityDateBtn.setText("SELECT DATE")
            binding.newCoStartDateBtn.setText("SELECT DATE")
            binding.newCoEndDateBtn.setText("SELECT DATE")
            binding.newOEMStartDateBtn.setText("SELECT DATE")
            binding.newOEMEndDateBtn.setText("SELECT DATE")
            binding.newPhoneText.setText("")
            binding.newZipText.setText("")
            binding.newAdd1Text.setText("")
            binding.newAdd2Text.setText("")
            binding.newCityText.setText("")
            binding.newZipText2.setText("")
            binding.newEmailText.setText("")
            binding.newStateSpinner.setSelection(0)
            binding.newPersonnelTypeSpinner.setSelection(0)
            binding.rspUserId.setText("")
            binding.rspEmailId.setText("")
            binding.newZipText.setError(null)
            binding.newZipText2.setError(null)
            binding.newPhoneText.setError(null)
            binding.newCertNoText.setError(null)
            binding.stateTextView.setError(null)
            binding.newEmailText.setError(null)
            binding.personnelTypeTextViewId.setError(null)
            onlyOneContractSignerLogic()
            binding.addNewPersonnelDialogue.visibility = View.VISIBLE
//            val hyperlinktxt : String = "<a href='"+textViewAceURL.text.toString()+"'>Tap here to open Link</a>"

            binding.urlLink.isClickable = true;
            binding.urlLink.movementMethod = LinkMovementMethod.getInstance()
            hyperlinktxt =
                "<a href='" + binding.newACEURLText.text.toString() + "'>" + binding.newACEURLText.text.toString() + "</a>"
            binding.urlLink.text = Html.fromHtml(hyperlinktxt, Html.FROM_HTML_MODE_COMPACT)
            binding.alphaBackgroundForPersonnelDialogs.visibility = View.VISIBLE
            (activity as FormsActivity).overrideBackButton = true
        }


        // contractSignerIsNotCheckedLogic()

        binding.newCertStartDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.newCertStartDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.newCertStartDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireActivity(),
                R.style.CustomDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    binding.newCertStartDateBtn!!.text = sdf.format(c.time)
                },
                year,
                month,
                day
            )
            dpd.show()
        }

        binding.editNewCertStartDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.editNewCertStartDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.editNewCertStartDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireActivity(),
                R.style.CustomDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    binding.editNewCertStartDateBtn!!.text = sdf.format(c.time)
                },
                year,
                month,
                day
            )
            dpd.show()
        }

        binding.newOEMStartDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.newOEMStartDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.newOEMStartDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireActivity(),
                R.style.CustomDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    binding.newOEMStartDateBtn!!.text = sdf.format(c.time)
                },
                year,
                month,
                day
            )
            dpd.show()
        }

        binding.newOEMEndDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.newOEMEndDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.newOEMEndDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireActivity(),
                R.style.CustomDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    binding.newOEMEndDateBtn!!.text = sdf.format(c.time)
                },
                year,
                month,
                day
            )
            dpd.show()
        }

        binding.newEditOEMStartDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.newEditOEMStartDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.newEditOEMStartDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireActivity(),
                R.style.CustomDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    binding.newEditOEMStartDateBtn!!.text = sdf.format(c.time)
                },
                year,
                month,
                day
            )
            dpd.show()
        }

        binding.newEditOEMEndDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.newEditOEMEndDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.newEditOEMEndDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireActivity(),
                R.style.CustomDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    binding.newEditOEMEndDateBtn!!.text = sdf.format(c.time)
                },
                year,
                month,
                day
            )
            dpd.show()
        }

        binding.newCoStartDateBtn.setOnClickListener {
//            if (newCoStartDateBtn.text.equals("SELECT DATE")) {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.newCoStartDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.newCoStartDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireActivity(),
                R.style.CustomDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    binding.newCoStartDateBtn!!.text = sdf.format(c.time)
                },
                year,
                month,
                day
            )
            dpd.show()
//            }
        }
        binding.editNewEndDateBtn.setOnClickListener {
//            if (newCoStartDateBtn.text.equals("SELECT DATE")) {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.editNewEndDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.editNewEndDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireActivity(),
                R.style.CustomDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    binding.editNewEndDateBtn!!.text = sdf.format(c.time)
                },
                year,
                month,
                day
            )
            dpd.show()
//            }
        }

        binding.editNewCoStartDateBtn.setOnClickListener {
//            if (newCoStartDateBtn.text.equals("SELECT DATE")) {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.editNewCoStartDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.editNewCoStartDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireActivity(),
                R.style.CustomDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    binding.editNewCoStartDateBtn!!.text = sdf.format(c.time)
                },
                year,
                month,
                day
            )
            dpd.show()
//            }
        }

        binding.newSeniorityDateBtn.setOnClickListener {
//            if (newSeniorityDateBtn.text.equals("SELECT DATE")) {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.newSeniorityDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.newSeniorityDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireActivity(),
                R.style.CustomDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    binding.newSeniorityDateBtn!!.text = sdf.format(c.time)
                },
                year,
                month,
                day
            )
            dpd.show()
//            }
        }
        binding.editNewSeniorityDateBtn.setOnClickListener {
//            if (newSeniorityDateBtn.text.equals("SELECT DATE")) {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.editNewSeniorityDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.editNewSeniorityDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireActivity(),
                R.style.CustomDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    binding.editNewSeniorityDateBtn!!.text = sdf.format(c.time)
                },
                year,
                month,
                day
            )
            dpd.show()
//            }
        }

        binding.newStartDateBtn.setOnClickListener {
//            if (newStartDateBtn.text.equals("SELECT DATE")) {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.newStartDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.newStartDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireActivity(),
                R.style.CustomDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    binding.newStartDateBtn!!.text = sdf.format(c.time)
                },
                year,
                month,
                day
            )
            dpd.show()
//            }
        }
        binding.editNewStartDateBtn.setOnClickListener {
//            if (newStartDateBtn.text.equals("SELECT DATE")) {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.editNewStartDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.editNewStartDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireActivity(),
                R.style.CustomDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    binding.editNewStartDateBtn!!.text = sdf.format(c.time)
                },
                year,
                month,
                day
            )
            dpd.show()
//            }
        }


        fillData()

        // --- Add to List: stages a validated certificate entry without calling the API ---
        binding.addToListBtn.setOnClickListener {
            if (validateCertificationInputs()) {
                val entry = StagedCertificateEntry()
                entry.personnelId = selectedPersonnelID

                for (fac in TypeTablesModel.getInstance().PersonnelCertificationType) {
                    if (binding.newCertTypeSpinner.selectedItem.toString() == fac.PersonnelCertName) {
                        entry.certificationTypeId = fac.PersonnelCertID
                        entry.certificationTypeName = fac.PersonnelCertName
                    }
                }
                entry.certificationDate = binding.newCertStartDateBtn.text.toString()
                entry.expirationDate = binding.newCertEndDateBtn.text.toString()
                entry.certDesc = binding.newCertDescText.text?.toString() ?: ""

                stagedCertificates.add(entry)
                stagedAdapter.notifyItemInserted(stagedCertificates.size - 1)

                // Reset form fields for next entry
                binding.newCertTypeSpinner.setSelection(0)
                binding.newCertStartDateBtn.setText("SELECT DATE")
                binding.newCertEndDateBtn.setText("SELECT DATE")
                binding.newCertDescText.setText("")
                binding.newCertStartDateBtn.setError(null)
                binding.certTypeTextView.setError(null)
                binding.expirationDateText.setError(null)

                binding.submitAllBtn.isEnabled = true
            } else {
                Utility.showValidationAlertDialog(activity, validationMsg)
            }
        }

        // --- Submit All: validate all staged entries then call API sequentially ---
        binding.submitAllBtn.setOnClickListener {
            if (!(requireActivity() as FormsActivity).isNetworkAvailable) {
                Utility.showInternetWarningDialog(
                    requireContext(),
                    (requireActivity() as FormsActivity).networkStatusErrorMsg
                )
                return@setOnClickListener
            }
            if (!validateAllStagedEntries()) return@setOnClickListener

            isSubmitting = true
            stagedAdapter.setSubmitting(true)
            binding.submitAllBtn.isEnabled = false
            binding.addToListBtn.isEnabled = false
            binding.closeCertDialogBtn.isEnabled = false
            binding.retryFailedBtn.visibility = View.GONE
            binding.personnelLoadingText.text = "Saving..."
            binding.personnelLoadingView.visibility = View.VISIBLE

            submitNextStagedCert(0)
        }

        // --- Close: dismiss dialog and clear staged list ---
        binding.closeCertDialogBtn.setOnClickListener {
            closeCertificateDialog()
        }

        // --- Retry Failed: reset failed entries to PENDING and re-submit ---
        binding.retryFailedBtn.setOnClickListener {
            for (entry in stagedCertificates) {
                if (entry.status == StagedCertStatus.FAILED) {
                    entry.status = StagedCertStatus.PENDING
                    entry.errorMessage = ""
                }
            }
            stagedAdapter.notifyDataSetChanged()
            binding.retryFailedBtn.visibility = View.GONE

            isSubmitting = true
            stagedAdapter.setSubmitting(true)
            binding.closeCertDialogBtn.isEnabled = false
            binding.personnelLoadingText.text = "Saving..."
            binding.personnelLoadingView.visibility = View.VISIBLE

            submitNextStagedCert(0)
        }

        binding.submitNewPersnRecordBtn.setOnClickListener {
            if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                if (validateInputs()) {
//                    binding.addNewPersonnelDialogue.visibility = View.GONE
//                    binding.alphaBackgroundForPersonnelDialogs.visibility = View.GONE
                    binding.personnelLoadingText.text = "Saving ..."
                    binding.personnelLoadingView.visibility = View.VISIBLE
                    (activity as FormsActivity).overrideBackButton = false

                    var PersonnelTypeId = ""

                    for (fac in TypeTablesModel.getInstance().PersonnelType) {
                        if (binding.newPersonnelTypeSpinner.getSelectedItem().toString()
                                .equals(fac.PersonnelTypeName)
                        )

                            PersonnelTypeId = fac.PersonnelTypeID
                    }

                    var FirstName = if (binding.newFirstNameText.text.toString()
                            .isNullOrEmpty()
                    ) "" else binding.newFirstNameText.text.toString()
                    var LastName = if (binding.newLastNameText.text.toString()
                            .isNullOrEmpty()
                    ) "" else binding.newLastNameText.text.toString()
                    // if title is has no rsp role, then do not send to RSP
                    var RSP_UserName =
                        if (binding.roleHint.visibility == View.VISIBLE) "" else binding.rspUserId.text.toString()//FacilityDataModel.getInstance().tblPersonnel[0].RSP_UserName
                    var RSP_Email =
                        binding.rspEmailId.text.toString()//FacilityDataModel.getInstance().tblPersonnel[0].RSP_Email
                    var facNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo
                    var CertificationNum = if (binding.newCertNoText.text.toString()
                            .isNullOrEmpty()
                    ) "" else binding.newCertNoText.text.toString()
                    var ASECertificationNum = if (binding.newASECertNoText.text.toString()
                            .isNullOrEmpty() || binding.newASECertNoText.text.toString()
                            .equals("ASE-")
                    ) "" else binding.newASECertNoText.text.toString()
                    var ContractSigner =
                        if (binding.newSignerCheck.isChecked == true) "true" else "false"
                    var PrimaryMailRecipient =
                        if (binding.newACSCheck.isChecked == true) "true" else "false"
                    var ReportRec = if (binding.newReportCheck.isChecked == true) "1" else "0"
                    var NotificationRec =
                        if (binding.newNotificationCheck.isChecked == true) "1" else "0"
                    var ComplaintContact =
                        if (binding.newComplaintContact.isChecked == true) "1" else "0"
                    var startDate =
                        if (binding.newStartDateBtn.text.equals("SELECT DATE")) "" else binding.newStartDateBtn.text.toString()
                            .appToApiSubmitFormatMMDDYYYY()
                    var ExpirationDate =
                        if (binding.newEndDateBtn.text.equals("SELECT DATE")) "" else binding.newEndDateBtn.text.toString()
                            .appToApiSubmitFormatMMDDYYYY()
                    var SeniorityDate =
                        if (binding.newSeniorityDateBtn.text.equals("SELECT DATE")) "" else binding.newSeniorityDateBtn.text.toString()
                            .appToApiSubmitFormatMMDDYYYY()
                    var OEMStartDate =
                        if (binding.newOEMStartDateBtn.text.equals("SELECT DATE")) "" else binding.newOEMStartDateBtn.text.toString()
                            .appToApiSubmitFormatMMDDYYYY()
                    var OEMEndDate =
                        if (binding.newOEMEndDateBtn.text.equals("SELECT DATE")) "" else binding.newOEMEndDateBtn.text.toString()
                            .appToApiSubmitFormatMMDDYYYY()
                    var ace_url = if (binding.newACEURLText.text.toString()
                            .isNullOrEmpty()
                    ) "" else binding.newACEURLText.text.toString()
                    var sendToRSP = if (binding.rspUserId.text.toString()
                            .isNotEmpty() && (binding.roleHint.visibility == View.GONE)
                    ) 1 else 0
                    var rspActionId = 1; // ADD
                    Log.v(
                        "ADD PERSON ->",
                        UpdateFacilityPersonnelData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=" + FacilityDataModel.getInstance().clubCode + "&personnelId=&personnelTypeId=$PersonnelTypeId&firstName=$FirstName&lastName=${LastName}&seniorityDate=$SeniorityDate&certificationNum=$CertificationNum&certificationNumASE=$ASECertificationNum&startDate=$startDate&contractSigner=$ContractSigner&insertBy=${
                            ApplicationPrefs.getInstance(activity).loggedInUserID
                        }&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${
                            ApplicationPrefs.getInstance(
                                activity
                            ).loggedInUserID
                        }&updateDate=" + Date().toApiSubmitFormat() + "&active=1&primaryMailRecipient=$PrimaryMailRecipient&rsp_userName=$RSP_UserName&rsp_email=$RSP_Email&rsp_phone=${binding.newPhoneText.text}&endDate=${ExpirationDate}&ASE_URL=${ace_url}&OEMStartDate=${OEMStartDate}&OEMEndDate=${OEMEndDate}&ReportRecipient=${ReportRec}&NotificationRecipient=${NotificationRec}&ComplaintContact=${ComplaintContact}&sendToRSP=${sendToRSP}&rspActionId=${rspActionId}" + Utility.getLoggingParameters(
                            activity,
                            0,
                            getPersonnelChanges(0, 0)
                        )
                    )
                    Volley.newRequestQueue(context).add(
                        StringRequest(Request.Method.GET,
                            UpdateFacilityPersonnelData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=" + FacilityDataModel.getInstance().clubCode + "&personnelId=0&personnelTypeId=$PersonnelTypeId&firstName=$FirstName&lastName=${LastName}&seniorityDate=$SeniorityDate&certificationNum=$CertificationNum&certificationNumASE=$ASECertificationNum&startDate=$startDate&contractSigner=$ContractSigner&insertBy=${
                                ApplicationPrefs.getInstance(activity).loggedInUserID
                            }&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${
                                ApplicationPrefs.getInstance(
                                    activity
                                ).loggedInUserID
                            }&updateDate=" + Date().toApiSubmitFormat() + "&active=1&primaryMailRecipient=$PrimaryMailRecipient&rsp_userName=$RSP_UserName&rsp_email=$RSP_Email&rsp_phone=${binding.newPhoneText.text}&endDate=${ExpirationDate}&ASE_URL=${ace_url}&OEMStartDate=${OEMStartDate}&OEMEndDate=${OEMEndDate}&ReportRecipient=${ReportRec}&NotificationRecipient=${NotificationRec}&ComplaintContact=${ComplaintContact}&sendToRSP=${sendToRSP}&rspActionId=${rspActionId}" + Utility.getLoggingParameters(
                                activity,
                                0,
                                getPersonnelChanges(0, 0)
                            ),
                            { response ->
                                requireActivity().runOnUiThread {
                                    Log.v("ADD PERSONNEL RESPONSE", response.toString())
                                    var rspResponse = ""
                                    var tokenResponse = ""
                                    var personnelResponse = ""
                                    if (response.toString()
                                            .contains("CreatePersonnel:[Failed", false)
                                    ) {
                                        personnelResponse = response.toString().substring(
                                            response.toString().indexOf("CreatePersonnel:[") + 17,
                                            response.toString().indexOf("]")
                                        )
//                                        Utility.showSubmitAlertDialog(
//                                            activity,
//                                            false,
//                                            "Personnel ( " + errorMsg + " )"
//                                        )
                                    } else if (response.toString()
                                            .contains("CreatePersonnel:[Success", false)
                                    ) {

                                        if (response.toString()
                                                .contains("RSPToken")
                                        ) tokenResponse =
                                            response.toString().substring(
                                                response.toString().indexOf("RSPToken:[") + 10,
                                                response.toString().indexOf("]\nCreateRSPUser")
                                            )
                                        if (response.toString()
                                                .contains("CreateRSPUser")
                                        ) rspResponse = response.toString().substring(
                                            response.toString().indexOf("CreateRSPUser:[") + 15,
                                            response.toString().lastIndexOf("]")
                                        )
                                        Log.v("RSP TOKEN", tokenResponse)
                                        Log.v("RSP RESPONSE", rspResponse)
                                    }
                                    if (response.toString().contains("returnCode>0<", false)) {
                                        HasChangedModel.getInstance().updateChangedData("Personnel Screen","Personnel","",getPersonnelChanges(0, 0))
//                                        if (response.toString()
//                                                .contains("Duplicate ASE Certification ID")
//                                        ) {
//                                            Utility.showUnifiedErrorDialog(
//                                                activity,
//                                                "Personnel - Duplicate ASE Certification ID - Record Not Saved"
//                                            )
//                                        } else if (response.toString()
//                                                .contains("Invalid ASE Certification ID")
//                                        ) {
//                                            Utility.showUnifiedErrorDialog(
//                                                activity,
//                                                "Personnel - Invalid ASE Certification ID - Record Not Saved"
//                                            )
//                                        } else
                                        if ((response.toString()
                                                .contains("<ErrorFlag>1</ErrorFlag>")) //&& sendToRSP == 0
                                        ) {
                                            var errorMsg = response.toString().substring(
                                                response.toString().indexOf("<ErrorMsg") + 10,
                                                response.toString().indexOf("</ErrorMsg")
                                            )
                                            Utility.showUnifiedErrorDialog(
                                                activity,
                                                "Personnel -" + errorMsg
                                            )
                                        } else {
//                                            Utility.showSubmitAlertDialog(
//                                                activity,
//                                                true,
//                                                "Personnel"
//                                            )
                                            var msg = "Personnel: Record saved successfully"
                                            if (personnelResponse.isNotEmpty()) msg =
                                                "Personnel: " + personnelResponse
                                            if (tokenResponse.isNotEmpty())
                                                msg += "\nRSP Token: " + tokenResponse
                                            if (rspResponse.isNotEmpty())
                                                msg += "\nRSP Action: " + rspResponse
                                            val personnelID = response.toString().substring(
                                                response.toString().indexOf("<PersonnelID") + 13,
                                                response.toString().indexOf("</PersonnelID")
                                            ).toInt()
                                            if (personnelID == 0)
                                                Utility.showUnifiedErrorDialog(activity, msg)
                                            else {
                                                Utility.showUnifiedInformationDialog(activity, msg)
                                                var item = TblPersonnel()
                                                for (fac in TypeTablesModel.getInstance().PersonnelType) {
                                                    if (binding.newPersonnelTypeSpinner.getSelectedItem()
                                                            .toString()
                                                            .equals(fac.PersonnelTypeName)
                                                    )
                                                        item.PersonnelTypeID =
                                                            fac.PersonnelTypeID.toInt()
                                                }
                                                item.PersonnelID = response.toString().substring(
                                                    response.toString()
                                                        .indexOf("<PersonnelID") + 13,
                                                    response.toString().indexOf("</PersonnelID")
                                                ).toInt()
                                                item.FirstName =
                                                    if (binding.newFirstNameText.text.toString()
                                                            .isNullOrEmpty()
                                                    ) "" else binding.newFirstNameText.text.toString()
                                                item.LastName =
                                                    if (binding.newLastNameText.text.toString()
                                                            .isNullOrEmpty()
                                                    ) "" else binding.newLastNameText.text.toString()
                                                item.RSP_UserName =
                                                    if (binding.rspUserId.text.toString()
                                                            .isNullOrEmpty()
                                                    ) "" else binding.rspUserId.text.toString()
                                                item.RSP_Email =
                                                    if (binding.rspEmailId.text.toString()
                                                            .isNullOrEmpty()
                                                    ) "" else binding.rspEmailId.text.toString()
                                                item.RSP_Phone =
                                                    binding.newPhoneText.text.toString()
                                                item.CertificationNum =
                                                    if (binding.newCertNoText.text.toString()
                                                            .isNullOrEmpty()
                                                    ) "" else binding.newCertNoText.text.toString()
                                                item.CertificationNum_ASE =
                                                    if (binding.newASECertNoText.text.toString()
                                                            .isNullOrEmpty()
                                                    ) "" else binding.newASECertNoText.text.toString()
                                                item.ContractSigner =
                                                    binding.newSignerCheck.isChecked
                                                item.PrimaryMailRecipient =
                                                    binding.newACSCheck.isChecked
                                                item.startDate =
                                                    if (binding.newStartDateBtn.text.equals("SELECT DATE")) "" else binding.newStartDateBtn.text.toString()
                                                        .appToApiSubmitFormatMMDDYYYY()
                                                item.endDate =
                                                    if (binding.newEndDateBtn.text.equals("SELECT DATE")) "" else binding.newEndDateBtn.text.toString()
                                                        .appToApiSubmitFormatMMDDYYYY()
                                                item.SeniorityDate =
                                                    if (binding.newSeniorityDateBtn.text.equals("SELECT DATE")) "" else binding.newSeniorityDateBtn.text.toString()
                                                        .appToApiSubmitFormatMMDDYYYY()
                                                item.ReportRecipient =
                                                    binding.newReportCheck.isChecked
                                                item.NotificationRecipient =
                                                    binding.newNotificationCheck.isChecked
                                                item.ComplaintContact =
                                                    binding.newComplaintContact.isChecked
                                                HasChangedModel.getInstance().groupFacilityPersonnel[0].FacilityPersonnel =
                                                    true
                                                HasChangedModel.getInstance()
                                                    .changeDoneForFacilityPersonnel()
                                                (activity as FormsActivity).saveDone = true
                                                if (ContractSigner.toBoolean()) {
                                                    var signerItem = TblPersonnelSigner()
                                                    signerItem.Addr1 =
                                                        if (binding.newAdd1Text.text.toString()
                                                                .isNullOrEmpty()
                                                        ) "" else binding.newAdd1Text.text.toString()
                                                    signerItem.Addr2 =
                                                        if (binding.newAdd2Text.text.toString()
                                                                .isNullOrEmpty()
                                                        ) "" else binding.newAdd2Text.text.toString()
                                                    signerItem.CITY =
                                                        if (binding.newCityText.text.toString()
                                                                .isNullOrEmpty()
                                                        ) "" else binding.newCityText.text.toString()
                                                    //                                        signerItem.ST = if (newStateSpinner.selectedItem.toString().isNullOrEmpty()) "" else newStateSpinner.selectedItem.toString()
                                                    signerItem.ST =
                                                        if (binding.newStateSpinner.selectedItemPosition == 0) "" else statesAbbrev.get(
                                                            binding.newStateSpinner.selectedItemPosition
                                                        );
                                                    signerItem.ZIP =
                                                        if (binding.newZipText.text.toString()
                                                                .isNullOrEmpty()
                                                        ) "" else binding.newZipText.text.toString()
                                                    signerItem.ZIP4 =
                                                        if (binding.newZipText2.text.toString()
                                                                .isNullOrEmpty()
                                                        ) "" else binding.newZipText2.text.toString()
                                                    signerItem.Phone =
                                                        if (binding.newPhoneText.text.equals("SELECT DATE")) "" else binding.newPhoneText.text.toString()
                                                    signerItem.email =
                                                        binding.rspEmailId.text.toString()
                                                    signerItem.ContractStartDate =
                                                        if (binding.newCoStartDateBtn.text.equals("SELECT DATE")) "" else binding.newCoStartDateBtn.text.toString()
                                                            .appToApiSubmitFormatMMDDYYYY()
                                                    signerItem.PersonnelID = item.PersonnelID
                                                    item.ContractEndDate =
                                                        if (binding.newCoEndDateBtn.text.equals("SELECT DATE")) "" else binding.newCoEndDateBtn.text.toString()
                                                            .appToApiSubmitFormatMMDDYYYY()
                                                    Volley.newRequestQueue(context).add(
                                                        StringRequest(Request.Method.GET,
                                                            UpdateFacilityPersonnelSignerData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=" + FacilityDataModel.getInstance().clubCode + "&personnelId=${signerItem.PersonnelID}&addr1=${signerItem.Addr1}&addr2=${signerItem.Addr2}&city=${signerItem.CITY}&st=${signerItem.ST}&phone=${signerItem.Phone}&email=${signerItem.email}&zip=${signerItem.ZIP}&zip4=${signerItem.ZIP4}&contractStartDate=${signerItem.ContractStartDate}&contractEndDate=${item.ContractEndDate}&insertBy=${
                                                                ApplicationPrefs.getInstance(
                                                                    activity
                                                                ).loggedInUserID
                                                            }&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${
                                                                ApplicationPrefs.getInstance(
                                                                    activity
                                                                ).loggedInUserID
                                                            }&updateDate=" + Date().toApiSubmitFormat() + "&active=1",
                                                            Response.Listener { response ->
                                                                requireActivity().runOnUiThread {
                                                                    if (response.toString()
                                                                            .contains(
                                                                                "returnCode>0<",
                                                                                false
                                                                            )
                                                                    ) {
                                                                        Utility.showSubmitAlertDialog(
                                                                            activity,
                                                                            true,
                                                                            "Contract Signer"
                                                                        )
                                                                        FacilityDataModel.getInstance().tblPersonnel.add(
                                                                            item
                                                                        )
                                                                        FacilityDataModelOrg.getInstance().tblPersonnel.add(
                                                                            item
                                                                        )
                                                                        if (FacilityDataModel.getInstance().tblPersonnelSigner.filter { s -> s.PersonnelID == signerItem.PersonnelID }
                                                                                .isEmpty())
                                                                            FacilityDataModel.getInstance().tblPersonnelSigner.add(
                                                                                signerItem
                                                                            )
                                                                        else {
                                                                            FacilityDataModel.getInstance().tblPersonnelSigner.removeIf { s -> s.PersonnelID == signerItem.PersonnelID }
                                                                            FacilityDataModel.getInstance().tblPersonnelSigner.add(
                                                                                signerItem
                                                                            )
                                                                        }
                                                                        fillPersonnelTableView()
                                                                        altTableRow(2)
                                                                    } else {
                                                                        var errorMessage =
                                                                            response.toString()
                                                                                .substring(
                                                                                    response.toString()
                                                                                        .indexOf("<message") + 9,
                                                                                    response.toString()
                                                                                        .indexOf("</message")
                                                                                )
                                                                        Utility.showUnifiedErrorDialog(
                                                                            activity,
                                                                            "Contract Signer (Error: " + errorMessage + " )"
                                                                        )
                                                                    }
                                                                    binding.personnelLoadingView.visibility =
                                                                        View.GONE
                                                                    binding.personnelLoadingText.text =
                                                                        "Loading ..."
                                                                }
                                                            },
                                                            Response.ErrorListener {
                                                                Utility.showSubmitAlertDialog(
                                                                    activity,
                                                                    false,
                                                                    "Contract Signer (Error: " + it.message + " )"
                                                                )
                                                                binding.personnelLoadingView.visibility =
                                                                    View.GONE
                                                                binding.personnelLoadingText.text =
                                                                    "Loading ..."
                                                            })
                                                    )

                                                } else {
                                                    FacilityDataModel.getInstance().tblPersonnel.add(
                                                        item
                                                    )
                                                    FacilityDataModelOrg.getInstance().tblPersonnel.add(
                                                        item
                                                    )
                                                    fillPersonnelTableView()
                                                    altTableRow(2)
                                                }
                                            }
                                        }
                                    } else {
                                        var errorMessage = response.toString().substring(
                                            response.toString().indexOf("<message") + 9,
                                            response.toString().indexOf("</message")
                                        )
                                        Utility.showSubmitAlertDialog(
                                            activity,
                                            false,
                                            "Personnel (Error: " + errorMessage + " )"
                                        )
                                    }
                                    binding.personnelLoadingView.visibility = View.GONE
                                    binding.personnelLoadingText.text = "Loading ..."
                                }
                            },
                            {
                                Utility.showSubmitAlertDialog(
                                    activity,
                                    false,
                                    "Personnel (Error: " + it.message + " )"
                                )
                                binding.personnelLoadingView.visibility = View.GONE
                                binding.personnelLoadingText.text = "Loading ..."
                            })
                    ).setRetryPolicy(
                        DefaultRetryPolicy(
                            10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                    )

                } else {
                    //                Utility.showValidationAlertDialog(activity,"Please fill all the required fields")
                    Utility.showValidationAlertDialog(activity, validationMsg)
                }
            } else {
                Utility.showInternetWarningDialog(
                    requireContext(),
                    (requireActivity() as FormsActivity).networkStatusErrorMsg
                )
            }
        }
        onlyOneMailRecepientLogic()
        altTableRow(2)
        altCertTableRow(2)
    }

    private fun setAlertColoring() {
//        var ASEExpDateStr = ""
        var ASEExpDatedays = 0
        var YAlert = false
        var RAlert = false
        var toolTipStr = ""
        var currentPersonnelID = 0
        personIDWithExpiredCerts.clear()
        personIDWithAboutExpiredCerts.clear()
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        for (i in 0..FacilityDataModel.getInstance().tblPersonnelCertification.size - 1) {
            if (FacilityDataModel.getInstance().tblPersonnelCertification[i].PersonnelID > 0 && FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID == FacilityDataModel.getInstance().tblPersonnelCertification[i].PersonnelID }
                    .isNotEmpty()) {
                if (FacilityDataModel.getInstance().tblPersonnelCertification[i].CertificationTypeId.equals(
                        "L1"
                    ) || FacilityDataModel.getInstance().tblPersonnelCertification[i].CertificationTypeId.equals(
                        "C1"
                    ) || FacilityDataModel.getInstance().tblPersonnelCertification[i].CertificationTypeId.equals(
                        "A9"
                    )
                ) {

                } else {
                    // Handle issue when no dates are available and avoid crash facnum=7444 - clubcode=036
                    if (FacilityDataModel.getInstance().tblPersonnelCertification[i].ExpirationDate.apiToAppFormatMMDDYYYY()=="") continue
                    if (FacilityDataModel.getInstance().tblPersonnelCertification[i].CertificationDate.apiToAppFormatMMDDYYYY()=="") continue
                    val ASEExpDate =
                        sdf.parse(FacilityDataModel.getInstance().tblPersonnelCertification[i].ExpirationDate.apiToAppFormatMMDDYYYY())
                    val ASEExpDatedays =
                        (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                    if (ASEExpDatedays <= 0) {
                        personIDWithExpiredCerts.add(FacilityDataModel.getInstance().tblPersonnelCertification[i].PersonnelID)
                    } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                        personIDWithAboutExpiredCerts.add(FacilityDataModel.getInstance().tblPersonnelCertification[i].PersonnelID)
                    }
                    RAlert = RAlert || (ASEExpDatedays <= 0)
                    YAlert = YAlert || (ASEExpDatedays <= 180 && ASEExpDatedays > 0)
                    if (ASEExpDatedays <= 0 && FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID == FacilityDataModel.getInstance().tblPersonnelCertification[i].PersonnelID }.size > 0) {
                        if (currentPersonnelID != FacilityDataModel.getInstance().tblPersonnelCertification[i].PersonnelID) {
                            currentPersonnelID =
                                FacilityDataModel.getInstance().tblPersonnelCertification[i].PersonnelID
                            toolTipStr =
                                toolTipStr + FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID == FacilityDataModel.getInstance().tblPersonnelCertification[i].PersonnelID }[0].FirstName + " " + FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID == FacilityDataModel.getInstance().tblPersonnelCertification[i].PersonnelID }[0].LastName + " has expired Certificate(s) \n"
                        } else {

                        }
                    } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0 && FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID == FacilityDataModel.getInstance().tblPersonnelCertification[i].PersonnelID }.size > 0) {
                        if (currentPersonnelID != FacilityDataModel.getInstance().tblPersonnelCertification[i].PersonnelID) {
                            currentPersonnelID = FacilityDataModel.getInstance().tblPersonnelCertification[i].PersonnelID
                            toolTipStr = toolTipStr + FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID == FacilityDataModel.getInstance().tblPersonnelCertification[i].PersonnelID }[0].FirstName + " " + FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID == FacilityDataModel.getInstance().tblPersonnelCertification[i].PersonnelID }[0].LastName + " has about to expire Certificate(s) in ${ASEExpDatedays} day(s) \n"
                        } else {

                        }
                    }
                }
            }
        }

        binding.alertPersonnelRIcon.visibility = if (RAlert) View.VISIBLE else View.GONE
        binding.alertPersonnelYIcon.visibility =
            if (YAlert && !binding.alertPersonnelRIcon.isVisible) VISIBLE else GONE
        binding.alertPersonnelRIcon.tooltipText = toolTipStr
        binding.alertPersonnelYIcon.tooltipText = toolTipStr
        binding.alertPersonnelRIcon.isClickable = true
        binding.alertPersonnelRIcon.setOnClickListener({
            Utility.showUnifiedInformationDialog(requireContext(), toolTipStr)
        })
        binding.alertPersonnelYIcon.isClickable = true
        binding.alertPersonnelYIcon.setOnClickListener({
            Utility.showUnifiedInformationDialog(requireContext(), toolTipStr)
        })
//        if (RAlert) {
//            binding.alertPersonnelRIcon.isClickable = true
//            binding.alertPersonnelRIcon.setOnClickListener({
//                Utility.showUnifiedInformationDialog(requireContext(),toolTipStr)
//            })
//        } else {
//            binding.alertPersonnelRIcon.isClickable = false
//        }


        val animation: Animation = AlphaAnimation(1.0f, 0.0f)
        animation.duration = 500 //1 second duration for each animation cycle
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = Animation.INFINITE //repeating indefinitely
        animation.repeatMode = Animation.REVERSE //animation will start from end point once ended.
        binding.alertPersonnelRIcon.startAnimation(animation) //to start animation
        binding.alertPersonnelYIcon.startAnimation(animation) //to start animation

    }

    fun getCertificationChanges(action: Int, personnelId: Int): String {
        var strChanges = ""
        try {
            if (action == 0) {
                strChanges += "Certification for personnel  (" + FacilityDataModel.getInstance().tblPersonnel.filter { s->s.PersonnelID==personnelId }[0].FirstName + " " + FacilityDataModel.getInstance().tblPersonnel.filter { s->s.PersonnelID==personnelId }[0].LastName + ") added as: Certification Type (" + binding.newCertTypeSpinner.selectedItem.toString() + ") , Description (" + binding.newCertDescText.text.toString() + "), Start Date (" + binding.newCertStartDateBtn.text.toString() + ") and End Date (" + binding.newCertEndDateBtn.text.toString() + ")"
            }
            else if (action == 1) {
                strChanges += "Certification for personnel  (" + FacilityDataModel.getInstance().tblPersonnel.filter { s->s.PersonnelID==personnelId }[0].FirstName + " " + FacilityDataModel.getInstance().tblPersonnel.filter { s->s.PersonnelID==personnelId }[0].LastName + ") updated as: Description (" + binding.editNewCertDescText.text.toString() + "), Start Date (" + binding.editNewCertStartDateBtn.text.toString() + ") and End Date (" + binding.editNewCertEndDateBtn.text.toString() + ")"
            }
//        else { // personnelID for Edit is the rowID
//            if (edit_.text.toString() != FacilityDataModelOrg.getInstance().tblPersonnelCertification[personnelId].CertID) {
//                strChanges += "Email changed from (" + FacilityDataModelOrg.getInstance().tblFacilityEmail[rowId].email + ") to (" + newChangesEmailText.text.toString() + ") - "
//            }
//        }
            strChanges = strChanges.removeSuffix(" - ")
        } catch (e: Exception) {
            Log.v("Error", e.message.toString())
        }
        return strChanges
    }

    private fun getPersonnelChanges(action: Int, rowId: Int): String {
        var strChanges = ""
        try {
            if (action == 0) {
                strChanges += "New personnel added with first name (" + if (binding.newFirstNameText.text.toString()
                        .isEmpty()
                ) "" else binding.newFirstNameText.text.toString() + ") , last name (" + if (binding.newLastNameText.text.toString()
                        .isEmpty()
                ) "" else binding.newLastNameText.text.toString()
                strChanges += "), position (" + binding.newPersonnelTypeSpinner.getSelectedItem()
                    .toString() + ") and start date (" + if (binding.newStartDateBtn.text.equals("SELECT DATE")) "" else binding.newStartDateBtn.text.toString()
                strChanges += "), end date (" + if (binding.newEndDateBtn.text.equals("SELECT DATE")) "" else binding.newEndDateBtn.text.toString() + ") and certification ID # (" + if (binding.newCertNoText.text.toString()
                        .isEmpty()
                ) "" else binding.newCertNoText.text.toString() + ") and ASE Certification ID # (" + if (binding.newASECertNoText.text.toString()
                        .isEmpty()
                ) "" else binding.newASECertNoText.text.toString()
                strChanges += "), Seniority date (" + if (binding.newSeniorityDateBtn.text.equals("SELECT DATE")) "" else binding.newSeniorityDateBtn.text.toString() + ") and contract signer (" + if (binding.newSignerCheck.isChecked == true) "true" else "false"
                strChanges += ") and RSP User ID (" + binding.rspUserId.text.toString() + ")"
                strChanges += ") and RSP Email (" + binding.rspEmailId.text.toString() + ")"
                strChanges += ") and primary mail recipient (" + if (binding.newACSCheck.isChecked == true) "true" else "false" + ")"
                strChanges += ") and Notification recipient (" + if (binding.newNotificationCheck.isChecked == true) "true" else "false" + ")"
                strChanges += ") and Report recipient (" + if (binding.newReportCheck.isChecked == true) "true" else "false" + ")"
                strChanges += ") and Complaint Contact (" + if (binding.newComplaintContact.isChecked == true) "true" else "false" + ")"
            } else {
                if (binding.editNewFirstNameText.text.toString() != FacilityDataModelOrg.getInstance().tblPersonnel[rowId].FirstName) {
                    strChanges += "First Name changed from (" + FacilityDataModelOrg.getInstance().tblPersonnel[rowId].FirstName + ") to (" + binding.editNewFirstNameText.text.toString() + ") - "
                }
                if (binding.editNewLastNameText.text.toString() != FacilityDataModelOrg.getInstance().tblPersonnel[rowId].LastName) {
                    strChanges += "Last Name changed from (" + FacilityDataModelOrg.getInstance().tblPersonnel[rowId].LastName + ") to (" + binding.editNewLastNameText.text.toString() + ") - "
                }
                if (binding.editRspUserId.text.toString() != FacilityDataModelOrg.getInstance().tblPersonnel[rowId].RSP_UserName) {
                    strChanges += "RSP User ID changed from (" + FacilityDataModelOrg.getInstance().tblPersonnel[rowId].RSP_UserName + ") to (" + binding.editRspUserId.text.toString() + ") - "
                }
                if (binding.editRspEmailId.text.toString() != FacilityDataModelOrg.getInstance().tblPersonnel[rowId].RSP_Email) {
                    strChanges += "RSP Email changed from (" + FacilityDataModelOrg.getInstance().tblPersonnel[rowId].RSP_Email + ") to (" + binding.editRspEmailId.text.toString() + ") - "
                }
                if (binding.editNewPersonnelTypeSpinner.getSelectedItem()
                        .toString() != (TypeTablesModel.getInstance().PersonnelType.filter { s ->
                        s.PersonnelTypeID.toInt()
                            .equals(FacilityDataModelOrg.getInstance().tblPersonnel[rowId].PersonnelTypeID)
                    }[0].PersonnelTypeName)
                ) {
                    strChanges += "Position changed from (" + TypeTablesModel.getInstance().PersonnelType.filter { s ->
                        s.PersonnelTypeID.equals(
                            FacilityDataModelOrg.getInstance().tblPersonnel[rowId].PersonnelTypeID.toString()
                        )
                    }[0].PersonnelTypeName + ") to (" + binding.editNewPersonnelTypeSpinner.getSelectedItem()
                        .toString() + ") - "
                }
                if (!binding.editNewStartDateBtn.text.toString().equals("SELECT DATE")) {
                    if (binding.editNewStartDateBtn.text.toString() != FacilityDataModelOrg.getInstance().tblPersonnel[rowId].startDate.apiToAppFormatMMDDYYYY()) {
                        strChanges += "Start Date changed from (" + FacilityDataModelOrg.getInstance().tblPersonnel[rowId].startDate.apiToAppFormatMMDDYYYY() + ") to (" + binding.editNewStartDateBtn.text.toString() + ") - "
                    }
                }
                if (!binding.editNewEndDateBtn.text.toString().equals("SELECT DATE")) {
                    if (binding.editNewEndDateBtn.text.toString() != FacilityDataModelOrg.getInstance().tblPersonnel[rowId].endDate.apiToAppFormatMMDDYYYY()) {
                        strChanges += "End Date changed from (" + FacilityDataModelOrg.getInstance().tblPersonnel[rowId].endDate.apiToAppFormatMMDDYYYY() + ") to (" + binding.editNewEndDateBtn.text.toString() + ") - "
                    }
                }
                if (!binding.editNewSeniorityDateBtn.text.toString().equals("SELECT DATE")) {
                    if (binding.editNewSeniorityDateBtn.text.toString() != FacilityDataModelOrg.getInstance().tblPersonnel[rowId].SeniorityDate.apiToAppFormatMMDDYYYY()) {
                        strChanges += "End Date changed from (" + FacilityDataModelOrg.getInstance().tblPersonnel[rowId].SeniorityDate.apiToAppFormatMMDDYYYY() + ") to (" + binding.editNewSeniorityDateBtn.text.toString() + ") - "
                    }
                }
                if (binding.editNewSignerCheck.isChecked != FacilityDataModelOrg.getInstance().tblPersonnel[rowId].ContractSigner) {
                    strChanges += "Contract Signer flag changed from (" + FacilityDataModelOrg.getInstance().tblPersonnel[rowId].ContractSigner + ") to (" + binding.editNewSignerCheck.isChecked + ") - "
                }
                if (binding.editNewACSCheck.isChecked != FacilityDataModelOrg.getInstance().tblPersonnel[rowId].PrimaryMailRecipient) {
                    strChanges += "Primary Mail Recipient changed from (" + FacilityDataModelOrg.getInstance().tblPersonnel[rowId].PrimaryMailRecipient + ") to (" + binding.editNewACSCheck.isChecked + ") - "
                }
                if (binding.editNewComplaintCheck.isChecked != FacilityDataModelOrg.getInstance().tblPersonnel[rowId].ComplaintContact) {
                    strChanges += "Complaint Contact changed from (" + FacilityDataModelOrg.getInstance().tblPersonnel[rowId].ComplaintContact + ") to (" + binding.editNewComplaintCheck.isChecked + ") - "
                }
            }
            strChanges = strChanges.removeSuffix(" - ")
        } catch (e: Exception) {
            Log.v("Error", e.message.toString())
        }
        return strChanges
    }

//    var ContractSigner=
//    var PrimaryMailRecipient=

    var isFirstRun: Boolean = true

    fun enable_contractSignerFeilds() {

        binding.newEmailText.isEnabled = true
        binding.newCoStartDateBtn.isEnabled = true
        // SAEED MOSS ISSUE
        //newPhoneText.isEnabled = true

        binding.newZipText.isEnabled = true
        binding.newCityText.isEnabled = true
        binding.newAdd1Text.isEnabled = true
        binding.newStateSpinner.isEnabled = true
        binding.newZipText2.isEnabled = true
        binding.newAdd2Text.isEnabled = true
        binding.stateTextView.isEnabled = true
        binding.phoneTextId.isEnabled = true
        binding.zipCodeTextId.isEnabled = true
        binding.emailAddressTextId.isEnabled = true
        binding.contractSignerStartDateTextId.isEnabled = true
        binding.contractSignerEndDateTextId.isEnabled = true
        binding.newCoEndDateBtn.isEnabled = true
        binding.cityTextId.isEnabled = true
        binding.address2TextId.isEnabled = true
        binding.address1TextId.isEnabled = true
        binding.newCoStartDateBtn.setTextColor(
            binding.newCoStartDateBtn.getContext().getResources().getColor(R.color.blue)
        );
        binding.newCoEndDateBtn.setTextColor(
            binding.newCoStartDateBtn.getContext().getResources().getColor(R.color.blue)
        );
        binding.contractSignerFieldsLinearLayourId.setBackgroundColor(
            binding.newCoStartDateBtn.getContext().getResources().getColor(R.color.white)
        );

    }

    fun disablecontractSignerFeilds() {

        binding.newEmailText.isEnabled = false
        binding.newCoStartDateBtn.isEnabled = false

        binding.newPhoneText.isEnabled = false

        binding.newZipText.isEnabled = false
        binding.newCityText.isEnabled = false
        binding.newAdd1Text.isEnabled = false
        binding.newStateSpinner.isEnabled = false
        binding.newZipText2.isEnabled = false
        binding.newAdd2Text.isEnabled = false
        binding.stateTextView.isEnabled = false
        binding.phoneTextId.isEnabled = true
        binding.zipCodeTextId.isEnabled = false
        binding.emailAddressTextId.isEnabled = false
        binding.contractSignerStartDateTextId.isEnabled = false
        binding.contractSignerEndDateTextId.isEnabled = false
        binding.newCoEndDateBtn.isEnabled = false
        binding.cityTextId.isEnabled = false
        binding.address2TextId.isEnabled = false
        binding.address1TextId.isEnabled = false
        binding.newCoStartDateBtn.setTextColor(
            binding.newCoStartDateBtn.getContext().getResources().getColor(R.color.gray)
        );
        binding.newCoEndDateBtn.setTextColor(
            binding.newCoStartDateBtn.getContext().getResources().getColor(R.color.gray)
        );
        binding.contractSignerFieldsLinearLayourId.setBackgroundColor(
            binding.newCoStartDateBtn.getContext().getResources()
                .getColor(R.color.contractSignerFieldsAreDisabledColor)
        );

    }

    fun edit_disableContractSignerIsChecked() {

        binding.editNewEmailText.isEnabled = false
        binding.editNewCoStartDateBtn.isEnabled = false
        // SAEED MOSS ISSUE
        // edit_newPhoneText.isEnabled = false
        binding.editNewPhoneText.isEnabled = true
        binding.editNewEndDateBtn.isEnabled = true
        binding.editNewStartDateBtn.isEnabled = true

        binding.editNewZipText.isEnabled = false
        binding.editNewCityText.isEnabled = false
        binding.editNewAdd1Text.isEnabled = false
        binding.editNewStateSpinner.isEnabled = false
        binding.editNewZipText2.isEnabled = false
        binding.editNewAdd2Text.isEnabled = false
        binding.editStateTextView.isEnabled = false
        binding.editPhoneTextId.isEnabled = false
        binding.editZipCodeTextId.isEnabled = false
        binding.editEmailAddressTextId.isEnabled = false
        binding.editContractSignerEndDateTextId.isEnabled = false
        binding.editContractSignerEndDateTextId.isEnabled = false
        binding.editNewCoEndDateBtn.isEnabled = false
        binding.editCityTextId.isEnabled = false
        binding.editAddress2TextId.isEnabled = false
        binding.editAddress1TextId.isEnabled = false
        binding.editNewCoStartDateBtn.setTextColor(
            binding.editNewCoStartDateBtn.getContext().getResources().getColor(R.color.gray)
        );
        binding.editNewCoEndDateBtn.setTextColor(
            binding.editNewCoEndDateBtn.getContext().getResources().getColor(R.color.gray)
        );
        binding.editContractSignerFieldsLinearLayourId.setBackgroundColor(
            binding.editContractSignerFieldsLinearLayourId.getContext().getResources()
                .getColor(R.color.contractSignerFieldsAreDisabledColor)
        );

    }

    fun edit_enableContractSignerIsChecked() {

        binding.editNewEmailText.isEnabled = false
        binding.editNewCoStartDateBtn.isEnabled = false
        // SAEED MOSS ISSUE
        binding.editNewPhoneText.isEnabled = false
        binding.editNewEndDateBtn.isEnabled = false
        binding.editNewStartDateBtn.isEnabled = false

        binding.editNewZipText.isEnabled = false
        binding.editNewCityText.isEnabled = false
        binding.editNewAdd1Text.isEnabled = false
        binding.editNewStateSpinner.isEnabled = false
        binding.editNewZipText2.isEnabled = false
        binding.editNewAdd2Text.isEnabled = false
        binding.editStateTextView.isEnabled = false // 3.08
        binding.editPhoneTextId.isEnabled = false // 3.08

        binding.editRspEmailId.isEnabled = false // 3.08
        binding.editRspUserId.isEnabled = false // 3.08

        binding.editZipCodeTextId.isEnabled = true
        binding.editEmailAddressTextId.isEnabled = true
        binding.editContractSignerStartDateTextId.isEnabled = true
        binding.editContractSignerEndDateTextId.isEnabled = true
        binding.editNewCoEndDateBtn.isEnabled = false
        binding.editCityTextId.isEnabled = true
        binding.editAddress2TextId.isEnabled = true
        binding.editAddress1TextId.isEnabled = true
        binding.editNewCoStartDateBtn.setTextColor(
            binding.editNewCoStartDateBtn.getContext().getResources().getColor(R.color.green)
        );
        binding.editNewCoEndDateBtn.setTextColor(
            binding.editNewCoEndDateBtn.getContext().getResources().getColor(R.color.green)
        );
        binding.editContractSignerFieldsLinearLayourId.setBackgroundColor(
            binding.editContractSignerFieldsLinearLayourId.getContext().getResources()
                .getColor(R.color.white)
        );


    }

    fun preparePersonnelPage() {
        isFirstRun = false
        personnelTypeList = TypeTablesModel.getInstance().PersonnelType
        personTypeArray.clear()
        personTypeIDsArray.clear()
        personTypeIDsArray.add("-1")
        personTypeArray.add("Not Selected")
        for (fac in personnelTypeList) {
            personTypeArray.add(fac.PersonnelTypeName)
            personTypeIDsArray.add(fac.PersonnelTypeID)
        }
        var personTypeAdapter = ArrayAdapter<String>(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            personTypeArray
        )
        personTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.newPersonnelTypeSpinner.adapter = personTypeAdapter
        binding.editNewPersonnelTypeSpinner.adapter = personTypeAdapter



        binding.newPersonnelTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedTitle = binding.newPersonnelTypeSpinner.selectedItem.toString()
                    if (TypeTablesModel.getInstance().PersonnelType.filter { s -> s.PersonnelTypeName == selectedTitle }
                            .isNotEmpty()) {
                        if (TypeTablesModel.getInstance().PersonnelType.filter { s -> s.PersonnelTypeName == selectedTitle }[0].RspRole.equals(
                                "No Access"
                            )
                        ) {
                            binding.roleHint.visibility = View.VISIBLE
                        } else {
                            binding.roleHint.visibility = View.GONE
                        }
                    } else {
                        binding.roleHint.visibility = View.GONE
                    }
                }
            }

        binding.editNewPersonnelTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedTitle = binding.editNewPersonnelTypeSpinner.selectedItem.toString()
                    if (TypeTablesModel.getInstance().PersonnelType.filter { s -> s.PersonnelTypeName == selectedTitle }
                            .isNotEmpty()) {
                        if (TypeTablesModel.getInstance().PersonnelType.filter { s -> s.PersonnelTypeName == selectedTitle }[0].RspRole.equals(
                                "No Access"
                            )
                        ) {
                            binding.editRoleHint.visibility = View.VISIBLE
                        } else {
                            binding.editRoleHint.visibility = View.GONE
                        }
                    } else {
                        binding.editRoleHint.visibility = View.GONE
                    }
                }
            }

        certificationTypeList = TypeTablesModel.getInstance().PersonnelCertificationType
        certTypeArray.clear()
        certTypeArray.add("Not Selected")
        for (fac in certificationTypeList) {
            certTypeArray.add(fac.PersonnelCertName)
        }
        var certTypeAdapter = ArrayAdapter<String>(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            certTypeArray
        )
        certTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.newCertTypeSpinner.adapter = certTypeAdapter

        var citiesAdapter =
            ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, states)
        citiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.newStateSpinner.adapter = citiesAdapter
        binding.editNewStateSpinner.adapter = citiesAdapter
// HERE


        binding.newCertCatSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    certTypeArray.clear()
                    certTypeArray.add("Not Selected")
                    for (fac in certificationTypeList) {
                        if (binding.newCertCatSpinner.selectedItem.toString().equals("ASE")) {
                            if (fac.Category.equals(binding.newCertCatSpinner.selectedItem.toString()) || fac.Category.isNullOrEmpty())
                                certTypeArray.add(fac.PersonnelCertName)
                        } else {
                            if (fac.Category.equals(binding.newCertCatSpinner.selectedItem.toString()))
                                certTypeArray.add(fac.PersonnelCertName)
                        }
                    }
                    binding.newCertTypeSpinner.setSelection(0);
                }
            }
        binding.editNewCertTypeSpinner.isEnabled = false
        binding.editNewCertCatSpinner.isEnabled = false
    }

    fun getTypeName(typeID: String): String {
        var typeName = "Not Selected"
        for (fac in personnelTypeList) {
            if (fac.PersonnelTypeID.equals(typeID)) {
                typeName = fac.PersonnelTypeName
            }
        }
        return typeName
    }

    fun fillData() {
        endDateMustBeAfterStartDateLogic()
        edit_endDateMustBeAfterStartDateLogic()
        binding.newZipText.addTextChangedListener(zipOfFiveDigitsWatcher)
        binding.newZipText2.addTextChangedListener(zipOfFourDigitsWatcher)
        binding.newPhoneText.addTextChangedListener(phoneTenDigitsWatcher)
        binding.newEmailText.addTextChangedListener(emailValidationWatcher)
        binding.newACEURLText.addTextChangedListener(addNewAceUrlWatcher)
        binding.newEditACEURLText.addTextChangedListener(editAceUrlWatcher)
    }

    fun emailFormatValidation(target: CharSequence): Boolean {
        emailValid = !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
        return emailValid
    }


    var emailValidationWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable) {
            if (!emailFormatValidation(binding.newEmailText.text.toString())) {
                binding.newEmailText.setError("assure email format standards")
            }
        }
    }


    var editAceUrlWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            edithyperlinktxt =
                "<a href='" + binding.newEditACEURLText.text.toString() + "'>" + binding.newEditACEURLText.text.toString() + "</a>"
            binding.editurlLink.text = Html.fromHtml(edithyperlinktxt, Html.FROM_HTML_MODE_COMPACT)
        }

        override fun afterTextChanged(s: Editable) {

        }
    }

    var addNewAceUrlWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            hyperlinktxt =
                "<a href='" + binding.newACEURLText.text.toString() + "'>" + binding.newACEURLText.text.toString() + "</a>"
            binding.urlLink.text = Html.fromHtml(hyperlinktxt, Html.FROM_HTML_MODE_COMPACT)
        }

        override fun afterTextChanged(s: Editable) {

        }
    }


    var zipOfFiveDigitsWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {


            if (s.length > 5 || s.length < 5) {

                binding.newZipText.setError("input required 5 elements")
                zipFormat = false

            } else zipFormat = true


        }
    }
    var zipOfFourDigitsWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {


            if (s.length > 4 || s.length < 4) {

                binding.newZipText2.setError("input required 4 elements")

            }


        }
    }
    var phoneTenDigitsWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {


            if (s.length > 10 || s.length < 10) {

                binding.newPhoneText.setError("input required 10 elements")


            }


        }
    }

    fun endDateMustBeAfterStartDateLogic() {

        binding.newCoEndDateBtn.setOnClickListener {
            if (binding.newCoStartDateBtn.text.toString().uppercase(getDefault()).equals("SELECT DATE")) {
                binding.newCoEndDateBtn.setError("Required Field")
                Utility.showValidationAlertDialog(activity, "Please enter Start Date")
            } else {
                binding.newCoEndDateBtn.setError(null)
                val c = Calendar.getInstance()
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                if (!binding.newCoEndDateBtn.text.toString().equals("SELECT DATE")) {
                    var currentDate = (sdf.parse(binding.newCoEndDateBtn.text.toString()))
                    c.setTime(currentDate)
                }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(
                    requireActivity(),
                    R.style.CustomDatePickerDialogTheme,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        val myFormat = "MM/dd/yyyy" // mention the format you need
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        c.set(year, monthOfYear, dayOfMonth)
                        binding.newCoEndDateBtn!!.text = sdf.format(c.time)
                    },
                    year,
                    month,
                    day
                )
                dpd.show()

            }

        }
        binding.newEndDateBtn.setOnClickListener {
            if (binding.newStartDateBtn.text.toString().uppercase(getDefault()).equals("SELECT DATE")) {
                binding.newStartDateBtn.setError("Required Field")
                Utility.showValidationAlertDialog(activity, "Please enter Start Date")
            } else {
                binding.newEndDateBtn.setError(null)
                val c = Calendar.getInstance()
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                if (!binding.newEndDateBtn.text.toString().equals("SELECT DATE")) {
                    var currentDate = (sdf.parse(binding.newEndDateBtn.text.toString()))
                    c.setTime(currentDate)
                }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(
                    requireActivity(),
                    R.style.CustomDatePickerDialogTheme,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        // Display Selected date in textbox
                        val myFormat = "MM/dd/yyyy" // mention the format you need
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        c.set(year, monthOfYear, dayOfMonth)
                        binding.newEndDateBtn!!.text = sdf.format(c.time)
                    },
                    year,
                    month,
                    day
                )
                dpd.show()

            }

        }
        binding.newCertEndDateBtn.setOnClickListener {
            if (binding.newCertStartDateBtn.text.toString().uppercase(getDefault()).equals("SELECT DATE")) {
                binding.newCertStartDateBtn.setError("Required Field")
                Utility.showValidationAlertDialog(activity, "Please enter Certificate Start Date")
            } else {
                binding.newCertStartDateBtn.setError(null)
                val c = Calendar.getInstance()
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                if (!binding.newCertStartDateBtn.text.toString().equals("SELECT DATE")) {
                    var currentDate = (sdf.parse(binding.newCertStartDateBtn.text.toString()))
                    c.setTime(currentDate)
                }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(
                    requireActivity(),
                    R.style.CustomDatePickerDialogTheme,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        // Display Selected date in textbox
                        val myFormat = "MM/dd/yyyy" // mention the format you need
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        c.set(year, monthOfYear, dayOfMonth)
                        binding.newCertEndDateBtn!!.text = sdf.format(c.time)
                    },
                    year,
                    month,
                    day
                )
                dpd.show()

            }

        }

        binding.editNewCertEndDateBtn.setOnClickListener {
            if (binding.editNewCertStartDateBtn.text.toString().uppercase(getDefault())
                    .equals("SELECT DATE")
            ) {
                binding.editNewCertStartDateBtn.setError("Required Field")
                Utility.showValidationAlertDialog(activity, "Please enter Certificate Start Date")
            } else {
                binding.editNewCertEndDateBtn.setError(null)
                val c = Calendar.getInstance()
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                if (!binding.editNewCertEndDateBtn.text.toString().equals("SELECT DATE")) {
                    var currentDate = (sdf.parse(binding.editNewCertEndDateBtn.text.toString()))
                    c.setTime(currentDate)
                }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(
                    requireActivity(),
                    R.style.CustomDatePickerDialogTheme,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        // Display Selected date in textbox
                        val myFormat = "MM/dd/yyyy" // mention the format you need
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        c.set(year, monthOfYear, dayOfMonth)
                        binding.editNewCertEndDateBtn!!.text = sdf.format(c.time)
                    },
                    year,
                    month,
                    day
                )
                dpd.show()

            }

        }

    }

    fun edit_endDateMustBeAfterStartDateLogic() {

        binding.editNewCoEndDateBtn.setOnClickListener(View.OnClickListener {
            if (binding.editNewCoStartDateBtn.text.toString().uppercase(getDefault()).equals("SELECT DATE")) {

                binding.editNewCoEndDateBtn.setError("Required Field")
                Utility.showValidationAlertDialog(activity, "Please enter Contract End Date")
//                Toast.makeText(context,"please enter a start date first",Toast.LENGTH_LONG).show()
            } else {
                binding.editNewCoEndDateBtn.setError(null)
                val c = Calendar.getInstance()
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                if (!binding.editNewCoEndDateBtn.text.toString().equals("SELECT DATE")) {
                    var currentDate = (sdf.parse(binding.editNewCoEndDateBtn.text.toString()))
                    c.setTime(currentDate)
                }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(
                    requireActivity(),
                    R.style.CustomDatePickerDialogTheme,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        // Display Selected date in textbox
                        val myFormat = "MM/dd/yyyy" // mention the format you need
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        c.set(year, monthOfYear, dayOfMonth)
                        binding.editNewCoEndDateBtn!!.text = sdf.format(c.time)
                    },
                    year,
                    month,
                    day
                )
                dpd.show()

            }

        })
        binding.editNewEndDateBtn.setOnClickListener(View.OnClickListener {
            if (binding.editNewStartDateBtn.text.toString().uppercase(getDefault()).equals("SELECT DATE")) {
                binding.editNewEndDateBtn.setError("Required Field")
                Utility.showValidationAlertDialog(activity, "Please enter ِEnd Date")
            } else {
                binding.editNewEndDateBtn.setError(null)
                val c = Calendar.getInstance()
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                if (!binding.editNewEndDateBtn.text.toString().equals("SELECT DATE")) {
                    var currentDate = (sdf.parse(binding.editNewEndDateBtn.text.toString()))
                    c.setTime(currentDate)
                }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(
                    requireActivity(),
                    R.style.CustomDatePickerDialogTheme,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        // Display Selected date in textbox
                        val myFormat = "MM/dd/yyyy" // mention the format you need
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        c.set(year, monthOfYear, dayOfMonth)
                        binding.editNewEndDateBtn!!.text = sdf.format(c.time)
                    },
                    year,
                    month,
                    day
                )
                dpd.show()

            }

        })
    }


//    fun validateInputs(): Boolean {
//        var isInputsValid = true
//
//        firstName_textviewVal.setError(null)
//        lastName_textviewVal.setError(null)
//        coSignerAddr1Val.setError(null)
//        coSignerAddr2Val.setError(null)
//        coSignerCityVal.setError(null)
//        coSignerZip4Val.setError(null)
//        coSignerZipVal.setError(null)
//        coSignerEmailVal.setError(null)
//        coSignerPhoneVal.setError(null)
//        coSignerCoEndDateVal.setError(null)
//        coSignerCoStartDateVal.setError(null)
//        a1CertDateVal.setError(null)
//        a1ExpDateVal.setError(null)
//        a2CertDateVal.setError(null)
//        a2ExpDateVal.setError(null)
//        a3CertDateVal.setError(null)
//        a3ExpDateVal.setError(null)
//        a4CertDateVal.setError(null)
//        a4ExpDateVal.setError(null)
//        a5CertDateVal.setError(null)
//        a5ExpDateVal.setError(null)
//        a6CertDateVal.setError(null)
//        a6ExpDateVal.setError(null)
//        a7CertDateVal.setError(null)
//        a7ExpDateVal.setError(null)
//        a8CertDateVal.setError(null)
//        a8ExpDateVal.setError(null)
//        c1CertDateVal.setError(null)
//        c1ExpDateVal.setError(null)
//
//        if (firstName_textviewVal.text.toString().isNullOrEmpty()) {
//            isInputsValid = false
//            firstName_textviewVal.setError("Required Field")
//        }
//
//        if (lastName_textviewVal.text.toString().isNullOrEmpty()) {
//            isInputsValid = false
//            lastName_textviewVal.setError("Required Field")
//        }
//
//        if (contractSignerCheckBox.isChecked) {
//            if (coSignerAddr1Val.text.toString().isNullOrEmpty()) {
//                isInputsValid = false
//                coSignerAddr1Val.setError("Required Field")
//            }
//            if (coSignerAddr2Val.text.toString().isNullOrEmpty()) {
//                isInputsValid = false
//                coSignerAddr2Val.setError("Required Field")
//            }
//            if (coSignerCityVal.text.toString().isNullOrEmpty()) {
//                isInputsValid = false
//                coSignerCityVal.setError("Required Field")
//            }
//            if (coSignerZipVal.text.toString().isNullOrEmpty()) {
//                isInputsValid = false
//                coSignerZipVal.setError("Required Field")
//            }
//            if (coSignerZip4Val.text.toString().isNullOrEmpty()) {
//                isInputsValid = false
//                coSignerZip4Val.setError("Required Field")
//            }
//            if (coSignerPhoneVal.text.toString().isNullOrEmpty()) {
//                isInputsValid = false
//                coSignerPhoneVal.setError("Required Field")
//            }
//            if (coSignerEmailVal.text.toString().isNullOrEmpty()) {
//                isInputsValid = false
//                coSignerEmailVal.setError("Required Field")
//            }
//            if (coSignerCoStartDateVal.text.toString().isNullOrEmpty()) {
//                isInputsValid = false
//                coSignerCoStartDateVal.setError("Required Field")
//            }
//            if (coSignerCoEndDateVal.text.toString().isNullOrEmpty()) {
//                isInputsValid = false
//                coSignerCoEndDateVal.setError("Required Field")
//            }
//        }
//        if (!a1CertDateVal.text.equals("SELECT DATE")) {
//            if (a1ExpDateVal.text.equals("SELECT DATE")) {
//                isInputsValid = false
//                a1ExpDateVal.setError("Required Field")
//            }
//        }
//
//        if (!a2CertDateVal.text.equals("SELECT DATE")) {
//            if (a2ExpDateVal.text.equals("SELECT DATE")) {
//                isInputsValid = false
//                a2ExpDateVal.setError("Required Field")
//            }
//        }
//
//        if (!a3CertDateVal.text.equals("SELECT DATE")) {
//            if (a3ExpDateVal.text.equals("SELECT DATE")) {
//                isInputsValid = false
//                a3ExpDateVal.setError("Required Field")
//            }
//        }
//
//        if (!a4CertDateVal.text.equals("SELECT DATE")) {
//            if (a4ExpDateVal.text.equals("SELECT DATE")) {
//                isInputsValid = false
//                a4ExpDateVal.setError("Required Field")
//            }
//        }
//
//        if (!a5CertDateVal.text.equals("SELECT DATE")) {
//            if (a5ExpDateVal.text.equals("SELECT DATE")) {
//                isInputsValid = false
//                a5ExpDateVal.setError("Required Field")
//            }
//        }
//
//        if (!a6CertDateVal.text.equals("SELECT DATE")) {
//            if (a6ExpDateVal.text.equals("SELECT DATE")) {
//                isInputsValid = false
//                a6ExpDateVal.setError("Required Field")
//            }
//        }
//
//        if (!a7CertDateVal.text.equals("SELECT DATE")) {
//            if (a7ExpDateVal.text.equals("SELECT DATE")) {
//                isInputsValid = false
//                a7ExpDateVal.setError("Required Field")
//            }
//        }
//
//        if (!a8CertDateVal.text.equals("SELECT DATE")) {
//            if (a8ExpDateVal.text.equals("SELECT DATE")) {
//                isInputsValid = false
//                a8ExpDateVal.setError("Required Field")
//            }
//        }
//
//        if (!c1CertDateVal.text.equals("SELECT DATE")) {
//            if (c1ExpDateVal.text.equals("SELECT DATE")) {
//                isInputsValid = false
//                c1ExpDateVal.setError("Required Field")
//            }
//        }
//
//
//        return isInputsValid
//    }

//PersonnelDetailsTbl

    fun fillPersonnelDetailsTableView(personnelID: Int) {
        binding.detailsView.visibility = View.VISIBLE
        FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID.equals(personnelID) }
            .apply {
                (0 until size).forEach {
                    binding.dtlsType.text = getTypeName(get(it).PersonnelTypeID.toString())
                    binding.dtlsFirstName.text = get(it).FirstName
                    binding.dtlsLastName.text = get(it).LastName
                    binding.dtlsRSPUser.text = get(it).RSP_UserName
                    binding.dtlsEmail.text =
                        get(it).RSP_Email//if (get(it).ContractSigner) FacilityDataModel.getInstance().tblPersonnelSigner.filter { s -> s.PersonnelID == get(it).PersonnelID }[0].email else get(it).RSP_Email
                    binding.dtlsStartDate.text = get(it).RSP_Email
                    if (!(get(it).SeniorityDate.isNullOrEmpty())) {
                        try {
                            binding.dtlsSeniorityDate.text =
                                if (get(it).SeniorityDate.apiToAppFormatMMDDYYYY()
                                        .equals("01/01/1900")
                                ) "" else get(it).SeniorityDate.apiToAppFormatMMDDYYYY()
                        } catch (e: Exception) {
                            binding.dtlsSeniorityDate.text =
                                get(it).SeniorityDate.apiToAppFormatMMDDYYYY()
                        }
                    } else {
                        binding.dtlsSeniorityDate.text = ""
                    }
                    if (!(get(it).startDate.isNullOrEmpty())) {
                        try {
                            binding.dtlsStartDate.text =
                                if (get(it).startDate.apiToAppFormatMMDDYYYY()
                                        .equals("01/01/1900")
                                ) "" else get(it).startDate.apiToAppFormatMMDDYYYY()
                        } catch (e: Exception) {
                            binding.dtlsStartDate.text = get(it).startDate.apiToAppFormatMMDDYYYY()
                        }
                    } else {
                        binding.dtlsStartDate.text = ""
                    }
                    if (!(get(it).endDate.isNullOrEmpty())) {
                        try {
                            binding.dtlsEndDate.text = if (get(it).endDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else get(it).endDate.apiToAppFormatMMDDYYYY()
                        } catch (e: Exception) {
                            binding.dtlsEndDate.text = get(it).endDate.apiToAppFormatMMDDYYYY()
                        }
                    } else {
                        binding.dtlsEndDate.text = ""
                    }
                    binding.dtlsCertID.text = get(it).CertificationNum
                    binding.dtlsASECertID.text = get(it).CertificationNum_ASE
                    binding.dtlsASEUrl.text = get(it).ASE_Cert_URL
                    binding.contractSignerCB.isChecked = get(it).ContractSigner
                    binding.complaintCB.isChecked = get(it).ComplaintContact
                    binding.notificationCB.isChecked = get(it).NotificationRecipient
                    binding.ReportCB.isChecked = get(it).ReportRecipient
                    binding.primaryMailCB.isChecked = get(it).PrimaryMailRecipient
                }
            }
    }


    fun fillCertificationGrid() {
        if (binding.certificateGridTblLayout.childCount > 1) {
            for (i in binding.certificateGridTblLayout.childCount - 1 downTo 1) {
                binding.certificateGridTblLayout.removeViewAt(i)
            }
        }

        binding.certificateGridTitle.setText(FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + " - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName)
        var strASEListText = "ASE:"
        var strOEMListText = "OEM:"
        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.column = 0
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0
        rowLayoutParam.weight = 1.5F
        rowLayoutParam.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1.5F
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

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.width = 0
        rowLayoutParam3.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.width = 0
        rowLayoutParam4.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 1F
        rowLayoutParam5.column = 5
        rowLayoutParam5.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam5.width = 0
        rowLayoutParam5.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParamRow.weight = 1F

        val tableRow = TableRow(context)
        tableRow.layoutParams = rowLayoutParamRow
        tableRow.setBackgroundColor(Color.LTGRAY)
        tableRow.minimumHeight = 30

        val textView = TextView(context)
        textView.layoutParams = rowLayoutParam
        textView.gravity = Gravity.CENTER
        textView.text = "Tech. Name"
        textView.textSize = 14f
        textView.setTextColor(Color.BLACK)
        tableRow.addView(textView)

        val textView1 = TextView(context)
        textView1.layoutParams = rowLayoutParam1
        textView1.gravity = Gravity.CENTER
        textView1.text = "Certification #"
        textView1.textSize = 12f
        textView1.setTextColor(Color.BLACK)
        tableRow.addView(textView1)

        TypeTablesModel.getInstance().PersonnelCertificationType.filter { s -> s.Category.equals("ASE") }
            .apply {
                (0 until size).forEach {
                    strASEListText += " - " + get(it).PersonnelCertName

                    val textView2 = TextView(context)
                    textView2.layoutParams = rowLayoutParam2
                    textView2.gravity = Gravity.CENTER
                    textView2.text = get(it).PersonnelCertID
                    textView2.textSize = 12f
                    textView2.setTextColor(Color.BLACK)
                    tableRow.addView(textView2)
                }
            }
        binding.ASEListText.setText(strASEListText)

        binding.certificateGridTblLayout.addView(tableRow)

        TypeTablesModel.getInstance().PersonnelCertificationType.filter { s -> s.Category.equals("OEM") }
            .apply {
                (0 until size).forEach {
                    strOEMListText += " - " + get(it).PersonnelCertName
                }
            }
        binding.OEMListText.setText(strOEMListText)


        var personnelWithCert = ArrayList<Int>()
        FacilityDataModel.getInstance().tblPersonnelCertification.apply {
            (0 until size).forEach {
                if (!personnelWithCert.contains(get(it).PersonnelID)) {
                    personnelWithCert.add(get(it).PersonnelID)
                }
            }
        }
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        personnelWithCert.apply {
            (0 until size).forEach {
                if (FacilityDataModel.getInstance().tblPersonnel.filter { s ->
                        s.PersonnelID.equals(
                            personnelWithCert[it]
                        )
                    }.isNotEmpty()) {
                    val tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow
                    tableRow.minimumHeight = 30

                    val textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.gravity = Gravity.CENTER
                    textView.text = FacilityDataModel.getInstance().tblPersonnel.filter { s ->
                        s.PersonnelID.equals(personnelWithCert[it])
                    }[0].FirstName + " " + FacilityDataModel.getInstance().tblPersonnel.filter { s ->
                        s.PersonnelID.equals(
                            personnelWithCert[it]
                        )
                    }[0].LastName
                    textView.textSize = 14f
                    textView.setTextColor(Color.BLACK)
                    textView.minimumHeight = 30
                    tableRow.addView(textView)

                    val textView1 = TextView(context)
                    textView1.layoutParams = rowLayoutParam1
                    textView1.gravity = Gravity.CENTER
                    textView1.text = FacilityDataModel.getInstance().tblPersonnel.filter { s ->
                        s.PersonnelID.equals(personnelWithCert[it])
                    }[0].CertificationNum_ASE
                    textView1.textSize = 14f
                    textView1.setTextColor(Color.BLACK)
                    textView1.minimumHeight = 30
                    tableRow.addView(textView1)

                    val textView2 = TextView(context)
                    textView2.layoutParams = rowLayoutParam2
                    textView2.gravity = Gravity.CENTER
                    textView2.textSize = 14f
                    textView2.setTextColor(Color.BLACK)
                    textView2.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A1") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("A1") }[0].ExpirationDate
                        //HERE
                        textView2.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView2.setBackgroundColor(Color.RED)
                                textView2.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView2.setBackgroundColor(Color.YELLOW)
                                textView2.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView2.text = ""
                    }
                    tableRow.addView(textView2)

                    val textView3 = TextView(context)
                    textView3.layoutParams = rowLayoutParam2
                    textView3.gravity = Gravity.CENTER
                    textView3.textSize = 14f
                    textView3.setTextColor(Color.BLACK)
                    textView3.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A2") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("A2") }[0].ExpirationDate
                        textView3.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView3.setBackgroundColor(Color.RED)
                                textView3.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView3.setBackgroundColor(Color.YELLOW)
                                textView3.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView3.text = ""
                    }
                    tableRow.addView(textView3)

                    val textView4 = TextView(context)
                    textView4.layoutParams = rowLayoutParam2
                    textView4.gravity = Gravity.CENTER
                    textView4.textSize = 14f
                    textView4.setTextColor(Color.BLACK)
                    textView4.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A3") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("A3") }[0].ExpirationDate
                        textView4.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView4.setBackgroundColor(Color.RED)
                                textView4.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView4.setBackgroundColor(Color.YELLOW)
                                textView4.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView4.text = ""
                    }
                    tableRow.addView(textView4)

                    val textView5 = TextView(context)
                    textView5.layoutParams = rowLayoutParam2
                    textView5.gravity = Gravity.CENTER
                    textView5.textSize = 14f
                    textView5.setTextColor(Color.BLACK)
                    textView5.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A4") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("A4") }[0].ExpirationDate
                        textView5.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView5.setBackgroundColor(Color.RED)
                                textView5.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView5.setBackgroundColor(Color.YELLOW)
                                textView5.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView5.text = ""
                    }
                    tableRow.addView(textView5)

                    val textView6 = TextView(context)
                    textView6.layoutParams = rowLayoutParam2
                    textView6.gravity = Gravity.CENTER
                    textView6.textSize = 14f
                    textView6.setTextColor(Color.BLACK)
                    textView6.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A5") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("A5") }[0].ExpirationDate
                        textView6.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView6.setBackgroundColor(Color.RED)
                                textView6.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView6.setBackgroundColor(Color.YELLOW)
                                textView6.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView6.text = ""
                    }
                    tableRow.addView(textView6)

                    val textView7 = TextView(context)
                    textView7.layoutParams = rowLayoutParam2
                    textView7.gravity = Gravity.CENTER
                    textView7.textSize = 14f
                    textView7.setTextColor(Color.BLACK)
                    textView7.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A6") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("A6") }[0].ExpirationDate
                        textView7.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView7.setBackgroundColor(Color.RED)
                                textView7.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView7.setBackgroundColor(Color.YELLOW)
                                textView7.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView7.text = ""
                    }
                    tableRow.addView(textView7)

                    val textView8 = TextView(context)
                    textView8.layoutParams = rowLayoutParam2
                    textView8.gravity = Gravity.CENTER
                    textView8.textSize = 14f
                    textView8.setTextColor(Color.BLACK)
                    textView8.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A7") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("A7") }[0].ExpirationDate
                        textView8.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView8.setBackgroundColor(Color.RED)
                                textView8.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView8.setBackgroundColor(Color.YELLOW)
                                textView8.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView8.text = ""
                    }
                    tableRow.addView(textView8)

                    val textView9 = TextView(context)
                    textView9.layoutParams = rowLayoutParam2
                    textView9.gravity = Gravity.CENTER
                    textView9.textSize = 14f
                    textView9.setTextColor(Color.BLACK)
                    textView9.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A8") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("A8") }[0].ExpirationDate
                        textView9.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView9.setBackgroundColor(Color.RED)
                                textView9.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView9.setBackgroundColor(Color.YELLOW)
                                textView9.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView9.text = ""
                    }
                    tableRow.addView(textView9)

                    val textView10 = TextView(context)
                    textView10.layoutParams = rowLayoutParam2
                    textView10.gravity = Gravity.CENTER
                    textView10.textSize = 14f
                    textView10.setTextColor(Color.BLACK)
                    textView10.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A9") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("A9") }[0].ExpirationDate
                        textView10.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView10.setBackgroundColor(Color.RED)
                                textView10.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView10.setBackgroundColor(Color.YELLOW)
                                textView10.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView10.text = ""
                    }
                    tableRow.addView(textView10)

                    val textView11 = TextView(context)
                    textView11.layoutParams = rowLayoutParam2
                    textView11.gravity = Gravity.CENTER
                    textView11.textSize = 14f
                    textView11.setTextColor(Color.BLACK)
                    textView11.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("B2") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("B2") }[0].ExpirationDate
                        textView11.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView11.setBackgroundColor(Color.RED)
                                textView11.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView11.setBackgroundColor(Color.YELLOW)
                                textView11.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView11.text = ""
                    }
                    tableRow.addView(textView11)

                    val textView12 = TextView(context)
                    textView12.layoutParams = rowLayoutParam2
                    textView12.gravity = Gravity.CENTER
                    textView12.textSize = 14f
                    textView12.setTextColor(Color.BLACK)
                    textView12.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("B3") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("B3") }[0].ExpirationDate
                        textView12.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView12.setBackgroundColor(Color.RED)
                                textView12.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView12.setBackgroundColor(Color.YELLOW)
                                textView12.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView12.text = ""
                    }
                    tableRow.addView(textView12)

                    val textView13 = TextView(context)
                    textView13.layoutParams = rowLayoutParam2
                    textView13.gravity = Gravity.CENTER
                    textView13.textSize = 14f
                    textView13.setTextColor(Color.BLACK)
                    textView13.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("B4") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("B4") }[0].ExpirationDate
                        textView13.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView13.setBackgroundColor(Color.RED)
                                textView13.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView13.setBackgroundColor(Color.YELLOW)
                                textView13.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView13.text = ""
                    }
                    tableRow.addView(textView13)

                    val textView14 = TextView(context)
                    textView14.layoutParams = rowLayoutParam2
                    textView14.gravity = Gravity.CENTER
                    textView14.textSize = 14f
                    textView14.setTextColor(Color.BLACK)
                    textView14.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("C1") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("C1") }[0].ExpirationDate
                        textView14.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView14.setBackgroundColor(Color.RED)
                                textView14.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView14.setBackgroundColor(Color.YELLOW)
                                textView14.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView14.text = ""
                    }
                    tableRow.addView(textView14)

                    val textView15 = TextView(context)
                    textView15.layoutParams = rowLayoutParam2
                    textView15.gravity = Gravity.CENTER
                    textView15.textSize = 14f
                    textView15.setTextColor(Color.BLACK)
                    textView15.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("F1") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("F1") }[0].ExpirationDate
                        textView15.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView15.setBackgroundColor(Color.RED)
                                textView15.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView15.setBackgroundColor(Color.YELLOW)
                                textView15.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView15.text = ""
                    }
                    tableRow.addView(textView15)

                    val textView16 = TextView(context)
                    textView16.layoutParams = rowLayoutParam2
                    textView16.gravity = Gravity.CENTER
                    textView16.textSize = 14f
                    textView16.setTextColor(Color.BLACK)
                    textView16.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("G1") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("G1") }[0].ExpirationDate
                        textView16.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView16.setBackgroundColor(Color.RED)
                                textView16.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView16.setBackgroundColor(Color.YELLOW)
                                textView16.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView16.text = ""
                    }
                    tableRow.addView(textView16)

//                   val textView17 = TextView(context)
//                    textView17.layoutParams = rowLayoutParam2
//                    textView17.gravity = Gravity.CENTER
//                    textView17.textSize = 12f
//                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("G1") }.isNotEmpty()) {
//                        val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("G1") }[0].ExpirationDate
//                        textView17.text = if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY()
//                    } else {
//                        textView17.text = ""
//                    }
//                    tableRow.addView(textView17)

                    val textView18 = TextView(context)
                    textView18.layoutParams = rowLayoutParam2
                    textView18.gravity = Gravity.CENTER
                    textView18.textSize = 12f
                    textView18.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("I-CAR") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("I-CAR") }[0].ExpirationDate
                        textView18.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView18.setBackgroundColor(Color.RED)
                                textView18.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView18.setBackgroundColor(Color.YELLOW)
                                textView18.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView18.text = ""
                    }
                    tableRow.addView(textView18)

                    val textView19 = TextView(context)
                    textView19.layoutParams = rowLayoutParam2
                    textView19.gravity = Gravity.CENTER
                    textView19.textSize = 12f
                    textView19.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("L1") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("L1") }[0].ExpirationDate
                        textView19.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView19.setBackgroundColor(Color.RED)
                                textView19.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView19.setBackgroundColor(Color.YELLOW)
                                textView19.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView19.text = ""
                    }
                    tableRow.addView(textView19)

                    val textView20 = TextView(context)
                    textView20.layoutParams = rowLayoutParam2
                    textView20.gravity = Gravity.CENTER
                    textView20.textSize = 12f
                    textView20.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("L2") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("L2") }[0].ExpirationDate
                        textView20.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView20.setBackgroundColor(Color.RED)
                                textView20.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView20.setBackgroundColor(Color.YELLOW)
                                textView20.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView20.text = ""
                    }
                    tableRow.addView(textView20)
                    val textView21 = TextView(context)
                    textView21.layoutParams = rowLayoutParam2
                    textView21.gravity = Gravity.CENTER
                    textView21.textSize = 12f
                    textView21.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("L3") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("L3") }[0].ExpirationDate
                        textView21.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView21.setBackgroundColor(Color.RED)
                                textView21.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView21.setBackgroundColor(Color.YELLOW)
                                textView21.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView21.text = ""
                    }
                    tableRow.addView(textView21)
                    binding.certificateGridTblLayout.addView(tableRow)
                }
            }
        }
        altGridTableRow(2)

        val tableRow2 = TableRow(context)
        tableRow2.layoutParams = rowLayoutParamRow
        tableRow2.setBackgroundColor(Color.LTGRAY)
        tableRow2.minimumHeight = 30

        val textViewOEM = TextView(context)
        textViewOEM.layoutParams = rowLayoutParam
//        textViewOEM.gravity = Gravity.CENTER
        textViewOEM.text = "Tech. Name"
        textViewOEM.textSize = 14f
        textViewOEM.setTextColor(Color.BLACK)
        tableRow2.addView(textViewOEM)

        val textViewOEM1 = TextView(context)
        textViewOEM1.layoutParams = rowLayoutParam1
//        textViewOEM1.gravity = Gravity.CENTER
        textViewOEM1.text = "Certification #"
        textViewOEM1.textSize = 14f
        textViewOEM1.setTextColor(Color.BLACK)
        tableRow2.addView(textViewOEM1)

        binding.certificateGridOEMTblLayout.addView(tableRow2)

        FacilityDataModel.getInstance().tblPersonnel.filter { s -> !s.LastName.equals("PRG") }
            .apply {
                (0 until size).forEach {
                    val tableRow3 = TableRow(context)
                    tableRow3.layoutParams = rowLayoutParamRow
                    tableRow3.minimumHeight = 30

                    val textViewOEM3 = TextView(context)
                    textViewOEM3.layoutParams = rowLayoutParam
//            textViewOEM3.gravity = Gravity.CENTER
                    textViewOEM3.textSize = 14f
                    textViewOEM3.setTextColor(Color.BLACK)
                    textViewOEM3.minimumHeight = 30
                    textViewOEM3.text = get(it).FirstName + " - " + get(it).LastName
                    tableRow3.addView(textViewOEM3)

                    val textViewOEM4 = TextView(context)
                    textViewOEM4.layoutParams = rowLayoutParam1
//            textViewOEM3.gravity = Gravity.CENTER
                    textViewOEM4.textSize = 14f
                    textViewOEM4.setTextColor(Color.BLACK)
                    textViewOEM4.minimumHeight = 30
                    textViewOEM4.text = get(it).CertificationNum
                    tableRow3.addView(textViewOEM4)

                    binding.certificateGridOEMTblLayout.addView(tableRow3)
                }
            }
        altGridOEMTableRow(2)
    }

    fun fillCertificationGridAligned() {
        if (binding.certificateGridTblLayout.childCount > 1) {
            for (i in binding.certificateGridTblLayout.childCount - 1 downTo 1) {
                binding.certificateGridTblLayout.removeViewAt(i)
            }
        }

        binding.certificateGridTitle.setText(FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + " - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName)
        var strASEListText = "ASE:"
        var strOEMListText = "OEM:"

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParamRow.weight = 1F

        val tableRow = TableRow(context)
        tableRow.layoutParams = rowLayoutParamRow
        tableRow.setBackgroundColor(Color.LTGRAY)
        tableRow.minimumHeight = 30

        val textView = TextView(context)
        textView.gravity = Gravity.CENTER
        textView.text = "Tech. Name"
        textView.textSize = 14f
        textView.setTextColor(Color.BLACK)
        tableRow.addView(textView)

        val textView1 = TextView(context)
        textView1.gravity = Gravity.CENTER
        textView1.text = "Certification #"
        textView1.textSize = 12f
        textView1.setTextColor(Color.BLACK)
        tableRow.addView(textView1)

        TypeTablesModel.getInstance().PersonnelCertificationType.filter { s -> s.Category.equals("ASE") }
            .apply {
                (0 until size).forEach {
                    strASEListText += " - " + get(it).PersonnelCertName

                    val textView2 = TextView(context)
                    textView2.gravity = Gravity.CENTER
                    textView2.text = get(it).PersonnelCertID
                    textView2.textSize = 12f
                    textView2.setTextColor(Color.BLACK)
                    tableRow.addView(textView2)
                }
            }
        binding.ASEListText.setText(strASEListText)

        binding.certificateGridTblLayout.addView(tableRow)

        TypeTablesModel.getInstance().PersonnelCertificationType.filter { s -> s.Category.equals("OEM") }
            .apply {
                (0 until size).forEach {
                    strOEMListText += " - " + get(it).PersonnelCertName
                }
            }
        binding.OEMListText.setText(strOEMListText)


        var personnelWithCert = ArrayList<Int>()
        FacilityDataModel.getInstance().tblPersonnelCertification.apply {
            (0 until size).forEach {
                if (!personnelWithCert.contains(get(it).PersonnelID)) {
                    personnelWithCert.add(get(it).PersonnelID)
                }
            }
        }
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        personnelWithCert.apply {
            (0 until size).forEach {
                if (FacilityDataModel.getInstance().tblPersonnel.filter { s ->
                        s.PersonnelID.equals(
                            personnelWithCert[it]
                        )
                    }.isNotEmpty()) {
                    val tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow
                    tableRow.minimumHeight = 30

                    val textView = TextView(context)
                    textView.gravity = Gravity.CENTER
                    textView.text = FacilityDataModel.getInstance().tblPersonnel.filter { s ->
                        s.PersonnelID.equals(personnelWithCert[it])
                    }[0].FirstName + " " + FacilityDataModel.getInstance().tblPersonnel.filter { s ->
                        s.PersonnelID.equals(
                            personnelWithCert[it]
                        )
                    }[0].LastName
                    textView.textSize = 14f
                    textView.setTextColor(Color.BLACK)
                    textView.minimumHeight = 30
                    tableRow.addView(textView)

                    val textView1 = TextView(context)
                    textView1.gravity = Gravity.CENTER
                    textView1.text = FacilityDataModel.getInstance().tblPersonnel.filter { s ->
                        s.PersonnelID.equals(personnelWithCert[it])
                    }[0].CertificationNum_ASE
                    textView1.textSize = 14f
                    textView1.setTextColor(Color.BLACK)
                    textView1.minimumHeight = 30
                    tableRow.addView(textView1)

                    val textView2 = TextView(context)
                    textView2.gravity = Gravity.CENTER
                    textView2.textSize = 14f
                    textView2.setTextColor(Color.BLACK)
                    textView2.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A1") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("A1") }[0].ExpirationDate
                        //HERE
                        textView2.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView2.setBackgroundColor(Color.RED)
                                textView2.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView2.setBackgroundColor(Color.YELLOW)
                                textView2.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView2.text = ""
                    }
                    tableRow.addView(textView2)

                    val textView3 = TextView(context)
                    textView3.gravity = Gravity.CENTER
                    textView3.textSize = 14f
                    textView3.setTextColor(Color.BLACK)
                    textView3.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A2") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("A2") }[0].ExpirationDate
                        textView3.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView3.setBackgroundColor(Color.RED)
                                textView3.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView3.setBackgroundColor(Color.YELLOW)
                                textView3.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView3.text = ""
                    }
                    tableRow.addView(textView3)

                    val textView4 = TextView(context)
                    textView4.gravity = Gravity.CENTER
                    textView4.textSize = 14f
                    textView4.setTextColor(Color.BLACK)
                    textView4.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A3") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("A3") }[0].ExpirationDate
                        textView4.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView4.setBackgroundColor(Color.RED)
                                textView4.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView4.setBackgroundColor(Color.YELLOW)
                                textView4.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView4.text = ""
                    }
                    tableRow.addView(textView4)

                    val textView5 = TextView(context)
                    textView5.gravity = Gravity.CENTER
                    textView5.textSize = 14f
                    textView5.setTextColor(Color.BLACK)
                    textView5.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A4") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("A4") }[0].ExpirationDate
                        textView5.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView5.setBackgroundColor(Color.RED)
                                textView5.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView5.setBackgroundColor(Color.YELLOW)
                                textView5.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView5.text = ""
                    }
                    tableRow.addView(textView5)

                    val textView6 = TextView(context)
                    textView6.gravity = Gravity.CENTER
                    textView6.textSize = 14f
                    textView6.setTextColor(Color.BLACK)
                    textView6.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A5") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("A5") }[0].ExpirationDate
                        textView6.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView6.setBackgroundColor(Color.RED)
                                textView6.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView6.setBackgroundColor(Color.YELLOW)
                                textView6.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView6.text = ""
                    }
                    tableRow.addView(textView6)

                    val textView7 = TextView(context)
                    textView7.gravity = Gravity.CENTER
                    textView7.textSize = 14f
                    textView7.setTextColor(Color.BLACK)
                    textView7.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A6") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("A6") }[0].ExpirationDate
                        textView7.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView7.setBackgroundColor(Color.RED)
                                textView7.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView7.setBackgroundColor(Color.YELLOW)
                                textView7.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView7.text = ""
                    }
                    tableRow.addView(textView7)

                    val textView8 = TextView(context)
                    textView8.gravity = Gravity.CENTER
                    textView8.textSize = 14f
                    textView8.setTextColor(Color.BLACK)
                    textView8.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A7") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("A7") }[0].ExpirationDate
                        textView8.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView8.setBackgroundColor(Color.RED)
                                textView8.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView8.setBackgroundColor(Color.YELLOW)
                                textView8.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView8.text = ""
                    }
                    tableRow.addView(textView8)

                    val textView9 = TextView(context)
                    textView9.gravity = Gravity.CENTER
                    textView9.textSize = 14f
                    textView9.setTextColor(Color.BLACK)
                    textView9.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A8") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("A8") }[0].ExpirationDate
                        textView9.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView9.setBackgroundColor(Color.RED)
                                textView9.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView9.setBackgroundColor(Color.YELLOW)
                                textView9.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView9.text = ""
                    }
                    tableRow.addView(textView9)

                    val textView10 = TextView(context)
                    textView10.gravity = Gravity.CENTER
                    textView10.textSize = 14f
                    textView10.setTextColor(Color.BLACK)
                    textView10.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A9") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("A9") }[0].ExpirationDate
                        textView10.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView10.setBackgroundColor(Color.RED)
                                textView10.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView10.setBackgroundColor(Color.YELLOW)
                                textView10.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView10.text = ""
                    }
                    tableRow.addView(textView10)

                    val textView11 = TextView(context)
                    textView11.gravity = Gravity.CENTER
                    textView11.textSize = 14f
                    textView11.setTextColor(Color.BLACK)
                    textView11.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("B2") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("B2") }[0].ExpirationDate
                        textView11.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView11.setBackgroundColor(Color.RED)
                                textView11.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView11.setBackgroundColor(Color.YELLOW)
                                textView11.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView11.text = ""
                    }
                    tableRow.addView(textView11)

                    val textView12 = TextView(context)
                    textView12.gravity = Gravity.CENTER
                    textView12.textSize = 14f
                    textView12.setTextColor(Color.BLACK)
                    textView12.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("B3") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("B3") }[0].ExpirationDate
                        textView12.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView12.setBackgroundColor(Color.RED)
                                textView12.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView12.setBackgroundColor(Color.YELLOW)
                                textView12.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView12.text = ""
                    }
                    tableRow.addView(textView12)

                    val textView13 = TextView(context)
                    textView13.gravity = Gravity.CENTER
                    textView13.textSize = 14f
                    textView13.setTextColor(Color.BLACK)
                    textView13.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("B4") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("B4") }[0].ExpirationDate
                        textView13.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView13.setBackgroundColor(Color.RED)
                                textView13.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView13.setBackgroundColor(Color.YELLOW)
                                textView13.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView13.text = ""
                    }
                    tableRow.addView(textView13)

                    val textView14 = TextView(context)
                    textView14.gravity = Gravity.CENTER
                    textView14.textSize = 14f
                    textView14.setTextColor(Color.BLACK)
                    textView14.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("C1") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("C1") }[0].ExpirationDate
                        textView14.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView14.setBackgroundColor(Color.RED)
                                textView14.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView14.setBackgroundColor(Color.YELLOW)
                                textView14.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView14.text = ""
                    }
                    tableRow.addView(textView14)

                    val textView15 = TextView(context)
                    textView15.gravity = Gravity.CENTER
                    textView15.textSize = 14f
                    textView15.setTextColor(Color.BLACK)
                    textView15.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("F1") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("F1") }[0].ExpirationDate
                        textView15.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView15.setBackgroundColor(Color.RED)
                                textView15.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView15.setBackgroundColor(Color.YELLOW)
                                textView15.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView15.text = ""
                    }
                    tableRow.addView(textView15)

                    val textView16 = TextView(context)
                    textView16.gravity = Gravity.CENTER
                    textView16.textSize = 14f
                    textView16.setTextColor(Color.BLACK)
                    textView16.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("G1") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("G1") }[0].ExpirationDate
                        textView16.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView16.setBackgroundColor(Color.RED)
                                textView16.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView16.setBackgroundColor(Color.YELLOW)
                                textView16.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView16.text = ""
                    }
                    tableRow.addView(textView16)

//                   val textView17 = TextView(context)
//                    textView17.layoutParams = rowLayoutParam2
//                    textView17.gravity = Gravity.CENTER
//                    textView17.textSize = 12f
//                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("G1") }.isNotEmpty()) {
//                        val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("G1") }[0].ExpirationDate
//                        textView17.text = if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY()
//                    } else {
//                        textView17.text = ""
//                    }
//                    tableRow.addView(textView17)

                    val textView18 = TextView(context)
                    textView18.gravity = Gravity.CENTER
                    textView18.textSize = 12f
                    textView18.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("I-CAR") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("I-CAR") }[0].ExpirationDate
                        textView18.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView18.setBackgroundColor(Color.RED)
                                textView18.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView18.setBackgroundColor(Color.YELLOW)
                                textView18.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView18.text = ""
                    }
                    tableRow.addView(textView18)

                    val textView19 = TextView(context)
                    textView19.gravity = Gravity.CENTER
                    textView19.textSize = 12f
                    textView19.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("L1") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("L1") }[0].ExpirationDate
                        textView19.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView19.setBackgroundColor(Color.RED)
                                textView19.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView19.setBackgroundColor(Color.YELLOW)
                                textView19.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView19.text = ""
                    }
                    tableRow.addView(textView19)

                    val textView20 = TextView(context)
                    textView20.gravity = Gravity.CENTER
                    textView20.textSize = 12f
                    textView20.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("L2") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("L2") }[0].ExpirationDate
                        textView20.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView20.setBackgroundColor(Color.RED)
                                textView20.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView20.setBackgroundColor(Color.YELLOW)
                                textView20.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView20.text = ""
                    }
                    tableRow.addView(textView20)
                    val textView21 = TextView(context)
                    textView21.gravity = Gravity.CENTER
                    textView21.textSize = 12f
                    textView21.minimumHeight = 30
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("L3") }.isNotEmpty()
                    ) {
                        val expDate =
                            FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                                compareBy { it.CertificationTypeId })
                                .filter { s -> s.PersonnelID == personnelWithCert[it] }
                                .filter { s -> s.CertificationTypeId.equals("L3") }[0].ExpirationDate
                        textView21.text = if (expDate.apiToAppFormatMMDDYY()
                                .equals("01/01/1900")
                        ) "" else expDate.apiToAppFormatMMDDYY()
                        if (!expDate.equals("")) {
                            val ASEExpDate = sdf.parse(expDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            if (ASEExpDatedays <= 0) {
                                textView21.setBackgroundColor(Color.RED)
                                textView21.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            } else if (ASEExpDatedays <= 180 && ASEExpDatedays > 0) {
                                textView21.setBackgroundColor(Color.YELLOW)
                                textView21.setTypeface(textView.getTypeface(), Typeface.BOLD)
                            }
                        }
                    } else {
                        textView21.text = ""
                    }
                    tableRow.addView(textView21)
                    binding.certificateGridTblLayout.addView(tableRow)
                }
            }
        }
        altGridTableRow(2)

        val tableRow2 = TableRow(context)
        tableRow2.layoutParams = rowLayoutParamRow
        tableRow2.setBackgroundColor(Color.LTGRAY)
        tableRow2.minimumHeight = 30

        val textViewOEM = TextView(context)
//        textViewOEM.gravity = Gravity.CENTER
        textViewOEM.text = "Tech. Name"
        textViewOEM.textSize = 14f
        textViewOEM.setTextColor(Color.BLACK)
        tableRow2.addView(textViewOEM)

        val textViewOEM1 = TextView(context)
//        textViewOEM1.gravity = Gravity.CENTER
        textViewOEM1.text = "Certification #"
        textViewOEM1.textSize = 14f
        textViewOEM1.setTextColor(Color.BLACK)
        tableRow2.addView(textViewOEM1)

        binding.certificateGridOEMTblLayout.addView(tableRow2)

        FacilityDataModel.getInstance().tblPersonnel.filter { s -> !s.LastName.equals("PRG") }
            .apply {
                (0 until size).forEach {
                    val tableRow3 = TableRow(context)
                    tableRow3.layoutParams = rowLayoutParamRow
                    tableRow3.minimumHeight = 30

                    val textViewOEM3 = TextView(context)
//            textViewOEM3.gravity = Gravity.CENTER
                    textViewOEM3.textSize = 14f
                    textViewOEM3.setTextColor(Color.BLACK)
                    textViewOEM3.minimumHeight = 30
                    textViewOEM3.text = get(it).FirstName + " - " + get(it).LastName
                    tableRow3.addView(textViewOEM3)

                    val textViewOEM4 = TextView(context)
//            textViewOEM3.gravity = Gravity.CENTER
                    textViewOEM4.textSize = 14f
                    textViewOEM4.setTextColor(Color.BLACK)
                    textViewOEM4.minimumHeight = 30
                    textViewOEM4.text = get(it).CertificationNum
                    tableRow3.addView(textViewOEM4)

                    binding.certificateGridOEMTblLayout.addView(tableRow3)
                }
            }
        altGridOEMTableRow(2)
    }

    fun fillPersonnelTableView() {

        binding.addNewPersnRecordBtn.isEnabled =
            (FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PrimaryMailRecipient == true }
                .isNotEmpty())
        val layoutParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        if (binding.PersonnelResultsTbl.childCount > 1) {
            for (i in binding.PersonnelResultsTbl.childCount - 1 downTo 1) {
                binding.PersonnelResultsTbl.removeViewAt(i)
            }
        }
        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1.5F
//        rowLayoutParam.leftMargin = 10
        rowLayoutParam.column = 0
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0
        rowLayoutParam.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1.0F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0
        rowLayoutParam1.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1.0F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 0
        rowLayoutParam2.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.width = 0
        rowLayoutParam3.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1.5F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.width = 0
        rowLayoutParam4.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 1F
        rowLayoutParam5.column = 5
        rowLayoutParam5.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam5.width = 0
//        rowLayoutParam5.leftMargin = 10
        rowLayoutParam5.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam6 = TableRow.LayoutParams()
        rowLayoutParam6.weight = 1F
        rowLayoutParam6.column = 6
        rowLayoutParam6.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam6.width = 0
        rowLayoutParam6.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam7 = TableRow.LayoutParams()
        rowLayoutParam7.weight = 1F
        rowLayoutParam7.column = 7
        rowLayoutParam7.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam7.width = 0
        rowLayoutParam7.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam8 = TableRow.LayoutParams()
        rowLayoutParam8.weight = 1F
        rowLayoutParam8.column = 8
        rowLayoutParam8.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam8.width = 0
        rowLayoutParam8.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam82 = TableRow.LayoutParams()
        rowLayoutParam82.weight = 1F
        rowLayoutParam82.column = 9
        rowLayoutParam82.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam82.width = 0
        rowLayoutParam82.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam9 = TableRow.LayoutParams()
        rowLayoutParam9.weight = 1F
        rowLayoutParam9.column = 10
        rowLayoutParam9.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam9.width = 0
        rowLayoutParam9.gravity = Gravity.CENTER_HORIZONTAL

        val rowLayoutParam10 = TableRow.LayoutParams()
        rowLayoutParam10.weight = 1F
        rowLayoutParam10.column = 11
        rowLayoutParam10.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam10.width = 0
        rowLayoutParam10.gravity = Gravity.CENTER_HORIZONTAL
        var dateTobeFormated = ""

        val rowLayoutParam11 = TableRow.LayoutParams()
        rowLayoutParam11.weight = 0.8F
        rowLayoutParam11.column = 15
        rowLayoutParam11.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam11.width = 0
        rowLayoutParam11.gravity = Gravity.START

        val rowLayoutParam12 = TableRow.LayoutParams()
        rowLayoutParam12.weight = 1F
        rowLayoutParam12.column = 12
        rowLayoutParam12.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam12.width = 0
        rowLayoutParam12.gravity = Gravity.CENTER_HORIZONTAL
//        var dateTobeFormated = ""

        val rowLayoutParam14 = TableRow.LayoutParams()
        rowLayoutParam14.weight = 1F
        rowLayoutParam14.column = 14
        rowLayoutParam14.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam14.width = 0
        rowLayoutParam14.gravity = Gravity.CENTER_HORIZONTAL

        val rowLayoutParam13 = TableRow.LayoutParams()
        rowLayoutParam13.weight = 1F
        rowLayoutParam13.column = 13
        rowLayoutParam13.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam13.width = 0
        rowLayoutParam13.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam15 = TableRow.LayoutParams()
        rowLayoutParam15.weight = 1.5F
        rowLayoutParam15.column = 15
        rowLayoutParam15.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam15.width = 0
        rowLayoutParam15.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam16 = TableRow.LayoutParams()
        rowLayoutParam16.weight = 1.2F
        rowLayoutParam16.column = 16
        rowLayoutParam16.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam16.width = 0
        rowLayoutParam16.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParamRow.width = TableRow.LayoutParams.WRAP_CONTENT
//        rowLayoutParamRow.weight=1F

        FacilityDataModel.getInstance().tblPersonnel.apply {
            (0 until size).forEach {
                if (get(it).LastName == null) get(it).LastName = ""
                Log.v("RSP PHONE TABLE =>", get(it).RSP_Phone)
                if (PRGDataModel.getInstance().tblPRGPersonnelDetails.filter { s ->
                        s.personnelid.equals(
                            get(it).PersonnelID.toString()
                        )
                    }.isNotEmpty()) {
//                    get(it).ASE_Cert_URL = PRGDataModel.getInstance().tblPRGPersonnelDetails.filter { s->s.personnelid.equals(get(it).PersonnelID.toString())}[0].asecerturl
                    get(it).OEMstartDate =
                        PRGDataModel.getInstance().tblPRGPersonnelDetails.filter { s ->
                            s.personnelid.equals(get(it).PersonnelID.toString())
                        }[0].oemstartdate
                    get(it).OEMendDate =
                        PRGDataModel.getInstance().tblPRGPersonnelDetails.filter { s ->
                            s.personnelid.equals(get(it).PersonnelID.toString())
                        }[0].oemenddate
//                    get(it).ReportRecipient = (PRGDataModel.getInstance().tblPRGPersonnelDetails.filter { s->s.personnelid.equals(get(it).PersonnelID.toString())}[0].reportrecipient==1)
//                    get(it).NotificationRecipient = (PRGDataModel.getInstance().tblPRGPersonnelDetails.filter { s->s.personnelid.equals(get(it).PersonnelID.toString())}[0].notificationrecipient==1)
                }

                var tableRow = TableRow(context)
                tableRow.layoutParams = rowLayoutParamRow
                tableRow.minimumHeight = 30

                tableRow.setOnClickListener {
                    altTableRow(2)
                    tableRow.setBackgroundColor(Color.GREEN)
                    var currentTableRowIndex = binding.PersonnelResultsTbl.indexOfChild(tableRow)
                    var currentfacilityDataModelIndex = currentTableRowIndex - 1
                    binding.certTextViewVal.text =
                        "Personnel Certification(s) - ${FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex].FirstName} ${FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex].LastName}  "
                    fillPersonnelDetailsTableView(FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex].PersonnelID)
                    fillCertificationTableView(FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex].PersonnelID)
                    selectedPersonnelID =
                        FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex].PersonnelID
                }
                val updateBtn = TextView(context)
                updateBtn.layoutParams = rowLayoutParam11
                updateBtn.text = "EDIT"
                if (!get(it).LastName.isNullOrEmpty()) {
                    if (get(it).LastName.equals("PRG")) {
                        updateBtn.isEnabled = false
                        updateBtn.setTextColor(Color.GRAY)
                    } else {
                        updateBtn.isEnabled = true
                        updateBtn.setTextColor(Color.BLUE)
                    }
                } else {
                    updateBtn.isEnabled = true
                    updateBtn.setTextColor(Color.BLUE)
                }
//                updateBtn.isEnabled = !get(it).LastName.equals("PRG")
                updateBtn.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                updateBtn.textSize = 14f
                updateBtn.setBackgroundColor(Color.TRANSPARENT)

                tableRow.addView(updateBtn)

                val textView1 = TextView(context)
                textView1.layoutParams = rowLayoutParam
//                textView1.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView1.gravity = Gravity.CENTER_VERTICAL
                textView1.text = getTypeName(get(it).PersonnelTypeID.toString())
                textView1.minimumHeight = 30
                textView1.textSize = 14f
                textView1.setTextColor(Color.BLACK)
                tableRow.addView(textView1)

                val textView2 = TextView(context)
                textView2.layoutParams = rowLayoutParam1
                textView2.gravity = Gravity.CENTER_VERTICAL
//                textView2.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView2.text = get(it).FirstName
                textView2.tag = get(it).PersonnelID
                textView2.minimumHeight = 30
                textView2.textSize = 14f
                textView2.setTextColor(Color.BLACK)
                tableRow.addView(textView2)

                val textView3 = TextView(context)
                textView3.layoutParams = rowLayoutParam2
                textView3.gravity = Gravity.CENTER_VERTICAL
//                textView3.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView3.minimumHeight = 30
                textView3.text = get(it).LastName
                textView3.textSize = 14f
                textView3.setTextColor(Color.BLACK)
                tableRow.addView(textView3)

                val textView4 = TextView(context)
                textView4.layoutParams = rowLayoutParam3
                textView4.gravity = Gravity.CENTER_VERTICAL
                textView4.text = get(it).RSP_UserName
                textView4.minimumHeight = 30
                textView4.textSize = 14f
                textView4.setTextColor(Color.BLACK)
                tableRow.addView(textView4)

                val textView5 = TextView(context)
                textView5.layoutParams = rowLayoutParam4
                textView5.gravity = Gravity.CENTER_VERTICAL
                textView5.textSize = 14f
                textView5.setTextColor(Color.BLACK)
                textView5.text =
                    get(it).RSP_Email//if (get(it).ContractSigner) FacilityDataModel.getInstance().tblPersonnelSigner.filter { s->s.PersonnelID==get(it).PersonnelID}[0].email else get(it).RSP_Email
                textView5.minimumHeight = 30
                tableRow.addView(textView5)

                val textView50 = TextView(context)
                textView50.text = get(it).RSP_Email

                val textView6 = TextView(context)
                textView6.layoutParams = rowLayoutParam5
                textView6.minimumHeight = 30
                textView6.gravity = Gravity.CENTER_VERTICAL
                textView6.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                textView6.textSize = 14f
                textView6.setTextColor(Color.BLACK)
                if (!(get(it).SeniorityDate.isNullOrEmpty())) {
                    try {
                        textView6.text = if (get(it).SeniorityDate.apiToAppFormatMMDDYYYY()
                                .equals("01/01/1900")
                        ) "" else get(it).SeniorityDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textView6.text = get(it).SeniorityDate.apiToAppFormatMMDDYYYY()
                    }
                } else {
                    textView6.text = ""
                }

                tableRow.addView(textView6)

                val textView8 = TextView(context)
                textView8.layoutParams = rowLayoutParam6
                textView8.gravity = Gravity.CENTER_VERTICAL
                textView8.minimumHeight = 30
                textView8.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                textView8.textSize = 14f
                textView8.setTextColor(Color.BLACK)
                if (!(get(it).startDate.isNullOrEmpty())) {
                    try {
                        textView8.text = if (get(it).startDate.apiToAppFormatMMDDYYYY()
                                .equals("01/01/1900")
                        ) "" else get(it).startDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textView8.text = get(it).startDate.apiToAppFormatMMDDYYYY()
                    }
                } else {
                    textView8.text = ""
                }

                tableRow.addView(textView8)

                val textView9 = TextView(context)
                textView9.layoutParams = rowLayoutParam7
                textView9.gravity = Gravity.CENTER_VERTICAL
                textView9.minimumHeight = 30
                textView9.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_START
                textView9.textSize = 14f
                textView9.setTextColor(Color.BLACK)
                if (!(get(it).endDate.isNullOrEmpty())) {
                    try {
                        textView9.text = if (get(it).endDate.apiToAppFormatMMDDYYYY()
                                .equals("01/01/1900")
                        ) "" else get(it).endDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textView9.text = get(it).endDate.apiToAppFormatMMDDYYYY()
                    }
                } else {
                    textView9.text = ""
                }

                tableRow.addView(textView9)

                val textView7 = TextView(context)
                textView7.layoutParams = rowLayoutParam8
                textView7.gravity = Gravity.CENTER_VERTICAL
                if (personIDWithExpiredCerts.contains(get(it).PersonnelID)) {
                    textView7.setTextColor(Color.RED)
                } else if (personIDWithAboutExpiredCerts.contains(get(it).PersonnelID)) {
                    textView7.setTextColor(Color.YELLOW)
                }
                textView7.text = get(it).CertificationNum
                textView7.minimumHeight = 30
                textView7.textSize = 14f
                textView7.setTextColor(Color.BLACK)
                tableRow.addView(textView7)

                val textView72 = TextView(context)
                textView72.layoutParams = rowLayoutParam82
                textView72.gravity = Gravity.CENTER_VERTICAL
                if (personIDWithExpiredCerts.contains(get(it).PersonnelID)) {
                    textView72.setTextColor(Color.RED)
                } else if (personIDWithAboutExpiredCerts.contains(get(it).PersonnelID)) {
                    textView72.setTextColor(Color.YELLOW)
                }
                textView72.text = get(it).CertificationNum_ASE
                textView72.minimumHeight = 30
                textView72.textSize = 14f
                textView72.setTextColor(Color.BLACK)
                tableRow.addView(textView72)

                val checkBox10 = CheckBox(context)
                checkBox10.layoutParams = rowLayoutParam9
                checkBox10.gravity = Gravity.CENTER
                checkBox10.isChecked = get(it).ContractSigner
                checkBox10.minimumHeight = 30
                checkBox10.isEnabled = false
                checkBox10.textSize = 14f
                tableRow.addView(checkBox10)

                val checkBox11 = CheckBox(context)
                checkBox11.layoutParams = rowLayoutParam10
                checkBox11.gravity = Gravity.CENTER
                checkBox11.isChecked = get(it).PrimaryMailRecipient
                checkBox11.isEnabled = false
                checkBox11.minimumHeight = 30
                checkBox11.textSize = 14f
                tableRow.addView(checkBox11)

                val checkBox12 = CheckBox(context)
                checkBox12.layoutParams = rowLayoutParam12
                checkBox12.gravity = Gravity.CENTER
                checkBox12.isChecked = get(it).ReportRecipient
                checkBox12.minimumHeight = 30
                checkBox12.isEnabled = false
                checkBox12.textSize = 12f
                tableRow.addView(checkBox12)

                val checkBox13 = CheckBox(context)
                checkBox13.layoutParams = rowLayoutParam13
                checkBox13.gravity = Gravity.CENTER
                checkBox13.isChecked = get(it).NotificationRecipient
                checkBox13.isEnabled = false
                checkBox13.minimumHeight = 30
                checkBox13.textSize = 14f
                tableRow.addView(checkBox13)

                val checkBox14 = CheckBox(context)
                checkBox14.layoutParams = rowLayoutParam14
                checkBox14.gravity = Gravity.CENTER_HORIZONTAL
                checkBox14.isChecked = get(it).ComplaintContact
                checkBox14.isEnabled = false
                checkBox14.minimumHeight = 30
                checkBox14.textSize = 14f
                tableRow.addView(checkBox14)

                val textViewOEMStart = TextView(context)
                if (!(get(it).OEMstartDate.isNullOrEmpty())) {
                    try {
                        textViewOEMStart.text = if (get(it).OEMstartDate.apiToAppFormatMMDDYYYY()
                                .equals("01/01/1900")
                        ) "" else get(it).OEMstartDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textViewOEMStart.text = ""
                    }
                } else {
                    textViewOEMStart.text = ""
                }
                val textViewOEMEnd = TextView(context)

                if (!(get(it).OEMendDate.isNullOrEmpty())) {
                    try {
                        textViewOEMEnd.text = if (get(it).OEMendDate.apiToAppFormatMMDDYYYY()
                                .equals("01/01/1900")
                        ) "" else get(it).OEMendDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textViewOEMEnd.text = ""
                    }
                } else {
                    textViewOEMEnd.text = ""
                }

                val textViewAceURL = TextView(context)
                textViewAceURL.text = get(it).ASE_Cert_URL
                textViewAceURL.text = get(it).ASE_Cert_URL

                val textView15 = TextView(context)
                textView15.layoutParams = rowLayoutParam15
                textView15.gravity = Gravity.CENTER_VERTICAL
                var updateByName = ""
//                if (TypeTablesModel.getInstance().EmployeeList.filter { s-> s.NTLogin.equals(get(it).updateBy)}.isNotEmpty())
//                    updateByName = TypeTablesModel.getInstance().EmployeeList.filter { s-> s.NTLogin.lowercase().equals(get(it).updateBy.lowercase())}[0].FullName
//                else
//                    updateByName = get(it).updateByCsiSpecialist
                if (CsiSpecialistSingletonModel.getInstance().csiSpecialists.filter { s ->
                        s.accspecid.lowercase().equals(get(it).updateBy.lowercase())
                    }.isNotEmpty())
                    updateByName =
                        CsiSpecialistSingletonModel.getInstance().csiSpecialists.filter { s ->
                            s.accspecid.lowercase().equals(get(it).updateBy.lowercase())
                        }[0].specialistname.uppercase()
                else
                    updateByName = get(it).updateBy.uppercase()
                textView15.text = updateByName
                textView15.minimumHeight = 30
                textView15.textSize = 14f
                textView15.setTextColor(Color.BLACK)
                tableRow.addView(textView15)


                val textView16 = TextView(context)
                textView16.layoutParams = rowLayoutParam16
                textView16.minimumHeight = 30
                textView16.gravity = Gravity.CENTER_VERTICAL
//                textView16.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                textView16.textSize = 14f
                textView16.setTextColor(Color.BLACK)
                if (!(get(it).updateDate.isNullOrEmpty())) {
                    try {
                        textView16.text = if (get(it).updateDate.apiToAppFormatMMDDYYYY()
                                .equals("01/01/1900")
                        ) "" else get(it).updateDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textView16.text = get(it).updateDate.apiToAppFormatMMDDYYYY()
                    }
                } else {
                    textView16.text = ""
                }
                tableRow.addView(textView16)


//                updateBtn.isEnabled = (FacilityDataModel.getInstance().tblPersonnel.filter {s->s.PrimaryMailRecipient==true}.isNotEmpty())
//                if (FacilityDataModel.getInstance().tblPersonnel.filter {s->s.PrimaryMailRecipient==true}.isEmpty()) {
//                    updateBtn.tag = 1
//                } else {
//                    updateBtn.tag = 0
//                }
                binding.PersonnelResultsTbl.addView(tableRow)

                updateBtn.setOnClickListener {
                    var contractSignerFound = 0
                    var emailPrimaryFound = 0

                    if (checkBox10.isChecked) {
                        edit_enableContractSignerIsChecked()
                        binding.editNewSignerCheck.isChecked = true
                        binding.editNewFirstNameText.isEnabled = false
                        binding.editNewLastNameText.isEnabled = false
                        binding.editNewSignerCheck.isEnabled = false
                        binding.editNewSignerCheck.setOnCheckedChangeListener { buttonView, isChecked ->
                            if (binding.editNewSignerCheck.isChecked) {
                                edit_enableContractSignerIsChecked()
                            } else {
                                edit_disableContractSignerIsChecked()
                            }
                        }
                    } else {
                        edit_disableContractSignerIsChecked()
                        binding.editNewSignerCheck.isChecked = false
                        binding.editNewSignerCheck.isEnabled = false
                        binding.editNewFirstNameText.isEnabled = true
                        binding.editNewLastNameText.isEnabled = true
                    }

                    FacilityDataModel.getInstance().tblPersonnel.apply {
                        (0 until size).forEach {
                            if (get(it).ContractSigner.equals("true")) {
                                contractSignerFound++
                            }
                            if (get(it).PrimaryMailRecipient.equals("true")) {
                                emailPrimaryFound++
                            }
                            if (contractSignerFound > 0 && !checkBox10.isChecked) {
                                binding.editNewSignerCheck.setOnCheckedChangeListener { buttonView, isChecked ->
                                    if (binding.editNewSignerCheck.isChecked) {
                                        //                                Toast.makeText(context, "there's already contract signer for this contract", Toast.LENGTH_SHORT).show()
                                        Utility.showValidationAlertDialog(
                                            activity,
                                            "There is already contract signer for this contract"
                                        )
                                        binding.editNewSignerCheck.isChecked = false
                                    } else {
                                        edit_disableContractSignerIsChecked()
                                    }
                                }
                            }
                            if (emailPrimaryFound > 0 && !checkBox11.isChecked) {
                                binding.editNewACSCheck.setOnCheckedChangeListener { buttonView, isChecked ->
                                    if (binding.editNewACSCheck.isChecked) {
                                        Utility.showValidationAlertDialog(
                                            activity,
                                            "There's already primary email assigned for this contract"
                                        )
                                        binding.editNewACSCheck.isChecked = false
                                    }
                                }
                            }
                            if (emailPrimaryFound > 0 && checkBox11.isChecked) {
                                binding.editNewACSCheck.setOnCheckedChangeListener { buttonView, isChecked ->
                                    if (binding.editNewACSCheck.isChecked) {
                                        binding.editNewACSCheck.isChecked = true
                                    }
                                }
                            }

                            if (contractSignerFound == 0) {
                                binding.editNewSignerCheck.setOnClickListener(View.OnClickListener {
                                    if (binding.editNewSignerCheck.isChecked) {
                                        edit_enableContractSignerIsChecked()
                                    }


                                })

                            }


                        }
                    }
                    if (contractSignerFound == 0) {
                        binding.editNewSignerCheck.setOnCheckedChangeListener { buttonView, isChecked ->


                            if (binding.editNewSignerCheck.isChecked) {

                                edit_enableContractSignerIsChecked()

                            } else {

                                edit_disableContractSignerIsChecked()

                            }

                        }
                    }
                    if (contractSignerFound > 0 && checkBox10.isChecked) {
                        binding.editNewSignerCheck.isChecked = true


                    }


                    var currentTableRowIndex = binding.PersonnelResultsTbl.indexOfChild(tableRow)
                    var currentfacilityDataModelIndex = currentTableRowIndex - 1
                    Log.v("Table Row Index", " ==> " + currentTableRowIndex)
                    Log.v("Data Model Index", " ==> " + currentfacilityDataModelIndex)
                    Log.v(
                        "NAME ",
                        " ==> " + FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex].FirstName
                    )
                    //                    for (i in 0 until mainViewLinearId.childCount) {
                    //                        val child = mainViewLinearId.getChildAt(i)
                    //                        child.isEnabled = false
                    //                    }
                    //
                    //                    for (i in 0 until mainViewLinearId2.childCount) {
                    //                        val child = mainViewLinearId2.getChildAt(i)
                    //                        child.isEnabled = false
                    //                    }

                    val currRSPUserID = textView4.text.toString()
                    val currRSPEmailID = textView50.text.toString()
                    val currFirstName = textView2.text.toString()
                    val currLastName = textView3.text.toString()
                    val currTitle = textView1.text.toString()
                    var currEndDate = ""

                    binding.editNewFirstNameText.setText(textView2.text)
                    binding.editNewLastNameText.setText(textView3.text)
                    binding.editNewCertNoText.setText(textView7.text)
                    binding.editNewASECertNoText.setText(textView72.text)

//                    edit_newStartDateBtn.setText(textView8.text)
                    binding.editRspEmailId.setText(textView50.text)
                    binding.editRspUserId.setText(textView4.text)
                    binding.editNewPhoneText.setText(FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex].RSP_Phone)
                    Log.v(
                        "RSP PHONE ===> ",
                        FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex].RSP_Phone
                    )
                    binding.editRspUserId.isEnabled =
                        !binding.editNewSignerCheck.isChecked && binding.editRspUserId.text.isNullOrEmpty()
                    binding.editRspEmailId.isEnabled =
                        !binding.editNewSignerCheck.isChecked //&& binding.editRspEmailId.text.isNullOrEmpty()
//                    binding.editRspEmailId.isEnabled = (binding.editRspEmailId.text.isNullOrEmpty())
                    if (textView8.text.isNullOrEmpty() || textView8.equals("01/01/1900")) {
                        binding.editNewStartDateBtn.setText("SELECT DATE")
                    } else {
                        binding.editNewStartDateBtn.setText(textView8.text)
                    }
                    if (textView9.text.isNullOrEmpty() || textView9.equals("01/01/1900")) {
                        binding.editNewEndDateBtn.setText("SELECT DATE")
                        currEndDate = "SELECT DATE"
                    } else {
                        binding.editNewEndDateBtn.setText(textView9.text)
                        currEndDate = textView9.text.toString()
                    }
                    if (textView6.text.isNullOrEmpty() || textView6.equals("01/01/1900")) {
                        binding.editNewSeniorityDateBtn.setText("SELECT DATE")
                    } else {
                        binding.editNewSeniorityDateBtn.setText(textView6.text)

                    }

//                    if (textView6.text.isNullOrEmpty() || textView6.equals("01/01/1900")) {
//                        edit_newSeniorityDateBtn.setText("SELECT DATE")
//                    }else{
//                        edit_newSeniorityDateBtn.setText(textView6.text)
//
//                    }

                    if (textViewOEMEnd.text.isNullOrEmpty()) {
                        binding.newEditOEMEndDateBtn.setText("SELECT DATE")
                    } else {
                        binding.newEditOEMEndDateBtn.setText(textViewOEMEnd.text)
                    }

                    if (textViewOEMStart.text.isNullOrEmpty()) {
                        binding.newEditOEMStartDateBtn.setText("SELECT DATE")
                    } else {
                        binding.newEditOEMStartDateBtn.setText(textViewOEMStart.text)
                    }

                    binding.newEditACEURLText.setText(textViewAceURL.text)
                    edithyperlinktxt =
                        "<a href='" + binding.newEditACEURLText.text.toString() + "'>" + binding.newEditACEURLText.text.toString() + "</a>"
                    binding.editurlLink.text =
                        Html.fromHtml(edithyperlinktxt, Html.FROM_HTML_MODE_COMPACT)

                    if (FacilityDataModel.getInstance().tblPersonnelSigner.filter { S -> S.PersonnelID == textView2.tag }
                            .count() > 0) {
                        var model = TblPersonnelSigner()
                        model =
                            FacilityDataModel.getInstance().tblPersonnelSigner.filter { S -> S.PersonnelID == textView2.tag }[0]
//                        edit_newPhoneText.setText(model.Phone)
                        binding.editNewZipText.setText(model.ZIP)
                        binding.editNewAdd1Text.setText(model.Addr1)
                        binding.editNewAdd2Text.setText(model.Addr2)
                        binding.editNewCityText.setText(model.CITY)
                        binding.editNewZipText2.setText(model.ZIP4)
                        binding.editNewEmailText.setText(model.email)
                        binding.editNewStateSpinner.setSelection(statesAbbrev.indexOf(model.ST))
                        if (model.ContractStartDate.isNullOrEmpty() || model.ContractStartDate.equals(
                                "01/01/1900"
                            )
                        ) {
                            binding.editNewCoStartDateBtn.setText("SELECT DATE")
                        } else {
                            binding.editNewCoStartDateBtn.setText(model.ContractStartDate.apiToAppFormatMMDDYYYY())
                        }
                    } else {
//                        edit_newPhoneText.setText("")
                        binding.editNewZipText.setText("")
                        binding.editNewAdd1Text.setText("")
                        binding.editNewAdd2Text.setText("")
                        binding.editNewCityText.setText("")
                        binding.editNewZipText2.setText("")
                        binding.editNewEmailText.setText(textView5.text)
                        binding.editNewStateSpinner.setSelection(0)
                        binding.editNewCoStartDateBtn.setText("SELECT DATE")
                    }
                    binding.editNewCoEndDateBtn.setText("SELECT DATE")
                    var i = personTypeArray.indexOf(textView1.text.toString())
                    binding.editNewPersonnelTypeSpinner.setSelection(i)
                    binding.editNewZipText.setError(null)
                    binding.editNewZipText2.setError(null)
                    binding.editNewPhoneText.setError(null)
                    binding.editNewCertNoText.setError(null)
                    binding.editStateTextView.setError(null)
                    binding.editNewEmailText.setError(null)
                    binding.editPersonnelTypeTextViewId.setError(null)





                    binding.editNewACSCheck.isChecked = checkBox11.isChecked
                    binding.editNewReportCheck.isChecked = checkBox12.isChecked
                    binding.editNewNotificationCheck.isChecked = checkBox13.isChecked
                    binding.editNewComplaintCheck.isChecked = checkBox14.isChecked
                    binding.editAddNewPersonnelDialogue.visibility = View.VISIBLE
                    (activity as FormsActivity).overrideBackButton = true
                    binding.alphaBackgroundForPersonnelDialogs.visibility = View.VISIBLE
                    binding.editSubmitNewPersnRecordBtn.setOnClickListener {
                        if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                            if (FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PrimaryMailRecipient == true }
                                    .isNotEmpty() || binding.editNewACSCheck.isChecked) {
                                if (edit_validateInputs()) {
                                    binding.editAddNewPersonnelDialogue.visibility = View.GONE
                                    binding.alphaBackgroundForPersonnelDialogs.visibility =
                                        View.GONE
                                    (activity as FormsActivity).overrideBackButton = false
                                    binding.personnelLoadingText.text = "Saving ..."
                                    binding.personnelLoadingView.visibility = View.VISIBLE
                                    var PersonnelTypeId = ""
                                    for (fac in TypeTablesModel.getInstance().PersonnelType) {
                                        if (binding.editNewPersonnelTypeSpinner.getSelectedItem()
                                                .toString().equals(fac.PersonnelTypeName)
                                        )
                                            PersonnelTypeId = fac.PersonnelTypeID
                                    }
                                    var FirstName = if (binding.editNewFirstNameText.text.toString()
                                            .isNullOrEmpty()
                                    ) "" else binding.editNewFirstNameText.text.toString()
                                    var LastName = if (binding.editNewLastNameText.text.toString()
                                            .isNullOrEmpty()
                                    ) "" else binding.editNewLastNameText.text.toString()
                                    var RSP_UserName =
                                        if (binding.editRoleHint.visibility == VISIBLE) "" else binding.editRspUserId.text.toString()
                                    var RSP_Email = binding.editRspEmailId.text.toString()
                                    var facNo =
                                        FacilityDataModel.getInstance().tblFacilities[0].FACNo
                                    var CertificationNum =
                                        if (binding.editNewCertNoText.text.toString()
                                                .isNullOrEmpty()
                                        ) "" else binding.editNewCertNoText.text.toString()
                                    var ASECertificationNum =
                                        if (binding.editNewASECertNoText.text.toString()
                                                .isNullOrEmpty() || binding.editNewASECertNoText.text.toString()
                                                .equals("ASE-")
                                        ) "" else binding.editNewASECertNoText.text.toString()
                                    var ContractSigner =
                                        if (binding.editNewSignerCheck.isChecked == true) "1" else "0"
                                    var PrimaryMailRecipient =
                                        if (binding.editNewACSCheck.isChecked == true) "1" else "0"
                                    var ReportRec =
                                        if (binding.editNewReportCheck.isChecked == true) "1" else "0"
                                    var NotificationRec =
                                        if (binding.editNewNotificationCheck.isChecked == true) "1" else "0"
                                    var ComplaintContact =
                                        if (binding.editNewComplaintCheck.isChecked == true) "1" else "0"
                                    var startDate =
                                        if (binding.editNewStartDateBtn.text.equals("SELECT DATE")) "" else binding.editNewStartDateBtn.text.toString()
                                            .appToApiSubmitFormatMMDDYYYY()
                                    var ExpirationDate =
                                        if (binding.editNewEndDateBtn.text.equals("SELECT DATE")) "" else binding.editNewEndDateBtn.text.toString()
                                            .appToApiSubmitFormatMMDDYYYY()
                                    var SeniorityDate =
                                        if (binding.editNewSeniorityDateBtn.text.equals("SELECT DATE")) "" else binding.editNewSeniorityDateBtn.text.toString()
                                            .appToApiSubmitFormatMMDDYYYY()
                                    var personnelID =
                                        FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex].PersonnelID
                                    var OEMStartDate =
                                        if (binding.newEditOEMStartDateBtn.text.equals("SELECT DATE")) "" else binding.newEditOEMStartDateBtn.text.toString()
                                            .appToApiSubmitFormatMMDDYYYY()
                                    var OEMEndDate =
                                        if (binding.newEditOEMEndDateBtn.text.equals("SELECT DATE")) "" else binding.newEditOEMEndDateBtn.text.toString()
                                            .appToApiSubmitFormatMMDDYYYY()
                                    var ace_url = if (binding.newEditACEURLText.text.toString()
                                            .isNullOrEmpty()
                                    ) "" else binding.newEditACEURLText.text.toString()
                                    var sendToRSP = 0
                                    var rspActionId = 0
                                    if ((binding.editRoleHint.visibility == VISIBLE) && binding.editRspUserId.text.toString()
                                            .isNotEmpty()
                                    ) {
                                        sendToRSP = 1
                                        rspActionId = 3 // Delete
                                    } else if (binding.editRspUserId.text.toString() != currRSPUserID) {
                                        sendToRSP = 1
                                        rspActionId = 1 // ADD
                                    } else if (binding.editRspUserId.text.toString() == currRSPUserID && binding.editRspUserId.text.isNotEmpty()) {
                                        if (binding.editNewEndDateBtn.text.toString() != currEndDate) {
                                            sendToRSP = 1
                                            rspActionId = 3 // Delete
                                        } else if (binding.editRspEmailId.text.toString() != currRSPEmailID) {
                                            sendToRSP = 1
                                            rspActionId = 2 // Update
                                        } else if (binding.editNewFirstNameText.text.toString() != currFirstName) {
                                            sendToRSP = 1
                                            rspActionId = 2 // Update
                                        } else if (binding.editNewLastNameText.text.toString() != currLastName) {
                                            sendToRSP = 1
                                            rspActionId = 2 // Update
//                                        } else if (binding.editNewEndDateBtn.text.toString() != currEndDate) {
//                                            sendToRSP = 1
//                                            rspActionId = 3 // Delete
                                        } else if (binding.editNewPersonnelTypeSpinner.getSelectedItem()
                                                .toString() != currTitle
                                        ) {
                                            sendToRSP = 1
                                            rspActionId = 2 // Update
                                        }
                                    }
                                    Log.v(
                                        "EDIT PERSONNEL ->",
                                        UpdateFacilityPersonnelData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=" + FacilityDataModel.getInstance().clubCode + "&personnelId=${personnelID}&personnelTypeId=$PersonnelTypeId&firstName=$FirstName&lastName=${LastName}&seniorityDate=$SeniorityDate&certificationNum=$CertificationNum&certificationNumASE=$ASECertificationNum&startDate=$startDate&contractSigner=$ContractSigner&insertBy=${
                                            ApplicationPrefs.getInstance(activity).loggedInUserID
                                        }&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${
                                            ApplicationPrefs.getInstance(
                                                activity
                                            ).loggedInUserID
                                        }&sendToRSP=$sendToRSP&rspActionId=$rspActionId&updateDate=" + Date().toApiSubmitFormat() + "&active=1&primaryMailRecipient=$PrimaryMailRecipient&rsp_userName=$RSP_UserName&rsp_email=$RSP_Email&rsp_phone=${binding.editNewPhoneText.text}&endDate=${ExpirationDate}&ASE_URL=${ace_url}&OEMStartDate=${OEMStartDate}&OEMEndDate=${OEMEndDate}&ReportRecipient=${ReportRec}&NotificationRecipient=${NotificationRec}&ComplaintContact=${ComplaintContact}" + Utility.getLoggingParameters(
                                            activity,
                                            1,
                                            getPersonnelChanges(1, currentfacilityDataModelIndex)
                                        )
                                    )
                                    Volley.newRequestQueue(context).add(
                                        StringRequest(Request.Method.GET,
                                            UpdateFacilityPersonnelData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=" + FacilityDataModel.getInstance().clubCode + "&personnelId=${personnelID}&personnelTypeId=$PersonnelTypeId&firstName=$FirstName&lastName=${LastName}&seniorityDate=$SeniorityDate&certificationNum=$CertificationNum&certificationNumASE=$ASECertificationNum&startDate=$startDate&contractSigner=$ContractSigner&insertBy=${
                                                ApplicationPrefs.getInstance(activity).loggedInUserID
                                            }&insertDate=" + Date().toApiSubmitFormat() + "&sendToRSP=$sendToRSP&rspActionId=$rspActionId&updateBy=${
                                                ApplicationPrefs.getInstance(
                                                    activity
                                                ).loggedInUserID
                                            }&updateDate=" + Date().toApiSubmitFormat() + "&active=1&primaryMailRecipient=$PrimaryMailRecipient&rsp_userName=$RSP_UserName&rsp_email=$RSP_Email&rsp_phone=${binding.editNewPhoneText.text}&endDate=${ExpirationDate}&ASE_URL=${ace_url}&OEMStartDate=${OEMStartDate}&OEMEndDate=${OEMEndDate}&ReportRecipient=${ReportRec}&NotificationRecipient=${NotificationRec}&ComplaintContact=${ComplaintContact}" + Utility.getLoggingParameters(
                                                activity,
                                                1,
                                                getPersonnelChanges(
                                                    1,
                                                    currentfacilityDataModelIndex
                                                )
                                            ),
                                            { response ->
                                                requireActivity().runOnUiThread {
                                                    Log.v(
                                                        "EDIT PERSONNEL RESPONSE",
                                                        response.toString()
                                                    )

                                                    var rspResponse = ""
                                                    var tokenResponse = ""
                                                    var personnelResponse = ""
                                                    if (it.toString().contains(
                                                            "CreatePersonnel:[Failed",
                                                            false
                                                        )
                                                    ) {
                                                        personnelResponse =
                                                            response.toString().substring(
                                                                response.toString()
                                                                    .indexOf("CreatePersonnel:[") + 17,
                                                                response.toString().indexOf("]")
                                                            )
//                                                        val errorMsg = response.toString().substring(
//                                                            response.toString().indexOf("CreatePersonnel:[") + 17,
//                                                            response.toString().indexOf("]"))
//                                                        Utility.showSubmitAlertDialog(
//                                                            activity,
//                                                            false,
//                                                            "Personnel ( " + errorMsg + " )"
//                                                        )
                                                    } else if (response.toString().contains(
                                                            "CreatePersonnel:[Success",
                                                            false
                                                        )
                                                    ) {
                                                        if (response.toString()
                                                                .contains("RSPToken")
                                                        ) tokenResponse =
                                                            response.toString().substring(
                                                                response.toString()
                                                                    .indexOf("RSPToken:[") + 10,
                                                                response.toString()
                                                                    .indexOf("]\nCreateRSPUser")
                                                            )
                                                        if (response.toString()
                                                                .contains("CreateRSPUser")
                                                        ) rspResponse =
                                                            response.toString().substring(
                                                                response.toString()
                                                                    .indexOf("CreateRSPUser:[") + 15,
                                                                response.toString()
                                                                    .lastIndexOf("]")
                                                            )
                                                        Log.v("RSP TOKEN", tokenResponse)
                                                        Log.v("RSP RESPONSE", rspResponse)
                                                        Log.v(
                                                            "PERSONNEL RESPONSE",
                                                            personnelResponse
                                                        )
                                                    }
                                                    if (response.toString()
                                                            .contains("returnCode>0<", false)
                                                    ) {
                                                        HasChangedModel.getInstance().updateChangedData("Personnel Screen","Personnel","",getPersonnelChanges(
                                                            1,
                                                            currentfacilityDataModelIndex
                                                        ))
                                                        if ((response.toString()
                                                                .contains("<ErrorFlag>1</ErrorFlag>")) //&& sendToRSP == 0
                                                        ) {
                                                            var errorMsg = response.toString()
                                                                .substring(
                                                                    response.toString()
                                                                        .indexOf("<ErrorMsg") + 10,
                                                                    response.toString()
                                                                        .indexOf("</ErrorMsg")
                                                                )
                                                            Utility.showUnifiedErrorDialog(
                                                                activity,
                                                                "Personnel -" + errorMsg
                                                            )
                                                        } else {
//                                                            Utility.showSubmitAlertDialog(
//                                                                activity,
//                                                                true,
//                                                                "Personnel"
//                                                            )
                                                            var msg =
                                                                "Personnel: Record saved successfully"
                                                            if (personnelResponse.isNotEmpty()) msg =
                                                                "Personnel: " + personnelResponse
                                                            if (tokenResponse.isNotEmpty())
                                                                msg += "\nRSP Token: " + tokenResponse
                                                            if (rspResponse.isNotEmpty())
                                                                msg += "\nRSP Action: " + rspResponse
                                                            Utility.showUnifiedInformationDialog(
                                                                activity,
                                                                msg
                                                            )
                                                            var item =
                                                                FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex]
                                                            for (fac in TypeTablesModel.getInstance().PersonnelType) {
                                                                if (binding.editNewPersonnelTypeSpinner.getSelectedItem()
                                                                        .toString()
                                                                        .equals(fac.PersonnelTypeName)
                                                                )
                                                                    item.PersonnelTypeID =
                                                                        fac.PersonnelTypeID.toInt()
                                                            }
                                                            item.FirstName = FirstName
                                                            item.LastName = LastName
                                                            item.RSP_UserName = RSP_UserName
                                                            item.RSP_Email = RSP_Email
                                                            item.RSP_Phone =
                                                                binding.editNewPhoneText.text.toString()
                                                            item.CertificationNum = CertificationNum
                                                            if (!response.toString()
                                                                    .contains("Duplicate ASE Certification ID")
                                                            ) {
                                                                item.CertificationNum_ASE =
                                                                    ASECertificationNum
                                                            }
                                                            item.ContractSigner =
                                                                binding.editNewSignerCheck.isChecked
                                                            item.PrimaryMailRecipient =
                                                                binding.editNewACSCheck.isChecked
                                                            item.startDate = startDate
                                                            item.endDate = ExpirationDate
                                                            item.SeniorityDate = SeniorityDate
                                                            item.ASE_Cert_URL = ace_url
                                                            item.OEMstartDate = OEMStartDate
                                                            item.OEMendDate = OEMEndDate
                                                            item.ReportRecipient =
                                                                binding.editNewReportCheck.isChecked
                                                            item.NotificationRecipient =
                                                                binding.editNewNotificationCheck.isChecked
                                                            item.ComplaintContact =
                                                                binding.editNewComplaintCheck.isChecked
                                                            HasChangedModel.getInstance().groupFacilityPersonnel[0].FacilityPersonnel =
                                                                true
                                                            HasChangedModel.getInstance()
                                                                .changeDoneForFacilityPersonnel()
                                                            (activity as FormsActivity).saveDone =
                                                                true
                                                            if (ContractSigner.toBoolean()) {
                                                                val coAddr1 =
                                                                    if (binding.editNewAdd1Text.text.toString()
                                                                            .isNullOrEmpty()
                                                                    ) "" else binding.editNewAdd1Text.text.toString()
                                                                val coAddr2 =
                                                                    if (binding.editNewAdd2Text.text.toString()
                                                                            .isNullOrEmpty()
                                                                    ) "" else binding.editNewAdd2Text.text.toString()
                                                                val coCITY =
                                                                    if (binding.editNewCityText.text.toString()
                                                                            .isNullOrEmpty()
                                                                    ) "" else binding.editNewCityText.text.toString()
                                                                //                                                    val coST= if (edit_newStateSpinner.selectedItem.toString().isNullOrEmpty()) "" else edit_newStateSpinner.selectedItem.toString()
                                                                val coST =
                                                                    if (binding.editNewStateSpinner.selectedItemPosition == 0) "" else statesAbbrev.get(
                                                                        binding.editNewStateSpinner.selectedItemPosition
                                                                    );
                                                                val coZIP =
                                                                    if (binding.editNewZipText.text.toString()
                                                                            .isNullOrEmpty()
                                                                    ) "" else binding.editNewZipText.text.toString()
                                                                val coZIP4 =
                                                                    if (binding.editNewZipText2.text.toString()
                                                                            .isNullOrEmpty()
                                                                    ) "" else binding.editNewZipText2.text.toString()
                                                                val coPhone =
                                                                    binding.newPhoneText.text.toString()
                                                                val coemail =
                                                                    binding.editRspEmailId.text.toString()
                                                                val coContractStartDate =
                                                                    if (binding.editNewCoStartDateBtn.text.equals(
                                                                            "SELECT DATE"
                                                                        )
                                                                    ) "" else binding.editNewCoStartDateBtn.text.toString()
                                                                        .appToApiSubmitFormatMMDDYYYY()
                                                                val coContractEndDate =
                                                                    if (binding.editNewCoEndDateBtn.text.equals(
                                                                            "SELECT DATE"
                                                                        )
                                                                    ) "" else binding.editNewCoEndDateBtn.text.toString()
                                                                        .appToApiSubmitFormatMMDDYYYY()
                                                                Volley.newRequestQueue(context).add(
                                                                    StringRequest(Request.Method.GET,
                                                                        UpdateFacilityPersonnelSignerData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=" + FacilityDataModel.getInstance().clubCode + "&personnelId=${item.PersonnelID}&addr1=${coAddr1}&addr2=${coAddr2}&city=${coCITY}&st=${coST}&phone=${coPhone}&email=${coemail}&zip=${coZIP}&zip4=${coZIP4}&contractStartDate=${coContractStartDate}&contractEndDate=${coContractEndDate}&insertBy=${
                                                                            ApplicationPrefs.getInstance(
                                                                                activity
                                                                            ).loggedInUserID
                                                                        }&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${
                                                                            ApplicationPrefs.getInstance(
                                                                                activity
                                                                            ).loggedInUserID
                                                                        }&updateDate=" + Date().toApiSubmitFormat() + "&active=1",
                                                                        { response ->
                                                                            requireActivity().runOnUiThread {
                                                                                if (response.toString()
                                                                                        .contains(
                                                                                            "returnCode>0<",
                                                                                            false
                                                                                        )
                                                                                ) {
//                                                                                    HasChangedModel.getInstance().updateChangedData("Personnel Screen","Personnel","",getPersonnelChanges(
//                                                                                        1,
//                                                                                        currentfacilityDataModelIndex
//                                                                                    ))
                                                                                    Utility.showSubmitAlertDialog(
                                                                                        activity,
                                                                                        true,
                                                                                        "Contract Signer"
                                                                                    )
                                                                                    item.ContractStartDate =
                                                                                        coContractStartDate
                                                                                    item.ContractEndDate =
                                                                                        coContractEndDate
                                                                                    item.email =
                                                                                        coemail
                                                                                    item.Addr1 =
                                                                                        coAddr1
                                                                                    item.Addr2 =
                                                                                        coAddr2
                                                                                    item.CITY =
                                                                                        coCITY
                                                                                    item.ST = coST
                                                                                    item.ZIP4 =
                                                                                        coZIP4
                                                                                    item.ZIP = coZIP
                                                                                    //                                                                                item.RSP_Phone = coPhone
                                                                                    var signerItem =
                                                                                        TblPersonnelSigner()
                                                                                    signerItem.PersonnelID =
                                                                                        item.PersonnelID
                                                                                    signerItem.ContractStartDate =
                                                                                        coContractStartDate
                                                                                    signerItem.email =
                                                                                        coemail
                                                                                    signerItem.Addr1 =
                                                                                        coAddr1
                                                                                    signerItem.Addr2 =
                                                                                        coAddr2
                                                                                    signerItem.CITY =
                                                                                        coCITY
                                                                                    signerItem.ST =
                                                                                        coST
                                                                                    signerItem.ZIP4 =
                                                                                        coZIP4
                                                                                    signerItem.ZIP =
                                                                                        coZIP
                                                                                    signerItem.Phone =
                                                                                        coPhone
                                                                                    if (FacilityDataModel.getInstance().tblPersonnelSigner.filter { s -> s.PersonnelID == signerItem.PersonnelID }
                                                                                            .isEmpty())
                                                                                        FacilityDataModel.getInstance().tblPersonnelSigner.add(
                                                                                            signerItem
                                                                                        )
                                                                                    else {
                                                                                        FacilityDataModel.getInstance().tblPersonnelSigner.removeIf { s -> s.PersonnelID == signerItem.PersonnelID }
                                                                                        FacilityDataModel.getInstance().tblPersonnelSigner.add(
                                                                                            signerItem
                                                                                        )
                                                                                    }
                                                                                    fillPersonnelTableView()
                                                                                    altTableRow(2)
                                                                                } else {
                                                                                    var errorMessage =
                                                                                        response.toString()
                                                                                            .substring(
                                                                                                response.toString()
                                                                                                    .indexOf(
                                                                                                        "<message"
                                                                                                    ) + 9,
                                                                                                response.toString()
                                                                                                    .indexOf(
                                                                                                        "</message"
                                                                                                    )
                                                                                            )
                                                                                    Utility.showSubmitAlertDialog(
                                                                                        activity,
                                                                                        false,
                                                                                        "Contract Signer (Error: " + errorMessage + " )"
                                                                                    )
                                                                                }
                                                                                binding.personnelLoadingView.visibility =
                                                                                    View.GONE
                                                                                binding.personnelLoadingText.text =
                                                                                    "Loading ..."
                                                                            }
                                                                        },
                                                                        {
                                                                            Utility.showSubmitAlertDialog(
                                                                                activity,
                                                                                false,
                                                                                "Contract Signer (Error: " + it.message + " )"
                                                                            )
                                                                            binding.personnelLoadingView.visibility =
                                                                                View.GONE
                                                                            binding.personnelLoadingText.text =
                                                                                "Loading ..."
                                                                        })
                                                                )

                                                            } else {
                                                                FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex] =
                                                                    item
                                                                FacilityDataModelOrg.getInstance().tblPersonnel[currentfacilityDataModelIndex] =
                                                                    item
//                                                                FacilityDataModelOrg.getInstance().tblPersonnel[currentfacilityDataModelIndex] = item
                                                                fillPersonnelTableView()
                                                                altTableRow(2)
                                                            }
                                                        }
                                                        binding.personnelLoadingView.visibility =
                                                            View.GONE
                                                        binding.personnelLoadingText.text =
                                                            "Loading ..."
                                                    } else {
                                                        //                                                Utility.showSubmitAlertDialog(activity, false, "Personnel")
                                                        var errorMessage = response.toString()
                                                        try {
                                                            errorMessage = response.toString()
                                                                .substring(
                                                                    response.toString()
                                                                        .indexOf("<message") + 9,
                                                                    response.toString()
                                                                        .indexOf("</message")
                                                                )
                                                        } catch (e: Exception) {

                                                        }
                                                        Utility.showSubmitAlertDialog(
                                                            activity,
                                                            false,
                                                            "Personnel (Error: " + errorMessage + " )"
                                                        )
                                                        binding.personnelLoadingView.visibility =
                                                            View.GONE
                                                        binding.personnelLoadingText.text =
                                                            "Loading ..."
                                                    }
                                                }
                                            },
                                            {
                                                Utility.showSubmitAlertDialog(
                                                    activity,
                                                    false,
                                                    "Personnel (Error: " + it.message + " )"
                                                )
                                                binding.personnelLoadingView.visibility = View.GONE
                                                binding.personnelLoadingText.text = "Loading ..."
                                                fillPersonnelTableView()
                                                altTableRow(2)
                                            })
                                    ).setRetryPolicy(
                                        DefaultRetryPolicy(
                                            10000,
                                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                        )
                                    )

                                } else {

                                    //                            Toast.makeText(context,"please fill the required fields",Toast.LENGTH_SHORT).show()
                                    //                            Utility.showValidationAlertDialog(activity,"Please fill all the required fields")
                                    Utility.showValidationAlertDialog(activity, validationMsg)

                                }
                            } else {
                                Utility.showValidationAlertDialog(
                                    activity,
                                    "Shop has no primary mail recipient, Please assign it first"
                                )
                            }
                        } else {
                            Utility.showInternetWarningDialog(
                                requireContext(),
                                (requireActivity() as FormsActivity).networkStatusErrorMsg
                            )
                        }

                    }


                }


            }
        }
        if (FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelTypeID == 47 }
                .isEmpty()) {
            Log.v("Status -->", " NO PRG USER")
            binding.addNewPRGRecordBtn.visibility = VISIBLE
        } else {
            Log.v("Status -->", " THERE IS PRG USER")
            binding.addNewPRGRecordBtn.visibility = GONE
        }
        binding.addNewPersonnelDialogue.visibility = View.GONE
        binding.alphaBackgroundForPersonnelDialogs.visibility = View.GONE
    }


    fun fillCertificationTableView(personnelID: Int) {

        if (binding.certificationsTable.childCount > 1) {
            for (i in binding.certificationsTable.childCount - 1 downTo 1) {
                binding.certificationsTable.removeViewAt(i)
            }
        }
        var showCertAlarm = true
        FacilityDataModel.getInstance().tblPersonnelCertification.filter { s ->
            s.PersonnelID.equals(
                personnelID
            )
        }.apply {
            (0 until size).forEach {
                if (!get(it).CertificationTypeId.isNullOrEmpty()) {
                    // && TypeTablesModel.getInstance().PersonnelCertificationType.filter { s->s.PersonnelCertID.equals(get(it).CertificationTypeId)}.isNotEmpty()) {
                    showCertAlarm = true
                    val rowLayoutParam = TableRow.LayoutParams()
                    rowLayoutParam.weight = 0.5F
                    rowLayoutParam.column = 0
                    rowLayoutParam.leftMargin = 10
                    rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
                    rowLayoutParam.width = 0
                    rowLayoutParam.gravity = Gravity.CENTER_VERTICAL

                    val rowLayoutParam1 = TableRow.LayoutParams()
                    rowLayoutParam1.weight = 2F
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

                    val rowLayoutParam3 = TableRow.LayoutParams()
                    rowLayoutParam3.weight = 1F
                    rowLayoutParam3.column = 3
                    rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
                    rowLayoutParam3.width = 0
                    rowLayoutParam3.gravity = Gravity.CENTER_VERTICAL

                    val rowLayoutParam4 = TableRow.LayoutParams()
                    rowLayoutParam4.weight = 1F
                    rowLayoutParam4.column = 4
                    rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
                    rowLayoutParam4.width = 0
                    rowLayoutParam4.gravity = Gravity.CENTER_VERTICAL

                    val rowLayoutParam5 = TableRow.LayoutParams()
                    rowLayoutParam5.weight = 0.5F
                    rowLayoutParam5.column = 5
                    rowLayoutParam5.height = TableRow.LayoutParams.WRAP_CONTENT
                    rowLayoutParam5.width = 0
                    rowLayoutParam5.gravity = Gravity.CENTER_VERTICAL

                    val rowLayoutParamhidden = TableRow.LayoutParams()
                    rowLayoutParamhidden.weight = 0F
                    rowLayoutParamhidden.column = 5
                    rowLayoutParamhidden.height = TableRow.LayoutParams.WRAP_CONTENT
                    rowLayoutParamhidden.width = 0
                    rowLayoutParamhidden.gravity = Gravity.CENTER_VERTICAL


                    val rowLayoutParamRow = TableRow.LayoutParams()
                    rowLayoutParamRow.height = TableRow.LayoutParams.WRAP_CONTENT
                    rowLayoutParamRow.weight = 1F


                    val tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow


                    val textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.gravity = Gravity.CENTER
                    textView.text =
                        if (get(it).CertificationTypeId.contains("OEM")) "OEM" else "ASE"
                    textView.textSize = 14f
                    textView.setTextColor(Color.BLACK)
                    tableRow.addView(textView)


                    val textView1 = TextView(context)
                    textView1.layoutParams = rowLayoutParam1
                    textView1.gravity = Gravity.CENTER
                    if (TypeTablesModel.getInstance().PersonnelCertificationType.filter { s ->
                            s.PersonnelCertID.equals(
                                get(it).CertificationTypeId
                            )
                        }.isNotEmpty())
                        textView1.text =
                            TypeTablesModel.getInstance().PersonnelCertificationType.filter { s ->
                                s.PersonnelCertID.equals(get(it).CertificationTypeId)
                            }[0].PersonnelCertName
                    else
                        textView1.text = get(it).CertificationTypeId
                    textView1.textSize = 14f
                    textView1.setTextColor(Color.BLACK)
                    tableRow.addView(textView1)
                    if (textView1.text.contains("A9") || textView1.text.contains("L1") || textView1.text.contains(
                            "C1"
                        )
                    ) showCertAlarm = false

                    val textView2 = TextView(context)
                    textView2.layoutParams = rowLayoutParam2
                    textView2.gravity = Gravity.CENTER
                    textView2.textSize = 14f
                    textView2.setTextColor(Color.BLACK)
                    try {
                        textView2.text = get(it).CertificationDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textView2.text = ""
                    }
                    tableRow.addView(textView2)

                    val textView3 = TextView(context)
                    textView3.layoutParams = rowLayoutParam3
                    textView3.gravity = Gravity.CENTER
                    textView3.textSize = 14f
                    textView3.setTextColor(Color.BLACK)
//                    TableRow.LayoutParams()
                    try {
                        textView3.text = get(it).ExpirationDate.apiToAppFormatMMDDYYYY()
                        if (showCertAlarm) {
                            val sdf = SimpleDateFormat("MM/dd/yyyy")
                            val ASEExpDate =
                                sdf.parse(get(it).ExpirationDate.apiToAppFormatMMDDYYYY())
                            val ASEExpDatedays =
                                (ASEExpDate.getTime() - Date().getTime()) / 1000 / 60 / 60 / 24
                            val animation: Animation = AlphaAnimation(1.0f, 0.0f)
                            animation.duration = 500 //1 second duration for each animation cycle
                            animation.interpolator = LinearInterpolator()
                            animation.repeatCount = Animation.INFINITE //repeating indefinitely
                            animation.repeatMode =
                                Animation.REVERSE //animation will start from end point once ended.
                            if (ASEExpDatedays <= 0) {
                                textView3.setTextColor(Color.RED)
                                textView3.startAnimation(animation) //to start animation
                            } else if (ASEExpDatedays <= 180) {
                                textView3.setTextColor(resources.getColor(R.color.dark_yellow))
                                textView3.startAnimation(animation) //to start animation
                            } else {
                                textView3.clearAnimation()
                            }
                        }
                    } catch (e: Exception) {
                        textView3.text = ""
                    }
                    tableRow.addView(textView3)

                    val textView4 = TextView(context)
                    textView4.layoutParams = rowLayoutParam4
                    textView4.gravity = Gravity.CENTER
                    textView4.text = get(it).CertDesc
                    textView4.textSize = 14f
                    textView4.setTextColor(Color.BLACK)
                    tableRow.addView(textView4)

                    val textViewhidden = TextView(context)
                    textViewhidden.layoutParams = rowLayoutParamhidden
                    textViewhidden.gravity = Gravity.CENTER
                    textViewhidden.text = get(it).CertID
                    textViewhidden.textSize = 14f
                    textViewhidden.setTextColor(Color.BLACK)
                    textViewhidden.visibility = View.GONE
                    tableRow.addView(textViewhidden)

                    val updateCertBtn = TextView(context)
                    updateCertBtn.layoutParams = rowLayoutParam5
                    updateCertBtn.setTextColor(Color.BLUE)
                    updateCertBtn.text = "EDIT"
                    updateCertBtn.gravity = Gravity.CENTER
                    updateCertBtn.textSize = 12f
//                    updateCertBtn.tag = get(it).CertID
                    updateCertBtn.setBackgroundColor(Color.TRANSPARENT)

                    tableRow.addView(updateCertBtn)

                    binding.certificationsTable.addView(tableRow)

                    updateCertBtn.setOnClickListener {
                        binding.editNewCertificateDialogue.visibility = View.VISIBLE
                        (activity as FormsActivity).overrideBackButton = true
                        binding.alphaBackgroundForPersonnelDialogs.visibility = View.VISIBLE
//                        var currentModelIndex = FacilityDataModel.getInstance().tblPersonnelCertification.filter { s->s.CertificationTypeId.equals(textView.text.toString()) && s.}

//                        FacilityDataModel.getInstance().tblPersonnelCertification[currentTableRowIndex].apply {
//                            (0 until size).forEach { it2 ->
//                                if (get(it2).CertificationTypeId.contains("OEM")) edit_newCertCatSpinner.text = "OEM" else edit_newCertCatSpinner.text = "ASE"
//                                edit_newCertTypeSpinner.text = get(it2).CertificationTypeId
//                            }
//                        }

                        binding.editNewCertCatSpinner.text = textView.text.toString()
                        binding.editNewCertTypeSpinner.text = textView1.text.toString()
                        binding.editNewCertStartDateBtn.text = textView2.text.toString()
                        binding.editNewCertEndDateBtn.text = textView3.text.toString()
                        var currentCertId = textViewhidden.text.toString()

                        binding.editNewCertBtn.setOnClickListener {
                            if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                                if (edit_validateCertificationInputs()) {
                                    binding.editNewCertificateDialogue.visibility = View.GONE
                                    binding.alphaBackgroundForPersonnelDialogs.visibility =
                                        View.GONE
                                    (activity as FormsActivity).overrideBackButton = false
                                    binding.personnelLoadingText.text = "Saving ..."
                                    binding.personnelLoadingView.visibility = View.VISIBLE

                                    var item = TblPersonnelCertification()
                                    //                                for (fac in TypeTablesModel.getInstance().PersonnelCertificationType) {
                                    //                                    if (newCertTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelCertName))
                                    //                                        item.CertificationTypeId = fac.PersonnelCertID
                                    //                                }
                                    item =
                                        FacilityDataModel.getInstance().tblPersonnelCertification.filter { s ->
                                            s.CertID.equals(currentCertId)
                                        }[0]


                                    var urlString =
                                        "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&personnelId=${item.PersonnelID}" +
                                                "&certId=${currentCertId}&certificationTypeId=${item.CertificationTypeId}&certificationDate=${
                                                    binding.editNewCertStartDateBtn.text.toString()
                                                        .appToApiSubmitFormatMMDDYYYY()
                                                }&expirationDate=${
                                                    binding.editNewCertEndDateBtn.text.toString()
                                                        .appToApiSubmitFormatMMDDYYYY()
                                                }" +
                                                "&certDesc=${item.CertDesc}&insertBy=${
                                                    ApplicationPrefs.getInstance(
                                                        activity
                                                    ).loggedInUserID
                                                }&insertDate=${Date().toApiSubmitFormat()}&updateBy=${
                                                    ApplicationPrefs.getInstance(
                                                        activity
                                                    ).loggedInUserID
                                                }&updateDate=${Date().toApiSubmitFormat()}&active=1"
                                    Log.v(
                                        "CERTIFICATION ADD --- ",
                                        Constants.UpdatePersonnelCertification + urlString
                                    )
                                    Volley.newRequestQueue(context).add(
                                        StringRequest(Request.Method.GET,
                                            Constants.UpdatePersonnelCertification + urlString + Utility.getLoggingParameters(
                                                activity,
                                                0,
                                                getCertificationChanges(1, selectedPersonnelID)
                                            ),
                                            Response.Listener { response ->
                                                requireActivity().runOnUiThread {
                                                    if (response.toString()
                                                            .contains("returnCode>0<", false)
                                                    ) {
                                                        HasChangedModel.getInstance().updateChangedData("Personnel Screen","Personnel","",getCertificationChanges(1, selectedPersonnelID))
                                                        Utility.showSubmitAlertDialog(
                                                            activity,
                                                            true,
                                                            "Certification"
                                                        )
                                                        //                                                    item.CertID= response.toString().substring(response.toString().indexOf("<CertID")+8,response.toString().indexOf("</CertID"))
                                                        //                                                    FacilityDataModelOrg.getInstance().tblPersonnelCertification.filter { s->s.CertID.equals(currentCertId)}[0].apply {
                                                        //                                                        (0 until size).forEach {
                                                        //                                                            get(it).CertificationDate = item.CertificationDate
                                                        //                                                            get(it).ExpirationDate = item.ExpirationDate
                                                        //                                                        }
                                                        //                                                    }
                                                        item.CertificationDate =
                                                            binding.editNewCertStartDateBtn.text.toString()
                                                                .appToApiSubmitFormatMMDDYYYY()
                                                        item.ExpirationDate =
                                                            binding.editNewCertEndDateBtn.text.toString()
                                                                .appToApiSubmitFormatMMDDYYYY()
                                                        item =
                                                            FacilityDataModelOrg.getInstance().tblPersonnelCertification.filter { s ->
                                                                s.CertID.equals(currentCertId)
                                                            }[0]
                                                        item.CertificationDate =
                                                            binding.editNewCertStartDateBtn.text.toString()
                                                                .appToApiSubmitFormatMMDDYYYY()
                                                        item.ExpirationDate =
                                                            binding.editNewCertEndDateBtn.text.toString()
                                                                .appToApiSubmitFormatMMDDYYYY()
                                                        //                                                    FacilityDataModel.getInstance().tblPersonnelCertification.filter { s->s.CertID.equals(currentCertId)}[0].apply {
                                                        //                                                        (0 until size).forEach {
                                                        //                                                            get(it).CertificationDate = item.CertificationDate
                                                        //                                                            get(it).ExpirationDate = item.ExpirationDate
                                                        //                                                        }
                                                        //                                                    }
                                                        (activity as FormsActivity).saveDone = true
                                                        HasChangedModel.getInstance().groupFacilityPersonnel[0].FacilityPersonnel =
                                                            true
                                                        HasChangedModel.getInstance()
                                                            .changeDoneForFacilityPersonnel()
                                                        fillCertificationTableView(
                                                            selectedPersonnelID
                                                        )
                                                    } else {
                                                        var errorMessage = response.toString()
                                                            .substring(
                                                                response.toString()
                                                                    .indexOf("<message") + 9,
                                                                response.toString()
                                                                    .indexOf("</message")
                                                            )
                                                        Utility.showSubmitAlertDialog(
                                                            activity,
                                                            false,
                                                            "Certification (Error: " + errorMessage + " )"
                                                        )
                                                    }
                                                    binding.personnelLoadingView.visibility =
                                                        View.GONE
                                                    binding.personnelLoadingText.text =
                                                        "Loading ..."
                                                }
                                            },
                                            Response.ErrorListener {
                                                Utility.showSubmitAlertDialog(
                                                    activity,
                                                    false,
                                                    "Certification (Error: " + it.message + " )"
                                                )
                                                binding.personnelLoadingView.visibility = View.GONE
                                                binding.personnelLoadingText.text = "Loading ..."

                                            })
                                    )
                                    binding.editNewCertificateDialogue.visibility = View.GONE
                                } else {
                                    Utility.showValidationAlertDialog(activity, validationMsg)
                                }
                            } else {
                                Utility.showInternetWarningDialog(
                                    requireContext(),
                                    (requireActivity() as FormsActivity).networkStatusErrorMsg
                                )
                            }
                        }
                    }
                }
                altCertTableRow(2)
            }
        }
    }

    fun addTheLatestRowOfPortalAdmin() {
        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3

        FacilityDataModel.getInstance().tblPersonnel[FacilityDataModel.getInstance().tblPersonnel.size - 1].apply {


            var tableRow = TableRow(context)

            var textView = TextView(context)
            textView.layoutParams = rowLayoutParam
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            for (fac in TypeTablesModel.getInstance().PersonnelCertificationType) {
                if (CertificationTypeId.equals(fac.PersonnelCertID))

                    textView.text = fac.PersonnelCertName
            }
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam1
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

            textView.text = CertificationDate.apiToAppFormatMMDDYYYY()

            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam2
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            TableRow.LayoutParams()
            textView.text = endDate.apiToAppFormatMMDDYYYY()

            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam3
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = ""
            tableRow.addView(textView)


            binding.certificationsTable.addView(tableRow)

        }
        altCertTableRow(2)
    }

    fun addTheLatestRowOfPersonnelTable() {
        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3
        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1F
        rowLayoutParam4.column = 4
        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 1F
        rowLayoutParam5.column = 5
        val rowLayoutParam6 = TableRow.LayoutParams()
        rowLayoutParam6.weight = 1F
        rowLayoutParam6.column = 6
        val rowLayoutParam7 = TableRow.LayoutParams()
        rowLayoutParam7.weight = 1F
        rowLayoutParam7.column = 7
        val rowLayoutParam8 = TableRow.LayoutParams()
        rowLayoutParam8.weight = 1F
        rowLayoutParam8.column = 8
        val rowLayoutParam9 = TableRow.LayoutParams()
        rowLayoutParam9.weight = 1F
        rowLayoutParam9.column = 9
        val rowLayoutParam10 = TableRow.LayoutParams()
        rowLayoutParam10.weight = 1F
        rowLayoutParam10.column = 10

        FacilityDataModel.getInstance().tblPersonnel[FacilityDataModel.getInstance().tblPersonnel.size - 1].apply {


            var tableRow = TableRow(context)

            var textView = TextView(context)
            textView.layoutParams = rowLayoutParam
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            for (fac in TypeTablesModel.getInstance().PersonnelType) {
                if (PersonnelTypeID.equals(fac.PersonnelTypeID))

                    textView.text = fac.PersonnelTypeName
            }
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam1
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = FirstName
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam2
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            TableRow.LayoutParams()
            textView.text = LastName
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam3
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            TableRow.LayoutParams()
            textView.text = RSP_UserName
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam4
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            TableRow.LayoutParams()
            textView.text =
                if (ContractSigner) FacilityDataModel.getInstance().tblPersonnelSigner.filter { s -> s.PersonnelID == PersonnelID }[0].email else RSP_Email
            tableRow.addView(textView)


            textView = TextView(context)
            textView.layoutParams = rowLayoutParam5
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = SeniorityDate.apiToAppFormatMMDDYYYY()
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam6
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = CertificationNum
            tableRow.addView(textView)


            textView = TextView(context)
            textView.layoutParams = rowLayoutParam7
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = startDate.apiToAppFormatMMDDYYYY()
            tableRow.addView(textView)


            textView = TextView(context)
            textView.layoutParams = rowLayoutParam8
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = endDate.apiToAppFormatMMDDYYYY()
            tableRow.addView(textView)

            var checkBox = CheckBox(context)

            checkBox = CheckBox(context)
            checkBox.layoutParams = rowLayoutParam9
            checkBox.textAlignment = CheckBox.TEXT_ALIGNMENT_CENTER
            checkBox.isChecked = (ContractSigner.equals("true"))
            checkBox.isEnabled = false
            tableRow.addView(checkBox)

            checkBox = CheckBox(context)
            checkBox.layoutParams = rowLayoutParam10
            checkBox.textAlignment = CheckBox.TEXT_ALIGNMENT_CENTER
            checkBox.isChecked = (PrimaryMailRecipient.equals("true"))
            checkBox.isEnabled = false
            tableRow.addView(checkBox)

            binding.PersonnelResultsTbl.addView(tableRow)

        }
        altTableRow(2)
    }

    fun onlyOneContractSignerLogic() {
        var alreadyContractSignerFound = false


        FacilityDataModel.getInstance().tblPersonnel.apply {
            (0 until size).forEach {
                if (get(it).ContractSigner) {
                    binding.newSignerCheck.isEnabled = false
//                    edit_newSignerCheck.isEnabled = false
                    alreadyContractSignerFound = true
                    disablecontractSignerFeilds()
                }
            }
            if (!alreadyContractSignerFound) {
                binding.newSignerCheck.isEnabled = false
//                edit_newSignerCheck.isEnabled=true
                disablecontractSignerFeilds()
            }
            binding.newSignerCheck.setOnCheckedChangeListener { buttonView, isChecked ->
                if (binding.newSignerCheck.isChecked) {
                    if (alreadyContractSignerFound) {
                        binding.newSignerCheck.isChecked = false
                        disablecontractSignerFeilds()
                        Utility.showValidationAlertDialog(
                            activity,
                            "There is already a contract signer for this contract"
                        )
                    } else {
                        Utility.showValidationAlertDialog(
                            activity,
                            "No contract signer for this contract"
                        )
                        binding.newSignerCheck.isChecked = true
                        enable_contractSignerFeilds()
                    }
                }
                if (!binding.newSignerCheck.isChecked) {
                    binding.newSignerCheck.isChecked = false
                    disablecontractSignerFeilds()
                }
            }
//            edit_newSignerCheck.setOnCheckedChangeListener { buttonView, isChecked ->
//                if (edit_newSignerCheck.isChecked) {
//                    if (alreadyContractSignerFound) {
//                        edit_newSignerCheck.isChecked=false
//                        disablecontractSignerFeilds()
//                        Utility.showValidationAlertDialog(activity,"There is already a contract signer for this contract")
//                    }else
//                    {
//                        Utility.showValidationAlertDialog(activity,"No contract signer for this contract")
//                        edit_newSignerCheck.isChecked=true
//                        enable_contractSignerFeilds()
//                    }
//                }
//                if (!edit_newSignerCheck.isChecked){
//                    edit_newSignerCheck.isChecked=false
//                    disablecontractSignerFeilds()
//                }
//            }
        }


    }

    fun checkMarkChangesWasDoneForPersonnel() {

        val dateFormat1 = SimpleDateFormat("MM/dd/yyyy")

        var itemOrgAr = FacilityDataModelOrg.getInstance().tblPersonnel
        var itemAr = FacilityDataModel.getInstance().tblPersonnel
        if (itemOrgAr.size == itemAr.size) {
            for (i in 0 until itemOrgAr.size) {

                //if (itemAr[i].PrimaryMailRecipient.o){itemAr[i].PrimaryMailRecipient=false}
                if (
                    (itemOrgAr[i].endDate.isNullOrBlank() && !itemAr[i].endDate.isNullOrBlank()) ||
                    (itemOrgAr[i].startDate.isNullOrBlank() && !itemAr[i].startDate.isNullOrBlank()) ||
                    (itemOrgAr[i].SeniorityDate.isNullOrBlank() && !itemAr[i].SeniorityDate.isNullOrBlank())
                ) {

                    MarkChangeWasDone()
                } else
                    if (
                        (itemOrgAr[i].endDate.isNullOrBlank() && itemAr[i].endDate.isNullOrBlank()) ||
                        (itemOrgAr[i].startDate.isNullOrBlank() && itemAr[i].startDate.isNullOrBlank()) ||
                        (itemOrgAr[i].SeniorityDate.isNullOrBlank() && itemAr[i].SeniorityDate.isNullOrBlank())
                    ) {

                        if (
                            itemAr[i].FirstName != itemOrgAr[i].FirstName || itemAr[i].LastName != itemOrgAr[i].LastName ||
                            itemAr[i].RSP_UserName != itemOrgAr[i].RSP_UserName ||
                            itemAr[i].PersonnelTypeID != itemOrgAr[i].PersonnelTypeID ||
                            itemAr[i].RSP_Email != itemOrgAr[i].RSP_Email ||
                            itemAr[i].CertificationNum != itemOrgAr[i].CertificationNum ||
                            itemAr[i].ContractSigner != itemOrgAr[i].ContractSigner ||
                            itemAr[i].PrimaryMailRecipient != itemOrgAr[i].PrimaryMailRecipient


                        ) {
                            MarkChangeWasDone()

//                             Toast.makeText(context, "data submitted", Toast.LENGTH_SHORT).show()
                            Log.v("checkkk", itemOrgAr[i].FirstName + "==" + itemAr[i].FirstName)
                            Log.v("checkkk", itemOrgAr[i].LastName + "==" + itemAr[i].LastName)
                            Log.v(
                                "checkkk",
                                itemOrgAr[i].RSP_UserName + "==" + itemAr[i].RSP_UserName
                            )
                            Log.v("checkkk", itemOrgAr[i].RSP_Email + "==" + itemAr[i].RSP_Email)
                            Log.v(
                                "checkkk",
                                itemOrgAr[i].CertificationNum + "==" + itemAr[i].CertificationNum
                            )
                            //Log.v("checkkk", itemOrgAr[i].ContractSigner + "=="+ itemAr[i].ContractSigner)
                            //Log.v("checkkk", itemOrgAr[i].PrimaryMailRecipient + "=="+ itemAr[i].PrimaryMailRecipient)
                            //Log.v("checkkk", itemOrgAr[i].PersonnelTypeID + "=="+ itemAr[i].PersonnelTypeID)

                        }
                    } else
                        if (
                            itemAr[i].FirstName != itemOrgAr[i].FirstName || itemAr[i].LastName != itemOrgAr[i].LastName ||
                            itemAr[i].RSP_UserName != itemOrgAr[i].RSP_UserName ||
                            itemAr[i].RSP_Email != itemOrgAr[i].RSP_Email ||
                            itemAr[i].CertificationNum != itemOrgAr[i].CertificationNum ||
                            itemAr[i].PersonnelTypeID != itemOrgAr[i].PersonnelTypeID ||
                            itemAr[i].ContractSigner != itemOrgAr[i].ContractSigner ||
                            itemAr[i].PrimaryMailRecipient != itemOrgAr[i].PrimaryMailRecipient ||
                            dateFormat1.parse(itemAr[i].startDate.apiToAppFormat()) != dateFormat1.parse(
                                itemOrgAr[i].startDate.apiToAppFormat()
                            ) ||
                            dateFormat1.parse(itemAr[i].endDate.apiToAppFormat()) != dateFormat1.parse(
                                itemOrgAr[i].endDate.apiToAppFormat()
                            ) ||
                            dateFormat1.parse(itemAr[i].SeniorityDate.apiToAppFormat()) != dateFormat1.parse(
                                itemOrgAr[i].SeniorityDate.apiToAppFormat()
                            )
                        ) {
                            MarkChangeWasDone()
//                             Toast.makeText(context, "data submitted", Toast.LENGTH_SHORT).show()
                            Log.v("checkkk", itemOrgAr[i].FirstName + "==" + itemAr[i].FirstName)
                            Log.v("checkkk", itemOrgAr[i].LastName + "==" + itemAr[i].LastName)
                            Log.v(
                                "checkkk",
                                itemOrgAr[i].RSP_UserName + "==" + itemAr[i].RSP_UserName
                            )
                            Log.v("checkkk", itemOrgAr[i].RSP_Email + "==" + itemAr[i].RSP_Email)
                            Log.v(
                                "checkkk",
                                itemOrgAr[i].CertificationNum + "==" + itemAr[i].CertificationNum
                            )
                            //Log.v("checkkk", itemOrgAr[i].ContractSigner + "=="+ itemAr[i].ContractSigner)
                            //Log.v("checkkk", itemOrgAr[i].PrimaryMailRecipient + "=="+ itemAr[i].PrimaryMailRecipient)
                            //Log.v("checkkk", itemOrgAr[i].PersonnelTypeID + "=="+ itemAr[i].PersonnelTypeID)
                            Log.v("checkkk", itemOrgAr[i].startDate + "==" + itemAr[i].startDate)
                            Log.v("checkkk", itemOrgAr[i].endDate + "==" + itemAr[i].endDate)
                            Log.v(
                                "checkkk",
                                itemOrgAr[i].SeniorityDate + "==" + itemAr[i].SeniorityDate
                            )


                        }
            }
        } else {
            MarkChangeWasDone()
            Log.v("checkkk", "array not equal")


        }
    }

    fun onlyOneMailRecepientLogic() {

        FacilityDataModel.getInstance().tblPersonnel.apply {
            (0 until size).forEach {


                if (get(it).PrimaryMailRecipient.equals("true")) {

                    binding.newACSCheck.isEnabled = false
                }

            }
        }

    }

    fun altTableRow(alt_row: Int) {
        var childViewCount = binding.PersonnelResultsTbl.getChildCount();

        for (i in 1..childViewCount - 1) {
            var row: TableRow = binding.PersonnelResultsTbl.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(
                    getResources().getDrawable(
                        R.drawable.alt_row_color
                    )
                );
            } else {
                row.setBackground(
                    getResources().getDrawable(
                        R.drawable.row_color
                    )
                );
            }

        }
    }

    fun altGridTableRow(alt_row: Int) {
        var childViewCount = binding.certificateGridTblLayout.getChildCount();

        for (i in 1..childViewCount - 1) {
            var row: TableRow = binding.certificateGridTblLayout.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(
                    getResources().getDrawable(
                        R.drawable.alt_row_color
                    )
                );
            } else {
                row.setBackground(
                    getResources().getDrawable(
                        R.drawable.row_color
                    )
                );
            }

        }
    }

    fun altGridOEMTableRow(alt_row: Int) {
        var childViewCount = binding.certificateGridOEMTblLayout.getChildCount();

        for (i in 1..childViewCount - 1) {
            var row: TableRow = binding.certificateGridOEMTblLayout.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(
                    getResources().getDrawable(
                        R.drawable.alt_row_color
                    )
                );
            } else {
                row.setBackground(
                    getResources().getDrawable(
                        R.drawable.row_color
                    )
                );
            }

        }
    }

    fun altCertTableRow(alt_row: Int) {
        var childViewCount = binding.certificationsTable.getChildCount();

        for (i in 1..childViewCount - 1) {
            var row: TableRow = binding.certificationsTable.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(
                    getResources().getDrawable(
                        R.drawable.alt_row_color
                    )
                );
            } else {
                row.setBackground(
                    getResources().getDrawable(
                        R.drawable.row_color
                    )
                );
            }
        }
    }


    fun scopeOfServiceChangesWatcher() {
    }

    fun validateCertificationInputs(): Boolean {

        binding.certDateTextView.setError(null)
        binding.certTypeTextView.setError(null)
        validationMsg = ""

        var cert = TblPersonnel()

        cert.iscertInputValid = true

        if (binding.newCertStartDateBtn.text.toString().uppercase(getDefault()).equals("SELECT DATE")) {
            cert.iscertInputValid = false
            binding.certDateTextView.setError("Required Field")
            validationMsg = "Please fill all required fields"
        }
        if (!binding.newCertStartDateBtn.text.toString().uppercase(getDefault())
                .equals("SELECT DATE") && binding.newCertEndDateBtn.text.toString()
                .uppercase(getDefault())
                .equals("SELECT DATE")
        ) {
            cert.iscertInputValid = false
            binding.expirationDateText.setError("Required Field")
            validationMsg = "Please fill all required fields"
        }




        if (binding.newCertTypeSpinner.selectedItem.toString().contains("Not")) {
            cert.iscertInputValid = false
            binding.certTypeTextView.setError("required field")
            validationMsg = "Please fill all required fields"
        }
        var certificateType = ""
        for (fac in TypeTablesModel.getInstance().PersonnelCertificationType) {
            if (binding.newCertTypeSpinner.getSelectedItem().toString()
                    .equals(fac.PersonnelCertName)
            )
                certificateType = fac.PersonnelCertID
        }

        var datesOverlapping = false
        var certTypeExists = false
        FacilityDataModel.getInstance().tblPersonnelCertification.filter { s ->
            s.CertificationTypeId.equals(
                certificateType
            )
        }.apply {
            (0 until size).forEach {
                if (get(it).PersonnelID.equals(selectedPersonnelID)) {
                    if (Utility.datesAreOverlapping(
                            binding.newCertStartDateBtn.text.toString().toDateMMDDYYYY(),
                            binding.newCertEndDateBtn.text.toString().toDateMMDDYYYY(),
                            get(it).CertificationDate.toDateDBFormat(),
                            get(it).ExpirationDate.toDateDBFormat()
                        )
                    ) {
//                        datesOverlapping = true
                    } else {
                        certTypeExists = true
                    }
                }
            }
        }
        if (datesOverlapping) {
            Utility.showValidationAlertDialog(
                activity,
                "The certification overlaps with another active certification from the same type"
            )
            cert.iscertInputValid = !datesOverlapping
        }

        if (certTypeExists) {
            Utility.showValidationAlertDialog(
                activity,
                "This Personnel has a certification from the same type"
            )
            cert.iscertInputValid = !certTypeExists
        }

        if (!binding.newCertEndDateBtn.text.toString().uppercase(getDefault()).equals("SELECT DATE")) {
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val effDate = SimpleDateFormat(
                myFormat,
                Locale.US
            ).parse(binding.newCertStartDateBtn!!.text.toString())
            val expDate = SimpleDateFormat(
                myFormat,
                Locale.US
            ).parse(binding.newCertEndDateBtn!!.text.toString())
            if (expDate.before(effDate)) {
                cert.iscertInputValid = false
                binding.newCertEndDateBtn.setError("Should be after Effective Date")
                if (validationMsg.equals(""))
                    validationMsg = "Expiration Date should be after Start Date"
                else
                    validationMsg += "\nExpiration Date should be after Start Date"
            }
        }

        return cert.iscertInputValid
    }

    // Validates a single staged certificate entry against required-field rules
    // and date-overlap with already-saved records for the same person + type.
    fun validateCertificationInputs(entry: StagedCertificateEntry): Boolean {
        if (entry.certificationTypeId.isBlank()) return false
        if (entry.certificationDate.isBlank() || entry.certificationDate.uppercase() == "SELECT DATE") return false
        if (entry.expirationDate.isBlank() || entry.expirationDate.uppercase() == "SELECT DATE") return false

        val startDate = entry.certificationDate.toDateMMDDYYYY()
        val endDate = entry.expirationDate.toDateMMDDYYYY()

        for (saved in FacilityDataModel.getInstance().tblPersonnelCertification) {
            if (saved.PersonnelID == entry.personnelId &&
                saved.CertificationTypeId == entry.certificationTypeId
            ) {
                if (Utility.datesAreOverlapping(
                        startDate, endDate,
                        saved.CertificationDate.toDateDBFormat(),
                        saved.ExpirationDate.toDateDBFormat()
                    )
                ) return false
            }
        }
        return true
    }

    // Validates all staged entries collectively — required fields, saved-cert overlaps,
    // and cross-list overlaps between pending entries of the same type for the same person.
    fun validateAllStagedEntries(): Boolean {
        val errors = StringBuilder()
        for ((i, entry) in stagedCertificates.withIndex()) {
            val label = "Entry ${i + 1} (${entry.certificationTypeName.ifEmpty { "Unknown" }})"
            if (entry.certificationTypeId.isBlank()) {
                errors.appendLine("$label: Certificate type is required.")
                continue
            }
            if (entry.certificationDate.isBlank() || entry.certificationDate.uppercase() == "SELECT DATE") {
                errors.appendLine("$label: Start date is required.")
            }
            if (entry.expirationDate.isBlank() || entry.expirationDate.uppercase() == "SELECT DATE") {
                errors.appendLine("$label: Expiration date is required.")
            }
            // Overlap with saved records
            if (!validateCertificationInputs(entry)) {
                errors.appendLine("$label: Overlaps with an existing saved certificate of the same type.")
            }
        }
        // Cross-list overlap check between staged entries
        for (i in stagedCertificates.indices) {
            for (j in i + 1 until stagedCertificates.size) {
                val a = stagedCertificates[i]
                val b = stagedCertificates[j]
                if (a.personnelId == b.personnelId && a.certificationTypeId == b.certificationTypeId &&
                    a.certificationTypeId.isNotBlank()
                ) {
                    if (Utility.datesAreOverlapping(
                            a.certificationDate.toDateMMDDYYYY(), a.expirationDate.toDateMMDDYYYY(),
                            b.certificationDate.toDateMMDDYYYY(), b.expirationDate.toDateMMDDYYYY()
                        )
                    ) {
                        errors.appendLine(
                            "Entry ${i + 1} and Entry ${j + 1} have overlapping date ranges for the same certificate type."
                        )
                    }
                }
            }
        }
        if (errors.isNotEmpty()) {
            Utility.showValidationAlertDialog(activity, errors.toString().trimEnd())
            return false
        }
        return true
    }

    // Sequential recursive submission: submits one staged entry, updates its status,
    // then recurses to the next index. Skips already-SAVED entries (for retry path).
    fun submitNextStagedCert(index: Int) {
        if (index >= stagedCertificates.size) {
            // All done
            requireActivity().runOnUiThread {
                isSubmitting = false
                stagedAdapter.setSubmitting(false)
                binding.closeCertDialogBtn.isEnabled = true
                binding.personnelLoadingView.visibility = View.GONE
                binding.personnelLoadingText.text = "Loading ..."

                val savedCount = stagedCertificates.count { it.status == StagedCertStatus.SAVED }
                val total = stagedCertificates.size
                Toast.makeText(
                    requireContext(),
                    "$savedCount of $total certificate(s) saved.",
                    Toast.LENGTH_LONG
                ).show()

                val hasFailures = stagedCertificates.any { it.status == StagedCertStatus.FAILED }
                binding.retryFailedBtn.visibility = if (hasFailures) View.VISIBLE else View.GONE
            }
            return
        }

        val entry = stagedCertificates[index]
        if (entry.status == StagedCertStatus.SAVED) {
            submitNextStagedCert(index + 1)
            return
        }

        requireActivity().runOnUiThread {
            entry.status = StagedCertStatus.SAVING
            stagedAdapter.notifyItemChanged(index)
        }

        val certificationDate = entry.certificationDate.appToApiSubmitFormatMMDDYYYY()
        val expirationDate = entry.expirationDate.appToApiSubmitFormatMMDDYYYY()
        val urlString =
            "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}" +
                    "&clubCode=${FacilityDataModel.getInstance().clubCode}" +
                    "&personnelId=${entry.personnelId}" +
                    "&certId=&certificationTypeId=${entry.certificationTypeId}" +
                    "&certificationDate=${certificationDate}&expirationDate=${expirationDate}" +
                    "&certDesc=${entry.certDesc}" +
                    "&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}" +
                    "&insertDate=${Date().toApiSubmitFormat()}" +
                    "&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}" +
                    "&updateDate=${Date().toApiSubmitFormat()}&active=1"

        Volley.newRequestQueue(context).add(
            StringRequest(
                Request.Method.GET,
                Constants.UpdatePersonnelCertification + urlString + Utility.getLoggingParameters(
                    activity, 0, getCertificationChanges(0, entry.personnelId)
                ),
                Response.Listener { response ->
                    requireActivity().runOnUiThread {
                        if (response.toString().contains("returnCode>0<", false)) {
                            entry.status = StagedCertStatus.SAVED
                            val item = TblPersonnelCertification()
                            item.PersonnelID = entry.personnelId
                            item.CertificationTypeId = entry.certificationTypeId
                            item.CertificationDate = certificationDate
                            item.ExpirationDate = expirationDate
                            item.CertDesc = entry.certDesc
                            item.CertID = try {
                                response.toString().substring(
                                    response.toString().indexOf("<CertID") + 8,
                                    response.toString().indexOf("</CertID")
                                )
                            } catch (e: Exception) { "" }
                            FacilityDataModel.getInstance().tblPersonnelCertification.add(item)
                            FacilityDataModelOrg.getInstance().tblPersonnelCertification.add(item)
                            HasChangedModel.getInstance().updateChangedData(
                                "Personnel", "Certifications", "",
                                getCertificationChanges(0, entry.personnelId)
                            )
                            HasChangedModel.getInstance().groupFacilityPersonnel[0].FacilityPersonnel = true
                            HasChangedModel.getInstance().changeDoneForFacilityPersonnel()
                            fillCertificationTableView(entry.personnelId)
                            (activity as FormsActivity).saveDone = true
                            setAlertColoring()
                        } else {
                            entry.status = StagedCertStatus.FAILED
                            entry.errorMessage = try {
                                response.toString().substring(
                                    response.toString().indexOf("<message") + 9,
                                    response.toString().indexOf("</message")
                                )
                            } catch (e: Exception) { "Server error" }
                        }
                        stagedAdapter.notifyItemChanged(index)
                    }
                    submitNextStagedCert(index + 1)
                },
                Response.ErrorListener { error ->
                    requireActivity().runOnUiThread {
                        entry.status = StagedCertStatus.FAILED
                        entry.errorMessage = error.message ?: "Network error"
                        stagedAdapter.notifyItemChanged(index)
                    }
                    submitNextStagedCert(index + 1)
                }
            ).apply {
                retryPolicy = DefaultRetryPolicy(30_000, 0, 1.0f)
            }
        )
    }

    private fun closeCertificateDialog() {
        stagedCertificates.clear()
        stagedAdapter.notifyDataSetChanged()
        isSubmitting = false
        stagedAdapter.setSubmitting(false)
        binding.submitAllBtn.isEnabled = false
        binding.closeCertDialogBtn.isEnabled = false
        binding.addToListBtn.isEnabled = true
        binding.retryFailedBtn.visibility = View.GONE
        binding.personnelLoadingView.visibility = View.GONE
        // Reset form fields
        binding.newCertTypeSpinner.setSelection(0)
        binding.newCertStartDateBtn.setText("SELECT DATE")
        binding.newCertEndDateBtn.setText("SELECT DATE")
        binding.newCertDescText.setText("")
        binding.newCertStartDateBtn.setError(null)
        binding.certTypeTextView.setError(null)
        binding.expirationDateText.setError(null)
        (activity as FormsActivity).overrideBackButton = false
        binding.addNewCertificateDialogue.visibility = View.GONE
        binding.alphaBackgroundForPersonnelDialogs.visibility = View.GONE
    }

    fun edit_validateCertificationInputs(): Boolean {

        binding.editCertDateTextView.setError(null)
        binding.editExpirationDateText.setError(null)
//        certTypeTextView.setError(null)
        validationMsg = ""
        var cert = TblPersonnel()

        cert.iscertInputValid = true

        if (binding.editNewCertStartDateBtn.text.toString().uppercase(getDefault()).equals("SELECT DATE")) {
            cert.iscertInputValid = false
            binding.editNewCertStartDateBtn.setError("Required Field")
            validationMsg = "Please fill all required fields"
        }
        if (!binding.editNewCertStartDateBtn.text.toString().uppercase(getDefault())
                .equals("SELECT DATE") && binding.editNewCertEndDateBtn.text.toString()
                .uppercase(getDefault()).equals("SELECT DATE")
        ) {
            cert.iscertInputValid = false
            binding.editExpirationDateText.setError("Required Field")
            validationMsg = "Please fill all required fields"
        }


        var certificateType = ""
//        for (fac in TypeTablesModel.getInstance().PersonnelCertificationType) {
//            if (edit_newCertTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelCertName))
//                certificateType = fac.PersonnelCertID
//        }
        certificateType = binding.editNewCertTypeSpinner.text.toString()

        var datesOverlapping = false
        if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s ->
                s.CertificationTypeId.equals(
                    certificateType
                )
            }.size == 1) {
            datesOverlapping = true
        } else {
            FacilityDataModel.getInstance().tblPersonnelCertification.filter { s ->
                s.CertificationTypeId.equals(
                    certificateType
                )
            }.apply {
                (0 until size).forEach {
                    if (get(it).PersonnelID.equals(selectedPersonnelID)) {
                        if (Utility.datesAreOverlapping(
                                binding.editNewCertStartDateBtn.text.toString().toDateMMDDYYYY(),
                                binding.editNewCertEndDateBtn.text.toString().toDateMMDDYYYY(),
                                get(it).CertificationDate.toDateDBFormat(),
                                get(it).ExpirationDate.toDateDBFormat()
                            )
                        ) {
                            datesOverlapping = true
                        }
                    }
                }
            }
        }

        if (datesOverlapping) {
            Utility.showValidationAlertDialog(
                activity,
                "The certification overlaps with another active certification from the same type"
            )
            cert.iscertInputValid = !datesOverlapping
        }

        if (!binding.editNewCertEndDateBtn.text.toString().uppercase(getDefault()).equals("SELECT DATE")) {
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val effDate = SimpleDateFormat(
                myFormat,
                Locale.US
            ).parse(binding.editNewCertStartDateBtn!!.text.toString())
            val expDate = SimpleDateFormat(
                myFormat,
                Locale.US
            ).parse(binding.editNewCertEndDateBtn!!.text.toString())
            if (expDate.before(effDate)) {
                cert.iscertInputValid = false
                binding.editNewCertEndDateBtn.setError("Should be after Effective Date")
                if (validationMsg.equals(""))
                    validationMsg = "Expiration Date should be after Start Date"
                else
                    validationMsg += "\nExpiration Date should be after Start Date"
            }
        }

        return cert.iscertInputValid
    }

    fun validateInputs(): Boolean {

        var persn = TblPersonnel()

        validationMsg = ""
        persn.personnelIsInputsValid = true
        binding.rspEmailTextId.setError(null)
        binding.newCertNoText.setError(null)
        binding.newLastNameText.setError(null)
        binding.newFirstNameText.setError(null)
        binding.personnelTypeTextViewId.setError(null)
        binding.newStartDateBtn.setError(null)
        if (binding.newFirstNameText.text.toString().isNullOrEmpty()) {
            persn.personnelIsInputsValid = false
            binding.newFirstNameText.setError("required field")
            validationMsg = "Please fill all required fields"
        } else
            binding.newFirstNameText.setError(null)

        if (!binding.newCertNoText.text.toString().isNullOrEmpty()) {
            if (FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.CertificationNum == binding.newCertNoText.text.toString() }
                    .isNotEmpty()) {
                persn.personnelIsInputsValid = false
                binding.newCertNoText.setError("Duplicated Cert ID")
                validationMsg = "Please fill all required fields"
            }
        } else
            binding.newCertNoText.setError(null)

//        if (newCertNoText.text.toString().isNullOrEmpty()){
//            persn.personnelIsInputsValid=false
//            newCertNoText.setError("required field")
//        }
//        else
//            newCertNoText.setError(null)

        if (binding.newLastNameText.text.toString().isNullOrEmpty()) {

            persn.personnelIsInputsValid = false
            binding.newLastNameText.setError("required field")
            validationMsg = "Please fill all required fields"

        } else
            binding.newLastNameText.setError(null)


        if (binding.newPersonnelTypeSpinner.selectedItem.toString().contains("Selected")) {
            persn.personnelIsInputsValid = false
            binding.personnelTypeTextViewId.setError("required field")
            validationMsg = "Please fill all required fields"
        } else
            binding.personnelTypeTextViewId.setError(null)

        if (binding.newStartDateBtn.text.toString().contains("SELECT")) {
            persn.personnelIsInputsValid = false
            binding.newStartDateBtn.setError("required field")
            validationMsg = "Please fill all required fields"
        } else
            binding.newStartDateBtn.setError(null)

        if (binding.newSignerCheck.isChecked) {

            if (binding.newAdd1Text.text.toString().isNullOrEmpty()) {
                persn.personnelIsInputsValid = false
                binding.newAdd1Text.setError("required field")
                validationMsg = "Please fill all required fields"
            } else
                binding.newAdd1Text.setError(null)

            if (binding.newCityText.text.toString().isNullOrEmpty()) {

                persn.personnelIsInputsValid = false
                binding.newCityText.setError("required field")
                validationMsg = "Please fill all required fields"

            } else
                binding.newCityText.setError(null)

            if (binding.newStateSpinner.selectedItem.toString().contains("Select")) {

                persn.personnelIsInputsValid = false
                binding.stateTextView.setError("required field")
                validationMsg = "Please fill all required fields"

            } else
                binding.stateTextView.setError(null)


            if (binding.newZipText.text.toString().isNullOrEmpty() || zipFormat == false) {

                persn.personnelIsInputsValid = false
                binding.newZipText.setError("required field")
                validationMsg = "Please fill all required fields"

            } else
                binding.newZipText.setError(null)


//                if (newPhoneText.text.toString().isNullOrEmpty()){
//
//                    persn.personnelIsInputsValid=false
//                    newPhoneText.setError("required field")
//                    validationMsg = "Please fill all required fields"
//
//                }
//                else
//                    newPhoneText.setError(null)


            if (binding.newCoStartDateBtn.text.toString().contains("SELECT")) {

                persn.personnelIsInputsValid = false
                binding.newCoStartDateBtn.setError("required field")
                validationMsg = "Please fill all required fields"

            } else
                binding.newCoStartDateBtn.setError(null)


//                if (newEmailText.text.toString().isNullOrEmpty()||!emailFormatValidation(newEmailText.text.toString())){
//
//                    persn.personnelIsInputsValid=false
//                    newEmailText.setError("required field")
//                    validationMsg = "Please fill all required fields"
//
//                }  else
//                    newEmailText.setError(null)


        } else {
            binding.newEmailText.setError(null)
            binding.newCoStartDateBtn.setError(null)
            binding.newPhoneText.setError(null)
            binding.newZipText.setError(null)
            binding.stateTextView.setError(null)
            binding.newCityText.setError(null)
            binding.newAdd1Text.setError(null)
        }

        if (!binding.newEndDateBtn.text.toString().uppercase(getDefault()).equals("SELECT DATE")) {
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val effDate = SimpleDateFormat(
                myFormat,
                Locale.US
            ).parse(binding.newStartDateBtn!!.text.toString())
            val expDate =
                SimpleDateFormat(myFormat, Locale.US).parse(binding.newEndDateBtn!!.text.toString())
            if (expDate.before(effDate)) {
                persn.personnelIsInputsValid = false
                if (validationMsg.equals(""))
                    validationMsg = "End Date should be after Start Date"
                else
                    validationMsg += "\nEnd Date should be after Start Date"
                binding.newEndDateBtn.setError("Should be after Start Date")
//                \n" +
//                "End Date should be after Start Date"
            }
        }

        if (binding.rspUserId.text.toString().isNotEmpty() && (binding.rspEmailId.text.toString()
                .isNullOrEmpty() || !emailFormatValidation(binding.rspEmailId.text.toString()))
        ) {
            persn.personnelIsInputsValid = false
//            binding.rspEmailTextId.setError("required field")
            binding.rspEmailId.setError("required field")
            validationMsg = "Please fill all required fields"
        } else {
            binding.rspEmailId.setError(null)
        }

        if (binding.rspEmailId.text.toString()
                .isNotEmpty() && !emailFormatValidation(binding.rspEmailId.text.toString())
        ) {
            persn.personnelIsInputsValid = false
            binding.rspEmailId.setError("required field")
            validationMsg = "Email is not valid"
        } else {
            binding.rspEmailId.setError(null)
        }

        return persn.personnelIsInputsValid
    }

    fun edit_validateInputs(): Boolean {

        var persn = TblPersonnel()
        validationMsg = ""

        persn.personnelIsInputsValid = true

        if (binding.editNewFirstNameText.text.toString().isNullOrEmpty()) {

            persn.personnelIsInputsValid = false
            binding.editNewFirstNameText.setError("required field")
            validationMsg = "Please fill all required fields"

        } else
            binding.editNewFirstNameText.setError(null)


        if (binding.editNewLastNameText.text.toString().isNullOrEmpty()) {

            persn.personnelIsInputsValid = false
            binding.editNewLastNameText.setError("required field")
            validationMsg = "Please fill all required fields"

        } else
            binding.editNewLastNameText.setError(null)


        if (binding.editNewPersonnelTypeSpinner.selectedItem.toString().contains("Selected")) {
            persn.personnelIsInputsValid = false
            binding.editPersonnelTypeTextViewId.setError("required field")
            validationMsg = "Please fill all required fields"
        } else
            binding.editPersonnelTypeTextViewId.setError(null)

        if (binding.editNewStartDateBtn.text.toString().contains("SELECT")) {
            persn.personnelIsInputsValid = false
            binding.editNewStartDateBtn.setError("required field")
            validationMsg = "Please fill all required fields"
        } else
            binding.editNewStartDateBtn.setError(null)



        if (binding.editNewSignerCheck.isChecked) {

            if (binding.editNewAdd1Text.text.toString().isNullOrEmpty()) {

                persn.personnelIsInputsValid = false
                binding.editNewAdd1Text.setError("required field")
                validationMsg = "Please fill all required fields"
            } else
                binding.editNewAdd1Text.setError(null)

            if (binding.editNewCityText.text.toString().isNullOrEmpty()) {

                persn.personnelIsInputsValid = false
                binding.editNewCityText.setError("required field")
                validationMsg = "Please fill all required fields"

            } else
                binding.editNewCityText.setError(null)

            if (binding.editNewStateSpinner.selectedItem.toString().contains("Select")) {
                persn.personnelIsInputsValid = false
                binding.editStateTextView.setError("required field")
                validationMsg = "Please fill all required fields"
            } else
                binding.editStateTextView.setError(null)


            if (binding.editNewZipText.text.toString().isNullOrEmpty() || zipFormat == false) {

                persn.personnelIsInputsValid = false
                binding.editNewZipText.setError("required field")
                validationMsg = "Please fill all required fields"

            } else
                binding.editNewZipText.setError(null)


//                if (edit_newPhoneText.text.toString().isNullOrEmpty()){
//
//                    persn.personnelIsInputsValid=false
//                    edit_newPhoneText.setError("required field")
//                    validationMsg = "Please fill all required fields"
//
//                }
//                else
//                    edit_newPhoneText.setError(null)


            if (binding.editNewCoStartDateBtn.text.toString().contains("SELECT")) {

                persn.personnelIsInputsValid = false
                binding.editNewCoStartDateBtn.setError("required field")

                validationMsg = "Please fill all required fields"
            } else
                binding.editNewCoStartDateBtn.setError(null)


//                if (edit_newEmailText.text.toString().isNullOrEmpty()||!emailFormatValidation(edit_newEmailText.text.toString())){
//
//                    persn.personnelIsInputsValid=false
//                    edit_newEmailText.setError("required field")
//                    validationMsg = "Please fill all required fields"
//
//                }  else
//                    edit_newEmailText.setError(null)


        } else {
            binding.editNewEmailText.setError(null)
            binding.editNewCoStartDateBtn.setError(null)
            binding.editNewPhoneText.setError(null)
            binding.editNewZipText.setError(null)
            binding.editStateTextView.setError(null)
            binding.editNewCityText.setError(null)
            binding.editNewAdd1Text.setError(null)
        }

        if (!binding.editNewEndDateBtn.text.toString().uppercase(getDefault()).equals("SELECT DATE")) {
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val effDate = SimpleDateFormat(
                myFormat,
                Locale.US
            ).parse(binding.editNewStartDateBtn!!.text.toString())
            val expDate = SimpleDateFormat(
                myFormat,
                Locale.US
            ).parse(binding.editNewEndDateBtn!!.text.toString())
            if (expDate.before(effDate)) {
                persn.personnelIsInputsValid = false
                binding.editNewEndDateBtn.setError("Should be after Start Date")
                if (validationMsg.equals(""))
                    validationMsg = "End Date should be after Start Date"
                else
                    validationMsg += "\nEnd Date should be after Start Date"
            }
        }

        if (binding.editRspUserId.text.toString()
                .isNotEmpty() && (binding.editRspEmailId.text.toString()
                .isNullOrEmpty() || !emailFormatValidation(binding.editRspEmailId.text.toString()))
        ) {
            persn.personnelIsInputsValid = false
            binding.editRspEmailId.setError("required field")
            validationMsg = "Please fill all required fields"
        } else {
            binding.editRspEmailId.setError(null)
        }

        if (binding.editRspEmailId.text.toString()
                .isNotEmpty() && !emailFormatValidation(binding.editRspEmailId.text.toString())
        ) {
            persn.personnelIsInputsValid = false
            binding.editRspEmailId.setError("required field")
            validationMsg = "Email is not valid"
        } else {
            binding.editRspEmailId.setError(null)
        }


        return persn.personnelIsInputsValid
    }

    fun updateDialogs() {
        if (binding.addNewPersonnelDialogue != null) binding.addNewPersonnelDialogue.visibility =
            View.GONE
        if (binding.addNewCertificateDialogue != null) binding.addNewCertificateDialogue.visibility =
            View.GONE
        if (binding.alphaBackgroundForPersonnelDialogs != null) binding.alphaBackgroundForPersonnelDialogs.visibility =
            View.GONE
        if (binding.addNewPersonnelDialogue != null) binding.addNewPersonnelDialogue.visibility =
            View.GONE
        if (binding.editAddNewPersonnelDialogue != null) binding.editAddNewPersonnelDialogue.visibility =
            View.GONE
        if (binding.personnelLoadingView != null) binding.personnelLoadingView.visibility =
            View.GONE
    }


    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val isValidating = "param1"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1..
         * @return A new instance of fragment FragmentARRAVPersonnel.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(isValidating: Boolean): FragmentARRAVPersonnel {
            val fragment = FragmentARRAVPersonnel()
            val args = Bundle()
            args.putBoolean(this.isValidating, isValidating)
            fragment.arguments = args
            return fragment
        }
    }

}
// Required empty public constructor
