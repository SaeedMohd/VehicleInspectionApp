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
//        FacilityDataModel.getInstance().tblAAAPortalEmailFacilityRepTable.sortedWith(compareBy{ it.Year.toInt()}).sortedWith(compareBy { it.Quarter.toInt() }).sortedWith(compareBy { it.Month.toInt() }).toCollection(sortedList)
        FacilityDataModel.getInstance().tblAAAPortalEmailFacilityRepTable.sortedWith(compareBy{ it.Year+it.Quarter+it.Month}).toCollection(sortedList)
        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1.4F
        rowLayoutParam.column = 0
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParamRow.weight=1F

        // Headers
        textViewRow0200.text = "Total\nResponses"
        textViewRow0300.text = "Repair\nOrders"
        textViewRow0400.text = "Q1:\nSatisfied"
        textViewRow0500.text = "Q7:\nReturn"
        textViewRow0600.text = "Q2:\nRepair"
        textViewRow0700.text = "Q3:\nPersonnel"
        textViewRow0800.text = "Q4:\nEstimate"
        textViewRow0900.text = "Q5:\nClean"
        textViewRow1000.text = "Q6:\nReady"
        textViewRow1100.text = "Q8:\nMember"
        textViewRow1200.text = "Q9:\nChoose"
        sortedList.apply {
            (0 until size).forEach {
                if (it==0) {
                    textViewRow0001.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0101.text = get(it).Year
                    textViewRow0201.text = get(it).Total_x0020_Responses
                    textViewRow0301.text = get(it).RO_x0020_Count
                    textViewRow0401.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0501.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0601.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0701.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0801.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0901.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1001.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1101.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1201.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==1) {
                    textViewRow0002.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0102.text = get(it).Year
                    textViewRow0202.text = get(it).Total_x0020_Responses
                    textViewRow0302.text = get(it).RO_x0020_Count
                    textViewRow0402.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0502.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0602.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0702.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0802.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0902.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1002.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1102.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1202.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==2) {
                    textViewRow0003.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0103.text = get(it).Year
                    textViewRow0203.text = get(it).Total_x0020_Responses
                    textViewRow0303.text = get(it).RO_x0020_Count
                    textViewRow0403.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0503.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0603.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0703.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0803.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0903.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1003.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1103.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1203.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==3) {
                    textViewRow0004.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0104.text = get(it).Year
                    textViewRow0204.text = get(it).Total_x0020_Responses
                    textViewRow0304.text = get(it).RO_x0020_Count
                    textViewRow0404.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0504.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0604.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0704.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0804.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0904.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1004.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1104.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1204.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==4) {
                    textViewRow0005.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0105.text = get(it).Year
                    textViewRow0205.text = get(it).Total_x0020_Responses
                    textViewRow0305.text = get(it).RO_x0020_Count
                    textViewRow0405.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0505.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0605.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0705.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0805.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0905.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1005.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1105.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1205.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==5) {
                    textViewRow0006.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0106.text = get(it).Year
                    textViewRow0206.text = get(it).Total_x0020_Responses
                    textViewRow0306.text = get(it).RO_x0020_Count
                    textViewRow0406.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0506.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0606.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0706.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0806.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0906.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1006.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1106.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1206.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==6) {
                    textViewRow0007.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0107.text = get(it).Year
                    textViewRow0207.text = get(it).Total_x0020_Responses
                    textViewRow0307.text = get(it).RO_x0020_Count
                    textViewRow0407.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0507.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0607.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0707.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0807.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0907.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1007.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1107.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1207.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==7) {
                    textViewRow0008.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0108.text = get(it).Year
                    textViewRow0208.text = get(it).Total_x0020_Responses
                    textViewRow0308.text = get(it).RO_x0020_Count
                    textViewRow0408.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0508.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0608.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0708.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0808.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0908.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1008.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1108.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1208.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==8) {
                    textViewRow0009.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0109.text = get(it).Year
                    textViewRow0209.text = get(it).Total_x0020_Responses
                    textViewRow0309.text = get(it).RO_x0020_Count
                    textViewRow0409.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0509.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0609.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0709.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0809.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0909.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1009.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1109.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1209.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==9) {
                    textViewRow0010.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0110.text = get(it).Year
                    textViewRow0210.text = get(it).Total_x0020_Responses
                    textViewRow0310.text = get(it).RO_x0020_Count
                    textViewRow0410.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0510.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0610.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0710.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0810.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0910.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1010.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1110.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1210.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==10) {
                    textViewRow0011.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0111.text = get(it).Year
                    textViewRow0211.text = get(it).Total_x0020_Responses
                    textViewRow0311.text = get(it).RO_x0020_Count
                    textViewRow0411.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0511.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0611.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0711.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0811.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0911.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1011.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1111.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1211.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==11) {
                    textViewRow0012.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0112.text = get(it).Year
                    textViewRow0212.text = get(it).Total_x0020_Responses
                    textViewRow0312.text = get(it).RO_x0020_Count
                    textViewRow0412.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0512.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0612.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0712.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0812.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0912.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1012.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1112.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1212.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==12) {
                    textViewRow0013.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0113.text = get(it).Year
                    textViewRow0213.text = get(it).Total_x0020_Responses
                    textViewRow0313.text = get(it).RO_x0020_Count
                    textViewRow0413.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0513.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0613.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0713.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0813.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0913.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1013.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1113.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1213.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==13) {
                    textViewRow0014.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0114.text = get(it).Year
                    textViewRow0214.text = get(it).Total_x0020_Responses
                    textViewRow0314.text = get(it).RO_x0020_Count
                    textViewRow0414.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0514.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0614.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0714.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0814.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0914.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1014.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1114.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1214.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==14) {
                    textViewRow0015.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0115.text = get(it).Year
                    textViewRow0215.text = get(it).Total_x0020_Responses
                    textViewRow0315.text = get(it).RO_x0020_Count
                    textViewRow0415.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0515.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0615.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0715.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0815.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0915.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1015.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1115.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1215.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==15) {
                    textViewRow0016.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0116.text = get(it).Year
                    textViewRow0216.text = get(it).Total_x0020_Responses
                    textViewRow0316.text = get(it).RO_x0020_Count
                    textViewRow0416.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0516.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0616.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0716.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0816.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0916.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1016.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1116.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1216.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==16) {
                    textViewRow0017.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0117.text = get(it).Year
                    textViewRow0217.text = get(it).Total_x0020_Responses
                    textViewRow0317.text = get(it).RO_x0020_Count
                    textViewRow0417.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0517.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0617.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0717.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0817.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0917.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1017.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1117.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1217.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==17) {
                    textViewRow0018.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0118.text = get(it).Year
                    textViewRow0218.text = get(it).Total_x0020_Responses
                    textViewRow0318.text = get(it).RO_x0020_Count
                    textViewRow0418.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0518.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0618.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0718.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0818.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0918.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1018.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1118.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1218.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==18) {
                    textViewRow0019.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0119.text = get(it).Year
                    textViewRow0219.text = get(it).Total_x0020_Responses
                    textViewRow0319.text = get(it).RO_x0020_Count
                    textViewRow0419.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0519.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0619.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0719.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0819.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0919.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1019.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1119.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1219.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==19) {
                    textViewRow0020.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0120.text = get(it).Year
                    textViewRow0220.text = get(it).Total_x0020_Responses
                    textViewRow0320.text = get(it).RO_x0020_Count
                    textViewRow0420.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0520.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0620.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0720.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0820.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0920.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1020.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1120.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1220.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==20) {
                    textViewRow0021.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0121.text = get(it).Year
                    textViewRow0221.text = get(it).Total_x0020_Responses
                    textViewRow0321.text = get(it).RO_x0020_Count
                    textViewRow0421.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0521.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0621.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0721.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0821.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0921.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1021.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1121.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1221.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==21) {
                    textViewRow0022.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0122.text = get(it).Year
                    textViewRow0222.text = get(it).Total_x0020_Responses
                    textViewRow0322.text = get(it).RO_x0020_Count
                    textViewRow0422.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0522.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0622.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0722.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0822.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0922.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1022.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1122.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1222.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
                if (it==22) {
                    textViewRow0023.text = if (get(it).Month.toInt().monthNoToName().equals("")) "Q"+get(it).Quarter else get(it).Month.toInt().monthNoToName().substring(0,3)
                    textViewRow0123.text = get(it).Year
                    textViewRow0223.text = get(it).Total_x0020_Responses
                    textViewRow0323.text = get(it).RO_x0020_Count
                    textViewRow0423.text = if (get(it).Q1_x0020_Satisfied.isNullOrEmpty()) "" else "%.1f".format(get(it).Q1_x0020_Satisfied.toFloat())
                    textViewRow0523.text = if (get(it).Q7_x0020_Return.isNullOrEmpty()) "" else "%.1f".format(get(it).Q7_x0020_Return.toFloat())
                    textViewRow0623.text = if (get(it).Q2_x0020_Repair.isNullOrEmpty()) "" else "%.1f".format(get(it).Q2_x0020_Repair.toFloat())
                    textViewRow0723.text = if (get(it).Q3_x0020_Personnel.isNullOrEmpty()) "" else "%.1f".format(get(it).Q3_x0020_Personnel.toFloat())
                    textViewRow0823.text = if (get(it).Q4_x0020_Estimate.isNullOrEmpty()) "" else "%.1f".format(get(it).Q4_x0020_Estimate.toFloat())
                    textViewRow0923.text = if (get(it).Q5_x0020_Clean.isNullOrEmpty()) "" else "%.1f".format(get(it).Q5_x0020_Clean.toFloat())
                    textViewRow1023.text = if (get(it).Q6_x0020_Ready.isNullOrEmpty()) "" else "%.1f".format(get(it).Q6_x0020_Ready.toFloat())
                    textViewRow1123.text = if (get(it).Q8_x0020_Member.isNullOrEmpty()) "" else "%.1f".format(get(it).Q8_x0020_Member.toFloat())
                    textViewRow1223.text = if (get(it).Q9_x0020_Choose.isNullOrEmpty()) "" else "%.1f".format(get(it).Q9_x0020_Choose.toFloat())
                }
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
