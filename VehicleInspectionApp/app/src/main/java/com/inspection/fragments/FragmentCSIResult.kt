package com.inspection.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView

import com.inspection.R
import com.inspection.Utils.monthNoToName
import com.inspection.model.FacilityDataModel
import com.inspection.model.TblAAAPortalEmailFacilityRepTable
import kotlinx.android.synthetic.main.fragment_csiresult.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentCSIResult.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentCSIResult.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentCSIResult : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_csiresult, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillCSIResultsTableView()
    }

    fun fillCSIResultsTableView(){
        var sortedList = ArrayList<TblAAAPortalEmailFacilityRepTable>()
        FacilityDataModel.getInstance().tblAAAPortalEmailFacilityRepTable.sortedWith(compareBy{ it.Year.toInt()}).sortedWith(compareBy { it.Quarter.toInt() }).sortedWith(compareBy { it.Month.toInt() }).toCollection(sortedList)
        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1.4F
        rowLayoutParam.column = 0
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParamRow.weight=1F
//        textViewRow0.text = "Total Reponses     "
        sortedList.apply {
            (0 until size).forEach {
                if (it==0) {textViewRow0001.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3) }
                if (it==1) {textViewRow0002.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3) }
                if (it==2) {textViewRow0003.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3) }
                if (it==3) {textViewRow0004.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3) }
                if (it==4) {textViewRow0005.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3) }
                if (it==5) {textViewRow0006.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3) }
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
//        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
    }

    override fun onDetach() {
        super.onDetach()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentCSIResult.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentCSIResult().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
