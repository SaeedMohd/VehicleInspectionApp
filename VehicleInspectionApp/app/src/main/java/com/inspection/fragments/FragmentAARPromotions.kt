package com.inspection.fragments

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.FormsActivity
import com.inspection.MainActivity.Companion.activity
import com.inspection.MainActivity.Companion.mContext
import com.inspection.R
import com.inspection.Utils.*
import com.inspection.model.*
import kotlinx.android.synthetic.main.fragment_aar_promotions.*
import kotlinx.android.synthetic.main.fragment_aarav_personnel.*
import kotlinx.android.synthetic.main.fragment_arrav_facility.*
import kotlinx.android.synthetic.main.fragment_arrav_facility.cancelButton
import kotlinx.android.synthetic.main.fragment_arrav_facility.progressBarText
import kotlinx.android.synthetic.main.fragment_arrav_facility.saveButton
import kotlinx.android.synthetic.main.fragment_arrav_facility.scopeOfServicesChangesDialogueLoadingView
import kotlinx.android.synthetic.main.fragment_array_vehicle_services.*
import kotlinx.android.synthetic.main.scope_of_service_group_layout.PromotionsButton
import kotlinx.android.synthetic.main.scope_of_service_group_layout.vehiclesButton


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FragmentAARPromotions : Fragment() {
    // TODO: Rename and change types of parameters
    var PromotionsListView: ExpandableHeightGridView? = null
    internal var promotionsAdapter: CustomAdapter? = null
    private var promoTypeArray = ArrayList<String>()
    var promotionListItems=ArrayList<TypeTablesModel.PromoTypeClass>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_aar_promotions, container, false)
        PromotionsListView = view.findViewById(R.id.promotionListView)
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        IndicatorsDataModel.getInstance().tblScopeOfServices[0].PromotionsVisited= true
        (activity as FormsActivity).PromotionsButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
        promotionListItems = TypeTablesModel.getInstance().PromoType
        promoTypeArray.clear()
        for (fac in promotionListItems) {
            promoTypeArray.add(fac.PromoTypeName)
        }
        setServices()
        (activity as FormsActivity).saveRequired = false
        refreshButtonsState()
    }

    fun setServices() {
        Log.v("Promotions COunt => ", FacilityDataModel.getInstance().tblPromotions.size.toString())
        promotionListItems.clear()
        PromotionsListView?.adapter = null
        promotionsAdapter = context?.let { CustomAdapter(FacilityDataModel.getInstance().tblPromotions, it,this) }
        PromotionsListView!!.adapter = promotionsAdapter

        cancelButton.setOnClickListener {
            progressBarText.text = "Cancelling ..."
            promotionsDialogueLoadingView.visibility = View.VISIBLE
            FacilityDataModel.getInstance().tblPromotions.clear()
            for (i in 0..FacilityDataModelOrg.getInstance().tblPromotions.size-1) {
                var promotionItem = TblPromotions()
                promotionItem.PromoID = FacilityDataModelOrg.getInstance().tblPromotions[i].PromoID
                promotionItem.PromoPage = FacilityDataModelOrg.getInstance().tblPromotions[i].PromoPage
                promotionItem.CouponFileName  = FacilityDataModelOrg.getInstance().tblPromotions[i].CouponFileName
                promotionItem.EffDate = FacilityDataModelOrg.getInstance().tblPromotions[i].EffDate
                promotionItem.Participant = FacilityDataModelOrg.getInstance().tblPromotions[i].Participant
                promotionItem.CouponText = FacilityDataModelOrg.getInstance().tblPromotions[i].CouponText
                promotionItem.Description = FacilityDataModelOrg.getInstance().tblPromotions[i].Description
                promotionItem.Disclaimer = FacilityDataModelOrg.getInstance().tblPromotions[i].Disclaimer
                promotionItem.ExpDate = FacilityDataModelOrg.getInstance().tblPromotions[i].ExpDate
                promotionItem.HoverText = FacilityDataModelOrg.getInstance().tblPromotions[i].HoverText
                promotionItem.ParticipantUpdateBy = FacilityDataModelOrg.getInstance().tblPromotions[i].ParticipantUpdateBy
                promotionItem.ParticipantUpdateDate = FacilityDataModelOrg.getInstance().tblPromotions[i].ParticipantUpdateDate
                promotionItem.PromoTypeName = FacilityDataModelOrg.getInstance().tblPromotions[i].PromoTypeName
                promotionItem.SearchDescription = FacilityDataModelOrg.getInstance().tblPromotions[i].SearchDescription
                promotionItem.SearchResultsHeader = FacilityDataModelOrg.getInstance().tblPromotions[i].SearchResultsHeader
                promotionItem.ToolTip = FacilityDataModelOrg.getInstance().tblPromotions[i].ToolTip
                promotionItem.UpdateBy = FacilityDataModelOrg.getInstance().tblPromotions[i].UpdateBy
                promotionItem.UpdateDate = FacilityDataModelOrg.getInstance().tblPromotions[i].UpdateDate
                promotionItem.active = FacilityDataModelOrg.getInstance().tblPromotions[i].active
                FacilityDataModel.getInstance().tblPromotions.add(promotionItem)
            }
            (activity as FormsActivity).saveRequired = false
            setServices()

            refreshButtonsState()
            Utility.showMessageDialog(activity,"Confirmation ...","Changes cancelled succesfully ---")
            progressBarText.text = "Loading ..."
            promotionsDialogueLoadingView.visibility = View.GONE
        }
        saveButton.setOnClickListener {
//            promotionsDialogueLoadingView.visibility = View.VISIBLE
//            progressBarText.text = "Saving ..."
            (activity as FormsActivity).overrideBackButton = false
            var promoID = ""
            var participantFlag = ""
            var optInDate = ""
            var optOutDate = ""
            var promoID2 = ""
            var participantFlag2 = ""
            var optInDate2 = ""
            var optOutDate2 = ""
            FacilityDataModel.getInstance().tblPromotions.apply {
                (0 until size).forEach {
                    promoID += get(it).PromoID.toString() +","
                    participantFlag += get(it).Participant +","
                    optInDate += get(it).EffDate.split("T")[0] +","
                    optOutDate += get(it).ExpDate.split("T")[0] +","
                }
            }
            promoID = promoID.substring(0,promoID.length-1)
            participantFlag = participantFlag.substring(0,participantFlag.length-1)
            optInDate = optInDate.substring(0,optInDate.length-1)
            optOutDate = optOutDate.substring(0,optOutDate.length-1)
            var urlString = "" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubcode=${FacilityDataModel.getInstance().clubCode}&promoID=${promoID}&participateFlag=${participantFlag}&optInStartDate=${optInDate}&optInEndDate=${optOutDate}&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}"
            Log.v("Update Promotions --- ", urlString)
                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.UpdateFacilityPromotions + urlString + Utility.getLoggingParameters(activity, 0, ""),
                        Response.Listener { response ->
                            activity!!.runOnUiThread {
                                if (response.toString().contains("returnCode>0<", false)) {
                                    promotionsDialogueLoadingView.visibility = View.GONE
                                    progressBarText.text = "Loading ..."
                                    FacilityDataModelOrg.getInstance().tblPromotions.clear()
                                    for (i in 0..FacilityDataModel.getInstance().tblPromotions.size-1) {
                                        var promotionItem = TblPromotions()
                                        promotionItem.PromoID = FacilityDataModel.getInstance().tblPromotions[i].PromoID
                                        promotionItem.PromoPage = FacilityDataModel.getInstance().tblPromotions[i].PromoPage
                                        promotionItem.CouponFileName = FacilityDataModel.getInstance().tblPromotions[i].CouponFileName
                                        promotionItem.EffDate = FacilityDataModel.getInstance().tblPromotions[i].EffDate
                                        promotionItem.Participant = FacilityDataModel.getInstance().tblPromotions[i].Participant
                                        promotionItem.CouponText = FacilityDataModel.getInstance().tblPromotions[i].CouponText
                                        promotionItem.Description = FacilityDataModel.getInstance().tblPromotions[i].Description
                                        promotionItem.Disclaimer = FacilityDataModel.getInstance().tblPromotions[i].Disclaimer
                                        promotionItem.ExpDate = FacilityDataModel.getInstance().tblPromotions[i].ExpDate
                                        promotionItem.HoverText = FacilityDataModel.getInstance().tblPromotions[i].HoverText
                                        promotionItem.ParticipantUpdateBy = FacilityDataModel.getInstance().tblPromotions[i].ParticipantUpdateBy
                                        promotionItem.ParticipantUpdateDate = FacilityDataModel.getInstance().tblPromotions[i].ParticipantUpdateDate
                                        promotionItem.PromoTypeName = FacilityDataModel.getInstance().tblPromotions[i].PromoTypeName
                                        promotionItem.SearchDescription = FacilityDataModel.getInstance().tblPromotions[i].SearchDescription
                                        promotionItem.SearchResultsHeader = FacilityDataModel.getInstance().tblPromotions[i].SearchResultsHeader
                                        promotionItem.ToolTip = FacilityDataModel.getInstance().tblPromotions[i].ToolTip
                                        promotionItem.UpdateBy = FacilityDataModel.getInstance().tblPromotions[i].UpdateBy
                                        promotionItem.UpdateDate = FacilityDataModel.getInstance().tblPromotions[i].UpdateDate
                                        promotionItem.active = FacilityDataModel.getInstance().tblPromotions[i].active
                                        FacilityDataModelOrg.getInstance().tblPromotions.add(promotionItem)
                                    }
                                    (activity as FormsActivity).saveDone = true
                                    Utility.showSubmitAlertDialog(activity, true, "Promotions")
                                    (activity as FormsActivity).saveRequired = false
                                    refreshButtonsState()
//                                    HasChangedModel.getInstance().checkI()
//                                    HasChangedModel.getInstance().changeDoneForSoSVehicleServices()
                                } else {
                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
                                    Utility.showSubmitAlertDialog(activity, false, "Promotions (Error: " + errorMessage + " )")
                                    promotionsDialogueLoadingView.visibility = View.GONE
                                    progressBarText.text = "Loading ..."
                                }
                            }
                        }, Response.ErrorListener {
                    promotionsDialogueLoadingView.visibility = View.GONE
                    progressBarText.text = "Loading ..."
                    Utility.showSubmitAlertDialog(activity, false, "Promotions (Error: " + it.message + " )")
                }))


