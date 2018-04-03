package com.inspection.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.inspection.MainActivity

import com.inspection.R
import com.inspection.Utils.Consts
import com.inspection.Utils.SearchDialog
import com.inspection.Utils.toTime
import com.inspection.model.*
import com.inspection.singletons.AnnualVisitationSingleton
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.frgment_arrav_visitation_records.*
import java.time.Year
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FrgmentARRAnnualVisitationRecords.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FrgmentARRAnnualVisitationRecords.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAnnualVisitationRecords : android.support.v4.app.Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    var fragment: android.support.v4.app.Fragment? = null
    private var mListener: OnFragmentInteractionListener? = null
    var facilityNames = ArrayList<String>()
    var facilitiesList = ArrayList<AAAFacilityComplete>()
    var visitationList = ArrayList<AnnualVisitationInspectionFormData>()
    var itemSelected = false
    var facilityNameInputField: EditText? = null
    var firstLoading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frgment_arrav_visitation_records, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        facilityNameInputField = visitationfacilityNameVal
//        visitationfacilityIdVal.visibility = View.GONE
//        visitationfacilityIdTextView.visibility = View.GONE
        visitationfacilityListView.visibility = View.GONE


        // Inspection Type
        var inspectionTypes = arrayOf("Any", "Deficient", "Quarterly", "Annual", "Annual / Deficient", "Quarter / Deficient")

        var visitationYearFilterSpinnerEntries = mutableListOf<String>()
        var currentYear = Calendar.getInstance().get(Calendar.YEAR)

        (currentYear - 30..currentYear).forEach {
            visitationYearFilterSpinnerEntries.add("" + it)
        }

        visitationYearFilterSpinner.adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, visitationYearFilterSpinnerEntries)


        var dataAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, inspectionTypes)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        visitationinpectionTypeSpinner.adapter = dataAdapter
//        visitationinpectionTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                if (!firstLoading) {
//                    showVisitationBtn.performClick()
//                }
//            }
//        }


        // Inspection Kind
        var inspectionKinds = arrayOf("All", "Planned Visitation", "Regular Visitation")
        var dataAdapterkind = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, inspectionKinds)
        dataAdapterkind.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        visitationinpectionKindSpinner.adapter = dataAdapterkind
//        visitationinpectionKindSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                if (!firstLoading) {
//                    showVisitationBtn.performClick()
//                }
//            }
//        }


        newVisitationBtn.setOnClickListener({
            //            (activity as MainActivity).VisitationID = "0"
//            fragment = FragmentAnnualVisitationPager()
//            val fragmentManagerSC = fragmentManager
//            val ftSC = fragmentManagerSC!!.beginTransaction()
//            ftSC.replace(R.id.fragment, fragment)
//            ftSC.addToBackStack("")
//            ftSC.commit()
            var intent = Intent(context, com.inspection.fragments.ItemListActivity::class.java)
            startActivity(intent)
        })


        visitationSpecialistName.setOnClickListener(View.OnClickListener {
            var loadingDialog = SpotsDialog(context)
            loadingDialog.show()
            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.getAllPersonnelDetails + "",
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {
                            loadingDialog.dismiss()
                            var personnels = Gson().fromJson(response.toString(), Array<AAAPersonnelDetails>::class.java).toCollection(ArrayList())
                            var personnelNames = ArrayList<String>()
                            (0 until personnels.size).forEach {
                                personnelNames.add(personnels[it].firstname)
                            }
                            personnelNames.sort()
                            var searchDialog = SearchDialog(context, personnelNames)
                            searchDialog.show()
                            searchDialog.setOnDismissListener {
                                visitationSpecialistName.text = searchDialog.selectedString
                            }
                        })
                    }, Response.ErrorListener {
                loadingDialog.dismiss()
                Log.v("error while loading", "error while loading facilities")
                Log.v("Loading error", "" + it.message)
            }))
        })

        visitationfacilityNameVal.setOnClickListener(View.OnClickListener {
            var loadingDialog = SpotsDialog(context)
            loadingDialog.show()
            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.getfacilitiesURL + "",
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {
                            loadingDialog.dismiss()
                            var facilities = Gson().fromJson(response.toString(), Array<AAAFacilityComplete>::class.java).toCollection(ArrayList())
                            var facilityNames = ArrayList<String>()
                            (0 until facilities.size).forEach {
                                facilityNames.add(facilities[it].businessname)
                            }
                            facilityNames.sort()
                            var searchDialog = SearchDialog(context, facilityNames)
                            searchDialog.show()
                            searchDialog.setOnDismissListener {
                                visitationfacilityNameVal.text = searchDialog.selectedString
                            }
                        })
                    }, Response.ErrorListener {
                loadingDialog.dismiss()
                Log.v("error while loading", "error while loading facilities")
                Log.v("Loading error", "" + it.message)
            }))
        })

        firstLoading = false
        showVisitationBtn.performClick()

    }

    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            mListener = context
