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
import com.inspection.model.AAAFacilityComplete
import com.inspection.model.AAAVisitationRecords
import com.inspection.model.AnnualVisitationInspectionFormData
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.frgment_arrav_visitation_records.*
import java.util.ArrayList

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
        facilityNameInputField = visitationfacilityNameVal
        visitationfacilityIdVal.visibility = View.GONE
        visitationfacilityIdTextView.visibility = View.GONE
        visitationfacilityListView.visibility = View.GONE

        visitationfacilityNameVal.onFocusChangeListener = View.OnFocusChangeListener({ view: View, b: Boolean ->
            Log.v("********** focus is", "Focus is: " + b)
            itemSelected = !b
        })

        // Inspection Type
        var inspectionTypes = arrayOf("Any", "Deficient", "Quarterly", "Annual", "Annual / Deficient", "Quarter / Deficient")
        var dataAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, inspectionTypes)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visitationinpectionTypeSpinner.adapter = dataAdapter
        visitationinpectionTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (!firstLoading) {
                    showVisitationBtn.performClick()
                }
            }
        }


        // Inspection Kind
        var inspectionKinds = arrayOf("All", "Planned Visitation", "Regular Visitation")
        var dataAdapterkind = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, inspectionKinds)
        dataAdapterkind.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visitationinpectionKindSpinner.adapter = dataAdapterkind
        visitationinpectionKindSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(!firstLoading) {
                    showVisitationBtn.performClick()
                }
            }
        }

        visitationfacilityNameVal.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showVisitationBtn.performClick()
//                if (!itemSelected && visitationfacilityNameVal.text.length >= 3){
//                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.getfacilitiesURL+visitationfacilityNameVal.text,
//                            Response.Listener { response ->
//                                activity!!.runOnUiThread(Runnable {
//                                    facilitiesList = Gson().fromJson(response.toString() , Array<AAAFacilityComplete>::class.java).toCollection(ArrayList())
//                                    facilityNames.clear()
//                                    for (fac in facilitiesList){
//                                        facilityNames.add(fac.businessname)
//                                    }
//
//                                    visitationfacilityListView.visibility = View.VISIBLE
//                                    visitationfacilityListView.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, facilityNames)
//                                })
//                            }, Response.ErrorListener {
//                        Log.v("error while loading", "error while loading facilities")
//                    }))
//                }
            }

        })

        visitationfacilityListView.onItemClickListener = AdapterView.OnItemClickListener({ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(visitationfacilityNameVal.getWindowToken(), 0)
            itemSelected = true
            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.getFacilityWithIdUrl + visitationfacilityNameVal.text,
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {
                            facilitiesList = Gson().fromJson(response.toString(), Array<AAAFacilityComplete>::class.java).toCollection(ArrayList())
                            facilityNames.clear()
                            for (fac in facilitiesList) {
                                facilityNames.add(fac.businessname)
                            }

                            visitationfacilityListView.visibility = View.VISIBLE
                            visitationfacilityListView.adapter = ArrayAdapter<String>(context, R.layout.custom_visitation_list_item, facilityNames)
                        })
                    }, Response.ErrorListener {
                Log.v("error while loading", "error while loading facilities")
            }))
            visitationfacilityNameVal.setText(facilityNames.get(i).toString())
            // Facility Name will be needed in other forms
            (activity as MainActivity).FacilityName = facilityNames.get(i).toString()
            (activity as MainActivity).facilitySelected = facilitiesList.filter { s -> s.businessname == facilityNames.get(i) }.get(0)
            // Facility Number will be needed in other forms
            (activity as MainActivity).FacilityNumber = (activity as MainActivity).facilitySelected.facid.toString()
            visitationfacilityIdVal.setText((activity as MainActivity).facilitySelected.facid.toString())
            visitationfacilityListView.visibility = View.GONE
            visitationfacilityListView.adapter = null
            visitationfacilityNameVal.isEnabled = false
