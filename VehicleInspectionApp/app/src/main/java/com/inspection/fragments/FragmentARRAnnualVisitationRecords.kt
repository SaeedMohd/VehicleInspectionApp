package com.inspection.fragments

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
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
import kotlinx.android.synthetic.main.custom_visitation_list_item.*
import kotlinx.android.synthetic.main.fragment_aar_manual_visitation_form.*
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
class FragmentARRAnnualVisitationRecords : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null
    var facilityNames = ArrayList<String>()
    var facilitiesList = ArrayList<AAAFacilityComplete>()
    var visitationList = ArrayList<AAAVisitationRecords>()
    var itemSelected = false
    var facilityNameInputField: EditText? = null

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
            Log.v("********** focus is", "Focus is: "+b)
            itemSelected = !b
        })

        // Inspection Type
        var inspectionTypes = arrayOf("Deficient","Quarterly","Annual","Annual / Deficient","Quarter / Deficient")
        var dataAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, inspectionTypes)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visitationinpectionTypeSpinner.adapter = dataAdapter

        // Inspection Kind
        var inspectionKinds = arrayOf("All","Planned Visitation","Regular Visitation")
        var dataAdapterkind = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, inspectionKinds)
        dataAdapterkind.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visitationinpectionKindSpinner.adapter = dataAdapterkind

        visitationfacilityNameVal.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!itemSelected && visitationfacilityNameVal.text.length >= 3){
                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.getfacilitiesURL+visitationfacilityNameVal.text,
                            Response.Listener { response ->
                                activity!!.runOnUiThread(Runnable {
                                    facilitiesList = Gson().fromJson(response.toString() , Array<AAAFacilityComplete>::class.java).toCollection(ArrayList())
                                    facilityNames.clear()
                                    for (fac in facilitiesList){
                                        facilityNames.add(fac.businessname)
                                    }

                                    visitationfacilityListView.visibility = View.VISIBLE
                                    visitationfacilityListView.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, facilityNames)
                                })
                            }, Response.ErrorListener {
                        Log.v("error while loading", "error while loading facilities")
                    }))
                }
            }

        })

        visitationfacilityListView.onItemClickListener = AdapterView.OnItemClickListener({ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(visitationfacilityNameVal.getWindowToken(), 0)
            itemSelected = true
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
            itemSelected=false
            showVisitationBtn.setText("SHOW VISITATIONS")
            visitationfacilityNameVal.isEnabled = true
            visitationrecordsListView.adapter=null
        })

        showVisitationBtn.setOnClickListener({
            Log.v("Button Pressed"," ------- ")
            if (visitationfacilityIdTextView.text.isNullOrEmpty()) {
                Toast.makeText(context,"Please select the Facility ...",Toast.LENGTH_LONG).show()
            } else {
                Log.v("URL .... " , Consts.getFacilityVisitationRecords+visitationfacilityIdVal.text+"&inspectionType="+(visitationinpectionTypeSpinner.selectedItemPosition+1))
                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.getFacilityVisitationRecords+visitationfacilityIdVal.text+"&inspectionType="+(visitationinpectionTypeSpinner.selectedItemId+1),
                        Response.Listener { response ->
                            activity!!.runOnUiThread(Runnable {
                                visitationList = Gson().fromJson(response.toString() , Array<AAAVisitationRecords>::class.java).toCollection(ArrayList())
                                var visitationRecords = ArrayList<String>()
                                for (fac in visitationList){
                                    if (visitationinpectionKindSpinner.selectedItemPosition==0) {
                                        visitationRecords.add(fac.visitationid.toString() + " - " + fac.performedby)
                                    } else if (visitationinpectionKindSpinner.selectedItemPosition==1) {
                                        if (fac.inspectionstatus.equals("Planned Visitation"))
                                        visitationRecords.add(fac.visitationid.toString() + " - " + fac.performedby)
                                    } else {
                                        if (!fac.inspectionstatus.equals("Planned Visitation"))
                                            visitationRecords.add(fac.visitationid.toString() + " - " + fac.performedby)
                                    }
                                }

                                visitationrecordsListView.visibility = View.VISIBLE
                                var visitationRecordsAdapter = VisitationListAdapter (context,visitationList)
//                                visitationrecordsListView.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, visitationRecords)
                                visitationrecordsListView.adapter = visitationRecordsAdapter
                                showVisitationBtn.setText("SHOW VISITATIONS" + " - ("+visitationRecords.size+")")
                            })
                        }, Response.ErrorListener {
                    Log.v("error while loading", "error while loading visitation records")
                }))
            }
        })
    }

    // TODO: Rename method, update argument and hook method into UI event
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
            if (position%2!=0) vh.vrLL.setBackgroundResource(R.drawable.visitation_listitem_bkg_rtol)
            else vh.vrLL.setBackgroundResource(R.drawable.visitation_listitem_bkg)
            vh.vrLoadBtn.setOnClickListener({
                (activity as MainActivity).VisitationID = visitationList[position].visitationid.toString()
                Toast.makeText(context,"Selected Visitation ID:  "+ (activity as MainActivity).VisitationID,Toast.LENGTH_LONG).show()
                val fragment: android.support.v4.app.Fragment
                fragment = FragmentAnnualVisitationPager()
                val fragmentManagerSC = fragmentManager
                val ftSC = fragmentManagerSC!!.beginTransaction()
                ftSC.replace(R.id.fragment,fragment)
                ftSC.addToBackStack("")
                ftSC.commit()
//                (activity as MainActivity).supportActionBar!!.title = formsStringsArray[i].toString()
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
        val vrLL: LinearLayout

        init {
            this.vrID = view?.findViewById(R.id.visitationItemId) as TextView
            this.vrBy= view?.findViewById(R.id.visitationItemPerformedBy) as TextView
            this.vrStatus= view?.findViewById(R.id.visitationItemStatus) as TextView
            this.vrType = view?.findViewById(R.id.visitationItemType) as TextView
            this.vrDate = view?.findViewById(R.id.visitationItemPerformedDate) as TextView
            this.vrPlanned = view?.findViewById(R.id.visitationItemPlanned) as TextView
            this.vrLL = view?.findViewById(R.id.list_item_ll) as LinearLayout
            this.vrLoadBtn= view?.findViewById(R.id.loadBtn) as TextView
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


