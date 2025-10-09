package com.inspection.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
//import android.widget.TextView
//import androidx.core.view.marginTop
import com.inspection.FormsActivity

import com.inspection.R
import com.inspection.Utils.monthNoToName
import com.inspection.databinding.FragmentCsiresultBinding
//import com.inspection.databinding.FragmentFormsBinding
import com.inspection.model.FacilityDataModel
import com.inspection.model.IndicatorsDataModel
import com.inspection.model.TblAAAPortalEmailFacilityRepTable


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
    var formatColor = Color.BLACK
    var formatStyle = Typeface.NORMAL
    private var _binding: FragmentCsiresultBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentCsiresultBinding.bind(view)
        fillCSIResultsTableViewUpdated()
        IndicatorsDataModel.getInstance().tblSurveys[0].visited = true
        // SAEED TO BE REVIEWED
//        (activity as FormsActivity).csiResultsButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
    }

//    fun fillCSIResultsTableView(){
//        var sortedList = ArrayList<TblAAAPortalEmailFacilityRepTable>()
////        FacilityDataModel.getInstance().tblAAAPortalEmailFacilityRepTable.sortedWith(compareBy{ it.Year.toInt()}).sortedWith(compareBy { it.Quarter.toInt() }).sortedWith(compareBy { it.Month.toInt() }).toCollection(sortedList)
//        FacilityDataModel.getInstance().tblAAAPortalEmailFacilityRepTable.sortedWith(compareBy{ it.Year+it.Quarter+it.Month}).toCollection(sortedList)
//        val rowLayoutParam = TableRow.LayoutParams()
//        rowLayoutParam.weight = 1.4F
//        rowLayoutParam.column = 0
//        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
//        rowLayoutParam.width = 0
//        rowLayoutParam.gravity = Gravity.CENTER_VERTICAL
//
//        val rowLayoutParamRow = TableRow.LayoutParams()
//        rowLayoutParamRow.height = TableRow.LayoutParams.WRAP_CONTENT
//        rowLayoutParamRow.weight=1F
//
//        // Headers
//        binding.textViewRow0200.text = "Total\nResponses"
//        binding.textViewRow0300.text = "Repair\nOrders"
//        binding.textViewRow0400.text = "Q1:\nSatisfied"
//        binding.textViewRow0500.text = "Q7:\nReturn"
//        binding.textViewRow0600.text = "Q2:\nRepair"
//        binding.textViewRow0700.text = "Q3:\nPersonnel"
//        binding.textViewRow0800.text = "Q4:\nEstimate"
//        binding.textViewRow0900.text = "Q5:\nClean"
//        binding.textViewRow1000.text = "Q6:\nReady"
//        binding.textViewRow1100.text = "Q8:\nMember"
//        binding.textViewRow1200.text = "Q9:\nChoose"
//
//
//        sortedList.apply {
//            (0 until size).forEach {
//                var strQuarter = if (get(it).Quarter.equals("99")) "YTD" else "Q"+get(it).Quarter
//                if (get(it).Month.toInt().monthNoToName().equals("") || strQuarter.equals("YTD") ){
//                    formatColor = Color.BLUE
//                    formatStyle = Typeface.BOLD
//                } else {
//                    formatColor = Color.BLACK
//                    formatStyle = Typeface.NORMAL
//                }
//                if (it==0) { // Each it is a Column
//                    binding.textViewRow0001.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0101.text = get(it).Year
//                    binding.textViewRow0201.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0301.text = get(it).RO_x0020_Count
//                    binding.textViewRow0401.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0501.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0601.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0701.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0801.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0901.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1001.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1101.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1201.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==1) {
//                    binding.textViewRow0002.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0102.text = get(it).Year
//                    binding.textViewRow0202.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0302.text = get(it).RO_x0020_Count
//                    binding.textViewRow0402.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0502.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0602.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0702.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0802.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0902.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1002.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1102.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1202.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==2) {
//                    binding.textViewRow0003.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0103.text = get(it).Year
//                    binding.textViewRow0203.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0303.text = get(it).RO_x0020_Count
//                    binding.textViewRow0403.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0503.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0603.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0703.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0803.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0903.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1003.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1103.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1203.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==3) {
//                    binding.textViewRow0004.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0104.text = get(it).Year
//                    binding.textViewRow0204.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0304.text = get(it).RO_x0020_Count
//                    binding.textViewRow0404.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0504.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0604.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0704.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0804.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0904.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1004.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1104.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1204.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==4) {
//                    binding.textViewRow0005.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0105.text = get(it).Year
//                    binding.textViewRow0205.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0305.text = get(it).RO_x0020_Count
//                    binding.textViewRow0405.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0505.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0605.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0705.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0805.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0905.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1005.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1105.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1205.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==5) {
//                    binding.textViewRow0006.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0106.text = get(it).Year
//                    binding.textViewRow0206.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0306.text = get(it).RO_x0020_Count
//                    binding.textViewRow0406.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0506.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0606.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0706.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0806.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0906.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1006.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1106.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1206.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==6) {
//                    binding.textViewRow0007.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0107.text = get(it).Year
//                    binding.textViewRow0207.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0307.text = get(it).RO_x0020_Count
//                    binding.textViewRow0407.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0507.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0607.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0707.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0807.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0907.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1007.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1107.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1207.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==7) {
//                    binding.textViewRow0008.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0108.text = get(it).Year
//                    binding.textViewRow0208.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0308.text = get(it).RO_x0020_Count
//                    binding.textViewRow0408.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0508.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0608.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0708.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0808.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0908.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1008.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1108.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1208.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==8) {
//                    binding.textViewRow0009.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0109.text = get(it).Year
//                    binding.textViewRow0209.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0309.text = get(it).RO_x0020_Count
//                    binding.textViewRow0409.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0509.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0609.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0709.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0809.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0909.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1009.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1109.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1209.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==9) {
//                    binding.textViewRow0010.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0110.text = get(it).Year
//                    binding.textViewRow0210.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0310.text = get(it).RO_x0020_Count
//                    binding.textViewRow0410.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0510.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0610.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0710.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0810.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0910.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1010.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1110.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1210.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==10) {
//                    binding.textViewRow0011.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0111.text = get(it).Year
//                    binding.textViewRow0211.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0311.text = get(it).RO_x0020_Count
//                    binding.textViewRow0411.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0511.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0611.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0711.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0811.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0911.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1011.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1111.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1211.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==11) {
//                    binding.textViewRow0012.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0112.text = get(it).Year
//                    binding.textViewRow0212.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0312.text = get(it).RO_x0020_Count
//                    binding.textViewRow0412.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0512.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0612.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0712.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0812.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0912.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1012.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1112.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1212.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==12) {
//                    binding.textViewRow0013.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0113.text = get(it).Year
//                    binding.textViewRow0213.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0313.text = get(it).RO_x0020_Count
//                    binding.textViewRow0413.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0513.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0613.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0713.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0813.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0913.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1013.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1113.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1213.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==13) {
//                    binding.textViewRow0014.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0114.text = get(it).Year
//                    binding.textViewRow0214.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0314.text = get(it).RO_x0020_Count
//                    binding.textViewRow0414.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0514.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0614.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0714.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0814.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0914.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1014.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1114.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1214.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==14) {
//                    binding.textViewRow0015.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0115.text = get(it).Year
//                    binding.textViewRow0215.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0315.text = get(it).RO_x0020_Count
//                    binding.textViewRow0415.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0515.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0615.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0715.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0815.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0915.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1015.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1115.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1215.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==15) {
//                    binding.textViewRow0016.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0116.text = get(it).Year
//                    binding.textViewRow0216.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0316.text = get(it).RO_x0020_Count
//                    binding.textViewRow0416.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0516.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0616.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0716.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0816.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0916.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1016.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1116.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1216.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==16) {
//                    binding.textViewRow0017.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0117.text = get(it).Year
//                    binding.textViewRow0217.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0317.text = get(it).RO_x0020_Count
//                    binding.textViewRow0417.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0517.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0617.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0717.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0817.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0917.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1017.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1117.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1217.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==17) {
//                    binding.textViewRow0018.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0118.text = get(it).Year
//                    binding.textViewRow0218.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0318.text = get(it).RO_x0020_Count
//                    binding.textViewRow0418.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0518.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0618.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0718.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0818.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0918.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1018.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1118.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1218.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==18) {
//                    binding.textViewRow0019.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0119.text = get(it).Year
//                    binding.textViewRow0219.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0319.text = get(it).RO_x0020_Count
//                    binding.textViewRow0419.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0519.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0619.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0719.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0819.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0919.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1019.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1119.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1219.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==19) {
//                    binding.textViewRow0020.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0120.text = get(it).Year
//                    binding.textViewRow0220.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0320.text = get(it).RO_x0020_Count
//                    binding.textViewRow0420.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0520.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0620.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0720.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0820.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0920.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1020.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1120.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1220.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==20) {
//                    binding.textViewRow0021.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0121.text = get(it).Year
//                    binding.textViewRow0221.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0321.text = get(it).RO_x0020_Count
//                    binding.textViewRow0421.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0521.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0621.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0721.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0821.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0921.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1021.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1121.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1221.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==21) {
//                    binding.textViewRow0022.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0122.text = get(it).Year
//                    binding.textViewRow0222.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0322.text = get(it).RO_x0020_Count
//                    binding.textViewRow0422.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0522.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0622.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0722.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0822.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0922.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1022.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1122.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1222.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//                if (it==22) {
//                    binding.textViewRow0023.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
//                    binding.textViewRow0123.text = get(it).Year
//                    binding.textViewRow0223.text = get(it).Total_x0020_Responses
//                    binding.textViewRow0323.text = get(it).RO_x0020_Count
//                    binding.textViewRow0423.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
//                    binding.textViewRow0523.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
//                    binding.textViewRow0623.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
//                    binding.textViewRow0723.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
//                    binding.textViewRow0823.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
//                    binding.textViewRow0923.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
//                    binding.textViewRow1023.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
//                    binding.textViewRow1123.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
//                    binding.textViewRow1223.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
//                    formatTexts(it)
//                }
//            }
//        }
//    }

    fun fillCSIResultsTableViewUpdated(){
        var sortedList = ArrayList<TblAAAPortalEmailFacilityRepTable>()
//        FacilityDataModel.getInstance().tblAAAPortalEmailFacilityRepTable.sortedWith(compareBy{ it.Year.toInt()}).sortedWith(compareBy { it.Quarter.toInt() }).sortedWith(compareBy { it.Month.toInt() }).toCollection(sortedList)
        FacilityDataModel.getInstance().tblAAAPortalEmailFacilityRepTable.sortedWith(compareBy{ it.Year+it.Quarter+it.Month}).toCollection(sortedList)
        val rowLayoutParam0 = TableRow.LayoutParams()
        rowLayoutParam0.weight = 1F
        rowLayoutParam0.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam0.width = 0
        rowLayoutParam0.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParamHeader = TableRow.LayoutParams()
        rowLayoutParamHeader.weight = 2F
        rowLayoutParamHeader.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParamHeader.width = 0
        rowLayoutParamHeader.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParamRow.weight=1F
        rowLayoutParamRow.width = TableRow.LayoutParams.WRAP_CONTENT


//        val dividerRow = TableRow(context)
//        val dividerView = View(context)
//        dividerView.layoutParams = TableRow.LayoutParams(
//            TableRow.LayoutParams.MATCH_PARENT, 2
//        )
//        dividerView.setBackgroundColor(Color.GRAY)
//        dividerRow.addView(dividerView)


        // Headers
//        binding.textViewRow0200.text = "Total\nResponses"
//        binding.textViewRow0300.text = "Repair\nOrders"
//        binding.textViewRow0400.text = "Q1:\nSatisfied"
//        binding.textViewRow0500.text = "Q7:\nReturn"
//        binding.textViewRow0600.text = "Q2:\nRepair"
//        binding.textViewRow0700.text = "Q3:\nPersonnel"
//        binding.textViewRow0800.text = "Q4:\nEstimate"
//        binding.textViewRow0900.text = "Q5:\nClean"
//        binding.textViewRow1000.text = "Q6:\nReady"
//        binding.textViewRow1100.text = "Q8:\nMember"
//        binding.textViewRow1200.text = "Q9:\nChoose"

        var monthRow = TableRow(context)
        monthRow.layoutParams = rowLayoutParamRow
        monthRow.minimumHeight = 60
        monthRow.setBackgroundColor(Color.parseColor("#073763"))

        val tvMonthHdr = TextView(context)
        rowLayoutParamHeader.column = 0
        tvMonthHdr.layoutParams = rowLayoutParamHeader
        tvMonthHdr.gravity = Gravity.CENTER
        tvMonthHdr.text = ""
        tvMonthHdr.minimumHeight = 30
        tvMonthHdr.textSize = 14f
        tvMonthHdr.setTextColor(Color.BLACK)
        monthRow.addView(tvMonthHdr)


        var yearRow = TableRow(context)
        yearRow.layoutParams = rowLayoutParamRow
        yearRow.minimumHeight = 60
        yearRow.setBackgroundColor(Color.parseColor("#073763"))

        val tvYesrHdr = TextView(context)
        rowLayoutParamHeader.column = 0
        tvYesrHdr.layoutParams = rowLayoutParamHeader
        tvYesrHdr.gravity = Gravity.CENTER
        tvYesrHdr.text = ""
        tvYesrHdr.minimumHeight = 30
        tvYesrHdr.textSize = 14f
        tvYesrHdr.setTextColor(Color.BLACK)
        yearRow.addView(tvYesrHdr)

        var totResponsesRow = TableRow(context)
        totResponsesRow.layoutParams = rowLayoutParamRow
        totResponsesRow.minimumHeight = 60

        val tvTotResponsesHdr = TextView(context)
        rowLayoutParamHeader.column = 0
        tvTotResponsesHdr.layoutParams = rowLayoutParamHeader
        tvTotResponsesHdr.gravity = Gravity.CENTER
        tvTotResponsesHdr.text = "Total\nResponses"
        tvTotResponsesHdr.minimumHeight = 30
        tvTotResponsesHdr.textSize = 14f
        tvTotResponsesHdr.setTextColor(Color.BLUE)
        totResponsesRow.addView(tvTotResponsesHdr)

        var repairOrderRow = TableRow(context)
        repairOrderRow.layoutParams = rowLayoutParamRow
        repairOrderRow.minimumHeight = 60

        val tbRepairOrderRowHdr = TextView(context)
        rowLayoutParamHeader.column = 0
        tbRepairOrderRowHdr.layoutParams = rowLayoutParamHeader
        tbRepairOrderRowHdr.gravity = Gravity.CENTER
        tbRepairOrderRowHdr.text = "Repair\nOrders"
        tbRepairOrderRowHdr.minimumHeight = 30
        tbRepairOrderRowHdr.textSize = 14f
        tbRepairOrderRowHdr.setTextColor(Color.BLUE)
        repairOrderRow.addView(tbRepairOrderRowHdr)

        var satisfiedRow = TableRow(context)
        satisfiedRow.layoutParams = rowLayoutParamRow
        satisfiedRow.minimumHeight = 60

        val tvSatisfiedRowHdr = TextView(context)
        rowLayoutParamHeader.column = 0
        tvSatisfiedRowHdr.layoutParams = rowLayoutParamHeader
        tvSatisfiedRowHdr.gravity = Gravity.CENTER
        tvSatisfiedRowHdr.text = "Q1: Satisfied"
        tvSatisfiedRowHdr.minimumHeight = 30
        tvSatisfiedRowHdr.textSize = 14f
        tvSatisfiedRowHdr.setTextColor(Color.BLUE)
        satisfiedRow.addView(tvSatisfiedRowHdr)

        var returnRow = TableRow(context)
        returnRow.layoutParams = rowLayoutParamRow
        returnRow.minimumHeight = 60

        val tvReturnRowHdr = TextView(context)
        rowLayoutParamHeader.column = 0
        tvReturnRowHdr.layoutParams = rowLayoutParamHeader
        tvReturnRowHdr.gravity = Gravity.CENTER
        tvReturnRowHdr.text = "Q7: Return"
        tvReturnRowHdr.minimumHeight = 30
        tvReturnRowHdr.textSize = 14f
        tvReturnRowHdr.setTextColor(Color.BLUE)
        returnRow.addView(tvReturnRowHdr)

        var repairRow = TableRow(context)
        repairRow.layoutParams = rowLayoutParamRow
        repairRow.minimumHeight = 60

        val tvRepairRowHdr = TextView(context)
        rowLayoutParamHeader.column = 0
        tvRepairRowHdr.layoutParams = rowLayoutParamHeader
        tvRepairRowHdr.gravity = Gravity.CENTER
        tvRepairRowHdr.text = "Q2: Repair"
        tvRepairRowHdr.minimumHeight = 30
        tvRepairRowHdr.textSize = 14f
        tvRepairRowHdr.setTextColor(Color.BLUE)
        repairRow.addView(tvRepairRowHdr)

        var personnelRow = TableRow(context)
        personnelRow.layoutParams = rowLayoutParamRow
        personnelRow.minimumHeight = 60

        val tvPersonnelRowHdr = TextView(context)
        rowLayoutParamHeader.column = 0
        tvPersonnelRowHdr.layoutParams = rowLayoutParamHeader
        tvPersonnelRowHdr.gravity = Gravity.CENTER
        tvPersonnelRowHdr.text = "Q3: Personnel"
        tvPersonnelRowHdr.minimumHeight = 30
        tvPersonnelRowHdr.textSize = 14f
        tvPersonnelRowHdr.setTextColor(Color.BLUE)
        personnelRow.addView(tvPersonnelRowHdr)

        var estimateRow = TableRow(context)
        estimateRow.layoutParams = rowLayoutParamRow
        estimateRow.minimumHeight = 60

        val tvEstimateRowHdr = TextView(context)
        rowLayoutParamHeader.column = 0
        tvEstimateRowHdr.layoutParams = rowLayoutParamHeader
        tvEstimateRowHdr.gravity = Gravity.CENTER
        tvEstimateRowHdr.text = "Q4: Estimate"
        tvEstimateRowHdr.minimumHeight = 30
        tvEstimateRowHdr.textSize = 14f
        tvEstimateRowHdr.setTextColor(Color.BLUE)
        estimateRow.addView(tvEstimateRowHdr)

        var cleanRow = TableRow(context)
        cleanRow.layoutParams = rowLayoutParamRow
        cleanRow.minimumHeight = 60

        val tvCleanRowHdr = TextView(context)
        rowLayoutParamHeader.column = 0
        tvCleanRowHdr.layoutParams = rowLayoutParamHeader
        tvCleanRowHdr.gravity = Gravity.CENTER
        tvCleanRowHdr.text = "Q5: Clean"
        tvCleanRowHdr.minimumHeight = 30
        tvCleanRowHdr.textSize = 14f
        tvCleanRowHdr.setTextColor(Color.BLUE)
        cleanRow.addView(tvCleanRowHdr)

        var readyRow = TableRow(context)
        readyRow.layoutParams = rowLayoutParamRow
        readyRow.minimumHeight = 60

        val tvReadyRowHdr = TextView(context)
        rowLayoutParamHeader.column = 0
        tvReadyRowHdr.layoutParams = rowLayoutParamHeader
        tvReadyRowHdr.gravity = Gravity.CENTER
        tvReadyRowHdr.text = "Q6: Ready"
        tvReadyRowHdr.minimumHeight = 30
        tvReadyRowHdr.textSize = 14f
        tvReadyRowHdr.setTextColor(Color.BLUE)
        readyRow.addView(tvReadyRowHdr)

        var memberRow = TableRow(context)
        memberRow.layoutParams = rowLayoutParamRow
        memberRow.minimumHeight = 60

        val tvMemberRowHdr = TextView(context)
        rowLayoutParamHeader.column = 0
        tvMemberRowHdr.layoutParams = rowLayoutParamHeader
        tvMemberRowHdr.gravity = Gravity.CENTER
        tvMemberRowHdr.text = "Q8: Member"
        tvMemberRowHdr.minimumHeight = 30
        tvMemberRowHdr.textSize = 14f
        tvMemberRowHdr.setTextColor(Color.BLUE)
        memberRow.addView(tvMemberRowHdr)

        var chooseRow = TableRow(context)
        chooseRow.layoutParams = rowLayoutParamRow
        chooseRow.minimumHeight = 60

        val tvChooseRowHdr = TextView(context)
        rowLayoutParamHeader.column = 0
        tvChooseRowHdr.layoutParams = rowLayoutParamHeader
        tvChooseRowHdr.gravity = Gravity.CENTER
        tvChooseRowHdr.text = "Q9: Choose"
        tvChooseRowHdr.minimumHeight = 30
        tvChooseRowHdr.textSize = 14f
        tvChooseRowHdr.setTextColor(Color.BLUE)
        chooseRow.addView(tvChooseRowHdr)

        sortedList.apply {
            (0 until size).forEach {
                var strQuarter = if (get(it).Quarter.equals("99")) "YTD" else "Q"+get(it).Quarter
                if (get(it).Month.toInt().monthNoToName().equals("") || strQuarter.equals("YTD") ){
                    formatColor = Color.BLUE
                    formatStyle = Typeface.BOLD
                } else {
                    formatColor = Color.BLACK
                    formatStyle = Typeface.NORMAL
                }

                // 1st Row Months / Qs

                val tvMonth = TextView(context)
                rowLayoutParam0.column = it + 1
                tvMonth.layoutParams = rowLayoutParam0
                tvMonth.gravity = Gravity.CENTER
                tvMonth.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                tvMonth.minimumHeight = 30
                tvMonth.textSize = 14f
                tvMonth.setTextColor(Color.WHITE)
                tvMonth.setTypeface(null, formatStyle)
                monthRow.addView(tvMonth)

                val tvYear = TextView(context)
                rowLayoutParam0.column = it + 1
                tvYear.layoutParams = rowLayoutParam0
                tvYear.gravity = Gravity.CENTER
                tvYear.text = get(it).Year
                tvYear.minimumHeight = 30
                tvYear.textSize = 14f
                tvYear.setTextColor(Color.WHITE)
                tvYear.setTypeface(null, formatStyle)
                yearRow.addView(tvYear)

                val tvTotResponses = TextView(context)
                rowLayoutParam0.column = it + 1
                tvTotResponses.layoutParams = rowLayoutParam0
                tvTotResponses.gravity = Gravity.CENTER
                tvTotResponses.text = get(it).Total_x0020_Responses
                tvTotResponses.minimumHeight = 30
                tvTotResponses.textSize = 14f
                tvTotResponses.setTextColor(formatColor)
                tvTotResponses.setTypeface(null,formatStyle)
                totResponsesRow.addView(tvTotResponses)

                val tvRepairOrder = TextView(context)
                rowLayoutParam0.column = it + 1
                tvRepairOrder.layoutParams = rowLayoutParam0
                tvRepairOrder.gravity = Gravity.CENTER
                tvRepairOrder.text = get(it).RO_x0020_Count
                tvRepairOrder.minimumHeight = 30
                tvRepairOrder.textSize = 14f
                tvRepairOrder.setTextColor(formatColor)
                tvRepairOrder.setTypeface(null,formatStyle)
                repairOrderRow.addView(tvRepairOrder)

                val tvSatisfied = TextView(context)
                rowLayoutParam0.column = it + 1
                tvSatisfied.layoutParams = rowLayoutParam0
                tvSatisfied.gravity = Gravity.CENTER
                tvSatisfied.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                tvSatisfied.minimumHeight = 30
                tvSatisfied.textSize = 14f
                tvSatisfied.setTextColor(formatColor)
                tvSatisfied.setTypeface(null,formatStyle)
                satisfiedRow.addView(tvSatisfied)

                val tvReturn = TextView(context)
                rowLayoutParam0.column = it + 1
                tvReturn.layoutParams = rowLayoutParam0
                tvReturn.gravity = Gravity.CENTER
                tvReturn.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                tvReturn.minimumHeight = 30
                tvReturn.textSize = 14f
                tvReturn.setTextColor(formatColor)
                tvReturn.setTypeface(null,formatStyle)
                returnRow.addView(tvReturn)

                val tvRepair = TextView(context)
                rowLayoutParam0.column = it + 1
                tvRepair.layoutParams = rowLayoutParam0
                tvRepair.gravity = Gravity.CENTER
                tvRepair.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                tvRepair.minimumHeight = 30
                tvRepair.textSize = 14f
                tvRepair.setTextColor(formatColor)
                tvRepair.setTypeface(null,formatStyle)
                repairRow.addView(tvRepair)

                val tvPersonnel = TextView(context)
                rowLayoutParam0.column = it + 1
                tvPersonnel.layoutParams = rowLayoutParam0
                tvPersonnel.gravity = Gravity.CENTER
                tvPersonnel.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                tvPersonnel.minimumHeight = 30
                tvPersonnel.textSize = 14f
                tvPersonnel.setTextColor(formatColor)
                tvPersonnel.setTypeface(null,formatStyle)
                personnelRow.addView(tvPersonnel)

                val tvEstimate = TextView(context)
                rowLayoutParam0.column = it + 1
                tvEstimate.layoutParams = rowLayoutParam0
                tvEstimate.gravity = Gravity.CENTER
                tvEstimate.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                tvEstimate.minimumHeight = 30
                tvEstimate.textSize = 14f
                tvEstimate.setTextColor(formatColor)
                tvEstimate.setTypeface(null,formatStyle)
                estimateRow.addView(tvEstimate)

                val tvClean = TextView(context)
                rowLayoutParam0.column = it + 1
                tvClean.layoutParams = rowLayoutParam0
                tvClean.gravity = Gravity.CENTER
                tvClean.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                tvClean.minimumHeight = 30
                tvClean.textSize = 14f
                tvClean.setTextColor(formatColor)
                tvClean.setTypeface(null,formatStyle)
                cleanRow.addView(tvClean)

                val tvReady = TextView(context)
                rowLayoutParam0.column = it + 1
                tvReady.layoutParams = rowLayoutParam0
                tvReady.gravity = Gravity.CENTER
                tvReady.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                tvReady.minimumHeight = 30
                tvReady.textSize = 14f
                tvReady.setTextColor(formatColor)
                tvReady.setTypeface(null,formatStyle)
                readyRow.addView(tvReady)

                val tvMember = TextView(context)
                rowLayoutParam0.column = it + 1
                tvMember.layoutParams = rowLayoutParam0
                tvMember.gravity = Gravity.CENTER
                tvMember.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                tvMember.minimumHeight = 30
                tvMember.textSize = 14f
                tvMember.setTextColor(formatColor)
                tvMember.setTypeface(null,formatStyle)
                memberRow.addView(tvMember)

                val tvChoose = TextView(context)
                rowLayoutParam0.column = it + 1
                tvChoose.layoutParams = rowLayoutParam0
                tvChoose.gravity = Gravity.CENTER
                tvChoose.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                tvChoose.minimumHeight = 30
                tvChoose.textSize = 14f
                tvChoose.setTextColor(formatColor)
                tvChoose.setTypeface(null,formatStyle)
                chooseRow.addView(tvChoose)

            }
        }
        binding.CSIResultsTbl.addView(monthRow)
        binding.CSIResultsTbl.addView(yearRow)
        binding.CSIResultsTbl.addView(totResponsesRow)
        binding.CSIResultsTbl.addView(repairOrderRow)
        binding.CSIResultsTbl.addView(satisfiedRow)
        binding.CSIResultsTbl.addView(returnRow)
        binding.CSIResultsTbl.addView(repairRow)
        binding.CSIResultsTbl.addView(personnelRow)
        binding.CSIResultsTbl.addView(estimateRow)
        binding.CSIResultsTbl.addView(cleanRow)
        binding.CSIResultsTbl.addView(readyRow)
        binding.CSIResultsTbl.addView(memberRow)
        binding.CSIResultsTbl.addView(chooseRow)
    }