//        } else {
//            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
//        mListener = null
    }

    inner class VisitationListAdapter : BaseAdapter {

        private var visitationList = ArrayList<AnnualVisitationInspectionFormData>()
        private var context: Context? = null

        constructor(context: Context?, visitationList: ArrayList<AnnualVisitationInspectionFormData>) : super() {
            this.visitationList = visitationList
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            val view: View?
            val vh: ViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.custom_visitation_list_item, parent, false)
                vh = ViewHolder(view)
                view.tag = vh
            } else {
                view = convertView
                vh = view.tag as ViewHolder
            }

            vh.vrID.text = visitationList[position].annualvisitationid.toString()
            vh.vrBy.text = visitationList[position].facilityrepresentativename
            vh.vrDate.text = visitationList[position].dateofinspection
            vh.vrPlanned.text = visitationList[position].dateofinspection
            vh.vrPlanned.visibility = if (visitationList[position].dateofinspection.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
            vh.vrStatus.text = if (visitationList[position].dateofinspection.toTime() > Date().time) "Planned" else "regular"
            vh.vrType.text = visitationList[position].entityName
//            if (position%2!=0) vh.vrLL.setBackgroundResource(R.drawable.visitation_listitem_bkg_rtol)
//            else vh.vrLL.setBackgroundResource(R.drawable.visitation_listitem_bkg)
            vh.vrLoadBtn.setOnClickListener({
                AnnualVisitationSingleton.getInstance().clear()
                AnnualVisitationSingleton.getInstance().apply {
                    annualVisitationId = visitationList[position].annualvisitationid
                    facilityRepresentative = visitationList[position].facilityrepresentativename
                    automotiveSpecialist = visitationList[position].automotivespecialistname
                    dateOfVisitation = visitationList[position].dateofinspection.toTime()
                    inspectionType = visitationList[position].inspectiontypeid
                    monthDue = visitationList[position].monthdue
                    changesMade = visitationList[position].changesmade
                    paymentMethods = visitationList[position].paymentmethods

                    emailModel = AAAEmailModel()
                    emailModel!!.emailid = visitationList[position].emailaddressid

                    phoneModel = AAAPhoneModel()
                    phoneModel!!.phoneid = visitationList[position].phonenumberid

                    personnelId = visitationList[position].personnelid
                    vehicleServices = visitationList[position].vehicleservices
                    vehicles = visitationList[position].vehicles
                    affliations = visitationList[position].affiliations
                    defeciencies = visitationList[position].defeciencies
                    complaints = visitationList[position].complaints
                }

                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.getFacilityWithIdUrl + visitationList[position].facilityid,
                        Response.Listener { response ->
                            activity!!.runOnUiThread(Runnable {
                                var facilityComplete = Gson().fromJson(response.toString(), Array<AAAFacilityComplete>::class.java).toCollection(ArrayList()).get(0) as AAAFacilityComplete
                                AnnualVisitationSingleton.getInstance().apply {
                                    facilityId = facilityComplete.facid
                                    facilityName = facilityComplete.businessname
                                    facilityType = facilityComplete.facilitytypeid
                                    billingMonth = facilityComplete.billingmonth
                                    billingAmount = facilityComplete.billingamount
                                    contractType = facilityComplete.contracttypeid
                                    webSiteUrl = facilityComplete.website
                                    facilityType = facilityComplete.facilitytypeid
                                    currentContractDate = facilityComplete.contractcurrentdate
                                    setInsuranceExpirationDate(facilityComplete.insuranceexpdate)
                                    setInitialContractDate(facilityComplete.contractinitialdate)
                                    assignedTo = facilityComplete.assignedtoid
                                    office = facilityComplete.officeid
                                    entityName = facilityComplete.entityname
                                    timeZone = facilityComplete.timezoneid
                                    taxId = facilityComplete.taxidnumber
                                    repairOrderCount = facilityComplete.facilityrepairordercount
                                    serviceAvailability = facilityComplete.svcavailability
                                    ardNumber = facilityComplete.automotiverepairnumber
                                    setArdExpirationDate(facilityComplete.automotiverepairexpdate)
                                }

//                                val fragment: android.support.v4.app.Fragment
//                                fragment = FragmentAnnualVisitationPager()
//                                val fragmentManagerSC = fragmentManager
//                                val ftSC = fragmentManagerSC!!.beginTransaction()
//                                ftSC.replace(R.id.fragment, fragment)
//                                ftSC.addToBackStack("")
//                                ftSC.commit()
                                var intent = Intent(context, com.inspection.fragments.ItemListActivity::class.java)
                                startActivity(intent)
                            })
                        }, Response.ErrorListener {
                    Log.v("error while loading", "error while loading facilities")
                    Log.v("Loading error", "" + it.message)
                }))
            })
            return view
        }

        override fun getItem(position: Int): Any {
            // return item at 'position'
            return visitationList[position]
        }

        override fun getItemId(position: Int): Long {
            // return item Id by Long datatype
            return position.toLong()
        }

        override fun getCount(): Int {
            // return quantity of the list
            return visitationList.size
        }
    }

    private class ViewHolder(view: View?) {
        val vrID: TextView
        val vrBy: TextView
        val vrType: TextView
        val vrStatus: TextView
        val vrPlanned: TextView
        val vrDate: TextView
        val vrLoadBtn: TextView
        val vrLL: RelativeLayout

        init {
            this.vrID = view?.findViewById(R.id.visitationItemId) as TextView
            this.vrBy = view?.findViewById(R.id.visitationItemPerformedBy) as TextView
            this.vrStatus = view?.findViewById(R.id.visitationItemStatus) as TextView
            this.vrType = view?.findViewById(R.id.visitationItemType) as TextView
            this.vrDate = view?.findViewById(R.id.visitationItemPerformedDate) as TextView
            this.vrPlanned = view?.findViewById(R.id.visitationItemPlanned) as TextView
            this.vrLL = view?.findViewById(R.id.list_item_ll) as RelativeLayout
            this.vrLoadBtn = view?.findViewById(R.id.loadBtn) as TextView
        }

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
         * @return A new instance of fragment FrgmentARRAnnualVisitationRecords.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FragmentARRAnnualVisitationRecords {
            val fragment = FragmentARRAnnualVisitationRecords()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}