//            (activity as FormsActivity).saveRequired = false
//            refreshButtonsState()
        }
    }


    fun refreshButtonsState(){
        saveButton.isEnabled = (activity as FormsActivity).saveRequired
        cancelButton.isEnabled = (activity as FormsActivity).saveRequired
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentAARPromotions.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentAARPromotions().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}

class CustomAdapter(private val dataSet: ArrayList<TblPromotions>, mContext: Context,parentFragment : FragmentAARPromotions) :
        ArrayAdapter<Any?>(mContext, R.layout.promotion_item, dataSet as List<Any?>) {
    internal var parentFragment : FragmentAARPromotions = parentFragment
    private class ViewHolder {
//        lateinit var txtName: TextView
        lateinit var checkBox: CheckBox
        lateinit var textView: TextView
    }

    override fun getCount(): Int {
        return dataSet.size
    }

    override fun getItem(position: Int): TblPromotions {
        return dataSet[position] as TblPromotions
    }

    override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
    ): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        val result: View
        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView =
                    LayoutInflater.from(parent.context).inflate(R.layout.promotion_item, parent, false)

            viewHolder.checkBox =
                    convertView.findViewById(R.id.itemCheckBox)
            viewHolder.textView =
                    convertView.findViewById(R.id.itemTextView)
            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }

        val item: TblPromotions = getItem(position)
        viewHolder.checkBox.isChecked = item.Participant.equals("1")