//    fun formatTexts(section : Int){
//        if (section==0) {
//            binding.textViewRow0001.setTextColor(formatColor)
//            binding.textViewRow0101.setTextColor(formatColor)
//            binding.textViewRow0201.setTextColor(formatColor)
//            binding.textViewRow0301.setTextColor(formatColor)
//            binding.textViewRow0401.setTextColor(formatColor)
//            binding.textViewRow0501.setTextColor(formatColor)
//            binding.textViewRow0601.setTextColor(formatColor)
//            binding.textViewRow0701.setTextColor(formatColor)
//            binding.textViewRow0801.setTextColor(formatColor)
//            binding.textViewRow0901.setTextColor(formatColor)
//            binding.textViewRow1001.setTextColor(formatColor)
//            binding.textViewRow1101.setTextColor(formatColor)
//            binding.textViewRow1201.setTextColor(formatColor)
//            binding.textViewRow0001.setTypeface(binding.textViewRow0001.typeface,formatStyle)
//            binding.textViewRow0101.setTypeface(binding.textViewRow0101.typeface,formatStyle)
//            binding.textViewRow0201.setTypeface(binding.textViewRow0201.typeface,formatStyle)
//            binding.textViewRow0301.setTypeface(binding.textViewRow0301.typeface,formatStyle)
//            binding.textViewRow0401.setTypeface(binding.textViewRow0401.typeface,formatStyle)
//            binding.textViewRow0501.setTypeface(binding.textViewRow0501.typeface,formatStyle)
//            binding.textViewRow0601.setTypeface(binding.textViewRow0601.typeface,formatStyle)
//            binding.textViewRow0701.setTypeface(binding.textViewRow0701.typeface,formatStyle)
//            binding.textViewRow0801.setTypeface(binding.textViewRow0801.typeface,formatStyle)
//            binding.textViewRow0901.setTypeface(binding.textViewRow0901.typeface,formatStyle)
//            binding.textViewRow1001.setTypeface(binding.textViewRow1001.typeface,formatStyle)
//            binding.textViewRow1101.setTypeface(binding.textViewRow1101.typeface,formatStyle)
//            binding.textViewRow1201.setTypeface(binding.textViewRow1201.typeface,formatStyle)
//        } else if (section==1) {
//            binding.textViewRow0002.setTextColor(formatColor)
//            binding.textViewRow0102.setTextColor(formatColor)
//            binding.textViewRow0202.setTextColor(formatColor)
//            binding.textViewRow0302.setTextColor(formatColor)
//            binding.textViewRow0402.setTextColor(formatColor)
//            binding.textViewRow0502.setTextColor(formatColor)
//            binding.textViewRow0602.setTextColor(formatColor)
//            binding.textViewRow0702.setTextColor(formatColor)
//            binding.textViewRow0802.setTextColor(formatColor)
//            binding.textViewRow0902.setTextColor(formatColor)
//            binding.textViewRow1002.setTextColor(formatColor)
//            binding.textViewRow1102.setTextColor(formatColor)
//            binding.textViewRow1202.setTextColor(formatColor)
//            binding.textViewRow0002.setTypeface(binding.textViewRow0002.typeface,formatStyle)
//            binding.textViewRow0102.setTypeface(binding.textViewRow0102.typeface,formatStyle)
//            binding.textViewRow0202.setTypeface(binding.textViewRow0202.typeface,formatStyle)
//            binding.textViewRow0302.setTypeface(binding.textViewRow0302.typeface,formatStyle)
//            binding.textViewRow0402.setTypeface(binding.textViewRow0402.typeface,formatStyle)
//            binding.textViewRow0502.setTypeface(binding.textViewRow0502.typeface,formatStyle)
//            binding.textViewRow0602.setTypeface(binding.textViewRow0602.typeface,formatStyle)
//            binding.textViewRow0702.setTypeface(binding.textViewRow0702.typeface,formatStyle)
//            binding.textViewRow0802.setTypeface(binding.textViewRow0802.typeface,formatStyle)
//            binding.textViewRow0902.setTypeface(binding.textViewRow0902.typeface,formatStyle)
//            binding.textViewRow1002.setTypeface(binding.textViewRow1002.typeface,formatStyle)
//            binding.textViewRow1102.setTypeface(binding.textViewRow1102.typeface,formatStyle)
//            binding.textViewRow1202.setTypeface(binding.textViewRow1202.typeface,formatStyle)
//        } else if (section==2) {
//            binding.textViewRow0003.setTextColor(formatColor)
//            binding.textViewRow0103.setTextColor(formatColor)
//            binding.textViewRow0203.setTextColor(formatColor)
//            binding.textViewRow0303.setTextColor(formatColor)
//            binding.textViewRow0403.setTextColor(formatColor)
//            binding.textViewRow0503.setTextColor(formatColor)
//            binding.textViewRow0603.setTextColor(formatColor)
//            binding.textViewRow0703.setTextColor(formatColor)
//            binding.textViewRow0803.setTextColor(formatColor)
//            binding.textViewRow0903.setTextColor(formatColor)
//            binding.textViewRow1003.setTextColor(formatColor)
//            binding.textViewRow1103.setTextColor(formatColor)
//            binding.textViewRow1203.setTextColor(formatColor)
//            binding.textViewRow0003.setTypeface(binding.textViewRow0003.typeface,formatStyle)
//            binding.textViewRow0103.setTypeface(binding.textViewRow0103.typeface,formatStyle)
//            binding.textViewRow0203.setTypeface(binding.textViewRow0203.typeface,formatStyle)
//            binding.textViewRow0303.setTypeface(binding.textViewRow0303.typeface,formatStyle)
//            binding.textViewRow0403.setTypeface(binding.textViewRow0403.typeface,formatStyle)
//            binding.textViewRow0503.setTypeface(binding.textViewRow0503.typeface,formatStyle)
//            binding.textViewRow0603.setTypeface(binding.textViewRow0603.typeface,formatStyle)
//            binding.textViewRow0703.setTypeface(binding.textViewRow0703.typeface,formatStyle)
//            binding.textViewRow0803.setTypeface(binding.textViewRow0803.typeface,formatStyle)
//            binding.textViewRow0903.setTypeface(binding.textViewRow0903.typeface,formatStyle)
//            binding.textViewRow1003.setTypeface(binding.textViewRow1003.typeface,formatStyle)
//            binding.textViewRow1103.setTypeface(binding.textViewRow1103.typeface,formatStyle)
//            binding.textViewRow1203.setTypeface(binding.textViewRow1203.typeface,formatStyle)
//        } else if (section==3) {
//            binding.textViewRow0004.setTextColor(formatColor)
//            binding.textViewRow0104.setTextColor(formatColor)
//            binding.textViewRow0204.setTextColor(formatColor)
//            binding.textViewRow0304.setTextColor(formatColor)
//            binding.textViewRow0404.setTextColor(formatColor)
//            binding.textViewRow0504.setTextColor(formatColor)
//            binding.textViewRow0604.setTextColor(formatColor)
//            binding.textViewRow0704.setTextColor(formatColor)
//            binding.textViewRow0804.setTextColor(formatColor)
//            binding.textViewRow0904.setTextColor(formatColor)
//            binding.textViewRow1004.setTextColor(formatColor)
//            binding.textViewRow1104.setTextColor(formatColor)
//            binding.textViewRow1204.setTextColor(formatColor)
//            binding.textViewRow0004.setTypeface(binding.textViewRow0004.typeface,formatStyle)
//            binding.textViewRow0104.setTypeface(binding.textViewRow0104.typeface,formatStyle)
//            binding.textViewRow0204.setTypeface(binding.textViewRow0204.typeface,formatStyle)
//            binding.textViewRow0304.setTypeface(binding.textViewRow0304.typeface,formatStyle)
//            binding.textViewRow0404.setTypeface(binding.textViewRow0404.typeface,formatStyle)
//            binding.textViewRow0504.setTypeface(binding.textViewRow0504.typeface,formatStyle)
//            binding.textViewRow0604.setTypeface(binding.textViewRow0604.typeface,formatStyle)
//            binding.textViewRow0704.setTypeface(binding.textViewRow0704.typeface,formatStyle)
//            binding.textViewRow0804.setTypeface(binding.textViewRow0804.typeface,formatStyle)
//            binding.textViewRow0904.setTypeface(binding.textViewRow0904.typeface,formatStyle)
//            binding.textViewRow1004.setTypeface(binding.textViewRow1004.typeface,formatStyle)
//            binding.textViewRow1104.setTypeface(binding.textViewRow1104.typeface,formatStyle)
//            binding.textViewRow1204.setTypeface(binding.textViewRow1204.typeface,formatStyle)
//        } else if (section==4) {
//            binding.textViewRow0005.setTextColor(formatColor)
//            binding.textViewRow0105.setTextColor(formatColor)
//            binding.textViewRow0205.setTextColor(formatColor)
//            binding.textViewRow0305.setTextColor(formatColor)
//            binding.textViewRow0405.setTextColor(formatColor)
//            binding.textViewRow0505.setTextColor(formatColor)
//            binding.textViewRow0605.setTextColor(formatColor)
//            binding.textViewRow0705.setTextColor(formatColor)
//            binding.textViewRow0805.setTextColor(formatColor)
//            binding.textViewRow0905.setTextColor(formatColor)
//            binding.textViewRow1005.setTextColor(formatColor)
//            binding.textViewRow1105.setTextColor(formatColor)
//            binding.textViewRow1205.setTextColor(formatColor)
//            binding.textViewRow0005.setTypeface(binding.textViewRow0005.typeface,formatStyle)
//            binding.textViewRow0105.setTypeface(binding.textViewRow0105.typeface,formatStyle)
//            binding.textViewRow0205.setTypeface(binding.textViewRow0205.typeface,formatStyle)
//            binding.textViewRow0305.setTypeface(binding.textViewRow0305.typeface,formatStyle)
//            binding.textViewRow0405.setTypeface(binding.textViewRow0405.typeface,formatStyle)
//            binding.textViewRow0505.setTypeface(binding.textViewRow0505.typeface,formatStyle)
//            binding.textViewRow0605.setTypeface(binding.textViewRow0605.typeface,formatStyle)
//            binding.textViewRow0705.setTypeface(binding.textViewRow0705.typeface,formatStyle)
//            binding.textViewRow0805.setTypeface(binding.textViewRow0805.typeface,formatStyle)
//            binding.textViewRow0905.setTypeface(binding.textViewRow0905.typeface,formatStyle)
//            binding.textViewRow1005.setTypeface(binding.textViewRow1005.typeface,formatStyle)
//            binding.textViewRow1105.setTypeface(binding.textViewRow1105.typeface,formatStyle)
//            binding.textViewRow1205.setTypeface(binding.textViewRow1205.typeface,formatStyle)
//        } else if (section==5) {
//            binding.textViewRow0006.setTextColor(formatColor)
//            binding.textViewRow0106.setTextColor(formatColor)
//            binding.textViewRow0206.setTextColor(formatColor)
//            binding.textViewRow0306.setTextColor(formatColor)
//            binding.textViewRow0406.setTextColor(formatColor)
//            binding.textViewRow0506.setTextColor(formatColor)
//            binding.textViewRow0606.setTextColor(formatColor)
//            binding.textViewRow0706.setTextColor(formatColor)
//            binding.textViewRow0806.setTextColor(formatColor)
//            binding.textViewRow0906.setTextColor(formatColor)
//            binding.textViewRow1006.setTextColor(formatColor)
//            binding.textViewRow1106.setTextColor(formatColor)
//            binding.textViewRow1206.setTextColor(formatColor)
//            binding.textViewRow0006.setTypeface(binding.textViewRow0006.typeface,formatStyle)
//            binding.textViewRow0106.setTypeface(binding.textViewRow0106.typeface,formatStyle)
//            binding.textViewRow0206.setTypeface(binding.textViewRow0206.typeface,formatStyle)
//            binding.textViewRow0306.setTypeface(binding.textViewRow0306.typeface,formatStyle)
//            binding.textViewRow0406.setTypeface(binding.textViewRow0406.typeface,formatStyle)
//            binding.textViewRow0506.setTypeface(binding.textViewRow0506.typeface,formatStyle)
//            binding.textViewRow0606.setTypeface(binding.textViewRow0606.typeface,formatStyle)
//            binding.textViewRow0706.setTypeface(binding.textViewRow0706.typeface,formatStyle)
//            binding.textViewRow0806.setTypeface(binding.textViewRow0806.typeface,formatStyle)
//            binding.textViewRow0906.setTypeface(binding.textViewRow0906.typeface,formatStyle)
//            binding.textViewRow1006.setTypeface(binding.textViewRow1006.typeface,formatStyle)
//            binding.textViewRow1106.setTypeface(binding.textViewRow1106.typeface,formatStyle)
//            binding.textViewRow1206.setTypeface(binding.textViewRow1206.typeface,formatStyle)
//        } else if (section==6) {
//            binding.textViewRow0007.setTextColor(formatColor)
//            binding.textViewRow0107.setTextColor(formatColor)
//            binding.textViewRow0207.setTextColor(formatColor)
//            binding.textViewRow0307.setTextColor(formatColor)
//            binding.textViewRow0407.setTextColor(formatColor)
//            binding.textViewRow0507.setTextColor(formatColor)
//            binding.textViewRow0607.setTextColor(formatColor)
//            binding.textViewRow0707.setTextColor(formatColor)
//            binding.textViewRow0807.setTextColor(formatColor)
//            binding.textViewRow0907.setTextColor(formatColor)
//            binding.textViewRow1007.setTextColor(formatColor)
//            binding.textViewRow1107.setTextColor(formatColor)
//            binding.textViewRow1207.setTextColor(formatColor)
//            binding.textViewRow0007.setTypeface(binding.textViewRow0007.typeface,formatStyle)
//            binding.textViewRow0107.setTypeface(binding.textViewRow0107.typeface,formatStyle)
//            binding.textViewRow0207.setTypeface(binding.textViewRow0207.typeface,formatStyle)
//            binding.textViewRow0307.setTypeface(binding.textViewRow0307.typeface,formatStyle)
//            binding.textViewRow0407.setTypeface(binding.textViewRow0407.typeface,formatStyle)
//            binding.textViewRow0507.setTypeface(binding.textViewRow0507.typeface,formatStyle)
//            binding.textViewRow0607.setTypeface(binding.textViewRow0607.typeface,formatStyle)
//            binding.textViewRow0707.setTypeface(binding.textViewRow0707.typeface,formatStyle)
//            binding.textViewRow0807.setTypeface(binding.textViewRow0807.typeface,formatStyle)
//            binding.textViewRow0907.setTypeface(binding.textViewRow0907.typeface,formatStyle)
//            binding.textViewRow1007.setTypeface(binding.textViewRow1007.typeface,formatStyle)
//            binding.textViewRow1107.setTypeface(binding.textViewRow1107.typeface,formatStyle)
//            binding.textViewRow1207.setTypeface(binding.textViewRow1207.typeface,formatStyle)
//        } else if (section==7) {
//            binding.textViewRow0008.setTextColor(formatColor)
//            binding.textViewRow0108.setTextColor(formatColor)
//            binding.textViewRow0208.setTextColor(formatColor)
//            binding.textViewRow0308.setTextColor(formatColor)
//            binding.textViewRow0408.setTextColor(formatColor)
//            binding.textViewRow0508.setTextColor(formatColor)
//            binding.textViewRow0608.setTextColor(formatColor)
//            binding.textViewRow0708.setTextColor(formatColor)
//            binding.textViewRow0808.setTextColor(formatColor)
//            binding.textViewRow0908.setTextColor(formatColor)
//            binding.textViewRow1008.setTextColor(formatColor)
//            binding.textViewRow1108.setTextColor(formatColor)
//            binding.textViewRow1208.setTextColor(formatColor)
//            binding.textViewRow0008.setTypeface(binding.textViewRow0008.typeface,formatStyle)
//            binding.textViewRow0108.setTypeface(binding.textViewRow0108.typeface,formatStyle)
//            binding.textViewRow0208.setTypeface(binding.textViewRow0208.typeface,formatStyle)
//            binding.textViewRow0308.setTypeface(binding.textViewRow0308.typeface,formatStyle)
//            binding.textViewRow0408.setTypeface(binding.textViewRow0408.typeface,formatStyle)
//            binding.textViewRow0508.setTypeface(binding.textViewRow0508.typeface,formatStyle)
//            binding.textViewRow0608.setTypeface(binding.textViewRow0608.typeface,formatStyle)
//            binding.textViewRow0708.setTypeface(binding.textViewRow0708.typeface,formatStyle)
//            binding.textViewRow0808.setTypeface(binding.textViewRow0808.typeface,formatStyle)
//            binding.textViewRow0908.setTypeface(binding.textViewRow0908.typeface,formatStyle)
//            binding.textViewRow1008.setTypeface(binding.textViewRow1008.typeface,formatStyle)
//            binding.textViewRow1108.setTypeface(binding.textViewRow1108.typeface,formatStyle)
//            binding.textViewRow1208.setTypeface(binding.textViewRow1208.typeface,formatStyle)
//        } else if (section==8) {
//            binding.textViewRow0009.setTextColor(formatColor)
//            binding.textViewRow0109.setTextColor(formatColor)
//            binding.textViewRow0209.setTextColor(formatColor)
//            binding.textViewRow0309.setTextColor(formatColor)
//            binding.textViewRow0409.setTextColor(formatColor)
//            binding.textViewRow0509.setTextColor(formatColor)
//            binding.textViewRow0609.setTextColor(formatColor)
//            binding.textViewRow0709.setTextColor(formatColor)
//            binding.textViewRow0809.setTextColor(formatColor)
//            binding.textViewRow0909.setTextColor(formatColor)
//            binding.textViewRow1009.setTextColor(formatColor)
//            binding.textViewRow1109.setTextColor(formatColor)
//            binding.textViewRow1209.setTextColor(formatColor)
//            binding.textViewRow0009.setTypeface(binding.textViewRow0009.typeface,formatStyle)
//            binding.textViewRow0109.setTypeface(binding.textViewRow0109.typeface,formatStyle)
//            binding.textViewRow0209.setTypeface(binding.textViewRow0209.typeface,formatStyle)
//            binding.textViewRow0309.setTypeface(binding.textViewRow0309.typeface,formatStyle)
//            binding.textViewRow0409.setTypeface(binding.textViewRow0409.typeface,formatStyle)
//            binding.textViewRow0509.setTypeface(binding.textViewRow0509.typeface,formatStyle)
//            binding.textViewRow0609.setTypeface(binding.textViewRow0609.typeface,formatStyle)
//            binding.textViewRow0709.setTypeface(binding.textViewRow0709.typeface,formatStyle)
//            binding.textViewRow0809.setTypeface(binding.textViewRow0809.typeface,formatStyle)
//            binding.textViewRow0909.setTypeface(binding.textViewRow0909.typeface,formatStyle)
//            binding.textViewRow1009.setTypeface(binding.textViewRow1009.typeface,formatStyle)
//            binding.textViewRow1109.setTypeface(binding.textViewRow1109.typeface,formatStyle)
//            binding.textViewRow1209.setTypeface(binding.textViewRow1209.typeface,formatStyle)
//        } else if (section==9) {
//            binding.textViewRow0010.setTextColor(formatColor)
//            binding.textViewRow0110.setTextColor(formatColor)
//            binding.textViewRow0210.setTextColor(formatColor)
//            binding.textViewRow0310.setTextColor(formatColor)
//            binding.textViewRow0410.setTextColor(formatColor)
//            binding.textViewRow0510.setTextColor(formatColor)
//            binding.textViewRow0610.setTextColor(formatColor)
//            binding.textViewRow0710.setTextColor(formatColor)
//            binding.textViewRow0810.setTextColor(formatColor)
//            binding.textViewRow0910.setTextColor(formatColor)
//            binding.textViewRow1010.setTextColor(formatColor)
//            binding.textViewRow1110.setTextColor(formatColor)
//            binding.textViewRow1210.setTextColor(formatColor)
//            binding.textViewRow0010.setTypeface(binding.textViewRow0010.typeface,formatStyle)
//            binding.textViewRow0110.setTypeface(binding.textViewRow0110.typeface,formatStyle)
//            binding.textViewRow0210.setTypeface(binding.textViewRow0210.typeface,formatStyle)
//            binding.textViewRow0310.setTypeface(binding.textViewRow0310.typeface,formatStyle)
//            binding.textViewRow0410.setTypeface(binding.textViewRow0410.typeface,formatStyle)
//            binding.textViewRow0510.setTypeface(binding.textViewRow0510.typeface,formatStyle)
//            binding.textViewRow0610.setTypeface(binding.textViewRow0610.typeface,formatStyle)
//            binding.textViewRow0710.setTypeface(binding.textViewRow0710.typeface,formatStyle)
//            binding.textViewRow0810.setTypeface(binding.textViewRow0810.typeface,formatStyle)
//            binding.textViewRow0910.setTypeface(binding.textViewRow0910.typeface,formatStyle)
//            binding.textViewRow1010.setTypeface(binding.textViewRow1010.typeface,formatStyle)
//            binding.textViewRow1110.setTypeface(binding.textViewRow1110.typeface,formatStyle)
//            binding.textViewRow1210.setTypeface(binding.textViewRow1210.typeface,formatStyle)
//        } else if (section==10) {
//            binding.textViewRow0011.setTextColor(formatColor)
//            binding.textViewRow0111.setTextColor(formatColor)
//            binding.textViewRow0211.setTextColor(formatColor)
//            binding.textViewRow0311.setTextColor(formatColor)
//            binding.textViewRow0411.setTextColor(formatColor)
//            binding.textViewRow0511.setTextColor(formatColor)
//            binding.textViewRow0611.setTextColor(formatColor)
//            binding.textViewRow0711.setTextColor(formatColor)
//            binding.textViewRow0811.setTextColor(formatColor)
//            binding.textViewRow0911.setTextColor(formatColor)
//            binding.textViewRow1011.setTextColor(formatColor)
//            binding.textViewRow1111.setTextColor(formatColor)
//            binding.textViewRow1211.setTextColor(formatColor)
//            binding.textViewRow0011.setTypeface(binding.textViewRow0011.typeface,formatStyle)
//            binding.textViewRow0111.setTypeface(binding.textViewRow0111.typeface,formatStyle)
//            binding.textViewRow0211.setTypeface(binding.textViewRow0211.typeface,formatStyle)
//            binding.textViewRow0311.setTypeface(binding.textViewRow0311.typeface,formatStyle)
//            binding.textViewRow0411.setTypeface(binding.textViewRow0411.typeface,formatStyle)
//            binding.textViewRow0511.setTypeface(binding.textViewRow0511.typeface,formatStyle)
//            binding.textViewRow0611.setTypeface(binding.textViewRow0611.typeface,formatStyle)
//            binding.textViewRow0711.setTypeface(binding.textViewRow0711.typeface,formatStyle)
//            binding.textViewRow0811.setTypeface(binding.textViewRow0811.typeface,formatStyle)
//            binding.textViewRow0911.setTypeface(binding.textViewRow0911.typeface,formatStyle)
//            binding.textViewRow1011.setTypeface(binding.textViewRow1011.typeface,formatStyle)
//            binding.textViewRow1111.setTypeface(binding.textViewRow1111.typeface,formatStyle)
//            binding.textViewRow1211.setTypeface(binding.textViewRow1211.typeface,formatStyle)
//        } else if (section==11) {
//            binding.textViewRow0012.setTextColor(formatColor)
//            binding.textViewRow0112.setTextColor(formatColor)
//            binding.textViewRow0212.setTextColor(formatColor)
//            binding.textViewRow0312.setTextColor(formatColor)
//            binding.textViewRow0412.setTextColor(formatColor)
//            binding.textViewRow0512.setTextColor(formatColor)
//            binding.textViewRow0612.setTextColor(formatColor)
//            binding.textViewRow0712.setTextColor(formatColor)
//            binding.textViewRow0812.setTextColor(formatColor)
//            binding.textViewRow0912.setTextColor(formatColor)
//            binding.textViewRow1012.setTextColor(formatColor)
//            binding.textViewRow1112.setTextColor(formatColor)
//            binding.textViewRow1212.setTextColor(formatColor)
//            binding.textViewRow0012.setTypeface(binding.textViewRow0012.typeface,formatStyle)
//            binding.textViewRow0112.setTypeface(binding.textViewRow0112.typeface,formatStyle)
//            binding.textViewRow0212.setTypeface(binding.textViewRow0212.typeface,formatStyle)
//            binding.textViewRow0312.setTypeface(binding.textViewRow0312.typeface,formatStyle)
//            binding.textViewRow0412.setTypeface(binding.textViewRow0412.typeface,formatStyle)
//            binding.textViewRow0512.setTypeface(binding.textViewRow0512.typeface,formatStyle)
//            binding.textViewRow0612.setTypeface(binding.textViewRow0612.typeface,formatStyle)
//            binding.textViewRow0712.setTypeface(binding.textViewRow0712.typeface,formatStyle)
//            binding.textViewRow0812.setTypeface(binding.textViewRow0812.typeface,formatStyle)
//            binding.textViewRow0912.setTypeface(binding.textViewRow0912.typeface,formatStyle)
//            binding.textViewRow1012.setTypeface(binding.textViewRow1012.typeface,formatStyle)
//            binding.textViewRow1112.setTypeface(binding.textViewRow1112.typeface,formatStyle)
//            binding.textViewRow1212.setTypeface(binding.textViewRow1212.typeface,formatStyle)
//        } else if (section==12) {
//            binding.textViewRow0013.setTextColor(formatColor)
//            binding.textViewRow0113.setTextColor(formatColor)
//            binding.textViewRow0213.setTextColor(formatColor)
//            binding.textViewRow0313.setTextColor(formatColor)
//            binding.textViewRow0413.setTextColor(formatColor)
//            binding.textViewRow0513.setTextColor(formatColor)
//            binding.textViewRow0613.setTextColor(formatColor)
//            binding.textViewRow0713.setTextColor(formatColor)
//            binding.textViewRow0813.setTextColor(formatColor)
//            binding.textViewRow0913.setTextColor(formatColor)
//            binding.textViewRow1013.setTextColor(formatColor)
//            binding.textViewRow1113.setTextColor(formatColor)
//            binding.textViewRow1213.setTextColor(formatColor)
//            binding.textViewRow0013.setTypeface(binding.textViewRow0013.typeface,formatStyle)
//            binding.textViewRow0113.setTypeface(binding.textViewRow0113.typeface,formatStyle)
//            binding.textViewRow0213.setTypeface(binding.textViewRow0213.typeface,formatStyle)
//            binding.textViewRow0313.setTypeface(binding.textViewRow0313.typeface,formatStyle)
//            binding.textViewRow0413.setTypeface(binding.textViewRow0413.typeface,formatStyle)
//            binding.textViewRow0513.setTypeface(binding.textViewRow0513.typeface,formatStyle)
//            binding.textViewRow0613.setTypeface(binding.textViewRow0613.typeface,formatStyle)
//            binding.textViewRow0713.setTypeface(binding.textViewRow0713.typeface,formatStyle)
//            binding.textViewRow0813.setTypeface(binding.textViewRow0813.typeface,formatStyle)
//            binding.textViewRow0913.setTypeface(binding.textViewRow0913.typeface,formatStyle)
//            binding.textViewRow1013.setTypeface(binding.textViewRow1013.typeface,formatStyle)
//            binding.textViewRow1113.setTypeface(binding.textViewRow1113.typeface,formatStyle)
//            binding.textViewRow1213.setTypeface(binding.textViewRow1213.typeface,formatStyle)
//        } else if (section==13) {
//            binding.textViewRow0014.setTextColor(formatColor)
//            binding.textViewRow0114.setTextColor(formatColor)
//            binding.textViewRow0214.setTextColor(formatColor)
//            binding.textViewRow0314.setTextColor(formatColor)
//            binding.textViewRow0414.setTextColor(formatColor)
//            binding.textViewRow0514.setTextColor(formatColor)
//            binding.textViewRow0614.setTextColor(formatColor)
//            binding.textViewRow0714.setTextColor(formatColor)
//            binding.textViewRow0814.setTextColor(formatColor)
//            binding.textViewRow0914.setTextColor(formatColor)
//            binding.textViewRow1014.setTextColor(formatColor)
//            binding.textViewRow1114.setTextColor(formatColor)
//            binding.textViewRow1214.setTextColor(formatColor)
//            binding.textViewRow0014.setTypeface(binding.textViewRow0014.typeface,formatStyle)
//            binding.textViewRow0114.setTypeface(binding.textViewRow0114.typeface,formatStyle)
//            binding.textViewRow0214.setTypeface(binding.textViewRow0214.typeface,formatStyle)
//            binding.textViewRow0314.setTypeface(binding.textViewRow0314.typeface,formatStyle)
//            binding.textViewRow0414.setTypeface(binding.textViewRow0414.typeface,formatStyle)
//            binding.textViewRow0514.setTypeface(binding.textViewRow0514.typeface,formatStyle)
//            binding.textViewRow0614.setTypeface(binding.textViewRow0614.typeface,formatStyle)
//            binding.textViewRow0714.setTypeface(binding.textViewRow0714.typeface,formatStyle)
//            binding.textViewRow0814.setTypeface(binding.textViewRow0814.typeface,formatStyle)
//            binding.textViewRow0914.setTypeface(binding.textViewRow0914.typeface,formatStyle)
//            binding.textViewRow1014.setTypeface(binding.textViewRow1014.typeface,formatStyle)
//            binding.textViewRow1114.setTypeface(binding.textViewRow1114.typeface,formatStyle)
//            binding.textViewRow1214.setTypeface(binding.textViewRow1214.typeface,formatStyle)
//        } else if (section==14) {
//            binding.textViewRow0015.setTextColor(formatColor)
//            binding.textViewRow0115.setTextColor(formatColor)
//            binding.textViewRow0215.setTextColor(formatColor)
//            binding.textViewRow0315.setTextColor(formatColor)
//            binding.textViewRow0415.setTextColor(formatColor)
//            binding.textViewRow0515.setTextColor(formatColor)
//            binding.textViewRow0615.setTextColor(formatColor)
//            binding.textViewRow0715.setTextColor(formatColor)
//            binding.textViewRow0815.setTextColor(formatColor)
//            binding.textViewRow0915.setTextColor(formatColor)
//            binding.textViewRow1015.setTextColor(formatColor)
//            binding.textViewRow1115.setTextColor(formatColor)
//            binding.textViewRow1215.setTextColor(formatColor)
//            binding.textViewRow0015.setTypeface(binding.textViewRow0015.typeface,formatStyle)
//            binding.textViewRow0115.setTypeface(binding.textViewRow0115.typeface,formatStyle)
//            binding.textViewRow0215.setTypeface(binding.textViewRow0215.typeface,formatStyle)
//            binding.textViewRow0315.setTypeface(binding.textViewRow0315.typeface,formatStyle)
//            binding.textViewRow0415.setTypeface(binding.textViewRow0415.typeface,formatStyle)
//            binding.textViewRow0515.setTypeface(binding.textViewRow0515.typeface,formatStyle)
//            binding.textViewRow0615.setTypeface(binding.textViewRow0615.typeface,formatStyle)
//            binding.textViewRow0715.setTypeface(binding.textViewRow0715.typeface,formatStyle)
//            binding.textViewRow0815.setTypeface(binding.textViewRow0815.typeface,formatStyle)
//            binding.textViewRow0915.setTypeface(binding.textViewRow0915.typeface,formatStyle)
//            binding.textViewRow1015.setTypeface(binding.textViewRow1015.typeface,formatStyle)
//            binding.textViewRow1115.setTypeface(binding.textViewRow1115.typeface,formatStyle)
//            binding.textViewRow1215.setTypeface(binding.textViewRow1215.typeface,formatStyle)
//        } else if (section==15) {
//            binding.textViewRow0016.setTextColor(formatColor)
//            binding.textViewRow0116.setTextColor(formatColor)
//            binding.textViewRow0216.setTextColor(formatColor)
//            binding.textViewRow0316.setTextColor(formatColor)
//            binding.textViewRow0416.setTextColor(formatColor)
//            binding.textViewRow0516.setTextColor(formatColor)
//            binding.textViewRow0616.setTextColor(formatColor)
//            binding.textViewRow0716.setTextColor(formatColor)
//            binding.textViewRow0816.setTextColor(formatColor)
//            binding.textViewRow0916.setTextColor(formatColor)
//            binding.textViewRow1016.setTextColor(formatColor)
//            binding.textViewRow1116.setTextColor(formatColor)
//            binding.textViewRow1216.setTextColor(formatColor)
//            binding.textViewRow0016.setTypeface(binding.textViewRow0016.typeface,formatStyle)
//            binding.textViewRow0116.setTypeface(binding.textViewRow0116.typeface,formatStyle)
//            binding.textViewRow0216.setTypeface(binding.textViewRow0216.typeface,formatStyle)
//            binding.textViewRow0316.setTypeface(binding.textViewRow0316.typeface,formatStyle)
//            binding.textViewRow0416.setTypeface(binding.textViewRow0416.typeface,formatStyle)
//            binding.textViewRow0516.setTypeface(binding.textViewRow0516.typeface,formatStyle)
//            binding.textViewRow0616.setTypeface(binding.textViewRow0616.typeface,formatStyle)
//            binding.textViewRow0716.setTypeface(binding.textViewRow0716.typeface,formatStyle)
//            binding.textViewRow0816.setTypeface(binding.textViewRow0816.typeface,formatStyle)
//            binding.textViewRow0916.setTypeface(binding.textViewRow0916.typeface,formatStyle)
//            binding.textViewRow1016.setTypeface(binding.textViewRow1016.typeface,formatStyle)
//            binding.textViewRow1116.setTypeface(binding.textViewRow1116.typeface,formatStyle)
//            binding.textViewRow1216.setTypeface(binding.textViewRow1216.typeface,formatStyle)
//        } else if (section==16) {
//            binding.textViewRow0017.setTextColor(formatColor)
//            binding.textViewRow0117.setTextColor(formatColor)
//            binding.textViewRow0217.setTextColor(formatColor)
//            binding.textViewRow0317.setTextColor(formatColor)
//            binding.textViewRow0417.setTextColor(formatColor)
//            binding.textViewRow0517.setTextColor(formatColor)
//            binding.textViewRow0617.setTextColor(formatColor)
//            binding.textViewRow0717.setTextColor(formatColor)
//            binding.textViewRow0817.setTextColor(formatColor)
//            binding.textViewRow0917.setTextColor(formatColor)
//            binding.textViewRow1017.setTextColor(formatColor)
//            binding.textViewRow1117.setTextColor(formatColor)
//            binding.textViewRow1217.setTextColor(formatColor)
//            binding.textViewRow0017.setTypeface(binding.textViewRow0017.typeface,formatStyle)
//            binding.textViewRow0117.setTypeface(binding.textViewRow0117.typeface,formatStyle)
//            binding.textViewRow0217.setTypeface(binding.textViewRow0217.typeface,formatStyle)
//            binding.textViewRow0317.setTypeface(binding.textViewRow0317.typeface,formatStyle)
//            binding.textViewRow0417.setTypeface(binding.textViewRow0417.typeface,formatStyle)
//            binding.textViewRow0517.setTypeface(binding.textViewRow0517.typeface,formatStyle)
//            binding.textViewRow0617.setTypeface(binding.textViewRow0617.typeface,formatStyle)
//            binding.textViewRow0717.setTypeface(binding.textViewRow0717.typeface,formatStyle)
//            binding.textViewRow0817.setTypeface(binding.textViewRow0817.typeface,formatStyle)
//            binding.textViewRow0917.setTypeface(binding.textViewRow0917.typeface,formatStyle)
//            binding.textViewRow1017.setTypeface(binding.textViewRow1017.typeface,formatStyle)
//            binding.textViewRow1117.setTypeface(binding.textViewRow1117.typeface,formatStyle)
//            binding.textViewRow1217.setTypeface(binding.textViewRow1217.typeface,formatStyle)
//        } else if (section==17) {
//            binding.textViewRow0018.setTextColor(formatColor)
//            binding.textViewRow0118.setTextColor(formatColor)
//            binding.textViewRow0218.setTextColor(formatColor)
//            binding.textViewRow0318.setTextColor(formatColor)
//            binding.textViewRow0418.setTextColor(formatColor)
//            binding.textViewRow0518.setTextColor(formatColor)
//            binding.textViewRow0618.setTextColor(formatColor)
//            binding.textViewRow0718.setTextColor(formatColor)
//            binding.textViewRow0818.setTextColor(formatColor)
//            binding.textViewRow0918.setTextColor(formatColor)
//            binding.textViewRow1018.setTextColor(formatColor)
//            binding.textViewRow1118.setTextColor(formatColor)
//            binding.textViewRow1218.setTextColor(formatColor)
//            binding.textViewRow0018.setTypeface(binding.textViewRow0018.typeface,formatStyle)
//            binding.textViewRow0118.setTypeface(binding.textViewRow0118.typeface,formatStyle)
//            binding.textViewRow0218.setTypeface(binding.textViewRow0218.typeface,formatStyle)
//            binding.textViewRow0318.setTypeface(binding.textViewRow0318.typeface,formatStyle)
//            binding.textViewRow0418.setTypeface(binding.textViewRow0418.typeface,formatStyle)
//            binding.textViewRow0518.setTypeface(binding.textViewRow0518.typeface,formatStyle)
//            binding.textViewRow0618.setTypeface(binding.textViewRow0618.typeface,formatStyle)
//            binding.textViewRow0718.setTypeface(binding.textViewRow0718.typeface,formatStyle)
//            binding.textViewRow0818.setTypeface(binding.textViewRow0818.typeface,formatStyle)
//            binding.textViewRow0918.setTypeface(binding.textViewRow0918.typeface,formatStyle)
//            binding.textViewRow1018.setTypeface(binding.textViewRow1018.typeface,formatStyle)
//            binding.textViewRow1118.setTypeface(binding.textViewRow1118.typeface,formatStyle)
//            binding.textViewRow1218.setTypeface(binding.textViewRow1218.typeface,formatStyle)
//        } else if (section==18) {
//            binding.textViewRow0019.setTextColor(formatColor)
//            binding.textViewRow0119.setTextColor(formatColor)
//            binding.textViewRow0219.setTextColor(formatColor)
//            binding.textViewRow0319.setTextColor(formatColor)
//            binding.textViewRow0419.setTextColor(formatColor)
//            binding.textViewRow0519.setTextColor(formatColor)
//            binding.textViewRow0619.setTextColor(formatColor)
//            binding.textViewRow0719.setTextColor(formatColor)
//            binding.textViewRow0819.setTextColor(formatColor)
//            binding.textViewRow0919.setTextColor(formatColor)
//            binding.textViewRow1019.setTextColor(formatColor)
//            binding.textViewRow1119.setTextColor(formatColor)
//            binding.textViewRow1219.setTextColor(formatColor)
//            binding.textViewRow0019.setTypeface(binding.textViewRow0019.typeface,formatStyle)
//            binding.textViewRow0119.setTypeface(binding.textViewRow0119.typeface,formatStyle)
//            binding.textViewRow0219.setTypeface(binding.textViewRow0219.typeface,formatStyle)
//            binding.textViewRow0319.setTypeface(binding.textViewRow0319.typeface,formatStyle)
//            binding.textViewRow0419.setTypeface(binding.textViewRow0419.typeface,formatStyle)
//            binding.textViewRow0519.setTypeface(binding.textViewRow0519.typeface,formatStyle)
//            binding.textViewRow0619.setTypeface(binding.textViewRow0619.typeface,formatStyle)
//            binding.textViewRow0719.setTypeface(binding.textViewRow0719.typeface,formatStyle)
//            binding.textViewRow0819.setTypeface(binding.textViewRow0819.typeface,formatStyle)
//            binding.textViewRow0919.setTypeface(binding.textViewRow0919.typeface,formatStyle)
//            binding.textViewRow1019.setTypeface(binding.textViewRow1019.typeface,formatStyle)
//            binding.textViewRow1119.setTypeface(binding.textViewRow1119.typeface,formatStyle)
//            binding.textViewRow1219.setTypeface(binding.textViewRow1219.typeface,formatStyle)
//        } else if (section==19) {
//            binding.textViewRow0020.setTextColor(formatColor)
//            binding.textViewRow0120.setTextColor(formatColor)
//            binding.textViewRow0220.setTextColor(formatColor)
//            binding.textViewRow0320.setTextColor(formatColor)
//            binding.textViewRow0420.setTextColor(formatColor)
//            binding.textViewRow0520.setTextColor(formatColor)
//            binding.textViewRow0620.setTextColor(formatColor)
//            binding.textViewRow0720.setTextColor(formatColor)
//            binding.textViewRow0820.setTextColor(formatColor)
//            binding.textViewRow0920.setTextColor(formatColor)
//            binding.textViewRow1020.setTextColor(formatColor)
//            binding.textViewRow1120.setTextColor(formatColor)
//            binding.textViewRow1220.setTextColor(formatColor)
//            binding.textViewRow0020.setTypeface(binding.textViewRow0020.typeface,formatStyle)
//            binding.textViewRow0120.setTypeface(binding.textViewRow0120.typeface,formatStyle)
//            binding.textViewRow0220.setTypeface(binding.textViewRow0220.typeface,formatStyle)
//            binding.textViewRow0320.setTypeface(binding.textViewRow0320.typeface,formatStyle)
//            binding.textViewRow0420.setTypeface(binding.textViewRow0420.typeface,formatStyle)
//            binding.textViewRow0520.setTypeface(binding.textViewRow0520.typeface,formatStyle)
//            binding.textViewRow0620.setTypeface(binding.textViewRow0620.typeface,formatStyle)
//            binding.textViewRow0720.setTypeface(binding.textViewRow0720.typeface,formatStyle)
//            binding.textViewRow0820.setTypeface(binding.textViewRow0820.typeface,formatStyle)
//            binding.textViewRow0920.setTypeface(binding.textViewRow0920.typeface,formatStyle)
//            binding.textViewRow1020.setTypeface(binding.textViewRow1020.typeface,formatStyle)
//            binding.textViewRow1120.setTypeface(binding.textViewRow1120.typeface,formatStyle)
//            binding.textViewRow1220.setTypeface(binding.textViewRow1220.typeface,formatStyle)
//        } else if (section==20) {
//            binding.textViewRow0021.setTextColor(formatColor)
//            binding.textViewRow0121.setTextColor(formatColor)
//            binding.textViewRow0221.setTextColor(formatColor)
//            binding.textViewRow0321.setTextColor(formatColor)
//            binding.textViewRow0421.setTextColor(formatColor)
//            binding.textViewRow0521.setTextColor(formatColor)
//            binding.textViewRow0621.setTextColor(formatColor)
//            binding.textViewRow0721.setTextColor(formatColor)
//            binding.textViewRow0821.setTextColor(formatColor)
//            binding.textViewRow0921.setTextColor(formatColor)
//            binding.textViewRow1021.setTextColor(formatColor)
//            binding.textViewRow1121.setTextColor(formatColor)
//            binding.textViewRow1221.setTextColor(formatColor)
//            binding.textViewRow0021.setTypeface(binding.textViewRow0021.typeface,formatStyle)
//            binding.textViewRow0121.setTypeface(binding.textViewRow0121.typeface,formatStyle)
//            binding.textViewRow0221.setTypeface(binding.textViewRow0221.typeface,formatStyle)
//            binding.textViewRow0321.setTypeface(binding.textViewRow0321.typeface,formatStyle)
//            binding.textViewRow0421.setTypeface(binding.textViewRow0421.typeface,formatStyle)
//            binding.textViewRow0521.setTypeface(binding.textViewRow0521.typeface,formatStyle)
//            binding.textViewRow0621.setTypeface(binding.textViewRow0621.typeface,formatStyle)
//            binding.textViewRow0721.setTypeface(binding.textViewRow0721.typeface,formatStyle)
//            binding.textViewRow0821.setTypeface(binding.textViewRow0821.typeface,formatStyle)
//            binding.textViewRow0921.setTypeface(binding.textViewRow0921.typeface,formatStyle)
//            binding.textViewRow1021.setTypeface(binding.textViewRow1021.typeface,formatStyle)
//            binding.textViewRow1121.setTypeface(binding.textViewRow1121.typeface,formatStyle)
//            binding.textViewRow1221.setTypeface(binding.textViewRow1221.typeface,formatStyle)
//        } else if (section==21) {
//            binding.textViewRow0022.setTextColor(formatColor)
//            binding.textViewRow0122.setTextColor(formatColor)
//            binding.textViewRow0222.setTextColor(formatColor)
//            binding.textViewRow0322.setTextColor(formatColor)
//            binding.textViewRow0422.setTextColor(formatColor)
//            binding.textViewRow0522.setTextColor(formatColor)
//            binding.textViewRow0622.setTextColor(formatColor)
//            binding.textViewRow0722.setTextColor(formatColor)
//            binding.textViewRow0822.setTextColor(formatColor)
//            binding.textViewRow0922.setTextColor(formatColor)
//            binding.textViewRow1022.setTextColor(formatColor)
//            binding.textViewRow1122.setTextColor(formatColor)
//            binding.textViewRow1222.setTextColor(formatColor)
//            binding.textViewRow0022.setTypeface(binding.textViewRow0021.typeface,formatStyle)
//            binding.textViewRow0122.setTypeface(binding.textViewRow0021.typeface,formatStyle)
//            binding.textViewRow0222.setTypeface(binding.textViewRow0121.typeface,formatStyle)
//            binding.textViewRow0322.setTypeface(binding.textViewRow0221.typeface,formatStyle)
//            binding.textViewRow0422.setTypeface(binding.textViewRow0321.typeface,formatStyle)
//            binding.textViewRow0522.setTypeface(binding.textViewRow0421.typeface,formatStyle)
//            binding.textViewRow0622.setTypeface(binding.textViewRow0521.typeface,formatStyle)
//            binding.textViewRow0722.setTypeface(binding.textViewRow0621.typeface,formatStyle)
//            binding.textViewRow0822.setTypeface(binding.textViewRow0721.typeface,formatStyle)
//            binding.textViewRow0922.setTypeface(binding.textViewRow0821.typeface,formatStyle)
//            binding.textViewRow1022.setTypeface(binding.textViewRow0921.typeface,formatStyle)
//            binding.textViewRow1122.setTypeface(binding.textViewRow1021.typeface,formatStyle)
//            binding.textViewRow1222.setTypeface(binding.textViewRow1121.typeface,formatStyle)
//        } else if (section==22) {
//            binding.textViewRow0023.setTextColor(formatColor)
//            binding.textViewRow0123.setTextColor(formatColor)
//            binding.textViewRow0223.setTextColor(formatColor)
//            binding.textViewRow0423.setTextColor(formatColor)
//            binding.textViewRow0523.setTextColor(formatColor)
//            binding.textViewRow0623.setTextColor(formatColor)
//            binding.textViewRow0323.setTextColor(formatColor)
//            binding.textViewRow0723.setTextColor(formatColor)
//            binding.textViewRow0823.setTextColor(formatColor)
//            binding.textViewRow0923.setTextColor(formatColor)
//            binding.textViewRow1023.setTextColor(formatColor)
//            binding.textViewRow1123.setTextColor(formatColor)
//            binding.textViewRow1223.setTextColor(formatColor)
//            binding.textViewRow0023.setTypeface(binding.textViewRow0021.typeface,formatStyle)
//            binding.textViewRow0123.setTypeface(binding.textViewRow0121.typeface,formatStyle)
//            binding.textViewRow0223.setTypeface(binding.textViewRow0221.typeface,formatStyle)
//            binding.textViewRow0423.setTypeface(binding.textViewRow0321.typeface,formatStyle)
//            binding.textViewRow0523.setTypeface(binding.textViewRow0421.typeface,formatStyle)
//            binding.textViewRow0623.setTypeface(binding.textViewRow0521.typeface,formatStyle)
//            binding.textViewRow0323.setTypeface(binding.textViewRow0621.typeface,formatStyle)
//            binding.textViewRow0723.setTypeface(binding.textViewRow0721.typeface,formatStyle)
//            binding.textViewRow0823.setTypeface(binding.textViewRow0821.typeface,formatStyle)
//            binding.textViewRow0923.setTypeface(binding.textViewRow0921.typeface,formatStyle)
//            binding.textViewRow1023.setTypeface(binding.textViewRow1021.typeface,formatStyle)
//            binding.textViewRow1123.setTypeface(binding.textViewRow1121.typeface,formatStyle)
//            binding.textViewRow1223.setTypeface(binding.textViewRow1221.typeface,formatStyle)
//        }
//    }

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


//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment FragmentCSIResult.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//                FragmentCSIResult().apply {
//                    arguments = Bundle().apply {
//                        putString(ARG_PARAM1, param1)
//                        putString(ARG_PARAM2, param2)
//                    }
//                }
//    }
}
