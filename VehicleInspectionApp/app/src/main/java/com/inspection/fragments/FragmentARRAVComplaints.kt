package com.inspection.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.inspection.MainActivity

import com.inspection.R
import com.inspection.Utils.Consts
import com.inspection.Utils.toast
import com.inspection.model.AAAFacilityComplaints
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.fragment_arrav_complaints.*
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVComplaints.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVComplaints.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVComplaints : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    private var facilityComplaintsList = ArrayList<AAAFacilityComplaints>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arrav_complaints, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // The no os complaints , justified and ratio need to be clarified when all are showed
        compeditBtn.setOnClickListener({
            for (fac in facilityComplaintsList) {
                if (fac.complaintid.equals(complaint_id_textviewVal.text.toString())){
                    fac.comments = comments_textviewVal.text.toString()
                }
                BuildComplaintsList()
            }
        })
        compShowAllBtn.setOnClickListener({
            prepareComplaints(true)
        })
        prepareComplaints(true)
    }




    fun prepareComplaints (boolAll : Boolean) {

            progressbarComp.visibility = View.VISIBLE
            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.getFacilityComplaintsURL+ AnnualVisitationSingleton.getInstance().facilityId+"&all="+boolAll.toString(),
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {
                            facilityComplaintsList= Gson().fromJson(response.toString(), Array<AAAFacilityComplaints>::class.java).toCollection(ArrayList())
//                            drawProgramsTable()
                            BuildComplaintsList()
                        })
                    }, Response.ErrorListener {
                Log.v("error while loading", "error while loading facility complaints")
                context!!.toast("Connection Error. Please check the internet connection")
            }))
            progressbarComp.visibility = View.INVISIBLE
    }

    fun BuildComplaintsList() {
        val inflater = (activity as MainActivity)
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val parentLayout = compListLL
        parentLayout.removeAllViews()
        for (fac in facilityComplaintsList) {
            val vCompRow = inflater.inflate(R.layout.custom_complain_list_item, parentLayout, false)
            val compId= vCompRow.findViewById(R.id.compItemId) as TextView
            val compFirstName= vCompRow.findViewById(R.id.compItem1stName) as TextView
            val complastName= vCompRow.findViewById(R.id.compItem2ndName) as TextView
            val compReceievedDate= vCompRow.findViewById(R.id.compItemDate) as TextView
            val compReason= vCompRow.findViewById(R.id.compItemReason) as TextView
            val compResolution= vCompRow.findViewById(R.id.compItemResolution) as TextView
            val compComments= vCompRow.findViewById(R.id.compItemComments) as TextView
            compId.text = fac.complaintid.toString()
            compFirstName.text = fac.firstname
            complastName.text = fac.lastname
            compReceievedDate.text = if (fac.receiveddate.length>11 ) Consts.appFormat.format(Consts.dbFormat.parse(fac.receiveddate)) else fac.receiveddate
            compReason.text = fac.complaintreasonname
            compResolution.text = fac.complaintresolutionname
            compComments.text = fac.comments
            vCompRow.setOnClickListener({
                complaint_id_textviewVal.setText(fac.complaintid)
                received_date_textviewVal.text = if (fac.receiveddate.isNullOrEmpty() || fac.receiveddate.equals("NULL") || fac.receiveddate.equals("") || fac.receiveddate.toLowerCase().equals("no date provided")) "No Date Provided" else  {
                    if (fac.receiveddate.length>11 ) Consts.appFormat.format(Consts.dbFormat.parse(fac.receiveddate)) else fac.receiveddate
                }
                first_name_textviewVal.setText(fac.firstname)
                last_name_textviewVal.setText(fac.lastname)
                complaint_reason_textviewVal.setText(fac.complaintreasonname)
                complaint_resolution_textviewVal.setText(fac.complaintresolutionname)
                comments_textviewVal.setText(fac.comments)

                no_of_complaints_textviewVal.setText(fac.noofcomplaintslastyear.toString())
                no_of_justified_complaints_textviewVal.setText(fac.noofjustifiedlastyear.toString())
                justified_complaint_ratio_textviewVal.setText(Math.round(fac.noofjustifiedlastyear.toFloat() / fac.totalorders.toFloat()).toString())
            })
            parentLayout.addView(vCompRow)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

    }

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
         * @return A new instance of fragment FragmentARRAVFacility.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FragmentARRAVComplaints {
            val fragment = FragmentARRAVComplaints()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