//            facilityNameEditText?.setError(null)
//            facilityRepresentativeNameEditText?.setError(null)
//            automotiveSpecialistEditText?.setError(null)
//            loadLastInspection()
        })

        clearBtn.setOnClickListener({
            visitationfacilityNameVal.setText("")
            visitationfacilityIdVal.setText("")
            itemSelected = false
            showVisitationBtn.setText("SHOW VISITATIONS")
            visitationfacilityNameVal.isEnabled = true
// Old List View
//            visitationrecordsListView.adapter=null
        })

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

        showVisitationBtn.setOnClickListener({
            var urlStr = String.format(Consts.getAnnualVisitations, visitationfacilityNameVal.text, visitationinpectionTypeSpinner.selectedItemId)
            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, urlStr,
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {
                            if (visitationrecordsLL != null) {
                                visitationrecordsLL.removeAllViews()
                            }
                            visitationList = Gson().fromJson(response.toString(), Array<AnnualVisitationInspectionFormData>::class.java).toCollection(ArrayList())
                            var visitationRecords = ArrayList<String>()
                            for (fac in visitationList) {
                                if (visitationinpectionKindSpinner.selectedItemPosition == 0) {
                                    visitationRecords.add(fac.annualvisitationid.toString() + " - " + fac.facilityrepresentativename)
                                } else if (visitationinpectionKindSpinner.selectedItemPosition == 1) {
                                    if (fac.inspectionstatus.equals("Planned Visitation"))
                                        visitationRecords.add(fac.annualvisitationid.toString() + " - " + fac.facilityrepresentativename)
                                } else {
                                    if (!fac.inspectionstatus.equals("Planned Visitation"))
                                        visitationRecords.add(fac.annualvisitationid.toString() + " - " + fac.facilityrepresentativename)
                                }
                            }

//                            val inflater = (activity as MainActivity)
//                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                            val vVisitationRecordsLL = visitationrecordsLL
//                            BuildVisitationRecords(vVisitationRecordsLL, inflater)

                            visitationfacilityListView.visibility = View.VISIBLE
                            var visitationRecordsAdapter = VisitationListAdapter(context, visitationList)
                            visitationfacilityListView.adapter = visitationRecordsAdapter
                        })
                    }, Response.ErrorListener {
                Log.v("error while loading", "error while loading visitation records")
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

        private var visitationList = ArrayList<AAAVisitationRecords>()
        private var context: Context? = null

        constructor(context: Context?, visitationList: ArrayList<AAAVisitationRecords>) : super() {
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

            vh.vrID.text = visitationList[position].visitationid.toString()
            vh.vrBy.text = visitationList[position].performedby
            vh.vrDate.text = visitationList[position].dateperformed
            vh.vrPlanned.text = visitationList[position].dateplanned
            vh.vrPlanned.visibility = if (visitationList[position].dateplanned.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
            vh.vrStatus.text = visitationList[position].inspectionstatus
            vh.vrType.text = visitationList[position].name
//            if (position%2!=0) vh.vrLL.setBackgroundResource(R.drawable.visitation_listitem_bkg_rtol)
//            else vh.vrLL.setBackgroundResource(R.drawable.visitation_listitem_bkg)
            vh.vrLoadBtn.setOnClickListener({
                (activity as MainActivity).FacilityName = visitationList[position].name
                (activity as MainActivity).FacilityNumber = "" + visitationList[position].facid
                (activity as MainActivity).VisitationID = visitationList[position].visitationid.toString()
                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.getFacilityWithIdUrl + visitationList[position].facid,
                        Response.Listener { response ->
                            activity!!.runOnUiThread(Runnable {
                                var facilityComplete = Gson().fromJson(response.toString(), Array<AAAFacilityComplete>::class.java).toCollection(ArrayList()).get(0) as AAAFacilityComplete
                                (activity as MainActivity).facilitySelected = facilityComplete

                                AnnualVisitationSingleton.getInstance().facilityId = facilityComplete.facid
                                AnnualVisitationSingleton.getInstance().facilityName = facilityComplete.businessname
                                AnnualVisitationSingleton.getInstance().facilityType = facilityComplete.facilitytypeid
                                AnnualVisitationSingleton.getInstance().billingMonth = facilityComplete.billingmonth
                                AnnualVisitationSingleton.getInstance().billingAmount = facilityComplete.billingamount
                                AnnualVisitationSingleton.getInstance().contractType = facilityComplete.contracttypeid
                                AnnualVisitationSingleton.getInstance().webSiteUrl = facilityComplete.website
                                AnnualVisitationSingleton.getInstance().facilityType = facilityComplete.facilitytypeid
                                AnnualVisitationSingleton.getInstance().currentContractDate = facilityComplete.contractcurrentdate
                                AnnualVisitationSingleton.getInstance().setInsuranceExpirationDate(facilityComplete.insuranceexpdate)
                                AnnualVisitationSingleton.getInstance().setInitialContractDate(facilityComplete.contractinitialdate)
                                AnnualVisitationSingleton.getInstance().assignedTo = facilityComplete.assignedtoid
                                AnnualVisitationSingleton.getInstance().office = facilityComplete.officeid
                                AnnualVisitationSingleton.getInstance().entityName = facilityComplete.entityname
                                AnnualVisitationSingleton.getInstance().timeZone = facilityComplete.timezoneid
                                AnnualVisitationSingleton.getInstance().taxId = facilityComplete.taxidnumber
                                AnnualVisitationSingleton.getInstance().repairOrderCount =  facilityComplete.facilityrepairordercount
                                AnnualVisitationSingleton.getInstance().serviceAvailability = facilityComplete.svcavailability
                                AnnualVisitationSingleton.getInstance().ardNumber = facilityComplete.automotiverepairnumber
                                AnnualVisitationSingleton.getInstance().setArdExpirationDate(facilityComplete.automotiverepairexpdate)

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


