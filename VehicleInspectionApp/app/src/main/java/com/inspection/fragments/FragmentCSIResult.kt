package com.inspection.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
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
    var formatColor = Color.BLACK
    var formatStyle = Typeface.NORMAL

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
                var strQuarter = if (get(it).Quarter.equals("99")) "YTD" else "Q"+get(it).Quarter
                if (get(it).Month.toInt().monthNoToName().equals("") || strQuarter.equals("YTD") ){
                    formatColor = Color.BLUE
                    formatStyle = Typeface.BOLD
                } else {
                    formatColor = Color.BLACK
                    formatStyle = Typeface.NORMAL
                }
                if (it==0) {
                    textViewRow0001.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==1) {
                    textViewRow0002.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==2) {
                    textViewRow0003.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==3) {
                    textViewRow0004.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==4) {
                    textViewRow0005.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==5) {
                    textViewRow0006.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==6) {
                    textViewRow0007.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==7) {
                    textViewRow0008.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==8) {
                    textViewRow0009.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==9) {
                    textViewRow0010.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==10) {
                    textViewRow0011.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==11) {
                    textViewRow0012.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==12) {
                    textViewRow0013.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==13) {
                    textViewRow0014.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==14) {
                    textViewRow0015.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==15) {
                    textViewRow0016.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==16) {
                    textViewRow0017.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==17) {
                    textViewRow0018.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==18) {
                    textViewRow0019.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==19) {
                    textViewRow0020.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==20) {
                    textViewRow0021.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==21) {
                    textViewRow0022.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
                if (it==22) {
                    textViewRow0023.text = if (get(it).Month.toInt().monthNoToName().equals("")) strQuarter else get(it).Month.toInt().monthNoToName().substring(0,3)
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
                    formatTexts(it)
                }
            }
        }
    }

    fun formatTexts(section : Int){
        if (section==0) {
            textViewRow0001.setTextColor(formatColor)
            textViewRow0101.setTextColor(formatColor)
            textViewRow0201.setTextColor(formatColor)
            textViewRow0301.setTextColor(formatColor)
            textViewRow0401.setTextColor(formatColor)
            textViewRow0501.setTextColor(formatColor)
            textViewRow0601.setTextColor(formatColor)
            textViewRow0701.setTextColor(formatColor)
            textViewRow0801.setTextColor(formatColor)
            textViewRow0901.setTextColor(formatColor)
            textViewRow1001.setTextColor(formatColor)
            textViewRow1101.setTextColor(formatColor)
            textViewRow1201.setTextColor(formatColor)
            textViewRow0001.setTypeface(textViewRow0001.typeface,formatStyle)
            textViewRow0101.setTypeface(textViewRow0101.typeface,formatStyle)
            textViewRow0201.setTypeface(textViewRow0201.typeface,formatStyle)
            textViewRow0301.setTypeface(textViewRow0301.typeface,formatStyle)
            textViewRow0401.setTypeface(textViewRow0401.typeface,formatStyle)
            textViewRow0501.setTypeface(textViewRow0501.typeface,formatStyle)
            textViewRow0601.setTypeface(textViewRow0601.typeface,formatStyle)
            textViewRow0701.setTypeface(textViewRow0701.typeface,formatStyle)
            textViewRow0801.setTypeface(textViewRow0801.typeface,formatStyle)
            textViewRow0901.setTypeface(textViewRow0901.typeface,formatStyle)
            textViewRow1001.setTypeface(textViewRow1001.typeface,formatStyle)
            textViewRow1101.setTypeface(textViewRow1101.typeface,formatStyle)
            textViewRow1201.setTypeface(textViewRow1201.typeface,formatStyle)
        } else if (section==1) {
            textViewRow0002.setTextColor(formatColor)
            textViewRow0102.setTextColor(formatColor)
            textViewRow0202.setTextColor(formatColor)
            textViewRow0302.setTextColor(formatColor)
            textViewRow0402.setTextColor(formatColor)
            textViewRow0502.setTextColor(formatColor)
            textViewRow0602.setTextColor(formatColor)
            textViewRow0702.setTextColor(formatColor)
            textViewRow0802.setTextColor(formatColor)
            textViewRow0902.setTextColor(formatColor)
            textViewRow1002.setTextColor(formatColor)
            textViewRow1102.setTextColor(formatColor)
            textViewRow1202.setTextColor(formatColor)
            textViewRow0002.setTypeface(textViewRow0002.typeface,formatStyle)
            textViewRow0102.setTypeface(textViewRow0102.typeface,formatStyle)
            textViewRow0202.setTypeface(textViewRow0202.typeface,formatStyle)
            textViewRow0302.setTypeface(textViewRow0302.typeface,formatStyle)
            textViewRow0402.setTypeface(textViewRow0402.typeface,formatStyle)
            textViewRow0502.setTypeface(textViewRow0502.typeface,formatStyle)
            textViewRow0602.setTypeface(textViewRow0602.typeface,formatStyle)
            textViewRow0702.setTypeface(textViewRow0702.typeface,formatStyle)
            textViewRow0802.setTypeface(textViewRow0802.typeface,formatStyle)
            textViewRow0902.setTypeface(textViewRow0902.typeface,formatStyle)
            textViewRow1002.setTypeface(textViewRow1002.typeface,formatStyle)
            textViewRow1102.setTypeface(textViewRow1102.typeface,formatStyle)
            textViewRow1202.setTypeface(textViewRow1202.typeface,formatStyle)
        } else if (section==2) {
            textViewRow0003.setTextColor(formatColor)
            textViewRow0103.setTextColor(formatColor)
            textViewRow0203.setTextColor(formatColor)
            textViewRow0303.setTextColor(formatColor)
            textViewRow0403.setTextColor(formatColor)
            textViewRow0503.setTextColor(formatColor)
            textViewRow0603.setTextColor(formatColor)
            textViewRow0703.setTextColor(formatColor)
            textViewRow0803.setTextColor(formatColor)
            textViewRow0903.setTextColor(formatColor)
            textViewRow1003.setTextColor(formatColor)
            textViewRow1103.setTextColor(formatColor)
            textViewRow1203.setTextColor(formatColor)
            textViewRow0003.setTypeface(textViewRow0003.typeface,formatStyle)
            textViewRow0103.setTypeface(textViewRow0103.typeface,formatStyle)
            textViewRow0203.setTypeface(textViewRow0203.typeface,formatStyle)
            textViewRow0303.setTypeface(textViewRow0303.typeface,formatStyle)
            textViewRow0403.setTypeface(textViewRow0403.typeface,formatStyle)
            textViewRow0503.setTypeface(textViewRow0503.typeface,formatStyle)
            textViewRow0603.setTypeface(textViewRow0603.typeface,formatStyle)
            textViewRow0703.setTypeface(textViewRow0703.typeface,formatStyle)
            textViewRow0803.setTypeface(textViewRow0803.typeface,formatStyle)
            textViewRow0903.setTypeface(textViewRow0903.typeface,formatStyle)
            textViewRow1003.setTypeface(textViewRow1003.typeface,formatStyle)
            textViewRow1103.setTypeface(textViewRow1103.typeface,formatStyle)
            textViewRow1203.setTypeface(textViewRow1203.typeface,formatStyle)
        } else if (section==3) {
            textViewRow0004.setTextColor(formatColor)
            textViewRow0104.setTextColor(formatColor)
            textViewRow0204.setTextColor(formatColor)
            textViewRow0304.setTextColor(formatColor)
            textViewRow0404.setTextColor(formatColor)
            textViewRow0504.setTextColor(formatColor)
            textViewRow0604.setTextColor(formatColor)
            textViewRow0704.setTextColor(formatColor)
            textViewRow0804.setTextColor(formatColor)
            textViewRow0904.setTextColor(formatColor)
            textViewRow1004.setTextColor(formatColor)
            textViewRow1104.setTextColor(formatColor)
            textViewRow1204.setTextColor(formatColor)
            textViewRow0004.setTypeface(textViewRow0004.typeface,formatStyle)
            textViewRow0104.setTypeface(textViewRow0104.typeface,formatStyle)
            textViewRow0204.setTypeface(textViewRow0204.typeface,formatStyle)
            textViewRow0304.setTypeface(textViewRow0304.typeface,formatStyle)
            textViewRow0404.setTypeface(textViewRow0404.typeface,formatStyle)
            textViewRow0504.setTypeface(textViewRow0504.typeface,formatStyle)
            textViewRow0604.setTypeface(textViewRow0604.typeface,formatStyle)
            textViewRow0704.setTypeface(textViewRow0704.typeface,formatStyle)
            textViewRow0804.setTypeface(textViewRow0804.typeface,formatStyle)
            textViewRow0904.setTypeface(textViewRow0904.typeface,formatStyle)
            textViewRow1004.setTypeface(textViewRow1004.typeface,formatStyle)
            textViewRow1104.setTypeface(textViewRow1104.typeface,formatStyle)
            textViewRow1204.setTypeface(textViewRow1204.typeface,formatStyle)
        } else if (section==4) {
            textViewRow0005.setTextColor(formatColor)
            textViewRow0105.setTextColor(formatColor)
            textViewRow0205.setTextColor(formatColor)
            textViewRow0305.setTextColor(formatColor)
            textViewRow0405.setTextColor(formatColor)
            textViewRow0505.setTextColor(formatColor)
            textViewRow0605.setTextColor(formatColor)
            textViewRow0705.setTextColor(formatColor)
            textViewRow0805.setTextColor(formatColor)
            textViewRow0905.setTextColor(formatColor)
            textViewRow1005.setTextColor(formatColor)
            textViewRow1105.setTextColor(formatColor)
            textViewRow1205.setTextColor(formatColor)
            textViewRow0005.setTypeface(textViewRow0005.typeface,formatStyle)
            textViewRow0105.setTypeface(textViewRow0105.typeface,formatStyle)
            textViewRow0205.setTypeface(textViewRow0205.typeface,formatStyle)
            textViewRow0305.setTypeface(textViewRow0305.typeface,formatStyle)
            textViewRow0405.setTypeface(textViewRow0405.typeface,formatStyle)
            textViewRow0505.setTypeface(textViewRow0505.typeface,formatStyle)
            textViewRow0605.setTypeface(textViewRow0605.typeface,formatStyle)
            textViewRow0705.setTypeface(textViewRow0705.typeface,formatStyle)
            textViewRow0805.setTypeface(textViewRow0805.typeface,formatStyle)
            textViewRow0905.setTypeface(textViewRow0905.typeface,formatStyle)
            textViewRow1005.setTypeface(textViewRow1005.typeface,formatStyle)
            textViewRow1105.setTypeface(textViewRow1105.typeface,formatStyle)
            textViewRow1205.setTypeface(textViewRow1205.typeface,formatStyle)
        } else if (section==5) {
            textViewRow0006.setTextColor(formatColor)
            textViewRow0106.setTextColor(formatColor)
            textViewRow0206.setTextColor(formatColor)
            textViewRow0306.setTextColor(formatColor)
            textViewRow0406.setTextColor(formatColor)
            textViewRow0506.setTextColor(formatColor)
            textViewRow0606.setTextColor(formatColor)
            textViewRow0706.setTextColor(formatColor)
            textViewRow0806.setTextColor(formatColor)
            textViewRow0906.setTextColor(formatColor)
            textViewRow1006.setTextColor(formatColor)
            textViewRow1106.setTextColor(formatColor)
            textViewRow1206.setTextColor(formatColor)
            textViewRow0006.setTypeface(textViewRow0006.typeface,formatStyle)
            textViewRow0106.setTypeface(textViewRow0106.typeface,formatStyle)
            textViewRow0206.setTypeface(textViewRow0206.typeface,formatStyle)
            textViewRow0306.setTypeface(textViewRow0306.typeface,formatStyle)
            textViewRow0406.setTypeface(textViewRow0406.typeface,formatStyle)
            textViewRow0506.setTypeface(textViewRow0506.typeface,formatStyle)
            textViewRow0606.setTypeface(textViewRow0606.typeface,formatStyle)
            textViewRow0706.setTypeface(textViewRow0706.typeface,formatStyle)
            textViewRow0806.setTypeface(textViewRow0806.typeface,formatStyle)
            textViewRow0906.setTypeface(textViewRow0906.typeface,formatStyle)
            textViewRow1006.setTypeface(textViewRow1006.typeface,formatStyle)
            textViewRow1106.setTypeface(textViewRow1106.typeface,formatStyle)
            textViewRow1206.setTypeface(textViewRow1206.typeface,formatStyle)
        } else if (section==6) {
            textViewRow0007.setTextColor(formatColor)
            textViewRow0107.setTextColor(formatColor)
            textViewRow0207.setTextColor(formatColor)
            textViewRow0307.setTextColor(formatColor)
            textViewRow0407.setTextColor(formatColor)
            textViewRow0507.setTextColor(formatColor)
            textViewRow0607.setTextColor(formatColor)
            textViewRow0707.setTextColor(formatColor)
            textViewRow0807.setTextColor(formatColor)
            textViewRow0907.setTextColor(formatColor)
            textViewRow1007.setTextColor(formatColor)
            textViewRow1107.setTextColor(formatColor)
            textViewRow1207.setTextColor(formatColor)
            textViewRow0007.setTypeface(textViewRow0007.typeface,formatStyle)
            textViewRow0107.setTypeface(textViewRow0107.typeface,formatStyle)
            textViewRow0207.setTypeface(textViewRow0207.typeface,formatStyle)
            textViewRow0307.setTypeface(textViewRow0307.typeface,formatStyle)
            textViewRow0407.setTypeface(textViewRow0407.typeface,formatStyle)
            textViewRow0507.setTypeface(textViewRow0507.typeface,formatStyle)
            textViewRow0607.setTypeface(textViewRow0607.typeface,formatStyle)
            textViewRow0707.setTypeface(textViewRow0707.typeface,formatStyle)
            textViewRow0807.setTypeface(textViewRow0807.typeface,formatStyle)
            textViewRow0907.setTypeface(textViewRow0907.typeface,formatStyle)
            textViewRow1007.setTypeface(textViewRow1007.typeface,formatStyle)
            textViewRow1107.setTypeface(textViewRow1107.typeface,formatStyle)
            textViewRow1207.setTypeface(textViewRow1207.typeface,formatStyle)
        } else if (section==7) {
            textViewRow0008.setTextColor(formatColor)
            textViewRow0108.setTextColor(formatColor)
            textViewRow0208.setTextColor(formatColor)
            textViewRow0308.setTextColor(formatColor)
            textViewRow0408.setTextColor(formatColor)
            textViewRow0508.setTextColor(formatColor)
            textViewRow0608.setTextColor(formatColor)
            textViewRow0708.setTextColor(formatColor)
            textViewRow0808.setTextColor(formatColor)
            textViewRow0908.setTextColor(formatColor)
            textViewRow1008.setTextColor(formatColor)
            textViewRow1108.setTextColor(formatColor)
            textViewRow1208.setTextColor(formatColor)
            textViewRow0008.setTypeface(textViewRow0008.typeface,formatStyle)
            textViewRow0108.setTypeface(textViewRow0108.typeface,formatStyle)
            textViewRow0208.setTypeface(textViewRow0208.typeface,formatStyle)
            textViewRow0308.setTypeface(textViewRow0308.typeface,formatStyle)
            textViewRow0408.setTypeface(textViewRow0408.typeface,formatStyle)
            textViewRow0508.setTypeface(textViewRow0508.typeface,formatStyle)
            textViewRow0608.setTypeface(textViewRow0608.typeface,formatStyle)
            textViewRow0708.setTypeface(textViewRow0708.typeface,formatStyle)
            textViewRow0808.setTypeface(textViewRow0808.typeface,formatStyle)
            textViewRow0908.setTypeface(textViewRow0908.typeface,formatStyle)
            textViewRow1008.setTypeface(textViewRow1008.typeface,formatStyle)
            textViewRow1108.setTypeface(textViewRow1108.typeface,formatStyle)
            textViewRow1208.setTypeface(textViewRow1208.typeface,formatStyle)
        } else if (section==8) {
            textViewRow0009.setTextColor(formatColor)
            textViewRow0109.setTextColor(formatColor)
            textViewRow0209.setTextColor(formatColor)
            textViewRow0309.setTextColor(formatColor)
            textViewRow0409.setTextColor(formatColor)
            textViewRow0509.setTextColor(formatColor)
            textViewRow0609.setTextColor(formatColor)
            textViewRow0709.setTextColor(formatColor)
            textViewRow0809.setTextColor(formatColor)
            textViewRow0909.setTextColor(formatColor)
            textViewRow1009.setTextColor(formatColor)
            textViewRow1109.setTextColor(formatColor)
            textViewRow1209.setTextColor(formatColor)
            textViewRow0009.setTypeface(textViewRow0009.typeface,formatStyle)
            textViewRow0109.setTypeface(textViewRow0109.typeface,formatStyle)
            textViewRow0209.setTypeface(textViewRow0209.typeface,formatStyle)
            textViewRow0309.setTypeface(textViewRow0309.typeface,formatStyle)
            textViewRow0409.setTypeface(textViewRow0409.typeface,formatStyle)
            textViewRow0509.setTypeface(textViewRow0509.typeface,formatStyle)
            textViewRow0609.setTypeface(textViewRow0609.typeface,formatStyle)
            textViewRow0709.setTypeface(textViewRow0709.typeface,formatStyle)
            textViewRow0809.setTypeface(textViewRow0809.typeface,formatStyle)
            textViewRow0909.setTypeface(textViewRow0909.typeface,formatStyle)
            textViewRow1009.setTypeface(textViewRow1009.typeface,formatStyle)
            textViewRow1109.setTypeface(textViewRow1109.typeface,formatStyle)
            textViewRow1209.setTypeface(textViewRow1209.typeface,formatStyle)
        } else if (section==9) {
            textViewRow0010.setTextColor(formatColor)
            textViewRow0110.setTextColor(formatColor)
            textViewRow0210.setTextColor(formatColor)
            textViewRow0310.setTextColor(formatColor)
            textViewRow0410.setTextColor(formatColor)
            textViewRow0510.setTextColor(formatColor)
            textViewRow0610.setTextColor(formatColor)
            textViewRow0710.setTextColor(formatColor)
            textViewRow0810.setTextColor(formatColor)
            textViewRow0910.setTextColor(formatColor)
            textViewRow1010.setTextColor(formatColor)
            textViewRow1110.setTextColor(formatColor)
            textViewRow1210.setTextColor(formatColor)
            textViewRow0010.setTypeface(textViewRow0010.typeface,formatStyle)
            textViewRow0110.setTypeface(textViewRow0110.typeface,formatStyle)
            textViewRow0210.setTypeface(textViewRow0210.typeface,formatStyle)
            textViewRow0310.setTypeface(textViewRow0310.typeface,formatStyle)
            textViewRow0410.setTypeface(textViewRow0410.typeface,formatStyle)
            textViewRow0510.setTypeface(textViewRow0510.typeface,formatStyle)
            textViewRow0610.setTypeface(textViewRow0610.typeface,formatStyle)
            textViewRow0710.setTypeface(textViewRow0710.typeface,formatStyle)
            textViewRow0810.setTypeface(textViewRow0810.typeface,formatStyle)
            textViewRow0910.setTypeface(textViewRow0910.typeface,formatStyle)
            textViewRow1010.setTypeface(textViewRow1010.typeface,formatStyle)
            textViewRow1110.setTypeface(textViewRow1110.typeface,formatStyle)
            textViewRow1210.setTypeface(textViewRow1210.typeface,formatStyle)
        } else if (section==10) {
            textViewRow0011.setTextColor(formatColor)
            textViewRow0111.setTextColor(formatColor)
            textViewRow0211.setTextColor(formatColor)
            textViewRow0311.setTextColor(formatColor)
            textViewRow0411.setTextColor(formatColor)
            textViewRow0511.setTextColor(formatColor)
            textViewRow0611.setTextColor(formatColor)
            textViewRow0711.setTextColor(formatColor)
            textViewRow0811.setTextColor(formatColor)
            textViewRow0911.setTextColor(formatColor)
            textViewRow1011.setTextColor(formatColor)
            textViewRow1111.setTextColor(formatColor)
            textViewRow1211.setTextColor(formatColor)
            textViewRow0011.setTypeface(textViewRow0011.typeface,formatStyle)
            textViewRow0111.setTypeface(textViewRow0111.typeface,formatStyle)
            textViewRow0211.setTypeface(textViewRow0211.typeface,formatStyle)
            textViewRow0311.setTypeface(textViewRow0311.typeface,formatStyle)
            textViewRow0411.setTypeface(textViewRow0411.typeface,formatStyle)
            textViewRow0511.setTypeface(textViewRow0511.typeface,formatStyle)
            textViewRow0611.setTypeface(textViewRow0611.typeface,formatStyle)
            textViewRow0711.setTypeface(textViewRow0711.typeface,formatStyle)
            textViewRow0811.setTypeface(textViewRow0811.typeface,formatStyle)
            textViewRow0911.setTypeface(textViewRow0911.typeface,formatStyle)
            textViewRow1011.setTypeface(textViewRow1011.typeface,formatStyle)
            textViewRow1111.setTypeface(textViewRow1111.typeface,formatStyle)
            textViewRow1211.setTypeface(textViewRow1211.typeface,formatStyle)
        } else if (section==11) {
            textViewRow0012.setTextColor(formatColor)
            textViewRow0112.setTextColor(formatColor)
            textViewRow0212.setTextColor(formatColor)
            textViewRow0312.setTextColor(formatColor)
            textViewRow0412.setTextColor(formatColor)
            textViewRow0512.setTextColor(formatColor)
            textViewRow0612.setTextColor(formatColor)
            textViewRow0712.setTextColor(formatColor)
            textViewRow0812.setTextColor(formatColor)
            textViewRow0912.setTextColor(formatColor)
            textViewRow1012.setTextColor(formatColor)
            textViewRow1112.setTextColor(formatColor)
            textViewRow1212.setTextColor(formatColor)
            textViewRow0012.setTypeface(textViewRow0012.typeface,formatStyle)
            textViewRow0112.setTypeface(textViewRow0112.typeface,formatStyle)
            textViewRow0212.setTypeface(textViewRow0212.typeface,formatStyle)
            textViewRow0312.setTypeface(textViewRow0312.typeface,formatStyle)
            textViewRow0412.setTypeface(textViewRow0412.typeface,formatStyle)
            textViewRow0512.setTypeface(textViewRow0512.typeface,formatStyle)
            textViewRow0612.setTypeface(textViewRow0612.typeface,formatStyle)
            textViewRow0712.setTypeface(textViewRow0712.typeface,formatStyle)
            textViewRow0812.setTypeface(textViewRow0812.typeface,formatStyle)
            textViewRow0912.setTypeface(textViewRow0912.typeface,formatStyle)
            textViewRow1012.setTypeface(textViewRow1012.typeface,formatStyle)
            textViewRow1112.setTypeface(textViewRow1112.typeface,formatStyle)
            textViewRow1212.setTypeface(textViewRow1212.typeface,formatStyle)
        } else if (section==12) {
            textViewRow0013.setTextColor(formatColor)
            textViewRow0113.setTextColor(formatColor)
            textViewRow0213.setTextColor(formatColor)
            textViewRow0313.setTextColor(formatColor)
            textViewRow0413.setTextColor(formatColor)
            textViewRow0513.setTextColor(formatColor)
            textViewRow0613.setTextColor(formatColor)
            textViewRow0713.setTextColor(formatColor)
            textViewRow0813.setTextColor(formatColor)
            textViewRow0913.setTextColor(formatColor)
            textViewRow1013.setTextColor(formatColor)
            textViewRow1113.setTextColor(formatColor)
            textViewRow1213.setTextColor(formatColor)
            textViewRow0013.setTypeface(textViewRow0013.typeface,formatStyle)
            textViewRow0113.setTypeface(textViewRow0113.typeface,formatStyle)
            textViewRow0213.setTypeface(textViewRow0213.typeface,formatStyle)
            textViewRow0313.setTypeface(textViewRow0313.typeface,formatStyle)
            textViewRow0413.setTypeface(textViewRow0413.typeface,formatStyle)
            textViewRow0513.setTypeface(textViewRow0513.typeface,formatStyle)
            textViewRow0613.setTypeface(textViewRow0613.typeface,formatStyle)
            textViewRow0713.setTypeface(textViewRow0713.typeface,formatStyle)
            textViewRow0813.setTypeface(textViewRow0813.typeface,formatStyle)
            textViewRow0913.setTypeface(textViewRow0913.typeface,formatStyle)
            textViewRow1013.setTypeface(textViewRow1013.typeface,formatStyle)
            textViewRow1113.setTypeface(textViewRow1113.typeface,formatStyle)
            textViewRow1213.setTypeface(textViewRow1213.typeface,formatStyle)
        } else if (section==13) {
            textViewRow0014.setTextColor(formatColor)
            textViewRow0114.setTextColor(formatColor)
            textViewRow0214.setTextColor(formatColor)
            textViewRow0314.setTextColor(formatColor)
            textViewRow0414.setTextColor(formatColor)
            textViewRow0514.setTextColor(formatColor)
            textViewRow0614.setTextColor(formatColor)
            textViewRow0714.setTextColor(formatColor)
            textViewRow0814.setTextColor(formatColor)
            textViewRow0914.setTextColor(formatColor)
            textViewRow1014.setTextColor(formatColor)
            textViewRow1114.setTextColor(formatColor)
            textViewRow1214.setTextColor(formatColor)
            textViewRow0014.setTypeface(textViewRow0014.typeface,formatStyle)
            textViewRow0114.setTypeface(textViewRow0114.typeface,formatStyle)
            textViewRow0214.setTypeface(textViewRow0214.typeface,formatStyle)
            textViewRow0314.setTypeface(textViewRow0314.typeface,formatStyle)
            textViewRow0414.setTypeface(textViewRow0414.typeface,formatStyle)
            textViewRow0514.setTypeface(textViewRow0514.typeface,formatStyle)
            textViewRow0614.setTypeface(textViewRow0614.typeface,formatStyle)
            textViewRow0714.setTypeface(textViewRow0714.typeface,formatStyle)
            textViewRow0814.setTypeface(textViewRow0814.typeface,formatStyle)
            textViewRow0914.setTypeface(textViewRow0914.typeface,formatStyle)
            textViewRow1014.setTypeface(textViewRow1014.typeface,formatStyle)
            textViewRow1114.setTypeface(textViewRow1114.typeface,formatStyle)
            textViewRow1214.setTypeface(textViewRow1214.typeface,formatStyle)
        } else if (section==14) {
            textViewRow0015.setTextColor(formatColor)
            textViewRow0115.setTextColor(formatColor)
            textViewRow0215.setTextColor(formatColor)
            textViewRow0315.setTextColor(formatColor)
            textViewRow0415.setTextColor(formatColor)
            textViewRow0515.setTextColor(formatColor)
            textViewRow0615.setTextColor(formatColor)
            textViewRow0715.setTextColor(formatColor)
            textViewRow0815.setTextColor(formatColor)
            textViewRow0915.setTextColor(formatColor)
            textViewRow1015.setTextColor(formatColor)
            textViewRow1115.setTextColor(formatColor)
            textViewRow1215.setTextColor(formatColor)
            textViewRow0015.setTypeface(textViewRow0015.typeface,formatStyle)
            textViewRow0115.setTypeface(textViewRow0115.typeface,formatStyle)
            textViewRow0215.setTypeface(textViewRow0215.typeface,formatStyle)
            textViewRow0315.setTypeface(textViewRow0315.typeface,formatStyle)
            textViewRow0415.setTypeface(textViewRow0415.typeface,formatStyle)
            textViewRow0515.setTypeface(textViewRow0515.typeface,formatStyle)
            textViewRow0615.setTypeface(textViewRow0615.typeface,formatStyle)
            textViewRow0715.setTypeface(textViewRow0715.typeface,formatStyle)
            textViewRow0815.setTypeface(textViewRow0815.typeface,formatStyle)
            textViewRow0915.setTypeface(textViewRow0915.typeface,formatStyle)
            textViewRow1015.setTypeface(textViewRow1015.typeface,formatStyle)
            textViewRow1115.setTypeface(textViewRow1115.typeface,formatStyle)
            textViewRow1215.setTypeface(textViewRow1215.typeface,formatStyle)
        } else if (section==15) {
            textViewRow0016.setTextColor(formatColor)
            textViewRow0116.setTextColor(formatColor)
            textViewRow0216.setTextColor(formatColor)
            textViewRow0316.setTextColor(formatColor)
            textViewRow0416.setTextColor(formatColor)
            textViewRow0516.setTextColor(formatColor)
            textViewRow0616.setTextColor(formatColor)
            textViewRow0716.setTextColor(formatColor)
            textViewRow0816.setTextColor(formatColor)
            textViewRow0916.setTextColor(formatColor)
            textViewRow1016.setTextColor(formatColor)
            textViewRow1116.setTextColor(formatColor)
            textViewRow1216.setTextColor(formatColor)
            textViewRow0016.setTypeface(textViewRow0016.typeface,formatStyle)
            textViewRow0116.setTypeface(textViewRow0116.typeface,formatStyle)
            textViewRow0216.setTypeface(textViewRow0216.typeface,formatStyle)
            textViewRow0316.setTypeface(textViewRow0316.typeface,formatStyle)
            textViewRow0416.setTypeface(textViewRow0416.typeface,formatStyle)
            textViewRow0516.setTypeface(textViewRow0516.typeface,formatStyle)
            textViewRow0616.setTypeface(textViewRow0616.typeface,formatStyle)
            textViewRow0716.setTypeface(textViewRow0716.typeface,formatStyle)
            textViewRow0816.setTypeface(textViewRow0816.typeface,formatStyle)
            textViewRow0916.setTypeface(textViewRow0916.typeface,formatStyle)
            textViewRow1016.setTypeface(textViewRow1016.typeface,formatStyle)
            textViewRow1116.setTypeface(textViewRow1116.typeface,formatStyle)
            textViewRow1216.setTypeface(textViewRow1216.typeface,formatStyle)
        } else if (section==16) {
            textViewRow0017.setTextColor(formatColor)
            textViewRow0117.setTextColor(formatColor)
            textViewRow0217.setTextColor(formatColor)
            textViewRow0317.setTextColor(formatColor)
            textViewRow0417.setTextColor(formatColor)
            textViewRow0517.setTextColor(formatColor)
            textViewRow0617.setTextColor(formatColor)
            textViewRow0717.setTextColor(formatColor)
            textViewRow0817.setTextColor(formatColor)
            textViewRow0917.setTextColor(formatColor)
            textViewRow1017.setTextColor(formatColor)
            textViewRow1117.setTextColor(formatColor)
            textViewRow1217.setTextColor(formatColor)
            textViewRow0017.setTypeface(textViewRow0017.typeface,formatStyle)
            textViewRow0117.setTypeface(textViewRow0117.typeface,formatStyle)
            textViewRow0217.setTypeface(textViewRow0217.typeface,formatStyle)
            textViewRow0317.setTypeface(textViewRow0317.typeface,formatStyle)
            textViewRow0417.setTypeface(textViewRow0417.typeface,formatStyle)
            textViewRow0517.setTypeface(textViewRow0517.typeface,formatStyle)
            textViewRow0617.setTypeface(textViewRow0617.typeface,formatStyle)
            textViewRow0717.setTypeface(textViewRow0717.typeface,formatStyle)
            textViewRow0817.setTypeface(textViewRow0817.typeface,formatStyle)
            textViewRow0917.setTypeface(textViewRow0917.typeface,formatStyle)
            textViewRow1017.setTypeface(textViewRow1017.typeface,formatStyle)
            textViewRow1117.setTypeface(textViewRow1117.typeface,formatStyle)
            textViewRow1217.setTypeface(textViewRow1217.typeface,formatStyle)
        } else if (section==17) {
            textViewRow0018.setTextColor(formatColor)
            textViewRow0118.setTextColor(formatColor)
            textViewRow0218.setTextColor(formatColor)
            textViewRow0318.setTextColor(formatColor)
            textViewRow0418.setTextColor(formatColor)
            textViewRow0518.setTextColor(formatColor)
            textViewRow0618.setTextColor(formatColor)
            textViewRow0718.setTextColor(formatColor)
            textViewRow0818.setTextColor(formatColor)
            textViewRow0918.setTextColor(formatColor)
            textViewRow1018.setTextColor(formatColor)
            textViewRow1118.setTextColor(formatColor)
            textViewRow1218.setTextColor(formatColor)
            textViewRow0018.setTypeface(textViewRow0018.typeface,formatStyle)
            textViewRow0118.setTypeface(textViewRow0118.typeface,formatStyle)
            textViewRow0218.setTypeface(textViewRow0218.typeface,formatStyle)
            textViewRow0318.setTypeface(textViewRow0318.typeface,formatStyle)
            textViewRow0418.setTypeface(textViewRow0418.typeface,formatStyle)
            textViewRow0518.setTypeface(textViewRow0518.typeface,formatStyle)
            textViewRow0618.setTypeface(textViewRow0618.typeface,formatStyle)
            textViewRow0718.setTypeface(textViewRow0718.typeface,formatStyle)
            textViewRow0818.setTypeface(textViewRow0818.typeface,formatStyle)
            textViewRow0918.setTypeface(textViewRow0918.typeface,formatStyle)
            textViewRow1018.setTypeface(textViewRow1018.typeface,formatStyle)
            textViewRow1118.setTypeface(textViewRow1118.typeface,formatStyle)
            textViewRow1218.setTypeface(textViewRow1218.typeface,formatStyle)
        } else if (section==18) {
            textViewRow0019.setTextColor(formatColor)
            textViewRow0119.setTextColor(formatColor)
            textViewRow0219.setTextColor(formatColor)
            textViewRow0319.setTextColor(formatColor)
            textViewRow0419.setTextColor(formatColor)
            textViewRow0519.setTextColor(formatColor)
            textViewRow0619.setTextColor(formatColor)
            textViewRow0719.setTextColor(formatColor)
            textViewRow0819.setTextColor(formatColor)
            textViewRow0919.setTextColor(formatColor)
            textViewRow1019.setTextColor(formatColor)
            textViewRow1119.setTextColor(formatColor)
            textViewRow1219.setTextColor(formatColor)
            textViewRow0019.setTypeface(textViewRow0019.typeface,formatStyle)
            textViewRow0119.setTypeface(textViewRow0119.typeface,formatStyle)
            textViewRow0219.setTypeface(textViewRow0219.typeface,formatStyle)
            textViewRow0319.setTypeface(textViewRow0319.typeface,formatStyle)
            textViewRow0419.setTypeface(textViewRow0419.typeface,formatStyle)
            textViewRow0519.setTypeface(textViewRow0519.typeface,formatStyle)
            textViewRow0619.setTypeface(textViewRow0619.typeface,formatStyle)
            textViewRow0719.setTypeface(textViewRow0719.typeface,formatStyle)
            textViewRow0819.setTypeface(textViewRow0819.typeface,formatStyle)
            textViewRow0919.setTypeface(textViewRow0919.typeface,formatStyle)
            textViewRow1019.setTypeface(textViewRow1019.typeface,formatStyle)
            textViewRow1119.setTypeface(textViewRow1119.typeface,formatStyle)
            textViewRow1219.setTypeface(textViewRow1219.typeface,formatStyle)
        } else if (section==19) {
            textViewRow0020.setTextColor(formatColor)
            textViewRow0120.setTextColor(formatColor)
            textViewRow0220.setTextColor(formatColor)
            textViewRow0320.setTextColor(formatColor)
            textViewRow0420.setTextColor(formatColor)
            textViewRow0520.setTextColor(formatColor)
            textViewRow0620.setTextColor(formatColor)
            textViewRow0720.setTextColor(formatColor)
            textViewRow0820.setTextColor(formatColor)
            textViewRow0920.setTextColor(formatColor)
            textViewRow1020.setTextColor(formatColor)
            textViewRow1120.setTextColor(formatColor)
            textViewRow1220.setTextColor(formatColor)
            textViewRow0020.setTypeface(textViewRow0020.typeface,formatStyle)
            textViewRow0120.setTypeface(textViewRow0120.typeface,formatStyle)
            textViewRow0220.setTypeface(textViewRow0220.typeface,formatStyle)
            textViewRow0320.setTypeface(textViewRow0320.typeface,formatStyle)
            textViewRow0420.setTypeface(textViewRow0420.typeface,formatStyle)
            textViewRow0520.setTypeface(textViewRow0520.typeface,formatStyle)
            textViewRow0620.setTypeface(textViewRow0620.typeface,formatStyle)
            textViewRow0720.setTypeface(textViewRow0720.typeface,formatStyle)
            textViewRow0820.setTypeface(textViewRow0820.typeface,formatStyle)
            textViewRow0920.setTypeface(textViewRow0920.typeface,formatStyle)
            textViewRow1020.setTypeface(textViewRow1020.typeface,formatStyle)
            textViewRow1120.setTypeface(textViewRow1120.typeface,formatStyle)
            textViewRow1220.setTypeface(textViewRow1220.typeface,formatStyle)
        } else if (section==20) {
            textViewRow0021.setTextColor(formatColor)
            textViewRow0121.setTextColor(formatColor)
            textViewRow0221.setTextColor(formatColor)
            textViewRow0321.setTextColor(formatColor)
            textViewRow0421.setTextColor(formatColor)
            textViewRow0521.setTextColor(formatColor)
            textViewRow0621.setTextColor(formatColor)
            textViewRow0721.setTextColor(formatColor)
            textViewRow0821.setTextColor(formatColor)
            textViewRow0921.setTextColor(formatColor)
            textViewRow1021.setTextColor(formatColor)
            textViewRow1121.setTextColor(formatColor)
            textViewRow1221.setTextColor(formatColor)
            textViewRow0021.setTypeface(textViewRow0021.typeface,formatStyle)
            textViewRow0121.setTypeface(textViewRow0121.typeface,formatStyle)
            textViewRow0221.setTypeface(textViewRow0221.typeface,formatStyle)
            textViewRow0321.setTypeface(textViewRow0321.typeface,formatStyle)
            textViewRow0421.setTypeface(textViewRow0421.typeface,formatStyle)
            textViewRow0521.setTypeface(textViewRow0521.typeface,formatStyle)
            textViewRow0621.setTypeface(textViewRow0621.typeface,formatStyle)
            textViewRow0721.setTypeface(textViewRow0721.typeface,formatStyle)
            textViewRow0821.setTypeface(textViewRow0821.typeface,formatStyle)
            textViewRow0921.setTypeface(textViewRow0921.typeface,formatStyle)
            textViewRow1021.setTypeface(textViewRow1021.typeface,formatStyle)
            textViewRow1121.setTypeface(textViewRow1121.typeface,formatStyle)
            textViewRow1221.setTypeface(textViewRow1221.typeface,formatStyle)
        } else if (section==21) {
            textViewRow0022.setTextColor(formatColor)
            textViewRow0122.setTextColor(formatColor)
            textViewRow0222.setTextColor(formatColor)
            textViewRow0322.setTextColor(formatColor)
            textViewRow0422.setTextColor(formatColor)
            textViewRow0522.setTextColor(formatColor)
            textViewRow0622.setTextColor(formatColor)
            textViewRow0722.setTextColor(formatColor)
            textViewRow0822.setTextColor(formatColor)
            textViewRow0922.setTextColor(formatColor)
            textViewRow1022.setTextColor(formatColor)
            textViewRow1122.setTextColor(formatColor)
            textViewRow1222.setTextColor(formatColor)
            textViewRow0022.setTypeface(textViewRow0021.typeface,formatStyle)
            textViewRow0122.setTypeface(textViewRow0021.typeface,formatStyle)
            textViewRow0222.setTypeface(textViewRow0121.typeface,formatStyle)
            textViewRow0322.setTypeface(textViewRow0221.typeface,formatStyle)
            textViewRow0422.setTypeface(textViewRow0321.typeface,formatStyle)
            textViewRow0522.setTypeface(textViewRow0421.typeface,formatStyle)
            textViewRow0622.setTypeface(textViewRow0521.typeface,formatStyle)
            textViewRow0722.setTypeface(textViewRow0621.typeface,formatStyle)
            textViewRow0822.setTypeface(textViewRow0721.typeface,formatStyle)
            textViewRow0922.setTypeface(textViewRow0821.typeface,formatStyle)
            textViewRow1022.setTypeface(textViewRow0921.typeface,formatStyle)
            textViewRow1122.setTypeface(textViewRow1021.typeface,formatStyle)
            textViewRow1222.setTypeface(textViewRow1121.typeface,formatStyle)
        } else if (section==22) {
            textViewRow0023.setTextColor(formatColor)
            textViewRow0123.setTextColor(formatColor)
            textViewRow0223.setTextColor(formatColor)
            textViewRow0423.setTextColor(formatColor)
            textViewRow0523.setTextColor(formatColor)
            textViewRow0623.setTextColor(formatColor)
            textViewRow0323.setTextColor(formatColor)
            textViewRow0723.setTextColor(formatColor)
            textViewRow0823.setTextColor(formatColor)
            textViewRow0923.setTextColor(formatColor)
            textViewRow1023.setTextColor(formatColor)
            textViewRow1123.setTextColor(formatColor)
            textViewRow1223.setTextColor(formatColor)
            textViewRow0023.setTypeface(textViewRow0021.typeface,formatStyle)
            textViewRow0123.setTypeface(textViewRow0121.typeface,formatStyle)
            textViewRow0223.setTypeface(textViewRow0221.typeface,formatStyle)
            textViewRow0423.setTypeface(textViewRow0321.typeface,formatStyle)
            textViewRow0523.setTypeface(textViewRow0421.typeface,formatStyle)
            textViewRow0623.setTypeface(textViewRow0521.typeface,formatStyle)
            textViewRow0323.setTypeface(textViewRow0621.typeface,formatStyle)
            textViewRow0723.setTypeface(textViewRow0721.typeface,formatStyle)
            textViewRow0823.setTypeface(textViewRow0821.typeface,formatStyle)
            textViewRow0923.setTypeface(textViewRow0921.typeface,formatStyle)
            textViewRow1023.setTypeface(textViewRow1021.typeface,formatStyle)
            textViewRow1123.setTypeface(textViewRow1121.typeface,formatStyle)
            textViewRow1223.setTypeface(textViewRow1221.typeface,formatStyle)
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