//        viewHolder.checkBox.tag = item.Participant
        viewHolder.checkBox.text = item.Description
        viewHolder.checkBox.isClickable = true
        viewHolder.checkBox.setOnClickListener {
            if (viewHolder.checkBox.isChecked) {
                FacilityDataModel.getInstance().tblPromotions.filter { s->s.PromoID.equals(item.PromoID)}[0].apply {
                    Participant = "1"
                }
                item.Participant = "1"

            } else {
                item.Participant = "0"
                viewHolder.checkBox.tag = "2"
                FacilityDataModel.getInstance().tblPromotions.filter { s->s.PromoID.equals(item.PromoID)}[0].apply {
                    Participant = "2"
                }
            }
            (context as FormsActivity).saveRequired = true
            parentFragment.refreshButtonsState()
        }
        viewHolder.textView.setOnClickListener {
            Utility.showMessageDialog(context,"Promotion Info ...","Hover Text --> " + Html.fromHtml(item.HoverText, Html.FROM_HTML_MODE_COMPACT) + "\n\n" + "Disclaimer --> " + Html.fromHtml(item.Disclaimer, Html.FROM_HTML_MODE_COMPACT) + "\n\n" + "Coupon --> " + Html.fromHtml(item.CouponText, Html.FROM_HTML_MODE_COMPACT))
        }
        return result
    }
}